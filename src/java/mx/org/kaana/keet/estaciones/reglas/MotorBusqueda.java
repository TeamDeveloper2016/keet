package mx.org.kaana.keet.estaciones.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.keet.estaciones.beans.Estacion;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

public class MotorBusqueda implements Serializable{
	
	private static final long serialVersionUID = -2951697223110542896L;
	private Long idEstacion;

	public MotorBusqueda(Long idEstacion) {
		this.idEstacion = idEstacion;
	}
	
	public Estacion toEstacion() throws Exception{
		Estacion regresar       = null;
		Map<String, Object>params= null;
		try {
		  params= new HashMap<>();
			params.put("idEstacion", this.idEstacion);
			regresar= (Estacion) DaoFactory.getInstance().toEntity(Estacion.class, "TcKeetEstacionsDto", "byId", params);
			if(regresar!= null && regresar.isValid())
				regresar.setIkEmpaqueUnidadMedida(new UISelectEntity(regresar.getIdEmpaqueUnidadMedida()));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toEstacion
	

}
