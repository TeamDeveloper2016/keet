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
@Table(name="tc_keet_grupos_constructivos")
public class TcKeetGruposConstructivosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="clave")
  private String clave;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_grupo_constructivo")
  private Long idGrupoConstructivo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetGruposConstructivosDto() {
    this(new Long(-1L));
  }

  public TcKeetGruposConstructivosDto(Long key) {
    this(null, null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcKeetGruposConstructivosDto(String descripcion, String clave, Long idGrupoConstructivo, Long idUsuario, String nombre) {
    setDescripcion(descripcion);
    setClave(clave);
    setIdGrupoConstructivo(idGrupoConstructivo);
    setIdUsuario(idUsuario);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getClave() {
    return clave;
  }

  public void setIdGrupoConstructivo(Long idGrupoConstructivo) {
    this.idGrupoConstructivo = idGrupoConstructivo;
  }

  public Long getIdGrupoConstructivo() {
    return idGrupoConstructivo;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
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
  	return getIdGrupoConstructivo();
  }

  @Override
  public void setKey(Long key) {
  	this.idGrupoConstructivo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdGrupoConstructivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
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
		regresar.put("clave", getClave());
		regresar.put("idGrupoConstructivo", getIdGrupoConstructivo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getClave(), getIdGrupoConstructivo(), getIdUsuario(), getNombre(), getRegistro()
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
    regresar.append("idGrupoConstructivo~");
    regresar.append(getIdGrupoConstructivo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdGrupoConstructivo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetGruposConstructivosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdGrupoConstructivo()!= null && getIdGrupoConstructivo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetGruposConstructivosDto other = (TcKeetGruposConstructivosDto) obj;
    if (getIdGrupoConstructivo() != other.idGrupoConstructivo && (getIdGrupoConstructivo() == null || !getIdGrupoConstructivo().equals(other.idGrupoConstructivo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdGrupoConstructivo() != null ? getIdGrupoConstructivo().hashCode() : 0);
    return hash;
  }

}


