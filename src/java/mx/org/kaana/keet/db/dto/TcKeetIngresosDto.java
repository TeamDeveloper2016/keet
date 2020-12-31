package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
@Table(name="tc_keet_ingresos")
public class TcKeetIngresosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descuentos")
  private Double descuentos;
  @Column (name="id_ingreso_estatus")
  private Long idIngresoEstatus;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_ingreso")
  private Long idIngreso;
  @Column (name="id_cliente")
  private Long idCliente;
  @Column (name="id_desarrollo")
  private Long idDesarrollo;
  @Column (name="fecha_recepcion")
  private LocalDate fechaRecepcion;
  @Column (name="saldo")
  private Double saldo;
  @Column (name="fecha_factura")
  private LocalDate fechaFactura;
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
  @Column (name="impuestos")
  private Double impuestos;
  @Column (name="id_contrato")
  private Long idContrato;
  @Column (name="sub_total")
  private Double subTotal;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="orden")
  private Long orden;
  @Column (name="disponible")
  private Double disponible;

  public TcKeetIngresosDto() {
    this(new Long(-1L));
  }

  public TcKeetIngresosDto(Long key) {
    this(null, null, new Long(-1L), null, null, LocalDate.now(), null, LocalDate.now(), null, null, null, null, null, null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKeetIngresosDto(Double descuentos, Long idIngresoEstatus, Long idIngreso, Long idCliente, Long idDesarrollo, LocalDate fechaRecepcion, Double saldo, LocalDate fechaFactura, Long ejercicio, String consecutivo, Double total, String factura, Long idUsuario, Double impuestos, Long idContrato, Double subTotal, String observaciones, Long idEmpresa, Long orden, Double disponible) {
    setDescuentos(descuentos);
    setIdIngresoEstatus(idIngresoEstatus);
    setIdIngreso(idIngreso);
    setIdCliente(idCliente);
    setIdDesarrollo(idDesarrollo);
    setFechaRecepcion(fechaRecepcion);
    setSaldo(saldo);
    setFechaFactura(fechaFactura);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
    setConsecutivo(consecutivo);
    setTotal(total);
    setFactura(factura);
    setIdUsuario(idUsuario);
    setImpuestos(impuestos);
    setIdContrato(idContrato);
    setSubTotal(subTotal);
    setObservaciones(observaciones);
    setIdEmpresa(idEmpresa);
    setOrden(orden);
    setDisponible(disponible);
  }
	
  public void setDescuentos(Double descuentos) {
    this.descuentos = descuentos;
  }

  public Double getDescuentos() {
    return descuentos;
  }

  public void setIdIngresoEstatus(Long idIngresoEstatus) {
    this.idIngresoEstatus = idIngresoEstatus;
  }

  public Long getIdIngresoEstatus() {
    return idIngresoEstatus;
  }

  public void setIdIngreso(Long idIngreso) {
    this.idIngreso = idIngreso;
  }

  public Long getIdIngreso() {
    return idIngreso;
  }

  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public Long getIdCliente() {
    return idCliente;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setFechaRecepcion(LocalDate fechaRecepcion) {
    this.fechaRecepcion = fechaRecepcion;
  }

  public LocalDate getFechaRecepcion() {
    return fechaRecepcion;
  }

  public void setSaldo(Double saldo) {
    this.saldo = saldo;
  }

  public Double getSaldo() {
    return saldo;
  }

  public void setFechaFactura(LocalDate fechaFactura) {
    this.fechaFactura = fechaFactura;
  }

  public LocalDate getFechaFactura() {
    return fechaFactura;
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

  public void setImpuestos(Double impuestos) {
    this.impuestos = impuestos;
  }

  public Double getImpuestos() {
    return impuestos;
  }

  public void setIdContrato(Long idContrato) {
    this.idContrato = idContrato;
  }

  public Long getIdContrato() {
    return idContrato;
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

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setDisponible(Double disponible) {
    this.disponible = disponible;
  }

  public Double getDisponible() {
    return disponible;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdIngreso();
  }

  @Override
  public void setKey(Long key) {
  	this.idIngreso = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescuentos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdIngresoEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdIngreso());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCliente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaRecepcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSaldo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaFactura());
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
		regresar.append(getImpuestos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSubTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDisponible());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descuentos", getDescuentos());
		regresar.put("idIngresoEstatus", getIdIngresoEstatus());
		regresar.put("idIngreso", getIdIngreso());
		regresar.put("idCliente", getIdCliente());
		regresar.put("idDesarrollo", getIdDesarrollo());
		regresar.put("fechaRecepcion", getFechaRecepcion());
		regresar.put("saldo", getSaldo());
		regresar.put("fechaFactura", getFechaFactura());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("total", getTotal());
		regresar.put("factura", getFactura());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("impuestos", getImpuestos());
		regresar.put("idContrato", getIdContrato());
		regresar.put("subTotal", getSubTotal());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("orden", getOrden());
		regresar.put("disponible", getDisponible());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescuentos(), getIdIngresoEstatus(), getIdIngreso(), getIdCliente(), getIdDesarrollo(), getFechaRecepcion(), getSaldo(), getFechaFactura(), getEjercicio(), getRegistro(), getConsecutivo(), getTotal(), getFactura(), getIdUsuario(), getImpuestos(), getIdContrato(), getSubTotal(), getObservaciones(), getIdEmpresa(), getOrden(), getDisponible()
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
    regresar.append("idIngreso~");
    regresar.append(getIdIngreso());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdIngreso());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetIngresosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdIngreso()!= null && getIdIngreso()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetIngresosDto other = (TcKeetIngresosDto) obj;
    if (getIdIngreso() != other.idIngreso && (getIdIngreso() == null || !getIdIngreso().equals(other.idIngreso))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdIngreso() != null ? getIdIngreso().hashCode() : 0);
    return hash;
  }

}


