package mx.org.kaana.keet.catalogos.contratos.materiales.reglas;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.catalogos.contratos.materiales.beans.ArchivoEvidenciaVale;
import mx.org.kaana.keet.catalogos.contratos.vales.beans.DetalleVale;
import mx.org.kaana.keet.db.dto.TcKeetValesArchivosDto;
import mx.org.kaana.keet.db.dto.TcKeetValesDetallesDto;
import mx.org.kaana.keet.db.dto.TcKeetValesDetallesEntregaDto;
import mx.org.kaana.keet.db.dto.TcKeetValesDto;
import mx.org.kaana.keet.enums.EEstatusVales;
import mx.org.kaana.keet.enums.ETiposEntregas;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.db.dto.TcManticAlmacenesArticulosDto;
import mx.org.kaana.mantic.db.dto.TcManticAlmacenesUbicacionesDto;
import mx.org.kaana.mantic.db.dto.TcManticArticulosDto;
import mx.org.kaana.mantic.db.dto.TcManticInventariosDto;
import mx.org.kaana.mantic.db.dto.TcManticMovimientosDto;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx{
	
	private static final String GENERAL      = "GENERAL";
	private Long idVale;
	private List<DetalleVale> materiales;
	private List<Long>ids;
	private List<Long>idsGenerados;
	private String clave;
	private Double cantidadVariable;
	private List<ArchivoEvidenciaVale>documentos;	
	private Long idDesarrollo;

	public Transaccion(Long idVale, List<DetalleVale> materiales) {
		this.idVale    = idVale;
		this.materiales= materiales;
	} // Transaccion
	
	public Transaccion(Long idVale, List<Long>ids, List<Long>idsGenerados, String clave) {
		this.idVale      = idVale;
		this.ids         = ids;
		this.idsGenerados= idsGenerados;
		this.clave       = clave;
	} // Transaccion

	public Transaccion(List<ArchivoEvidenciaVale> documentos, Long idVale, String clave, Long idDesarrollo) {
		this.documentos  = documentos;
		this.idVale      = idVale;
		this.clave       = clave;
		this.idDesarrollo= idDesarrollo;
	} // Transaccion
	
	public List<Long> getIds() {
		return ids;
	}	// getIds

	public List<Long> getIdsGenerados() {
		return idsGenerados;
	}

	public String getClave() {
		return clave;
	}
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
		boolean regresar         = true;
		Map<String, Object>params= null;		
		try {
			switch(accion){
				case AGREGAR:				
					this.ids= new ArrayList<>();
					this.idsGenerados= new ArrayList<>();
					if(procesarEntrega(sesion))
						regresar= actualizaEstatusVale(sesion);
					break;						
				case DEPURAR:				
					regresar= depurarEntrega(sesion);
					break;
				case COMPLEMENTAR:					
					if(registrarEvidencia(sesion))
						regresar= actualizaStock(sesion);
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
	} // ejecutar	
	
	private boolean procesarEntrega(Session sesion) throws Exception{
		boolean regresar                     = false;
		List<TcKeetValesDetallesDto> detalles= null;
		Map<String, Object>params            = null;
		int countConfirmados                 = 0;
		int countProcesados                  = 0;
		Double cantidadPivote                = 0D;
		int count                            = 0;
		try {
			for(DetalleVale detalle: this.materiales){
				if(detalle.isCheck() && detalle.getCantidad()>0){
					cantidadPivote= detalle.getCantidad();					
					params= new HashMap<>();
					params.put("idVale", this.idVale);
					params.put("idArticulo", detalle.getIdArticulo());
					detalles= DaoFactory.getInstance().toEntitySet(sesion, TcKeetValesDetallesDto.class, "TcKeetValesDetallesDto", "pendienteEntrega", params, Constantes.SQL_TODOS_REGISTROS);
					if(!detalles.isEmpty()){
						countConfirmados= countConfirmados + detalles.size();						
						if(detalles.size()== 1){							
							countProcesados++;
							registrarEntrega(sesion, detalles.get(0));
							procesaDetalle(sesion, detalles.get(0), cantidadPivote, true);
						} // if
						else{
							this.cantidadVariable= cantidadPivote;							
							for(count=0; count<detalles.size(); count++){
								countProcesados++;
								if(this.cantidadVariable > 0D){
									registrarEntrega(sesion, detalles.get(count));
									procesaDetalle(sesion, detalles.get(count), this.cantidadVariable, (count== (detalles.size()-1)));
								}	// if
							} // for
						} // else
					} // if
					else
						throw new Exception("Ocurrio un error al procesar la entrega de materiales;");
				} // if
			} // for
			regresar= countConfirmados== countProcesados;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // procesarEntrega
	
	private void procesaDetalle(Session sesion, TcKeetValesDetallesDto detalleDto, Double cantidadPivote, boolean ultimo) throws Exception{
		ETiposEntregas tipoEntrega= null;
		try {			
			this.ids.add(detalleDto.getIdValeDetalle());
			detalleDto.setEntregado(LocalDateTime.now());
			detalleDto.setSurtido(cantidadPivote);
			tipoEntrega= toTipoEntrega(cantidadPivote, detalleDto.getCantidad()); 
			detalleDto.setIdTipoEntrega(tipoEntrega.getKey());
			switch(tipoEntrega){
				case COMPLETO:
					this.cantidadVariable= 0D;
					detalleDto.setDiferencia(cantidadPivote - detalleDto.getCantidad());
					break;
				case MAS:					 
					this.cantidadVariable= cantidadPivote - detalleDto.getCantidad();
					if(this.cantidadVariable > 0 && !ultimo){
						detalleDto.setDiferencia(0D);
						detalleDto.setSurtido(detalleDto.getCantidad());
						detalleDto.setIdTipoEntrega(ETiposEntregas.COMPLETO.getKey());
					} // if
					else{
						registrarDiferencias(sesion, detalleDto, this.cantidadVariable, ETiposEntregas.MAS);
						detalleDto.setDiferencia(cantidadPivote - detalleDto.getCantidad());
					} // else					
					break;
				case MENOS:
					this.cantidadVariable= detalleDto.getCantidad() - cantidadPivote;					
					registrarDiferencias(sesion, detalleDto, this.cantidadVariable);
					detalleDto.setDiferencia(detalleDto.getCantidad() - cantidadPivote);														
					this.cantidadVariable= 0D;
					break;
			} // switch										
			DaoFactory.getInstance().update(sesion, detalleDto);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
	} // procesaDetalle
	
	private void registrarDiferencias(Session sesion, TcKeetValesDetallesDto detalle, Double cantidad) throws Exception{
		registrarDiferencias(sesion, detalle, cantidad, ETiposEntregas.NINGUNO);
	} // registrarDiferencias
	
	private void registrarDiferencias(Session sesion, TcKeetValesDetallesDto detalle, Double cantidad, ETiposEntregas tipoEntrega) throws Exception{
		TcKeetValesDetallesDto dtoClon= null;
		Long idGenerado               = -1L;       
		try {
			dtoClon= (TcKeetValesDetallesDto) detalle.clone();
			dtoClon.setIdValeDetalle(-1L);
			dtoClon.setCantidad(cantidad);
			dtoClon.setCosto(dtoClon.getPrecio() * dtoClon.getCantidad());
			dtoClon.setSurtido(null);
			dtoClon.setEntregado(null);
			dtoClon.setDiferencia(null);
			dtoClon.setIdTipoEntrega(tipoEntrega.getKey());
			dtoClon.setRegistro(LocalDateTime.now());
			idGenerado= DaoFactory.getInstance().insert(sesion, dtoClon);			
			this.idsGenerados.add(idGenerado);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // registrarDiferencia
	
	private ETiposEntregas toTipoEntrega(Double cantidadSeleccionado, Double cantidadActual){
		ETiposEntregas regresar= null;
		if(cantidadSeleccionado.equals(cantidadActual))
			regresar= ETiposEntregas.COMPLETO;
		else if (cantidadSeleccionado > cantidadActual)
			regresar= ETiposEntregas.MAS;
		else
			regresar= ETiposEntregas.MENOS;
		return regresar;
	} // toTipoEntrega
	
	private boolean actualizaEstatusVale(Session sesion) throws Exception{
		boolean regresar = false;
		TcKeetValesDto vale= null;
		try {
			vale= (TcKeetValesDto) DaoFactory.getInstance().findById(sesion, TcKeetValesDto.class, this.idVale);
			vale.setIdValeEstatus(toIdEstatus(sesion));
			regresar= DaoFactory.getInstance().update(sesion, vale)>= 1L;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // actualizaEstatusvale
	
	private Long toIdEstatus(Session sesion) throws Exception{
		Long regresar            = null;
		Long total               = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idVale", this.idVale);
			total= DaoFactory.getInstance().toField(sesion, "TcKeetValesDetallesDto", "pendienteEntregaTotal", params, "total").toLong();
			regresar= total.equals(0) ? EEstatusVales.ENTREGADO.getKey() : EEstatusVales.INCOMPLETO.getKey();
		} // try
		catch (Exception e) {			
			throw e; 
		} // catch		
		return regresar;
	} // toIdEstatus
	
	private void registrarEntrega(Session sesion, TcKeetValesDetallesDto detalleDto) throws Exception{
		TcKeetValesDetallesEntregaDto entrega= null;
		try {
			entrega= new TcKeetValesDetallesEntregaDto();
			entrega.setCantidad(detalleDto.getCantidad());
			entrega.setCodigo(detalleDto.getCodigo());
			entrega.setCosto(detalleDto.getCosto());
			entrega.setIdArticulo(detalleDto.getIdArticulo());
			entrega.setIdMaterial(detalleDto.getIdMaterial());
			entrega.setIdVale(detalleDto.getIdVale());
			entrega.setIdValeDetalle(detalleDto.getIdValeDetalle());
			entrega.setNombre(detalleDto.getNombre());
			entrega.setPrecio(detalleDto.getPrecio());
			entrega.setRegistro(LocalDateTime.now());
			this.clave= Fecha.formatear(Fecha.FECHA_HORA_LARGA, entrega.getRegistro());
			entrega.setClave(this.clave);
			DaoFactory.getInstance().insert(sesion, entrega);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // registrarEntrega
	
	private boolean depurarEntrega(Session sesion) throws Exception{
		boolean regresar                            = true;
		List<TcKeetValesDetallesEntregaDto> entregas= null;
		TcKeetValesDetallesDto detallePivote        = null;
		Map<String, Object>params                   = null;		
		try {
			for(int count=0; count< this.idsGenerados.size(); count++)
				DaoFactory.getInstance().delete(sesion, TcKeetValesDetallesDto.class, this.idsGenerados.get(count));
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_vale=" + this.idVale + " and clave='" + this.clave + "'");
			entregas= DaoFactory.getInstance().toEntitySet(sesion, TcKeetValesDetallesEntregaDto.class, "TcKeetValesDetallesEntregaDto", "row", params);
			for(TcKeetValesDetallesEntregaDto entrega: entregas){
				detallePivote= (TcKeetValesDetallesDto) DaoFactory.getInstance().findById(sesion, TcKeetValesDetallesDto.class, entrega.getIdValeDetalle());
				detallePivote.setSurtido(null);
				detallePivote.setEntregado(null);
				detallePivote.setDiferencia(null);
				detallePivote.setIdTipoEntrega(ETiposEntregas.NINGUNO.getKey());
				DaoFactory.getInstance().update(sesion, detallePivote);
				DaoFactory.getInstance().delete(sesion, entrega);
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		return regresar;
	} // depurarEntrega
	
	private boolean registrarEvidencia(Session sesion) throws Exception{
		boolean regresar= true;
		try {
			for(ArchivoEvidenciaVale archivo: this.documentos){
				if(DaoFactory.getInstance().insert(sesion, (TcKeetValesArchivosDto)archivo)>= 1L)
					toSaveFile(archivo.getIdArchivo());
			} // for					
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarEvidencia
	
	private boolean actualizaStock(Session sesion) throws Exception{
		boolean regresar      = false;
		Long idAlmacen        = -1L;
		List<Entity> articulos= null;
		TcKeetValesDto vale   = null;
		try {
			vale= (TcKeetValesDto) DaoFactory.getInstance().findById(sesion, TcKeetValesDto.class, this.idVale);
			idAlmacen= toIdAlmacen(sesion);
			articulos= toArticulos(sesion);
			regresar= alterarStockArticulos(sesion, vale.getConsecutivo(), idAlmacen, articulos);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // actualizarStock
	
	private List<Entity> toArticulos(Session sesion) throws Exception{		
		List<Entity>regresar     = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idVale", this.idVale);
			params.put("clave", this.clave);
			regresar= DaoFactory.getInstance().toEntitySet(sesion, "VistaEntregaMaterialesDto", "evidencia", params);
	} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // afectarinventarios
	
	private Long toIdAlmacen(Session sesion) throws Exception{
		Long regresar            = null;
		Entity almacen           = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_desarrollo=" + this.idDesarrollo);
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
	
	private boolean alterarStockArticulos(Session sesion, String consecutivo, Long idAlmacen, List<Entity>articulos) throws Exception{
		TcManticAlmacenesArticulosDto almacenArticulo= null;
		TcManticArticulosDto articuloVenta           = null;		
		Map<String, Object>params                    = null;
		boolean regresar                             = false;
		int count                                    = 0; 
		Double stock                                 = 0D;
		try {			
			params= new HashMap<>();
			for(Entity articulo: articulos){
				stock= 0D;
				if(articulo.isValid()){
					params.put(Constantes.SQL_CONDICION, "id_articulo=" + articulo.getKey() + " and id_almacen=" + idAlmacen);
					almacenArticulo= (TcManticAlmacenesArticulosDto) DaoFactory.getInstance().toEntity(sesion, TcManticAlmacenesArticulosDto.class, "TcManticAlmacenesArticulosDto", "row", params);
					if(almacenArticulo!= null) {
						stock= almacenArticulo.getStock();
						almacenArticulo.setStock(almacenArticulo.getStock() - articulo.toDouble("surtido"));
						regresar= DaoFactory.getInstance().update(sesion, almacenArticulo)>= 1L;
					} // if
					else{
						stock= 0D;
						regresar= generarAlmacenArticulo(sesion, idAlmacen, articulo.getKey(), articulo.toDouble("surtido"));
					} // else					
					registrarMovimiento(sesion, consecutivo, idAlmacen, articulo.toDouble("surtido"), articulo.getKey(), stock);
					if(regresar) {
						articuloVenta= (TcManticArticulosDto) DaoFactory.getInstance().findById(sesion, TcManticArticulosDto.class, articulo.getKey());
						articuloVenta.setStock(articuloVenta.getStock() - articulo.toDouble("surtido"));
						if(DaoFactory.getInstance().update(sesion, articuloVenta)>= 1L)
							regresar= actualizaInventario(sesion, idAlmacen, articulo.getKey(), articulo.toDouble("surtido"));
					} // if
					if(regresar)
						count++;
				} // if
				else
					count++;
			} // for		
			regresar= count== articulos.size();			
		} // try		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // alterarStockArticulos
	
	private boolean actualizaInventario(Session sesion, Long idAlmacen, Long idArticulo, Double cantidad) throws Exception{
		boolean regresar                 = false;
		TcManticInventariosDto inventario= null;
		Map<String, Object>params        = null;
		try {
			params= new HashMap<>();
			params.put("idAlmacen", idAlmacen);
			params.put("idArticulo", idArticulo);
			inventario= (TcManticInventariosDto) DaoFactory.getInstance().toEntity(sesion, TcManticInventariosDto.class, "TcManticInventariosDto", "inventario", params);
			if(inventario!= null){
				inventario.setSalidas(inventario.getSalidas() + cantidad);
				inventario.setStock(inventario.getStock() - cantidad);
				regresar= DaoFactory.getInstance().update(sesion, inventario)>= 1L;
			} // if
			else{
				inventario= new TcManticInventariosDto();
				inventario.setEjercicio(Long.valueOf(Fecha.getAnioActual()));
				inventario.setEntradas(0D);
				inventario.setIdAlmacen(idAlmacen);
				inventario.setIdArticulo(idArticulo);
				inventario.setIdUsuario(JsfBase.getIdUsuario());
				inventario.setInicial(0D);
				inventario.setSalidas(cantidad);
				inventario.setStock(cantidad);
				inventario.setIdAutomatico(1L);
				regresar= DaoFactory.getInstance().insert(sesion, inventario)>= 1L;
			} // else				
		} // try
		finally {			
			Methods.clean(params);
		} // catch		
		return regresar;
	} // actualizaInventario
	
	private boolean generarAlmacenArticulo(Session sesion, Long idAlmacen, Long idArticulo, Double cantidad) throws Exception{
		boolean regresar                             = false;
		TcManticAlmacenesArticulosDto almacenArticulo= null;		
		almacenArticulo= new TcManticAlmacenesArticulosDto();
		almacenArticulo.setIdAlmacen(idAlmacen);
		almacenArticulo.setIdArticulo(idArticulo);
		almacenArticulo.setIdUsuario(JsfBase.getIdUsuario());
		almacenArticulo.setMaximo(0D);
		almacenArticulo.setMinimo(0D);
		almacenArticulo.setStock(0 - cantidad);
		almacenArticulo.setIdAlmacenUbicacion(toIdAlmacenUbicacion(sesion, idAlmacen));
		regresar= DaoFactory.getInstance().insert(sesion, almacenArticulo)>= 1L;		
		return regresar;
	} // generarAlmacenArticulo
	
	private Long toIdAlmacenUbicacion(Session sesion, Long idAlmacen) throws Exception{
		Long regresar                            = -1L;
		TcManticAlmacenesUbicacionesDto ubicacion= null;
		Map<String, Object>params                = null;		
		try {
			params= new HashMap<>();
			params.put("idAlmacen", idAlmacen);
			ubicacion= (TcManticAlmacenesUbicacionesDto) DaoFactory.getInstance().toEntity(sesion, TcManticAlmacenesUbicacionesDto.class, "TcManticAlmacenesUbicacionesDto", "general", params);
			if(ubicacion!= null)
				regresar= ubicacion.getKey();
			else{
				ubicacion= new TcManticAlmacenesUbicacionesDto();
				ubicacion.setNivel(1L);
				ubicacion.setPiso(GENERAL);
				ubicacion.setDescripcion(GENERAL);
				ubicacion.setIdUsuario(JsfBase.getIdUsuario());				
				ubicacion.setIdAlmacen(idAlmacen);
				regresar= DaoFactory.getInstance().insert(sesion, ubicacion);
			} // else
		} // try
		finally{
			Methods.clean(params);
		} // catch		
		return regresar;
	} // toIdAlmacenUbicacion	
	
	private void registrarMovimiento(Session sesion, String consecutivo, Long idAlmacen, Double cantidad, Long idArticulo, Double stock) throws Exception{
		Double calculo= Numero.toRedondearSat(stock - cantidad) ;
		TcManticMovimientosDto movimiento= new TcManticMovimientosDto(
			  consecutivo,            // String consecutivo, 
				7L,                     // Long idTipoMovimiento, 
				JsfBase.getIdUsuario(), // Long idUsuario, 
				idAlmacen,              // Long idAlmacen, 
				-1L,                    // Long idMovimiento, 
				cantidad,               // Double cantidad, 
				idArticulo,             // Long idArticulo, 
				stock,                  // Double stock, 
				calculo,                // Double calculo
				"Entrega de material [Vale[".concat(consecutivo).concat("]]")
		  );
			DaoFactory.getInstance().insert(sesion, movimiento); 
	} // registrarMovimiento
}