package mx.org.kaana.keet.catalogos.desarrollos.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.keet.catalogos.desarrollos.beans.Desarrollo;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

public class MotorBusqueda implements Serializable{
	
	private static final long serialVersionUID = -2951697223110542896L;
	private Long idDesarrollo;

	public MotorBusqueda(Long idDesarrollo) {
		this.idDesarrollo = idDesarrollo;
	}
	
	public Desarrollo toDesarrollo() throws Exception{
		Desarrollo regresar       = null;
		Map<String, Object>params= null;
		try {
		  params= new HashMap<>();
			params.put("idDesarrollo", this.idDesarrollo);
			regresar= (Desarrollo) DaoFactory.getInstance().toEntity(Desarrollo.class, "TcKeetDesarrollosDto", "byId", params);
			if(regresar!= null && regresar.isValid())
				regresar.setIkCliente(new UISelectEntity(regresar.getIdCliente()));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toDesarrollo
	

}
