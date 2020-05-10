package mx.org.kaana.keet.catalogos.prototipos.reglas;

import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.catalogos.prototipos.beans.Documento;
import mx.org.kaana.keet.catalogos.prototipos.beans.RegistroPrototipo;
import mx.org.kaana.keet.catalogos.prototipos.beans.SistemaConstructivo;
import mx.org.kaana.keet.db.dto.TcKeetPrototiposDiasDto;
import mx.org.kaana.keet.enums.EDiasSemana;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
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
					this.prototipo.getPrototipo().setIdEmpresa(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
					regresar= DaoFactory.getInstance().insert(sesion, this.prototipo.getPrototipo())>= 1L;
					registrarDias(sesion);
					for(SistemaConstructivo item: this.prototipo.getConstructivos()){
						item.setIdUsuario(idUsuario);
						item.setIdPrototipo(this.prototipo.getPrototipo().getIdPrototipo());
						actualizarConstructivo(sesion, item);
					} // for
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.prototipo.getPrototipo())>= 1L;
					registrarDias(sesion);
					for(SistemaConstructivo item: this.prototipo.getConstructivos()){
						item.setIdPrototipo(this.prototipo.getPrototipo().getIdPrototipo());
						actualizarConstructivo(sesion, item);
					} // for
					break;				
				case ELIMINAR:
					for(SistemaConstructivo item: this.prototipo.getConstructivos())
						DaoFactory.getInstance().delete(sesion, item);
					deleteDias(sesion);
					DaoFactory.getInstance().delete(sesion, this.prototipo.getPrototipo());
					break;
				case SUBIR:
					for(Documento item: this.prototipo.getDocumentos()){						
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

	private void actualizarConstructivo(Session sesion, SistemaConstructivo item) throws Exception {
		try {
			switch(item.getAccion()){
				case INSERT:					
					DaoFactory.getInstance().insert(sesion, item);
					break;	
				case UPDATE:					
					DaoFactory.getInstance().update(sesion, item);
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
	
	private void registrarDias(Session sesion) throws Exception{		
		TcKeetPrototiposDiasDto prototipoDia= null;
		try {			
			if(deleteDias(sesion)){
				for(String dia: this.prototipo.getDiasSeleccionados()){
					prototipoDia= new TcKeetPrototiposDiasDto();
					prototipoDia.setIdPrototipo(this.prototipo.getPrototipo().getIdPrototipo());
					prototipoDia.setIdUsuario(JsfBase.getIdUsuario());
					prototipoDia.setIdNombreDia(EDiasSemana.fromName(dia).getKey());
					DaoFactory.getInstance().insert(sesion, prototipoDia);
				} // for
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // registrarDias
	
	private boolean deleteDias(Session sesion) throws Exception{
		boolean regresar         = false;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idPrototipo", this.prototipo.getPrototipo().getIdPrototipo());
			regresar= DaoFactory.getInstance().execute(ESql.DELETE, sesion, "TcKeetPrototiposDiasDto", "byIdPrototipo", params)>= 0L;
		} // try
		catch (Exception e) {			
			throw e; 
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // deleteDias
}