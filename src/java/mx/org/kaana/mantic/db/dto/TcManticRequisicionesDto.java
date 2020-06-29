package mx.org.kaana.mantic.db.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.LocalDate;
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
@Table(name="tc_mantic_requisiciones")
public class TcManticRequisicionesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descuentos")
  private Double descuentos;
  @Column (name="id_proveedor")
  private Long idProveedor;
  @Column (name="id_desarrollo")
  private Long idDesarrollo;
  @Column (name="descuento")
  private String descuento;
  @Column (name="id_requisicion_estatus")
  private Long idRequisicionEstatus;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_requisicion")
  private Long idRequisicion;
  @Column (name="id_solicita")
  private Long idSolicita;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="fecha_pedido")
  private LocalDate fechaPedido;
  @Column (name="total")
  private Double total;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="impuestos")
  private Double impuestos;
  @Column (name="sub_total")
  private Double subTotal;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="fecha_entregada")
  private LocalDate fechaEntregada;
  @Column (name="orden")
  private Long orden;

  public TcManticRequisicionesDto() {
    this(new Long(-1L));
  }

  public TcManticRequisicionesDto(Long key) {
    this(null, null, null, null, null, new Long(-1L), null, null, null, LocalDate.now(), null, null, null, null, null, null, LocalDate.now(), null);
    setKey(key);
  }

  public TcManticRequisicionesDto(Double descuentos, Long idProveedor, Long idDesarrollo, String descuento, Long idRequisicionEstatus, Long idRequisicion, Long idSolicita, Long ejercicio, String consecutivo, LocalDate fechaPedido, Double total, Long idUsuario, Double impuestos, Double subTotal, String observaciones, Long idEmpresa, LocalDate fechaEntregada, Long orden) {
    setDescuentos(descuentos);
    setIdProveedor(idProveedor);
    setIdDesarrollo(idDesarrollo);
    setDescuento(descuento);
    setIdRequisicionEstatus(idRequisicionEstatus);
    setIdRequisicion(idRequisicion);
    setIdSolicita(idSolicita);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
    setConsecutivo(consecutivo);
    setFechaPedido(fechaPedido);
    setTotal(total);
    setIdUsuario(idUsuario);
    setImpuestos(impuestos);
    setSubTotal(subTotal);
    setObservaciones(observaciones);
    setIdEmpresa(idEmpresa);
    setFechaEntregada(fechaEntregada);
    setOrden(orden);
  }
	
  public void setDescuentos(Double descuentos) {
    this.descuentos = descuentos;
  }

  public Double getDescuentos() {
    return descuentos;
  }

  public void setIdProveedor(Long idProveedor) {
    this.idProveedor = idProveedor;
  }

  public Long getIdProveedor() {
    return idProveedor;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setDescuento(String descuento) {
    this.descuento = descuento;
  }

  public String getDescuento() {
    return descuento;
  }

  public void setIdRequisicionEstatus(Long idRequisicionEstatus) {
    this.idRequisicionEstatus = idRequisicionEstatus;
  }

  public Long getIdRequisicionEstatus() {
    return idRequisicionEstatus;
  }

  public void setIdRequisicion(Long idRequisicion) {
    this.idRequisicion = idRequisicion;
  }

  public Long getIdRequisicion() {
    return idRequisicion;
  }

  public void setIdSolicita(Long idSolicita) {
    this.idSolicita = idSolicita;
  }

  public Long getIdSolicita() {
    return idSolicita;
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

  public void setFechaPedido(LocalDate fechaPedido) {
    this.fechaPedido = fechaPedido;
  }

  public LocalDate getFechaPedido() {
    return fechaPedido;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getTotal() {
    return total;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
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

  public void setFechaEntregada(LocalDate fechaEntregada) {
    this.fechaEntregada = fechaEntregada;
  }

  public LocalDate getFechaEntregada() {
    return fechaEntregada;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdRequisicion();
  }

  @Override
  public void setKey(Long key) {
  	this.idRequisicion = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescuentos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDescuento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdRequisicionEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdRequisicion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdSolicita());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaPedido());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImpuestos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSubTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaEntregada());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descuentos", getDescuentos());
		regresar.put("idProveedor", getIdProveedor());
		regresar.put("idDesarrollo", getIdDesarrollo());
		regresar.put("descuento", getDescuento());
		regresar.put("idRequisicionEstatus", getIdRequisicionEstatus());
		regresar.put("idRequisicion", getIdRequisicion());
		regresar.put("idSolicita", getIdSolicita());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("fechaPedido", getFechaPedido());
		regresar.put("total", getTotal());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("impuestos", getImpuestos());
		regresar.put("subTotal", getSubTotal());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("fechaEntregada", getFechaEntregada());
		regresar.put("orden", getOrden());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
			getDescuentos(), getIdProveedor(), getIdDesarrollo(), getDescuento(), getIdRequisicionEstatus(), getIdRequisicion(), getIdSolicita(), getEjercicio(), getRegistro(), getConsecutivo(), getFechaPedido(), getTotal(), getIdUsuario(), getImpuestos(), getSubTotal(), getObservaciones(), getIdEmpresa(), getFechaEntregada(), getOrden()
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
    regresar.append("idRequisicion~");
    regresar.append(getIdRequisicion());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdRequisicion());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticRequisicionesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdRequisicion()!= null && getIdRequisicion()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticRequisicionesDto other = (TcManticRequisicionesDto) obj;
    if (getIdRequisicion() != other.idRequisicion && (getIdRequisicion() == null || !getIdRequisicion().equals(other.idRequisicion))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdRequisicion() != null ? getIdRequisicion().hashCode() : 0);
    return hash;
  }

}


