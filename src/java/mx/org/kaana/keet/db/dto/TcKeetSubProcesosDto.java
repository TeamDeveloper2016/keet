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
@Table(name="tc_keet_sub_procesos")
public class TcKeetSubProcesosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_proceso")
  private Long idProceso;
  @Column (name="descripcion")
  private String descripcion;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_sub_proceso")
  private Long idSubProceso;
  @Column (name="nombre")
  private String nombre;
	@Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetSubProcesosDto() {
    this(new Long(-1L));
  }

  public TcKeetSubProcesosDto(Long key) {
    this(null, null, new Long(-1L), null, -1L);
    setKey(key);
  }

  public TcKeetSubProcesosDto(Long idProceso, String descripcion, Long idSubProceso, String nombre, Long idUsuario) {
    setIdProceso(idProceso);
    setDescripcion(descripcion);
    setIdSubProceso(idSubProceso);
    setNombre(nombre);
    setIdUsuario(idUsuario);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdProceso(Long idProceso) {
    this.idProceso = idProceso;
  }

  public Long getIdProceso() {
    return idProceso;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdSubProceso(Long idSubProceso) {
    this.idSubProceso = idSubProceso;
  }

  public Long getIdSubProceso() {
    return idSubProceso;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
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
  	return getIdSubProceso();
  }

  @Override
  public void setKey(Long key) {
  	this.idSubProceso = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdProceso());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdSubProceso());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idProceso", getIdProceso());
		regresar.put("descripcion", getDescripcion());
		regresar.put("idSubProceso", getIdSubProceso());
		regresar.put("nombre", getNombre());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getIdProceso(), getDescripcion(), getIdSubProceso(), getNombre(), getIdUsuario(), getRegistro()
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
    regresar.append("idSubProceso~");
    regresar.append(getIdSubProceso());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdSubProceso());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetSubProcesosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdSubProceso()!= null && getIdSubProceso()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetSubProcesosDto other = (TcKeetSubProcesosDto) obj;
    if (getIdSubProceso() != other.idSubProceso && (getIdSubProceso() == null || !getIdSubProceso().equals(other.idSubProceso))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdSubProceso() != null ? getIdSubProceso().hashCode() : 0);
    return hash;
  }

}


