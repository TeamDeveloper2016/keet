package mx.org.kaana.keet.prestamos.proveedor.pagos.reglas;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.db.dto.TcKeetAnticiposBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetAnticiposControlesDto;
import mx.org.kaana.keet.db.dto.TcKeetAnticiposDetallesDto;
import mx.org.kaana.keet.db.dto.TcKeetAnticiposDto;
import mx.org.kaana.keet.db.dto.TcKeetAnticiposPagosDto;
import mx.org.kaana.keet.db.dto.TcKeetDeudoresDto;
import mx.org.kaana.keet.db.dto.TcKeetIncidentesDto;
import mx.org.kaana.keet.db.dto.TcKeetMorososDto;
import mx.org.kaana.keet.prestamos.enums.EEstatusPrestamos;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.enums.EEstatusIncidentes;
import mx.org.kaana.mantic.incidentes.beans.Incidente;
import org.hibernate.Session;

public class Transaccion extends mx.org.kaana.mantic.incidentes.reglas.Transaccion {

	protected TcKeetAnticiposPagosDto prestamosPagos;
  private Long idAnticipoPago;	
  private Long idMoroso;	
  private int pagos;	
  private Double cambio;	

	public Transaccion(Long idAnticipoPago) {
    super(new Incidente());
    this.idAnticipoPago= idAnticipoPago;
  }
  
	public Transaccion(TcKeetAnticiposPagosDto prestamosPagos) {
		super(new Incidente());
		this.prestamosPagos= prestamosPagos;	
		this.pagos= 0;
	}

	public int getPagos() {
		return pagos;
	}

	public Double getCambio() {
		return cambio;
	}
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar            = false;
		TcKeetMorososDto deudoresDto= null;
		TcKeetAnticiposPagosDto pago= null;
		try {
			switch(accion) {
				case REGISTRAR:
          regresar= this.toRegistrar(sesion);
					break;
				case COMPLETO:
          regresar= this.toComplemento(sesion);
					break;
				case ELIMINAR:
          regresar= this.toDeletePago(sesion);
          break;
			} // switch	
			this.cambio= pago!= null? pago.getCambio(): 0D;
		} // try 
		catch (Exception e) {			
			throw new Exception(e);
		} // catch		
		return regresar;
	}	// ejecutar
	
  public Boolean toRegistrar(Session sesion) throws Exception {
		boolean regresar            = Boolean.FALSE;
		TcKeetMorososDto morosos    = null;
		TcKeetAnticiposPagosDto pago= null;
		try {
      pago= this.calcularPago(sesion, this.prestamosPagos.getIdAnticipo(), this.prestamosPagos.getPago(), EEstatusPrestamos.LIQUIDADA);
      morosos= (TcKeetMorososDto)DaoFactory.getInstance().findById(sesion, TcKeetMorososDto.class, this.idMoroso);
      morosos.setSaldo(morosos.getSaldo()- pago.getAbono());
      morosos.setDisponible(morosos.getDisponible()+ pago.getAbono());
      DaoFactory.getInstance().update(sesion, morosos);
      regresar= DaoFactory.getInstance().insert(sesion, pago)>= 1L;
      this.toCheckIncidentes(sesion, this.prestamosPagos.getIdAnticipo(), pago.getIdAnticipoPago(), this.prestamosPagos.getConsecutivo(), pago.getAbono());
		} // try 
		catch (Exception e) {			
			throw new Exception(e);
		} // catch		
		return regresar;
  }
  
  public Boolean toComplemento(Session sesion) throws Exception {
		boolean regresar            = Boolean.FALSE;
		TcKeetMorososDto deudoresDto= null;
		TcKeetAnticiposPagosDto pago= null;
		try {
      pago= this.prestamosPagos;
      pago.setCambio(this.prestamosPagos.getPago());
      for(TcKeetAnticiposDto item: (List<TcKeetAnticiposDto>) DaoFactory.getInstance().toEntitySet(sesion, TcKeetAnticiposDto.class, "TcKeetAnticiposDto", "activosByIdDeudor", ((TcKeetAnticiposDto) DaoFactory.getInstance().findById(sesion, TcKeetAnticiposDto.class, this.prestamosPagos.getIdAnticipo())).toMap())) {
        pago= this.calcularPago(sesion, item.getIdAnticipo(), pago.getCambio(), EEstatusPrestamos.SALDADA);
        pago.setRegistro(LocalDateTime.now());
        deudoresDto= (TcKeetMorososDto)DaoFactory.getInstance().findById(sesion, TcKeetDeudoresDto.class, this.idMoroso);
        deudoresDto.setSaldo(deudoresDto.getSaldo()- pago.getAbono());
        deudoresDto.setDisponible(deudoresDto.getDisponible()+ pago.getAbono());
        DaoFactory.getInstance().update(sesion, deudoresDto);
        regresar= DaoFactory.getInstance().insert(sesion, pago)>= 1L;
        this.toCheckIncidentes(sesion, item.getIdAnticipo(), pago.getIdAnticipoPago(), this.prestamosPagos.getConsecutivo(), pago.getAbono());
        this.pagos++;
        if(pago.getCambio()== 0D)
          break;
      } // for
		} // try 
		catch (Exception e) {			
			throw new Exception(e);
		} // catch		
		return regresar;
  }
  
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("ejercicio", this.getCurrentYear());
			Value next= DaoFactory.getInstance().toField(sesion, "TcKeetAnticiposPagosDto", "siguiente", params, "siguiente");
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
	} // toSiguiente

	private TcKeetAnticiposPagosDto calcularPago(Session sesion, Long idAnticipo, Double pago, EEstatusPrestamos tipoFinalizar) throws Exception {
		TcKeetAnticiposPagosDto regresar= null;
		TcKeetAnticiposDto prestamosDto = null;
		Siguiente siguiente             = null;
		Double restante, abono          = null;
		TcKeetAnticiposBitacoraDto bitacoraDto= null;
		try {
			prestamosDto= (TcKeetAnticiposDto) DaoFactory.getInstance().findById(sesion, TcKeetAnticiposDto.class, idAnticipo);
			this.idMoroso= prestamosDto.getIdMoroso();
			siguiente= this.toSiguiente(sesion);
			if((prestamosDto.getSaldo()-pago)>= 0) { // sin cambio
				abono= pago;
				restante= 0D;
			} // if
      else {
				abono= prestamosDto.getSaldo();
				restante= pago- prestamosDto.getSaldo();
			} // else
			prestamosDto.setSaldo(prestamosDto.getSaldo()-abono);
			if(prestamosDto.getSaldo()== 0D)
				prestamosDto.setIdAnticipoEstatus(tipoFinalizar.getIdEstatusPrestamo());
			else
				prestamosDto.setIdAnticipoEstatus(EEstatusPrestamos.PARCIALIZADA.getIdEstatusPrestamo());
			DaoFactory.getInstance().update(sesion, prestamosDto);
			bitacoraDto= new TcKeetAnticiposBitacoraDto(prestamosDto.getKey(), prestamosDto.getIdAnticipoEstatus(), "", JsfBase.getIdUsuario(), -1L);
			DaoFactory.getInstance().insert(sesion, bitacoraDto);
			regresar= new TcKeetAnticiposPagosDto(
        siguiente.getConsecutivo(), // String consecutivo
        restante, // Double cambio
        idAnticipo, // Long idAnticipo
        this.prestamosPagos.getIdAfectaNomina(), // Long idAfectaNomina
        JsfBase.getIdUsuario(), // Long idUsuario      
        this.prestamosPagos.getObservaciones(), // String observaciones      
        -1L, // Long idAnticipoPago
        abono, // Double abono   
        siguiente.getOrden(), // Long orden  
        pago, // Double pago  
        Long.parseLong(String.valueOf(this.getCurrentYear())) // Long ejercicio      
      );
		} // try 
		catch (Exception e) {
			throw e;
		} // catch
		return regresar;
	}
	
  private void toCheckIncidentes(Session sesion, Long idAnticipo, Long idAnticipoPago, String consecutivo, Double abono) throws Exception {
    Map<String, Object> params = null;
    try {      
      params = new HashMap<>();      
      params.put("idAnticipo", idAnticipo);      
      params.put(Constantes.SQL_CONDICION, "tc_keet_incidentes.id_nomina is null and tc_keet_incidentes.id_incidente_estatus in (2)");
      List<TcKeetIncidentesDto> items= (List<TcKeetIncidentesDto>)DaoFactory.getInstance().toEntitySet(sesion, TcKeetIncidentesDto.class, "VistaAnticiposDto", "pagos", params);
      if(items!= null && !items.isEmpty()) {
        for (TcKeetIncidentesDto pago: items) {
          if(pago.getCosto()<= abono) {
            pago.setIdIncidenteEstatus(EEstatusIncidentes.APLICADA.getIdEstatusInicidente());
            DaoFactory.getInstance().update(sesion, pago);
            abono-= pago.getCosto();
            TcKeetAnticiposDetallesDto detalle= new TcKeetAnticiposDetallesDto(
              pago.getIdIncidente(), // Long idIncidente, 
              null, // Long idComplemento, 
              idAnticipoPago, // Long idAnticipoPago, 
              -1L // Long idAnticipoDetalle
            );
            DaoFactory.getInstance().insert(sesion, detalle);
          } // if
          else
            if(abono> 0D) {
              TcKeetIncidentesDto adicional= pago.toClon();
              pago.setIdIncidenteEstatus(EEstatusIncidentes.APLICADA.getIdEstatusInicidente());
              adicional.setCosto(Numero.toRedondearSat(pago.getCosto()- abono));
              pago.setCosto(abono);
              DaoFactory.getInstance().update(sesion, pago);
              Siguiente siguientes= this.toContinuar(sesion);			
              adicional.setConsecutivo(siguientes.getConsecutivo());			
              adicional.setOrden(siguientes.getOrden());			
              adicional.setEjercicio(Long.valueOf(Fecha.getAnioActual()));			
              adicional.setObservaciones("FOLIO (N) ANTICIPO[".concat(consecutivo).concat("] [").concat(Fecha.formatear(Fecha.FECHA_HORA_CORTA, LocalDateTime.now())).concat("]"));
              DaoFactory.getInstance().insert(sesion, adicional);
              abono= 0D;
              TcKeetAnticiposDetallesDto detalle= new TcKeetAnticiposDetallesDto(
                pago.getIdIncidente(), // Long idIncidente, 
                adicional.getIdIncidente(), // Long idComplemento, 
                idAnticipoPago, // Long idAnticipoPago, 
                -1L // Long idAnticipoDetalle
              );
              DaoFactory.getInstance().insert(sesion, detalle);
            } // if
        } // for
      } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }

  private Boolean toDeletePago(Session sesion) throws Exception {
    Boolean regresar          = Boolean.FALSE;
    Map<String, Object> params= null;
    try {      
      params = new HashMap<>();      
      params.put("idAnticipoPago", this.idAnticipoPago);
      List<TcKeetAnticiposDetallesDto> incidentes= (List<TcKeetAnticiposDetallesDto>)DaoFactory.getInstance().toEntitySet(sesion, TcKeetAnticiposDetallesDto.class, "TcKeetAnticiposDetallesDto", "incidentes", params);
      if(incidentes!= null && !incidentes.isEmpty()) {
        // RECUPERAR LOS INCIDENTES DEL PAGO PARA CAMBIARLOS DE ESTATUS DE APLICADO A ACEPTADO
        for (TcKeetAnticiposDetallesDto item: incidentes) {
          if(item.getIdComplemento()!= null) {
            params.put("idIncidente", item.getIdComplemento());
            TcKeetIncidentesDto complemento= (TcKeetIncidentesDto)DaoFactory.getInstance().toEntity(sesion, TcKeetIncidentesDto.class, "TcKeetIncidentesDto", "igual", params);
            if(complemento!= null && complemento.getIdNomina()!= null) 
              throw new RuntimeException("No se puede eliminar el pago, porque ya se pago una parte en la nómina "+ complemento.getIdNomina());
            params.put("idIncidente", item.getIdIncidente());
            TcKeetIncidentesDto movimiento= (TcKeetIncidentesDto)DaoFactory.getInstance().toEntity(sesion, TcKeetIncidentesDto.class, "TcKeetIncidentesDto", "igual", params);
            movimiento.setCosto(Numero.toRedondearSat(movimiento.getCosto()+ (complemento!= null? complemento.getCosto(): 0D)));
            movimiento.setIdIncidenteEstatus(EEstatusIncidentes.ACEPTADA.getIdEstatusInicidente());
            DaoFactory.getInstance().update(sesion, movimiento);
            DaoFactory.getInstance().delete(sesion, complemento);
          } // if
          else {
            params.put("idIncidente", item.getIdIncidente());
            TcKeetIncidentesDto movimiento= (TcKeetIncidentesDto)DaoFactory.getInstance().toEntity(sesion, TcKeetIncidentesDto.class, "TcKeetIncidentesDto", "igual", params);
            movimiento.setIdIncidenteEstatus(EEstatusIncidentes.ACEPTADA.getIdEstatusInicidente());
            DaoFactory.getInstance().update(sesion, movimiento);
          } // if
        } // for
      } // if
      // ELIMINAR EL PAGO Y ACTUALIZAR EL ESTATUS DEL ANTICIPO
      TcKeetAnticiposPagosDto pago= (TcKeetAnticiposPagosDto)DaoFactory.getInstance().findById(sesion, TcKeetAnticiposPagosDto.class, this.idAnticipoPago);
      if(pago!= null) {
        TcKeetAnticiposDto anticipo= (TcKeetAnticiposDto)DaoFactory.getInstance().findById(sesion, TcKeetAnticiposDto.class, pago.getIdAnticipo());
        anticipo.setSaldo(Numero.toRedondearSat(anticipo.getSaldo()+ pago.getAbono()));
        if(Objects.equals(anticipo.getSaldo(), anticipo.getImporte()))
          anticipo.setIdAnticipoEstatus(EEstatusPrestamos.INICIALIZADA.getIdEstatusPrestamo());
        else
          anticipo.setIdAnticipoEstatus(EEstatusPrestamos.PARCIALIZADA.getIdEstatusPrestamo());
        DaoFactory.getInstance().update(sesion, anticipo);
        TcKeetAnticiposBitacoraDto bitacora= new TcKeetAnticiposBitacoraDto(anticipo.getIdAnticipo(), anticipo.getIdAnticipoEstatus(), "SE CANCELO UN PAGO ["+ pago.getAbono()+ "]", JsfBase.getIdUsuario(), -1L);
			  DaoFactory.getInstance().insert(sesion, bitacora);        
        DaoFactory.getInstance().delete(sesion, pago);
        // ESTE REGISTRO ES PARA LLEVAR EL CONTROL DE QUIENES HAN BORRRADOS LOS PAGOS
        TcKeetAnticiposControlesDto control= new TcKeetAnticiposControlesDto(
          "EL PAGO SE ELIMINO PORQUE HUBO UN ERROR", // String justificacion, 
          JsfBase.getIdUsuario(), // Long idUsuario, 
          this.idAnticipoPago, // Long idAnticipoPago, 
          -1L, // Long idAnticipoControl
          pago.getAbono() // Double importe      
        );
        DaoFactory.getInstance().insert(sesion, control);
        regresar= Boolean.TRUE;
      } // if
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