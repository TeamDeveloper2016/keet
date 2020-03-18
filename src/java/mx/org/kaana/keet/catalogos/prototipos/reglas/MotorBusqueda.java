package mx.org.kaana.keet.catalogos.prototipos.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.keet.catalogos.prototipos.beans.Prototipo;
import mx.org.kaana.keet.catalogos.prototipos.beans.SistemaConstructivo;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

public class MotorBusqueda implements Serializable{
	
	private static final long serialVersionUID = -2951697223110542896L;
	private Long idPrototipo;

	public MotorBusqueda(Long idPrototipo) {
		this.idPrototipo = idPrototipo;
	}
	
	public Prototipo toPrototipo() throws Exception{
		Prototipo regresar       = null;
		Map<String, Object>params= null;
		try {
		  params= new HashMap<>();
			params.put("idPrototipo", this.idPrototipo);
			regresar= (Prototipo) DaoFactory.getInstance().toEntity(Prototipo.class, "TcKeetPrototiposDto", "byId", params);
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
	} // toPrototipo
	
	public List<SistemaConstructivo> toConstructivos() throws Exception{
		List<SistemaConstructivo> regresar= null;
		Map<String, Object>params         = null;
		try {
		  params= new HashMap<>();
			params.put("idPrototipo", this.idPrototipo);
			regresar= DaoFactory.getInstance().toEntitySet(SistemaConstructivo.class, "VistaPrototiposDto", "constructivosById", params);			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toConstructivos
}
