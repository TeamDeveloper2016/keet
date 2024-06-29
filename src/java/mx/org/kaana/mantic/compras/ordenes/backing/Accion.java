package mx.org.kaana.mantic.compras.ordenes.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.keet.compras.beans.General;
import mx.org.kaana.keet.compras.beans.Individual;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Cifrar;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.formato.Periodo;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.OrdenCompra;
import mx.org.kaana.mantic.compras.ordenes.reglas.Transaccion;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.compras.ordenes.beans.Detalle;
import mx.org.kaana.mantic.compras.ordenes.beans.OrdenCompraProcess;
import mx.org.kaana.mantic.compras.ordenes.enums.EOrdenes;
import mx.org.kaana.mantic.compras.ordenes.reglas.AdminOrdenes;
import mx.org.kaana.mantic.comun.IBaseArticulos;
import mx.org.kaana.mantic.comun.IBaseStorage;
import mx.org.kaana.mantic.enums.ETipoMediosPago;
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
  private List<Detalle> detalles;

	public String getTitulo() {
		return "(".concat(tipoOrden.name()).concat(")");
	}

	public EOrdenes getTipoOrden() {
		return tipoOrden;
	}
	
	public String getAgregar() {
		return this.accion.equals(EAccion.AGREGAR)? "none": "";
	}

  public List<Detalle> getDetalles() {
    return detalles;
  }
	
	@PostConstruct
  @Override
  protected void init() {		
    try {
//      this.accion   = EAccion.MODIFICAR;
//      this.attrs.put("idOrdenCompra", 7042L);
      this.accion   = JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("idOrdenCompra", JsfBase.getFlashAttribute("idOrdenCompra")== null? -1L: JsfBase.getFlashAttribute("idOrdenCompra"));
			this.attrs.put("procesado", Boolean.FALSE);
			this.tipoOrden= JsfBase.getParametro("zOyOxDwIvGuCt")== null? EOrdenes.DIRECTA: EOrdenes.valueOf(Cifrar.descifrar(JsfBase.getParametro("zOyOxDwIvGuCt")));
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("isPesos", false);
			this.attrs.put("buscaPorCodigo", false);
			this.attrs.put("seleccionado", null);
			this.attrs.put("familiasSeleccion", new Object[] {});
			this.attrs.put("lotesSeleccion", new Object[] {});
			this.attrs.put("isBanco", Boolean.FALSE);
			this.attrs.put("textGlobal", "");
			this.attrs.put("textIndividual", "");
			this.attrs.put("idTodos", 2L);
			this.doLoad();
			this.attrs.put("procesado", Boolean.TRUE);
      this.detalles= new ArrayList<>();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 

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
      this.toLoadBancos();
      this.toLoadTiposMediosPagos();
      this.toLoadTiposPagos();
      this.toLoadAlmacenistas();
      this.toLoadEmpresaTipoContacto();
      this.toLoadTipoOrden();
			this.attrs.put("before", this.getAdminOrden().getIdAlmacen());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 

	private void toLoadCatalog() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
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
				this.toUpdateFamilias();
			} // if	
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
		List<Columna> columns           = new ArrayList<>();
    Map<String, Object> params      = new HashMap<>();		
		List<UISelectEntity> desarrollos= null;
    try {
			//params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa=" + ((OrdenCompra)this.getAdminOrden().getOrden()).getIkEmpresa().getKey());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
        case "cafu":
    			params.put("operador", "=");
          break;
        case "gylvi": 
    			params.put("operador", "<=");
          break;
        case "triana":
    			params.put("operador", "<=");
          break;
        default:
    			params.put("operador", "<=");
          break;
      } // swtich
			params.put("idContratoEstatus", EContratosEstatus.ACTIVO.getKey());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
			desarrollos= (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave");
			if(!desarrollos.isEmpty()) {
				this.attrs.put("desarrollos", desarrollos);			
				if(this.accion.equals(EAccion.AGREGAR)) 
          ((OrdenCompra)this.getAdminOrden().getOrden()).setIkDesarrollo(desarrollos.get(0));
        else
				  ((OrdenCompra)this.getAdminOrden().getOrden()).setIkDesarrollo(desarrollos.get(desarrollos.indexOf(((OrdenCompra)this.getAdminOrden().getOrden()).getIkDesarrollo())));
        ((OrdenCompra)this.getAdminOrden().getOrden()).setIkCliente(new UISelectEntity(((OrdenCompra)this.getAdminOrden().getOrden()).getIkDesarrollo().toLong("idCliente")));
			  this.attrs.put("cliente", ((OrdenCompra)this.getAdminOrden().getOrden()).getIkDesarrollo().toString("razonSocial"));
        columns.clear();
        columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
        columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
				params.clear();
				params.put(Constantes.SQL_CONDICION, "tc_mantic_almacenes.id_desarrollo=" + ((OrdenCompra)this.getAdminOrden().getOrden()).getIdDesarrollo());
				this.attrs.put("almacenes", UIEntity.build("TcManticAlmacenesDto", "row", params, columns));
				List<UISelectEntity> almacenes= (List<UISelectEntity>)this.attrs.get("almacenes");
				if(!almacenes.isEmpty()) 
   				if(this.accion.equals(EAccion.AGREGAR))
            ((OrdenCompra)this.getAdminOrden().getOrden()).setIkProveedor(almacenes.get(0));
          else  
  				  ((OrdenCompra)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(almacenes.indexOf(((OrdenCompra)this.getAdminOrden().getOrden()).getIkAlmacen())));
			} // if
      else {
				this.attrs.put("desarrollos", new ArrayList<>());
				((OrdenCompra)this.getAdminOrden().getOrden()).setIkDesarrollo(new UISelectEntity(-1L));
				this.attrs.put("cliente", "");
				this.attrs.put("almacenes", new ArrayList<>());
				((OrdenCompra)this.getAdminOrden().getOrden()).setIkAlmacen(new UISelectEntity(-1L));
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
	} 
	
	public void doLoadContratos() {
		List<UISelectEntity> contratos= null;
		Map<String, Object>params     = new HashMap<>();
		try {
      // A PARTIR DEL 1 DE OCTUBRE SE PODRA HACER VALIDACIONES DE QUE LOS CONTRATOS ESTEN COMPLETOS PARA DEJARLOS COMO ACTIVOS
			params.put("idDesarrollo", ((OrdenCompra)this.getAdminOrden().getOrden()).getIdDesarrollo());
      params.put(Constantes.SQL_CONDICION,  "tc_keet_contratos.id_contrato_estatus= "+ EContratosEstatus.ACTIVO.getKey());
			contratos= UIEntity.seleccione("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
			this.attrs.put("contratos", contratos);
      if(!contratos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          ((OrdenCompra)this.getAdminOrden().getOrden()).setIkContrato(contratos.get(0));
        else  
          ((OrdenCompra)this.getAdminOrden().getOrden()).setIkContrato(contratos.get(contratos.indexOf(((OrdenCompra)this.getAdminOrden().getOrden()).getIkContrato())));
			this.doLoadLotes();
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} 
	
	public void doLoadLotes() {
		List<UISelectEntity> lotes= null;
		Map<String, Object>params = new HashMap<>();
    Object[] list             = null;
		try {
			params.put("idContrato", ((OrdenCompra)this.getAdminOrden().getOrden()).getIdContrato());
			lotes= UIEntity.build("TcKeetContratosLotesDto", "byContratoContratistas", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("lotes", lotes);						
      if(!lotes.isEmpty()) 
        if(!this.accion.equals(EAccion.AGREGAR)) { 
    			params.put("idOrdenCompra", ((OrdenCompra)this.getAdminOrden().getOrden()).getIdOrdenCompra());
          List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet("TcKeetOrdenesContratosLotesDto", "lotes", params);
          if(!items.isEmpty()) {
            list     = new Object[items.size()];
            int count= 0;
            for (Entity entity : items) {
              int index= lotes.indexOf(new UISelectEntity(entity.toLong("idContratoLote")));
              if(index>= 0)
                list[count++]= lotes.get(index);
            } // for
          } // if  
        } // if  
      if(list== null)
        list= new Object[] {};
      this.attrs.put("lotesSeleccion", list);
      // if((Boolean)this.attrs.get("procesado"))
      //  this.doEraseArticulos();
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
	} 
	
	private void toLoadCondiciones(UISelectEntity proveedor) {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
  		params.put("idProveedor", proveedor.getKey());
      this.attrs.put("condiciones", UIEntity.build("VistaOrdenesComprasDto", "condiciones", params, columns));
			List<UISelectEntity> condiciones= (List<UISelectEntity>) this.attrs.get("condiciones");
			if(!condiciones.isEmpty()) {        
				if(this.accion.equals(EAccion.AGREGAR)) {
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
      else {
        ((OrdenCompra)this.getAdminOrden().getOrden()).setDescuento("0");
				this.doUpdatePorcentaje();
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
	} 
	
	public void doUpdateProveedor() {
		try {
			if(this.tipoOrden.equals(EOrdenes.PROVEEDOR)) {
				this.getAdminOrden().getArticulos().clear();
        this.toCleanDetalles();
  			this.getAdminOrden().getArticulos().add(new Articulo(-1L));
				this.getAdminOrden().toCalculate();
			} // if	
			else 
				this.toSearchCodigos();
			List<UISelectEntity> proveedores= (List<UISelectEntity>)this.attrs.get("proveedores");
			this.toLoadCondiciones(proveedores.get(proveedores.indexOf((UISelectEntity)((OrdenCompra)this.getAdminOrden().getOrden()).getIkProveedor())));
			this.toUpdateFamilias();
		}	// try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
	} 
	
	private void toUpdateFamilias() throws Exception {
		UISelectEntity proveedor     = (UISelectEntity) this.attrs.get("proveedor");
		List<UISelectEntity> familias= null;
		Map<String, Object>params    = new HashMap<>();
    Object[] list                = null;
		try {
			params.put("idProveedor", proveedor.getKey());
			familias= UIEntity.build("VistaFamiliasProveedoresDto", "row", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("familias", familias);			
      if(!familias.isEmpty())
        if(!this.accion.equals(EAccion.AGREGAR)) {
    			params.put("idOrdenCompra", ((OrdenCompra)this.getAdminOrden().getOrden()).getIdOrdenCompra());
          List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet("TcKeetOrdenesFamiliasDto", "familias", params);
          if(!items.isEmpty()) {
            list     = new Object[items.size()];
            int count= 0;
            for (Entity entity: items) {
              int index= familias.indexOf(new UISelectEntity(entity.toLong("idFamilia")));
              if(index>= 0)
                list[count++]= familias.get(index);
              else {
                list[count++]= familias.get(0);
                LOG.error("ESTA FAMILIA ["+ proveedor.getKey()+ "] NO EXISTE PARA ESTE PROVEEDOR ["+ entity.toLong("idFamilia")+ "],VERIFICAR PORQUE NO EXISTE");
              } // else  
            } // for
          } // if  
        } // if  
      if(list== null)
        list= new Object[] {};
      this.attrs.put("familiasSeleccion",  list);
		} // try
		catch (Exception e) {		
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} 
	
	public void doUpdateAlmacen() {
		this.attrs.put("idAlmacen", ((OrdenCompra)this.getAdminOrden().getOrden()).getIkAlmacen().getKey());
		if(this.tipoOrden.equals(EOrdenes.ALMACEN)) {
  		this.getAdminOrden().getArticulos().clear();
      this.toCleanDetalles();
			this.getAdminOrden().getArticulos().add(new Articulo(-1L));
			this.getAdminOrden().toCalculate();
		} // if	
	} 

	public void doTabChange(TabChangeEvent event) {
    Object[] familias= (Object[])this.attrs.get("familiasSeleccion");
    Object[] lotes   = (Object[])this.attrs.get("lotesSeleccion");
    switch (event.getTab().getTitle()) {
      case "Articulos":
        if(familias.length> 0) {
          this.toLoadArticulos(Arrays.asList(familias), Arrays.asList(lotes));
          UIBackingUtilities.update("contenedorGrupos:sinIva");
          UIBackingUtilities.update("contenedorGrupos:paginator");
        } // if
        else {
          this.getAdminOrden().getArticulos().clear();
          this.toCleanDetalles();
          UIBackingUtilities.execute("janal.show([{summary: 'Proveedor:', detail: 'No tiene definido una familia'}]);"); 
          // UIBackingUtilities.execute("janal.show([{summary: 'Contrato:', detail: 'No tiene definido un lote'},{summary: 'Proveedor:', detail: 'No tiene definido una familia'}]);"); 
        } // else			
        break;
      case "Faltantes":
        this.doLoadFaltantes();
        break;
      case "Ventas perdidas":
        this.doLoadPerdidas();
        break;
      case "Historial":
        this.doLoadHistorico();
        break;
      case "Requisiciones":
        UIBackingUtilities.update(familias.length> 0? "PF('idResidente').enable()": "PF('idResidente').disable()");
        UIBackingUtilities.update(familias.length> 0? "PF('idTodos').enable()": "PF('idTodos').disable()");
        if(familias.length> 0) {
          this.toLoadArticulos(Arrays.asList(familias), Arrays.asList(lotes));
          this.toLoadResidentes();
          this.toLoadDetalle(Boolean.FALSE);
        }  // if
        else {
          this.detalles= new ArrayList<>();
          UIBackingUtilities.execute("janal.show([{summary: 'Proveedor:', detail: 'No tiene definido una familia'}]);"); 
        } // if  
        break;
      default:
        break;
    } // switch
	} 
  
  public void doLoadHistorico() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      params.put("idContrato", ((OrdenCompra)this.getAdminOrden().getOrden()).getIdContrato());
      columns.add(new Columna("usuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("etapa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("familia", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("contrato", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("noViviendas", EFormatoDinamicos.NUMERO_CON_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      this.attrs.put("historico", UIEntity.build("VistaFamiliasProveedoresDto", "lazy", params, columns, Constantes.SQL_TODOS_REGISTROS));
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

	private void toLoadArticulos(List<Object> familiasSeleccion, List<Object> lotesSeleccion) {
		List<Entity> articulos    = null;
    Map<String, Object> params= new HashMap<>();
		UISelectEntity proveedor  = null;
		StringBuilder familias    = new StringBuilder();
		int count                 = 0;
		try {
			if(getAdminOrden().getArticulos().isEmpty()) {
				proveedor= (UISelectEntity) this.attrs.get("proveedor");
        StringBuilder sb= new StringBuilder();
        for (Object lote: lotesSeleccion) {
          familias.delete(0, familias.length());
          for (Object familia: familiasSeleccion) {
            params.put("idContratoLote", ((UISelectEntity)lote).getKey());
            params.put("idFamilia", ((UISelectEntity)familia).getKey());
            params.put("idContrato", ((OrdenCompra)this.getAdminOrden().getOrden()).getIdContrato());
            Value idKey= (Value)DaoFactory.getInstance().toField("VistaFamiliasProveedoresDto", "existe", params, "idKey");
            if(idKey== null || idKey.getData()== null) {
              familias.append(((UISelectEntity)familia).getKey()).append(", ");
            } // if
          } // for
          if(familias.length()> 0) {
            String clave= this.toClaveMateriales(((UISelectEntity)lote).getKey());
            sb.append("(tc_keet_materiales.clave like '").append(clave).append("%' and tc_mantic_articulos.id_familia in (").append(familias.substring(0, familias.length()- 2)).append("))");
            sb.append(" or ");
          } // if
        } // for
        if(sb.length()> 0) {
          params.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));				
          params.put("idProveedor", proveedor.getKey());				
          articulos= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaCapturaMaterialesDto", "materialesVariasClave", params);
          if(articulos!= null && !articulos.isEmpty()){
            this.getAdminOrden().getArticulos().clear();
            this.toCleanDetalles();
            for (Entity articulo : articulos) {
              this.getAdminOrden().getArticulos().add(count, new Articulo(articulo.getKey()));
              this.toMoveDataArticulo(new UISelectEntity(articulo), count++);
            } // for
            this.getAdminOrden().toCalculate();
          } // if
          else
            this.getAdminOrden().toStartCalculate();
        } // if
        else 
          this.getAdminOrden().toStartCalculate();
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

	protected void toMoveDataArticulo(UISelectEntity articulo, Integer index) throws Exception {
		Articulo temporal= this.getAdminOrden().getArticulos().get(index);
		Map<String, Object> params= new HashMap<>();
		try {
			if(articulo.size()> 1) {
				this.doSearchArticulo(articulo.toLong("idArticulo"), index);
				params.put("idArticulo", articulo.toLong("idArticulo"));
				params.put("idProveedor", this.getAdminOrden().getIdProveedor());
				params.put("idAlmacen", this.getAdminOrden().getIdAlmacen());
				temporal.setKey(articulo.toLong("idArticulo"));
				temporal.setIdArticulo(articulo.toLong("idArticulo"));
				temporal.setIdProveedor(this.getAdminOrden().getIdProveedor());
				temporal.setIdRedondear(articulo.toLong("idRedondear"));
				// verificar el codigo principal del articulo y recuperar el valor del multiplo paras las ordenes de compra
				Entity codigo= (Entity)DaoFactory.getInstance().toEntity("TcManticArticulosCodigosDto", "codigo", params);
				if(codigo== null || codigo.isEmpty()) {
  				temporal.setCodigo(articulo.containsKey("codigo")? articulo.toString("codigo"): "");
					temporal.setMultiplo(1L);
				} // if
				else {
				  temporal.setCodigo(codigo.toString("codigo"));
				  temporal.setMultiplo(codigo.toLong("multiplo"));
					temporal.setCantidad(Double.valueOf(temporal.getMultiplo()));
				}	// else
				if(Cadena.isVacio(articulo.toString("propio")))
					LOG.warn("El articulo ["+ articulo.toLong("idArticulo")+" ] no tiene codigo asignado '"+ articulo.toString("nombre")+ "'");
				temporal.setPropio(articulo.toString("propio"));
				temporal.setNombre(articulo.toString("nombre"));
				temporal.setOrigen(articulo.toString("origen"));
				temporal.setValor(articulo.toDouble(this.getPrecio()));
				// SI VIENE EN LA CONSULTA EL CAMPO DE PORCENTAJE ES LA SUMA DE LA COLUMNA DE DESCUENTO Y EXTRA SEPARADA POR COMA
				// ASIGNARLA PARA CALCULAR EL COSTO REAL DEL ARTICULO
				if(articulo.containsKey("porcentajes")) {
					//temporal.setMorado(articulo.toString("morado"));
					temporal.setPorcentajes(articulo.toString("porcentajes"));
				} // if	
				// SI VIENE DE IMPORTAR EL ARTICULO DE UN XML ENTONCES CONSIDERAR EL COSTO DE LA FACTURA CON RESPECTO AL DEL CATALOGOD E ARTICULOS
				if(articulo.containsKey("costo")) 
  				temporal.setCosto(articulo.toDouble("costo"));
			  else
				  temporal.setCosto(articulo.toDouble(this.getPrecio()));
				temporal.setIva(articulo.toDouble("iva"));				
				temporal.setSat(articulo.get("sat").getData()!= null ? articulo.toString("sat") : "");				
				temporal.setDescuento(this.getAdminOrden().getDescuento());
				temporal.setExtras(this.getAdminOrden().getExtras());				
				// SON ARTICULOS QUE ESTAN EN LA FACTURA MAS NO EN LA ORDEN DE COMPRA
				if(articulo.containsKey("descuento")) 
				  temporal.setDescuento(articulo.toString("descuento"));
				if(articulo.containsKey("cantidad")) {
				  temporal.setCantidad(articulo.toDouble("cantidad"));
				  temporal.setSolicitados(articulo.toDouble("cantidad"));
				} // if	
				if(temporal.getCantidad()< 1D)					
					temporal.setCantidad(1D);
				temporal.setCuantos(0D);
				temporal.setUltimo(this.attrs.get("ultimo")!= null);
				temporal.setSolicitado(this.attrs.get("solicitado")!= null);
				temporal.setUnidadMedida(articulo.toString("unidadMedida"));
				temporal.setPrecio(articulo.toDouble("precio"));				
				
				// RECUPERA EL STOCK DEL ALMACEN MAS SABER SI YA FUE HUBO UN CONTEO O NO
				Entity inventario= (Entity)DaoFactory.getInstance().toEntity("TcManticInventariosDto", "stock", params);
				if(inventario!= null && inventario.size()> 0) {
				  temporal.setStock(inventario.toDouble("stock"));
				  //temporal.setIdAutomatico(inventario.toLong("idAutomatico"));
				} // if
				// Esto es para cuando se agregan articulos de forma directa del archivo XML
				if(articulo.containsKey("disponible")) 
  				temporal.setDisponible(articulo.toBoolean("disponible"));
				if(index== this.getAdminOrden().getArticulos().size()- 1) {
					this.getAdminOrden().getArticulos().add(new Articulo(-1L));
  				this.getAdminOrden().toAddUltimo(this.getAdminOrden().getArticulos().size()- 1);
					UIBackingUtilities.execute("jsArticulos.update("+ (this.getAdminOrden().getArticulos().size()- 1)+ ");");
				} // if	
				if(articulo.containsKey("facturado")) 
					temporal.setFacturado(true);
				UIBackingUtilities.execute("jsArticulos.callback('"+ articulo.getKey()+ "');");
				this.getAdminOrden().toCalculate(index);
				if(this.attrs.get("paginator")== null || !(boolean)this.attrs.get("paginator"))
				  this.attrs.put("paginator", this.getAdminOrden().getArticulos().size()> Constantes.REGISTROS_LOTE_TOPE);
				//if(this instanceof IBaseStorage)
 				//	((IBaseStorage)this).toSaveRecord();
			} // if	
			else
				temporal.setNombre("<span class='janal-color-orange'>EL ARTICULO NO EXISTE EN EL CATALOGO !</span>");
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}
	
	private String toClaveMateriales(Long idContratoLote) throws Exception {
		StringBuilder regresar        = null;
		UISelectEntity contrato       = null;
		List<UISelectEntity> contratos= null;
		try {
			contratos= (List<UISelectEntity>) this.attrs.get("contratos");
			contrato = ((OrdenCompra)this.getAdminOrden().getOrden()).getIkContrato();
			regresar = new StringBuilder();
			regresar.append(Cadena.rellenar(JsfBase.getAutentifica().getEmpresa().getIdEmpresa().toString(), 3, '0', true));
			regresar.append(Fecha.getAnioActual());
			regresar.append(Cadena.rellenar(contratos.get(contratos.indexOf(contrato)).toString("orden"), 3, '0', true));
			regresar.append(toOrdenContratoLote(idContratoLote));
		} // try
		catch (Exception e) {		
			throw e;
		} // catch
		return regresar.toString();
	} 
	
	private String toOrdenContratoLote(Long idContratoLote) throws Exception{
		String regresar           = null;
		Map<String, Object> params= new HashMap<>();
		Entity contratoLote       = null;
		try {
			params.put(Constantes.SQL_CONDICION, "id_contrato_lote=" + idContratoLote );
			contratoLote= (Entity) DaoFactory.getInstance().toEntity("TcKeetContratosLotesDto", "row", params);
			regresar= Cadena.rellenar(contratoLote.toString("orden"), 3, '0', true);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} 
	
	private void checkDevolucionesPendientes(Long idProveedor) {
		Map<String, Object> params= new HashMap<>();
		List<Entity> pendientes   = null;
		try {
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
	} 

  public void doUpdatePlazo() {
		if(((OrdenCompra)this.getAdminOrden().getOrden()).getIkProveedorPago()!= null) {
			List<UISelectEntity> condiciones= (List<UISelectEntity>) this.attrs.get("condiciones");
      ((OrdenCompra)this.getAdminOrden().getOrden()).setIkProveedorPago(condiciones.get(condiciones.indexOf(((OrdenCompra)this.getAdminOrden().getOrden()).getIkProveedorPago())));
      ((OrdenCompra)this.getAdminOrden().getOrden()).setDescuento(((OrdenCompra)this.getAdminOrden().getOrden()).getIkProveedorPago().toString("descuento"));
      this.doUpdatePorcentaje();
		} // if
	}	

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
	}	
	
	@Override
  public void doFindArticulo(Integer index) {
		super.doFindArticulo(index);
		List<UISelectEntity> articulos= (List<UISelectEntity>)this.attrs.get("articulos");
		if(articulos!= null) {
		  int position= articulos.indexOf((UISelectEntity)this.attrs.get("articulo"));
		  if(position>= 0)
        this.attrs.put("seleccionado", articulos.get(position));
		} // if	
    // AQUI SE AGREGAN LOS ARTICULOS PARA LA ORDEN DE COMPRA Y RECUPERAR LOS LOS ARTICULOS POR CONTRATO Y POR LOTE
    this.toLoadPartidas();
	} 

	public void doAutoSaveOrden() {
	  this.toSaveRecord();	
	} 
	
	@Override
	public void toSaveRecord() {
    Transaccion transaccion= null;
    try {			
      if(!Objects.equals(((OrdenCompra)this.getAdminOrden().getOrden()).getIdDesarrollo(), -1L)) {
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
      } // if	
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
	} 

	public void doGlobalEvent(Boolean isViewException) {
		LOG.error("ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA");
		if(isViewException && this.getAdminOrden().getArticulos().size()> 0)
		  this.toSaveRecord();
    //UIBackingUtilities.execute("alert('ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA');");
	} // doGlobalEvent

  public void toSearchCodigos() {
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idProveedor", this.getAdminOrden().getIdProveedor());
			for (Articulo articulo: this.getAdminOrden().getArticulos()) {
				params.put("idArticulo", articulo.getIdArticulo());
				Value codigo= (Value)DaoFactory.getInstance().toField("TcManticArticulosCodigosDto", "codigo", params, "codigo");
				articulo.setModificado(codigo!= null? !Objects.equals(codigo.toString(), articulo.getCodigo()): !Cadena.isVacio(articulo.getCodigo()));
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
	}	

	@Override
	public void doSearchArticulo(Long idArticulo, Integer index) {
		this.attrs.put("idAlmacen", ((OrdenCompra)this.getAdminOrden().getOrden()).getIkAlmacen().getKey());
		super.doSearchArticulo(idArticulo, index);
	} 
	
	public void doCleanLookForFaltantes() {
		this.attrs.put("lookForFaltantes", "");
		this.doLoadFaltantes();
	} 
	
	public void doLookForFaltantes() {
		this.doLoadFaltantes();
	} 
	
  public void doCleanLookForPerdidos() {
		this.attrs.put("lookForPerdidos", "");
		this.doLoadPerdidas();
	} 

  public void doLookForPerdidos() {
		this.doLoadPerdidas();
	} 

	public void doUpdateCliente() {
		List<UISelectEntity> desarrollos= null;
		UISelectEntity desarrollo       = null;
		List<Columna> columns           = new ArrayList<>();
		Map<String, Object>params       = new HashMap<>();
		try {
			desarrollos= (List<UISelectEntity>) this.attrs.get("desarrollos");
			desarrollo = ((OrdenCompra)this.getAdminOrden().getOrden()).getIkDesarrollo();
      UISelectEntity cliente= desarrollos.get(desarrollos.indexOf(desarrollo));
			this.attrs.put("cliente", cliente.toString("razonSocial"));			
      ((OrdenCompra)this.getAdminOrden().getOrden()).setIkCliente(cliente);
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			params.put(Constantes.SQL_CONDICION, "tc_mantic_almacenes.id_desarrollo=" + desarrollo.getKey());
      this.attrs.put("almacenes", UIEntity.build("TcManticAlmacenesDto", "row", params, columns));
 			List<UISelectEntity> almacenes= (List<UISelectEntity>)this.attrs.get("almacenes");
			if(!almacenes.isEmpty()) 
			  ((OrdenCompra)this.getAdminOrden().getOrden()).setIkAlmacen(almacenes.get(0));
			this.doLoadContratos();
      this.toLoadAlmacenistas();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
	} 
  
  public void doCheckArticulos() {
  	OrdenCompraProcess orden= null;
    String todos            = null; 
    StringBuilder sb        = new StringBuilder();
    try {			
			orden= new OrdenCompraProcess();
			orden.setOrdenCompra((OrdenCompra)this.getAdminOrden().getOrden());
			orden.setArticulos(this.getAdminOrden().getArticulos());
			orden.setLotes(Arrays.asList((Object[])this.attrs.get("lotesSeleccion")));
      // VERIFICAR SI LA ORDEN DE COMPRA ES ORDINARIA VERIFICAR LOS UMBRALES POR CONTRATO Y POR LOTE
      sb.append(orden.getOrdenCompra().toCheckGeneral());
      if(orden.getLotes()!= null && !orden.getLotes().isEmpty()) {
        if(sb.length()> 0)
          sb.append(",");
        sb.append(((OrdenCompra)this.getAdminOrden().getOrden()).toCheckIndividual(orden.getArticulos()));
      } // if  
      todos= ((OrdenCompra)this.getAdminOrden().getOrden()).toCheckTodos(orden.getArticulos());
      if(!Cadena.isVacio(todos))
        sb.append(sb.length()== 0? "": ",").append(todos);
      // FALTA VALIDAR AQUELLOS ARTICULOS QUE NO CORRESPONDE A NINGUN LOTE
      if(sb.length()> 0) {
        sb.insert(0, "janal.show([").append("]);");
        UIBackingUtilities.execute(sb.toString()); 
        JsfBase.addMessage("Existen articulos con errores en la orden de compra !", ETipoMensaje.ALERTA);
      } // if
      else
        JsfBase.addMessage("Todo esta correcto con la orden de compra !", ETipoMensaje.ALERTA);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      orden= null;
    } // 
  }
	
	public String doAceptar() {  
    Transaccion transaccion = null;
    String regresar         = null;
  	OrdenCompraProcess orden= null;
    String todos            = null; 
    StringBuilder sb        = new StringBuilder();
    try {			
			// this.getAdminOrden().toCheckTotales();
			((OrdenCompra)this.getAdminOrden().getOrden()).setDescuentos(this.getAdminOrden().getTotales().getDescuento());
			((OrdenCompra)this.getAdminOrden().getOrden()).setExcedentes(this.getAdminOrden().getTotales().getExtra());
			((OrdenCompra)this.getAdminOrden().getOrden()).setImpuestos(this.getAdminOrden().getTotales().getIva());
			((OrdenCompra)this.getAdminOrden().getOrden()).setSubTotal(this.getAdminOrden().getTotales().getSubTotal());
			((OrdenCompra)this.getAdminOrden().getOrden()).setTotal(this.getAdminOrden().getTotales().getTotal());
			orden= new OrdenCompraProcess();
			orden.setOrdenCompra((OrdenCompra)this.getAdminOrden().getOrden());
			orden.setArticulos(this.getAdminOrden().getArticulos());
			orden.setFamilias(Arrays.asList((Object[])this.attrs.get("familiasSeleccion")));
			orden.setLotes(Arrays.asList((Object[])this.attrs.get("lotesSeleccion")));
      // VERIFICAR SI LA ORDEN DE COMPRA ES ORDINARIA VERIFICAR LOS UMBRALES POR CONTRATO Y POR LOTE
//      if(Objects.equals(((OrdenCompra)this.getAdminOrden().getOrden()).getIdTipoOrden(), 1L)) {
//        sb.append(orden.getOrdenCompra().toCheckGeneral());
//        if(orden.getLotes()!= null && !orden.getLotes().isEmpty()) {
//          if(sb.length()> 0)
//            sb.append(",");
//          sb.append(((OrdenCompra)this.getAdminOrden().getOrden()).toCheckIndividual(orden.getArticulos()));
//        } // if  
//        todos= ((OrdenCompra)this.getAdminOrden().getOrden()).toCheckTodos(orden.getArticulos());
//        if(!Cadena.isVacio(todos))
//          sb.append(sb.length()== 0? "": ",").append(todos);
//      } // if
//      else 
        sb.append(((OrdenCompra)this.getAdminOrden().getOrden()).toCheckPartidas(orden.getArticulos()));
      // FALTA VALIDAR AQUELLOS ARTICULOS QUE NO CORRESPONDE A NINGUN LOTE
      if(sb.length()> 0) {
        // sb.insert(0, "janal.show([{summary: 'Orden compra:', detail: 'SE SUGIERE CAMBIAR DE ORDINARIA A OTRO TIPO'},").append("]);");
        sb.insert(0, "janal.show([").append("]);");
        UIBackingUtilities.execute(sb.toString()); 
        JsfBase.addMessage("Existen articulos con errores en la orden de compra !", ETipoMensaje.ALERTA);
      } // if
      else {
  			transaccion= new Transaccion(orden);
	  		this.getAdminOrden().toAdjustArticulos();
        if (transaccion.ejecutar(this.accion)) {
          this.attrs.put("idOrdenCompra", ((OrdenCompra)this.getAdminOrden().getOrden()).getIdOrdenCompra());
          if(this.accion.equals(EAccion.AGREGAR)) {
            regresar = this.doCancelar();
            UIBackingUtilities.execute("jsArticulos.back('gener\\u00F3 orden de compra', '"+ ((OrdenCompra)this.getAdminOrden().getOrden()).getConsecutivo()+ "');");
          } // if	
          else
            this.getAdminOrden().toStartCalculate();
          if(!this.accion.equals(EAccion.CONSULTAR)) 
            JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" la orden de compra"), ETipoMensaje.INFORMACION);
          // VERIFICAR SI YA ESTAN COMPLETA LAS REQUISICIONES Y CAMBIARLES EL ESTATUS A COTIZADAS
          transaccion.ejecutar(EAccion.RESTAURAR);
          this.toLoadDetalle(Boolean.FALSE);
        } // if
        else 
          JsfBase.addMessage("Ocurrió un error al registrar la orden de compra", ETipoMensaje.ALERTA);      			
      } // else
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } 

  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idOrdenCompra", this.attrs.get("idOrdenCompra"));
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar
 
  public void doEraseArticulos() {
    this.getAdminOrden().getArticulos().clear();
    this.toCleanDetalles();
    if(this.getAdminOrden().getArticulos().size()> 0)
      this.getAdminOrden().toCalculate();
    ((OrdenCompra)this.getAdminOrden().getOrden()).toCleanPartidas();
  }

	private void toLoadTiposMediosPagos() {
		List<UISelectEntity> tiposMediosPagos= null;
		Map<String, Object>params            = new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, "id_cobro_caja=1");
			tiposMediosPagos= UIEntity.build("TcManticTiposMediosPagosDto", "row", params);
			this.attrs.put("tiposMediosPagos", tiposMediosPagos);
      if(!tiposMediosPagos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          ((OrdenCompra)this.getAdminOrden().getOrden()).setIkTipoMedioPago(tiposMediosPagos.get(0));
        else  
          ((OrdenCompra)this.getAdminOrden().getOrden()).setIkTipoMedioPago(tiposMediosPagos.get(tiposMediosPagos.indexOf(((OrdenCompra)this.getAdminOrden().getOrden()).getIkTipoMedioPago())));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	} 
  
	private void toLoadBancos() {
		List<UISelectEntity> bancos= null;
		Map<String, Object> params = new HashMap<>();
		List<Columna> columns      = new ArrayList<>();
		try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			bancos= UIEntity.seleccione("TcManticBancosDto", "row", params, columns, Constantes.SQL_TODOS_REGISTROS, "nombre");
			this.attrs.put("bancos", bancos);
      if(!bancos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          ((OrdenCompra)this.getAdminOrden().getOrden()).setIkBanco(bancos.get(0));
        else  
          ((OrdenCompra)this.getAdminOrden().getOrden()).setIkBanco(bancos.get(bancos.indexOf(((OrdenCompra)this.getAdminOrden().getOrden()).getIkBanco())));
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	} 
  
	private void toLoadTiposPagos() {
		List<UISelectEntity> tiposPagos= null;
		Map<String, Object>params      = new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			tiposPagos= UIEntity.seleccione("TcManticTiposPagosDto", "row", params, "nombre");
			this.attrs.put("tiposPagos", tiposPagos);
      if(!tiposPagos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          ((OrdenCompra)this.getAdminOrden().getOrden()).setIkTipoPago(tiposPagos.get(0));
        else  {
          int index= tiposPagos.indexOf(((OrdenCompra)this.getAdminOrden().getOrden()).getIkTipoPago());
          if(index>= 0)
            ((OrdenCompra)this.getAdminOrden().getOrden()).setIkTipoPago(tiposPagos.get(index));
          else
            ((OrdenCompra)this.getAdminOrden().getOrden()).setIkTipoPago(tiposPagos.get(0));
        } // if  
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	} 
  
	private void toLoadAlmacenistas() {
		List<UISelectEntity> almacenistas= null;
		Map<String, Object>params        = new HashMap<>();
		try {
 			params.put("campoLlave", "tc_mantic_personas.id_persona");
			params.put("idDesarrollo", ((OrdenCompra)this.getAdminOrden().getOrden()).getIdDesarrollo());
			params.put(Constantes.SQL_CONDICION, "tr_mantic_empresa_personal.id_puesto= 1");
  	  almacenistas= UIEntity.build("VistaContratosDto", "personalAsignado", params);
			this.attrs.put("almacenistas", almacenistas);
      if(!almacenistas.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          ((OrdenCompra)this.getAdminOrden().getOrden()).setIkAlmacenista(almacenistas.get(0));
        else { 
          int index= almacenistas.indexOf(((OrdenCompra)this.getAdminOrden().getOrden()).getIkAlmacenista());
          if(index>= 0)
            ((OrdenCompra)this.getAdminOrden().getOrden()).setIkAlmacenista(almacenistas.get(index));
          else
            ((OrdenCompra)this.getAdminOrden().getOrden()).setIkAlmacenista(almacenistas.get(0));
        } // if  
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	} 
  
	private void toLoadEmpresaTipoContacto() {
		List<UISelectEntity> empresaTiposContactos= null;
		Map<String, Object>params                 = new HashMap<>();
		try {
			params.put("idEmpresa", ((OrdenCompra)this.getAdminOrden().getOrden()).getIdEmpresa());
			params.put("idTipoContacto", "9, 10, 11, 15, 16, 17, 18");
			empresaTiposContactos= UIEntity.build("TrManticEmpresaTipoContactoDto", "tipos", params);
			this.attrs.put("empresaTiposContactos", empresaTiposContactos);
      if(!empresaTiposContactos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          ((OrdenCompra)this.getAdminOrden().getOrden()).setIkEmpresaTipoContacto(empresaTiposContactos.get(0));
        else {
          int index= empresaTiposContactos.indexOf(((OrdenCompra)this.getAdminOrden().getOrden()).getIkEmpresaTipoContacto());
          if(index>= 0)
            ((OrdenCompra)this.getAdminOrden().getOrden()).setIkEmpresaTipoContacto(empresaTiposContactos.get(index));
          else
            ((OrdenCompra)this.getAdminOrden().getOrden()).setIkEmpresaTipoContacto(empresaTiposContactos.get(0));
        } // if  
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	} 
 
	private void toLoadTipoOrden() {
		List<UISelectEntity> tiposOrdenes= null;
		Map<String, Object>params        = new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			tiposOrdenes= UIEntity.build("TcKeetTiposOrdenesDto", "row", params);
			this.attrs.put("tiposOrdenes", tiposOrdenes);
      if(!tiposOrdenes.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          ((OrdenCompra)this.getAdminOrden().getOrden()).setIkTipoOrden(tiposOrdenes.get(0));
        else {
          int index= tiposOrdenes.indexOf(((OrdenCompra)this.getAdminOrden().getOrden()).getIkTipoOrden());
          if(index>= 0)
            ((OrdenCompra)this.getAdminOrden().getOrden()).setIkTipoOrden(tiposOrdenes.get(index));
          else
            ((OrdenCompra)this.getAdminOrden().getOrden()).setIkTipoOrden(tiposOrdenes.get(0));
        } // if  
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} 
 
	public void doCheckTipoMedioPago() {
		Long tipoMedioPago= null;
		try {
      UIBackingUtilities.execute(
        "janal.renovate('contenedorGrupos\\\\:idBanco', {validaciones: 'libre', mascara: 'libre'});"
      );		
			tipoMedioPago= ((OrdenCompra)this.getAdminOrden().getOrden()).getIdTipoMedioPago();
			this.attrs.put("isBanco", !ETipoMediosPago.EFECTIVO.getIdTipoMedioPago().equals(tipoMedioPago));
      if(!ETipoMediosPago.EFECTIVO.getIdTipoMedioPago().equals(tipoMedioPago)) 
        UIBackingUtilities.execute(
          "janal.renovate('contenedorGrupos\\\\:idBanco', {validaciones: 'requerido', mascara: 'libre'});"
        );		
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} 
 
  @Override
  protected void toMoveData(UISelectEntity articulo, Integer index) throws Exception {  
    super.toMoveData(articulo, index);
    Articulo temporal         = this.getAdminOrden().getArticulos().get(index);
		Map<String, Object> params= new HashMap<>();
		try {
			if(articulo.size()> 1) {
				params.put("idArticulo", articulo.toLong("idArticulo"));
				params.put("idProveedor", ((OrdenCompra)this.getAdminOrden().getOrden()).getIdProveedor());
				params.put("idCliente", ((OrdenCompra)this.getAdminOrden().getOrden()).getIdCliente());
        // RECUPERAR EL PRECIO CONVENIO O EL PRECIO DE LISTA O EL PRECIO BASE
        Entity precios= (Entity)DaoFactory.getInstance().toEntity("VistaOrdenesComprasDto", "precios", params);
        if(precios!= null && !precios.isEmpty()) {
          temporal.setValor(precios.toDouble("precio"));
			    temporal.setCosto(precios.toDouble("precio"));
          temporal.setPrecio(precios.toDouble("precio"));
          this.getAdminOrden().toCalculate(index);
        } // if  
      } // if   
		} // try
		finally {
			Methods.clean(params);
		} // finally 
  }
 
	public String toColorGeneral(General row) {
    String regresar= (row.getDiferencia()< 0D) || (row.getTotal()!= 0D && row.getTotal()> row.getDiferencia())? "janal-tr-orange": "";
    String text= (String)this.attrs.get("textGlobal");
    if(!Cadena.isVacio(text))
      regresar= row.getCodigo().contains(text.toUpperCase()) || row.getNombre().contains(text.toUpperCase())? regresar: "janal-display-none";
		return regresar;
	} 
  
	public String toColorIndividual(Individual row) {
    String regresar= (row.getDiferencia()< 0D) || (row.getTotal()!= 0D && row.getTotal()> row.getDiferencia())? "janal-tr-orange": "";
    String text= (String)this.attrs.get("textIndividual");
    if(!Cadena.isVacio(text))
      regresar= row.getLote().contains(text.toUpperCase()) || row.getCodigo().contains(text.toUpperCase()) || row.getNombre().contains(text.toUpperCase()) ? regresar: "janal-display-none";
		return regresar;
	} 

  public void doUpdateIndividualTotal(Individual row) {
    if(Objects.equals(row.getCantidad(), Numero.redondearSat(row.getDiferencia()+ row.getTotal())))
      row.setModificado(Boolean.TRUE);
  }

  private void toLoadPartidas() {
    try {
      StringBuilder articulos= new StringBuilder();
      for (Articulo item: this.getAdminOrden().getArticulos()) {
        if(!Objects.equals(item.getIdArticulo(), -1L))
          articulos.append(item.getIdArticulo()).append(",");
      } // for
      StringBuilder lotes= new StringBuilder();
      Object[] items= (Object[])this.attrs.get("lotesSeleccion");
      for (Object item: items) {
        if(!Objects.equals(((UISelectEntity)item).getKey(), -1L))
          lotes.append(((UISelectEntity)item).getKey()).append(",");
      } // for
      if(articulos.length()> 0 && lotes.length()> 0)
        ((OrdenCompra)this.getAdminOrden().getOrden()).toLoadArticulos(articulos.substring(0, articulos.length()- 1), lotes.substring(0, lotes.length()- 1));
      else
        if(articulos.length()> 0)
          ((OrdenCompra)this.getAdminOrden().getOrden()).toLoadArticulos(articulos.substring(0, articulos.length()- 1));
 		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
  }
  
  @Override
	public void doCalculate(Integer index) {
		super.doCalculate(index);
 		if(index>= 0 && index< this.getAdminOrden().getArticulos().size()) 
      ((OrdenCompra)this.getAdminOrden().getOrden()).toLookArticulos(this.getAdminOrden().getArticulos().get(index));
	}	
 
  @Override
	public void doDeleteArticulo(Integer index) {
 		if(index>= 0 && index< this.getAdminOrden().getArticulos().size()) 
      ((OrdenCompra)this.getAdminOrden().getOrden()).toEraseArticulos(this.getAdminOrden().getArticulos().get(index));
    super.doDeleteArticulo(index);
  }
 
  private void toLoadDetalle(Boolean todos) {
		Map<String, Object> params= new HashMap<>();
    UISelectEntity residente  = (UISelectEntity)this.attrs.get("idResidente");
    StringBuilder sb          = new StringBuilder();
    try {
      params.put("sortOrder", "order by tc_mantic_requisiciones_detalles.id_requisicion_detalle");
      params.put("sucursales", ((OrdenCompra)this.getAdminOrden().getOrden()).getIdEmpresa());
      params.put("idDesarrollo", ((OrdenCompra)this.getAdminOrden().getOrden()).getIdDesarrollo());
      params.put("idContrato", ((OrdenCompra)this.getAdminOrden().getOrden()).getIdContrato());
      Periodo periodo= new Periodo();
      periodo.addMeses(-6);
      sb.append("(tc_mantic_requisiciones.id_requisicion_estatus in (2, 5)) and ");
      sb.append("(date_format(tc_mantic_requisiciones.registro, '%Y%m%d')>= '").append(periodo.toString()).append("') and ");
      if(!Objects.equals(residente.getKey(), -1L))
        sb.append("(tc_mantic_requisiciones.id_solicita= ").append(residente.getKey()).append(") and ");
      if(todos)
        sb.append(Constantes.SQL_VERDADERO).append(" and ");
      else  
        sb.append("(tc_keet_requisiciones_ordenes.id_requisicion_orden is null) and ");
      sb.delete(sb.length()- 4, sb.length());
      if(((OrdenCompra)this.getAdminOrden().getOrden()).isValid())
        sb.append(" or (tc_mantic_ordenes_compras.id_orden_compra= ").append(((OrdenCompra)this.getAdminOrden().getOrden()).getIdOrdenCompra()).append(")");
      params.put(Constantes.SQL_CONDICION, sb.toString());
      // and (tc_mantic_requisiciones.id_contrato= {idContrato} or {idContrato}= -1)
      // SE SOLICITO QUE NO SE VERIFIQUE POR CONTRATO LA BUSQUEDA DE LAS PARTIDAS 28/07/2024
      this.detalles= (List<Detalle>)DaoFactory.getInstance().toEntitySet(Detalle.class, "VistaRequisicionesDto", "detalle", params);
      if(Objects.equals(this.detalles, null))
        this.detalles= new ArrayList<>();
      else {
        for (Detalle item: this.detalles) {
          int index= ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().indexOf(item);
          if(index>= 0) {
            Detalle value= ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().get(index);
            item.setIdEliminado(value.getIdEliminado());
            item.setObservaciones(value.getObservaciones());
          } // if
        } // for
      } // else 
      UIBackingUtilities.resetDataTable("detalle");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally		
  }

  public void doLoadTodos() {
    this.toLoadDetalle(Objects.equals((Long)this.attrs.get("idTodos"), 1L));
  }
  
  public void doLoadResidentes() {
    this.toLoadDetalle(Boolean.TRUE);
  }
 
	private void toLoadResidentes() {
		List<UISelectEntity> residentes= null;
		Map<String, Object>params       = new HashMap<>();
		try {
      params.put("sucursales", ((OrdenCompra)this.getAdminOrden().getOrden()).getIdEmpresa());
			params.put("idDesarrollo", ((OrdenCompra)this.getAdminOrden().getOrden()).getIdDesarrollo());
  	  residentes= UIEntity.seleccione("VistaRequisicionesDto", "residentes", params, "cuenta");
			this.attrs.put("residentes", residentes);
			this.attrs.put("idResidente", UIBackingUtilities.toFirstKeySelectEntity(residentes));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	} 
  
  public void doQuitar(Detalle row) {
    try {      
      int position= this.getAdminOrden().getArticulos().indexOf(new Articulo(row.getIdArticulo()));
      if(position>= 0) {
        // DECREMENTAR EL VALOR DE CANTIDAD
        Double value= this.getAdminOrden().getArticulos().get(position).getCantidad()- row.getCantidad();
        this.getAdminOrden().getArticulos().get(position).setCantidad(value);
        int index= ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().indexOf(row);
        if(index>= 0) {
          Detalle item= ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().get(index);
          if(Objects.equals(item.getSql(), ESql.SELECT) || Objects.equals(item.getSql(), ESql.UPDATE)) {
            item.setSql(ESql.DELETE);
            item.setIdRequisicionDetalle(-1L);
          } // if  
          else  
            ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().remove(index);
        } // if  
        // SE DEBE DE ELIMINAR DE LA LISTA DE PARTIDAS
        if(value<= 0) 
          this.doDeleteArticulo(position);
        this.getAdminOrden().toCalculate();
      } // if	
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doAgregar(Detalle row) {
  	List<UISelectEntity> articulos= null;
    UISelectEntity articulo       = null;
    Integer index                 = this.getAdminOrden().getArticulos().size()- 1;
		List<Columna> columns         = new ArrayList<>();
    Map<String, Object> params    = new HashMap<>();
    try {
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			params.put("idAlmacen", JsfBase.getAutentifica().getEmpresa().getIdAlmacen());
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getDependencias());
  		params.put("idProveedor", this.attrs.get("proveedor")== null? -1L: ((UISelectEntity)this.attrs.get("proveedor")).getKey());
  		params.put("idArticulo", row.getIdArticulo());
  		params.put("codigo", "WXYZ");	
  		params.put("idArticuloTipo", Cadena.isVacio(this.attrs.get("idArticuloTipo"))? "1": this.attrs.get("idArticuloTipo"));	
      articulos= (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", "porNombre", params, columns, 20L);
      this.attrs.put("articulos", articulos);
      if(!Objects.equals(articulos, null) && !articulos.isEmpty()) {
        articulo= articulos.get(0);
        int position= this.getAdminOrden().getArticulos().indexOf(new Articulo(articulo.toLong("idArticulo")));
        if(articulo.size()> 1 && position>= 0) {
          if(index!= position) {
            // INCREMENTAR EL VALOR DE CANTIDAD
            Double value= this.getAdminOrden().getArticulos().get(position).getCantidad()+ row.getCantidad();
            this.getAdminOrden().getArticulos().get(position).setCantidad(value);
          } // if	
        } // if	
        else {
          articulo.getValue("cantidad").setData(row.getCantidad());
          this.toMoveData(articulo, index);
        } // else  
        
        // AGREGAR ESTA REQUISICION DETALLE A LA LISTA
        position= ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().indexOf(row);
        if(position>= 0) {
          Detalle value= ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().get(position);
          if(Objects.equals(value.getSql(), ESql.DELETE)) {
            value.setSql(ESql.UPDATE);
            value.setIdRequisicionDetalle(row.getIdRequisicionDetalle());
          } // if  
        } // if
        else {
          Detalle item= new Detalle();
          item.setSql(ESql.INSERT);
          item.setId(row.getIdRequisicionDetalle());
          item.setIdRequisicionDetalle(row.getIdRequisicionDetalle());
          item.setIdOrdenCompra(((OrdenCompra)this.getAdminOrden().getOrden()).getIdOrdenCompra());
          item.setIdArticulo(articulo.toLong("idArticulo"));
          item.setIdEliminado(2L);
          item.setIdUsuario(JsfBase.getIdUsuario());
          ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().add(item);
          this.attrs.put("articulo", null);
        } // if  
        this.getAdminOrden().toCalculate();
      } // if  
      else 
        this.attrs.put("articulo", null);
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
  
  public void doEliminar(Detalle row) {
    try {
      int position= ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().indexOf(row);
      if(position>= 0) {
        Detalle value= ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().get(position);
        if(!Objects.equals(value.getSql(), ESql.INSERT)) {
          value.setSql(ESql.UPDATE);
          value.setIdRequisicionDetalle(row.getIdRequisicionDetalle());
          value.setIdEliminado(1L);
        } // if  
      } // if
      else {
        Detalle item= new Detalle();
        item.setSql(ESql.INSERT);
        item.setId(row.getIdRequisicionDetalle());
        item.setIdRequisicionDetalle(row.getIdRequisicionDetalle());
        item.setIdOrdenCompra(((OrdenCompra)this.getAdminOrden().getOrden()).getIdOrdenCompra());
        item.setIdArticulo(row.getIdArticulo());
        item.setIdEliminado(1L);
        item.setIdUsuario(JsfBase.getIdUsuario());
        item.setObservaciones((String)this.attrs.get("comentarios"));
        ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().add(item);
      } // else 
      row.setIdEliminado(1L);
      row.setObservaciones((String)this.attrs.get("comentarios"));
      // this.attrs.put("comentarios", null);
      this.getAdminOrden().toCalculate();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  }
  
  public void doRecuperar(Detalle row) {
    try {
      int position= ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().indexOf(row);
      if(position>= 0) {
        Detalle value= ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().get(position);
        if(Objects.equals(value.getSql(), ESql.INSERT))
          ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().remove(position);
        else {
          value.setSql(ESql.DELETE);
          value.setIdRequisicionDetalle(-1L);
          value.setIdEliminado(2L);
        } // else 
        row.setIdEliminado(2L);
        this.getAdminOrden().toCalculate();
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  }
  
  public Boolean doView(Detalle row, Integer boton) {
    Boolean regresar= Objects.equals((Long)this.attrs.get("idTodos"), 2L);
    if(regresar) {
      Detalle existe= this.toFindPartida(row);
      switch(boton) {
        case 1: // eliminar
          regresar= Objects.equals(existe.getIdRequisicionDetalle(), -1L) && Objects.equals(row.getIdEliminado(), 2L);
          break;
        case 2: // recuperar
          regresar= Objects.equals(row.getIdEliminado(), 1L);
          break;
        case 3: // agregar
          regresar= Objects.equals(row.getIdEliminado(), 2L) && 
                  (
                    (Objects.equals(row.getFolio(), "") || Objects.equals(row.getIdOrdenCompra(), ((OrdenCompra)this.getAdminOrden().getOrden()).getIdOrdenCompra())) &&
                    (Objects.equals(existe.getIdRequisicionDetalle(), -1L))
                  );
          
          break;
        case 4: // quitar
          regresar= Objects.equals(row.getIdEliminado(), 2L) && 
                  (
                   (Objects.equals(row.getFolio(), "") || Objects.equals(row.getIdOrdenCompra(), ((OrdenCompra)this.getAdminOrden().getOrden()).getIdOrdenCompra())) &&
                   (!Objects.equals(existe.getIdRequisicionDetalle(), -1L))
                  );
          break;
      } // switch
    } // if  
    return regresar; 
  }
  
  private Detalle toFindPartida(Detalle row) {
    Detalle regresar= new Detalle();
    int index= ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().indexOf(row);
    if(index>= 0)
      regresar= ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().get(index);
    return regresar;
  } 
  
	public String toColor(Detalle row) {
		return !Objects.equals(row.getIdOrdenCompra(), -1L) && Objects.equals(row.getIdOrdenCompra(), ((OrdenCompra)this.getAdminOrden().getOrden()).getIdOrdenCompra())? "janal-tr-diferencias": "";
	} 
 
  private void toCleanDetalles() {
    int index= 0;
    while(index< ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().size()) {
      Detalle item= ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().get(index);
      if(Objects.equals(item.getSql(), ESql.INSERT))
        ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().remove(index);
      else {
        item.setSql(ESql.DELETE);
        index++;
      } // else
    } // while
  } 
  
  public void doMessage(Detalle row) {
    row.setObservaciones((String)this.attrs.get("comentarios"));
    int position= ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().indexOf(row);
    if(position>= 0) {
      Detalle value= ((OrdenCompra)this.getAdminOrden().getOrden()).getDetalles().get(position);
      value.setObservaciones((String)this.attrs.get("comentarios"));
    } // if 
  }
  
}