package mx.org.kaana.keet.catalogos.contratos.personal.backing;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.keet.catalogos.contratos.beans.ContratoPersonal;
import mx.org.kaana.keet.catalogos.contratos.reglas.MotorBusqueda;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

@Named(value = "keetCatalogosContratosPersonalIncidencias")
@ViewScoped
public class Incidencias extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;		
	private ScheduleModel eventModel;
	private ContratoPersonal contratoPersonal;		

	public ContratoPersonal getContratoPersonal() {
		return contratoPersonal;
	}

	public void setContratoPersonal(ContratoPersonal contratoPersonal) {
		this.contratoPersonal = contratoPersonal;
	}	

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}	
	
  @PostConstruct
  @Override
  protected void init() {
		EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;
		Long idContratoPersona   = null;
    try {
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");			
			idContratoPersona= (Long) JsfBase.getFlashAttribute("idContratoPersona");			
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("idDesarrollo", idDesarrollo);
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());			
			this.attrs.put("idContratoPersona", idContratoPersona);
			loadEmpleado();
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
  
	private void loadEmpleado() throws Exception{
		MotorBusqueda motor= null;
		try {
			motor= new MotorBusqueda((Long)this.attrs.get("idContratoPersona"));
			this.contratoPersonal= motor.toPersonaIncidencia();						
			this.eventModel= new DefaultScheduleModel();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // loadCatalogos			
	
	public String doAceptar(){
		String regresar= null;
		try {
			JsfBase.setFlashAttribute("opcionResidente", (EOpcionesResidente) this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("idDesarrollo", (Long) this.attrs.get("idDesarrollo"));			
			regresar= "empleados".concat(Constantes.REDIRECIONAR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		return regresar;
	} // doAceptar
	
	public String doCancelar() {
    String regresar= null;    		
    try {			
			JsfBase.setFlashAttribute("opcionResidente", (EOpcionesResidente) this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("idDesarrollo", (Long) this.attrs.get("idDesarrollo"));			
			regresar= "empleados".concat(Constantes.REDIRECIONAR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
}