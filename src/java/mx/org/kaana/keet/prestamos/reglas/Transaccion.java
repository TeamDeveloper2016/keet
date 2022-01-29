package mx.org.kaana.keet.prestamos.reglas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.prestamos.beans.Documento;
import mx.org.kaana.keet.db.dto.TcKeetDeudoresDto;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosLotesDto;
import mx.org.kaana.keet.enums.ETiposIncidentes;
import mx.org.kaana.keet.prestamos.beans.RegistroPrestamo;
import mx.org.kaana.keet.prestamos.enums.EEstatusPrestamos;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.enums.EEstatusIncidentes;
import mx.org.kaana.mantic.incidentes.beans.Incidente;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends mx.org.kaana.mantic.incidentes.reglas.Transaccion {

  private static final Log LOG = LogFactory.getLog(Transaccion.class);
  
	private RegistroPrestamo prestamo;	

	public Transaccion(RegistroPrestamo prestamo) {
		super(new Incidente());
		this.prestamo = prestamo;
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
					siguiente= this.toSiguiente(sesion);
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
					if(this.prestamo.getPrestamo().getIdAfectaNomina().equals(1L)) {
						List<Incidente> incidentes= this.toLoadIncidente(deudoresDto.getIdEmpresaPersona());
            for (Incidente item: incidentes) {
              this.setIncidente(item);
  						super.ejecutar(sesion, EAccion.DESTRANSFORMACION);
            } // if
					} // if
					break;
				case MODIFICAR:
					deudoresDto= (TcKeetDeudoresDto)DaoFactory.getInstance().findById(TcKeetDeudoresDto.class, this.prestamo.getPrestamo().getIdDeudor());
          double diferencia= this.prestamo.getPrestamo().getImporte()- this.prestamo.getImporte();
					deudoresDto.setSaldo(deudoresDto.getSaldo()+ diferencia);
					deudoresDto.setDisponible(deudoresDto.getDisponible()- diferencia);
					DaoFactory.getInstance().update(sesion, deudoresDto);
					regresar= DaoFactory.getInstance().update(sesion, this.prestamo.getPrestamo())>= 1L;
					bitacoraDto= new TcKeetPrestamosBitacoraDto("SE MODIFICO EL ANTICIPO", this.prestamo.getPrestamo().getKey(), -1L, idUsuario, this.prestamo.getPrestamo().getIdPrestamoEstatus());
					regresar= DaoFactory.getInstance().insert(sesion, bitacoraDto)>= 1L;
          if(!Objects.equals(this.prestamo.getPrestamo().getIdDeudor(), this.prestamo.getIdDeudor()))
            DaoFactory.getInstance().deleteAll(sesion, TcKeetPrestamosLotesDto.class, this.prestamo.getPrestamo().toMap());
					if(Objects.equals(this.prestamo.getPrestamo().getIdAfectaNomina(), 2L)) {
            for (TcKeetPrestamosLotesDto item: this.prestamo.getLotes()) {
              item.setIdPrestamo(this.prestamo.getPrestamo().getIdPrestamo());
              if(!item.isValid())
                DaoFactory.getInstance().insert(sesion, item);
              int index= this.prestamo.getRegistros().indexOf(item.getIdPrestamoLote());
              if(index>= 0)
                this.prestamo.getRegistros().remove(index);
            } // for
            for (Long key: this.prestamo.getRegistros()) {
              DaoFactory.getInstance().delete(sesion, TcKeetPrestamosLotesDto.class, key);
            } // for
          } // if
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
          // IR A LOS INCIDENTES DE NOMINA QUE NO SE HAN COBRADO Y CANCELARLOS
					if(this.prestamo.getPrestamo().getIdAfectaNomina().equals(1L)) {
						List<Incidente> incidentes= this.toLoadPrestamosIncidente(sesion);
            if(incidentes!= null && !incidentes.isEmpty())
              for (Incidente item: incidentes) {
                item.setIdIncidenteEstatus(EEstatusIncidentes.CANCELADA.getIdEstatusInicidente());
                this.setIncidente(item);
                super.ejecutar(sesion, EAccion.ASIGNAR);
              } // if  
					} // if
					break;
				case SUBIR:					
					for (Documento dto : this.prestamo.getDocumentos()) {
						if (DaoFactory.getInstance().insert(sesion, dto) >= 1L) 
							this.toSaveFile(dto.getIdArchivo());
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

	private List<Incidente> toLoadPrestamosIncidente(Session sesion) throws Exception {
    List<Incidente> regresar  = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("idPrestamo", this.prestamo.getPrestamo().getIdPrestamo());
      regresar= (List<Incidente>)DaoFactory.getInstance().toEntitySet(sesion, Incidente.class, "TcManticIncidentesDto", "prestamos", params);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
    return regresar;  
  } 
  
	private List<Incidente> toLoadIncidente(Long idEmpresaPersona) throws Exception {
    List<Incidente> regresar= new ArrayList<>();
    double total  = this.prestamo.getPrestamo().getImporte();
    double importe= (int)(total / this.prestamo.getPrestamo().getSemanas());
    double residuo= this.prestamo.getPrestamo().getImporte() % this.prestamo.getPrestamo().getSemanas();
    double suma   = 0D;
    for(int x= 1; x<= this.prestamo.getPrestamo().getSemanas().intValue(); x++) {
      if(residuo!= 0D && x== this.prestamo.getPrestamo().getSemanas().intValue()) 
        importe= (int)(total- suma);
      else 
        suma+= importe; 
      Incidente item= new Incidente();
      //item.setCosto(importe);
      item.setCosto(this.prestamo.getPagos().get(x));
      item.setTipoIncidente(ETiposIncidentes.ABONO_NOMINA.name());
      item.setIdTipoIncidente(ETiposIncidentes.ABONO_NOMINA.getKey());
      item.setVigenciaInicio(LocalDate.now());
      item.setVigenciaFin(LocalDate.now());
      item.setIdIncidenteEstatus(EEstatusIncidentes.ACEPTADA.getIdEstatusInicidente());
      item.setIdEmpresaPersona(idEmpresaPersona);
      item.setIdPrestamo(this.prestamo.getPrestamo().getIdPrestamo());
      item.setObservaciones("FOLIO ("+ x+ ") PRESTAMO[".concat(this.prestamo.getPrestamo().getConsecutivo()).concat("] [").concat(Fecha.formatear(Fecha.FECHA_HORA_CORTA, this.prestamo.getPrestamo().getRegistro())).concat("]"));
      regresar.add(item);
    } // for
    return regresar;
	}
	
  public static void main(String ... args) {
    LOG.info(100.50% 4);   
    LOG.info((int)(100.50/ 4));   
    LOG.debug("ok");   
  }
  
}