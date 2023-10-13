package mx.org.kaana.keet.entregas.reglas;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import static mx.org.kaana.kajool.enums.ESql.INSERT;
import static mx.org.kaana.kajool.enums.ESql.SELECT;
import static mx.org.kaana.kajool.enums.ESql.UPDATE;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.db.dto.TcKeetEntregasDetallesDto;
import mx.org.kaana.keet.entregas.beans.Material;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticAlmacenesArticulosDto;
import mx.org.kaana.mantic.db.dto.TcManticAlmacenesUbicacionesDto;
import mx.org.kaana.mantic.db.dto.TcManticInventariosDto;
import mx.org.kaana.mantic.db.dto.TcManticMovimientosDto;
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
        this.toAffectAlmacenes(sesion, this.orden.getEntrega().getConsecutivo(), item);
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
			params.put("idEmpresa", this.orden.getEntrega().getIdEmpresa());			
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
 
  protected void toAffectAlmacenes(Session sesion, String consecutivo, Material item) throws Exception {
		Map<String, Object> params= new HashMap<>();
		double stock= 0D;
		try {
			params.put("idAlmacen", this.orden.getEntrega().getIdAlmacen());
			params.put("idArticulo", item.getIdArticulo());
			TcManticAlmacenesArticulosDto ubicacion= (TcManticAlmacenesArticulosDto)DaoFactory.getInstance().findFirst(sesion, TcManticAlmacenesArticulosDto.class,  params, "ubicacion");
			if(ubicacion== null) {
			  TcManticAlmacenesUbicacionesDto general= (TcManticAlmacenesUbicacionesDto)DaoFactory.getInstance().findFirst(sesion, TcManticAlmacenesUbicacionesDto.class, params, "general");
				if(general== null) {
  				general= new TcManticAlmacenesUbicacionesDto("GENERAL", "", "GENERAL", "", "", JsfBase.getAutentifica().getPersona().getIdUsuario(), this.orden.getEntrega().getIdAlmacen(), -1L);
					DaoFactory.getInstance().insert(sesion, general);
				} // if	
			  Entity entity= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcManticArticulosDto", "inventario", params);
				TcManticAlmacenesArticulosDto articulo= new TcManticAlmacenesArticulosDto(entity.toDouble("minimo"), -1L, general.getIdUsuario(), general.getIdAlmacen(), entity.toDouble("maximo"), general.getIdAlmacenUbicacion(), item.getIdArticulo(), (item.getCantidad()- item.getCuantos()));
				DaoFactory.getInstance().insert(sesion, articulo);
		  } // if
			else { 
				stock= ubicacion.getStock();
				ubicacion.setStock(ubicacion.getStock()- (item.getCantidad()- item.getCuantos()));
				DaoFactory.getInstance().update(sesion, ubicacion);
			} // if

			// generar un registro en la bitacora de movimientos de los articulos 
			TcManticMovimientosDto entrada= new TcManticMovimientosDto(
			  consecutivo, // String consecutivo, 
				8L, // Long idTipoMovimiento, 
				JsfBase.getIdUsuario(), // Long idUsuario, 
				this.orden.getEntrega().getIdAlmacen(), // Long idAlmacen, 
				-1L, // Long idMovimiento, 
				(item.getCantidad()- item.getCuantos()), // Double cantidad, 
				item.getIdArticulo(), // Long idArticulo, 
				stock, // Double stock, 
				Numero.toRedondearSat(stock- (item.getCantidad()- item.getCuantos())), // Double calculo
				null // String observaciones
		  );
			DaoFactory.getInstance().insert(sesion, entrada);
			
			// afectar el inventario general de articulos dentro del almacen
			TcManticInventariosDto inventario= (TcManticInventariosDto)DaoFactory.getInstance().findFirst(sesion, TcManticInventariosDto.class, "inventario", params);
			if(inventario== null)
				DaoFactory.getInstance().insert(sesion, 
          new TcManticInventariosDto(
            JsfBase.getIdUsuario(), 
            this.orden.getEntrega().getIdAlmacen(), // idAlmacen
            (item.getCantidad()- item.getCuantos()), // entrada
            -1L, //idInventario
            item.getIdArticulo(), // idArticulo
            0D,  // inicial
            (item.getCantidad()- item.getCuantos()), // stock
            0D, // salida
            new Long(Calendar.getInstance().get(Calendar.YEAR)), // ejercicio
            1L)); // idAutomatico
			else {
				inventario.setEntradas(inventario.getEntradas()- item.getCantidad());
				inventario.setStock((inventario.getStock()< 0D? 0D: inventario.getStock())- (item.getCantidad()- item.getCuantos()));
				DaoFactory.getInstance().update(sesion, inventario);
			} // else
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	}	

}