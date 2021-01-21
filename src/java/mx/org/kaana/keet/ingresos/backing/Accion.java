package mx.org.kaana.keet.ingresos.backing;

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
import mx.org.kaana.mantic.db.dto.TcManticFacturasDto;
import mx.org.kaana.mantic.db.dto.TcManticVentasDto;
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

@Named(value= "keetIngresosAccion")
@ViewScoped
public class Accion extends IBaseImportar implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Accion.class);
  private static final long serialVersionUID= 327393488565639367L;
	
	private EAccion accion;	
  private TcManticVentasDto ingreso;
  private TcManticFacturasDto comprobante;
  private TcManticClientesDto cliente;
	private UISelectEntity ikEmpresa;
	private UISelectEntity ikDesarrollo;
	private UISelectEntity ikCliente;
	private UISelectEntity ikContrato;

  public TcManticVentasDto getIngreso() {
    return ingreso;
  }

  public TcManticFacturasDto getComprobante() {
    return comprobante;
  }

  public TcManticClientesDto getCliente() {
    return cliente;
  }
  
	public UISelectEntity getIkEmpresa() {
		return ikEmpresa;
	}

	public void setIkEmpresa(UISelectEntity ikEmpresa) {
		this.ikEmpresa=ikEmpresa;
		if(this.ikEmpresa!= null)
		  this.ingreso.setIdEmpresa(this.ikEmpresa.getKey());
	}

  public UISelectEntity getIkDesarrollo() {
    return ikDesarrollo;
  }

  public void setIkDesarrollo(UISelectEntity ikDesarrollo) {
    this.ikDesarrollo = ikDesarrollo;
		if(this.ikDesarrollo!= null)
		  this.ingreso.setIdDesarrollo(this.ikDesarrollo.getKey());
  }

	public UISelectEntity getIkCliente() {
		return ikCliente;
	}

	public void setIkCliente(UISelectEntity ikCliente) {
		this.ikCliente=ikCliente;
		if(this.ikCliente!= null)
		  this.ingreso.setIdCliente(this.ikCliente.getKey());
	}

  public UISelectEntity getIkContrato() {
    return ikContrato;
  }

  public void setIkContrato(UISelectEntity ikContrato) {
    this.ikContrato = ikContrato;
		if(this.ikContrato!= null)
		  this.ingreso.setIdContrato(this.ikContrato.getKey());
  }

	public String getConsultar() {
		return this.accion.equals(EAccion.CONSULTAR)? "none": "";
	}
  
	public Boolean getDiferente() {
	  return this.getEmisor()!= null && this.cliente!= null && !this.getReceptor().getRfc().equals(this.cliente.getRfc());
	}
  
	@PostConstruct
  @Override
  protected void init() {		
    try {
//			if(JsfBase.getFlashAttribute("accion")== null)
//				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("idVenta", JsfBase.getFlashAttribute("idVenta")== null? -1L: JsfBase.getFlashAttribute("idVenta"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
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
      switch (this.accion) {
        case AGREGAR:								
          this.comprobante= new TcManticFacturasDto();
          this.comprobante.setIdUsuario(JsfBase.getIdUsuario());
          this.comprobante.setIntentos(0L);
          this.ingreso= new TcManticVentasDto(
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
    			this.setIkEmpresa(new UISelectEntity(JsfBase.getAutentifica().getEmpresa().getIdEmpresa()));
          this.setIkDesarrollo(new UISelectEntity(-1L));
          this.setIkCliente(new UISelectEntity(-1L));
          this.setIkContrato(new UISelectEntity(-1L));
          break;
        case MODIFICAR:					
        case CONSULTAR:					
          this.ingreso= (TcManticVentasDto)DaoFactory.getInstance().findById(TcManticVentasDto.class, (Long)this.attrs.get("idVenta"));
          if(!Cadena.isVacio(this.ingreso.getIdFactura()))
            this.comprobante= (TcManticFacturasDto)DaoFactory.getInstance().findById(TcManticFacturasDto.class, this.ingreso.getIdFactura());
          else
            this.comprobante= new TcManticFacturasDto();
          this.setIkEmpresa(new UISelectEntity(new Entity(this.ingreso.getIdEmpresa())));
          this.setIkDesarrollo(new UISelectEntity(new Entity(this.ingreso.getIdDesarrollo())));
          this.setIkCliente(new UISelectEntity(new Entity(this.ingreso.getIdCliente())));
          if(this.ingreso.getIdContrato()== null)
            this.setIkContrato(new UISelectEntity(-1L));
          else
            this.setIkContrato(new UISelectEntity(new Entity(this.ingreso.getIdContrato())));
          this.cliente= (TcManticClientesDto)DaoFactory.getInstance().findById(TcManticClientesDto.class, this.ingreso.getIdCliente());
          break;
      } // switch
      this.toLoadCatalog();
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

  public String doAceptar() { 
    this.ingreso.setIdExtra(this.ingreso.getIdContrato()<= 0? 1L: 2L);
    Transaccion transaccion= null;
    String regresar        = null;
    try {
      if(!Cadena.isVacio((String)this.attrs.get("observaciones"))) {
        if(this.getXml()!= null)
          this.getXml().setObservaciones((String)this.attrs.get("observaciones")); 
        if(this.getPdf()!= null)
          this.getPdf().setObservaciones((String)this.attrs.get("observaciones"));
      } // if
      if(Cadena.isVacio(this.attrs.get("folio"))) {
        if(!Cadena.isVacio(this.getXml())) {
          if(this.getReceptor().getRfc().equals(this.cliente.getRfc())) {
            transaccion = new Transaccion(this.ingreso, this.comprobante, this.getXml(), this.getPdf());
            if (transaccion.ejecutar(this.accion)) {
              regresar= this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);
              if(!this.accion.equals(EAccion.CONSULTAR)) 
                JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" la factura."), ETipoMensaje.INFORMACION);
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
    JsfBase.setFlashAttribute("idVenta", this.ingreso.getIdVenta());
    return regresar;
  } // doAccion

  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idVenta", this.attrs.get("idVenta"));
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
 			List<UISelectEntity> empresas= (List<UISelectEntity>)this.attrs.get("empresas");
			if(!empresas.isEmpty()) {
				this.attrs.put("idPedidoSucursal", empresas.get(0));
				if(this.accion.equals(EAccion.AGREGAR))
  				this.setIkEmpresa(empresas.get(0));
			  else 
				  this.setIkEmpresa(empresas.get(empresas.indexOf(this.getIkEmpresa())));
			} // if	
  		params.put("sucursales", this.getIkEmpresa());
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
		List<Columna> columns           = null;
    Map<String, Object> params      = null;		
		List<UISelectEntity> desarrollos= null;
    try {
			params= new HashMap<>();						
			params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa=" + this.getIkEmpresa().getKey());
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
			desarrollos= (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave");
			if(!desarrollos.isEmpty()) {
				this.attrs.put("desarrollos", desarrollos);			
				if(this.accion.equals(EAccion.AGREGAR)) 
          this.setIkDesarrollo(desarrollos.get(0));
        else
				  this.setIkDesarrollo(desarrollos.get(desarrollos.indexOf(this.getIkDesarrollo())));
        this.setIkCliente(new UISelectEntity(this.getIkDesarrollo().toLong("idCliente")));
			  this.attrs.put("cliente", this.getIkDesarrollo().toString("razonSocial"));
			} // if
      else {
				this.attrs.put("desarrollos", new ArrayList<>());
				this.setIkDesarrollo(new UISelectEntity(-1L));
				this.attrs.put("cliente", "");
			} // else
			this.doLoadContratos();
    } // try
    catch (Exception e) {
      throw e;
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
			params.put("idDesarrollo", this.ingreso.getIdDesarrollo());
			contratos= UIEntity.seleccione("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
			this.attrs.put("contratos", contratos);
      if(!contratos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          this.setIkContrato(contratos.get(0));
        else  
          this.setIkContrato(contratos.get(contratos.indexOf(this.getIkContrato())));
		} // try // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadContratos
  
	public void doUpdateCliente() {
		List<UISelectEntity> desarrollos= null;
		UISelectEntity desarrollo       = null;
		List<Columna> columns           = null;
		Map<String, Object>params       = new HashMap<>();
		try {
			desarrollos= (List<UISelectEntity>) this.attrs.get("desarrollos");
			desarrollo = this.getIkDesarrollo();
      UISelectEntity item= desarrollos.get(desarrollos.indexOf(desarrollo));
			this.attrs.put("cliente", item.toString("razonSocial"));			
      this.setIkCliente(new UISelectEntity(item.toLong("idCliente")));
      this.cliente= (TcManticClientesDto)DaoFactory.getInstance().findById(TcManticClientesDto.class, this.ingreso.getIdCliente());
      params.put("idCliente", this.cliente!= null? this.cliente.getIdCliente(): -1L);
			Entity domicilio= (Entity)DaoFactory.getInstance().toEntity("VistaClientesDto", "domiciliosCliente", params);
      if(domicilio!= null && !domicilio.isEmpty())
        this.ingreso.setIdClienteDomicilio(domicilio.getKey());
      else
        JsfBase.addMessage("El cliente ".concat(this.cliente.getRazonSocial()).concat(" no tiene un domicilio registrado !"), ETipoMensaje.ERROR);
			this.doLoadContratos();
		} // try // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
	} // doUpdateCliente  
  
	public void doTabChange(TabChangeEvent event) {
    switch (event.getTab().getTitle()) {
      case "Importar":
        if(this.ingreso.getIdDesarrollo()!= null && this.ingreso.getIdDesarrollo()> 0L) {
     		  if(this.attrs.get("faltantes")== null)
		  	    this.doLoadFiles("TcManticFacturasArchivosDto", this.ingreso.getIdFactura(), "idFactura", false, 1D);
        } // if
        else
    			JsfBase.addMessage("Se tiene que seleccionar un desarrollo primero.", ETipoMensaje.ALERTA);      			
        break;
    } // switch    
	}
	  
	public void doFileUpload(FileUploadEvent event) throws Exception {
    try {
      if(this.ingreso.getIdDesarrollo()!= null && this.ingreso.getIdDesarrollo()> 0L) {
        String nameFile= Archivo.toFormatName(this.cliente.getRfc(), event.getFile().getFileName().toUpperCase().substring(event.getFile().getFileName().lastIndexOf(".")));
        this.doFileUpload(event, this.ingreso.getVigencia().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(), Configuracion.getInstance().getPropiedadSistemaServidor("facturama"), this.cliente.getRfc(), false, 1D, nameFile);
        if(event.getFile().getFileName().toUpperCase().endsWith(EFormatos.XML.name())) {
          this.ingreso.setDescuentos(0D);
          this.ingreso.setImpuestos(Numero.getDouble(this.getFactura().getImpuesto().getTraslado().getImporte(), 0D));
          this.ingreso.setSubTotal(Numero.getDouble(this.getFactura().getSubTotal(), 0D));
          this.ingreso.setTotal(Numero.getDouble(this.getFactura().getTotal(), 0D));
          this.ingreso.setIdTipoMedioPago(this.toClaveCatalogo(0, this.getFactura().getFormaPago())); // EFECTIVO, CHEQUE, TRANSFERENCIA getFormaPago(30) 
          this.ingreso.setIdTipoPago(this.toClaveCatalogo(1, this.getFactura().getMetodoPago())); // PUE, PPD, PUE getMetodoPago(PUE)
          this.ingreso.setIdUsoCfdi(this.toClaveCatalogo(2, this.getFactura().getReceptor().getUsoCfdi())); // GO3 getReceptor.getUsoCfi(G02)
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
          this.doCheckFolio();
        } // if
      } // if
      else 
        JsfBase.addMessage("Se tiene que seleccionar un desarrollo primero.", ETipoMensaje.ALERTA);      			
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
	
	@Override
	protected void finalize() throws Throwable {
		try {
			this.doCancelar();
		} // try
		finally {
			super.finalize();
		} // finally	
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
 
  private Long toClaveCatalogo(int tipo, String clave) {
    Long regresar= null;
    Map<String, Object> params = null;
    try {      
      params = new HashMap<>();      
      params.put("clave", clave);      
      Value value= null;
      switch(tipo) {
        case 0:  
          value= DaoFactory.getInstance().toField("TcManticTiposMediosPagosDto", "clave", params, "idKey");
          break;
        case 1:  
          value= DaoFactory.getInstance().toField("TcManticTiposPagosDto", "clave", params, "idKey");
          break;
        case 2:  
          value= DaoFactory.getInstance().toField("TcManticUsosCfdiDto", "identically", params, "idKey");
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
  
}