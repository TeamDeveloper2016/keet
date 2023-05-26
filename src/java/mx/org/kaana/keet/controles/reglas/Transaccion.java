package mx.org.kaana.keet.controles.reglas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetControlesDto;
import mx.org.kaana.keet.controles.beans.RegistroControl;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {
	
	private RegistroControl registroControl;
	private String messageError= "";

	public Transaccion(RegistroControl dto) {
		this.registroControl = dto;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar          = false;
		List<TcKeetControlesDto> toArriba= null;
		List<TcKeetControlesDto> toAbajo= null;
		Controles controles       = null;
		try {
			controles= new Controles();
			switch(accion){
				case AGREGAR:			
					this.registroControl.getControl().setIdUsuario(JsfBase.getIdUsuario());
					this.registroControl.getControl().setClave(controles.toNextKey(this.registroControl.getControl().getClave(), this.registroControl.getControl().getNivel().intValue(), 1));
					regresar= DaoFactory.getInstance().insert(sesion, this.registroControl.getControl())>= 1L;
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.registroControl.getControl())>= 1L;
					break;				
				case ELIMINAR:
					if (controles.isChild(this.registroControl.getControl().getClave(), this.registroControl.getControl().getNivel().intValue())) {
						DaoFactory.getInstance().delete(sesion, this.registroControl.getControl());
						if (controles.getFather(this.registroControl.getControl().getClave())!=null) {
							this.registroControl= new RegistroControl(controles.getFather(this.registroControl.getControl().getClave()).getKey());
							int count=controles.toCountChildren(this.registroControl.getControl().getClave(), this.registroControl.getControl().getNivel().intValue());
							if (count==0) {
								this.registroControl.getControl().setUltimo(1L);
								DaoFactory.getInstance().update(sesion, this.registroControl.getControl());
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
					int index= -1;
					List<TcKeetControlesDto> list=controles.toChildren(this.registroControl.getControl().getClave(), this.registroControl.getControl().getNivel().intValue());
					if (EAccion.SUBIR.equals(accion)) {
						index= list.indexOf((TcKeetControlesDto)this.registroControl.getControl())-1;
					} // if
					else {
						index=list.indexOf((TcKeetControlesDto)this.registroControl.getControl())+1;
					} // else
					if (index>=0&&index<list.size()) {
						TcKeetControlesDto change=list.get(index);
						List<TcKeetControlesDto> allMenu=controles.toAllChildren(this.registroControl.getControl().getClave(), this.registroControl.getControl().getNivel().intValue()+1);
						List<TcKeetControlesDto> allChange=controles.toAllChildren(change.getClave(), change.getNivel().intValue()+1);
						updateChildren(sesion, controles.toKey(change.getClave(), change.getNivel().intValue()), allMenu);
						updateChildren(sesion, controles.toKey(this.registroControl.getControl().getClave(), this.registroControl.getControl().getNivel().intValue()), allChange);
						String newKey=this.registroControl.getControl().getClave();
						this.registroControl.getControl().setClave(change.getClave());
						change.setClave(newKey);
						DaoFactory.getInstance().update(sesion, this.registroControl.getControl());
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
      if(e!= null)
        if(e.getCause()!= null)
          this.messageError= this.messageError.concat("<br/>").concat(e.getCause().toString());
        else
          this.messageError= this.messageError.concat("<br/>").concat(e.getMessage());
			throw new Exception(this.messageError);
		} // catch	
		finally {
			Methods.clean(toArriba);
			Methods.clean(toAbajo);
		} // finally
		return regresar;
	}	// ejecutar
	
	private void updateChildren(Session session, String newKey, List<TcKeetControlesDto> allChildren) throws Exception {
		for (TcKeetControlesDto dto : allChildren) {
			String key=dto.getClave().substring(newKey.length());
			dto.setClave(newKey.concat(key));
			DaoFactory.getInstance().update(session, dto);
		} // for
	}
	
}