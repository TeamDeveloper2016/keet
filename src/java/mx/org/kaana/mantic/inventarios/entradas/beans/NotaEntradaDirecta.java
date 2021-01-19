package mx.org.kaana.mantic.inventarios.entradas.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.kajool.db.comun.operation.IActions;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.inventarios.entradas.enums.EGastos;
import static mx.org.kaana.mantic.inventarios.entradas.enums.EGastos.DIRECTOS;
import static mx.org.kaana.mantic.inventarios.entradas.enums.EGastos.INDIRECTOS;
import static mx.org.kaana.mantic.inventarios.entradas.enums.EGastos.MANO_DE_OBRA;

/**
 *@company KAJOOL
 *@project KAJOOL (Control system polls)
 *@date 15/01/2021
 *@time 04:22:26 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class NotaEntradaDirecta extends NotaEntrada implements Serializable {

  private static final long serialVersionUID = -5382825768231834838L;

	private UISelectEntity ikEmpresa;
  private List<IActions> proyectos;
  private List<IActions> empleados;
  private Double inDirectos;
  private Double directos;
  private Double manoDeObra;

  public NotaEntradaDirecta() throws Exception {
    this(1L, null);
  }

  public NotaEntradaDirecta(Long key, Long idOrdenCompra) throws Exception {
    super(key, idOrdenCompra);
    this.proyectos= new ArrayList<>();
    this.empleados= new ArrayList<>();
    this.inDirectos= 0D;
    this.directos  = 0D;
    this.manoDeObra= 0D;
  }

  public NotaEntradaDirecta(Double descuentos, Long idProveedor, String descuento, Long idOrdenCompra, Long idDirecta, LocalDate fechaRecepcion, String extras, Long idNotaEntrada, LocalDate fechaFactura, Long idNotaEstatus, Long ejercicio, String consecutivo, Double total, String factura, Long idUsuario, Long idAlmacen, Double subTotal, Double impuestos, Double tipoDeCambio, Long idSinIva, String observaciones, Long idEmpresa, Long orden, Double excedentes, Long idProveedorPago) {
    super(descuentos, idProveedor, descuento, idOrdenCompra, idDirecta, fechaRecepcion, extras, idNotaEntrada, fechaFactura, idNotaEstatus, ejercicio, consecutivo, total, factura, idUsuario, idAlmacen, subTotal, impuestos, tipoDeCambio, idSinIva, observaciones, idEmpresa, orden, excedentes, idProveedorPago);
    this.proyectos= new ArrayList<>();
    this.empleados= new ArrayList<>();
  }
  
	public UISelectEntity getIkEmpresa() {
		return ikEmpresa;
	}

	public void setIkEmpresa(UISelectEntity ikEmpresa) {
		this.ikEmpresa=ikEmpresa;
		if(this.ikEmpresa!= null)
		  this.setIdEmpresa(this.ikEmpresa.getKey());
	}
  
  public List<IActions> getProyectos() {
    return proyectos;
  }

  public List<IActions> getEmpleados() {
    return this.empleados;
  }

  public Double getInDirectos() {
    return this.inDirectos;
  }

  public Double getDirectos() {
    return this.directos;
  }

  public Double getManoDeObra() {
    return this.manoDeObra;
  }

  public Double getSaldo() {
    return Numero.toRedondearSat(this.getDeuda()- (this.inDirectos+ this.directos+ this.manoDeObra));
  }

  public String getInDirectos$() {
    return Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, this.inDirectos);
  }

  public String getDirectos$() {
    return Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, this.directos);
  }

  public String getManoDeObra$() {
    return Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, this.manoDeObra);
  }

  public Double getGasto() {
    return Numero.toRedondearSat(this.inDirectos+ this.directos+ this.manoDeObra);
  }

  public String getGasto$() {
    return Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, Numero.toRedondearSat(this.inDirectos+ this.directos+ this.manoDeObra));
  }

  public String getSaldo$() {
    return Global.format(EFormatoDinamicos.MILES_CON_DECIMALES, Numero.toRedondearSat(this.getDeuda()- (this.inDirectos+ this.directos+ this.manoDeObra)));
  }

  public IActions isEqualProyecto(NotaProyecto proyecto) {
    IActions regresar= null;
    for (IActions item: this.proyectos) {
      if(((NotaProyecto)item.getDto()).isEqual(proyecto)) {
        regresar= item;
        break;
      } // if  
    } // for
    return regresar;
  }
  
  public IActions isEqualEmplado(NotaEmpleado empleado) {
    IActions regresar= null;
    for (IActions item: this.empleados) {
      if(((NotaEmpleado)item.getDto()).isEqual(empleado)) {
        regresar= item;
        break;
      } // if  
    } // for
    return regresar;
  }
  
  public void toAdd(EGastos gastos, Double importe) {
    switch(gastos) {
      case INDIRECTOS:
        this.inDirectos+= importe;
        break;
      case DIRECTOS:
        this.directos+= importe;
        break;
      case MANO_DE_OBRA:
        this.manoDeObra+= importe;
        break;
    } // switch
  }
  
  public void toRemove(EGastos gastos, Double importe) {
    switch(gastos) {
      case INDIRECTOS:
        this.inDirectos-= importe;
        break;
      case DIRECTOS:
        this.directos-= importe;
        break;
      case MANO_DE_OBRA:
        this.manoDeObra-= importe;
        break;
    } // switch
  }
  
  @Override
  protected void finalize() throws Throwable {
    super.finalize(); 
    Methods.clean(this.proyectos);
    Methods.clean(this.empleados);
  }
  
}
