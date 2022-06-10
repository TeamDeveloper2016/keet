package mx.org.kaana.sakbe.combustibles.backing;

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
import mx.org.kaana.kajool.enums.EAccion;
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
import mx.org.kaana.sakbe.enums.ECombustiblesEstatus;

@Named(value = "sakbeCombustiblesDesarrollos")
@ViewScoped
public class Desarrollos extends IBaseFilter implements Serializable {

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
			if(JsfBase.getParametro("opcion")!= null) { 
				encriptar= new Encriptar();
				opcionResidente= EOpcionesResidente.valueOf(encriptar.desencriptar(JsfBase.getParametro("opcion")));				
			} // if
			else if(JsfBase.getFlashAttribute("opcion")!= null)
				opcionResidente= (EOpcionesResidente) JsfBase.getFlashAttribute("opcion");											
			else
				opcionResidente= EOpcionesResidente.EMPLEADOS;		
			this.attrs.put("ikTipoCombustible", JsfBase.getFlashAttribute("ikTipoCombustible")== null? 1L: JsfBase.getFlashAttribute("ikTipoCombustible"));
      this.toLoadTiposCombustibles();
			this.attrs.put("seguimiento", JsfBase.getFlashAttribute("seguimiento")== null? "/Paginas/Sakbe/Combustibles/visor": JsfBase.getFlashAttribute("seguimiento"));
			this.attrs.put("idContratoEstatus", 8L);
			this.attrs.put("titulo", opcionResidente.getTitulo());
			this.attrs.put("opcionResidente", opcionResidente);
      this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "visor": JsfBase.getFlashAttribute("retorno"));
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
	
	public String doPagina(Entity row) {
    String regresar          = null;    
		EOpcionesResidente opcion= null;
    try {
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));
    	JsfBase.setFlashAttribute("accion", EAccion.AGREGAR);
			JsfBase.setFlashAttribute("idDesarrollo", this.seleccionado.getKey());
      JsfBase.setFlashAttribute("ikTipoCombustible", this.attrs.get("ikTipoCombustible"));      
      JsfBase.setFlashAttribute("porcentaje", this.attrs.get("porcentaje"));    
      JsfBase.setFlashAttribute("seguimiento", this.attrs.get("seguimiento"));
  	  JsfBase.setFlashAttribute("retorno", "/Paginas/Sakbe/Combustibles/desarrollos.jsf?opcion=52df68e378f074");
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
			JsfBase.setFlashAttribute("retorno", "/Paginas/Sakbe/Combustibles/desarrollos.jsf");			
			regresar= "/Paginas/Keet/Catalogos/Contratos/georeferencia.jsf".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina
 
  public String doCancelar() {
  	JsfBase.setFlashAttribute("idCombustible", this.attrs.get("idCombustible"));
  	JsfBase.setFlashAttribute("ikTipoCombustible", this.attrs.get("ikTipoCombustible"));
    JsfBase.setFlashAttribute("porcentaje", this.attrs.get("porcentaje"));    
  	JsfBase.setFlashAttribute("combustible", this.attrs.get("combustible"));
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  }
 
  private Entity toLoadCombustible() throws Exception {
    Entity regresar           = null;
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idTipoCombustible", this.attrs.get("ikTipoCombustible"));      
      params.put("disponibles", ECombustiblesEstatus.ACEPTADO.getKey()+ ","+ ECombustiblesEstatus.EN_PROCESO.getKey());      
      regresar= (Entity)DaoFactory.getInstance().toEntity("VistaCombustiblesDto", "litros", params);
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
 
	private void toLoadTiposCombustibles() throws Exception {
		List<UISelectEntity> tiposCombustibles= null;
		Map<String, Object>params             = new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			tiposCombustibles= UIEntity.build("TcSakbeTiposCombustiblesDto", "row", params);
			this.attrs.put("tiposCombustibles", tiposCombustibles);
      if(!tiposCombustibles.isEmpty()) 
  	    this.attrs.put("idTipoCombustible", new UISelectEntity((Long)this.attrs.get("ikTipoCombustible")));
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

  public void doLoadPorcentajes() throws Exception {
    this.attrs.put("ikTipoCombustible", ((UISelectEntity)this.attrs.get("idTipoCombustible")).getKey());
    this.attrs.put("porcentaje", this.toLoadCombustible());
  }
  
}