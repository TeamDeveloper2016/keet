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
@Table(name="tc_sakbe_herramientas")
public class TcSakbeHerramientasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="clave")
  private String clave;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_herramienta")
  private Long idHerramienta;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcSakbeHerramientasDto() {
    this(new Long(-1L));
  }

  public TcSakbeHerramientasDto(Long key) {
    this(null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcSakbeHerramientasDto(String descripcion, String clave, Long idUsuario, Long idHerramienta, String nombre) {
    setDescripcion(descripcion);
    setClave(clave);
    setIdUsuario(idUsuario);
    setIdHerramienta(idHerramienta);
    setNombre(nombre);
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

  public void setIdHerramienta(Long idHerramienta) {
    this.idHerramienta = idHerramienta;
  }

  public Long getIdHerramienta() {
    return idHerramienta;
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
  	return getIdHerramienta();
  }

  @Override
  public void setKey(Long key) {
  	this.idHerramienta = key;
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
		regresar.append(getIdHerramienta());
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
		regresar.put("clave", getClave());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idHerramienta", getIdHerramienta());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getClave(), getIdUsuario(), getIdHerramienta(), getNombre(), getRegistro()
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
    regresar.append("idHerramienta~");
    regresar.append(getIdHerramienta());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdHerramienta());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeHerramientasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdHerramienta()!= null && getIdHerramienta()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcSakbeHerramientasDto other = (TcSakbeHerramientasDto) obj;
    if (getIdHerramienta() != other.idHerramienta && (getIdHerramienta() == null || !getIdHerramienta().equals(other.idHerramienta))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdHerramienta() != null ? getIdHerramienta().hashCode() : 0);
    return hash;
  }

}


