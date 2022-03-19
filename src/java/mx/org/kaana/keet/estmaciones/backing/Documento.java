package mx.org.kaana.keet.estmaciones.backing;

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
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.keet.estmaciones.reglas.Estimaciones;
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
import mx.org.kaana.mantic.inventarios.comun.IBaseImportar;
import mx.org.kaana.keet.estmaciones.reglas.Operacion;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.db.dto.TcManticVentasDto;
import mx.org.kaana.mantic.libs.factura.beans.Concepto;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.StreamedContent;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "keetEstimacionesDocumento")
@ViewScoped
public class Documento extends IBaseImportar implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Documento.class);
  private static final long serialVersionUID= 327393488565639367L;

	protected EAccion accion;	
  protected Ingreso ingreso;
  protected Factura comprobante;
  protected List<Articulo> articulos;
	private UISelectEntity ikSerie;
	private UISelectEntity ikTipoComprobante;
  private Estimaciones estimaciones;

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
	  return this.getEmisor()!= null && this.estimaciones.getEstimacion().getIkCliente()!= null && !this.getReceptor().getRfc().equals(this.estimaciones.getEstimacion().getIkCliente().toString("rfc"));
	}

  public Estimaciones getEstimaciones() {
    return estimaciones;
  }

	@PostConstruct
  @Override
  protected void init() {		
    try {
			if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("idEstimacion", JsfBase.getFlashAttribute("idEstimacion")== null? -1L: JsfBase.getFlashAttribute("idEstimacion"));
      this.estimaciones= new Estimaciones((Long)this.attrs.get("idEstimacion"));
      if(Objects.equals(this.estimaciones.getEstimacion().getIdVenta(), null)) {
        this.estimaciones.getEstimacion().setIdVenta(-1L);
        this.accion= EAccion.AGREGAR;
      } // if  
      else 
        this.accion= EAccion.MODIFICAR;
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "/Paginas/Keet/Estimaciones/filtro": JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_FACTURA);
			this.attrs.put("folio", "");
			this.attrs.put("idFactura", -1L);
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	@Override
  public void doLoad() {
    Map<String, Object> params= new HashMap<>();
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      switch (this.accion) {
        case REPROCESAR:								
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
            null, // Long idCliente, 
            "0", // String descuento, 
            1L, // Long ejercicio, 
            1L, // Long consecutivo, 
            JsfBase.getIdUsuario(), // Long idUsuario, 
            0D, // Double impuestos, 
            3L, // Long idUsoCfdi, 
            1L,// Long idSinIva, 
            0D, // Double subTotal, 
            null, // String observaciones, 
            -1L, // Long idEmpresa, 
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
          this.setIkTipoComprobante(this.toLeyendas(EClaveCatalogo.COMPROBANTES, this.ingreso.getIdTipoComprobante()));
          this.ingreso.setIdEmpresa(this.estimaciones.getEstimacion().getIdEmpresa());
          this.ingreso.setIdCliente(this.estimaciones.getEstimacion().getIdCliente());
          this.ingreso.setIdDesarrollo(this.estimaciones.getEstimacion().getIdDesarrollo());
          this.ingreso.setIdContrato(this.estimaciones.getEstimacion().getIdContrato());
          this.articulos= new ArrayList<>();
          break;
        case MODIFICAR:					
        case CONSULTAR:					
          params.put("idVenta", this.estimaciones.getEstimacion().getIdVenta());
          this.ingreso= (Ingreso)DaoFactory.getInstance().toEntity(Ingreso.class, "TcManticVentasDto", "detalle", params);
          if(!Cadena.isVacio(this.ingreso.getIdFactura())) {
            this.attrs.put("idFactura", this.ingreso.getIdFactura());
            params.put("idFactura", this.ingreso.getIdFactura());
            this.comprobante= (Factura)DaoFactory.getInstance().toEntity(Factura.class, "TcManticFacturasDto", "identically", params);
          } // if  
          else
            this.comprobante= new Factura();
          this.setIkSerie(this.toLeyendas(EClaveCatalogo.SERIES, this.ingreso.getIdSerie()));
          this.setIkTipoComprobante(this.toLeyendas(EClaveCatalogo.COMPROBANTES, this.ingreso.getIdTipoComprobante()));
          break;
      } // switch
      this.toLoadCatalog();
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally 
  } // doLoad

  public String doAceptar() { 
    this.ingreso.setIdExtra(2L);
    Operacion transaccion= null;
    String regresar      = null;
    try {
      if(!Cadena.isVacio((String)this.attrs.get("observaciones"))) {
        if(this.getXml()!= null && Cadena.isVacio(this.getXml().getObservaciones()))
          this.getXml().setObservaciones((String)this.attrs.get("observaciones")); 
        if(this.getPdf()!= null && Cadena.isVacio(this.getPdf().getObservaciones()))
          this.getPdf().setObservaciones((String)this.attrs.get("observaciones"));
      } // if
      if(Cadena.isVacio(this.attrs.get("folio"))) {
        if(!Cadena.isVacio(this.getXml())) {
          if(this.getReceptor().getRfc().equals(this.estimaciones.getEstimacion().getIkCliente().toString("rfc"))) {
            if(Objects.equals(this.estimaciones.getEstimacion().getIdVenta(), -1L))
              this.estimaciones.getEstimacion().setIdVenta(null);
            transaccion = new Operacion(this.ingreso, this.comprobante, this.articulos, this.getXml(), this.getPdf(), this.estimaciones);
            if (transaccion.ejecutar(this.accion)) {
              regresar= this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);
              if(this.accion.equals(EAccion.AGREGAR)) 
                UIBackingUtilities.execute("jsArticulos.back('gener\\u00F3 la factura ', '"+ this.ingreso.getTicket()+ "');");
              else
                if(!this.accion.equals(EAccion.CONSULTAR)) 
                  JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR)? "agregó": "modificó").concat(" la factura"), ETipoMensaje.INFORMACION);
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
    JsfBase.setFlashAttribute("idEstimacion", this.estimaciones.getEstimacion().getIdEstimacion());
    return regresar;
  } // doAccion

  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idEstimacion", this.estimaciones.getEstimacion().getIdEstimacion());
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

	private void toLoadCatalog() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns));
  		params.put("sucursales", this.estimaciones.getEstimacion().getIkEmpresa());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.remove(0);
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("clientes", UIEntity.seleccione("TcManticClientesDto", "sucursales", params, columns, "clave"));
			this.doLoadDesarrollos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally    
	}

	public void doLoadDesarrollos() {
		List<Columna> columns     = null;
    Map<String, Object> params= null;		
    try {
			params= new HashMap<>();					
      params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
			params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa=" + this.estimaciones.getEstimacion().getIkEmpresa().getKey());
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
  		this.attrs.put("desarrollos", (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave"));			
  	  this.attrs.put("cliente", this.estimaciones.getEstimacion().getIkCliente().toString("razonSocial"));
			this.doLoadContratos();
    } // try
    catch (Exception e) {
			Error.mensaje(e);			
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // doLoadDesarrollos
	
	public void doLoadContratos() {
		List<UISelectEntity> contratos= null;
		Map<String, Object>params     = null;
		try {
			params= new HashMap<>();
			params.put("idDesarrollo", this.estimaciones.getEstimacion().getIdDesarrollo());
			contratos= UIEntity.seleccione("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
			this.attrs.put("contratos", contratos);
		} // try 
		catch (Exception e) {			
			Error.mensaje(e);			
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadContratos
  
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
      String rfc= this.estimaciones.getEstimacion().getIkCliente().toString("rfc");
      String nameFile= Archivo.toFormatName(rfc, event.getFile().getFileName().toUpperCase().substring(event.getFile().getFileName().lastIndexOf(".")));
      this.doFileUpload(event, this.ingreso.getVigencia().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(), Configuracion.getInstance().getPropiedadSistemaServidor("facturama"), rfc, false, 1D, nameFile);
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
          if(!Objects.equals(this.estimaciones.getEstimacion().getFacturar(), this.ingreso.getTotal()))
            UIBackingUtilities.execute("janal.warn('facturar', 'El importe por facturar [$ "+ Global.format(EFormatoDinamicos.MILES_SAT_DECIMALES, this.estimaciones.getEstimacion().getFacturar())
                    + "] es diferente al importe de la factura [$ "+ Global.format(EFormatoDinamicos.MILES_SAT_DECIMALES, this.ingreso.getTotal())+ "]');");
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
			params.put("idCliente", this.estimaciones.getEstimacion().getIdCliente());
			params.put("idVenta", this.estimaciones.getEstimacion().getIdVenta());
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
		return this.doPdfFileDownload("");
	}	
	
	public void doViewDocument() {
		this.doViewDocument("");
	}

	public void doViewFile() {
		this.doViewFile("");
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
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(conceptos);
    } // finally
  }
 
  public void doCancelXml() {
    Operacion transaccion= null;
    try {
      transaccion = new Operacion(this.ingreso, this.comprobante, this.articulos, this.getXml(), this.getPdf(), this.estimaciones);
      if (transaccion.ejecutar(EAccion.ELIMINAR)) {
        this.doDeleteXml();
        this.doDeletePdf();
        this.accion= EAccion.AGREGAR;
        this.attrs.put("idFactura", -1L);
        JsfBase.addMessage("Se eliminó la factura y la cuenta x cobrar !", ETipoMensaje.INFORMACION);
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch
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
      if(this.articulos== null)
        this.articulos= new ArrayList<>();
      else  
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