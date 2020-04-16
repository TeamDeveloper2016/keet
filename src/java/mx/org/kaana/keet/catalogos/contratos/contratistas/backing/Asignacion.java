package mx.org.kaana.keet.catalogos.contratos.contratistas.backing;

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
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.catalogos.contratos.contratistas.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosContratosContratistasAsignacion")
@ViewScoped
public class Asignacion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;				
	
  @PostConstruct
  @Override
  protected void init() {				
    try {						
			this.attrs.put("idContrato", (Long) JsfBase.getFlashAttribute("idContrato"));
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			loadCatalogos();
			loadDepartamentos();			
    } // try 
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
  
	private void loadCatalogos() throws Exception {
		Entity contrato          = null;
		List<UISelectItem> lotes = null;
		Map<String, Object>params= null;
		String[] idsLotes        = null;
		try {
			params= new HashMap<>();			
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato=".concat(this.attrs.get("idContrato").toString()));
			contrato= (Entity) DaoFactory.getInstance().toEntity("VistaContratosLotesDto", "principal", params);
			this.attrs.put("contrato", contrato);
			params.clear();
			params.put("idContrato", this.attrs.get("idContrato"));
			lotes= UISelect.build("TcKeetContratosLotesDto", "byContratoContratistas", params, "descripcionLote", " ", EFormatoDinamicos.MAYUSCULAS);
			this.attrs.put("lotes", lotes);			
			this.attrs.put("idsLotes", idsLotes);			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadCatalogos
	
	private void loadDepartamentos() {
		List<UISelectItem> departamentos= null;
		Map<String, Object> params      = null;		
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);			
			departamentos= UISelect.seleccione("TcKeetDepartamentosDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("departamentos", departamentos);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally		
	} // loadDepartamentos
	
  public void doLoad() {    						
		String condicion            = null;    			
		List<Columna> columns       = null;
    Map<String, Object> params  = null;
		List<UISelectEntity> nombres= null;		
    try {
			columns= new ArrayList<>();      
			params= new HashMap<>();
			condicion= this.toPrepare();									
      columns.add(new Columna("nombreCompleto", EFormatoDinamicos.MAYUSCULAS));			
			String nombreEmpleado= (String)this.attrs.get("nombreEmpleado"); 
			nombreEmpleado= !Cadena.isVacio(nombreEmpleado) ? nombreEmpleado.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim(): "WXYZ";		
			if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
				params.put("idEmpresa", this.attrs.get("idEmpresa"));
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales()); 
			params.put("nombreEmpleado", nombreEmpleado);	
			params.put(Constantes.SQL_CONDICION, condicion);	
      nombres= (List<UISelectEntity>) UIEntity.build("VistaContratosDto", "contratistas", params, columns, 20L);
      this.attrs.put("nombres", nombres);
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally    
  } // doLoad					
	
	public List<UISelectEntity> doCompleteNombreEmpleado(String query) {
		this.attrs.put("nombreEmpleado", query);
    this.doLoad();		
		return (List<UISelectEntity>)this.attrs.get("nombres");
	}	// doCompleteNombreEmpleado
	
	private String toPrepare() {
		StringBuilder sb= new StringBuilder();		
		if(this.attrs.get("idDepartamento")!= null && !Cadena.isVacio(this.attrs.get("idDepartamento")) && Long.valueOf(this.attrs.get("idDepartamento").toString())>= 1L)
			sb.append("tc_keet_departamentos.id_departamento=").append(this.attrs.get("idDepartamento")).append(" and ");		
		return Cadena.isVacio(sb)? Constantes.SQL_VERDADERO: sb.substring(0, sb.length()-4);
	} // toPrepare
	
	public String doAceptar() {
    String regresar        = null;
		Transaccion transaccion= null;
    try {			
			transaccion= new Transaccion(((UISelectEntity)this.attrs.get("nombre")).getKey(), (String[])this.attrs.get("idsLotes"));
			if(transaccion.ejecutar(EAccion.REPROCESAR)){				
				JsfBase.setFlashAttribute("idContrato", this.attrs.get("idContrato"));			
				regresar= "lotes".concat(Constantes.REDIRECIONAR);			
				JsfBase.addMessage("Asignaci�n de contratista a lotes", "La asignaci�n se realizo de forma correcta.", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("Asignaci�n de contratista a lotes", "Ocurri� un error al realizar la asignaci�n.", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doAceptar		
	
	public String doCancelar() {
    String regresar= null;    		
    try {			
			JsfBase.setFlashAttribute("idContrato", this.attrs.get("idContrato"));			
			regresar= "lotes".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
}