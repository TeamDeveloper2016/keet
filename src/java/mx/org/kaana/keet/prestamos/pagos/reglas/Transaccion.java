package mx.org.kaana.keet.prestamos.pagos.reglas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.db.dto.TcKeetDeudoresDto;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosDto;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosPagosDto;
import mx.org.kaana.keet.enums.ETiposIncidentes;
import mx.org.kaana.keet.prestamos.enums.EEstatusPrestamos;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.enums.EEstatusIncidentes;
import mx.org.kaana.mantic.incidentes.beans.Incidente;
import org.hibernate.Session;

public class Transaccion extends mx.org.kaana.mantic.incidentes.reglas.Transaccion {

	protected TcKeetPrestamosPagosDto prestamosPagos;
  private Long idDeudor;	
  private int pagos;	
  private Double cambio;	


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
					pago= calcularPago(sesion, this.prestamosPagos.getIdPrestamo(), this.prestamosPagos.getPago(), EEstatusPrestamos.LIQUIDADA);
					deudoresDto= (TcKeetDeudoresDto)DaoFactory.getInstance().findById(sesion, TcKeetDeudoresDto.class, this.idDeudor);
					deudoresDto.setSaldo(deudoresDto.getSaldo()- pago.getAbono());
					deudoresDto.setDisponible(deudoresDto.getDisponible()+ pago.getAbono());
					DaoFactory.getInstance().update(sesion, deudoresDto);
					regresar= DaoFactory.getInstance().insert(sesion, pago)>= 1L;
					this.pagos= 1;
//					if(this.prestamosPagosDto.getIdAfectaNomina().equals(1L)) {
//						this.toLoadIncidente(deudoresDto.getIdEmpresaPersona(), keetPrestamosPagosDto);
//						super.ejecutar(sesion, EAccion.DESTRANSFORMACION);
//					} // if
					break;
				case COMPLETO:
					pago= this.prestamosPagos;
					pago.setCambio(this.prestamosPagos.getPago());
					for(TcKeetPrestamosDto item: (List<TcKeetPrestamosDto>) DaoFactory.getInstance().toEntitySet(sesion, TcKeetPrestamosDto.class,"TcKeetPrestamosDto", "activosByIdDeudor", ((TcKeetPrestamosDto) DaoFactory.getInstance().findById(sesion, TcKeetPrestamosDto.class, this.prestamosPagos.getIdPrestamo())).toMap())){
						pago= calcularPago(sesion, item.getIdPrestamo(), pago.getCambio(), EEstatusPrestamos.SALDADA);
						pago.setRegistro(LocalDateTime.now());
						deudoresDto= (TcKeetDeudoresDto)DaoFactory.getInstance().findById(sesion, TcKeetDeudoresDto.class, this.idDeudor);
						deudoresDto.setSaldo(deudoresDto.getSaldo()- pago.getAbono());
						deudoresDto.setDisponible(deudoresDto.getDisponible()+ pago.getAbono());
						DaoFactory.getInstance().update(sesion, deudoresDto);
						regresar= DaoFactory.getInstance().insert(sesion, pago)>= 1L;
						this.pagos++;
//						if(keetPrestamosPagosDto.getIdAfectaNomina().equals(1L)){
//							this.toLoadIncidente(sesion, deudoresDto.getIdEmpresaPersona(), keetPrestamosPagosDto);
//							super.ejecutar(sesion, EAccion.DESTRANSFORMACION);
//						} // if
						if(pago.getCambio()== 0D)
							break;
					} // for
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
	
	private void toLoadIncidente(Long idEmpresaPersona, TcKeetPrestamosPagosDto prestamoPagoDto) throws Exception{
		this.getIncidente().setCosto(prestamoPagoDto.getAbono());
		this.getIncidente().setTipoIncidente(ETiposIncidentes.ABONO_NOMINA.name());
		this.getIncidente().setIdTipoIncidente(ETiposIncidentes.ABONO_NOMINA.getKey());
		this.getIncidente().setVigenciaInicio(LocalDate.now());
		this.getIncidente().setVigenciaFin(LocalDate.now());
		this.getIncidente().setIdIncidenteEstatus(EEstatusIncidentes.ACEPTADA.getIdEstatusInicidente());
		this.getIncidente().setIdEmpresaPersona(idEmpresaPersona);
	}
	
}