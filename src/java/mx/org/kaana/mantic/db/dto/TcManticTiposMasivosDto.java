package mx.org.kaana.mantic.db.dto;

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
@Table(name="tc_mantic_tipos_masivos")
public class TcManticTiposMasivosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_tipo_masivo")
  private Long idTipoMasivo;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcManticTiposMasivosDto() {
    this(new Long(-1L));
  }

  public TcManticTiposMasivosDto(Long key) {
    this(null, new Long(-1L), null);
    setKey(key);
  }

  public TcManticTiposMasivosDto(String descripcion, Long idTipoMasivo, String nombre) {
    setDescripcion(descripcion);
    setIdTipoMasivo(idTipoMasivo);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdTipoMasivo(Long idTipoMasivo) {
    this.idTipoMasivo = idTipoMasivo;
  }

  public Long getIdTipoMasivo() {
    return idTipoMasivo;
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
  	return getIdTipoMasivo();
  }

  @Override
  public void setKey(Long key) {
  	this.idTipoMasivo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoMasivo());
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
		regresar.put("descripcion", getDescripcion());
		regresar.put("idTipoMasivo", getIdTipoMasivo());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdTipoMasivo(), getNombre(), getRegistro()
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
    regresar.append("idTipoMasivo~");
    regresar.append(getIdTipoMasivo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdTipoMasivo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticTiposMasivosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdTipoMasivo()!= null && getIdTipoMasivo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticTiposMasivosDto other = (TcManticTiposMasivosDto) obj;
    if (getIdTipoMasivo() != other.idTipoMasivo && (getIdTipoMasivo() == null || !getIdTipoMasivo().equals(other.idTipoMasivo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdTipoMasivo() != null ? getIdTipoMasivo().hashCode() : 0);
    return hash;
  }

}


