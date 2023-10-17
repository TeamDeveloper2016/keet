package mx.org.kaana.keet.paquetes.reglas;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import static mx.org.kaana.kajool.enums.ESql.INSERT;
import static mx.org.kaana.kajool.enums.ESql.SELECT;
import static mx.org.kaana.kajool.enums.ESql.UPDATE;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetPaquetesDetallesDto;
import mx.org.kaana.keet.db.dto.TcKeetPaquetesDto;
import mx.org.kaana.keet.paquetes.beans.Material;
import mx.org.kaana.keet.paquetes.beans.Paquete;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private Paquete paquete;	
  private Long idPaquete;
  private Entity[] modelos;
	private String messageError;

	public Transaccion(Paquete paquete) {
		this.paquete= paquete;
	}

	public Transaccion(Long idPaquete, Entity[] modelos) {
		this.idPaquete= idPaquete;
    this.modelos  = modelos;
	}

	public String getMessageError() {
		return messageError;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar          = Boolean.FALSE;
		Map<String, Object> params= new HashMap<>();
		try {
			this.messageError= "Ocurrio un error al ".concat(accion.name().toLowerCase()).concat(" el paquete");
			switch(accion) {
				case AGREGAR:
          this.paquete.setIdUsuario(JsfBase.getIdUsuario());
          DaoFactory.getInstance().insert(sesion, this.paquete);
          regresar= this.toFillMateriales(sesion);
					break;				
				case MODIFICAR:
          this.paquete.setIdUsuario(JsfBase.getIdUsuario());
          DaoFactory.getInstance().update(sesion, this.paquete);
          regresar= this.toFillMateriales(sesion);
					break;				
				case ELIMINAR:
          params.put("idPaquete", this.paquete.getIdPaquete());
          DaoFactory.getInstance().deleteAll(sesion, TcKeetPaquetesDetallesDto.class, params);
					regresar= DaoFactory.getInstance().delete(sesion, this.paquete)>= 1L;
  				break;
        case COMPLEMENTAR:
					regresar= this.toClonPaquete(sesion);
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
	
  private Boolean toFillMateriales(Session sesion) throws Exception {
    Boolean regresar= Boolean.FALSE;
    try {   
      for (Material item: this.paquete.getMateriales()) {
        switch(item.getSql()) {
          case INSERT:
            item.setIdUsuario(JsfBase.getIdUsuario());
            item.setIdPaquete(this.paquete.getIdPaquete());
            DaoFactory.getInstance().insert(sesion, item);
            break;
          case UPDATE:
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

  private boolean toClonPaquete(Session sesion) throws Exception {
    boolean regresar          = Boolean.FALSE;
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idPaquete", this.idPaquete);
      TcKeetPaquetesDto source= (TcKeetPaquetesDto)DaoFactory.getInstance().findById(TcKeetPaquetesDto.class, this.idPaquete);
      List<TcKeetPaquetesDetallesDto> detalles= (List<TcKeetPaquetesDetallesDto>)DaoFactory.getInstance().findViewCriteria(TcKeetPaquetesDetallesDto.class, params, "igual");
      if(!Objects.equals(source, null)) {
        for (Entity item: this.modelos) {
          TcKeetPaquetesDto clon= new TcKeetPaquetesDto(
            source.getIdProceso(), //  Long idProceso, 
            -1L, // Long idPaquete, 
            item.toLong("idDesarrollo"), // Long idDesarrollo, 
            JsfBase.getIdUsuario(), // Long idUsuario, 
            source.getIdSubProceso(), // Long idSubProceso, 
            source.getObservaciones(), // String observaciones, 
            item.toLong("idPrototipo") // Long idPrototipo
          );
          DaoFactory.getInstance().insert(sesion, clon);
          for (TcKeetPaquetesDetallesDto detalle: detalles) {
            TcKeetPaquetesDetallesDto target= new TcKeetPaquetesDetallesDto(
              clon.getIdPaquete(), // Long idPaquete, 
              JsfBase.getIdUsuario(), // Long idUsuario, 
              detalle.getObservaciones(), // String observaciones, 
              detalle.getCantidad(), // Double cantidad, 
              detalle.getIdArticulo(), // Long idArticulo, 
              -1L, // Long idPaqueteDetalle, 
              detalle.getCodigo(), // String codigo, 
              detalle.getNombre() // String nombre      
            );
            DaoFactory.getInstance().insert(sesion, target);
          } // for
        } // for
      } // if
      regresar= Boolean.TRUE;
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