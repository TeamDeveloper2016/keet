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
@Table(name="tc_keet_tipos_obras")
public class TcKeetTiposObrasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="clave")
  private String clave;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_tipo_obra")
  private Long idTipoObra;
  @Column (name="nombre")
  private String nombre;
  @Column (name="id_division")
  private Long idDivision;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetTiposObrasDto() {
    this(new Long(-1L));
  }

  public TcKeetTiposObrasDto(Long key) {
    this(null, null, null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcKeetTiposObrasDto(String descripcion, String clave, Long idUsuario, Long idTipoObra, String nombre, Long idDivision) {
    setDescripcion(descripcion);
    setClave(clave);
    setIdUsuario(idUsuario);
    setIdTipoObra(idTipoObra);
    setNombre(nombre);
    setIdDivision(idDivision);
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

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdTipoObra(Long idTipoObra) {
    this.idTipoObra = idTipoObra;
  }

  public Long getIdTipoObra() {
    return idTipoObra;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setIdDivision(Long idDivision) {
    this.idDivision = idDivision;
  }

  public Long getIdDivision() {
    return idDivision;
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
  	return getIdTipoObra();
  }

  @Override
  public void setKey(Long key) {
  	this.idTipoObra = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoObra());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDivision());
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
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idTipoObra", getIdTipoObra());
		regresar.put("nombre", getNombre());
		regresar.put("idDivision", getIdDivision());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getClave(), getIdUsuario(), getIdTipoObra(), getNombre(), getIdDivision(), getRegistro()
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
    regresar.append("idTipoObra~");
    regresar.append(getIdTipoObra());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdTipoObra());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetTiposObrasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdTipoObra()!= null && getIdTipoObra()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetTiposObrasDto other = (TcKeetTiposObrasDto) obj;
    if (getIdTipoObra() != other.idTipoObra && (getIdTipoObra() == null || !getIdTipoObra().equals(other.idTipoObra))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdTipoObra() != null ? getIdTipoObra().hashCode() : 0);
    return hash;
  }

}


