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
@Table(name="tc_keet_proveedores_materiales")
public class TcKeetProveedoresMaterialesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_proveedor")
  private Long idProveedor;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_proveedor_material")
  private Long idProveedorMaterial;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetProveedoresMaterialesDto() {
    this(new Long(-1L));
  }

  public TcKeetProveedoresMaterialesDto(Long key) {
    this(null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKeetProveedoresMaterialesDto(Long idProveedor, Long idUsuario, Long idProveedorMaterial, Long idArticulo) {
    setIdProveedor(idProveedor);
    setIdUsuario(idUsuario);
    setIdProveedorMaterial(idProveedorMaterial);
    setIdArticulo(idArticulo);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdProveedor(Long idProveedor) {
    this.idProveedor = idProveedor;
  }

  public Long getIdProveedor() {
    return idProveedor;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdProveedorMaterial(Long idProveedorMaterial) {
    this.idProveedorMaterial = idProveedorMaterial;
  }

  public Long getIdProveedorMaterial() {
    return idProveedorMaterial;
  }

  public void setIdArticulo(Long idArticulo) {
    this.idArticulo = idArticulo;
  }

  public Long getIdArticulo() {
    return idArticulo;
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
  	return getIdProveedorMaterial();
  }

  @Override
  public void setKey(Long key) {
  	this.idProveedorMaterial = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProveedorMaterial());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idProveedor", getIdProveedor());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idProveedorMaterial", getIdProveedorMaterial());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdProveedor(), getIdUsuario(), getIdProveedorMaterial(), getIdArticulo(), getRegistro()
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
    regresar.append("idProveedorMaterial~");
    regresar.append(getIdProveedorMaterial());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdProveedorMaterial());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetProveedoresMaterialesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdProveedorMaterial()!= null && getIdProveedorMaterial()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetProveedoresMaterialesDto other = (TcKeetProveedoresMaterialesDto) obj;
    if (getIdProveedorMaterial() != other.idProveedorMaterial && (getIdProveedorMaterial() == null || !getIdProveedorMaterial().equals(other.idProveedorMaterial))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdProveedorMaterial() != null ? getIdProveedorMaterial().hashCode() : 0);
    return hash;
  }
}