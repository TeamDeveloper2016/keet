package mx.org.kaana.keet.catalogos.proyectos.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.catalogos.proyectos.beans.Lote;
import mx.org.kaana.keet.catalogos.proyectos.beans.Proyecto;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

public class MotorBusqueda implements Serializable{
	
	private static final long serialVersionUID = -2951697223110542896L;
	private Long idProyecto;

	public MotorBusqueda(Long idProyecto) {
		this.idProyecto = idProyecto;
	}
	
	public Proyecto toProyecto() throws Exception{
		Proyecto regresar       = null;
		Map<String, Object>params= null;
		try {
		  params= new HashMap<>();
			params.put("idProyecto", this.idProyecto);
			regresar= (Proyecto) DaoFactory.getInstance().toEntity(Proyecto.class, "TcKeetProyectosDto", "byId", params);
			if(regresar!= null && regresar.isValid()){
				regresar.setIkCliente(new UISelectEntity(regresar.getIdCliente()));
				regresar.setIkDesarrollo(new UISelectEntity(regresar.getIdDesarrollo()));
				regresar.setIkTipoObra(new UISelectEntity(regresar.getIdTipoObra()));
			} // if			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toPrototipo	
	
	public List<Lote> toLotes() throws Exception{
		List<Lote> regresar= null;
		Map<String, Object>params         = null;
		try {
		  params= new HashMap<>();
			params.put("idProyecto", this.idProyecto);
			regresar= DaoFactory.getInstance().toEntitySet(Lote.class, "TcKeetProyectosLotesDto", "byProyecto", params);			
			for(Lote item: regresar)
				item.setAccion(ESql.UPDATE);
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