package mx.org.kaana.keet.compras.requisiciones.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.compras.requisiciones.beans.RegistroRequisicion;
import mx.org.kaana.keet.compras.requisiciones.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.requisiciones.beans.Requisicion;
import mx.org.kaana.mantic.db.dto.TcManticRequisicionesBitacoraDto;

@Named(value= "keetComprasRequisicionesFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {  

	private static final long serialVersionUID = 2808389228136682363L;
	private LocalDate fechaInicio;
	private LocalDate fechaFin;

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}
		
  @PostConstruct
  @Override
  protected void init() {
    try {
			this.fechaInicio= LocalDate.of(Fecha.getAnioActual(), 1, 1);
			this.fechaFin= LocalDate.of(Fecha.getAnioActual(), Fecha.getMesActual()+1, 15);
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.attrs.put("idRequisicion", JsfBase.getFlashAttribute("idRequisicion"));
      this.attrs.put("sortOrder", "order by tc_mantic_requisiciones.id_empresa, tc_mantic_requisiciones.ejercicio, tc_mantic_requisiciones.orden");
			loadCatalog();      
			if(!Cadena.isVacio(this.attrs.get("idRequisicion")))
				doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
 
	private void loadCatalog() {		
    try {			
			this.loadEmpresas();
			this.doLoadDesarrollos();			
			this.loadEjercicios();
    } // try
    catch (Exception e) {
      throw e;
    } // catch       
	} // loadCatalog
	
	private void loadEmpresas() {
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
			columns.clear();
      this.attrs.put("estatusFiltro", (List<UISelectEntity>) UIEntity.seleccione("TcManticRequisicionesEstatusDto", "row", params, columns, "nombre"));
			this.attrs.put("idRequisicionEstatus", new UISelectEntity("-1"));
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} // loadCatalog
	
	public void doLoadDesarrollos() {
		List<Columna> columns     = null;
    Map<String, Object> params= null;
		UISelectEntity empresa    = null;
    try {
			params= new HashMap<>();			
			empresa= (UISelectEntity) this.attrs.get("idEmpresa");
			if(empresa.getKey()>= 1L)
        params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa=" + empresa.getKey());
			else
				params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa in (" + JsfBase.getAutentifica().getEmpresa().getSucursales() + ")");			
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("desarrollos", (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave"));			
			this.attrs.put("desarrollo", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("desarrollos")));			
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // loadEmpresas	
	
	private void loadEjercicios(){		
		List<UISelectItem> ejercicios= null;
		List<UISelectItem> semanas   = null;
		try {						
			ejercicios= UISelect.seleccione("TcKeetGastosDto", "ejercicios", Collections.EMPTY_MAP, "ejercicio", EFormatoDinamicos.MAYUSCULAS);			
			this.attrs.put("ejercicios", ejercicios);
			this.attrs.put("ejercicio", UIBackingUtilities.toFirstKeySelectItem(ejercicios));
			semanas= new ArrayList<>();
			for(int count=0; count<53; count++)
				semanas.add(new UISelectItem(new Long(count+1), "Semana ".concat(String.valueOf(count+1))));
			semanas.add(0, new UISelectItem(-1L, "SELECCIONE"));
			this.attrs.put("semanas", semanas);
			this.attrs.put("semana", UIBackingUtilities.toFirstKeySelectItem(semanas));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doLoadEstatus	
	
	public List<UISelectEntity> doCompleteArticuloFiltro(String query) {		
		this.attrs.put("codigoFiltro", query);
    this.doUpdateArticulosFiltro();
		return (List<UISelectEntity>)this.attrs.get("articulosFiltro");
	}	// doCompleteArticulo
	
	public void doUpdateArticulosFiltro() {
		List<Columna> columns         = null;
    Map<String, Object> params    = null;
		List<UISelectEntity> articulos= null;
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			params= new HashMap<>();
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", -1L);
			String search= (String) this.attrs.get("codigoFiltro"); 
			if(!Cadena.isVacio(search)) 
  			search= search.replaceAll(Constantes.CLEAN_SQL, "").trim().toUpperCase().replaceAll("(,| |\\t)+", ".*.*");			
			else
				search= "WXYZ";
  		params.put("codigo", search);			        
      articulos= (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", "porNombreTipoArticulo", params, columns, 40L);
      this.attrs.put("articulosFiltro", articulos);
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}	// doUpdateArticulos
	
	public List<UISelectEntity> doCompleteProveedor(String codigo) {
 		List<Columna> columns     = null;
    Map<String, Object> params= null;
		boolean buscaPorCodigo    = false;
    try {
			params= new HashMap<>();
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));			
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			if(!Cadena.isVacio(codigo)) {
  			codigo= codigo.replaceAll(Constantes.CLEAN_SQL, "").trim();
				buscaPorCodigo= codigo.startsWith(".");
				if(buscaPorCodigo)
					codigo= codigo.trim().substring(1);
				codigo= codigo.toUpperCase().replaceAll("(,| |\\t)+", ".*.*");
			} // if	
			else
				codigo= "WXYZ";
  		params.put("codigo", codigo);
			if(buscaPorCodigo)
        this.attrs.put("proveedores", UIEntity.build("TcManticProveedoresDto", "porCodigo", params, columns, 40L));
			else
        this.attrs.put("proveedores", UIEntity.build("TcManticProveedoresDto", "porNombre", params, columns, 40L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
		return (List<UISelectEntity>)this.attrs.get("proveedores");
	}	// doCompleteProveedor
	
  @Override
  public void doLoad() {
    List<Columna> columns     = null;
		Map<String, Object> params= this.toPrepare();
    try {
      columns = new ArrayList<>();
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));      
      columns.add(new Columna("fechaEntregada", EFormatoDinamicos.FECHA_CORTA));      
      columns.add(new Columna("fechaPedido", EFormatoDinamicos.FECHA_CORTA));      
      this.lazyModel = new FormatCustomLazy("VistaRequisicionesDesarrolloDto", params, columns);
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

	protected Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb= new StringBuilder();
		sb.append("(date_format(tc_mantic_requisiciones.fecha_pedido, '%Y%m%d')>= date_format('").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, this.fechaInicio)).append("', '%Y%m%d')) and ");					
		sb.append("(date_format(tc_mantic_requisiciones.fecha_entregada, '%Y%m%d')<= date_format('").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, this.fechaFin)).append("', '%Y%m%d')) and ");							
		if(!Cadena.isVacio(this.attrs.get("idRequisicion")) && !this.attrs.get("idRequisicion").toString().equals("-1")){
  		sb.append("(tc_mantic_requisiciones.id_requisicion=").append(this.attrs.get("idRequisicion")).append(") and ");
			this.attrs.put("idRequisicion", null);
		} // if
		if(!Cadena.isVacio(this.attrs.get("consecutivo")))
  		sb.append("(tc_mantic_requisiciones.consecutivo like '%").append(this.attrs.get("consecutivo")).append("%') and ");		
		UISelectEntity estatus= (UISelectEntity) this.attrs.get("idRequisicionEstatus");
		if(estatus!= null && !estatus.getKey().equals(-1L))
  		sb.append("(tc_mantic_requisiciones.id_requisicion_estatus= ").append(estatus.getKey()).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !((UISelectEntity)this.attrs.get("idEmpresa")).getKey().equals(-1L))
		  sb.append("(tc_mantic_requisiciones.id_empresa= ").append(this.attrs.get("idEmpresa")).append(") and ");		
		if(!Cadena.isVacio(this.attrs.get("desarrollo")) && !((UISelectEntity)this.attrs.get("desarrollo")).getKey().equals(-1L))
		  sb.append("(tc_mantic_requisiciones.id_desarrollo= ").append(((UISelectEntity)this.attrs.get("desarrollo")).getKey()).append(") and ");		
		if(!Cadena.isVacio(this.attrs.get("ejercicio")) && !this.attrs.get("ejercicio").equals("-1"))
		  sb.append("(tc_mantic_requisiciones.ejercicio= '").append(this.attrs.get("ejercicio")).append("') and ");		
		if(this.attrs.get("nombre")!= null && ((UISelectEntity)this.attrs.get("nombre")).getKey()> 0L) 
			sb.append("tc_mantic_requisiciones_detalles.id_articulo=").append(((UISelectEntity)this.attrs.get("nombre")).getKey()).append(" and ");						
		else if(!Cadena.isVacio(JsfBase.getParametro("nombre_input"))) { 
			String nombre= JsfBase.getParametro("nombre_input").replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*");
			sb.append("(tc_mantic_requisiciones_detalles.nombre regexp '.*").append(nombre).append(".*' or tc_mantic_requisiciones_detalles.nombre regexp '.*").append(nombre).append(".*') and ");				
		} // if	
		if(this.attrs.get("proveedor")!= null && ((UISelectEntity)this.attrs.get("proveedor")).getKey()> 0L)
			sb.append("(tc_mantic_proveedores.razon_social like '%").append(((List<UISelectEntity>)this.attrs.get("proveedores")).get(((List<UISelectEntity>)this.attrs.get("proveedores")).indexOf(((UISelectEntity)this.attrs.get("proveedor")))).toString("razonSocial")).append("%') and ");
		else
 		  if(!Cadena.isVacio(JsfBase.getParametro("razonSocial_input")))
			  sb.append("(tc_mantic_proveedores.razon_social like '%").append(JsfBase.getParametro("razonSocial_input")).append("%') and ");
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	} // toPrepare	
	
	public void doLoadEstatus(){
		Entity seleccionado          = null;
		Map<String, Object>params    = null;
		List<UISelectItem> allEstatus= null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_requisicion_estatus in (".concat(seleccionado.toString("estatusAsociados")).concat(")"));
			allEstatus= UISelect.build("TcManticRequisicionesEstatusDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);			
			this.attrs.put("allEstatus", allEstatus);
			this.attrs.put("estatus", allEstatus.get(0));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadEstatus
	
  public String doAccion(String accion) {
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Compras/Requisiciones/filtro");		
			JsfBase.setFlashAttribute("idRequisicion", eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR) ? ((Entity)this.attrs.get("seleccionado")).getKey() : -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return "/Paginas/Keet/Compras/Requisiciones/accion".concat(Constantes.REDIRECIONAR);
  } // doAccion  
	
  public void doEliminar() {
		Transaccion transaccion        = null;
		Entity seleccionado            = null;
		RegistroRequisicion requisicion= null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
			requisicion= new RegistroRequisicion(new Requisicion(seleccionado.getKey()));
			transaccion= new Transaccion(requisicion, this.attrs.get("justificacionEliminar").toString());
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar", "La requisición de compra se ha eliminado correctamente.", ETipoMensaje.ERROR);
			else
				JsfBase.addMessage("Eliminar", "Ocurrió un error al eliminar la requisicion de compra.", ETipoMensaje.ERROR);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
  } // doEliminar	
	
	public void doActualizarEstatus() {
		Transaccion transaccion                  = null;
		TcManticRequisicionesBitacoraDto bitacora= null;
		Entity seleccionado                      = null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			bitacora= new TcManticRequisicionesBitacoraDto(-1L, (String)this.attrs.get("justificacion"), JsfBase.getIdUsuario(), Long.valueOf(this.attrs.get("estatus").toString()), seleccionado.getKey());
			transaccion= new Transaccion(bitacora);
			if(transaccion.ejecutar(EAccion.JUSTIFICAR))
				JsfBase.addMessage("Cambio estatus", "Se realizo el cambio de estatus de forma correcta", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("Cambio estatus", "Ocurrio un error al realizar el cambio de estatus", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally {
			this.attrs.put("justificacion", "");
		} // finally
	}	// doActualizaEstatus
}
