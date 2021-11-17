package mx.org.kaana.keet.catalogos.partidas.reglas;

import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.keet.db.dto.TcKeetPartidasDto;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {
	
	private TcKeetPartidasDto partidasDto;

	public Transaccion(TcKeetPartidasDto partidasDto) {
		this.partidasDto = partidasDto;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar   = false;
		try {

			switch(accion){
				case AGREGAR:	
					this.partidasDto.setIdUsuario(JsfBase.getIdUsuario());
					this.partidasDto.setOrden(1L);
					this.partidasDto.setNivel(6L);
					DaoFactory.getInstance().insert(sesion, this.partidasDto);
					break;
				case MODIFICAR:
					DaoFactory.getInstance().update(sesion, this.partidasDto);
					break;				
				case ELIMINAR:
				  DaoFactory.getInstance().delete(sesion, this.partidasDto);
					break;
			} // switch
			regresar= true;
		} // try
		catch (Exception e) {			
			throw new Exception((e!= null? e.getCause().toString(): ""));
		} // catch		
		return regresar;
	}	// ejecutar

}