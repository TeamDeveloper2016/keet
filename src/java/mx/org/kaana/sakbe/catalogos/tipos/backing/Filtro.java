package mx.org.kaana.sakbe.catalogos.tipos.backing;

import java.io.Serializable;
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
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.sakbe.catalogos.tipos.beans.TipoMaquinaria;
import mx.org.kaana.sakbe.catalogos.tipos.reglas.Transaccion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value = "sakbeCatalogosTiposFiltro")
@ViewScoped 
public class Filtro extends IBaseFilter implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Filtro.class);
  private static final long serialVersionUID= 8793667741599328338L;

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.attrs.put("idTipoMaquinaria", JsfBase.getFlashAttribute("idTipoMaquinaria"));
			this.toLoadCatalogos();
      if(this.attrs.get("idTipoMaquinaria")!= null) {
			  this.doLoad();
        this.attrs.put("idTipoMaquinaria", null);
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
      params.put("sortOrder", "order by tc_sakbe_tipos_maquinarias.registro desc");
      columns = new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));      
      this.lazyModel= new FormatCustomLazy("VistaTiposMaquinariasDto", params, columns);
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
			JsfBase.setFlashAttribute("idTipoMaquinaria", eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR)? ((Entity)this.attrs.get("seleccionado")).getKey() : -1L);
      JsfBase.setFlashAttribute("retorno", "/Paginas/Sakbe/Catalogos/Tipos/filtro".concat(Constantes.REDIRECIONAR));		
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return "/Paginas/Sakbe/Catalogos/Tipos/accion".concat(Constantes.REDIRECIONAR);
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
      params.put("idTipoMaquinaria", seleccionado.getKey());
			transaccion= new Transaccion((TipoMaquinaria)DaoFactory.getInstance().toEntity(TipoMaquinaria.class, "TcSakbeTiposMaquinariasDto", "igual", params));
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar", "El tipo de maquinaria se ha eliminado correctamente", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("Eliminar", "Ocurrió un error al eliminar el tipo de maquinaria", ETipoMensaje.ALERTA);								
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
		if(!Cadena.isVacio(this.attrs.get("idTipoMaquinaria")) && !this.attrs.get("idTipoMaquinaria").toString().equals("-1"))
  		sb.append("(tc_sakbe_tipos_maquinarias.id_tipo_maquinaria= ").append(this.attrs.get("idTipoMaquinaria")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("clave")))
  		sb.append("(tc_sakbe_tipos_maquinarias.clave like '%").append(((UISelectEntity)this.attrs.get("clave")).getKey()).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("nombre")))
  		sb.append("(tc_sakbe_tipos_maquinarias.placa like '%").append(((UISelectEntity)this.attrs.get("nombre")).getKey()).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("idMaquinariaGrupo")) && !this.attrs.get("idMaquinariaGrupo").toString().equals("-1"))
  		sb.append("(tc_sakbe_tipos_maquinarias.id_maquinaria_grupo= ").append(this.attrs.get("idMaquinariaGrupo")).append(") and ");
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
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("maquinariasGrupos", (List<UISelectEntity>) UIEntity.seleccione("TcSakbeMaquinariasGruposDto", "row", params, columns, "nombre"));			
			this.attrs.put("idMaquinariaGrupo", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("maquinariasGrupos")));			
    } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}
  
	public void doGlobalEvent(Boolean isViewException) {
		LOG.error("ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA ["+ isViewException+ "]");
	}
  
}
