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
@Table(name="tc_keet_anticipos_pagos")
public class TcKeetAnticiposPagosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="cambio")
  private Double cambio;
  @Column (name="id_anticipo")
  private Long idAnticipo;
  @Column (name="id_afecta_nomina")
  private Long idAfectaNomina;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="observaciones")
  private String observaciones;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_anticipo_pago")
  private Long idAnticipoPago;
  @Column (name="abono")
  private Double abono;
  @Column (name="orden")
  private Long orden;
  @Column (name="pago")
  private Double pago;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetAnticiposPagosDto() {
    this(new Long(-1L));
  }

  public TcKeetAnticiposPagosDto(Long key) {
    this(null, null, null, null, null, null, new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcKeetAnticiposPagosDto(String consecutivo, Double cambio, Long idAnticipo, Long idAfectaNomina, Long idUsuario, String observaciones, Long idAnticipoPago, Double abono, Long orden, Double pago, Long ejercicio) {
    setConsecutivo(consecutivo);
    setCambio(cambio);
    setIdAnticipo(idAnticipo);
    setIdAfectaNomina(idAfectaNomina);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setIdAnticipoPago(idAnticipoPago);
    setAbono(abono);
    setOrden(orden);
    setPago(pago);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
  }
	
  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public String getConsecutivo() {
    return consecutivo;
  }

  public void setCambio(Double cambio) {
    this.cambio = cambio;
  }

  public Double getCambio() {
    return cambio;
  }

  public void setIdAnticipo(Long idAnticipo) {
    this.idAnticipo = idAnticipo;
  }

  public Long getIdAnticipo() {
    return idAnticipo;
  }

  public void setIdAfectaNomina(Long idAfectaNomina) {
    this.idAfectaNomina = idAfectaNomina;
  }

  public Long getIdAfectaNomina() {
    return idAfectaNomina;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdAnticipoPago(Long idAnticipoPago) {
    this.idAnticipoPago = idAnticipoPago;
  }

  public Long getIdAnticipoPago() {
    return idAnticipoPago;
  }

  public void setAbono(Double abono) {
    this.abono = abono;
  }

  public Double getAbono() {
    return abono;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setPago(Double pago) {
    this.pago = pago;
  }

  public Double getPago() {
    return pago;
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

  @Transient
  @Override
  public Long getKey() {
  	return getIdAnticipoPago();
  }

  @Override
  public void setKey(Long key) {
  	this.idAnticipoPago = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCambio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAnticipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAfectaNomina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAnticipoPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAbono());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("cambio", getCambio());
		regresar.put("idAnticipo", getIdAnticipo());
		regresar.put("idAfectaNomina", getIdAfectaNomina());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idAnticipoPago", getIdAnticipoPago());
		regresar.put("abono", getAbono());
		regresar.put("orden", getOrden());
		regresar.put("pago", getPago());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getConsecutivo(), getCambio(), getIdAnticipo(), getIdAfectaNomina(), getIdUsuario(), getObservaciones(), getIdAnticipoPago(), getAbono(), getOrden(), getPago(), getEjercicio(), getRegistro()
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
    regresar.append("idAnticipoPago~");
    regresar.append(getIdAnticipoPago());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAnticipoPago());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetAnticiposPagosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAnticipoPago()!= null && getIdAnticipoPago()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetAnticiposPagosDto other = (TcKeetAnticiposPagosDto) obj;
    if (getIdAnticipoPago() != other.idAnticipoPago && (getIdAnticipoPago() == null || !getIdAnticipoPago().equals(other.idAnticipoPago))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAnticipoPago() != null ? getIdAnticipoPago().hashCode() : 0);
    return hash;
  }

}


