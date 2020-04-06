package mx.org.kaana.keet.estaciones.reglas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.estaciones.beans.RegistroEstacion;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {
	
	private RegistroEstacion registroEstacion;

	public Transaccion(RegistroEstacion dto) {
		this.registroEstacion = dto;
	}
	

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar          = false;
		Map<String, Object> params= null;
		String clave              = null;
		List<TcKeetEstacionesDto> toArriba= null;
		List<TcKeetEstacionesDto> toAbajo= null;
		Value value               = null;
		try {
			params=new HashMap<>();
			switch(accion){
				case AGREGAR:			
					this.registroEstacion.getEstacion().setIdUsuario(JsfBase.getIdUsuario());
					params.put("clave", this.registroEstacion.getEstacion().claveSinCeros(this.registroEstacion.getEstacion().getNivel()-1L));
					params.put("nivel", this.registroEstacion.getEstacion().getNivel());
					value= DaoFactory.getInstance().toField("TcKeetEstacionesDto", "maxClave", params, "clave");
					clave= value.getData()== null? this.registroEstacion.getEstacion().getClave(): value.getData$();
					this.registroEstacion.getEstacion().setClave(this.registroEstacion.getEstacion().calcularClave(clave, this.registroEstacion.getEstacion().getNivel(),1));
					regresar= DaoFactory.getInstance().insert(sesion, this.registroEstacion.getEstacion())>= 1L;
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.registroEstacion.getEstacion())>= 1L;
					break;				
				case ELIMINAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.registroEstacion.getEstacion())>= 1L;
					break;
				case SUBIR:
					params.put("clave", this.registroEstacion.getEstacion().claveSinCeros(this.registroEstacion.getEstacion().getNivel()-1L));
					params.put("nivel", this.registroEstacion.getEstacion().getNivel());
					value= DaoFactory.getInstance().toField("TcKeetEstacionesDto", "minClave", params, "clave");
					clave= value.getData()== null? this.registroEstacion.getEstacion().getClave(): value.getData$();
					if(Numero.getLong(this.registroEstacion.getEstacion().getClave())> Numero.getLong(clave)){
						toAbajo= this.registroEstacion.getEstacion().getHijos(this.registroEstacion.getEstacion().calcularClave(-1), this.registroEstacion.getEstacion().getNivel());
						toArriba= this.registroEstacion.getEstacion().getHijos();
						for(TcKeetEstacionesDto item: toAbajo ){
							item.setClave(this.registroEstacion.getEstacion().calcularClave(item.getClave(), this.registroEstacion.getEstacion().getNivel(), 1));
							DaoFactory.getInstance().update(sesion,item);
						} // for
						for(TcKeetEstacionesDto item: toArriba){
							item.setClave(this.registroEstacion.getEstacion().calcularClave(item.getClave(), this.registroEstacion.getEstacion().getNivel(), -1));
							DaoFactory.getInstance().update(sesion,item);
						} // for
					} // if
					else
						throw new Exception("No es posible subir la estación");
					break;				
				case BAJAR:
					params.put("clave", this.registroEstacion.getEstacion().claveSinCeros(this.registroEstacion.getEstacion().getNivel()- 1L));
					params.put("nivel", this.registroEstacion.getEstacion().getNivel());
					value= DaoFactory.getInstance().toField("TcKeetEstacionesDto", "maxClave", params, "clave");
					clave= value.getData()== null? this.registroEstacion.getEstacion().getClave(): value.getData$();
					if(Numero.getLong(this.registroEstacion.getEstacion().getClave())< Numero.getLong(clave)){
						toArriba= this.registroEstacion.getEstacion().getHijos(this.registroEstacion.getEstacion().calcularClave(1), this.registroEstacion.getEstacion().getNivel());
						toAbajo= this.registroEstacion.getEstacion().getHijos();
						for(TcKeetEstacionesDto item: toAbajo){
							item.setClave(this.registroEstacion.getEstacion().calcularClave(item.getClave(), this.registroEstacion.getEstacion().getNivel(), 1));
							DaoFactory.getInstance().update(sesion,item);
						} // for
						for(TcKeetEstacionesDto item: toArriba){
							item.setClave(this.registroEstacion.getEstacion().calcularClave(item.getClave(), this.registroEstacion.getEstacion().getNivel(), -1));
							DaoFactory.getInstance().update(sesion,item);
						} // for
					} // if
					else
						throw new Exception("No es posible bajar la estación");
					
					break;				
			} // switch
		} // try
		catch (Exception e) {			
			throw new Exception(e);
		} // catch	
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}	// ejecutar
	
}