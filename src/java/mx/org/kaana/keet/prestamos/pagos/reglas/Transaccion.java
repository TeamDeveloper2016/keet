package mx.org.kaana.keet.prestamos.pagos.reglas;

import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.db.dto.TcKeetDeudoresDto;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosDto;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosPagosDto;
import mx.org.kaana.keet.prestamos.enums.EEstatusPrestamos;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private TcKeetPrestamosPagosDto prestamosPagosDto;
  private Long idDeudor;	


	public Transaccion(TcKeetPrestamosPagosDto prestamosPagosDto) {
		this.prestamosPagosDto= prestamosPagosDto;	
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar               = false;
		TcKeetDeudoresDto deudoresDto  = null;
		TcKeetPrestamosPagosDto keetPrestamosPagosDto= null;
		try {
			switch(accion){
				case REGISTRAR:
					keetPrestamosPagosDto= calcularPago(sesion, this.prestamosPagosDto.getIdPrestamo(), this.prestamosPagosDto.getPago(), EEstatusPrestamos.LIQUIDADA);
					deudoresDto= (TcKeetDeudoresDto)DaoFactory.getInstance().findById(TcKeetDeudoresDto.class, this.idDeudor);
					deudoresDto.setSaldo(deudoresDto.getSaldo()- keetPrestamosPagosDto.getAbono());
					deudoresDto.setDisponible(deudoresDto.getDisponible()+ keetPrestamosPagosDto.getAbono());
					DaoFactory.getInstance().update(sesion, deudoresDto);
					regresar= DaoFactory.getInstance().insert(sesion, keetPrestamosPagosDto)>= 1L;
					break;
					
				case COMPLETO:
					/*
					prestamosPagoDto= calcularPago(sesion, this.prestamosPagosDto.getIdPrestamo(), this.prestamosPagosDto.getPago(), EEstatusPrestamos.LIQUIDADA, idDeudor);
					deudoresDto= (TcKeetDeudoresDto)DaoFactory.getInstance().findById(TcKeetDeudoresDto.class, idDeudor);
					deudoresDto.setSaldo(deudoresDto.getSaldo()- this.prestamosPagosDto.getAbono());
					deudoresDto.setDisponible(deudoresDto.getDisponible()+ this.prestamosPagosDto.getAbono());
					DaoFactory.getInstance().update(sesion, deudoresDto);
					regresar= DaoFactory.getInstance().insert(sesion, prestamosPagoDto)>= 1L;
*/
					break;
			} // switch						
		} // try
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
		Double cambio, abono            = null;
		try {
			prestamosDto= (TcKeetPrestamosDto) DaoFactory.getInstance().findById(sesion, TcKeetPrestamosDto.class, idPrestamo);
			this.idDeudor= prestamosDto.getIdDeudor();
			siguiente= toSiguiente(sesion);
			if((prestamosDto.getSaldo()-pago)>= 0){ //sin cambio
				abono= pago;
				cambio= 0D;
			} // if
			else{
				abono= prestamosDto.getSaldo();
				cambio= pago- prestamosDto.getSaldo();
			}// else
			prestamosDto.setSaldo(prestamosDto.getSaldo()-abono);
			if(prestamosDto.getSaldo()== 0D)
				prestamosDto.setIdPrestamoEstatus(tipoFinalizar.getIdEstatusPrestamo());
			else
				prestamosDto.setIdPrestamoEstatus(EEstatusPrestamos.PARCIALIZADA.getIdEstatusPrestamo());
			DaoFactory.getInstance().update(sesion, prestamosDto);
			regresar= new TcKeetPrestamosPagosDto(siguiente.getConsecutivo(), cambio, idPrestamo, JsfBase.getIdUsuario(), -1L, this.prestamosPagosDto.getObservaciones(), abono, siguiente.getOrden(), pago, Long.parseLong(String.valueOf(this.getCurrentYear())));
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		return regresar;
	}

	
}