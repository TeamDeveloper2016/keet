package mx.org.kaana.keet.controles.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.keet.controles.beans.Control;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

public class MotorBusqueda implements Serializable{
	
	private static final long serialVersionUID = -2951697223110542896L;
	private Long idControl;

	public MotorBusqueda(Long idControl) {
		this.idControl = idControl;
	}
	
	public Control toControl() throws Exception {
		Control regresar       = null;
		Map<String, Object>params= null;
		try {
		  params= new HashMap<>();
			params.put("idControl", this.idControl);
			regresar= (Control) DaoFactory.getInstance().toEntity(Control.class, "TcKeetControlesDto", "byId", params);
			if(regresar!= null && regresar.isValid() && regresar.getIdEmpaqueUnidadMedida()!= null)
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
