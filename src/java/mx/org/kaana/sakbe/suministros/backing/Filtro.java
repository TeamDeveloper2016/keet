package mx.org.kaana.sakbe.suministros.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.sakbe.combustibles.reglas.Transaccion;
import mx.org.kaana.sakbe.combustibles.beans.Combustible;
import mx.org.kaana.sakbe.db.dto.TcSakbeCombustiblesBitacoraDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;

@Named(value = "sakbeSuministrosFiltro")
@ViewScoped 
public class Filtro extends IBaseFilter implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Filtro.class);
  private static final long serialVersionUID= 8793667741599428338L;
	private LocalDate fechaInicio;
	private LocalDate fechaTermino;
  
  public LocalDate getFechaInicio() {
    return fechaInicio;
  }

  public void setFechaInicio(LocalDate fechaInicio) {
    this.fechaInicio = fechaInicio;
  }

  public LocalDate getFechaTermino() {
    return fechaTermino;
  }

  public void setFechaTermino(LocalDate fechaTermino) {
    this.fechaTermino = fechaTermino;
  }
	
  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.attrs.put("idCombustible", JsfBase.getFlashAttribute("idCombustible"));
      this.attrs.put("idSuministro", JsfBase.getFlashAttribute("idSuministro"));
			this.toLoadCatalog();
      if(this.attrs.get("idSuministro")!= null) 
			  this.doLoad();
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
      params.put("sortOrder", "order by tc_sakbe_suministros.registro desc");
      columns = new ArrayList<>();
      columns.add(new Columna("desarrollo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("recibio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("litros", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("horas", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));      
      this.lazyModel = new FormatCustomLazy("VistaCombustiblesDto", "suministros", params, columns);
			this.attrs.put("idSuministro", null);
      this.reset();
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

  protected void reset() {
    UIBackingUtilities.resetDataTable();
  }
  
  public String doModificar(Entity seleccionado) {
    this.attrs.put("seleccionado", seleccionado);
    return this.doAccion("MODIFICAR");
  }
  
  public String doAccion(String accion) {
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("retorno", this.toPagina());		
			JsfBase.setFlashAttribute("idCombustible", eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR)? ((Entity)this.attrs.get("seleccionado")).getKey() : -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return "/Paginas/Sakbe/Suministros/accion".concat(Constantes.REDIRECIONAR);
  } // doAccion  
	
  public String toPagina() {
    return "/Paginas/Sakbe/Suministros/filtro";
  }
  
  public void doEliminar(Entity seleccionado) {
    this.attrs.put("seleccionado", seleccionado);
    this.doEliminar();
  }
  
  public void doEliminar() {
		Transaccion transaccion   = null;
		Entity seleccionado       = null;
	  Map<String, Object> params= new HashMap<>();	
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
      params.put("idCombustible", seleccionado.getKey());
			transaccion= new Transaccion((Combustible)DaoFactory.getInstance().toEntity(Combustible.class, "TcSakbeCombustiblesDto", "igual", params));
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar", "El suministro se ha eliminado correctamente", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("Eliminar", "Ocurrió un error al eliminar el suministro", ETipoMensaje.ALERTA);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
    finally {
      Methods.clean(params);
    } // finally
  } // doEliminar

  public void doRecuperar(Entity seleccionado) {
		Transaccion transaccion   = null;
	  Map<String, Object> params= new HashMap<>();	
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
      params.put("idCombustible", seleccionado.getKey());
      Combustible combustible= (Combustible)DaoFactory.getInstance().toEntity(Combustible.class, "TcSakbeCombustiblesDto", "igual", params);
			transaccion= new Transaccion(combustible);
			if(transaccion.ejecutar(EAccion.COMPLEMENTAR))
				JsfBase.addMessage("Eliminar", "El suministro se recuperó correctamente", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("Eliminar", "Ocurrió un error al recuperar el suministro", ETipoMensaje.ALERTA);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
    finally {
      Methods.clean(params);
    } // finally
  } // doRecuperar

	protected Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb= new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idCombustible")) && !this.attrs.get("idCombustible").toString().equals("-1"))
  		sb.append("(tc_sakbe_combustibles.id_combustible=").append(this.attrs.get("idCombustible")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("consecutivo")))
  		sb.append("(tc_sakbe_combustibles.consecutivo like '%").append(this.attrs.get("consecutivo")).append("%') and ");
		if(!Cadena.isVacio(this.fechaInicio))
		  sb.append("(date_format(tc_sakbe_combustibles.fecha, '%Y%m%d')>= '").append(this.fechaInicio.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.fechaTermino))
		  sb.append("(date_format(tc_sakbe_combustibles.fecha, '%Y%m%d')<= '").append(this.fechaTermino.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("ticket")))
  		sb.append("(tc_sakbe_combustibles.ticket=").append(((UISelectEntity)this.attrs.get("ticket")).getKey()).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("lugar")))
  		sb.append("(tc_sakbe_combustibles.lugar like '%").append(this.attrs.get("lugar")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("idCombustibleEstatus")) && !this.attrs.get("idCombustibleEstatus").toString().equals("-1"))
  		sb.append("(tc_sakbe_combustibles.id_combustible_estatus= ").append(this.attrs.get("idCombustibleEstatus")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
		  regresar.put("idEmpresa", this.attrs.get("idEmpresa"));
		else
		  regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	}
	
	protected void toLoadCatalog() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("catalogo", (List<UISelectEntity>) UIEntity.seleccione("TcSakbeSuministrosEstatusDto", "row", params, columns, "nombre"));
			this.attrs.put("idSuministroEstatus", new UISelectEntity("-1"));
      this.doLoadDesarrollos();
    } // try
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
  		params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);			
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("desarrollos", (List<UISelectEntity>) UIEntity.seleccione("TcKeetDesarrollosDto", "row", params, columns, "clave"));			
			this.attrs.put("idDesarrollo", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("desarrollos")));			
      this.doLoadMaquinarias();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // doLoadDesarrollos	
  
	public void doLoadMaquinarias() {
		List<Columna> columns     = null;
    Map<String, Object> params= null;
    try {
			params= new HashMap<>();	
  		params.put("idDesarrollo", this.attrs.get("idDesarrollo"));			
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("marca", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("maquinarias", (List<UISelectEntity>) UIEntity.seleccione("VistaCombustiblesDto", "maquinarias", params, columns, "clave"));			
			this.attrs.put("idMaquinaria", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("maquinarias")));			
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // doLoadMaquinarias

	public List<UISelectEntity> doCompleteCombustible(String ticket) {
		List<UISelectEntity> regresar= null;		
		Map<String, Object> params   = new HashMap<>();
		List<Columna> columns        = null;		
		try {			
			columns= new ArrayList<>();
			columns.add(new Columna("consecutivo", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("fecha", EFormatoDinamicos.FECHA_CORTA));
			columns.add(new Columna("litros", EFormatoDinamicos.MILES_SAT_DECIMALES));
			params.put("consecutivo", ticket);
			regresar= UIEntity.build("VistaCombustiblesDto", "combustibles", params, columns, 30L);						
      this.attrs.put("combustibles", regresar);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(columns);
			Methods.clean(params);
		} // finally
		return regresar;
	} // doCompleteCombustible
  
	public void doAsignaCombustible(SelectEvent event) {
		UISelectEntity seleccionado = null;
		List<UISelectEntity> codigos= null;
		try {
			codigos = (List<UISelectEntity>) this.attrs.get("combustibles");
			seleccionado= codigos.get(codigos.indexOf((UISelectEntity)event.getObject()));
			this.attrs.put("idCombustible", seleccionado);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doAsignaCombustible
  
	public void doLoadEstatus() {
		Entity seleccionado          = null;
		Map<String, Object>params    = null;
		List<UISelectItem> allEstatus= null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_combustible_estatus in (".concat(seleccionado.toString("estatusAsociados")).concat(")"));
			allEstatus= UISelect.build("TcSakbeCombustiblesEstatusDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);			
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
	
	public void doActualizarEstatus() {
		Transaccion transaccion                = null;
    TcSakbeCombustiblesBitacoraDto bitacora= null;
		Entity seleccionado       = null;
	  Map<String, Object> params= new HashMap<>();	
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
      params.put("idCombustible", seleccionado.toMap());
			Combustible orden= (Combustible)DaoFactory.getInstance().toEntity(Combustible.class, "TcSakbeCombustiblesDto", "igual", params);
  	  bitacora= new TcSakbeCombustiblesBitacoraDto((String) this.attrs.get("justificacion"), -1L, JsfBase.getIdUsuario(), Long.valueOf((String)this.attrs.get("estatus")), seleccionado.getKey());
			transaccion = new Transaccion(orden, bitacora);
			if(transaccion.ejecutar(EAccion.JUSTIFICAR)) {
				JsfBase.addMessage("Cambio estatus", "Se realizo el cambio de estatus de forma correcta.", ETipoMensaje.INFORMACION);
      } // if  
			else
				JsfBase.addMessage("Cambio estatus", "Ocurrio un error al realizar el cambio de estatus.", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally{
			this.attrs.put("justificacion", "");
			Methods.clean(params);
		} // finally
	}	// doActualizaEstatus
	
	public void doGlobalEvent(Boolean isViewException) {
		LOG.error("ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA ["+ isViewException+ "]");
	}

  public String doSuministros(Entity seleccionado) {
    this.attrs.put("seleccionado", seleccionado);
    return this.doSuministros();
  }
  
  public String doSuministros() {
		JsfBase.setFlashAttribute("idCombustible", this.attrs.get("idCombustible"));
		return "/Paginas/Sakbe/Suministros/filtro".concat(Constantes.REDIRECIONAR);
	}
  
	@Override
	protected void finalize() throws Throwable {
    super.finalize();
	}	// finalize
  
}
