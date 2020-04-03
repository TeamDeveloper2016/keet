package mx.org.kaana.keet.catalogos.contratos.personal.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.catalogos.contratos.beans.ContratoPersonal;
import mx.org.kaana.keet.catalogos.contratos.reglas.MotorBusqueda;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.keet.enums.ETiposIncidentes;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.mantic.enums.EEstatusIncidentes;
import mx.org.kaana.mantic.incidentes.beans.Incidente;
import org.hibernate.Session;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

@Named(value = "keetCatalogosContratosPersonalIncidencias")
@ViewScoped
public class Incidencias extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;		
	private ScheduleModel eventModel;
	private ScheduleEvent event;
	private ContratoPersonal contratoPersonal;		
	private Integer count;

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

	public ScheduleEvent getEvent() {
		return event;
	}

	public void setEvent(ScheduleEvent event) {
		this.event = event;
	}
	
  @PostConstruct
  @Override
  protected void init() {
		EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;
		Long idContratoPersona   = null;
    try {
			this.count= 0;
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
			this.event= new DefaultScheduleEvent();
			loadIncidencias();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // loadCatalogos			
	
	private void loadIncidencias() throws Exception{
		DefaultScheduleEvent registro= null;
		List<Incidente> incidencias  = null;
		MotorBusqueda motor          = null;
		try {
			motor= new MotorBusqueda((Long)this.attrs.get("idContratoPersona"));
			incidencias= motor.toIncidencias();
			if(!incidencias.isEmpty()){
				for(Incidente incidente: incidencias){
					registro= DefaultScheduleEvent.builder()
									.id(incidente.getIdIncidente().toString())
									.allDay(true)
									.data(incidente)
									.description("Incidencia: ".concat(incidente.getTipoIncidente()).concat(" Estatus: ").concat(incidente.getEstatus()))
									.title("Incidencia: ".concat(incidente.getTipoIncidente()).concat(" Estatus: ").concat(incidente.getEstatus()))
									.startDate(incidente.getVigenciaInicio().atStartOfDay())
									.endDate(incidente.getVigenciaFin().atStartOfDay())
									.styleClass(ETiposIncidentes.fromId(incidente.getIdTipoIncidente()).getStyleClass())
									.build();
					this.eventModel.addEvent(registro);
				} // for
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // loadIncidencias
	
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

	public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
		Incidente newIncidente           = null;
		DefaultScheduleEvent defaultEvent= null;
		try {			
			this.count++;
			newIncidente= loadNewIncidente(selectEvent);			
			if(verificaExistente(newIncidente)){				
				defaultEvent= DefaultScheduleEvent.builder()
								.id(newIncidente.getIdIncidente().toString())
								.allDay(true)
								.data(newIncidente)
								.description("Incidencia: ".concat(newIncidente.getTipoIncidente()).concat(" Estatus: ").concat(newIncidente.getEstatus()))
								.title("Incidencia: ".concat(newIncidente.getTipoIncidente()).concat(" Estatus: ").concat(newIncidente.getEstatus()))
								.startDate(newIncidente.getVigenciaInicio().atStartOfDay())
								.endDate(newIncidente.getVigenciaFin().atStartOfDay())
								.styleClass(ETiposIncidentes.fromId(newIncidente.getIdTipoIncidente()).getStyleClass())
								.build();	
				this.eventModel.addEvent(defaultEvent);
			} // if
			else
				JsfBase.addAlert("Agregar incidencia", "Ya se encuentra registrada una incidencia en esa fecha.", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch				    
  } // onDateSelect
	
	private boolean verificaExistente(Incidente incidente) throws Exception{
		boolean regresar  = true;
		long totalDias    = 0L;
		LocalDate initDate= null;		
		try {			
			totalDias= DAYS.between(incidente.getVigenciaInicio(), incidente.getVigenciaFin());
			initDate = incidente.getVigenciaInicio();
			if(totalDias== 0)
				totalDias=1;
			for(int co=0; co< totalDias; co++){				
				for(ScheduleEvent ev: this.eventModel.getEvents()){
					if(initDate.atStartOfDay().isEqual(ev.getStartDate()) || initDate.atStartOfDay().isEqual(ev.getEndDate()) || (initDate.atStartOfDay().isAfter(ev.getStartDate()) && initDate.atStartOfDay().isBefore(ev.getEndDate()))){
						regresar= false;
						break;
					} // if
				} // for
				if(!regresar)
					break;
				else 
					initDate= initDate.plusDays(1);
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // verificaExistente
	
	private Incidente loadNewIncidente(SelectEvent<LocalDateTime> selectEvent){
		Incidente regresar= null;
		try {
			regresar= new Incidente();
			regresar.setIdIncidente(this.count * -1L);
			regresar.setAccion(ESql.INSERT);
			regresar.setIdDesarrollo((Long) this.attrs.get("idDesarrollo"));
			regresar.setEstatus(EEstatusIncidentes.CAPTURADA.name());
			regresar.setIdIncidenteEstatus(EEstatusIncidentes.CAPTURADA.getIdEstatusInicidente());
			regresar.setIdPersona(this.contratoPersonal.getIdEmpresaPersona());
			regresar.setIdTipoIncidente(ETiposIncidentes.FALTA.getKey());
			regresar.setTipoIncidente(ETiposIncidentes.FALTA.getNombre());
			regresar.setVigenciaInicio(selectEvent.getObject().toLocalDate());
			regresar.setVigenciaFin(selectEvent.getObject().toLocalDate());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadNewIncidente
	
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