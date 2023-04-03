package mx.org.kaana.mantic.db.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 10/10/2016
 *@time 11:58:22 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Entity
@Table(name="tc_mantic_ordenes_compras")
public class TcManticOrdenesComprasDto implements IBaseDto, Serializable, Cloneable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_proveedor_pago")
  private Long idProveedorPago;
  @Column (name="descuentos")
  private Double descuentos;
  @Column (name="excedentes")
  private Double excedentes;
  @Column (name="id_proveedor")
  private Long idProveedor;
  @Column (name="id_cliente")
  private Long idCliente;
  @Column (name="descuento")
  private String descuento;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_orden_compra")
  private Long idOrdenCompra;
  @Column (name="extras")
  private String extras;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="id_gasto")
  private Long idGasto;
  @Column (name="total")
  private Double total;
  @Column (name="id_orden_estatus")
  private Long idOrdenEstatus;
  @Column (name="entrega_estimada")
  private LocalDate entregaEstimada;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_almacen")
  private Long idAlmacen;
  @Column (name="impuestos")
  private Double impuestos;
  @Column (name="sub_total")
  private Double subTotal;
  @Column (name="tipo_de_cambio")
  private Double tipoDeCambio;
  @Column (name="id_sin_iva")
  private Long idSinIva;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="orden")
  private Long orden;
  @Column (name="id_desarrollo")
  private Long idDesarrollo;
  @Column (name="id_contrato")
  private Long idContrato;
  @Column (name="id_banco")
  private Long idBanco;
  @Column (name="id_tipo_medio_pago")
  private Long idTipoMedioPago;
  @Column (name="id_tipo_pago")
  private Long idTipoPago;
  @Column (name="referencia")
  private String referencia;
  @Column (name="id_almacenista")
  private Long idAlmacenista;
  @Column (name="id_empresa_tipo_contacto")
  private Long idEmpresaTipoContacto;
  @Column (name="id_orden_fuente")
  private Long idOrdenFuente;
  @Column (name="id_tipo_orden")
  private Long idTipoOrden;

  public TcManticOrdenesComprasDto() {
    this(new Long(-1L));
  }

  public TcManticOrdenesComprasDto(Long key) {
    this(null, null, null, null, null, new Long(-1L), null, null, null, null, null, null, LocalDate.now(), null, null, null, null, null, null, null, null, null, null, null, null, -1L, -1L, -1L, null, -1L, -1L, null, 1L);
    setKey(key);
  }

  public TcManticOrdenesComprasDto(Long idProveedorPago, Double descuentos, Long idProveedor, Long idCliente, String descuento, Long idOrdenCompra, String extras, Long ejercicio, String consecutivo, Long idGasto, Double total, Long idOrdenEstatus, LocalDate entregaEstimada, Long idUsuario, Long idAlmacen, Double impuestos, Double subTotal, Double tipoDeCambio, Long idSinIva, String observaciones, Long idEmpresa, Long orden, Double excedentes, Long idDesarrollo, Long idContrato, Long idBanco, Long idTipoMedioPago, Long idTipoPago, String referencia, Long idAlmacenista, Long idEmpresaTipoContacto, Long idOrdenFuente, Long idTipoOrden) {
    setIdProveedorPago(idProveedorPago);
    setDescuentos(descuentos);
    setExcedentes(excedentes);
    setIdProveedor(idProveedor);
    setIdCliente(idCliente);
    setDescuento(descuento);
    setIdOrdenCompra(idOrdenCompra);
    setExtras(extras);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
    setConsecutivo(consecutivo);
    setIdGasto(idGasto);
    setTotal(total);
    setIdOrdenEstatus(idOrdenEstatus);
    setEntregaEstimada(entregaEstimada);
    setIdUsuario(idUsuario);
    setIdAlmacen(idAlmacen);
    setImpuestos(impuestos);
    setSubTotal(subTotal);
    setTipoDeCambio(tipoDeCambio);
    setIdSinIva(idSinIva);
    setObservaciones(observaciones);
    setIdEmpresa(idEmpresa);
    setOrden(orden);
		this.idDesarrollo= idDesarrollo;
		this.idContrato= idContrato;
    this.idBanco= idBanco;
    this.idTipoMedioPago= idTipoMedioPago;
    this.idTipoPago= idTipoPago;
    this.referencia= referencia;
    this.idAlmacenista= idAlmacenista;
    this.idEmpresaTipoContacto= idEmpresaTipoContacto;
    this.idOrdenFuente= idOrdenFuente;
    this.idTipoOrden= idTipoOrden;
 }
	
  public void setIdProveedorPago(Long idProveedorPago) {
    this.idProveedorPago = idProveedorPago;
  }

  public Long getIdProveedorPago() {
    return idProveedorPago;
  }

  public void setDescuentos(Double descuentos) {
    this.descuentos = descuentos;
  }

  public Double getDescuentos() {
    return descuentos;
  }

	public Double getExcedentes() {
		return excedentes;
	}

	public void setExcedentes(Double excedentes) {
		this.excedentes=excedentes;
	}

  public void setIdProveedor(Long idProveedor) {
    this.idProveedor = idProveedor;
  }

  public Long getIdProveedor() {
    return idProveedor;
  }

  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public Long getIdCliente() {
    return idCliente;
  }

  public void setDescuento(String descuento) {
    this.descuento = descuento;
  }

  public String getDescuento() {
    return descuento;
  }

  public void setIdOrdenCompra(Long idOrdenCompra) {
    this.idOrdenCompra = idOrdenCompra;
  }

  public Long getIdOrdenCompra() {
    return idOrdenCompra;
  }

  public void setExtras(String extras) {
    this.extras = extras;
  }

  public String getExtras() {
    return extras;
  }

  public void setEjercicio(Long ejercicio) {
    this.ejercicio = ejercicio;
  }

  public Long getEjercicio() {
    return ejercicio;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public String getConsecutivo() {
    return consecutivo;
  }

  public void setIdGasto(Long idGasto) {
    this.idGasto = idGasto;
  }

  public Long getIdGasto() {
    return idGasto;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getTotal() {
    return total;
  }

  public void setIdOrdenEstatus(Long idOrdenEstatus) {
    this.idOrdenEstatus = idOrdenEstatus;
  }

  public Long getIdOrdenEstatus() {
    return idOrdenEstatus;
  }

  public void setEntregaEstimada(LocalDate entregaEstimada) {
    this.entregaEstimada = entregaEstimada;
  }

  public LocalDate getEntregaEstimada() {
    return entregaEstimada;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdAlmacen(Long idAlmacen) {
    this.idAlmacen = idAlmacen;
  }

  public Long getIdAlmacen() {
    return idAlmacen;
  }

  public void setImpuestos(Double impuestos) {
    this.impuestos = impuestos;
  }

  public Double getImpuestos() {
    return impuestos;
  }

  public void setSubTotal(Double subTotal) {
    this.subTotal = subTotal;
  }

  public Double getSubTotal() {
    return subTotal;
  }

  public void setTipoDeCambio(Double tipoDeCambio) {
    this.tipoDeCambio = tipoDeCambio;
  }

  public Double getTipoDeCambio() {
    return tipoDeCambio;
  }

  public void setIdSinIva(Long idSinIva) {
    this.idSinIva = idSinIva;
  }

  public Long getIdSinIva() {
    return idSinIva;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdEmpresa(Long idEmpresa) {
    this.idEmpresa = idEmpresa;
  }

  public Long getIdEmpresa() {
    return idEmpresa;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

	public Long getIdDesarrollo() {
		return idDesarrollo;
	}

	public void setIdDesarrollo(Long idDesarrollo) {
		this.idDesarrollo = idDesarrollo;
	}	

  public Long getIdContrato() {
    return idContrato;
  }

  public void setIdContrato(Long idContrato) {
    this.idContrato = idContrato;
  }

  public Long getIdBanco() {
    return idBanco;
  }

  public void setIdBanco(Long idBanco) {
    this.idBanco = idBanco;
  }

  public Long getIdTipoMedioPago() {
    return idTipoMedioPago;
  }

  public void setIdTipoMedioPago(Long idTipoMedioPago) {
    this.idTipoMedioPago = idTipoMedioPago;
  }

  public Long getIdTipoPago() {
    return idTipoPago;
  }

  public void setIdTipoPago(Long idTipoPago) {
    this.idTipoPago = idTipoPago;
  }

  public String getReferencia() {
    return referencia;
  }

  public void setReferencia(String referencia) {
    this.referencia = referencia;
  }

  public Long getIdAlmacenista() {
    return idAlmacenista;
  }

  public void setIdAlmacenista(Long idAlmacenista) {
    this.idAlmacenista = idAlmacenista;
  }

  public Long getIdEmpresaTipoContacto() {
    return idEmpresaTipoContacto;
  }

  public void setIdEmpresaTipoContacto(Long idEmpresaTipoContacto) {
    this.idEmpresaTipoContacto = idEmpresaTipoContacto;
  }

  public Long getIdOrdenFuente() {
    return idOrdenFuente;
  }

  public void setIdOrdenFuente(Long idOrdenFuente) {
    this.idOrdenFuente = idOrdenFuente;
  }

  public Long getIdTipoOrden() {
    return idTipoOrden;
  }

  public void setIdTipoOrden(Long idTipoOrden) {
    this.idTipoOrden = idTipoOrden;
  }
	
  @Transient
  @Override
  public Long getKey() {
  	return getIdOrdenCompra();
  }

  @Override
  public void setKey(Long key) {
  	this.idOrdenCompra = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdProveedorPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDescuentos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getExcedentes());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCliente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDescuento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdOrdenCompra());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getExtras());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdGasto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdOrdenEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEntregaEstimada());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAlmacen());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImpuestos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSubTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTipoDeCambio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdSinIva());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdBanco());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoMedioPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getReferencia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAlmacenista());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaTipoContacto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdOrdenFuente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoOrden());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idProveedorPago", getIdProveedorPago());
		regresar.put("descuentos", getDescuentos());
		regresar.put("excedentes", getExcedentes());
		regresar.put("idProveedor", getIdProveedor());
		regresar.put("idCliente", getIdCliente());
		regresar.put("descuento", getDescuento());
		regresar.put("idOrdenCompra", getIdOrdenCompra());
		regresar.put("extras", getExtras());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idGasto", getIdGasto());
		regresar.put("total", getTotal());
		regresar.put("idOrdenEstatus", getIdOrdenEstatus());
		regresar.put("entregaEstimada", getEntregaEstimada());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idAlmacen", getIdAlmacen());
		regresar.put("impuestos", getImpuestos());
		regresar.put("subTotal", getSubTotal());
		regresar.put("tipoDeCambio", getTipoDeCambio());
		regresar.put("idSinIva", getIdSinIva());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("orden", getOrden());
		regresar.put("idDesarrollo", getIdDesarrollo());
		regresar.put("idContrato", getIdContrato());
		regresar.put("idBanco", getIdBanco());
		regresar.put("idTipoMedioPago", getIdTipoMedioPago());
		regresar.put("idTipoPago", getIdTipoPago());
		regresar.put("referencia", getReferencia());
		regresar.put("idAlmacenista", getIdAlmacenista());
		regresar.put("idEmpresaTipoContacto", getIdEmpresaTipoContacto());
		regresar.put("idOrdenFuente", getIdOrdenFuente());
		regresar.put("idTipoOrden", getIdTipoOrden());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
			getIdProveedorPago(), getDescuentos(), getIdProveedor(), getIdCliente(), getDescuento(), getIdOrdenCompra(), getExtras(), getEjercicio(), getRegistro(), getConsecutivo(), getIdGasto(), getTotal(), getIdOrdenEstatus(), getEntregaEstimada(), getIdUsuario(), getIdAlmacen(), getImpuestos(), getSubTotal(), getTipoDeCambio(), getIdSinIva(), getObservaciones(), getIdEmpresa(), getOrden(), getExcedentes(), getIdDesarrollo(), getIdContrato(), getIdBanco(), getIdTipoMedioPago(), getIdTipoPago(), getReferencia(), getIdAlmacenista(), getIdEmpresaTipoContacto(), getIdOrdenFuente(), getIdTipoOrden()
    };
    return regresar;
  }

  @Override
  public Object toValue(String name) {
    return Methods.getValue(this, name);
  }

  @Override
  public String toAllKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("|");
    regresar.append("idOrdenCompra~");
    regresar.append(getIdOrdenCompra());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdOrdenCompra());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticOrdenesComprasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdOrdenCompra()!= null && getIdOrdenCompra()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticOrdenesComprasDto other = (TcManticOrdenesComprasDto) obj;
    if (getIdOrdenCompra() != other.idOrdenCompra && (getIdOrdenCompra() == null || !getIdOrdenCompra().equals(other.idOrdenCompra))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdOrdenCompra() != null ? getIdOrdenCompra().hashCode() : 0);
    return hash;
  }
  
  public Object clone() throws CloneNotSupportedException {  
	  return (TcManticOrdenesComprasDto)super.clone();  
  }  
  
}