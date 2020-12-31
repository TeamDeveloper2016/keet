package mx.org.kaana.mantic.enums;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 19/06/2018
 *@time 07:51:53 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public enum ETipoMovimiento {
  
	INCIDENCIAS("incidencia(s)", "tc_mantic_incidentes", "idIncidente", "VistaIncidenciasDto"), 
	ORDENES_COMPRAS("orden(es) de compra(s)", "tc_mantic_ordenes", "idOrdenCompra", "VistaOrdenesComprasDto"), 
	NOTAS_ENTRADAS("nota(s) de entrada(s)", "tc_mantic_notas", "idNotaEntrada", "VistaNotasEntradasDto"), 
	VENTAS("venta(s)", "tc_mantic_ventas", "idVenta", "VistaVentasDto"), 
	SERVICIOS("servicio(s)", "tc_mantic_servicios", "idServicio", "VistaTallerServiciosDto"), 
	DEVOLUCIONES("devolucion(es)", "tc_mantic_devoluciones", "idDevolucion", "VistaDevolucionesDto"),
	NOTAS_CREDITOS("nota(s) de credito(s)", "tc_mantic_creditos_notas", "idCreditoNota", "VistaNotasCreditosDto"),
	CIERRES_CAJA("cierre(s) de caja", "tc_mantic_cierres", "idCierre", "VistaCierresCajasDto"),
	FACTURAS_FICTICIAS("factura(s)", "tc_mantic_ficticias", "idFicticia", "VistaFicticiasDto"),
	TRANSFERENCIAS("transferencia(s)", "tc_mantic_transferencias", "idTransferencia", "VistaAlmacenesTransferenciasDto"),
	INGRESOS("factura(s)", "tc_keet_ingresos", "idIngreso", "VistaIngresosDto");
	 
	private final String title;
	private final String table;
	private final String idKey;
	private final String proceso;
	
	private ETipoMovimiento(String title, String table, String idKey, String proceso) {
		this.title= title;
		this.table= table;
		this.idKey= idKey;
		this.proceso= proceso;
	}
	
	public String getTitle() {
		return "Movimientos generados de las ".concat(this.title);
	}
	
	public String getTable() {
		return this.table;
	}
	
	public String getIdKey() {
		return this.idKey;
	}
	
	public String getProceso() {
		return proceso;
	}
	
}
