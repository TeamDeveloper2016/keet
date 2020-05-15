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

	public Transaccion(Revision revision) {
		this.revision= revision;		
	}		

	public Transaccion(List<IBaseDestajoArchivo> documentos) {
		this.documentos= documentos;
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
					if(this.revision.getTipo().equals(1L))
						regresar= processDestajoContratista(sesion, idUsuario);
					else
						regresar= processDestajoSubContratista(sesion, idUsuario);
					break;						
				case REPROCESAR:									
					idUsuario= JsfBase.getIdUsuario();
					this.factorAcumulado= 0D;
					if(this.revision.getTipo().equals(1L))
						regresar= processRechazoContratista(sesion, idUsuario);
					else
						regresar= processRechazoSubContratista(sesion, idUsuario);
					break;						
				case SUBIR:
					for(IBaseDestajoArchivo incidencia: this.documentos){
						if(incidencia.getTipo().equals(1L))
							dto= (DestajoContratistaArchivo) incidencia;
						else
							dto= (DestajoProveedorArchivo) incidencia;
						if(DaoFactory.getInstance().insert(sesion, dto)>= 1L)
							toSaveFile(incidencia.getIdArchivo());
					} // for
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
	
	private boolean processDestajoContratista(Session sesion, Long idUsuario) throws Exception{
		boolean regresar= false;
		Long key= -1L;
		TcKeetContratosDestajosContratistasDto dto= null;
		TcKeetEstacionesDto estacion= null;
		Map<String, Object>params= null;
		boolean inicioTrabajo= false;
		try {
			estacion= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion());
			inicioTrabajo= validaInicioTrabajo(sesion, null, true);
			dto= new TcKeetContratosDestajosContratistasDto();		
			dto.setIdUsuario(idUsuario);
			dto.setSemana(toSemana());
			dto.setPeriodo(toPeriodo());
			dto.setIdEstacion(this.revision.getIdEstacion());
			dto.setIdContratoLoteContratista(this.revision.getIdFigura());
			dto.setIdNomina(null);
			dto.setCosto(0D);
			dto.setPorcentaje(0D);
			dto.setIdEstacionEstatus(EEstacionesEstatus.INICIAR.getKey());
			key= DaoFactory.getInstance().insert(sesion, dto);
			if(processPuntosContratistas(sesion, idUsuario, key)){				
				dto.setPorcentaje(this.factorAcumulado);
				dto.setCosto((estacion.getCosto() * this.factorAcumulado) / 100);
				dto.setIdEstacionEstatus(toIdEstacionEstatus(estacion, dto.getCosto()));
				if(DaoFactory.getInstance().update(sesion, dto)>= 1L){
					params= new HashMap<>();
					params.put("idEstacionEstatus", dto.getIdEstacionEstatus());
					params.put("cargo".concat(dto.getSemana().toString()), ((Double)estacion.toValue("cargo".concat(dto.getSemana().toString()))) + dto.getCosto());										
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
		return regresar;
	} // loadContratista
	
	private boolean processDestajoSubContratista(Session sesion, Long idUsuario) throws Exception{
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
				dto.setIdEstacionEstatus(toIdEstacionEstatus(estacion, dto.getCosto()));
				if(DaoFactory.getInstance().update(sesion, dto)>= 1L){
					params= new HashMap<>();
					params.put("idEstacionEstatus", dto.getIdEstacionEstatus());
					params.put("cargo".concat(dto.getSemana().toString()), ((Double)estacion.toValue("cargo".concat(dto.getSemana().toString()))) + dto.getCosto());					
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
	
	private boolean processRechazoContratista(Session sesion, Long idUsuario) throws Exception{
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
				costo= dto.getCosto();
				porcentaje= dto.getPorcentaje();
				if(processRechazosContratistas(sesion, idUsuario, puntoRevision, dto.getIdContratoDestajoContratista())){
					estacion= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion());
					dto.setPorcentaje(porcentaje - this.factorAcumulado);
					dto.setCosto(costo - ((estacion.getCosto() * this.factorAcumulado) / 100));
					dto.setIdEstacionEstatus(CANCELADO);
					if(DaoFactory.getInstance().update(sesion, dto)>= 1L){
						params.clear();
						params.put("idEstacionEstatus", toIdEstacionEstatus(estacion, costo));
						params.put("cargo".concat(dto.getSemana().toString()), ((Double)estacion.toValue("cargo".concat(dto.getSemana().toString()))) - costo);											
						if(validaInicioTrabajo(sesion, dto.getIdContratoDestajoContratista(), true)){							
							actualizaInicioContratoLote(sesion, false);
							//params.put("abono".concat(dto.getSemana().toString()), 0D);
						} // if
						if(DaoFactory.getInstance().update(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion(), params)>= 1L)
							regresar= actualizaEstacionPadre(sesion, estacion, costo, dto.getSemana().toString(), false);											
					} // if				
				} // if			
			} // for			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadContratista
	
	private boolean processRechazoSubContratista(Session sesion, Long idUsuario) throws Exception{
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
				costo= dto.getCosto();
				porcentaje= dto.getPorcentaje();
				if(processRechazosSubContratistas(sesion, idUsuario, puntoRevision, dto.getIdContratoDestajoProveedor())){
					estacion= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion());
					dto.setPorcentaje(porcentaje - this.factorAcumulado);
					dto.setCosto(costo - ((estacion.getCosto() * this.factorAcumulado) / 100));
					dto.setIdEstacionEstatus(CANCELADO);
					if(DaoFactory.getInstance().update(sesion, dto)>= 1L){
						params= new HashMap<>();
						params.put("idEstacionEstatus", toIdEstacionEstatus(estacion, dto.getCosto()));
						params.put("cargo".concat(dto.getSemana().toString()), ((Double)estacion.toValue("cargo".concat(dto.getSemana().toString()))) - costo);											
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
	
	private boolean processPuntosContratistas(Session sesion, Long idUsuario, Long idContratoDestajo) throws Exception{
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
	
	private boolean processPuntosSubContratistas(Session sesion, Long idUsuario, Long idContratoDestajo) throws Exception{
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
		
	private boolean processRechazosContratistas(Session sesion, Long idUsuario, Entity puntoRevision, Long idContratoDestajoContratista) throws Exception{
		boolean regresar= true;
		TcKeetContratosRechazosContratistasDto dto= null;
		TcKeetContratosPuntosContratistasDto punto= null;
		Map<String, Object>params= null;		
		try {				
				params= new HashMap<>();
				params.put("idContratoDestajoContratista", idContratoDestajoContratista);
				params.put("idPuntoPaquete", puntoRevision.getKey());
				punto= (TcKeetContratosPuntosContratistasDto) DaoFactory.getInstance().toEntity(sesion, TcKeetContratosPuntosContratistasDto.class, "TcKeetContratosPuntosContratistasDto", "puntoPivote", params);
				dto= new TcKeetContratosRechazosContratistasDto();
				dto.setIdContratoDestajoContratista(idContratoDestajoContratista);
				dto.setIdPuntoPaquete(puntoRevision.getKey());
				dto.setIdUsuario(idUsuario);
				dto.setObservaciones(this.revision.getObservaciones());
				dto.setLatitud(punto.getLatitud());
				dto.setLongitud(punto.getLongitud());
				dto.setDistancia(punto.getDistancia());
				DaoFactory.getInstance().insert(sesion, dto);
				this.factorAcumulado= this.factorAcumulado + puntoRevision.toDouble("factor");				
				DaoFactory.getInstance().execute(ESql.DELETE, sesion, "TcKeetContratosPuntosContratistasDto", "rechazo", params);			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // processPuntosContratistas
	
	private boolean processRechazosSubContratistas(Session sesion, Long idUsuario, Entity puntoRevision, Long idContratoDestajoProveedor) throws Exception{
		boolean regresar= true;
		TcKeetContratosRechazosProveedoresDto dto= null;
		TcKeetContratosPuntosProveedoresDto punto= null;
		Map<String, Object>params= null;
		try {			
			params= new HashMap<>();
			params.put("idContratoDestajoProveedor", idContratoDestajoProveedor);
			params.put("idPuntoPaquete", puntoRevision.getKey());
			punto= (TcKeetContratosPuntosProveedoresDto) DaoFactory.getInstance().toEntity(sesion, TcKeetContratosPuntosProveedoresDto.class, "TcKeetContratosPuntosProveedoresDto", "puntoPivote", params);
			dto= new TcKeetContratosRechazosProveedoresDto();
			dto.setIdContratoDestajoProveedor(idContratoDestajoProveedor);
			dto.setIdPuntoPaquete(puntoRevision.getKey());
			dto.setIdUsuario(idUsuario);
			dto.setObservaciones(this.revision.getObservaciones());
			dto.setLatitud(punto.getLatitud());
			dto.setLongitud(punto.getLongitud());
			dto.setDistancia(punto.getDistancia());
			DaoFactory.getInstance().insert(sesion, dto);
			this.factorAcumulado= this.factorAcumulado + puntoRevision.toDouble("factor");				
			DaoFactory.getInstance().execute(ESql.DELETE, sesion, "TcKeetContratosPuntosProveedoresDto", "rechazo", params);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // processPuntosSubContratistas
	
	private Long toSemana() throws Exception{
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
	
	private Long toIdEstacionEstatus(TcKeetEstacionesDto estacion, Double costoActual){
		Long regresar   = -1L;		
		Double acumulado= 0D;
		try {
			for(int count=0; count<55; count++)
				acumulado= acumulado + ((Double) estacion.toValue("cargo".concat(String.valueOf(count+1))));
			acumulado= acumulado + costoActual;			
			regresar= acumulado>= estacion.getCosto() ? EEstacionesEstatus.TERMINADO.getKey() : EEstacionesEstatus.EN_PROCESO.getKey();			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;				
	} // toIdEstacionEstatus
	
	private boolean validaInicioTrabajo(Session sesion, Long idContratoDestajo, boolean contratista) throws Exception{
		boolean regresar         = false;
		Long total               = 0L;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idDepartamento", this.revision.getIdDepartamento());
			params.put("clave", this.revision.getClave());
			params.put("estatus", CANCELADO);
			if(contratista){
				params.put("condicionContratista", idContratoDestajo!= null ? "id_contrato_destajo_contratista != " + idContratoDestajo : Constantes.SQL_VERDADERO);
				params.put("condicionProveedor", Constantes.SQL_VERDADERO);
			} // if
			else{
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
	
	private void actualizaInicioContratoLote(Session sesion, boolean inicio) throws Exception{
		TcKeetContratosLotesDto contratoLote= null;
		try {
			contratoLote= (TcKeetContratosLotesDto) DaoFactory.getInstance().findById(sesion, TcKeetContratosLotesDto.class, this.revision.getIdContratoLote());
			contratoLote.setArranque(inicio ? LocalDate.now() : null);			
			DaoFactory.getInstance().update(sesion, contratoLote);
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
	} // actualizaInicioContratoLote
	
	private boolean actualizaEstacionPadre(Session sesion, TcKeetEstacionesDto hijo, Double total, String semana, boolean alta) throws Exception{
		boolean regresar         = false;
		Estaciones estaciones    = null;
		TcKeetEstacionesDto padre= null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			estaciones= new Estaciones();
			padre= estaciones.getFather(hijo.getClave());			
			params.put("idEstacionEstatus", toIdEstacionEstatus(padre, total));
			if(alta)
				params.put("cargo".concat(semana), ((Double)padre.toValue("cargo".concat(semana))) + total);								
			else
				params.put("cargo".concat(semana), ((Double)padre.toValue("cargo".concat(semana))) - total);								
			regresar= DaoFactory.getInstance().update(sesion, TcKeetEstacionesDto.class, padre.getIdEstacion(), params)>= 1L;
		} // try
		catch (Exception e) {			
			throw e; 
		} // catch		
		return regresar;
	} // actualizaEstacionPadre
}