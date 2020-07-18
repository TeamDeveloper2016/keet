package mx.org.kaana.keet.compras.requisiciones.reglas;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.Session;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.compras.requisiciones.beans.RegistroRequisicion;
import mx.org.kaana.keet.db.dto.TcKeetDesarrollosDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.db.dto.TcManticAlmacenesDto;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesComprasDto;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesDetallesDto;
import mx.org.kaana.mantic.db.dto.TcManticRequisicionesBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticRequisicionesDetallesDto;
import mx.org.kaana.mantic.db.dto.TcManticRequisicionesDto;
import mx.org.kaana.mantic.db.dto.TcManticRequisicionesProveedoresDto;
import mx.org.kaana.mantic.enums.EEstatusRequisiciones;
import mx.org.kaana.mantic.ventas.beans.ArticuloVenta;
import org.apache.log4j.Logger;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Transaccion extends IBaseTnx {

  private static final Logger LOG  = Logger.getLogger(Transaccion.class);
	private TcManticRequisicionesBitacoraDto bitacora;
	private RegistroRequisicion requisicion;	
	private List<Articulo> articulos;
	private String messageError;	
	private String justificacion;	

	public Transaccion(TcManticRequisicionesBitacoraDto bitacora) {
		this.bitacora= bitacora;
	} // Transaccion	
	
	public Transaccion(RegistroRequisicion requisicion, String justificacion) {
		this(requisicion, new ArrayList<Articulo>(), justificacion);
	} // Transaccion

	public Transaccion(RegistroRequisicion requisicion, List<Articulo> articulos) {
		this(requisicion, articulos, "");
	}
	
	public Transaccion(RegistroRequisicion requisicion, List<Articulo> articulos, String justificacion) {
		this.requisicion  = requisicion;		
		this.articulos    = articulos;
		this.justificacion= justificacion;
	} // Transaccion

	public String getMessageError() {
		return messageError;
	} // Transaccion	
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar                       = false;
		Map<String, Object> params             = null;
		Long idRequisicionEstatus              = null;
		TcManticRequisicionesDto bitRequisicion= null;
		try {
			idRequisicionEstatus= EEstatusRequisiciones.ELABORADA.getIdEstatusRequisicion();
			params= new HashMap<>();
			if(this.requisicion!= null)
				params.put("idRequisicion", this.requisicion.getRequisicion().getIdRequisicion());
			this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" la requisición.");
			switch(accion) {
				case AGREGAR:
				case REGISTRAR:			
					idRequisicionEstatus= accion.equals(EAccion.AGREGAR) ? EEstatusRequisiciones.SOLICITADA.getIdEstatusRequisicion() : idRequisicionEstatus;
					regresar= this.requisicion.getRequisicion().getIdRequisicion()!= null && !this.requisicion.getRequisicion().getIdRequisicion().equals(-1L) ? actualizarRequisicion(sesion, idRequisicionEstatus) : registrarRequisicion(sesion, idRequisicionEstatus);					
					break;
				case MODIFICAR:
					regresar= actualizarRequisicion(sesion, EEstatusRequisiciones.ELABORADA.getIdEstatusRequisicion());					
					break;				
				case ELIMINAR:
					idRequisicionEstatus= EEstatusRequisiciones.ELIMINADA.getIdEstatusRequisicion();
					bitRequisicion= (TcManticRequisicionesDto) DaoFactory.getInstance().findById(TcManticRequisicionesDto.class, this.requisicion.getRequisicion().getIdRequisicion());
					bitRequisicion.setIdRequisicionEstatus(idRequisicionEstatus);
					if(DaoFactory.getInstance().update(sesion, bitRequisicion)>= 1L)
						regresar= registraBitacora(sesion, this.requisicion.getRequisicion().getIdRequisicion(), idRequisicionEstatus, this.justificacion);					
					break;
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L){
						bitRequisicion= (TcManticRequisicionesDto) DaoFactory.getInstance().findById(sesion, TcManticRequisicionesDto.class, this.bitacora.getIdRequisicion());
						bitRequisicion.setIdRequisicionEstatus(this.bitacora.getIdRequisicionEstatus());
						regresar= DaoFactory.getInstance().update(sesion, bitRequisicion)>= 1L;
						if(this.bitacora.getIdRequisicionEstatus().equals(EEstatusRequisiciones.COTIZADA.getIdEstatusRequisicion()))
							procesarOrdenCompra(sesion, this.bitacora.getIdRequisicion());
					} // if
					break;												
				case REPROCESAR:
				case COPIAR:
					regresar= actualizarRequisicion(sesion, EEstatusRequisiciones.COTIZADA.getIdEstatusRequisicion());				
					break;				
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {		
			Error.mensaje(e);
			throw new Exception(this.messageError.concat("<br/>")+ e);
		} // catch		
		if(this.requisicion!= null)
			LOG.info("Se genero de forma correcta la requsición: "+ this.requisicion.getRequisicion().getConsecutivo());
		return regresar;
	}	// ejecutar

	private boolean registrarRequisicion(Session sesion, Long idRequisicionEstatus) throws Exception{
		boolean regresar     = false;
		Siguiente consecutivo= null;
		try {
			consecutivo= this.toSiguiente(sesion);
			this.requisicion.getRequisicion().setConsecutivo(consecutivo.getConsecutivo());
			this.requisicion.getRequisicion().setOrden(consecutivo.getOrden());
			this.requisicion.getRequisicion().setIdRequisicionEstatus(idRequisicionEstatus);
			this.requisicion.getRequisicion().setEjercicio(new Long(Fecha.getAnioActual()));
			this.requisicion.getRequisicion().setIdSolicita(JsfBase.getAutentifica().getPersona().getIdPersona());
			this.requisicion.getRequisicion().setIdUsuario(JsfBase.getIdUsuario());
			this.requisicion.getRequisicion().setIdEmpresa(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			regresar= DaoFactory.getInstance().insert(sesion, this.requisicion.getRequisicion())>= 1L;
			if(regresar){
				registraRequisicionProveedor(sesion);
				regresar= registraBitacora(sesion, this.requisicion.getRequisicion().getIdRequisicion(), idRequisicionEstatus, "");
				toFillArticulos(sesion);
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
		return regresar;
	} // registrarRequisicion
	
	private boolean actualizarRequisicion(Session sesion, Long idRequisicionEstatus) throws Exception{
		boolean regresar         = false;
		Map<String, Object>params= null;
		try {						
			this.requisicion.getRequisicion().setIdRequisicionEstatus(idRequisicionEstatus);						
			regresar= DaoFactory.getInstance().update(sesion, this.requisicion.getRequisicion())>= 1L;
			if(registraBitacora(sesion, this.requisicion.getRequisicion().getIdRequisicion(), idRequisicionEstatus, "")){
				params= new HashMap<>();
				params.put("idRequisicion", this.requisicion.getRequisicion().getIdRequisicion());				
				regresar= DaoFactory.getInstance().deleteAll(sesion, TcManticRequisicionesDetallesDto.class, params)>= 1;
				toFillArticulos(sesion);
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally			
		return regresar;
	} // actualizarRequisicion
	
	protected boolean registraBitacora(Session sesion, Long idRequisicion, Long idRequisicionEstatus, String justificacion) throws Exception{
		boolean regresar                         = false;
		TcManticRequisicionesBitacoraDto bitRequisicion= null;
		try {
			bitRequisicion= new TcManticRequisicionesBitacoraDto(-1L, justificacion, JsfBase.getIdUsuario(), idRequisicionEstatus, idRequisicion);
			regresar= DaoFactory.getInstance().insert(sesion, bitRequisicion)>= 1L;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarBitacora
	
	private void toFillArticulos(Session sesion) throws Exception {
		List<Articulo> todos= (List<Articulo>)DaoFactory.getInstance().toEntitySet(sesion, Articulo.class, "TcManticRequisicionesDetallesDto", "detalle", this.requisicion.getRequisicion().toMap());
		for (Articulo item: todos){ 
			if(this.articulos.indexOf(item)< 0)
				DaoFactory.getInstance().delete(sesion, item);
		} // for
		for (Articulo articulo: this.articulos) {
			TcManticRequisicionesDetallesDto item= articulo.toRequisicionDetalle();
			item.setIdRequisicion(this.requisicion.getRequisicion().getIdRequisicion());
			item.setIdTipoPrecio(((ArticuloVenta)articulo).getPrecioLista().getKey());
			if(DaoFactory.getInstance().findIdentically(sesion, TcManticRequisicionesDetallesDto.class, item.toMap())== null) 
				DaoFactory.getInstance().insert(sesion, item);
		} // for
	} // toFillArticulos
	
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());
			params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticRequisicionesDto", "siguiente", params, "siguiente");
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
	
	private boolean registraRequisicionProveedor(Session sesion) throws Exception{
		TcManticRequisicionesProveedoresDto dto= null;		
		boolean regresar= false;
		try {			
			dto= new TcManticRequisicionesProveedoresDto();
			dto.setIdProveedor(this.requisicion.getRequisicion().getIdProveedor());
			dto.setIdRequisicion(this.requisicion.getRequisicion().getIdRequisicion());									
			regresar= DaoFactory.getInstance().insert(sesion, dto) >= 1L;							
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			this.messageError= "Error al registrar articulos de proveedor, verifique que no haya duplicados";
		} // finally
		return regresar;
	} // registraRequisicionProveedor
	
	private void procesarOrdenCompra(Session sesion, Long idRequisicion) throws Exception{
		TcManticRequisicionesDto req                      = null;
		List<TcManticRequisicionesDetallesDto> reqDetalles= null;
		Map<String, Object>params                         = null;
		TcManticOrdenesComprasDto ordenCompra             = null;		
		Siguiente siguiente                               = null;
		TcManticOrdenesBitacoraDto bitOrdenCompra         = null;
		try {
			req= (TcManticRequisicionesDto) DaoFactory.getInstance().findById(sesion, TcManticRequisicionesDto.class, idRequisicion);
			params= new HashMap<>();
			params.put("idRequisicion", idRequisicion);
			reqDetalles= DaoFactory.getInstance().toEntitySet(sesion, TcManticRequisicionesDetallesDto.class, "TcManticRequisicionesDetallesDto", "detalle", params);
			siguiente= this.toSiguienteOrden(sesion, req.getIdEmpresa());
			ordenCompra= loadOrdenCompra(sesion, siguiente, req);
			if(DaoFactory.getInstance().insert(sesion, ordenCompra)>= 1){
				if(procesarDetalleOrden(sesion, ordenCompra.getIdOrdenCompra(), reqDetalles)){
					bitOrdenCompra= new TcManticOrdenesBitacoraDto(ordenCompra.getIdOrdenEstatus(), "", JsfBase.getIdUsuario(), ordenCompra.getIdOrdenCompra(), -1L, ordenCompra.getConsecutivo(), ordenCompra.getTotal());
					DaoFactory.getInstance().insert(sesion, bitOrdenCompra);
				} // if
			} // if
		} // try
		catch (Exception e) {			
			throw e; 
		} // catch		
	} // procesarOrdenCompra
	
	private Siguiente toSiguienteOrden(Session sesion, Long idEmpresa) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());
			params.put("idEmpresa", idEmpresa);
		  params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticOrdenesComprasDto", "siguiente", params, "siguiente");
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
	} // toSiguienteOrden
	
	private TcManticOrdenesComprasDto loadOrdenCompra(Session sesion, Siguiente siguiente, TcManticRequisicionesDto req) throws Exception{
		TcManticOrdenesComprasDto regresar= null;
		TcKeetDesarrollosDto desarrollo   = null;
		TcManticAlmacenesDto almacen      = null;
		Map<String, Object>params         = null;
		Entity proveedorPago              = null;
		try {
			desarrollo= (TcKeetDesarrollosDto) DaoFactory.getInstance().findById(sesion, TcKeetDesarrollosDto.class, req.getIdDesarrollo());
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_desarrollo=".concat(req.getIdDesarrollo().toString()));
			almacen= (TcManticAlmacenesDto) DaoFactory.getInstance().toEntity(sesion, TcManticAlmacenesDto.class, "TcManticAlmacenesDto", "row", params);
			params.clear();
			params.put(Constantes.SQL_CONDICION, "id_proveedor=".concat(req.getIdProveedor().toString()));
			proveedorPago= (Entity) DaoFactory.getInstance().toEntity(sesion, "TrManticProveedorPagoDto", "row", params);
			regresar= new TcManticOrdenesComprasDto();
			regresar.setConsecutivo(siguiente.getConsecutivo());
			regresar.setOrden(siguiente.getOrden());			
			regresar.setIdProveedorPago(proveedorPago.toLong("idProveedorPago"));			
			regresar.setDescuentos(req.getDescuentos());
			regresar.setExcedentes(0D);
			regresar.setIdProveedor(req.getIdProveedor());
			regresar.setIdCliente(desarrollo.getIdCliente());
			regresar.setDescuento(req.getDescuento());			
			regresar.setExtras("");
			regresar.setEjercicio(req.getEjercicio());
			regresar.setRegistro(LocalDateTime.now());			
			regresar.setIdGasto(1L);
			regresar.setTotal(req.getTotal());
			regresar.setIdOrdenEstatus(1L);
			regresar.setEntregaEstimada(req.getFechaEntregada());
			regresar.setIdUsuario(JsfBase.getIdUsuario());
			regresar.setIdAlmacen(almacen.getIdAlmacen());
			regresar.setImpuestos(req.getImpuestos());
			regresar.setSubTotal(req.getSubTotal());
			regresar.setTipoDeCambio(1D);
			regresar.setIdSinIva(1L);
			regresar.setObservaciones(req.getObservaciones());
			regresar.setIdEmpresa(req.getIdEmpresa());			
			regresar.setIdDesarrollo(req.getIdDesarrollo());
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		return regresar;
	} // loadOrdenCompra
	
	private boolean procesarDetalleOrden(Session sesion, Long idOrdenCompra, List<TcManticRequisicionesDetallesDto> reqDetalles) throws Exception{
		boolean regresar= true;
		try {
			for(TcManticRequisicionesDetallesDto detalle: reqDetalles)
				DaoFactory.getInstance().insert(sesion, loadOrdenDetalle(idOrdenCompra, detalle));
		} // try
		catch (Exception e) {			
			throw e; 
		} // catch		
		return regresar;
	} // procesarDetalleOrden
	
	private TcManticOrdenesDetallesDto loadOrdenDetalle(Long idOrdenCompra, TcManticRequisicionesDetallesDto reqDetalle){
		TcManticOrdenesDetallesDto regresar= null;
		try {
			regresar= new TcManticOrdenesDetallesDto();			
			regresar.setDescuentos(reqDetalle.getDescuentos());			
			regresar.setCodigo(reqDetalle.getCodigo());
			regresar.setCosto(reqDetalle.getCosto());
			regresar.setDescuento(reqDetalle.getDescuento());
			regresar.setIdOrdenCompra(idOrdenCompra);			
			regresar.setNombre(reqDetalle.getNombre());
			regresar.setImporte(reqDetalle.getImporte());			
			regresar.setRegistro(LocalDateTime.now());
			regresar.setPropio(reqDetalle.getPropio());			
			regresar.setIva(reqDetalle.getIva());
			regresar.setImpuestos(reqDetalle.getImpuestos());
			regresar.setSubTotal(reqDetalle.getSubTotal());
			regresar.setCantidad(reqDetalle.getCantidad());
			regresar.setIdArticulo(reqDetalle.getIdArticulo());
			regresar.setExtras("0");
			regresar.setImportes(0D);
			regresar.setExcedentes(0D);
			regresar.setPrecios(0D);
			regresar.setCantidades(0D);
			regresar.setCostoReal(0D);
			regresar.setCostoCalculado(0D);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadOrdenDetalle
} 
