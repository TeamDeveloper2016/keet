package mx.org.kaana.keet.prestamos.reglas;

import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.prestamos.beans.Documento;
import mx.org.kaana.keet.db.dto.TcKeetDeudoresDto;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosBitacoraDto;
import mx.org.kaana.keet.prestamos.beans.RegistroPrestamo;
import mx.org.kaana.keet.prestamos.enums.EEstatusPrestamos;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private RegistroPrestamo prestamo;	
	private IBaseDto dtoDelete;

	public Transaccion(IBaseDto dtoDelete) {
		this.dtoDelete = dtoDelete;
	}	
	
	public Transaccion(RegistroPrestamo prestamo) {
		this.prestamo= prestamo;	
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar             = true;
		Long idUsuario               = -1L;
		Siguiente siguiente          = null;
		TcKeetDeudoresDto deudoresDto= null;
		TcKeetPrestamosBitacoraDto bitacoraDto= null;
		try {
			idUsuario= JsfBase.getIdUsuario();
			switch(accion){
				case AGREGAR:
					siguiente= toSiguiente(sesion);
					this.prestamo.getPrestamo().setConsecutivo(siguiente.getConsecutivo());
					this.prestamo.getPrestamo().setOrden(siguiente.getOrden());
					this.prestamo.getPrestamo().setEjercicio(Long.parseLong(String.valueOf(this.getCurrentYear())));
					this.prestamo.getPrestamo().setIdUsuario(idUsuario);
					this.prestamo.getPrestamo().setIdPrestamoEstatus(EEstatusPrestamos.INICIALIZADA.getIdEstatusPrestamo());
					this.prestamo.getPrestamo().setSaldo(this.prestamo.getPrestamo().getImporte());
					deudoresDto= (TcKeetDeudoresDto)DaoFactory.getInstance().findById(TcKeetDeudoresDto.class, this.prestamo.getPrestamo().getIdDeudor());
					deudoresDto.setSaldo(deudoresDto.getSaldo()+ this.prestamo.getPrestamo().getImporte());
					deudoresDto.setDisponible(deudoresDto.getDisponible()- this.prestamo.getPrestamo().getImporte());
					DaoFactory.getInstance().update(sesion, deudoresDto);
					regresar= DaoFactory.getInstance().insert(sesion, this.prestamo.getPrestamo())>= 1L;
					bitacoraDto= new TcKeetPrestamosBitacoraDto("", this.prestamo.getPrestamo().getKey(), -1L, idUsuario, this.prestamo.getPrestamo().getIdPrestamoEstatus());
					regresar= DaoFactory.getInstance().insert(sesion, bitacoraDto)>= 1L;
					break;
				case DESACTIVAR:
					this.prestamo.getPrestamo().setIdPrestamoEstatus(EEstatusPrestamos.CANCELADA.getIdEstatusPrestamo());// CANCELADA
					deudoresDto= (TcKeetDeudoresDto)DaoFactory.getInstance().findById(TcKeetDeudoresDto.class, this.prestamo.getPrestamo().getIdDeudor());
					deudoresDto.setSaldo(deudoresDto.getSaldo()- this.prestamo.getPrestamo().getSaldo());
					deudoresDto.setDisponible(deudoresDto.getDisponible()+ this.prestamo.getPrestamo().getSaldo());
					DaoFactory.getInstance().update(sesion, deudoresDto);
					regresar= DaoFactory.getInstance().update(sesion, this.prestamo.getPrestamo())>= 1L;
					bitacoraDto= new TcKeetPrestamosBitacoraDto("", this.prestamo.getPrestamo().getKey(), -1L, idUsuario, this.prestamo.getPrestamo().getIdPrestamoEstatus());
					regresar= DaoFactory.getInstance().insert(sesion, bitacoraDto)>= 1L;
					break;
				case SUBIR:					
					for (Documento dto : this.prestamo.getDocumentos()) {
						if (DaoFactory.getInstance().insert(sesion, dto) >= 1L) 
							toSaveFile(dto.getIdArchivo());
					} // for
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
			Value next= DaoFactory.getInstance().toField(sesion, "TcKeetPrestamosDto", "siguiente", params, "siguiente");
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

	
}