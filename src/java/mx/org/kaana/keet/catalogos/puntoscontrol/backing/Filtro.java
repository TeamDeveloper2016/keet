package mx.org.kaana.keet.catalogos.puntoscontrol.backing;

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

@Named(value = "keetCatalogosPuntosControlFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			if(JsfBase.getFlashAttribute("dPuntoGrupoProcess")!= null){
				this.attrs.put("dPuntoGrupoProcess", JsfBase.getFlashAttribute("dPuntoGrupoProcess"));
				doLoad();
				this.attrs.put("dPuntoGrupoProcess", null);
			} // if
			this.attrs.put("departamentos", UIEntity.seleccione("TcKeetDepartamentosDto", "especialidades",  "nombre"));
			//
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns    = null;
		Map<String, Object>params= null;
    try {
      params= this.toPrepare();	
      columns= new ArrayList<>();
      columns.add(new Columna("paquete", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("departamento", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      this.lazyModel = new FormatCustomLazy("VistaPuntosControlDto", params, columns);
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
      JsfBase.setFlashAttribute("dPuntoGrupo", eaccion.equals(EAccion.AGREGAR) ? -1L : ((Entity) this.attrs.get("seleccionado")).getKey());
			JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/PuntosControl/filtro");
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
    UISelectEntity cliente        = (UISelectEntity)this.attrs.get("cliente");
    List<UISelectEntity>provedores= (List<UISelectEntity>)this.attrs.get("clientes");
		if(this.attrs.get("dPuntoGrupoProcess")!= null && !Cadena.isVacio(this.attrs.get("dPuntoGrupoProcess")))
			sb.append("tc_keet_puntos_grupos.id_punto_grupo=").append(this.attrs.get("dPuntoGrupoProcess")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("nombre")))
			sb.append("(tc_keet_puntos_grupos.descripcion like '%").append(this.attrs.get("nombre")).append("%') and ");
    if(!Cadena.isVacio(this.attrs.get("departamento")) && ((UISelectEntity)this.attrs.get("departamento")).getKey()>= 1L)				
			sb.append("(tc_keet_departamentos.id_departamento in (").append(((UISelectEntity)this.attrs.get("departamento")).getKey()).append(")) and ");
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	} // toPrepare

  public String doMasivo() {
    JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/PuntosControl/filtro"); 
    return "/Paginas/Mantic/Catalogos/Masivos/importar".concat(Constantes.REDIRECIONAR);
	}
}