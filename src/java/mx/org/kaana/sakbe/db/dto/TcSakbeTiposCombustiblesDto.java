package mx.org.kaana.sakbe.db.dto;

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
@Table(name="tc_sakbe_tipos_combustibles")
public class TcSakbeTiposCombustiblesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_tipo_combustible")
  private Long idTipoCombustible;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;
	@Column (name="id_tipo_insumo")
  private Long idTipoInsumo;

  public TcSakbeTiposCombustiblesDto() {
    this(new Long(-1L));
  }

  public TcSakbeTiposCombustiblesDto(Long key) {
    this(null, null, new Long(-1L), null, 1L);
    setKey(key);
  }

  public TcSakbeTiposCombustiblesDto(String descripcion, Long idUsuario, Long idTipoCombustible, String nombre, Long idTipoInsumo) {
    setDescripcion(descripcion);
    setIdUsuario(idUsuario);
    setIdTipoCombustible(idTipoCombustible);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
    setIdTipoInsumo(idTipoInsumo);
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

  public void setIdTipoCombustible(Long idTipoCombustible) {
    this.idTipoCombustible = idTipoCombustible;
  }

  public Long getIdTipoCombustible() {
    return idTipoCombustible;
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

  public Long getIdTipoInsumo() {
    return idTipoInsumo;
  }

  public void setIdTipoInsumo(Long idTipoInsumo) {
    this.idTipoInsumo = idTipoInsumo;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdTipoCombustible();
  }

  @Override
  public void setKey(Long key) {
  	this.idTipoCombustible = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoCombustible());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoInsumo());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idTipoCombustible", getIdTipoCombustible());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
		regresar.put("idTipoInsumo", getIdTipoInsumo());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getDescripcion(), getIdUsuario(), getIdTipoCombustible(), getNombre(), getRegistro(), getIdTipoInsumo()
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
    regresar.append("idTipoCombustible~");
    regresar.append(getIdTipoCombustible());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdTipoCombustible());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeTiposCombustiblesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdTipoCombustible()!= null && getIdTipoCombustible()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcSakbeTiposCombustiblesDto other = (TcSakbeTiposCombustiblesDto) obj;
    if (getIdTipoCombustible() != other.idTipoCombustible && (getIdTipoCombustible() == null || !getIdTipoCombustible().equals(other.idTipoCombustible))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdTipoCombustible() != null ? getIdTipoCombustible().hashCode() : 0);
    return hash;
  }

}


