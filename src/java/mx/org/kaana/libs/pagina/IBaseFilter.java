package mx.org.kaana.libs.pagina;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 17-feb-2014
 * @time 16:19:07
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public abstract class IBaseFilter extends IBaseAttribute implements Serializable {

	private static final long serialVersionUID=-5324353121363296376L;
	protected FormatLazyModel lazyModel;
	
/**
 * Obtiene el Objeto lazyModel de tipo FormatLazyModel
 * @return
 */	
	public FormatLazyModel getLazyModel() {
		return lazyModel;
	}
	/**
   * Metodo abstracto para implementar para la carga de la consulta
   */
	public abstract void doLoad();	
	
  protected String toLookForEmpresaLogo(Long idEmpresa) {
    String regresar           = null;
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idEmpresa", idEmpresa);      
      Value value = DaoFactory.getInstance().toField("TcManticEmpresasDto", "logo", params, "imagen");
      if(value!= null && value.getData()!= null)
        regresar= JsfBase.getRealPath(Constantes.RUTA_IMAGENES).concat(value.toString());
      else
        regresar= JsfBase.getRealPath(Constantes.RUTA_IMAGENES).concat(Configuracion.getInstance().getEmpresa("logo"));
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
  
}
