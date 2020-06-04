package mx.org.kaana.keet.catalogos.contratos.vales.especiales.reglas;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.catalogos.contratos.vales.beans.Vale;
import mx.org.kaana.keet.db.dto.TcKeetValesBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetValesDetallesDto;
import mx.org.kaana.keet.db.dto.TcKeetValesDto;
import mx.org.kaana.keet.enums.ETiposEntregas;
import mx.org.kaana.keet.enums.EValesEstatus;
import mx.org.kaana.keet.nomina.reglas.Semanas;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private static final Log LOG= LogFactory.getLog(Transaccion.class);	
	private Vale vale;
	private String qr;
	private Long idVale;

	public Transaccion(Vale vale) {
		this(vale, -1L);
	}	

	public Transaccion(Long idVale) {
		this(null, idVale);
	}	

	public Transaccion(Vale vale, Long idVale) {
		this.vale  = vale;
		this.idVale= idVale;
	}
	
	public Long getIdVale() {
		return idVale;
	}	
	
	public String getQr() {
		return qr;
	}	
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar         = true;
		Map<String, Object>params= null;		
		try {
			switch(accion){
				case AGREGAR:												
					regresar= procesarVale(sesion);					
					break;		
				case MODIFICAR:
					regresar= modificarVale(sesion);
					break;
				case DEPURAR:
					regresar= depurarVale(sesion);
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
	
	private boolean procesarVale(Session sesion) throws Exception{
		boolean regresar      = false;		
		TcKeetValesDto valeDto= null;		
		Siguiente siguiente    = null;
		try {
			siguiente= toSiguiente(sesion);			
			valeDto= loadVale(siguiente);			
			this.idVale= DaoFactory.getInstance().insert(sesion, valeDto);
			if(this.idVale>= 1L){
				if(registrarBitacora(sesion, EValesEstatus.DISPONIBLE.getKey())){
					regresar= registrarDetalle(sesion);				
					generateQr(siguiente, valeDto.getRegistro());					
				} // if
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // procesarVale
	
	private TcKeetValesDto loadVale(Siguiente siguiente) throws Exception{
		TcKeetValesDto regresar= null;		
		Semanas semana         = null;
		try {
			regresar= new TcKeetValesDto();			
			semana= new Semanas();
			regresar.setEjercicio(Long.valueOf(this.getCurrentYear()));
			regresar.setConsecutivo(siguiente.getConsecutivo());
			regresar.setOrden(siguiente.getOrden());
			regresar.setSemana(semana.getSemanaEnCurso());			
			regresar.setIdAlmacen(this.vale.getIdAlmacen());			
			regresar.setCantidad(toCantidad());
			regresar.setCosto(toCosto());
			regresar.setIdTipoVale(this.vale.getIdTipoVale());
			regresar.setIdUsuario(JsfBase.getIdUsuario());
			regresar.setIdValeContratista(this.vale.getTipoFigura());
			if(this.vale.getTipoFigura().equals(1L))
				regresar.setIdContratoLoteContratista(this.vale.getIdFigura());
			else
				regresar.setIdContratoLoteProveedor(this.vale.getIdFigura());
			regresar.setJustificacion(this.vale.getJustificacion());			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
		return regresar;
	} // loadVale
	
	private Double toCantidad(){
		Double regresar= 0D;
		for(Articulo articulo: this.vale.getArticulos()){
			regresar= regresar + articulo.getCantidad();
		} // for
		return regresar;
	} // toCantidad
	
	private Double toCosto(){
		Double regresar= 0D;
		for(Articulo articulo: this.vale.getArticulos()){
			regresar= regresar + (articulo.getPrecio() * articulo.getCantidad());
		} // for
		return regresar;
	} // toTotal
	
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());			
			Value next= DaoFactory.getInstance().toField(sesion, "TcKeetValesDto", "siguiente", params, "siguiente");
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
		TcKeetValesBitacoraDto bitacora= null;
		try {
			bitacora= new TcKeetValesBitacoraDto();
			bitacora.setIdUsuario(JsfBase.getIdUsuario());
			bitacora.setIdVale(this.idVale);
			bitacora.setIdValeEstatus(idEstatus);
			bitacora.setJustificacion(Cadena.isVacio(this.vale.getJustificacion()) ? "Registro de bitacora:" + EValesEstatus.fromId(idEstatus).getNombre() : this.vale.getJustificacion());
			regresar= DaoFactory.getInstance().insert(sesion, bitacora)>= 1L;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarBitacora
	
	private boolean registrarDetalle(Session sesion) throws Exception{
		boolean regresar              = true;
		TcKeetValesDetallesDto detalle= null;
		try {
			for(Articulo recordDetalle: this.vale.getArticulos()){
				if(recordDetalle.isValid()){
					detalle= new TcKeetValesDetallesDto();
					detalle.setCantidad(recordDetalle.getCantidad());
					detalle.setCodigo(recordDetalle.getPropio());
					detalle.setCosto((recordDetalle.getCosto() * recordDetalle.getCantidad()));
					detalle.setIdArticulo(recordDetalle.getIdArticulo());				
					detalle.setIdVale(this.idVale);
					detalle.setNombre(recordDetalle.getNombre());
					detalle.setPrecio(recordDetalle.getPrecio());				
					detalle.setIdTipoEntrega(ETiposEntregas.NINGUNO.getKey());				
					DaoFactory.getInstance().insert(sesion, detalle);	
				} // if
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarDetalle	
	
	private void generateQr(Siguiente siguiente, LocalDateTime registro){
		StringBuilder cadenaQr= new StringBuilder();
		cadenaQr.append(JsfBase.getAutentifica().getEmpresa().getIdEmpresa()).append("-");
		cadenaQr.append(siguiente.getConsecutivo()).append("-");
		cadenaQr.append(this.vale.getIdFigura()).append("-");
		cadenaQr.append(this.vale.getNombreFigura()).append("-");
		cadenaQr.append(Fecha.formatear(Fecha.FECHA_HORA_LARGA, registro));
		this.qr= cadenaQr.toString();
	} // generateQr		
	
	private boolean modificarVale(Session sesion) throws Exception{
		boolean regresar      = false;		
		TcKeetValesDto valeDto= null;
		try {			
			if(depurarDetalle(sesion)){				
				valeDto= (TcKeetValesDto) DaoFactory.getInstance().findById(sesion, TcKeetValesDto.class, this.idVale);
				valeDto.setCantidad(toCantidad());
				valeDto.setCosto(toCosto());
				if(DaoFactory.getInstance().update(sesion, valeDto)>= 1L){					
					if(registrarBitacora(sesion, EValesEstatus.DISPONIBLE.getKey())){
						regresar= registrarDetalle(sesion);											
					} // if
				} // if				
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
		return regresar;
	} // modificarVale	
	
	private boolean depurarDetalle(Session sesion) throws Exception{
		boolean regresar         = false;
		Map<String, Object>params= null;
		try {			
			params= new HashMap<>();
			params.put("idVale", this.idVale);
			regresar= DaoFactory.getInstance().execute(ESql.DELETE, sesion, "TcKeetValesDetallesDto", "rows", params)>= 1L;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // registrarPadres
	
	private boolean depurarBitacora(Session sesion) throws Exception{
		boolean regresar         = false;
		Map<String, Object>params= null;
		try {			
			params= new HashMap<>();
			params.put("idVale", this.idVale);
			regresar= DaoFactory.getInstance().execute(ESql.DELETE, sesion, "TcKeetValesBitacoraDto", "rows", params)>= 1L;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // registrarPadres
	
	private boolean depurarVale(Session sesion) throws Exception{
		boolean regresar= false;
		try {
			if(depurarBitacora(sesion)){
				if(depurarDetalle(sesion)){					
					regresar= DaoFactory.getInstance().delete(sesion, TcKeetValesDto.class, this.idVale)>= 1L;					
				} // if
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // depurarVale
}