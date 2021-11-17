package mx.org.kaana.keet.catalogos.puntoscontrol.reglas;

import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.catalogos.puntoscontrol.beans.PuntoControl;
import mx.org.kaana.keet.catalogos.puntoscontrol.beans.RegistroPunto;
import mx.org.kaana.keet.db.dto.TcKeetPuntosPaquetesDto;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {
	
	private RegistroPunto registroPunto;

	public Transaccion(RegistroPunto registroPunto) {
		this.registroPunto = registroPunto;
	}		

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar= true;	
		try {
			switch(accion){
				case AGREGAR:			
					this.registroPunto.getPuntoGrupo().setIdUsuario(JsfBase.getIdUsuario());
					DaoFactory.getInstance().insert(sesion, this.registroPunto.getPuntoGrupo());
					for(PuntoControl item: this.registroPunto.getPuntosControles())
				    actualizarPuntoControl(sesion, item);
					break;
				case MODIFICAR:
					DaoFactory.getInstance().update(sesion, this.registroPunto.getPuntoGrupo());
					for(PuntoControl item: this.registroPunto.getPuntosControles())
				    actualizarPuntoControl(sesion, item);
					break;				
				case ELIMINAR:
					for(PuntoControl item: this.registroPunto.getPuntosControles()){
						item.setAccion(ESql.DELETE);
				    actualizarPuntoControl(sesion, item);
					} // for
				  DaoFactory.getInstance().delete(sesion, this.registroPunto.getPuntoGrupo());
					break;
			} // switch			
		} // try
		catch (Exception e) {			
			throw new Exception((e!= null? e.getCause().toString(): ""));
		} // catch		
		return regresar;
	}	// ejecutar

	private void actualizarPuntoControl(Session sesion, PuntoControl item) throws Exception {
		Value orden                       = null;
		TcKeetPuntosPaquetesDto paqueteDto= null;
		Map<String, Object>params         = null;		
		try {
			params= new HashMap<>();
			switch(item.getAccion()){
				case INSERT:	
					item.setIdUsuario(JsfBase.getIdUsuario());
					orden= DaoFactory.getInstance().toField(sesion, "VistaPuntosControlDto", "getOrdenPuntoControl", this.registroPunto.getPuntoGrupo().toMap(), "orden");
					item.setOrden(orden==null || orden.getData()== null? 1L: orden.toLong());
					DaoFactory.getInstance().insert(sesion, item);
					paqueteDto= new TcKeetPuntosPaquetesDto(-1L,JsfBase.getIdUsuario(), item.getKey(), this.registroPunto.getPuntoGrupo().getKey());
					DaoFactory.getInstance().insert(sesion, paqueteDto);
					break;	
				case UPDATE:					
					DaoFactory.getInstance().update(sesion, item);
					break;	
				case DELETE:
					params.put("idPuntoGrupo", this.registroPunto.getPuntoGrupo().getKey());
					params.put("idPuntoControl", item.getKey());
					paqueteDto= (TcKeetPuntosPaquetesDto)DaoFactory.getInstance().toEntity(TcKeetPuntosPaquetesDto.class,"TcKeetPuntosPaquetesDto","find", params);
					DaoFactory.getInstance().delete(sesion, paqueteDto);
					//DaoFactory.getInstance().delete(sesion, item);
					break;
			} // switch
		} // try
		catch (Exception e) {			
			throw new Exception(e);
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // actualizarConstructivo	
}