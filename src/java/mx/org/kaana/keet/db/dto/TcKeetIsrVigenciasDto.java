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
@Table(name="tc_keet_isr_vigencias")
public class TcKeetIsrVigenciasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="inicio")
  private LocalDate inicio;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_isr_vigencia")
  private Long idIsrVigencia;
  @Column (name="orden")
  private Long orden;
  @Column (name="termino")
  private LocalDate termino;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetIsrVigenciasDto() {
    this(new Long(-1L));
  }

  public TcKeetIsrVigenciasDto(Long key) {
    this(LocalDate.now(), new Long(-1L), null, LocalDate.now(), null);
    setKey(key);
  }

  public TcKeetIsrVigenciasDto(LocalDate inicio, Long idIsrVigencia, Long orden, LocalDate termino, Long ejercicio) {
    setInicio(inicio);
    setIdIsrVigencia(idIsrVigencia);
    setOrden(orden);
    setTermino(termino);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
  }
	
  public void setInicio(LocalDate inicio) {
    this.inicio = inicio;
  }

  public LocalDate getInicio() {
    return inicio;
  }

  public void setIdIsrVigencia(Long idIsrVigencia) {
    this.idIsrVigencia = idIsrVigencia;
  }

  public Long getIdIsrVigencia() {
    return idIsrVigencia;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setTermino(LocalDate termino) {
    this.termino = termino;
  }

  public LocalDate getTermino() {
    return termino;
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
  	return getIdIsrVigencia();
  }

  @Override
  public void setKey(Long key) {
  	this.idIsrVigencia = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getInicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdIsrVigencia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTermino());
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
		regresar.put("inicio", getInicio());
		regresar.put("idIsrVigencia", getIdIsrVigencia());
		regresar.put("orden", getOrden());
		regresar.put("termino", getTermino());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getInicio(), getIdIsrVigencia(), getOrden(), getTermino(), getEjercicio(), getRegistro()
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
    regresar.append("idIsrVigencia~");
    regresar.append(getIdIsrVigencia());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdIsrVigencia());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetIsrVigenciasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdIsrVigencia()!= null && getIdIsrVigencia()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetIsrVigenciasDto other = (TcKeetIsrVigenciasDto) obj;
    if (getIdIsrVigencia() != other.idIsrVigencia && (getIdIsrVigencia() == null || !getIdIsrVigencia().equals(other.idIsrVigencia))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdIsrVigencia() != null ? getIdIsrVigencia().hashCode() : 0);
    return hash;
  }

}


