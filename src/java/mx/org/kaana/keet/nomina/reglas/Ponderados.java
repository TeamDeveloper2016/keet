package mx.org.kaana.keet.nomina.reglas;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 26/01/2022
 *@time 08:42:00 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Ponderados extends Empleados implements Serializable {
  
  private static final Log LOG = LogFactory.getLog(Ponderados.class);
  private static final long serialVersionUID = -3364616917422678893L;

  private static final String TOTAL     = "costo";
  private static final String POR_EL_DIA= "porElDia";
  private static final String DESTAJOS  = "destajos";
  
  public Ponderados(Long idNomina) throws Exception {
    super(idNomina);
  }
  
  @Override
  protected String process() throws Exception {
    String regresar           = "";
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idNomina", this.idNomina);      
      params.put("idTipoNomina", 1L);      
      this.nomina= (Entity)DaoFactory.getInstance().toEntity("VistaNominaDto", "nomina", params);
      regresar= Archivo.toFormatNameFile(Constantes.ARCHIVO_PATRON_NOMBRE, "PONDERAROS-".concat(this.nomina.toString("semana")).concat(".").concat(EFormatos.XLS.name().toLowerCase()));
      this.posicionFila   = 0;
      this.posicionColumna= 0;
      this.libro= Workbook.createWorkbook(new File(this.path.concat(regresar)));
      this.hoja = this.libro.createSheet(Constantes.ARCHIVO_PATRON_NOMBRE, 0);
      this.totales.clear();
      this.totales.put(POR_EL_DIA, 0D);
      this.totales.put(DESTAJOS, 0D);
      this.totales.put(TOTAL, 0D);
      this.toAddTitulosEmpleado();
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<Entity> contratos= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaCostosDto", "ponderados", params, Constantes.SQL_TODOS_REGISTROS);
      for (Entity item: contratos) {
        this.toAddRegistro(item);
      } // for
      this.toAddSubtotales();
      this.toAddView(0, 5);
      this.toAddView(1, 20);
      this.toAddView(2, 20);
      this.toAddView(3, 15);
      this.toAddView(4, 15);
      this.toAddView(5, 15);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
  	  this.libro.write();
      this.libro.close();
    } // finally
    return regresar;
  }  
  
  @Override
  protected void toAddTitulosEmpleado() throws Exception {
    this.posicionFila++;
    String  semana= "SEMANA ".concat(this.nomina.toString("semana"));
//    String  semana= "MONTOS DE LA SEMANA ".concat(this.nomina.toString("semana").
//      concat(" DEL ").concat(Fecha.formatear(Fecha.FECHA_NOMBRE_MES, this.nomina.toDate("inicio")).toUpperCase()).
//      concat(" AL ").concat(Fecha.formatear(Fecha.FECHA_NOMBRE_MES, this.nomina.toDate("termino")).toUpperCase()));
    this.addCell(this.posicionColumna, this.posicionFila, "No", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 1, this.posicionFila, "DESARROLLO", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 2, this.posicionFila, "FRENTE", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 3, this.posicionFila, "", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 4, this.posicionFila, semana, Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 5, this.posicionFila++, "", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    
    this.addCell(this.posicionColumna, this.posicionFila, "", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 1, this.posicionFila, "", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 2, this.posicionFila, "", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 3, this.posicionFila, "POR EL DÍA", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 4, this.posicionFila, "DESTAJOS", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 5, this.posicionFila, "TOTAL", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
  }   
  
  protected void toAddRegistro(Entity row) throws Exception {
    this.posicionFila++;
    this.addCell(this.posicionColumna, this.posicionFila, String.valueOf(this.posicionFila- 2));
    this.addCell(this.posicionColumna+ 1, this.posicionFila, row.toString("desarrollo"));
    this.addCell(this.posicionColumna+ 2, this.posicionFila, row.toString("nombre")); 
    this.addNumber(this.posicionColumna+ 3, this.posicionFila, Numero.toRedondearSat(row.toDouble(POR_EL_DIA)), this.value);
    this.addNumber(this.posicionColumna+ 4, this.posicionFila, Numero.toRedondearSat(row.toDouble(DESTAJOS)), this.value);
    this.addNumber(this.posicionColumna+ 5, this.posicionFila, Numero.toRedondearSat(row.toDouble(TOTAL)), this.value);
    this.totales.put(POR_EL_DIA, this.totales.get(POR_EL_DIA)+ row.toDouble(POR_EL_DIA));
    this.totales.put(DESTAJOS, this.totales.get(DESTAJOS)+ row.toDouble(DESTAJOS));
    this.totales.put(TOTAL, this.totales.get(TOTAL)+ row.toDouble(TOTAL));
  }
  
  protected void toAddSubtotales() throws Exception {
    this.posicionFila++;
    this.addCell(this.posicionColumna+ 2, this.posicionFila, "TOTAL:"); 
    this.addNumber(this.posicionColumna+ 3, this.posicionFila, Numero.toRedondearSat(this.totales.get(POR_EL_DIA)), this.value);
    this.addNumber(this.posicionColumna+ 4, this.posicionFila, Numero.toRedondearSat(this.totales.get(DESTAJOS)), this.value);
    this.addNumber(this.posicionColumna+ 5, this.posicionFila, Numero.toRedondearSat(this.totales.get(TOTAL)), this.value);
  }  
  
  public static void main(String ... args) throws Exception {
    Ponderados corte= new Ponderados(184L);
    LOG.info(corte.local());
  }
  
}
