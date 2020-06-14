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
@Table(name="tc_keet_cajas_chicas_cierres")
public class TcKeetCajasChicasCierresDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="acumulado")
  private Double acumulado;
  @Column (name="id_caja_chica_cierre_estatus")
  private Long idCajaChicaCierreEstatus;
  @Column (name="ejecicio")
  private Long ejecicio;
  @Column (name="saldo")
  private Double saldo;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_caja_chica_cierre")
  private Long idCajaChicaCierre;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="id_caja_chica")
  private Long idCajaChica;
  @Column (name="id_nomina_periodo")
  private Long idNominaPeriodo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="orden")
  private Long orden;
  @Column (name="termino")
  private LocalDateTime termino;
  @Column (name="disponible")
  private Double disponible;

  public TcKeetCajasChicasCierresDto() {
    this(new Long(-1L));
  }

  public TcKeetCajasChicasCierresDto(Long key) {
    this(null, null, null, null, new Long(-1L), null, null, null, null, null, null, LocalDateTime.now(), null);
    setKey(key);
  }

  public TcKeetCajasChicasCierresDto(Double acumulado, Long idCajaChicaCierreEstatus, Long ejecicio, Double saldo, Long idCajaChicaCierre, String consecutivo, Long idCajaChica, Long idNominaPeriodo, Long idUsuario, String observaciones, Long orden, LocalDateTime termino, Double disponible) {
    setAcumulado(acumulado);
    setIdCajaChicaCierreEstatus(idCajaChicaCierreEstatus);
    setEjecicio(ejecicio);
    setSaldo(saldo);
    setIdCajaChicaCierre(idCajaChicaCierre);
    setRegistro(LocalDateTime.now());
    setConsecutivo(consecutivo);
    setIdCajaChica(idCajaChica);
    setIdNominaPeriodo(idNominaPeriodo);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setOrden(orden);
    setTermino(termino);
    setDisponible(disponible);
  }
	
  public void setAcumulado(Double acumulado) {
    this.acumulado = acumulado;
  }

  public Double getAcumulado() {
    return acumulado;
  }

  public void setIdCajaChicaCierreEstatus(Long idCajaChicaCierreEstatus) {
    this.idCajaChicaCierreEstatus = idCajaChicaCierreEstatus;
  }

  public Long getIdCajaChicaCierreEstatus() {
    return idCajaChicaCierreEstatus;
  }

  public void setEjecicio(Long ejecicio) {
    this.ejecicio = ejecicio;
  }

  public Long getEjecicio() {
    return ejecicio;
  }

  public void setSaldo(Double saldo) {
    this.saldo = saldo;
  }

  public Double getSaldo() {
    return saldo;
  }

  public void setIdCajaChicaCierre(Long idCajaChicaCierre) {
    this.idCajaChicaCierre = idCajaChicaCierre;
  }

  public Long getIdCajaChicaCierre() {
    return idCajaChicaCierre;
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

  public void setIdCajaChica(Long idCajaChica) {
    this.idCajaChica = idCajaChica;
  }

  public Long getIdCajaChica() {
    return idCajaChica;
  }

  public void setIdNominaPeriodo(Long idNominaPeriodo) {
    this.idNominaPeriodo = idNominaPeriodo;
  }

  public Long getIdNominaPeriodo() {
    return idNominaPeriodo;
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

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setTermino(LocalDateTime termino) {
    this.termino = termino;
  }

  public LocalDateTime getTermino() {
    return termino;
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
  	return getIdCajaChicaCierre();
  }

  @Override
  public void setKey(Long key) {
  	this.idCajaChicaCierre = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getAcumulado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCajaChicaCierreEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjecicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSaldo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCajaChicaCierre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCajaChica());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaPeriodo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTermino());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDisponible());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("acumulado", getAcumulado());
		regresar.put("idCajaChicaCierreEstatus", getIdCajaChicaCierreEstatus());
		regresar.put("ejecicio", getEjecicio());
		regresar.put("saldo", getSaldo());
		regresar.put("idCajaChicaCierre", getIdCajaChicaCierre());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idCajaChica", getIdCajaChica());
		regresar.put("idNominaPeriodo", getIdNominaPeriodo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("orden", getOrden());
		regresar.put("termino", getTermino());
		regresar.put("disponible", getDisponible());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getAcumulado(), getIdCajaChicaCierreEstatus(), getEjecicio(), getSaldo(), getIdCajaChicaCierre(), getRegistro(), getConsecutivo(), getIdCajaChica(), getIdNominaPeriodo(), getIdUsuario(), getObservaciones(), getOrden(), getTermino(), getDisponible()
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
    regresar.append("idCajaChicaCierre~");
    regresar.append(getIdCajaChicaCierre());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdCajaChicaCierre());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetCajasChicasCierresDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdCajaChicaCierre()!= null && getIdCajaChicaCierre()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetCajasChicasCierresDto other = (TcKeetCajasChicasCierresDto) obj;
    if (getIdCajaChicaCierre() != other.idCajaChicaCierre && (getIdCajaChicaCierre() == null || !getIdCajaChicaCierre().equals(other.idCajaChicaCierre))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdCajaChicaCierre() != null ? getIdCajaChicaCierre().hashCode() : 0);
    return hash;
  }

}


