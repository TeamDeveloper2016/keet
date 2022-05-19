package mx.org.kaana.keet.catalogos.contratos.personal.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Encriptar;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosContratosPersonalFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;		
	private List<UISelectEntity> desarrollos;
	private UISelectEntity seleccionado;
	
	public List<UISelectEntity> getDesarrollos() {
		return desarrollos;
	}

	public void setDesarrollos(List<UISelectEntity> desarrollos) {
		this.desarrollos = desarrollos;
	}	

	public UISelectEntity getSeleccionado() {
		return seleccionado;
	}

	public void setSeleccionado(UISelectEntity seleccionado) {
		this.seleccionado = seleccionado;
	}	
	
  @PostConstruct
  @Override
  protected void init() {
		Encriptar encriptar               = null;
		EOpcionesResidente opcionResidente= null;
    try {
			if(JsfBase.getParametro("opcion")!= null){
				encriptar= new Encriptar();
				opcionResidente= EOpcionesResidente.valueOf(encriptar.desencriptar(JsfBase.getParametro("opcion")));				
			} // if
			else if(JsfBase.getFlashAttribute("opcion")!= null)
				opcionResidente= (EOpcionesResidente) JsfBase.getFlashAttribute("opcion");											
			else
				opcionResidente= EOpcionesResidente.EMPLEADOS;		
			this.attrs.put("titulo", opcionResidente.getTitulo());
			this.attrs.put("opcionResidente", opcionResidente);
      this.attrs.put("idContratoEstatus", opcionResidente.getIdContratoEstatus());
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
//			if(JsfBase.getFlashAttribute("idDesarrolloProcess")!= null) {
//				this.attrs.put("idDesarrolloProcess", JsfBase.getFlashAttribute("idDesarrolloProcess"));
//				this.doLoad();
//				this.attrs.put("idDesarrolloProcess", null);
//			} // if
//			else
				this.doLoad();
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
      params.put("idContratoEstatus", this.attrs.get("idContratoEstatus"));
      columns= new ArrayList<>();
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("domicilio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));			
	    this.desarrollos= UIEntity.build("VistaDesarrollosDto", JsfBase.isAdminEncuestaOrAdmin()? "lazy": "residentes", params, columns);  
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

	private Map<String, Object> toPrepare() throws Exception {
		Map<String, Object> regresar = new HashMap<>();
		StringBuilder condicion      = new StringBuilder();
		UISelectEntity cliente       = (UISelectEntity) this.attrs.get("cliente");
		List<UISelectEntity> clientes= (List<UISelectEntity>)this.attrs.get("clientes");
		if(this.attrs.get("idDesarrolloProcess")!= null && !Cadena.isVacio(this.attrs.get("idDesarrolloProcess")))
			condicion.append("tc_keet_desarrollos.id_desarrollo=").append(this.attrs.get("idDesarrolloProcess")).append(" and ");		
		if(!Cadena.isVacio(this.attrs.get("nombres"))){
			condicion.append("(tc_keet_desarrollos.nombres like '%").append(this.attrs.get("nombres")).append("%' or ");
			condicion.append("tc_keet_desarrollos.clave like '%").append(this.attrs.get("nombres")).append("%') and ");
		} // if
		if(clientes!= null && cliente!= null && clientes.indexOf(cliente)>= 0) 
			condicion.append("(tc_mantic_clientes.razon_social like '%").append(clientes.get(clientes.indexOf(cliente)).toString("razonSocial")).append("%') and ");
		else if(!Cadena.isVacio(JsfBase.getParametro("razonSocial_input")))
			condicion.append("(tc_mantic_clientes.razon_social like '%").append(JsfBase.getParametro("razonSocial_input")).append("%') and ");    			
		regresar.put(Constantes.SQL_CONDICION, condicion.length()== 0 ? Constantes.SQL_VERDADERO : condicion.substring(0, condicion.length()- 4));		
		regresar.put("idPersona", JsfBase.getAutentifica().getPersona().getIdPersona());
		return regresar;		
	} // toPrepare  
	
	public List<UISelectEntity> doCompleteCliente(String codigo) {
 		List<Columna> columns     = null;
    Map<String, Object> params= null;		
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			params= new HashMap<>();
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
  		params.put("codigo", toCodigo(codigo));			
      this.attrs.put("clientes", UIEntity.build("TcManticClientesDto", (String) this.attrs.get("idXml"), params, columns, 40L));			
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
	
	private String toCodigo(String regresar){		
		boolean buscaPorCodigo= false;
		try {
			if(!Cadena.isVacio(regresar)) {
  			regresar= regresar.replaceAll(Constantes.CLEAN_SQL, "").trim();
				buscaPorCodigo= regresar.startsWith(".");
				if(buscaPorCodigo)
					regresar= regresar.trim().substring(1);
				regresar= regresar.toUpperCase().replaceAll("(,| |\\t)+", ".*.*");
			} // if	
			else
				regresar= "WXYZ";
			this.attrs.put("idXml", buscaPorCodigo ? "porCodigo" : "porNombre");
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toCodigo	
	
	public String doPagina() {
    String regresar          = null;    
		EOpcionesResidente opcion= null;
    try {
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("idDesarrollo", this.seleccionado.getKey());
			JsfBase.setFlashAttribute("opcionResidente", opcion);			
			regresar= opcion.getRuta().concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina
	
	public String doGeoreferencia() {
    String regresar          = null;    
		EOpcionesResidente opcion= null;
    try {
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("idDesarrollo", this.seleccionado.getKey());
			JsfBase.setFlashAttribute("opcionResidente", opcion);			
			JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf");			
			regresar= "/Paginas/Keet/Catalogos/Contratos/georeferencia.jsf".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina
  
}