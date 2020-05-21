package mx.org.kaana.mantic.incidentes.reglas;

import java.time.LocalDate;
import static java.time.temporal.ChronoUnit.DAYS;
import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.procesos.enums.EEstatus;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.enums.ETiposIncidentes;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesDto;
import mx.org.kaana.mantic.db.dto.TrManticEmpresaPersonalDto;
import mx.org.kaana.mantic.enums.EEstatusIncidentes;
import mx.org.kaana.mantic.incidentes.beans.Incidente;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	protected Incidente incidente;
	private String messageError;	
	private String observaciones;	
	private boolean estatus;

	public Transaccion(Incidente incidente) {
		this(incidente, "");
	}
	
	public Transaccion(Incidente incidente, String observaciones) {
		this.incidente    = incidente;
		this.observaciones= observaciones;
		this.estatus      = false;
	}

	protected Incidente getIncidente() {
		return incidente;
	}

	protected void setIncidente(Incidente incidente) {
		this.incidente = incidente;
	}	

	public boolean isEstatus() {
		return estatus;
	}

	public void setEstatus(boolean estatus) {
		this.estatus = estatus;
	}	
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
		boolean regresar= false;
		try {
			switch(accion){
				case DESTRANSFORMACION:			
					regresar= registrarIncidente(sesion);					
					break;
				case AGREGAR:			
					if(verificaExistente(sesion))						
						regresar= registrarIncidente(sesion);					
					break;
				case MODIFICAR:									
					if(verificaExistente(sesion))
						regresar= modificarIncidente(sesion, false);
					break;				
				case ASIGNAR:
					regresar= modificarIncidente(sesion, true);
					break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {			
			throw new Exception(this.messageError.concat("<br/>") + e);
		} // catch		
		return regresar;
	} // ejecutar		
	
	private boolean verificaExistente(Session sesion) throws Exception {
		boolean regresar         = true;
		this.messageError = "No es posible agregar una incidencia del mismo tipo al mismo empleado cuando hay una vigente.";
		Long totalDias    = DAYS.between(this.incidente.getVigenciaInicio(), this.incidente.getVigenciaFin());
		LocalDate initDate= this.incidente.getVigenciaInicio();
		if(totalDias== 0)
			totalDias=1L;
		for(int count=0; count< totalDias; count++) {
			Map<String, Object> params= new HashMap<>();
			params.put("idEmpresaPersona", this.incidente.getIdEmpresaPersona());
			params.put("idTipoIncidente", this.incidente.getIdTipoIncidente());
			params.put("estatus", toEstatus());
			params.put("fecha", initDate.toString());
			Entity registro= (Entity) DaoFactory.getInstance().toEntity(sesion, "TcManticIncidentesDto", "existente", params);
			if(registro!= null && registro.isValid()){
				regresar= false;
				break;
			} // if
			else 
				initDate= initDate.plusDays(1);
		} // for
		return regresar;
	} // verificaExistente
	
	private String toEstatus() {
		StringBuilder regresar= null;
		try {
			regresar= new StringBuilder("");
			regresar.append(EEstatusIncidentes.ACEPTADA.getIdEstatusInicidente());
			regresar.append(",");
			regresar.append(EEstatusIncidentes.APLICADA.getIdEstatusInicidente());
			regresar.append(",");
			regresar.append(EEstatusIncidentes.CAPTURADA.getIdEstatusInicidente());		
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar.toString();
	} // toEstatus
	
	private boolean registrarIncidente(Session sesion) throws Exception{
		boolean regresar         = false;
		TcManticIncidentesDto dto= null;
		Long key                 = -1L;
		Siguiente consecutivo    = null;
		try {
			dto= new TcManticIncidentesDto();
			consecutivo= this.toSiguiente(sesion);			
			dto.setConsecutivo(consecutivo.getConsecutivo());			
			dto.setOrden(consecutivo.getOrden());			
			dto.setEjercicio(Long.valueOf(Fecha.getAnioActual()));			
			dto.setIdIncidenteEstatus(this.estatus ? this.incidente.getIdEmpresaPersona() : EEstatusIncidentes.CAPTURADA.getIdEstatusInicidente());			
			if(this.incidente.getIdDesarrollo()!= null && this.incidente.getIdDesarrollo() > 0)
				dto.setIdDesarrollo(this.incidente.getIdDesarrollo());
			dto.setIdEmpresaPersona(this.incidente.getIdEmpresaPersona());
			if(this.incidente.getCosto()!= null && this.incidente.getCosto()>0D)
				dto.setCosto(this.incidente.getCosto());
			dto.setIdTipoIncidente(this.incidente.getIdTipoIncidente());	
			dto.setIdUsuario(JsfBase.getIdUsuario());
			dto.setObservaciones(this.incidente.getObservaciones());
			dto.setInicio(this.incidente.getVigenciaInicio());
			dto.setTermino(this.incidente.getVigenciaFin());		
			key= DaoFactory.getInstance().insert(sesion, dto);
			if(key>= 1L)
				regresar= registrarBitacora(sesion, key, this.estatus ? this.incidente.getIdEmpresaPersona() : EEstatusIncidentes.CAPTURADA.getIdEstatusInicidente());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch	
		return regresar;
	} // registrarIncidente
	
	private boolean registrarBitacora(Session sesion, Long idIncidente, Long idEstatus) throws Exception{
		boolean regresar                 = false;
		TcManticIncidentesBitacoraDto dto= null;
		try {
			dto= new TcManticIncidentesBitacoraDto();
			dto.setIdIncidente(idIncidente);
			dto.setIdIncidenteEstatus(idEstatus);
			dto.setIdUsuario(JsfBase.getIdUsuario());
			dto.setJustificacion(this.incidente.getObservaciones());
			regresar= DaoFactory.getInstance().insert(sesion, dto)>= 1L;
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		return regresar;
	} // registrarBitacora
	
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());			
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticIncidentesDto", "siguiente", params, "siguiente");
			if(next.getData()!= null)
				regresar= new Siguiente(next.toLong());
			else
				regresar= new Siguiente(Configuracion.getInstance().isEtapaDesarrollo()? 900001L: 1L); 
		} // try		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toSiguiente
	
	private boolean modificarIncidente(Session sesion, boolean estatus) throws Exception{
		boolean regresar         = false;
		boolean actualizaEstatus = false;
		TcManticIncidentesDto dto= null;
		try {
			dto= (TcManticIncidentesDto) DaoFactory.getInstance().findById(sesion, TcManticIncidentesDto.class, this.incidente.getIdIncidente());
			actualizaEstatus= dto.getIdIncidenteEstatus().equals(EEstatusIncidentes.CANCELADA.getIdEstatusInicidente()) || this.incidente.getIdIncidenteEstatus().equals(EEstatusIncidentes.CANCELADA.getIdEstatusInicidente());
			if(estatus)
				dto.setIdIncidenteEstatus(this.incidente.getIdIncidenteEstatus());
			dto.setIdEmpresaPersona(this.incidente.getIdEmpresaPersona());
			dto.setIdTipoIncidente(this.incidente.getIdTipoIncidente());			
			dto.setObservaciones(this.incidente.getObservaciones());
			dto.setInicio(this.incidente.getVigenciaInicio());
			dto.setTermino(this.incidente.getVigenciaFin());		
			if(DaoFactory.getInstance().update(sesion, dto)>= 1L){
				this.incidente.setObservaciones(this.observaciones);
				if(registrarBitacora(sesion, this.incidente.getIdIncidente(), dto.getIdIncidenteEstatus())){
					regresar= actualizaEstatusEmpleado(sesion, dto.getIdIncidenteEstatus(), actualizaEstatus);
				} // if
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // modificarIncidente
	
	private boolean actualizaEstatusEmpleado(Session sesion, Long idIncidenteEstatus, boolean actualizaEstatus) throws Exception{
		boolean regresar                  = true;
		TrManticEmpresaPersonalDto persona= null;
		try {
			if(actualizaEstatus && (ETiposIncidentes.ALTA.getKey().equals(this.incidente.getIdTipoIncidente()) || ETiposIncidentes.REINGRESO.getKey().equals(this.incidente.getIdTipoIncidente()) || ETiposIncidentes.BAJA.getKey().equals(this.incidente.getIdTipoIncidente()))){
				persona= (TrManticEmpresaPersonalDto) DaoFactory.getInstance().findById(sesion, TrManticEmpresaPersonalDto.class, this.incidente.getIdEmpresaPersona());
				switch(ETiposIncidentes.fromId(this.incidente.getIdTipoIncidente())){
					case ALTA:
					case REINGRESO:
						if(EEstatusIncidentes.CANCELADA.getIdEstatusInicidente().equals(idIncidenteEstatus)){
							persona.setIdActivo(2L);						
							persona.setIngreso(LocalDate.of(2999, 12, 31));
						} // if
						else{
							persona.setIdActivo(1L);						
							persona.setIngreso(LocalDate.now());
						} // else
						break;
					case BAJA:
						if(EEstatusIncidentes.CANCELADA.getIdEstatusInicidente().equals(idIncidenteEstatus)){
							persona.setIdActivo(1L);
							persona.setIngreso(LocalDate.now());
						} // if
						else{
							persona.setIdActivo(2L);
							persona.setIngreso(LocalDate.of(2999, 12, 31));
						} // else
						break;
				} // switch				
				regresar= DaoFactory.getInstance().update(sesion, persona)>= 1L;
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // actualizaEstatusEmpleado
	
	private boolean eliminarIncidente(Session sesion) throws Exception{
		boolean regresar= false;
		TcManticIncidentesDto dto= null;
		try {
			dto= (TcManticIncidentesDto) DaoFactory.getInstance().findById(sesion, TcManticIncidentesDto.class, this.incidente.getIdIncidente());
			dto.setIdIncidenteEstatus(EEstatusIncidentes.CANCELADA.getIdEstatusInicidente());
			if(DaoFactory.getInstance().update(sesion, dto)>= 1L){
				this.incidente.setObservaciones("La incidencia fue cancelada.");
				regresar= registrarBitacora(sesion, this.incidente.getIdIncidente(), dto.getIdIncidenteEstatus());
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		return regresar;
	} // eliminarIncidente
}
