package mx.org.kaana.keet.catalogos.contratos.materiales.backing;

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
import mx.org.kaana.keet.enums.EEstatusVales;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosContratosMaterialesFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;			
	private RegistroDesarrollo registroDesarrollo;		
	private List<Entity> vales;
	
	public RegistroDesarrollo getRegistroDesarrollo() {
		return registroDesarrollo;
	}

	public void setRegistroDesarrollo(RegistroDesarrollo registroDesarrollo) {
		this.registroDesarrollo = registroDesarrollo;
	}			

	public List<Entity> getVales() {
		return vales;
	}

	public void setVales(List<Entity> vales) {
		this.vales = vales;
	}

  @PostConstruct
  @Override
  protected void init() {		
    EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;		
		UISelectEntity figura    = null;
    try {			
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");			
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("opcionAdicional", JsfBase.getFlashAttribute("opcionAdicional"));
			this.attrs.put("idDesarrollo", idDesarrollo);
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());				
      this.attrs.put("destajos", false);				
      this.attrs.put("persona", false);				
      this.attrs.put("proveedor", false);							
			this.loadCatalogos();			
			if(JsfBase.getFlashAttribute("idDesarrolloProcess")!= null){				
				figura= (UISelectEntity) JsfBase.getFlashAttribute("figura");				
				this.doLoadFiguras();				
				this.attrs.put("figura", ((List<UISelectEntity>)this.attrs.get("figuras")).get(((List<UISelectEntity>)this.attrs.get("figuras")).indexOf(figura)));				
			} // if
			this.doLoad();
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void loadCatalogos() throws Exception {
		Map<String, Object>params = null;
		List<UISelectItem> estatus= null;
		try {
			params= new HashMap<>();
			this.registroDesarrollo= new RegistroDesarrollo((Long)this.attrs.get("idDesarrollo"));      
			this.attrs.put("domicilio", toDomicilio());						
			estatus= new ArrayList<>();
			for(EEstatusVales eestatus: EEstatusVales.values()){
				if(eestatus.equals(EEstatusVales.DISPONIBLE) || eestatus.equals(EEstatusVales.INCOMPLETO))
					estatus.add(new UISelectItem(eestatus.getKey(), eestatus.name()));
			} // for
			estatus.add(0, new UISelectItem(-1L, "SELECCIONE"));
			this.attrs.put("listEstatus", estatus);
			this.attrs.put("estatus", UIBackingUtilities.toFirstKeySelectItem(estatus));
			this.doLoadFiguras();      
		} // try
    finally {
			Methods.clean(params);
		} // finally
	} // loadCatalogos	
	
	public void doLoadFiguras(){
		List<UISelectEntity> figuras= null;
		Map<String, Object>params   = null;
		List<Columna> campos        = null;
		try {
			params= new HashMap<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));			
			campos= new ArrayList<>();
			campos.add(new Columna("nombreCompleto", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("puesto", EFormatoDinamicos.MAYUSCULAS));
			figuras= UIEntity.seleccione("VistaEntregaMaterialesDto", "empleadosAsociados", params, campos, "puesto");
			this.attrs.put("figuras", figuras);
			this.attrs.put("figura", UIBackingUtilities.toFirstKeySelectEntity(figuras));
			this.attrs.put("destajos", false);
			this.attrs.put("persona", false);
			this.attrs.put("proveedor", false);
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
		Map<String, Object>params         = null;
		List<UISelectEntity> valesCriterio= null;
		UISelectEntity valeCriterio       = null;		
    try {   						      		
			params= this.toPrepare();										
			this.vales= DaoFactory.getInstance().toEntitySet("VistaEntregaMaterialesDto", "vales", params);	
			valesCriterio= UIEntity.seleccione("VistaEntregaMaterialesDto", "vales", params, "consecutivo");
			valeCriterio= UIBackingUtilities.toFirstKeySelectEntity(valesCriterio);
			this.attrs.put("totalRegistros", this.vales.size());
			this.attrs.put("valesCriterios", valesCriterio);
			this.attrs.put("valeCriterio", valeCriterio);
			if(!this.vales.isEmpty()) 
				this.toEstatusVale();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {      
      Methods.clean(params);
    } // finally		
  } // doLoad			
	
	private Map<String, Object> toPrepare(){
		Map<String, Object>params   = null;
		List<UISelectEntity> figuras= null;		
		UISelectEntity figura       = null;
		StringBuilder condicion     = null;
		try {
			params= new HashMap<>();
			figuras= (List<UISelectEntity>) this.attrs.get("figuras");
			figura= figuras.get(figuras.indexOf((UISelectEntity) this.attrs.get("figura")));
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			params.put("condicionContratista", Constantes.SQL_VERDADERO);
			params.put("condicionSubcontratista", Constantes.SQL_VERDADERO);			
			if(figura.getKey()>= 1L){
				if(figura.toLong("tipo").equals(1L)) {
					params.put("condicionContratista", "tc_keet_contratos_lotes_contratistas.id_empresa_persona=".concat(figura.getKey().toString().substring(4)));
					params.put("condicionSubcontratista", "tc_keet_contratos_lotes_proveedores.id_proveedor=-1");
				} // if
				else{
					params.put("condicionSubcontratista", "tc_keet_contratos_lotes_proveedores.id_proveedor=".concat(figura.getKey().toString().substring(4)));
					params.put("condicionContratista", "tc_keet_contratos_lotes_contratistas.id_empresa_persona=-1");
				} // else
			} // if			
			condicion= new StringBuilder();						
			if(this.attrs.get("valeCriterio")!= null && ((UISelectEntity)this.attrs.get("valeCriterio")).getKey()>= 1L)
				condicion.append("tc_keet_vales.id_vale=").append(((UISelectEntity)this.attrs.get("valeCriterio")).getKey()).append(" and ");			
			if(this.attrs.get("estatus")!= null && Long.valueOf(this.attrs.get("estatus").toString())>= 1L)
				condicion.append("tc_keet_vales.id_vale_estatus=").append(this.attrs.get("estatus")).append(" and ");	
			else
				condicion.append("tc_keet_vales.id_vale_estatus in (1, 5) and ");	
			params.put(Constantes.SQL_CONDICION, Cadena.isVacio(condicion) ? Constantes.SQL_VERDADERO : condicion.substring(0, condicion.length()-4));
			this.attrs.put("persona", figura.toLong("tipo").equals(1L));
			this.attrs.put("proveedor", figura.toLong("tipo").equals(2L));
			this.attrs.put("destajos", false);
			this.attrs.put("figura", figura);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		return params;
	} // toPrepare	
	
	private void toEstatusVale() throws Exception {		
		for(Entity vale: this.vales){															
			vale.put("iconEstatus", new Value("iconEstatus", EEstatusVales.fromId(vale.toLong("idValeEstatus")).getSemaforo()));				
		} // for		
	} // toEstatusManzanaLote	
	
	public String doEntrega() {
    String regresar      = null;    		
		Entity seleccionado  = null;		
		UISelectEntity figura= null;
    try {						
			figura= toFigura();
			seleccionado= (Entity) this.attrs.get("seleccionado");			
			JsfBase.setFlashAttribute("opcionResidente", (EOpcionesResidente)this.attrs.get("opcionResidente"));												
			if(this.attrs.get("opcionAdicional")!= null)
				JsfBase.setFlashAttribute("opcionAdicional", (EOpcionesResidente)this.attrs.get("opcionAdicional"));												
			JsfBase.setFlashAttribute("seleccionado", seleccionado);												
			JsfBase.setFlashAttribute("figura", figura);									
			JsfBase.setFlashAttribute("idDepartamento", figura.toLong("idDepartamento"));									
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));							
			JsfBase.setFlashAttribute("flujo", "filtro");										
			regresar= "entrega".concat(Constantes.REDIRECIONAR);										
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doEntrega
	
	private UISelectEntity toFigura() throws Exception{		
		UISelectEntity regresar  = null;
		Entity figura            = null;
		Entity seleccionado      = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			seleccionado= (Entity) this.attrs.get("seleccionado");
			params.put("id", seleccionado.toLong("idFigura"));
			figura= (Entity) DaoFactory.getInstance().toEntity("VistaEntregaMaterialesDto", seleccionado.toLong("figura").equals(1L) ? "contratista" : "subcontratista", params);
			regresar= new UISelectEntity(figura);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toFigura
	
	public String doConsultar() {
    String regresar= null;    				
    try {			
			doEntrega();
			JsfBase.setFlashAttribute("nombreAccionOk", "Regresar");							
			regresar= "resumen".concat(Constantes.REDIRECIONAR);										
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doConsultar
	
	public String doCancelar() {
    String regresar          = null;    
		EOpcionesResidente opcion= null;		
    try {			
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("idDesarrolloProcess", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("opcion", opcion);						
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));			
			JsfBase.setFlashAttribute("opcionResidente", opcion);									
			regresar= opcion.getRetorno().concat(Constantes.REDIRECIONAR_AMPERSON);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
}