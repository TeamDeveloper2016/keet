package mx.org.kaana.keet.cajachica.reglas;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.keet.cajachica.beans.ArchivoGasto;
import mx.org.kaana.keet.cajachica.beans.Gasto;
import mx.org.kaana.keet.db.dto.TcKeetCajasChicasCierresBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetCajasChicasCierresDto;
import mx.org.kaana.keet.db.dto.TcKeetCajasChicasDto;
import mx.org.kaana.keet.db.dto.TcKeetGastosBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetGastosDetallesDto;
import mx.org.kaana.keet.db.dto.TcKeetGastosDto;
import mx.org.kaana.keet.enums.EEstatusCajasChicas;
import mx.org.kaana.keet.enums.EEstatusGastos;
import mx.org.kaana.keet.enums.ETiposIncidentes;
import mx.org.kaana.keet.nomina.reglas.Semanas;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Encriptar;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Variables;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.wassenger.Cafu;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesDto;
import mx.org.kaana.mantic.enums.EEstatusIncidentes;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.enums.ETiposContactos;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private static final Log LOG= LogFactory.getLog(Transaccion.class);	
	private List<ArchivoGasto> documentos;
	private Long idGasto;
	private Gasto gasto;
	private boolean ok;
	
	private Long idCajaChicaCierre;
	private Double cantidad;
	private String observaciones;
	private Long idAfectaNomina;
	private Long idDesarrollo;
	private Long idEmpresaPersona;

  public Transaccion(Long idCajaChicaCierre, Long idAfectaNomina, String observaciones) {
    this(-1L, Boolean.FALSE);
    this.idCajaChicaCierre= idCajaChicaCierre;
    this.idAfectaNomina= idAfectaNomina;
    this.observaciones = observaciones;
  }
  
	public Transaccion(Long idGasto, boolean ok) {
		this(idGasto, null, ok);
	}
	
	public Transaccion(Gasto gasto, List<ArchivoGasto> documentos) {
		this(-1L, gasto);
    this.documentos= documentos;
	}	
	
	public Transaccion(Long idGasto, Gasto gasto) {
		this.idGasto= idGasto;
		this.gasto  = gasto;
	}	

	public Transaccion(Long idGasto, Gasto gasto, boolean ok) {
		this.idGasto= idGasto;
		this.gasto  = gasto;
		this.ok     = ok;
	}	

	public Transaccion(Long idCajaChicaCierre, Double cantidad, String observaciones, Long idDesarrollo) {
		this(idCajaChicaCierre, cantidad, observaciones, 2L, idDesarrollo, -1L);
	}
	
	public Transaccion(Long idCajaChicaCierre, Double cantidad, String observaciones, Long idAfectaNomina, Long idDesarrollo, Long idEmpresaPersona) {
		this.idCajaChicaCierre= idCajaChicaCierre;
		this.cantidad         = cantidad;
		this.observaciones    = observaciones;
		this.idAfectaNomina   = idAfectaNomina;
		this.idDesarrollo     = idDesarrollo;
		this.idEmpresaPersona = idEmpresaPersona;
	}
  
	public Transaccion(List<ArchivoGasto> documentos) {
		this.documentos = documentos;
	}	
	
	public Long getIdGasto() {
		return idGasto;
	}	

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar         = true;
		Map<String, Object>params= null;		
		try {
			switch(accion) {
				case AGREGAR:												
					regresar= this.procesarGasto(sesion);					
					break;			
				case MODIFICAR:
					regresar= this.confirmarGasto(sesion);		
					break;								
				case SUBIR:
					for(ArchivoGasto item: this.documentos) {
						if(DaoFactory.getInstance().insert(sesion, item)>= 1L)
							this.toSaveFile(item.getIdArchivo());
					} // for
					break;
				case ACTIVAR:
					regresar= this.realizarCierre(sesion);
					break;
				case ASIGNAR:
					regresar= this.abonarCaja(sesion);
					break;
				case REGISTRAR:
					regresar= this.revisarGasto(sesion);
					break;
				case PROCESAR:
					regresar= this.procesarCierre(sesion);
					break;
				case MOVIMIENTOS:
					regresar= this.message(sesion, this.idCajaChicaCierre);
					break;
			} // switch
		} // try
		catch (Exception e) {			
			throw new Exception((e!= null? e.getCause().toString(): ""));
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}	// ejecutar	
	
	private boolean procesarGasto(Session sesion) throws Exception {
		boolean regresar        = false;		
		TcKeetGastosDto gastoDto= null;		
		Siguiente siguiente     = null;		
		try {
			if(this.gasto.getIdGasto() <= 0L) {
				siguiente= this.toSiguiente(sesion);			
				gastoDto = this.toLoadGasto(siguiente);		
				this.idGasto= DaoFactory.getInstance().insert(sesion, gastoDto);
			} // if
      else {
				gastoDto= (TcKeetGastosDto) DaoFactory.getInstance().findById(sesion, TcKeetGastosDto.class, this.gasto.getIdGasto());
				gastoDto.setArticulos(this.gasto.getTotalArticulos()-1L);
				gastoDto.setImporte(toImporte());
				DaoFactory.getInstance().update(sesion, gastoDto);
				this.idGasto= this.gasto.getIdGasto();
			} // else							
			if(this.idGasto>= 1L) {
				if(registrarBitacora(sesion, EEstatusGastos.DISPONIBLE.getKey()))
					regresar= this.registrarDetalle(sesion);													
			} // if
      this.processFiles(sesion, gastoDto);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // procesarVale
	
	private TcKeetGastosDto toLoadGasto(Siguiente siguiente) throws Exception {
		TcKeetGastosDto regresar= null;				
		try {
			regresar= new TcKeetGastosDto();						
			regresar.setIdAbono(2L);
			regresar.setEjercicio(Long.valueOf(this.getCurrentYear()));
			regresar.setConsecutivo(siguiente.getConsecutivo());
			regresar.setOrden(siguiente.getOrden());			
			regresar.setIdUsuario(JsfBase.getIdUsuario());	
			regresar.setArticulos(this.gasto.getTotalArticulos()-1L);
			regresar.setIdCajaChicaCierre(this.gasto.getIdCajaChicaCierre());
			regresar.setIdTipoMedioPago(this.gasto.getIdTipoMedioPago());
			regresar.setIdEmpresa(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			regresar.setIdEmpresaPersona(JsfBase.getAutentifica().getPersona().getIdEmpresaPersona());
			regresar.setImporte(this.toImporte());
			regresar.setIdGastoEstatus(EEstatusGastos.DISPONIBLE.getKey());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
		return regresar;
	} // toLoadGasto
	
	private Double toImporte() {
		Double regresar= 0D;
		for(Articulo articulo: this.gasto.getArticulos())
			regresar= regresar + articulo.getImporte();
		return regresar;
	} // toImporte
	
	private Double toArticulos() {
		Double regresar= 0D;
		for(Articulo articulo: this.gasto.getArticulos())
			regresar= regresar + articulo.getCantidad();
		return regresar;
	} // toImporte
	
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());			
			Value next= DaoFactory.getInstance().toField(sesion, "TcKeetGastosDto", "siguiente", params, "siguiente");
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
	
	private Siguiente toSiguienteCierre(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());			
			Value next= DaoFactory.getInstance().toField(sesion, "TcKeetCajasChicasCierresDto", "siguiente", params, "siguiente");
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
	
	private boolean registrarBitacora(Session sesion, Long idEstatus) throws Exception {
		boolean regresar               = false;
		TcKeetGastosBitacoraDto bitacora= null;
		try {
			bitacora= new TcKeetGastosBitacoraDto();
			bitacora.setIdUsuario(JsfBase.getIdUsuario());
			bitacora.setIdGasto(this.idGasto);
			bitacora.setIdGastoEstatus(idEstatus);
			bitacora.setJustificacion("REGISTRO EN BITACORA: " + EEstatusGastos.fromId(idEstatus).getNombre());
			regresar= DaoFactory.getInstance().insert(sesion, bitacora)>= 1L;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarBitacora
	
	private boolean registrarDetalle(Session sesion) throws Exception {
		boolean regresar               = true;
		TcKeetGastosDetallesDto detalle= null;
		try {
			if(this.gasto.getIdGasto() >= 1L)
				DaoFactory.getInstance().execute(ESql.DELETE, sesion, "TcKeetGastosDetallesDto", "rows", Variables.toMap("idGasto~".concat(this.idGasto.toString())));			
			for(Articulo recordDetalle: this.gasto.getArticulos()) {
				if(recordDetalle.isValid()) {
					detalle= new TcKeetGastosDetallesDto();										
					detalle.setIdGasto(this.idGasto);
					detalle.setIdArticulo(recordDetalle.getIdArticulo());				
					detalle.setCodigo(recordDetalle.getPropio());
					detalle.setNombre(recordDetalle.getNombre().toUpperCase());
					detalle.setCantidad(recordDetalle.getCantidad());
					detalle.setUnidadMedida(recordDetalle.getUnidadMedida());
					detalle.setCosto(recordDetalle.getCosto());															
					detalle.setIva(recordDetalle.getIva());
					detalle.setImpuestos(recordDetalle.getImpuestos());					
					detalle.setSubTotal(recordDetalle.getSubTotal());					
					detalle.setImporte(recordDetalle.getImporte());					
					DaoFactory.getInstance().insert(sesion, detalle);	
				} // if
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarDetalle		
	
	private boolean confirmarGasto(Session sesion) throws Exception {
		boolean regresar    = false;
		TcKeetGastosDto item= null;
		try {
			item= (TcKeetGastosDto) DaoFactory.getInstance().findById(sesion, TcKeetGastosDto.class, this.idGasto);
			if(this.ok) {
				item.setIdGastoEstatus(EEstatusGastos.ACEPTADO.getKey());
				if(DaoFactory.getInstance().update(sesion, item)>= 1L) {
					if(registrarBitacora(sesion, EEstatusGastos.ACEPTADO.getKey()))
						regresar= this.afectarCaja(sesion, item.getIdCajaChicaCierre(), item.getImporte());
				} // if
			} // if
      else {
				item.setIdGastoEstatus(EEstatusGastos.CANCELADO.getKey());
				if(DaoFactory.getInstance().update(sesion, item)>= 1L) {
					if(this.registrarBitacora(sesion, EEstatusGastos.CANCELADO.getKey()));
            regresar= this.afectarCaja(sesion, item.getIdCajaChicaCierre(), item.getImporte()* -1D);
        } // if  
			} // else
      this.processFiles(sesion, item);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch	
		return regresar;
	} // confirmarGasto
	
	private boolean afectarCaja(Session sesion, Long idCaja, Double importe) throws Exception {
		boolean regresar                = false;
		TcKeetCajasChicasCierresDto caja= null; 
		try {
			caja= (TcKeetCajasChicasCierresDto) DaoFactory.getInstance().findById(sesion, TcKeetCajasChicasCierresDto.class, idCaja);
			caja.setAcumulado(caja.getAcumulado()+ importe);
			caja.setDisponible(caja.getDisponible()- importe);			
			caja.setIdCajaChicaCierreEstatus(EEstatusCajasChicas.PARCIALIZADO.getKey());
			if(DaoFactory.getInstance().update(sesion, caja)>= 1L)
				regresar= registrarBitacoraCaja(sesion, idCaja, EEstatusCajasChicas.PARCIALIZADO.getKey());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} //afectarCaja
	
	private boolean registrarBitacoraCaja(Session sesion, Long idCajaCierre, Long idEstatus) throws Exception {
		boolean regresar= false;
		TcKeetCajasChicasCierresBitacoraDto bitacora= null;
		try {
			bitacora= new TcKeetCajasChicasCierresBitacoraDto();
			bitacora.setIdCajaChicaCierre(idCajaCierre);
			bitacora.setIdCajaChicaCierreEstatus(idEstatus);
			bitacora.setIdUsuario(JsfBase.getIdUsuario());
			bitacora.setJustificacion("REGISTRO DE GASTO");
			regresar= DaoFactory.getInstance().insert(sesion, bitacora)>= 1L;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarBitacoraCaja
	
	private boolean abonarCaja(Session sesion) throws Exception {
		boolean regresar                  = false;
		TcKeetCajasChicasCierresDto cierre= null;
		TcKeetGastosDto abono             = null;
		Siguiente siguiente               = null;
		try {
			cierre= (TcKeetCajasChicasCierresDto) DaoFactory.getInstance().findById(sesion, TcKeetCajasChicasCierresDto.class, this.idCajaChicaCierre);
			cierre.setAcumulado(cierre.getAcumulado() - this.cantidad);			
			cierre.setDisponible(cierre.getDisponible() - this.cantidad);			
			if(DaoFactory.getInstance().update(sesion, cierre)>= 1L) {
				if(registrarBitacoraCaja(sesion, this.idCajaChicaCierre, EEstatusCajasChicas.PARCIALIZADO.getKey())) {
					siguiente= toSiguiente(sesion);
					abono= loadAbono(siguiente);
					this.idGasto= DaoFactory.getInstance().insert(sesion, abono);
					if(this.idGasto>= 1L) {
						if(registrarBitacora(sesion, EEstatusGastos.ACEPTADO.getKey())) {
							regresar= registraDetalleAbono(sesion);
						} // if
					} // if
				} // if
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // abonarCaja
	
	private TcKeetGastosDto loadAbono(Siguiente siguiente) throws Exception {
		TcKeetGastosDto regresar= null;				
		try {
			regresar= new TcKeetGastosDto();						
			regresar.setIdAbono(1L);
			regresar.setEjercicio(Long.valueOf(this.getCurrentYear()));
			regresar.setConsecutivo(siguiente.getConsecutivo());
			regresar.setOrden(siguiente.getOrden());			
			regresar.setIdUsuario(JsfBase.getIdUsuario());	
			regresar.setArticulos(1L);
			regresar.setIdCajaChicaCierre(this.idCajaChicaCierre);
			regresar.setIdEmpresa(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			regresar.setIdEmpresaPersona(JsfBase.getAutentifica().getPersona().getIdEmpresaPersona());
			regresar.setImporte(this.cantidad);
			regresar.setIdGastoEstatus(EEstatusGastos.ACEPTADO.getKey());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
		return regresar;
	} // loadAbono
	
	private boolean registraDetalleAbono(Session sesion) throws Exception {		
		boolean regresar               = false;
		TcKeetGastosDetallesDto detalle= null;
		try {			
			detalle= new TcKeetGastosDetallesDto();										
			detalle.setIdGasto(this.idGasto);			
			detalle.setCodigo("ABONO");
			detalle.setNombre(this.observaciones);
			detalle.setCantidad(1D);
			detalle.setUnidadMedida("");
			detalle.setCosto(0D);															
			detalle.setIva(0D);
			detalle.setImpuestos(0D);					
			detalle.setSubTotal(0D);					
			detalle.setImporte(this.cantidad);					
			regresar= DaoFactory.getInstance().insert(sesion, detalle)>= 1L;					
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registraDetalleAbono		
	
	private boolean realizarCierre(Session sesion) throws Exception {
		boolean regresar                  = false;
		TcKeetCajasChicasCierresDto cierre= null;
		TcKeetCajasChicasCierresDto nuevo= null;
		try {
			cierre= (TcKeetCajasChicasCierresDto) DaoFactory.getInstance().findById(sesion, TcKeetCajasChicasCierresDto.class, this.idCajaChicaCierre);
			cierre.setIdCajaChicaCierreEstatus(EEstatusCajasChicas.TERMINADO.getKey());
			cierre.setTermino(LocalDateTime.now());
			if(DaoFactory.getInstance().update(sesion, cierre)>= 1L) {
				if(registrarBitacoraCaja(sesion, cierre.getIdCajaChicaCierre(), EEstatusCajasChicas.TERMINADO.getKey())) {
					if(this.idAfectaNomina.equals(1L)) {
						this.registrarInicidenciaNomina(sesion);
					} // if
					//if(cierre.getDisponible()<0)
						//registrarInicidenciaGasto(sesion, JsfBase.getAutentifica().getPersona().getIdEmpresaPersona(), cierre.getDisponible());					
					nuevo= this.toLoadNuevoCierre(sesion, cierre.getIdCajaChica(), cierre.getDisponible(), cierre.getIdNominaPeriodo());
					if(DaoFactory.getInstance().insert(sesion, nuevo)>= 1L) {
						if(registrarBitacoraCaja(sesion, nuevo.getIdCajaChicaCierre(), EEstatusCajasChicas.INICIADO.getKey())) {
							regresar= this.actualizarGastos(sesion, cierre.getIdCajaChicaCierre(), nuevo.getIdCajaChicaCierre());
						} // if
					} // if
				} // if
			} // if 
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // realizarCierre
	
	private TcKeetCajasChicasCierresDto toLoadNuevoCierre(Session sesion, Long idCajaChica, Double disponible, Long idNominaPeriodo) throws Exception {
		TcKeetCajasChicasCierresDto regresar= null;
		Siguiente siguiente                 = null;
		Semanas semana                      = null;
		try {
			siguiente= this.toSiguienteCierre(sesion);
			regresar= new TcKeetCajasChicasCierresDto();
			regresar.setIdCajaChica(idCajaChica);
			regresar.setEjercicio(Long.valueOf(Fecha.getAnioActual()));
			regresar.setOrden(siguiente.getOrden());
			regresar.setConsecutivo(siguiente.getConsecutivo());
			regresar.setIdCajaChicaCierreEstatus(EEstatusCajasChicas.INICIADO.getKey());
			regresar.setTermino(LocalDateTime.now());
			regresar.setObservaciones(this.observaciones);
			regresar.setAcumulado(0D);
			regresar.setDisponible(this.cantidad+ disponible);
			regresar.setSaldo(this.cantidad+ disponible);
			regresar.setIdUsuario(JsfBase.getIdUsuario());
			regresar.setIdAfectaNomina(this.idAfectaNomina);
			regresar.setNomina(this.cantidad);
			regresar.setIdEmpresaPersona(this.idEmpresaPersona);
			semana= new Semanas();
			regresar.setIdNominaPeriodo(semana.getSemanaSiguiente(idNominaPeriodo).getIdNominaPeriodo());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadCierre
	
	private boolean actualizarGastos(Session sesion, Long idCajaAnterior, Long idCajaNuevo) throws Exception {
		boolean regresar         = false;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idCajaAnterior", idCajaAnterior);
			params.put("idCajaNuevo", idCajaNuevo);
			DaoFactory.getInstance().execute(ESql.UPDATE, sesion, "TcKeetGastosDto", "aplicado", params);
			regresar= DaoFactory.getInstance().execute(ESql.UPDATE, sesion, "TcKeetGastosDto", "disponibles", params)> -1L;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		return regresar;
	} // actualizarGastos
	
	private void registrarInicidenciaNomina(Session sesion) throws Exception {		
		TcManticIncidentesDto dto= null;
		Long key                 = -1L;
		Siguiente consecutivo    = null;
		try {
			dto= new TcManticIncidentesDto();
			consecutivo= this.toSiguienteIncidente(sesion);			
			dto.setConsecutivo(consecutivo.getConsecutivo());			
			dto.setOrden(consecutivo.getOrden());			
			dto.setEjercicio(Long.valueOf(Fecha.getAnioActual()));						
			dto.setIdIncidenteEstatus(EEstatusIncidentes.CAPTURADA.getIdEstatusInicidente());						
			dto.setIdDesarrollo(this.idDesarrollo);
			dto.setIdEmpresaPersona(JsfBase.getAutentifica().getPersona().getIdEmpresaPersona());			
			dto.setCosto(this.cantidad);
			dto.setIdTipoIncidente(ETiposIncidentes.APERTURA_CAJA.getKey());	
			dto.setIdUsuario(JsfBase.getIdUsuario());
			dto.setObservaciones(this.observaciones);
			dto.setInicio(LocalDate.now());
			dto.setTermino(LocalDate.now());		
			key= DaoFactory.getInstance().insert(sesion, dto);
			if(key>= 1L)
				registrarBitacoraIncidente(sesion, key, EEstatusIncidentes.CAPTURADA.getIdEstatusInicidente());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // registrarInicidenciaNomina
	
	private void registrarInicidenciaGasto(Session sesion, Long idEmpresaPersona, Double costo) throws Exception {
		TcManticIncidentesDto dto= null;
		Long key                 = -1L;
		Siguiente consecutivo    = null;
		try {
			dto= new TcManticIncidentesDto();
			consecutivo= this.toSiguienteIncidente(sesion);			
			dto.setConsecutivo(consecutivo.getConsecutivo());			
			dto.setOrden(consecutivo.getOrden());			
			dto.setEjercicio(Long.valueOf(Fecha.getAnioActual()));						
			dto.setIdIncidenteEstatus(EEstatusIncidentes.CAPTURADA.getIdEstatusInicidente());						
			dto.setIdDesarrollo(this.idDesarrollo);
			dto.setIdEmpresaPersona(idEmpresaPersona);			
			dto.setCosto(costo);
			dto.setIdTipoIncidente(ETiposIncidentes.SALDO_CAJA.getKey());	
			dto.setIdUsuario(JsfBase.getIdUsuario());
			dto.setObservaciones(this.observaciones);
			dto.setInicio(LocalDate.now());
			dto.setTermino(LocalDate.now());		
			key= DaoFactory.getInstance().insert(sesion, dto);
			if(key>= 1L)
				this.registrarBitacoraIncidente(sesion, key, EEstatusIncidentes.CAPTURADA.getIdEstatusInicidente());			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // registrarInicidenciaNomina
	
	private Siguiente toSiguienteIncidente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());			
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticIncidentesDto", "siguiente", params, "siguiente");
			if(next.getData()!= null)
				regresar= new Siguiente(next.toLong());
			else
				regresar= new Siguiente(Configuracion.getInstance().isEtapaDesarrollo()? 900001L: 1L); 
		} // try		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toSiguiente
	
	private boolean registrarBitacoraIncidente(Session sesion, Long idIncidente, Long idEstatus) throws Exception {
		boolean regresar                 = false;
		TcManticIncidentesBitacoraDto dto= null;
		try {
			dto= new TcManticIncidentesBitacoraDto();
			dto.setIdIncidente(idIncidente);
			dto.setIdIncidenteEstatus(idEstatus);
			dto.setIdUsuario(JsfBase.getIdUsuario());
			dto.setJustificacion(this.observaciones);
			regresar= DaoFactory.getInstance().insert(sesion, dto)>= 1L;
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		return regresar;
	} // registrarBitacora
	
	private boolean revisarGasto(Session sesion) throws Exception {
		boolean regresar        = false;
		TcKeetGastosDto gastoDto= null;
		try {
			gastoDto= (TcKeetGastosDto) DaoFactory.getInstance().findById(sesion, TcKeetGastosDto.class, this.idGasto);
      if(Objects.equals(gastoDto.getIdGastoEstatus(), EEstatusIncidentes.CAPTURADA.getIdEstatusInicidente()))
        gastoDto.setIdGastoEstatus(EEstatusIncidentes.ACEPTADA.getIdEstatusInicidente());
			gastoDto.setRevisado(1L);
			regresar= DaoFactory.getInstance().update(sesion, gastoDto)>= 1L;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // revisarGasto
  
  public boolean procesarCierre(Session sesion) throws Exception {
   boolean regresar           = Boolean.FALSE;
    Map<String, Object> params= null;
    try {      
      params = new HashMap<>();      
      params.put(Constantes.SQL_CONDICION, "id_activa= 1");      
      List<TcKeetCajasChicasDto> cajasChicas= (List<TcKeetCajasChicasDto>)DaoFactory.getInstance().toEntitySet(sesion, TcKeetCajasChicasDto.class, "TcKeetCajasChicasDto", params);
      for (TcKeetCajasChicasDto item: cajasChicas) {
        params.put("idCajaChica", item.getIdCajaChica());
        params.put("idNominaPeriodo", this.idCajaChicaCierre);
			  TcKeetCajasChicasCierresDto activa= (TcKeetCajasChicasCierresDto)DaoFactory.getInstance().toEntity(sesion, TcKeetCajasChicasCierresDto.class, "TcKeetCajasChicasCierresDto", "ultima", params);
        if(activa!= null) {
          activa.setIdCajaChicaCierreEstatus(EEstatusCajasChicas.TERMINADO.getKey());
          activa.setTermino(LocalDateTime.now());
          if(DaoFactory.getInstance().update(sesion, activa)>= 1L) 
            this.registrarBitacoraCaja(sesion, activa.getIdCajaChicaCierre(), EEstatusCajasChicas.TERMINADO.getKey());
        } // if
        else {
          activa= new TcKeetCajasChicasCierresDto();
          activa.setIdCajaChica(item.getIdCajaChica());
          activa.setDisponible(0D);
        } // else  
        this.idEmpresaPersona= JsfBase.getAutentifica().getPersona().getIdEmpresaPersona();
        this.cantidad        = 0D;
        TcKeetCajasChicasCierresDto nuevo= this.toLoadNuevoCierre(sesion, activa.getIdCajaChica(), activa.getSaldo(), this.idCajaChicaCierre);
        if(DaoFactory.getInstance().insert(sesion, nuevo)>= 1L) {
          this.registrarBitacoraCaja(sesion, nuevo.getIdCajaChicaCierre(), EEstatusCajasChicas.INICIADO.getKey());
          if(activa.isValid()) {
            this.actualizarGastos(sesion, activa.getIdCajaChicaCierre(), nuevo.getIdCajaChicaCierre());
            if(Objects.equals(this.idAfectaNomina, 1L)) {
              this.idDesarrollo= item.getIdDesarrollo();
              params.put("idNominaPeriodo", activa.getIdNominaPeriodo());      
              params.put(Constantes.SQL_CONDICION, "tc_keet_cajas_chicas.id_desarrollo= "+ this.idDesarrollo);      
              params.put("sortOrder", "");      
              List<Entity> residentes= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "VistaCierresCajasChicasDto", "residentes", params);
              if(residentes!= null && !residentes.isEmpty())
                for (Entity residente: residentes) {
                  if(residente.toLong("idEmpresaPersona")!= null)
                    this.registrarInicidenciaGasto(sesion, residente.toLong("idEmpresaPersona"), residente.toDouble("total"));
                } // for
            } // if
          } // if  
          regresar= Boolean.TRUE;
        } // if
      } // for
      this.message(sesion, this.idCajaChicaCierre);
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
 
  private void processFiles(Session sesion, TcKeetGastosDto dto) throws Exception {
    String pattern= "/".concat(Cadena.rellenar("9", 4, '9', true)).concat(Cadena.rellenar("0", 6, '0', true)).concat("/");
    try {
      if(this.documentos!= null && !this.documentos.isEmpty()) {
        String path= Configuracion.getInstance().getPropiedadSistemaServidor("gastos").concat(this.documentos.get(0).getRuta().replace(pattern, "/".concat(dto.getConsecutivo()).concat("/")));
        File file  = new File(path);		
        if (!file.exists())
          file.mkdirs();
        for(ArchivoGasto item: this.documentos) {
          item.setIdGasto(dto.getIdGasto());
          item.setConsecutivo(dto.getConsecutivo());
          item.setImporte(dto.getImporte());
          item.setArticulos(dto.getArticulos());
          item.setRuta(item.getRuta().replace(pattern, "/".concat(dto.getConsecutivo()).concat("/")));
          String old= item.getAlias();
          item.setAlias(item.getAlias().replace(pattern, "/".concat(dto.getConsecutivo()).concat("/")));
          Archivo.copyDeleteSource(old, item.getAlias(), true);
          if(DaoFactory.getInstance().insert(sesion, item)>= 1L)
            this.toSaveFile(item.getIdArchivo());
        } // for
      } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }

  public String toReporte(Session sesion, Reporte jasper, Entity figura, Long idNominaPeriodo) throws Exception {    
    String regresar              = null;
		Map<String, Object>parametros= null;
		EReportes seleccion          = EReportes.CAJA_CHICA;    
    Map<String, Object>params    = null;
    Parametros comunes           = null;
    try {
      params = new HashMap<>();  
      comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      parametros= comunes.getComunes();
      parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getNombre().toUpperCase());
      parametros.put("REPORTE_TITULO", seleccion.getTitulo());
      parametros.put("NOMBRE_REPORTE", seleccion.getTitulo());
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));
      parametros.put("REPORTE_FIGURA", "CORTE GENERAL");
      parametros.put("REPORTE_DEPARTAMENTO", JsfBase.getAutentifica().getPersona().getNombreCompleto());
      parametros.put("REPORTE_PERIODO", figura.toString("inicio")+ " al "+ figura.toString("termino"));
      params.put("idNominaPeriodo", idNominaPeriodo);
      jasper.toAsignarReporte(new ParametrosReporte(seleccion, params, parametros));		
      regresar= jasper.getAlias();
      String name= JsfBase.getRealPath(jasper.getNombre());
      File file= new File(name);
      if(!file.exists())
        jasper.toProcess(sesion);
      LOG.info("Reporte generado: "+ name);
    } // try
    catch(Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);			
    } // catch	
    return regresar;
  } // toReporte 	  
  
  private boolean message(Session sesion, Long idNominaPeriodo) throws Exception {
    boolean regresar                = false;
    List<Columna> columns           = null;		
    Map<String, Object> params      = new HashMap<>();
    Map<String, Object> administradores= new HashMap<>();
    Reporte jasper                  = null; 
		try {
      columns= new ArrayList<>();      
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));                  
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));    
      columns.add(new Columna("total", EFormatoDinamicos.MONEDA_CON_DECIMALES));
      params.put("idNominaPeriodo", idNominaPeriodo);
      params.put("sortOrder", "order by tr_mantic_empresa_personal.id_empresa_persona");
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);      
      List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "VistaCierresCajasChicasDto", "residentes", params);
      if(items!= null && !items.isEmpty()) {
        UIBackingUtilities.toFormatEntitySet(items, columns);
        jasper= new Reporte();	
        jasper.init();      
        for (Entity item: items) {
          params.put(Constantes.SQL_CONDICION, "id_persona="+ item.toLong("idPersona"));
          List<Entity> celulares= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "TrManticPersonaTipoContactoDto", "row", params);
          String celular= null;
          if(celulares!= null && !celulares.isEmpty())
            for (Entity telefono: celulares) {
              if(Objects.equals(telefono.toLong("idPreferido"), 1L) && (Objects.equals(telefono.toLong("idTipoContacto"), ETiposContactos.CELULAR.getKey()) || Objects.equals(telefono.toLong("idTipoContacto"), ETiposContactos.CELULAR_NEGOCIO.getKey()) || Objects.equals(telefono.toLong("idTipoContacto"), ETiposContactos.CELULAR_PERSONAL.getKey()))) 
                celular= telefono.toString("valor");
            } // for
          this.toSendMessage(sesion, celular, item);
        } // for
        List<Entity> rows= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "VistaCierresCajasChicasDto", "administradores", params);
        for (Entity item: rows) 
          administradores.put(item.toString("persona"), item.toString("valor"));
        // NOTIFICAR A TODOS LOS ADMINISTRADORES CON LOS REPORTES GENERADOS DE LOS RESIDENTES
        this.toNotificarAdministradores(sesion, jasper, administradores, items.get(0), idNominaPeriodo);
      } // if  
      regresar= true;
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(columns);
      jasper= null;
    } // finally
    return regresar;
  }

	public String toSendMessage(Session sesion, String residente, Entity sujeto) {		
    String regresar= "";
		Cafu notificar = null;
		try {
      if(residente!= null) {
        notificar= new Cafu(
          sujeto.toString("residente"), 
          residente, 
          sujeto.toString("total"), 
          sujeto.toString("nomina"), 
          "*"+ sujeto.toString("inicio")+ "* al *"+ sujeto.toString("termino")+ "*"
        );
        LOG.info("Enviando mensaje por whatsapp al celular: "+ residente);
        notificar.doSendGasto(sesion);
        if(residente.length()<= 0)
          JsfBase.addMessage("No se selecciono ningún celular, por favor verifiquelo e intente de nueva cuenta.", ETipoMensaje.ALERTA);
      } // if  
		} // try
		catch(Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
    return regresar;
	} // toSendWessage

	public void toNotificarAdministradores(Session sesion, Reporte jasper, Map<String, Object> administradores, Entity sujeto, Long idNominaPeriodo) {		
		Cafu notificar = null;
		try {
      // CAMBIAR POR UNA COLECCION CON EL NOMBRE DEL RESIENTE Y SU CELULAR
      if(administradores!= null && !administradores.isEmpty()) {
        Encriptar encriptar= new Encriptar();
  			String nombre= this.toReporte(sesion, jasper, sujeto, idNominaPeriodo);
        administradores.put("Alejandro Jiménez García", encriptar.desencriptar("cd4b3e3924191b057b8187"));
        switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
          case "cafu":
            administradores.put("Carlos Alberto Calderon Solano", encriptar.desencriptar("dc58cd49352018057c9fff"));
            administradores.put("Irma de Lourdes Hernandez Romo", encriptar.desencriptar("150075e05dc2b3a69fea2b"));
            administradores.put("Claro Lidia Flores Alcantara", encriptar.desencriptar("d745383f3f39201f107797"));
            break;
          case "gylvi":
            administradores.put("Luis Cesar Lopez Manzur", encriptar.desencriptar("89f468ef6bec68d249b0d1"));
            administradores.put("Jordi Alfonso Fariña Quiroz", encriptar.desencriptar("b8a5989f9b9e999e93fa00"));
            break;
          case "triana":
            administradores.put("Jesús Fernando Villalpando Cisneros", encriptar.desencriptar("c2bfb2a5999c9b9f99fe01"));
            administradores.put("José Refugio Villalpando Vargas", encriptar.desencriptar("69d448cf47cdb4a495fa1e"));
        } // swtich
        notificar= new Cafu("", "", nombre, sujeto.toString("nomina"), "*"+ sujeto.toString("inicio")+ "* al *"+ sujeto.toString("termino")+ "*");
        for (String administrador: administradores.keySet()) {
          notificar.setNombre(Cadena.nombrePersona(administrador));
          notificar.setCelular((String)administradores.get(administrador));
          LOG.info("Enviando mensaje de whatsup al celular: "+ administrador);
          notificar.doSendCajaChica(sesion);
        } // for
        if(administradores.isEmpty())
          JsfBase.addMessage("No se selecciono ningún celular, por favor verifiquelo e intente de nueva cuenta.", ETipoMensaje.ALERTA);
      } // if  
		} // try // try
		catch(Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	} // toNotificarAdministradores

}