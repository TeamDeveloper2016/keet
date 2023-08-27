package mx.org.kaana.keet.nomina.reglas;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
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
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import static mx.org.kaana.keet.nomina.reglas.Egresos.CAFU_PERSONAL_DIA;
import static mx.org.kaana.keet.nomina.reglas.Egresos.CAFU_PERSONAL_OBRA;
import static mx.org.kaana.keet.nomina.reglas.Egresos.CAFU_PERSONAL;
import static mx.org.kaana.keet.nomina.reglas.Egresos.GYLVI_PERSONAL_DIA;
import static mx.org.kaana.keet.nomina.reglas.Egresos.GYLVI_PERSONAL_OBRA;
import static mx.org.kaana.keet.nomina.reglas.Egresos.GYLVI_PERSONAL;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.archivo.XlsBase;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 15/06/2022
 *@time 07:42:00 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Comparativo extends XlsBase implements Serializable {
  
  private static final Log LOG = LogFactory.getLog(Comparativo.class);
  private static final long serialVersionUID = -3364636967422678893L;
  public static final String[] TITULOS= new String[] {"Desarrollos:", "Contratos:", "Destajos:", "Gente por el día admon:", "Porcentaje vs total fraccionamiento:", "Porcentaje vs nómina global:", "Gente por el día obra:", "Porcentaje vs total fraccionamiento:", "Porcentaje vs nómina global:", "Total por contrato en $:", "Total por fraccionamiento en $:", "Ponderado por contrato vs Fraccionamiento:", "Ponderado por contrato vs Global:", "Ponderado fraccionamiento vs global"};
  
  private Long idEmpresa;
  private Long idNomina;
  private Entity nomina;
  private Integer indice;
  private String path;
  private List<Long> desarrollos;
  private List<Long> contratos;
  private Map<Long, Long> relacion;
  private Map<Long, Integer> posiciones;
  private Map<Long, Double> destajos;
  private Map<Long, Double> desarrolloTotal;
  private Map<Long, Integer> desarrolloContratos;
  private Map<Long, Double> gentePorDia;
  private Map<Long, Double> subGentePorDia;
  private Map<Long, Double> gentePorObra;
  private Map<Long, Double> subGentePorObra;
  private Map<Long, Double> totalContrato;
  private Map<Long, Double> totalDesarrollo;
  private Double totalGeneral;
  private WritableCellFormat number;
  private WritableCellFormat negritas;

  public Comparativo() throws Exception {
    this(-1L, -1L);  
  }
  
  public Comparativo(Long idEmpresa, Long idNomina) throws Exception {
    this.idEmpresa= idEmpresa;
    this.idNomina = idNomina;
    this.indice   = 0;
    this.desarrollos    = new ArrayList<>();
    this.contratos      = new ArrayList<>();
    this.relacion       = new HashMap<>();
    this.posiciones     = new HashMap<>();
    this.destajos       = new HashMap<>();
    this.desarrolloTotal= new HashMap<>();
    this.desarrolloContratos= new HashMap<>();
    this.gentePorDia    = new HashMap<>();
    this.subGentePorDia = new HashMap<>();
    this.gentePorObra   = new HashMap<>();
    this.subGentePorObra= new HashMap<>();
    this.totalContrato  = new HashMap<>();
    this.totalDesarrollo= new HashMap<>();
    this.init();
  }

  private void init() throws Exception {
    this.contratos.clear();
    this.desarrollos.clear();
    this.desarrolloTotal.clear();
    this.desarrolloContratos.clear();
    this.destajos.clear();
    this.gentePorDia.clear();
    this.subGentePorDia.clear();
    this.gentePorObra.clear();
    this.subGentePorObra.clear();
    this.totalContrato.clear();
    this.totalDesarrollo.clear();
    this.totalGeneral= 0D;
    this.number      = this.toNumber(Alignment.RIGHT, Colour.WHITE, Colour.BLACK, Boolean.FALSE);
    this.negritas    = this.toNumberNegritas(Alignment.RIGHT, Colour.WHITE, Colour.BLACK, Boolean.FALSE);
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
  
  private String process() throws Exception {
    String regresar           = "";
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idNomina", this.idNomina);      
      params.put("idTipoNomina", 1L);      
      params.put("equals", "!=");      
      params.put("orden", "desc");      
      params.put("sucursales", this.idEmpresa);      
      if(Objects.equals(this.idNomina, -1L))
        this.nomina= (Entity)DaoFactory.getInstance().toEntity("VistaNominaDto", "secuencia", params);
      else
        this.nomina= (Entity)DaoFactory.getInstance().toEntity("VistaNominaDto", "nomina", params);
      params.put("idEmpresa", this.idEmpresa);
      List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaContratosMaterialesDto", "contratos", params);
      if(items!= null && !items.isEmpty()) {
        regresar= Archivo.toFormatNameFile(Constantes.ARCHIVO_PATRON_NOMBRE, "RESUMEN-SEMANA-".concat(nomina.toString("semana")).concat("_").concat(items.get(0).toString("empresa")).concat(".").concat(EFormatos.XLS.name().toLowerCase()));
        this.posicionFila   = 0;
        this.posicionColumna= 1;
        this.libro= Workbook.createWorkbook(new File(this.path.concat(regresar)));
        this.hoja = this.libro.createSheet(Constantes.ARCHIVO_PATRON_NOMBRE, 0);
        this.addCell(this.posicionColumna, this.posicionFila, "SEMANA:");
        this.addCellNegritas(this.posicionColumna+ 1, this.posicionFila++, nomina.toString("semana"), Alignment.LEFT);
        this.addCell(this.posicionColumna, this.posicionFila, "PERIODO:");
        this.addCellNegritas(this.posicionColumna+ 1, this.posicionFila++, Global.format(EFormatoDinamicos.FECHA_NOMBRE_MES, nomina.toString("inicio"))+ " al "+ Global.format(EFormatoDinamicos.FECHA_NOMBRE_MES, nomina.toString("termino")), Alignment.LEFT);
        this.posicionColumna= 1;
        this.addCellNegritas(this.posicionColumna--, this.posicionFila+ 1, "REPORTE DE NOMINA SEMANAL", Alignment.CENTRE);
        this.toHeaders(items);
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
  	  this.libro.write();
      this.libro.close();
    } // finally
    return regresar;
  }  

  public void toHeaders(List<Entity> items) throws Exception {
    Integer count    = 0;
    Long idDesarrollo= null;
    try {      
      this.posicionFila++;
      this.toAddView(this.posicionColumna, 35);
      this.addCell(this.posicionColumna, this.posicionFila+ 1, TITULOS[this.indice++], Alignment.RIGHT);
      this.addCell(this.posicionColumna, this.posicionFila+ 2, TITULOS[this.indice++], Alignment.RIGHT);
      this.posicionColumna++;
      for (Entity item: items) {
        this.toAddView(this.posicionColumna, 25);
        if(Objects.equals(idDesarrollo, null) || !Objects.equals(idDesarrollo, item.toLong("idDesarrollo"))) {
          if(!Objects.equals(idDesarrollo, null)) 
            count= 0;
          idDesarrollo= item.toLong("idDesarrollo");
          this.addCell(this.posicionColumna, this.posicionFila+ 1, item.toString("desarrollo"));
          this.desarrollos.add(idDesarrollo);
          this.posiciones.put(idDesarrollo, this.posicionColumna);
        } // if  
        this.addCell(this.posicionColumna++, this.posicionFila+ 2, item.toString("nombre"));
        this.contratos.add(item.toLong("idContrato"));
        
        // AGREGAR LA RELACION QUE EXISTE ENTRE LOS CONTRATOS Y LOS DESARROLLOS
        this.relacion.put(item.toLong("idContrato"), idDesarrollo);
        count++;
        this.desarrolloContratos.put(idDesarrollo, count);
      } // for
      // AGREGAR LA CANTIDAD DE CONTRATOS POR DESARROLLO
      this.toAddView(this.posicionColumna, 25);
      this.clean();
      this.toDestajos();
    } // try
    catch (Exception e) {
      throw e;     
    } // catch	
  }

  public void toDestajos() throws Exception {
    Map<String, Object> params= new HashMap<>();
    try {      
      this.posicionColumna= 0;
      this.posicionFila+= 3;
      params.put("idEmpresa", this.idEmpresa);
      params.put("idNomina", this.nomina.toLong("idNomina"));
      this.addCellNegritas(this.posicionColumna++, this.posicionFila, TITULOS[this.indice++], Alignment.RIGHT);
      List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaContratosMaterialesDto", "destajos", params);
      if(items!= null && !items.isEmpty())
        for (Long idContrato: this.contratos) {
          int index= this.toIndexOf(items, new Entity(idContrato));
          if(index>= 0) {
            this.destajos.put(idContrato, items.get(index).toDouble("importe"));
            this.destajos.put(0L, this.destajos.get(0L)+ items.get(index).toDouble("importe"));
            this.totalGeneral+= items.get(index).toDouble("importe");
            this.addDouble(this.posicionColumna++, this.posicionFila, Numero.toRedondearSat(items.get(index).toDouble("importe")), this.negritas);
            
            // ESTE ES EL TOTAL DE LOS DESATAJOS POR DESARROLLO
            this.desarrolloTotal.put(items.get(index).toLong("idDesarrollo"), this.desarrolloTotal.get(items.get(index).toLong("idDesarrollo"))+ items.get(index).toDouble("importe"));
          } // if
          else
            this.addDouble(this.posicionColumna++, this.posicionFila, Numero.toRedondearSat(0D), this.negritas);
        } // for
      this.addDouble(this.posicionColumna, this.posicionFila, Numero.toRedondearSat(this.destajos.get(0L)), this.negritas);
      this.toPersonalPorDia();
    } // try
    catch (Exception e) {
      throw e;     
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }

  public void toPersonalPorDia() {
    Map<String, Object> params = new HashMap<>();
    try {      
      this.posicionColumna= 0;
      this.posicionFila+= 1;
      params.put("idEmpresa", this.idEmpresa);
      params.put("idNomina", this.nomina.toLong("idNomina"));
      this.addCellNegritas(this.posicionColumna, this.posicionFila, TITULOS[this.indice++], Alignment.RIGHT);
      this.addCellNegritas(this.posicionColumna, this.posicionFila+ 4, TITULOS[this.indice+ 2], Alignment.RIGHT);
      switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
        case "cafu":
          params.put("puestosPorDia", CAFU_PERSONAL_DIA);      
          params.put("puestosPorObra", CAFU_PERSONAL_OBRA);      
          params.put("personal", CAFU_PERSONAL);      
          break;
        case "gylvi":
          params.put("puestosPorDia", GYLVI_PERSONAL_DIA);      
          params.put("puestosPorObra", GYLVI_PERSONAL_OBRA);      
          params.put("personal", GYLVI_PERSONAL);      
          break;
        case "triana":
          params.put("puestosPorDia", CAFU_PERSONAL_DIA);      
          params.put("puestosPorObra", CAFU_PERSONAL_OBRA);      
          params.put("personal", CAFU_PERSONAL);      
          break;
        default:  
          params.put("puestosPorDia", CAFU_PERSONAL_DIA);      
          params.put("puestosPorObra", CAFU_PERSONAL_OBRA);      
          params.put("personal", CAFU_PERSONAL);      
          break;
      } // switch
      List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaContratosMaterialesDto", "personalPorDiaObra", params);
      if(items!= null && !items.isEmpty())
        for (Long idDesarrollo: this.desarrollos) {
          int index= this.toIndexOf(items, new Entity(idDesarrollo));
          if(index>= 0) {
            this.gentePorDia.put(idDesarrollo, items.get(index).toDouble("porDia"));
            this.gentePorDia.put(0L, this.gentePorDia.get(0L)+ items.get(index).toDouble("porDia"));
            this.addDouble(this.posiciones.get(idDesarrollo), this.posicionFila, Numero.toRedondearSat(items.get(index).toDouble("porDia")), this.number);
            this.totalGeneral+= items.get(index).toDouble("porDia");
            
            this.gentePorObra.put(idDesarrollo, items.get(index).toDouble("porObra"));
            this.gentePorObra.put(0L, this.gentePorObra.get(0L)+ items.get(index).toDouble("porObra"));
            this.addDouble(this.posiciones.get(idDesarrollo), this.posicionFila+ 4, Numero.toRedondearSat(items.get(index).toDouble("porObra")), this.number);
            this.totalGeneral+= items.get(index).toDouble("porObra");
          } // if
          else {
            this.addDouble(this.posiciones.get(idDesarrollo), this.posicionFila, Numero.toRedondearSat(0D), this.number);
            this.addDouble(this.posiciones.get(idDesarrollo), this.posicionFila+ 4, Numero.toRedondearSat(0D), this.number);
          } // else  
        } // for
      // ES EL TOTAL DE PERSONAS POR EL DIA ADMIN
      this.addDouble(this.contratos.size()+ 1, this.posicionFila, Numero.toRedondearSat(this.gentePorDia.get(0L)), this.negritas);
      // ES EL TOTAL DE PERSONAL POR EL DIA DE OBRA
      this.addDouble(this.contratos.size()+ 1, this.posicionFila+ 4, Numero.toRedondearSat(this.gentePorObra.get(0L)), this.negritas);
      // ES EL TOTAL GENERAL DE LA SUMA DE LOS DESTAJOS MAS EL PERSONAL POR EL DIA ADMIN Y EL PERSONAL POR EL DIA DE OBRA
      this.addCellNegritas(this.posicionColumna, this.posicionFila+ 8, TITULOS[this.indice+ 5], Alignment.RIGHT);
      this.addDouble(this.contratos.size()+ 1, this.posicionFila+ 8, Numero.toRedondearSat(this.totalGeneral), this.negritas);
      
      this.toPorcentajesGenerales();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  private void toPorcentajesGenerales() {
    Double sumaPorDia       = 0D;
    Double sumaPorObra      = 0D;
    Double porcentajeTotal  = 0D;
    Double porcentajeGeneral= 0D;
    Map<String, Object> params= new HashMap<>();
    try {      
      this.posicionColumna= 0;
      this.posicionFila+= 1;
      params.put("idEmpresa", this.idEmpresa);
      params.put("idNomina", this.nomina.toLong("idNomina"));
      // TITULOS DEL PERSONAL POR EL DIA ADMIN
      this.addCell(this.posicionColumna, this.posicionFila, TITULOS[this.indice], Alignment.RIGHT);
      this.addCell(this.posicionColumna, this.posicionFila+ 1, TITULOS[this.indice+ 1], Alignment.RIGHT);
      // TITULOS DEL PERSONAL POR EL DIA OBRA
      this.addCell(this.posicionColumna, this.posicionFila+ 4, TITULOS[this.indice+ 3], Alignment.RIGHT);
      this.addCell(this.posicionColumna, this.posicionFila+ 5, TITULOS[this.indice+ 4], Alignment.RIGHT);
      for (Long idDesarrollo: this.desarrollos) {
        porcentajeTotal  = Objects.equals(this.gentePorDia.get(0L), 0D)? 0D: this.gentePorDia.get(idDesarrollo)/ this.gentePorDia.get(0L)* 100;
        porcentajeGeneral= Objects.equals(this.totalGeneral, 0D)? 0D: this.gentePorDia.get(idDesarrollo)/ this.totalGeneral* 100;
        this.addDouble(this.posiciones.get(idDesarrollo), this.posicionFila, Numero.toRedondearSat(porcentajeTotal), this.number);
        this.addDouble(this.posiciones.get(idDesarrollo), this.posicionFila+ 1, Numero.toRedondearSat(porcentajeGeneral), this.number);
        sumaPorDia+= porcentajeTotal;

        porcentajeTotal  = Objects.equals(this.gentePorObra.get(0L), 0D)? 0D: this.gentePorObra.get(idDesarrollo)/ this.gentePorObra.get(0L)* 100;
        porcentajeGeneral= Objects.equals(this.totalGeneral, 0D)? 0D: this.gentePorObra.get(idDesarrollo)/ this.totalGeneral* 100;
        this.addDouble(this.posiciones.get(idDesarrollo), this.posicionFila+ 4, Numero.toRedondearSat(porcentajeTotal), this.number);
        this.addDouble(this.posiciones.get(idDesarrollo), this.posicionFila+ 5, Numero.toRedondearSat(porcentajeGeneral), this.number);
        sumaPorObra+= porcentajeTotal;
      } // for
      this.addDouble(this.contratos.size()+ 1, this.posicionFila, Numero.toRedondearSat(sumaPorDia), this.negritas);
      this.addDouble(this.contratos.size()+ 1, this.posicionFila+ 5, Numero.toRedondearSat(sumaPorObra), this.negritas);
      this.toPorcentajesTotales();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  private void toPorcentajesTotales() {
    Double destajo   = 0D;
    Double base      = 0D;
    Double porcentaje= 0D;
    try {      
      this.posicionColumna= 0;
      // TITULOS DE LOS PORCENTAJES GENERALES
      this.addCell(this.posicionColumna, this.posicionFila+ 9, TITULOS[this.indice+ 7], Alignment.RIGHT);
      this.addCell(this.posicionColumna, this.posicionFila+ 10, TITULOS[this.indice+ 8], Alignment.RIGHT);
      this.posicionColumna++;
      for (Long idContrato: this.contratos) {
        destajo= this.desarrolloTotal.get(this.relacion.get(idContrato));
        base   = Objects.equals(destajo, 0D)? 100/this.desarrolloContratos.get(this.relacion.get(idContrato)): this.destajos.get(idContrato)/ destajo* 100;
        this.addDouble(this.posicionColumna, this.posicionFila+ 9, Numero.toRedondearSat(base), this.number);
        
        // COLOCAR EL IMPORTE DEL SUBTOTAL DE GENTE POR EL DÍA MULTIPLICANDO EL PORCENTAJE GLOBAL POR EL IMPORTE POR EL DÍA
        porcentaje= (base/ 100)* this.gentePorDia.get(this.relacion.get(idContrato));
        this.addDouble(this.posicionColumna, this.posicionFila+ 2, Numero.toRedondearSat(porcentaje), this.negritas);
        this.subGentePorDia.put(idContrato, porcentaje);
                
        // COLOCAR EL IMPORTE DEL SUBTOTAL DE GENTE POR OBRA MULTIPLICANDO EL PORCENTAJE GLOBAL POR EL IMPORTE POR OBRA
        porcentaje= (base/ 100)* this.gentePorObra.get(this.relacion.get(idContrato));
        this.addDouble(this.posicionColumna, this.posicionFila+ 6, Numero.toRedondearSat(porcentaje), this.negritas);
        this.subGentePorObra.put(idContrato, porcentaje);
        
        // CALCULAR EL SUBTOTAL DE LOS DESTAJOS MAS LOS SUBTOTALES DE LA GENTE POR EL DÍA Y LOS DE OBRA
        porcentaje= this.destajos.get(idContrato)+ this.subGentePorDia.get(idContrato)+ this.subGentePorObra.get(idContrato);
        this.addDouble(this.posicionColumna, this.posicionFila+ 7, Numero.toRedondearSat(porcentaje), this.negritas);
        this.totalContrato.put(idContrato, porcentaje);
        this.totalDesarrollo.put(this.relacion.get(idContrato), this.totalDesarrollo.get(this.relacion.get(idContrato))+ porcentaje);
        
        // CALCULAR EL PORCENTAJE GLOBAL UNA VEZ QUE YA SE TIENEN LOS IMPORTE GLOBALES POR CONTRATO
        porcentaje= Objects.equals(this.totalGeneral, 0D)? 0D: this.totalContrato.get(idContrato)/ this.totalGeneral* 100;
        this.addDouble(this.posicionColumna++, this.posicionFila+ 10, Numero.toRedondearSat(porcentaje), this.number);
      } // for
      this.toTotalesGeneral();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

  private void toTotalesGeneral() {
    Double suma      = 0D;
    Double porcentaje= 0D;
    try {  
      this.posicionColumna= 0;
      // TITULOS DE LOS TOTALES GENERALES
      this.addCell(this.posicionColumna, this.posicionFila+ 8, TITULOS[this.indice+ 6], Alignment.RIGHT);
      this.addCellNegritas(this.posicionColumna, this.posicionFila+ 11, TITULOS[this.indice+ 9], Alignment.RIGHT);
      for (Long idDesarrollo: this.desarrollos) {
        this.addDouble(this.posiciones.get(idDesarrollo), this.posicionFila+ 8, Numero.toRedondearSat(this.totalDesarrollo.get(idDesarrollo)), this.number);
        porcentaje= Objects.equals(this.totalGeneral, 0D)? 0D: this.totalDesarrollo.get(idDesarrollo)/ this.totalGeneral* 100;
        this.addDouble(this.posiciones.get(idDesarrollo), this.posicionFila+ 11, Numero.toRedondearSat(porcentaje), this.negritas);
        suma+= porcentaje;
      } // for
      this.addDouble(this.contratos.size()+ 1, this.posicionFila+ 11, Numero.toRedondearSat(suma), this.negritas);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  };

  public int toIndexOf(List<Entity> list, Entity look) {
    int regresar= 0;
    if(list.size()> 0)
      for (Entity item: list) {
        if(Objects.equals(item.getKey(), look.getKey()))
          break;
        else
          regresar++;
      } // for
    return regresar>= list.size()? -1: regresar;
  } 
  
  private void clean() {
    for (Long item : this.contratos) {
      this.destajos.put(item, 0D);
      this.gentePorDia.put(item, 0D);
      this.subGentePorObra.put(item, 0D);
      this.totalContrato.put(item, 0D);
    } // for
    this.destajos.put(0L, 0D);
    this.gentePorDia.put(0L, 0D);
    this.subGentePorObra.put(0L, 0D);
    for (Long item : this.desarrollos) {
      this.desarrolloTotal.put(item, 0D);
      this.gentePorDia.put(item, 0D);
      this.gentePorObra.put(item, 0D);
      this.totalDesarrollo.put(item, 0D);
    } // for
    this.gentePorDia.put(0L, 0D);
    this.gentePorObra.put(0L, 0D);
    this.totalContrato.put(0L, 0D);
  } 
  
  @Override
  public void finalize() {
    Methods.clean(this.contratos);
    Methods.clean(this.desarrollos);
    Methods.clean(this.destajos);
    Methods.clean(this.gentePorDia);
    Methods.clean(this.subGentePorDia);
    Methods.clean(this.gentePorObra);
    Methods.clean(this.subGentePorObra);
    Methods.clean(this.totalContrato);
    Methods.clean(this.totalDesarrollo);
    super.finalize(); 
  }
  
  public static void main(String ... args) throws Exception {
    // Comparativo corte= new Comparativo(1L, 123L);
    Comparativo corte= new Comparativo(1L, -1L);
    LOG.info(corte.local());
  }
  
}
