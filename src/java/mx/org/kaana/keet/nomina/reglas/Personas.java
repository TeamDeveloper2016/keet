package mx.org.kaana.keet.nomina.reglas;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.keet.nomina.enums.ENominaEstatus;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.formato.Fecha;
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

public class Personas extends Empleados implements Serializable {
  
  private static final Log LOG = LogFactory.getLog(Personas.class);
  private static final long serialVersionUID = -3364616967422678893L;

  public Personas(Long idNomina) throws Exception {
    super(idNomina);
  }
  
  @Override
  protected String process() throws Exception {
    String regresar           = "";
    String desarrollo         = "";
    Map<Long, Object> caja    = new HashMap<>();
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idNomina", this.idNomina);      
      params.put("idTipoNomina", 1L);      
      this.nomina= (Entity)DaoFactory.getInstance().toEntity("VistaNominaDto", "nomina", params);
      regresar= Archivo.toFormatNameFile(Constantes.ARCHIVO_PATRON_NOMBRE, "NOMINA-".concat(this.nomina.toString("semana")).concat(".").concat(EFormatos.XLS.name().toLowerCase()));
      this.posicionFila   = 0;
      this.posicionColumna= 0;
      this.libro= Workbook.createWorkbook(new File(this.path.concat(regresar)));
      this.hoja = this.libro.createSheet(Constantes.ARCHIVO_PATRON_NOMBRE, 0);
      this.addCell(this.posicionColumna, this.posicionFila++, "NOMINA DEL PERSONAL DE LA SEMANA ".
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
        this.toAddTitulosEmpleado();
        for (Entity item: personas) {
          if(!Objects.equals(desarrollo, item.toString("desarrollo"))) {
            if(!Objects.equals(desarrollo, "")) 
              this.toAddSubtotalesEmpleado(desarrollo);
            LOG.info("------------------------DESARROLLO ["+ item.toString("desarrollo")+"]-------------------------------");
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
      } // if
      else {
        this.posicionFila++;
        this.addCellColor(this.posicionColumna, this.posicionFila, "LA NOMINA NO TIENE EMPLEADOS", jxl.format.Colour.BLUE);
        this.toAddView(0, 80);
      } // else
      this.posicionFila++;
      desarrollo= "";
      this.totales.clear();
      List<Entity> proveedores= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaNominaDto", "exportarProveedores", params);
      if(proveedores!= null && !proveedores.isEmpty()) {
        for (Entity item: proveedores) {
          if(!Objects.equals(desarrollo, item.toString("desarrollo"))) {
            if(!Objects.equals(desarrollo, "")) 
              this.toAddSubtotalesProveedor(desarrollo);
            LOG.info("------------------------DESARROLLO ["+ item.toString("desarrollo")+"]-------------------------------");
            desarrollo= item.toString("desarrollo");
            this.init();
          } // if  
          this.toProveedor(item);
          // break;
        } // for
        if(!Objects.equals(desarrollo, "")) 
          this.toAddSubtotalesProveedor(desarrollo);
        this.posicionFila++;
      } // if
      else {
        this.posicionFila++;
        this.addCellColor(this.posicionColumna, this.posicionFila, "LA NOMINA NO TIENE PROVEEDORES", jxl.format.Colour.BLUE);
        this.toAddView(0, 80);
      } // else      
      if((personas!= null && !personas.isEmpty()) || (proveedores!= null && !proveedores.isEmpty())) {
        this.toAddView(0, 20);
        this.toAddView(1, 12);
        this.toAddView(2, 25);
        this.toAddView(3, 25);
        this.toAddView(4, 25);
        this.toAddView(5, 70);
        this.toAddView(13, 15);
        this.toAddView(14, 15);
        this.toAddView(15, 15);
        this.toAddView(16, 15);
        if(this.isCafu) {
          this.toAddView(17, 15);
          this.toAddView(18, 15);
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
  
  @Override
  protected void toAddTitulosEmpleado() throws Exception {
    this.posicionFila++;
    this.addCell(this.posicionColumna, this.posicionFila, "DESARRLLO", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 1, this.posicionFila, "CLAVE", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 2, this.posicionFila, "CATEGORIA", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 3, this.posicionFila, "DEPARTAMENTO", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 4, this.posicionFila, "PUESTO", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 5, this.posicionFila, "NOMBRE", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 6, this.posicionFila, "DOM", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 7, this.posicionFila, "LUN", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 8, this.posicionFila, "MAR", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 9, this.posicionFila, "MIE", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 10, this.posicionFila, "JUE", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 11, this.posicionFila, "VIE", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 12, this.posicionFila, "SAB", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 13, this.posicionFila, "PERCEPCIONES", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 14, this.posicionFila, "DEDUCCIONES", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 15, this.posicionFila, "RETENCIONES", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    this.addCell(this.posicionColumna+ 16, this.posicionFila, "NETO", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    if(this.isCafu) {
      this.addCell(this.posicionColumna+ 17, this.posicionFila, "SUELDO DIARIO", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
      this.addCell(this.posicionColumna+ 18, this.posicionFila, "SOBRE SUELDO", Alignment.CENTRE, Colour.YELLOW, Colour.BLACK, Boolean.FALSE);
    } // if  
  }
  
  @Override
  public void toEmpleado(Entity item, Double cajaChica) throws Exception {
    Map<String, Object> params= new HashMap<>();
    StringBuilder faltas      = new StringBuilder(Constantes.SEPARADOR);
    try {      
      this.posicionFila++;
      this.addCell(this.posicionColumna, this.posicionFila, item.toString("desarrollo"));
      this.addCell(this.posicionColumna+ 1, this.posicionFila, item.toString("clave"));
      this.addCell(this.posicionColumna+ 2, this.posicionFila, item.toString("categoria"));
      this.addCell(this.posicionColumna+ 3, this.posicionFila, item.toString("departamento"));
      this.addCell(this.posicionColumna+ 4, this.posicionFila, item.toString("puesto"));
      this.addCell(this.posicionColumna+ 5, this.posicionFila, item.toString("nombre"));
      this.addNumber(this.posicionColumna+ 13, this.posicionFila, Numero.toRedondearSat(item.toDouble("percepciones")), this.value);
      this.addNumber(this.posicionColumna+ 14, this.posicionFila, Numero.toRedondearSat(item.toDouble("deducciones")), this.value);
      this.addNumber(this.posicionColumna+ 15, this.posicionFila, Numero.toRedondearSat(item.toDouble("aportaciones")), this.value);
      this.addNumber(this.posicionColumna+ 16, this.posicionFila, Numero.toRedondearSat(item.toDouble("neto")), this.number);
      if(this.isCafu) {
        this.addNumber(this.posicionColumna+ 17, this.posicionFila, Numero.toRedondearSat(item.toDouble("sueldoImss")), this.value);
        this.addNumber(this.posicionColumna+ 18, this.posicionFila, Numero.toRedondearSat(item.toDouble("sobreSueldo")), this.value);
      } // if  
      this.subTotales.put("percepciones", this.subTotales.get("percepciones")+ item.toDouble("percepciones"));
      this.subTotales.put("deducciones", this.subTotales.get("deducciones")+ item.toDouble("deducciones"));
      this.subTotales.put("aportaciones", this.subTotales.get("aportaciones")+ item.toDouble("aportaciones"));
      this.subTotales.put("cajaChica", this.subTotales.get("cajaChica")+ cajaChica);
      this.subTotales.put("neto", this.subTotales.get("neto")+ item.toDouble("neto"));
      this.subTotales.put("sueldoImss", this.subTotales.get("sueldoImss")+ item.toDouble("sueldoImss"));
      this.subTotales.put("sobreSueldo", this.subTotales.get("sobreSueldo")+ item.toDouble("sobreSueldo"));
      //  RECUPERAR LAS FALTAS DEL PERSONAL Y COLOCAR UNA X INICIANDO DE SABADO A VIERNES SIN CONSIDERAR LOS DOMINGOS
      params.put("idEmpresaPersona", item.toString("idEmpresaPersona"));      
      params.put("idNomina", this.idNomina);      
      List<Entity> dias= (List<Entity>)DaoFactory.getInstance().toEntitySet("TcManticIncidentesDto", "dias", params);
      if(dias!= null && !dias.isEmpty()) 
        for (Entity dia: dias) 
          faltas.append(dia.toLong("dia"));
      for (int x= 1; x < 8; x++) 
        if(faltas.toString().contains(String.valueOf(x)))
          this.addCell(this.posicionColumna+ x+ 5, this.posicionFila, "", Alignment.CENTRE);
        else 
          this.addCell(this.posicionColumna+ x+ 5, this.posicionFila, "X", Alignment.CENTRE);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }

  @Override
  protected void toAddSubtotalesEmpleado(String desarrollo) throws Exception {
    this.posicionFila++;
    for (int x = 0; x < 13; x++) 
      this.addNumber(this.posicionColumna+ x, this.posicionFila, 0D, this.total);
    this.addNumber(this.posicionColumna+ 13, this.posicionFila, Numero.toRedondearSat(this.subTotales.get("percepciones")), this.total);
    this.addNumber(this.posicionColumna+ 14, this.posicionFila, Numero.toRedondearSat(this.subTotales.get("deducciones")), this.total);
    this.addNumber(this.posicionColumna+ 15, this.posicionFila, Numero.toRedondearSat(this.subTotales.get("aportaciones")), this.total);
    this.addNumber(this.posicionColumna+ 16, this.posicionFila, Numero.toRedondearSat(this.subTotales.get("neto")), this.total);
    if(this.isCafu) {
      this.addNumber(this.posicionColumna+ 17, this.posicionFila, Numero.toRedondearSat(this.subTotales.get("sueldoImss")), this.total);
      this.addNumber(this.posicionColumna+ 18, this.posicionFila, Numero.toRedondearSat(this.subTotales.get("sobreSueldo")), this.total);
    } // if  
    this.totales.put(desarrollo, this.subTotales.get("neto"));
  }
  
  @Override
  public void toProveedor(Entity item) throws Exception {
    try {      
      this.posicionFila++;
      this.addCell(this.posicionColumna, this.posicionFila, item.toString("desarrollo"));
      this.addCell(this.posicionColumna+ 1, this.posicionFila, item.toString("clave"));
      this.addCell(this.posicionColumna+ 2, this.posicionFila, "SUB CONTRATISTA");
      this.addCell(this.posicionColumna+ 3, this.posicionFila, item.toString("departamento"));
      this.addCell(this.posicionColumna+ 4, this.posicionFila, item.toString("nombre"));
      this.addNumber(this.posicionColumna+ 16, this.posicionFila, Numero.toRedondearSat(item.toDouble("total")), this.number);
      this.subTotales.put("total", this.subTotales.get("total")+ item.toDouble("total"));
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }

  @Override
  protected void toAddSubtotalesProveedor(String desarrollo) throws Exception {
    this.posicionFila++;
    for (int x= 0; x < 15; x++) 
      this.addNumber(this.posicionColumna+ x, this.posicionFila, 0D, this.total);
    this.addNumber(this.posicionColumna+ 16, this.posicionFila, Numero.toRedondearSat(this.subTotales.get("total")), this.total);
    this.totales.put(desarrollo, this.subTotales.get("total"));
  }
  
  public static void main(String ... args) throws Exception {
    Personas corte= new Personas(226L);
    LOG.info(corte.local());
  }
  
}
