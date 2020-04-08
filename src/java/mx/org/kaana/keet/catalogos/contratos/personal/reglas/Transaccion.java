package mx.org.kaana.keet.catalogos.contratos.personal.reglas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.beans.SelectionItem;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.catalogos.contratos.personal.beans.DocumentoIncidencia;
import mx.org.kaana.keet.db.dto.TcKeetContratosPersonalDto;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.incidentes.beans.Incidente;
import org.hibernate.Session;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

public class Transaccion extends mx.org.kaana.mantic.incidentes.reglas.Transaccion {

	private Long idDesarrollo;	
	private List<SelectionItem> empleados;
	private ScheduleModel eventModel;
	private List<DocumentoIncidencia> incidencias;

	public Transaccion(List<DocumentoIncidencia> incidencias) {
		this(new Incidente());
		this.incidencias= incidencias;
	}	
	
	public Transaccion(ScheduleModel eventModel) {
		this(new Incidente());
		this.eventModel= eventModel;
	}		
	
	public Transaccion(Incidente incidente) {
		this(incidente, -1L, new ArrayList<>());
	}
	
	public Transaccion(Long idDesarrollo, List<SelectionItem> empleados) {
		this(new Incidente(), idDesarrollo, empleados);
	}
	
	public Transaccion(Incidente incidente, Long idDesarrollo, List<SelectionItem> empleados) {
		super(incidente);
		this.idDesarrollo= idDesarrollo;		
		this.empleados   = empleados;
	}	
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar              = true;
		Map<String, Object>params     = null;
		TcKeetContratosPersonalDto dto= null;
		Long idUsuario                = -1L;
		Incidente incidente           = null;
		EAccion accionIncidente       = null;
		try {
			switch(accion){
				case PROCESAR:				
					params= new HashMap<>();					
					params.put("idDesarrollo", this.idDesarrollo);
					if(DaoFactory.getInstance().execute(ESql.DELETE, sesion, "TcKeetContratosPersonalDto", "contrato", params)>= 0L){
						idUsuario= JsfBase.getIdUsuario();
						for(SelectionItem item: this.empleados){
							dto= new TcKeetContratosPersonalDto();							
							dto.setIdDesarrollo(this.idDesarrollo);
							dto.setIdEmpresaPersona(Long.valueOf(item.getKey()));
							dto.setIdUsuario(idUsuario);
							dto.setIdVigente(1L);
							dto.setObservaciones("Asignación de empleado al desarrollo " + this.idDesarrollo);
							DaoFactory.getInstance().insert(sesion, dto);
						} // for
					} // if
					break;				
				case REGISTRAR:
					for(ScheduleEvent event: this.eventModel.getEvents()){
						incidente= (Incidente) event.getData();
						switch(incidente.getAccion()){
							case INSERT:
								incidente.setIdIncidente(-1L);
								accionIncidente= EAccion.AGREGAR;								
								break;
							case UPDATE:
								accionIncidente= EAccion.ASIGNAR;								
								break;
						} // switch						
						setIncidente(incidente);
						super.ejecutar(sesion, accionIncidente);
					} // for
					break;
				case SUBIR:
					for(DocumentoIncidencia incidencia: this.incidencias){
						if(DaoFactory.getInstance().insert(sesion, incidencia)>=1L)
							toSaveFile(incidencia.getIdArchivo());
					} // for
					break;
				default:
					super.ejecutar(sesion, accion);
					break;
			} // switch
		} // try
		catch (Exception e) {			
			throw new Exception(e);
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	}	// ejecutar
}