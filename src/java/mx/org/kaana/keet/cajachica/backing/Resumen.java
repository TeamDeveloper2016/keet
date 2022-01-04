package mx.org.kaana.keet.cajachica.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import mx.org.kaana.keet.cajachica.reglas.Transaccion;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.keet.enums.EEstatusGastos;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCajaChicaResumen")
@ViewScoped
public class Resumen extends IBaseFilter implements Serializable {

	private static final long serialVersionUID = 2847354766000406350L;  		
	
  @PostConstruct
  @Override
  protected void init() {		
    EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;		
    try {
			this.attrs.put("isAdmin", JsfBase.isAdminEncuestaOrAdmin());						
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());							
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");						
			this.attrs.put("idGasto", JsfBase.getFlashAttribute("idGasto"));			
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));			
			this.attrs.put("retornoInicial", JsfBase.getFlashAttribute("retornoInicial"));			
			this.attrs.put("consecutivo", JsfBase.getFlashAttribute("consecutivo"));      						
			this.attrs.put("idRechazaGasto", JsfBase.getFlashAttribute("idRechazaGasto")!= null? (Boolean)JsfBase.getFlashAttribute("idRechazaGasto"): false);
			this.attrs.put("opcionResidente", opcion);			
			this.attrs.put("idDesarrollo", idDesarrollo);      									
			this.attrs.put("acciones", true);      									
      this.attrs.put("pathPivote", Configuracion.getInstance().getEtapaServidor().name().toLowerCase().concat("/").concat("gastos").concat("/"));						
			this.loadCatalogos();						
			this.loadCajaChica();
			this.doLoad();					
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void loadCatalogos(){
		Entity desarrollo        = null;
		Entity gasto             = null;		
		Map<String, Object>params= null;		
		try {
			params= new HashMap<>();
      params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
			params.put(Constantes.SQL_CONDICION, "tc_keet_desarrollos.id_desarrollo=".concat(this.attrs.get("idDesarrollo").toString()));
			desarrollo= (Entity) DaoFactory.getInstance().toEntity("VistaDesarrollosDto", "lazy", params);
			this.attrs.put("desarrollo", desarrollo);
			params.clear();
			params.put(Constantes.SQL_CONDICION, "tc_keet_gastos.id_gasto=".concat(this.attrs.get("idGasto").toString()));
			gasto= (Entity) DaoFactory.getInstance().toEntity("VistaTcKeetGastosDto", "row", params);
			this.attrs.put("gasto", gasto);		
      this.toLoadTiposPagos();
			this.attrs.put("idCajaChicaCierre", gasto.toString("idCajaChicaCierre"));
			this.attrs.put("acciones", gasto.toLong("idGastoEstatus").equals(EEstatusGastos.DISPONIBLE.getKey()));
			this.attrs.put("rechazar", 
                     ((Objects.equals(gasto.toLong("idGastoEstatus"), EEstatusGastos.DISPONIBLE.getKey()) || 
                      Objects.equals(gasto.toLong("idGastoEstatus"), EEstatusGastos.ACEPTADO.getKey()))
                      && (Boolean)this.attrs.get("idRechazaGasto")));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally	
	} // loadCatalogos				
	
	private void loadCajaChica() throws Exception{
		Map<String, Object>params= null;
		Entity cajaChica         = null;		
		List<Columna>campos      = null;
    try {    			
			campos= new ArrayList<>();
			campos.add(new Columna("saldo", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			campos.add(new Columna("acumulado", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			campos.add(new Columna("disponible", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			campos.add(new Columna("pendiente", EFormatoDinamicos.NUMERO_CON_DECIMALES));			
			params= new HashMap<>();
			params.put("idCajaChicaCierre", this.attrs.get("idCajaChicaCierre"));			
			cajaChica= (Entity) DaoFactory.getInstance().toEntity("VistaCajaChicaDto", "findCajaChicaCierre", params);
			UIBackingUtilities.toFormatEntity(cajaChica, campos);
			this.attrs.put("cajaChica", cajaChica);		
		} // try
		catch (Exception e) {			
			throw e;  
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // loadCajaChica
	
  @Override
  public void doLoad() {
		Map<String, Object>params= null;
    List<Columna> campos     = null;						
    try {    			
			campos= new ArrayList<>();
			campos.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));						
			campos.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));			
			campos.add(new Columna("cantidad", EFormatoDinamicos.NUMERO_CON_DECIMALES));			
			campos.add(new Columna("importe", EFormatoDinamicos.NUMERO_CON_DECIMALES));			
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_gasto=".concat(this.attrs.get("idGasto").toString()));				
			this.lazyModel= new FormatLazyModel("TcKeetGastosDetallesDto", "row", params, campos);
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
			transaccion= new Transaccion(((Entity)this.attrs.get("gasto")).getKey(), true);
			if(transaccion.ejecutar(EAccion.MODIFICAR)) {
				JsfBase.addMessage("Aceptar gasto", "El gasto fue aceptado correctamente.", ETipoMensaje.INFORMACION);
				regresar= doCancelar();
				JsfBase.setFlashAttribute("idGasto", null);			
			} // if
			else
				JsfBase.addMessage("Aceptar gasto", "Ocurrió un error al aceptar el gasto.", ETipoMensaje.INFORMACION);
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
			transaccion= new Transaccion(((Entity)this.attrs.get("gasto")).getKey(), false);
			if(transaccion.ejecutar(EAccion.MODIFICAR)){			
				JsfBase.addMessage("Rechazar gasto", "El gasto fue rechazado correctamente.", ETipoMensaje.INFORMACION);
				regresar= doCancelar();
				JsfBase.setFlashAttribute("idGasto", null);			
			} // if
			else
				JsfBase.addMessage("Rechazar gasto", "Ocurrió un error al rechazar el gasto.", ETipoMensaje.INFORMACION);
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
			regresar= this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);						
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar	  
	
	private void toSetFlash(){		
		JsfBase.setFlashAttribute("idGasto", this.attrs.get("idGasto"));			
		JsfBase.setFlashAttribute("retornoInicial", this.attrs.get("retornoInicial"));											
		JsfBase.setFlashAttribute("opcionResidente", this.attrs.get("opcionResidente"));											
		JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));											
	} // toSetFlash		
  
	private void toLoadTiposPagos() {
		List<UISelectEntity> tiposMediosPagos= null;
		Map<String, Object>params            = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_cobro_caja=1");
			tiposMediosPagos= UIEntity.build("TcManticTiposMediosPagosDto", "row", params);
			this.attrs.put("tiposMediosPagos", tiposMediosPagos);
      if(tiposMediosPagos!= null && !tiposMediosPagos.isEmpty()) {
        int index= tiposMediosPagos.indexOf(new UISelectEntity(((Entity)this.attrs.get("gasto")).toLong("idTipoMedioPago")));
        if(index>= 0)
          this.attrs.put("idTipoMedioPago", tiposMediosPagos.get(index));          
        else
          this.attrs.put("idTipoMedioPago", UIBackingUtilities.toFirstKeySelectEntity(tiposMediosPagos));
      } // if
      this.doLoadImportados();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toLoadTiposPagos
  
	public void doLoadImportados() {
  	String dns= Configuracion.getInstance().getPropiedad("sistema.dns.".concat(Configuracion.getInstance().getEtapaServidor().name().toLowerCase()));			
		String url= dns.substring(0, dns.indexOf(JsfBase.getContext())).concat("/").concat((String)this.attrs.get("pathPivote"));
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
		try {
			columns= new ArrayList<>();      
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      params.put("idGasto", this.attrs.get("idGasto"));
      params.put("url", url);
		  this.attrs.put("importados", UIEntity.build("VistaGastosDto", "importados", params, columns));
		} // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    }// finally
	} // doLoadImportados
  
}