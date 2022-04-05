package mx.org.kaana.keet.ingresos.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import mx.org.kaana.mantic.db.dto.TcManticVentasDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 21/07/2021
 *@time 09:07:50 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Ingreso extends TcManticVentasDto implements Serializable {

  private static final long serialVersionUID = 4832430489877925905L;

  public Ingreso() {
    this(-1L);
  }

  public Ingreso(Long key) {
    super(key);
    init();
  }

  public Ingreso(Double descuentos, Long idFactura, Long idCredito, String extras, Double total, Long idAlmacen, Double tipoDeCambio, Long orden, Long idAutorizar, Long idCliente, String descuento, Long ejercicio, Long consecutivo, Long idUsuario, Double impuestos, Long idUsoCfdi, Long idSinIva, Double subTotal, String observaciones, Long idEmpresa, Long idVenta, LocalDate dia, Long idVentaEstatus, Long idFacturar) {
    super(descuentos, idFactura, idCredito, extras, total, idAlmacen, tipoDeCambio, orden, idAutorizar, idCliente, descuento, ejercicio, consecutivo, idUsuario, impuestos, idUsoCfdi, idSinIva, subTotal, observaciones, idEmpresa, idVenta, dia, idVentaEstatus, idFacturar);
    init();
  }

  public Ingreso(Double descuentos, Long idFactura, Long idCredito, String extras, Double global, Double utilidad, Double total, Long idAlmacen, Double tipoDeCambio, Long orden, Long idAutorizar, Long idCliente, String descuento, Long ejercicio, Long consecutivo, Long idUsuario, Double impuestos, Long idUsoCfdi, Long idSinIva, Double subTotal, String observaciones, Long idEmpresa, Long idVenta, LocalDate dia, Long idVentaEstatus, String cotizacion, String ticket, Long ccotizacion, Long cticket, LocalDate vigencia, Long idManual, Long idFacturar) {
    super(descuentos, idFactura, idCredito, extras, global, utilidad, total, idAlmacen, tipoDeCambio, orden, idAutorizar, idCliente, descuento, ejercicio, consecutivo, idUsuario, impuestos, idUsoCfdi, idSinIva, subTotal, observaciones, idEmpresa, idVenta, dia, idVentaEstatus, cotizacion, ticket, ccotizacion, cticket, vigencia, idManual, idFacturar);
    init();
  }

  public Ingreso(Double descuentos, Long idFactura, Long idCredito, String extras, Double global, Double utilidad, Double total, Long idAlmacen, Double tipoDeCambio, Long orden, Long idAutorizar, Long idCliente, String descuento, Long ejercicio, Long consecutivo, Long idUsuario, Double impuestos, Long idUsoCfdi, Long idSinIva, Double subTotal, String observaciones, Long idEmpresa, Long idVenta, LocalDate dia, Long idVentaEstatus, String cotizacion, String ticket, Long ccotizacion, Long cticket, LocalDate vigencia, Long idManual, Long idFacturar, LocalDateTime cobro) {
    super(descuentos, idFactura, idCredito, extras, global, utilidad, total, idAlmacen, tipoDeCambio, orden, idAutorizar, idCliente, descuento, ejercicio, consecutivo, idUsuario, impuestos, idUsoCfdi, idSinIva, subTotal, observaciones, idEmpresa, idVenta, dia, idVentaEstatus, cotizacion, ticket, ccotizacion, cticket, vigencia, idManual, idFacturar, cobro);
    init();
  }

  public Ingreso(Double descuentos, Long idFactura, Long idCredito, String extras, Double global, Double utilidad, Double total, Long idAlmacen, Double tipoDeCambio, Long orden, Long idAutorizar, Long idCliente, String descuento, Long ejercicio, Long consecutivo, Long idUsuario, Double impuestos, Long idUsoCfdi, Long idSinIva, Double subTotal, String observaciones, Long idEmpresa, Long idVenta, LocalDate dia, Long idVentaEstatus, String cotizacion, String ticket, Long ccotizacion, Long cticket, LocalDate vigencia, Long idManual, Long idFacturar, LocalDateTime cobro, Long idClienteDomicilio, Long idTipoMedioPago, Long idTipoPago, Long idBanco, String referencia, Long idTipoDocumento, Long candado) {
    super(descuentos, idFactura, idCredito, extras, global, utilidad, total, idAlmacen, tipoDeCambio, orden, idAutorizar, idCliente, descuento, ejercicio, consecutivo, idUsuario, impuestos, idUsoCfdi, idSinIva, subTotal, observaciones, idEmpresa, idVenta, dia, idVentaEstatus, cotizacion, ticket, ccotizacion, cticket, vigencia, idManual, idFacturar, cobro, idClienteDomicilio, idTipoMedioPago, idTipoPago, idBanco, referencia, idTipoDocumento, candado);
    init();
  }

  public Ingreso(Double descuentos, Long idFactura, Long idCredito, String extras, Double global, Double utilidad, Double total, Long idAlmacen, Double tipoDeCambio, Long orden, Long idAutorizar, Long idCliente, String descuento, Long ejercicio, Long consecutivo, Long idUsuario, Double impuestos, Long idUsoCfdi, Long idSinIva, Double subTotal, String observaciones, Long idEmpresa, Long idVenta, LocalDate dia, Long idVentaEstatus, String cotizacion, String ticket, Long ccotizacion, Long cticket, LocalDate vigencia, Long idManual, Long idFacturar, LocalDateTime cobro, Long idClienteDomicilio, Long idTipoMedioPago, Long idTipoPago, Long idBanco, String referencia, Long idTipoDocumento, Long candado, Long idDesarrollo, Long idContrato, Long idExtra) {
    super(descuentos, idFactura, idCredito, extras, global, utilidad, total, idAlmacen, tipoDeCambio, orden, idAutorizar, idCliente, descuento, ejercicio, consecutivo, idUsuario, impuestos, idUsoCfdi, idSinIva, subTotal, observaciones, idEmpresa, idVenta, dia, idVentaEstatus, cotizacion, ticket, ccotizacion, cticket, vigencia, idManual, idFacturar, cobro, idClienteDomicilio, idTipoMedioPago, idTipoPago, idBanco, referencia, idTipoDocumento, candado, idDesarrollo, idContrato, idExtra);
    init();
  }
  
  private void init() {
  }
  
  @Override
  public Class toHbmClass() {
    return TcManticVentasDto.class;
  }
  
}
