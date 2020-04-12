package mx.org.kaana.keet.catalogos.dinamicos.puestos.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;

@Named(value = "keetCatalogosDinamicosPuestosFiltro")
@ViewScoped
public class Filtro extends mx.org.kaana.keet.catalogos.dinamicos.backing.Filtro implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;  

	@PostConstruct
  @Override
  protected void init() {
		super.init();
		loadEmpresas();
	}
	
	private void loadEmpresas() {
		Map<String, Object>params= null;
		List<Columna> columns    = null;
		try {
			params= new HashMap<>();
			columns= new ArrayList<>();			
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch				
	} // loadEmpresas
	
	@Override
	protected Map<String, Object> toPrepare() {
		Map<String, Object> regresar= null;
		StringBuilder sb            = null;
		try {			
			regresar= new HashMap<>();
			sb= new StringBuilder("");						
			if(!Cadena.isVacio(this.attrs.get("clave"))) 
				sb.append("clave like '%").append(this.attrs.get("clave")).append("%' and ");						  					
			if(!Cadena.isVacio(this.attrs.get("nombre"))) 
				sb.append("nombre like '%").append(this.attrs.get("nombre")).append("%' and ");						  					
			if(!Cadena.isVacio(this.attrs.get("descripcion"))) 
				sb.append("descripcion like '%").append(this.attrs.get("descripcion")).append("%' and ");						  
			if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()>= 1L)				
				sb.append("id_empresa in (").append(((UISelectEntity)this.attrs.get("idEmpresa")).getKey()).append(") and ");
			else
				sb.append("id_empresa in (").append(JsfBase.getAutentifica().getEmpresa().getSucursales()).append(") and ");			
			regresar.put("condicion", Cadena.isVacio(sb.toString()) ? Constantes.SQL_VERDADERO : sb.substring(0, sb.length()- 4));			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toCondicion 
}