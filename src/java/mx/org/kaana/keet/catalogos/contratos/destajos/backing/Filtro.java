package mx.org.kaana.keet.catalogos.contratos.destajos.backing;

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
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.desarrollos.beans.RegistroDesarrollo;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosContratosDestajosFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;			
	private RegistroDesarrollo registroDesarrollo;		
	private List<Entity> lotes;
	
	public RegistroDesarrollo getRegistroDesarrollo() {
		return registroDesarrollo;
	}

	public void setRegistroDesarrollo(RegistroDesarrollo registroDesarrollo) {
		this.registroDesarrollo = registroDesarrollo;
	}			

	public List<Entity> getLotes() {
		return lotes;
	}

	public void setLotes(List<Entity> lotes) {
		this.lotes = lotes;
	}
	
  @PostConstruct
  @Override
  protected void init() {		
    EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;
    try {
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");			
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("idDesarrollo", idDesarrollo);
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());						
			loadCatalogos();			
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void loadCatalogos(){
		try {
			this.registroDesarrollo= new RegistroDesarrollo((Long)this.attrs.get("idDesarrollo"));      
			this.attrs.put("domicilio", toDomicilio());			
			this.loadEspecialidades();			
			this.doLoadFiguras();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // loadCatalogos	
	
	private void loadEspecialidades(){
		List<UISelectItem>especialidades= null;
		Map<String, Object>params       = null;
		try {
			params= new HashMap<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			especialidades= UISelect.seleccione("VistaCapturaDestajosDto", "especialidades", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
			this.attrs.put("especialidades", especialidades);
			this.attrs.put("especialidad", UIBackingUtilities.toFirstKeySelectItem(especialidades));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadEspecialidades
	
	public void doLoadFiguras(){
		List<UISelectEntity> figuras= null;
		Map<String, Object>params   = null;
		List<Columna> campos        = null;
		try {
			params= new HashMap<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			params.put("idDepartamento", this.attrs.get("especialidad"));
			campos= new ArrayList<>();
			campos.add(new Columna("nombreCompleto", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("puesto", EFormatoDinamicos.MAYUSCULAS));
			figuras= UIEntity.seleccione("VistaCapturaDestajosDto", "empleadosAsociados", params, campos, "puesto");
			this.attrs.put("figuras", figuras);
			this.attrs.put("figura", UIBackingUtilities.toFirstKeySelectEntity(figuras));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
			Methods.clean(campos);
		} // finally
	} // doLoadFiguras
	
	private String toDomicilio(){
		StringBuilder regresar= null;
		try {
			regresar= new StringBuilder();
			regresar.append(this.registroDesarrollo.getDomicilio().getCalle()).append(" , ");
			if(!Cadena.isVacio(this.registroDesarrollo.getDomicilio().getNumeroExterior()))
				regresar.append(this.registroDesarrollo.getDomicilio().getNumeroExterior()).append(" , ");
			if(!Cadena.isVacio(this.registroDesarrollo.getDomicilio().getNumeroInterior()))
				regresar.append(this.registroDesarrollo.getDomicilio().getNumeroInterior()).append(" , ");
			regresar.append(this.registroDesarrollo.getDomicilio().getAsentamiento()).append(" , C.P. ");
			regresar.append(this.registroDesarrollo.getDomicilio().getCodigoPostal());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar.toString();
	} // toDomicilio
	
  @Override
  public void doLoad() {
		Map<String, Object>params   = null;
    List<Columna> columns       = null;		
		List<UISelectEntity> figuras= null;
		UISelectEntity figura       = null;
		String idXml                = null;
    try {      
			figuras= (List<UISelectEntity>) this.attrs.get("figuras");
			figura= figuras.get(figuras.indexOf((UISelectEntity) this.attrs.get("figura")));
			params= new HashMap<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			params.put("idFigura", figura.getKey() > 0 ? figura.getKey().toString().substring(4) : figura.getKey());
      columns= new ArrayList<>();      
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("fechaInicio", EFormatoDinamicos.FECHA_CORTA));                  
      columns.add(new Columna("fechaTermino", EFormatoDinamicos.FECHA_CORTA));    
			idXml= figura.toLong("tipo").equals(1L) ? "lotesContratistas" : "lotesSubContratistas";
	    this.lotes= DaoFactory.getInstance().toEntitySet("VistaCapturaDestajosDto", idXml, params);			
			if(!this.lotes.isEmpty()) 
			  UIBackingUtilities.toFormatEntitySet(this.lotes, columns);							
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {      
      Methods.clean(columns);
      Methods.clean(params);
    } // finally		
  } // doLoad	
	
	public String doConceptos() {
    String regresar             = null;    		
		Entity seleccionado         = null;
		List<UISelectEntity> figuras= null;
		UISelectEntity figura       = null;
    try {			
			figuras= (List<UISelectEntity>) this.attrs.get("figuras");
			figura= figuras.get(figuras.indexOf((UISelectEntity) this.attrs.get("figura")));
			seleccionado= (Entity) this.attrs.get("seleccionado");			
			JsfBase.setFlashAttribute("idContratoLote", seleccionado.getKey());									
			JsfBase.setFlashAttribute("figura", figura);									
			regresar= "conceptos".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina
	
	public String doCancelar() {
    String regresar          = null;    
		EOpcionesResidente opcion= null;		
    try {			
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("idDesarrolloProcess", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("opcion", opcion);			
			regresar= opcion.getRetorno().concat(Constantes.REDIRECIONAR_AMPERSON);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
}