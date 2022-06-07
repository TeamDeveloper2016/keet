package mx.org.kaana.sakbe.suministros.reglas;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.sakbe.suministros.beans.Evidencia;
import mx.org.kaana.sakbe.db.dto.TcSakbeSuministrosBitacoraDto;
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
		boolean regresar                       = false;
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
          regresar= this.toEvidencias(sesion);
					break;
				case MODIFICAR:
          this.suministro.setIdSuministroEstatus(ESuministrosEstatus.TERMINADO.getKey());
					DaoFactory.getInstance().update(sesion, this.suministro);
  			  registro= new TcSakbeSuministrosBitacoraDto("", JsfBase.getIdUsuario(), this.suministro.getIdSuministroEstatus(), -1L, this.suministro.getIdSuministro());
				  DaoFactory.getInstance().insert(sesion, registro);
          regresar= this.toEvidencias(sesion);
					break;				
				case ELIMINAR:
					this.suministro.setIdSuministroEstatus(ECombustiblesEstatus.ELIMINADO.getKey());
          DaoFactory.getInstance().update(sesion, this.suministro);
  			  registro= new TcSakbeSuministrosBitacoraDto("", JsfBase.getIdUsuario(), this.suministro.getIdSuministroEstatus(), -1L, this.suministro.getIdSuministro());
          regresar= DaoFactory.getInstance().insert(sesion, registro)>= 1L;
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.suministro.setIdSuministroEstatus(this.bitacora.getIdSuministroEstatus());
						regresar= DaoFactory.getInstance().update(sesion, this.suministro)>= 1L;
					} // if
					break;
				case COMPLEMENTAR: 
					this.suministro.setIdSuministroEstatus(ECombustiblesEstatus.TERMINADO.getKey());
					DaoFactory.getInstance().update(sesion, this.suministro);
  			  registro= new TcSakbeSuministrosBitacoraDto("", -1L, JsfBase.getIdUsuario(), this.suministro.getIdSuministroEstatus(), this.suministro.getIdCombustible());
          regresar= DaoFactory.getInstance().insert(sesion, registro)>= 1L;
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
  
} 