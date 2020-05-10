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
@Table(name="tc_keet_rubros")
public class TcKeetRubrosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="codigo")
  private String codigo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_empaque_unidad_medida")
  private Long idEmpaqueUnidadMedida;
  @Column (name="orden")
  private Long orden;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_rubro")
  private Long idRubro;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetRubrosDto() {
    this(new Long(-1L));
  }

  public TcKeetRubrosDto(Long key) {
    this(null, null, null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKeetRubrosDto(String descripcion, String codigo, Long idUsuario, Long idEmpaqueUnidadMedida, Long orden, Long idRubro, String nombre) {
    setDescripcion(descripcion);
    setCodigo(codigo);
    setIdUsuario(idUsuario);
    setIdEmpaqueUnidadMedida(idEmpaqueUnidadMedida);
    setOrden(orden);
    setIdRubro(idRubro);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdEmpaqueUnidadMedida(Long idEmpaqueUnidadMedida) {
    this.idEmpaqueUnidadMedida = idEmpaqueUnidadMedida;
  }

  public Long getIdEmpaqueUnidadMedida() {
    return idEmpaqueUnidadMedida;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setIdRubro(Long idRubro) {
    this.idRubro = idRubro;
  }

  public Long getIdRubro() {
    return idRubro;
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
  	return getIdRubro();
  }

  @Override
  public void setKey(Long key) {
  	this.idRubro = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpaqueUnidadMedida());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdRubro());
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
		regresar.put("codigo", getCodigo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idEmpaqueUnidadMedida", getIdEmpaqueUnidadMedida());
		regresar.put("orden", getOrden());
		regresar.put("idRubro", getIdRubro());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getCodigo(), getIdUsuario(), getIdEmpaqueUnidadMedida(), getOrden(), getIdRubro(), getNombre(), getRegistro()
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
    regresar.append("idRubro~");
    regresar.append(getIdRubro());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdRubro());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetRubrosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdRubro()!= null && getIdRubro()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetRubrosDto other = (TcKeetRubrosDto) obj;
    if (getIdRubro() != other.idRubro && (getIdRubro() == null || !getIdRubro().equals(other.idRubro))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdRubro() != null ? getIdRubro().hashCode() : 0);
    return hash;
  }

}


