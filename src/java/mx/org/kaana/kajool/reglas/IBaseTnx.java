package mx.org.kaana.kajool.reglas;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date May 2, 2012
 * @time 1:53:33 PM
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */
import com.google.common.base.Objects;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.hibernate.SessionFactoryFacade;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EEtapaServidor;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.db.dto.TcKeetBitacorasDto;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import org.hibernate.Session;
import org.hibernate.Transaction;

public abstract class IBaseTnx {

	private	Transaction transaction;
	private Long idFuenteDato;
	private Session sesion;

	public IBaseTnx() {
		this(-1L);
	}

	public IBaseTnx(Long idFuenteDato) {
		this.idFuenteDato= idFuenteDato;
	}

	protected abstract boolean ejecutar(Session sesion, EAccion accion) throws Exception;

	public final boolean ejecutar(EAccion accion) throws Exception {
		boolean regresar= false;
		try {
			this.sesion= SessionFactoryFacade.getInstance().getSession(this.idFuenteDato);
			this.transaction= sesion.beginTransaction();
			this.sesion.clear();
			regresar= ejecutar(sesion, accion);
			this.transaction.commit();
		} // try
		catch (Exception e) {
			if (this.transaction!= null) {
				this.transaction.rollback();
			} // if
			throw e;
		} // catch
		finally {
			if (this.sesion!= null) {
				this.sesion.close();
			} // if
			this.transaction= null;
			this.sesion     = null;
		} // finally
		return regresar;
	}
	
	protected void commit() throws Exception {
  	if (this.transaction!= null && this.transaction.isActive()) {
			this.transaction.commit();
			this.transaction.begin();
		} // if	
	}

	protected Siguiente toNextItem(Long orden) {
		return new Siguiente(orden);
	}

  protected int getCurrentYear() {
		//return Configuracion.getInstance().isEtapaDesarrollo()? Fecha.getAnioActual()* 10+ 1: Fecha.getAnioActual();
		return Fecha.getAnioActual();
	}	
	
  protected String getCurrentSign() {
		return Configuracion.getInstance().isEtapaDesarrollo()? ">": "<=";
	}	

  protected final void bitacora(Session sesion, String proceso, IBaseDto newData) throws Exception {
		if(newData!= null) {
		  IBaseDto oldData= DaoFactory.getInstance().findById(sesion, newData.getClass(), newData.getKey());
		  if(oldData!= null) {
				Map<String, Object> old= oldData.toMap();
				Map<String, Object> tmp= newData.toMap();
				for(String key: old.keySet()) {
          if(!Objects.equal(old.get(key),tmp.get(key))) {
						TcKeetBitacorasDto bitacora= new TcKeetBitacorasDto(
							oldData.getKey(), //Long idKey, 
							String.valueOf(tmp.get(key)!= null? tmp.get(key): ""), // String despues, 
							Configuracion.getInstance().getEtapaServidor().equals(EEtapaServidor.DESARROLLO)? 1L: JsfBase.getIdUsuario(), // Long idUsuario, 
							proceso, // String proceso, 
							-1L, // Long idBitacora, 
							newData.getClass().getName(), // String tabla, 
							key, // String campo, 
							String.valueOf(old.get(key)!= null? old.get(key): "") // String antes
						);
						DaoFactory.getInstance().insert(sesion, bitacora);
					} // if
				} // for
			}	// if
		} // if	
	}

  protected final void bitacora(String proceso, IBaseDto newData) throws Exception {
		bitacora(this.sesion, proceso, newData);
	}
	
}
