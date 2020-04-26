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
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosContratosDestajosConceptos")
@ViewScoped
public class Conceptos extends IBaseFilter implements Serializable {

	private static final long serialVersionUID = 2847354766000406350L;  
	
  @PostConstruct
  @Override
  protected void init() {		
    EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;
		Entity figura            = null;
		Entity seleccionado      = null;
		Long idDepartamento      = null;
    try {
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());						
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");			
			figura= (Entity) JsfBase.getFlashAttribute("figura");	
			seleccionado= (Entity) JsfBase.getFlashAttribute("seleccionado");	
			idDepartamento= Long.valueOf((String)JsfBase.getFlashAttribute("idDepartamento"));	
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("figura", figura);      
			this.attrs.put("seleccionado", seleccionado);      			
			this.attrs.put("idDesarrollo", idDesarrollo);      
			this.attrs.put("idDepartamento", idDepartamento);      			
			this.attrs.put("nombreConcepto", "");      			
			loadCatalogos();			
			doLoad();
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void loadCatalogos(){
		Entity contrato          = null;
		Entity contratoLote      = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato=".concat(((Entity)this.attrs.get("seleccionado")).toString("idContrato")));
			contrato= (Entity) DaoFactory.getInstance().toEntity("VistaContratosLotesDto", "principal", params);
			this.attrs.put("contrato", contrato);
			params.clear();
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos_lotes.id_contrato_lote=".concat(((Entity)this.attrs.get("seleccionado")).getKey().toString()));
			contratoLote= (Entity) DaoFactory.getInstance().toEntity("TcKeetContratosLotesDto", "row", params);
			this.attrs.put("contratoLote", contratoLote);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally	
	} // loadCatalogos		
	
  @Override
  public void doLoad() {
		Map<String, Object>params   = null;
    List<Columna> columns       = null;				
    try {      			
			params= this.toPrepare();
      columns= new ArrayList<>();      
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));                  
	    this.lazyModel= new FormatLazyModel("VistaCapturaDestajosDto", "conceptos", params, columns);			
			UIBackingUtilities.resetDataTable();
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
	
	private Map<String, Object> toPrepare(){
		Map<String, Object> regresar= null;
		try {
			regresar= new HashMap<>();
			regresar.put("idDepartamento", this.attrs.get("idDepartamento"));
			regresar.put("clave", toClaveEstacion());
			regresar.put("estatus", EEstacionesEstatus.INICIAR.getKey() + "," + EEstacionesEstatus.EN_PROCESO.getKey());			
			regresar.put("nombreConcepto", this.attrs.get("nombreConcepto").toString().toUpperCase());			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toPrepare
	
	private String toClaveEstacion(){
		StringBuilder regresar= null;
		try {			
			regresar= new StringBuilder();
			regresar.append(Cadena.rellenar(this.attrs.get("idEmpresa").toString(), 3, '0', true));
			regresar.append(Fecha.getAnioActual());
			regresar.append(Cadena.rellenar(((Entity)this.attrs.get("seleccionado")).toString("ordenContrato"), 3, '0', true));
			regresar.append(Cadena.rellenar(((Entity)this.attrs.get("seleccionado")).toString("orden"), 3, '0', true));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar.toString();
	} // toClaveEstacion
	
	public String doPuntosRevision(Entity seleccionado) {
    String regresar             = null;    		
		List<UISelectEntity> figuras= null;
		UISelectEntity figura       = null;
    try {			
			figuras= (List<UISelectEntity>) this.attrs.get("figuras");
			figura= figuras.get(figuras.indexOf((UISelectEntity) this.attrs.get("figura")));			
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
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("opcionResidente", opcion);			
			regresar= "filtro".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
}