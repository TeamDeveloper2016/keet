package mx.org.kaana.keet.catalogos.contratos.materiales.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.catalogos.desarrollos.beans.RegistroDesarrollo;
import mx.org.kaana.keet.comun.gps.Point;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.enums.EEstatusVales;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
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
		Long idDepartamento      = null;
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
				idDepartamento= (Long) JsfBase.getFlashAttribute("idDepartamento");
				figura= (UISelectEntity) JsfBase.getFlashAttribute("figura");
				this.attrs.put("especialidad", idDepartamento);
				this.doLoadFiguras();				
				this.attrs.put("figura", ((List<UISelectEntity>)this.attrs.get("figuras")).get(((List<UISelectEntity>)this.attrs.get("figuras")).indexOf(figura)));
				doLoad();
			} // if
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void loadCatalogos() throws Exception {
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			this.registroDesarrollo= new RegistroDesarrollo((Long)this.attrs.get("idDesarrollo"));      
			this.attrs.put("domicilio", toDomicilio());			
			this.loadEspecialidades();			
			this.doLoadFiguras();      
		} // try
    finally {
			Methods.clean(params);
		} // finally
	} // loadCatalogos	
	
	private void loadEspecialidades() {
		List<UISelectItem>especialidades= null;
		Map<String, Object>params       = null;
		try {
			params= new HashMap<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			especialidades= UISelect.seleccione("VistaEntregaMaterialesDto", "especialidades", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
			this.attrs.put("especialidades", especialidades);
			this.attrs.put("especialidad", UIBackingUtilities.toFirstKeySelectItem(especialidades));
		} // try
		finally {
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
	
	public void doLoadLotes(){
		Map<String, Object>params         = null;
		List<UISelectEntity> lotesCriterio= null;
		UISelectEntity loteCriterio       = null;
		String idXml                      = null;
		List<UISelectEntity> figuras      = null;		
		UISelectEntity figura             = null;		
		try {
			figuras= (List<UISelectEntity>) this.attrs.get("figuras");
			figura= figuras.get(figuras.indexOf((UISelectEntity) this.attrs.get("figura")));
			params= new HashMap<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			params.put("idFigura", figura.getKey() > 0 ? figura.getKey().toString().substring(4) : figura.getKey());			
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);			
			idXml= figura.toLong("tipo").equals(1L) ? "lotesContratistas" : "lotesSubContratistas";			
			lotesCriterio= UIEntity.seleccione("VistaEntregaMaterialesDto", idXml, params, "descripcionLote");
			loteCriterio= UIBackingUtilities.toFirstKeySelectEntity(figuras);
			this.attrs.put("lotesCriterio", lotesCriterio);
			this.attrs.put("loteCriterio", loteCriterio);			
			doLoad();			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doLoadLotes
	
  @Override
  public void doLoad() {
		Map<String, Object>params         = null;
		List<UISelectEntity> valesCriterio= null;
		UISelectEntity valeCriterio       = null;
		String idXml                      = null;
    try {   						      		
			params= toPrepare();				
			idXml= toIdXml();
			this.vales= DaoFactory.getInstance().toEntitySet("VistaEntregaMaterialesDto", idXml, params);	
			valesCriterio= UIEntity.seleccione("VistaEntregaMaterialesDto", idXml, params, "consecutivo");
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
			params.put("idFigura", figura.getKey() > 0 ? figura.getKey().toString().substring(4) : figura.getKey());
			condicion= new StringBuilder();			
			if(this.attrs.get("loteCriterio")!= null && ((UISelectEntity)this.attrs.get("loteCriterio")).getKey()>= 1L)
				condicion.append("tc_keet_contratos_lotes.id_contrato_lote=").append(((UISelectEntity)this.attrs.get("loteCriterio")).getKey()).append(" and ");			
			if(this.attrs.get("valeCriterio")!= null && ((UISelectEntity)this.attrs.get("valeCriterio")).getKey()>= 1L)
				condicion.append("tc_keet_vales.id_vale=").append(((UISelectEntity)this.attrs.get("valeCriterio")).getKey()).append(" and ");			
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
	
	private String toIdXml(){
		return ((List<UISelectEntity>) this.attrs.get("figuras")).get(((List<UISelectEntity>) this.attrs.get("figuras")).indexOf((UISelectEntity) this.attrs.get("figura"))).toLong("tipo").equals(1L) ? "valesContratistas" : "valesSubContratistas";
	} // toIdXml
	
	private void toEstatusVale() throws Exception {		
		for(Entity vale: this.vales){															
			vale.put("iconEstatus", new Value("iconEstatus", EEstatusVales.fromId(vale.toLong("idValeEstatus")).getSemaforo()));				
		} // for		
	} // toEstatusManzanaLote	
	
	public String doEntrega() {
    String regresar             = null;    		
		Entity seleccionado         = null;
		List<UISelectEntity> figuras= null;
		UISelectEntity figura       = null;
    try {			
			figuras= (List<UISelectEntity>) this.attrs.get("figuras");
			figura= figuras.get(figuras.indexOf((UISelectEntity) this.attrs.get("figura")));
			seleccionado= (Entity) this.attrs.get("seleccionado");			
			JsfBase.setFlashAttribute("opcionResidente", (EOpcionesResidente)this.attrs.get("opcionResidente"));												
			if(this.attrs.get("opcionAdicional")!= null)
				JsfBase.setFlashAttribute("opcionAdicional", (EOpcionesResidente)this.attrs.get("opcionAdicional"));												
			JsfBase.setFlashAttribute("seleccionado", seleccionado);												
			JsfBase.setFlashAttribute("figura", figura);									
			JsfBase.setFlashAttribute("idDepartamento", Long.valueOf(this.attrs.get("especialidad").toString()));									
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));				
			JsfBase.setFlashAttribute("georreferencia", new Point(Numero.getDouble(seleccionado.toString("latitud"), 21.890563), Numero.getDouble(seleccionado.toString("longitud"), -102.252030)));							
			JsfBase.setFlashAttribute("flujo", "filtro");										
			regresar= "entrega".concat(Constantes.REDIRECIONAR);										
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