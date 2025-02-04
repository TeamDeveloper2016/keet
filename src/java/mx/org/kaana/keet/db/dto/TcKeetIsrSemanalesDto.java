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
@Table(name="tc_keet_isr_semanales")
public class TcKeetIsrSemanalesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_isr_semanal")
  private Long idIsrSemanal;
  @Column (name="superior")
  private Double superior;
  @Column (name="inferior")
  private Double inferior;
  @Column (name="id_isr_vigencia")
  private Long idIsrVigencia;
  @Column (name="porcentaje")
  private Double porcentaje;
  @Column (name="cuota_fija")
  private Double cuotaFija;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetIsrSemanalesDto() {
    this(new Long(-1L));
  }

  public TcKeetIsrSemanalesDto(Long key) {
    this(new Long(-1L), null, null, null, null, null);
    setKey(key);
  }

  public TcKeetIsrSemanalesDto(Long idIsrSemanal, Double superior, Double inferior, Long idIsrVigencia, Double porcentaje, Double cuotaFija) {
    setIdIsrSemanal(idIsrSemanal);
    setSuperior(superior);
    setInferior(inferior);
    setIdIsrVigencia(idIsrVigencia);
    setPorcentaje(porcentaje);
    setCuotaFija(cuotaFija);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdIsrSemanal(Long idIsrSemanal) {
    this.idIsrSemanal = idIsrSemanal;
  }

  public Long getIdIsrSemanal() {
    return idIsrSemanal;
  }

  public void setSuperior(Double superior) {
    this.superior = superior;
  }

  public Double getSuperior() {
    return superior;
  }

  public void setInferior(Double inferior) {
    this.inferior = inferior;
  }

  public Double getInferior() {
    return inferior;
  }

  public void setIdIsrVigencia(Long idIsrVigencia) {
    this.idIsrVigencia = idIsrVigencia;
  }

  public Long getIdIsrVigencia() {
    return idIsrVigencia;
  }

  public void setPorcentaje(Double porcentaje) {
    this.porcentaje = porcentaje;
  }

  public Double getPorcentaje() {
    return porcentaje;
  }

  public void setCuotaFija(Double cuotaFija) {
    this.cuotaFija = cuotaFija;
  }

  public Double getCuotaFija() {
    return cuotaFija;
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
  	return getIdIsrSemanal();
  }

  @Override
  public void setKey(Long key) {
  	this.idIsrSemanal = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdIsrSemanal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSuperior());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getInferior());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdIsrVigencia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPorcentaje());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCuotaFija());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idIsrSemanal", getIdIsrSemanal());
		regresar.put("superior", getSuperior());
		regresar.put("inferior", getInferior());
		regresar.put("idIsrVigencia", getIdIsrVigencia());
		regresar.put("porcentaje", getPorcentaje());
		regresar.put("cuotaFija", getCuotaFija());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdIsrSemanal(), getSuperior(), getInferior(), getIdIsrVigencia(), getPorcentaje(), getCuotaFija(), getRegistro()
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
    regresar.append("idIsrSemanal~");
    regresar.append(getIdIsrSemanal());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdIsrSemanal());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetIsrSemanalesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdIsrSemanal()!= null && getIdIsrSemanal()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetIsrSemanalesDto other = (TcKeetIsrSemanalesDto) obj;
    if (getIdIsrSemanal() != other.idIsrSemanal && (getIdIsrSemanal() == null || !getIdIsrSemanal().equals(other.idIsrSemanal))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdIsrSemanal() != null ? getIdIsrSemanal().hashCode() : 0);
    return hash;
  }

}


