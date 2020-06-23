package mx.org.kaana.keet.cajachica.backing;

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
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.cajachica.reglas.Transaccion;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCajaChicaAbonar")
@ViewScoped
public class Abonar extends IBaseAttribute implements Serializable {

	private static final long serialVersionUID= 2847354766000406350L;  			
	
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
			this.attrs.put("idCajaChicaCierre", JsfBase.getFlashAttribute("idCajaChicaCierre"));						
			this.attrs.put("semana", JsfBase.getFlashAttribute("semana"));						
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));						
			this.attrs.put("opcionResidente", opcion);						
			this.attrs.put("idDesarrollo", idDesarrollo);      						
			doLoad();											
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init	
	  
  public void doLoad() {			
		Entity desarrollo        = null;		
		Entity cajaChica         = null;		
		Map<String, Object>params= null;
		List<Columna>campos      = null;
    try {    			
			campos= new ArrayList<>();
			campos.add(new Columna("saldo", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			campos.add(new Columna("acumulado", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			campos.add(new Columna("disponible", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "tc_keet_desarrollos.id_desarrollo=".concat(this.attrs.get("idDesarrollo").toString()));
			desarrollo= (Entity) DaoFactory.getInstance().toEntity("VistaDesarrollosDto", "lazy", params);
			this.attrs.put("desarrollo", desarrollo);
			params.clear();
			params.put("idCajaChicaCierre", this.attrs.get("idCajaChicaCierre").toString());
			cajaChica= (Entity) DaoFactory.getInstance().toEntity("VistaCajaChicaDto", "findCajaChicaCierre", params);
			UIBackingUtilities.toFormatEntity(cajaChica, campos);
			this.attrs.put("cajaChica", cajaChica);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch    
		finally{
			Methods.clean(params);
		} // finally	
  } // doLoad	  	
	
	public String doAceptar() {
    String regresar        = null;    		
		Transaccion transaccion= null;				
    try {												
			transaccion= new Transaccion(Long.valueOf(this.attrs.get("idCajaChicaCierre").toString()), Double.valueOf(this.attrs.get("importe").toString()), this.attrs.get("observaciones").toString(), (Long) this.attrs.get("idDesarrollo"));
			if(transaccion.ejecutar(EAccion.ASIGNAR)){
				JsfBase.addMessage("Abonar a caja chica", "Se realizó el abono de forma correcta.", ETipoMensaje.INFORMACION);									
				regresar= "filtro".concat(Constantes.REDIRECIONAR);
			} // if
			else
				JsfBase.addMessage("Abonar a caja chica", "Ocurrió un error al realizar el abono.", ETipoMensaje.ERROR);						
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina  		
		
	public String doCancelar() {
    String regresar          = null;    		
    try {												
			regresar= "filtro".concat(Constantes.REDIRECIONAR);						
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar			
}