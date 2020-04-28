package mx.org.kaana.keet.catalogos.contratos.destajos.reglas;

import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetContratosDestajosContratistasDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosDestajosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosPuntosContratistasDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosPuntosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.nomina.reglas.Semanas;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private static final Log LOG= LogFactory.getLog(Transaccion.class);
	private Long idFigura;
	private Long tipo;	
	private Long idEstacion;
	private Entity[] puntosRevision;
	private Double factorAcumulado;

	public Transaccion(Long idFigura, Long tipo, Long idEstacion, Entity[] puntosRevision) {
		this.idFigura      = idFigura;
		this.tipo          = tipo;
		this.idEstacion    = idEstacion;
		this.puntosRevision= puntosRevision;
	} // Transaccion
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar         = true;
		Map<String, Object>params= null;
		Long idUsuario           = -1L;
		try {
			switch(accion){
				case PROCESAR:									
					idUsuario= JsfBase.getIdUsuario();
					this.factorAcumulado= 0D;
					if(this.tipo.equals(1L))
						processDestajoContratista(sesion, idUsuario);
					else
						processDestajoSubContratista(sesion, idUsuario);
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
	
	private Long processDestajoContratista(Session sesion, Long idUsuario) throws Exception{
		Long regresar= -1L;
		TcKeetContratosDestajosContratistasDto dto= null;
		TcKeetEstacionesDto estacion= null;
		Map<String, Object>params= null;
		try {
			dto= new TcKeetContratosDestajosContratistasDto();		
			dto.setIdUsuario(idUsuario);
			dto.setSemana(toSemana());
			dto.setPeriodo(toPeriodo());
			dto.setIdEstacion(this.idEstacion);
			dto.setIdContratoLoteContratista(this.idFigura);
			dto.setIdNomina(null);
			dto.setCosto(0D);
			dto.setPorcentaje(0D);
			dto.setIdEstacionEstatus(EEstacionesEstatus.INICIAR.getKey());
			regresar= DaoFactory.getInstance().insert(sesion, dto);
			if(processPuntosContratistas(sesion, idUsuario, regresar)){
				estacion= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.idEstacion);
				dto.setPorcentaje(this.factorAcumulado);
				dto.setCosto((estacion.getCosto() * this.factorAcumulado) / 100);
				dto.setIdEstacionEstatus(toIdEstacionEstatus(estacion, dto.getCosto()));
				if(DaoFactory.getInstance().update(sesion, dto)>= 1L){
					params= new HashMap<>();
					params.put("idEstacionEstatus", dto.getIdEstacionEstatus());
					params.put("cargo".concat(dto.getSemana().toString()), ((Double)estacion.toValue("cargo".concat(dto.getSemana().toString()))) + dto.getCosto());
					DaoFactory.getInstance().update(sesion, TcKeetEstacionesDto.class, this.idEstacion, params);
				} // if				
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadContratista
	
	private Long processDestajoSubContratista(Session sesion, Long idUsuario) throws Exception{
		Long regresar= -1L;
		TcKeetContratosDestajosProveedoresDto dto= null;
		TcKeetEstacionesDto estacion= null;
		Map<String, Object>params= null;
		try {
			dto= new TcKeetContratosDestajosProveedoresDto();
			dto.setIdUsuario(idUsuario);
			dto.setSemana(toSemana());
			dto.setPeriodo(toPeriodo());
			dto.setIdEstacion(this.idEstacion);
			dto.setIdContratoLoteProveedor(this.idFigura);
			dto.setIdNomina(null);
			dto.setCosto(0D);
			dto.setPorcentaje(0D);
			dto.setIdEstacionEstatus(EEstacionesEstatus.INICIAR.getKey());
			regresar= DaoFactory.getInstance().insert(sesion, dto);
			if(processPuntosSubContratistas(sesion, idUsuario, regresar)){
				estacion= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.idEstacion);
				dto.setPorcentaje(this.factorAcumulado);
				dto.setCosto((estacion.getCosto() * this.factorAcumulado) / 100);
				dto.setIdEstacionEstatus(toIdEstacionEstatus(estacion, dto.getCosto()));
				if(DaoFactory.getInstance().update(sesion, dto)>= 1L){
					params= new HashMap<>();
					params.put("idEstacionEstatus", dto.getIdEstacionEstatus());
					params.put("cargo".concat(dto.getSemana().toString()), ((Double)estacion.toValue("cargo".concat(dto.getSemana().toString()))) + dto.getCosto());
					DaoFactory.getInstance().update(sesion, TcKeetEstacionesDto.class, this.idEstacion, params);
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
	
	private boolean processPuntosContratistas(Session sesion, Long idUsuario, Long idContratoDestajo) throws Exception{
		boolean regresar= true;
		TcKeetContratosPuntosContratistasDto dto= null;
		try {
			for(Entity puntoRevision: this.puntosRevision){
				dto= new TcKeetContratosPuntosContratistasDto();
				dto.setFactor(puntoRevision.toDouble("factor"));
				dto.setIdContratoDestajoContratista(idContratoDestajo);
				dto.setIdPuntoPaquete(puntoRevision.getKey());
				dto.setIdRevisado(2L);
				dto.setIdUsuario(idUsuario);
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
			for(Entity puntoRevision: this.puntosRevision){
				dto= new TcKeetContratosPuntosProveedoresDto();
				dto.setFactor(puntoRevision.toDouble("factor"));
				dto.setIdContratoDestajoProveedor(idContratoDestajo);
				dto.setIdPuntoPaquete(puntoRevision.getKey());
				dto.setIdRevisado(2L);
				dto.setIdUsuario(idUsuario);
				DaoFactory.getInstance().insert(sesion, dto);
				this.factorAcumulado= this.factorAcumulado + dto.getFactor();
			} // for
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
			regresar= Long.valueOf(semanas.getSemana());
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
}