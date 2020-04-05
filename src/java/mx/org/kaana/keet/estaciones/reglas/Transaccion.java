package mx.org.kaana.keet.estaciones.reglas;

import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.estaciones.beans.RegistroEstacion;
import mx.org.kaana.libs.pagina.JsfBase;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {
	
	private RegistroEstacion registroEstacion;

	public Transaccion(RegistroEstacion dto) {
		this.registroEstacion = dto;
	}
	

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar   = false;
		try {
			switch(accion){
				case AGREGAR:			
					this.registroEstacion.getEstacion().setIdUsuario(JsfBase.getIdUsuario());
					regresar= DaoFactory.getInstance().insert(sesion, this.registroEstacion.getEstacion())>= 1L;
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.registroEstacion.getEstacion())>= 1L;
					break;				
				case ELIMINAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.registroEstacion.getEstacion())>= 1L;
					break;
			} // switch
		} // try
		catch (Exception e) {			
			throw new Exception(e);
		} // catch		
		return regresar;
	}	// ejecutar
	
}