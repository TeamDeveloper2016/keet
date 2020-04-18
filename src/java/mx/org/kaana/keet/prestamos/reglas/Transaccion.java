package mx.org.kaana.keet.prestamos.reglas;

import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.prestamos.beans.Documento;
import mx.org.kaana.keet.prestamos.beans.RegistroPrestamo;
import mx.org.kaana.libs.pagina.JsfBase;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private RegistroPrestamo prestamo;	
	private IBaseDto dtoDelete;

	public Transaccion(IBaseDto dtoDelete) {
		this.dtoDelete = dtoDelete;
	}	
	
	public Transaccion(RegistroPrestamo prestamo) {
		this.prestamo= prestamo;	
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar= true;
		Long idUsuario  = -1L;
		try {
			idUsuario= JsfBase.getIdUsuario();
			switch(accion){
				case AGREGAR:
					this.prestamo.getPrestamo().setIdUsuario(idUsuario);
					regresar= DaoFactory.getInstance().insert(sesion, this.prestamo.getPrestamo())>= 1L;
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.prestamo.getPrestamo())>= 1L;
				case ELIMINAR:
					DaoFactory.getInstance().delete(sesion, this.prestamo.getPrestamo());
					break;
				case SUBIR:
					for(Documento item: this.prestamo.getDocumentos()){						
						if(DaoFactory.getInstance().insert(sesion, item)>=1L)
							toSaveFile(item.getIdArchivo());
					} // for
					break;
				case DEPURAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.dtoDelete)>= 1L;
					break;
			} // switch						
		} // try
		catch (Exception e) {			
			throw new Exception(e);
		} // catch		
		return regresar;
	}	// ejecutar

	
}