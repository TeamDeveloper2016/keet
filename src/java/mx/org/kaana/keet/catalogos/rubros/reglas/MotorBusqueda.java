package mx.org.kaana.keet.catalogos.rubros.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.keet.catalogos.rubros.beans.Rubro;
import mx.org.kaana.keet.catalogos.rubros.beans.RubroGrupo;
import mx.org.kaana.keet.db.dto.TcKeetPuntosGruposDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

public class MotorBusqueda implements Serializable{
	
	private static final long serialVersionUID = -2951697223110542896L;
	private Long idRubro;

	public MotorBusqueda(Long idRubro) {
		this.idRubro = idRubro;
	}
	
	
	public Rubro toRubro() throws Exception {
		Rubro regresar= null;
	  Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_rubro=".concat(this.idRubro.toString()));
			regresar= (Rubro) DaoFactory.getInstance().toEntity(Rubro.class, "TcKeetRubrosDto", params);
			if(regresar!= null && regresar.isValid()){
				regresar.setIkEmpaqueUnidadMedida(new UISelectEntity(regresar.getIdEmpaqueUnidadMedida()));
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toDomicilio
	
	public List<RubroGrupo> toRubrosGrupos() throws Exception {
		List<RubroGrupo> regresar= null;
		Map<String, Object>params= null;
		TcKeetPuntosGruposDto puntosGruposDto= null;
		try {
			params= new HashMap<>();
			params.put("idRubro", this.idRubro);
			regresar= (List<RubroGrupo>)DaoFactory.getInstance().toEntitySet(RubroGrupo.class,"TcKeetRubrosGruposDto", "byRubro", params);
			for(RubroGrupo item: regresar){
				item.setIkPuntoGrupo(new UISelectEntity(item.getIdPuntoGrupo()));
				puntosGruposDto= (TcKeetPuntosGruposDto)DaoFactory.getInstance().findById(TcKeetPuntosGruposDto.class, item.getIdPuntoGrupo());
				item.setDepartamento(new UISelectEntity(puntosGruposDto.getIdDepartamento()));
				item.cargaPuntosGrupos();
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toDomicilio
	

}
