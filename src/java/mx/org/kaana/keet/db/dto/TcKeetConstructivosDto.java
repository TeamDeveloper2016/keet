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
@Table(name="tc_keet_constructivos")
public class TcKeetConstructivosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="id_grupo_constructivo")
  private Long idGrupoConstructivo;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_constructivo")
  private Long idConstructivo;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetConstructivosDto() {
    this(new Long(-1L));
  }

  public TcKeetConstructivosDto(Long key) {
    this(null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKeetConstructivosDto(String descripcion, Long idGrupoConstructivo, Long idConstructivo, String nombre) {
    setDescripcion(descripcion);
    setIdGrupoConstructivo(idGrupoConstructivo);
    setIdConstructivo(idConstructivo);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdGrupoConstructivo(Long idGrupoConstructivo) {
    this.idGrupoConstructivo = idGrupoConstructivo;
  }

  public Long getIdGrupoConstructivo() {
    return idGrupoConstructivo;
  }

  public void setIdConstructivo(Long idConstructivo) {
    this.idConstructivo = idConstructivo;
  }

  public Long getIdConstructivo() {
    return idConstructivo;
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
  	return getIdConstructivo();
  }

  @Override
  public void setKey(Long key) {
  	this.idConstructivo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdGrupoConstructivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdConstructivo());
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
		regresar.put("idGrupoConstructivo", getIdGrupoConstructivo());
		regresar.put("idConstructivo", getIdConstructivo());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdGrupoConstructivo(), getIdConstructivo(), getNombre(), getRegistro()
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
    regresar.append("idConstructivo~");
    regresar.append(getIdConstructivo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdConstructivo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetConstructivosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdConstructivo()!= null && getIdConstructivo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetConstructivosDto other = (TcKeetConstructivosDto) obj;
    if (getIdConstructivo() != other.idConstructivo && (getIdConstructivo() == null || !getIdConstructivo().equals(other.idConstructivo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdConstructivo() != null ? getIdConstructivo().hashCode() : 0);
    return hash;
  }

}


