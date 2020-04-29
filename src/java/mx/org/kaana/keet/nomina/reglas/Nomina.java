package mx.org.kaana.keet.nomina.reglas;

import com.eteks.jeks.JeksExpression;
import com.eteks.jeks.JeksExpressionParser;
import com.eteks.jeks.JeksInterpreter;
import com.eteks.jeks.JeksTableModel;
import com.eteks.parser.CompilationException;
import com.eteks.parser.CompiledExpression;
import mx.org.kaana.libs.formato.Error;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.keet.db.dto.TcKeetNominasConceptosDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasPeriodosDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasPersonasDto;
import mx.org.kaana.keet.nomina.beans.Concepto;
import mx.org.kaana.keet.nomina.functions.Redondea;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 28/04/2020
 *@time 07:53:30 PM 
 *author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Nomina implements Serializable {

	private static final long serialVersionUID=5957203435283148419L;
	private static final Log LOG=LogFactory.getLog(Nomina.class);
	private static final Integer ROWS            = 1;
	private static final Integer COLUMNS         = 200;
	private static final Integer NUMERO_DE_LETRAS= 25;
	private static final String APORTACIONES     = "C1";
	private static final String DEDUCCIONES      = "D1";
	private static final String PERCEPCIONES     = "E1";
	private static final String NETO             = "F1";
	
	private Session sesion;
  private JeksTableModel model;
  private JeksExpressionParser parser;
  private CompiledExpression expression;
  private JeksInterpreter interprete;
	private Map<String, Double> constants;
	private TcKeetNominasDto nomina;
	private List<Concepto> conceptos;
	
	public Nomina(Session sesion, TcKeetNominasDto nomina) throws Exception {
		this.sesion= sesion;
		this.nomina= nomina;
		this.model = new JeksTableModel(this.ROWS /*rows*/, this.COLUMNS /*columns*/);
    this.parser= new JeksExpressionParser(model);
    this.parser.addUserFunction(new Redondea());
    this.interprete= new JeksInterpreter();
		this.load();
	}

	private void load() throws Exception {
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			params.put("idNomina", this.nomina.getIdNomina());
			List<Entity> entities= DaoFactory.getInstance().toEntitySet(this.sesion, "TcKeetNominasConstantesDto", "todos", Collections.EMPTY_MAP);
			this.constants= new HashMap<>();
			entities.forEach((entity) -> {
				this.constants.put(entity.toString("siglas"), entity.toDouble("valor"));
			}); // for
			TcKeetNominasPeriodosDto semana= (TcKeetNominasPeriodosDto)DaoFactory.getInstance().findById(this.sesion, TcKeetNominasPeriodosDto.class, this.nomina.getIdNominaPeriodo());
			params.put("semana", semana.getOrden());
			this.conceptos= (List<Concepto>)DaoFactory.getInstance().toEntitySet(this.sesion, Concepto.class, "TcKeetNominasConceptosDto", "todos", params);
			for (Concepto concepto: conceptos) {
				concepto.setColumna(this.toColumn(concepto.getCelda()));
			} // for
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}
	
	private void cleanRow() {
		for (int col=0; col< this.COLUMNS; col++) {
  		model.setValueAt(null, 0/*row*/, col/*column*/);
		} // for
	}
	
	private void addCell(int column, String value) throws CompilationException {
	  this.expression= this.parser.compileExpression(value);
    this.model.setValueAt(this.expression, 0, column);
	}
	
	private String toCell(int column) {
    return this.model.getColumnName(column);
  }
	
	private int toColumn(String cell) {
    int regresar= 0;
    if(cell.length()> 0) {
      for(int x= cell.length()-2; x>= 0; x--) {
        if(x== cell.length()-2)
          regresar+= ((byte)cell.charAt(x))- 65;
        else
          regresar+= (((byte)cell.charAt(x))- 64)* (NUMERO_DE_LETRAS+ 1);
      } // for
    } // if
    else
      regresar= -1;		
    return regresar;
	}
	
	private String toExpression(int column) {
		if(this.model.getValueAt(0, column)!= null)
      return ((JeksExpression)this.model.getValueAt(0, column)).getDefinition();
		else
			return null;
	}
	
	private double toValue(int column) {
    double regresar= 0;
    String parche  = null;
    try {
      if((this.model.getValueAt(0, column)!= null) && (!this.model.getValueAt(0, column).equals("")) ) {
        parche= ((CompiledExpression)this.model.getValueAt(0, column)).computeExpression(interprete).toString();
        regresar= Double.parseDouble(parche);
      } // if
    } // catch
    catch (Exception e) {
			Error.mensaje(e);
      regresar= 0D;
    }
    return regresar;		
	}
	
	private String lookForConstants(String key) {
	  String regresar= "1";
		if(this.constants.containsKey(key))
			regresar= this.constants.get(key).toString();
		return regresar;
	} 
	
	private String transform(String value) {
		StringBuilder regresar= new StringBuilder();
		StringTokenizer values= new StringTokenizer(value, "()*/+-^=><, ", true);
		String token          = null;
		while (values.hasMoreTokens()) {
			token=values.nextToken();
			switch (token.charAt(0)) {
				case ' ':
					break;
				case '{':
					token=token.substring(1, token.length()-1);
					regresar.append(this.lookForConstants(token));
					break;
        default:
          regresar.append(token);
          break;			
		  } // switch
		} // while
		return regresar.toString();
	}
	
	private Double toTotal(String cell) {
	  Double regresar= 0D;
		int index= this.conceptos.indexOf(new Concepto(cell));
		if(index>= 0)
			regresar= this.conceptos.get(index).getValor();
		return regresar;
	}
	
	public void process(TcKeetNominasPersonasDto empleado) throws CompilationException, Exception {
		this.constants.put("sueldo", empleado.getNeto());
		LOG.debug("------------------------[ "+ empleado.getIdEmpresaPersona()+ " ]----------------------------");
		String value= null;
		this.cleanRow();
		for (Concepto concepto: this.conceptos) {
			value = this.transform(concepto.getFormula());
 			this.addCell(concepto.getColumna(), value);
		} // for
		for (Concepto concepto: this.conceptos) {
			value = this.transform(concepto.getFormula());
			concepto.setValor(this.toValue(concepto.getColumna()));
  		LOG.debug("("+ concepto.getColumna()+ ") ["+ value+ "] {"+ concepto.getValor()+ "}");
		} // for
		empleado.setAportaciones(this.toTotal(this.APORTACIONES));
		empleado.setDeducciones(this.toTotal(this.DEDUCCIONES));
		empleado.setPercepciones(this.toTotal(this.PERCEPCIONES));
		empleado.setNeto(this.toTotal(this.NETO));
		DaoFactory.getInstance().insert(this.sesion, empleado);
		for (Concepto concepto: this.conceptos) {
			concepto.setIdNominaPersona(empleado.getIdNominaPersona());
  		DaoFactory.getInstance().insert(this.sesion, concepto);
		} // for
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.model = null;
    this.parser= null;
    this.interprete= null;
    this.expression= null;
	}
	
}
