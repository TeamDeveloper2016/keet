package mx.org.kaana.keet.nomina.reglas;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import jxl.CellView;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WriteException;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.Concepto;
import mx.org.kaana.keet.nomina.beans.Criterio;
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.Lote;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.archivo.XlsBase;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
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

public class Egresos extends XlsBase implements Serializable {
  
  private static final Log LOG = LogFactory.getLog(Egresos.class);
  private static final long serialVersionUID = -3364636967422678893L;
  
  private Long idNomina;
  private Long idContrato;
  private Entity nomina;
  private Map<String, Double> subTotales;
  private Map<String, Double> totales;
  private List<Double> resumen;
  private String path;
  private Double global;
  private int posicion;

  public Egresos() {
    this(-1L);  
  }
  
  public Egresos(Long idNomina) {
    this(idNomina, -1L);
  }

  public Egresos(Long idNomina, Long idContrato) {
    this.idNomina  = idNomina;
    this.idContrato= idContrato;
    this.subTotales= new HashMap<>();
    this.totales   = new HashMap<>();
    this.resumen   = new ArrayList<>();
    this.path      = "";
    this.init();
  }

  public Long getIdNomina() {
    return idNomina;
  }

  public Long getIdContrato() {
    return idContrato;
  }
  
  private void init() {
    this.subTotales.clear();
    this.totales.clear();
    this.resumen.add(0D);
    this.resumen.add(0D);
    this.resumen.add(0D);
    this.resumen.add(0D);
    this.global    = 0D;
    this.posicion  = 0;
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
      params.put("idContratoEstatus", Objects.equals(this.idContrato, -1L)? EContratosEstatus.COBRADO.getKey(): EContratosEstatus.TERMINADO.getKey());
      params.put("idContrato", this.idContrato);      
      if(Objects.equals(this.idNomina, -1L))
        this.nomina= (Entity)DaoFactory.getInstance().toEntity("VistaNominaDto", "ultima", params);
      else
        this.nomina= (Entity)DaoFactory.getInstance().toEntity("VistaNominaDto", "nomina", params);
      regresar= Archivo.toFormatNameFile("IMOX", "SEMANA-".concat(this.nomina.toString("semana")).concat(".").concat(EFormatos.XLS.name().toLowerCase()));
      List<Entity> contratos= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaNominaDto", "contratos", params);
      if(contratos!= null && !contratos.isEmpty()) {
        this.posicionFila   = 0;
        this.posicionColumna= 0;
        this.libro= Workbook.createWorkbook(new File(this.path.concat(regresar)));
        this.hoja = this.libro.createSheet("IMOX", 0);
        this.addCell(this.posicionColumna, this.posicionFila, "CONTROL DE PAGO DE DESTAJOS Y SUBCONTRATOS");
        for (Entity item: contratos) {
          LOG.info("------------------------CONTRATO ["+ item.toString("clave")+"]-------------------------------");
          this.toContrato(item);
          this.init();
          this.posicionFila++;
          this.posicionFila++;
          LOG.info("-----------------------------------------------------------------------");
          // break;
        } // for
        this.addCell(this.posicionColumna+ 1, this.posicionFila, "TOTAL GENERAL ADMINISTRATIVOS DE OBRA");
        this.addCellTotal(this.posicionColumna+ 2, this.posicionFila, "$0.00", Alignment.RIGHT, Boolean.FALSE);
        this.addCell(this.posicionColumna+ 3, this.posicionFila++, "(PENDIENTE)");
        this.addCell(this.posicionColumna+ 1, this.posicionFila, "TOTAL GENERAL ADMINISTRATIVO POR DIA");
        this.addCellTotal(this.posicionColumna+ 2, this.posicionFila, "$0.00", Alignment.RIGHT, Boolean.FALSE);
        this.addCell(this.posicionColumna+ 3, this.posicionFila++, "(PENDIENTE)");
        this.toAddView(0, 12);
        this.toAddView(1, 70);
      } // if
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
  
  public void toContrato(Entity contrato) throws Exception {
    Map<String, Object> params= new HashMap<>();
    List<Entity> lotes        = null;
    List<Concepto> model      = new ArrayList<>();
    List<Lote> fields         = new ArrayList<>();
    String anterior           = "";
    try {      
      params.put("idNomina", this.idNomina);      
      params.put("idTipoNomina", 1L);      
      params.put("idContrato", contrato.toLong("idContrato"));
      params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
      params.put("semana", this.nomina.toString("semana"));
      params.put("clave", this.toTokenClave(contrato));
      lotes= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaNominaDto", "destajos", params, Constantes.SQL_TODOS_REGISTROS);
      if(lotes!= null && !lotes.isEmpty()) {
        this.toOrderConceptos(model, lotes, 1L, 2L, model.size());
        this.toOrderConceptos(model, lotes, 2L, 2L, model.size());
        this.toOrderConceptos(model, lotes, 1L, 1L, model.size());
        this.toOrderConceptos(model, lotes, 2L, 1L, model.size());
        for (Entity item: lotes) {
          String lote= item.toString("lote").replaceAll("-", "");
          int index= model.indexOf(new Concepto(item.toString("codigo")));
          if(index>= 0) {
            Concepto concepto= model.get(index);
            concepto.put(lote, new Criterio(lote, item.toLong("idTipo"), item.toLong("idExtra"), item.toDouble("porcentaje"), item.toDouble("costo"), item.toDouble("anticipo"), item.toLong("idNomina"), item.toString("semana"), item.toLong("actual"), item));
            if(!Objects.equals(lote, anterior)) {
              fields.add(new Lote(lote, lote, "", ""));
              anterior= lote;
            } // if  
          } // if
          else
            throw new RuntimeException("El concepto ["+ item.toString("codigo")+ "] no existe en la consulta !");
        } // for
      } // if
      if(!model.isEmpty()) {
        this.posicionFila++;
        this.posicionFila++;
        this.addCell(this.posicionColumna, this.posicionFila, "EMPRESA:");
        this.addCell(this.posicionColumna+ 1, this.posicionFila++, contrato.toString("empresa"));
        this.addCell(this.posicionColumna, this.posicionFila, "CLIENTE:");
        this.addCell(this.posicionColumna+ 1, this.posicionFila++, contrato.toString("cliente"));
        this.addCell(this.posicionColumna, this.posicionFila, "OBRA:");
        this.addCell(this.posicionColumna+ 1, this.posicionFila++, contrato.toString("desarrollo"));
        this.addCell(this.posicionColumna, this.posicionFila, "FRENTE:");
        this.addCell(this.posicionColumna+ 1, this.posicionFila++, contrato.toString("contrato"));
        this.addCell(this.posicionColumna, this.posicionFila, "SEMANA:");
        if(Objects.equals(this.idNomina, -1L))
          this.addCellColor(this.posicionColumna+ 1, this.posicionFila++, "ACUMULADO");
        else
          this.addCellColor(this.posicionColumna+ 1, this.posicionFila++, this.nomina.toString("semana"));
        this.posicionFila++;
        this.addRow("DESTAJOS", model, 1L, 2L, fields);
        this.addRow("SUBCONTRATOS", model, 2L, 2L, fields);
        this.addRow("OBRA EXTRA / ADICIONALES DESTAJOS", model, 1L, 1L, fields);
        this.addRow("OBRA EXTRA / ADICIONALES SUBCONTRATOS", model, 2L, 1L, fields);
        this.posicionFila++;
        this.posicionFila++;
        this.toRowTotales(this.totales, fields, 0L, "TOTAL:", 0);
        this.posicionFila++;
        this.posicionFila++;
        this.addCell(this.posicionColumna+ 1, this.posicionFila++, "RESUMEN DE LOS TOTALES DEL CONTRATO");
        this.addCell(this.posicionColumna+ 1, this.posicionFila, "TOTAL DESTAJOS:");
        this.addCellCosto(this.posicionColumna+ 2, this.posicionFila++, Numero.formatear(Numero.MONEDA_SAT_DECIMALES, this.resumen.get(0)), Alignment.RIGHT);
        this.addCell(this.posicionColumna+ 1, this.posicionFila, "TOTAL SUBCONTRATOS:");
        this.addCellCosto(this.posicionColumna+ 2, this.posicionFila++, Numero.formatear(Numero.MONEDA_SAT_DECIMALES, this.resumen.get(1)), Alignment.RIGHT);
        this.addCell(this.posicionColumna+ 1, this.posicionFila, "TOTAL DESTAJOS OBRA EXTRA/ADICIONALES:");
        this.addCellCosto(this.posicionColumna+ 2, this.posicionFila++, Numero.formatear(Numero.MONEDA_SAT_DECIMALES, this.resumen.get(2)), Alignment.RIGHT);
        this.addCell(this.posicionColumna+ 1, this.posicionFila, "TOTAL SUBCONTRATOS OBRA EXTRA/ADICIONALES:");
        this.addCellCosto(this.posicionColumna+ 2, this.posicionFila++, Numero.formatear(Numero.MONEDA_SAT_DECIMALES, this.resumen.get(3)), Alignment.RIGHT);
        this.addCell(this.posicionColumna+ 1, this.posicionFila, "TOTAL:");
        this.addCellTotal(this.posicionColumna+ 2, this.posicionFila++, Numero.formatear(Numero.MONEDA_SAT_DECIMALES, this.global), Alignment.RIGHT, Boolean.TRUE);
      } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(model);
      Methods.clean(fields);
      Methods.clean(params);
    } // finally
  }
  
  private void toOrderConceptos(List<Concepto> model, List<Entity> lotes, Long idTipo, Long idExtra, int count) throws Exception {
    try {      
      for (Entity item: lotes) {
        if(Objects.equals(idTipo, item.toLong("idTipo")) && Objects.equals(idExtra, item.toLong("idExtra")))
          if(!model.contains(new Concepto(item.toString("codigo"))))
            model.add(new Concepto(count++, item.toString("codigo"), item.toString("nombre"), idTipo, idExtra));
      } // for
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }
  
  private void toAddView(int column, int characters) {
    CellView cell= this.hoja.getColumnView(column);
    cell.setSize(characters* 256 + 100);
    this.hoja.setColumnView(column, cell);      
  }
  
  private WritableCellFormat toCellColorFormat(Colour colour) throws WriteException {
    WritableFont cellFonts = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.WHITE);
    WritableCellFormat regresar= new WritableCellFormat(cellFonts);
    regresar.setBorder(jxl.format.Border.BOTTOM, jxl.format.BorderLineStyle.THIN);
    regresar.setAlignment(Alignment.CENTRE);
    regresar.setBackground(colour);
    return regresar;
  }  
  
  private WritableCellFormat toCellCostoFormat(Alignment alignment) throws WriteException {
    WritableFont cellFonts = new WritableFont(WritableFont.ARIAL, 11, WritableFont.NO_BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
    WritableCellFormat regresar= new WritableCellFormat(cellFonts);
    regresar.setAlignment(alignment);
    return regresar;
  }  
  
  private WritableCellFormat toCellTotalFormat(Alignment alignment, Boolean line) throws WriteException {
    WritableFont cellFonts = new WritableFont(WritableFont.ARIAL, 11, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
    WritableCellFormat regresar= new WritableCellFormat(cellFonts);
    if(line)
      regresar.setBorder(jxl.format.Border.TOP, jxl.format.BorderLineStyle.THIN);
    regresar.setAlignment(alignment);
    return regresar;
  }  
  
  private String toTokenClave(Entity contrato) {
    StringBuilder regresar = new StringBuilder();
    regresar.append(Cadena.rellenar(String.valueOf(contrato.toLong("idEmpresa")), 3, '0', true)).append(contrato.toLong("ejercicio")).append(Cadena.rellenar(String.valueOf(contrato.toLong("orden")), 3, '0', true));
    return regresar.toString();
  }

  private void addRow(String titulo, List<Concepto> model, Long idTipo, Long idExtra, List<Lote> fields) throws Exception {
    Double costo= 0D;
    try {
      this.addCell(this.posicionColumna, this.posicionFila++, titulo);
      this.addCellColor(this.posicionColumna, this.posicionFila, "CODIGO");
      this.addCellColor(this.posicionColumna+ 1, this.posicionFila, "CONCEPTO");
      int column= 2;
      for (Lote item: fields) {
        if(Objects.equals(idTipo, 1L) && Objects.equals(idExtra, 1L)) 
          this.toAddView(this.posicionColumna+ column, 17);
        this.addCellColor(this.posicionColumna+ (column++), this.posicionFila, item.getName());
      } // for
      this.posicionFila++;
      for (Concepto item: model) {
        if(Objects.equals(idTipo, item.getIdTipo()) && Objects.equals(idExtra, item.getIdExtra())) {
          this.addCell(this.posicionColumna, this.posicionFila, item.getCodigo());
          this.addCell(this.posicionColumna+ 1, this.posicionFila, item.getNombre());
          column= 2;
          for (Lote field: fields) {
            Criterio criterio= (Criterio)item.get(field.getField());
            if(criterio!= null) {
              this.addCellCosto(this.posicionColumna+ (column++), this.posicionFila, Numero.formatear(Numero.MONEDA_SAT_DECIMALES, criterio.getCosto()));
              costo= criterio.getCosto();
              if(Objects.equals(idTipo, 2L))
                costo= costo* Constantes.IMPORTE_NETO;
              if(this.subTotales.containsKey(this.posicion+ "|"+ field.getField())) 
                this.subTotales.put(this.posicion+ "|"+ field.getField(), this.subTotales.get(this.posicion+ "|"+field.getField())+ criterio.getCosto());
              else 
                this.subTotales.put(this.posicion+ "|"+ field.getField(), criterio.getCosto());
              if(this.totales.containsKey("0|"+ field.getField())) 
                this.totales.put("0|"+ field.getField(), this.totales.get("0|"+ field.getField())+ costo);
              else
                this.totales.put("0|"+ field.getField(), costo);
              this.resumen.set(this.posicion, this.resumen.get(this.posicion)+ costo);
              this.global+= costo;
            } // if  
            else
              this.addCellCosto(this.posicionColumna+ (column++), this.posicionFila, "");
          } // for
          this.posicionFila++;
        } // if
      } // for
      this.toRowTotales(this.subTotales, fields, idTipo, "SUB TOTAL:", this.posicion);
      this.posicion++;
      this.posicionFila++;
      this.posicionFila++;
    } // try
    catch(Exception e) {
      throw e;
    } // catch
  }
  
  private void toRowTotales(Map<String, Double> costos, List<Lote> fields, Long idTipo, String titulo, int key) throws Exception {
    try {
      this.addCellTotal(this.posicionColumna+ 1, this.posicionFila, titulo, Alignment.RIGHT, Boolean.TRUE);
      int column= 2;
      for (Lote field : fields) {
        if(costos.containsKey(key+ "|"+ field.getName()))
          this.addCellTotal(this.posicionColumna+ (column++), this.posicionFila, Numero.formatear(Numero.MONEDA_SAT_DECIMALES, costos.get(key+ "|"+ field.getName())), Alignment.CENTRE, Boolean.TRUE);
        else
          this.addCellTotal(this.posicionColumna+ (column++), this.posicionFila, "$ 0.00", Alignment.CENTRE, Boolean.TRUE);
      } // for
      if(Objects.equals(idTipo, 2L)) {
        this.posicionFila++;
        column= 2;
        this.addCellTotal(this.posicionColumna+ 1, this.posicionFila, "SUB-TOTAL (NETO):", Alignment.RIGHT, Boolean.FALSE);
        for (Lote field: fields) {
          if(costos.containsKey(key+ "|"+ field.getName()))
            this.addCellTotal(this.posicionColumna+ (column++), this.posicionFila, Numero.formatear(Numero.MONEDA_SAT_DECIMALES, costos.get(key+ "|"+ field.getName())* Constantes.IMPORTE_NETO), Alignment.CENTRE, Boolean.FALSE);
          else
            this.addCellTotal(this.posicionColumna+ (column++), this.posicionFila, "$ 0.00", Alignment.CENTRE, Boolean.FALSE);
        } // for
      } // if  
    } // try
    catch(Exception e) {
      throw e;
    } // catch
  }
  
  private void addCell(int column, int row, String data) throws Exception {
    try {
      Label label= new Label(column, row, data);
      this.hoja.addCell(label);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }
  
  private void addCellColor(int column, int row, String data) throws Exception {
    try {
      // Colour colour= new Colour(10000, "1", 221, 221, 221) {};
      Label label= new Label(column, row, data, this.toCellColorFormat(jxl.format.Colour.BLUE_GREY));
      this.hoja.addCell(label);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }
  
  private void addCellCosto(int column, int row, String data) throws Exception {
    this.addCellCosto(column, row, data, Alignment.CENTRE);
  }
  
  private void addCellCosto(int column, int row, String data, Alignment alignment) throws Exception {
    try {
      Label label= new Label(column, row, data, this.toCellCostoFormat(alignment));
      this.hoja.addCell(label);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }
  
  private void addCellTotal(int column, int row, String data, Alignment alignment, Boolean line) throws Exception {
    try {
      Label label= new Label(column, row, data, this.toCellTotalFormat(alignment, line));
      this.hoja.addCell(label);
    } // trt
    catch(Exception e) {
      throw e;
    } // catch
  }

  @Override
  public void finalize() {
    Methods.clean(this.subTotales);
    Methods.clean(this.totales);
    Methods.clean(this.resumen);
    super.finalize(); 
  }

  
  public static void main(String ... args) throws Exception {
    Egresos corte= new Egresos(-1L, 6L);
    LOG.info(corte.local());
  }
  
}
