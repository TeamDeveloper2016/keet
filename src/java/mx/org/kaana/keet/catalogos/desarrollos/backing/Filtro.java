package mx.org.kaana.keet.catalogos.desarrollos.backing;

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
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosDesarrollosFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			if(JsfBase.getFlashAttribute("idDesarrolloProcess")!= null){
				this.attrs.put("idDesarrolloProcess", JsfBase.getFlashAttribute("idDesarrolloProcess"));
				this.doLoad();
				this.attrs.put("idDesarrolloProcess", null);
			} // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns    = new ArrayList<>();
		Map<String, Object>params= this.toPrepare();
    try {
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("domicilio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
      this.lazyModel = new FormatCustomLazy("VistaDesarrollosDto", "desarrollos", params, columns);
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
      JsfBase.setFlashAttribute("idDesarrollo", eaccion.equals(EAccion.AGREGAR) ? -1L : ((Entity) this.attrs.get("seleccionado")).getKey());
			JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Desarrollos/filtro");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion  
	
	public List<UISelectEntity> doCompleteCliente(String codigo) {
 		List<Columna> columns     = null;
    Map<String, Object> params= null;
		boolean buscaPorCodigo    = false;
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			params= new HashMap<>();
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
        this.attrs.put("clientes", UIEntity.build("TcManticClientesDto", "porCodigo", params, columns, 40L));
			else
        this.attrs.put("clientes", UIEntity.build("TcManticClientesDto", "porNombre", params, columns, 40L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
		return (List<UISelectEntity>)this.attrs.get("clientes");
	}	// doCompleteCliente	
	
	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar  = new HashMap<>();	
		StringBuilder sb              = new StringBuilder();
    UISelectEntity cliente        = (UISelectEntity)this.attrs.get("cliente");
    List<UISelectEntity>provedores= (List<UISelectEntity>)this.attrs.get("clientes");
		if(this.attrs.get("idDesarrolloProcess")!= null && !Cadena.isVacio(this.attrs.get("idDesarrolloProcess")))
			sb.append("tc_keet_desarrollos.id_desarrollo=").append(this.attrs.get("idDesarrolloProcess")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("clave")))
			sb.append("(tc_keet_desarrollos.clave like '%").append(this.attrs.get("clave")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("nombres")))
			sb.append("(tc_keet_desarrollos.nombres like '%").append(this.attrs.get("nombres")).append("%') and ");
    if(provedores!= null && cliente!= null && provedores.indexOf(cliente)>= 0) 
			sb.append("(tc_mantic_clientes.razon_social like '%").append(provedores.get(provedores.indexOf(cliente)).toString("razonSocial")).append("%') and ");
		else{
 		  if(!Cadena.isVacio(JsfBase.getParametro("razonSocial_input")))
			  sb.append("(tc_mantic_clientes.razon_social like '%").append(JsfBase.getParametro("razonSocial_input")).append("%') and ");    
		} // else
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	} // toPrepare

  public String doMasivo() {
    JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Desarrollos/filtro"); 
    return "/Paginas/Mantic/Catalogos/Masivos/importar".concat(Constantes.REDIRECIONAR);
	}
  
  public String doAdicionales() {
    JsfBase.setFlashAttribute("idDesarrollo", ((Entity) this.attrs.get("seleccionado")).getKey());
    JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Contratos/filtro");
    return "/Paginas/Keet/Catalogos/Desarrollos/adicionales".concat(Constantes.REDIRECIONAR);
  }
  
}