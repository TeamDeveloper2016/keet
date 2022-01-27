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
@Table(name="tc_keet_anticipos_lotes")
public class TcKeetAnticiposLotesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="anticipo")
  private Double anticipo;
  @Column (name="id_anticipo")
  private Long idAnticipo;
  @Column (name="codigo")
  private String codigo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_pagado")
  private Long idPagado;
  @Column (name="id_contrato_lote")
  private Long idContratoLote;
  @Column (name="id_estacion")
  private Long idEstacion;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_anticipo_lote")
  private Long idAnticipoLote;
	@Column (name="pagado")
  private Double pagado;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetAnticiposLotesDto() {
    this(new Long(-1L));
  }

  public TcKeetAnticiposLotesDto(Long key) {
    this(null, null, null, null, null, null, null, new Long(-1L), 0D);
    setKey(key);
  }

  public TcKeetAnticiposLotesDto(Double anticipo, Long idAnticipo, String codigo, Long idUsuario, Long idPagado, Long idContratoLote, Long idEstacion, Long idAnticipoLote, Double pagado) {
    setAnticipo(anticipo);
    setIdAnticipo(idAnticipo);
    setCodigo(codigo);
    setIdUsuario(idUsuario);
    setIdPagado(idPagado);
    setIdContratoLote(idContratoLote);
    setIdEstacion(idEstacion);
    setIdAnticipoLote(idAnticipoLote);
    setPagado(pagado);
    setRegistro(LocalDateTime.now());
  }
	
  public void setAnticipo(Double anticipo) {
    this.anticipo = anticipo;
  }

  public Double getAnticipo() {
    return anticipo;
  }

  public void setIdAnticipo(Long idAnticipo) {
    this.idAnticipo = idAnticipo;
  }

  public Long getIdAnticipo() {
    return idAnticipo;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getCodigo() {
    return codigo;
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

  public void setIdAnticipoLote(Long idAnticipoLote) {
    this.idAnticipoLote = idAnticipoLote;
  }

  public Long getIdAnticipoLote() {
    return idAnticipoLote;
  }

  public Double getPagado() {
    return pagado;
  }

  public void setPagado(Double pagado) {
    this.pagado = pagado;
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
  	return getIdAnticipoLote();
  }

  @Override
  public void setKey(Long key) {
  	this.idAnticipoLote = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getAnticipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAnticipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPagado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEstacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAnticipoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPagado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("anticipo", getAnticipo());
		regresar.put("idAnticipo", getIdAnticipo());
		regresar.put("codigo", getCodigo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idPagado", getIdPagado());
		regresar.put("idContratoLote", getIdContratoLote());
		regresar.put("idEstacion", getIdEstacion());
		regresar.put("idAnticipoLote", getIdAnticipoLote());
		regresar.put("pagado", getPagado());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getAnticipo(), getIdAnticipo(), getCodigo(), getIdUsuario(), getIdPagado(), getIdContratoLote(), getIdEstacion(), getIdAnticipoLote(), getPagado(), getRegistro()
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
    regresar.append("idAnticipoLote~");
    regresar.append(getIdAnticipoLote());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAnticipoLote());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetAnticiposLotesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAnticipoLote()!= null && getIdAnticipoLote()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetAnticiposLotesDto other = (TcKeetAnticiposLotesDto) obj;
    if (getIdAnticipoLote() != other.idAnticipoLote && (getIdAnticipoLote() == null || !getIdAnticipoLote().equals(other.idAnticipoLote))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAnticipoLote() != null ? getIdAnticipoLote().hashCode() : 0);
    return hash;
  }

}


