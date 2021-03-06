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
@Table(name="tc_mantic_notas_entradas")
public class TcManticNotasEntradasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descuentos")
  private Double descuentos;
  @Column (name="excedentes")
  private Double excedentes;
  @Column (name="id_proveedor")
  private Long idProveedor;
  @Column (name="id_proveedor_pago")
  private Long idProveedorPago;
  @Column (name="descuento")
  private String descuento;
  @Column (name="id_orden_compra")
  private Long idOrdenCompra;
  @Column (name="id_nota_tipo")
  private Long idNotaTipo;
  @Column (name="fecha_recepcion")
  private LocalDate fechaRecepcion;
  @Column (name="extras")
  private String extras;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nota_entrada")
  private Long idNotaEntrada;
  @Column (name="fecha_factura")
  private LocalDate fechaFactura;
  @Column (name="id_nota_estatus")
  private Long idNotaEstatus;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="total")
  private Double total;
  @Column (name="factura")
  private String factura;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_almacen")
  private Long idAlmacen;
  @Column (name="sub_total")
  private Double subTotal;
  @Column (name="impuestos")
  private Double impuestos;
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
  @Column (name="dias_plazo")
  private Long diasPlazo;
  @Column (name="fecha_pago")
  private LocalDate fechaPago;
  @Column (name="deuda")
  private Double deuda;
  @Column (name="original")
  private Double original;
  @Column (name="id_cliente")
  private Long idCliente;
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
  
	
  public TcManticNotasEntradasDto() {
    this(new Long(-1L));
  }

  public TcManticNotasEntradasDto(Long key) {
    this(null, null, null, null, null, LocalDate.now(), null, new Long(-1L), LocalDate.now(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 30L, LocalDate.now(), 0D, null, 0D, -1L, -1L, -1L, -1L, -1L, -1L, null, -1L, -1L);
    setKey(key);
  }

  public TcManticNotasEntradasDto(Double descuentos, Long idProveedor, String descuento, Long idOrdenCompra, Long idNotaTipo, LocalDate fechaRecepcion, String extras, Long idNotaEntrada, LocalDate fechaFactura, Long idNotaEstatus, Long ejercicio, String consecutivo, Double total, String factura, Long idUsuario, Long idAlmacen, Double subTotal, Double impuestos, Double tipoDeCambio, Long idSinIva, String observaciones, Long idEmpresa, Long orden, Double excedentes, Long diasPlazo, LocalDate fechaPago, Double deuda, Long idProveedorPago, Double original, Long idDesarrollo, Long idCliente, Long idContrato, Long idBanco, Long idTipoMedioPago, Long idTipoPago, String referencia, Long idAlmacenista, Long idEmpresaTipoContacto) {
    setDescuentos(descuentos);
    setExcedentes(excedentes);
    setIdProveedor(idProveedor);
    setDescuento(descuento);
    setIdOrdenCompra(idOrdenCompra);
    setIdNotaTipo(idNotaTipo);
    setFechaRecepcion(fechaRecepcion);
    setExtras(extras);
    setIdNotaEntrada(idNotaEntrada);
    setFechaFactura(fechaFactura);
    setIdNotaEstatus(idNotaEstatus);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
    setConsecutivo(consecutivo);
    setTotal(total);
    setFactura(factura);
    setIdUsuario(idUsuario);
    setIdAlmacen(idAlmacen);
    setSubTotal(subTotal);
    setImpuestos(impuestos);
    setTipoDeCambio(tipoDeCambio);
    setIdSinIva(idSinIva);
    setObservaciones(observaciones);
    setIdEmpresa(idEmpresa);
    setOrden(orden);
    setDiasPlazo(diasPlazo);
    setFechaPago(fechaPago);
    setDeuda(deuda);
		this.idProveedorPago= idProveedorPago;
		this.original= original;
		this.idCliente= idCliente;
		this.idDesarrollo= idDesarrollo;
		this.idContrato= idContrato;
    this.idBanco= idBanco;
    this.idTipoMedioPago= idTipoMedioPago;
    this.idTipoPago= idTipoPago;
    this.referencia= referencia;
    this.idAlmacenista= idAlmacenista;
    this.idEmpresaTipoContacto= idEmpresaTipoContacto;
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

	public Long getIdProveedorPago() {
		return idProveedorPago;
	}

	public void setIdProveedorPago(Long idProveedorPago) {
		this.idProveedorPago=idProveedorPago;
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

	public Long getIdNotaTipo() {
		return idNotaTipo;
	}

	public void setIdNotaTipo(Long idNotaTipo) {
		this.idNotaTipo=idNotaTipo;
	}

  public void setFechaRecepcion(LocalDate fechaRecepcion) {
    this.fechaRecepcion = fechaRecepcion;
  }

  public LocalDate getFechaRecepcion() {
    return fechaRecepcion;
  }

  public void setExtras(String extras) {
    this.extras = extras;
  }

  public String getExtras() {
    return extras;
  }

  public void setIdNotaEntrada(Long idNotaEntrada) {
    this.idNotaEntrada = idNotaEntrada;
  }

  public Long getIdNotaEntrada() {
    return idNotaEntrada;
  }

  public void setFechaFactura(LocalDate fechaFactura) {
    this.fechaFactura = fechaFactura;
  }

  public LocalDate getFechaFactura() {
    return fechaFactura;
  }

  public void setIdNotaEstatus(Long idNotaEstatus) {
    this.idNotaEstatus = idNotaEstatus;
  }

  public Long getIdNotaEstatus() {
    return idNotaEstatus;
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

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getTotal() {
    return total;
  }

  public void setFactura(String factura) {
    this.factura = factura;
  }

  public String getFactura() {
    return factura;
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

  public void setSubTotal(Double subTotal) {
    this.subTotal = subTotal;
  }

  public Double getSubTotal() {
    return subTotal;
  }

  public void setImpuestos(Double impuestos) {
    this.impuestos = impuestos;
  }

  public Double getImpuestos() {
    return impuestos;
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

	public Long getDiasPlazo() {
		return diasPlazo;
	}

	public void setDiasPlazo(Long diasPlazo) {
		this.diasPlazo=diasPlazo;
	}

	public LocalDate getFechaPago() {
		return fechaPago;
	}

	public void setFechaPago(LocalDate fechaPago) {
		this.fechaPago=fechaPago;
	}

	public Double getDeuda() {
		return deuda;
	}

	public void setDeuda(Double deuda) {
		this.deuda=deuda;
	}

	public Double getOriginal() {
		return original;
	}

	public void setOriginal(Double original) {
		this.original=original;
	}

  public Long getIdCliente() {
    return idCliente;
  }

  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
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

  @Transient
  @Override
  public Long getKey() {
  	return getIdNotaEntrada();
  }

  @Override
  public void setKey(Long key) {
  	this.idNotaEntrada = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescuentos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getExcedentes());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProveedorPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDescuento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdOrdenCompra());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaTipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaRecepcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getExtras());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaEntrada());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaFactura());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFactura());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAlmacen());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSubTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImpuestos());
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
		regresar.append(getDiasPlazo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDeuda());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOriginal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCliente());
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
     regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descuentos", getDescuentos());
		regresar.put("excedentes", getExcedentes());
		regresar.put("idProveedor", getIdProveedor());
		regresar.put("idProveedorPago", getIdProveedorPago());
		regresar.put("descuento", getDescuento());
		regresar.put("idOrdenCompra", getIdOrdenCompra());
		regresar.put("idNotaTipo", getIdNotaTipo());
		regresar.put("fechaRecepcion", getFechaRecepcion());
		regresar.put("extras", getExtras());
		regresar.put("idNotaEntrada", getIdNotaEntrada());
		regresar.put("fechaFactura", getFechaFactura());
		regresar.put("idNotaEstatus", getIdNotaEstatus());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("total", getTotal());
		regresar.put("factura", getFactura());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idAlmacen", getIdAlmacen());
		regresar.put("subTotal", getSubTotal());
		regresar.put("impuestos", getImpuestos());
		regresar.put("tipoDeCambio", getTipoDeCambio());
		regresar.put("idSinIva", getIdSinIva());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("orden", getOrden());
		regresar.put("diasPlazo", getDiasPlazo());
		regresar.put("fechaPago", getFechaPago());
		regresar.put("deuda", getDeuda());
		regresar.put("original", getOriginal());
		regresar.put("idDesarrollo", getIdDesarrollo());
		regresar.put("idCliente", getIdCliente());
		regresar.put("idContrato", getIdContrato());
		regresar.put("idBanco", getIdBanco());
		regresar.put("idTipoMedioPago", getIdTipoMedioPago());
		regresar.put("idTipoPago", getIdTipoPago());
		regresar.put("referencia", getReferencia());
		regresar.put("idAlmacenista", getIdAlmacenista());
		regresar.put("IdEmpresaTipoContacto", getIdEmpresaTipoContacto());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getDescuentos(), getIdProveedor(), getIdProveedorPago(), getDescuento(), getIdOrdenCompra(), getIdNotaTipo(), getFechaRecepcion(), getExtras(), getIdNotaEntrada(), getFechaFactura(), getIdNotaEstatus(), getEjercicio(), getRegistro(), getConsecutivo(), getTotal(), getFactura(), getIdUsuario(), getIdAlmacen(), getSubTotal(), getImpuestos(), getTipoDeCambio(), getIdSinIva(), getObservaciones(), getIdEmpresa(), getOrden(), getExcedentes(), getDiasPlazo(), getFechaPago(), getDeuda(), getOriginal(), getIdDesarrollo(), getIdCliente(), getIdContrato(), getIdBanco(), getIdTipoMedioPago(), getIdTipoPago(), getReferencia(), getIdAlmacenista(), getIdEmpresaTipoContacto()
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
    regresar.append("idNotaEntrada~");
    regresar.append(getIdNotaEntrada());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNotaEntrada());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticNotasEntradasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNotaEntrada()!= null && getIdNotaEntrada()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticNotasEntradasDto other = (TcManticNotasEntradasDto) obj;
    if (getIdNotaEntrada() != other.idNotaEntrada && (getIdNotaEntrada() == null || !getIdNotaEntrada().equals(other.idNotaEntrada))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNotaEntrada() != null ? getIdNotaEntrada().hashCode() : 0);
    return hash;
  }
  
}