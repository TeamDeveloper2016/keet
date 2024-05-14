package mx.org.kaana.mantic.enums;

import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.libs.reportes.IReportAttribute;

public enum EReportes implements IReportAttribute {

	ORDEN_COMPRA              ("VistaReportesOrdenesComprasDto", "reporteOrden", "Orden de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/orden", EFormatos.PDF, "OC", "/Paginas/Mantic/Compras/Ordenes/filtro"),
	ORDENES_COMPRA            ("VistaReportesOrdenesComprasDto", "ordenesCompra", "Ordenes de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/ordenesCompra", EFormatos.PDF, "OCS", "/Paginas/Mantic/Compras/Ordenes/filtro"),
	ORDEN_DETALLE             ("VistaReportesOrdenesComprasDto", "ordenDetalle", "Orden de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/ordenDetalle", EFormatos.PDF, "OCD", "/Paginas/Mantic/Compras/Ordenes/filtro"),
	ORDEN_DETALLE_DIF         ("VistaReportesOrdenesComprasDto", "diferenciasOrdenCompra", "Orden de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/ordenDetalleDiferencias", EFormatos.PDF, "OCDD", "/Paginas/Mantic/Compras/Ordenes/filtro"),
	ORDEN_DETALLE_DIF_DIF     ("VistaReportesOrdenesComprasDto", "diferenciasOrdenCompra", "Orden de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/ordenDetalleDiferencias", EFormatos.PDF, "OCDD", "/Paginas/Mantic/Compras/Ordenes/diferencias"),
	ORDEN_DETALLES_COMP       ("VistaReportesOrdenesComprasDto", "detalleCompletoOrdenCompra", "Orden de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/detCompletoOrdenCompra", EFormatos.PDF, "OCDC", "/Paginas/Mantic/Compras/Ordenes/filtro"),
	ORDEN_DETALLES_COMP2      ("VistaReportesOrdenesComprasDto", "detalleCompletoOrdenCompra", "Orden de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/detalleCompletoOrdenCompra", EFormatos.PDF, "OCDC", "/Paginas/Mantic/Compras/Ordenes/filtro"),
	TICKET_VENTA              ("VistaTicketVentaDto", "ticket", "Ticket de venta", "/Paginas/Mantic/Ventas/Caja/Reportes/ticketVenta", EFormatos.XLS, "TV", "/Paginas/Mantic/Ventas/Caja/accion"),
	NOTAS_ENTRADA             ("VistaNotasEntradasDto", "lazy", "Notas de Entrada", "/Paginas/Mantic/Inventarios/Entradas/Reportes/notasEntrada", EFormatos.PDF, "NE", "/Paginas/Mantic/Inventarios/Entradas/filtro"),
	NOTA_ENTRADA_DETALLE      ("VistaReporteNotaEntrada", "detalleNotaEntrada", "Nota de Entrada", "/Paginas/Mantic/Inventarios/Entradas/Reportes/notaEntradaDetalle", EFormatos.PDF, "NED", "/Paginas/Mantic/Inventarios/Entradas/filtro"),
	NOTA_ENTRADA_DETALLE_D    ("VistaReporteNotaEntrada", "detalleNotaEntradaDif", "Nota de Entrada", "/Paginas/Mantic/Inventarios/Entradas/Reportes/notaEntradaDetalleDif", EFormatos.PDF, "NDD", "/Paginas/Mantic/Inventarios/Entradas/filtro"),
	DEVOLUCIONES              ("VistaDevolucionesDto", "lazy", "Devoluciones", "/Paginas/Mantic/Inventarios/Devoluciones/Reportes/devoluciones", EFormatos.PDF, "D", "/Paginas/Mantic/Inventarios/Devoluciones/filtro"),
	DEVOLUCIONES_DETALLE      ("VistaReporteDevoluciones", "detalleDevolucion", "Devolución", "/Paginas/Mantic/Inventarios/Devoluciones/Reportes/devolucionDetalle", EFormatos.PDF, "DD", "/Paginas/Mantic/Inventarios/Devoluciones/filtro"),
	NOTAS_CREDITO             ("VistaCreditosNotasDto", "lazy", "Notas de credito", "/Paginas/Mantic/Inventarios/Creditos/Reportes/notasCredito", EFormatos.PDF, "NC", "/Paginas/Mantic/Inventarios/Caja/Creditos/filtro"),
	NOTA_CREDITO_DETALLE      ("VistaReporteNotasCredito", "detalleNostasCredito", "Nota de crédito", "/Paginas/Mantic/Inventarios/Creditos/Reportes/notaCreditoDetalle", EFormatos.PDF, "NCD", "/Paginas/Mantic/Inventarios/Caja/Creditos/filtro"),
  CUENTAS_POR_PAGAR         ("VistaEmpresasDto", "cuentasBusqueda", "Cuentas por pagar", "/Paginas/Mantic/Catalogos/Empresas/Cuentas/Reportes/cuentasPorPagar", EFormatos.PDF, "CXP", "/Paginas/Mantic/Catalogos/Empresas/Cuentas/saldos"),
	CUENTA_PAGAR_DETALLE      ("VistaReporteCuentaPorPagarDetalle", "pagosDeuda", "Cuenta por pagar", "/Paginas/Mantic/Catalogos/Empresas/Cuentas/Reportes/cuentaPorPagarDetalle", EFormatos.PDF, "CXPD", "/Paginas/Mantic/Catalogos/Empresas/Cuentas/saldos"),
  CUENTAS_POR_COBRAR        ("VistaClientesDto", "cuentasBusqueda", "Cuentas por cobrar", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/Reportes/cuentasPorCobrar", EFormatos.PDF, "CXC", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos"),
  PAGOS_CUENTAS_POR_COBRAR  ("VistaClientesDto", "pagosCuentasCobrar", "Pagos a cuentas por cobrar", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/Reportes/cuentasPorCobrar", EFormatos.PDF, "PCXC", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos"),
  DEUDAS_CLIENTES           ("VistaDeudasClienteDto", "row", "Estado de cuenta", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/Reportes/estadoDeCuenta", EFormatos.PDF, "ECC", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos"),
  CLIENTE_ESTADO_CUENTA     ("VistaDeudasClienteDto", "estadoDeCuenta", "Estado de cuenta", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/Reportes/estadoDeCuenta", EFormatos.PDF, "ECC", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos"),  
  DEUDAS_CLIENTES_PENDIENTES("VistaDeudasClienteDto", "pendientes", "Estado de cuenta", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/Reportes/clienteDeudasDetalle", EFormatos.PDF, "ECC", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos"),
  DEUDAS_CLIENTES_DETALLE   ("VistaDeudasClienteDto", "individual", "Detalle deuda", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/Reportes/deudaDetalleSaldo", EFormatos.PDF, "DD", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos"),
	CUENTA_COBRAR_DETALLE     ("VistaReporteCuentaPorCobrarDetalle", "cobroDeuda", "Cuenta por cobrar", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/Reportes/cuentaPorCobrarDetalle", EFormatos.PDF, "CXCD", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos"),
  REQUISICIONES             ("VistaReporteRequisiciones", "requisiciones", "Requisiciones", "/Paginas/Mantic/Compras/Requisiciones/Reportes/requisiciones", EFormatos.PDF, "RE", "/Paginas/Mantic/Catalogos/Proveedores/Requisiciones/filtro"),
	REQUISICIONES_DETALLE     ("VistaReporteRequisiciones", "requisicionDetalle", "Requisición", "/Paginas/Mantic/Compras/Requisiciones/Reportes/requisicionDetalle", EFormatos.PDF, "RED", "/Paginas/Mantic/Compras/Requisiciones/filtro"),
	REQUISICIONES_DETALLE_PROV("VistaReporteRequisiciones", "requisicionDetalle", "Requisición", "/Paginas/Mantic/Compras/Requisiciones/Reportes/requisicionDetalle", EFormatos.PDF, "RED", "/Paginas/Mantic/Compras/Requisiciones/filtro"),
  TRANSFERENCIAS            ("VistaAlmacenesTransferenciasDto", "lazy", "Transferencias", "/Paginas/Mantic/Catalogos/Almacenes/Transferencias/Reportes/transferencias", EFormatos.PDF, "T", "/Paginas/Mantic/Catalogos/Almacenes/Transferencias/filtro"),
	TRANSFERENCIAS_DETALLE    ("VistaReporteTransferenciasDto", "row", "Transferencia", "/Paginas/Mantic/Catalogos/Almacenes/Transferencias/Reportes/transferenciasDetalle", EFormatos.PDF, "TD", "/Paginas/Mantic/Catalogos/Almacenes/Transferencias/filtro"),
  UBICACIONES               ("VistaAlmacenesUbicacionesDto", "lazy", "Ubicaciones", "/Paginas/Mantic/Catalogos/Almacenes/Ubicaciones/Reportes/ubicaciones", EFormatos.PDF, "UB", "/Paginas/Mantic/Catalogos/Almacenes/Ubicaciones/filtro"),
	UBICACIONES_DETALLE       ("VistaAlmacenesUbicacionesDto", "lazyArticulo", "Ubicacion", "/Paginas/Mantic/Catalogos/Almacenes/Ubicaciones/Reportes/ubicacionesDetalle", EFormatos.PDF, "ubicaciones_detalle", "/Paginas/Mantic/Catalogos/Almacenes/Ubicaciones/filtro"),
  CONFTONTAS                ("VistaConfrontasDto", "lazy", "Confrontas", "/Paginas/Mantic/Catalogos/Almacenes/Confrontas/Reportes/confrontas", EFormatos.PDF, "CF", "/Paginas/Mantic/Catalogos/Almacenes/Confrontas/filtro"),
	CONFTONTAS_DETALLE        ("VistaConfrontasReporteDto", "consulta", "Confronta", "/Paginas/Mantic/Catalogos/Almacenes/Confrontas/Reportes/confrontaDetalle", EFormatos.PDF, "CFD", "/Paginas/Mantic/Catalogos/Almacenes/Confrontas/filtro"),
  VENTAS_A_CREDITO          ("VistaVentasDto", "lazy", "Ventas a credito", "/Paginas/Mantic/Ventas/Autorizacion/Reportes/ventasCredito", EFormatos.PDF, "VC", "/Paginas/Mantic/Ventas/Autorizacion/filtro"),
  VENTA_A_CREDITO_DETALLE   ("VistaReporteVentasDetalle", "detalleVenta", "Venta a crédito", "/Paginas/Mantic/Ventas/Autorizacion/Reportes/ventaCreditoDetalle", EFormatos.PDF, "VCD", "/Paginas/Mantic/Ventas/Autorizacion/filtro"),
  COTIZACIONES              ("VistaVentasDto", "cotizacion", "Cotizaciones", "/Paginas/Mantic/Ventas/Cotizacion/Reportes/cotizaciones", EFormatos.PDF, "CZ", "/Paginas/Mantic/Ventas/Cotizacion/filtro"),
	COTIZACION_DETALLE        ("VistaTicketVentaDto", "ventas", "Cotización", "/Paginas/Mantic/Ventas/Cotizacion/Reportes/cotizacionDetalle", EFormatos.PDF, "CZD", "/Paginas/Mantic/Ventas/Cotizacion/filtro"),
  CUENTAS                   ("VistaVentasDto", "lazy", "Cuentas abiertas", "/Paginas/Mantic/Ventas/Cuentas/Reportes/cuentas", EFormatos.PDF, "C", "/Paginas/Mantic/Ventas/Cuentas/filtro"),
	CUENTAS_DETALLE           ("VistaTicketVentaDto", "ventas", "Cuenta abierta", "/Paginas/Mantic/Ventas/Cuentas/Reportes/cuentaDetalle", EFormatos.PDF, "CD", "/Paginas/Mantic/Ventas/Cuentas/filtro"),
  CIERRES_CAJA              ("VistaCierresCajasDto", "lazy", "Cierres caja", "/Paginas/Mantic/Ventas/Caja/Cierres/Reportes/cierres", EFormatos.PDF, "CC", "/Paginas/Mantic/Ventas/Caja/Cierres/filtro"),
  ABONOS_RETIROS            ("VistaCierresCajasDto", "retiros", "Abonos y retiros de efectivo", "/Paginas/Mantic/Ventas/Caja/Cierres/Reportes/abonosRetiros", EFormatos.PDF, "AR", "/Paginas/Mantic/Ventas/Caja/Cierres/ambos"),
  APARTADOS                 ("VistaTcManticApartadosDto", "apartados", "Apartados", "/Paginas/Mantic/Ventas/Apartados/Reportes/apartados", EFormatos.PDF, "A", "/Paginas/Mantic/Ventas/Apartados/filtro"),
	APARTADO_DETALLE          ("VistaTcManticApartadosDto", "pagosApartado", "Apartado", "/Paginas/Mantic/Ventas/Apartados/Reportes/apartadoDetalle", EFormatos.PDF, "AD", "/Paginas/Mantic/Ventas/Apartados/abono"),
  FACTURAS_FICTICIAS        ("VistaFicticiasDto", "lazy", "Listado de facturación", "/Paginas/Mantic/Facturas/Reportes/facturas", EFormatos.PDF, "LF", "/Paginas/Mantic/Facturas/filtro"),
  COMPLEMENTOS_PAGO         ("VistaFicticiasDto", "lazy", "Listado de complementos de pagos", "/Paginas/Mantic/Facturas/Reportes/complementos", EFormatos.PDF, "LCP", "/Paginas/Mantic/Facturas/filtro"),
	FACTURAS_FICTICIAS_DETALLE("reporteFacturaDetalle", "ficticiaDetalle", "Facturación", "/Paginas/Mantic/Facturas/Reportes/facturaDetalle", EFormatos.PDF, "F", "/Paginas/Mantic/Facturas/filtro"),
	COMPLEMENTO_PAGO_DETALLE  ("reporteFacturaDetalle", "ficticiaDetalle", "Complemento de pago", "/Paginas/Mantic/Facturas/Reportes/complementoDetalle", EFormatos.PDF, "CMP", "/Paginas/Mantic/Facturas/filtro"),
	FACTURAS_RESUMEN          ("VistaReporteJuntaFacturas", "facturas", "Facturama", "", EFormatos.PDF, "FFS", "/Paginas/Mantic/Facturas/filtro"),
	EMPLEADOS                 ("VistaEmpleadosEstatusDto", "empleados", "Plantilla de personal por desarrollo", "/Paginas/Mantic/Catalogos/Empleados/Reportes/empleados", EFormatos.PDF, "PP", "/Paginas/Mantic/Catalogos/Empleados/filtro"),
	EMPLEADOS_DESARROLLO      ("VistaEmpleadosEstatusDto", "desarrollo", "Plantilla de personal por desarrollo", "/Paginas/Keet/Catalogos/Contratos/Personal/Reportes/empleados", EFormatos.PDF, "PPS", "/Paginas/Mantic/Catalogos/Contratos/Personal/consulta"),
	PRESTAMOS                 ("VistaPrestamosDto", "lazy", "Préstamos", "/Paginas/Keet/Prestamos/Reportes/prestamos", EFormatos.PDF, "PR", "Paginas/Keet/Prestamos/filtro"),
	ANTICIPOS                 ("VistaAnticiposDto", "lazy", "Anticipos", "/Paginas/Keet/Prestamos/Proveedor/Reportes/prestamos", EFormatos.PDF, "AN", "Paginas/Keet/Prestamos/Proveedor/filtro"),
	PRESTAMOS_PAGOS           ("VistaReportesPrestamosDto", "prestamosPagos", "Resumen de pagos", "/Paginas/Keet/Prestamos/Reportes/pagosPrestamos", EFormatos.PDF, "PPR", "/Paginas/Keet/Prestamos/filtro"),
	ANTICIPOS_PAGOS           ("VistaReportesAnticiposDto", "anticiposPagos", "Resumen de pagos de anticipos", "/Paginas/Keet/Prestamos/Proveedor/Reportes/pagosPrestamos", EFormatos.PDF, "PAN", "/Paginas/Keet/Prestamos/Proveedor/filtro"),
	RESUMEN_PRESTAMOS         ("VistaReportesPrestamosDto", "resumenPrestamos", "Resumen de los préstamos", "/Paginas/Keet/Prestamos/Reportes/resumenPrestamos", EFormatos.PDF, "RSPR", "/Paginas/Keet/Prestamos/filtro"),
	RESUMEN_ANTICIPOS         ("VistaReportesAnticiposDto", "resumenAnticipos", "Resumen de los anticipos", "/Paginas/Keet/Prestamos/Proveedor/Reportes/resumenPrestamos", EFormatos.PDF, "RSAN", "/Paginas/Keet/Prestamos/Proveedor/filtro"),
	NOMINA_SUBCONTRATISTA     ("VistaNominaReportesDto", "proveedorDetalle", "Detalle de nómina del subcontratista", "/Paginas/Keet/Nominas/Reportes/detalleSubcontratista", EFormatos.PDF, "DNS", "/Paginas/Keet/Nominas/filtro"),
  RESUMEN_NOMINA_SUBC       ("VistaNominaReportesDto", "resumen", " Resumen de nómina de los subcontratistas", "/Paginas/Keet/Nominas/Reportes/nominaSubcontratistas", EFormatos.PDF, "RSNS", "/Paginas/Keet/Nominas/filtro"),
  LISTADO_NOMINA            ("VistaNominaDto", "lazy", "Listado de nómina", "/Paginas/Keet/Nominas/Reportes/nomina", EFormatos.PDF, "LN", "/Paginas/Keet/Nominas/filtro"),
  NOMINA_LISTADO            ("VistaNominaReportesDto", "listadoNomina", "Listado nómina", "/Paginas/Keet/Nominas/Reportes/nominaEmpresa", EFormatos.PDF, "N", "/Paginas/Keet/Nominas/filtro"),
  DETALLE_NOMINA_PERSONAS   ("VistaNominaConsultasDto", "personas", "Detalle de nómina del personal", "/Paginas/Keet/Nominas/Reportes/detallePersona", EFormatos.PDF, "DNP", "/Paginas/Keet/Nominas/filtro"),
  LISTADO_NOMINA_PERSONAS   ("VistaNominaConsultasDto", "personas", "Listado de nómina del personal", "/Paginas/Keet/Nominas/Reportes/personas", EFormatos.PDF, "LNC", "/Paginas/Keet/Nominas/personas"),
  LISTADO_NOMINA_CALCULADA  ("VistaNominaConsultasDto", "calculada", "Listado de nómina del personal", "/Paginas/Keet/Nominas/Reportes/personas", EFormatos.PDF, "LNC", "/Paginas/Keet/Nominas/personas"),
  LISTADO_NOMINA_PROVEEDORES("VistaNominaConsultasDto", "proveedores", "Listado de nómina de los subcontratistas", "/Paginas/Keet/Nominas/Reportes/proveedores", EFormatos.PDF, "LNS", "/Paginas/Keet/Nominas/proveedores"),
  DESTAJOS_CONTRATISTA_X    ("VistaNominaConsultasDto", "personas", "Detalle del destajos del contratista", "/Paginas/Keet/Nominas/Reportes/detalleDestajosContratistas", EFormatos.PDF, "DDC", "/Paginas/Keet/Nominas/filtro"),
  DESTAJOS_CAT_CONTRATISTA  ("VistaNominaConsultasDto", "destajoPersona", "Destajos del contratista", "/Paginas/Keet/Catalogos/Contratos/Destajos/Reportes/detallesDestajos", EFormatos.PDF, "DC", "/Paginas/Keet/Catalogos/Contratos/Destajos/filtro"),
  DESTAJOS_CAT_RESIDENTE    ("VistaNominaConsultasDto", "destajoResidente", "Avande del residente de obra", "/Paginas/Keet/Controles/Reportes/detallesDestajos", EFormatos.PDF, "AR", "/Paginas/Keet/Controles/control"),
  DESTAJOS_CAT_SUBCONTRATISTA("VistaNominaConsultasDto", "destajoProveedor", "Destajos del subcontratista", "/Paginas/Keet/Catalogos/Contratos/Destajos/Reportes/detallesProveedor", EFormatos.PDF, "DS", "/Paginas/Keet/Catalogos/Contratos/Destajos/filtro"),
  DESTAJOS_TOTALES_SUBCONTRATISTA("VistaNominaConsultasReportesDto", "destajoProveedor", "Destajos del subcontratista", "/Paginas/Keet/Catalogos/Contratos/Destajos/Reportes/destajosProveedor", EFormatos.PDF, "DSD", "/Paginas/Keet/Catalogos/Contratos/Destajos/filtro"),
  DESTAJOS_TOTALES_CONTRATISTA("VistaNominaConsultasReportesDto", "destajoPersona", "Destajos del contratista", "/Paginas/Keet/Catalogos/Contratos/Destajos/Reportes/destajosPersona", EFormatos.PDF, "DCD", "/Paginas/Keet/Catalogos/Contratos/Destajos/filtro"),
  DESTAJOS_TOTALES_RESIDENTE("VistaNominaConsultasReportesDto", "destajoResidente", "Avance del resindente de obra", "/Paginas/Keet/Controles/Reportes/destajosPersona", EFormatos.PDF, "ARD", "/Paginas/Keet/Controles/control"),
  PUNTOS_CONTROL            ("VistaPuntosControlReporteDto", "principal", "Puntos de revisión", "/Paginas/Keet/Catalogos/PuntosControl/Reportes/puntosRevision", EFormatos.PDF, "PC", "/Paginas/Keet/Catalogos/PuntosControl/filtro"),
  ESTACIONES                ("VistaReportesEstacionesDto", "estaciones", "Estaciones", "/Paginas/Keet/Estaciones/Reportes/estaciones", EFormatos.PDF, "ES", "/Paginas/Keet/Estaciones/contrato"),
  PROTOTIPOS                ("VistaReportesEstacionesDto", "estaciones", "Estaciones", "/Paginas/Keet/Estaciones/Reportes/estaciones", EFormatos.PDF, "ESP", "/Paginas/Keet/Estaciones/contrato"),
  MATERIALES                ("VistaReportesEstacionesDto", "materiales", "Materiales", "/Paginas/Keet/Materiales/Reportes/materiales", EFormatos.PDF, "MA", "/Paginas/Keet/Materiales/filtro"),
  CONTROLES                 ("VistaReportesEstacionesDto", "controles", "Controles", "/Paginas/Keet/Estaciones/Reportes/estaciones", EFormatos.PDF, "CTR", "/Paginas/Keet/Estaciones/contrato"),
  CAJA_CHICA                ("VistaReportesCajaChicaDto", "detalle", "Listado de gastos de caja chica", "/Paginas/Keet/CajaChica/Reportes/detalleCajaChica", EFormatos.PDF, "CCH", "/Paginas/Keet/CajaChica/filtro"),
  ESTIMACION_SALDOS         ("VistaEstimacionesDto", "saldos", "Estado de cuenta", "/Paginas/Keet/Estimaciones/Reportes/estimacion", EFormatos.PDF, "EC", "/Paginas/Keet/Estimaciones/saldos"),
  CONTRATO_RESUMEN          ("VistaContratosDto", "findDesarrollo", "Resumen de estimación de obra", "/Paginas/Contenedor/Reportes/resumen", EFormatos.PDF, "RS", "/Paginas/Contenedor/resumen"),
  VALE_ALMACEN              ("VistaBoletasDto", "folio", "Vale de almacen", "/Paginas/Keet/Vales/Reportes/vale", EFormatos.PDF, "VA", "/Paginas/Keet/Vales/filtro"),
  ANALISIS_COSTOS           ("VistaCostosDto", "lazy", "Análsis de costos de los contratos", "/Paginas/Keet/Costos/Reportes/costos", EFormatos.PDF, "AC", "/Paginas/Keet/Costos/costos");
			
	private final String proceso;
  private final String idXml;
  private final String titulo; 
  private final String jrxml;
  private final EFormatos formato;
  private final String nombre;
  private final String regresar;

	private EReportes(String proceso, String idXml, String titulo, String jrxml, EFormatos formato, String nombre, String regresar) {
    this.proceso = proceso;
    this.idXml   = idXml;
    this.titulo  = titulo;
    this.jrxml   = jrxml;
    this.formato = formato;
    this.nombre  = nombre;
    this.regresar= regresar;
  }

	@Override
  public String getIdentificador() {
    return EReportes.class.getName();
  }

  @Override
  public String getProceso() {
    return proceso;
  }

  @Override
  public String getIdXml() {
    return idXml;
  }

  @Override
  public String getJrxml() {
    return jrxml;
  }

  @Override
  public EFormatos getFormato() {
    return formato;
  }

  @Override
  public String getNombre() {
    return nombre;
  }

  @Override
  public String getTitulo() {
    return titulo;
  }

  @Override
  public String getRegresar() {
    return regresar;
  }
  
  public Boolean getAutomatico() {
    return Boolean.TRUE;
	}
}
