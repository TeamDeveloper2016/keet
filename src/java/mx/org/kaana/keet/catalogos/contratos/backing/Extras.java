package mx.org.kaana.keet.catalogos.contratos.backing;

import java.io.File;
import java.io.Serializable;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.db.dto.TcKeetContratosDto;
import mx.org.kaana.keet.ingresos.beans.Factura;
import mx.org.kaana.keet.ingresos.beans.Ingreso;
import mx.org.kaana.keet.ingresos.enums.EClaveCatalogo;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.comun.IBaseStorage;
import mx.org.kaana.mantic.db.dto.TcManticClientesDto;
import mx.org.kaana.mantic.inventarios.comun.IBaseImportar;
import mx.org.kaana.keet.ingresos.reglas.Transaccion;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.formato.Variables;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.db.dto.TcManticVentasDto;
import mx.org.kaana.mantic.libs.factura.beans.Concepto;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.StreamedContent;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 4/04/2022
 *@time 04:44:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "keetCatalogosContratosExtras")
@ViewScoped
public class Extras extends IBaseImportar implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Extras.class);
  private static final long serialVersionUID= 327393488565639367L;

	protected EAccion accion;	
  protected Ingreso ingreso;
  protected Factura comprobante;
	private TcKeetContratosDto contrato;
  protected TcManticClientesDto cliente;
  protected List<Articulo> articulos;
	private UISelectEntity ikSerie;
	private UISelectEntity ikTipoComprobante;

  public TcKeetContratosDto getContrato() {
    return contrato;
  }

  public TcManticClientesDto getCliente() {
    return cliente;
  }
  
  public TcManticVentasDto getIngreso() {
    return ingreso;
  }

  public Factura getComprobante() {
    return comprobante;
  }

	public UISelectEntity getIkSerie() {
		return ikSerie;
	}

	public void setIkSerie(UISelectEntity ikSerie) {
		this.ikSerie=ikSerie;
		if(this.ikSerie!= null)
		  this.ingreso.setIdSerie(this.ikSerie.getKey());
	}
	public UISelectEntity getIkTipoComprobante() {
		return ikTipoComprobante;
	}

	public void setIkTipoComprobante(UISelectEntity ikTipoComprobante) {
		this.ikTipoComprobante=ikTipoComprobante;
		if(this.ikTipoComprobante!= null)
		  this.ingreso.setIdTipoComprobante(this.ikTipoComprobante.getKey());
	}
  
	public String getConsultar() {
		return this.accion.equals(EAccion.CONSULTAR)? "none": "";
	}
  
	public Boolean getDiferente() {
	  return this.getEmisor()!= null && this.cliente!= null && !this.getReceptor().getRfc().equals(this.cliente.getRfc());
	}

  public String getTotal() {
		Double total   = 0D;
		String regresar= "";
		try {
			List<Entity>items= (List<Entity>)this.lazyModel.getWrappedData();
			for(Entity item: items)
				total+= item.toDouble("importe");
		  regresar= Numero.formatear(Numero.MILES_CON_DECIMALES, total);
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		return regresar;
  }
  
	@PostConstruct
  @Override
  protected void init() {		
    try {
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "/Paginas/Keet/Catalogos/Contratos/filtro": JsfBase.getFlashAttribute("retorno"));
			// if(JsfBase.getFlashAttribute("idContrato")== null)
			// 	UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("idContrato", JsfBase.getFlashAttribute("idContrato")== null? -1L: JsfBase.getFlashAttribute("idContrato"));
      this.attrs.put("idCliente", JsfBase.getFlashAttribute("idCliente")== null? -1L: JsfBase.getFlashAttribute("idCliente"));
      this.attrs.put("idContrato", 39L);
      this.attrs.put("idCliente", 5L);
      this.contrato= (TcKeetContratosDto)DaoFactory.getInstance().findById(TcKeetContratosDto.class, (Long)this.attrs.get("idContrato"));
      this.cliente = (TcManticClientesDto)DaoFactory.getInstance().findById(TcManticClientesDto.class, (Long)this.attrs.get("idCliente"));
      this.accion= EAccion.AGREGAR;
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_FACTURA);
			this.attrs.put("folio", "");
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	@Override
  public void doLoad() {
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      this.toLoadFacturas();
      switch (this.accion) {
        case AGREGAR:								
          this.comprobante= new Factura();
          this.comprobante.setIdUsuario(JsfBase.getIdUsuario());
          this.comprobante.setIntentos(0L);
          this.ingreso= new Ingreso(
            0D, // Double descuentos, 
            null, // Long idFactura, 
            1L, // Long idCredito, 
            "0", // String extras, 
            0D, // Double global, 
            0D, // Double utilidad, 
            0D, // Double total, 
            null, // Long idAlmacen, 
            1D, // Double tipoDeCambio, 
            1L, // Long orden, 
            2L, // Long idAutorizar, 
            this.cliente.getIdCliente(), // Long idCliente, 
            "0", // String descuento, 
            1L, // Long ejercicio, 
            1L, // Long consecutivo, 
            JsfBase.getIdUsuario(), // Long idUsuario, 
            0D, // Double impuestos, 
            3L, // Long idUsoCfdi, 
            1L,// Long idSinIva, 
            0D, // Double subTotal, 
            null, // String observaciones, 
            this.cliente.getIdEmpresa(), // Long idEmpresa, 
            -1L, // Long idVenta, 
            LocalDate.now(), // LocalDate dia, 
            12L, //  Long idVentaEstatus, 
            null, // String cotizacion, 
            null, // String ticket, 
            null, // Long ccotizacion, 
            null, // Long cticket, 
            LocalDate.now(), // LocalDate vigencia, 
            1L, // Long idManual, 
            2L, // Long idFacturar, 
            LocalDateTime.now() // LocalDateTime cobro 
          );
          this.ingreso.setIdTipoDocumento(1L);
          this.ingreso.setIdFactura(-1L);
    			this.setIkSerie(new UISelectEntity(1L));
    			this.setIkTipoComprobante(new UISelectEntity(1L));
          this.articulos= new ArrayList<>();
          break;
        case MODIFICAR:					
        case CONSULTAR:					
          this.ingreso= (Ingreso)DaoFactory.getInstance().toEntity(Ingreso.class, "TcManticVentasDto", "detalle", Variables.toMap("idVenta".concat("~")+ this.attrs.get("idVenta")));
          if(!Cadena.isVacio(this.ingreso.getIdFactura()))
            this.comprobante= (Factura)DaoFactory.getInstance().toEntity(Factura.class, "TcManticFacturasDto", "identically", Variables.toMap("idFactura~"+ this.ingreso.getIdFactura()));
          else
            this.comprobante= new Factura();
          this.setIkSerie(this.toLeyendas(EClaveCatalogo.SERIES, this.ingreso.getIdSerie()));
          this.setIkTipoComprobante(this.toLeyendas(EClaveCatalogo.COMPROBANTES, this.ingreso.getIdTipoComprobante()));
          this.cliente= (TcManticClientesDto)DaoFactory.getInstance().findById(TcManticClientesDto.class, this.ingreso.getIdCliente());
          break;
      } // switch
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

  private void toLoadFacturas() {
    List<Columna> columns     = null;    
    Map<String, Object> params= new HashMap<>();
    try {
      params.put("idContrato", this.contrato.getIdContrato());
      params.put("sortOrder", "order by tc_mantic_facturas.registro desc");
      columns = new ArrayList<>();
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("timbrado", EFormatoDinamicos.FECHA_HORA_CORTA));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      this.lazyModel = new FormatCustomLazy("VistaVentasDto", "extras", params, columns);
      UIBackingUtilities.resetDataTable();
    } // try 
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
  }
  
  public String doAceptar() { 
    this.ingreso.setIdExtra(1L);
    Transaccion transaccion= null;
    String regresar        = null;
    try {
      if(!Cadena.isVacio((String)this.attrs.get("observaciones"))) {
        if(this.getXml()!= null && Cadena.isVacio(this.getXml().getObservaciones()))
          this.getXml().setObservaciones((String)this.attrs.get("observaciones")); 
        if(this.getPdf()!= null && Cadena.isVacio(this.getPdf().getObservaciones()))
          this.getPdf().setObservaciones((String)this.attrs.get("observaciones"));
      } // if
      if(Cadena.isVacio(this.attrs.get("folio"))) {
        if(!Cadena.isVacio(this.getXml())) {
          if(this.getReceptor().getRfc().equals(this.cliente.getRfc())) {
            transaccion = new Transaccion(this.ingreso, this.comprobante, this.articulos, this.getXml(), this.getPdf());
            if (transaccion.ejecutar(this.accion)) {
              regresar= this.doCancelar();
              if(this.accion.equals(EAccion.AGREGAR)) 
                UIBackingUtilities.execute("jsArticulos.back('gener\\u00F3 la factura ', '"+ this.ingreso.getTicket()+ "');");
              else
                if(!this.accion.equals(EAccion.CONSULTAR)) 
                  JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" la factura"), ETipoMensaje.INFORMACION);
            } // if
            else 
              JsfBase.addMessage("Ocurrió un error al registrar la factura !", ETipoMensaje.ERROR);      			
          } // if
          else 
            JsfBase.addMessage("El RFC del cliente no coincide con el RFC de la factura !", ETipoMensaje.ERROR);
        } // if
        else 
          JsfBase.addMessage("Se tiene que importar el documento XML de la factura !", ETipoMensaje.ERROR);
      } // if
      else 
        JsfBase.addMessage((String)this.attrs.get("folio"), ETipoMensaje.ERROR);
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idContratoProcess", this.attrs.get("idContrato"));
  	JsfBase.setFlashAttribute("idCliente", this.cliente.getIdCliente());
  	JsfBase.setFlashAttribute("ikContrato", this.attrs.get("idContrato"));
  	JsfBase.setFlashAttribute("idEstimacion", this.attrs.get("idEstimacion"));
		if(this.ingreso== null || !this.ingreso.isValid()) {
			if(this.getXml()!= null && this.getXml().getRuta()!= null) {
			  File oldNameFile= new File(Configuracion.getInstance().getPropiedadSistemaServidor("facturama").concat(this.getXml().getRuta()).concat(this.getXml().getName()));
			  oldNameFile.delete();
			} // if	
			if(this.getPdf()!= null && this.getPdf().getRuta()!= null) {
			  File oldNameFile= new File(Configuracion.getInstance().getPropiedadSistemaServidor("facturama").concat(this.getPdf().getRuta()).concat(this.getPdf().getName()));
			  oldNameFile.delete();
			} // if	
		} // 
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar

	public void doTabChange(TabChangeEvent event) {
    switch (event.getTab().getTitle()) {
      case "Importar":
        if(this.attrs.get("faltantes")== null) 
          this.doLoadFiles("TcManticFacturasArchivosDto", this.ingreso.getIdFactura(), "idFactura", false, 1D);
        break;
    } // switch    
	}
	  
	public void doFileUpload(FileUploadEvent event) throws Exception {
    try {
      String nameFile= Archivo.toFormatName(this.cliente.getRfc(), event.getFile().getFileName().toUpperCase().substring(event.getFile().getFileName().lastIndexOf(".")));
      this.doFileUpload(event, this.ingreso.getVigencia().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(), Configuracion.getInstance().getPropiedadSistemaServidor("facturama"), this.cliente.getRfc(), false, 1D, nameFile);
      if(event.getFile().getFileName().toUpperCase().endsWith(EFormatos.XML.name())) {
        if(Objects.equals("I", this.getFactura().getTipoDeComprobante())) {
          this.ingreso.setDescuentos(0D);
          this.ingreso.setImpuestos(Numero.getDouble(this.getFactura().getImpuesto().getTraslado().getImporte(), 0D));
          this.ingreso.setSubTotal(Numero.getDouble(this.getFactura().getSubTotal(), 0D));
          this.ingreso.setTotal(Numero.getDouble(this.getFactura().getTotal(), 0D));
          this.ingreso.setIdTipoMedioPago(this.toClaveCatalogo(EClaveCatalogo.MEDIOS_PAGO, this.getFactura().getFormaPago())); // EFECTIVO, CHEQUE, TRANSFERENCIA getFormaPago(30) 
          this.ingreso.setIdTipoPago(this.toClaveCatalogo(EClaveCatalogo.TIPOS_PAGOS, this.getFactura().getMetodoPago())); // PUE, PPD, PUE getMetodoPago(PUE)
          this.ingreso.setIdUsoCfdi(this.toClaveCatalogo(EClaveCatalogo.USOS_CFDI, this.getFactura().getReceptor().getUsoCfdi())); // GO3 getReceptor.getUsoCfi(G02)
          this.ingreso.setIdSerie(this.toClaveCatalogo(EClaveCatalogo.SERIES, this.getFactura().getSerie())); // A o B
          this.ingreso.setIdTipoComprobante(this.toClaveCatalogo(EClaveCatalogo.COMPROBANTES, this.getFactura().getTipoDeComprobante())); // I o E
          this.comprobante.setFolio(this.getFactura().getFolio());
          this.comprobante.setSelloCfdi(this.getFactura().getTimbreFiscalDigital().getSelloCfd());
          this.comprobante.setSelloSat(this.getFactura().getTimbreFiscalDigital().getSelloSat());
          this.comprobante.setCertificadoDigital(this.getFactura().getNoCertificado());
          this.comprobante.setCertificadoSat(this.getFactura().getTimbreFiscalDigital().getNoCertificadoSat());
          this.comprobante.setFolioFiscal(this.getFactura().getTimbreFiscalDigital().getUuid());
          this.comprobante.setUltimoIntento(Fecha.toLocalDate(this.getFactura().getTimbreFiscalDigital().getFechaTimbrado()));
          this.comprobante.setTimbrado(Fecha.toLocalDateTime(this.getFactura().getTimbreFiscalDigital().getFechaTimbrado()));
          this.comprobante.setRegistro(Fecha.toLocalDateTime(this.getFactura().getFecha()));
          this.comprobante.setCadenaOriginal(this.toCadenaOriginal(Configuracion.getInstance().getPropiedadSistemaServidor("facturama").concat(this.getXml().getRuta()).concat(this.getXml().getName())));
          this.setIkSerie(this.toLeyendas(EClaveCatalogo.SERIES, this.ingreso.getIdSerie()));
          this.setIkTipoComprobante(this.toLeyendas(EClaveCatalogo.COMPROBANTES, this.ingreso.getIdTipoComprobante()));
          this.doCheckFolio();
          this.toReadArticulos();
        } // if  
        else {
          this.doDeleteXml();
          JsfBase.addMessage("Factura:", "Solo se pueden importar facturas !", ETipoMensaje.ERROR); 
        } // else  
      } // if
    } // try
    catch(Exception e ) {
      throw e;
    } // catch
	} // doFileUpload	
	
	public void doCheckFolio() {
		Map<String, Object> params=null;
		try {
			this.attrs.put("folio", "");
			params=new HashMap<>();
			params.put("factura", this.comprobante.getFolio());
			params.put("idCliente", this.ingreso.getIdCliente());
			params.put("idVenta", this.ingreso.getIdVenta());
			int month= Calendar.getInstance().get(Calendar.MONTH);
			if(month<= 5) {
				params.put("inicio", Calendar.getInstance().get(Calendar.YEAR)+ "0101");
				params.put("termino", Calendar.getInstance().get(Calendar.YEAR)+ "0630");
			} // if
			else {
				params.put("inicio", Calendar.getInstance().get(Calendar.YEAR)+ "0701");
				params.put("termino", Calendar.getInstance().get(Calendar.YEAR)+ "1231");
			} // else
			Entity entity= (Entity)DaoFactory.getInstance().toEntity("VistaVentasDto", "folio", params);
			if(entity!= null && entity.size()> 0) {
				UIBackingUtilities.execute("$('#contenedorGrupos\\\\:factura').val('');janal.show([{summary: 'Error:', detail: 'El folio ["+ this.comprobante.getFolio()+ "] se registró en la factura con consecutivo "+ entity.toString("consecutivo")+ ", el dia "+ Global.format(EFormatoDinamicos.FECHA_HORA, entity.toTimestamp("registro"))+ " hrs.'}]);");
   			this.attrs.put("folio", "El folio ["+ this.comprobante.getFolio()+ "] se registró en la factura con consecutivo "+ entity.toString("consecutivo")+ ", el dia "+ Global.format(EFormatoDinamicos.FECHA_HORA, entity.toTimestamp("registro"))+ " hrs.");
      } // if  
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally    
	}
	
	public StreamedContent doFileDownload() {
		return this.doPdfFileDownload(Configuracion.getInstance().getPropiedadSistemaServidor("facturama"));
	}	
	
	public void doViewDocument() {
		this.doViewDocument(Configuracion.getInstance().getPropiedadSistemaServidor("facturama"));
	}

	public void doViewFile() {
		this.doViewFile(Configuracion.getInstance().getPropiedadSistemaServidor("facturama"));
	}

	public void doLoadXmlFile() {
		try {
			if(this.getXml()!= null) {
				String alias= Configuracion.getInstance().getPropiedadSistemaServidor("facturama").concat(this.getXml().getRuta()).concat(this.getXml().getName());
				this.toReadFactura(new File(alias), false, 1D);
			} // if	
	  }	// try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
	}

	public void doAutoSaveOrden() {
	  this.toSaveRecord();	
	}
	
	@Override
	public void toSaveRecord() {
    
	}
	
	public void doGlobalEvent(Boolean isViewException) {
		LOG.error("ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA ["+ isViewException+ "]");
		if(isViewException && this.getXml()!= null)
		  this.toSaveRecord();
    //UIBackingUtilities.execute("alert('ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA');");
	}
	
	private String toCadenaOriginal(String xml) throws Exception {
		StreamSource source       = new StreamSource(new File(xml));
		StreamSource stylesource  = new StreamSource(this.getClass().getResourceAsStream("/mx/org/kaana/mantic/libs/factura/cadenaoriginal_3_3.xslt"));
		TransformerFactory factory= TransformerFactory.newInstance();
		Transformer transformer   = factory.newTransformer(stylesource);
		StreamResult result       = new StreamResult(new StringWriter());
		transformer.transform(source, result);
		return result.getWriter().toString();
	} // toCadenaOriginal
 
  private Long toClaveCatalogo(EClaveCatalogo tipo, String clave) {
    Long regresar= null;
    Map<String, Object> params = null;
    try {      
      params = new HashMap<>();      
      params.put("clave", clave);      
      Value value= null;
      switch(tipo) {
        case MEDIOS_PAGO:  
          value= DaoFactory.getInstance().toField("TcManticTiposMediosPagosDto", "clave", params, "idKey");
          break;
        case TIPOS_PAGOS:  
          value= DaoFactory.getInstance().toField("TcManticTiposPagosDto", "clave", params, "idKey");
          break;
        case USOS_CFDI:  
          value= DaoFactory.getInstance().toField("TcManticUsosCfdiDto", "identically", params, "idKey");
          break;
        case SERIES:  
          value= DaoFactory.getInstance().toField("TcKeetSeriesDto", "clave", params, "idKey");
          break;
        case COMPROBANTES:  
          value= DaoFactory.getInstance().toField("TcKeetTiposComprobantesDto", "clave", params, "idKey");
          break;
      } // switch
      if(value!= null && value.getData()!= null)
        regresar= value.toLong();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }

  private UISelectEntity toLeyendas(EClaveCatalogo tipo, Long idKey) {
    UISelectEntity regresar   = null;
    List<UISelectEntity> items= null;
    Map<String, Object> params= null;
    try {      
      params = new HashMap<>();      
      params.put("idTipo", 1);
      switch(tipo) {
        case SERIES:  
          params.put(Constantes.SQL_CONDICION, "id_serie="+ (idKey== null? -1L: idKey));
          items= UIEntity.build("TcKeetSeriesDto", "row", params, Collections.EMPTY_LIST);
          break;
        case COMPROBANTES:  
          params.put(Constantes.SQL_CONDICION, "id_tipo_comprobante="+ (idKey== null? -1L: idKey));
          items= UIEntity.build("TcKeetTiposComprobantesDto", "row", params, Collections.EMPTY_LIST);
          break;
      } // switch
      if(items!= null && !items.isEmpty())
        regresar= items.get(0);
      else
        regresar= new UISelectEntity(-1L);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }

  private void toReadArticulos() throws Exception {
    Map<String, Object> params = null;
    List<Articulo> conceptos   = null;
    try {
      params = new HashMap<>();
      params.put("idArticuloTipo", 3);
      this.articulos.clear();
      if(this.getFactura()!= null && !this.getFactura().getConceptos().isEmpty()) {
        // int size= this.getFactura().getConceptos().size();
        params.put(Constantes.SQL_CONDICION, "tc_mantic_unidades_medidas.clave = '".concat(this.getFactura().getConceptos().get(0).getClaveUnidad()).concat("'"));
        conceptos= (List<Articulo>)DaoFactory.getInstance().toEntitySet(Articulo.class, "VistaIngresosDto", "articulos", params);
        if(conceptos== null || conceptos.isEmpty()) {
          params.put(Constantes.SQL_CONDICION, "tc_mantic_unidades_medidas.clave is not null");
          conceptos= (List<Articulo>)DaoFactory.getInstance().toEntitySet(Articulo.class, "VistaIngresosDto", "articulos", params);
        } // if
        if(conceptos!= null) {
          int count= 0;
          for (Concepto concepto: this.getFactura().getConceptos()) {
            Articulo item= conceptos.get(count);
            item.setNombre(concepto.getDescripcion());
            item.setDescuento(concepto.getDescuento());
            item.setCodigo(concepto.getNoIdentificacion());
            item.setSat(concepto.getClaveProdServ());
            item.setCantidad(Numero.getDouble(concepto.getCantidad(), 0D));
            item.setPrecio(Numero.getDouble(concepto.getValorUnitario(), 0D));
            item.setCosto(item.getPrecio());
            item.setIva(Numero.getDouble(concepto.getTraslado().getTasaCuota(), 0D));
            item.setSubTotal(Numero.getDouble(concepto.getTraslado().getBase(), 0D));
            item.setImpuestos(Numero.getDouble(concepto.getTraslado().getImporte(), 0D));
            item.setImporte(Numero.getDouble(concepto.getImporte(), 0D));
            item.setUnidadMedida(concepto.getUnidad());
            count++;
            this.articulos.add(item);
            if(count>= this.getFactura().getConceptos().size())
              break;
          } // for
        } // if
      } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(conceptos);
    } // finally
  }
 
  public void doDeleteXml() {
    if(this.resetXml(Configuracion.getInstance().getPropiedadSistemaServidor("facturama"))) {
      this.ingreso.setDescuentos(0D);
      this.ingreso.setImpuestos(0D);
      this.ingreso.setSubTotal(0D);
      this.ingreso.setTotal(0D);
      this.ingreso.setIdTipoMedioPago(1L); // EFECTIVO, CHEQUE, TRANSFERENCIA getFormaPago(30) 
      this.ingreso.setIdTipoPago(-1L); // PUE, PPD, PUE getMetodoPago(PUE)
      this.ingreso.setIdUsoCfdi(-1L); // GO3 getReceptor.getUsoCfi(G02)
      this.ingreso.setIdSerie(-1L); // A o B
      this.ingreso.setIdTipoComprobante(-1L); // I o E
      this.comprobante.setFolio("");
      this.comprobante.setSelloCfdi("");
      this.comprobante.setSelloSat("");
      this.comprobante.setCertificadoDigital("");
      this.comprobante.setCertificadoSat("");
      this.comprobante.setFolioFiscal("");
      this.comprobante.setUltimoIntento(LocalDate.now());
      this.comprobante.setTimbrado(null);
      this.comprobante.setRegistro(LocalDateTime.now());
      this.comprobante.setCadenaOriginal(null);
      this.setIkSerie(new UISelectEntity(1L));
      this.setIkTipoComprobante(new UISelectEntity(1L));
      this.articulos.clear();
      if(this.attrs.get("faltantes")!= null) {
        List<Articulo> faltantes= (List<Articulo>)this.attrs.get("faltantes");
        faltantes.clear();
        this.attrs.put("faltantes", faltantes);
      } // if  
    } // if  
  }
  
  public void doDeletePdf() {
    this.resetPdf(Configuracion.getInstance().getPropiedadSistemaServidor("facturama"));
  }

  public void doCancel(Entity row) {
		Transaccion transaccion= null;
		try {
			transaccion= new Transaccion((Ingreso)DaoFactory.getInstance().toEntity(Ingreso.class, "TcManticVentasDto", "detalle", row.toMap()));
			if(transaccion.ejecutar(EAccion.DEPURAR)) {
				JsfBase.addMessage("Cancelar", "La factura de los extras se ha cancelado correctamente.", ETipoMensaje.ERROR);
        this.toLoadFacturas();
      } // if  
			else
				JsfBase.addMessage("Cancelar", "Ocurrió un error al cancelar la factura de los extras.", ETipoMensaje.ERROR);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
  }        
  
  public void doDelete(Entity row) {
		Transaccion transaccion = null;
		try {
			transaccion= new Transaccion((Ingreso)DaoFactory.getInstance().toEntity(Ingreso.class, "TcManticVentasDto", "detalle", row.toMap()));
			if(transaccion.ejecutar(EAccion.ELIMINAR)) {
				JsfBase.addMessage("Eliminar", "La factura de los extras se ha eliminado correctamente.", ETipoMensaje.ERROR);
        this.toLoadFacturas();
      } // if  
			else
				JsfBase.addMessage("Eliminar", "Ocurrió un error al eliminar la factura de los extras.", ETipoMensaje.ERROR);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
  }        
  
	@Override
	protected void finalize() throws Throwable {
		try {
			this.doCancelar();
      Methods.clean(this.articulos);
		} // try
		finally {
			super.finalize();
		} // finally	
	}
 
}