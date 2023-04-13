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
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.enums.EOpcionesResidente;
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
import mx.org.kaana.mantic.enums.ETipoMovimiento;
import mx.org.kaana.sakbe.suministros.reglas.Transaccion;
import mx.org.kaana.sakbe.suministros.beans.Suministro;
import mx.org.kaana.sakbe.db.dto.TcSakbeSuministrosBitacoraDto;
import mx.org.kaana.sakbe.enums.ECombustiblesEstatus;
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
      this.attrs.put("idSuministro", JsfBase.getFlashAttribute("idSuministro"));
      this.attrs.put("ikTipoCombustible", JsfBase.getFlashAttribute("ikTipoCombustible")== null? 1L: JsfBase.getFlashAttribute("ikTipoCombustible"));
      this.toLoadTiposCombustibles();
			this.toLoadCatalog();
      if(this.attrs.get("idSuministro")!= null) {
			  this.doLoad();
        this.attrs.put("idSuministro", null);
      } // if  
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
      columns.add(new Columna("fecha", EFormatoDinamicos.DIA_FECHA_HORA));      
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));      
      this.lazyModel= new FormatCustomLazy("VistaSuministrosDto", params, columns);
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
  
  public String doAccion(String accion) {
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
      Entity seleccionado= (Entity)this.attrs.get("seleccionado");
      this.attrs.put("idTipoCombustible", seleccionado.toLong("idTipoCombustible"));
			JsfBase.setFlashAttribute("accion", eaccion);		
      JsfBase.setFlashAttribute("opcionResidente", EOpcionesResidente.DIESEL);
      JsfBase.setFlashAttribute("idDesarrollo", seleccionado.toLong("idDesarrollo"));
      JsfBase.setFlashAttribute("idTipoCombustible", seleccionado.toLong("idTipoCombustible"));
      JsfBase.setFlashAttribute("idSuministro", seleccionado.toLong("idSuministro"));
      JsfBase.setFlashAttribute("seguimiento", "/Paginas/Sakbe/Suministros/visor");
      JsfBase.setFlashAttribute("porcentaje", this.toLoadCombustible());
      JsfBase.setFlashAttribute("retorno", this.toPagina().concat(Constantes.REDIRECIONAR));		
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
      params.put("idSuministro", seleccionado.getKey());
			transaccion= new Transaccion((Suministro)DaoFactory.getInstance().toEntity(Suministro.class, "TcSakbeSuministrosDto", "igual", params));
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
      params.put("idSuministro", seleccionado.getKey());
      Suministro suministro= (Suministro)DaoFactory.getInstance().toEntity(Suministro.class, "TcSakbeSuministrosDto", "igual", params);
			transaccion= new Transaccion(suministro);
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
		if(!Cadena.isVacio(this.attrs.get("idSuministro")) && !this.attrs.get("idSuministro").toString().equals("-1"))
  		sb.append("(tc_sakbe_suministros.id_suministro= ").append(this.attrs.get("idSuministro")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && !this.attrs.get("idDesarrollo").toString().equals("-1"))
  		sb.append("(tc_sakbe_suministros.id_desarrollo= ").append(this.attrs.get("idDesarrollo")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idMaquinaria")) && !this.attrs.get("idMaquinaria").toString().equals("-1"))
  		sb.append("(tc_sakbe_suministros.id_maquinaria=").append(this.attrs.get("idMaquinaria")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("consecutivo")))
  		sb.append("(tc_sakbe_suministros.consecutivo like '%").append(this.attrs.get("consecutivo")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("clave")))
  		sb.append("(tc_sakbe_maquinarias.clave like '%").append(((UISelectEntity)this.attrs.get("clave")).getKey()).append("%') and ");
		if(!Cadena.isVacio(this.fechaInicio))
		  sb.append("(date_format(tc_sakbe_suministros.registro, '%Y%m%d')>= '").append(this.fechaInicio.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.fechaTermino))
		  sb.append("(date_format(tc_sakbe_suministros.registro, '%Y%m%d')<= '").append(this.fechaTermino.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("litros")))
  		sb.append("(tc_sakbe_suministros.litros= '").append(this.attrs.get("litros")).append("') and ");
		if(!Cadena.isVacio(this.attrs.get("recibio")))
  		sb.append("(tc_sakbe_suministros.recibio like '%").append(((UISelectEntity)this.attrs.get("recibio")).getKey()).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("idSuministroEstatus")) && !this.attrs.get("idSuministroEstatus").toString().equals("-1"))
  		sb.append("(tc_sakbe_suministros.id_suministro_estatus= ").append(this.attrs.get("idSuministroEstatus")).append(") and ");
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
			params.put(Constantes.SQL_CONDICION, "id_suministro_estatus in (2,3,4)");
			columns= new ArrayList<>();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("catalogo", (List<UISelectEntity>) UIEntity.seleccione("TcSakbeSuministrosEstatusDto", "row", params, columns, "nombre"));
			this.attrs.put("idSuministroEstatus", new UISelectEntity("-1"));
      this.doLoadDesarrollos();
    } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}
	
	public void doLoadDesarrollos() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
  		params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);			
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("desarrollos", (List<UISelectEntity>) UIEntity.seleccione("TcKeetDesarrollosDto", "row", params, columns, "clave"));			
			this.attrs.put("idDesarrollo", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("desarrollos")));			
      this.doLoadMaquinarias();
    } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // doLoadDesarrollos	
  
	public void doLoadMaquinarias() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
  		params.put("idDesarrollo", this.attrs.get("idDesarrollo"));			
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("marca", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("maquinarias", (List<UISelectEntity>) UIEntity.seleccione("VistaSuministrosDto", "maquinarias", params, columns, "clave", Constantes.SQL_TODOS_REGISTROS));
			this.attrs.put("idMaquinaria", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("maquinarias")));			
    } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
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
		Map<String, Object>params    = new HashMap<>();
		List<UISelectItem> allEstatus= null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			params.put(Constantes.SQL_CONDICION, "id_suministro_estatus in (".concat(seleccionado.toString("estatusAsociados")).concat(")"));
			allEstatus= UISelect.build("TcSakbeSuministrosEstatusDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);			
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
		Transaccion transaccion               = null;
    TcSakbeSuministrosBitacoraDto bitacora= null;
		Entity seleccionado       = null;
	  Map<String, Object> params= new HashMap<>();	
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
      params.put("idSuministro", seleccionado.toMap());
			Suministro orden= (Suministro)DaoFactory.getInstance().toEntity(Suministro.class, "TcSakbeSuministrosDto", "igual", params);
  	  bitacora= new TcSakbeSuministrosBitacoraDto((String)this.attrs.get("justificacion"), JsfBase.getIdUsuario(), -1L, Long.valueOf((String)this.attrs.get("estatus")), seleccionado.getKey());
			transaccion = new Transaccion(orden, bitacora);
			if(transaccion.ejecutar(EAccion.JUSTIFICAR)) {
				JsfBase.addMessage("Cambio estatus", "Se realizo el cambio de estatus de forma correcta", ETipoMensaje.INFORMACION);
      } // if  
			else
				JsfBase.addMessage("Cambio estatus", "Ocurrio un error al realizar el cambio de estatus", ETipoMensaje.ERROR);
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
    this.attrs.put("suministro", seleccionado.clone());
    return this.doSuministros();
  }
  
  public String doSuministros() {
    String regresar= null;
    try {      
      JsfBase.setFlashAttribute("seguimiento", "/Paginas/Sakbe/Suministros/visor");
      JsfBase.setFlashAttribute("ikTipoCombustible", ((UISelectEntity)this.attrs.get("idTipoCombustible")).getKey());
      JsfBase.setFlashAttribute("retorno", "/Paginas/Sakbe/Suministros/visor");		
      regresar= "/Paginas/Sakbe/Combustibles/desarrollos.jsf".concat(Constantes.REDIRECIONAR).concat("&opcion=52df68e378f074");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
		return regresar;
	}
  
  public String doLubricantes(Entity seleccionado) {
    this.attrs.put("suministro", seleccionado.clone());
    return this.doSuministros();
  }
  
  public String Lubricantes() {
    String regresar= null;
    try {      
      JsfBase.setFlashAttribute("seguimiento", "/Paginas/Sakbe/Suministros/visor");
      JsfBase.setFlashAttribute("ikTipoCombustible", ((UISelectEntity)this.attrs.get("idTipoCombustible")).getKey());
      JsfBase.setFlashAttribute("retorno", "/Paginas/Sakbe/Suministros/visor");		
      regresar= "/Paginas/Sakbe/Combustibles/desarrollos.jsf".concat(Constantes.REDIRECIONAR).concat("&opcion=aabf54d864e06de06ef006");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
		return regresar;
	}
  
  private Entity toLoadCombustible() throws Exception {
    Entity regresar           = null;
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idTipoCombustible", this.attrs.get("ikTipoCombustible"));      
      params.put("disponibles", ECombustiblesEstatus.ACEPTADO.getKey()+ ","+ ECombustiblesEstatus.EN_PROCESO.getKey());      
      regresar= (Entity)DaoFactory.getInstance().toEntity("VistaCombustiblesDto", "litros", params);
      if(regresar== null || regresar.isEmpty()) {
        regresar= new Entity(-1L);
        regresar.put("idTipoInsumo", new Value("idTipoInsumo", ((UISelectEntity)this.attrs.get("idTipoCombustible")).toLong("idTipoInsumo")));
        regresar.put("saldo", new Value("saldo", 0D));
        regresar.put("litros", new Value("litros", 0D));
        regresar.put("tickets", new Value("tickets", 0D));
      } // if
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

	public String doMovimientos() {
		JsfBase.setFlashAttribute("tipo", ETipoMovimiento.SUMINISTROS);
		JsfBase.setFlashAttribute(ETipoMovimiento.SUMINISTROS.getIdKey(), ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("regreso", "/Paginas/Sakbe/Suministros/filtro");
		return "/Paginas/Mantic/Compras/Ordenes/movimientos".concat(Constantes.REDIRECIONAR);
	}

	private void toLoadTiposCombustibles() throws Exception {
		List<UISelectEntity> tiposCombustibles= null;
		Map<String, Object>params             = new HashMap<>();
		try {
			params.put("idTipoInsumo", "1,2,3,4");
			tiposCombustibles= UIEntity.build("TcSakbeTiposCombustiblesDto", "grupo", params);
			this.attrs.put("tiposCombustibles", tiposCombustibles);
      if(!tiposCombustibles.isEmpty()) 
  			this.attrs.put("idTipoCombustible", tiposCombustibles.get(0));
      else  
  			this.attrs.put("idTipoCombustible", new UISelectEntity(-1L));
      this.doLoadPorcentajes();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // toLoadTiposCombustibles 
  
  public void doLoadPorcentajes() {
    try {
      List<UISelectEntity> insumos= (List<UISelectEntity>) this.attrs.get("tiposCombustibles");
      if (insumos!= null && !insumos.isEmpty()) {
        int index = insumos.indexOf((UISelectEntity) this.attrs.get("idTipoCombustible"));
        if (index >= 0) 
          this.attrs.put("idTipoCombustible", insumos.get(index));
      } // if
      this.attrs.put("ikTipoCombustible", ((UISelectEntity)this.attrs.get("idTipoCombustible")).getKey());
      this.attrs.put("porcentaje", this.toLoadCombustible());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
}
