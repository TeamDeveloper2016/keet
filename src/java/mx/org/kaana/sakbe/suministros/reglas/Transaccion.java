package mx.org.kaana.sakbe.suministros.reglas;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.hibernate.Session;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.sakbe.combustibles.beans.Combustible;
import mx.org.kaana.sakbe.db.dto.TcSakbeCombustiblesDto;
import mx.org.kaana.sakbe.suministros.beans.Evidencia;
import mx.org.kaana.sakbe.db.dto.TcSakbeSuministrosBitacoraDto;
import mx.org.kaana.sakbe.db.dto.TcSakbeSuministrosDetallesDto;
import mx.org.kaana.sakbe.enums.ECombustiblesEstatus;
import mx.org.kaana.sakbe.enums.ESuministrosEstatus;
import mx.org.kaana.sakbe.suministros.beans.Suministro;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Transaccion extends IBaseTnx implements Serializable {

  
  private static final Log LOG = LogFactory.getLog(Transaccion.class);
	private static final long serialVersionUID=-3186367186737673670L;
 
	private Suministro suministro;	
	private String messageError;
	private TcSakbeSuministrosBitacoraDto bitacora;
  private List<Evidencia> evidencia;

	public Transaccion(Suministro suministro, TcSakbeSuministrosBitacoraDto bitacora) {
		this(suministro);
		this.bitacora= bitacora;
	} // Transaccion
	
	public Transaccion(Suministro suministro) {
		this(suministro, Collections.EMPTY_LIST);
	} // Transaccion

  public Transaccion(Suministro suministro, List<Evidencia> evidencia) {
    this.suministro= suministro;
    this.evidencia = evidencia;
  }
  
	public String getMessageError() {
		return messageError;
	} // getMessageError

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar                      = false;
		TcSakbeSuministrosBitacoraDto registro= null;
		try {
			this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" para el suministro de combustible");
			switch(accion) {
				case AGREGAR:
					Siguiente consecutivo= this.toSiguiente(sesion);
					this.suministro.setConsecutivo(consecutivo.getConsecutivo());
					this.suministro.setOrden(consecutivo.getOrden());
					this.suministro.setEjercicio(new Long(Fecha.getAnioActual()));
          this.suministro.setIdSuministroEstatus(ESuministrosEstatus.TERMINADO.getKey());
					DaoFactory.getInstance().insert(sesion, this.suministro);
					registro= new TcSakbeSuministrosBitacoraDto("", JsfBase.getIdUsuario(), this.suministro.getIdSuministroEstatus(), -1L, this.suministro.getIdSuministro());
					DaoFactory.getInstance().insert(sesion, registro);
          this.toEvidencias(sesion);
          regresar= this.toAddCombustibles(sesion);
					break;
				case MODIFICAR:
          this.suministro.setIdSuministroEstatus(ESuministrosEstatus.TERMINADO.getKey());
					DaoFactory.getInstance().update(sesion, this.suministro);
  			  registro= new TcSakbeSuministrosBitacoraDto("", JsfBase.getIdUsuario(), this.suministro.getIdSuministroEstatus(), -1L, this.suministro.getIdSuministro());
				  DaoFactory.getInstance().insert(sesion, registro);
          this.toEvidencias(sesion);
          regresar= this.toAddCombustibles(sesion);
					break;				
				case ELIMINAR:
					this.suministro.setIdSuministroEstatus(ECombustiblesEstatus.ELIMINADO.getKey());
          DaoFactory.getInstance().update(sesion, this.suministro);
  			  registro= new TcSakbeSuministrosBitacoraDto("", JsfBase.getIdUsuario(), this.suministro.getIdSuministroEstatus(), -1L, this.suministro.getIdSuministro());
          DaoFactory.getInstance().insert(sesion, registro);
          regresar= this.toRemoveCombustibles(sesion);
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.suministro.setIdSuministroEstatus(this.bitacora.getIdSuministroEstatus());
						DaoFactory.getInstance().update(sesion, this.suministro);
            if(Objects.equals(this.suministro.getIdSuministroEstatus(), ESuministrosEstatus.CANCELADO))
              regresar= this.toRemoveCombustibles(sesion);
            else
              if(Objects.equals(this.suministro.getIdSuministroEstatus(), ESuministrosEstatus.TERMINADO))
                regresar= this.toAddCombustibles(sesion);
					} // if
					break;
				case COMPLEMENTAR: 
					this.suministro.setIdSuministroEstatus(ECombustiblesEstatus.TERMINADO.getKey());
					DaoFactory.getInstance().update(sesion, this.suministro);
  				this.suministro.setIdSuministroEstatus(this.bitacora.getIdSuministroEstatus());
  			  registro= new TcSakbeSuministrosBitacoraDto("", -1L, JsfBase.getIdUsuario(), this.suministro.getIdSuministroEstatus(), this.suministro.getIdSuministro());
          DaoFactory.getInstance().insert(sesion, registro);
          regresar= this.toAddCombustibles(sesion);
					break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {		
			Error.mensaje(e);
			throw new Exception(this.messageError.concat("<br/>")+ (e!= null? e.getCause().toString(): ""));
		} // catch		
		if(this.suministro!= null)
			LOG.info("Se generó de forma correcta el folio: "+ this.suministro.getConsecutivo());
		return regresar;
	}	// ejecutar

	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());
		  params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcSakbeSuministrosDto", "siguiente", params, "siguiente");
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
	}
  
  private Boolean toEvidencias(Session sesion) throws Exception {
    Boolean regresar= this.evidencia.size()<= 0;
    try {   
      for (Evidencia item: this.evidencia) {
        switch(item.getSql()) {
          case SELECT:
            regresar= Boolean.TRUE;
            break;
          case INSERT:
            item.setIdSuministro(this.suministro.getIdSuministro());
            regresar= DaoFactory.getInstance().insert(sesion, item)> 0L;
            break;
          case UPDATE:
            regresar= DaoFactory.getInstance().update(sesion, item)> 0L;
            break;
          case DELETE:
            regresar= DaoFactory.getInstance().delete(sesion, item)> 0L;
            break;
        } // switch
      } // for
    } // try
    catch (Exception e) {
			throw e;
    } // catch	
    return regresar;
  }
  
  private Boolean toAddCombustibles(Session sesion) throws Exception {
    Boolean regresar                         = Boolean.FALSE;
    Map<String, Object> params               = new HashMap<>();
    List<TcSakbeCombustiblesDto> combustibles= null;
    TcSakbeSuministrosDetallesDto saldos           = null;
    try {      
      double total     = this.suministro.getLitros();
      double diferencia= 0D;
      params.put("idTipoCombustible", this.suministro.getIdTipoCombustible());      
      params.put("disponibles", ECombustiblesEstatus.ACEPTADO.getKey()+ ","+ ECombustiblesEstatus.EN_PROCESO.getKey());      
      combustibles= (List<TcSakbeCombustiblesDto>)DaoFactory.getInstance().findViewCriteria(sesion, TcSakbeCombustiblesDto.class, params, Constantes.SQL_TODOS_REGISTROS, "abiertos");
      for (TcSakbeCombustiblesDto item: combustibles) {
        if(total> item.getSaldo())
          diferencia= item.getSaldo();
        else
          diferencia= total;
        item.setSaldo(item.getSaldo()- diferencia);
        if(item.getSaldo()<= 0D)
          item.setIdCombustibleEstatus(ECombustiblesEstatus.TERMINADO.getKey());
        else  
          item.setIdCombustibleEstatus(ECombustiblesEstatus.EN_PROCESO.getKey());
        DaoFactory.getInstance().update(sesion, item);
        saldos= new TcSakbeSuministrosDetallesDto(
          JsfBase.getIdUsuario(), // Long idUsuario, 
          diferencia, // Double litros, 
          -1L, // Long idSuministroDetalle
          this.suministro.getIdSuministro(), // Long idSuministro, 
          item.getIdCombustible() // Long idCombustible
        );
        DaoFactory.getInstance().insert(sesion, saldos);
        total-= diferencia;
        if(total<= 0)
          break;
      } // for
      regresar= Boolean.TRUE;
    } // try
    catch (Exception e) {
			throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
  
  private Boolean toRemoveCombustibles(Session sesion) throws Exception {
    Boolean regresar              = Boolean.FALSE;
    Map<String, Object> params    = new HashMap<>();
    List<Combustible> combustibles= null;
    try {      
      params.put("idTipoCombustible", this.suministro.getIdTipoCombustible());      
      params.put("disponibles", ECombustiblesEstatus.ACEPTADO.getKey()+ ","+ ECombustiblesEstatus.EN_PROCESO.getKey());      
      combustibles= (List<Combustible>)DaoFactory.getInstance().findViewCriteria(sesion, Combustible.class, params, Constantes.SQL_TODOS_REGISTROS, "depurar");
      for (Combustible item: combustibles) {
        item.setSaldo(item.getSaldo()+ item.getLitrox());
        if(Objects.equals(item.getLitros(), item.getSaldo()))
          item.setIdCombustibleEstatus(ECombustiblesEstatus.ACEPTADO.getKey());
        else
          item.setIdCombustibleEstatus(ECombustiblesEstatus.EN_PROCESO.getKey());
        DaoFactory.getInstance().update(sesion, item);
        DaoFactory.getInstance().delete(sesion, TcSakbeSuministrosDetallesDto.class, item.getIdSuministroDetalle());
      } // for
      regresar= Boolean.TRUE;
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