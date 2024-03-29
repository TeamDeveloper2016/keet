package mx.org.kaana.keet.catalogos.contratos.vales.autorizacion.backing;

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
import mx.org.kaana.keet.catalogos.contratos.vales.autorizacion.reglas.Transaccion;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosContratosValesAutorizacionResumen")
@ViewScoped
public class Resumen extends IBaseFilter implements Serializable {

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
			this.attrs.put("justificacion", "");    
			this.attrs.put("totalMateriales", 0L);
			if(Cadena.isVacio(this.attrs.get("qr")))
				toQr();
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
		List<UISelectItem>cargos = null;
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
			loadTiposVales();
			this.attrs.put("tipoVale", vale.toLong("idTipoVale"));
			this.attrs.put("observaciones", vale.toString("justificacion"));
			cargos= new ArrayList<>();
			cargos.add(new UISelectItem(-1L, "SELECCIONE"));
			cargos.add(new UISelectItem(1L, "SI"));
			cargos.add(new UISelectItem(2L, "NO"));
			this.attrs.put("cargos", cargos);
			this.attrs.put("cargo", UIBackingUtilities.toFirstKeySelectItem(cargos));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally	
	} // loadCatalogos			
	
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
	
	private void loadTiposVales(){
		List<UISelectItem> tiposVales= null;
		Map<String, Object>params    = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_tipo_vale != 1");
			tiposVales= UISelect.seleccione("TcKeetTiposValesDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
			this.attrs.put("tiposVales", tiposVales);			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadTiposvales
	
  @Override
  public void doLoad() {
		Map<String, Object>params= null;
    List<Columna> campos     = null;						
    try {    			
			campos= new ArrayList<>();
			campos.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));			
			campos.add(new Columna("tipoEntrega", EFormatoDinamicos.MAYUSCULAS));			
			campos.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));			
			campos.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));			
			campos.add(new Columna("entregado", EFormatoDinamicos.FECHA_HORA_CORTA));			
			params= new HashMap<>();
			params.put("idVale", ((Entity)this.attrs.get("seleccionadoPivote")).getKey());	
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));			
			this.lazyModel= new FormatLazyModel("VistaEntregaMaterialesDto", "resumen", params, campos);
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
    String regresar        = null;   
		Transaccion transaccion= null;
    try {					
			transaccion= new Transaccion(((Entity)this.attrs.get("seleccionadoPivote")).getKey(), true, this.attrs.get("justificacion").toString(), Long.valueOf(this.attrs.get("cargo").toString()));
			if(transaccion.ejecutar(EAccion.PROCESAR)){			
				JsfBase.addMessage("Aceptar vale", "El vale fue aceptado correctamente", ETipoMensaje.INFORMACION);
				regresar= doCancelar();
			} // if
			else
				JsfBase.addMessage("Aceptar vale", "Ocurri� un error al aceptar el vale", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doAceptar
	
	public String doRechazar() {
    String regresar= null;    				
    Transaccion transaccion= null;
    try {					
			transaccion= new Transaccion(((Entity)this.attrs.get("seleccionadoPivote")).getKey(), false, this.attrs.get("justificacion").toString(), Long.valueOf(this.attrs.get("cargo").toString()));
			if(transaccion.ejecutar(EAccion.PROCESAR)){			
				JsfBase.addMessage("Rechazar vale", "El vale fue rechazado correctamente", ETipoMensaje.INFORMACION);
				regresar= doCancelar();
			} // if
			else
				JsfBase.addMessage("Rechazar vale", "Ocurri� un error al rechazar el vale", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doRechazar	  
	
	public String doCancelar() {
    String regresar= null;    				
    try {																
			toSetFlash();
			regresar= "filtro".concat(Constantes.REDIRECIONAR);						
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar	  
	
	private void toSetFlash(){		
		JsfBase.setFlashAttribute("opcionResidente", this.attrs.get("opcionResidente"));									
		JsfBase.setFlashAttribute("figura", this.attrs.get("figura"));									
		JsfBase.setFlashAttribute("seleccionado", this.attrs.get("seleccionadoPivote"));									
		JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));									
		JsfBase.setFlashAttribute("idDepartamento", this.attrs.get("idDepartamento"));											
		JsfBase.setFlashAttribute("georreferencia", this.attrs.get("georreferencia"));
		JsfBase.setFlashAttribute("opcionAdicional", this.attrs.get("opcionAdicional"));			
	} // toSetFlash		
}