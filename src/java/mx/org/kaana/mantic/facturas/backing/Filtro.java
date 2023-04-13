package mx.org.kaana.mantic.facturas.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.procesos.reportes.beans.Definicion;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.mantic.ventas.reglas.MotorBusqueda;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.comun.JuntarReporte;
import mx.org.kaana.mantic.facturas.reglas.Transaccion;
import mx.org.kaana.mantic.db.dto.TcManticFicticiasBitacoraDto;
import mx.org.kaana.mantic.enums.EEstatusFicticias;
import mx.org.kaana.mantic.enums.EEstatusVentas;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.enums.ETipoMovimiento;
import mx.org.kaana.mantic.facturas.beans.Correo;
import mx.org.kaana.mantic.facturas.beans.FacturaFicticia;
import mx.org.kaana.mantic.facturas.comun.FiltroFactura;
import mx.org.kaana.mantic.facturas.enums.ETiposComprobantes;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;

@Named(value= "manticFacturasFiltro")
@ViewScoped
public class Filtro extends FiltroFactura implements Serializable {

  private static final long serialVersionUID = 8793667741599428332L;
	private static final Log LOG= LogFactory.getLog(Filtro.class);			
  private List<Entity> registros= null;

  public List<Entity> getRegistros() {
    return registros;
  }
	
  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("idEmpresa", new UISelectEntity(JsfBase.getAutentifica().getEmpresa().getIdEmpresa()));
      this.attrs.put("idVenta", JsfBase.getFlashAttribute("idVenta"));
			this.toLoadCatalog();
      if(this.attrs.get("idVenta")!= null) 
			  this.doLoad();			
      this.attrs.remove("idVenta"); 
      this.attrs.put("facturama", -1L);
			super.initBase();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
 
  @Override
  public void doLoad() {
    List<Columna> columns     = null;
		Map<String, Object> params= this.toPrepare();
    try {
      columns = new ArrayList<>();
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));      
      columns.add(new Columna("timbrado", EFormatoDinamicos.FECHA_HORA_CORTA));   
      columns.add(new Columna("cancelada", EFormatoDinamicos.FECHA_CORTA));   
      params.put("sortOrder", "order by tc_mantic_ventas.registro desc");
      this.lazyModel = new FormatCustomLazy("VistaVentasDto", params, columns);
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

  public String doAccion(String accion) {
    return this.toAccion(accion, "/Paginas/Mantic/Facturas/accion");
  }
  
  public String doModificar() {
    JsfBase.setFlashAttribute("accion", EAccion.MODIFICAR);		
    JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Facturas/filtro");		
    JsfBase.setFlashAttribute("idVenta", ((Entity)this.attrs.get("seleccionado")).getKey());
    return "/Paginas/Keet/Ingresos/modificar".concat(Constantes.REDIRECIONAR);
  }
  
  public String doFactura(String accion) {
    return this.toAccion(accion, "/Paginas/Keet/Ingresos/accion");
  }
  
  public String doPago(String accion) {
    return this.toAccion(accion, "/Paginas/Mantic/Facturas/complemento");
  }
  
  public String toAccion(String accion, String pagina) {
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
      if(!Cadena.isVacio(this.attrs.get("seleccionado")) && Objects.equals(1L, ((Entity)this.attrs.get("seleccionado")).toLong("idManual")))
        pagina = "/Paginas/Keet/Ingresos/accion";
      if(!Cadena.isVacio(this.attrs.get("seleccionado")) && Objects.equals(3L, ((Entity)this.attrs.get("seleccionado")).toLong("idTipoComprobante")))
        pagina= "/Paginas/Mantic/Facturas/complemento";
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Facturas/filtro");		
			JsfBase.setFlashAttribute("idVenta", eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR) ? ((Entity)this.attrs.get("seleccionado")).getKey() : -1L);
			if(eaccion.equals(EAccion.AGREGAR)) {
				JsfBase.setFlashAttribute("observaciones", null);		
				JsfBase.setFlashAttribute("idCliente", null);		
			} // if
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return pagina.concat(Constantes.REDIRECIONAR);
  } // doAccion  
	
  public void doEliminar() {
		Transaccion transaccion = null;
		Entity seleccionado     = null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
			transaccion= new Transaccion(new FacturaFicticia(seleccionado.getKey()), this.attrs.get("justificacionEliminar").toString());
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar", "La factura se ha eliminado correctamente", ETipoMensaje.ERROR);
			else
				JsfBase.addMessage("Eliminar", "Ocurrió un error al eliminar la factura", ETipoMensaje.ERROR);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
  } // doEliminar	
	
	protected void toLoadCatalog() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));			
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));
      columns.add(new Columna("limiteCredito", EFormatoDinamicos.MONEDA_SAT_DECIMALES));      
			this.doLoadDocumentoEstatus();
			this.doLoadDesarrollos();
			this.doLoadContratos();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // toLoadCatalog
	
	public void doLoadDesarrollos() {
		List<Columna> columns     = null;
    Map<String, Object> params= null;
//		UISelectEntity empresa    = null;
    try {
			params= new HashMap<>();			
//			empresa= (UISelectEntity) this.attrs.get("idEmpresa");
//			if(empresa.getKey()>= 1L)
//        params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa=" + empresa.getKey());
//			else
  		params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa in (" + JsfBase.getAutentifica().getEmpresa().getSucursales() + ")");			
			params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
      columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("desarrollos", (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "nombres"));			
			this.attrs.put("idDesarrollo", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("desarrollos")));			
      this.doLoadContratos();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} // doLoadDesarrollos
	
	public void doLoadContratos() {
		List<Columna> columns    = null;
		Map<String, Object>params= new HashMap<>();
		UISelectEntity empresa   = null;
    List<UISelectEntity> clientes = null;
    List<UISelectEntity> contratos= null;
	  try {
			columns= new ArrayList<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			empresa= (UISelectEntity)this.attrs.get("idEmpresa");
			if(empresa!= null && empresa.getKey()> 0L) 
			  params.put("sucursales", empresa.getKey());
			else
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && !this.attrs.get("idDesarrollo").toString().equals("-1")) {
        params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			  contratos= UIEntity.build("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS);
        clientes = UIEntity.build("VistaIngresosDto", "clientes", params, columns, Constantes.SQL_TODOS_REGISTROS);
      } // if  
      else {
        contratos= UIEntity.seleccione("VistaContratosDto", "byEmpresa", params, "clave");
        clientes = UIEntity.seleccione("TcManticClientesDto", "sucursales", params, columns, "razonSocial");
      } // else   
      this.attrs.put("clientes", clientes);
      if(clientes!= null && !clientes.isEmpty())
        this.attrs.put("idCliente", clientes.get(0));
      this.attrs.put("contratos", contratos);
      if(contratos!= null && !contratos.isEmpty())
        this.attrs.put("idContrato", contratos.get(0));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} // doLoadContratos
  
	public List<UISelectEntity> doCompleteCliente(String query) {
		this.attrs.put("codigoCliente", query);
    this.doUpdateClientes();		
		return (List<UISelectEntity>)this.attrs.get("clientes");
	}	// doCompleteCliente
	
	public void doUpdateClientes() {
		List<Columna> columns     = null;
    Map<String, Object> params= null;
    try {
			params= new HashMap<>();
			columns= new ArrayList<>();
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));			
			params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
			String search= (String)this.attrs.get("codigoCliente"); 
			search= !Cadena.isVacio(search) ? search.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*") : "WXYZ";
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
	
	public void doAsignaCliente(SelectEvent event) {
		UISelectEntity seleccion     = null;
		List<UISelectEntity> clientes= null;
		try {
			clientes= (List<UISelectEntity>) this.attrs.get("clientes");
			seleccion= clientes.get(clientes.indexOf((UISelectEntity)event.getObject()));
			this.toFindCliente(seleccion);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doAsignaCliente
	
	private void toFindCliente(UISelectEntity seleccion) {
		List<UISelectEntity> clientesSeleccion= null;
		MotorBusqueda motorBusqueda           = null;
		try {
			clientesSeleccion= new ArrayList<>();
			clientesSeleccion.add(seleccion);
			motorBusqueda= new mx.org.kaana.mantic.ventas.reglas.MotorBusqueda(-1L);
			clientesSeleccion.add(0, new UISelectEntity(motorBusqueda.toClienteDefault()));
			this.attrs.put("clientesSeleccion", clientesSeleccion);
			this.attrs.put("clienteSeleccion", seleccion);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // toFindCliente
	
	public void doReporte(String nombre) throws Exception {
		this.doReporte(nombre, false);
	} // doReporte	
  
  public void doReporteFacturas(String nombre) throws Exception {
		Map<String, Object>params    = null;
		EReportes reporteSeleccion   = null;
    List<Definicion> definiciones= null;
		try{		
      params= this.toPrepare();	
      //es importante este orden para los grupos en el reporte	
      definiciones = new ArrayList<>();
      params.put("sortOrder", "order by tc_mantic_ventas.id_empresa, tc_mantic_clientes.id_cliente, tc_mantic_ventas.ejercicio, tc_mantic_ventas.orden");
      reporteSeleccion= EReportes.valueOf(nombre);
      this.reporte= JsfBase.toReporte();	
      definiciones.add(new Definicion((Map<String, Object>) ((HashMap) params).clone(), params, reporteSeleccion.getProceso(), reporteSeleccion.getIdXml(), reporteSeleccion.getJrxml()));
      this.reporte.toAsignarReportes(new JuntarReporte(definiciones, reporteSeleccion, "/Paginas/Mantic/Facturas/filtro", false, false));
      if(doVerificarReporte())
        this.reporte.doAceptar();
    } // try
    catch(Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);			
    } // catch	
  } // doReporte	
	
	public void doLoadDocumentoEstatus() {
		List<Columna> columns     = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			columns= new ArrayList<>();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
		  params.put("idTipoDocumento", "1");
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
//			if(this.attrs.get("idTipoDocumento")== null || (Long)this.attrs.get("idTipoDocumento")== -1L)
//			  params.put("idTipoDocumento", "1, 2");
//			else
//			  params.put("idTipoDocumento", this.attrs.get("idTipoDocumento"));
      this.attrs.put("estatusFiltro", (List<UISelectEntity>) UIEntity.seleccione("TcManticVentasEstatusDto", "rows", params, columns, "nombre"));
			this.attrs.put("idVentaEstatus", new UISelectEntity("-1"));
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
	
	public void doLoadEstatus() {
		Entity seleccionado          = null;
		Map<String, Object>params    = null;
		List<UISelectItem> allEstatus= null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			params= new HashMap<>();
			params.put("idTipoDocumento", seleccionado.toLong("idTipoDocumento"));
			params.put("estatusAsociados", seleccionado.toString("estatusAsociados"));
			allEstatus= UISelect.build("TcManticVentasEstatusDto", "estatus", params, "nombre", EFormatoDinamicos.MAYUSCULAS);			
			this.attrs.put("allEstatus", allEstatus);
			this.attrs.put("estatus", allEstatus.get(0).getValue().toString());
      this.doLoadMails();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadEstatus
	
	public void doActualizarEstatus() {
		Transaccion transaccion              = null;
		FacturaFicticia orden                = null;
		TcManticFicticiasBitacoraDto bitacora= null;
		Entity seleccionado                  = null;
		StringBuilder emails                 = null;
    List<Columna> columns                = null;
    try {
      this.registros= null;
			seleccionado= (Entity)this.attrs.get("seleccionado");
			orden   = (FacturaFicticia) DaoFactory.getInstance().toEntity(FacturaFicticia.class, "TcManticFicticiasDto", "detalle", seleccionado.toMap());
			bitacora= new TcManticFicticiasBitacoraDto(orden.getTicket(), (String)this.attrs.get("justificacion"), Long.valueOf(this.attrs.get("estatus").toString()), JsfBase.getIdUsuario(), seleccionado.getKey(), -1L, orden.getTotal());
			emails  = new StringBuilder("");
			if(this.getSelectedCorreos()!= null && !this.getSelectedCorreos().isEmpty()){
				for(Correo mail: this.getSelectedCorreos())
					if(!Cadena.isVacio(mail.getDescripcion()))
						emails.append(mail.getDescripcion()).append(", ");
			} // if
      if(Objects.equals(EEstatusFicticias.CANCELADA.getIdEstatusFicticia(), bitacora.getIdFicticiaEstatus())) {
        if(Objects.equals(orden.getIdTipoComprobante(), ETiposComprobantes.COMPLEMENTO_PAGO.getIdTipoComprobante()))
          this.registros= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaIngresosDto", "encontrado", orden.toMap());
        else 
          this.registros= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaIngresosDto", "existe", orden.toMap());
        if(this.registros!= null && !this.registros.isEmpty()) {
          columns = new ArrayList<>();
          columns.add(new Columna("timbrado", EFormatoDinamicos.FECHA_HORA_CORTA));
          columns.add(new Columna("pagado", EFormatoDinamicos.MILES_CON_DECIMALES));
          UIBackingUtilities.toFormatEntitySet(registros, columns);
          UIBackingUtilities.execute("PF('dlgConsulta').show();");
        } // if  
      } // if 
      if(this.registros== null || this.registros.isEmpty()) {
        transaccion= new Transaccion(orden, bitacora, emails.toString(), (String)this.attrs.get("justificacion"));
        if(transaccion.ejecutar(EAccion.JUSTIFICAR)) {
          if(bitacora.getIdFicticiaEstatus().equals(EEstatusFicticias.TIMBRADA.getIdEstatusFicticia()) || bitacora.getIdFicticiaEstatus().equals(EEstatusVentas.ELIMINADA.getIdEstatusVenta()))
            this.doSendMail();				
          JsfBase.addMessage("Cambio estatus", "Se realizó el cambio de estatus de forma correcta", ETipoMensaje.INFORMACION);
        } // if
        else
          JsfBase.addMessage("Cambio estatus", "Ocurrió un error al realizar el cambio de estatus", ETipoMensaje.ERROR);
      } // if  
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally {
			this.attrs.put("justificacion", "");
			this.setSelectedCorreos(new ArrayList<>());
      Methods.clean(columns);
		} // finally
	}	// doActualizaEstatus
	
  public String doSincronizar() {
		JsfBase.setFlashAttribute("accion", EAccion.GENERAR);		
		JsfBase.setFlashAttribute("retorno", "filtro");		
		return "sincronizar".concat(Constantes.REDIRECIONAR);
  } // doSincronizar  

	public String doImportar() {
		JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Facturas/filtro");		
		JsfBase.setFlashAttribute("idVenta", ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("idFactura", ((Entity)this.attrs.get("seleccionado")).toLong("idFactura"));
		return "importar".concat(Constantes.REDIRECIONAR);
	} // doImportar
	
	public void doClonar() {
		Transaccion transaccion= null;
		Entity seleccionado    = null;
		FacturaFicticia dto    = null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
			dto= (FacturaFicticia)DaoFactory.getInstance().findById(FacturaFicticia.class, seleccionado.getKey());
			if(dto!= null) {
				FacturaFicticia copia= SerializationUtils.clone(dto);
				transaccion= new Transaccion(copia);
				if(transaccion.ejecutar(EAccion.COPIAR)) {
					UIBackingUtilities.execute("janal.back('clon\\u00F3 la factura ', '"+ copia.getConsecutivo()+ "');");
					JsfBase.addMessage("Clonar", "La factura se ha clonó correctamente", ETipoMensaje.ERROR);
				} // if	
				else
					JsfBase.addMessage("Clonar", "Ocurrió un error al clonar la factura", ETipoMensaje.ERROR);								
			} // if	
			else
				JsfBase.addMessage("Clonar", "Ocurrió un error al clonar la factura, por favor intentelo de nuevo", ETipoMensaje.ERROR);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doClonar

	public String doMovimientos() {
		JsfBase.setFlashAttribute("tipo", ETipoMovimiento.FACTURAS_FICTICIAS);
		JsfBase.setFlashAttribute(ETipoMovimiento.FACTURAS_FICTICIAS.getIdKey(), ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("regreso", "/Paginas/Mantic/Facturas/filtro");
		return "/Paginas/Mantic/Compras/Ordenes/movimientos".concat(Constantes.REDIRECIONAR);
	} // doMovimientos
	
	public void doMontoUpdate() {
	  if(this.attrs.get("montoInicio")!= null && this.attrs.get("montoTermino")== null)
			this.attrs.put("montoTermino", this.attrs.get("montoInicio"));
	  if(this.attrs.get("montoTermino")!= null && this.attrs.get("montoInicio")== null)
			this.attrs.put("montoInicio", this.attrs.get("montoTermino"));
	} // doMontoUpdate
	
	public void doUpdateCodigos() {
		List<Columna> columns     = null;
    Map<String, Object> params= null;
    try {
			params= new HashMap<>();
			columns= new ArrayList<>();
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			String search= (String)this.attrs.get("codigoCodigo"); 
			search= !Cadena.isVacio(search) ? search.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim(): "WXYZ";
			params.put("idAlmacen", JsfBase.getAutentifica().getEmpresa().getIdAlmacen());
			if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
				params.put("sucursales", this.attrs.get("idEmpresa"));
			else
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", -1L);			
  		params.put("codigo", search);			
      params.put("idArticuloTipo", "1");	      
      this.attrs.put("codigos", (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", "porCodigo", params, columns, 20L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}	// doUpdateCodigos

	public List<UISelectEntity> doCompleteCodigo(String query) {
		this.attrs.put("codigoCodigo", query);
    this.doUpdateCodigos();		
		return (List<UISelectEntity>)this.attrs.get("codigos");
	}	// doCompleteCodigo

	public void doAsignaCodigo(SelectEvent event) {
		UISelectEntity seleccion    = null;
		List<UISelectEntity> codigos= null;
		try {
			codigos= (List<UISelectEntity>) this.attrs.get("codigos");
			seleccion= codigos.get(codigos.indexOf((UISelectEntity)event.getObject()));
			this.attrs.put("codigoSeleccion", seleccion);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doAsignaCodigo
	
	public void doUpdateArticulos() {
		List<Columna> columns     = null;
    Map<String, Object> params= null;
    try {
			params= new HashMap<>();
			columns= new ArrayList<>();
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			String search= (String)this.attrs.get("codigoArticulo"); 
			search= !Cadena.isVacio(search) ? search.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim(): "WXYZ";
			params.put("idAlmacen", JsfBase.getAutentifica().getEmpresa().getIdAlmacen());
			if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
				params.put("sucursales", this.attrs.get("idEmpresa"));
			else
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", -1L);			
  		params.put("codigo", search.replaceAll("[ ]", "*.*"));			
      params.put("idArticuloTipo", "1");	      
      this.attrs.put("articulos", (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", "porNombre", params, columns, 20L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}	// doUpdateArticulo

	public List<UISelectEntity> doCompleteArticulo(String query) {
		this.attrs.put("codigoArticulo", query);
    this.doUpdateArticulos();		
		return (List<UISelectEntity>)this.attrs.get("articulos");
	}	// doCompleteCodigo

	public void doAsignaArticulo(SelectEvent event) {
		UISelectEntity seleccion      = null;
		List<UISelectEntity> articulos= null;
		try {
			articulos= (List<UISelectEntity>) this.attrs.get("articulos");
			seleccion= articulos.get(articulos.indexOf((UISelectEntity)event.getObject()));
			this.attrs.put("articulosSeleccion", seleccion);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doAsignaArticulo

	public String doFacturaColor(Entity row) {
		String regresar= "";
//		if(row.toLong("idFacturaEstatus").equals(EEstatusFacturas.AUTOMATICO.getIdEstatusFactura()))
//			regresar= "janal-tr-nuevo";
//		else 
     if(row.toLong("idTipoComprobante").equals(ETiposComprobantes.COMPLEMENTO_PAGO.getIdTipoComprobante()))
			  regresar= "janal-tr-diferencias";
		return regresar;
	}  // doFacturaColor

	public String doComplemento(String accion) {
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Facturas/filtro");		
			JsfBase.setFlashAttribute("idVenta", eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR) ? ((Entity)this.attrs.get("seleccionado")).getKey() : -1L);
			if(eaccion.equals(EAccion.AGREGAR)) {
				JsfBase.setFlashAttribute("observaciones", null);		
				JsfBase.setFlashAttribute("idCliente", null);		
			} // if
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return "/Paginas/Mantic/Ventas/Facturas/accion".concat(Constantes.REDIRECIONAR);
  } // doComplemento 

	public void doMoveSection() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
		List<UISelectEntity> documento= null;
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cantidad", EFormatoDinamicos.NUMERO_CON_DECIMALES));
      columns.add(new Columna("impuestos", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("precio", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("fecha", EFormatoDinamicos.FECHA_HORA));
			params.put("idVenta", ((Entity)this.attrs.get("seleccionado")).getKey());
			documento= (List<UISelectEntity>) UIEntity.build("VistaKardexDto", "venta", params, columns, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("documentos", documento);
			if(documento!= null && !documento.isEmpty()) {
				documento.get(0).put("articulos", new Value("articulos", documento.size()));
        this.attrs.put("documento", documento.get(0));
			} // if	
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
		
	public void doCancelarFacturacion() {
		Transaccion transaccion= null;
		Entity seleccionado    = null;		
		FacturaFicticia orden  = null;
    List<Columna> columns  = null;
		try {
      this.registros= null;
			seleccionado  = (Entity)this.attrs.get("seleccionado");						
			orden = new FacturaFicticia();
			orden.setKey(seleccionado.getKey());
			orden.setIdFactura(seleccionado.toLong("idFactura"));
			orden.setIdCliente(seleccionado.toLong("idCliente"));
      orden.setTotal(seleccionado.toDouble("total"));
      orden.setTicket(seleccionado.toString("ticket"));
      orden.setIdTipoMedioPago(seleccionado.toLong("idTipoMedioPago"));
      orden.setFechaPago(seleccionado.toTimestamp("fechaPago"));
      orden.setIdBanco(seleccionado.toLong("idBanco"));
      orden.setReferencia(seleccionado.toString("referencia"));
      orden.setIdTipoComprobante(seleccionado.toLong("idTipoComprobante"));
      if(Objects.equals(orden.getIdTipoComprobante(), ETiposComprobantes.COMPLEMENTO_PAGO.getIdTipoComprobante()))
        this.registros= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaIngresosDto", "encontrado", orden.toMap());
      else 
        this.registros= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaIngresosDto", "existe", orden.toMap());
      if(this.registros!= null && !this.registros.isEmpty()) {
        columns = new ArrayList<>();
        columns.add(new Columna("timbrado", EFormatoDinamicos.FECHA_HORA_CORTA));
        columns.add(new Columna("pagado", EFormatoDinamicos.MILES_CON_DECIMALES));
        UIBackingUtilities.toFormatEntitySet(registros, columns);
        UIBackingUtilities.execute("PF('dlgConsulta').show();");
      } // if  
      if(this.registros== null || this.registros.isEmpty()) {
        transaccion= new Transaccion(orden, (String)this.attrs.get("justificacionCancelar"));
        if(transaccion.ejecutar(EAccion.DEPURAR))
          JsfBase.addMessage("Cambio estatus", "Se realizo el cambio de estatus de forma correcta", ETipoMensaje.INFORMACION);
        else
          JsfBase.addMessage("Cambio estatus", "Ocurrio un error al realizar el cambio de estatus", ETipoMensaje.ERROR);
      } // if  
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally {
			this.attrs.put("justificacionCancelar", "");
			this.setSelectedCorreos(new ArrayList<>());
      Methods.clean(columns);
		} // finally
	}	// doActualizaEstatus
	
	public void doAutomatico(){
		Transaccion transaccion= null;
		Entity seleccionado    = null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");
			transaccion= new Transaccion(seleccionado.getKey());
			if(transaccion.ejecutar(EAccion.PROCESAR))
				JsfBase.addMessage("Automatico", "Se actualizó el estatus de forma correcta", ETipoMensaje.INFORMACION);			
			else
				JsfBase.addMessage("Automatico", "Ocurrió un error al realizar el cambio de estatus", ETipoMensaje.ERROR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doAutomatico
  
	public String doRecordPagos() {
    try {
      Entity seleccionado= (Entity)this.attrs.get("seleccionado");						
      JsfBase.setFlashAttribute("idVenta", seleccionado.getKey());
      JsfBase.setFlashAttribute("idCliente", seleccionado.toLong("idCliente"));
      JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Facturas/filtro");
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		return "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos".concat(Constantes.REDIRECIONAR);
	}
 
	@Override
	protected void finalize() throws Throwable {
    super.finalize();		
	}	// finalize
  
}