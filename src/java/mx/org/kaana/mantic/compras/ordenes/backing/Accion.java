package mx.org.kaana.mantic.compras.ordenes.backing;

import com.google.common.base.Objects;
import java.io.Serializable;
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
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Cifrar;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.OrdenCompra;
import mx.org.kaana.mantic.compras.ordenes.reglas.Transaccion;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.compras.ordenes.enums.EOrdenes;
import mx.org.kaana.mantic.compras.ordenes.reglas.AdminOrdenes;
import mx.org.kaana.mantic.comun.IBaseArticulos;
import mx.org.kaana.mantic.comun.IBaseStorage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.TabChangeEvent;


@Named(value= "manticComprasOrdendesAccion")
@ViewScoped
public class Accion extends IBaseArticulos implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Accion.class);
  private static final long serialVersionUID= 327393488565639367L;
	private EAccion accion;
	private EOrdenes tipoOrden;

	public String getTitulo() {
		return "(".concat(tipoOrden.name()).concat(")");
	}

	public EOrdenes getTipoOrden() {
		return tipoOrden;
	}
	
	public String getAgregar() {
		return this.accion.equals(EAccion.AGREGAR)? "none": "";
	}
	
	@PostConstruct
  @Override
  protected void init() {		
    try {
      this.accion   = JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
			this.tipoOrden= JsfBase.getParametro("zOyOxDwIvGuCt")== null? EOrdenes.NORMAL: EOrdenes.valueOf(Cifrar.descifrar(JsfBase.getParametro("zOyOxDwIvGuCt")));
      this.attrs.put("idOrdenCompra", JsfBase.getFlashAttribute("idOrdenCompra")== null? -1L: JsfBase.getFlashAttribute("idOrdenCompra"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("isPesos", false);
			this.attrs.put("buscaPorCodigo", false);
			this.attrs.put("seleccionado", null);
			this.attrs.put("familiasSeleccion", new String[]{});
			this.attrs.put("lotesSeleccion", new String[]{});
			doLoad();
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
          this.setAdminOrden(new AdminOrdenes(new OrdenCompra(-1L)));
    			this.attrs.put("sinIva", false);
          break;
        case MODIFICAR:			
        case CONSULTAR:											
          this.setAdminOrden(new AdminOrdenes((OrdenCompra)DaoFactory.getInstance().toEntity(OrdenCompra.class, "TcManticOrdenesComprasDto", "detalle", this.attrs)));
    			this.attrs.put("sinIva", this.getAdminOrden().getIdSinIva().equals(1L));
          break;
      } // switch
			this.attrs.put("paginator", this.getAdminOrden().getArticulos().size()> Constantes.REGISTROS_LOTE_TOPE);
			//this.doResetDataTable();
			this.toLoadCatalog();
			this.attrs.put("before", this.getAdminOrden().getIdAlmacen());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad  

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
  				((OrdenCompra)this.getAdminOrden().getOrden()).setIkEmpresa(empresas.get(0));
			  else 
				  ((OrdenCompra)this.getAdminOrden().getOrden()).setIkEmpresa(empresas.get(empresas.indexOf(((OrdenCompra)this.getAdminOrden().getOrden()).getIkEmpresa())));
			} // if	
  		params.put("sucursales", ((OrdenCompra)this.getAdminOrden().getOrden()).getIkEmpresa());
      this.attrs.put("almacenes", UIEntity.seleccione("TcManticAlmacenesDto", "almacenes", params, columns, "clave"));
 			List<UISelectEntity> almacenes= (List<UISelectEntity>)this.attrs.get("almacenes");
			if(!almacenes.isEmpty()) {
				if(this.accion.equals(EAccion.AGREGAR))
				  ((OrdenCompra)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(0));
			  else
				  ((OrdenCompra)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(almacenes.indexOf(((OrdenCompra)this.getAdminOrden().getOrden()).getIkAlmacen())));
			} // if
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.remove(0);
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("clientes", UIEntity.seleccione("TcManticClientesDto", "sucursales", params, columns, "clave"));
      this.attrs.put("proveedores", UIEntity.build("VistaOrdenesComprasDto", "moneda", params, columns));
			List<UISelectEntity> proveedores= (List<UISelectEntity>)this.attrs.get("proveedores");
			if(!proveedores.isEmpty()) { 
				if(this.accion.equals(EAccion.AGREGAR))
				  ((OrdenCompra)this.getAdminOrden().getOrden()).setIkProveedor(proveedores.get(0));
				else
				  ((OrdenCompra)this.getAdminOrden().getOrden()).setIkProveedor(proveedores.get(proveedores.indexOf(((OrdenCompra)this.getAdminOrden().getOrden()).getIkProveedor())));
				this.toLoadCondiciones(((OrdenCompra)this.getAdminOrden().getOrden()).getIkProveedor());
				toUpdateFamilias();
			} // if	
			doLoadDesarrollos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} // toLoadCatalog

	public void doLoadDesarrollos() {
		List<Columna> columns           = null;
    Map<String, Object> params      = null;		
		List<UISelectEntity> desarrollos= null;
		UISelectEntity desarrollo       = null;
    try {
			params= new HashMap<>();						
			params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa=" + ((OrdenCompra)this.getAdminOrden().getOrden()).getIkEmpresa().getKey());
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
			desarrollos= (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave");
			if(!desarrollos.isEmpty()){
				this.attrs.put("desarrollos", desarrollos);			
				if(this.accion.equals(EAccion.AGREGAR))
					desarrollo= UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("desarrollos"));				
				else
					desarrollo= new UISelectEntity(((OrdenCompra)this.getAdminOrden().getOrden()).getIdDesarrollo());				
				this.attrs.put("desarrollo", desarrollo);
				this.attrs.put("cliente", desarrollos.get(desarrollos.indexOf(desarrollo)).toString("razonSocial"));
				params= new HashMap<>();
				params.put(Constantes.SQL_CONDICION, "tc_mantic_almacenes.id_desarrollo=" + desarrollo.getKey());
				this.attrs.put("almacenes", UIEntity.build("TcManticAlmacenesDto", "row", params, columns));
				List<UISelectEntity> almacenes= (List<UISelectEntity>)this.attrs.get("almacenes");
				if(!almacenes.isEmpty()) 
					((OrdenCompra)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(0));				
			} // if
			else{
				this.attrs.put("desarrollos", new ArrayList<>());
				this.attrs.put("desarrollo", new UISelectEntity(-1L));
				this.attrs.put("cliente", "");
				this.attrs.put("almacenes", new ArrayList<>());
				((OrdenCompra)this.getAdminOrden().getOrden()).setIkAlmacen(new UISelectEntity(-1L));
			} // else
			doLoadContratos();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // doLoadDesarrollos
	
	private void doLoadContratos(){
		List<UISelectEntity> contratos= null;
		UISelectEntity desarrollo     = null;
		Map<String, Object>params     = null;
		try {
			desarrollo= (UISelectEntity) this.attrs.get("desarrollo");
			params= new HashMap<>();
			params.put("idDesarrollo", desarrollo.getKey());
			contratos= UIEntity.seleccione("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
			this.attrs.put("contratos", contratos);
			this.attrs.put("contrato", UIBackingUtilities.toFirstKeySelectEntity(contratos));
			doLoadLotes();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadContratos
	
	public void doLoadLotes(){
		UISelectEntity contrato  = null;
		List<UISelectItem> lotes = null;
		Map<String, Object>params= null;
		try {
			contrato= (UISelectEntity) this.attrs.get("contrato");
			params= new HashMap<>();
			params.put("idContrato", contrato.getKey());
			lotes= UISelect.build("TcKeetContratosLotesDto", "byContratoContratistas", params, "descripcionLote", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("lotes", lotes);						
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadLotes
	
	private LocalDate toCalculateFechaEstimada(Calendar fechaEstimada, int tipoDia, int dias) {
		fechaEstimada.set(Calendar.DATE, fechaEstimada.get(Calendar.DATE)+ dias);
		if(tipoDia== 2) {
			fechaEstimada.add(Calendar.DATE, ((int)(dias/5)* 2));
			int dia= fechaEstimada.get(Calendar.DAY_OF_WEEK);
			dias= dia== Calendar.SUNDAY? 1: dia== Calendar.SATURDAY? 2: 0;
			fechaEstimada.add(Calendar.DATE, dias);
		} // if
		//return LocalDate.of(fechaEstimada.get(Calendar.YEAR), fechaEstimada.get(Calendar.MONTH), fechaEstimada.get(Calendar.DAY_OF_MONTH));
		return LocalDateTime.ofInstant(fechaEstimada.toInstant(), ZoneId.systemDefault()).toLocalDate();
	} // toCalculateFechaEstimada
	
	private void toLoadCondiciones(UISelectEntity proveedor) {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
  		params.put("idProveedor", proveedor.getKey());
      this.attrs.put("condiciones", UIEntity.build("VistaOrdenesComprasDto", "condiciones", params, columns));
			List<UISelectEntity> condiciones= (List<UISelectEntity>) this.attrs.get("condiciones");
			if(!condiciones.isEmpty()) {        
				if(this.accion.equals(EAccion.AGREGAR)){
					((OrdenCompra)this.getAdminOrden().getOrden()).setDescuento(condiciones.get(0).toString("descuento"));
					this.doUpdatePorcentaje();
				  ((OrdenCompra)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(0));
				} // if
				else {
					int index= condiciones.indexOf(((OrdenCompra)this.getAdminOrden().getOrden()).getIkProveedorPago());
					if(index>= 0)
				    ((OrdenCompra)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(index));
					else {
						((OrdenCompra)this.getAdminOrden().getOrden()).setDescuento(condiciones.get(0).toString("descuento"));
						this.doUpdatePorcentaje();
						((OrdenCompra)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(0));
					} // if
				} // if		
			} // if	
			this.attrs.put("proveedor", proveedor);
			((OrdenCompra)this.getAdminOrden().getOrden()).setEntregaEstimada(this.toCalculateFechaEstimada(Calendar.getInstance(), proveedor.toInteger("idTipoDia"), proveedor.toInteger("dias")));
			this.checkDevolucionesPendientes(proveedor.getKey());
    } // try
    catch (Exception e) {
			Error.mensaje(e);
			//JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} // toLoadCondiciones
	
	public void doUpdateProveedor() {
		try {
			if(this.tipoOrden.equals(EOrdenes.PROVEEDOR)) {
				this.getAdminOrden().getArticulos().clear();
  			this.getAdminOrden().getArticulos().add(new Articulo(-1L));
				this.getAdminOrden().toCalculate();
			} // if	
			else 
				this.toSearchCodigos();
			List<UISelectEntity> proveedores= (List<UISelectEntity>)this.attrs.get("proveedores");
			this.toLoadCondiciones(proveedores.get(proveedores.indexOf((UISelectEntity)((OrdenCompra)this.getAdminOrden().getOrden()).getIkProveedor())));
			toUpdateFamilias();
		}	// try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
	} // doUpdateProveedor
	
	private void toUpdateFamilias(){
		UISelectEntity proveedor   = null;
		List<UISelectItem> familias= null;
		Map<String, Object>params  = null;
		try {
			proveedor= (UISelectEntity) this.attrs.get("proveedor");
			params= new HashMap<>();
			params.put("idProveedor", proveedor.getKey());
			familias= UISelect.build("VistaFamiliasProveedoresDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("familias", familias);			
		} // try
		catch (Exception e) {		
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // toUpdateFamilias
	
	public void doUpdateAlmacen() {
		this.attrs.put("idAlmacen", ((OrdenCompra)this.getAdminOrden().getOrden()).getIkAlmacen().getKey());
		if(this.tipoOrden.equals(EOrdenes.ALMACEN)) {
  		this.getAdminOrden().getArticulos().clear();
			this.getAdminOrden().getArticulos().add(new Articulo(-1L));
			this.getAdminOrden().toCalculate();
		} // if	
	} // doUpdateAlmacen

	public void doTabChange(TabChangeEvent event) {
		if(event.getTab().getTitle().equals("Articulos")) {
			if(this.attrs.get("proveedores")== null) 
  	  	JsfBase.addMessage("No se selecciono ningun proveedor !", ETipoMensaje.INFORMACION);
			if(this.attrs.get("articulos")== null) {
				switch(this.tipoOrden) {
					case NORMAL:
						break;
					case ALMACEN: 
						this.toLoadArticulos("almacen");
						break;
					case PROVEEDOR:
						this.toLoadArticulos("proveedor");
						break;
				} // switch
			} // if
			UIBackingUtilities.update("contenedorGrupos:sinIva");
			UIBackingUtilities.update("contenedorGrupos:paginator");
		} // if	
		else 
			if(event.getTab().getTitle().equals("Faltantes")) 
        this.doLoadFaltantes();
			else 
			  if(event.getTab().getTitle().equals("Ventas perdidas")) 
           this.doLoadPerdidas();
	} // doTabChange
  
	public void toLoadArticulos(String idXml) {
		List<Articulo> articulos  = null;
    Map<String, Object> params= new HashMap<>();
		try {
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.putAll(((OrdenCompra)this.getAdminOrden().getOrden()).toMap());
			articulos= (List<Articulo>)DaoFactory.getInstance().toEntitySet(Articulo.class, "VistaOrdenesComprasDto", idXml, params);
      if(articulos!= null && this.getAdminOrden().getArticulos().isEmpty())
				for (Articulo articulo : articulos) {
					articulo.toPrepare(
						(Boolean)this.attrs.get("sinIva"), 
						((OrdenCompra)this.getAdminOrden().getOrden()).getTipoDeCambio(), 
						((OrdenCompra)this.getAdminOrden().getOrden()).getIdProveedor()
					);
					this.getAdminOrden().add(articulo);
				} // for
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
    finally {
      Methods.clean(params);
    } // finally
	} // toLoadArticulos

	private void checkDevolucionesPendientes(Long idProveedor) {
		Map<String, Object> params= null;
		List<Entity> pendientes   = null;
		try {
			params    = new HashMap<>();
			params.put("idProveedor", idProveedor);
			pendientes= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaOrdenesComprasDto", "pendientes", params);
			this.attrs.put("pendientes", "<hr class='ui-separator ui-state-default ui-corner-all'/>");
			if(pendientes!= null && !pendientes.isEmpty()) {
				StringBuilder sb= new StringBuilder("<hr class='ui-separator ui-state-default ui-corner-all'/><i style='margin-top:-23px; margin-right:-10px; float: right;' class='fa fa-fw fa-2x fa-truck janal-color-orange' style='float:right;' title='");
			  sb.append("Devoluciones pendientes por entregar al proveedor:\n\n");	
				for (Entity pendiente: pendientes) {
				  sb.append(pendiente.toString("consecutivo"));	
				  sb.append(" - ");	
				  sb.append(this.doFechaEstandar(pendiente.toTimestamp("registro")));	
				  sb.append(" - [ $ ");	
				  sb.append(this.doDecimalSat(pendiente.toDouble("total")));	
				  sb.append(" ]\n");	
				} // for
				sb.append("\n'></i>");
  			this.attrs.put("pendientes", sb.toString());
			} // if	
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
			Methods.clean(pendientes);
		} // finally
	} // checkDevolucionesPendientes

  public void doUpdatePlazo() {
		if(((OrdenCompra)this.getAdminOrden().getOrden()).getIkProveedorPago()!= null) {
			List<UISelectEntity> condiciones= (List<UISelectEntity>) this.attrs.get("condiciones");
      ((OrdenCompra)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(condiciones.indexOf(((OrdenCompra)this.getAdminOrden().getOrden()).getIkProveedorPago())));
      ((OrdenCompra)this.getAdminOrden().getOrden()).setDescuento(((OrdenCompra)this.getAdminOrden().getOrden()).getIkProveedorPago().toString("descuento"));
      this.doUpdatePorcentaje();
		} // if
	}	// doUpdatePlazo

	public void doEliminarPerdido() {
		Transaccion transaccion= null;
		try {
			UISelectEntity perdido= (UISelectEntity)this.attrs.get("perdidoRemove");   		
			transaccion= new Transaccion(perdido.getKey());
			if(transaccion.ejecutar(EAccion.DEPURAR)){
				List<UISelectEntity> perdidos= (List<UISelectEntity>)this.attrs.get("perdidos");
				perdidos.remove(perdidos.indexOf(perdido));
				this.attrs.put("perdidos", perdidos);
			} // if
		} // try
	  catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
	}	// doEliminarPerdido
	
	@Override
  public void doFindArticulo(Integer index) {
		super.doFindArticulo(index);
		List<UISelectEntity> articulos= (List<UISelectEntity>)this.attrs.get("articulos");
		if(articulos!= null) {
		  int position= articulos.indexOf((UISelectEntity)this.attrs.get("articulo"));
		  if(position>= 0)
        this.attrs.put("seleccionado", articulos.get(position));
		} // if	
	} // doFindArticulo

	public void doAutoSaveOrden() {
	  this.toSaveRecord();	
	} // doAutoSaveOrden
	
	@Override
	public void toSaveRecord() {
    Transaccion transaccion= null;
    try {			
			((OrdenCompra)this.getAdminOrden().getOrden()).setDescuentos(this.getAdminOrden().getTotales().getDescuento());
			((OrdenCompra)this.getAdminOrden().getOrden()).setExcedentes(this.getAdminOrden().getTotales().getExtra());
			((OrdenCompra)this.getAdminOrden().getOrden()).setImpuestos(this.getAdminOrden().getTotales().getIva());
			((OrdenCompra)this.getAdminOrden().getOrden()).setSubTotal(this.getAdminOrden().getTotales().getSubTotal());
			((OrdenCompra)this.getAdminOrden().getOrden()).setTotal(this.getAdminOrden().getTotales().getTotal());
			transaccion = new Transaccion(((OrdenCompra)this.getAdminOrden().getOrden()), this.getAdminOrden().getArticulos());
			this.getAdminOrden().toAdjustArticulos();
			if (transaccion.ejecutar(EAccion.MOVIMIENTOS)) {
   			UIBackingUtilities.execute("jsArticulos.back('guard\\u00F3 orden de compra', '"+ ((OrdenCompra)this.getAdminOrden().getOrden()).getConsecutivo()+ "');");
				this.accion= EAccion.MODIFICAR;
				this.getAdminOrden().getArticulos().add(new Articulo(-1L));
				this.attrs.put("autoSave", Global.format(EFormatoDinamicos.FECHA_HORA, Fecha.getRegistro()));
			} // if	
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

  public void toSearchCodigos() {
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("idProveedor", this.getAdminOrden().getIdProveedor());
			for (Articulo articulo: this.getAdminOrden().getArticulos()) {
				params.put("idArticulo", articulo.getIdArticulo());
				Value codigo= (Value)DaoFactory.getInstance().toField("TcManticArticulosCodigosDto", "codigo", params, "codigo");
				articulo.setModificado(codigo!= null? !Objects.equal(codigo.toString(), articulo.getCodigo()): !Cadena.isVacio(articulo.getCodigo()));
				articulo.setCodigo(codigo== null? "": codigo.toString());
			} // for
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	}	// toSearchCodigos

	@Override
	public void doSearchArticulo(Long idArticulo, Integer index) {
		this.attrs.put("idAlmacen", ((OrdenCompra)this.getAdminOrden().getOrden()).getIkAlmacen().getKey());
		super.doSearchArticulo(idArticulo, index);
	} // doSearchArticulo
	
	public void doCleanLookForFaltantes() {
		this.attrs.put("lookForFaltantes", "");
		this.doLoadFaltantes();
	} // doCleanLookForFaltantes
	
	public void doLookForFaltantes() {
		this.doLoadFaltantes();
	} // doLookForFaltantes
	
  public void doCleanLookForPerdidos() {
		this.attrs.put("lookForPerdidos", "");
		this.doLoadPerdidas();
	} // doCleanLookForPerdidos

  public void doLookForPerdidos() {
		this.doLoadPerdidas();
	} // doLookForPerdidos

	public void doUpdateCliente(){
		List<UISelectEntity> desarrollos= null;
		UISelectEntity desarrollo       = null;
		List<Columna> columns           = null;
		Map<String, Object>params       = null;
		try {
			desarrollos= (List<UISelectEntity>) this.attrs.get("desarrollos");
			desarrollo= (UISelectEntity) this.attrs.get("desarrollo");
			this.attrs.put("cliente", desarrollos.get(desarrollos.indexOf(desarrollo)).toString("razonSocial"));			
			columns= new ArrayList<>();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "tc_mantic_almacenes.id_desarrollo=" + desarrollo.getKey());
      this.attrs.put("almacenes", UIEntity.build("TcManticAlmacenesDto", "row", params, columns));
 			List<UISelectEntity> almacenes= (List<UISelectEntity>)this.attrs.get("almacenes");
			if(!almacenes.isEmpty()) 
			  ((OrdenCompra)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(0));
			doLoadContratos();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doUpdateCliente
	
	public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    try {			
			 // this.getAdminOrden().toCheckTotales();
			((OrdenCompra)this.getAdminOrden().getOrden()).setDescuentos(this.getAdminOrden().getTotales().getDescuento());
			((OrdenCompra)this.getAdminOrden().getOrden()).setExcedentes(this.getAdminOrden().getTotales().getExtra());
			((OrdenCompra)this.getAdminOrden().getOrden()).setImpuestos(this.getAdminOrden().getTotales().getIva());
			((OrdenCompra)this.getAdminOrden().getOrden()).setSubTotal(this.getAdminOrden().getTotales().getSubTotal());
			((OrdenCompra)this.getAdminOrden().getOrden()).setTotal(this.getAdminOrden().getTotales().getTotal());
			((OrdenCompra)this.getAdminOrden().getOrden()).setIdDesarrollo(((UISelectEntity)this.attrs.get("desarrollo")).getKey());
			transaccion = new Transaccion(((OrdenCompra)this.getAdminOrden().getOrden()), this.getAdminOrden().getArticulos());
			this.getAdminOrden().toAdjustArticulos();
			if (transaccion.ejecutar(this.accion)) {
				if(this.accion.equals(EAccion.AGREGAR)) {
 				  regresar = this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);
    			UIBackingUtilities.execute("jsArticulos.back('gener\\u00F3 orden de compra', '"+ ((OrdenCompra)this.getAdminOrden().getOrden()).getConsecutivo()+ "');");
				} // if	
				else
					this.getAdminOrden().toStartCalculate();
 				if(!this.accion.equals(EAccion.CONSULTAR)) 
    			JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" la orden de compra."), ETipoMensaje.INFORMACION);
  			JsfBase.setFlashAttribute("idOrdenCompra", ((OrdenCompra)this.getAdminOrden().getOrden()).getIdOrdenCompra());
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar la orden de compra.", ETipoMensaje.ALERTA);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idOrdenCompra", ((OrdenCompra)this.getAdminOrden().getOrden()).getIdOrdenCompra());
    return (String)this.attrs.get("retorno");
  } // doCancelar
}