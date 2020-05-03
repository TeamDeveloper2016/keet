package mx.org.kaana.keet.catalogos.puntoscontrol.reglas;

import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.catalogos.puntoscontrol.beans.RegistroPunto;
import mx.org.kaana.libs.pagina.JsfBase;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {
	
	private RegistroPunto registroPunto;

	public Transaccion(RegistroPunto registroPunto) {
		this.registroPunto = registroPunto;
	}
		

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar   = false;
		Siguiente siguiente= null;
		try {
			switch(accion){
				case AGREGAR:			
					this.registroPunto.getPuntoGrupo().setIdUsuario(JsfBase.getIdUsuario());
					
				
					break;
				case MODIFICAR:
				
					break;				
				case ELIMINAR:
				
					break;
			} // switch
		} // try
		catch (Exception e) {			
			throw new Exception(e);
		} // catch		
		return regresar;
	}	// ejecutar
	
}