package mx.org.kaana.mantic.compras.ordenes.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Calendar;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesComprasDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 8/05/2018
 *@time 10:29:26 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class OrdenCompra extends TcManticOrdenesComprasDto implements Serializable {

	private static final long serialVersionUID=3088884892456452488L;
	
	private UISelectEntity ikEmpresa;
	private UISelectEntity ikAlmacen;
	private UISelectEntity ikDesarrollo;
	private UISelectEntity ikCliente;
	private UISelectEntity ikContrato;
	private UISelectEntity ikProveedor;
	private UISelectEntity ikProveedorPago;
	private UISelectEntity ikBanco;
	private UISelectEntity ikTipoMedioPago;
	private UISelectEntity ikTipoPago;
	private UISelectEntity ikAlmacenista;
	private UISelectEntity ikEmpresaTipoContacto;

	public OrdenCompra() {
		this(-1L);
	}

	public OrdenCompra(Long key) {
		this(-1L, 0D, -1L, -1L, "0.00", -1L, "0.00", new Long(Calendar.getInstance().get(Calendar.YEAR)), Calendar.getInstance().get(Calendar.YEAR)+ "00000", 2L, 0D, 1L, LocalDate.now(), 1L, -1L, 0D, 0D, 1D, 2L, "", -1L, 1L, 0D);
	}

	public OrdenCompra(Long idProveedorPago, Double descuentos, Long idProveedor, Long idCliente, String descuento, Long idOrdenCompra, String extras, Long ejercicio, String consecutivo, Long idGasto, Double total, Long idOrdenEstatus, LocalDate entregaEstimada, Long idUsuario, Long idAlmacen, Double impuestos, Double subTotal, Double tipoDeCambio, Long idSinIva, String observaciones, Long idEmpresa, Long orden, Double excedentes) {
		super(idProveedorPago, descuentos, idProveedor, idCliente, descuento, idOrdenCompra, extras, ejercicio, consecutivo, idGasto, total, idOrdenEstatus, entregaEstimada, idUsuario, idAlmacen, impuestos, subTotal, tipoDeCambio, idSinIva, observaciones, idEmpresa, orden, excedentes, -1L, -1L, -1L, -1L, -1L, null, -1L, -1L, null);
	}

	public UISelectEntity getIkEmpresa() {
		return ikEmpresa;
	}

	public void setIkEmpresa(UISelectEntity ikEmpresa) {
		this.ikEmpresa=ikEmpresa;
		if(this.ikEmpresa!= null)
		  this.setIdEmpresa(this.ikEmpresa.getKey());
	}

	public UISelectEntity getIkAlmacen() {
		return ikAlmacen;
	}

	public void setIkAlmacen(UISelectEntity ikAlmacen) {
		this.ikAlmacen=ikAlmacen;
		if(this.ikAlmacen!= null)
		  this.setIdAlmacen(this.ikAlmacen.getKey());
	}

	public UISelectEntity getIkProveedor() {
		return ikProveedor;
	}

	public void setIkProveedor(UISelectEntity ikProveedor) {
		this.ikProveedor=ikProveedor;
		if(this.ikProveedor!= null)
		  this.setIdProveedor(this.ikProveedor.getKey());
	}

	public UISelectEntity getIkProveedorPago() {
		return ikProveedorPago;
	}

	public void setIkProveedorPago(UISelectEntity ikProveedorPago) {
		this.ikProveedorPago=ikProveedorPago;
		if(this.ikProveedorPago!= null)
		  this.setIdProveedorPago(this.ikProveedorPago.getKey());
	}

  public UISelectEntity getIkDesarrollo() {
    return ikDesarrollo;
  }

  public void setIkDesarrollo(UISelectEntity ikDesarrollo) {
    this.ikDesarrollo = ikDesarrollo;
		if(this.ikDesarrollo!= null)
		  this.setIdDesarrollo(this.ikDesarrollo.getKey());
  }

	public UISelectEntity getIkCliente() {
		return ikCliente;
	}

	public void setIkCliente(UISelectEntity ikCliente) {
		this.ikCliente=ikCliente;
		if(this.ikCliente!= null)
		  this.setIdCliente(this.ikCliente.containsKey("idCliente")? this.ikCliente.toLong("idCliente"): this.ikCliente.getKey());
	}

  public UISelectEntity getIkContrato() {
    return ikContrato;
  }

  public void setIkContrato(UISelectEntity ikContrato) {
    this.ikContrato = ikContrato;
		if(this.ikContrato!= null)
		  this.setIdContrato(this.ikContrato.getKey());
  }

  public UISelectEntity getIkBanco() {
    return ikBanco;
  }

  public void setIkBanco(UISelectEntity ikBanco) {
    this.ikBanco = ikBanco;
		if(this.ikBanco!= null)
		  this.setIdBanco(this.ikBanco.getKey());
  }

  public UISelectEntity getIkTipoMedioPago() {
    return ikTipoMedioPago;
  }

  public void setIkTipoMedioPago(UISelectEntity ikTipoMedioPago) {
    this.ikTipoMedioPago = ikTipoMedioPago;
		if(this.ikTipoMedioPago!= null)
		  this.setIdTipoMedioPago(this.ikTipoMedioPago.getKey());
  }

  public UISelectEntity getIkTipoPago() {
    return ikTipoPago;
  }

  public void setIkTipoPago(UISelectEntity ikTipoPago) {
    this.ikTipoPago = ikTipoPago;
		if(this.ikTipoPago!= null)
		  this.setIdTipoPago(this.ikTipoPago.getKey());
  }

  public UISelectEntity getIkAlmacenista() {
    return ikAlmacenista;
  }

  public void setIkAlmacenista(UISelectEntity ikAlmacenista) {
    this.ikAlmacenista = ikAlmacenista;
		if(this.ikAlmacenista!= null)
		  this.setIdAlmacenista(this.ikAlmacenista.getKey());
  }

  public UISelectEntity getIkEmpresaTipoContacto() {
    return ikEmpresaTipoContacto;
  }

  public void setIkEmpresaTipoContacto(UISelectEntity ikEmpresaTipoContacto) {
    this.ikEmpresaTipoContacto = ikEmpresaTipoContacto;
		if(this.ikEmpresaTipoContacto!= null)
		  this.setIdEmpresaTipoContacto(this.ikEmpresaTipoContacto.getKey());
  }
	
	@Override
	public Class toHbmClass() {
		return TcManticOrdenesComprasDto.class;
	}
	
}
