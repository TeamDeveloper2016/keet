package mx.org.kaana.keet.nomina.reglas;

import com.eteks.jeks.JeksExpression;
import com.eteks.jeks.JeksExpressionParser;
import com.eteks.jeks.JeksInterpreter;
import com.eteks.jeks.JeksTableModel;
import com.eteks.parser.CompilationException;
import com.eteks.parser.CompiledExpression;
import mx.org.kaana.libs.formato.Error;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.keet.db.dto.TcKeetNominasDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasPeriodosDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasPersonasDto;
import mx.org.kaana.keet.nomina.beans.Concepto;
import mx.org.kaana.keet.nomina.enums.ECodigosIncidentes;
import mx.org.kaana.keet.nomina.enums.EGrupoConceptos;
import mx.org.kaana.keet.nomina.functions.Redondea;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesDto;
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
	
	private Session sesion;
  private JeksTableModel model;
  private JeksExpressionParser parser;
  private CompiledExpression expression;
  private JeksInterpreter interprete;
	private Map<String, Double> constants;
	private TcKeetNominasDto nomina;
	private List<Concepto> conceptos;
	private List<Concepto> personales;
	
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
			for (Concepto concepto: conceptos) 
				concepto.setColumna(this.toColumn(concepto.getCelda()));
			this.personales= (List<Concepto>)DaoFactory.getInstance().toEntitySet(this.sesion, Concepto.class, "TcKeetNominasConceptosDto", "personales", params);
			for (Concepto concepto: personales)
				concepto.setColumna(this.toColumn(concepto.getCelda()));
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
			regresar= Cadena.isVacio(this.conceptos.get(index).getValor())? 0D: this.conceptos.get(index).getValor();
		return regresar;
	}
	
	private List<Concepto> toClone() throws CloneNotSupportedException {
		List<Concepto> regresar= new ArrayList<>();
		for (Concepto concepto: this.conceptos) {
			regresar.add((Concepto)concepto.clone());
		} // for
		return regresar;
	}
	
	private void toLookForEquals(List<Concepto> particulares, List<TcManticIncidentesDto> incidentes, ECodigosIncidentes incidente) throws CloneNotSupportedException {
		int count= 0;
		for (TcManticIncidentesDto item: incidentes) {
			// BUSCAR LOS INCIDENTES QUE COINCIDAN EN LA LISTA QUE SE TIENE DE CONCEPTOS
			if(Objects.equals(item.getIdTipoIncidente(), incidente.idTipoIncidente()) && count< incidente.size()) {
				item.setIdNomina(this.nomina.getIdNomina());
				int index= this.personales.indexOf(new Concepto(incidente.celdas()[count]));
				// PARA AQUELLOS INCIDENTES ENCONTRADOS BUSCAR EL CONCEPTO CORRESPONDIENTE
				if(index>= 0) {
					Concepto concepto= (Concepto)this.personales.get(index).clone();
					if(incidente.recuperar())
						concepto.setFormula(concepto.getFormula().replace("{".concat(incidente.name()).concat("}"), item.getCosto().toString()));
					particulares.add(concepto);
				}
			  count++;
			} // if
		} // for
	}
	
	private void toIncidencias(List<Concepto> particulares, Long idEmpresaPersona) throws Exception {
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			params.put("idNomina", this.nomina.getIdNomina());
			params.put("idEmpresaPersona", idEmpresaPersona);
			List<TcManticIncidentesDto> incidentes= (List<TcManticIncidentesDto>)DaoFactory.getInstance().toEntitySet(this.sesion, TcManticIncidentesDto.class, "VistaNominaDto", "incidentes", params);
			if(incidentes!= null && !incidentes.isEmpty()) {
				this.toLookForEquals(particulares, incidentes, ECodigosIncidentes.FALTA);
				this.toLookForEquals(particulares, incidentes, ECodigosIncidentes.DIAFESTIVO);
				this.toLookForEquals(particulares, incidentes, ECodigosIncidentes.TRIPLE);
				this.toLookForEquals(particulares, incidentes, ECodigosIncidentes.EXEDENTE);
				this.toLookForEquals(particulares, incidentes, ECodigosIncidentes.PRESTAMO);
				this.toLookForEquals(particulares, incidentes, ECodigosIncidentes.ABONO);
				int count= 0;
				// REMOVER TODOS LOS INCIDENTES QUE SE ALCANZARON A APLICAR EN LA NOMINA
				while(count< incidentes.size()) {
					if(Cadena.isVacio(incidentes.get(count).getIdNomina()))
						incidentes.remove(count);
					else {
						// APLICAR AQUELLOS INCIDENTES QUE SI FUERON SELECCIONADOS
						DaoFactory.getInstance().update(this.sesion, incidentes.get(count));
						count++;
					} // else
				} // while
			} // if
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}

	public void process(TcKeetNominasPersonasDto empleado) throws CompilationException, Exception {
		this.constants.put("sueldo", empleado.getNeto());
		LOG.info("------------------------[ "+ empleado.getIdEmpresaPersona()+ " ]----------------------------");
		this.cleanRow();
		// CLONAR LOS CONCEPTOS GLOBALES PARA QUE SOLO APLIQUE AL EMPLEADO
		List<Concepto> particulares= this.toClone();
		// BUSCAR LAS INCIDENCIAS DE ESE EMPLEADO
		this.toIncidencias(particulares, empleado.getIdEmpresaPersona());
		for (Concepto concepto: particulares) {
 			this.addCell(concepto.getColumna(), this.transform(concepto.getFormula()));
		} // for
		for (Concepto concepto: particulares) {
			concepto.setValor(this.toValue(concepto.getColumna()));
  		LOG.info("("+ concepto.getColumna()+ ") ["+ this.transform(concepto.getFormula())+ "] {"+ concepto.getValor()+ "}");
		} // for
		// RECUPERAR LOS CALCULOS Y SACAR LOS TOTALES PARA EL PAGO DE NOMINA
		empleado.setAportaciones(this.toTotal(EGrupoConceptos.APORTACIONES.celda()));
		empleado.setDeducciones(this.toTotal(EGrupoConceptos.DEDUCCIONES.celda()));
		empleado.setPercepciones(this.toTotal(EGrupoConceptos.PERCEPCIONES.celda()));
		empleado.setNeto(this.toTotal(EGrupoConceptos.NETO.celda()));
		DaoFactory.getInstance().insert(this.sesion, empleado);
		// ALMACENAR EL DETALLE DE CALCULO DE LA NOMINA
		for (Concepto concepto: particulares) {
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
