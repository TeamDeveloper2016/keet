package mx.org.kaana.keet.catalogos.dinamicos.reglas;

import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private IBaseDto dto;

	public Transaccion(IBaseDto dto) {
		this.dto = dto;
	}	

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {	
		boolean regresar        = false;
		try {	
			switch(accion){
				case AGREGAR:			
					regresar= DaoFactory.getInstance().insert(sesion, this.dto)>= 1L;
					break;
				case MODIFICAR:				
					regresar= DaoFactory.getInstance().update(sesion, this.dto)>= 1L;
					break;				
				case ELIMINAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.dto)>= 1L;				
					break;				
			} // switch
			if(!regresar)
        throw new Exception("Error al realizar la transaccion del catalogo.");
		} // try
		catch (Exception e) {			
			throw new Exception(e);
		} // catch		
		return regresar;
	} // ejecutar
}
