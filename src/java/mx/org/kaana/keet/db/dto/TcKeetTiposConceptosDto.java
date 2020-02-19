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
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Lob;
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
@Table(name="tc_keet_tipos_conceptos")
public class TcKeetTiposConceptosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_tipo_concepto")
  private Long idTipoConcepto;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetTiposConceptosDto() {
    this(new Long(-1L));
  }

  public TcKeetTiposConceptosDto(Long key) {
    this(new Long(-1L), null, null);
    setKey(key);
  }

  public TcKeetTiposConceptosDto(Long idTipoConcepto, String descripcion, String nombre) {
    setIdTipoConcepto(idTipoConcepto);
    setDescripcion(descripcion);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdTipoConcepto(Long idTipoConcepto) {
    this.idTipoConcepto = idTipoConcepto;
  }

  public Long getIdTipoConcepto() {
    return idTipoConcepto;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
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
  	return getIdTipoConcepto();
  }

  @Override
  public void setKey(Long key) {
  	this.idTipoConcepto = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdTipoConcepto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idTipoConcepto", getIdTipoConcepto());
		regresar.put("descripcion", getDescripcion());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdTipoConcepto(), getDescripcion(), getNombre(), getRegistro()
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
    regresar.append("idTipoConcepto~");
    regresar.append(getIdTipoConcepto());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdTipoConcepto());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetTiposConceptosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdTipoConcepto()!= null && getIdTipoConcepto()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetTiposConceptosDto other = (TcKeetTiposConceptosDto) obj;
    if (getIdTipoConcepto() != other.idTipoConcepto && (getIdTipoConcepto() == null || !getIdTipoConcepto().equals(other.idTipoConcepto))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdTipoConcepto() != null ? getIdTipoConcepto().hashCode() : 0);
    return hash;
  }

}


