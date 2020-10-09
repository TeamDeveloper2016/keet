package mx.org.kaana.mantic.inventarios.entradas.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Calendar;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.mantic.db.dto.TcManticNotasEntradasDto;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesComprasDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 8/05/2018
 *@time 10:29:26 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class NotaEntrada extends TcManticNotasEntradasDto implements Serializable {

	private static final long serialVersionUID=3088884892456452488L;
	
	private UISelectEntity ikProveedor;
  private UISelectEntity ikProveedorPago;
	private UISelectEntity ikOrdenCompra;
	private UISelectEntity ikDesarrollo;
	private UISelectEntity ikCliente;
	private UISelectEntity ikContrato;
	private UISelectEntity ikBanco;
	private UISelectEntity ikTipoMedioPago;
	private UISelectEntity ikTipoPago;
	private UISelectEntity ikAlmacen;
	private UISelectEntity ikAlmacenista;
	private UISelectEntity ikEmpresaTipoContacto;

	public NotaEntrada() throws Exception {
		this(1L, null);
	}

	public NotaEntrada(Long key, Long idOrdenCompra) throws Exception {
		super(0D, null, "0.00", idOrdenCompra, 1L, LocalDate.now(), "0.00", key, LocalDate.now(), 1L, new Long(Calendar.getInstance().get(Calendar.YEAR)), Calendar.getInstance().get(Calendar.YEAR)+ "00000", 0D, "", 1L, -1L, 0D, 0D, 1D, 2L, "", -1L, 1L, 0D, 30L, LocalDate.now(), 0D, -1L, 0D, -1L, -1L, -1L, -1L, -1L, -1L, null, -1L, -1L);
		if(!Cadena.isVacio(idOrdenCompra) && idOrdenCompra> 0L) {
		  TcManticOrdenesComprasDto compra= (TcManticOrdenesComprasDto)DaoFactory.getInstance().findById(TcManticOrdenesComprasDto.class, idOrdenCompra);
		  super.setIdProveedor(compra.getIdProveedor());
			super.setIdProveedorPago(compra.getIdProveedorPago());
			super.setIdAlmacen(compra.getIdAlmacen());
		} // if
	}

	public NotaEntrada(Double descuentos, Long idProveedor, String descuento, Long idOrdenCompra, Long idDirecta, LocalDate fechaRecepcion, String extras, Long idNotaEntrada, LocalDate fechaFactura, Long idNotaEstatus, Long ejercicio, String consecutivo, Double total, String factura, Long idUsuario, Long idAlmacen, Double subTotal, Double impuestos, Double tipoDeCambio, Long idSinIva, String observaciones, Long idEmpresa, Long orden, Double excedentes, Long idProveedorPago) {
		super(descuentos, idProveedor, descuento, idOrdenCompra, idDirecta, fechaRecepcion, extras, idNotaEntrada, fechaFactura, idNotaEstatus, ejercicio, consecutivo, total, factura, idUsuario, idAlmacen, subTotal, impuestos, tipoDeCambio, idSinIva, observaciones, idEmpresa, orden, excedentes, 30L, LocalDate.now(), 0D, idProveedorPago, 0D, -1L, -1L, -1L, -1L, -1L, -1L, null, -1L, -1L);
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
	
	public UISelectEntity getIkOrdenCompra() {
		return ikOrdenCompra;
	}

	public void setIkOrdenCompra(UISelectEntity ikOrdenCompra) {
		this.ikOrdenCompra=ikOrdenCompra;
		if(this.ikOrdenCompra!= null)
  	  this.setIdOrdenCompra(this.ikOrdenCompra.getKey());
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
		  this.setIdCliente(this.ikCliente.getKey());
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
		return TcManticNotasEntradasDto.class;
	}
	
}
