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
@Table(name="tc_keet_planos")
public class TcKeetPlanosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_plano")
  private Long idPlano;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_especialidad")
  private Long idEspecialidad;
  @Column (name="decripcion")
  private String decripcion;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetPlanosDto() {
    this(new Long(-1L));
  }

  public TcKeetPlanosDto(Long key) {
    this(new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcKeetPlanosDto(Long idPlano, Long idUsuario, Long idEspecialidad, String decripcion, String nombre) {
    setIdPlano(idPlano);
    setIdUsuario(idUsuario);
    setIdEspecialidad(idEspecialidad);
    setDecripcion(decripcion);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdPlano(Long idPlano) {
    this.idPlano = idPlano;
  }

  public Long getIdPlano() {
    return idPlano;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdEspecialidad(Long idEspecialidad) {
    this.idEspecialidad = idEspecialidad;
  }

  public Long getIdEspecialidad() {
    return idEspecialidad;
  }

  public void setDecripcion(String decripcion) {
    this.decripcion = decripcion;
  }

  public String getDecripcion() {
    return decripcion;
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
  	return getIdPlano();
  }

  @Override
  public void setKey(Long key) {
  	this.idPlano = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdPlano());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEspecialidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDecripcion());
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
		regresar.put("idPlano", getIdPlano());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idEspecialidad", getIdEspecialidad());
		regresar.put("decripcion", getDecripcion());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdPlano(), getIdUsuario(), getIdEspecialidad(), getDecripcion(), getNombre(), getRegistro()
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
    regresar.append("idPlano~");
    regresar.append(getIdPlano());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPlano());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPlanosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPlano()!= null && getIdPlano()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPlanosDto other = (TcKeetPlanosDto) obj;
    if (getIdPlano() != other.idPlano && (getIdPlano() == null || !getIdPlano().equals(other.idPlano))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPlano() != null ? getIdPlano().hashCode() : 0);
    return hash;
  }

}


