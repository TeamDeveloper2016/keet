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
@Table(name="tc_keet_prestamos_lotes")
public class TcKeetPrestamosLotesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="anticipo")
  private Double anticipo;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_prestamo_lote")
  private Long idPrestamoLote;
  @Column (name="codigo")
  private String codigo;
  @Column (name="id_prestamo")
  private Long idPrestamo;
  @Column (name="pagado")
  private Double pagado;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_pagado")
  private Long idPagado;
  @Column (name="id_contrato_lote")
  private Long idContratoLote;
  @Column (name="id_estacion")
  private Long idEstacion;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetPrestamosLotesDto() {
    this(new Long(-1L));
  }

  public TcKeetPrestamosLotesDto(Long key) {
    this(null, new Long(-1L), null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKeetPrestamosLotesDto(Double anticipo, Long idPrestamoLote, String codigo, Long idPrestamo, Double pagado, Long idUsuario, Long idPagado, Long idContratoLote, Long idEstacion) {
    setAnticipo(anticipo);
    setIdPrestamoLote(idPrestamoLote);
    setCodigo(codigo);
    setIdPrestamo(idPrestamo);
    setPagado(pagado);
    setIdUsuario(idUsuario);
    setIdPagado(idPagado);
    setIdContratoLote(idContratoLote);
    setIdEstacion(idEstacion);
    setRegistro(LocalDateTime.now());
  }
	
  public void setAnticipo(Double anticipo) {
    this.anticipo = anticipo;
  }

  public Double getAnticipo() {
    return anticipo;
  }

  public void setIdPrestamoLote(Long idPrestamoLote) {
    this.idPrestamoLote = idPrestamoLote;
  }

  public Long getIdPrestamoLote() {
    return idPrestamoLote;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setIdPrestamo(Long idPrestamo) {
    this.idPrestamo = idPrestamo;
  }

  public Long getIdPrestamo() {
    return idPrestamo;
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

  public void setIdPagado(Long idPagado) {
    this.idPagado = idPagado;
  }

  public Long getIdPagado() {
    return idPagado;
  }

  public void setIdContratoLote(Long idContratoLote) {
    this.idContratoLote = idContratoLote;
  }

  public Long getIdContratoLote() {
    return idContratoLote;
  }

  public void setIdEstacion(Long idEstacion) {
    this.idEstacion = idEstacion;
  }

  public Long getIdEstacion() {
    return idEstacion;
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
  	return getIdPrestamoLote();
  }

  @Override
  public void setKey(Long key) {
  	this.idPrestamoLote = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getAnticipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrestamoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrestamo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPagado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPagado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEstacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("anticipo", getAnticipo());
		regresar.put("idPrestamoLote", getIdPrestamoLote());
		regresar.put("codigo", getCodigo());
		regresar.put("idPrestamo", getIdPrestamo());
		regresar.put("pagado", getPagado());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idPagado", getIdPagado());
		regresar.put("idContratoLote", getIdContratoLote());
		regresar.put("idEstacion", getIdEstacion());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getAnticipo(), getIdPrestamoLote(), getCodigo(), getIdPrestamo(), getPagado(), getIdUsuario(), getIdPagado(), getIdContratoLote(), getIdEstacion(), getRegistro()
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
    regresar.append("idPrestamoLote~");
    regresar.append(getIdPrestamoLote());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPrestamoLote());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPrestamosLotesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPrestamoLote()!= null && getIdPrestamoLote()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPrestamosLotesDto other = (TcKeetPrestamosLotesDto) obj;
    if (getIdPrestamoLote() != other.idPrestamoLote && (getIdPrestamoLote() == null || !getIdPrestamoLote().equals(other.idPrestamoLote))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPrestamoLote() != null ? getIdPrestamoLote().hashCode() : 0);
    return hash;
  }

}


