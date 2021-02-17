package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
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
@Table(name="tc_keet_ventas_pagos")
public class TcKeetVentasPagosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_detalle")
  private Long idDetalle;
  @Column (name="insoluto")
  private Double insoluto;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_venta_pago")
  private Long idVentaPago;
  @Column (name="pagado")
  private Double pagado;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="parcialidad")
  private Long parcialidad;
  @Column (name="saldo")
  private Double saldo;
  @Column (name="id_venta")
  private Long idVenta;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetVentasPagosDto() {
    this(new Long(-1L));
  }

  public TcKeetVentasPagosDto(Long key) {
    this(null, null, new Long(-1L), null, null, null, null, null);
    setKey(key);
  }

  public TcKeetVentasPagosDto(Long idDetalle, Double insoluto, Long idVentaPago, Double pagado, Long idUsuario, Long parcialidad, Double saldo, Long idVenta) {
    setIdDetalle(idDetalle);
    setInsoluto(insoluto);
    setIdVentaPago(idVentaPago);
    setPagado(pagado);
    setIdUsuario(idUsuario);
    setParcialidad(parcialidad);
    setSaldo(saldo);
    setIdVenta(idVenta);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdDetalle(Long idDetalle) {
    this.idDetalle = idDetalle;
  }

  public Long getIdDetalle() {
    return idDetalle;
  }

  public void setInsoluto(Double insoluto) {
    this.insoluto = insoluto;
  }

  public Double getInsoluto() {
    return insoluto;
  }

  public void setIdVentaPago(Long idVentaPago) {
    this.idVentaPago = idVentaPago;
  }

  public Long getIdVentaPago() {
    return idVentaPago;
  }

  public void setPagado(Double pagado) {
    this.pagado = pagado;
  }

  public Double getPagado() {
    return pagado;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setParcialidad(Long parcialidad) {
    this.parcialidad = parcialidad;
  }

  public Long getParcialidad() {
    return parcialidad;
  }

  public void setSaldo(Double saldo) {
    this.saldo = saldo;
  }

  public Double getSaldo() {
    return saldo;
  }

  public void setIdVenta(Long idVenta) {
    this.idVenta = idVenta;
  }

  public Long getIdVenta() {
    return idVenta;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdVentaPago();
  }

  @Override
  public void setKey(Long key) {
  	this.idVentaPago = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getInsoluto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdVentaPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPagado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getParcialidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSaldo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdVenta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idDetalle", getIdDetalle());
		regresar.put("insoluto", getInsoluto());
		regresar.put("idVentaPago", getIdVentaPago());
		regresar.put("pagado", getPagado());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("parcialidad", getParcialidad());
		regresar.put("saldo", getSaldo());
		regresar.put("idVenta", getIdVenta());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdDetalle(), getInsoluto(), getIdVentaPago(), getPagado(), getIdUsuario(), getParcialidad(), getSaldo(), getIdVenta(), getRegistro()
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
    regresar.append("idVentaPago~");
    regresar.append(getIdVentaPago());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdVentaPago());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetVentasPagosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdVentaPago()!= null && getIdVentaPago()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetVentasPagosDto other = (TcKeetVentasPagosDto) obj;
    if (getIdVentaPago() != other.idVentaPago && (getIdVentaPago() == null || !getIdVentaPago().equals(other.idVentaPago))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdVentaPago() != null ? getIdVentaPago().hashCode() : 0);
    return hash;
  }

}


