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
		Estaciones estaciones     = null;
		try {
			params=new HashMap<>();
			estaciones= new Estaciones();
			switch(accion){
				case AGREGAR:			
					this.registroEstacion.getEstacion().setIdUsuario(JsfBase.getIdUsuario());
					this.registroEstacion.getEstacion().setClave(estaciones.toNextKey(this.registroEstacion.getEstacion().getClave(), this.registroEstacion.getEstacion().getNivel().intValue(), 1));
					regresar= DaoFactory.getInstance().insert(sesion, this.registroEstacion.getEstacion())>= 1L;
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.registroEstacion.getEstacion())>= 1L;
					break;				
				case ELIMINAR:
					if (estaciones.isChild(this.registroEstacion.getEstacion().getClave(), this.registroEstacion.getEstacion().getNivel().intValue())) {
						DaoFactory.getInstance().delete(sesion, this.registroEstacion.getEstacion());
						if (estaciones.getFather(this.registroEstacion.getEstacion().getClave())!=null) {
							this.registroEstacion= new RegistroEstacion(estaciones.getFather(this.registroEstacion.getEstacion().getClave()).getKey());
							int count=estaciones.toCountChildren(this.registroEstacion.getEstacion().getClave(), this.registroEstacion.getEstacion().getNivel().intValue());
							if (count==0) {
								this.registroEstacion.getEstacion().setUltimo(1L);
								DaoFactory.getInstance().update(sesion, this.registroEstacion.getEstacion());
							} // if	
						} // if	
					}	// if
					else {
						JsfBase.addMsgProperties("error_eliminar_estaciones_dependientes");
						throw new RuntimeException();
					} // else
					break;
				case SUBIR:
				case BAJAR:
					int index=-1;
					List<TcKeetEstacionesDto> list=estaciones.toChildren(this.registroEstacion.getEstacion().getClave(), this.registroEstacion.getEstacion().getNivel().intValue());
					if (EAccion.SUBIR.equals(accion)) {
						index= list.indexOf((TcKeetEstacionesDto)this.registroEstacion.getEstacion())-1;
					} // if
					else {
						index=list.indexOf((TcKeetEstacionesDto)this.registroEstacion.getEstacion())+1;
					} // else
					if (index>=0&&index<list.size()) {
						TcKeetEstacionesDto change=list.get(index);
						List<TcKeetEstacionesDto> allMenu=estaciones.toAllChildren(this.registroEstacion.getEstacion().getClave(), this.registroEstacion.getEstacion().getNivel().intValue()+1);
						List<TcKeetEstacionesDto> allChange=estaciones.toAllChildren(change.getClave(), change.getNivel().intValue()+1);
						updateChildren(sesion, estaciones.toKey(change.getClave(), change.getNivel().intValue()), allMenu);
						updateChildren(sesion, estaciones.toKey(this.registroEstacion.getEstacion().getClave(), this.registroEstacion.getEstacion().getNivel().intValue()), allChange);
						String newKey=this.registroEstacion.getEstacion().getClave();
						this.registroEstacion.getEstacion().setClave(change.getClave());
						change.setClave(newKey);
						DaoFactory.getInstance().update(sesion, this.registroEstacion.getEstacion());
						DaoFactory.getInstance().update(sesion, change);
					} // if
					else {
						throw new RuntimeException("No se puede mover la estación");
					} // else
					break;
			} // switch
			regresar= true;
		} // try
		catch (Exception e) {			
			throw new Exception(e);
		} // catch	
		finally {
			Methods.clean(params);
			Methods.clean(toArriba);
			Methods.clean(toAbajo);
		} // finally
		return regresar;
	}	// ejecutar
	
	private void updateChildren(Session session, String newKey, List<TcKeetEstacionesDto> allChildren) throws Exception {
		for (TcKeetEstacionesDto dto : allChildren) {
			String key=dto.getClave().substring(newKey.length());
			dto.setClave(newKey.concat(key));
			DaoFactory.getInstance().update(session, dto);
		} // for
	}
	
}