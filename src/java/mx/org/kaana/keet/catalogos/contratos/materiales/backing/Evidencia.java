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
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.keet.catalogos.contratos.materiales.reglas.Transaccion;

@Named(value = "keetCatalogosContratosMaterialesEvidencia")
@ViewScoped
public class Evidencia extends IBaseFilter implements Serializable {

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
			this.attrs.put("isAdmin", JsfBase.isAdminEncuestaOrAdmin());						
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());	
			this.attrs.put("georreferencia", JsfBase.getFlashAttribute("georreferencia"));
			this.attrs.put("opcionAdicional", JsfBase.getFlashAttribute("opcionAdicional"));
			this.attrs.put("qr", JsfBase.getFlashAttribute("qr"));
			this.attrs.put("idsProcesados", JsfBase.getFlashAttribute("idsProcesados"));
			this.attrs.put("idsGenerados", JsfBase.getFlashAttribute("idsGenerados"));
			this.attrs.put("claveGenerada", JsfBase.getFlashAttribute("claveGenerada"));
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");			
			figura= (Entity) JsfBase.getFlashAttribute("figura");	
			seleccionado= (Entity) JsfBase.getFlashAttribute("seleccionado");	
			idDepartamento= (Long)JsfBase.getFlashAttribute("idDepartamento");	
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("figura", figura);      
			this.attrs.put("seleccionadoPivote", seleccionado);      			
			this.attrs.put("idDesarrollo", idDesarrollo);      
			this.attrs.put("idDepartamento", idDepartamento);      			
			this.attrs.put("nombreConcepto", "");    
			this.attrs.put("totalMateriales", 0L);
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
		Entity vale              = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato=".concat(((Entity)this.attrs.get("seleccionadoPivote")).toString("idContrato")));
			contrato= (Entity) DaoFactory.getInstance().toEntity("VistaContratosLotesDto", "principal", params);
			this.attrs.put("contrato", contrato);
			params.clear();
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos_lotes.id_contrato_lote=".concat(((Entity)this.attrs.get("seleccionadoPivote")).toString("idContratoLote")));
			contratoLote= (Entity) DaoFactory.getInstance().toEntity("TcKeetContratosLotesDto", "row", params);
			this.attrs.put("contratoLote", contratoLote);
			params.clear();
			params.put(Constantes.SQL_CONDICION, "tc_keet_vales.id_vale=".concat(((Entity)this.attrs.get("seleccionadoPivote")).getKey().toString()));
			vale= (Entity) DaoFactory.getInstance().toEntity("TcKeetValesDto", "row", params);
			this.attrs.put("vale", vale);			
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
		Map<String, Object>params= null;
    List<Columna> campos     = null;						
    try {    			
			campos= new ArrayList<>();
			campos.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("costo", EFormatoDinamicos.MONEDA_SAT_DECIMALES));
			params= new HashMap<>();
			params.put("idVale", ((Entity)this.attrs.get("seleccionadoPivote")).getKey());
			params.put("clave", this.attrs.get("claveGenerada"));
			this.lazyModel= new FormatLazyModel("VistaEntregaMaterialesDto", "evidencia", params, campos);			
			UIBackingUtilities.resetDataTable();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {      
      Methods.clean(campos);
      Methods.clean(params);
    } // finally		
  } // doLoad	  					
	
	public String doAceptar() {
    String regresar= null;    				
    try {																
			toSetFlash();
			regresar= "resumen".concat(Constantes.REDIRECIONAR);						
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina	  
	
	private void toSetFlash(){
		JsfBase.setFlashAttribute("claveGenerada", this.attrs.get("claveGenerada"));					
		JsfBase.setFlashAttribute("qr", this.attrs.get("qr"));		
		JsfBase.setFlashAttribute("opcionResidente", this.attrs.get("opcionResidente"));									
		JsfBase.setFlashAttribute("figura", this.attrs.get("figura"));									
		JsfBase.setFlashAttribute("seleccionado", this.attrs.get("seleccionadoPivote"));									
		JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));									
		JsfBase.setFlashAttribute("idDepartamento", this.attrs.get("idDepartamento"));											
		JsfBase.setFlashAttribute("georreferencia", this.attrs.get("georreferencia"));
		JsfBase.setFlashAttribute("opcionAdicional", this.attrs.get("opcionAdicional"));			
	} // toSetFlash
	
	public String doCancelar() {
    String regresar          = null;    
		EOpcionesResidente opcion= null;		
		Transaccion transaccion  = null;
    try {			
			transaccion= new Transaccion(((Entity)this.attrs.get("seleccionadoPivote")).getKey(), (List<Long>)this.attrs.get("idsProcesados"), (List<Long>)this.attrs.get("idsGenerados"), this.attrs.get("claveGenerada").toString());
			if(transaccion.ejecutar(EAccion.DEPURAR)){
				opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));
				JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));
				JsfBase.setFlashAttribute("idDesarrolloProcess", this.attrs.get("idDesarrollo"));
				JsfBase.setFlashAttribute("figura", this.attrs.get("figura"));
				JsfBase.setFlashAttribute("idDepartamento", this.attrs.get("idDepartamento"));									
				JsfBase.setFlashAttribute("opcionResidente", opcion);			
				JsfBase.setFlashAttribute("opcionAdicional", this.attrs.get("opcionAdicional"));			
				JsfBase.setFlashAttribute("flujo", "resumen");														
				JsfBase.setFlashAttribute("seleccionado", this.attrs.get("seleccionadoPivote"));																				
				regresar= "entrega".concat(Constantes.REDIRECIONAR);			
			} // if
			else
				JsfBase.addMessage("Entrega de material", "Ocurrió un error al cancelar la entrega de material.", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
}