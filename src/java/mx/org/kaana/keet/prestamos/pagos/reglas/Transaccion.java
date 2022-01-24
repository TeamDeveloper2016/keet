package mx.org.kaana.keet.prestamos.pagos.reglas;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.db.dto.TcKeetDeudoresDto;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosControlesDto;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosDetallesDto;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosDto;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosPagosDto;
import mx.org.kaana.keet.prestamos.enums.EEstatusPrestamos;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesDto;
import mx.org.kaana.mantic.enums.EEstatusIncidentes;
import mx.org.kaana.mantic.incidentes.beans.Incidente;
import org.hibernate.Session;

public class Transaccion extends mx.org.kaana.mantic.incidentes.reglas.Transaccion {

	protected TcKeetPrestamosPagosDto prestamosPagos;
  private Long idPrestamoPago;	
  private Long idDeudor;	
  private int pagos;	
  private Double cambio;	

	public Transaccion(Long idPrestamoPago) {
    super(new Incidente());
    this.idPrestamoPago= idPrestamoPago;
  }
  
	public Transaccion(TcKeetPrestamosPagosDto prestamosPagos) {
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
		boolean regresar             = false;
		TcKeetDeudoresDto deudoresDto= null;
		TcKeetPrestamosPagosDto pago = null;
		try {
			switch(accion){
				case REGISTRAR:
					pago= this.calcularPago(sesion, this.prestamosPagos.getIdPrestamo(), this.prestamosPagos.getPago(), EEstatusPrestamos.LIQUIDADA);
					deudoresDto= (TcKeetDeudoresDto)DaoFactory.getInstance().findById(sesion, TcKeetDeudoresDto.class, this.idDeudor);
					deudoresDto.setSaldo(deudoresDto.getSaldo()- pago.getAbono());
					deudoresDto.setDisponible(deudoresDto.getDisponible()+ pago.getAbono());
					DaoFactory.getInstance().update(sesion, deudoresDto);
					regresar= DaoFactory.getInstance().insert(sesion, pago)>= 1L;
          this.toCheckIncidentes(sesion, this.prestamosPagos.getIdPrestamo(), pago.getIdPrestamoPago(), this.prestamosPagos.getConsecutivo(), pago.getAbono());
					this.pagos= 1;
//					if(this.prestamosPagosDto.getIdAfectaNomina().equals(1L)) {
//						this.toLoadIncidente(deudoresDto.getIdEmpresaPersona(), keetPrestamosPagosDto);
//						super.ejecutar(sesion, EAccion.DESTRANSFORMACION);
//					} // if
					break;
				case COMPLETO:
					pago= this.prestamosPagos;
					pago.setCambio(this.prestamosPagos.getPago());
					for(TcKeetPrestamosDto item: (List<TcKeetPrestamosDto>) DaoFactory.getInstance().toEntitySet(sesion, TcKeetPrestamosDto.class, "TcKeetPrestamosDto", "activosByIdDeudor", ((TcKeetPrestamosDto) DaoFactory.getInstance().findById(sesion, TcKeetPrestamosDto.class, this.prestamosPagos.getIdPrestamo())).toMap())){
						pago= calcularPago(sesion, item.getIdPrestamo(), pago.getCambio(), EEstatusPrestamos.SALDADA);
						pago.setRegistro(LocalDateTime.now());
						deudoresDto= (TcKeetDeudoresDto)DaoFactory.getInstance().findById(sesion, TcKeetDeudoresDto.class, this.idDeudor);
						deudoresDto.setSaldo(deudoresDto.getSaldo()- pago.getAbono());
						deudoresDto.setDisponible(deudoresDto.getDisponible()+ pago.getAbono());
						DaoFactory.getInstance().update(sesion, deudoresDto);
						regresar= DaoFactory.getInstance().insert(sesion, pago)>= 1L;
            this.toCheckIncidentes(sesion, item.getIdPrestamo(), pago.getIdPrestamoPago(),this.prestamosPagos.getConsecutivo(), pago.getAbono());
						this.pagos++;
//						if(keetPrestamosPagosDto.getIdAfectaNomina().equals(1L)){
//							this.toLoadIncidente(sesion, deudoresDto.getIdEmpresaPersona(), keetPrestamosPagosDto);
//							super.ejecutar(sesion, EAccion.DESTRANSFORMACION);
//						} // if
						if(pago.getCambio()== 0D)
							break;
					} // for
					break;
				case ELIMINAR:
          regresar= this.toDeletePago(sesion);
          break;
			} // switch	
			this.cambio= pago!= null? pago.getCambio(): 0D;
		} // try // try // try // try
		catch (Exception e) {			
			throw new Exception(e);
		} // catch		
		return regresar;
	}	// ejecutar
	
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());
			Value next= DaoFactory.getInstance().toField(sesion, "TcKeetPrestamosPagosDto", "siguiente", params, "siguiente");
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

	private TcKeetPrestamosPagosDto calcularPago(Session sesion,Long idPrestamo, Double pago, EEstatusPrestamos tipoFinalizar) throws Exception{
		TcKeetPrestamosPagosDto regresar= null;
		TcKeetPrestamosDto prestamosDto = null;
		Siguiente siguiente             = null;
		Double restante, abono          = null;
		TcKeetPrestamosBitacoraDto bitacoraDto= null;
		try {
			prestamosDto= (TcKeetPrestamosDto) DaoFactory.getInstance().findById(sesion, TcKeetPrestamosDto.class, idPrestamo);
			this.idDeudor= prestamosDto.getIdDeudor();
			siguiente= this.toSiguiente(sesion);
			if((prestamosDto.getSaldo()-pago)>= 0) { //sin cambio
				abono= pago;
				restante= 0D;
			} // if
			else{
				abono= prestamosDto.getSaldo();
				restante= pago- prestamosDto.getSaldo();
			}// else
			prestamosDto.setSaldo(prestamosDto.getSaldo()-abono);
			if(prestamosDto.getSaldo()== 0D)
				prestamosDto.setIdPrestamoEstatus(tipoFinalizar.getIdEstatusPrestamo());
			else
				prestamosDto.setIdPrestamoEstatus(EEstatusPrestamos.PARCIALIZADA.getIdEstatusPrestamo());
			DaoFactory.getInstance().update(sesion, prestamosDto);
			bitacoraDto= new TcKeetPrestamosBitacoraDto("", prestamosDto.getKey(), -1L, JsfBase.getIdUsuario(), prestamosDto.getIdPrestamoEstatus());
			DaoFactory.getInstance().insert(sesion, bitacoraDto);
			regresar= new TcKeetPrestamosPagosDto(siguiente.getConsecutivo(), restante, idPrestamo, JsfBase.getIdUsuario(), this.prestamosPagos.getIdAfectaNomina(), -1L, this.prestamosPagos.getObservaciones(), abono, siguiente.getOrden(), pago, Long.parseLong(String.valueOf(this.getCurrentYear())));
		} // try // try
		catch (Exception e) {
			throw e;
		} // catch
		return regresar;
	}
	
  private void toCheckIncidentes(Session sesion, Long idPrestamo, Long idPrestamoPago, String consecutivo, Double abono) throws Exception {
    Map<String, Object> params = null;
    try {      
      params = new HashMap<>();      
      params.put("idPrestamo", idPrestamo);      
      params.put(Constantes.SQL_CONDICION, "tc_mantic_incidentes.id_nomina is null and tc_mantic_incidentes.id_incidente_estatus in (2)");
      List<TcManticIncidentesDto> items= (List<TcManticIncidentesDto>)DaoFactory.getInstance().toEntitySet(sesion, TcManticIncidentesDto.class, "VistaPrestamosDto", "pagos", params);
      if(items!= null && !items.isEmpty()) {
        for (TcManticIncidentesDto pago: items) {
          if(pago.getCosto()<= abono) {
            pago.setIdIncidenteEstatus(EEstatusIncidentes.APLICADA.getIdEstatusInicidente());
            DaoFactory.getInstance().update(sesion, pago);
            abono-= pago.getCosto();
            TcKeetPrestamosDetallesDto detalle= new TcKeetPrestamosDetallesDto(
              -1L, // Long idPrestamoDetalle
              pago.getIdIncidente(), // Long idIncidente, 
              idPrestamoPago, // Long idPrestamoPago
              null // Long idComplemento, 
            );
            DaoFactory.getInstance().insert(sesion, detalle);
          } // if
          else
            if(abono> 0D) {
              TcManticIncidentesDto adicional= pago.toClon();
              pago.setIdIncidenteEstatus(EEstatusIncidentes.APLICADA.getIdEstatusInicidente());
              adicional.setCosto(pago.getCosto()- abono);
              pago.setCosto(abono);
              DaoFactory.getInstance().update(sesion, pago);
              Siguiente siguientes= this.toContinuar(sesion);			
              adicional.setConsecutivo(siguientes.getConsecutivo());			
              adicional.setOrden(siguientes.getOrden());			
              adicional.setEjercicio(Long.valueOf(Fecha.getAnioActual()));			
              adicional.setObservaciones("FOLIO (N) PRESTAMOS[".concat(consecutivo).concat("] [").concat(Fecha.formatear(Fecha.FECHA_HORA_CORTA, LocalDateTime.now())).concat("]"));
              DaoFactory.getInstance().insert(sesion, adicional);
              abono= 0D;
              TcKeetPrestamosDetallesDto detalle= new TcKeetPrestamosDetallesDto(
                -1L, // Long idPrestamoDetalle
                pago.getIdIncidente(), // Long idIncidente, 
                idPrestamoPago, // Long idPrestamoPago
                adicional.getIdIncidente() // Long idComplemento, 
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
      params.put("idPrestamoPago", this.idPrestamoPago);
      List<TcKeetPrestamosDetallesDto> incidentes= (List<TcKeetPrestamosDetallesDto>)DaoFactory.getInstance().toEntitySet(sesion, TcKeetPrestamosDetallesDto.class, "TcKeetPrestamosDetallesDto", "incidentes", params);
      if(incidentes!= null && !incidentes.isEmpty()) {
        // RECUPERAR LOS INCIDENTES DEL PAGO PARA CAMBIARLOS DE ESTATUS DE APLICADO A ACEPTADO
        for (TcKeetPrestamosDetallesDto item: incidentes) {
          if(item.getIdComplemento()!= null) {
            params.put("idIncidente", item.getIdComplemento());
            TcManticIncidentesDto complemento= (TcManticIncidentesDto)DaoFactory.getInstance().toEntity(sesion, TcManticIncidentesDto.class, "TcManticIncidentesDto", "igual", params);
            if(complemento!= null && complemento.getIdNomina()!= null) 
              throw new RuntimeException("No se puede eliminar el pago, porque ya se pago una parte en la nómina "+ complemento.getIdNomina());
            params.put("idIncidente", item.getIdIncidente());
            TcManticIncidentesDto movimiento= (TcManticIncidentesDto)DaoFactory.getInstance().toEntity(sesion, TcManticIncidentesDto.class, "TcManticIncidentesDto", "igual", params);
            movimiento.setCosto(Numero.toRedondearSat(movimiento.getCosto()+ (complemento!= null? complemento.getCosto(): 0D)));
            movimiento.setIdIncidenteEstatus(EEstatusIncidentes.ACEPTADA.getIdEstatusInicidente());
            DaoFactory.getInstance().update(sesion, movimiento);
            DaoFactory.getInstance().delete(sesion, complemento);
          } // if
          else {
            params.put("idIncidente", item.getIdIncidente());
            TcManticIncidentesDto movimiento= (TcManticIncidentesDto)DaoFactory.getInstance().toEntity(sesion, TcManticIncidentesDto.class, "TcManticIncidentesDto", "igual", params);
            movimiento.setIdIncidenteEstatus(EEstatusIncidentes.ACEPTADA.getIdEstatusInicidente());
            DaoFactory.getInstance().update(sesion, movimiento);
          } // if
        } // for
        // ESTE REGISTRO ES PARA LLEVAR EL CONTROL DE QUIENES HAN BORRRADOS LOS PAGOS
        TcKeetPrestamosControlesDto control= new TcKeetPrestamosControlesDto(
          "EL PAGO SE ELIMINO PORQUE HUBO UN ERROR", // String justificacion, 
          JsfBase.getIdUsuario(), // Long idUsuario, 
          this.idPrestamoPago, // Long idPrestamoPago, 
          -1L // Long idPrestamoControl
        );
        DaoFactory.getInstance().insert(sesion, control);
      } // if
      // ELIMINAR EL PAGO Y ACTUALIZAR EL ESTATUS DEL PRESTAMO
      TcKeetPrestamosPagosDto pago= (TcKeetPrestamosPagosDto)DaoFactory.getInstance().findById(sesion, TcKeetPrestamosPagosDto.class, this.idPrestamoPago);
      if(pago!= null) {
        TcKeetPrestamosDto anticipo= (TcKeetPrestamosDto)DaoFactory.getInstance().findById(sesion, TcKeetPrestamosDto.class, pago.getIdPrestamo());
        anticipo.setSaldo(Numero.toRedondearSat(anticipo.getSaldo()+ pago.getAbono()));
        if(Objects.equals(anticipo.getSaldo(), anticipo.getImporte()))
          anticipo.setIdPrestamoEstatus(EEstatusPrestamos.INICIALIZADA.getIdEstatusPrestamo());
        else
          anticipo.setIdPrestamoEstatus(EEstatusPrestamos.PARCIALIZADA.getIdEstatusPrestamo());
        DaoFactory.getInstance().update(sesion, anticipo);
      } // if
      DaoFactory.getInstance().delete(sesion, pago);
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