package mx.org.kaana.mantic.db.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
@Table(name="tc_mantic_puestos")
public class TcManticPuestosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="clave")
  private String clave;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_puesto")
  private Long idPuesto;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="orden")
  private Long orden;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcManticPuestosDto() {
    this(new Long(-1L));
  }

  public TcManticPuestosDto(Long key) {
    this(null, new Long(-1L), null, null, null, null, null);
    setKey(key);
  }

  public TcManticPuestosDto(String clave, Long idPuesto, String descripcion, Long orden, Long idEmpresa, String nombre, Long idUsuario) {
    setClave(clave);
    setIdPuesto(idPuesto);
    setDescripcion(descripcion);
    setOrden(orden);
    setIdEmpresa(idEmpresa);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
		setIdUsuario(idUsuario);
  }
	
  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getClave() {
    return clave;
  }

  public void setIdPuesto(Long idPuesto) {
    this.idPuesto = idPuesto;
  }

  public Long getIdPuesto() {
    return idPuesto;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setIdEmpresa(Long idEmpresa) {
    this.idEmpresa = idEmpresa;
  }

  public Long getIdEmpresa() {
    return idEmpresa;
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

	public Long getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}	
	
  @Transient
  @Override
  public Long getKey() {
  	return getIdPuesto();
  }

  @Override
  public void setKey(Long key) {
  	this.idPuesto = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPuesto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("clave", getClave());
		regresar.put("idPuesto", getIdPuesto());
		regresar.put("descripcion", getDescripcion());
		regresar.put("orden", getOrden());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
		regresar.put("idUsuario", getIdUsuario());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
			getClave(), getIdPuesto(), getDescripcion(), getOrden(), getIdEmpresa(), getNombre(), getRegistro(), getIdUsuario()
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
    regresar.append("idPuesto~");
    regresar.append(getIdPuesto());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPuesto());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticPuestosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPuesto()!= null && getIdPuesto()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticPuestosDto other = (TcManticPuestosDto) obj;
    if (getIdPuesto() != other.idPuesto && (getIdPuesto() == null || !getIdPuesto().equals(other.idPuesto))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPuesto() != null ? getIdPuesto().hashCode() : 0);
    return hash;
  }
}