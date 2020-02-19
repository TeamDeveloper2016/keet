package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.sql.Blob;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
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
@Table(name="tc_keet_nominas_personas")
public class TcKeetNominasPersonasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="neto")
  private Double neto;
  @Column (name="id_persona")
  private Long idPersona;
  @Column (name="deducciones")
  private Double deducciones;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nomina_persona")
  private Long idNominaPersona;
  @Column (name="percepciones")
  private Double percepciones;
  @Column (name="id_nomina")
  private Long idNomina;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetNominasPersonasDto() {
    this(new Long(-1L));
  }

  public TcKeetNominasPersonasDto(Long key) {
    this(null, null, null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcKeetNominasPersonasDto(Double neto, Long idPersona, Double deducciones, Long idNominaPersona, Double percepciones, Long idNomina) {
    setNeto(neto);
    setIdPersona(idPersona);
    setDeducciones(deducciones);
    setIdNominaPersona(idNominaPersona);
    setPercepciones(percepciones);
    setIdNomina(idNomina);
    setRegistro(LocalDateTime.now());
  }
	
  public void setNeto(Double neto) {
    this.neto = neto;
  }

  public Double getNeto() {
    return neto;
  }

  public void setIdPersona(Long idPersona) {
    this.idPersona = idPersona;
  }

  public Long getIdPersona() {
    return idPersona;
  }

  public void setDeducciones(Double deducciones) {
    this.deducciones = deducciones;
  }

  public Double getDeducciones() {
    return deducciones;
  }

  public void setIdNominaPersona(Long idNominaPersona) {
    this.idNominaPersona = idNominaPersona;
  }

  public Long getIdNominaPersona() {
    return idNominaPersona;
  }

  public void setPercepciones(Double percepciones) {
    this.percepciones = percepciones;
  }

  public Double getPercepciones() {
    return percepciones;
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

  @Transient
  @Override
  public Long getKey() {
  	return getIdNominaPersona();
  }

  @Override
  public void setKey(Long key) {
  	this.idNominaPersona = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getNeto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDeducciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNominaPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPercepciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNomina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("neto", getNeto());
		regresar.put("idPersona", getIdPersona());
		regresar.put("deducciones", getDeducciones());
		regresar.put("idNominaPersona", getIdNominaPersona());
		regresar.put("percepciones", getPercepciones());
		regresar.put("idNomina", getIdNomina());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getNeto(), getIdPersona(), getDeducciones(), getIdNominaPersona(), getPercepciones(), getIdNomina(), getRegistro()
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
    regresar.append("idNominaPersona~");
    regresar.append(getIdNominaPersona());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNominaPersona());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNominasPersonasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNominaPersona()!= null && getIdNominaPersona()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetNominasPersonasDto other = (TcKeetNominasPersonasDto) obj;
    if (getIdNominaPersona() != other.idNominaPersona && (getIdNominaPersona() == null || !getIdNominaPersona().equals(other.idNominaPersona))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNominaPersona() != null ? getIdNominaPersona().hashCode() : 0);
    return hash;
  }
}
