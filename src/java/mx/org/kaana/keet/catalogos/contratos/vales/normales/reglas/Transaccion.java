package mx.org.kaana.keet.catalogos.contratos.vales.normales.reglas;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.catalogos.contratos.vales.beans.DetalleVale;
import mx.org.kaana.keet.catalogos.contratos.vales.beans.MaterialVale;
import mx.org.kaana.keet.catalogos.contratos.vales.beans.Vale;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.db.dto.TcKeetMaterialesDto;
import mx.org.kaana.keet.db.dto.TcKeetValesBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetValesDetallesDto;
import mx.org.kaana.keet.db.dto.TcKeetValesDto;
import mx.org.kaana.keet.db.dto.TcKeetValesMaterialesDto;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.enums.ETiposEntregas;
import mx.org.kaana.keet.enums.EEstatusVales;
import mx.org.kaana.keet.materiales.reglas.Materiales;
import mx.org.kaana.keet.nomina.reglas.Semanas;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
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
			valeDto= loadVale(sesion, siguiente);			
			this.idVale= DaoFactory.getInstance().insert(sesion, valeDto);
			if(this.idVale>= 1L){
				if(registrarBitacora(sesion, EEstatusVales.DISPONIBLE.getKey())){
					regresar= registrarDetalle(sesion, valeDto.getSemana().toString());				
					generateQr(siguiente, valeDto.getRegistro());
					registrarPadres(sesion);
				} // if
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // procesarVale
	
	private TcKeetValesDto loadVale(Session sesion, Siguiente siguiente) throws Exception{
		TcKeetValesDto regresar= null;		
		Semanas semana         = null;
		try {
			regresar= new TcKeetValesDto();			
			semana= new Semanas();
			regresar.setEjercicio(Long.valueOf(this.getCurrentYear()));
			regresar.setConsecutivo(siguiente.getConsecutivo());
			regresar.setOrden(siguiente.getOrden());
			regresar.setSemana(semana.getSemanaEnCurso());			
			regresar.setIdAlmacen(toIdAlmacen(sesion));			
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
			regresar.setIdValeEstatus(EEstatusVales.DISPONIBLE.getKey());
		} // try 
		catch (Exception e) {			
			throw e;
		} // catch				
		return regresar;
	} // loadVale
	
	private Double toCantidad(){
		Double regresar=0D;
		for(MaterialVale material: this.vale.getMateriales()){
			regresar= regresar + material.getCantidad();
		} // for
		return regresar;
	} // toCantidad
	
	private Double toCosto(){
		Double regresar=0D;
		for(MaterialVale material: this.vale.getMateriales()){
			regresar= regresar + (material.getCosto() * material.getCantidad());
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
			bitacora.setJustificacion(Cadena.isVacio(this.vale.getJustificacion()) ? "Registro de bitacora:" + EEstatusVales.fromId(idEstatus).getNombre() : this.vale.getJustificacion());
			regresar= DaoFactory.getInstance().insert(sesion, bitacora)>= 1L;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarBitacora
	
	private boolean registrarDetalle(Session sesion, String semana) throws Exception{
		boolean regresar              = true;
		TcKeetValesDetallesDto detalle= null;
		try {
			for(DetalleVale recordDetalle: this.vale.getDetalle()){
				detalle= new TcKeetValesDetallesDto();
				detalle.setCantidad(recordDetalle.getCantidad());
				detalle.setCodigo(recordDetalle.getCodigo());
				detalle.setCosto((recordDetalle.getCosto() * recordDetalle.getCantidad()));
				detalle.setIdArticulo(recordDetalle.getIdArticulo());
				detalle.setIdMaterial(recordDetalle.getIdMaterial());
				detalle.setIdVale(this.idVale);
				detalle.setNombre(recordDetalle.getNombre());
				detalle.setPrecio(recordDetalle.getPrecio());				
				detalle.setIdTipoEntrega(ETiposEntregas.NINGUNO.getKey());				
				if(DaoFactory.getInstance().insert(sesion, detalle)>= 1L)
					actualizaMaterial(sesion, detalle.getIdMaterial(), semana, detalle.getCosto(), true);
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarDetalle
	
	private void actualizaMaterial(Session sesion, Long idMaterial, String semana, Double costo, boolean alta) throws Exception{
		Map<String, Object>params   = null;
		TcKeetMaterialesDto material= null;
		try {
			material= (TcKeetMaterialesDto) DaoFactory.getInstance().findById(sesion, TcKeetMaterialesDto.class, idMaterial);
			params= new HashMap<>();
			params.put("idEstacionEstatus", toIdEstacionEstatus(material, costo, alta));
			if(alta)
				params.put("cargo".concat(semana), (material.toValue("cargo".concat(semana)) != null ? ((Double)material.toValue("cargo".concat(semana))) : 0D) + costo);										
			else
				params.put("cargo".concat(semana), (material.toValue("cargo".concat(semana)) != null ? ((Double)material.toValue("cargo".concat(semana))) : 0D) - costo);										
			if(DaoFactory.getInstance().update(sesion, TcKeetMaterialesDto.class, idMaterial, params)>= 1L)
				actualizaEstacionPadre(sesion, material, costo, semana, alta);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
	} //actualizaMaterial
	
	private void generateQr(Siguiente siguiente, LocalDateTime registro){
		StringBuilder cadenaQr= new StringBuilder();
		cadenaQr.append(JsfBase.getAutentifica().getEmpresa().getIdEmpresa()).append("-");
		cadenaQr.append(siguiente.getConsecutivo()).append("-");
		cadenaQr.append(this.vale.getIdFigura()).append("-");
		cadenaQr.append(this.vale.getNombreFigura()).append("-");
		cadenaQr.append(Fecha.formatear(Fecha.FECHA_HORA_LARGA, registro));
		this.qr= cadenaQr.toString();
	} // generateQr
	
	private boolean actualizaEstacionPadre(Session sesion, TcKeetMaterialesDto hijo, Double total, String semana, boolean alta) throws Exception{
		boolean regresar                = true;
		Materiales materiales           = null;
		List<TcKeetMaterialesDto> padres= null;
		Map<String, Object>params       = null;
		try {
			params= new HashMap<>();
			materiales= new Materiales();
			padres= materiales.toFather(hijo.getClave());			
			for(TcKeetMaterialesDto padre: padres){
				params.put("idEstacionEstatus", toIdEstacionEstatus(padre, total, alta));
				if(alta)
					params.put("cargo".concat(semana), (padre.toValue("cargo".concat(semana)) != null ? ((Double)padre.toValue("cargo".concat(semana))) : 0D) + total);								
				else
					params.put("cargo".concat(semana), (padre.toValue("cargo".concat(semana)) != null ? ((Double)padre.toValue("cargo".concat(semana))) : 0D) - total);								
				DaoFactory.getInstance().update(sesion, TcKeetEstacionesDto.class, padre.getIdMaterial(), params);
			} // for
		} // try
		catch (Exception e) {			
			throw e; 
		} // catch		
		return regresar;
	} // actualizaEstacionPadre
	
	private Long toIdEstacionEstatus(TcKeetMaterialesDto material, Double costoActual, boolean alta){
		Long regresar       = -1L;		
		Double acumulado    = 0D;
		Double cargoEstacion= 0D;
		try {
			for(int count=0; count<55; count++){
				cargoEstacion= material.toValue("cargo".concat(String.valueOf(count+1)))!= null ? ((Double) material.toValue("cargo".concat(String.valueOf(count+1)))) : 0D;
				acumulado= acumulado + cargoEstacion;
			} // for
			if(alta)
				acumulado= acumulado + costoActual;			
			else
				acumulado= acumulado - costoActual;			
			regresar= acumulado>= material.getCosto() ? EEstacionesEstatus.TERMINADO.getKey() : EEstacionesEstatus.EN_PROCESO.getKey();			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;				
	} // toIdEstacionEstatus
	
	private void registrarPadres(Session sesion) throws Exception{
		TcKeetValesMaterialesDto dto= null;
		try {
			for(DetalleVale detalleVale: this.vale.getPadres()){
				dto= new TcKeetValesMaterialesDto();
				dto.setIdVale(this.idVale);
				dto.setIdMaterial(detalleVale.getIdMaterial());
				DaoFactory.getInstance().insert(sesion, dto);
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // registrarPadres
	
	private boolean modificarVale(Session sesion) throws Exception{
		boolean regresar                    = false;
		List<TcKeetValesDetallesDto>detalles= null;
		Map<String, Object>params           = null;
		Semanas semanas                     = null;
		Long semana                         = null;
		TcKeetValesDto valeDto              = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_vale=" + this.idVale);
			detalles= DaoFactory.getInstance().toEntitySet(sesion, TcKeetValesDetallesDto.class, "TcKeetValesDetallesDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
			semanas= new Semanas();
			semana= semanas.getSemanaEnCurso();
			if(!detalles.isEmpty()){				
				for(TcKeetValesDetallesDto detalle: detalles){
					actualizaMaterial(sesion, detalle.getIdMaterial(), semana.toString(), detalle.getCosto(), false);														
				} // fot
			} // if
			if(depurarDetalle(sesion)){
				if(depurarPadres(sesion)){
					valeDto= (TcKeetValesDto) DaoFactory.getInstance().findById(sesion, TcKeetValesDto.class, this.idVale);
					valeDto.setCantidad(toCantidad());
					valeDto.setCosto(toCosto());
					if(DaoFactory.getInstance().update(sesion, valeDto)>= 1L){
						this.vale.setJustificacion("REGENERACION DEL VALE");
						if(registrarBitacora(sesion, EEstatusVales.DISPONIBLE.getKey())){
							regresar= registrarDetalle(sesion, semana.toString());					
							registrarPadres(sesion);
						} // if
					} // if
				} // if
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // modificarVale

	private boolean depurarPadres(Session sesion) throws Exception{
		boolean regresar         = false;
		Map<String, Object>params= null;
		try {			
			params= new HashMap<>();
			params.put("idVale", this.idVale);
			regresar= DaoFactory.getInstance().execute(ESql.DELETE, sesion, "TcKeetValesMaterialesDto", "rows", params)>= 1L;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // registrarPadres
	
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
					if(depurarPadres(sesion)){
						regresar= DaoFactory.getInstance().delete(sesion, TcKeetValesDto.class, this.idVale)>= 1L;
					} // if
				} // if
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // depurarVale
	
	private Long toIdAlmacen(Session sesion) throws Exception{
		Long regresar            = null;
		Entity almacen           = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_desarrollo=" + this.vale.getIdDesarrollo());
			almacen= (Entity) DaoFactory.getInstance().toEntity(sesion, "TcManticAlmacenesDto", "row", params);
			regresar= almacen.toLong("idAlmacen");
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toIdAlmacen
}