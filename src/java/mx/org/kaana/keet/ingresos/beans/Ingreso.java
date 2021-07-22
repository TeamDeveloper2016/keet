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

  private Double retencion1;
  private Double retencion2;
  private Double retencion3;
  private Double retencion4;
  private Double retencion5;
  private Double retencion6;
  private Double retencion7;

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
    this.retencion1= 0D;
    this.retencion2= 0D;
    this.retencion3= 0D;
    this.retencion4= 0D;
    this.retencion5= 0D;
    this.retencion6= 0D;
    this.retencion7= 0D;
  }
  
  public Double getRetencion1() {
    return retencion1;
  }

  public void setRetencion1(Double retencion1) {
    this.retencion1 = retencion1;
  }

  public Double getRetencion2() {
    return retencion2;
  }

  public void setRetencion2(Double retencion2) {
    this.retencion2 = retencion2;
  }

  public Double getRetencion3() {
    return retencion3;
  }

  public void setRetencion3(Double retencion3) {
    this.retencion3 = retencion3;
  }

  public Double getRetencion4() {
    return retencion4;
  }

  public void setRetencion4(Double retencion4) {
    this.retencion4 = retencion4;
  }

  public Double getRetencion5() {
    return retencion5;
  }

  public void setRetencion5(Double retencion5) {
    this.retencion5 = retencion5;
  }

  public Double getRetencion6() {
    return retencion6;
  }

  public void setRetencion6(Double retencion6) {
    this.retencion6 = retencion6;
  }

  public Double getRetencion7() {
    return retencion7;
  }

  public void setRetencion7(Double retencion7) {
    this.retencion7 = retencion7;
  }
  
  @Override
  public Class toHbmClass() {
    return TcManticVentasDto.class;
  }
  
}
