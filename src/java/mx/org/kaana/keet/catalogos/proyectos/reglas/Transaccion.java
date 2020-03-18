package mx.org.kaana.keet.catalogos.proyectos.reglas;

import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.catalogos.proyectos.beans.RegistroProyecto;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private RegistroProyecto proyecto;	

	public Transaccion(RegistroProyecto proyecto) {
		this.proyecto= proyecto;	
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar   = false;
		Siguiente siguiente= null;
		try {
			switch(accion){
				case AGREGAR:					
					siguiente= toSiguiente(sesion);
					this.proyecto.getProyecto().setConsecutivo(siguiente.getConsecutivo());
					this.proyecto.getProyecto().setOrden(siguiente.getOrden());
					this.proyecto.getProyecto().setEjercicio(Long.parseLong(String.valueOf(this.getCurrentYear())));
					regresar= DaoFactory.getInstance().insert(sesion, this.proyecto.getProyecto())>= 1L;
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.proyecto.getProyecto())>= 1L;
					break;				
				case ELIMINAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.proyecto.getProyecto())>= 1L;
					break;
				/*case SUBIR:
					for(TcKeetProyectosArchivosDto item: this.proyecto.getDocuemntos()){
						item.setIdPlano(this.proyecto.getIkPlano().getKey());
						DaoFactory.getInstance().insert(sesion, item);
					} // for
					break;*/
			} // switch
		} // try
		catch (Exception e) {			
			throw new Exception(e);
		} // catch		
		return regresar;
	}	// ejecutar
	
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());
			params.put("idCliente", this.proyecto.getProyecto().getIdCliente());
			Value next= DaoFactory.getInstance().toField(sesion, "TcKeetProyectosDto", "siguiente", params, "siguiente");
			if(next.getData()!= null)
			  regresar= new Siguiente(next.toLong());
			else
			  regresar= new Siguiente(Configuracion.getInstance().isEtapaDesarrollo()? 900001L: 1L);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toSiguiente
}