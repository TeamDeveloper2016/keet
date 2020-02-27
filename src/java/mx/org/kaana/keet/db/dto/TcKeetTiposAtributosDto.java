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
@Table(name="tc_keet_tipos_atributos")
public class TcKeetTiposAtributosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="nombre")
  private String nombre;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_tipo_atributo")
  private Long idTipoAtributo;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetTiposAtributosDto() {
    this(new Long(-1L));
  }

  public TcKeetTiposAtributosDto(Long key) {
    this(null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKeetTiposAtributosDto(String descripcion, Long idUsuario, String nombre, Long idTipoAtributo) {
    setDescripcion(descripcion);
    setIdUsuario(idUsuario);
    setNombre(nombre);
    setIdTipoAtributo(idTipoAtributo);
    setRegistro(LocalDateTime.now());
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
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

  public void setIdTipoAtributo(Long idTipoAtributo) {
    this.idTipoAtributo = idTipoAtributo;
  }

  public Long getIdTipoAtributo() {
    return idTipoAtributo;
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
  	return getIdTipoAtributo();
  }

  @Override
  public void setKey(Long key) {
  	this.idTipoAtributo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoAtributo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("nombre", getNombre());
		regresar.put("idTipoAtributo", getIdTipoAtributo());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdUsuario(), getNombre(), getIdTipoAtributo(), getRegistro()
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
    regresar.append("idTipoAtributo~");
    regresar.append(getIdTipoAtributo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdTipoAtributo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetTiposAtributosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdTipoAtributo()!= null && getIdTipoAtributo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetTiposAtributosDto other = (TcKeetTiposAtributosDto) obj;
    if (getIdTipoAtributo() != other.idTipoAtributo && (getIdTipoAtributo() == null || !getIdTipoAtributo().equals(other.idTipoAtributo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdTipoAtributo() != null ? getIdTipoAtributo().hashCode() : 0);
    return hash;
  }

}


