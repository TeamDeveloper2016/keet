package mx.org.kaana.keet.catalogos.prototipos.reglas;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.catalogos.prototipos.beans.Documento;
import mx.org.kaana.keet.catalogos.prototipos.beans.RegistroPrototipo;
import mx.org.kaana.keet.catalogos.prototipos.beans.SistemaConstructivo;
import mx.org.kaana.keet.db.dto.TcKeetContratosDestajosContratistasDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosDestajosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosPuntosContratistasDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosPuntosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosRechazosContratistasDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosRechazosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetErroresEstacionesDto;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.db.dto.TcKeetPrototiposHabilesDto;
import mx.org.kaana.keet.enums.EDiasSemana;
import mx.org.kaana.keet.estaciones.beans.Partida;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private RegistroPrototipo prototipo;	
  private List<Partida> partidas;
	private IBaseDto dtoDelete;
	private Long idContratoLote;
  private String messageError;

	public Transaccion(IBaseDto dtoDelete) {
		this.dtoDelete = dtoDelete;
	}	
	
	public Transaccion(RegistroPrototipo prototipo) {
		this.prototipo= prototipo;	
	}

	public Transaccion(Long idContratoLote, List<Partida> partidas) {
    this.idContratoLote= idContratoLote;
		this.partidas= partidas;	
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar= true;
		Long idUsuario  = -1L;
		try {
      this.messageError= "";
			idUsuario= JsfBase.getIdUsuario();
			switch(accion){
				case AGREGAR:
					this.prototipo.getPrototipo().setIdUsuario(idUsuario);
					this.prototipo.getPrototipo().setIdEmpresa(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
					regresar= DaoFactory.getInstance().insert(sesion, this.prototipo.getPrototipo())>= 1L;
          this.registrarDias(sesion);
					for(SistemaConstructivo item: this.prototipo.getConstructivos()){
						item.setIdUsuario(idUsuario);
						item.setIdPrototipo(this.prototipo.getPrototipo().getIdPrototipo());
						this.actualizarConstructivo(sesion, item);
					} // for
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.prototipo.getPrototipo())>= 1L;
          this.registrarDias(sesion);
					for(SistemaConstructivo item: this.prototipo.getConstructivos()){
						item.setIdPrototipo(this.prototipo.getPrototipo().getIdPrototipo());
						this.actualizarConstructivo(sesion, item);
					} // for
					break;				
				case ELIMINAR:
					for(SistemaConstructivo item: this.prototipo.getConstructivos())
						DaoFactory.getInstance().delete(sesion, item);
          this.deleteDias(sesion);
					DaoFactory.getInstance().delete(sesion, this.prototipo.getPrototipo());
					break;
				case SUBIR:
					for(Documento item: this.prototipo.getDocumentos()){						
						if(DaoFactory.getInstance().insert(sesion, item)>=1L)
							this.toSaveFile(item.getIdArchivo());
					} // for
					break;
				case DEPURAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.dtoDelete)>= 1L;
					break;
				case TRANSFORMACION:
					regresar= this.toApplyDates(sesion);
					break;
				case DESTRANSFORMACION:
					regresar= this.toApplyCost(sesion);
					break;
				case MOVIMIENTOS:
					regresar= this.toApplyDays(sesion);
					break;
				case GENERAR:
					regresar= this.toChangeData(sesion);
					break;
				case RESTAURAR:
					regresar= this.toDeleteData(sesion);
					break;
			} // switch						
		} // try
		catch (Exception e) {			
      if(e!= null)
        if(e.getCause()!= null)
          this.messageError= this.messageError.concat("<br/>").concat(e.getCause().toString());
        else
          this.messageError= this.messageError.concat("<br/>").concat(e.getMessage());
			throw new Exception(this.messageError);
		} // catch		
		return regresar;
	}	// ejecutar

	private void actualizarConstructivo(Session sesion, SistemaConstructivo item) throws Exception {
		try {
			switch(item.getAccion()) {
				case INSERT:					
					DaoFactory.getInstance().insert(sesion, item);
					break;	
				case UPDATE:					
					DaoFactory.getInstance().update(sesion, item);
					break;	
				case DELETE:
					DaoFactory.getInstance().delete(sesion, item);
					break;
			} // switch
		} // try
		catch (Exception e) {			
			throw new Exception(e);
		} // catch		
	} // actualizarConstructivo

  private void registrarDias(Session sesion) throws Exception {
		TcKeetPrototiposHabilesDto habil= null;
		try {			
			if(this.deleteDias(sesion)) {
				for(String dia: this.prototipo.getDiasSeleccionados()) {
					habil= new TcKeetPrototiposHabilesDto();
					habil.setIdPrototipo(this.prototipo.getPrototipo().getIdPrototipo());
					habil.setIdUsuario(JsfBase.getIdUsuario());
					habil.setIdNombreDia(EDiasSemana.fromName(dia).getKey());
					DaoFactory.getInstance().insert(sesion, habil);
				} // for
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // registrarDias
	
	private boolean deleteDias(Session sesion) throws Exception {
		boolean regresar         = false;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idPrototipo", this.prototipo.getPrototipo().getIdPrototipo());
			regresar= DaoFactory.getInstance().execute(ESql.DELETE, sesion, "TcKeetPrototiposHabilesDto", "igual", params)>= 0L;
		} // try
		catch (Exception e) {			
			throw e; 
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // deleteDias
   
  private Boolean toApplyDates(Session sesion) throws Exception {
    Boolean regresar          = Boolean.FALSE;
    Map<String, Object> params= new HashMap<>();
    Estaciones estaciones     = new Estaciones(sesion);
    String clave              = null;
    try {      
      for (Partida item: this.partidas) {
        clave= estaciones.toOnlyKey(item.getClave(), 6);
        params.put("clave", clave);     
        params.put("inicio", Fecha.formatear(Fecha.FECHA_ESTANDAR, item.getInicio()));
        params.put("termino", Fecha.formatear(Fecha.FECHA_ESTANDAR, item.getTermino()));
        DaoFactory.getInstance().updateAll(sesion, TcKeetEstacionesDto.class, params, "fechas");
        switch(item.getAccion()) {
          case INSERT:
            DaoFactory.getInstance().insert(sesion, item);
            break;
          case SELECT:
            if(item.isDiasDiferente())
              DaoFactory.getInstance().update(sesion, item);
            break;
        } // switch
      } // for
      clave= estaciones.toOnlyKey(clave, 5);
      params.put("clave", estaciones.toCode(clave));     
      params.put("inicio", Fecha.formatear(Fecha.FECHA_ESTANDAR, this.partidas.get(0).getInicio()));
      params.put("termino", Fecha.formatear(Fecha.FECHA_ESTANDAR, this.partidas.get(this.partidas.size()- 1).getTermino()));
      regresar= DaoFactory.getInstance().updateAll(sesion, TcKeetEstacionesDto.class, params, "periodo")> 0L;
      if(regresar) {
        TcKeetContratosLotesDto lote= (TcKeetContratosLotesDto)DaoFactory.getInstance().findById(sesion, TcKeetContratosLotesDto.class, this.idContratoLote);
        lote.setInicio(this.partidas.get(0).getInicio());
        lote.setTermino(this.partidas.get(this.partidas.size()- 1).getTermino());
        regresar= DaoFactory.getInstance().update(sesion, lote)> 0L;
      } // if  
    } // try
    catch (Exception e) {
			throw e; 
    } // catch	
    finally {
      Methods.clean(params);
      estaciones= null;
    } // finally
    return regresar;
  }
  
  private Boolean toApplyCost(Session sesion) throws Exception {
    Boolean regresar     = Boolean.FALSE;
    Estaciones estaciones= new Estaciones(sesion);
    try {      
      for (Partida item: this.partidas) {
        switch(item.getAccion()) {
          case SELECT:
            if(item.isCostoDiferente()) {
              TcKeetEstacionesBitacoraDto bitacora= new TcKeetEstacionesBitacoraDto(
                item.getIdEstacionEstatus(), // Long idEstacionEstatus, 
                "SE MODIFICO EL CONCEPTO ".concat(item.getCodigo()).concat(", ").concat(item.getNombre()).concat("; COSTO DE ")+ item.getCosto()+ " | "+ item.getCostoAnterior()+ ", ANTICIPO DE "+ item.getAnticipo()+ " | "+ item.getAnticipoAnterior(), // String justificacion
                JsfBase.getIdUsuario(), // Long idUsuario, 
                item.getIdEstacion(), // Long idEstacion, 
                -1L // Long idEstacionBitacora
              );
              DaoFactory.getInstance().insert(sesion, bitacora);
              TcKeetEstacionesDto estacion= (TcKeetEstacionesDto)DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, item.getIdEstacion());
              this.toUpdateFathers(sesion, estaciones, estacion, item);
              estacion.setCosto(item.getCosto());
              estacion.setAnticipo(item.getAnticipo());
              regresar= DaoFactory.getInstance().update(sesion, estacion)>= 1L;
            } // if  
            break;
        } // switch
      } // for
    } // try
    catch (Exception e) {
			throw e; 
    } // catch	
    finally {
      estaciones= null;
    } // finally
    return regresar;
  }
  
  private Boolean toApplyDays(Session sesion) throws Exception {
    Boolean regresar= Boolean.FALSE;
    try {      
      for (Partida item: this.partidas) {
        switch(item.getAccion()) {
          case SELECT:
            if(item.isDiasDiferente()) 
              DaoFactory.getInstance().update(sesion, item);
            break;
          case INSERT:
            DaoFactory.getInstance().insert(sesion, item);
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
  
  private Boolean toChangeData(Session sesion) throws Exception {
    Boolean regresar     = Boolean.FALSE;
    Estaciones estaciones= new Estaciones(sesion);
    try {      
      for (Partida item: this.partidas) {
        TcKeetEstacionesDto estacion= (TcKeetEstacionesDto)DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, item.getIdEstacion());
        if(item.getIdRubro().getKey()> 0L && this.toCheckConcepto(sesion, item, estaciones.toKey(estacion.getClave(), 4))) {
          if(item.isCostoDiferente()) {
            this.toUpdateFathers(sesion, estaciones, estacion, item);
            estacion.setCosto(item.getCosto());
            estacion.setAnticipo(item.getAnticipo());
          } // if  
          TcKeetEstacionesBitacoraDto bitacora= new TcKeetEstacionesBitacoraDto(
            estacion.getIdEstacionEstatus(), // Long idEstacionEstatus, 
            "SE CAMBIO EL CONCEPTO ".concat(estacion.getCodigo()).concat(" POR ").concat(item.getIdRubro().toString("codigo")).concat(" | ").concat(estacion.getNombre()).concat(" CON UN COSTO DE ")+ item.getCosto()+ ", ANTICIPO DE "+ item.getAnticipo(), // String justificacion
            JsfBase.getIdUsuario(), // Long idUsuario, 
            estacion.getIdEstacion(), // Long idEstacion, 
            -1L // Long idEstacionBitacora
          );
          DaoFactory.getInstance().insert(sesion, bitacora);
          estacion.setCodigo(item.getIdRubro().toString("codigo"));
          estacion.setNombre(item.getIdRubro().toString("nombre"));
          estacion.setDescripcion(item.getIdRubro().toString("nombre"));
          estacion.setRegistro(LocalDateTime.now());
          DaoFactory.getInstance().update(sesion, estacion);
        } // if
      } // for
      regresar= Boolean.TRUE;
    } // try
    catch (Exception e) {
			throw e; 
    } // catch	
    return regresar;
  }
  
  private Boolean toDeleteData(Session sesion) throws Exception {
    Boolean regresar          = Boolean.FALSE;
    Map<String, Object> params= new HashMap<>();
    Estaciones estaciones     = new Estaciones(sesion);
    try {      
      for (Partida item: this.partidas) {
        TcKeetEstacionesBitacoraDto bitacora= new TcKeetEstacionesBitacoraDto(
          item.getIdEstacionEstatus(), // Long idEstacionEstatus, 
          "SE ELIMINO EL CONCEPTO ".concat(item.getCodigo()).concat(" | ").concat(item.getNombre()).concat(" CON UN COSTO DE ")+ item.getCosto()+ ", ANTICIPO DE "+ item.getAnticipo(), // String justificacion
          JsfBase.getIdUsuario(), // Long idUsuario, 
          item.getIdEstacion(), // Long idEstacion, 
          -1L // Long idEstacionBitacora
        );
        DaoFactory.getInstance().insert(sesion, bitacora);
        item.setCosto(0D);
        item.setAnticipo(0D);
        TcKeetEstacionesDto estacion= (TcKeetEstacionesDto)DaoFactory.getInstance().findById(sesion, TcKeetEstacionesDto.class, item.getIdEstacion());
        Long idFhater= this.toUpdateFathers(sesion, estaciones, estacion, item);
        params.put("idEstacion", item.getIdEstacion());
        params.put("idArranque", idFhater);
        DaoFactory.getInstance().updateAll(sesion, TcKeetContratosLotesDto.class, params);
        DaoFactory.getInstance().deleteAll(sesion, TcKeetErroresEstacionesDto.class, params);
        DaoFactory.getInstance().deleteAll(sesion, TcKeetContratosRechazosContratistasDto.class, params);
        DaoFactory.getInstance().deleteAll(sesion, TcKeetContratosPuntosContratistasDto.class, params);
        DaoFactory.getInstance().deleteAll(sesion, TcKeetContratosDestajosContratistasDto.class, params);
        DaoFactory.getInstance().deleteAll(sesion, TcKeetContratosRechazosProveedoresDto.class, params);
        DaoFactory.getInstance().deleteAll(sesion, TcKeetContratosPuntosProveedoresDto.class, params);
        DaoFactory.getInstance().deleteAll(sesion, TcKeetContratosDestajosProveedoresDto.class, params);
        // NO SE ELIMINA LA BITACORA PARA SABER QUIEN ELIMINO EL CONCEPTO
        // DaoFactory.getInstance().deleteAll(sesion, TcKeetEstacionesBitacoraDto.class, params);
        DaoFactory.getInstance().delete(sesion, estacion);
      } // for
      regresar= Boolean.TRUE;
    } // try
    catch (Exception e) {
			throw e; 
    } // catch	
    finally {
      Methods.clean(params);
    } // catch	
    return regresar;
  }
  
  private Long toUpdateFathers(Session sesion, Estaciones estaciones, TcKeetEstacionesDto origen, Partida estacion) throws Exception {
    Long regresar= estacion.getIdEstacion();
    try {      
      Double diferencia= Numero.toRedondearSat(estacion.getCosto()- origen.getCosto());
      Double disparidad= Numero.toRedondearSat(estacion.getAnticipo()- origen.getAnticipo());
      List<TcKeetEstacionesDto> items= estaciones.toFather(estacion.getClave());
      if(items!= null && !items.isEmpty()) {
        // ESTE ES EL ID_ESTACION DE LA ESTACION PADRE ESTA EN LA POSICION CERO DEL ARREGLO
        regresar= items.get(0).getIdEstacion();
        items.remove(items.size()- 1);
        for (TcKeetEstacionesDto item: items) {
          item.setCosto(Numero.toRedondearSat(item.getCosto()+ diferencia));
          item.setAnticipo(Numero.toRedondearSat(item.getAnticipo()+ disparidad));
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
    return regresar;
  }
  
  private Boolean toCheckConcepto(Session sesion, Partida partida, String clave) throws Exception {
    Boolean regresar          = Boolean.FALSE;
    Map<String, Object> params= new HashMap<>();
    try {  
      params.put("clave", clave);
      params.put("codigo", partida.getIdRubro().toString("codigo"));
      Entity entity= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcKeetEstacionesDto", "concepto", params);
      if(entity!= null && !entity.isEmpty()) 
        throw new RuntimeException("El concepto [".concat(partida.getIdRubro().toString("codigo")).concat(" | ").concat(partida.getIdRubro().toString("nombre")).concat("] ya existe en el lote"));
      regresar= Boolean.TRUE;
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // catch	
    return regresar;
 }
  
  public static void main(String ... args) {
    Estaciones estaciones= new Estaciones();
    String clave= estaciones.toOnlyKey("0012021002003013002000000", 5);
    System.out.println(clave);
    clave= estaciones.toOnlyKey(clave, 4);
    System.out.println(estaciones.toCode(clave));
  }
  
}