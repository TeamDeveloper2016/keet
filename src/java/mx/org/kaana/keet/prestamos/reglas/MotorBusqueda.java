package mx.org.kaana.keet.prestamos.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.keet.prestamos.beans.Prestamo;
import mx.org.kaana.keet.prestamos.proveedor.beans.Anticipo;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

public class MotorBusqueda implements Serializable{

	private static final long serialVersionUID = 6871737745782566134L;
	private Long idPrestamo;

	public MotorBusqueda(Long idPrestamo) {
		this.idPrestamo = idPrestamo;
	}
	
	public Prestamo toPrestamo() throws Exception{
		Prestamo regresar       = null;
		Map<String, Object>params= null;
		try {
		  params= new HashMap<>();
			params.put("idPrestamo", this.idPrestamo);
			regresar= (Prestamo) DaoFactory.getInstance().toEntity(Prestamo.class, "TcKeetPrestamosDto", "byId", params);
			if(regresar!= null && regresar.isValid())
				regresar.setIkDeudor(new UISelectEntity(regresar.getIdDeudor()));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toPrestamo
	
	public Anticipo toAnticipo() throws Exception {
		Anticipo regresar        = null;
		Map<String, Object>params= null;
		try {
		  params= new HashMap<>();
			params.put("idAnticipo", this.idPrestamo);
			regresar= (Anticipo) DaoFactory.getInstance().toEntity(Prestamo.class, "TcKeetAnticiposDto", "byId", params);
			if(regresar!= null && regresar.isValid())
				regresar.setIkDeudor(new UISelectEntity(regresar.getIdMoroso()));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toPrestamo
	
}
