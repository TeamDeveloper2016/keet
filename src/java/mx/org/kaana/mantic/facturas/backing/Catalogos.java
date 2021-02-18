package mx.org.kaana.mantic.facturas.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.event.AjaxBehaviorEvent;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.recurso.LoadImages;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.ventas.reglas.MotorBusqueda;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.facturas.reglas.Transaccion;
import mx.org.kaana.mantic.comun.IBaseStorage;
import mx.org.kaana.mantic.db.dto.TcManticArticulosDto;
import mx.org.kaana.mantic.db.dto.TcManticFacturasDto;
import mx.org.kaana.mantic.enums.ETipoMediosPago;
import mx.org.kaana.mantic.enums.ETiposDomicilios;
import mx.org.kaana.mantic.facturas.beans.FacturaFicticia;
import mx.org.kaana.mantic.facturas.beans.Parcial;
import mx.org.kaana.mantic.facturas.reglas.AdminFacturas;
import mx.org.kaana.mantic.ventas.beans.SaldoCliente;
import mx.org.kaana.mantic.ventas.comun.IBaseVenta;
import mx.org.kaana.mantic.ventas.reglas.CambioUsuario;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.StreamedContent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.TabChangeEvent;

public class Catalogos extends IBaseVenta implements IBaseStorage, Serializable {

	private static final Log LOG               = LogFactory.getLog(Catalogos.class);  
  private static final long serialVersionUID = 327393488565639367L;
	private static final String VENDEDOR_PERFIL= "VENDEDOR DE PISO";
	private static final String INDIVIDUAL     = "1";
	private static final Long ESTATUS_ELABORADA= 11L;
	
	protected EAccion accion;	
	private SaldoCliente saldoCliente;
	private StreamedContent image;
	private FormatLazyModel almacenes;
  private UISelectEntity domicilioContratoBusqueda;
  protected Long idTipo;

	public Catalogos() {
		super("menudeo", true);
    this.idTipo= 1L;
	}
	
	@Override
	public SaldoCliente getSaldoCliente() {
		return saldoCliente;
	}	

	@Override
	public void setSaldoCliente(SaldoCliente saldoCliente) {
		this.saldoCliente = saldoCliente;
	}

	public StreamedContent getImage() {
		return image;
	}

	@Override
	public FormatLazyModel getAlmacenes() {
		return almacenes;
	}	

	public UISelectEntity getDomicilioContratoBusqueda() {
    return domicilioContratoBusqueda;
  }

  public void setDomicilioContratoBusqueda(UISelectEntity domicilioContratoBusqueda) {
    this.domicilioContratoBusqueda = domicilioContratoBusqueda;
  }
  
  @PostConstruct
  @Override
  protected void init() {		
    try {
      this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("accionInicial", JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idVenta", JsfBase.getFlashAttribute("idVenta")== null? -1L: JsfBase.getFlashAttribute("idVenta"));
      this.attrs.put("idCliente", JsfBase.getFlashAttribute("idCliente")== null? -1L: JsfBase.getFlashAttribute("idCliente"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? null: JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("isPesos", false);
			this.attrs.put("sinIva", false);
			this.attrs.put("buscaPorCodigo", false);
			this.attrs.put("activeLogin", false);
			this.attrs.put("autorized", false);
			this.attrs.put("expirada", false);
			this.attrs.put("isIndividual", true);
			this.attrs.put("descuentoIndividual", 0);
			this.attrs.put("descuentoGlobal", 0);
			this.attrs.put("tipoDescuento", INDIVIDUAL);
			this.doActivarDescuento();
			this.attrs.put("descripcion", "Imagen no disponible");
			this.attrs.put("mostrarBanco", false);
			this.attrs.put("decuentoAutorizadoActivo", false);
			this.attrs.put("tipoDecuentoAutorizadoActivo", MENUDEO);
			this.attrs.put("ticketLock", -1L);
			this.image= LoadImages.getImage(-1L);
			this.attrs.put("observaciones", JsfBase.getFlashAttribute("observaciones")== null? "" : JsfBase.getFlashAttribute("observaciones"));
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			this.attrs.put("isMatriz", JsfBase.isAdminEncuestaOrAdmin());
      this.loadSeries();
			this.loadSucursales();
			this.loadBancos();
			this.loadCfdis();
			this.loadTiposMediosPagos();
			this.loadTiposPagos();
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	@Override
  public void doLoad() {
		Long idCliente             = -1L;
		Long idClienteDomicilio    = -1L;
		TcManticFacturasDto factura= null;
		this.saldoCliente          = new SaldoCliente();
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      switch (this.accion) {
        case AGREGAR:											
          this.setAdminOrden(new AdminFacturas(new FacturaFicticia(-1L)));
          this.toLoadCollections();
          this.doLoadDomicilio(true);
					this.attrs.put("consecutivo", "");		
          break;
        case MODIFICAR:			
        case CONSULTAR:			
          this.setAdminOrden(new AdminFacturas((FacturaFicticia)DaoFactory.getInstance().toEntity(FacturaFicticia.class, "VistaFicticiasDto", "ficticia", this.attrs)));
    			this.attrs.put("sinIva", this.getAdminOrden().getIdSinIva().equals(1L));					
					this.attrs.put("consecutivo", ((FacturaFicticia)this.getAdminOrden().getOrden()).getConsecutivo());	
					if(((FacturaFicticia)this.getAdminOrden().getOrden()).getIdFactura()!= null &&((FacturaFicticia)this.getAdminOrden().getOrden()).getIdFactura()>-1L) {
						factura= (TcManticFacturasDto) DaoFactory.getInstance().findById(TcManticFacturasDto.class, ((FacturaFicticia)this.getAdminOrden().getOrden()).getIdFactura());
						this.attrs.put("observaciones", factura.getObservaciones());					
					} // if
					else
						this.attrs.put("observaciones", "");			
          this.toLoadCollections();
          break;
      } // switch		
   		this.loadCatalogs();					
      this.doUpdateDesarrollos();      
      idCliente= ((FacturaFicticia)getAdminOrden().getOrden()).getIdCliente();
      if(idCliente!= null && !idCliente.equals(-1L)) {
        this.doAsignaClienteInicial(idCliente);
        this.loadDomicilios(idCliente);
        idClienteDomicilio= ((FacturaFicticia)getAdminOrden().getOrden()).getIdClienteDomicilio();
        if(idClienteDomicilio!= null && !idClienteDomicilio.equals(-1L))
          this.attrs.put("domicilio", new UISelectEntity(idClienteDomicilio));
      } // if
      this.toContratoDomicilio();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

	private void loadCatalogs() {
		List<UISelectEntity> sucursales     = null;
		List<UISelectEntity> cfdis          = null;
		List<UISelectEntity> tiposMedioPagos= null;
		List<UISelectEntity> tiposPagos     = null;
		List<UISelectEntity> bancos         = null;
		try {
			sucursales= (List<UISelectEntity>)this.attrs.get("sucursales");
			if(sucursales!= null && !sucursales.isEmpty()) {
        if(EAccion.AGREGAR.equals(this.accion))
          ((FacturaFicticia)this.getAdminOrden().getOrden()).setIkEmpresa((sucursales.get(0)));
				for(Entity sucursal: sucursales) {
					if(sucursal.getKey().equals(((FacturaFicticia)getAdminOrden().getOrden()).getIdEmpresa()))
						this.attrs.put("idEmpresa", sucursal);
				} // for
			} // if
			cfdis= (List<UISelectEntity>) this.attrs.get("cfdis");
			for(Entity cfdi: cfdis) {
				if(cfdi.getKey().equals(((FacturaFicticia)getAdminOrden().getOrden()).getIdUsoCfdi()))
					this.attrs.put("cfdi", cfdi);
			} // for
			tiposMedioPagos= (List<UISelectEntity>) this.attrs.get("tiposMedioPagos");
			for(Entity tiposMedioPago: tiposMedioPagos) {
				if(tiposMedioPago.getKey().equals(((FacturaFicticia)getAdminOrden().getOrden()).getIdTipoMedioPago()))
					this.attrs.put("tipoMedioPago", tiposMedioPago);
			} // for			
			tiposPagos= (List<UISelectEntity>) this.attrs.get("tiposPagos");
			for(Entity tipoPago: tiposPagos) {
				if(tipoPago.getKey().equals(((FacturaFicticia)getAdminOrden().getOrden()).getIdTipoPago()))
					this.attrs.put("tipoPago", tipoPago);
			} // for
			doValidaTipoPago();
			if(Boolean.valueOf(this.attrs.get("mostrarBanco").toString())) {
				bancos= (List<UISelectEntity>) this.attrs.get("bancos");
				for(Entity banco: bancos) {
					if(banco.getKey().equals(((FacturaFicticia)getAdminOrden().getOrden()).getIdBanco()))
						this.attrs.put("banco", banco);
				} // for
				this.attrs.put("referencia", ((FacturaFicticia)getAdminOrden().getOrden()).getReferencia());
			} // if
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
			throw e;
		} // catch		
	}
	
	public void doAsignaClienteInicial(Long idCliente) {
		UISelectEntity seleccion              = null;
		List<UISelectEntity> clientesSeleccion= null;
		MotorBusqueda motorBusqueda           = null; 
		try {
      clientesSeleccion= (List<UISelectEntity>)this.attrs.get("clientesSeleccion");
      clientesSeleccion.clear();
      if(idCliente> 0) {
        motorBusqueda= new MotorBusqueda(null, idCliente);
        seleccion    = new UISelectEntity(motorBusqueda.toCliente());
        clientesSeleccion.add(seleccion);
        ((FacturaFicticia)this.getAdminOrden().getOrden()).setIkCliente(seleccion);
        this.setPrecio(Cadena.toBeanNameEspecial(seleccion.toString("tipoVenta")));
      } // if
      else {
        ((FacturaFicticia)this.getAdminOrden().getOrden()).setIkCliente(new UISelectEntity(-1L));
        this.setPrecio("menudeo");
      } // else
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doAsignaCliente	
	
  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    try {			
			this.loadOrdenVenta();
			transaccion = new Transaccion((FacturaFicticia)this.getAdminOrden().getOrden(), this.getAdminOrden().getArticulos(), (String)this.attrs.get("observaciones"));
			this.getAdminOrden().toAdjustArticulos();
			if (transaccion.ejecutar(this.accion)) {
				if(this.accion.equals(EAccion.AGREGAR))				  
    			UIBackingUtilities.execute("jsArticulos.back('gener\\u00F3 la factura ', '"+ ((FacturaFicticia)this.getAdminOrden().getOrden()).getTicket()+ "');");
        else
				  if(this.accion.equals(EAccion.MODIFICAR))
				    JsfBase.addMessage("Se modificó la factura con consecutivo ["+ ((FacturaFicticia)this.getAdminOrden().getOrden()).getTicket()+ "].", ETipoMensaje.INFORMACION);
     		JsfBase.setFlashAttribute("idVenta", ((FacturaFicticia)this.getAdminOrden().getOrden()).getIdVenta());
				regresar= (this.attrs.get("retorno")!= null ? this.attrs.get("retorno").toString() : "filtro").concat(Constantes.REDIRECIONAR);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar la factura.", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

	@Override
  public String doCancelar() {   
		String regresar= null;
		try {			
			JsfBase.setFlashAttribute("idVenta", ((FacturaFicticia)this.getAdminOrden().getOrden()).getIdVenta());
			regresar= this.attrs.get("retorno") != null ? (String)this.attrs.get("retorno") : "filtro";
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar
	
	@Override
	public void doReCalculatePreciosArticulos(Long idCliente) {
		doReCalculatePreciosArticulos(true, idCliente);
	} // doReCalculatePreciosArticulos
	
	@Override
	public void doReCalculatePreciosArticulos(boolean descuentoVigente, Long idCliente) {
		MotorBusqueda motor          = null;
		TcManticArticulosDto articulo= null;
		String descuento             = null;
		String sinDescuento          = "0";
		try {
			if(!getAdminOrden().getArticulos().isEmpty()) {
				for(Articulo beanArticulo: getAdminOrden().getArticulos()) {
					if(beanArticulo.getIdArticulo()!= null && !beanArticulo.getIdArticulo().equals(-1L)) {
						motor= new MotorBusqueda(beanArticulo.getIdArticulo());
						articulo= motor.toArticulo();
						beanArticulo.setValor((Double) articulo.toValue(getPrecio()));
						beanArticulo.setCosto((Double) articulo.toValue(getPrecio()));
						if(descuentoVigente) {
							descuento= toDescuentoVigente(beanArticulo.getIdArticulo());
							if(descuento!= null)
								beanArticulo.setDescuento(descuento);							
						} // if
						else
							beanArticulo.setDescuento(sinDescuento);
					} // if
				} // for					
				if(getAdminOrden().getArticulos().size()>1) {					
					getAdminOrden().toCalculate();
					UIBackingUtilities.update("@(.filas) @(.recalculo) @(.informacion)");
				} // if
			} // if			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} // doReCalculatePreciosArticulos
	
	@Override
	protected void toMoveData(UISelectEntity articulo, Integer index) throws Exception {
		String descuentoPivote = null;
		String descuentoVigente= null;		
		try {			
			descuentoPivote = this.getAdminOrden().getDescuento();
			descuentoVigente= this.toDescuentoVigente(articulo.toLong("idArticulo"));				
			if(descuentoVigente!= null)
				this.getAdminOrden().setDescuento(descuentoVigente);					
			super.toMoveData(articulo, index);	
			this.getAdminOrden().setDescuento(descuentoPivote);
			this.attrs.put("descripcion", articulo.toString("nombre"));
			this.image= LoadImages.getImage(JsfBase.getAutentifica().getEmpresa().getIdEmpresa().toString(), articulo.toLong("idArticulo").toString());
			this.saldoCliente.setTotalVenta(getAdminOrden().getTotales().getTotal());
			UIBackingUtilities.update("deudor");
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch	
	} // toMoveData
	
	@Override
	protected void toMoveArticulo(Articulo articulo, Integer index) throws Exception {		
		String descuentoPivote = null;
		String descuentoVigente= null;		
		try {	
			descuentoPivote= getAdminOrden().getDescuento();
			descuentoVigente= toDescuentoVigente(articulo.getIdArticulo());				
			if(descuentoVigente!= null)					
				getAdminOrden().setDescuento(descuentoVigente);																	
			super.toMoveArticulo(articulo, index);	
			getAdminOrden().setDescuento(descuentoPivote);
			this.attrs.put("descripcion", articulo.getNombre());
			this.image= LoadImages.getImage(JsfBase.getAutentifica().getEmpresa().getIdEmpresa().toString(), articulo.getIdArticulo().toString());
			this.saldoCliente.setTotalVenta(getAdminOrden().getTotales().getTotal());
			UIBackingUtilities.update("deudor");
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch	
	} // toMoveData
	
	private String toDescuentoVigente(Long idArticulo) throws Exception {
		MotorBusqueda motorBusqueda= null;
		Entity descuentoVigente    = null;
		String regresar            = null;
		try {
			motorBusqueda= new MotorBusqueda(idArticulo, -1L);			
			descuentoVigente= motorBusqueda.toDescuentoArticulo();
			if(descuentoVigente!= null)
				regresar= descuentoVigente.toString("porcentaje");
		} // try
		catch (Exception e) {			
			throw e;			
		} // catch		
		return regresar;
	} // toDescuentoVigente
	
	private void loadOrdenVenta() {
		// this.getAdminOrden().toCheckTotales();
		((FacturaFicticia)this.getAdminOrden().getOrden()).setIdTipoPago(Long.valueOf(this.attrs.get("tipoPago").toString()));
		((FacturaFicticia)this.getAdminOrden().getOrden()).setIdTipoMedioPago(Long.valueOf(this.attrs.get("tipoMedioPago").toString()));
		((FacturaFicticia)this.getAdminOrden().getOrden()).setIdUsoCfdi(Long.valueOf(this.attrs.get("cfdi").toString()));
		if(!Long.valueOf(this.attrs.get("tipoMedioPago").toString()).equals(ETipoMediosPago.EFECTIVO.getIdTipoMedioPago())) {
			((FacturaFicticia)this.getAdminOrden().getOrden()).setIdBanco(Long.valueOf(this.attrs.get("banco").toString()));
			((FacturaFicticia)this.getAdminOrden().getOrden()).setReferencia(this.attrs.get("referencia").toString());
		} // if
		((FacturaFicticia)this.getAdminOrden().getOrden()).setDescuentos(this.getAdminOrden().getTotales().getDescuentos());
		((FacturaFicticia)this.getAdminOrden().getOrden()).setImpuestos(this.getAdminOrden().getTotales().getIva());
		((FacturaFicticia)this.getAdminOrden().getOrden()).setSubTotal(this.getAdminOrden().getTotales().getSubTotal());
		((FacturaFicticia)this.getAdminOrden().getOrden()).setTotal(this.getAdminOrden().getTotales().getTotal());
		((FacturaFicticia)this.getAdminOrden().getOrden()).setSaldo(this.getAdminOrden().getTotales().getTotal());
		((FacturaFicticia)this.getAdminOrden().getOrden()).setDiferencia(this.getAdminOrden().getTotales().getTotal());
		((FacturaFicticia)this.getAdminOrden().getOrden()).setIdTipoMoneda(1L);
		((FacturaFicticia)this.getAdminOrden().getOrden()).setUtilidad(this.getAdminOrden().getTotales().getUtilidad());
		((FacturaFicticia)this.getAdminOrden().getOrden()).setIdClienteDomicilio(((Entity)this.attrs.get("domicilio")).getKey());		
		if(((FacturaFicticia)this.getAdminOrden().getOrden()).getTipoDeCambio()< 1)
			((FacturaFicticia)this.getAdminOrden().getOrden()).setTipoDeCambio(1D);
	} // loadOrdenVenta
	
	public void doCerrarTicket() {		
		Transaccion transaccion= null;
    try {								
			if(!this.getAdminOrden().getArticulos().isEmpty() && (this.getAdminOrden().getArticulos().size() > 1 || (this.getAdminOrden().getArticulos().size()== 1 && (this.getAdminOrden().getArticulos().get(0).getIdArticulo()!= null && !this.getAdminOrden().getArticulos().get(0).getIdArticulo().equals(-1L))))) {
				this.loadOrdenVenta();
				transaccion = new Transaccion((FacturaFicticia)this.getAdminOrden().getOrden(), this.getAdminOrden().getArticulos());
				this.getAdminOrden().toAdjustArticulos();
				if (transaccion.ejecutar(EAccion.REGISTRAR)) {				
					UIBackingUtilities.execute("jsArticulos.back('cerr\\u00F3 la cuenta', '"+ ((FacturaFicticia)this.getAdminOrden().getOrden()).getConsecutivo()+ "');");
					JsfBase.addMessage("Se guardo la cuenta de venta.", ETipoMensaje.INFORMACION);	
					this.init();
				} // if
				else 
					JsfBase.addMessage("Ocurrió un error al registrar la cuenta de venta.", ETipoMensaje.ERROR);      			
			} // if	
			if(((FacturaFicticia)this.getAdminOrden().getOrden()).isValid()) {
				transaccion= new Transaccion((FacturaFicticia)this.getAdminOrden().getOrden());
				transaccion.ejecutar(EAccion.NO_APLICA);
			} // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
	} // doCerrarTicket	
	
	@Override
	public void doAlmacenesArticulo(Long idArticulo, Integer index) {
		Map<String, Object>params= null;
		List<Columna>columns     = null;
		try {
			if(idArticulo!= null) {
				params= new HashMap<>();
				params.put("idArticulo", idArticulo);
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getDependencias());
				columns= new ArrayList<>();
				columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
				columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
				columns.add(new Columna("stock", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
				columns.add(new Columna("minimo", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
				columns.add(new Columna("maximo", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
				columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA));
				this.almacenes= new FormatLazyModel("VistaKardexDto", "almacenesDetalle", params, columns);
				UIBackingUtilities.resetDataTable("almacenes");
				UIBackingUtilities.execute("PF('dlgAlmacenes').show();");				
			} // if
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // doDetailArticulo
	
	public void doLoadUsers() {
		List<UISelectEntity> vendedores= null;
		Map<String, Object>params      = null;
		List<Columna> campos           = null;
		try {
			campos= new ArrayList<>();
			params= new HashMap<>();
			params.put("idGrupo", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("perfil", VENDEDOR_PERFIL);
			params.put("idUsuario", JsfBase.getIdUsuario());
			campos.add(new Columna("nombreCompleto", EFormatoDinamicos.MAYUSCULAS));
			vendedores= UIEntity.build("VistaTcJanalUsuariosDto", "cambioUsuario", params, campos, Constantes.SQL_TODOS_REGISTROS);
			if(!vendedores.isEmpty()) {
				this.attrs.put("vendedores", vendedores);
				this.attrs.put("vendedor", UIBackingUtilities.toFirstKeySelectEntity(vendedores));
				UIBackingUtilities.execute("PF('dlgCloseTicket').show();");
			} // if
			else{
				JsfBase.addMessage("Cambio de usuario", "No hay mas usuarios con el mismo perfil", ETipoMensaje.INFORMACION);
				UIBackingUtilities.execute("janal.desbloquear();");
			} // else
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
			Methods.clean(campos);
		} // finally
	} // doLoadUsers
	
	public void doActualizaImage(String idImage, String descripcion) {
		String idEmpresa= null;
		try {
			if(!Cadena.isVacio(descripcion))
  			this.attrs.put("descripcion", descripcion);
			idEmpresa= JsfBase.getAutentifica().getEmpresa().getIdEmpresa().toString();
			if(!idImage.equals("-1")) {
				this.image= LoadImages.getImage(idEmpresa, idImage);
				this.attrs.put("imagePivote", idImage);
			} // if
			else if (getAdminOrden().getArticulos().isEmpty() || (getAdminOrden().getArticulos().size()== 1 && getAdminOrden().getArticulos().get(0).getIdArticulo().equals(-1L)))
				this.image= LoadImages.getImage(idEmpresa, idImage);
			else
				this.image= LoadImages.getImage(idEmpresa, this.attrs.get("imagePivote").toString());
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doActualizaImage
	
	@Override
	public void doUpdateArticulos() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
		boolean buscaPorCodigo    = false;
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			params.put("idAlmacen", JsfBase.getAutentifica().getEmpresa().getIdAlmacen());
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", this.attrs.get("proveedor")== null? new UISelectEntity(new Entity(-1L)): ((UISelectEntity)this.attrs.get("proveedor")).getKey());
			String search= (String) this.attrs.get("codigo"); 
			if(!Cadena.isVacio(search)) {				
				buscaPorCodigo= (((boolean)this.attrs.get("buscaPorCodigo")) && !search.startsWith(".")) || (!((boolean)this.attrs.get("buscaPorCodigo")) && search.startsWith("."));  			
				if(search.startsWith("."))
					search= search.trim().substring(1);				
				search= search.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*");
			} // if	
			else
				search= "WXYZ";
  		params.put("codigo", search);			
      params.put("idArticuloTipo", "3");	      
			if(buscaPorCodigo)
        this.attrs.put("articulos", (List<UISelectEntity>) UIEntity.buildImage("VistaOrdenesComprasDto", "porCodigo", params, columns, 20L));
			else
        this.attrs.put("articulos", (List<UISelectEntity>) UIEntity.buildImage("VistaOrdenesComprasDto", "porNombre", params, columns, 20L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} // doUpdateArticulos	
	
	protected void loadSeries() {
		List<UISelectEntity> series= null;
		Map<String, Object>params  = null;
		List<Columna> columns      = null;
		try {
			columns= new ArrayList<>();
			params= new HashMap<>();
			params.put("idTipo", this.idTipo);
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
			series=(List<UISelectEntity>) UIEntity.build("TcKeetSeriesDto", params, columns);
			this.attrs.put("series", series);
			series=(List<UISelectEntity>) UIEntity.build("TcKeetTiposComprobantesDto", params, columns);
			this.attrs.put("comprobantes", series);
			series=(List<UISelectEntity>) UIEntity.build("TcManticTiposMonedasDto", params, columns);
			this.attrs.put("monedas", series);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} // loadSeries
  
	@Override
	protected void loadSucursales() {
		List<UISelectEntity> sucursales= null;
		Map<String, Object>params      = null;
		List<Columna> columns          = null;
		try {
			columns= new ArrayList<>();
			params= new HashMap<>();
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			sucursales=(List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns);
			this.attrs.put("sucursales", sucursales);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} // loadSucursales
	
	@Override
	public void doActivarDescuento() {
		String tipoDescuento= null;		
		try {
			tipoDescuento= this.attrs.get("tipoDescuento").toString();
			this.attrs.put("isIndividual", tipoDescuento.equals(INDIVIDUAL));
			this.attrs.put(tipoDescuento.equals(INDIVIDUAL) ? "descuentoGlobal" : "descuentoIndividual", 0);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doActivarDescuento
	
	@Override
	public void doAplicarDescuento() {
		doAplicarDescuento(-1);
	} // doAplicarDescuento
	
	@Override
	public void doAplicarDescuento(Integer index) {
		Boolean isIndividual       = false;
		CambioUsuario cambioUsuario= null;
		String cuenta              = null;
		String contrasenia         = null;
		Double global              = 0D;
		try {
			if(!getAdminOrden().getArticulos().isEmpty()) {
				cuenta= this.attrs.get("usuarioDescuento").toString();
				contrasenia= this.attrs.get("passwordDescuento").toString();
				cambioUsuario= new CambioUsuario(cuenta, contrasenia);
				if(cambioUsuario.validaPrivilegiosDescuentos()) {
					isIndividual= Boolean.valueOf(this.attrs.get("isIndividual").toString());
					if(isIndividual) {
						getAdminOrden().getArticulos().get(index).setDescuento(this.attrs.get("descuentoIndividual").toString());
						if(getAdminOrden().getArticulos().get(index).autorizedDiscount())
							UIBackingUtilities.execute("jsArticulos.divDiscount('".concat(this.attrs.get("descuentoIndividual").toString()).concat("');"));
						else
							JsfBase.addMessage("No es posble aplicar el descuento, el descuento es superior a la utilidad", ETipoMensaje.ERROR);
					} // if
					else{		
						global= Double.valueOf(this.attrs.get("descuentoGlobal").toString());
						getAdminOrden().toCalculate();
						if(global < getAdminOrden().getTotales().getUtilidad()) {
							getAdminOrden().getTotales().setGlobal(global);							
							getAdminOrden().toCalculate();
						} // if
						else
							JsfBase.addMessage("No es posble aplicar el descuento, el descuento es superior a la utilidad", ETipoMensaje.ERROR);
					} // else
				} // if
				else
					JsfBase.addMessage("El usuario no tiene privilegios o el usuario y la contraseña son incorrectos", ETipoMensaje.ERROR);
			} // if
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally{			
			this.attrs.put("isIndividual", true);
			this.attrs.put("descuentoIndividual", 0);
			this.attrs.put("descuentoGlobal", 0);
			this.attrs.put("tipoDescuento", INDIVIDUAL);
			this.attrs.put("usuarioDescuento", "");
			this.attrs.put("passwordDescuento", "");
		} // finally
	} // doAplicarDescuento	
	
	public void doUpdateDesarrollos() {
		try {
 			this.attrs.put("clientesSeleccion", new ArrayList<>());
      this.doLoadDesarrollos();
			this.loadDomicilios(((FacturaFicticia)this.getAdminOrden().getOrden()).getIdCliente());
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doUpdateForEmpresa	
	
	public void doLoadDesarrollos() {
		List<Columna> columns           = null;
    Map<String, Object> params      = null;		
		List<UISelectEntity> desarrollos= null;
    try {
			params= new HashMap<>();						
			params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa=" + ((FacturaFicticia)this.getAdminOrden().getOrden()).getIkEmpresa().getKey());
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
			desarrollos= (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave");
			if(!desarrollos.isEmpty()) {
				this.attrs.put("desarrollos", desarrollos);			
				if(this.accion.equals(EAccion.AGREGAR)) 
          ((FacturaFicticia)this.getAdminOrden().getOrden()).setIkDesarrollo(desarrollos.get(0));
        else
				  ((FacturaFicticia)this.getAdminOrden().getOrden()).setIkDesarrollo(desarrollos.get(desarrollos.indexOf(((FacturaFicticia)this.getAdminOrden().getOrden()).getIkDesarrollo())));
        ((FacturaFicticia)this.getAdminOrden().getOrden()).setIkCliente(new UISelectEntity(((FacturaFicticia)this.getAdminOrden().getOrden()).getIkDesarrollo().toLong("idCliente")));
			  this.doAsignaClienteInicial(((FacturaFicticia)this.getAdminOrden().getOrden()).getIdCliente());
			} // if
      else {
				this.attrs.put("desarrollos", new ArrayList<>());
				((FacturaFicticia)this.getAdminOrden().getOrden()).setIkDesarrollo(new UISelectEntity(-1L));
        ((FacturaFicticia)this.getAdminOrden().getOrden()).setIkCliente(new UISelectEntity(-1L));
			  this.doAsignaClienteInicial(-1L);
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
			params.put("idDesarrollo", ((FacturaFicticia)this.getAdminOrden().getOrden()).getIdDesarrollo());
			contratos= UIEntity.seleccione("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
			this.attrs.put("contratos", contratos);
      if(contratos== null || contratos.isEmpty()) 
        ((FacturaFicticia)this.getAdminOrden().getOrden()).setIkContrato(new UISelectEntity(-1L));
      else  
        if(this.accion.equals(EAccion.AGREGAR))
          ((FacturaFicticia)this.getAdminOrden().getOrden()).setIkContrato(contratos.get(0));
        else  
          ((FacturaFicticia)this.getAdminOrden().getOrden()).setIkContrato(contratos.get(contratos.indexOf(((FacturaFicticia)this.getAdminOrden().getOrden()).getIkContrato())));
      this.doUpdateDomicilio(null);
		} // try // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadContratos
  
	protected void loadDomicilios(Long idCliente) throws Exception {
		Map<String, Object>params     = null;
		List<UISelectEntity>domicilios= null;
		List<Columna>campos           = null;
		try {
			params= new HashMap<>();					
			params.put("idCliente", idCliente);
			campos= new ArrayList<>();
			campos.add(new Columna("calle", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("asentamiento", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("localidad", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("municipio", EFormatoDinamicos.MAYUSCULAS));
			domicilios= UIEntity.build("VistaClientesDto", "domiciliosCliente", params, campos, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("domicilios", domicilios);
			if(domicilios!= null && !domicilios.isEmpty()) {
        UISelectEntity domicilio= UIBackingUtilities.toFirstKeySelectEntity(domicilios);
				this.attrs.put("domicilio", domicilio);
        ((FacturaFicticia)this.getAdminOrden().getOrden()).setIdClienteDomicilio(domicilio.getKey());
      } // if  
      else
        if(idCliente!= null && idCliente> 0)
          JsfBase.addMessage("El cliente no tiene un domicilio registrado !", ETipoMensaje.ERROR);
		} // try		
		finally {
			Methods.clean(params);
		} // finally
	} // loadDomicilios
	
	@Override
	public void doAsignaCliente(SelectEvent event) {
		UISelectEntity seleccion     = null;
		List<UISelectEntity> clientes= null;
		try {
			clientes= (List<UISelectEntity>) this.attrs.get("clientes");
			seleccion= clientes.get(clientes.indexOf((UISelectEntity)event.getObject()));
			this.toFindCliente(seleccion);
			this.loadDomicilios(seleccion.getKey());
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doAsignaCliente
	
	protected void toFindCliente(UISelectEntity seleccion) {
		List<UISelectEntity> clientesSeleccion= null;
		try {
			clientesSeleccion= (List<UISelectEntity>)this.attrs.get("clientesSeleccion");
			clientesSeleccion.clear();
			clientesSeleccion.add(seleccion);
      ((FacturaFicticia)this.getAdminOrden().getOrden()).setIkCliente(seleccion);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // toFindCliente
	
	@Override
	public List<UISelectEntity> doCompleteCliente(String query) {
		this.attrs.put("codigoCliente", query);
    this.doUpdateClientes();		
		return (List<UISelectEntity>)this.attrs.get("clientes");
	}	// doCompleteCliente
	
	@Override
	public void doUpdateClientes() {
		List<Columna> columns     = null;
    Map<String, Object> params= null;
    try {
			params= new HashMap<>();
			columns= new ArrayList<>();
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
  		params.put("idEmpresa", ((FacturaFicticia)this.getAdminOrden().getOrden()).getIdEmpresa());
			String search= (String) this.attrs.get("codigoCliente"); 
			search= !Cadena.isVacio(search)? search.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*") : "WXYZ";
  		params.put(Constantes.SQL_CONDICION, "upper(tc_mantic_clientes.razon_social) regexp '.*".concat(search).concat(".*'").concat(" or upper(tc_mantic_clientes.rfc) regexp '.*".concat(search).concat(".*'")));			
      this.attrs.put("clientes", (List<UISelectEntity>) UIEntity.build("VistaClientesDto", "findRazonSocial", params, columns, 20L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}	// doUpdateClientes
	
	public void doUpdateContratos(AjaxBehaviorEvent event) {
		List<UISelectEntity> desarrollos= null;
		UISelectEntity desarrollo       = null;
		Map<String, Object>params       = new HashMap<>();
		try {
			desarrollos= (List<UISelectEntity>) this.attrs.get("desarrollos");
			desarrollo = ((FacturaFicticia)this.getAdminOrden().getOrden()).getIkDesarrollo();
      UISelectEntity item= desarrollos.get(desarrollos.indexOf(desarrollo));
      ((FacturaFicticia)this.getAdminOrden().getOrden()).setIkCliente(new UISelectEntity(item.toLong("idCliente")));
      this.doAsignaClienteInicial(((FacturaFicticia)this.getAdminOrden().getOrden()).getIdCliente());
      this.loadDomicilios(((FacturaFicticia)this.getAdminOrden().getOrden()).getIdCliente());
			this.doLoadContratos();
		} // try // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	} // doUpdateContratos
  
	public String doClientes() {
		String regresar        = null;
		Transaccion transaccion= null;
		try {
      Long idCliente= ((FacturaFicticia)this.getAdminOrden().getOrden()).getIdCliente();
			if(idCliente!= null && idCliente> 0) {
				if(!this.getAdminOrden().getArticulos().isEmpty() && (this.getAdminOrden().getArticulos().size() > 1 || (this.getAdminOrden().getArticulos().size()== 1 && (this.getAdminOrden().getArticulos().get(0).getIdArticulo()!= null && !this.getAdminOrden().getArticulos().get(0).getIdArticulo().equals(-1L))))) {
					((FacturaFicticia)this.getAdminOrden().getOrden()).setIdVentaEstatus(ESTATUS_ELABORADA);
					this.loadOrdenVenta();
					transaccion = new Transaccion((FacturaFicticia)this.getAdminOrden().getOrden(), this.getAdminOrden().getArticulos(), (String)this.attrs.get("observaciones"));
					this.getAdminOrden().toAdjustArticulos();
					transaccion.ejecutar(EAccion.DESACTIVAR);
					JsfBase.setFlashAttribute("idVenta", transaccion.getOrden().getIdVenta());
				} // if
				else
					JsfBase.setFlashAttribute("idVenta", -1L);																											
				JsfBase.setFlashAttribute("idCliente", idCliente);
				JsfBase.setFlashAttribute("accion", EAccion.MODIFICAR);
			} // if
      else {
				JsfBase.setFlashAttribute("idVenta", -1L);																							
				JsfBase.setFlashAttribute("idCliente", -1L);
				JsfBase.setFlashAttribute("accion", EAccion.AGREGAR);
			} // else
			JsfBase.setFlashAttribute("observaciones", this.attrs.get("observaciones"));								
			JsfBase.setFlashAttribute("regreso", "/Paginas/Mantic/Facturas/accion.jsf");								
			regresar= "/Paginas/Mantic/Ventas/cliente.jsf".concat(Constantes.REDIRECIONAR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
		return regresar;
	} // doCatalogos
	
	private void loadBancos() {
		List<UISelectEntity> bancos= null;
		Map<String, Object> params = null;
		List<Columna> campos       = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			campos= new ArrayList<>();
			campos.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			bancos= UIEntity.build("TcManticBancosDto", "row", params, campos, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("bancos", bancos);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadBancos
	
	private void loadCfdis() {
		List<UISelectEntity> cfdis= null;
		List<Columna> campos      = null;
		Map<String, Object>params = null;
		try {
			params= new HashMap<>();
			campos= new ArrayList<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			campos.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			cfdis= UIEntity.seleccione("TcManticUsosCfdiDto", "row", params, campos, Constantes.SQL_TODOS_REGISTROS, "clave");
			this.attrs.put("cfdis", cfdis);
			this.attrs.put("cfdi", new UISelectEntity("-1"));
			for(Entity record: cfdis) {
				if(record.getKey().equals(3L))
					this.attrs.put("cfdi", record);
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // loadCfdis
	
	private void loadTiposMediosPagos() {
		List<UISelectEntity> tiposPagos= null;
		Map<String, Object>params      = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_cobro_caja=1");
			tiposPagos= UIEntity.build("TcManticTiposMediosPagosDto", "row", params);
			this.attrs.put("tiposMedioPagos", tiposPagos);
			this.attrs.put("tipoMedioPago", UIBackingUtilities.toFirstKeySelectEntity(tiposPagos));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadTiposPagos
	
	private void loadTiposPagos() {
		List<UISelectEntity> tiposPagos= null;
		Map<String, Object>params      = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			tiposPagos= UIEntity.seleccione("TcManticTiposPagosDto", "row", params, "nombre");
			this.attrs.put("tiposPagos", tiposPagos);
			this.attrs.put("tipoPago", UIBackingUtilities.toFirstKeySelectEntity(tiposPagos));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadTiposPagos
	
	public void doValidaTipoPago() {
		Long tipoPago= -1L;
		try {
			tipoPago= Long.valueOf(this.attrs.get("tipoMedioPago").toString());
			this.attrs.put("mostrarBanco", !ETipoMediosPago.EFECTIVO.getIdTipoMedioPago().equals(tipoPago));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doValidaTipoPago

	@Override
	public void toSaveRecord() {
		Transaccion transaccion= null;
		EAccion eaccion        = null;		
    try {			
			this.loadOrdenVenta();
			eaccion= ((FacturaFicticia)this.getAdminOrden().getOrden()).getIdVenta().equals(-1L) ? EAccion.AGREGAR : EAccion.MODIFICAR;						
			transaccion = new Transaccion((FacturaFicticia)this.getAdminOrden().getOrden(), this.getAdminOrden().getArticulos(), (String)this.attrs.get("observaciones"));			
			if (transaccion.ejecutar(eaccion)) { 
				if(eaccion.equals(EAccion.AGREGAR))
					UIBackingUtilities.execute("jsArticulos.back('gener\\u00F3 la factura ', '"+ ((FacturaFicticia)this.getAdminOrden().getOrden()).getConsecutivo()+ "');");
  			JsfBase.setFlashAttribute("idVenta", ((FacturaFicticia)this.getAdminOrden().getOrden()).getIdVenta());
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar la factura.", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
	} // toSaveRecord
	
	public void doGlobalEvent(Boolean isViewException) {
		LOG.error("ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA");
		if(isViewException && this.getAdminOrden().getArticulos().size()> 0)
		  this.toSaveRecord();
    //UIBackingUtilities.execute("alert('ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA');");
	} // doGlobalEvent

	public void doTabChange(TabChangeEvent event) {
		switch(event.getTab().getTitle()) {
      case "Generales":
        break;
      case "Lotes":
        break;
    } // switch
	}	// doTabChange	

	private void toLoadCollections() {
		this.toLoadTiposDomicilios();	
		this.toLoadDomicilios();
		this.doLoadEntidades();		
		this.doCompleteCodigoPostal(((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().getCodigoPostal());
  }

  private void toLoadTiposDomicilios() {
    List<UISelectItem> tiposDomicilios = null;
    try {
      tiposDomicilios = new ArrayList<>();
      for (ETiposDomicilios tipoDomicilio : ETiposDomicilios.values()) {
        tiposDomicilios.add(new UISelectItem(tipoDomicilio.getKey(), Cadena.reemplazarCaracter(tipoDomicilio.name(), '_', ' ')));
      } // for
      this.attrs.put("tiposDomicilios", tiposDomicilios);
    } // try
    catch (Exception e) {
      throw e;
    } // catch		    
  } // toLoadTiposDomicilios
  
  public void doLoadDomicilio(boolean all) {    
		List<Entity> domicilios= null;
    try {
			if(all){
				if(!((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().getDomicilio().getKey().equals(-1L)) {
					domicilios= (List<Entity>)this.attrs.get("domicilios");
					((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setDomicilio(domicilios.get(domicilios.indexOf(((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().getDomicilio())));
					((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setIdDomicilio(domicilios.get(domicilios.indexOf(((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().getDomicilio())).getKey());
				} // if
				else{
					((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setDomicilio(new Entity(-1L));
					((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setIdDomicilio(-1L);
				} // else		
				this.doLoadEntidades();				
			} // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } // doLoadDomicilio  
 
  protected void toLoadDomicilios() {
		List<UISelectEntity> domicilios= null;
		try {
			domicilios= new ArrayList<>();
			this.attrs.put("domicilios", domicilios);     
			((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setDomicilio(new Entity(-1L, "SELECCIONE"));
      ((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setIdDomicilio(-1L);
		} // try
		catch (Exception e) {		
			throw e;
		} // catch		
	} // toLoadDomicilios

  public void doLoadEntidades() {
    List<UISelectEntity> entidades= null;
		List<Columna>campos           = null;
    Map<String, Object> params    = null;
    try {
      params = new HashMap<>();
      params.put("idPais", 1);
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			campos= new ArrayList<>();
			campos.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
      entidades= UIEntity.build("TcJanalEntidadesDto", "comboEntidades", params, campos, Constantes.SQL_TODOS_REGISTROS);
      this.attrs.put("entidades", entidades);
      ((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setIdEntidad(entidades.get(0));
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
    this.doLoadMunicipios();
  } // doLoadEntidades
  
  public void doLoadMunicipios() {
    List<UISelectEntity> municipios= null;
    Map<String, Object> params= null;
		List<Columna>campos= null;
    try {
			if(!((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().getIdEntidad().getKey().equals(-1L)) {
				params = new HashMap<>();
				params.put("idEntidad", ((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().getIdEntidad().getKey());
				campos= new ArrayList<>();
				campos.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
				municipios = UIEntity.build("TcJanalMunicipiosDto", "comboMunicipios", params, campos, Constantes.SQL_TODOS_REGISTROS);
				this.attrs.put("municipios", municipios);
				((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setIdMunicipio(municipios.get(0));
			} // if
			else
				((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setIdMunicipio(new Entity(-1L));
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
    this.doLoadLocalidades();
  } // doLoadMunicipios
  
  public void doLoadLocalidades() {
    List<UISelectEntity> localidades= null;
    Map<String, Object> params= null;
		List<Columna>campos= null;
    try {
			if(!((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().getIdMunicipio().getKey().equals(-1L)) {
				params = new HashMap<>();
				params.put("idMunicipio", ((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().getIdMunicipio().getKey());
				campos= new ArrayList<>();
				campos.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
				localidades = UIEntity.build("TcJanalLocalidadesDto", "comboLocalidades", params, campos, Constantes.SQL_TODOS_REGISTROS);
				this.attrs.put("localidades", localidades);
				((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setIdLocalidad(localidades.get(0));
			} // if
      else 
				((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setIdLocalidad(new Entity(-1L));
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // doLoadLocalidades

  public void doLoadCodigosPostales() {
    List<UISelectItem> codigosPostales = null;
    Map<String, Object> params = null;
    try {
			if(!((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().getIdEntidad().getKey().equals(-1L)) {
				params = new HashMap<>();
				params.put(Constantes.SQL_CONDICION, "id_entidad=" + ((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().getIdEntidad().getKey());
				codigosPostales = UISelect.build("TcManticCodigosPostalesDto", "row", params, "codigo", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
				this.attrs.put("codigosPostales", codigosPostales);
				if (!codigosPostales.isEmpty()) {
					((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setCodigoPostal(codigosPostales.get(0).getLabel());
					((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setIdCodigoPostal((Long) codigosPostales.get(0).getValue());
					((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setNuevoCp(true);
          this.attrs.put("codigoSeleccionado", codigosPostales.get(0));
				} // if
				else 
					((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setNuevoCp(false);				
			} // if
			else
				((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setNuevoCp(false);				
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // doLoadCodigosPostales  
  
	private void toAsignaEntidad() {
		List<Entity>entidades= null;
		try {
			if(((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().getIdEntidad().getKey()> 0L) {
				entidades= (List<Entity>) this.attrs.get("entidades");
        int index= entidades.indexOf(new UISelectEntity(((FacturaFicticia)this.getAdminOrden().getOrden()).getDomicilioContrato().getIdEntidad()));
        if(index>= 0)
          ((FacturaFicticia)this.getAdminOrden().getOrden()).getDomicilioContrato().setIdEntidad(entidades.get(index)); 
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toAsignaEntidad

	private void toAsignaMunicipio() {
		List<Entity>municipios= null;
		try {
			if(((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().getIdMunicipio().getKey()> 0L) {
				municipios= (List<Entity>) this.attrs.get("municipios");
        int index= municipios.indexOf(new UISelectEntity(((FacturaFicticia)this.getAdminOrden().getOrden()).getDomicilioContrato().getIdMunicipio()));
        if(index>= 0)
          ((FacturaFicticia)this.getAdminOrden().getOrden()).getDomicilioContrato().setIdMunicipio(municipios.get(index)); 
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toAsignaMunicipio
  
	private void toAsignaLocalidad() {
		List<Entity>localidades= null;
		try {
			if(((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().getIdLocalidad().getKey()> 0L) {
				localidades= (List<Entity>) this.attrs.get("localidades");
        int index= localidades.indexOf(new UISelectEntity(((FacturaFicticia)this.getAdminOrden().getOrden()).getDomicilioContrato().getIdLocalidad()));
        if(index>= 0)
          ((FacturaFicticia)this.getAdminOrden().getOrden()).getDomicilioContrato().setIdLocalidad(localidades.get(index)); 
			} // if			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toAsignaLocalidad

	public List<UISelectEntity> doCompleteCodigoPostal(String query) {		
		if(((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().getIdEntidad().getKey()>= 1L && !Cadena.isVacio(query)) {
			this.attrs.put("condicionCodigoPostal", query);
			this.doLoadCodigosPostales();		
			return (List<UISelectEntity>)this.attrs.get("allCodigosPostales");
		} // if
    else {
			((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setNuevoCp(false);
			((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setIdCodigoPostal(-1L);
			((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setCodigoPostal("");
	  	return new ArrayList<>();
		} // else		
	}	// doCompleteCliente

  private void toContratoDomicilio() {
    try {
      this.toAsignaEntidad();
			this.doLoadMunicipios();
      this.toAsignaMunicipio();
      this.toAsignaLocalidad();		
    } // try
    catch (Exception e) {
      mx.org.kaana.libs.formato.Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // toContratoDomicilio

	public void doAsignaContratoDomicilio() {
		List<UISelectEntity> domiciliosBusqueda= null;
		UISelectEntity domicilio               = null;
		try {
			domiciliosBusqueda=(List<UISelectEntity>)this.attrs.get("contratoDomiciliosBusqueda");
			domicilio         = domiciliosBusqueda.get(domiciliosBusqueda.indexOf(this.domicilioContratoBusqueda));
			((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setDomicilio(domicilio);
      ((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setIdDomicilio(domicilio.getKey());
			((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setCalle(domicilio.toString("calle"));
			((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setCodigoPostal(domicilio.toString("codigoPostal"));
			((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setColonia(domicilio.toString("asentamiento"));
			((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setEntreCalle(domicilio.toString("entreCalle"));
			((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setyCalle(domicilio.toString("ycalle"));
			((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setExterior(domicilio.toString("numeroExterior"));
			((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setInterior(domicilio.toString("numeroInterior"));
			((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setPrincipal(true);
			domicilio.put("idEntidad", new Value("idEntidad", domicilio.toLong("idEntidad")));
			domicilio.put("idMunicipio", new Value("idMunicipio", domicilio.toLong("idMunicipio")));
			domicilio.put("idLocalidad", new Value("idLocalidad", domicilio.toLong("idLocalidad")));
      if(!ESql.INSERT.equals(((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().getSqlAccion()))
        ((FacturaFicticia)getAdminOrden().getOrden()).getDomicilioContrato().setSqlAccion(ESql.UPDATE);
			this.toAsignaEntidad();
			this.doLoadMunicipios();
			this.toAsignaMunicipio();
			this.toAsignaLocalidad();			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doAsignaDomicilio

  public void doBusquedaContratoDomicilios() {
    List<UISelectEntity> domicilios= null;
    Map<String, Object> params     = null;
		List<Columna>campos            = null;
    try {
      params = new HashMap<>();      
      params.put(Constantes.SQL_CONDICION, "upper(calle) like upper('%".concat(this.attrs.get("calle").toString()).concat("%')"));
			campos= new ArrayList<>();
			campos.add(new Columna("calle", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("numeroExterior", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("numeroInterior", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("asentamiento", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("entidad", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("municipio", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("localidad", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("domicilio", EFormatoDinamicos.MAYUSCULAS));
      domicilios = UIEntity.build("VistaDomiciliosCatalogosDto", "domicilios", params, campos, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("contratoDomiciliosBusqueda", domicilios);      
			this.attrs.put("resultados", domicilios.size());      
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // doLoadDomicilios

  public void doUpdateDomicilio(AjaxBehaviorEvent event) {
    try {
      List<UISelectEntity> contratos= (List<UISelectEntity>)this.attrs.get("contratos");
      int index= contratos.indexOf(((FacturaFicticia)getAdminOrden().getOrden()).getIkContrato());
      if(index>= 0)
        ((FacturaFicticia)getAdminOrden().getOrden()).setIkContrato(contratos.get(index));
      this.attrs.put("isContrato", ((FacturaFicticia)getAdminOrden().getOrden()).getIdContrato()!= null && ((FacturaFicticia)getAdminOrden().getOrden()).getIdContrato()> 0);
      ((AdminFacturas)this.getAdminOrden()).toContratoDomicilios();
      ((AdminFacturas)this.getAdminOrden()).toLoadContratoLotes();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  }

  public String doColorRow(Parcial row) {
    return row.getSqlAccion().equals(ESql.DELETE)? "janal-table-tr-hide": ""; 
  }
  
  public void doAgregarLote() {
    Parcial clon= new Parcial(this.attrs.get("idContratoLote")!= null? new Long((String)this.attrs.get("idContratoLote")): -1L);
    int index= ((FacturaFicticia)this.getAdminOrden().getOrden()).getDisponibles().indexOf(clon);
    if(index>= 0)
      clon= ((FacturaFicticia)this.getAdminOrden().getOrden()).getDisponibles().get(index);
    if(((FacturaFicticia)this.getAdminOrden().getOrden()).getParciales().indexOf(clon)< 0) {
      ((FacturaFicticia)this.getAdminOrden().getOrden()).getDisponibles().remove(clon);
      if(clon.getSqlAccion().equals(ESql.DELETE))
        clon.setSqlAccion(ESql.UPDATE);
      ((FacturaFicticia)this.getAdminOrden().getOrden()).getParciales().add(clon);
    } // if  
   ((AdminFacturas)this.getAdminOrden()).getTotales().setParciales(((FacturaFicticia)this.getAdminOrden().getOrden()).getParciales().size());
//    UIBackingUtilities.execute("janal.restore();");
  }
  
  public void doEliminarLote(Parcial clon) {
    if(clon!= null) {
      int index= ((FacturaFicticia)this.getAdminOrden().getOrden()).getParciales().indexOf(clon);
      if(index>= 0) {
        ((FacturaFicticia)this.getAdminOrden().getOrden()).getParciales().remove(index);
        if(clon.getSqlAccion().equals(ESql.SELECT) || clon.getSqlAccion().equals(ESql.UPDATE))
          clon.setSqlAccion(ESql.DELETE);
        ((FacturaFicticia)this.getAdminOrden().getOrden()).getDisponibles().add(clon);
        ((AdminFacturas)this.getAdminOrden()).getTotales().setParciales(((FacturaFicticia)this.getAdminOrden().getOrden()).getParciales().size());
        // UIBackingUtilities.execute("janal.restore();");
      } // if  
    } // if
  }
   
  public void doSincronizarLote() {
    this.toAsignaEntidad();
    this.toAsignaMunicipio();
    this.toAsignaLocalidad();
    for (Parcial item : ((FacturaFicticia)this.getAdminOrden().getOrden()).getDisponibles()) {
      item.setEntidad(((FacturaFicticia)this.getAdminOrden().getOrden()).getDomicilioContrato().getIdEntidad().toString("descripcion"));
      item.setMunicipio(((FacturaFicticia)this.getAdminOrden().getOrden()).getDomicilioContrato().getIdMunicipio().toString("descripcion"));
      item.setLocalidad(((FacturaFicticia)this.getAdminOrden().getOrden()).getDomicilioContrato().getIdLocalidad().toString("descripcion"));
      item.setCodigoPostal(((FacturaFicticia)this.getAdminOrden().getOrden()).getDomicilioContrato().getCodigoPostal());
      item.setCalle(((FacturaFicticia)this.getAdminOrden().getOrden()).getDomicilioContrato().getCalle());
    } // for
    for (Parcial item : ((FacturaFicticia)this.getAdminOrden().getOrden()).getParciales()) {
      item.setEntidad(((FacturaFicticia)this.getAdminOrden().getOrden()).getDomicilioContrato().getIdEntidad().toString("descripcion"));
      item.setMunicipio(((FacturaFicticia)this.getAdminOrden().getOrden()).getDomicilioContrato().getIdMunicipio().toString("descripcion"));
      item.setLocalidad(((FacturaFicticia)this.getAdminOrden().getOrden()).getDomicilioContrato().getIdLocalidad().toString("descripcion"));
      item.setCodigoPostal(((FacturaFicticia)this.getAdminOrden().getOrden()).getDomicilioContrato().getCodigoPostal());
      item.setCalle(((FacturaFicticia)this.getAdminOrden().getOrden()).getDomicilioContrato().getCalle());
    } // for
  }

  public void doLookForCodigoPostal() {
		Map<String, Object>params= null;
    try {
      params             = new HashMap<>();			
      String codigoPostal= ((FacturaFicticia)this.getAdminOrden().getOrden()).getDomicilioContrato().getCodigoPostal();
      if(!Cadena.isVacio(codigoPostal)) {
        params.put("codigo", codigoPostal);			
        Value value= (Value)DaoFactory.getInstance().toField("TcManticCodigosPostalesDto", "unico", params, "idCodigoPostal");
        if(value!= null && value.getData()!= null)
          ((FacturaFicticia)this.getAdminOrden().getOrden()).getDomicilioContrato().setIdCodigoPostal(value.toLong());
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
  
}
