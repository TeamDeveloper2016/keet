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
import mx.org.kaana.keet.comun.Catalogos;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
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
			this.loadCatalogos();
			Catalogos.toLoadEspecialidades(this.attrs);
			this.doLoad();
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
			params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());						
			params.put(Constantes.SQL_CONDICION, condicion);	
      nombres= (List<UISelectEntity>) UIEntity.seleccione("VistaContratosDto", "contratistasSubAsociados", params, columns, Constantes.SQL_TODOS_REGISTROS, "departamento");
      this.attrs.put("nombres", nombres);
			this.attrs.put("nombre", UIBackingUtilities.toFirstKeySelectEntity(nombres));
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
	
	private String toPrepare() {
		StringBuilder sb= new StringBuilder();		
		if(this.attrs.get("idDepartamento")!= null && !Cadena.isVacio(this.attrs.get("idDepartamento")) && Long.valueOf(this.attrs.get("idDepartamento").toString())>= 1L)
			sb.append("tc_keet_departamentos.id_departamento=").append(this.attrs.get("idDepartamento")).append(" and ");		
		return Cadena.isVacio(sb)? Constantes.SQL_VERDADERO: sb.substring(0, sb.length()-4);
	} // toPrepare
	
	public String doAceptar() {
    String regresar        = null;
		Transaccion transaccion= null;		
		UISelectEntity figura  = null;
    try {			
			figura= ((List<UISelectEntity>)this.attrs.get("nombres")).get(((List<UISelectEntity>)this.attrs.get("nombres")).indexOf(((UISelectEntity)this.attrs.get("nombre"))));			
			transaccion= new Transaccion(Long.valueOf(figura.getKey().toString().substring(4)), (String[])this.attrs.get("idsLotes"), figura.toLong("tipo"));
			if(transaccion.ejecutar(EAccion.REPROCESAR)){				
				JsfBase.setFlashAttribute("idContrato", this.attrs.get("idContrato"));			
				regresar= "lotes".concat(Constantes.REDIRECIONAR);			
				JsfBase.addMessage("Asignación de contratista a lotes", "La asignación se realizo de forma correcta.", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("Asignación de contratista a lotes", "Ocurrió un error al realizar la asignación.", ETipoMensaje.ERROR);
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