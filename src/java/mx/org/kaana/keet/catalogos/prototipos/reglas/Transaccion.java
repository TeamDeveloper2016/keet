package mx.org.kaana.keet.catalogos.prototipos.reglas;

import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.catalogos.prototipos.beans.RegistroPrototipo;
import mx.org.kaana.keet.catalogos.prototipos.beans.SistemaConstructivo;
import mx.org.kaana.keet.db.dto.TcKeetPrototiposArchivosDto;
import mx.org.kaana.libs.pagina.JsfBase;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private RegistroPrototipo prototipo;	
	private IBaseDto dtoDelete;

	public Transaccion(IBaseDto dtoDelete) {
		this.dtoDelete = dtoDelete;
	}	
	
	public Transaccion(RegistroPrototipo prototipo) {
		this.prototipo= prototipo;	
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar= true;
		Long idUsuario  = -1L;
		try {
			idUsuario= JsfBase.getIdUsuario();
			switch(accion){
				case AGREGAR:
					this.prototipo.getPrototipo().setIdUsuario(idUsuario);
					regresar= DaoFactory.getInstance().insert(sesion, this.prototipo.getPrototipo())>= 1L;
					for(SistemaConstructivo item: this.prototipo.getConstructivos()){
						item.setIdUsuario(idUsuario);
						item.setIdPrototipo(this.prototipo.getPrototipo().getIdPrototipo());
						actualizarConstructivo(sesion, item);
					} // for
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.prototipo.getPrototipo())>= 1L;
					for(SistemaConstructivo item: this.prototipo.getConstructivos()){
						item.setIdPrototipo(this.prototipo.getPrototipo().getIdPrototipo());
						actualizarConstructivo(sesion, item);
					} // for
					break;				
				case ELIMINAR:
					for(SistemaConstructivo item: this.prototipo.getConstructivos())
						DaoFactory.getInstance().delete(sesion, item);
					DaoFactory.getInstance().delete(sesion, this.prototipo.getPrototipo());
					break;
				case SUBIR:
					for(TcKeetPrototiposArchivosDto item: this.prototipo.getDocumentos()){
						item.setIdPlano(this.prototipo.getPrototipo().getIkPlano().getKey());
						DaoFactory.getInstance().insert(sesion, item);
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

	private void actualizarConstructivo(Session sesion, SistemaConstructivo item) throws Exception {
		try {
			switch(item.getAccion()){
				case INSERT:					
					DaoFactory.getInstance().insert(sesion, item);
					break;	
				case DELETE:
					DaoFactory.getInstance().delete(sesion, item);
					break;
			} // switch
		} // try
		catch (Exception e) {			
			throw new Exception(e);
		} // catch		
	} // actualizarConstructivo
}