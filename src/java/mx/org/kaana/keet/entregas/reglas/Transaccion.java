package mx.org.kaana.keet.entregas.reglas;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import static mx.org.kaana.kajool.enums.ESql.INSERT;
import static mx.org.kaana.kajool.enums.ESql.SELECT;
import static mx.org.kaana.kajool.enums.ESql.UPDATE;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.db.dto.TcKeetEntregasDetallesDto;
import mx.org.kaana.keet.entregas.beans.Material;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private AdminEntregas orden;	
	private String messageError;

	public Transaccion(AdminEntregas orden) {
		this.orden= orden;
	}

	public String getMessageError() {
		return messageError;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar          = Boolean.FALSE;
    Siguiente siguiente       = null;
		Map<String, Object> params= new HashMap<>();
		try {
      this.orden.getEntrega().setIdUsuario(JsfBase.getIdUsuario());
			this.messageError= "Ocurrio un error al ".concat(accion.name().toLowerCase()).concat(" la entrega de materiales");
			switch(accion) {
				case AGREGAR:
          siguiente= this.toSiguiente(sesion);
          this.orden.getEntrega().setConsecutivo(siguiente.getConsecutivo());
          this.orden.getEntrega().setEjercicio(Long.valueOf(this.getCurrentYear()));
          this.orden.getEntrega().setOrden(siguiente.getOrden());
          this.orden.getEntrega().setIdPaquete(this.orden.getMateriales().get(0).getIdPaquete());
          this.toCheckCompleto();
          DaoFactory.getInstance().insert(sesion, this.orden.getEntrega());
          regresar= this.toFillMateriales(sesion);
					break;				
				case MODIFICAR:
          this.toCheckCompleto();
          DaoFactory.getInstance().update(sesion, this.orden.getEntrega());
          regresar= this.toFillMateriales(sesion);
					break;				
				case ELIMINAR:
          params.put("idEntrega", this.orden.getEntrega().getIdEntrega());
          DaoFactory.getInstance().deleteAll(sesion, TcKeetEntregasDetallesDto.class, params);
					regresar= DaoFactory.getInstance().delete(sesion, this.orden.getEntrega())>= 1L;
  				break;
			} // switch
			if(!regresar)
        throw new Exception("");
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
      Methods.clean(params);
    } // finally
		return regresar;
	}	// ejecutar
	
  private void toCheckCompleto() throws Exception {
    Long idCompleto= 1L;
    try {   
      for (Material item: this.orden.getMateriales()) {
        if(!Objects.equals(item.getCantidad(), item.getTotal()))
          idCompleto= 2L;
      } // for
      this.orden.getEntrega().setIdCompleto(idCompleto);
    } // try
    catch (Exception e) {
			throw e;
    } // catch	
  }
 
  private Boolean toFillMateriales(Session sesion) throws Exception {
    Boolean regresar= Boolean.FALSE;
    try {   
      for (Material item: this.orden.getMateriales()) {
        switch(item.getSql()) {
          case INSERT:
            item.setIdUsuario(JsfBase.getIdUsuario());
            item.setIdEntrega(this.orden.getEntrega().getIdEntrega());
            item.setIdCompleto(Objects.equals(item.getCantidad(), item.getTotal())? 1L: 2L);
            item.setIdRecibe(this.orden.getEntrega().getIdRecibe());
            DaoFactory.getInstance().insert(sesion, item);
            break;
          case UPDATE:
            item.setIdUsuario(JsfBase.getIdUsuario());
            item.setIdCompleto(Objects.equals(item.getCantidad(), item.getTotal())? 1L: 2L);
            item.setIdRecibe(this.orden.getEntrega().getIdRecibe());
            item.setRegistro(LocalDateTime.now());
            DaoFactory.getInstance().update(sesion, item);
            break;
          case DELETE:
            DaoFactory.getInstance().delete(sesion, item);
            break;
          case SELECT:
            break;
        } // switch
      } // for
      regresar= Boolean.TRUE;
    } // try
    catch (Exception e) {
			throw e;
    } // catch	
    return regresar;
  }
 
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("ejercicio", this.getCurrentYear());			
			Value next= DaoFactory.getInstance().toField(sesion, "TcKeetEntregasDto", "siguiente", params, "siguiente");
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