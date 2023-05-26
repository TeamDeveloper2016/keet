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
	private String messageError;

	public Transaccion(RegistroDesarrollo dto) {
		this.registroDesarrollo = dto;
	}
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar   = false;
		Siguiente siguiente= null;
		try {
      this.messageError= "";
			switch(accion) {
				case AGREGAR:			
					this.registroDesarrollo.getDesarrollo().setIdUsuario(JsfBase.getIdUsuario());
					this.registroDesarrollo.getDomicilio().setIdUsuario(JsfBase.getIdUsuario());
					this.registroDesarrollo.getDomicilio().setIdDomicilio(-1L);
					DaoFactory.getInstance().insert(sesion, this.registroDesarrollo.getDomicilio());
					this.registroDesarrollo.getDesarrollo().setIdDomicilio(this.registroDesarrollo.getDomicilio().getKey());
					regresar= DaoFactory.getInstance().insert(sesion, this.registroDesarrollo.getDesarrollo())>= 1L;
					break;
				case MODIFICAR:
					this.registroDesarrollo.getDomicilio().setIdDomicilio(this.registroDesarrollo.getDesarrollo().getIdDomicilio());
					DaoFactory.getInstance().update(sesion, this.registroDesarrollo.getDomicilio());
					regresar= DaoFactory.getInstance().update(sesion, this.registroDesarrollo.getDesarrollo())>= 1L;
					break;				
				case ELIMINAR:
					DaoFactory.getInstance().delete(sesion, this.registroDesarrollo.getDomicilio());
					regresar= DaoFactory.getInstance().delete(sesion, this.registroDesarrollo.getDesarrollo())>= 1L;
					break;
			} // switch
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
	
}