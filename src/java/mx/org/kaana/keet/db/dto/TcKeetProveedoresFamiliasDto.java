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
@Table(name="tc_keet_proveedores_familias")
public class TcKeetProveedoresFamiliasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_proveedor")
  private Long idProveedor;
  @Column (name="id_familia")
  private Long idFamilia;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="observaciones")
  private String observaciones;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_proveedor_familia")
  private Long idProveedorFamilia;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetProveedoresFamiliasDto() {
    this(new Long(-1L));
  }

  public TcKeetProveedoresFamiliasDto(Long key) {
    this(null, null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKeetProveedoresFamiliasDto(Long idProveedor, Long idFamilia, Long idUsuario, String observaciones, Long idProveedorFamilia) {
    setIdProveedor(idProveedor);
    setIdFamilia(idFamilia);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setIdProveedorFamilia(idProveedorFamilia);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdProveedor(Long idProveedor) {
    this.idProveedor = idProveedor;
  }

  public Long getIdProveedor() {
    return idProveedor;
  }

  public void setIdFamilia(Long idFamilia) {
    this.idFamilia = idFamilia;
  }

  public Long getIdFamilia() {
    return idFamilia;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdProveedorFamilia(Long idProveedorFamilia) {
    this.idProveedorFamilia = idProveedorFamilia;
  }

  public Long getIdProveedorFamilia() {
    return idProveedorFamilia;
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
  	return getIdProveedorFamilia();
  }

  @Override
  public void setKey(Long key) {
  	this.idProveedorFamilia = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdFamilia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProveedorFamilia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idProveedor", getIdProveedor());
		regresar.put("idFamilia", getIdFamilia());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idProveedorFamilia", getIdProveedorFamilia());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdProveedor(), getIdFamilia(), getIdUsuario(), getObservaciones(), getIdProveedorFamilia(), getRegistro()
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
    regresar.append("idProveedorFamilia~");
    regresar.append(getIdProveedorFamilia());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdProveedorFamilia());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetProveedoresFamiliasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdProveedorFamilia()!= null && getIdProveedorFamilia()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetProveedoresFamiliasDto other = (TcKeetProveedoresFamiliasDto) obj;
    if (getIdProveedorFamilia() != other.idProveedorFamilia && (getIdProveedorFamilia() == null || !getIdProveedorFamilia().equals(other.idProveedorFamilia))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdProveedorFamilia() != null ? getIdProveedorFamilia().hashCode() : 0);
    return hash;
  }
}