package mx.org.kaana.mantic.inventarios.entradas.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.kajool.db.comun.operation.IActions;
import mx.org.kaana.libs.reflection.Methods;

/**
 *@company KAJOOL
 *@project KAJOOL (Control system polls)
 *@date 15/01/2021
 *@time 04:22:26 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class NotaEntradaDirecta extends NotaEntrada implements Serializable {

  private static final long serialVersionUID = -5382825768231834838L;

  private List<IActions> proyectos;
  private List<IActions> empleados;

  public NotaEntradaDirecta() throws Exception {
    this(1L, null);
  }

  public NotaEntradaDirecta(Long key, Long idOrdenCompra) throws Exception {
    super(key, idOrdenCompra);
    this.proyectos= new ArrayList<>();
    this.empleados= new ArrayList<>();
  }

  public NotaEntradaDirecta(Double descuentos, Long idProveedor, String descuento, Long idOrdenCompra, Long idDirecta, LocalDate fechaRecepcion, String extras, Long idNotaEntrada, LocalDate fechaFactura, Long idNotaEstatus, Long ejercicio, String consecutivo, Double total, String factura, Long idUsuario, Long idAlmacen, Double subTotal, Double impuestos, Double tipoDeCambio, Long idSinIva, String observaciones, Long idEmpresa, Long orden, Double excedentes, Long idProveedorPago) {
    super(descuentos, idProveedor, descuento, idOrdenCompra, idDirecta, fechaRecepcion, extras, idNotaEntrada, fechaFactura, idNotaEstatus, ejercicio, consecutivo, total, factura, idUsuario, idAlmacen, subTotal, impuestos, tipoDeCambio, idSinIva, observaciones, idEmpresa, orden, excedentes, idProveedorPago);
    this.proyectos= new ArrayList<>();
    this.empleados= new ArrayList<>();
  }
  
  public List<IActions> getProyectos() {
    return proyectos;
  }

  public List<IActions> getEmpleados() {
    return empleados;
  }

  @Override
  protected void finalize() throws Throwable {
    super.finalize(); 
    Methods.clean(this.proyectos);
    Methods.clean(this.empleados);
  }
  
  
}
