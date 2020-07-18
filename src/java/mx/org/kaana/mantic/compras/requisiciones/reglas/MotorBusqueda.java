package mx.org.kaana.mantic.compras.requisiciones.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.requisiciones.beans.Requisicion;
import mx.org.kaana.mantic.compras.requisiciones.beans.RequisicionProveedor;
import mx.org.kaana.mantic.db.dto.TcManticRequisicionesDto;

public class MotorBusqueda implements Serializable{

	private static final long serialVersionUID = 7005819281384345041L;
	private Long idRequisicion;

	public MotorBusqueda() {
		this(-1L);
	} // MotorBusqueda
	
	public MotorBusqueda(Long idRequisicion) {
		this.idRequisicion = idRequisicion;
	} // MotorBusqueda	
	
	public Requisicion toRequisicion() throws Exception{
		TcManticRequisicionesDto dto= null;
		Requisicion regresar= null;
		try {
			dto= (TcManticRequisicionesDto) DaoFactory.getInstance().findById(TcManticRequisicionesDto.class, this.idRequisicion);
			regresar= new Requisicion();
			regresar.setDescuentos(dto.getDescuentos());
			regresar.setIdProveedor(dto.getIdProveedor());
			regresar.setIdDesarrollo(dto.getIdDesarrollo());
			regresar.setDescuento(dto.getDescuento());
			regresar.setIdRequisicionEstatus(dto.getIdRequisicionEstatus());
			regresar.setIdRequisicion(this.idRequisicion);
			regresar.setIdSolicita(dto.getIdSolicita());
			regresar.setEjercicio(dto.getEjercicio());
			regresar.setRegistro(dto.getRegistro());
			regresar.setConsecutivo(dto.getConsecutivo());
			regresar.setFechaPedido(dto.getFechaPedido());
			regresar.setTotal(dto.getTotal());
			regresar.setIdUsuario(dto.getIdUsuario());
			regresar.setImpuestos(dto.getImpuestos());
			regresar.setSubTotal(dto.getSubTotal());
			regresar.setObservaciones(dto.getObservaciones());
			regresar.setIdEmpresa(dto.getIdEmpresa());
			regresar.setFechaEntregada(dto.getFechaEntregada());
			regresar.setOrden(dto.getOrden());
			regresar.setEntrega(dto.getFechaEntregada());
			regresar.setPedido(dto.getFechaPedido());			
			regresar.setIdContrato(dto.getIdContrato());			
			regresar.setIdPrototipo(dto.getIdPrototipo());			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toRequisicion	
	
	public List<RequisicionProveedor> toArticulosProveedor() throws Exception {
		List<RequisicionProveedor> regresar= null;
		Map<String, Object>params          = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_requisicion=" + this.idRequisicion);
			regresar= DaoFactory.getInstance().toEntitySet(RequisicionProveedor.class, "TcManticRequisicionesProveedoresDto", "row", params, Constantes.SQL_TODOS_REGISTROS);			
		} // try
		catch (Exception e) {		
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toArticulosProveedor
}
