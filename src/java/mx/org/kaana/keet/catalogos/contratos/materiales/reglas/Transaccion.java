package mx.org.kaana.keet.catalogos.contratos.materiales.reglas;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.catalogos.contratos.vales.beans.DetalleVale;
import mx.org.kaana.keet.db.dto.TcKeetValesDetallesDto;
import mx.org.kaana.keet.db.dto.TcKeetValesDetallesEntregaDto;
import mx.org.kaana.keet.db.dto.TcKeetValesDto;
import mx.org.kaana.keet.enums.EEstatusVales;
import mx.org.kaana.keet.enums.ETiposEntregas;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.reflection.Methods;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx{
	
	private Long idVale;
	private List<DetalleVale> materiales;
	private List<Long>ids;
	private List<Long>idsGenerados;
	private String clave;
	private Double cantidadVariable;

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
				case MODIFICAR:					
					break;
				case DEPURAR:				
					regresar= depurarEntrega(sesion);
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
						registrarDiferencias(sesion, detalleDto, this.cantidadVariable);
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
			dtoClon.setIdTipoEntrega(ETiposEntregas.NINGUNO.getKey());
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
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		return regresar;
	} // depurarEntrega
}