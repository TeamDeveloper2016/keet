package mx.org.kaana.keet.estmaciones.reglas;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import org.hibernate.Session;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.db.dto.TcKeetEstimacionesBitacoraDto;
import mx.org.kaana.keet.estmaciones.beans.Retencion;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
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
 
	private Estimaciones orden;	
	private String messageError;
  private TcKeetEstimacionesBitacoraDto bitacora;

	public Transaccion(Estimaciones orden) {
		this(orden, null);
	}

	public Transaccion(Estimaciones orden, TcKeetEstimacionesBitacoraDto bitacora) {
		this.orden   = orden;
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
		boolean regresar     = false;
		Siguiente consecutivo= null;
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
					break;
				case MODIFICAR:
          DaoFactory.getInstance().update(sesion, this.orden.getEstimacion());
          this.bitacora= new TcKeetEstimacionesBitacoraDto(this.orden.getEstimacion().getIdEstimacionEstatus(), -1L,  this.orden.getEstimacion().getIdEstimacion(), JsfBase.getIdUsuario(),  "");
          regresar= DaoFactory.getInstance().insert(sesion, bitacora)>= 1L;
          this.toFillDetalle(sesion);
					break;				
				case ELIMINAR:
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.orden.getEstimacion().setIdEstimacionEstatus(this.bitacora.getIdEstimacionEstatus());
 						regresar= DaoFactory.getInstance().update(sesion, this.orden.getEstimacion())>= 1L;
					} // if
					break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {
      Error.mensaje(e);			
			throw new Exception(this.messageError.concat("<br/>")+ (e!= null? e.getCause().toString(): ""));
		} // catch		
		LOG.info("Se registro de forma correcta la estimación: "+ this.orden.getEstimacion().getConsecutivo());
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
		Siguiente regresar= null;
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
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

} 