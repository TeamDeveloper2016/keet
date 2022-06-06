package mx.org.kaana.sakbe.combustibles.reglas;

import com.google.common.base.Objects;
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
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.sakbe.combustibles.beans.Combustible;
import mx.org.kaana.sakbe.combustibles.beans.Evidencia;
import mx.org.kaana.sakbe.db.dto.TcSakbeCombustiblesBitacoraDto;
import mx.org.kaana.sakbe.enums.ECombustiblesEstatus;
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
 
	private Combustible combustible;	
	private String messageError;
	private TcSakbeCombustiblesBitacoraDto bitacora;
  private List<Evidencia> evidencia;

	public Transaccion(Combustible combustible, TcSakbeCombustiblesBitacoraDto bitacora) {
		this(combustible);
		this.bitacora= bitacora;
	} // Transaccion
	
	public Transaccion(Combustible combustible) {
		this(combustible, Collections.EMPTY_LIST);
	} // Transaccion

  public Transaccion(Combustible combustible, List<Evidencia> evidencia) {
    this.combustible= combustible;
    this.evidencia  = evidencia;
  }
  
	public String getMessageError() {
		return messageError;
	} // getMessageError

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar                       = false;
		TcSakbeCombustiblesBitacoraDto registro= null;
		try {
			this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" para el ticket de compra");
			if(this.combustible!= null && Objects.equal(-1L, this.combustible.getIdBanco())) 
        this.combustible.setIdBanco(null);
			switch(accion) {
				case AGREGAR:
					Siguiente consecutivo= this.toSiguiente(sesion);
					this.combustible.setConsecutivo(consecutivo.getConsecutivo());
					this.combustible.setOrden(consecutivo.getOrden());
					this.combustible.setEjercicio(new Long(Fecha.getAnioActual()));
          this.combustible.setSaldo(this.combustible.getLitros());
          this.combustible.setIdCombustibleEstatus(ECombustiblesEstatus.ACEPTADO.getKey());
					DaoFactory.getInstance().insert(sesion, this.combustible);
					registro= new TcSakbeCombustiblesBitacoraDto("", -1L, JsfBase.getIdUsuario(), this.combustible.getIdCombustibleEstatus(), this.combustible.getIdCombustible());
					DaoFactory.getInstance().insert(sesion, registro);
          regresar= this.toEvidencias(sesion);
					break;
				case MODIFICAR:
          double diferencia= Numero.toRedondearSat(this.combustible.getLitros()- this.combustible.getSaldo());
          this.combustible.setSaldo(Numero.toRedondearSat(this.combustible.getSaldo()+ diferencia));
          this.combustible.setIdCombustibleEstatus(ECombustiblesEstatus.ACEPTADO.getKey());
					DaoFactory.getInstance().update(sesion, this.combustible);
  			  registro= new TcSakbeCombustiblesBitacoraDto("", -1L, JsfBase.getIdUsuario(), this.combustible.getIdCombustibleEstatus(), this.combustible.getIdCombustible());
				  DaoFactory.getInstance().insert(sesion, registro);
          regresar= this.toEvidencias(sesion);
					break;				
				case ELIMINAR:
					this.combustible.setIdCombustibleEstatus(ECombustiblesEstatus.ELIMINADO.getKey());
          DaoFactory.getInstance().update(sesion, this.combustible);
  			  registro= new TcSakbeCombustiblesBitacoraDto("", -1L, JsfBase.getIdUsuario(), this.combustible.getIdCombustibleEstatus(), this.combustible.getIdCombustible());
          regresar= DaoFactory.getInstance().insert(sesion, registro)>= 1L;
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.combustible.setIdCombustibleEstatus(this.bitacora.getIdCombustibleEstatus());
						regresar= DaoFactory.getInstance().update(sesion, this.combustible)>= 1L;
					} // if
					break;
				case DEPURAR:
					break;
				case COMPLEMENTAR: 
					this.combustible.setIdCombustibleEstatus(ECombustiblesEstatus.ELABORADO.getKey());
					DaoFactory.getInstance().update(sesion, this.combustible);
  			  registro= new TcSakbeCombustiblesBitacoraDto("", -1L, JsfBase.getIdUsuario(), this.combustible.getIdCombustibleEstatus(), this.combustible.getIdCombustible());
          regresar= DaoFactory.getInstance().insert(sesion, registro)>= 1L;
					break;
				case COMPLETO: 
					break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {		
			Error.mensaje(e);
			throw new Exception(this.messageError.concat("<br/>")+ (e!= null? e.getCause().toString(): ""));
		} // catch		
		if(this.combustible!= null)
			LOG.info("Se generó de forma correcta el folio: "+ this.combustible.getConsecutivo());
		return regresar;
	}	// ejecutar

	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());
			params.put("idEmpresa", this.combustible.getIdEmpresa());
		  params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcSakbeCombustiblesDto", "siguiente", params, "siguiente");
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
            item.setIdCombustible(this.combustible.getIdCombustible());
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