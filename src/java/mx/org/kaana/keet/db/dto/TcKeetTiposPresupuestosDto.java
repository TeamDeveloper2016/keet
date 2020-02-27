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
@Table(name="tc_keet_tipos_presupuestos")
public class TcKeetTiposPresupuestosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_tipo_presupuesto")
  private Long idTipoPresupuesto;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetTiposPresupuestosDto() {
    this(new Long(-1L));
  }

  public TcKeetTiposPresupuestosDto(Long key) {
    this(null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKeetTiposPresupuestosDto(String descripcion, Long idUsuario, Long idTipoPresupuesto, String nombre) {
    setDescripcion(descripcion);
    setIdUsuario(idUsuario);
    setIdTipoPresupuesto(idTipoPresupuesto);
    setNombre(nombre);
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

  public void setIdTipoPresupuesto(Long idTipoPresupuesto) {
    this.idTipoPresupuesto = idTipoPresupuesto;
  }

  public Long getIdTipoPresupuesto() {
    return idTipoPresupuesto;
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
  	return getIdTipoPresupuesto();
  }

  @Override
  public void setKey(Long key) {
  	this.idTipoPresupuesto = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoPresupuesto());
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
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idTipoPresupuesto", getIdTipoPresupuesto());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdUsuario(), getIdTipoPresupuesto(), getNombre(), getRegistro()
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
    regresar.append("idTipoPresupuesto~");
    regresar.append(getIdTipoPresupuesto());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdTipoPresupuesto());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetTiposPresupuestosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdTipoPresupuesto()!= null && getIdTipoPresupuesto()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetTiposPresupuestosDto other = (TcKeetTiposPresupuestosDto) obj;
    if (getIdTipoPresupuesto() != other.idTipoPresupuesto && (getIdTipoPresupuesto() == null || !getIdTipoPresupuesto().equals(other.idTipoPresupuesto))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdTipoPresupuesto() != null ? getIdTipoPresupuesto().hashCode() : 0);
    return hash;
  }

}


