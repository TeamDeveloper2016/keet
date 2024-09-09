package mx.org.kaana.keet.catalogos.contratos.destajos.reglas;

import java.io.File;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import mx.org.kaana.keet.db.dto.TcKeetAnticiposLotesDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosContratistasArchivosDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosDestajosContratistasDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosDestajosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosProveedoresArchivosDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosPuntosContratistasDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosPuntosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosRechazosContratistasDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosRechazosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosLotesDto;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.keet.nomina.reglas.Semanas;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private static final Log LOG= LogFactory.getLog(Transaccion.class);
  
	private Revision revision;	
	private Double factorAcumulado;
	private List<IBaseDestajoArchivo>documentos;	
	private ConceptoExtra conceptoExtra;
	private Long idNomina;
	private Long idEstatus;
	private Long idTipoArchivo;
	private Long idContratoArchivo;
	private String messageError;

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
	
	public Transaccion(Long idTipoArchivo, Long idContratoArchivo) {
		this.idTipoArchivo= idTipoArchivo;
		this.idContratoArchivo= idContratoArchivo;
	}	
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar         = Boolean.TRUE;
		Map<String, Object>params= null;
		Long idUsuario           = -1L;
		IBaseDto dto             = null;
		try {
      this.messageError= "";
			switch(accion) {
				case PROCESAR:									
					idUsuario= JsfBase.getIdUsuario();
          this.idNomina= this.toLoadUltimaNomina();
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
					for(IBaseDestajoArchivo incidencia: this.documentos) {
						if(incidencia.getTipo().equals(1L))
							dto= (DestajoContratistaArchivo) incidencia;
						else
							dto= (DestajoProveedorArchivo) incidencia;
						if(DaoFactory.getInstance().insert(sesion, dto)>= 1L)
							this.toSaveFile(incidencia.getIdArchivo());
					} // for
					break;
				case AGREGAR:					
					regresar= this.agregarConceptoExtra(sesion);					
					break;
				case MODIFICAR:
					if(Objects.equals(this.conceptoExtra.getTipo(), 1L))
						regresar= this.modificarConceptoExtraContratista(sesion);
					else
						regresar= this.modificarConceptoExtraSubContratista(sesion);
					break;
				case ELIMINAR:
					idUsuario= JsfBase.getIdUsuario();
					this.factorAcumulado= 0D;
					if(Objects.equals(this.revision.getTipo(), 1L))
						regresar= this.eliminarConceptoExtraContratista(sesion, idUsuario);
					else
						regresar= this.eliminarConceptoExtraSubContratista(sesion, idUsuario);
					break;
				case DEPURAR:
          if(Objects.equals(this.idTipoArchivo, 1L))
            this.toDepurarEvidenciaContratista(sesion);
          else
            this.toDepurarEvidenciaProveedor(sesion);
					break;
			} // switch
		} // try
		catch (Exception e) {			
      if(e!= null)
        if(e.getCause()!= null)
          this.messageError= this.messageError.concat("<br/>").concat(e.getCause().toString());
        else
          this.messageError= this.messageError.concat("<br/>")+ e.getMessage();
			throw new Exception(this.messageError);
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}	
	
	private boolean processDestajoContratista(Session sesion, Long idUsuario) throws Exception {
		boolean regresar            = Boolean.FALSE;
		TcKeetContratosDestajosContratistasDto dto= null;
		TcKeetEstacionesDto estacion= null;
		Map<String, Object>params   = new HashMap<>();
		try {
			estacion= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion());
			params.put("idNomina", this.idNomina);
			params.put("idEstacion", this.revision.getIdEstacion());
			params.put("idContratoLoteContratista", this.revision.getIdFigura());
      dto= (TcKeetContratosDestajosContratistasDto) DaoFactory.getInstance().toEntity(sesion, TcKeetContratosDestajosContratistasDto.class, "TcKeetContratosDestajosContratistasDto", "destajo", params);
      if(dto== null) {
			  dto= new TcKeetContratosDestajosContratistasDto();		
			  dto.setIdUsuario(idUsuario);
			  dto.setSemana(this.toSemana());
			  dto.setPeriodo(this.toPeriodo());
			  dto.setIdEstacion(this.revision.getIdEstacion());
			  dto.setIdContratoLoteContratista(this.revision.getIdFigura());
			  dto.setIdNomina(null);
			  dto.setCosto(0D);
			  dto.setPorcentaje(0D);
			  dto.setAnticipo(0D);
			  dto.setIdEstacionEstatus(EEstacionesEstatus.INICIAR.getKey());
  			DaoFactory.getInstance().insert(sesion, dto);
      } // if  
			if(this.processPuntosContratistas(sesion, idUsuario, dto.getKey())) {				
				dto.setPorcentaje(dto.getPorcentaje()+ this.factorAcumulado);
				dto.setCosto(dto.getCosto()+ ((estacion.getCosto()* this.factorAcumulado)/ 100));
				dto.setAnticipo(dto.getAnticipo()+ ((estacion.getAnticipo()* this.factorAcumulado)/ 100));
				dto.setIdEstacionEstatus(this.toIdEstacionEstatus());
				if(DaoFactory.getInstance().update(sesion, dto)>= 1L) {
					params.put("idEstacionEstatus", dto.getIdEstacionEstatus());
          String columna= "cargo".concat(dto.getSemana().toString());
					params.put("cargo", estacion.getCargo()+ dto.getCosto());										
					params.put("retencion", estacion.getRetencion()+ dto.getAnticipo());										
					params.put(columna, (Double)estacion.toValue(columna)+ dto.getCosto());										
					params.put("entrega", LocalDate.now());										
					if(DaoFactory.getInstance().update(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion(), params)>= 1L) 
						regresar= this.actualizaEstacionPadre(sesion, estacion, dto.getCosto(), dto.getAnticipo(), dto.getSemana().toString(), Boolean.TRUE, this.revision.getIdContratoLote());
          
          // ACTUALIZAR EL CONCEPTO DEL PRESTAMO EN LA TABLA DE PRESTAMOS LOTES CON PAGADO= 1 Y EL ANTICIPO PAGADO
          params.clear();
          params.put("idEstacion", this.revision.getIdEstacion());
          params.put("signo", "+");
          params.put("anticipo", dto.getAnticipo());
          DaoFactory.getInstance().updateAll(sesion, TcKeetPrestamosLotesDto.class, params);
				} // if				
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} 
	
	private boolean processDestajoSubContratista(Session sesion, Long idUsuario) throws Exception {
		boolean regresar= Boolean.FALSE;
		TcKeetContratosDestajosProveedoresDto dto= null;
		TcKeetEstacionesDto estacion             = null;
		Map<String, Object>params                = new HashMap<>();
		try {
			estacion= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion());
			params.put("idNomina", this.idNomina);
			params.put("idEstacion", this.revision.getIdEstacion());
			params.put("idContratoLoteProveedor", this.revision.getIdFigura());
      dto= (TcKeetContratosDestajosProveedoresDto) DaoFactory.getInstance().toEntity(sesion, TcKeetContratosDestajosProveedoresDto.class, "TcKeetContratosDestajosProveedoresDto", "destajo", params);
      if(dto== null) {
        dto= new TcKeetContratosDestajosProveedoresDto();
        dto.setIdUsuario(idUsuario);
        dto.setSemana(this.toSemana());
        dto.setPeriodo(this.toPeriodo());
        dto.setIdEstacion(this.revision.getIdEstacion());
        dto.setIdContratoLoteProveedor(this.revision.getIdFigura());
        dto.setIdNomina(null);
        dto.setCosto(0D);
        dto.setAnticipo(0D);
        dto.setPorcentaje(0D);
        dto.setIdEstacionEstatus(EEstacionesEstatus.INICIAR.getKey());
        DaoFactory.getInstance().insert(sesion, dto);
      } // if
			if(this.processPuntosSubContratistas(sesion, idUsuario, dto.getKey())) {	
				dto.setPorcentaje(dto.getPorcentaje()+ this.factorAcumulado);
				dto.setCosto(dto.getCosto()+ ((estacion.getCosto()* this.factorAcumulado)/ 100));
				dto.setAnticipo(dto.getAnticipo()+ ((estacion.getAnticipo()* this.factorAcumulado)/ 100));
				dto.setIdEstacionEstatus(this.toIdEstacionEstatus());
				if(DaoFactory.getInstance().update(sesion, dto)>= 1L) {
					params.clear();
					params.put("idEstacionEstatus", dto.getIdEstacionEstatus());
          String columna= "cargo".concat(dto.getSemana().toString());
					params.put("cargo", estacion.getCargo()+ dto.getCosto());
					params.put("retencion", estacion.getRetencion()+ dto.getAnticipo());
					params.put(columna, (Double)estacion.toValue(columna)+ dto.getCosto());										
					params.put("entrega", LocalDate.now());										
					if(DaoFactory.getInstance().update(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion(), params)>= 1L)
						regresar= this.actualizaEstacionPadre(sesion, estacion, dto.getCosto(), dto.getAnticipo(), dto.getSemana().toString(), Boolean.TRUE, this.revision.getIdContratoLote());											
				} // if
        
        // ACTUALIZAR EL CONCEPTO DEL PRESTAMO EN LA TABLA DE ANTICIPOS LOTES CON PAGADO= 1 Y EL ANTICIPO PAGADO
        params.clear();
        params.put("idEstacion", this.revision.getIdEstacion());
        params.put("signo", "+");
        params.put("anticipo", dto.getAnticipo());
        DaoFactory.getInstance().updateAll(sesion, TcKeetAnticiposLotesDto.class, params);
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} 
	
	private boolean processRechazoContratista(Session sesion, Long idUsuario) throws Exception {
		boolean regresar= Boolean.FALSE;		
		TcKeetContratosDestajosContratistasDto dto= null;
		TcKeetEstacionesDto estacion= null;
		Map<String, Object>params   = new HashMap<>();
		Double costo                = 0D;
		Double anticipo             = 0D;
		Double porcentaje           = 0D;
		try {			
			for(Entity puntoRevision: this.revision.getPuntosRevision()) {
				this.factorAcumulado= 0D;
				params.put("idEstacion", this.revision.getIdEstacion());
				params.put("idContratoLoteContratista", this.revision.getIdFigura());
				params.put("idEstacionEstatus", EEstacionesEstatus.CANCELADO.getKey());
				params.put("idPuntoPaquete", puntoRevision.getKey());
				dto       = (TcKeetContratosDestajosContratistasDto) DaoFactory.getInstance().toEntity(sesion, TcKeetContratosDestajosContratistasDto.class, "VistaCapturaDestajosDto", "evidenciaDestajoContratista", params);					
				costo     = dto.getCosto()!= null? dto.getCosto(): 0D;
				anticipo  = dto.getAnticipo()!= null? dto.getAnticipo(): 0D;
				porcentaje= dto.getPorcentaje()!= null? dto.getPorcentaje(): 0D;
				if(this.processRechazosContratistas(sesion, idUsuario, puntoRevision, dto.getIdContratoDestajoContratista())) {
					estacion= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion());
					dto.setPorcentaje(porcentaje- this.factorAcumulado);
          Double calculo  = (estacion.getCosto()* this.factorAcumulado)/ 100;
          Double retencion= (estacion.getAnticipo()* this.factorAcumulado)/ 100;
					dto.setCosto(Numero.toRedondearSat(costo- calculo));
					dto.setAnticipo(Numero.toRedondearSat(anticipo- retencion));
					dto.setIdEstacionEstatus(dto.getCosto()<= 0D? EEstacionesEstatus.CANCELADO.getKey(): EEstacionesEstatus.EN_PROCESO.getKey());
					if(DaoFactory.getInstance().update(sesion, dto)>= 1L) {
						params.clear();
						params.put("idEstacionEstatus", (estacion.getCargo().longValue()- calculo.longValue())<= 0L? EEstacionesEstatus.INICIAR.getKey(): this.toIdEstacionEstatus());
            String columna= "cargo".concat(dto.getSemana().toString());
            params.put("cargo", Numero.toRedondearSat(estacion.getCargo()- calculo));
            params.put("retencion", Numero.toRedondearSat(estacion.getRetencion()- retencion));
            params.put(columna, Numero.toRedondearSat((Double)estacion.toValue(columna)- calculo));
            params.put("entrega", LocalDate.now());
						if(DaoFactory.getInstance().update(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion(), params)>= 1L)
							regresar= this.actualizaEstacionPadre(sesion, estacion, calculo, retencion, dto.getSemana().toString(), Boolean.FALSE, this.revision.getIdContratoLote());											
					} // if			

          // ACTUALIZAR EL CONCEPTO DEL PRESTAMO EN LA TABLA DE PRESTAMOS LOTES CON PAGADO= 1 Y EL ANTICIPO PAGADO
          params.clear();
          params.put("idEstacion", this.revision.getIdEstacion());
          params.put("signo", "-");
          params.put("anticipo", retencion);
          DaoFactory.getInstance().updateAll(sesion, TcKeetPrestamosLotesDto.class, params);
				} // if			
			} // for			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadContratista
	
	private boolean processRechazoSubContratista(Session sesion, Long idUsuario) throws Exception {
		boolean regresar= Boolean.FALSE;
		TcKeetContratosDestajosProveedoresDto dto= null;
		TcKeetEstacionesDto estacion= null;
		Map<String, Object>params   = new HashMap<>();
		Double costo                = 0D;
		Double anticipo             = 0D;
		Double porcentaje           = 0D;
		try {
			for(Entity puntoRevision: this.revision.getPuntosRevision()) {
				this.factorAcumulado= 0D;
				params.put("idEstacion", this.revision.getIdEstacion());
				params.put("idContratoLoteProveedor", this.revision.getIdFigura());
				params.put("idEstacionEstatus", EEstacionesEstatus.CANCELADO.getKey());
				params.put("idPuntoPaquete", puntoRevision.getKey());
				dto= (TcKeetContratosDestajosProveedoresDto) DaoFactory.getInstance().toEntity(sesion, TcKeetContratosDestajosProveedoresDto.class, "VistaCapturaDestajosDto", "evidenciaDestajoProveedor", params);					
				costo   = dto.getCosto()!= null? dto.getCosto(): 0D;
				anticipo= dto.getAnticipo()!= null? dto.getAnticipo(): 0D;
				porcentaje= dto.getPorcentaje()!= null? dto.getPorcentaje(): 0D;
				if(this.processRechazosSubContratistas(sesion, idUsuario, puntoRevision, dto.getIdContratoDestajoProveedor())){
					estacion= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion());
					dto.setPorcentaje(porcentaje- this.factorAcumulado);
          Double calculo  = (estacion.getCosto()* this.factorAcumulado)/ 100;
          Double retencion= (estacion.getAnticipo()* this.factorAcumulado)/ 100;
					dto.setCosto(Numero.toRedondearSat(costo- calculo));
					dto.setAnticipo(Numero.toRedondearSat(anticipo- retencion));
					dto.setIdEstacionEstatus(dto.getCosto()<= 0D? EEstacionesEstatus.CANCELADO.getKey(): EEstacionesEstatus.EN_PROCESO.getKey());
					if(DaoFactory.getInstance().update(sesion, dto)>= 1L) {
						params.clear();
						params.put("idEstacionEstatus", (estacion.getCargo().longValue()- calculo.longValue())<= 0L? EEstacionesEstatus.INICIAR.getKey(): this.toIdEstacionEstatus());
            String columna= "cargo".concat(dto.getSemana().toString());
            params.put("cargo", Numero.toRedondearSat(estacion.getCargo()- calculo));
            params.put("retencion", Numero.toRedondearSat(estacion.getRetencion()- retencion));
            params.put(columna, Numero.toRedondearSat((Double)estacion.toValue(columna)- calculo));
						if(DaoFactory.getInstance().update(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion(), params)>= 1L)
							regresar= this.actualizaEstacionPadre(sesion, estacion, calculo, retencion, dto.getSemana().toString(), Boolean.FALSE, this.revision.getIdContratoLote());											
					} // if
          
          // ACTUALIZAR EL CONCEPTO DEL PRESTAMO EN LA TABLA DE ANTICIPOS LOTES CON PAGADO= 1 Y EL ANTICIPO PAGADO
          params.clear();
          params.put("idEstacion", this.revision.getIdEstacion());
          params.put("signo", "-");
          params.put("anticipo", retencion);
          DaoFactory.getInstance().updateAll(sesion, TcKeetAnticiposLotesDto.class, params);
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
		boolean regresar= Boolean.TRUE;
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
		boolean regresar= Boolean.TRUE;
		TcKeetContratosPuntosProveedoresDto dto= null;
		try {
			for(Entity puntoRevision: this.revision.getPuntosRevision()) {
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
		return processRechazosContratistas(sesion, idUsuario, puntoRevision, idContratoDestajoContratista, Boolean.TRUE);
	} // processRechazosContratistas
	
	private boolean processRechazosContratistas(Session sesion, Long idUsuario, Entity puntoRevision, Long idContratoDestajoContratista, boolean detalle) throws Exception {
		boolean regresar                          = Boolean.TRUE;
		TcKeetContratosRechazosContratistasDto dto= null;
		TcKeetContratosPuntosContratistasDto punto= null;
		Map<String, Object>params                 = new HashMap<>();		
		try {				
      params.put("idContratoDestajoContratista", idContratoDestajoContratista);
      params.put("idPuntoPaquete", puntoRevision.getKey());
      punto= (TcKeetContratosPuntosContratistasDto) DaoFactory.getInstance().toEntity(sesion, TcKeetContratosPuntosContratistasDto.class, "TcKeetContratosPuntosContratistasDto", "puntoPivote", params);
      if(detalle) {
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
		return processRechazosSubContratistas(sesion, idUsuario, puntoRevision, idContratoDestajoProveedor, Boolean.TRUE);
	} // processRechazosSubContratistas
	
	private boolean processRechazosSubContratistas(Session sesion, Long idUsuario, Entity puntoRevision, Long idContratoDestajoProveedor, boolean detalle) throws Exception {
		boolean regresar= Boolean.TRUE;
		TcKeetContratosRechazosProveedoresDto dto= null;
		TcKeetContratosPuntosProveedoresDto punto= null;
		Map<String, Object>params                = new HashMap<>();
		try {			
			params.put("idContratoDestajoProveedor", idContratoDestajoProveedor);
			params.put("idPuntoPaquete", puntoRevision.getKey());
			punto= (TcKeetContratosPuntosProveedoresDto) DaoFactory.getInstance().toEntity(sesion, TcKeetContratosPuntosProveedoresDto.class, "TcKeetContratosPuntosProveedoresDto", "puntoPivote", params);
			if(detalle) {
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
	
	private Long toPeriodo() {
		return 1L;
	} // toPeriodo	
	
	private Long toIdEstacionEstatus() {
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
		boolean regresar         = Boolean.FALSE;
		Long total               = 0L;
		Map<String, Object>params= new HashMap<>();
		try {
			params.put("idDepartamento", idDepartamento);
			params.put("clave", clave);
			params.put("estatus", EEstacionesEstatus.CANCELADO.getKey());
			if(contratista) {
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
    finally {
      Methods.clean(params);
    } // finally
		return regresar;
	} // validaInicioTrabajo
	
	private void actualizaInicioContratoLote(Session sesion, boolean inicio) throws Exception {
		actualizaInicioContratoLoteExtra(sesion, inicio, this.revision.getIdContratoLote());
	} // actualizaInicioContratoLote
	
	private void actualizaInicioContratoLoteExtra(Session sesion, boolean inicio, Long idContratoLote) throws Exception {
		TcKeetContratosLotesDto contratoLote= null;
		Map<String, Object>params           = new HashMap<>();
		try {
			contratoLote= (TcKeetContratosLotesDto) DaoFactory.getInstance().findById(sesion, TcKeetContratosLotesDto.class, idContratoLote);
			contratoLote.setArranque(inicio? LocalDate.now(): null);			
			contratoLote.setConcluyo(inicio? LocalDate.now(): null);			
			DaoFactory.getInstance().update(sesion, contratoLote);
      if(inicio) {
  			params.put("idContrato", contratoLote.getIdContrato());
        DaoFactory.getInstance().updateAll(sesion, TcKeetContratosDto.class, params, "arranque");
      } // if
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
    finally {
      Methods.clean(params);
    } // finally
	} // actualizaInicioContratoLoteExtra
	
	private boolean actualizaEstacionPadre(Session sesion, TcKeetEstacionesDto hijo, Double total, Double anticipo, String semana, boolean alta, Long idContratoLote) throws Exception {
		boolean regresar                = Boolean.TRUE;
		Estaciones estaciones           = null;
		List<TcKeetEstacionesDto> padres= null;
    TcKeetEstacionesDto padre       = null;
    Long idEstacionEstatus          = 1L;
    Double value                    = 0D;
		Map<String, Object>params       = new HashMap<>();
		try {			
			estaciones= new Estaciones(sesion);
			padres    = estaciones.toFather(hijo.getClave());			
      if(padres!= null && !padres.isEmpty()) {
        padres.remove(padres.size()- 1);
        int index= padres.size()- 1;
        while(index>= 0) {
          padre= padres.get(index);
          String clave= estaciones.toOnlyKey(padre.getClave(), padre.getNivel().intValue()+ 1);
          params.put("clave", clave);
          params.put("longitud", clave.length());
          params.put("nivel", padre.getNivel()+ 1L);
          Entity row= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcKeetEstacionesDto", "totales", params);
          if(row!= null && !row.isEmpty()) {
            idEstacionEstatus= Objects.equals(row.toLong("terminados"), row.toLong("total"))? EEstacionesEstatus.TERMINADO.getKey(): Objects.equals(row.toLong("iniciados"), row.toLong("total"))? EEstacionesEstatus.INICIAR.getKey(): EEstacionesEstatus.EN_PROCESO.getKey();
            padre.setIdEstacionEstatus(idEstacionEstatus);
            // SI SE CANCELA EL DESTAJO O EL EXTRA DEL CONCEPTO YA NO SE ACTUALIZA LA FECHA DE LOS PADRES Y SE MANTEIENE LA ANTERIOR
            if(alta)
              padre.setEntrega(LocalDate.now());
            String columna= "cargo".concat(semana);
            value= (padre.toValue(columna)!= null? ((Double)padre.toValue(columna)): 0D)+ (alta? total* 1D: total* -1D);
            Methods.setValue(padre, columna, new Object[] {value});
            padre.setCargo((padre.getCargo()!= null? padre.getCargo(): 0D)+ (alta? total* 1D: total* -1D));
            padre.setRetencion((padre.getRetencion()!= null? padre.getRetencion(): 0D)+ (alta? anticipo* 1D: anticipo* -1D));
            DaoFactory.getInstance().update(sesion, padre);
            // ACTUALIZAR EL ESTATUS DEL LOTE DEL CONTRATO CON EL AVANCE
            if(Objects.equals(padre.getNivel(), 4L)) {
			        TcKeetContratosLotesDto contratoLote= (TcKeetContratosLotesDto) DaoFactory.getInstance().findById(sesion, TcKeetContratosLotesDto.class, idContratoLote);
			        contratoLote.setIdContratoLoteEstatus(Objects.equals(EEstacionesEstatus.TERMINADO.getKey(), idEstacionEstatus)? idEstacionEstatus+ 1L: idEstacionEstatus);			
              // INICIARLIZAR LA FECHA DE INICIO DEL LOTE CUANDO SE REGISTRE EL PRIMER DESTAJO
              if(Cadena.isVacio(contratoLote.getArranque()))
           			contratoLote.setArranque(LocalDate.now());			
           		contratoLote.setConcluyo(LocalDate.now());			
			        DaoFactory.getInstance().update(sesion, contratoLote);
            } // if
          } // if  
          index--;
        } // for
      } // if
		} // try
		catch (Exception e) {			
			throw e; 
		} // catch		
    finally {
      Methods.clean(params);
    } // finally
		return regresar;
	} // actualizaEstacionPadre
	
	private boolean agregarConceptoExtra(Session sesion) throws Exception {
		boolean regresar              = Boolean.FALSE;
		TcKeetEstacionesDto estacion  = null;
		TcKeetEstacionesDto clon      = null;
		List<TcKeetEstacionesDto> list= null;
		Estaciones estaciones         = null;
		Entity concepto               = null;
		String semana                 = null;
		String clave                  = null;
		try {
			estacion  = (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.conceptoExtra.getIdEstacion());
			estaciones= new Estaciones(sesion);			
			list      = estaciones.toAllChildren(estacion.getClave(), estacion.getNivel().intValue()+1);			
      if(list!= null && !list.isEmpty())
			  clave   = estaciones.toNextKey(list.get(list.size()-1).getClave(), estacion.getNivel().intValue()+1);
      else
			  clave   = estaciones.toNextKey(estacion.getClave(), estacion.getNivel().intValue()+1);
			clon= (TcKeetEstacionesDto) estacion.clone();
			clon.setIdEstacion(-1L);
			clon.setClave(clave);
			clon.setNivel(clon.getNivel()+1);
			for(int count=0; count<55; count++) {
				clon.setCargo(0D);
				clon.setAnticipo(0D);
				Methods.setValue(clon, "cargo".concat(String.valueOf(count+1)), new Object[]{ 0D });
				Methods.setValue(clon, "abono".concat(String.valueOf(count+1)), new Object[]{ 0D });
			} // for
			clon.setNombre(this.conceptoExtra.getDescripcion());
			clon.setDescripcion(this.conceptoExtra.getDescripcion());
			concepto= this.toConcepto(sesion);
			clon.setCodigo(concepto.toString("codigo"));
			clon.setIdEstacionEstatus(EEstacionesEstatus.TERMINADO.getKey());
      clon.setEntrega(LocalDate.now());
			clon.setCosto(this.conceptoExtra.getImporte());
			clon.setAnticipo(0D);
			semana= this.toSemana().toString();
  		clon.setCargo(this.conceptoExtra.getImporte());
  		clon.setRetencion(0D);
			Methods.setValue(clon, "cargo".concat(semana), new Object[] {this.conceptoExtra.getImporte()});
			if(DaoFactory.getInstance().insert(sesion, clon)>= 1L){
				if(this.actualizaEstacionPadre(sesion, clon, this.conceptoExtra.getImporte(), 0D, semana, Boolean.TRUE, this.conceptoExtra.getIdContratoLote())) {
					if(this.conceptoExtra.getTipo().equals(1L))
						regresar= this.processDestajoContratistaExtra(sesion, clon);
					else
						regresar= this.processDestajoSubContratistaExtra(sesion, clon);
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
		Map<String, Object>params= new HashMap<>();
		try {
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
		boolean regresar      = Boolean.FALSE;		
		try {
			dto= new TcKeetContratosDestajosContratistasDto();		
			dto.setIdUsuario(JsfBase.getIdUsuario());
			dto.setSemana(this.toSemana());
			dto.setPeriodo(this.toPeriodo());
			dto.setIdEstacion(estacion.getIdEstacion());
			dto.setIdContratoLoteContratista(this.conceptoExtra.getIdFigura());
			dto.setIdNomina(null);
			dto.setCosto(this.conceptoExtra.getImporte());
			dto.setAnticipo(0D);
			dto.setPorcentaje(100D);
			dto.setIdEstacionEstatus(EEstacionesEstatus.TERMINADO.getKey());
      dto.setJustificacion(this.conceptoExtra.getJustificacion());
			idContratoDestajo= DaoFactory.getInstance().insert(sesion, dto);
			this.conceptoExtra.setPuntosRevision(this.loadPuntosRevision(sesion, estacion.getIdEstacion()));
			this.processPuntosContratistasExtras(sesion, JsfBase.getIdUsuario(), idContratoDestajo);
      regresar= Boolean.TRUE;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	}
	
	private boolean processPuntosContratistasExtras(Session sesion, Long idUsuario, Long idContratoDestajo) throws Exception {
		boolean regresar= Boolean.TRUE;
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
		boolean regresar      = Boolean.FALSE;		
		try {
			dto= new TcKeetContratosDestajosProveedoresDto();		
			dto.setIdUsuario(JsfBase.getIdUsuario());
			dto.setSemana(this.toSemana());
			dto.setPeriodo(this.toPeriodo());
			dto.setIdEstacion(estacion.getIdEstacion());
			dto.setIdContratoLoteProveedor(this.conceptoExtra.getIdFigura());
			dto.setIdNomina(null);
			dto.setCosto(this.conceptoExtra.getImporte());
			dto.setAnticipo(0D);
			dto.setPorcentaje(100D);
			dto.setIdEstacionEstatus(EEstacionesEstatus.TERMINADO.getKey());
      dto.setJustificacion(this.conceptoExtra.getJustificacion());
			idContratoDestajo= DaoFactory.getInstance().insert(sesion, dto);
			this.conceptoExtra.setPuntosRevision(this.loadPuntosRevision(sesion, estacion.getIdEstacion()));
			this.processPuntosSubContratistasExtras(sesion, JsfBase.getIdUsuario(), idContratoDestajo);
      regresar= Boolean.TRUE;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadContratista
	
	private boolean processPuntosSubContratistasExtras(Session sesion, Long idUsuario, Long idContratoDestajo) throws Exception {
		boolean regresar= Boolean.TRUE;
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
		Map<String, Object>params= new HashMap<>();
		try {
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
	
	private boolean modificarConceptoExtraContratista(Session sesion) throws Exception { 
		boolean regresar            = Boolean.FALSE;		
		TcKeetContratosDestajosContratistasDto dto= null;
		TcKeetEstacionesDto estacion= null;
		Double diferencia           = 0D;
		try {			
      dto       = (TcKeetContratosDestajosContratistasDto)DaoFactory.getInstance().findById(TcKeetContratosDestajosContratistasDto.class, this.conceptoExtra.getIdPivote());
      diferencia= Numero.toAjustarDecimales(this.conceptoExtra.getImporte()- dto.getCosto());
      dto.setCosto(this.conceptoExtra.getImporte());				
      dto.setJustificacion(this.conceptoExtra.getJustificacion());
      DaoFactory.getInstance().update(sesion, dto);
      estacion= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.conceptoExtra.getIdEstacion());
      estacion.setNombre(this.conceptoExtra.getDescripcion());
      estacion.setDescripcion(this.conceptoExtra.getDescripcion());
      estacion.setCosto(this.conceptoExtra.getImporte());
      estacion.setCargo(this.conceptoExtra.getImporte());
      Methods.setValue(estacion, "cargo"+ dto.getSemana(), new Object[] {this.conceptoExtra.getImporte()});
      DaoFactory.getInstance().update(sesion, estacion);
      if(!Objects.equals(diferencia, 0D))
        this.actualizaEstacionPadre(sesion, estacion, diferencia, 0D, dto.getSemana().toString(), Boolean.TRUE, this.conceptoExtra.getIdContratoLote());
      regresar= Boolean.TRUE;		
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
  }
  
	private boolean modificarConceptoExtraSubContratista(Session sesion) throws Exception { 
		boolean regresar            = Boolean.FALSE;		
		TcKeetContratosDestajosProveedoresDto dto= null;
		TcKeetEstacionesDto estacion= null;
		Double diferencia           = 0D;
		try {			
      dto       = (TcKeetContratosDestajosProveedoresDto)DaoFactory.getInstance().findById(TcKeetContratosDestajosProveedoresDto.class, this.conceptoExtra.getIdPivote());
      diferencia= this.conceptoExtra.getImporte()- dto.getCosto();
      dto.setCosto(this.conceptoExtra.getImporte());				
      dto.setJustificacion(this.conceptoExtra.getJustificacion());
      DaoFactory.getInstance().update(sesion, dto);
      estacion= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.conceptoExtra.getIdEstacion());
      estacion.setNombre(this.conceptoExtra.getDescripcion());
      estacion.setDescripcion(this.conceptoExtra.getDescripcion());
      estacion.setCosto(this.conceptoExtra.getImporte());
      Methods.setValue(estacion, "cargo"+ dto.getSemana(), new Object[] {this.conceptoExtra.getImporte()});
      estacion.setCargo(this.conceptoExtra.getImporte());
      DaoFactory.getInstance().update(sesion, estacion);
      if(!Objects.equals(diferencia, 0D))
        this.actualizaEstacionPadre(sesion, estacion, diferencia, 0D, dto.getSemana().toString(), Boolean.TRUE, this.conceptoExtra.getIdContratoLote());
      regresar= Boolean.TRUE;		
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
  }
  
	private boolean eliminarConceptoExtraContratista(Session sesion, Long idUsuario) throws Exception { 
		boolean regresar            = Boolean.FALSE;		
		TcKeetContratosDestajosContratistasDto dto= null;
		TcKeetEstacionesDto estacion= null;
		Map<String, Object>params   = new HashMap<>();
		Double costo, anticipo      = 0D;
		try {			
			for(Entity puntoRevision: this.revision.getPuntosRevision()) {
				this.factorAcumulado= 0D;
				params.put("idEstacion", this.revision.getIdEstacion());
				params.put("idContratoLoteContratista", this.revision.getIdFigura());
				params.put("idEstacionEstatus", EEstacionesEstatus.CANCELADO.getKey());
				params.put("idPuntoPaquete", puntoRevision.getKey());
				dto= (TcKeetContratosDestajosContratistasDto) DaoFactory.getInstance().toEntity(sesion, TcKeetContratosDestajosContratistasDto.class, "VistaCapturaDestajosDto", "evidenciaDestajoContratista", params);									
				costo   = dto.getCosto()!= null? dto.getCosto(): 0D;				
				anticipo= dto.getAnticipo()!= null? dto.getAnticipo(): 0D;				
				if(this.processRechazosContratistas(sesion, idUsuario, puntoRevision, dto.getIdContratoDestajoContratista(), Boolean.FALSE)) {
					estacion= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion());										
					if(this.actualizaEstacionPadre(sesion, estacion, costo, anticipo, dto.getSemana().toString(), Boolean.FALSE, this.revision.getIdContratoLote())) {
						if(DaoFactory.getInstance().delete(sesion, dto)>= 1L) {						
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
		boolean regresar            = Boolean.FALSE;
		TcKeetContratosDestajosProveedoresDto dto= null;
		TcKeetEstacionesDto estacion= null;
		Map<String, Object>params   = new HashMap<>();
		Double costo, anticipo      = 0D;
		try {
			for(Entity puntoRevision: this.revision.getPuntosRevision()) {
				this.factorAcumulado= 0D;
				params.put("idEstacion", this.revision.getIdEstacion());
				params.put("idContratoLoteProveedor", this.revision.getIdFigura());
				params.put("idEstacionEstatus", EEstacionesEstatus.CANCELADO.getKey());
				params.put("idPuntoPaquete", puntoRevision.getKey());
				dto     = (TcKeetContratosDestajosProveedoresDto) DaoFactory.getInstance().toEntity(sesion, TcKeetContratosDestajosProveedoresDto.class, "VistaCapturaDestajosDto", "evidenciaDestajoProveedor", params);
				costo   = dto.getCosto()!= null? dto.getCosto(): 0D;				
				anticipo= dto.getAnticipo()!= null? dto.getAnticipo(): 0D;				
				if(processRechazosSubContratistas(sesion, idUsuario, puntoRevision, dto.getIdContratoDestajoProveedor(), Boolean.FALSE)) {
					estacion= (TcKeetEstacionesDto) DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.revision.getIdEstacion());					
					if(this.actualizaEstacionPadre(sesion, estacion, costo, anticipo, dto.getSemana().toString(), Boolean.FALSE, this.revision.getIdContratoLote())) {
						if(DaoFactory.getInstance().delete(sesion, dto)>= 1L) {			
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
 
  private boolean toDepurarEvidenciaContratista(Session sesion) throws Exception {
    boolean regresar= Boolean.FALSE;
    try {
      TcKeetContratosContratistasArchivosDto archivo= (TcKeetContratosContratistasArchivosDto)DaoFactory.getInstance().findById(sesion, TcKeetContratosContratistasArchivosDto.class, this.idContratoArchivo);
      if(archivo!= null) {
        File file= new File(archivo.getAlias());
        if(file.exists())
          file.delete();
        else
          LOG.error("El archivo no existe fisicamente ["+ archivo.getAlias()+ "], verificar porque no existe !");
        DaoFactory.getInstance().delete(sesion, archivo);
        regresar= Boolean.TRUE;
      } // if
    } // try  
		catch (Exception e) {			
			throw e;
		} // catch		
    return regresar;
  }
  
  private boolean toDepurarEvidenciaProveedor(Session sesion) throws Exception {
    boolean regresar= Boolean.FALSE;
    try {
      TcKeetContratosProveedoresArchivosDto archivo= (TcKeetContratosProveedoresArchivosDto)DaoFactory.getInstance().findById(sesion, TcKeetContratosProveedoresArchivosDto.class, this.idContratoArchivo);
      if(archivo!= null) {
        File file= new File(archivo.getAlias());
        if(file.exists())
          file.delete();
        else
          LOG.error("El archivo no existe fisicamente ["+ archivo.getAlias()+ "], verificar porque no existe !");
        DaoFactory.getInstance().delete(sesion, archivo);
        regresar= Boolean.TRUE;
      } // if
    } // try  
		catch (Exception e) {			
			throw e;
		} // catch		
    return regresar;
  }
 
  private Long toLoadUltimaNomina() throws Exception {
    Long regresar             = -1L;
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idTipoNomina", 1L);
      Entity ultima= (Entity)DaoFactory.getInstance().toEntity("VistaNominaDto", "ultima", params);
      if(ultima!= null && !ultima.isEmpty()) 
        regresar= ultima.toLong("idNomina");
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
  
}