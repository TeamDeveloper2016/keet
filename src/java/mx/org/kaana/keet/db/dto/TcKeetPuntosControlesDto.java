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
@Table(name="tc_keet_puntos_controles")
public class TcKeetPuntosControlesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_punto_control")
  private Long idPuntoControl;
  @Column (name="orden")
  private Long orden;
  @Column (name="factor")
  private Double factor;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetPuntosControlesDto() {
    this(new Long(-1L));
  }

  public TcKeetPuntosControlesDto(Long key) {
    this(null, null, new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcKeetPuntosControlesDto(String descripcion, Long idUsuario, Long idPuntoControl, Long orden, Double factor, String nombre) {
    setDescripcion(descripcion);
    setIdUsuario(idUsuario);
    setIdPuntoControl(idPuntoControl);
    setOrden(orden);
    setFactor(factor);
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

  public void setIdPuntoControl(Long idPuntoControl) {
    this.idPuntoControl = idPuntoControl;
  }

  public Long getIdPuntoControl() {
    return idPuntoControl;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setFactor(Double factor) {
    this.factor = factor;
  }

  public Double getFactor() {
    return factor;
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
  	return getIdPuntoControl();
  }

  @Override
  public void setKey(Long key) {
  	this.idPuntoControl = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPuntoControl());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFactor());
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
		regresar.put("idPuntoControl", getIdPuntoControl());
		regresar.put("orden", getOrden());
		regresar.put("factor", getFactor());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdUsuario(), getIdPuntoControl(), getOrden(), getFactor(), getNombre(), getRegistro()
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
    regresar.append("idPuntoControl~");
    regresar.append(getIdPuntoControl());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPuntoControl());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPuntosControlesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPuntoControl()!= null && getIdPuntoControl()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPuntosControlesDto other = (TcKeetPuntosControlesDto) obj;
    if (getIdPuntoControl() != other.idPuntoControl && (getIdPuntoControl() == null || !getIdPuntoControl().equals(other.idPuntoControl))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPuntoControl() != null ? getIdPuntoControl().hashCode() : 0);
    return hash;
  }

}


