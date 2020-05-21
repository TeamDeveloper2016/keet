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
@Table(name="tc_keet_partidas")
public class TcKeetPartidasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="codigo")
  private String codigo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_partida")
  private Long idPartida;
  @Column (name="orden")
  private Long orden;
  @Column (name="nombre")
  private String nombre;
  @Column (name="nivel")
  private Long nivel;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetPartidasDto() {
    this(new Long(-1L));
  }

  public TcKeetPartidasDto(Long key) {
    this(null, null, null, new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcKeetPartidasDto(String descripcion, String codigo, Long idUsuario, Long idPartida, Long orden, String nombre, Long nivel) {
    setDescripcion(descripcion);
    setCodigo(codigo);
    setIdUsuario(idUsuario);
    setIdPartida(idPartida);
    setOrden(orden);
    setNombre(nombre);
    setNivel(nivel);
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

  public void setIdPartida(Long idPartida) {
    this.idPartida = idPartida;
  }

  public Long getIdPartida() {
    return idPartida;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNivel(Long nivel) {
    this.nivel = nivel;
  }

  public Long getNivel() {
    return nivel;
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
  	return getIdPartida();
  }

  @Override
  public void setKey(Long key) {
  	this.idPartida = key;
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
		regresar.append(getIdPartida());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNivel());
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
		regresar.put("idPartida", getIdPartida());
		regresar.put("orden", getOrden());
		regresar.put("nombre", getNombre());
		regresar.put("nivel", getNivel());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getCodigo(), getIdUsuario(), getIdPartida(), getOrden(), getNombre(), getNivel(), getRegistro()
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
    regresar.append("idPartida~");
    regresar.append(getIdPartida());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPartida());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPartidasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPartida()!= null && getIdPartida()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPartidasDto other = (TcKeetPartidasDto) obj;
    if (getIdPartida() != other.idPartida && (getIdPartida() == null || !getIdPartida().equals(other.idPartida))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPartida() != null ? getIdPartida().hashCode() : 0);
    return hash;
  }

}


