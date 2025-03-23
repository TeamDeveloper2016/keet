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
@Table(name="tc_keet_isr_subsidios")
public class TcKeetIsrSubsidiosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="subsidio")
  private Double subsidio;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_isr_subsidio")
  private Long idIsrSubsidio;
  @Column (name="id_isr_vigencia")
  private Long idIsrVigencia;
  @Column (name="sueldo_diario")
  private Double sueldoDiario;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetIsrSubsidiosDto() {
    this(new Long(-1L));
  }

  public TcKeetIsrSubsidiosDto(Long key) {
    this(null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcKeetIsrSubsidiosDto(Double subsidio, Long idIsrSubsidio, Long idIsrVigencia, Double sueldoDiario) {
    setSubsidio(subsidio);
    setIdIsrSubsidio(idIsrSubsidio);
    setIdIsrVigencia(idIsrVigencia);
    setSueldoDiario(sueldoDiario);
    setRegistro(LocalDateTime.now());
  }
	
  public void setSubsidio(Double subsidio) {
    this.subsidio = subsidio;
  }

  public Double getSubsidio() {
    return subsidio;
  }

  public void setIdIsrSubsidio(Long idIsrSubsidio) {
    this.idIsrSubsidio = idIsrSubsidio;
  }

  public Long getIdIsrSubsidio() {
    return idIsrSubsidio;
  }

  public void setIdIsrVigencia(Long idIsrVigencia) {
    this.idIsrVigencia = idIsrVigencia;
  }

  public Long getIdIsrVigencia() {
    return idIsrVigencia;
  }

  public void setSueldoDiario(Double sueldoDiario) {
    this.sueldoDiario = sueldoDiario;
  }

  public Double getSueldoDiario() {
    return sueldoDiario;
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
  	return getIdIsrSubsidio();
  }

  @Override
  public void setKey(Long key) {
  	this.idIsrSubsidio = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getSubsidio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdIsrSubsidio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdIsrVigencia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSueldoDiario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("subsidio", getSubsidio());
		regresar.put("idIsrSubsidio", getIdIsrSubsidio());
		regresar.put("idIsrVigencia", getIdIsrVigencia());
		regresar.put("sueldoDiario", getSueldoDiario());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getSubsidio(), getIdIsrSubsidio(), getIdIsrVigencia(), getSueldoDiario(), getRegistro()
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
    regresar.append("idIsrSubsidio~");
    regresar.append(getIdIsrSubsidio());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdIsrSubsidio());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetIsrSubsidiosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdIsrSubsidio()!= null && getIdIsrSubsidio()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetIsrSubsidiosDto other = (TcKeetIsrSubsidiosDto) obj;
    if (getIdIsrSubsidio() != other.idIsrSubsidio && (getIdIsrSubsidio() == null || !getIdIsrSubsidio().equals(other.idIsrSubsidio))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdIsrSubsidio() != null ? getIdIsrSubsidio().hashCode() : 0);
    return hash;
  }

}


