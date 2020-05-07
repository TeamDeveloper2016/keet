package mx.org.kaana.keet.catalogos.rubros.reglas;

import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.keet.catalogos.rubros.beans.RegistroRubro;
import mx.org.kaana.keet.catalogos.rubros.beans.RubroGrupo;
import mx.org.kaana.keet.db.dto.TcKeetRubrosGruposDto;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {
	
	private RegistroRubro registroRubro;

	public Transaccion(RegistroRubro registroRubro) {
		this.registroRubro = registroRubro;
	}
		

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar   = false;
		Siguiente siguiente= null;
		try {

			switch(accion){
				case AGREGAR:			
					this.registroRubro.getRubro().setIdUsuario(JsfBase.getIdUsuario());
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
		Value orden                       = null;
		TcKeetRubrosGruposDto paqueteDto= null;
		Map<String, Object>params         = null;
		try {
			params= new HashMap<>();
			switch(item.getAccion()){
				case INSERT:	
					item.setIdUsuario(JsfBase.getIdUsuario());
					orden= DaoFactory.getInstance().toField(sesion, "VistaRubroDto", "getOrdenRubroGrupo", this.registroRubro.getRubro().toMap(), "orden");
					DaoFactory.getInstance().insert(sesion, item);
					paqueteDto= new TcKeetRubrosGruposDto(-1L,JsfBase.getIdUsuario(), item.getKey(), this.registroRubro.getRubro().getKey());
					DaoFactory.getInstance().insert(sesion, paqueteDto);
					break;	
				case UPDATE:					
					DaoFactory.getInstance().update(sesion, item);
					break;	
				case DELETE:
					params.put("idRubro", this.registroRubro.getRubro().getKey());
					params.put("idRubroGrupo", item.getKey());
					paqueteDto= (TcKeetRubrosGruposDto)DaoFactory.getInstance().toEntity(TcKeetRubrosGruposDto.class,"TcKeetRubrosGruposDto","find", params);
					DaoFactory.getInstance().delete(sesion, paqueteDto);
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
	} // actualizarConstructivo
	
}