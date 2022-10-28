package mx.org.kaana.keet.catalogos.rubros.backing;

import java.io.Serializable;
import java.util.ArrayList;
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

@Named(value = "keetCatalogosRubrosFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			this.attrs.put("sortOrder", "order by tc_keet_rubros.registro desc"); 
			if(JsfBase.getFlashAttribute("idRubroProcess")!= null){
				this.attrs.put("idRubroProcess", JsfBase.getFlashAttribute("idRubroProcess"));
				doLoad();
				this.attrs.put("idRubroProcess", null);
			} // if
			loadCatalogos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	
	private void loadCatalogos(){
		Map<String, Object>params= null;
    try {
      params=  new HashMap<>();	
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			this.attrs.put("unidadesMedidas", UIEntity.seleccione("VistaEmpaqueUnidadDto", "row", params, "empaque"));
		} // try
		catch (Exception e) {
			throw e;
		} // catch	
		finally{
			Methods.clean(params);
		}
	} // loadCatalogos

  @Override
  public void doLoad() {
    List<Columna> columns    = null;
		Map<String, Object>params= null;
    try {
      params= this.toPrepare();	
      columns= new ArrayList<>();
      columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("unidad", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      this.lazyModel = new FormatCustomLazy("VistaRubrosDto", params, columns);
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
    EAccion eaccion= null;
		String regresar= null; 
    try {
			regresar= "accion".concat(Constantes.REDIRECIONAR);
      eaccion = EAccion.valueOf(accion.toUpperCase());
      JsfBase.setFlashAttribute("accion", eaccion);      
      JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(accion.toUpperCase()));      
      JsfBase.setFlashAttribute("idRubro", eaccion.equals(EAccion.AGREGAR) ? -1L : ((Entity) this.attrs.get("seleccionado")).getKey());
			JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Rubros/filtro");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion  
	
		
	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar  = new HashMap<>();	
		StringBuilder sb              = new StringBuilder();
		UISelectEntity unidad         = (UISelectEntity)this.attrs.get("empaqueUnidadMedida");
		if(this.attrs.get("idRubroProcess")!= null && !Cadena.isVacio(this.attrs.get("idRubroProcess")))
			sb.append("tc_keet_rubros.id_rubro=").append(this.attrs.get("idRubroProcess")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("codigo")))
			sb.append("(tc_keet_rubros.codigo like '%").append(this.attrs.get("codigo")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("nombre")))
			sb.append("(tc_keet_rubros.nombre like '%").append(this.attrs.get("nombre")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("paquete")))
			sb.append("(tc_keet_rubros_grupos.paquetes >= '").append(this.attrs.get("paquete")).append("') and ");
		if(!Cadena.isVacio(this.attrs.get("extra")))
			sb.append("(tc_keet_rubros.id_extra = ").append(this.attrs.get("extra")).append(") and ");
		if(unidad!= null && unidad.getKey()>0L)
			sb.append("(tc_keet_rubros.id_empaque_unidad_medida = ").append(unidad.getKey()).append(") and ");
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	} // toPrepare

  public String doMasivo() {
    JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Rubros/filtro"); 
    return "/Paginas/Mantic/Catalogos/Masivos/importar".concat(Constantes.REDIRECIONAR);
	}
}