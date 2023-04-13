package mx.org.kaana.mantic.catalogos.clientes.cuentas.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.clientes.cuentas.beans.Cuenta;
import mx.org.kaana.mantic.catalogos.clientes.cuentas.beans.Pago;
import mx.org.kaana.mantic.catalogos.clientes.cuentas.reglas.Transaccion;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.db.dto.TcManticClientesPagosDto;
import mx.org.kaana.mantic.enums.EEstatusClientes;
import mx.org.kaana.mantic.enums.ETipoMediosPago;
import mx.org.kaana.mantic.inventarios.comun.IBaseImportar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;

@Named(value = "manticCatalogosClientesCuentasDeuda")
@ViewScoped
public class Deuda extends IBaseImportar implements Serializable {

  private static final Log LOG = LogFactory.getLog(Deuda.class);
  private static final long serialVersionUID = 8793667741599428879L;	
  
  private enum EClaveCatalogo {MEDIOS_PAGO, TIPOS_PAGOS, USOS_CFDI, SERIES, COMPROBANTES};

	private FormatLazyModel detallePagos;
	private List<Cuenta> pagosSegmento;
	private List<Cuenta> pagosCuenta;
	private List<SelectItem> contratos;
  private Pago pago;

	public FormatLazyModel getDetallePagos() {
		return detallePagos;
	}

	public List<Cuenta> getPagosSegmento() {
		return pagosSegmento;
	}	

  public Pago getPago() {
    return pago;
  }

  public void setPago(Pago pago) {
    this.pago= pago;
  }

  public List<Cuenta> getPagosCuenta() {
    return pagosCuenta;
  }

  public void setCuentas(List<Cuenta> pagosCuenta) {
    this.pagosCuenta = pagosCuenta;
  }

  public List<SelectItem> getContratos() {
    return contratos;
  }
	
  @PostConstruct
  @Override
  protected void init() {
		Long idEmpresaInicial= -1L;
    try {			
      this.pago= new Pago();
      this.attrs.put("idCliente", JsfBase.getFlashAttribute("idCliente"));         
			idEmpresaInicial= JsfBase.getAutentifica().getEmpresa().getIdEmpresa();
			this.attrs.put("idEmpresa", idEmpresaInicial);
			this.attrs.put("idEmpresaGeneral", idEmpresaInicial);
			this.attrs.put("idEmpresaSegmento", idEmpresaInicial);
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("mostrarBancoGeneral", false);
			this.attrs.put("mostrarBanco", false);
			this.attrs.put("saldar", "2");
			this.attrs.put("saldarGeneral", "2");
			this.attrs.put("saldarSegmento", "2");
			this.attrs.put("fechaPago", LocalDate.now());
			this.attrs.put("fechaPagoGeneral", LocalDate.now());
			this.attrs.put("fechaPagoSegmento", LocalDate.now());
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
				this.loadSucursales();							
			this.doLoadCajas();
			this.doLoadCajasGeneral();
			this.doLoadCajasSegmento();
			this.loadBancos();
			this.loadTiposPagos();
			this.loadClienteDeuda();			
      if((Boolean)this.attrs.get("activePagoGeneral")) {
        UIBackingUtilities.execute("janal.bloquear(); PF('dlgPagoSegmento').show();");
        this.doLoadCuentas();
      } // if
			this.attrs.put("observaciones", "");
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_FACTURA);
      this.attrs.put("total", 0D);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

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
	
	private void loadSucursales() {
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
			this.attrs.put("idEmpresa", sucursales.get(0));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} // loadSucursales
	
	public void doLoadCajas() {
		List<UISelectEntity> cajas= null;
		Map<String, Object>params = null;
		List<Columna> columns     = null;
		try {
			columns= new ArrayList<>();
			params= new HashMap<>();
			params.put("idEmpresa", this.attrs.get("idEmpresa"));
			columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			cajas=(List<UISelectEntity>) UIEntity.build("TcManticCajasDto", "cajas", params, columns);
			this.attrs.put("cajas", cajas);
			this.attrs.put("caja", cajas.get(0));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch	
	} // loadCajas
	
	public void doLoadCajasGeneral() {
		List<UISelectEntity> cajas= null;
		Map<String, Object>params = null;
		List<Columna> columns     = null;
		try {
			columns= new ArrayList<>();
			params= new HashMap<>();
			params.put("idEmpresa", this.attrs.get("idEmpresaGeneral"));
			columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			cajas=(List<UISelectEntity>) UIEntity.build("TcManticCajasDto", "cajas", params, columns);
			this.attrs.put("cajasGenerales", cajas);
			this.attrs.put("cajaGeneral", cajas.get(0));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch	
	} // loadCajas
	
	public void doLoadCajasSegmento() {
		List<UISelectEntity> cajas= null;
		Map<String, Object>params = null;
		List<Columna> columns     = null;
		try {
			columns= new ArrayList<>();
			params= new HashMap<>();
			params.put("idEmpresa", this.attrs.get("idEmpresaSegmento"));
			columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			cajas=(List<UISelectEntity>) UIEntity.build("TcManticCajasDto", "cajas", params, columns);
			this.attrs.put("cajasSegmento", cajas);
			this.attrs.put("cajaSegmento", cajas.get(0));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch	
	} // loadCajas
	
	private void loadClienteDeuda() throws Exception {
		Entity deuda             = null;
    List<Columna> columns    = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idCliente", this.attrs.get("idCliente"));						
			deuda= (Entity) DaoFactory.getInstance().toEntity("VistaClientesDto", "deuda", params);
      columns= new ArrayList<>();  
			columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("deuda", EFormatoDinamicos.MILES_CON_DECIMALES));
      if(deuda!= null) {
        this.attrs.put("deuda", deuda);
        this.attrs.put("pago", deuda.toDouble("saldo"));
        this.attrs.put("pagoGeneral", deuda.toDouble("saldo"));
        this.attrs.put("pagoSegmento", deuda.toDouble("saldo"));
        this.attrs.put("recuperarPagoSegmento", deuda.toDouble("saldo"));
        UIBackingUtilities.toFormatEntity(deuda, columns);
      } // if  
			this.doLoad();
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally{
			Methods.clean(params);
      Methods.clean(columns);
		} // finally
	} // loadClienteDeuda
	
  @Override
  public void doLoad() {
    List<Columna> columns     = null;
	  Map<String, Object> params= null;
		List<Entity> cuentas      = null;
    try {  	  
			params= new HashMap<>();
			params.put("idCliente", this.attrs.get("idCliente"));						
			params.put("sortOrder", "order by tc_mantic_ventas.registro desc");			
			// params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes_deudas.id_cliente_deuda_estatus in (1, 2)");			
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);			
      columns= new ArrayList<>();  
			columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
			columns.add(new Columna("limite", EFormatoDinamicos.FECHA_CORTA));
			columns.add(new Columna("saldo", EFormatoDinamicos.MILES_SAT_DECIMALES));
			columns.add(new Columna("importe", EFormatoDinamicos.MILES_SAT_DECIMALES));
			columns.add(new Columna("persona", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			this.lazyModel = new FormatCustomLazy("VistaClientesDto", "cuentas", params, columns);			
			cuentas= DaoFactory.getInstance().toEntitySet("VistaClientesDto", "cuentas", params);
			this.validaPagoGeneral(cuentas);
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
  } // doLoad
	
	private void validaPagoGeneral(List<Entity> cuentas) {
		int count= 0;
		try {
			for(Entity cuenta: cuentas) {
				if(!(cuenta.toLong("idClienteDeudaEstatus").equals(EEstatusClientes.FINALIZADA.getIdEstatus())))
					count++;
			} // for
			this.attrs.put("activePagoGeneral", count>0);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
			throw e;
		} // catch
	} // validaPagogeneral
	
	public void doLoadCuentas() {
	  Map<String, Object> params= null;	
		try {
			params= new HashMap<>();
			params.put("idCliente", this.attrs.get("idCliente"));						
			params.put("sortOrder", "order by	tc_mantic_clientes_deudas.registro desc");			
			params.put(Constantes.SQL_CONDICION, " tc_mantic_clientes_deudas.saldo> 0 and tc_mantic_clientes_deudas.id_cliente_deuda_estatus in(1, 2)");			
			this.pagosSegmento= (List<Cuenta>)DaoFactory.getInstance().toEntitySet(Cuenta.class, "VistaClientesDto", "cuentas", params);      
      UIBackingUtilities.resetDataTable("tablaSegmentos");		
      this.attrs.put("contrato", "TODOS");
      this.contratos= new ArrayList<>();
      this.contratos.add(new SelectItem("TODOS", "TODOS"));
      if(this.pagosSegmento== null) 
        this.pagosSegmento= new ArrayList<>();
      else {
        StringBuilder sb= new StringBuilder();
        for (Cuenta item: this.pagosSegmento) {
          item.setTotal(item.getSaldo());
          if(sb.indexOf(item.getContrato())< 0) {
            this.contratos.add(new SelectItem(item.getContrato(), item.getContrato())); 
            sb.append(item.getContrato()).append(Constantes.SEPARADOR);
          } // if  
        } // for
      } // if
		} // try 
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch 		
    finally {
      Methods.clean(params);
    } // finally		
	} 
  
	public String doRegresar() {	  
    JsfBase.setFlashAttribute("idCliente", this.attrs.get("idCliente"));
		return "saldos".concat(Constantes.REDIRECIONAR);
	} // doRegresar
	
	public void doRegistrarPago() {
		Transaccion transaccion      = null;
		TcManticClientesPagosDto pago= null;
		boolean tipoPago             = false;
		boolean saldar               = false;
		try {
			if(this.validaPago()) {
				saldar= Long.valueOf(this.attrs.get("saldar").toString()).equals(1L);
				pago= new TcManticClientesPagosDto();
				pago.setIdClienteDeuda(((Entity)this.attrs.get("seleccionado")).getKey());
				pago.setIdUsuario(JsfBase.getIdUsuario());
				pago.setObservaciones((String)this.attrs.get("observaciones"));
				pago.setPago((Double)this.attrs.get("pago"));
				pago.setFechaPago((LocalDate)this.attrs.get("fechaPago"));
				pago.setIdTipoMedioPago(Long.valueOf(this.attrs.get("tipoPago").toString()));
				tipoPago= pago.getIdTipoMedioPago().equals(ETipoMediosPago.EFECTIVO.getIdTipoMedioPago());
				transaccion= new Transaccion(pago, Long.valueOf(this.attrs.get("idCliente").toString()), Long.valueOf(this.attrs.get("idEmpresa").toString()), tipoPago? -1L: Long.valueOf(this.attrs.get("banco").toString()), tipoPago? "" : this.attrs.get("referencia").toString(), saldar);
				if(transaccion.ejecutar(EAccion.AGREGAR)) {
					JsfBase.addMessage("Registrar pago", "Se registro el pago de forma correcta", ETipoMensaje.INFORMACION);
					this.loadClienteDeuda();					
				} // if
				else
					JsfBase.addMessage("Registrar pago", "Ocurrió un error al registrar el pago", ETipoMensaje.ERROR);
			} // if
			else
				JsfBase.addMessage("Registrar pago", "El pago debe ser menor o igual al saldo restante", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch				
	} // doRegistrarPago
	
	public void onRowToggle(ToggleEvent event) {
		try {
			this.attrs.put("registroSeleccionado", (Entity) event.getData());
			if (!event.getVisibility().equals(Visibility.HIDDEN)) 
				this.loadHistorialPagos();			
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // onRowToggle
	
	private void loadHistorialPagos() throws Exception{
		List<Columna> columns     = null;
	  Map<String, Object> params= null;	
    try {  	  
			params= new HashMap<>();
			params.put("idClienteDeuda", ((Entity)this.attrs.get("registroSeleccionado")).getKey());			
      columns= new ArrayList<>();  
			columns.add(new Columna("fechaPago", EFormatoDinamicos.FECHA_CORTA));
			columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
			columns.add(new Columna("pago", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("saldo", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("persona", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("observaciones", EFormatoDinamicos.MAYUSCULAS));
			this.detallePagos = new FormatCustomLazy("VistaClientesDto", "pagosDeuda", params, columns);
      UIBackingUtilities.resetDataTable("tablaDetalle");		
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally			
	} // loadHistorialPagos
	
	public void doRegistrarPagoGeneral() {
		Transaccion transaccion      = null;
		TcManticClientesPagosDto pago= null;
		boolean tipoPago             = false;
		boolean saldar               = false;
		try {
			if(this.validaPagoGeneral()) {
				saldar= Long.valueOf(this.attrs.get("saldarGeneral").toString()).equals(1L);
				pago= new TcManticClientesPagosDto();
				pago.setIdUsuario(JsfBase.getIdUsuario());
				pago.setObservaciones((String)this.attrs.get("observacionesGeneral"));
				pago.setPago((Double)this.attrs.get("pagoGeneral"));
 				pago.setFechaPago((LocalDate)this.attrs.get("fechaPagoGeneral"));
				pago.setIdTipoMedioPago(Long.valueOf(this.attrs.get("tipoPagoGeneral").toString()));
				tipoPago= pago.getIdTipoMedioPago().equals(ETipoMediosPago.EFECTIVO.getIdTipoMedioPago());
				transaccion= new Transaccion(pago, Long.valueOf(this.attrs.get("idCliente").toString()), Long.valueOf(this.attrs.get("idEmpresa").toString()), tipoPago? -1L: Long.valueOf(this.attrs.get("bancoGeneral").toString()), tipoPago? "": (String)this.attrs.get("referenciaGeneral"), saldar);
				if(transaccion.ejecutar(EAccion.PROCESAR)) {
					JsfBase.addMessage("Registrar pago", "Se registro el pago de forma correcta");
					this.loadClienteDeuda();					
				} // if
				else
					JsfBase.addMessage("Registrar pago", "Ocurrió un error al registrar el pago", ETipoMensaje.ERROR);
			} // if
			else
				JsfBase.addMessage("Registrar pago", "El pago debe ser menor o igual al saldo restante", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch				
	} // doRegistrarPago
	
	public void doRegistrarPagoSegmento() {
		Transaccion transaccion         = null;
		TcManticClientesPagosDto importe= null;
		boolean tipoPago                = false;
		boolean saldar                  = false;
    List<Entity> cuentas            = new ArrayList<>();
		try {
			if(this.validaPagoSegmento()) { 
				saldar= Long.valueOf(this.attrs.get("saldarSegmento").toString()).equals(1L);
				importe= new TcManticClientesPagosDto();
				importe.setIdUsuario(JsfBase.getIdUsuario());
				importe.setObservaciones(this.pago.getObservaciones());
				importe.setPago(this.pago.getImporte());
 				importe.setFechaPago(this.pago.getFecha());
				importe.setIdTipoMedioPago(Long.valueOf(this.attrs.get("tipoPagoSegmento").toString()));
				tipoPago= importe.getIdTipoMedioPago().equals(ETipoMediosPago.EFECTIVO.getIdTipoMedioPago());
        for (Cuenta item: this.pagosSegmento) {
          cuentas.add(item.toEntity());
        } // for
				transaccion= new Transaccion(importe, Long.valueOf(this.attrs.get("idCliente").toString()), Long.valueOf(this.attrs.get("idEmpresa").toString()), tipoPago? -1L: Long.valueOf(this.attrs.get("bancoSegmento").toString()), tipoPago? "": (String)this.attrs.get("referenciaSegmento"), cuentas, saldar);
				if(transaccion.ejecutar(EAccion.COMPLEMENTAR)) {
					JsfBase.addMessage("Registrar pago", "Se registró el pago de forma correcta");
					this.loadClienteDeuda();					
				} // if
				else
					JsfBase.addMessage("Registrar pago", "Ocurrió un error al registrar el pago", ETipoMensaje.ERROR);
			} // if
			else
				JsfBase.addMessage("Registrar pago", "El pago debe ser menor o igual al saldo restante", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
    finally {
      Methods.clean(cuentas);
    } // finally
	} // doRegistrarPagoSegmento
	
	private boolean validaPago() {
		boolean regresar= false;
		Double importe  = 0D;
		Double saldo    = 0D;
		Entity deuda    = null;
		try {
			importe= (Double)this.attrs.get("pago");
			deuda  = (Entity)this.attrs.get("seleccionado");
			if(importe> 0D && !deuda.toLong("idClienteDeudaEstatus").equals(EEstatusClientes.FINALIZADA.getIdEstatus())) {
				saldo   = Numero.toRedondearSat(deuda.toDouble("saldo"));
				regresar= importe<= saldo;
			} // if
		} // try
		catch (Exception e) {		
			throw e;
		} // catch
		return regresar;
	} // validaPago
  
	private boolean validaPagoGeneral() {
		boolean regresar= false;
		Double importe  = 0D;
		Double saldo    = 0D;
		Entity deuda    = null;
		try {
			importe= (Double)this.attrs.get("pagoGeneral");
			if(importe> 0D) {
				deuda= (Entity) this.attrs.get("deuda");
				saldo= Numero.toRedondearSat(deuda.toDouble("saldo"));
				regresar= importe<= saldo;
			} // if
		} // try
		catch (Exception e) {		
			throw e;
		} // catch
		return regresar;
	} // validaPagoGeneral
	
	private boolean validaPagoSegmento() {
		boolean regresar= false;
		Double importe  = 0D;
		try {
			importe= Numero.toRedondearSat((Double)this.attrs.get("pagoSegmento"));
  		regresar= importe> 0 && this.pago.getImporte()> 0 && this.pago.getImporte()<= importe;
		} // try
		catch (Exception e) {		
			throw e;
		} // catch
		return regresar;
	} // validaPagoSegmento
	
	private void loadTiposPagos() {
		List<UISelectEntity> tiposPagos= null;
		Map<String, Object>params      = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_cobro_caja=1");
			tiposPagos= UIEntity.build("TcManticTiposMediosPagosDto", "row", params);
			this.attrs.put("tiposPagos", tiposPagos);
			this.attrs.put("tipoPago", UIBackingUtilities.toFirstKeySelectEntity(tiposPagos));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // loadTiposPagos
	
	public void doValidaTipoPago() {
		Long tipoPago= -1L;
		try {
			tipoPago= Long.valueOf(this.attrs.get("tipoPago").toString());
			this.attrs.put("mostrarBanco", !ETipoMediosPago.EFECTIVO.getIdTipoMedioPago().equals(tipoPago));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doValidaTipoPago
	
	public void doValidaTipoPagoGeneral() {
		Long tipoPago= -1L;
		try {
			tipoPago= Long.valueOf(this.attrs.get("tipoPagoGeneral").toString());
			this.attrs.put("mostrarBancoGeneral", !ETipoMediosPago.EFECTIVO.getIdTipoMedioPago().equals(tipoPago));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doValidaTipoPagoGeneral
	
	public void doValidaTipoPagoSegmento() {
		Long tipoPago= -1L;
		try {
			tipoPago= Long.valueOf(this.attrs.get("tipoPagoSegmento").toString());
			this.attrs.put("mostrarBancoSegmento", !ETipoMediosPago.EFECTIVO.getIdTipoMedioPago().equals(tipoPago));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doValidaTipoPagoSegmento
	
	public void doActualizaPago() {
		Entity seleccionado= null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");
			this.attrs.put("pago", seleccionado.toDouble("saldo"));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doActualizaPago
  
	public void doFileUpload(FileUploadEvent event) throws Exception {
    try {
      Entity cliente= (Entity)this.attrs.get("deuda");
      if(cliente.toLong("idDesarrollo")!= null && cliente.toLong("idDesarrollo")> 0L) {
        String nameFile= Archivo.toFormatName(cliente.toString("rfc"), event.getFile().getFileName().toUpperCase().substring(event.getFile().getFileName().lastIndexOf(".")));
        this.doFileUpload(event, LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(), Configuracion.getInstance().getPropiedadSistemaServidor("cobros"), cliente.toString("rfc"), false, 1D, nameFile);
        if(event.getFile().getFileName().toUpperCase().endsWith(EFormatos.XML.name())) {
          if(Objects.equals("E", this.getFactura().getTipoDeComprobante())) {
            this.attrs.put("total", Numero.getDouble(this.getFactura().getTotal(), 0D));
            this.pago.setTotal(Numero.getDouble(this.getFactura().getTotal(), 0D));
            this.pago.setIdTipoMedioPago(this.toClaveCatalogo(EClaveCatalogo.MEDIOS_PAGO, this.getFactura().getFormaPago())); // EFECTIVO, CHEQUE, TRANSFERENCIA getFormaPago(30) 
            this.pago.setTipoMedioPago(this.getFactura().getFormaPago()); // EFECTIVO, CHEQUE, TRANSFERENCIA getFormaPago(30) 
            this.pago.setIdTipoPago(this.toClaveCatalogo(EClaveCatalogo.TIPOS_PAGOS, this.getFactura().getMetodoPago())); // PUE, PPD, PUE getMetodoPago(PUE)
            this.pago.setTipoPago(this.getFactura().getMetodoPago()); // PUE, PPD, PUE getMetodoPago(PUE)
            this.pago.setIdUsoCfdi(this.toClaveCatalogo(EClaveCatalogo.USOS_CFDI, this.getFactura().getReceptor().getUsoCfdi())); // GO3 getReceptor.getUsoCfi(G02)
            this.pago.setSerie(this.getFactura().getSerie()); // A o B
            this.pago.setIdTipoComprobante(this.toClaveCatalogo(EClaveCatalogo.COMPROBANTES, this.getFactura().getTipoDeComprobante())); // I o E
            this.pago.setFolio(this.getFactura().getFolio());
            this.pago.setTimbrado(Fecha.toLocalDateTime(this.getFactura().getTimbreFiscalDigital().getFechaTimbrado()));
            this.pago.setFecha(Fecha.toLocalDate(this.getFactura().getFecha()));
            this.doCheckFolio();
            if(this.attrs.get("faltantes")!= null) {
              List<Articulo> faltantes= (List<Articulo>)this.attrs.get("faltantes");
              StringBuilder sb= new StringBuilder();
              for (Articulo item : faltantes) {
                sb.append(item.getNombre()).append("|");
              } // for
              this.pago.setObservaciones(sb.substring(0, sb.length()- 1));
            } // if
          } // if  
          else {
            this.doDeleteXml();
            JsfBase.addMessage("Nota de crédito:", "Solo se pueden importar notas de credito !", ETipoMensaje.ERROR); 
          } // else  
        } // if
      } // if
      else 
        JsfBase.addMessage("Se tiene que seleccionar un desarrollo primero", ETipoMensaje.ALERTA);      			
    } // try
    catch(Exception e ) {
      throw e;
    } // catch
	} // doFileUpload	
  
	public Boolean getDiferente() {
    Entity cliente= (Entity)this.attrs.get("deuda");
	  return this.getEmisor()!= null && cliente!= null && !this.getReceptor().getRfc().equals(cliente.toString("rfc"));
	}
 
  public void doDeleteXml() {
    if(this.resetXml(Configuracion.getInstance().getPropiedadSistemaServidor("cobros"))) {
    } // if  
  }
  
  public void doDeletePdf() {
    this.resetPdf(Configuracion.getInstance().getPropiedadSistemaServidor("cobros"));
  }
 
	public void doCheckFolio() {
		Map<String, Object> params=null;
		try {
			this.attrs.put("folio", "");
			params=new HashMap<>();
			params.put("folio", this.getFactura().getFolio());
			params.put("idCliente", this.attrs.get("idCliente"));
			int month= Calendar.getInstance().get(Calendar.MONTH);
			if(month<= 5) {
				params.put("inicio", Calendar.getInstance().get(Calendar.YEAR)+ "0101");
				params.put("termino", Calendar.getInstance().get(Calendar.YEAR)+ "0630");
			} // if
			else {
				params.put("inicio", Calendar.getInstance().get(Calendar.YEAR)+ "0701");
				params.put("termino", Calendar.getInstance().get(Calendar.YEAR)+ "1231");
			} // else
			Entity entity= (Entity)DaoFactory.getInstance().toEntity("VistaVentasDto", "credito", params);
			if(entity!= null && entity.size()> 0) {
				UIBackingUtilities.execute("janal.show([{summary: 'Error:', detail: 'El folio ["+ this.getFactura().getFolio()+ "] de la nota de crédito se registró en el pago ["+ entity.toString("consecutivo")+ "], el día "+ Global.format(EFormatoDinamicos.FECHA_HORA, entity.toTimestamp("registro"))+ " hrs.'}]);");
   			this.attrs.put("folio", "El folio ["+ this.getFactura().getFolio()+ "] de la nota de crédito se registró en el pago ["+ entity.toString("consecutivo")+ "], el día "+ Global.format(EFormatoDinamicos.FECHA_HORA, entity.toTimestamp("registro"))+ " hrs.");
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
 
	public void doLoadBeanCuentas() {
	  Map<String, Object> params= null;	
		try {
			params= new HashMap<>();
			params.put("idCliente", this.attrs.get("idCliente"));						
			params.put("sortOrder", "order by	tc_mantic_clientes_deudas.registro desc");			
			params.put(Constantes.SQL_CONDICION, " tc_mantic_clientes_deudas.saldo> 0 and tc_mantic_clientes_deudas.id_cliente_deuda_estatus in(1, 2)");			
			this.pagosCuenta= (List<Cuenta>)DaoFactory.getInstance().toEntitySet(Cuenta.class, "VistaClientesDto", "cuentas", params);      
      UIBackingUtilities.resetDataTable("tablaCuentas");		
      this.attrs.put("tipoPagoCuenta", new UISelectEntity(this.pago.getIdTipoMedioPago()));
      if(this.pagosCuenta== null)
        this.pagosCuenta= new ArrayList<>();        
      else
        for (Cuenta item: this.pagosCuenta) {
          item.setTotal(item.getSaldo());
        } // for
		} // try 
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch 		
    finally {
      Methods.clean(params);
    } // finally
	} 
 
	public void doRegistrarPagoCuenta() {
		Transaccion transaccion         = null;
		TcManticClientesPagosDto importe= null;
		boolean tipoPago                = false;
		boolean saldar                  = false;
    List<Entity> cuentas            = new ArrayList<>();
		try {
			if(this.pago.getImporte()> 0D && Objects.equals(this.pago.getTotal(), this.pago.getImporte())) { 
				saldar= Long.valueOf(this.attrs.get("saldarSegmento").toString()).equals(1L);
        if(!Cadena.isVacio((String)this.attrs.get("observaciones"))) {
          if(this.getXml()!= null && Cadena.isVacio(this.getXml().getObservaciones()))
            this.getXml().setObservaciones((String)this.attrs.get("observaciones")); 
          if(this.getPdf()!= null && Cadena.isVacio(this.getPdf().getObservaciones()))
            this.getPdf().setObservaciones((String)this.attrs.get("observaciones"));
        } // if
				importe= new TcManticClientesPagosDto();
				importe.setIdUsuario(JsfBase.getIdUsuario());
				importe.setObservaciones(this.pago.getObservaciones());
				importe.setFolio(this.pago.getFolio());
				importe.setPago(this.pago.getImporte());
 				importe.setFechaPago(this.pago.getFecha());
				importe.setIdTipoMedioPago(Long.valueOf(this.attrs.get("tipoPagoCuenta").toString()));
				tipoPago= importe.getIdTipoMedioPago().equals(ETipoMediosPago.EFECTIVO.getIdTipoMedioPago());
				importe.setIdBanco(tipoPago? null: Long.valueOf(this.attrs.get("bancoCuenta").toString()));
				importe.setReferencia(tipoPago? null: this.pago.getReferencia());
        for (Cuenta item: this.pagosCuenta) {
          cuentas.add(item.toEntity());
        } // for
				transaccion= new Transaccion(importe, Long.valueOf(this.attrs.get("idCliente").toString()), Long.valueOf(this.attrs.get("idEmpresa").toString()), cuentas, this.getXml(), this.getPdf(), saldar);
				if(transaccion.ejecutar(EAccion.COMPLETO)) {
					JsfBase.addMessage("Registrar pago", "Se registró el pago de forma correcta");
					this.loadClienteDeuda();					
				} // if
				else
					JsfBase.addMessage("Registrar pago", "Ocurrió un error al registrar el pago", ETipoMensaje.ERROR);
			} // if
			else
				JsfBase.addMessage("Registrar pago", "El pago debe ser menor o igual al saldo restante", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
    finally {
      Methods.clean(cuentas);
    } // finally
	} // doRegistrarPagoCuenta

  public void doRowUpdateCuenta(Cuenta row)  {
    try { 
      this.pago.setImporte(0D);
      if(this.pagosCuenta!= null && this.pagosCuenta.size()> 0) 
        for (Cuenta item: this.pagosCuenta) {
          if(item.getActivo())
            this.pago.addImporte(item.getTotal());
        } // for
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

  public void doRowUpdateSegmento(Cuenta row)  {
    try { 
      this.pago.setImporte(0D);
      if(this.pagosSegmento!= null && this.pagosSegmento.size()> 0) 
        for (Cuenta item: this.pagosSegmento) {
          if(item.getActivo())
            this.pago.addImporte(item.getTotal());
        } // for
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

	public void doValidaTipoPagoCuenta() {
		Long tipoPago= -1L;
		try {
			tipoPago= Long.valueOf(this.attrs.get("tipoPagoCuenta").toString());
			this.attrs.put("mostrarBancoCuenta", !ETipoMediosPago.EFECTIVO.getIdTipoMedioPago().equals(tipoPago));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doValidaTipoPagoCuenta
 
	public String toColor(Cuenta row) {
    String contrato= (String)this.attrs.get("contrato");
		return Objects.equals(contrato, row.getContrato()) || Objects.equals(contrato, "TODOS")? "": "janal-display-none";
	} // toColor
  
}