package mx.org.kaana.keet.catalogos.puntoscontrol.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.keet.catalogos.puntoscontrol.beans.PuntoControl;
import mx.org.kaana.keet.catalogos.puntoscontrol.beans.PuntoGrupo;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

public class MotorBusqueda implements Serializable{
	
	private static final long serialVersionUID = -2951697223110542896L;
	private Long idPuntoGrupo;

	public MotorBusqueda(Long idPuntoGrupo) {
		this.idPuntoGrupo = idPuntoGrupo;
	}
	
	
	public PuntoGrupo toPuntoGrupo() throws Exception {
		PuntoGrupo regresar= null;
		try {
			regresar= (PuntoGrupo) DaoFactory.getInstance().findById(PuntoGrupo.class, this.idPuntoGrupo);
			if(regresar!= null && regresar.isValid())
				regresar.setIkDepartamento(new UISelectEntity(regresar.getIdDepartamento()));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toDomicilio
	
	public List<PuntoControl> toPuntosControles() throws Exception {
		List<PuntoControl> regresar= null;
		Map<String, Object>params              = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_punto_grupo=".concat(this.idPuntoGrupo.toString()));
			regresar= (List<PuntoControl>)DaoFactory.getInstance().toEntitySet(PuntoControl.class,"TcKeetPuntosControlesDto", "row", params);
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
