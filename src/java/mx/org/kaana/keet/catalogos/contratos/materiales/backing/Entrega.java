package mx.org.kaana.keet.catalogos.contratos.materiales.backing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.keet.catalogos.contratos.materiales.reglas.Transaccion;
import mx.org.kaana.keet.catalogos.contratos.vales.beans.DetalleVale;

@Named(value = "keetCatalogosContratosMaterialesEntrega")
@ViewScoped
public class Entrega extends IBaseFilter implements Serializable {

	private static final long serialVersionUID = 2847354766000406350L;  		
	private List<DetalleVale> materiales;

	public List<DetalleVale> getMateriales() {
		return materiales;
	}

	public void setMateriales(List<DetalleVale> materiales) {
		this.materiales = materiales;
	}
	
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
			this.attrs.put("opcionAdicional", JsfBase.getFlashAttribute("opcionAdicional"));			
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
		Map<String, Object>params= new HashMap<>();
		try {
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
			this.toQr();
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
    try {    						
			params= new HashMap<>();
			params.put("idVale", ((Entity)this.attrs.get("seleccionadoPivote")).getKey());
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			this.materiales= DaoFactory.getInstance().toEntitySet(DetalleVale.class, "VistaValesDetallesDto", "entrega", params);			
			this.attrs.put("totalRegistros", this.materiales.size());			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {            
      Methods.clean(params);
    } // finally		
  } // doLoad	  					
	
	private void toQr(){
		StringBuilder cadenaQr= null;
		Entity figura         = null;
		Entity seleccionado   = null;
		try {			
			figura= (Entity) this.attrs.get("figura");
			seleccionado= (Entity) this.attrs.get("seleccionadoPivote");
			cadenaQr= new StringBuilder();
			cadenaQr.append(seleccionado.toString("idEmpresa")).append("-");
			cadenaQr.append(seleccionado.toString("consecutivo")).append("-");
			cadenaQr.append(figura.toLong("tipo").equals(1L) ? seleccionado.toLong("idContratoLoteContratista") : seleccionado.toLong("idContratoLoteProveedor")).append("-");
			cadenaQr.append(figura.toString("nombreCompleto")).append("-");
			cadenaQr.append(Fecha.formatear(Fecha.FECHA_HORA_LARGA, seleccionado.toTimestamp("registro")));
			this.attrs.put("qr", cadenaQr.toString());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toQr	
	
	public String doAceptar() {
    String regresar        = null;    				
		Transaccion transaccion= null;
    try {					
			if(validarSeleccion()){
				transaccion= new Transaccion(((Entity)this.attrs.get("seleccionadoPivote")).getKey(), this.materiales);
				if(transaccion.ejecutar(EAccion.AGREGAR)){
					JsfBase.setFlashAttribute("claveGenerada", transaccion.getClave());
					JsfBase.setFlashAttribute("idsGenerados", transaccion.getIdsGenerados());
					JsfBase.setFlashAttribute("idsProcesados", transaccion.getIds());
					JsfBase.setFlashAttribute("qr", this.attrs.get("qr"));
					toSetFlash();
					regresar= "evidencia".concat(Constantes.REDIRECIONAR);						
				} // if
				else
					JsfBase.addMessage("Entrega de material", "Ocurri� un error al procesar la entrega de material", ETipoMensaje.ERROR);
			} // if
			else
				JsfBase.addMessage("Entrega de material", "No se ha seleccionado ningun material para realizar la entrega", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina	  
	
	private boolean validarSeleccion(){
		boolean regresar= false;
		int count       = 0;
		try {
			for(DetalleVale detalle: this.materiales){
				if(detalle.isCheck())					
					count++;
			} // for
			regresar= count>= 1;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // validarSeleccion
	
	private void toSetFlash(){
		JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));									
		JsfBase.setFlashAttribute("figura", this.attrs.get("figura"));											
		JsfBase.setFlashAttribute("idDepartamento", this.attrs.get("idDepartamento"));	
		JsfBase.setFlashAttribute("opcionResidente", this.attrs.get("opcionResidente"));									
		JsfBase.setFlashAttribute("opcionAdicional", this.attrs.get("opcionAdicional"));					
		JsfBase.setFlashAttribute("flujo", "entrega");										
		JsfBase.setFlashAttribute("seleccionado", this.attrs.get("seleccionadoPivote"));											
	} // toSetFlash
	
	public String doCancelar() {
    String regresar          = null;    
		EOpcionesResidente opcion= null;		
    try {			
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("idDesarrolloProcess", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("figura", this.attrs.get("figura"));
			JsfBase.setFlashAttribute("idDepartamento", this.attrs.get("idDepartamento"));									
			JsfBase.setFlashAttribute("opcionResidente", opcion);			
			JsfBase.setFlashAttribute("opcionAdicional", this.attrs.get("opcionAdicional"));			
			JsfBase.setFlashAttribute("flujo", "resumen");										
			JsfBase.setFlashAttribute("seleccionado", this.attrs.get("seleccionadoPivote"));									
			regresar= "filtro".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
}