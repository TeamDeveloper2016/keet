package mx.org.kaana.keet.prestamos.proveedor.reglas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.db.dto.TcKeetAnticiposBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetAnticiposLotesDto;
import mx.org.kaana.keet.prestamos.proveedor.beans.Documento;
import mx.org.kaana.keet.db.dto.TcKeetMorososDto;
import mx.org.kaana.keet.enums.ETiposIncidentes;
import mx.org.kaana.keet.prestamos.enums.EEstatusPrestamos;
import mx.org.kaana.keet.prestamos.proveedor.beans.RegistroAnticipo;
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
  
	private RegistroAnticipo prestamo;	

	public Transaccion(RegistroAnticipo prestamo) {
		super(new Incidente());
		this.prestamo = prestamo;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar             = true;
		Long idUsuario               = -1L;
		Siguiente siguiente          = null;
		TcKeetMorososDto deudoresDto = null;
		TcKeetAnticiposBitacoraDto bitacoraDto= null;
		try {
			idUsuario= JsfBase.getIdUsuario();
			switch(accion) {
				case AGREGAR:
					siguiente= this.toSiguiente(sesion);
					this.prestamo.getPrestamo().setConsecutivo(siguiente.getConsecutivo());
					this.prestamo.getPrestamo().setOrden(siguiente.getOrden());
					this.prestamo.getPrestamo().setEjercicio(Long.parseLong(String.valueOf(this.getCurrentYear())));
					this.prestamo.getPrestamo().setIdUsuario(idUsuario);
					this.prestamo.getPrestamo().setIdAnticipoEstatus(EEstatusPrestamos.INICIALIZADA.getIdEstatusPrestamo());
					this.prestamo.getPrestamo().setSaldo(this.prestamo.getPrestamo().getImporte());
					deudoresDto= (TcKeetMorososDto)DaoFactory.getInstance().findById(TcKeetMorososDto.class, this.prestamo.getPrestamo().getIdMoroso());
					deudoresDto.setSaldo(deudoresDto.getSaldo()+ this.prestamo.getPrestamo().getImporte());
					deudoresDto.setDisponible(deudoresDto.getDisponible()- this.prestamo.getPrestamo().getImporte());
					DaoFactory.getInstance().update(sesion, deudoresDto);
					regresar= DaoFactory.getInstance().insert(sesion, this.prestamo.getPrestamo())>= 1L;
					bitacoraDto= new TcKeetAnticiposBitacoraDto(this.prestamo.getPrestamo().getKey(), this.prestamo.getPrestamo().getIdAnticipoEstatus(), "", idUsuario, -1L);
					regresar= DaoFactory.getInstance().insert(sesion, bitacoraDto)>= 1L;
					if(this.prestamo.getPrestamo().getIdAfectaNomina().equals(1L)) {
						List<Incidente> incidentes= this.toLoadIncidente(deudoresDto.getIdProveedor());
            for (Incidente item: incidentes) {
              this.setIncidente(item);
  						super.ejecutar(sesion, EAccion.COMPLEMENTAR);
            } // if
					} // if
          else {
            for (TcKeetAnticiposLotesDto item: this.prestamo.getLotes()) {
              if(item.isValid())
                DaoFactory.getInstance().update(sesion, item);
              else
                DaoFactory.getInstance().insert(sesion, item);
            } // for
          } // if
					break;
				case DESACTIVAR:
					this.prestamo.getPrestamo().setIdAnticipoEstatus(EEstatusPrestamos.CANCELADA.getIdEstatusPrestamo()); // CANCELADA
					deudoresDto= (TcKeetMorososDto)DaoFactory.getInstance().findById(TcKeetMorososDto.class, this.prestamo.getPrestamo().getIdMoroso());
					deudoresDto.setSaldo(deudoresDto.getSaldo()- this.prestamo.getPrestamo().getSaldo());
					deudoresDto.setDisponible(deudoresDto.getDisponible()+ this.prestamo.getPrestamo().getSaldo());
					DaoFactory.getInstance().update(sesion, deudoresDto);
					regresar= DaoFactory.getInstance().update(sesion, this.prestamo.getPrestamo())>= 1L;
					bitacoraDto= new TcKeetAnticiposBitacoraDto(this.prestamo.getPrestamo().getKey(), this.prestamo.getPrestamo().getIdAnticipoEstatus(), "", idUsuario, -1L);
					regresar= DaoFactory.getInstance().insert(sesion, bitacoraDto)>= 1L;
          // ELIMINAR TODOS LOS ANTICIPOS DE PAGOS DE LOS LOTES QUE FUERON SELECCIONADOS 
          DaoFactory.getInstance().deleteAll(sesion, TcKeetAnticiposLotesDto.class, this.prestamo.getPrestamo().toMap());
          // IR A LOS INCIDENTES DE NOMINA QUE NO SE HAN COBRADO Y CANCELARLOS
					if(this.prestamo.getPrestamo().getIdAfectaNomina().equals(1L)) {
						List<Incidente> incidentes= this.toLoadPrestamosIncidente(sesion);
            if(incidentes!= null && !incidentes.isEmpty())
              for (Incidente item: incidentes) {
                item.setIdIncidenteEstatus(EEstatusIncidentes.CANCELADA.getIdEstatusInicidente());
                this.setIncidente(item);
                super.ejecutar(sesion, EAccion.MOVIMIENTOS);
              } // if  
					} // if
					break;
				case SUBIR:					
					for (Documento dto: this.prestamo.getDocumentos()) {
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
			Value next= DaoFactory.getInstance().toField(sesion, "TcKeetAnticiposDto", "siguiente", params, "siguiente");
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
			params.put("idAnticipo", this.prestamo.getPrestamo().getIdAnticipo());
      regresar= (List<Incidente>)DaoFactory.getInstance().toEntitySet(sesion, Incidente.class, "TcKeetIncidentesDto", "prestamos", params);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
    return regresar;  
  } 
  
	private List<Incidente> toLoadIncidente(Long idProveedor) throws Exception {
    List<Incidente> regresar= new ArrayList<>();
    double total  = this.prestamo.getPrestamo().getImporte();
    double importe= (int)(total/ this.prestamo.getPrestamo().getSemanas());
    double residuo= this.prestamo.getPrestamo().getImporte()% this.prestamo.getPrestamo().getSemanas();
    double suma   = 0D;
    for(int x= 1; x<= this.prestamo.getPrestamo().getSemanas().intValue(); x++) {
      if(residuo!= 0D && x== this.prestamo.getPrestamo().getSemanas().intValue()) 
        importe= (int)(total- suma);
      else 
        suma+= importe; 
      Incidente item= new Incidente();
      //item.setCosto(importe);
      // SE TOMA EL IMPORTE QUE SE DEJO EN LA LISTA DE PAGOS POR SEMANA
      item.setCosto(this.prestamo.getPagos().get(x));
      item.setTipoIncidente(ETiposIncidentes.ABONO_NOMINA.name());
      item.setIdTipoIncidente(ETiposIncidentes.ABONO_NOMINA.getKey());
      item.setVigenciaInicio(LocalDate.now());
      item.setVigenciaFin(LocalDate.now());
      item.setIdIncidenteEstatus(EEstatusIncidentes.ACEPTADA.getIdEstatusInicidente());
      item.setIdEmpresaPersona(idProveedor);
      item.setIdPrestamo(this.prestamo.getPrestamo().getIdAnticipo());
      item.setObservaciones("FOLIO ("+ x+ ") ANTICIPO[".concat(this.prestamo.getPrestamo().getConsecutivo()).concat("] [").concat(Fecha.formatear(Fecha.FECHA_HORA_CORTA, this.prestamo.getPrestamo().getRegistro())).concat("]"));
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