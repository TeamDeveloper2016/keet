package mx.org.kaana.mantic.enums;

import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.libs.reportes.IReportAttribute;

public enum EReportes implements IReportAttribute{

	ORDEN_COMPRA              ("VistaReportesOrdenesComprasDto", "reporteOrden", "Orden de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/orden", EFormatos.PDF, "orden_de_compra", "/Paginas/Mantic/Compras/Ordenes/filtro"),
	ORDENES_COMPRA            ("VistaReportesOrdenesComprasDto", "ordenesCompra", "Ordenes de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/ordenesCompra", EFormatos.PDF, "ordenes_de_compra", "/Paginas/Mantic/Compras/Ordenes/filtro"),
	ORDEN_DETALLE             ("VistaReportesOrdenesComprasDto", "ordenDetalle", "Orden de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/ordenDetalle", EFormatos.PDF, "orden_de_compra_detalle", "/Paginas/Mantic/Compras/Ordenes/filtro"),
	ORDEN_DETALLE_DIF         ("VistaReportesOrdenesComprasDto", "diferenciasOrdenCompra", "Orden de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/ordenDetalleDiferencias", EFormatos.PDF, "orden_de_compra_diferencias_detalle", "/Paginas/Mantic/Compras/Ordenes/filtro"),
	ORDEN_DETALLE_DIF_DIF     ("VistaReportesOrdenesComprasDto", "diferenciasOrdenCompra", "Orden de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/ordenDetalleDiferencias", EFormatos.PDF, "orden_de_compra_diferencias_detalle", "/Paginas/Mantic/Compras/Ordenes/diferencias"),
	ORDEN_DETALLES_COMP       ("VistaReportesOrdenesComprasDto", "detalleCompletoOrdenCompra", "Orden de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/detCompletoOrdenCompra", EFormatos.PDF, "orden_de_compra_detalles_completos", "/Paginas/Mantic/Compras/Ordenes/filtro"),
	ORDEN_DETALLES_COMP2      ("VistaReportesOrdenesComprasDto", "detalleCompletoOrdenCompra", "Orden de compra", "/Paginas/Mantic/Compras/Ordenes/Reportes/detalleCompletoOrdenCompra", EFormatos.PDF, "orden_de_compra_detalles_completos", "/Paginas/Mantic/Compras/Ordenes/filtro"),
	TICKET_VENTA              ("VistaTicketVentaDto", "ticket", "Ticket de venta", "/Paginas/Mantic/Ventas/Caja/Reportes/ticketVenta", EFormatos.XLS, "ticket_venta", "/Paginas/Mantic/Ventas/Caja/accion"),
	NOTAS_ENTRADA             ("VistaNotasEntradasDto", "lazy", "Notas de Entrada", "/Paginas/Mantic/Inventarios/Entradas/Reportes/notasEntrada", EFormatos.PDF, "notas_de_entrada", "/Paginas/Mantic/Inventarios/Entradas/filtro"),
	NOTA_ENTRADA_DETALLE      ("VistaReporteNotaEntrada", "detalleNotaEntrada", "Nota de Entrada", "/Paginas/Mantic/Inventarios/Entradas/Reportes/notaEntradaDetalle", EFormatos.PDF, "nota_de_entrada_detalle", "/Paginas/Mantic/Inventarios/Entradas/filtro"),
	NOTA_ENTRADA_DETALLE_D    ("VistaReporteNotaEntrada", "detalleNotaEntradaDif", "Nota de Entrada", "/Paginas/Mantic/Inventarios/Entradas/Reportes/notaEntradaDetalleDif", EFormatos.PDF, "nota_de_entrada_detalle_diferencias", "/Paginas/Mantic/Inventarios/Entradas/filtro"),
	DEVOLUCIONES              ("VistaDevolucionesDto", "lazy", "Devoluciones", "/Paginas/Mantic/Inventarios/Devoluciones/Reportes/devoluciones", EFormatos.PDF, "devoluciones", "/Paginas/Mantic/Inventarios/Devoluciones/filtro"),
	DEVOLUCIONES_DETALLE      ("VistaReporteDevoluciones", "detalleDevolucion", "Devoluci�n", "/Paginas/Mantic/Inventarios/Devoluciones/Reportes/devolucionDetalle", EFormatos.PDF, "devolucion_detalle", "/Paginas/Mantic/Inventarios/Devoluciones/filtro"),
	NOTAS_CREDITO             ("VistaCreditosNotasDto", "lazy", "Notas de credito", "/Paginas/Mantic/Inventarios/Creditos/Reportes/notasCredito", EFormatos.PDF, "notas_de_credito", "/Paginas/Mantic/Inventarios/Caja/Creditos/filtro"),
	NOTA_CREDITO_DETALLE      ("VistaReporteNotasCredito", "detalleNostasCredito", "Nota de cr�dito", "/Paginas/Mantic/Inventarios/Creditos/Reportes/notaCreditoDetalle", EFormatos.PDF, "nota_de_credito_detalle", "/Paginas/Mantic/Inventarios/Caja/Creditos/filtro"),
  CUENTAS_POR_PAGAR         ("VistaEmpresasDto", "cuentasBusqueda", "Cuentas por pagar", "/Paginas/Mantic/Catalogos/Empresas/Cuentas/Reportes/cuentasPorPagar", EFormatos.PDF, "cuentas_por_pagar", "/Paginas/Mantic/Catalogos/Empresas/Cuentas/saldos"),
	CUENTA_PAGAR_DETALLE      ("VistaReporteCuentaPorPagarDetalle", "pagosDeuda", "Cuenta por pagar", "/Paginas/Mantic/Catalogos/Empresas/Cuentas/Reportes/cuentaPorPagarDetalle", EFormatos.PDF, "cuenta_por_pagar_detalle", "/Paginas/Mantic/Catalogos/Empresas/Cuentas/saldos"),
  CUENTAS_POR_COBRAR        ("VistaClientesDto", "cuentasBusqueda", "Cuentas por cobrar", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/Reportes/cuentasPorCobrar", EFormatos.PDF, "cuentas_por_cobrar", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos"),
  DEUDAS_CLIENTES           ("VistaDeudasClienteDto", "row", "Estado de cuenta", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/Reportes/clienteDeudasDetalle", EFormatos.PDF, "estado_cueta_cliente", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos"),
  DEUDAS_CLIENTES_PENDIENTES("VistaDeudasClienteDto", "pendientes", "Estado de cuenta", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/Reportes/clienteDeudasDetalle", EFormatos.PDF, "estado_cueta_cliente", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos"),
  DEUDAS_CLIENTES_DETALLE   ("VistaDeudasClienteDto", "individual", "Detalle deuda", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/Reportes/deudaDetalleSaldo", EFormatos.PDF, "detalle_deuda", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos"),
	CUENTA_COBRAR_DETALLE     ("VistaReporteCuentaPorCobrarDetalle", "cobroDeuda", "Cuenta por cobrar", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/Reportes/cuentaPorCobrarDetalle", EFormatos.PDF, "cuenta_por_cobrar_detalle", "/Paginas/Mantic/Catalogos/Clientes/Cuentas/saldos"),
  REQUISICIONES             ("VistaReporteRequisiciones", "requisiciones", "Requisiciones", "/Paginas/Mantic/Compras/Requisiciones/Reportes/requisiciones", EFormatos.PDF, "requisiciones", "/Paginas/Mantic/Catalogos/Proveedores/Requisiciones/filtro"),
	REQUISICIONES_DETALLE     ("VistaReporteRequisiciones", "requisicionDetalle", "Requisici�n", "/Paginas/Mantic/Compras/Requisiciones/Reportes/requisicionDetalle", EFormatos.PDF, "requisicion_detalle", "/Paginas/Mantic/Compras/Requisiciones/filtro"),
	REQUISICIONES_DETALLE_PROV("VistaReporteRequisiciones", "requisicionDetalle", "Requisici�n", "/Paginas/Mantic/Compras/Requisiciones/Reportes/requisicionDetalle", EFormatos.PDF, "requisicion_detalle", "/Paginas/Mantic/Compras/Requisiciones/filtro"),
  TRANSFERENCIAS            ("VistaAlmacenesTransferenciasDto", "lazy", "Transferencias", "/Paginas/Mantic/Catalogos/Almacenes/Transferencias/Reportes/transferencias", EFormatos.PDF, "transferencias", "/Paginas/Mantic/Catalogos/Almacenes/Transferencias/filtro"),
	TRANSFERENCIAS_DETALLE    ("VistaReporteTransferenciasDto", "row", "Transferencia", "/Paginas/Mantic/Catalogos/Almacenes/Transferencias/Reportes/transferenciasDetalle", EFormatos.PDF, "transferencias_detalle", "/Paginas/Mantic/Catalogos/Almacenes/Transferencias/filtro"),
  UBICACIONES               ("VistaAlmacenesUbicacionesDto", "lazy", "Ubicaciones", "/Paginas/Mantic/Catalogos/Almacenes/Ubicaciones/Reportes/ubicaciones", EFormatos.PDF, "ubicaciones", "/Paginas/Mantic/Catalogos/Almacenes/Ubicaciones/filtro"),
	UBICACIONES_DETALLE       ("VistaAlmacenesUbicacionesDto", "lazyArticulo", "Ubicacion", "/Paginas/Mantic/Catalogos/Almacenes/Ubicaciones/Reportes/ubicacionesDetalle", EFormatos.PDF, "ubicaciones_detalle", "/Paginas/Mantic/Catalogos/Almacenes/Ubicaciones/filtro"),
  CONFTONTAS                ("VistaConfrontasDto", "lazy", "Confrontas", "/Paginas/Mantic/Catalogos/Almacenes/Confrontas/Reportes/confrontas", EFormatos.PDF, "confrontas", "/Paginas/Mantic/Catalogos/Almacenes/Confrontas/filtro"),
	CONFTONTAS_DETALLE        ("VistaConfrontasReporteDto", "consulta", "Confronta", "/Paginas/Mantic/Catalogos/Almacenes/Confrontas/Reportes/confrontaDetalle", EFormatos.PDF, "confronta_detalle", "/Paginas/Mantic/Catalogos/Almacenes/Confrontas/filtro"),
  VENTAS_A_CREDITO          ("VistaVentasDto", "lazy", "Ventas a credito", "/Paginas/Mantic/Ventas/Autorizacion/Reportes/ventasCredito", EFormatos.PDF, "ventas_a_credito", "/Paginas/Mantic/Ventas/Autorizacion/filtro"),
  VENTA_A_CREDITO_DETALLE   ("VistaReporteVentasDetalle", "detalleVenta", "Venta a cr�dito", "/Paginas/Mantic/Ventas/Autorizacion/Reportes/ventaCreditoDetalle", EFormatos.PDF, "venta_a_credito_detalle", "/Paginas/Mantic/Ventas/Autorizacion/filtro"),
  COTIZACIONES              ("VistaVentasDto", "cotizacion", "Cotizaciones", "/Paginas/Mantic/Ventas/Cotizacion/Reportes/cotizaciones", EFormatos.PDF, "cotizaciones", "/Paginas/Mantic/Ventas/Cotizacion/filtro"),
	COTIZACION_DETALLE        ("VistaTicketVentaDto", "ventas", "Cotizaci�n", "/Paginas/Mantic/Ventas/Cotizacion/Reportes/cotizacionDetalle", EFormatos.PDF, "cotizacion_detalle", "/Paginas/Mantic/Ventas/Cotizacion/filtro"),
  CUENTAS                   ("VistaVentasDto", "lazy", "Cuentas abiertas", "/Paginas/Mantic/Ventas/Cuentas/Reportes/cuentas", EFormatos.PDF, "cuentas", "/Paginas/Mantic/Ventas/Cuentas/filtro"),
	CUENTAS_DETALLE           ("VistaTicketVentaDto", "ventas", "Cuenta abierta", "/Paginas/Mantic/Ventas/Cuentas/Reportes/cuentaDetalle", EFormatos.PDF, "cuenta_detalle", "/Paginas/Mantic/Ventas/Cuentas/filtro"),
  CIERRES_CAJA              ("VistaCierresCajasDto", "lazy", "Cierres caja", "/Paginas/Mantic/Ventas/Caja/Cierres/Reportes/cierres", EFormatos.PDF, "cierres_caja", "/Paginas/Mantic/Ventas/Caja/Cierres/filtro"),
  ABONOS_RETIROS            ("VistaCierresCajasDto", "retiros", "Abonos y retiros de efectivo", "/Paginas/Mantic/Ventas/Caja/Cierres/Reportes/abonosRetiros", EFormatos.PDF, "abonos_retiros", "/Paginas/Mantic/Ventas/Caja/Cierres/ambos"),
  APARTADOS                 ("VistaTcManticApartadosDto", "apartados", "Apartados", "/Paginas/Mantic/Ventas/Apartados/Reportes/apartados", EFormatos.PDF, "apartados", "/Paginas/Mantic/Ventas/Apartados/filtro"),
	APARTADO_DETALLE          ("VistaTcManticApartadosDto", "pagosApartado", "Apartado", "/Paginas/Mantic/Ventas/Apartados/Reportes/apartadoDetalle", EFormatos.PDF, "apartado_detalle", "/Paginas/Mantic/Ventas/Apartados/abono"),
  FACTURAS_FICTICIAS        ("VistaFicticiasDto", "lazy", "Listado de facturaci�n", "/Paginas/Mantic/Facturas/Reportes/facturas", EFormatos.PDF, "listado_de_facturacion", "/Paginas/Mantic/Facturas/filtro"),
  COMPLEMENTOS_PAGO         ("VistaFicticiasDto", "lazy", "Listado de complementos de pagos", "/Paginas/Mantic/Facturas/Reportes/complementos", EFormatos.PDF, "listado_de_complementos_pago", "/Paginas/Mantic/Facturas/filtro"),
	FACTURAS_FICTICIAS_DETALLE("reporteFacturaDetalle", "ficticiaDetalle", "Facturaci�n", "/Paginas/Mantic/Facturas/Reportes/facturaDetalle", EFormatos.PDF, "facturacion", "/Paginas/Mantic/Facturas/filtro"),
	COMPLEMENTO_PAGO_DETALLE  ("reporteFacturaDetalle", "ficticiaDetalle", "Complemento de pago", "/Paginas/Mantic/Facturas/Reportes/complementoDetalle", EFormatos.PDF, "complementoPago", "/Paginas/Mantic/Facturas/filtro"),
	FACTURAS_RESUMEN          ("VistaReporteJuntaFacturas", "facturas", "Facturama", "", EFormatos.PDF, "facturama_facturas_seleccionadas", "/Paginas/Mantic/Facturas/filtro"),
	EMPLEADOS                 ("VistaEmpleadosEstatusDto", "empleados", "Plantilla de personal por desarrollo", "/Paginas/Mantic/Catalogos/Empleados/Reportes/empleados", EFormatos.PDF, "plantilla_de_personal", "/Paginas/Mantic/Catalogos/Empleados/filtro"),
	EMPLEADOS_DESARROLLO      ("VistaEmpleadosEstatusDto", "desarrollo", "Plantilla de personal por desarrollo", "/Paginas/Keet/Catalogos/Contratos/Personal/Reportes/empleados", EFormatos.PDF, "plantilla_de_personal", "/Paginas/Mantic/Catalogos/Contratos/Personal/consulta"),
	PRESTAMOS                 ("VistaPrestamosDto", "lazy", "Pr�stamos", "/Paginas/Keet/Prestamos/Reportes/prestamos", EFormatos.PDF, "prestamos", "Paginas/Keet/Prestamos/filtro"),
	PRESTAMOS_PAGOS           ("VistaReportesPrestamosDto", "prestamosPagos", "Resumen de pagos", "/Paginas/Keet/Prestamos/Reportes/pagosPrestamos", EFormatos.PDF, "pagos_prestamo", "/Paginas/Keet/Prestamos/filtro"),
	RESUMEN_PRESTAMOS         ("VistaReportesPrestamosDto", "resumenPrestamos", "Resumen de los pr�stamos", "/Paginas/Keet/Prestamos/Reportes/resumenPrestamos", EFormatos.PDF, "resumen_prestamos", "/Paginas/Keet/Prestamos/filtro"),
	NOMINA_SUBCONTRATISTA     ("VistaNominaReportesDto", "proveedorDetalle", "Detalle de n�mina del subcontratista", "/Paginas/Keet/Nominas/Reportes/detalleSubcontratista", EFormatos.PDF, "detalle_nomina_subcontratista", "/Paginas/Keet/Nominas/filtro"),
  RESUMEN_NOMINA_SUBC       ("VistaNominaReportesDto", "resumen", " Resumen de n�mina de los subcontratistas", "/Paginas/Keet/Nominas/Reportes/nominaSubcontratistas", EFormatos.PDF, "resumen_nomina_subcontratista", "/Paginas/Keet/Nominas/filtro"),
  LISTADO_NOMINA            ("VistaNominaDto", "lazy", "Listado de n�mina", "/Paginas/Keet/Nominas/Reportes/nomina", EFormatos.PDF, "listado_nomina", "/Paginas/Keet/Nominas/filtro"),
  DETALLE_NOMINA_PERSONAS   ("VistaNominaConsultasDto", "personas", "Detalle de n�mina del personal", "/Paginas/Keet/Nominas/Reportes/detallePersona", EFormatos.PDF, "detalle_nomina_persona", "/Paginas/Keet/Nominas/filtro"),
  LISTADO_NOMINA_PERSONAS   ("VistaNominaConsultasDto", "personas", "Listado de n�mina del personal", "/Paginas/Keet/Nominas/Reportes/personas", EFormatos.PDF, "listado_nomina_contratistas", "/Paginas/Keet/Nominas/personas"),
  LISTADO_NOMINA_PROVEEDORES("VistaNominaConsultasDto", "proveedores", "Listado de n�mina de los subcontratistas", "/Paginas/Keet/Nominas/Reportes/proveedores", EFormatos.PDF, "listado_nomina_subcontratistas", "/Paginas/Keet/Nominas/proveedores"),
  DESTAJOS_CONTRATISTA      ("VistaNominaConsultasDto", "personas", "Detalle del destajos del contratista", "/Paginas/Keet/Nominas/Reportes/detalleDestajosContratistas", EFormatos.PDF, "detalle_destajos_contratista", "/Paginas/Keet/Nominas/filtro"),
  DESTAJOS_CAT_CONTRATISTA  ("VistaNominaConsultasDto", "destajoPersona", "Destajos del contratista", "/Paginas/Keet/Catalogos/Contratos/Destajos/Reportes/detallesDestajos", EFormatos.PDF, "destajos_contratista", "/Paginas/Keet/Catalogos/Contratos/Destajos/filtro"),
  DESTAJOS_CAT_RESIDENTE    ("VistaNominaConsultasDto", "destajoResidente", "Avande del residente de obra", "/Paginas/Keet/Controles/Reportes/detallesDestajos", EFormatos.PDF, "avance_residentes", "/Paginas/Keet/Controles/control"),
  DESTAJOS_CAT_SUBCONTRATISTA("VistaNominaConsultasDto", "destajoProveedor", "Destajos del subcontratista", "/Paginas/Keet/Catalogos/Contratos/Destajos/Reportes/detallesDestajos", EFormatos.PDF, "destajos_subcontratista", "/Paginas/Keet/Catalogos/Contratos/Destajos/filtro"),
  DESTAJOS_TOTALES_SUBCONTRATISTA("VistaNominaConsultasReportesDto", "destajoProveedor", "Destajos del subcontratista", "/Paginas/Keet/Catalogos/Contratos/Destajos/Reportes/destajosPersona", EFormatos.PDF, "destajos_subcontratista_desarrollos", "/Paginas/Keet/Catalogos/Contratos/Destajos/filtro"),
  DESTAJOS_TOTALES_CONTRATISTA("VistaNominaConsultasReportesDto", "destajoPersona", "Destajos del contratista", "/Paginas/Keet/Catalogos/Contratos/Destajos/Reportes/destajosPersona", EFormatos.PDF, "destajos_contratista_desarrollos", "/Paginas/Keet/Catalogos/Contratos/Destajos/filtro"),
  DESTAJOS_TOTALES_RESIDENTE("VistaNominaConsultasReportesDto", "destajoResidente", "Avance del resindente de obra", "/Paginas/Keet/Controles/Reportes/destajosPersona", EFormatos.PDF, "avances_residentes_desarrollos", "/Paginas/Keet/Controles/control"),
  NOMINA_LISTADO("VistaNominaReportesDto", "listadoNomina", "Listado n�mina", "/Paginas/Keet/Nominas/Reportes/nominaEmpresa", EFormatos.PDF, "nomina", "/Paginas/Keet/Nominas/filtro"),
  PUNTOS_CONTROL("VistaPuntosControlReporteDto", "principal", "Puntos de revisi�n", "/Paginas/Keet/Catalogos/PuntosControl/Reportes/puntosRevision", EFormatos.PDF, "puntos_control", "/Paginas/Keet/Catalogos/PuntosControl/filtro"),
  ESTACIONES("VistaReportesEstaciones", "estaciones", "Estaciones", "/Paginas/Keet/Estaciones/Reportes/estaciones", EFormatos.PDF, "estaciones", "/Paginas/Keet/Estaciones/contrato"),
  PROTOTIPOS("VistaReportesEstaciones", "estaciones", "Estaciones", "/Paginas/Keet/Estaciones/Reportes/estaciones", EFormatos.PDF, "estaciones_prototipo", "/Paginas/Keet/Estaciones/contrato"),
  MATERIALES("VistaReportesEstaciones", "materiales", "Materiales", "/Paginas/Keet/Materiales/Reportes/materiales", EFormatos.PDF, "materiales", "/Paginas/Keet/Materiales/filtro"),
  CONTROLES("VistaReportesEstaciones", "controles", "Controles", "/Paginas/Keet/Estaciones/Reportes/estaciones", EFormatos.PDF, "controles", "/Paginas/Keet/Estaciones/contrato");
		
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
