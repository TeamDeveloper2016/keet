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
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCajaChicaCierre")
@ViewScoped
public class Cierre extends IBaseAttribute implements Serializable {

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
			this.attrs.put("idAfectaNomina", 1L);
			loadResidentes();
			doLoad();											
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
  
	private void loadResidentes(){
		List<UISelectEntity> residentes= null;
		List<Columna> campos           = null;
		Map<String, Object> params     = null;
		try {
			params= new HashMap<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			campos= new ArrayList<>();		
			campos.add(new Columna("nombreCompleto", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("departamento", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("puesto", EFormatoDinamicos.MAYUSCULAS));
			residentes= UIEntity.seleccione("VistaGeoreferenciaLotesDto", "residentes", params, campos, Constantes.SQL_TODOS_REGISTROS, "departamento");
			this.attrs.put("residentes", residentes);
			this.attrs.put("residente", UIBackingUtilities.toFirstKeySelectEntity(residentes));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // loadResidentes
	
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
			campos.add(new Columna("pendiente", EFormatoDinamicos.NUMERO_CON_DECIMALES));			
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "tc_keet_desarrollos.id_desarrollo=".concat(this.attrs.get("idDesarrollo").toString()));
			desarrollo= (Entity) DaoFactory.getInstance().toEntity("VistaDesarrollosDto", "lazy", params);
			this.attrs.put("desarrollo", desarrollo);
			params.clear();
			params.put("idCajaChicaCierre", this.attrs.get("idCajaChicaCierre").toString());
			cajaChica= (Entity) DaoFactory.getInstance().toEntity("VistaCajaChicaDto", "findCajaChicaCierre", params);
			this.attrs.put("importe", cajaChica.toDouble("acumulado"));
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
    String regresar                = null;    		
		Transaccion transaccion        = null;	
		UISelectEntity residente       = null;
		UISelectEntity seleccionado    = null;
		List<UISelectEntity> residentes= null;
    try {									
			seleccionado= (UISelectEntity) this.attrs.get("residente");
			residentes= (List<UISelectEntity>) this.attrs.get("residentes");
			residente= residentes.get(residentes.indexOf(seleccionado));
			transaccion= new Transaccion(Long.valueOf(this.attrs.get("idCajaChicaCierre").toString()), Double.valueOf(this.attrs.get("importe").toString()), this.attrs.get("observaciones").toString(), Long.valueOf(this.attrs.get("idAfectaNomina").toString()), (Long)this.attrs.get("idDesarrollo"), residente.toLong("idEmpresaPersona"));
			if(transaccion.ejecutar(EAccion.ACTIVAR)){
				JsfBase.addMessage("Cierre de caja chica", "Se realizó el cierre de caja chica de forma correcta.", ETipoMensaje.INFORMACION);									
				regresar= "filtro".concat(Constantes.REDIRECIONAR);
			} // if
			else
				JsfBase.addMessage("Cierre de caja chica", "Ocurrió un error al realizar el cierre de caja chica.", ETipoMensaje.ERROR);						
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina  	
		
	public String doCancelar() {
    String regresar= null;    		
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