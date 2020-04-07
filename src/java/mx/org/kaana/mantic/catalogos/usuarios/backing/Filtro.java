package mx.org.kaana.mantic.catalogos.usuarios.backing;

import java.io.Serializable;
import java.sql.Date;
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
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.enums.ETipoPersona;

@Named(value = "manticCatalogosUsuariosFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("idTipoPersona", ETipoPersona.USUARIO.getIdTipoPersona());
			this.toLoadCatalog();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
  
  @Override
  public void doLoad() {
    List<Columna> campos      = null;
		Map<String, Object> params= this.toPrepare();
    try {
      campos = new ArrayList<>();
      params.put("sortOrder", "order by tc_mantic_personas.nombres");
      campos.add(new Columna("nombreCompleto", EFormatoDinamicos.MAYUSCULAS));
      campos.add(new Columna("cuenta", EFormatoDinamicos.MAYUSCULAS));     
      campos.add(new Columna("ultimoAcceso", EFormatoDinamicos.FECHA_HORA_CORTA));     
      this.lazyModel = new FormatCustomLazy("VistaPersonasDto", "lazy", params, campos);
      UIBackingUtilities.resetDataTable();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(campos);
    } // finally		
  } // doLoad

	private void toLoadCatalog() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.attrs.put("perfiles", (List<UISelectEntity>) UIEntity.seleccione("TcJanalPerfilesDto", "row", params, columns, "descripcion"));
			this.attrs.put("idPerfil", new UISelectEntity("-1"));
    } // try
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}
	
	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb= new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("cuenta")))
  		sb.append("(upper(tc_mantic_personas.cuenta) like '%").append(this.attrs.get("cuenta")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("nombres")))
  		sb.append("(concat(tc_mantic_personas.nombres,' ', tc_mantic_personas.paterno, ' ', tc_mantic_personas.materno) like '%").append(this.attrs.get("nombres")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("paterno")))
  		sb.append("(tc_mantic_personas.paterno like '%").append(this.attrs.get("paterno")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("materno")))
  		sb.append("(tc_mantic_personas.materno like '%").append(this.attrs.get("materno")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("rfc")))
  		sb.append("(tc_mantic_personas.rfc like '%").append(this.attrs.get("rfc")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("idTipoSexo")) && !this.attrs.get("idTipoSexo").toString().equals("-1"))
  		sb.append("(tc_mantic_personas.id_tipo_sexo=").append(this.attrs.get("idTipoSexo")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idPerfil")) && !this.attrs.get("idPerfil").toString().equals("-1"))
  		sb.append("(tc_janal_usuarios.idPerfil=").append(this.attrs.get("idPerfil")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
		  regresar.put("idEmpresa", this.attrs.get("idEmpresa"));
		else
		  regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
		if(!Cadena.isVacio(this.attrs.get("fechaInicio")))
		  sb.append("(date_format(tc_janal_usuarios.ultimo_acceso, '%Y%m%d')>= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("fechaTermino")))
		  sb.append("(date_format(tc_janal_usuarios.ultimo_acceso, '%Y%m%d')<= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaTermino"))).append("') and ");	
    regresar.put("sucursales", JsfBase.getAutentifica().getEmpresa().getDependencias());
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	}

  public String doAccion(String accion) {
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("tipoPersona", this.attrs.get("idTipoPersona"));		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Catalogos/Usuarios/filtro");		
			JsfBase.setFlashAttribute("idPersona", (eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR)) ? ((Entity)this.attrs.get("seleccionado")).getKey() : -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return "/Paginas/Mantic/Catalogos/Personas/accion".concat(Constantes.REDIRECIONAR);
  } // doAccion  
	
}
