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
@Table(name="tc_keet_prestamos")
public class TcKeetPrestamosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="consecutivo")
  private String consecutivo;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_prestamo")
  private Long idPrestamo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_afecta_nomina")
  private Long idAfectaNomina;
  @Column (name="id_deudor")
  private Long idDeudor;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="saldo")
  private Double saldo;
  @Column (name="orden")
  private Long orden;
  @Column (name="importe")
  private Double importe;
  @Column (name="id_prestamo_estatus")
  private Long idPrestamoEstatus;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="semanas")
  private Long semanas;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="id_desarrollo")
  private Long idDesarrollo;
  @Column (name="id_contrato")
  private Long idContrato;

  public TcKeetPrestamosDto() {
    this(new Long(-1L));
  }

  public TcKeetPrestamosDto(Long key) {
    this(null, new Long(-1L), null, null, null, null, null, null, null, null, null, 1L, null, null, null);
    setKey(key);
  }

  public TcKeetPrestamosDto(String consecutivo, Long idPrestamo, Long idUsuario, Long idAfectaNomina, Long idDeudor, String observaciones, Double saldo, Long orden, Double importe, Long idPrestamoEstatus, Long ejercicio, Long semanas, Long idEmpresa, Long idContrato, Long idDesarrollo) {
    setConsecutivo(consecutivo);
    setIdPrestamo(idPrestamo);
    setIdUsuario(idUsuario);
    setIdAfectaNomina(idAfectaNomina);
    setIdDeudor(idDeudor);
    setObservaciones(observaciones);
    setSaldo(saldo);
    setOrden(orden);
    setImporte(importe);
    setIdPrestamoEstatus(idPrestamoEstatus);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
    this.semanas= semanas;
    setIdEmpresa(idEmpresa);
    setIdContrato(idContrato);
    setIdDesarrollo(idDesarrollo);
  }
	
  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public String getConsecutivo() {
    return consecutivo;
  }

  public void setIdPrestamo(Long idPrestamo) {
    this.idPrestamo = idPrestamo;
  }

  public Long getIdPrestamo() {
    return idPrestamo;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdAfectaNomina(Long idAfectaNomina) {
    this.idAfectaNomina = idAfectaNomina;
  }

  public Long getIdAfectaNomina() {
    return idAfectaNomina;
  }

  public void setIdDeudor(Long idDeudor) {
    this.idDeudor = idDeudor;
  }

  public Long getIdDeudor() {
    return idDeudor;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setSaldo(Double saldo) {
    this.saldo = saldo;
  }

  public Double getSaldo() {
    return saldo;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setImporte(Double importe) {
    this.importe = importe;
  }

  public Double getImporte() {
    return importe;
  }

  public void setIdPrestamoEstatus(Long idPrestamoEstatus) {
    this.idPrestamoEstatus = idPrestamoEstatus;
  }

  public Long getIdPrestamoEstatus() {
    return idPrestamoEstatus;
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

  public Long getSemanas() {
    return semanas;
  }

  public void setSemanas(Long semanas) {
    this.semanas = semanas;
  }

  public Long getIdEmpresa() {
    return idEmpresa;
  }

  public void setIdEmpresa(Long idEmpresa) {
    this.idEmpresa = idEmpresa;
  }
  
  public Long getIdContrato() {
    return idContrato;
  }

  public void setIdContrato(Long idContrato) {
    this.idContrato = idContrato;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }
  
  @Transient
  @Override
  public Long getKey() {
  	return getIdPrestamo();
  }

  @Override
  public void setKey(Long key) {
  	this.idPrestamo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrestamo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAfectaNomina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDeudor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSaldo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrestamoEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSemanas());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idPrestamo", getIdPrestamo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idAfectaNomina", getIdAfectaNomina());
		regresar.put("idDeudor", getIdDeudor());
		regresar.put("observaciones", getObservaciones());
		regresar.put("saldo", getSaldo());
		regresar.put("orden", getOrden());
		regresar.put("importe", getImporte());
		regresar.put("idPrestamoEstatus", getIdPrestamoEstatus());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("semanas", getSemanas());
		regresar.put("idDesarrollo", getIdDesarrollo());
		regresar.put("idContrato", getIdContrato());
		regresar.put("idEmpresa", getIdEmpresa());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getConsecutivo(), getIdPrestamo(), getIdUsuario(), getIdAfectaNomina(), getIdDeudor(), getObservaciones(), getSaldo(), getOrden(), getImporte(), getIdPrestamoEstatus(), getEjercicio(), getRegistro(), getSemanas(), getIdDesarrollo(), getIdContrato(), getIdEmpresa()
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
    regresar.append("idPrestamo~");
    regresar.append(getIdPrestamo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPrestamo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPrestamosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPrestamo()!= null && getIdPrestamo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPrestamosDto other = (TcKeetPrestamosDto) obj;
    if (getIdPrestamo() != other.idPrestamo && (getIdPrestamo() == null || !getIdPrestamo().equals(other.idPrestamo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPrestamo() != null ? getIdPrestamo().hashCode() : 0);
    return hash;
  }

}


