package mx.org.kaana.mantic.compras.requisiciones.reglas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import org.hibernate.Session;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.compras.requisiciones.beans.RegistroRequisicion;
import mx.org.kaana.mantic.compras.requisiciones.beans.RequisicionProveedor;
import mx.org.kaana.mantic.db.dto.TcManticRequisicionesBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticRequisicionesDetallesDto;
import mx.org.kaana.mantic.db.dto.TcManticRequisicionesDto;
import mx.org.kaana.mantic.db.dto.TcManticRequisicionesProveedoresDto;
import mx.org.kaana.mantic.enums.EEstatusRequisiciones;
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
	private Long idRequisicion;

	public Transaccion(Long idRequisicion) {
    this.idRequisicion= idRequisicion;
  }
  
	public Transaccion(TcManticRequisicionesBitacoraDto bitacora) {
		this.bitacora= bitacora;
	} // Transaccion
	
	public Transaccion(RegistroRequisicion requisicion) {
		this(requisicion, "");
	}
	
	public Transaccion(RegistroRequisicion requisicion, String justificacion) {
		this(requisicion, new ArrayList<Articulo>(), justificacion);
	} // Transaccion

	public Transaccion(RegistroRequisicion requisicion, List<Articulo> articulos) {
		this(requisicion, articulos, "");
	}
	
	public Transaccion(RegistroRequisicion requisicion, List<Articulo> articulos, String justificacion) {
		this.requisicion= requisicion;		
		this.articulos  = articulos;
	} // Transaccion

	public String getMessageError() {
		return messageError;
	} // Transaccion

	public void setMessageError(String messageError) {
		this.messageError = messageError;
	}	

	public Long getIdRequisicion() {
		return idRequisicion;
	}

	public RegistroRequisicion getRequisicion() {
		return requisicion;
	}
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar                       = false;
		Map<String, Object> params             = new HashMap<>();
		Long idRequisicionEstatus              = null;
		TcManticRequisicionesDto bitRequisicion= null;
		try {
			idRequisicionEstatus= EEstatusRequisiciones.ELABORADA.getIdEstatusRequisicion();
			if(this.requisicion!= null)
				params.put("idRequisicion", this.requisicion.getRequisicion().getIdRequisicion());
			this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" la requisición");
			switch(accion) {
				case AGREGAR:
				case REGISTRAR:			
					idRequisicionEstatus= Objects.equals(accion, EAccion.AGREGAR)? EEstatusRequisiciones.ELABORADA.getIdEstatusRequisicion(): idRequisicionEstatus;
					regresar= !Objects.equals(this.requisicion.getRequisicion().getIdRequisicion(), null) && 
                    !Objects.equals(this.requisicion.getRequisicion().getIdRequisicion(), -1L) ? 
                  this.actualizarRequisicion(sesion, idRequisicionEstatus): 
                  this.registrarRequisicion(sesion, idRequisicionEstatus);					
					break;
				case MODIFICAR:
					regresar= this.actualizarRequisicion(sesion, EEstatusRequisiciones.ELABORADA.getIdEstatusRequisicion());					
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
					} // if
					break;												
				case REPROCESAR:
				case COPIAR:
					regresar= this.actualizarRequisicion(sesion, EEstatusRequisiciones.COTIZADA.getIdEstatusRequisicion());				
					break;				
				case GENERAR:
          regresar= this.toClonar(sesion);
          break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {		
			Error.mensaje(e);
			throw new Exception(this.messageError.concat("<br/>")+ (e!= null? e.getCause().toString(): ""));
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
			this.requisicion.getRequisicion().setDescuento("0");
			this.requisicion.getRequisicion().setDescuentos(0D);
			regresar= DaoFactory.getInstance().insert(sesion, this.requisicion.getRequisicion())>= 1L;
			if(regresar) {
				this.registraRequisicionProveedor(sesion, this.requisicion.getRequisicion().getIdRequisicion());
				regresar= this.registraBitacora(sesion, this.requisicion.getRequisicion().getIdRequisicion(), idRequisicionEstatus, "");
				this.toFillArticulos(sesion);
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
		return regresar;
	} // registrarVenta
	
	private boolean actualizarRequisicion(Session sesion, Long idRequisicionEstatus) throws Exception{
		boolean regresar         = false;
		Map<String, Object>params= null;
		try {						
			this.requisicion.getRequisicion().setIdRequisicionEstatus(idRequisicionEstatus);						
			regresar= DaoFactory.getInstance().update(sesion, this.requisicion.getRequisicion())>= 1L;
			if(registraBitacora(sesion, this.requisicion.getRequisicion().getIdRequisicion(), idRequisicionEstatus, "")){
				params= new HashMap<>();
				params.put("idRequisicion", this.requisicion.getRequisicion().getIdRequisicion());
				registraRequisicionProveedor(sesion, this.requisicion.getRequisicion().getIdRequisicion());
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
	} // actualizarVenta
	
	protected boolean registraBitacora(Session sesion, Long idRequisicion, Long idRequisicionEstatus, String justificacion) throws Exception{
		boolean regresar                         = false;
		TcManticRequisicionesBitacoraDto bitVenta= null;
		try {
			bitVenta= new TcManticRequisicionesBitacoraDto(-1L, justificacion, JsfBase.getIdUsuario(), idRequisicionEstatus, idRequisicion);
			regresar= DaoFactory.getInstance().insert(sesion, bitVenta)>= 1L;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarBitacora
	
	private void toFillArticulos(Session sesion) throws Exception {
		List<Articulo> todos= (List<Articulo>)DaoFactory.getInstance().toEntitySet(sesion, Articulo.class, "TcManticRequisicionesDetallesDto", "detalle", this.requisicion.getRequisicion().toMap());
		for (Articulo item: todos) 
			if(this.articulos.indexOf(item)< 0)
				DaoFactory.getInstance().delete(sesion, item);
		for (Articulo articulo: this.articulos) {
			TcManticRequisicionesDetallesDto item= articulo.toRequisicionDetalle();
      if(Cadena.isVacio(item.getCodigo()))
        item.setCodigo(item.getPropio());
			item.setIdRequisicion(this.requisicion.getRequisicion().getIdRequisicion());
			if(DaoFactory.getInstance().findIdentically(sesion, TcManticRequisicionesDetallesDto.class, item.toMap())== null) 
				DaoFactory.getInstance().insert(sesion, item);
		} // for
	} // toFillArticulos
	
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("ejercicio", this.getCurrentYear());
			params.put("idEmpresa", this.requisicion.getRequisicion().getIdEmpresa());
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
	
	private boolean registraRequisicionProveedor(Session sesion, Long idRequisicion) throws Exception {
		TcManticRequisicionesProveedoresDto dto= null;
		ESql sqlAccion  = null;		
		int count       = 0;
		boolean validate= false;
		boolean regresar= false;
		try {
			if(!this.requisicion.getProveedores().isEmpty()){
				for(RequisicionProveedor requisicionProveedor: this.requisicion.getProveedores()){
					requisicionProveedor.setIdRequisicion(idRequisicion);				
					dto= (TcManticRequisicionesProveedoresDto) requisicionProveedor;				
					sqlAccion= requisicionProveedor.getSqlAccion();
					switch(sqlAccion){
						case INSERT:
							dto.setIdRequisicionProveedor(-1L);
							validate= registrar(sesion, dto);
							break;
						case UPDATE:
							validate= actualizar(sesion, dto);
							break;
					} // switch
					if(validate)
						count++;
				} // for		
				regresar= count== this.requisicion.getProveedores().size();
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			this.messageError= "Error al registrar articulos de proveedor, verifique que no haya duplicados";
		} // finally
		return regresar;
	} 
	
	private Boolean registrar(Session sesion, IBaseDto dto) throws Exception {
		return DaoFactory.getInstance().insert(sesion, dto) >= 1L;
	} // registrar
	
	private Boolean actualizar(Session sesion, IBaseDto dto) throws Exception {
		return DaoFactory.getInstance().update(sesion, dto) >= 1L;
	} // actualizar

	private Boolean toClonar(Session sesion) throws Exception {
		Boolean regresar= Boolean.FALSE;
		Siguiente consecutivo= null;
		Map<String, Object> params= new HashMap<>();
		try {
			consecutivo= this.toSiguiente(sesion);
      TcManticRequisicionesDto item= (TcManticRequisicionesDto)DaoFactory.getInstance().findById(sesion, TcManticRequisicionesDto.class, this.idRequisicion);
      TcManticRequisicionesDto clon= new TcManticRequisicionesDto(
        item.getDescuentos(), //  Double descuentos, 
        item.getIdProveedor(), // Long idProveedor, 
        item.getIdDesarrollo(), // Long idDesarrollo, 
        item.getDescuento(), // String descuento, 
        1L, // Long idRequisicionEstatus, 
        -1L, // Long idRequisicion, 
        item.getIdSolicita(), // Long idSolicita, 
        new Long(this.getCurrentYear()), // Long ejercicio, 
        consecutivo.getConsecutivo(), // String consecutivo, 
        item.getFechaPedido(), // LocalDate fechaPedido, 
        item.getTotal(), // Double total, 
        JsfBase.getIdUsuario(), // Long idUsuario, 
        item.getImpuestos(), // Double impuestos, 
        item.getSubTotal(), // Double subTotal, 
        item.getObservaciones(), // String observaciones, 
        item.getIdEmpresa(), // Long idEmpresa, 
        item.getFechaEntregada(), // LocalDate fechaEntregada, 
        consecutivo.getOrden(), // Long orden, 
        item.getIdContrato(), // Long idContrato, 
        item.getIdPrototipo(), // Long idPrototipo, 
        item.getIdEnviar() //  Long idEnviar      
      );
      regresar= DaoFactory.getInstance().insert(sesion, clon)> 0L;
			params.put("idRequisicion", this.idRequisicion);
			params.put("idClon", clon.getIdRequisicion());
      DaoFactory.getInstance().execute(ESql.INSERT, sesion, "TcManticRequisicionesDetallesDto", "row", params);
      this.idRequisicion= clon.getIdRequisicion();
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
