package mx.org.kaana.keet.controles.reglas;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.controles.beans.DestajoResidenteArchivo;
import mx.org.kaana.keet.controles.beans.ConceptoExtra;
import mx.org.kaana.keet.controles.beans.Revision;
import mx.org.kaana.keet.catalogos.contratos.destajos.comun.IBaseDestajoArchivo;
import mx.org.kaana.keet.db.dto.TcKeetContratosDestajosResidentesDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosPuntosResidentesDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosRechazosResidentesDto;
import mx.org.kaana.keet.db.dto.TcKeetControlesDto;
import mx.org.kaana.keet.enums.EControlesEstatus;
import mx.org.kaana.keet.nomina.reglas.Semanas;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Seguimiento extends IBaseTnx {

	private static final Log LOG       = LogFactory.getLog(Seguimiento.class);
	private static final Long CANCELADO= 4L;
	private Revision revision;	
	private Double factorAcumulado;
	private List<IBaseDestajoArchivo>documentos;	
	private ConceptoExtra conceptoExtra;
	private Long idEstatus;

	public Seguimiento(Revision revision, Long idEstatus) {
		this.revision = revision;		
		this.idEstatus= idEstatus;
	}		

	public Seguimiento(List<IBaseDestajoArchivo> documentos) {
		this.documentos= documentos;
	}

	public Seguimiento(ConceptoExtra conceptoExtra) {
		this.conceptoExtra= conceptoExtra;
	}	
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar         = true;
		Map<String, Object>params= null;
		Long idUsuario           = -1L;
		IBaseDto dto             = null;
		try {
			switch(accion){
				case PROCESAR:									
					idUsuario= JsfBase.getIdUsuario();
					this.factorAcumulado= 0D;
  				regresar= processDestajoContratista(sesion, idUsuario);
					break;						
				case REPROCESAR:									
					idUsuario= JsfBase.getIdUsuario();
					this.factorAcumulado= 0D;
					regresar= processRechazoContratista(sesion, idUsuario);
					break;						
				case SUBIR:
					for(IBaseDestajoArchivo incidencia: this.documentos){
  					dto= (DestajoResidenteArchivo) incidencia;
						if(DaoFactory.getInstance().insert(sesion, dto)>= 1L)
							toSaveFile(incidencia.getIdArchivo());
					} // for
					break;
				case AGREGAR:					
					regresar= agregarConceptoExtra(sesion);					
					break;
				case ELIMINAR:
					idUsuario= JsfBase.getIdUsuario();
					this.factorAcumulado= 0D;
					regresar= eliminarConceptoExtraContratista(sesion, idUsuario);
					break;
			} // switch
		} // try
		catch (Exception e) {			
			throw new Exception((e!= null? e.getCause().toString(): ""));
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	}	// ejecutar	
	
	private boolean processDestajoContratista(Session sesion, Long idUsuario) throws Exception {
		boolean regresar= false;
		Long key        = -1L;
		TcKeetContratosDestajosResidentesDto dto= null;
		TcKeetControlesDto control= null;
		Map<String, Object>params = null;
		boolean inicioTrabajo     = false;
		try {
			control= (TcKeetControlesDto) DaoFactory.getInstance().findById(sesion, TcKeetControlesDto.class, this.revision.getIdControl());
			inicioTrabajo= validaInicioTrabajo(sesion, null);
			dto= new TcKeetContratosDestajosResidentesDto();		
			dto.setIdUsuario(idUsuario);
			dto.setSemana(toSemana());
			dto.setPeriodo(toPeriodo());
			dto.setIdControl(this.revision.getIdControl());
			dto.setIdContratoLoteResidente(this.revision.getIdFigura());
			dto.setIdNomina(null);
			dto.setCosto(0D);
			dto.setPorcentaje(0D);
			dto.setIdControlEstatus(EControlesEstatus.INICIAR.getKey());
			key= DaoFactory.getInstance().insert(sesion, dto);
			if(processPuntosContratistas(sesion, idUsuario, key)){				
				dto.setPorcentaje(this.factorAcumulado);
				dto.setCosto((control.getCosto() * this.factorAcumulado) / 100);
				dto.setIdControlEstatus(this.idEstatus); 
				if(DaoFactory.getInstance().update(sesion, dto)>= 1L){
					params= new HashMap<>();
					params.put("idControlEstatus", dto.getIdControlEstatus());
					params.put("cargo".concat(dto.getSemana().toString()), (control.toValue("cargo".concat(dto.getSemana().toString())) != null ? ((Double)control.toValue("cargo".concat(dto.getSemana().toString()))) : 0D) + dto.getCosto());										
					if(inicioTrabajo){
						actualizaInicioContratoLote(sesion, true);
						//params.put("abono".concat(dto.getSemana().toString()), dto.getCosto());
					} // if
					if(DaoFactory.getInstance().update(sesion, TcKeetControlesDto.class, this.revision.getIdControl(), params)>= 1L)
						regresar= actualizaEstacionPadre(sesion, control, dto.getCosto(), dto.getSemana().toString(), true);											
				} // if				
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadContratista
	
	private boolean processRechazoContratista(Session sesion, Long idUsuario) throws Exception{
		boolean regresar= false;		
		TcKeetContratosDestajosResidentesDto dto= null;
		TcKeetControlesDto control= null;
		Map<String, Object>params = null;
		Double costo              = 0D;
		Double porcentaje         = 0D;
		try {			
			for(Entity puntoRevision: this.revision.getPuntosRevision()) {
				this.factorAcumulado= 0D;
				params= new HashMap<>();
				params.put("idControl", this.revision.getIdControl());
				params.put("idContratoLoteResidente", this.revision.getIdFigura());
				params.put("idControlEstatus", CANCELADO);
				params.put("idPuntoPaquete", puntoRevision.getKey()); 
				dto= (TcKeetContratosDestajosResidentesDto) DaoFactory.getInstance().toEntity(sesion, TcKeetContratosDestajosResidentesDto.class, "VistaCapturaDestajosDto", "evidenciaDestajoResidente", params);					
				costo= dto.getCosto()!= null ? dto.getCosto(): 0D;
				porcentaje= dto.getPorcentaje()!= null ? dto.getPorcentaje(): 0D;
				if(processPuntosResidentes(sesion, idUsuario, puntoRevision, dto.getIdContratoDestajoResidente())) {
					control= (TcKeetControlesDto) DaoFactory.getInstance().findById(sesion, TcKeetControlesDto.class, this.revision.getIdControl());
					dto.setPorcentaje(porcentaje - this.factorAcumulado);
					dto.setCosto(costo - ((control.getCosto() * this.factorAcumulado) / 100));
					dto.setIdControlEstatus(CANCELADO);
					if(DaoFactory.getInstance().update(sesion, dto)>= 1L){
						params.clear();
						params.put("idControlEstatus", this.idEstatus);
						params.put("cargo".concat(dto.getSemana().toString()), (control.toValue("cargo".concat(dto.getSemana().toString()))!= null ? ((Double)control.toValue("cargo".concat(dto.getSemana().toString()))) : null) - costo);											
						if(validaInicioTrabajo(sesion, dto.getIdContratoDestajoResidente())) {				
							actualizaInicioContratoLote(sesion, false);
						} // if
						if(DaoFactory.getInstance().update(sesion, TcKeetControlesDto.class, this.revision.getIdControl(), params)>= 1L)
							regresar= actualizaEstacionPadre(sesion, control, costo, dto.getSemana().toString(), false);											
					} // if				
				} // if			
			} // for			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadContratista
	
	private boolean processPuntosContratistas(Session sesion, Long idUsuario, Long idResidenteDestajo) throws Exception{
		boolean regresar= true;
		TcKeetContratosPuntosResidentesDto dto= null;
		try {
			for(Entity puntoRevision: this.revision.getPuntosRevision()) {
				dto= new TcKeetContratosPuntosResidentesDto();
				dto.setFactor(puntoRevision.toDouble("factor"));
				dto.setIdContratoDestajoResidente(idResidenteDestajo);
				dto.setIdPuntoPaquete(puntoRevision.getKey());
				dto.setIdRevisado(2L);
				dto.setIdUsuario(idUsuario);
				dto.setLatitud(this.revision.getLatitud());
				dto.setLongitud(this.revision.getLongitud());
				dto.setDistancia(this.revision.getMetros());
				DaoFactory.getInstance().insert(sesion, dto);
				this.factorAcumulado= this.factorAcumulado + dto.getFactor();
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // processPuntosContratistas
	
	private boolean processPuntosResidentes(Session sesion, Long idUsuario, Entity puntoRevision, Long idContratoDestajoResidente) throws Exception{
		return processPuntosResidentes(sesion, idUsuario, puntoRevision, idContratoDestajoResidente, true);
	} // processPuntosResidentes
	
	private boolean processPuntosResidentes(Session sesion, Long idUsuario, Entity puntoRevision, Long idContratoDestajoResidente, boolean detalle) throws Exception{
		boolean regresar= true;
		TcKeetContratosRechazosResidentesDto dto= null;
		TcKeetContratosPuntosResidentesDto punto= null;
		Map<String, Object>params= null;		
		try {				
				params= new HashMap<>();
				params.put("idContratoDestajoResidente", idContratoDestajoResidente);
				params.put("idPuntoPaquete", puntoRevision.getKey());
				punto= (TcKeetContratosPuntosResidentesDto) DaoFactory.getInstance().toEntity(sesion, TcKeetContratosPuntosResidentesDto.class, "TcKeetContratosPuntosResidentesDto", "puntoPivote", params);
				if(detalle) {
					dto= new TcKeetContratosRechazosResidentesDto();
					dto.setIdContratoDestajoResidente(idContratoDestajoResidente);
					dto.setIdPuntoPaquete(puntoRevision.getKey());
					dto.setIdUsuario(idUsuario);
					dto.setObservaciones(this.revision.getObservaciones());
					dto.setLatitud(punto.getLatitud());
					dto.setLongitud(punto.getLongitud());
					dto.setDistancia(punto.getDistancia());
					DaoFactory.getInstance().insert(sesion, dto);
				} // if
				this.factorAcumulado= this.factorAcumulado + puntoRevision.toDouble("factor");				
				DaoFactory.getInstance().execute(ESql.DELETE, sesion, "TcKeetContratosPuntosResidentesDto", "rechazo", params);			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // processPuntosResidentes
	
	private Long toSemana() throws Exception {
		Long regresar  = -1L;
		Semanas semanas= null;
		try {
			semanas= new Semanas();
			regresar= semanas.getSemanaEnCurso();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toSemana
	
	private Long toPeriodo() {
		return 1L;
	} // toPeriodo	
	
	private boolean validaInicioTrabajo(Session sesion, Long idContratoDestajoResidente) throws Exception{
		return validaInicioTrabajoExtra(sesion, idContratoDestajoResidente, this.revision.getIdDepartamento(), this.revision.getClave());
	} // validaInicioTrabajo
	
	private boolean validaInicioTrabajoExtra(Session sesion, Long idContratoDestajoResidente, Long idDepartamento, String clave) throws Exception{
		boolean regresar         = false;
		Long total               = 0L;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idDepartamento", idDepartamento);
			params.put("clave", clave);
			params.put("estatus", CANCELADO);
      params.put(Constantes.SQL_CONDICION, idContratoDestajoResidente!= null ? "id_contrato_destajo_residente != " + idContratoDestajoResidente : Constantes.SQL_VERDADERO);
			total= DaoFactory.getInstance().toField(sesion, "VistaCapturaDestajosDto", "totalTrabajosLotesResidente", params, "total").toLong();
			regresar= total.equals(0L);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // validaInicioTrabajo
	
	private void actualizaInicioContratoLote(Session sesion, boolean inicio) throws Exception{
		actualizaInicioContratoLoteExtra(sesion, inicio, this.revision.getIdContratoLote());
	} // actualizaInicioContratoLote
	
	private void actualizaInicioContratoLoteExtra(Session sesion, boolean inicio, Long idContratoLote) throws Exception{
		TcKeetContratosLotesDto contratoLote= null;
		try {
			contratoLote= (TcKeetContratosLotesDto) DaoFactory.getInstance().findById(sesion, TcKeetContratosLotesDto.class, idContratoLote);
			contratoLote.setArranque(inicio ? LocalDate.now() : null);			
			DaoFactory.getInstance().update(sesion, contratoLote);
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
	} // actualizaInicioContratoLoteExtra
	
	private boolean actualizaEstacionPadre(Session sesion, TcKeetControlesDto hijo, Double total, String semana, boolean alta) throws Exception{
		boolean regresar                = true;
		Controles controles             = null;
		List<TcKeetControlesDto> padres = null;
		List<TcKeetControlesDto> hijos  = null;
		Map<String, Object>params       = null;
		int count                       = 0;
		try {			
			params   = new HashMap<>();
			controles= new Controles();
			padres= controles.toFather(hijo.getClave());			
			for(TcKeetControlesDto padre: padres){
				count=0;
				hijos= controles.toAllChildren(padre.getClave(), new Long(padre.getNivel()+1L).intValue());
				for(TcKeetControlesDto hijorecord: hijos){
					if(hijorecord.getIdControlEstatus().equals(EControlesEstatus.TERMINADO.getKey()))
						count++;
				} // for
				if(hijos.size()>0)
					params.put("idControlEstatus", count== hijos.size() ? EControlesEstatus.TERMINADO.getKey() : EControlesEstatus.EN_PROCESO.getKey());				
				else
					params.put("idControlEstatus", this.idEstatus);				
				if(alta)
					params.put("cargo".concat(semana), (padre.toValue("cargo".concat(semana)) != null ? ((Double)padre.toValue("cargo".concat(semana))) : 0D) + total);								
				else
					params.put("cargo".concat(semana), (padre.toValue("cargo".concat(semana)) != null ? ((Double)padre.toValue("cargo".concat(semana))) : 0D) - total);								
				DaoFactory.getInstance().update(sesion, TcKeetControlesDto.class, padre.getIdControl(), params);
			} // for
		} // try
		catch (Exception e) {			
			throw e; 
		} // catch		
		return regresar;
	} // actualizaEstacionPadre
	
	private boolean agregarConceptoExtra(Session sesion) throws Exception{
		boolean regresar             = false;
		TcKeetControlesDto control   = null;
		TcKeetControlesDto clone     = null;
		List<TcKeetControlesDto> list= null;
		Controles controles          = null;
		Entity concepto              = null;
		String semana                = null;
		String clave                 = null;
		try {
			control= (TcKeetControlesDto) DaoFactory.getInstance().findById(sesion, TcKeetControlesDto.class, this.conceptoExtra.getIdControl());
			controles= new Controles();			
			list= controles.toAllChildren(control.getClave(), control.getNivel().intValue()+1);			
			clave= controles.toNextKey(list.get(list.size()-1).getClave(), control.getNivel().intValue()+1);
			clone= (TcKeetControlesDto) control.clone();
			clone.setIdControl(-1L);
			clone.setClave(clave);
			clone.setNivel(clone.getNivel()+ 1);
			for(int count=0; count<55; count++) {
				Methods.setValue(clone, "cargo".concat(String.valueOf(count+1)), new Object[]{0D});
				Methods.setValue(clone, "abono".concat(String.valueOf(count+1)), new Object[]{0D});
			} // for
			clone.setNombre(this.conceptoExtra.getDescripcion());
			concepto= toConcepto(sesion);
			clone.setCodigo(concepto.toString("codigo"));
			clone.setIdControlEstatus(EControlesEstatus.TERMINADO.getKey());
			clone.setCosto(this.conceptoExtra.getImporte());
			semana= toSemana().toString();
			Methods.setValue(clone, "cargo".concat(semana), new Object[]{this.conceptoExtra.getImporte()});
			if(DaoFactory.getInstance().insert(sesion, clone)>= 1L){
				if(actualizaEstacionPadre(sesion, clone, this.conceptoExtra.getImporte(), semana, true)){
  				regresar= processDestajoContratistaExtra(sesion, clone);
				} // if
			} // if
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		return regresar;
	} // agregarConceptoExtra
	
	private Entity toConcepto(Session sesion) throws Exception{
		Entity regresar          = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idRubro", this.conceptoExtra.getIdRubro());
			regresar= (Entity) DaoFactory.getInstance().toEntity(sesion, "VistaRubrosDto", "byRubro", params);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toConcepto
	
	private boolean processDestajoContratistaExtra(Session sesion, TcKeetControlesDto control) throws Exception{		
		TcKeetContratosDestajosResidentesDto dto= null;
		Long idContratoDestajo= -1L;
		boolean regresar      = true;		
		try {
			dto= new TcKeetContratosDestajosResidentesDto();		
			dto.setIdUsuario(JsfBase.getIdUsuario());
			dto.setSemana(toSemana());
			dto.setPeriodo(toPeriodo());
			dto.setIdControl(control.getIdControl());
			dto.setIdContratoLoteResidente(this.conceptoExtra.getIdFigura());
			dto.setIdNomina(null);
			dto.setCosto(this.conceptoExtra.getImporte());
			dto.setPorcentaje(100D);
			dto.setIdControlEstatus(EControlesEstatus.TERMINADO.getKey());
			idContratoDestajo= DaoFactory.getInstance().insert(sesion, dto);
			this.conceptoExtra.setPuntosRevision(toLoadPuntosRevision(sesion, control.getIdControl()));
			if(processPuntosContratistasExtras(sesion, JsfBase.getIdUsuario(), idContratoDestajo)){												
				if(validaInicioTrabajoExtra(sesion, null, this.conceptoExtra.getIdDepartamento(), control.getClave()))
					actualizaInicioContratoLoteExtra(sesion, true, this.conceptoExtra.getIdContratoLote());				
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadContratista
	
	private boolean processPuntosContratistasExtras(Session sesion, Long idUsuario, Long idContratoDestajoResidente) throws Exception{
		boolean regresar= true;
		TcKeetContratosPuntosResidentesDto dto= null;
		try {
			for(Entity puntoRevision: this.conceptoExtra.getPuntosRevision()) {
				dto= new TcKeetContratosPuntosResidentesDto();
				dto.setFactor(puntoRevision.toDouble("factor"));
				dto.setIdContratoDestajoResidente(idContratoDestajoResidente);
				dto.setIdPuntoPaquete(puntoRevision.getKey());
				dto.setIdRevisado(2L);
				dto.setIdUsuario(idUsuario);
				dto.setLatitud(this.conceptoExtra.getLatitud());
				dto.setLongitud(this.conceptoExtra.getLongitud());
				dto.setDistancia(this.conceptoExtra.getMetros());
				DaoFactory.getInstance().insert(sesion, dto);				
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // processPuntosContratistas
	
	private Entity[] toLoadPuntosRevision(Session sesion, Long idControl) throws Exception{
		Entity[] regresar        = null;
		List<Entity>puntos       = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idDepartamento", this.conceptoExtra.getIdDepartamento());
			params.put("idPuntoGrupo", this.conceptoExtra.getIdPuntoGrupo());
			params.put("idControl", idControl); 
			puntos= DaoFactory.getInstance().toEntitySet(sesion, "VistaCapturaDestajosDto", "puntosRevisionControl", params);
			regresar= new Entity[puntos.size()];
			for(int count=0; count<puntos.size(); count++)
				regresar[count]= puntos.get(count);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toLoadPuntosRevision
	
	private boolean eliminarConceptoExtraContratista(Session sesion, Long idUsuario) throws Exception{ 
		boolean regresar= false;		
		TcKeetContratosDestajosResidentesDto dto= null;
		TcKeetControlesDto control= null;
		Map<String, Object>params = null;
		Double costo              = 0D;
		try {			
			for(Entity puntoRevision: this.revision.getPuntosRevision()) {
				this.factorAcumulado= 0D;
				params= new HashMap<>();
				params.put("idControl", this.revision.getIdControl());
				params.put("idContratoLoteResidente", this.revision.getIdFigura());
				params.put("idControlEstatus", CANCELADO);
				params.put("idPuntoPaquete", puntoRevision.getKey());
				dto= (TcKeetContratosDestajosResidentesDto) DaoFactory.getInstance().toEntity(sesion, TcKeetContratosDestajosResidentesDto.class, "VistaCapturaDestajosDto", "evidenciaDestajoResidentes", params);									
				costo= dto.getCosto()!= null ? dto.getCosto() : 0D;				
				if(processPuntosResidentes(sesion, idUsuario, puntoRevision, dto.getIdContratoDestajoResidente(), false)){					
					if(validaInicioTrabajo(sesion, dto.getIdContratoDestajoResidente())) {
						actualizaInicioContratoLote(sesion, false);
					} // if
					control= (TcKeetControlesDto) DaoFactory.getInstance().findById(sesion, TcKeetControlesDto.class, this.revision.getIdControl());										
					if(actualizaEstacionPadre(sesion, control, costo, dto.getSemana().toString(), false)){
						if(DaoFactory.getInstance().delete(sesion, dto)>= 1L){						
							regresar= DaoFactory.getInstance().delete(sesion, TcKeetControlesDto.class, this.revision.getIdControl())>= 1L;
						} // if				
					} // if			
				} // if			
			} // for			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // eliminarConceptoExtraContratista

}