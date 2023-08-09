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
import jxl.write.WritableCellFormat;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.keet.nomina.beans.Corte;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.archivo.XlsBase;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
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

public class ManoObra extends XlsBase implements Serializable {
  
  private static final Log LOG = LogFactory.getLog(ManoObra.class);
  private static final long serialVersionUID = -3364636967422678893L;
  
  private Long idDesarrollo;
  private Long idNomina;
  private Long idContrato;
  private Entity nomina;
  private List<Corte> totales;
  private List<Corte> nominas;
  private String path;
  private WritableCellFormat negritas;
  private WritableCellFormat porcentaje;
  
  public ManoObra() throws Exception {
    this(-1L);  
  }
  
  public ManoObra(Long idNomina) throws Exception {
    this(idNomina, -1L);
  }

  public ManoObra(Long idNomina, Long idDesarrollo) throws Exception {
    this(idNomina, idDesarrollo, -1L);
  }
  
  public ManoObra(Long idNomina, Long idDesarrollo, Long idContrato) throws Exception {
    this.idNomina    = idNomina;
    this.idDesarrollo= idDesarrollo;
    this.idContrato  = idContrato;
    this.totales     = new ArrayList<>();
    this.nominas     = new ArrayList<>();
    this.path        = "";
    this.negritas    = this.toCellFormatNegritas(Alignment.RIGHT);
    this.porcentaje  = this.toCellFormatPorcentaje(Alignment.RIGHT);
  }

  public Long getIdNomina() {
    return idNomina;
  }

  public Long getIdContrato() {
    return idContrato;
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
    List<Entity> semanas      = null;
    List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idNomina", this.idNomina);      
      params.put("idTipoNomina", 1L);      
      params.put("idDesarrollo", this.idDesarrollo);      
      params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
      params.put("idContrato", this.idContrato);
      params.put("operador", ">");
      semanas= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaNominaDto", "pagadas", params);
      if(semanas!= null && !semanas.isEmpty()) {
        columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
        columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
        UIBackingUtilities.toFormatEntitySet(semanas, columns);
        List<Entity> contratos= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaNominaDto", "listado", params);
        regresar= Archivo.toFormatNameFile("IMOX", "MANO_DE_OBRA".concat(".").concat(EFormatos.XLS.name().toLowerCase()));
        this.posicionFila   = 0;
        this.posicionColumna= 0;
        this.libro= Workbook.createWorkbook(new File(this.path.concat(regresar)));
        this.hoja = this.libro.createSheet("IMOX", 0);
        this.addCell(this.posicionColumna, this.posicionFila++, "MANO DE OBRA");
        this.addCell(this.posicionColumna++, this.posicionFila, "Fecha corte:");
        this.addCell(this.posicionColumna, this.posicionFila, Fecha.getHoy());
        this.posicionColumna= 4;
        int temporal= this.posicionFila;
        for (Entity iterator: semanas) {
          // COLOCAR TITULOS DE LAS COLUMNAS
          this.nomina= iterator;
          this.toAddView(this.posicionColumna, 15);
          this.addCell(this.posicionColumna, this.posicionFila++, nomina.toString("semana"), Alignment.CENTRE);
          this.addCell(this.posicionColumna, this.posicionFila++, nomina.toString("inicio"), Alignment.CENTRE);
          this.addCell(this.posicionColumna++, this.posicionFila++, nomina.toString("termino"), Alignment.CENTRE);
          this.posicionFila= temporal;
        } // for
        int count= 4;
        if(contratos!= null && !contratos.isEmpty()) {
          for (Entity iterator: semanas) {
            this.nomina= iterator;
            // INICIALIZAR LAS NOMINAS DE CADA CONTRATO CON EL VALOR DE CERO PARA SUMAR SUS VALORES
            for (Entity item: contratos) {
              this.nominas.add(new Corte(item.toLong("idContrato"), this.nomina.toLong("idNomina"), 0D, 0D, item.toDouble("total"), item.toDouble("destajos"), item.toDouble("subcontratados"), item.toDouble("porElDia")));
              this.totales.add(new Corte(item.toLong("idContrato"), this.nomina.toLong("idNomina"), 0D, 0D, item.toDouble("total"), item.toDouble("destajos"), item.toDouble("subcontratados"), item.toDouble("porElDia")));
            } // for
            this.posicionFila= 4;
            LOG.info("------------------------NOMINA ["+ this.nomina.toString("semana")+ ":"+ count+ "]-------------------------------");
            for (Entity item: contratos) {
              if(Objects.equals(count, 4))
                this.addCell(1, 0, item.toString("desarrollo"));
              this.toDestajo(item, count, semanas.size());
              this.posicionFila++;
            } // for
            for (Entity item: contratos) {
              this.toPorDia(item, count, semanas.size());
              this.posicionFila++;
            } // for
            for (Entity item: contratos) {
              this.toGlobal(item, count, semanas.size());
              this.posicionFila++;
            } // for
            count++;
          } // for 
          this.toAddView(0, 30);
          this.toAddView(1, 40);
          this.toAddView(2, 15);
          this.toAddView(0, 25);
        } // if
        else {
          this.posicionFila++;
          this.addCellColor(this.posicionColumna, this.posicionFila, "EL DESARROLLO NO CONTRATO NO TIENE CONTRATOS", jxl.format.Colour.BLUE);
          this.toAddView(0, 80);
        } // else
      } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(columns);
  	  this.libro.write();
      this.libro.close();
    } // finally
    return regresar;
  }  
  
  public void toDestajo(Entity contrato, int count, int semanas) throws Exception {
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idNomina", this.nomina.toLong("idNomina"));      
      params.put("idTipoNomina", 1L);      
      params.put("idContrato", contrato.toLong("idContrato"));
      params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
      params.put("idDesarrollo", this.idDesarrollo);
      params.put("operador", "");
      // RECUPERAR EL COSTO DE LA NOMINA DE DESTAJOS MAS LA NOMINA DE MANO DE OBRA
      Entity destajo= (Entity)DaoFactory.getInstance().toEntity("VistaNominaDto", "listado", params);
      Corte corte   = new Corte(contrato.toLong("idContrato"), this.nomina.toLong("idNomina"));
      int index     = this.nominas.indexOf(corte);
      if(index>= 0)
        corte= this.nominas.get(index);
      if(!Objects.equals(destajo, null)) 
        corte.addDestajo(destajo.toDouble("costo"));
      this.posicionColumna= 0;
      if(Objects.equals(count, 4)) {
        this.addCellColor(this.posicionColumna++, this.posicionFila, "CENTRO DE COSTOS", Alignment.CENTRE, jxl.format.Colour.BLUE);
        this.addCellColor(this.posicionColumna, this.posicionFila++, "DESTAJO", Alignment.CENTRE, jxl.format.Colour.BLUE);
        this.posicionColumna= 0;
        this.addCell(this.posicionColumna++, this.posicionFila, contrato.toString("contrato"));
        this.addCell(this.posicionColumna, this.posicionFila, "MONTO DE MANO DE OBRA:", Alignment.RIGHT);
        this.addDouble(this.posicionColumna+ 1, this.posicionFila++, corte.getDestajos(), this.negritas);
        this.addCell(this.posicionColumna, this.posicionFila, "MANO DE OBRA PAGADA:", Alignment.RIGHT);
        String sum= String.format("SUMA(%s:%s)", this.toColumnName(this.posicionColumna+ 3, this.posicionFila), this.toColumnName(semanas+ 3, this.posicionFila));
        this.addFormula(this.posicionColumna+ 1, this.posicionFila, sum, this.negritas);
        this.addCellColor(this.posicionColumna+ 1, this.posicionFila- 2, null, Alignment.CENTRE, jxl.format.Colour.BLUE);
        this.addCellColor(this.posicionColumna+ 2, this.posicionFila- 2, null, Alignment.CENTRE, jxl.format.Colour.BLUE);
        this.posicionColumna= 0;
      } // if  
      else
        this.posicionFila+=2;
      this.addCellColor(this.posicionColumna+ count, this.posicionFila- 2, null, Alignment.CENTRE, jxl.format.Colour.BLUE);
      
      String value= String.format("SI(%s=0, 0, (%s*100)/%s)", this.toColumnName(2, this.posicionFila- 1), this.toColumnName(this.posicionColumna+ count, this.posicionFila), this.toColumnName(2, this.posicionFila- 1));
      this.addFormula(this.posicionColumna+ count, this.posicionFila-1, value, this.porcentaje);
      this.addDouble(this.posicionColumna+ count, this.posicionFila, corte.getDestajo(), this.negritas);
      // this.addCell(this.posicionColumna+ count, this.posicionFila, corte.getDestajo()+ ":"+ this.nomina.toString("semana"));
      this.posicionFila++;
      index= this.totales.indexOf(corte);
      if(index>= 0)
        this.totales.get(index).addDestajo(corte.getDestajo());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }

  public void toPorDia(Entity contrato, int count, int semanas) throws Exception {
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idNomina", this.nomina.toLong("idNomina"));      
      params.put("idTipoNomina", 1L);      
      params.put("idContrato", contrato.toLong("idContrato"));
      params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
      params.put("idDesarrollo", this.idDesarrollo);
      params.put("operador", "");
      // RECUPERAR EL COSTO DE LA NOMINA DE DESTAJOS MAS LA NOMINA DE MANO DE OBRA
      Entity porDia= (Entity)DaoFactory.getInstance().toEntity("TcNominasContratosCostosDto", "contrato", params);
      Corte corte   = new Corte(contrato.toLong("idContrato"), this.nomina.toLong("idNomina"));
      int index     = this.nominas.indexOf(corte);
      if(index>= 0)
        corte= this.nominas.get(index);
      if(!Objects.equals(porDia, null)) 
        corte.addPorDia(porDia.toDouble("costo"));
      this.posicionColumna= 0;
      if(Objects.equals(count, 4)) {
        this.addCellColor(this.posicionColumna++, this.posicionFila, "CENTRO DE COSTOS", Alignment.CENTRE, jxl.format.Colour.BROWN);
        this.addCellColor(this.posicionColumna, this.posicionFila++, "GENTE POR DIA", Alignment.CENTRE, jxl.format.Colour.BROWN);
        this.posicionColumna= 0;
        this.addCell(this.posicionColumna++, this.posicionFila, contrato.toString("contrato"));
        this.addCell(this.posicionColumna, this.posicionFila, "MONTO DE MANO DE OBRA:", Alignment.RIGHT);
        this.addDouble(this.posicionColumna+ 1, this.posicionFila++, corte.getPorElDia(), this.negritas);
        this.addCell(this.posicionColumna, this.posicionFila, "MANO DE OBRA PAGADA:", Alignment.RIGHT);
        String sum= String.format("SUMA(%s:%s)", this.toColumnName(this.posicionColumna+ 3, this.posicionFila), this.toColumnName(semanas+ 3, this.posicionFila));
        this.addFormula(this.posicionColumna+ 1, this.posicionFila, sum, this.negritas);
        this.addCellColor(this.posicionColumna+ 1, this.posicionFila- 2, null, Alignment.CENTRE, jxl.format.Colour.BROWN);
        this.addCellColor(this.posicionColumna+ 2, this.posicionFila- 2, null, Alignment.CENTRE, jxl.format.Colour.BROWN);
        this.posicionColumna= 0;
      } // if
      else
        this.posicionFila+=2;
      this.addCellColor(this.posicionColumna+ count, this.posicionFila- 2, null, Alignment.CENTRE, jxl.format.Colour.BROWN);
      
      String value= String.format("SI(%s=0, 0, (%s*100)/%s)", this.toColumnName(2, this.posicionFila- 1), this.toColumnName(this.posicionColumna+ count, this.posicionFila), this.toColumnName(2, this.posicionFila- 1));
      this.addFormula(this.posicionColumna+ count, this.posicionFila-1, value, this.porcentaje);
      this.addDouble(this.posicionColumna+ count, this.posicionFila, corte.getPorDia(), this.negritas);
      this.posicionFila++;
      
      index= this.totales.indexOf(corte);
      if(index>= 0) 
        this.totales.get(index).addPorDia(corte.getPorDia());
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }

  public void toGlobal(Entity contrato, int count, int semanas) throws Exception {
    try {      
      Corte corte= new Corte(contrato.toLong("idContrato"), this.nomina.toLong("idNomina"));
      int index  = this.totales.indexOf(corte);
      if(index>= 0)
        corte= this.totales.get(index);
      this.posicionColumna= 0;
      if(Objects.equals(count, 4)) {
        this.addCellColor(this.posicionColumna++, this.posicionFila, "CENTRO DE COSTOS", Alignment.CENTRE, jxl.format.Colour.DARK_PURPLE);
        this.addCellColor(this.posicionColumna, this.posicionFila++, "GLOBAL", Alignment.CENTRE, jxl.format.Colour.DARK_PURPLE);
        this.posicionColumna= 0;
        this.addCell(this.posicionColumna++, this.posicionFila, contrato.toString("contrato"));
        this.addCell(this.posicionColumna, this.posicionFila, "MONTO DE MANO DE OBRA:", Alignment.RIGHT);
        this.addDouble(this.posicionColumna+ 1, this.posicionFila++, Numero.toRedondearSat(corte.getDestajos()+ corte.getPorElDia()), this.negritas);
        this.addCell(this.posicionColumna, this.posicionFila, "MANO DE OBRA PAGADA:", Alignment.RIGHT);
        String sum= String.format("SUMA(%s:%s)", this.toColumnName(this.posicionColumna+ 3, this.posicionFila), this.toColumnName(semanas+ 3, this.posicionFila));
        this.addFormula(this.posicionColumna+ 1, this.posicionFila, sum, this.negritas);
        this.addCellColor(this.posicionColumna+ 1, this.posicionFila- 2, null, Alignment.CENTRE, jxl.format.Colour.DARK_PURPLE);
        this.addCellColor(this.posicionColumna+ 2, this.posicionFila- 2, null, Alignment.CENTRE, jxl.format.Colour.DARK_PURPLE);
        this.posicionColumna= 0;
      } // if  
      else
        this.posicionFila+=2;
      this.addCellColor(this.posicionColumna+ count, this.posicionFila- 2, null, Alignment.CENTRE, jxl.format.Colour.DARK_PURPLE);
      
      String value= String.format("SI(%s=0, 0, (%s*100)/%s)", this.toColumnName(2, this.posicionFila- 1), this.toColumnName(this.posicionColumna+ count, this.posicionFila), this.toColumnName(2, this.posicionFila- 1));
      this.addFormula(this.posicionColumna+ count, this.posicionFila-1, value, this.porcentaje);
      this.addDouble(this.posicionColumna+ count, this.posicionFila, Numero.toRedondearSat(corte.getDestajo()+ corte.getPorDia()), this.negritas);
      this.posicionFila++;
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }

  @Override
  public void finalize() {
    Methods.clean(this.totales);
    Methods.clean(this.nominas);
    super.finalize(); 
  }
  
  public static void main(String ... args) throws Exception {
    ManoObra corte= new ManoObra(116L, 12L);
    LOG.info(corte.local());
  }
  
}
