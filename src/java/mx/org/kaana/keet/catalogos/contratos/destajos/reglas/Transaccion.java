package mx.org.kaana.keet.catalogos.contratos.destajos.reglas;

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
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.DestajoContratistaArchivo;
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.DestajoProveedorArchivo;
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.ConceptoExtra;
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.Revision;
import mx.org.kaana.keet.catalogos.contratos.destajos.comun.IBaseDestajoArchivo;
import mx.org.kaana.keet.db.dto.TcKeetContratosDestajosContratistasDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosDestajosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosPuntosContratistasDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosPuntosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosRechazosContratistasDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosRechazosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.keet.nomina.reglas.Semanas;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private static final Log LOG       = LogFactory.getLog(Transaccion.class);
	private static final Long CANCELADO= 4L;
	private Revision revision;	
	private Double factorAcumulado;
	private List<IBaseDestajoArchivo>documentos;	
	private ConceptoExtra conceptoExtra;
	private Long idEstatus;

	public Transaccion(Revision revision, Long idEstatus) {
		this.revision = revision;		
		this.idEstatus= idEstatus;
	}		

	public Transaccion(List<IBaseDestajoArchivo> documentos) {
		this.documentos= documentos;
	}

	public Transaccion(ConceptoExtra conceptoExtra) {
		this.conceptoExtra= conceptoExtra;
	}	
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar         = true;
		Map<String, Object>params= null;
		Long idUsuario           = -1L;
		IBaseDto dto             = null;
		try {
			switch(accion) {
				case PROCESAR:									
					idUsuario= JsfBase.getIdUsuario();
					this.factorAcumulado= 0D;
					if(this.revision.getTipo().equals(1L))
						regresar= this.processDestajoContratista(sesion, idUsuario);
					else
						regresar= this.processDestajoSubContratista(sesion, idUsuario);
					break;						
				case REPROCESAR:									
					idUsuario= JsfBase.getIdUsuario();
					this.factorAcumulado= 0D;
					if(this.revision.getTipo().equals(1L))
						regresar= this.processRechazoContratista(sesion, idUsuario);
					else
						regresar= this.processRechazoSubContratista(sesion, idUsuario);
					break;						
				case SUBIR:
					for(IBaseDestajoArchivo incidencia: this.documentos){
						if(incidencia.getTipo().equals(1L))
							dto= (DestajoContratistaArchivo) incidencia;
						else
							dto= (DestajoProveedorArchivo) incidencia;
						if(DaoFactory.getInstance().insert(sesion, dto)>= 1L)
							this.toSaveFile(incidencia.getIdArchivo());
					} // for
					break;
				case AGREGAR:					
					regresar= agregarConceptoExtra(sesion);					
					break;
				case ELIMINAR:
					idUsuario= JsfBase.getIdUsuario();
					this.factorAcumulado= 0D;
					if(this.revision.getTipo().equals(1L))
						regresar= this.eliminarConceptoExtraContratista(sesion, idUsuario);
					else
						regresar= this.eliminarConceptoExtraSubContratista(sesion, idUsuario);
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
	
	private boolean processDestajoContratista(Session sesion, Long idUsuario) throws Exception {
		boolean regresar= false;
		Long key        = -1L;
		TcKeetContratosDestajosContratistasDto dto= null;
		TcKeetEstacionesDto estacion= null;
		Map<String, Object>params   = null;
		boolean inicioTrabajo       = false;
		try {
			estacion     = (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion());
			inicioTrabajo= this.validaInicioTrabajo(sesion, null, true);
			dto= new TcKeetContratosDestajosContratistasDto();		
			dto.setIdUsuario(idUsuario);
			dto.setSemana(this.toSemana());
			dto.setPeriodo(this.toPeriodo());
			dto.setIdEstacion(this.revision.getIdEstacion());
			dto.setIdContratoLoteContratista(this.revision.getIdFigura());
			dto.setIdNomina(null);
			dto.setCosto(0D);
			dto.setPorcentaje(0D);
			dto.setIdEstacionEstatus(EEstacionesEstatus.INICIAR.getKey());
			key= DaoFactory.getInstance().insert(sesion, dto);
			if(this.processPuntosContratistas(sesion, idUsuario, key)) {				
				dto.setPorcentaje(this.factorAcumulado);
				dto.setCosto((estacion.getCosto() * this.factorAcumulado) / 100);
				//dto.setIdEstacionEstatus(toIdEstacionEstatus(estacion, dto.getCosto(), true));
				dto.setIdEstacionEstatus(this.toIdEstacionEstatus());
				if(DaoFactory.getInstance().update(sesion, dto)>= 1L) {
					params= new HashMap<>();
					params.put("idEstacionEstatus", dto.getIdEstacionEstatus());
					params.put("cargo".concat(dto.getSemana().toString()), (estacion.toValue("cargo".concat(dto.getSemana().toString())) != null ? ((Double)estacion.toValue("cargo".concat(dto.getSemana().toString()))) : 0D) + dto.getCosto());										
					if(inicioTrabajo) {
						this.actualizaInicioContratoLote(sesion, true);
						//params.put("abono".concat(dto.getSemana().toString()), dto.getCosto());
					} // if
					if(DaoFactory.getInstance().update(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion(), params)>= 1L)
						regresar= this.actualizaEstacionPadre(sesion, estacion, dto.getCosto(), dto.getSemana().toString(), true);											
				} // if				
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadContratista
	
	private boolean processDestajoSubContratista(Session sesion, Long idUsuario) throws Exception {
		boolean regresar= false;
		Long key= -1L;
		TcKeetContratosDestajosProveedoresDto dto= null;
		TcKeetEstacionesDto estacion= null;
		Map<String, Object>params= null;
		boolean inicioTrabajo= false;
		try {
			estacion= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion());
			inicioTrabajo= validaInicioTrabajo(sesion, null, false);
			dto= new TcKeetContratosDestajosProveedoresDto();
			dto.setIdUsuario(idUsuario);
			dto.setSemana(toSemana());
			dto.setPeriodo(toPeriodo());
			dto.setIdEstacion(this.revision.getIdEstacion());
			dto.setIdContratoLoteProveedor(this.revision.getIdFigura());
			dto.setIdNomina(null);
			dto.setCosto(0D);
			dto.setPorcentaje(0D);
			dto.setIdEstacionEstatus(EEstacionesEstatus.INICIAR.getKey());
			key= DaoFactory.getInstance().insert(sesion, dto);
			if(processPuntosSubContratistas(sesion, idUsuario, key)){				
				dto.setPorcentaje(this.factorAcumulado);
				dto.setCosto((estacion.getCosto() * this.factorAcumulado) / 100);
				//dto.setIdEstacionEstatus(toIdEstacionEstatus(estacion, dto.getCosto(), true));
				dto.setIdEstacionEstatus(toIdEstacionEstatus());
				if(DaoFactory.getInstance().update(sesion, dto)>= 1L){
					params= new HashMap<>();
					params.put("idEstacionEstatus", dto.getIdEstacionEstatus());
					params.put("cargo".concat(dto.getSemana().toString()), (estacion.toValue("cargo".concat(dto.getSemana().toString())) != null ? ((Double)estacion.toValue("cargo".concat(dto.getSemana().toString()))) : 0D) + dto.getCosto());					
					if(inicioTrabajo){
						actualizaInicioContratoLote(sesion, true);
						//params.put("abono".concat(dto.getSemana().toString()), dto.getCosto());
					} // if
					if(DaoFactory.getInstance().update(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion(), params)>= 1L)
						regresar= actualizaEstacionPadre(sesion, estacion, dto.getCosto(), dto.getSemana().toString(), true);											
				} // if
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // loadSubContratista
	
	private boolean processRechazoContratista(Session sesion, Long idUsuario) throws Exception {
		boolean regresar= false;		
		TcKeetContratosDestajosContratistasDto dto= null;
		TcKeetEstacionesDto estacion= null;
		Map<String, Object>params= null;
		Double costo= 0D;
		Double porcentaje= 0D;
		try {			
			for(Entity puntoRevision: this.revision.getPuntosRevision()) {
				this.factorAcumulado= 0D;
				params= new HashMap<>();
				params.put("idEstacion", this.revision.getIdEstacion());
				params.put("idContratoLoteContratista", this.revision.getIdFigura());
				params.put("idEstacionEstatus", CANCELADO);
				params.put("idPuntoPaquete", puntoRevision.getKey());
				dto= (TcKeetContratosDestajosContratistasDto) DaoFactory.getInstance().toEntity(sesion, TcKeetContratosDestajosContratistasDto.class, "VistaCapturaDestajosDto", "evidenciaDestajoContratista", params);					
				costo= dto.getCosto()!= null ? dto.getCosto() : 0D;
				porcentaje= dto.getPorcentaje()!= null ? dto.getPorcentaje(): 0D;
				if(processRechazosContratistas(sesion, idUsuario, puntoRevision, dto.getIdContratoDestajoContratista())){
					estacion= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion());
					dto.setPorcentaje(porcentaje - this.factorAcumulado);
					dto.setCosto(costo - ((estacion.getCosto() * this.factorAcumulado) / 100));
					dto.setIdEstacionEstatus(CANCELADO);
					if(DaoFactory.getInstance().update(sesion, dto)>= 1L){
						params.clear();
						//params.put("idEstacionEstatus", toIdEstacionEstatus(estacion, costo, false));
						params.put("idEstacionEstatus", toIdEstacionEstatus());
						params.put("cargo".concat(dto.getSemana().toString()), (estacion.toValue("cargo".concat(dto.getSemana().toString()))!= null ? ((Double)estacion.toValue("cargo".concat(dto.getSemana().toString()))) : null) - costo);											
						if(validaInicioTrabajo(sesion, dto.getIdContratoDestajoContratista(), true)){							
							this.actualizaInicioContratoLote(sesion, false);
							//params.put("abono".concat(dto.getSemana().toString()), 0D);
						} // if
						if(DaoFactory.getInstance().update(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion(), params)>= 1L)
							regresar= this.actualizaEstacionPadre(sesion, estacion, costo, dto.getSemana().toString(), false);											
					} // if				
				} // if			
			} // for			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadContratista
	
	private boolean processRechazoSubContratista(Session sesion, Long idUsuario) throws Exception {
		boolean regresar= false;
		TcKeetContratosDestajosProveedoresDto dto= null;
		TcKeetEstacionesDto estacion= null;
		Map<String, Object>params= null;
		Double costo= 0D;
		Double porcentaje= 0D;
		try {
			for(Entity puntoRevision: this.revision.getPuntosRevision()) {
				this.factorAcumulado= 0D;
				params= new HashMap<>();
				params.put("idEstacion", this.revision.getIdEstacion());
				params.put("idContratoLoteProveedor", this.revision.getIdFigura());
				params.put("idEstacionEstatus", CANCELADO);
				params.put("idPuntoPaquete", puntoRevision.getKey());
				dto= (TcKeetContratosDestajosProveedoresDto) DaoFactory.getInstance().toEntity(sesion, TcKeetContratosDestajosProveedoresDto.class, "VistaCapturaDestajosDto", "evidenciaDestajoProveedor", params);					
				costo= dto.getCosto()!= null ? dto.getCosto() : 0D;
				porcentaje= dto.getPorcentaje()!= null ? dto.getPorcentaje(): 0D;
				if(processRechazosSubContratistas(sesion, idUsuario, puntoRevision, dto.getIdContratoDestajoProveedor())){
					estacion= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion());
					dto.setPorcentaje(porcentaje - this.factorAcumulado);
					dto.setCosto(costo - ((estacion.getCosto() * this.factorAcumulado) / 100));
					dto.setIdEstacionEstatus(CANCELADO);
					if(DaoFactory.getInstance().update(sesion, dto)>= 1L){
						params= new HashMap<>();
						//params.put("idEstacionEstatus", toIdEstacionEstatus(estacion, dto.getCosto(), false));
						params.put("idEstacionEstatus", toIdEstacionEstatus());
						params.put("cargo".concat(dto.getSemana().toString()), (estacion.toValue("cargo".concat(dto.getSemana().toString())) != null ? ((Double)estacion.toValue("cargo".concat(dto.getSemana().toString()))) : 0D) - costo);											
						if(validaInicioTrabajo(sesion, dto.getIdContratoDestajoProveedor(), false)){
							actualizaInicioContratoLote(sesion, false);
							//params.put("abono".concat(dto.getSemana().toString()), 0D);
						} // if
						if(DaoFactory.getInstance().update(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion(), params)>= 1L)
							regresar= actualizaEstacionPadre(sesion, estacion, costo, dto.getSemana().toString(), false);											
					} // if
				} // if
			}
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // loadSubContratista
	
	private boolean processPuntosContratistas(Session sesion, Long idUsuario, Long idContratoDestajo) throws Exception {
		boolean regresar= true;
		TcKeetContratosPuntosContratistasDto dto= null;
		try {
			for(Entity puntoRevision: this.revision.getPuntosRevision()) {
				dto= new TcKeetContratosPuntosContratistasDto();
				dto.setFactor(puntoRevision.toDouble("factor"));
				dto.setIdContratoDestajoContratista(idContratoDestajo);
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
	
	private boolean processPuntosSubContratistas(Session sesion, Long idUsuario, Long idContratoDestajo) throws Exception {
		boolean regresar= true;
		TcKeetContratosPuntosProveedoresDto dto= null;
		try {
			for(Entity puntoRevision: this.revision.getPuntosRevision()){
				dto= new TcKeetContratosPuntosProveedoresDto();
				dto.setFactor(puntoRevision.toDouble("factor"));
				dto.setIdContratoDestajoProveedor(idContratoDestajo);
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
	} // processPuntosSubContratistas
		
	private boolean processRechazosContratistas(Session sesion, Long idUsuario, Entity puntoRevision, Long idContratoDestajoContratista) throws Exception {
		return processRechazosContratistas(sesion, idUsuario, puntoRevision, idContratoDestajoContratista, true);
	} // processRechazosContratistas
	
	private boolean processRechazosContratistas(Session sesion, Long idUsuario, Entity puntoRevision, Long idContratoDestajoContratista, boolean detalle) throws Exception {
		boolean regresar= true;
		TcKeetContratosRechazosContratistasDto dto= null;
		TcKeetContratosPuntosContratistasDto punto= null;
		Map<String, Object>params= null;		
		try {				
				params= new HashMap<>();
				params.put("idContratoDestajoContratista", idContratoDestajoContratista);
				params.put("idPuntoPaquete", puntoRevision.getKey());
				punto= (TcKeetContratosPuntosContratistasDto) DaoFactory.getInstance().toEntity(sesion, TcKeetContratosPuntosContratistasDto.class, "TcKeetContratosPuntosContratistasDto", "puntoPivote", params);
				if(detalle){
					dto= new TcKeetContratosRechazosContratistasDto();
					dto.setIdContratoDestajoContratista(idContratoDestajoContratista);
					dto.setIdPuntoPaquete(puntoRevision.getKey());
					dto.setIdUsuario(idUsuario);
					dto.setObservaciones(this.revision.getObservaciones());
					dto.setLatitud(punto.getLatitud());
					dto.setLongitud(punto.getLongitud());
					dto.setDistancia(punto.getDistancia());
					DaoFactory.getInstance().insert(sesion, dto);
				} // if
				this.factorAcumulado= this.factorAcumulado + puntoRevision.toDouble("factor");				
				DaoFactory.getInstance().execute(ESql.DELETE, sesion, "TcKeetContratosPuntosContratistasDto", "rechazo", params);			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // processPuntosContratistas
	
	private boolean processRechazosSubContratistas(Session sesion, Long idUsuario, Entity puntoRevision, Long idContratoDestajoProveedor) throws Exception {
		return processRechazosSubContratistas(sesion, idUsuario, puntoRevision, idContratoDestajoProveedor, true);
	} // processRechazosSubContratistas
	
	private boolean processRechazosSubContratistas(Session sesion, Long idUsuario, Entity puntoRevision, Long idContratoDestajoProveedor, boolean detalle) throws Exception {
		boolean regresar= true;
		TcKeetContratosRechazosProveedoresDto dto= null;
		TcKeetContratosPuntosProveedoresDto punto= null;
		Map<String, Object>params= null;
		try {			
			params= new HashMap<>();
			params.put("idContratoDestajoProveedor", idContratoDestajoProveedor);
			params.put("idPuntoPaquete", puntoRevision.getKey());
			punto= (TcKeetContratosPuntosProveedoresDto) DaoFactory.getInstance().toEntity(sesion, TcKeetContratosPuntosProveedoresDto.class, "TcKeetContratosPuntosProveedoresDto", "puntoPivote", params);
			if(detalle){
				dto= new TcKeetContratosRechazosProveedoresDto();
				dto.setIdContratoDestajoProveedor(idContratoDestajoProveedor);
				dto.setIdPuntoPaquete(puntoRevision.getKey());
				dto.setIdUsuario(idUsuario);
				dto.setObservaciones(this.revision.getObservaciones());
				dto.setLatitud(punto.getLatitud());
				dto.setLongitud(punto.getLongitud());
				dto.setDistancia(punto.getDistancia());
				DaoFactory.getInstance().insert(sesion, dto);
			} // if
			this.factorAcumulado= this.factorAcumulado + puntoRevision.toDouble("factor");				
			DaoFactory.getInstance().execute(ESql.DELETE, sesion, "TcKeetContratosPuntosProveedoresDto", "rechazo", params);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // processPuntosSubContratistas
	
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
	
	private Long toPeriodo(){
		return 1L;
	} // toPeriodo	
	
	private Long toIdEstacionEstatus(){
		return this.idEstatus;
	} // toIdEstacionEstatus
	
	/*
	private Long toIdEstacionEstatus(TcKeetEstacionesDto estacion, Double costoActual, boolean alta){
		Long regresar       = -1L;		
		Double acumulado    = 0D;
		Double cargoEstacion= 0D;
		try {
			for(int count=0; count<55; count++){
				cargoEstacion= estacion.toValue("cargo".concat(String.valueOf(count+1)))!= null ? ((Double) estacion.toValue("cargo".concat(String.valueOf(count+1)))) : 0D;
				acumulado= acumulado + cargoEstacion;
			} // for
			if(alta)
				acumulado= acumulado + costoActual;			
			else
				acumulado= acumulado - costoActual;			
			regresar= acumulado>= estacion.getCosto() ? EEstacionesEstatus.TERMINADO.getKey() : EEstacionesEstatus.EN_PROCESO.getKey();			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;				
	} // toIdEstacionEstatus
	*/
	
	private boolean validaInicioTrabajo(Session sesion, Long idContratoDestajo, boolean contratista) throws Exception {
		return validaInicioTrabajoExtra(sesion, idContratoDestajo, contratista, this.revision.getIdDepartamento(), this.revision.getClave());
	} // validaInicioTrabajo
	
	private boolean validaInicioTrabajoExtra(Session sesion, Long idContratoDestajo, boolean contratista, Long idDepartamento, String clave) throws Exception {
		boolean regresar         = false;
		Long total               = 0L;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idDepartamento", idDepartamento);
			params.put("clave", clave);
			params.put("estatus", CANCELADO);
			if(contratista){
				params.put("condicionContratista", idContratoDestajo!= null ? "id_contrato_destajo_contratista != " + idContratoDestajo : Constantes.SQL_VERDADERO);
				params.put("condicionProveedor", Constantes.SQL_VERDADERO);
			} // if
      else {
				params.put("condicionProveedor", idContratoDestajo!= null ? "id_contrato_destajo_proveedor != " + idContratoDestajo : Constantes.SQL_VERDADERO);
				params.put("condicionContratista", Constantes.SQL_VERDADERO);				
			} // else
			total= DaoFactory.getInstance().toField(sesion, "VistaCapturaDestajosDto", "totalTrabajosLotes", params, "total").toLong();
			regresar= total.equals(0L);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // validaInicioTrabajo
	
	private void actualizaInicioContratoLote(Session sesion, boolean inicio) throws Exception {
		actualizaInicioContratoLoteExtra(sesion, inicio, this.revision.getIdContratoLote());
	} // actualizaInicioContratoLote
	
	private void actualizaInicioContratoLoteExtra(Session sesion, boolean inicio, Long idContratoLote) throws Exception {
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
	
	private boolean actualizaEstacionPadre(Session sesion, TcKeetEstacionesDto hijo, Double total, String semana, boolean alta) throws Exception {
		boolean regresar                = true;
		Estaciones estaciones           = null;
		List<TcKeetEstacionesDto> padres= null;
		List<TcKeetEstacionesDto> hijos = null;
		Map<String, Object>params       = null;
		int count                       = 0;
		try {			
			params= new HashMap<>();
			estaciones= new Estaciones();
			padres= estaciones.toFather(hijo.getClave());			
			for(TcKeetEstacionesDto padre: padres){
				count=0;
				hijos= estaciones.toAllChildren(padre.getClave(), new Long(padre.getNivel()+1L).intValue());
				for(TcKeetEstacionesDto hijorecord: hijos){
					if(hijorecord.getIdEstacionEstatus().equals(EEstacionesEstatus.TERMINADO.getKey()))
						count++;
				} // for
				if(hijos.size()>0)
					params.put("idEstacionEstatus", count== hijos.size() ? EEstacionesEstatus.TERMINADO.getKey() : EEstacionesEstatus.EN_PROCESO.getKey());				
				else
					params.put("idEstacionEstatus", this.idEstatus);				
				if(alta)
					params.put("cargo".concat(semana), (padre.toValue("cargo".concat(semana)) != null ? ((Double)padre.toValue("cargo".concat(semana))) : 0D) + total);								
				else
					params.put("cargo".concat(semana), (padre.toValue("cargo".concat(semana)) != null ? ((Double)padre.toValue("cargo".concat(semana))) : 0D) - total);								
				DaoFactory.getInstance().update(sesion, TcKeetEstacionesDto.class, padre.getIdEstacion(), params);
			} // for
		} // try
		catch (Exception e) {			
			throw e; 
		} // catch		
		return regresar;
	} // actualizaEstacionPadre
	
	private boolean agregarConceptoExtra(Session sesion) throws Exception {
		boolean regresar                 = false;
		TcKeetEstacionesDto estacion     = null;
		TcKeetEstacionesDto estacionClon = null;
		List<TcKeetEstacionesDto> list   = null;
		Estaciones estaciones            = null;
		Entity concepto                  = null;
		String semana                    = null;
		String clave                     = null;
		try {
			estacion= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.conceptoExtra.getIdEstacion());
			estaciones= new Estaciones();			
			list= estaciones.toAllChildren(estacion.getClave(), estacion.getNivel().intValue()+1);			
			clave= estaciones.toNextKey(list.get(list.size()-1).getClave(), estacion.getNivel().intValue()+1);
			estacionClon= (TcKeetEstacionesDto) estacion.clone();
			estacionClon.setIdEstacion(-1L);
			estacionClon.setClave(clave);
			estacionClon.setNivel(estacionClon.getNivel()+1);
			for(int count=0; count<55; count++){
				Methods.setValue(estacionClon, "cargo".concat(String.valueOf(count+1)), new Object[]{0D});
				Methods.setValue(estacionClon, "abono".concat(String.valueOf(count+1)), new Object[]{0D});
			} // for
			estacionClon.setNombre(this.conceptoExtra.getDescripcion());
			estacionClon.setDescripcion(this.conceptoExtra.getDescripcion());
			concepto= toConcepto(sesion);
			estacionClon.setCodigo(concepto.toString("codigo"));
			estacionClon.setIdEstacionEstatus(EEstacionesEstatus.TERMINADO.getKey());
			estacionClon.setCosto(this.conceptoExtra.getImporte());
			semana= toSemana().toString();
			Methods.setValue(estacionClon, "cargo".concat(semana), new Object[]{this.conceptoExtra.getImporte()});
			if(DaoFactory.getInstance().insert(sesion, estacionClon)>= 1L){
				if(actualizaEstacionPadre(sesion, estacionClon, this.conceptoExtra.getImporte(), semana, true)){
					if(this.conceptoExtra.getTipo().equals(1L))
						regresar= processDestajoContratistaExtra(sesion, estacionClon);
					else
						regresar= processDestajoSubContratistaExtra(sesion, estacionClon);
				} // if
			} // if
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		return regresar;
	} // agregarConceptoExtra
	
	private Entity toConcepto(Session sesion) throws Exception {
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
	
	private boolean processDestajoContratistaExtra(Session sesion, TcKeetEstacionesDto estacion) throws Exception {		
		TcKeetContratosDestajosContratistasDto dto= null;
		Long idContratoDestajo= -1L;
		boolean regresar= true;		
		try {
			dto= new TcKeetContratosDestajosContratistasDto();		
			dto.setIdUsuario(JsfBase.getIdUsuario());
			dto.setSemana(toSemana());
			dto.setPeriodo(toPeriodo());
			dto.setIdEstacion(estacion.getIdEstacion());
			dto.setIdContratoLoteContratista(this.conceptoExtra.getIdFigura());
			dto.setIdNomina(null);
			dto.setCosto(this.conceptoExtra.getImporte());
			dto.setPorcentaje(100D);
			dto.setIdEstacionEstatus(EEstacionesEstatus.TERMINADO.getKey());
			idContratoDestajo= DaoFactory.getInstance().insert(sesion, dto);
			this.conceptoExtra.setPuntosRevision(loadPuntosRevision(sesion, estacion.getIdEstacion()));
			if(processPuntosContratistasExtras(sesion, JsfBase.getIdUsuario(), idContratoDestajo)){												
				if(validaInicioTrabajoExtra(sesion, null, true, this.conceptoExtra.getIdDepartamento(), estacion.getClave()))
					actualizaInicioContratoLoteExtra(sesion, true, this.conceptoExtra.getIdContratoLote());				
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadContratista
	
	private boolean processPuntosContratistasExtras(Session sesion, Long idUsuario, Long idContratoDestajo) throws Exception {
		boolean regresar= true;
		TcKeetContratosPuntosContratistasDto dto= null;
		try {
			for(Entity puntoRevision: this.conceptoExtra.getPuntosRevision()) {
				dto= new TcKeetContratosPuntosContratistasDto();
				dto.setFactor(puntoRevision.toDouble("factor"));
				dto.setIdContratoDestajoContratista(idContratoDestajo);
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
	
	private boolean processDestajoSubContratistaExtra(Session sesion, TcKeetEstacionesDto estacion) throws Exception {		
		TcKeetContratosDestajosProveedoresDto dto= null;
		Long idContratoDestajo= -1L;
		boolean regresar= true;		
		try {
			dto= new TcKeetContratosDestajosProveedoresDto();		
			dto.setIdUsuario(JsfBase.getIdUsuario());
			dto.setSemana(toSemana());
			dto.setPeriodo(toPeriodo());
			dto.setIdEstacion(estacion.getIdEstacion());
			dto.setIdContratoLoteProveedor(this.conceptoExtra.getIdFigura());
			dto.setIdNomina(null);
			dto.setCosto(this.conceptoExtra.getImporte());
			dto.setPorcentaje(100D);
			dto.setIdEstacionEstatus(EEstacionesEstatus.TERMINADO.getKey());
			idContratoDestajo= DaoFactory.getInstance().insert(sesion, dto);
			this.conceptoExtra.setPuntosRevision(loadPuntosRevision(sesion, estacion.getIdEstacion()));
			if(processPuntosSubContratistasExtras(sesion, JsfBase.getIdUsuario(), idContratoDestajo)){												
				if(validaInicioTrabajoExtra(sesion, null, true, this.conceptoExtra.getIdDepartamento(), estacion.getClave()))
					actualizaInicioContratoLoteExtra(sesion, true, this.conceptoExtra.getIdContratoLote());				
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadContratista
	
	private boolean processPuntosSubContratistasExtras(Session sesion, Long idUsuario, Long idContratoDestajo) throws Exception {
		boolean regresar= true;
		TcKeetContratosPuntosProveedoresDto dto= null;
		try {
			for(Entity puntoRevision: this.conceptoExtra.getPuntosRevision()) {
				dto= new TcKeetContratosPuntosProveedoresDto();
				dto.setFactor(puntoRevision.toDouble("factor"));
				dto.setIdContratoDestajoProveedor(idContratoDestajo);
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
	
	private Entity[] loadPuntosRevision(Session sesion, Long idEstacion) throws Exception {
		Entity[] regresar        = null;
		List<Entity>puntos       = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idDepartamento", this.conceptoExtra.getIdDepartamento());
			params.put("idPuntoGrupo", this.conceptoExtra.getIdPuntoGrupo());
			params.put("idEstacion", idEstacion);
			puntos= DaoFactory.getInstance().toEntitySet(sesion, "VistaCapturaDestajosDto", "puntosRevision", params);
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
	} // loadPuntosRevision
	
	private boolean eliminarConceptoExtraContratista(Session sesion, Long idUsuario) throws Exception { 
		boolean regresar= false;		
		TcKeetContratosDestajosContratistasDto dto= null;
		TcKeetEstacionesDto estacion= null;
		Map<String, Object>params= null;
		Double costo= 0D;
		try {			
			for(Entity puntoRevision: this.revision.getPuntosRevision()) {
				this.factorAcumulado= 0D;
				params= new HashMap<>();
				params.put("idEstacion", this.revision.getIdEstacion());
				params.put("idContratoLoteContratista", this.revision.getIdFigura());
				params.put("idEstacionEstatus", CANCELADO);
				params.put("idPuntoPaquete", puntoRevision.getKey());
				dto= (TcKeetContratosDestajosContratistasDto) DaoFactory.getInstance().toEntity(sesion, TcKeetContratosDestajosContratistasDto.class, "VistaCapturaDestajosDto", "evidenciaDestajoContratista", params);									
				costo= dto.getCosto()!= null ? dto.getCosto() : 0D;				
				if(processRechazosContratistas(sesion, idUsuario, puntoRevision, dto.getIdContratoDestajoContratista(), false)){					
					if(validaInicioTrabajo(sesion, dto.getIdContratoDestajoContratista(), true)){							
						actualizaInicioContratoLote(sesion, false);
					} // if
					estacion= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion());										
					if(actualizaEstacionPadre(sesion, estacion, costo, dto.getSemana().toString(), false)){
						if(DaoFactory.getInstance().delete(sesion, dto)>= 1L){						
							regresar= DaoFactory.getInstance().delete(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion())>= 1L;
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
	
	private boolean eliminarConceptoExtraSubContratista(Session sesion, Long idUsuario) throws Exception { 
		boolean regresar= false;
		TcKeetContratosDestajosProveedoresDto dto= null;
		TcKeetEstacionesDto estacion= null;
		Map<String, Object>params= null;
		Double costo= 0D;
		try {
			for(Entity puntoRevision: this.revision.getPuntosRevision()) {
				this.factorAcumulado= 0D;
				params= new HashMap<>();
				params.put("idEstacion", this.revision.getIdEstacion());
				params.put("idContratoLoteProveedor", this.revision.getIdFigura());
				params.put("idEstacionEstatus", CANCELADO);
				params.put("idPuntoPaquete", puntoRevision.getKey());
				dto= (TcKeetContratosDestajosProveedoresDto) DaoFactory.getInstance().toEntity(sesion, TcKeetContratosDestajosProveedoresDto.class, "VistaCapturaDestajosDto", "evidenciaDestajoProveedor", params);					
				costo= dto.getCosto()!= null ? dto.getCosto() : 0D;				
				if(processRechazosSubContratistas(sesion, idUsuario, puntoRevision, dto.getIdContratoDestajoProveedor(), false)){										
					if(validaInicioTrabajo(sesion, dto.getIdContratoDestajoProveedor(), false)){
						actualizaInicioContratoLote(sesion, false);
					} // if
					estacion= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion());					
					if(actualizaEstacionPadre(sesion, estacion, costo, dto.getSemana().toString(), false)){
						if(DaoFactory.getInstance().delete(sesion, dto)>= 1L){						
							regresar= DaoFactory.getInstance().delete(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion())>= 1L;
						} // if
					} // if
				} // if
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // eliminarConceptoExtraSubContratista
}