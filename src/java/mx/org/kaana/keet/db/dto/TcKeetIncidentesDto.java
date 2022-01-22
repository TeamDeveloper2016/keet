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
@Table(name="tc_keet_incidentes")
public class TcKeetIncidentesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_tipo_incidente")
  private Long idTipoIncidente;
  @Column (name="id_anticipo")
  private Long idAnticipo;
  @Column (name="id_proveedor")
  private Long idProveedor;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_incidente")
  private Long idIncidente;
  @Column (name="id_desarrollo")
  private Long idDesarrollo;
  @Column (name="id_incidente_estatus")
  private Long idIncidenteEstatus;
  @Column (name="inicio")
  private LocalDate inicio;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="id_nomina")
  private Long idNomina;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="termino")
  private LocalDate termino;
  @Column (name="orden")
  private Long orden;
  @Column (name="costo")
  private Double costo;

  public TcKeetIncidentesDto() {
    this(new Long(-1L));
  }

  public TcKeetIncidentesDto(Long key) {
    this(null, null, null, new Long(-1L), null, null, LocalDate.now(), null, null, null, null, null, LocalDate.now(), null, 0D);
    setKey(key);
  }

  public TcKeetIncidentesDto(Long idTipoIncidente, Long idAnticipo, Long idProveedor, Long idIncidente, Long idDesarrollo, Long idIncidenteEstatus, LocalDate inicio, Long ejercicio, Long idNomina, String consecutivo, Long idUsuario, String observaciones, LocalDate termino, Long orden, Double costo) {
    setIdTipoIncidente(idTipoIncidente);
    setIdAnticipo(idAnticipo);
    setIdProveedor(idProveedor);
    setIdIncidente(idIncidente);
    setIdDesarrollo(idDesarrollo);
    setIdIncidenteEstatus(idIncidenteEstatus);
    setInicio(inicio);
    setEjercicio(ejercicio);
    setIdNomina(idNomina);
    setRegistro(LocalDateTime.now());
    setConsecutivo(consecutivo);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setTermino(termino);
    setOrden(orden);
    setCosto(costo);
  }
	
  public void setIdTipoIncidente(Long idTipoIncidente) {
    this.idTipoIncidente = idTipoIncidente;
  }

  public Long getIdTipoIncidente() {
    return idTipoIncidente;
  }

  public void setIdAnticipo(Long idAnticipo) {
    this.idAnticipo = idAnticipo;
  }

  public Long getIdAnticipo() {
    return idAnticipo;
  }

  public void setIdProveedor(Long idProveedor) {
    this.idProveedor = idProveedor;
  }

  public Long getIdProveedor() {
    return idProveedor;
  }

  public void setIdIncidente(Long idIncidente) {
    this.idIncidente = idIncidente;
  }

  public Long getIdIncidente() {
    return idIncidente;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setIdIncidenteEstatus(Long idIncidenteEstatus) {
    this.idIncidenteEstatus = idIncidenteEstatus;
  }

  public Long getIdIncidenteEstatus() {
    return idIncidenteEstatus;
  }

  public void setInicio(LocalDate inicio) {
    this.inicio = inicio;
  }

  public LocalDate getInicio() {
    return inicio;
  }

  public void setEjercicio(Long ejercicio) {
    this.ejercicio = ejercicio;
  }

  public Long getEjercicio() {
    return ejercicio;
  }

  public void setIdNomina(Long idNomina) {
    this.idNomina = idNomina;
  }

  public Long getIdNomina() {
    return idNomina;
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

  public void setTermino(LocalDate termino) {
    this.termino = termino;
  }

  public LocalDate getTermino() {
    return termino;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public Double getCosto() {
    return costo;
  }

  public void setCosto(Double costo) {
    this.costo = costo;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdIncidente();
  }

  @Override
  public void setKey(Long key) {
  	this.idIncidente = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdTipoIncidente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAnticipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdIncidente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdIncidenteEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getInicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNomina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTermino());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCosto());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idTipoIncidente", getIdTipoIncidente());
		regresar.put("idAnticipo", getIdAnticipo());
		regresar.put("idProveedor", getIdProveedor());
		regresar.put("idIncidente", getIdIncidente());
		regresar.put("idDesarrollo", getIdDesarrollo());
		regresar.put("idIncidenteEstatus", getIdIncidenteEstatus());
		regresar.put("inicio", getInicio());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("idNomina", getIdNomina());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("termino", getTermino());
		regresar.put("orden", getOrden());
		regresar.put("costo", getCosto());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getIdTipoIncidente(), getIdAnticipo(), getIdProveedor(), getIdIncidente(), getIdDesarrollo(), getIdIncidenteEstatus(), getInicio(), getEjercicio(), getIdNomina(), getRegistro(), getConsecutivo(), getIdUsuario(), getObservaciones(), getTermino(), getOrden(), getCosto()
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
    regresar.append("idIncidente~");
    regresar.append(getIdIncidente());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdIncidente());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetIncidentesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdIncidente()!= null && getIdIncidente()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetIncidentesDto other = (TcKeetIncidentesDto) obj;
    if (getIdIncidente() != other.idIncidente && (getIdIncidente() == null || !getIdIncidente().equals(other.idIncidente))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdIncidente() != null ? getIdIncidente().hashCode() : 0);
    return hash;
  }

}


