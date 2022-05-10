package mx.org.kaana.keet.catalogos.contratos.personal.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.catalogos.contratos.beans.ContratoPersonal;
import mx.org.kaana.keet.catalogos.contratos.personal.reglas.Transaccion;
import mx.org.kaana.keet.catalogos.contratos.reglas.MotorBusqueda;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.keet.enums.ETiposIncidentes;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.enums.EEstatusIncidentes;
import mx.org.kaana.mantic.incidentes.beans.Incidente;
import org.primefaces.event.ScheduleEntryMoveEvent;
import org.primefaces.event.ScheduleEntryResizeEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

@Named(value = "keetCatalogosContratosPersonalIncidencias")
@ViewScoped
public class Incidencias extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID      = 8793667741599428879L;		
	private static final String[] FA_ICON_INCIDENCIA= {"fa-user-times", "fa-ship", "fa-male", "fa-user-md", "fa-user-md", "fa-user-md", "fa-user-md", "fa-user-md", "fa-user-md", "fa-user-md", "fa-user-md", "fa-user-md", "fa-user-md", "fa-user-md", "fa-user-md", "fa-user-md", "fa-user-md", "fa-user-md", "fa-user-md", "fa-user-md"};
	private static final String[] FA_ICON_ESTATUS   = {"fa-plus-square", "fa-check-square", "fa-bullseye", "fa-times-circle", "fa-check-square-o"};

	private ScheduleModel eventModel;
	private ScheduleEvent event;
	private ContratoPersonal contratoPersonal;		
	private Integer count;
	private List<Incidente> incidenciasDelete;

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
			this.attrs.put("observaciones", "");			
			this.count= 10000;
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");			
			idContratoPersona= (Long) JsfBase.getFlashAttribute("idContratoPersona");			
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("idDesarrollo", idDesarrollo);
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());			
			this.attrs.put("idContratoPersona", idContratoPersona);			
			this.incidenciasDelete= new ArrayList<>();
			this.loadTiposIncidentes();
			this.loadEmpleado();
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
  
	private void loadTiposIncidentes() {
		List<UISelectItem> tiposIncidentes= null;
		Map<String, Object>params         = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			params.put("grupo", 1L);
			tiposIncidentes= UISelect.build("TcManticTiposIncidentesDto", "byGrupo", params, "nombre", " ", EFormatoDinamicos.MAYUSCULAS);
			this.attrs.put("tiposIncidentes", tiposIncidentes);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // loadTiposInicidentes
	
	private void loadEmpleado() throws Exception {
		MotorBusqueda motor= null;
		try {
			motor= new MotorBusqueda((Long)this.attrs.get("idContratoPersona"));
			this.contratoPersonal= motor.toPersonaIncidencia(true);						
			this.eventModel= new DefaultScheduleModel();
			this.event= new DefaultScheduleEvent();
			this.loadIncidencias();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // loadCatalogos			
	
	private void loadIncidencias() throws Exception {
		DefaultScheduleEvent registro= null;
		List<Incidente> incidencias  = null;
		MotorBusqueda motor          = null;
		try {
			motor      = new MotorBusqueda((Long)this.attrs.get("idContratoPersona"));
			incidencias= motor.toIncidencias(1L);
			if(!incidencias.isEmpty()){
				for(Incidente incidente: incidencias) {
					incidente.setAccion(ESql.UPDATE);
					String icon= this.toShowIcon(incidente);
					registro= DefaultScheduleEvent.builder()
									.id(incidente.getIdIncidente().toString())
									.allDay(true)
									.data(incidente)
									.description(icon)
									.title(icon)
									.startDate(incidente.getVigenciaInicio().atStartOfDay())
									.endDate(incidente.getVigenciaFin().atStartOfDay())
									.styleClass(ETiposIncidentes.fromId(incidente.getIdTipoIncidente()).getStyleClass().concat(" janal-incidencia incidencia-".concat(incidente.getIdIncidente().toString())))
									.build();
					this.eventModel.addEvent(registro);
				} // for
			} // for
		} // try
		finally {			
			registro= null;
		} // finally		
	} // loadIncidencias	
	
	private String toShowIcon(Incidente newIncidente) {
		StringBuilder sb= new StringBuilder();
		sb.append("<div id='janal-update-").
		append(newIncidente.getIdIncidente()).
		append("'><span class='lg-pantalla'>").
		append(newIncidente.getTipoIncidente()).
		append("\n").
		append(newIncidente.getEstatus()).
		append("</span><span class='xs-pantalla'><i class='fa fa-fw ").
		append(FA_ICON_INCIDENCIA[newIncidente.getIdTipoIncidente().intValue()- 1]).
		append("'></i>").
		append("\n").
		append("<i class='fa fa-fw ").
		append(FA_ICON_ESTATUS[newIncidente.getIdIncidenteEstatus().intValue()- 1]).
		append("'></i>").
		append("</span></div>");
		return sb.toString();
	}
	
	public void onDateSelect(SelectEvent<LocalDateTime> selectEvent) {
		Incidente newIncidente           = null;
		DefaultScheduleEvent defaultEvent= null;
		boolean editable                 = true;
		try {			
			this.count++;
			newIncidente= loadNewIncidente(selectEvent);
			editable= !newIncidente.getIdTipoIncidente().equals(ETiposIncidentes.FALTA.getKey());			
			if(verificaExistente(newIncidente) && verificaExistenteAllTipos(newIncidente)) {
				String icon= this.toShowIcon(newIncidente);
				defaultEvent= DefaultScheduleEvent.builder()
								.id(newIncidente.getIdIncidente().toString())
								.allDay(true)
								.data(newIncidente)
								.description(icon)
								.title(icon)
								.startDate(newIncidente.getVigenciaInicio().atStartOfDay())
								.endDate(newIncidente.getVigenciaFin().atStartOfDay())
								.styleClass(ETiposIncidentes.fromId(newIncidente.getIdTipoIncidente()).getStyleClass().concat(" janal-incidencia incidencia-".concat(newIncidente.getIdIncidente().toString())))
								.editable(editable)
								.build();	
				this.eventModel.addEvent(defaultEvent);
				this.attrs.put("idSelectionEvent", ".incidencia-".concat(newIncidente.getIdIncidente().toString()));				
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
	
	private boolean verificaExistenteAllTipos(Incidente incidente) throws Exception{
		boolean regresar           = true;
		long totalDias             = 0L;
		LocalDate initDate         = null;		
		List<Incidente> incidencias= null;
		MotorBusqueda motor        = null;
		try {			
			motor= new MotorBusqueda((Long)this.attrs.get("idContratoPersona"));
			incidencias= motor.toAllIncidencias();
			for(Incidente incidenteDelete: this.incidenciasDelete){
				if(incidencias.indexOf(incidenteDelete)>= 0)
					incidencias.remove(incidencias.indexOf(incidenteDelete));
			} // for
			totalDias= DAYS.between(incidente.getVigenciaInicio(), incidente.getVigenciaFin());
			initDate = incidente.getVigenciaInicio();
			if(totalDias== 0)
				totalDias=1;
			for(int co=0; co< totalDias; co++){				
				for(Incidente ev: incidencias){
					if(initDate.isEqual(ev.getVigenciaInicio()) || initDate.isEqual(ev.getVigenciaFin()) || (initDate.isAfter(ev.getVigenciaInicio()) && initDate.isBefore(ev.getVigenciaFin()))){
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
	} // verificaExistenteAllTipos
	
	private Incidente loadNewIncidente(SelectEvent<LocalDateTime> selectEvent){
		Incidente regresar= null;
		try {
			regresar= new Incidente();
			regresar.setIdIncidente(this.count.longValue());
			regresar.setAccion(ESql.INSERT);
			regresar.setIdDesarrollo((Long) this.attrs.get("idDesarrollo"));
			regresar.setEstatus(EEstatusIncidentes.CAPTURADA.name());
			regresar.setIdIncidenteEstatus(EEstatusIncidentes.CAPTURADA.getIdEstatusInicidente());
			regresar.setIdEmpresaPersona(this.contratoPersonal.getIdEmpresaPersona());
			regresar.setIdTipoIncidente(ETiposIncidentes.FALTA.getKey());
			regresar.setTipoIncidente(ETiposIncidentes.FALTA.getNombre());
			regresar.setVigenciaInicio(selectEvent.getObject().toLocalDate());
			regresar.setVigenciaFin(selectEvent.getObject().toLocalDate());
			regresar.setEstatusAsociados("2");			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadNewIncidente
	
	public void onEventSelect(SelectEvent<ScheduleEvent> selectEvent) {
		Incidente seleccionado     = null;
		String asociados           = null;
		DateTimeFormatter formatter= null;
		try {
			formatter= DateTimeFormatter.ofPattern("dd/MM/yyyy");
			this.event= selectEvent.getObject();
			seleccionado= (Incidente) this.event.getData();
			// asociados= Cadena.isVacio(seleccionado.getEstatusAsociados()) ? seleccionado.getIdIncidenteEstatus().toString() : seleccionado.getEstatusAsociados().concat(",").concat(seleccionado.getIdIncidenteEstatus().toString());
			asociados= seleccionado.getIdIncidenteEstatus().toString();
			this.doLoadEstatus(asociados);
			this.attrs.put("fechaInicio", this.event.getStartDate().format(formatter));
			this.attrs.put("fechaFin", this.event.getEndDate().format(formatter));
			this.attrs.put("idIncidenteEstatus", seleccionado.getIdIncidenteEstatus());
			this.attrs.put("idIncidenteEstatus", seleccionado.getIdIncidenteEstatus());
			this.attrs.put("idTipoIncidente", seleccionado.getIdTipoIncidente());
			this.attrs.put("observaciones", seleccionado.getObservaciones());
			this.attrs.put("idSelectionEvent", ".incidencia-".concat(seleccionado.getIdIncidente().toString()));
			this.attrs.put("isDelete", seleccionado.getIdIncidenteEstatus().equals(EEstatusIncidentes.CAPTURADA.getIdEstatusInicidente()));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		    
  } // onEventSelect
	
	public void doLoadEstatus(String asociados) {		
		Map<String, Object>params    = null;
		List<UISelectItem> allEstatus= null;		
		try {			
			params= new HashMap<>();			
			params.put("estatusAsociados", asociados);
			allEstatus= UISelect.build("TcManticIncidentesEstatusDto", "estatus", params, "nombre", EFormatoDinamicos.MAYUSCULAS);			
			this.attrs.put("allEstatus", allEstatus);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadEstatus
	
	public void doApplyChange() {			
		DefaultScheduleEvent defaultEvent= null;
		Incidente incidente              = null;
		try {		
			incidente= ((Incidente)this.event.getData());			
			if(!ETiposIncidentes.fromId(Long.valueOf((String)this.attrs.get("idTipoIncidente"))).equals(ETiposIncidentes.FALTA) || (DAYS.between(incidente.getVigenciaInicio(), incidente.getVigenciaFin())== 0 && ETiposIncidentes.fromId(Long.valueOf((String)this.attrs.get("idTipoIncidente"))).equals(ETiposIncidentes.FALTA))){
				((Incidente)this.event.getData()).setEstatus(EEstatusIncidentes.fromIdEstatusIncidente(Long.valueOf((String)this.attrs.get("idIncidenteEstatus"))).name());
				((Incidente)this.event.getData()).setIdIncidenteEstatus(EEstatusIncidentes.fromIdEstatusIncidente(Long.valueOf((String)this.attrs.get("idIncidenteEstatus"))).getIdEstatusInicidente());			
				((Incidente)this.event.getData()).setIdTipoIncidente(ETiposIncidentes.fromId(Long.valueOf((String)this.attrs.get("idTipoIncidente"))).getKey());
				((Incidente)this.event.getData()).setTipoIncidente(ETiposIncidentes.fromId(Long.valueOf((String)this.attrs.get("idTipoIncidente"))).getNombre());			
				((Incidente)this.event.getData()).setObservaciones((String)this.attrs.get("observaciones"));			
				String icon= this.toShowIcon((Incidente)this.event.getData());
				defaultEvent= DefaultScheduleEvent.builder()
									.id(this.event.getId())
									.allDay(this.event.isAllDay())
									.data(this.event.getData())
									.description(icon)
									.title(icon)
									.startDate(((Incidente)this.event.getData()).getVigenciaInicio().atStartOfDay())
									.endDate(((Incidente)this.event.getData()).getVigenciaFin().atStartOfDay())
									.styleClass(ETiposIncidentes.fromId(((Incidente)this.event.getData()).getIdTipoIncidente()).getStyleClass().concat(" janal-incidencia incidencia-".concat(((Incidente)this.event.getData()).getIdIncidente().toString())))
									.build();		
				this.eventModel.updateEvent(defaultEvent);
				this.attrs.put("idSelectionEvent", ".incidencia-".concat(((Incidente)this.event.getData()).getIdIncidente().toString()));				
			} // if
			else
				JsfBase.addAlert("Actualizar falta", "La captura de las faltas solo deben de ser por un día.", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally {
 			this.attrs.put("observaciones", "");
    } // finally
	} // doApplyChange
	
	public void doDelete(){					
		Incidente seleccionado= null;
		try {						
			seleccionado= (Incidente) this.event.getData();
			if(seleccionado.getAccion().equals(ESql.UPDATE))
				this.incidenciasDelete.add(seleccionado);
			this.eventModel.deleteEvent(this.event);		
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doDelete
	
	public void onEventMove(ScheduleEntryMoveEvent event) {       		
		try {
			this.event= event.getScheduleEvent();
			updateEvent();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
  } // onEventMove
     
	public void onEventResize(ScheduleEntryResizeEvent event) {
		try {
			this.event= event.getScheduleEvent();
			if(!ETiposIncidentes.fromId(((Incidente)this.event.getData()).getIdTipoIncidente()).equals(ETiposIncidentes.FALTA))
				updateEvent();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // onEventResize

	private void updateEvent(){
		DefaultScheduleEvent defaultEvent= null;
		try {
			((Incidente)this.event.getData()).setVigenciaInicio(this.event.getStartDate().toLocalDate());
			((Incidente)this.event.getData()).setVigenciaFin(this.event.getEndDate().toLocalDate());	
			String icon= this.toShowIcon((Incidente)this.event.getData());
			defaultEvent= DefaultScheduleEvent.builder()
								.id(this.event.getId())
								.allDay(this.event.isAllDay())
								.data(this.event.getData())
								.description(icon)
								.title(icon)
								.startDate(((Incidente)this.event.getData()).getVigenciaInicio().atStartOfDay())
								.endDate(((Incidente)this.event.getData()).getVigenciaFin().atStartOfDay())
								.styleClass(ETiposIncidentes.fromId(((Incidente)this.event.getData()).getIdTipoIncidente()).getStyleClass().concat(" janal-incidencia incidencia-".concat(((Incidente)this.event.getData()).getIdIncidente().toString())))
								.build();		
			this.eventModel.updateEvent(defaultEvent);
			this.attrs.put("idSelectionEvent", ".incidencia-".concat(((Incidente)this.event.getData()).getIdIncidente().toString()));					
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // updateEvent
	
	public String doAceptar() {
		String regresar        = null;
		Transaccion transaccion= null;
		try {			
			if(this.eventModel.getEvents().size()> 0 || this.incidenciasDelete.size()> 0){
				transaccion= new Transaccion(this.eventModel, this.incidenciasDelete);
				if(transaccion.ejecutar(EAccion.REGISTRAR)){
					JsfBase.setFlashAttribute("opcionResidente", (EOpcionesResidente) this.attrs.get("opcionResidente"));
					JsfBase.setFlashAttribute("idDesarrollo", (Long) this.attrs.get("idDesarrollo"));			
					JsfBase.addMessage("Registro de incidencias", "las incidencias fueron registradas de forma correcta.", ETipoMensaje.INFORMACION);
					regresar= "empleados".concat(Constantes.REDIRECIONAR);
				} // if
				else
					JsfBase.addMessage("Registro de incidencias", "Ocurrio un error al registrar las incidencias.", ETipoMensaje.ERROR);
			} // if
			else
				JsfBase.addMessage("Registro de incidencias", "El empleado no cuenta con ninguna incidencia registrada.", ETipoMensaje.ERROR);
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