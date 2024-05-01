package mx.org.kaana.keet.estimacion.reglas;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.hibernate.Session;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.keet.db.dto.TcKeetContratosBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosDto;
import mx.org.kaana.keet.db.dto.TcKeetEstimacionesArchivosDto;
import mx.org.kaana.keet.db.dto.TcKeetEstimacionesBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetEstimacionesDetallesDto;
import mx.org.kaana.keet.db.dto.TcKeetEstimacionesDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasContratosDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasFrentesDto;
import mx.org.kaana.keet.estimacion.beans.EEstatusEstimaciones;
import mx.org.kaana.keet.estimacion.beans.Retencion;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.articulos.beans.Importado;
import org.apache.log4j.Logger;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 15/03/2022
 *@time 10:06:11 AM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Transaccion extends IBaseTnx implements Serializable {

  private static final Logger LOG = Logger.getLogger(Transaccion.class);
	private static final long serialVersionUID=-6069204157451117549L;
 
	private Long idEstimacion;	
	private Estimaciones orden;	
	private String messageError;
	private List<Importado> documentos;
  private TcKeetEstimacionesBitacoraDto bitacora;

	public Transaccion(Long idEstimacion) {
		this.idEstimacion= idEstimacion;
	}
  
	public Transaccion(Estimaciones orden, List<Importado> documentos) {
		this.orden= orden;
    this.documentos= documentos;
	}

	public Transaccion(TcKeetEstimacionesBitacoraDto bitacora) {
		this.bitacora= bitacora;
	}
	
	protected void setMessageError(String messageError) {
		this.messageError=messageError;
	}

	public String getMessageError() {
		return messageError;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar          = false;
		Siguiente consecutivo     = null;
    TcKeetEstimacionesDto item= null;
		try {
			this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" la esitmación");
			switch(accion) {
				case AGREGAR:
          consecutivo= this.toSiguiente(sesion);
          this.orden.getEstimacion().setConsecutivo(consecutivo.getConsecutivo());			
          this.orden.getEstimacion().setOrden(consecutivo.getOrden());			
          this.orden.getEstimacion().setEjercicio(new Long(Fecha.getAnioActual()));
          this.orden.getEstimacion().setIdUsuario(JsfBase.getIdUsuario());
          DaoFactory.getInstance().insert(sesion, this.orden.getEstimacion());
          this.bitacora= new TcKeetEstimacionesBitacoraDto(this.orden.getEstimacion().getIdEstimacionEstatus(), -1L,  this.orden.getEstimacion().getIdEstimacion(), JsfBase.getIdUsuario(),  "");
          regresar= DaoFactory.getInstance().insert(sesion, bitacora)>= 1L;
          this.toFillDetalle(sesion);
          this.toFillDocumentos(sesion);
          this.toUpdateFrentes(sesion, this.orden.getEstimacion().getIdNomina(), this.orden.getEstimacion().getIdContrato(), this.orden.getEstimacion().getImporte());
					break;
				case MODIFICAR:
          item= (TcKeetEstimacionesDto)DaoFactory.getInstance().findById(sesion, TcKeetEstimacionesDto.class, this.orden.getEstimacion().getIdEstimacion());
          Double value= this.orden.getEstimacion().getImporte()- item.getImporte();
          DaoFactory.getInstance().update(sesion, this.orden.getEstimacion());
          this.bitacora= new TcKeetEstimacionesBitacoraDto(this.orden.getEstimacion().getIdEstimacionEstatus(), -1L,  this.orden.getEstimacion().getIdEstimacion(), JsfBase.getIdUsuario(),  "");
          regresar= DaoFactory.getInstance().insert(sesion, bitacora)>= 1L;
          this.toFillDetalle(sesion);
          this.toFillDocumentos(sesion);
          this.toUpdateFrentes(sesion, item.getIdNomina(), item.getIdContrato(), value);
					break;				
				case ELIMINAR:
          item= (TcKeetEstimacionesDto)DaoFactory.getInstance().findById(sesion, TcKeetEstimacionesDto.class, this.idEstimacion);
          this.toUpdateFrentes(sesion, item.getIdNomina(), item.getIdContrato(), item.getImporte()* -1);
          regresar= this.toDeleteEstimacion(sesion);
					break;
				case DEPURAR:
          regresar= this.toDeleteDocumento(sesion);
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
            TcKeetEstimacionesDto estimacion= (TcKeetEstimacionesDto)DaoFactory.getInstance().findById(sesion, TcKeetEstimacionesDto.class, this.bitacora.getIdEstimacion());
						estimacion.setIdEstimacionEstatus(this.bitacora.getIdEstimacionEstatus());
            if(Objects.equals(this.bitacora.getIdEstimacionEstatus(), EEstatusEstimaciones.CANCELADA.getIdEstatusFicticia()))
              this.toCancelEstimacion(sesion);
            else
              if(Objects.equals(this.bitacora.getIdEstimacionEstatus(), EEstatusEstimaciones.TERMINADA.getIdEstatusFicticia()))
                this.toTerminateEstimacion(sesion);
            this.toCheckContrato(sesion, estimacion.getIdContrato());
 						regresar= DaoFactory.getInstance().update(sesion, estimacion)>= 1L;
					} // if
					break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {
      if(e!= null)
        if(e.getCause()!= null)
          this.messageError= this.messageError.concat("<br/>").concat(e.getCause().toString());
        else
          this.messageError= this.messageError.concat("<br/>").concat(e.getMessage());
			throw new Exception(this.messageError);
		} // catch		
		return regresar;
	}	// ejecutar

  private void toFillDetalle(Session sesion) throws Exception {
    try {   
      for (Retencion item: this.orden.getEstimacion().getRetenciones()) {
        switch(item.getSql()) {
          case INSERT:
            item.setIdUsuario(JsfBase.getIdUsuario());
            item.setIdEstimacion(this.orden.getEstimacion().getIdEstimacion());
            DaoFactory.getInstance().insert(sesion, item);
            break;
          case UPDATE:
            item.setRegistro(LocalDateTime.now());
            DaoFactory.getInstance().update(sesion, item);
            break;
          case SELECT:
          case DELETE:
            break;
        } // switch
      } // for
    } // try
    catch (Exception e) {
			throw e;
    } // catch	
  }
  
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("ejercicio", this.getCurrentYear());
			params.put("idEmpresa", this.orden.getEstimacion().getIdEmpresa());
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcKeetEstimacionesDto", "siguiente", params, "siguiente");
			if(next.getData()!= null)
			  regresar= new Siguiente(next.toLong());
			else
			  regresar= new Siguiente(Configuracion.getInstance().isEtapaDesarrollo()? 900001L: 1L);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toSiguiente

  private Boolean toDeleteEstimacion(Session sesion) throws Exception {
    Boolean regresar          = Boolean.FALSE;
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idEstimacion", this.idEstimacion);      
      DaoFactory.getInstance().deleteAll(sesion, TcKeetEstimacionesDetallesDto.class, params);
      DaoFactory.getInstance().deleteAll(sesion, TcKeetEstimacionesBitacoraDto.class, params);
      DaoFactory.getInstance().deleteAll(sesion, TcKeetEstimacionesArchivosDto.class, params);
      regresar= DaoFactory.getInstance().delete(sesion, new TcKeetEstimacionesDto(this.idEstimacion))> 0L;
    } // try
    catch (Exception e) {
			throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }

  private Boolean toCancelEstimacion(Session sesion) throws Exception {
    Boolean regresar= Boolean.TRUE;
    Map<String, Object> params= new HashMap<>();
    try {      
      // CANCELAR LA FACTURA Y LA VENTA ASOCIADA A LA ESTIMACION
      params.put("idEstimacion", this.idEstimacion);      
    } // try
    catch (Exception e) {
			throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
  
  private Boolean toTerminateEstimacion(Session sesion) throws Exception {
    Boolean regresar= Boolean.TRUE;
    Map<String, Object> params= new HashMap<>();
    try {      
      // CAMBIAR EL ESTATUS DE LA FACTURA Y LA VENTA
      params.put("idEstimacion", this.idEstimacion);      
    } // try
    catch (Exception e) {
			throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
  
  private void toCheckContrato(Session sesion, Long idContrato) throws Exception {
    Double total              = 0D;
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idContrato", idContrato);      
      Value value= DaoFactory.getInstance().toField("TcKeetEstimacionesDto", "total", params, "total");
      if(value!= null && value.getData()!= null)
        total= value.toDouble();
      TcKeetContratosDto item= (TcKeetContratosDto)DaoFactory.getInstance().findById(sesion, TcKeetContratosDto.class, idContrato);      
      if(item!= null && Objects.equals(total, item.getCosto())) {
        item.setIdContratoEstatus(EContratosEstatus.COBRADO.getKey());
        DaoFactory.getInstance().update(sesion, item);
        TcKeetContratosBitacoraDto evidencia= new TcKeetContratosBitacoraDto("CONTRATO ESTIMADO AL 100% SIN FONDO DE GARANTÍA", item.getIdContratoEstatus(), JsfBase.getIdUsuario(), -1L, item.getIdContrato());
        DaoFactory.getInstance().insert(sesion, evidencia);
      } // if  
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  private void toFillDocumentos(Session sesion) throws Exception {
    TcKeetEstimacionesArchivosDto tmp= null;
    try {      
      for (Importado item: this.documentos) {
        tmp= new TcKeetEstimacionesArchivosDto(
          -1L, // Long idEstimacionArchivo, 
          this.orden.getEstimacion().getIdEstimacion(), // Long idEstimacion, 
          item.getOriginal(), // String archivo, 
          item.getRuta(), // String ruta, 
          item.getFileSize(), // Long tamanio, 
          JsfBase.getIdUsuario(), // Long idUsuario, 
          2L, // Long idTipoArchivo, 
          item.getObservaciones(), // String observaciones, 
          1L, // Long idPrincipal, 
          Configuracion.getInstance().getPropiedadSistemaServidor("estimaciones").concat(item.getRuta()).concat(item.getName()), // String alias, 
          item.getName() // String nombre
        );
        this.toSaveFile(item.getIdArchivo());
        TcKeetEstimacionesArchivosDto exists= (TcKeetEstimacionesArchivosDto)DaoFactory.getInstance().toEntity(TcKeetEstimacionesArchivosDto.class, "TcKeetEstimacionesArchivosDto", "identically", tmp.toMap());
        File file= new File(tmp.getAlias());
        if(exists== null && file.exists()) {
          DaoFactory.getInstance().updateAll(sesion, TcKeetEstimacionesArchivosDto.class, tmp.toMap());
          DaoFactory.getInstance().insert(sesion, tmp);
        } // if
        else
          if(!file.exists())
            LOG.warn("INVESTIGAR PORQUE NO EXISTE EL ARCHIVO EN EL SERVIDOR: "+ tmp.getAlias());
        sesion.flush();
        this.toCheckDeleteFile(sesion, item.getName());
      } // for
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }
  
  private Boolean toDeleteDocumento(Session sesion) throws Exception {
    Boolean regresar= Boolean.FALSE;
    try {      
      regresar= DaoFactory.getInstance().delete(sesion, new TcKeetEstimacionesArchivosDto(this.idEstimacion))> 0L;
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    return regresar;
  }
  
  private void toUpdateFrentes(Session sesion, Long idNomina, Long idContrato, Double value) throws Exception {
    List<TcKeetNominasFrentesDto> items= null;
    Map<String, Object> params= new HashMap<>();
    Long idDesarrollo         = -1L;
    Double total              = 0D;
    try {      
      params.put("idNomina", idNomina);
      params.put("idContrato", idContrato);
      idDesarrollo= DaoFactory.getInstance().toField(sesion, "VistaCostosDto", "desarrollo", params, "idDesarrollo").toLong();
      params.put("idDesarrollo", idDesarrollo);
      TcKeetNominasFrentesDto frente= (TcKeetNominasFrentesDto)DaoFactory.getInstance().toEntity(sesion, TcKeetNominasFrentesDto.class, "TcKeetNominasFrentesDto", "igual", params);
      if(Objects.equals(frente, null)) {
        total= value;
        frente= new TcKeetNominasFrentesDto(
          value, // Double total, 
          JsfBase.getIdUsuario(), // Long idUsuario, 
          null, // Long idContrato, 
          -1L, // Long idNominaFrente, 
          100D, // Double porcentaje, 
          idNomina, // Long idNomina
          idDesarrollo // Long idDesarrollo
        );
        DaoFactory.getInstance().insert(sesion, frente);
      } // if
      else {
        total= frente.getTotal()+ value;
        frente.setTotal(total);
        frente.setIdUsuario(JsfBase.getIdUsuario());
        frente.setRegistro(LocalDateTime.now());
        DaoFactory.getInstance().update(sesion, frente);
      } // if
      
      Boolean existe= Boolean.FALSE;
      items= (List<TcKeetNominasFrentesDto>)DaoFactory.getInstance().toEntitySet(sesion, TcKeetNominasFrentesDto.class, "TcKeetNominasFrentesDto", "nomina", params);
      if(!Objects.equals(items, null) && !items.isEmpty()) {
        for (TcKeetNominasFrentesDto item: items) {
          if(Objects.equals(item.getIdContrato(), idContrato)) {
            item.setTotal(item.getTotal()+ value);
            existe= Boolean.TRUE;
          } // if  
          item.setIdUsuario(JsfBase.getIdUsuario());
          item.setPorcentaje(Objects.equals(total, 0D)? 0D: item.getTotal()*100/total); 
          DaoFactory.getInstance().update(sesion, item);
        } // for
      } // if
      if(!existe || Objects.equals(items, null) || items.isEmpty()) {
        frente= new TcKeetNominasFrentesDto(
          value, // Double total, 
          JsfBase.getIdUsuario(), // Long idUsuario, 
          idContrato, // Long idContrato, 
          -1L, // Long idNominaFrente, 
          Objects.equals(total, 0D)? 0D: value*100/total, // Double porcentaje, 
          idNomina, // Long idNomina
          idDesarrollo // Long idDesarrollo
        );
        DaoFactory.getInstance().insert(sesion, frente);
      } // if
      sesion.flush();
      this.toChekContratoCostos(sesion, idNomina, idDesarrollo);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  private void toChekContratoCostos(Session sesion, Long idNomina, Long idDesarrollo) throws Exception {
    TcKeetNominasContratosDto existe= null;
    List<Entity> contratos    = null;
    Entity nomina             = null;
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idNomina", idNomina);
      params.put("idDesarrollo", idDesarrollo);
      nomina= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcKeetNominasGruposDto", "identically", params);
      if(!Objects.equals(nomina, null) && !nomina.isEmpty()) {
        contratos= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "VistaCostosDto", "contratos", params);
        if(!Objects.equals(contratos, null) && !contratos.isEmpty()) {
          for (Entity item: contratos) {
            params.put("idContrato", item.toLong("idContrato"));
            if(Objects.equals(item.toLong("idContrato"), null))
              existe= (TcKeetNominasContratosDto)DaoFactory.getInstance().toEntity(sesion, TcKeetNominasContratosDto.class, "TcKeetNominasContratosDto", "contrato", params);
            else
              existe= (TcKeetNominasContratosDto)DaoFactory.getInstance().toEntity(sesion, TcKeetNominasContratosDto.class, "TcKeetNominasContratosDto", "identically", params);
            if(Objects.equals(existe, null)) {
              existe= new TcKeetNominasContratosDto(
                item.toDouble("total"), // Double total, 
                idDesarrollo, // Long idDesarrollo, 
                item.toLong("idContrato"), // Long idContrato, 
                -1L, // Long idNominaContrato, 
                item.toDouble("porcentaje"), // Double porcentaje, 
                idNomina // Long idNomina
              );
              DaoFactory.getInstance().insert(sesion, existe);
            } // if
            else {
              existe.setPorcentaje(item.toDouble("porcentaje"));
              existe.setTotal(item.toDouble("total"));
              existe.setRegistro(LocalDateTime.now());
              DaoFactory.getInstance().update(sesion, existe);
            } // else
          } // for
        } // if
      } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }

} 