package mx.org.kaana.keet.catalogos.rubros.reglas;

import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.keet.catalogos.rubros.beans.RegistroRubro;
import mx.org.kaana.keet.catalogos.rubros.beans.RubroGrupo;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {
	
	private RegistroRubro registroRubro;

	public Transaccion(RegistroRubro registroRubro) {
		this.registroRubro = registroRubro;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar   = false;
		try {

			switch(accion){
				case AGREGAR:			
					this.registroRubro.getRubro().setIdUsuario(JsfBase.getIdUsuario());
					this.registroRubro.getRubro().setOrden(1L);
					DaoFactory.getInstance().insert(sesion, this.registroRubro.getRubro());
					for(RubroGrupo item: this.registroRubro.getRubrosGrupos())
				    actualizarRubroGrupo(sesion, item);
					break;
				case MODIFICAR:
					DaoFactory.getInstance().update(sesion, this.registroRubro.getRubro());
					for(RubroGrupo item: this.registroRubro.getRubrosGrupos())
				    actualizarRubroGrupo(sesion, item);
					break;				
				case ELIMINAR:
					for(RubroGrupo item: this.registroRubro.getRubrosGrupos()){
						item.setAccion(ESql.DELETE);
				    actualizarRubroGrupo(sesion, item);
					} // for
				  DaoFactory.getInstance().delete(sesion, this.registroRubro.getRubro());
					break;
			} // switch
			regresar= true;
		} // try
		catch (Exception e) {			
			throw new Exception(e);
		} // catch		
		return regresar;
	}	// ejecutar


	private void actualizarRubroGrupo(Session sesion, RubroGrupo item) throws Exception {
		Map<String, Object>params         = null;
		try {
			params= new HashMap<>();
			switch(item.getAccion()){
				case INSERT:	
					item.setIdUsuario(JsfBase.getIdUsuario());
					item.setIdRubro(this.registroRubro.getRubro().getKey());
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
		finally{
			Methods.clean(params);
		} // finally
	} // actualizarRubroGrupo
	
}