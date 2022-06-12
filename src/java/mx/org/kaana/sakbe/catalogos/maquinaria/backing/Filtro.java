package mx.org.kaana.sakbe.catalogos.maquinaria.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.event.ValueChangeEvent;
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
import mx.org.kaana.sakbe.catalogos.maquinaria.beans.Maquinaria;
import mx.org.kaana.sakbe.catalogos.maquinaria.reglas.Transaccion;
import mx.org.kaana.sakbe.db.dto.TcSakbeMaquinariasBitacoraDto;
import mx.org.kaana.sakbe.db.dto.TrSakbeMaquinariaDesarrolloDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;

@Named(value = "sakbeCatalogosMaquinariaFiltro")
@ViewScoped 
public class Filtro extends IBaseFilter implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Filtro.class);
  private static final long serialVersionUID= 8793667741599428338L;

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.attrs.put("idMaquinaria", JsfBase.getFlashAttribute("idMaquinaria"));
      this.attrs.put("obras", this.toLoadObras());
			this.toLoadCatalogos();
      if(this.attrs.get("idMaquinaria")!= null) {
			  this.doLoad();
        this.attrs.put("idMaquinaria", null);
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
      params.put("sortOrder", "order by tc_sakbe_maquinarias.registro desc");
      columns = new ArrayList<>();
      columns.add(new Columna("desarrollo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("placa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("litros", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_SAT_DECIMALES));      
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));      
      this.lazyModel= new FormatCustomLazy("VistaMaquinariasDto", params, columns);
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
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("idMaquinaria", eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR)? ((Entity)this.attrs.get("seleccionado")).getKey() : -1L);
      JsfBase.setFlashAttribute("retorno", "/Paginas/Sakbe/Catalogos/Maquinaria/filtro".concat(Constantes.REDIRECIONAR));		
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return "/Paginas/Sakbe/Catalogos/Maquinaria/accion".concat(Constantes.REDIRECIONAR);
  } // doAccion  
	
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
      params.put("idMaquinaria", seleccionado.getKey());
			transaccion= new Transaccion((Maquinaria)DaoFactory.getInstance().toEntity(Maquinaria.class, "TcSakbeMaquinariasDto", "igual", params));
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar", "La maquinaria se ha eliminado correctamente", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("Eliminar", "Ocurrió un error al eliminar la maquinaria", ETipoMensaje.ALERTA);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
    finally {
      Methods.clean(params);
    } // finally
  } // doEliminar

	protected Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb= new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idMaquinaria")) && !this.attrs.get("idMaquinaria").toString().equals("-1"))
  		sb.append("(tc_sakbe_maquinarias.id_maquinaria= ").append(this.attrs.get("idMaquinaria")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && !this.attrs.get("idDesarrollo").toString().equals("-1"))
  		sb.append("(tc_keet_desarrollos.id_desarrollo= ").append(this.attrs.get("idDesarrollo")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("clave")))
  		sb.append("(tc_sakbe_maquinarias.clave like '%").append(((UISelectEntity)this.attrs.get("clave")).getKey()).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("placa")))
  		sb.append("(tc_sakbe_maquinarias.placa like '%").append(((UISelectEntity)this.attrs.get("placa")).getKey()).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("litros")))
  		sb.append("(tc_sakbe_maquinarias.litros= '").append(this.attrs.get("litros")).append("') and ");
		if(!Cadena.isVacio(this.attrs.get("marca")))
  		sb.append("(tc_sakbe_maquinarias.marca like '%").append(((UISelectEntity)this.attrs.get("marca")).getKey()).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("serie")))
  		sb.append("(tc_sakbe_maquinarias.serie like '%").append(((UISelectEntity)this.attrs.get("serie")).getKey()).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("idTipoMaquinaria")) && !this.attrs.get("idTipoMaquinaria").toString().equals("-1"))
  		sb.append("(tc_sakbe_maquinarias.id_tipo_maquinaria= ").append(this.attrs.get("idTipoMaquinaria")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idMaquinariaGrupo")) && !this.attrs.get("idMaquinariaGrupo").toString().equals("-1"))
  		sb.append("(tc_sakbe_tipos_maquinarias.id_maquinaria_grupo= ").append(this.attrs.get("idMaquinariaGrupo")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idMaquinariaEstatus")) && !this.attrs.get("idMaquinariaEstatus").toString().equals("-1"))
  		sb.append("(tc_sakbe_maquinarias.id_maquinaria_estatus= ").append(this.attrs.get("idMaquinariaEstatus")).append(") and ");
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
	
	protected void toLoadCatalogos() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			columns= new ArrayList<>();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("catalogo", (List<UISelectEntity>) UIEntity.seleccione("TcSakbeMaquinariasEstatusDto", "row", params, columns, "nombre"));
			this.attrs.put("idMaquinariaEstatus", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("catalogo")));
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
      this.toLoadTiposCombustibles();
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
  
	private List<String> toLoadObras() {
    List<String> regresar     = new ArrayList<>();
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
  		params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);			
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      List<UISelectEntity> desarrollos= (List<UISelectEntity>) UIEntity.build("TcKeetDesarrollosDto", "row", params, columns);
			if(desarrollos!= null && !desarrollos.isEmpty())
        for (UISelectEntity item : desarrollos) {
          regresar.add(item.getKey()+ " ".concat(Constantes.SEPARADOR).concat(" ").concat(item.toString("nombres")));
        } // for
    } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
    return regresar;
	} // doLoadObras	
  
	private void toLoadTiposCombustibles() {
		List<UISelectEntity> tiposCombustibles= null;
		Map<String, Object>params             = new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			tiposCombustibles= UIEntity.build("TcSakbeTiposCombustiblesDto", "row", params);
			this.attrs.put("tiposCombustibles", tiposCombustibles);
			this.attrs.put("idTipoCombustible", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("tiposCombustibles")));			
      this.toLoadMaquinariasGrupo();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // toLoadTiposCombustibles 
  
	private void toLoadMaquinariasGrupo() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
  		params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);			
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("maquinariasGrupos", (List<UISelectEntity>) UIEntity.seleccione("TcSakbeMaquinariasGruposDto", "row", params, columns, "nombre"));			
			this.attrs.put("idMaquinariaGrupo", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("maquinariasGrupos")));			
      this.doLoadTiposMaquinarias();
    } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // toLoadMaquinariasGrupo	
  
	public void doLoadTiposMaquinarias() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
  		params.put("idMaquinariaGrupo", ((UISelectEntity)this.attrs.get("idMaquinariaGrupo")).getKey());			
			columns= new ArrayList<>();
      columns.add(new Columna("grupo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("tiposMaquinarias", (List<UISelectEntity>) UIEntity.seleccione("TcSakbeTiposMaquinariasDto", "grupo", params, columns, "grupo"));			
			this.attrs.put("idTipoMaquinaria", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("tiposMaquinarias")));			
    } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // toLoadTiposMaquinarias	
  
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
			params.put(Constantes.SQL_CONDICION, "id_maquinaria_estatus in (".concat(seleccionado.toString("estatusAsociados")).concat(")"));
			allEstatus= UISelect.build("TcSakbeMaquinariasEstatusDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);			
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
    TcSakbeMaquinariasBitacoraDto bitacora= null;
		Entity seleccionado       = null;
	  Map<String, Object> params= new HashMap<>();	
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
      params.put("idMaquinaria", seleccionado.toMap());
			Maquinaria orden= (Maquinaria)DaoFactory.getInstance().toEntity(Maquinaria.class, "TcSakbeMaquinariasDto", "igual", params);
  	  bitacora= new TcSakbeMaquinariasBitacoraDto(seleccionado.getKey(), (String)this.attrs.get("justificacion"), JsfBase.getIdUsuario(), Long.valueOf((String)this.attrs.get("estatus")), -1L);
			transaccion = new Transaccion(orden, bitacora);
			if(transaccion.ejecutar(EAccion.JUSTIFICAR)) 
				JsfBase.addMessage("Cambio estatus", "Se realizo el cambio de estatus de forma correcta.", ETipoMensaje.INFORMACION);
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
  
  public void doValueChangeDesarrollo(ValueChangeEvent event) {
    try {      
      String[] values= event.getNewValue().toString().split("[|]");
      if(values!= null && values.length> 1 && !Objects.equals(values[0].trim(),"-1")) {
        this.attrs.put("ikDesarrollo", values[0].trim());      
        this.attrs.put("temporal", values[1].trim());      
      } // if  
      else
        this.attrs.put("ikDesarrollo", null);      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doChangeDesarrollo(Entity row) {
    Map<String, Object> params= new HashMap<>();
    try {      
      if(!Cadena.isVacio(this.attrs.get("ikDesarrollo"))) {
        params.put("idDesarrollo", this.attrs.get("ikDesarrollo"));
        params.put("idMaquinaria", row.toLong("idMaquinaria"));      
        Entity item= (Entity)DaoFactory.getInstance().toEntity("TrSakbeMaquinariaDesarrolloDto", "buscar", params);
        if(item== null || item.isEmpty()) {
          TrSakbeMaquinariaDesarrolloDto value= new TrSakbeMaquinariaDesarrolloDto(
            row.toLong("idMaquinaria"), // Long idMaquinaria, 
            new Long((String)this.attrs.get("ikDesarrollo")), // Long idDesarrollo, 
            JsfBase.getIdUsuario(), // Long idUsuario, 
            -1L // Long idMaquinariaDesarrollo
          );
          DaoFactory.getInstance().insert(value);
        } // if  
        else {
          params.put("idMaquinariaDesarrollo", item.getKey());      
          DaoFactory.getInstance().updateAll(TrSakbeMaquinariaDesarrolloDto.class, params);
        } // if
        row.get("desarrollo").setData(this.attrs.get("temporal"));
        this.attrs.put("ikDesarrollo", null);
        this.attrs.put("temporal", null);
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
