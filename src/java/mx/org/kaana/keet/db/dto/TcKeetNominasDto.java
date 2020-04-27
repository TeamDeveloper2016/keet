package mx.org.kaana.kajool.db.dto;

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
@Table(name="tc_keet_nominas")
public class TcKeetNominasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="fecha_dispersion")
  private LocalDate fechaDispersion;
  @Column (name="id_nomina_estatus")
  private Long idNominaEstatus;
  @Column (name="id_nomina_periodo")
  private Long idNominaPeriodo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_tipo_nomina")
  private Long idTipoNomina;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nomina")
  private Long idNomina;
  @Column (name="fecha_pago")
  private LocalDate fechaPago;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetNominasDto() {
    this(new Long(-1L));
  }

  public TcKeetNominasDto(Long key) {
    this(LocalDate.now(), null, null, null, null, new Long(-1L), LocalDate.now());
    setKey(key);
  }

  public TcKeetNominasDto(LocalDate fechaDispersion, Long idNominaEstatus, Long idNominaPeriodo, Long idUsuario, Long idTipoNomina, Long idNomina, LocalDate fechaPago) {
    setFechaDispersion(fechaDispersion);
    setIdNominaEstatus(idNominaEstatus);
    setIdNominaPeriodo(idNominaPeriodo);
    setIdUsuario(idUsuario);
    setIdTipoNomina(idTipoNomina);
    setIdNomina(idNomina);
    setFechaPago(fechaPago);
    setRegistro(LocalDateTime.now());
  }
	
  public void setFechaDispersion(LocalDate fechaDispersion) {
    this.fechaDispersion = fechaDispersion;
  }

  public LocalDate getFechaDispersion() {
    return fechaDispersion;
  }

  public void setIdNominaEstatus(Long idNominaEstatus) {
    this.idNominaEstatus = idNominaEstatus;
  }

  public Long getIdNominaEstatus() {
    return idNominaEstatus;
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

  public void setIdTipoNomina(Long idTipoNomina) {
    this.idTipoNomina = idTipoNomina;
  }

  public Long getIdTipoNomina() {
    return idTipoNomina;
  }

  public void setIdNomina(Long idNomina) {
    this.idNomina = idNomina;
  }

  public Long getIdNomina() {
    return idNomina;
  }

  public void setFechaPago(LocalDate fechaPago) {
    this.fechaPago = fechaPago;
  }

  public LocalDate getFechaPago() {
    return fechaPago;
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
  	return getIdNomina();
  }

  @Override
  public void setKey(Long key) {
  	this.idNomina = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getFechaDispersion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaPeriodo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoNomina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNomina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("fechaDispersion", getFechaDispersion());
		regresar.put("idNominaEstatus", getIdNominaEstatus());
		regresar.put("idNominaPeriodo", getIdNominaPeriodo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idTipoNomina", getIdTipoNomina());
		regresar.put("idNomina", getIdNomina());
		regresar.put("fechaPago", getFechaPago());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getFechaDispersion(), getIdNominaEstatus(), getIdNominaPeriodo(), getIdUsuario(), getIdTipoNomina(), getIdNomina(), getFechaPago(), getRegistro()
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
    regresar.append("idNomina~");
    regresar.append(getIdNomina());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNomina());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNominasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNomina()!= null && getIdNomina()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetNominasDto other = (TcKeetNominasDto) obj;
    if (getIdNomina() != other.idNomina && (getIdNomina() == null || !getIdNomina().equals(other.idNomina))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNomina() != null ? getIdNomina().hashCode() : 0);
    return hash;
  }

}


