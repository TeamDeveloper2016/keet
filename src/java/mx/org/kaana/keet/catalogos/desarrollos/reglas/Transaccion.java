package mx.org.kaana.keet.catalogos.desarrollos.reglas;

import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.catalogos.desarrollos.beans.RegistroDesarrollo;
import mx.org.kaana.libs.pagina.JsfBase;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {
	
	private RegistroDesarrollo registroDesarrollo;

	public Transaccion(RegistroDesarrollo dto) {
		this.registroDesarrollo = dto;
	}
	
	

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar   = false;
		Siguiente siguiente= null;
		try {
			switch(accion){
				case AGREGAR:			
					this.registroDesarrollo.getDesarrollo().setIdDomicilio(1L);
					this.registroDesarrollo.getDesarrollo().setIdUsuario(JsfBase.getIdUsuario());
					regresar= DaoFactory.getInstance().insert(sesion, this.registroDesarrollo.getDesarrollo())>= 1L;
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.registroDesarrollo.getDesarrollo())>= 1L;
					break;				
				case ELIMINAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.registroDesarrollo.getDesarrollo())>= 1L;
					break;
			} // switch
		} // try
		catch (Exception e) {			
			throw new Exception(e);
		} // catch		
		return regresar;
	}	// ejecutar
	
}