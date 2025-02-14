package mx.org.kaana.keet.nomina.reglas;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.write.WritableCellFormat;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.keet.nomina.enums.ENominaEstatus;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.archivo.XlsBase;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
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

public class Empleados extends XlsBase implements Serializable {
  
  private static final Log LOG = LogFactory.getLog(Empleados.class);
  private static final long serialVersionUID = -3364636967422678893L;
  
  
  protected Long idNomina;
  protected Entity nomina;
  protected Map<String, Double> subTotales;
  protected Map<String, Double> totales;
  protected String path;
  protected WritableCellFormat number;
  protected WritableCellFormat value;
  protected WritableCellFormat total;
  protected Boolean isCafu;
          
  public Empleados(Long idNomina) throws Exception {
    this.idNomina  = idNomina;
    this.subTotales= new HashMap<>();
    this.totales   = new HashMap<>();
    this.path      = "";
    this.number    = this.toNumber(Alignment.RIGHT, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.value     = this.toNumber(Alignment.RIGHT, Colour.UNKNOWN, Colour.BLACK, Boolean.FALSE);
    this.total     = this.toNumber(Alignment.RIGHT, Colour.YELLOW, Colour.BLACK, Boolean.TRUE);
    this.init();
  }

  public Long getIdNomina() {
    return idNomina;
  }

  protected void init() { 
    this.subTotales.clear();
    this.subTotales.put("percepciones", 0D);
    this.subTotales.put("deducciones", 0D);
    this.subTotales.put("cajaChica", 0D);
    this.subTotales.put("neto", 0D);
    this.subTotales.put("sueldoImss", 0D);
    this.subTotales.put("sobreSueldo", 0D);
    this.subTotales.put("anticipos", 0D);
    this.subTotales.put("adelantado", 0D);
    this.subTotales.put("total", 0D);
    switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
      case "cafu":
        this.isCafu= Boolean.TRUE;
        break;
      case "gylvi":
      case "triana":
        this.isCafu= Boolean.FALSE;
        break;
    } // swtich
  }

  @Override
  protected String getNombresColumnas() {
    return "";
  }

  @Override
  protected int getColumnasInformacion() {
    return 0;
  }

  @Override
  public boolean generarRegistros(boolean titulo) throws Exception {
    return Boolean.FALSE;
  }
  
  protected String local() throws Exception {
    this.path= "d:/";
    return this.process();
  }
  
  public String execute() throws Exception {
    this.path= JsfBase.getRealPath("").concat(EFormatos.XLS.toPath());
    return this.process();
  }
  
  protected String process() throws Exception {
    String regresar           = "";
    String desarrollo         = "";
    Map<Long, Object> caja    = new HashMap<>();
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idNomina", this.idNomina);      
      params.put("idTipoNomina", 1L);      
      this.nomina= (Entity)DaoFactory.getInstance().toEntity("VistaNominaDto", "nomina", params);
      regresar= Archivo.toFormatNameFile(Constantes.ARCHIVO_PATRON_NOMBRE, "SEMANA-".concat(this.nomina.toString("semana")).concat(".").concat(EFormatos.XLS.name().toLowerCase()));
      this.posicionFila   = 0;
      this.posicionColumna= 0;
      this.libro= Workbook.createWorkbook(new File(this.path.concat(regresar)));
      this.hoja = this.libro.createSheet(Constantes.ARCHIVO_PATRON_NOMBRE, 0);
      this.addCell(this.posicionColumna, this.posicionFila++, "LISTA DE RAYA DE LA SEMANA ".
              concat(this.nomina.toString("semana").
              concat(" DEL ").concat(Fecha.formatear(Fecha.FECHA_NOMBRE_MES, this.nomina.toDate("inicio")).toUpperCase()).
              concat(" AL ").concat(Fecha.formatear(Fecha.FECHA_NOMBRE_MES, this.nomina.toDate("termino")).toUpperCase())));
      List<Entity> residentes= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaComprasAlmacenDto", "residentes", params);
      if(residentes!= null && !residentes.isEmpty()) {
        for (Entity residente: residentes) {
          caja.put(residente.toLong("idEmpresaPersona"), residente.toDouble("gasto"));
        } // for
      } // if
      List<Entity> personas= null;
      if(Objects.equals(this.nomina.toLong("idNominaEstatus"), -1L) || Objects.equals(this.nomina.toLong("idNominaEstatus"), ENominaEstatus.TERMINADA))
        personas= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaNominaDto", "exportarEmpleados", params);
      else
        personas= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaNominaDto", "exportarCalculada", params);
      if(personas!= null && !personas.isEmpty()) {
        this.toAddTitulo("LISTADO DE ADMINISTRATIVOS Y DESTAJISTAS");
        this.posicionFila--;
        for (Entity item: personas) {
          if(!Objects.equals(desarrollo, item.toString("desarrollo"))) {
            if(!Objects.equals(desarrollo, "")) 
              this.toAddSubtotalesEmpleado(desarrollo);
            this.posicionFila++;
            this.posicionFila++;
            LOG.info("------------------------DESARROLLO ["+ item.toString("desarrollo")+"]-------------------------------");
            this.addCellColor(this.posicionColumna, this.posicionFila, item.toString("desarrollo"));
            this.toAddTitulosEmpleado();
            desarrollo= item.toString("desarrollo");
            this.init();
          } // if  
          // SI ES RESIDENTE RECUPERAR EL IMPORTE DE CAJA CHICA DE LA SEMANA 
          Double deposito= 0D;
          if((Objects.equals(item.toLong("idPuesto"), 8L) || Objects.equals(item.toLong("idPuesto"), 20L)) && caja.containsKey(item.toLong("idEmpresaPersona")))
            deposito= (Double)caja.get(item.toLong("idEmpresaPersona"));
          if(item.toDouble("neto")!= null && item.toDouble("neto")> 0)
            this.toEmpleado(item, deposito);
          // break;
        } // for
        if(!Objects.equals(desarrollo, "")) 
          this.toAddSubtotalesEmpleado(desarrollo);
        this.posicionFila++;
        this.posicionFila++;
        LOG.info("-----------------------------------------------------------------------");
        this.toAddTitulo("RESUMEN ADMINISTRATIVOS Y DESTAJISTAS");
        this.posicionFila++;
        Double suma= 0D;
        List<String> nombres = new ArrayList<>(this.totales.keySet());
        Collections.sort(nombres);
        for (String key: nombres) {
          this.addCell(this.posicionColumna, this.posicionFila, key);
          this.addNumber(this.posicionColumna+ 8, this.posicionFila++, Numero.toRedondearSat(this.totales.get(key)), this.number);
          suma+= this.totales.get(key);
        } // for
        this.addTotal();
        this.addNumber(this.posicionColumna+ 8, this.posicionFila++, Numero.toRedondearSat(suma), this.total);
      } // if
      else {
        this.posicionFila++;
        this.addCellColor(this.posicionColumna, this.posicionFila, "LA NOMINA NO TIENE EMPLEADOS", jxl.format.Colour.BLUE);
        this.toAddView(0, 80);
      } // else
      
      desarrollo= "";
      this.totales.clear();
      List<Entity> proveedores= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaNominaDto", "exportarProveedores", params);
      if(proveedores!= null && !proveedores.isEmpty()) {
        this.toAddTitulo("LISTADO DE LOS SUBCONTRATISTAS");
        this.posicionFila--;
        for (Entity item: proveedores) {
          if(!Objects.equals(desarrollo, item.toString("desarrollo"))) {
            if(!Objects.equals(desarrollo, "")) 
              this.toAddSubtotalesProveedor(desarrollo);
            this.posicionFila++;
            this.posicionFila++;
            LOG.info("------------------------DESARROLLO ["+ item.toString("desarrollo")+"]-------------------------------");
            this.addCellColor(this.posicionColumna, this.posicionFila, item.toString("desarrollo"));
            this.toAddTitulosProveedor();
            desarrollo= item.toString("desarrollo");
            this.init();
          } // if  
          this.toProveedor(item);
          // break;
        } // for
        if(!Objects.equals(desarrollo, "")) 
          this.toAddSubtotalesProveedor(desarrollo);
        this.posicionFila++;
        this.posicionFila++;
        LOG.info("-----------------------------------------------------------------------");
        this.toAddTitulo("RESUMEN DE LOS SUBCONTRATISTAS");
        this.posicionFila++;
        Double suma= 0D;
        List<String> nombres= new ArrayList<>(this.totales.keySet());
        Collections.sort(nombres);
        for (String key: nombres) {
          this.addCell(this.posicionColumna, this.posicionFila, key);
          this.addNumber(this.posicionColumna+ 8, this.posicionFila++, Numero.toRedondearSat(this.totales.get(key)), this.number);
          suma+= this.totales.get(key);
        } // for
        this.addTotal();
        this.addNumber(this.posicionColumna+ 8, this.posicionFila++, Numero.toRedondearSat(suma), this.total);
      } // if
      else {
        this.posicionFila++;
        this.addCellColor(this.posicionColumna, this.posicionFila, "LA NOMINA NO TIENE PROVEEDORES", jxl.format.Colour.BLUE);
        this.toAddView(0, 80);
      } // else      
      if((personas!= null && !personas.isEmpty()) || (proveedores!= null && !proveedores.isEmpty())) {
        this.toAddView(0, 23);
        this.toAddView(1, 30);
        this.toAddView(2, 23);
        this.toAddView(3, 13);
        this.toAddView(4, 70);
        this.toAddView(5, 15);
        this.toAddView(6, 15);
        this.toAddView(7, 13);
        this.toAddView(8, 13);
        if(this.isCafu) {
          this.toAddView(9, 13);
          this.toAddView(10, 17);
        } // if  
      } // if  
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(caja);
      Methods.clean(params);
  	  this.libro.write();
      this.libro.close();
    } // finally
    return regresar;
  }  
  
  protected void addTotal() throws Exception {
    this.addCell(this.posicionColumna, this.posicionFila, null, Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+1, this.posicionFila, null, Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+2, this.posicionFila, null, Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+3, this.posicionFila, null, Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+4, this.posicionFila, null, Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+5, this.posicionFila, null, Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+6, this.posicionFila, null, Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+7, this.posicionFila, "TOTAL:", Alignment.RIGHT, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
  }
  
  protected void toAddTitulo(String titulo) throws Exception {
    this.posicionFila++;
    this.addCell(this.posicionColumna, this.posicionFila, titulo, Alignment.LEFT, Colour.LIGHT_ORANGE, Colour.BLACK, Boolean.FALSE);
    for(int x= 1; x< 9; x++) {
      this.addCell(this.posicionColumna+ x, this.posicionFila, null, Alignment.LEFT, Colour.LIGHT_ORANGE, Colour.BLACK, Boolean.FALSE);
    } // for
  }
  
  protected void toAddTitulosEmpleado() throws Exception {
    this.posicionFila++;
    this.addCell(this.posicionColumna, this.posicionFila, "CATEGORIA", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 1, this.posicionFila, "DEPARTAMENTO", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 2, this.posicionFila, "PUESTO", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 3, this.posicionFila, "CLAVE", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 4, this.posicionFila, "NOMBRE", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 5, this.posicionFila, "PERCEPCIONES", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 6, this.posicionFila, "DEDUCCIONES", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 7, this.posicionFila, "ANTICIPOS", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 8, this.posicionFila, "NETO", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    if(this.isCafu) {
      this.addCell(this.posicionColumna+ 9, this.posicionFila, "SUELDO IMSS", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
      this.addCell(this.posicionColumna+ 10, this.posicionFila, "SOBRE SUELDO", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    } // if  
  }

  protected void toAddTitulosProveedor() throws Exception {
    this.posicionFila++;
    this.addCell(this.posicionColumna, this.posicionFila, "CATEGORIA", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 1, this.posicionFila, "DEPARTAMENTO", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 2, this.posicionFila, "PUESTO", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 3, this.posicionFila, "CLAVE", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 4, this.posicionFila, "NOMBRE", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 5, this.posicionFila, null, Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 6, this.posicionFila, null, Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 7, this.posicionFila, "ANTICIPOS", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 8, this.posicionFila, "NETO", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
  }

  protected void toAddSubtotalesEmpleado(String desarrollo) throws Exception {
    this.posicionFila++;
    this.addNumber(this.posicionColumna+ 5, this.posicionFila, Numero.toRedondearSat(this.subTotales.get("percepciones")), this.total);
    this.addNumber(this.posicionColumna+ 6, this.posicionFila, Numero.toRedondearSat(this.subTotales.get("deducciones")), this.total);
    this.addNumber(this.posicionColumna+ 7, this.posicionFila, Numero.toRedondearSat(this.subTotales.get("anticipos")), this.total);
    this.addNumber(this.posicionColumna+ 8, this.posicionFila, Numero.toRedondearSat(this.subTotales.get("neto")), this.total);
    if(this.isCafu) {
      this.addNumber(this.posicionColumna+ 9, this.posicionFila, Numero.toRedondearSat(this.subTotales.get("sueldoImss")), this.total);
      this.addNumber(this.posicionColumna+ 10, this.posicionFila, Numero.toRedondearSat(this.subTotales.get("sobreSueldo")), this.total);
    } // if  
    this.totales.put(desarrollo, this.subTotales.get("neto"));
  }
  
  protected void toAddSubtotalesProveedor(String desarrollo) throws Exception {
    this.posicionFila++;
    this.addNumber(this.posicionColumna+ 7, this.posicionFila, Numero.toRedondearSat(this.subTotales.get("adelantado")), this.total);
    this.addNumber(this.posicionColumna+ 8, this.posicionFila, Numero.toRedondearSat(this.subTotales.get("total")), this.total);
    this.totales.put(desarrollo, this.subTotales.get("total"));
  }
  
  public void toEmpleado(Entity item, Double cajaChica) throws Exception {
    try {      
      this.posicionFila++;
      this.addCell(this.posicionColumna, this.posicionFila, item.toString("categoria"));
      this.addCell(this.posicionColumna+ 1, this.posicionFila, item.toString("departamento"));
      this.addCell(this.posicionColumna+ 2, this.posicionFila, item.toString("puesto"));
      this.addCell(this.posicionColumna+ 3, this.posicionFila, item.toString("clave"));
      this.addCell(this.posicionColumna+ 4, this.posicionFila, item.toString("nombre"));
      this.addNumber(this.posicionColumna+ 5, this.posicionFila, Numero.toRedondearSat(item.toDouble("percepciones")), this.number);
      this.addNumber(this.posicionColumna+ 6, this.posicionFila, Numero.toRedondearSat(item.toDouble("deducciones")), this.number);
      this.addNumber(this.posicionColumna+ 7, this.posicionFila, Numero.toRedondearSat(item.toDouble("anticipos")), this.number);
      this.addNumber(this.posicionColumna+ 8, this.posicionFila, Numero.toRedondearSat(item.toDouble("neto")- item.toDouble("anticipos")), this.number);
      if(this.isCafu) {
        this.addNumber(this.posicionColumna+ 9, this.posicionFila, Numero.toRedondearSat(item.toDouble("sueldoImss")), this.number);
        this.addNumber(this.posicionColumna+ 10, this.posicionFila, Numero.toRedondearSat(item.toDouble("sobreSueldo")), this.number);
      } // if  
      this.subTotales.put("percepciones", this.subTotales.get("percepciones")+ item.toDouble("percepciones"));
      this.subTotales.put("deducciones", this.subTotales.get("deducciones")+ item.toDouble("deducciones"));
      this.subTotales.put("cajaChica", this.subTotales.get("cajaChica")+ cajaChica);
      this.subTotales.put("anticipos", this.subTotales.get("anticipos")+ item.toDouble("anticipos"));
      this.subTotales.put("neto", this.subTotales.get("neto")+ item.toDouble("neto")- item.toDouble("anticipos"));
      this.subTotales.put("sueldoImss", this.subTotales.get("sueldoImss")+ item.toDouble("sueldoImss"));
      this.subTotales.put("sobreSueldo", this.subTotales.get("sobreSueldo")+ item.toDouble("sobreSueldo"));
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }
  
  public void toProveedor(Entity item) throws Exception {
    try {      
      this.posicionFila++;
      this.addCell(this.posicionColumna, this.posicionFila, "SUB CONTRATISTA");
      this.addCell(this.posicionColumna+ 1, this.posicionFila, item.toString("departamento"));
      this.addCell(this.posicionColumna+ 2, this.posicionFila, item.toString("clave"));
      this.addCell(this.posicionColumna+ 3, this.posicionFila, item.toString("nombre"));
      this.addNumber(this.posicionColumna+ 7, this.posicionFila, Numero.toRedondearSat(item.toDouble("anticipos")), this.number);
      this.addNumber(this.posicionColumna+ 8, this.posicionFila, Numero.toRedondearSat(item.toDouble("total")), this.number);
      this.subTotales.put("adelantado", this.subTotales.get("adelantado")+ item.toDouble("anticipos"));
      this.subTotales.put("total", this.subTotales.get("total")+ item.toDouble("total"));
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }
  
  @Override
  public void finalize() {
    Methods.clean(this.subTotales);
    Methods.clean(this.totales);
    super.finalize(); 
  }
  
  public static void main(String ... args) throws Exception {
    Empleados corte= new Empleados(169L);
    LOG.info(corte.local());
  }
  
}
