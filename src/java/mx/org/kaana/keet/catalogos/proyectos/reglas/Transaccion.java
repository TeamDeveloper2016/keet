package mx.org.kaana.keet.catalogos.proyectos.reglas;

import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.catalogos.proyectos.beans.Proyecto;
import mx.org.kaana.keet.db.dto.TcKeetProyectosArchivosDto;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;

import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private Proyecto proyecto;	

	public Transaccion(Proyecto proyecto) {
		this.proyecto    = proyecto;	
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar          = false;
		Siguiente siguiente       = null;
		try {
			switch(accion){
				case AGREGAR:
					this.proyecto.setEjercicio(Long.parseLong(String.valueOf(this.getCurrentYear())));
					siguiente= toSiguiente(sesion);
					this.proyecto.setConsecutivo(siguiente.getConsecutivo());
					this.proyecto.setOrden(siguiente.getOrden());
					regresar= DaoFactory.getInstance().insert(sesion, this.proyecto)>= 1L;
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.proyecto)>= 1L;
					break;				
				case ELIMINAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.proyecto)>= 1L;
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
			params.put("idCliente", this.proyecto.getIdCliente());
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
	}

	
}