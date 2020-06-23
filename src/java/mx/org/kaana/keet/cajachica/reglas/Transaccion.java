package mx.org.kaana.keet.cajachica.reglas;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.cajachica.beans.ArchivoGasto;
import mx.org.kaana.keet.cajachica.beans.Gasto;
import mx.org.kaana.keet.db.dto.TcKeetCajasChicasCierresBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetCajasChicasCierresDto;
import mx.org.kaana.keet.db.dto.TcKeetGastosBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetGastosDetallesDto;
import mx.org.kaana.keet.db.dto.TcKeetGastosDto;
import mx.org.kaana.keet.enums.EEstatusCajasChicas;
import mx.org.kaana.keet.enums.EEstatusGastos;
import mx.org.kaana.keet.enums.ETiposIncidentes;
import mx.org.kaana.keet.nomina.reglas.Semanas;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesDto;
import mx.org.kaana.mantic.enums.EEstatusIncidentes;
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

	public Transaccion(Long idGasto, boolean ok) {
		this(idGasto, null, ok);
	}
	
	public Transaccion(Gasto gasto) {
		this(-1L, gasto);
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
		this(idCajaChicaCierre, cantidad, observaciones, 2L, idDesarrollo);
	}
	
	public Transaccion(Long idCajaChicaCierre, Double cantidad, String observaciones, Long idAfectaNomina, Long idDesarrollo) {
		this.idCajaChicaCierre= idCajaChicaCierre;
		this.cantidad         = cantidad;
		this.observaciones    = observaciones;
		this.idAfectaNomina   = idAfectaNomina;
		this.idDesarrollo     = idDesarrollo;
	}
	
	public Long getIdGasto() {
		return idGasto;
	}	

	public Transaccion(List<ArchivoGasto> documentos) {
		this.documentos = documentos;
	}	
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar         = true;
		Map<String, Object>params= null;		
		try {
			switch(accion){
				case AGREGAR:												
					regresar= procesarGasto(sesion);					
					break;			
				case MODIFICAR:
					regresar= confirmarGasto(sesion);		
					break;								
				case SUBIR:
					for(ArchivoGasto archivogasto: this.documentos){
						if(DaoFactory.getInstance().insert(sesion, archivogasto)>=1L)
							toSaveFile(archivogasto.getIdArchivo());
					} // for
					break;
				case ACTIVAR:
					regresar= realizarCierre(sesion);
					break;
				case ASIGNAR:
					regresar= abonarCaja(sesion);
					break;
			} // switch
		} // try
		catch (Exception e) {			
			throw new Exception(e);
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	}	// ejecutar	
	
	private boolean procesarGasto(Session sesion) throws Exception{
		boolean regresar        = false;		
		TcKeetGastosDto gastoDto= null;		
		Siguiente siguiente     = null;		
		try {
			siguiente= toSiguiente(sesion);			
			gastoDto= loadGasto(siguiente);			
			this.idGasto= DaoFactory.getInstance().insert(sesion, gastoDto);
			if(this.idGasto>= 1L){
				if(registrarBitacora(sesion, EEstatusGastos.DISPONIBLE.getKey()))
					regresar= registrarDetalle(sesion);													
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // procesarVale
	
	private TcKeetGastosDto loadGasto(Siguiente siguiente) throws Exception{
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
			regresar.setIdEmpresa(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			regresar.setIdEmpresaPersona(JsfBase.getAutentifica().getPersona().getIdEmpresaPersona());
			regresar.setImporte(toImporte());
			regresar.setIdGastoEstatus(EEstatusGastos.DISPONIBLE.getKey());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
		return regresar;
	} // loadVale
	
	private Double toImporte(){
		Double regresar= 0D;
		for(Articulo articulo: this.gasto.getArticulos())
			regresar= regresar + articulo.getImporte();
		return regresar;
	} // toImporte
	
	private Double toArticulos(){
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
	
	private boolean registrarBitacora(Session sesion, Long idEstatus) throws Exception{
		boolean regresar               = false;
		TcKeetGastosBitacoraDto bitacora= null;
		try {
			bitacora= new TcKeetGastosBitacoraDto();
			bitacora.setIdUsuario(JsfBase.getIdUsuario());
			bitacora.setIdGasto(this.idGasto);
			bitacora.setIdGastoEstatus(idEstatus);
			bitacora.setJustificacion("Registro de bitacora:" + EEstatusGastos.fromId(idEstatus).getNombre());
			regresar= DaoFactory.getInstance().insert(sesion, bitacora)>= 1L;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarBitacora
	
	private boolean registrarDetalle(Session sesion) throws Exception{
		boolean regresar               = true;
		TcKeetGastosDetallesDto detalle= null;
		try {
			for(Articulo recordDetalle: this.gasto.getArticulos()){
				if(recordDetalle.isValid()){
					detalle= new TcKeetGastosDetallesDto();										
					detalle.setIdGasto(this.idGasto);
					detalle.setIdArticulo(recordDetalle.getIdArticulo());				
					detalle.setCodigo(recordDetalle.getPropio());
					detalle.setNombre(recordDetalle.getNombre());
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
	
	private boolean confirmarGasto(Session sesion) throws Exception{
		boolean regresar     = false;
		TcKeetGastosDto gasto= null;
		try {
			gasto= (TcKeetGastosDto) DaoFactory.getInstance().findById(sesion, TcKeetGastosDto.class, this.idGasto);
			if(this.ok){
				gasto.setIdGastoEstatus(EEstatusGastos.ACEPTADO.getKey());
				if(DaoFactory.getInstance().update(sesion, gasto)>= 1L){
					if(registrarBitacora(sesion, EEstatusGastos.ACEPTADO.getKey()))
						regresar= afectarCaja(sesion, gasto.getIdCajaChicaCierre(), gasto.getImporte());
				} // if
			} // if
			else{
				gasto.setIdGastoEstatus(EEstatusGastos.CANCELADO.getKey());
				if(DaoFactory.getInstance().update(sesion, gasto)>= 1L)
					regresar= registrarBitacora(sesion, EEstatusGastos.CANCELADO.getKey());
			} // else
		} // try
		catch (Exception e) {			
			throw e;
		} // catch	
		return regresar;
	} // confirmarGasto
	
	private boolean afectarCaja(Session sesion, Long idCaja, Double importe) throws Exception{
		boolean regresar                = false;
		TcKeetCajasChicasCierresDto caja= null; 
		try {
			caja= (TcKeetCajasChicasCierresDto) DaoFactory.getInstance().findById(sesion, TcKeetCajasChicasCierresDto.class, idCaja);
			caja.setAcumulado(caja.getAcumulado() + importe);
			caja.setDisponible(caja.getDisponible() - importe);			
			caja.setIdCajaChicaCierreEstatus(EEstatusCajasChicas.PARCIALIZADO.getKey());
			if(DaoFactory.getInstance().update(sesion, caja)>= 1L)
				regresar= registrarBitacoraCaja(sesion, idCaja, EEstatusCajasChicas.PARCIALIZADO.getKey());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} //afectarCaja
	
	private boolean registrarBitacoraCaja(Session sesion, Long idCajaCierre, Long idEstatus) throws Exception{
		boolean regresar= false;
		TcKeetCajasChicasCierresBitacoraDto bitacora= null;
		try {
			bitacora= new TcKeetCajasChicasCierresBitacoraDto();
			bitacora.setIdCajaChicaCierre(idCajaCierre);
			bitacora.setIdCajaChicaCierreEstatus(idEstatus);
			bitacora.setIdUsuario(JsfBase.getIdUsuario());
			bitacora.setJustificacion("Registro de gasto");
			regresar= DaoFactory.getInstance().insert(sesion, bitacora)>= 1L;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarBitacoraCaja
	
	private boolean abonarCaja(Session sesion) throws Exception{
		boolean regresar                  = false;
		TcKeetCajasChicasCierresDto cierre= null;
		TcKeetGastosDto abono             = null;
		Siguiente siguiente               = null;
		try {
			cierre= (TcKeetCajasChicasCierresDto) DaoFactory.getInstance().findById(sesion, TcKeetCajasChicasCierresDto.class, this.idCajaChicaCierre);
			cierre.setDisponible(cierre.getDisponible() + this.cantidad);			
			if(DaoFactory.getInstance().update(sesion, cierre)>= 1L){
				if(registrarBitacoraCaja(sesion, this.idCajaChicaCierre, EEstatusCajasChicas.PARCIALIZADO.getKey())){
					siguiente= toSiguiente(sesion);
					abono= loadAbono(siguiente);
					this.idGasto= DaoFactory.getInstance().insert(sesion, abono);
					if(this.idGasto>= 1L){
						if(registrarBitacora(sesion, EEstatusGastos.ACEPTADO.getKey())){
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
	
	private TcKeetGastosDto loadAbono(Siguiente siguiente) throws Exception{
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
	
	private boolean registraDetalleAbono(Session sesion) throws Exception{		
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
	
	private boolean realizarCierre(Session sesion) throws Exception{
		boolean regresar                  = false;
		TcKeetCajasChicasCierresDto cierre= null;
		TcKeetCajasChicasCierresDto nuevo= null;
		try {
			cierre= (TcKeetCajasChicasCierresDto) DaoFactory.getInstance().findById(sesion, TcKeetCajasChicasCierresDto.class, this.idCajaChicaCierre);
			cierre.setIdCajaChicaCierreEstatus(EEstatusCajasChicas.TERMINADO.getKey());
			cierre.setTermino(LocalDateTime.now());
			if(DaoFactory.getInstance().update(sesion, cierre)>= 1L){
				if(registrarBitacoraCaja(sesion, cierre.getIdCajaChicaCierre(), EEstatusCajasChicas.TERMINADO.getKey())){
					if(this.idAfectaNomina.equals(1L)){
						registrarInicidenciaNomina(sesion);
					} // if
					if(cierre.getDisponible()<0)
						registrarInicidenciaGasto(sesion, cierre.getDisponible());					
					nuevo= loadCierre(sesion, cierre.getIdCajaChica());
					if(DaoFactory.getInstance().insert(sesion, nuevo)>= 1L){
						if(registrarBitacoraCaja(sesion, nuevo.getIdCajaChicaCierre(), EEstatusCajasChicas.INICIADO.getKey())){
							regresar= actualizarGastos(sesion, cierre.getIdCajaChicaCierre(), nuevo.getIdCajaChicaCierre());
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
	
	private TcKeetCajasChicasCierresDto loadCierre(Session sesion, Long idCajaChica) throws Exception{
		TcKeetCajasChicasCierresDto regresar= null;
		Siguiente siguiente                 = null;
		Semanas semana                      = null;
		try {
			siguiente= toSiguienteCierre(sesion);
			regresar= new TcKeetCajasChicasCierresDto();
			regresar.setIdCajaChica(idCajaChica);
			regresar.setEjercicio(Long.valueOf(Fecha.getAnioActual()));
			regresar.setOrden(siguiente.getOrden());
			regresar.setConsecutivo(siguiente.getConsecutivo());
			regresar.setIdCajaChicaCierreEstatus(EEstatusCajasChicas.INICIADO.getKey());
			regresar.setTermino(LocalDateTime.now());
			regresar.setObservaciones(this.observaciones);
			regresar.setAcumulado(0D);
			regresar.setDisponible(this.cantidad);
			regresar.setSaldo(this.cantidad);
			regresar.setIdUsuario(JsfBase.getIdUsuario());
			regresar.setIdAfectaNomina(this.idAfectaNomina);
			semana= new Semanas();
			regresar.setIdNominaPeriodo(semana.getSemanaEnCursoDto().getIdNominaPeriodo());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadCierre
	
	private boolean actualizarGastos(Session sesion, Long idCajaAnterior, Long idCajaNuevo) throws Exception{
		boolean regresar         = false;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idCajaAnterior", idCajaAnterior);
			params.put("idCajaNuevo", idCajaNuevo);
			regresar= DaoFactory.getInstance().execute(ESql.UPDATE, sesion, "TcKeetGastosDto", "disponibles", params)> -1L;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		return regresar;
	} // actualizarGastos
	
	private void registrarInicidenciaNomina(Session sesion) throws Exception{		
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
	
	private void registrarInicidenciaGasto(Session sesion, Double costo) throws Exception{
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
			dto.setCosto(costo*-1L);
			dto.setIdTipoIncidente(ETiposIncidentes.SALDO_CAJA.getKey());	
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
	
	private boolean registrarBitacoraIncidente(Session sesion, Long idIncidente, Long idEstatus) throws Exception{
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
}