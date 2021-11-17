package mx.org.kaana.keet.estaciones.reglas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.estaciones.beans.RegistroEstacion;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {
	
	private RegistroEstacion estacion;
  private UISelectEntity partida;

	public Transaccion(RegistroEstacion dto) {
		this.estacion = dto;
	}
  
	public Transaccion(RegistroEstacion dto, UISelectEntity partida) {
		this.estacion= dto;
    this.partida = partida;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar                  = false;
		Map<String, Object> params        = null;
		List<TcKeetEstacionesDto> toArriba= null;
		List<TcKeetEstacionesDto> toAbajo = null;
		Estaciones estaciones             = null;
		try {
			params    = new HashMap<>();
			estaciones= new Estaciones(sesion);
			switch(accion) {
				case AGREGAR:			
          this.estacion.getEstacion().setUltimo(1L);
					this.estacion.getEstacion().setIdUsuario(JsfBase.getIdUsuario());
          // AQUI FALTA IR A LOS PAPAS Y ACUMULAR LA DIFERENCIA SI ES QUE SE MODIFICO EL COSTO
          this.toUpdateFathers(sesion, accion);
          if(Objects.equals(4L, this.estacion.getEstacion().getNivel())) {
            this.estacion.getEstacion().setNivel(this.estacion.getEstacion().getNivel()+ 1L);
            TcKeetEstacionesDto padre= (TcKeetEstacionesDto)this.estacion.getEstacion().clone();
  					padre.setClave(estaciones.toNextKey(padre.getClave(), padre.getNivel().intValue(), 1));
            padre.setCodigo(this.partida.toString("codigo"));
            padre.setNombre(this.partida.toString("nombre"));
            padre.setDescripcion(this.partida.toString("descripcion"));
            padre.setUltimo(2L);
  					DaoFactory.getInstance().insert(sesion, padre);
            this.estacion.getEstacion().setClave(padre.getClave());
            this.estacion.getEstacion().setNivel(padre.getNivel());
          } // if
          this.estacion.getEstacion().setNivel(this.estacion.getEstacion().getNivel()+ 1L);
					this.estacion.getEstacion().setClave(estaciones.toNextKey(this.estacion.getEstacion().getClave(), this.estacion.getEstacion().getNivel().intValue(), 1));
		    	regresar= DaoFactory.getInstance().insert(sesion, this.estacion.getEstacion())>= 1L;
					break;
				case MODIFICAR:
          // AQUI FALTA IR A LOS PAPAS Y ACUMULAR LA DIFERENCIA SI ES QUE SE MODIFICO EL COSTO
          this.toUpdateFathers(sesion, accion);
					regresar= DaoFactory.getInstance().update(sesion, this.estacion.getEstacion())>= 1L;
					break;				
				case ELIMINAR:
					if (estaciones.isChild(this.estacion.getEstacion().getClave(), this.estacion.getEstacion().getNivel().intValue())) {
            // AQUI FALTA IR A LOS PAPAS Y ACUMULAR LA DIFERENCIA SI ES QUE SE MODIFICO EL COSTO
            this.toUpdateFathers(sesion, accion);
						DaoFactory.getInstance().delete(sesion, this.estacion.getEstacion());
            this.estacion= new RegistroEstacion(estaciones.getFather(this.estacion.getEstacion().getClave()).getKey());
            int count= estaciones.toCountChildren(this.estacion.getEstacion().getClave(), this.estacion.getEstacion().getNivel().intValue()+ 1);
            if (count==0) 
              DaoFactory.getInstance().delete(sesion, this.estacion.getEstacion());
					}	// if
					else {
						JsfBase.addMsgProperties("error_eliminar_estaciones_dependientes");
						throw new RuntimeException();
					} // else
					break;
				case SUBIR:
				case BAJAR:
					int index= -1;
					List<TcKeetEstacionesDto> list=estaciones.toChildren(this.estacion.getEstacion().getClave(), this.estacion.getEstacion().getNivel().intValue());
					if (EAccion.SUBIR.equals(accion)) 
						index= list.indexOf((TcKeetEstacionesDto)this.estacion.getEstacion())-1;
					else 
						index=list.indexOf((TcKeetEstacionesDto)this.estacion.getEstacion())+1;
					if (index>=0&&index<list.size()) {
						TcKeetEstacionesDto change=list.get(index);
						List<TcKeetEstacionesDto> allMenu=estaciones.toAllChildren(this.estacion.getEstacion().getClave(), this.estacion.getEstacion().getNivel().intValue()+1);
						List<TcKeetEstacionesDto> allChange=estaciones.toAllChildren(change.getClave(), change.getNivel().intValue()+1);
						updateChildren(sesion, estaciones.toKey(change.getClave(), change.getNivel().intValue()), allMenu);
						updateChildren(sesion, estaciones.toKey(this.estacion.getEstacion().getClave(), this.estacion.getEstacion().getNivel().intValue()), allChange);
						String newKey=this.estacion.getEstacion().getClave();
						this.estacion.getEstacion().setClave(change.getClave());
						change.setClave(newKey);
						DaoFactory.getInstance().update(sesion, this.estacion.getEstacion());
						DaoFactory.getInstance().update(sesion, change);
					} // if
					else {
						throw new RuntimeException("No se puede mover la estación, verifique por favor !");
					} // else
					break;
			} // switch
      // throw new RuntimeException("Hola!");
		} // try
		catch (Exception e) {			
			throw new Exception((e!= null? e.getCause().toString(): ""));
		} // catch	
		finally {
			Methods.clean(params);
			Methods.clean(toArriba);
			Methods.clean(toAbajo);
		} // finally
		return regresar;
	}	// ejecutar
	
	private void updateChildren(Session sesion, String newKey, List<TcKeetEstacionesDto> allChildren) throws Exception {
		for (TcKeetEstacionesDto dto : allChildren) {
			String key=dto.getClave().substring(newKey.length());
			dto.setClave(newKey.concat(key));
			DaoFactory.getInstance().update(sesion, dto);
		} // for
	}

  private void toUpdateFathers(Session sesion, EAccion accion) throws Exception {
    Estaciones estaciones= new Estaciones(sesion);
    try {      
      Double diferencia= 0D;
 			switch(accion) {
				case AGREGAR:	
          diferencia= this.estacion.getEstacion().getCosto();
          break;
				case MODIFICAR:			
          TcKeetEstacionesDto dto= (TcKeetEstacionesDto)DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, this.estacion.getEstacion().getKey());
          diferencia= Numero.toRedondearSat(this.estacion.getEstacion().getCosto()- dto.getCosto());
          break;
				case ELIMINAR:			
          diferencia= this.estacion.getEstacion().getCosto()* -1L;
          break;
      } // switch
      List<TcKeetEstacionesDto> items= estaciones.toFather(this.estacion.getEstacion().getClave());
      if(items!= null && !items.isEmpty()) {
        if(Objects.equals(6L, this.estacion.getEstacion().getNivel()) && !EAccion.AGREGAR.equals(accion))
          items.remove(items.size()- 1);
        for (TcKeetEstacionesDto item: items) {
          item.setCosto(Numero.toRedondearSat(item.getCosto()+ diferencia));
          DaoFactory.getInstance().update(sesion, item);
        } // for
      } // if  
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      estaciones= null;
    } // finally
  }
	
}