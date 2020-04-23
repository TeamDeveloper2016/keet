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
@Table(name="tc_keet_proveedores_departamentos")
public class TcKeetProveedoresDepartamentosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_proveedor_departamento")
  private Long idProveedorDepartamento;
  @Column (name="id_proveedor")
  private Long idProveedor;
  @Column (name="id_departamento")
  private Long idDepartamento;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetProveedoresDepartamentosDto() {
    this(new Long(-1L));
  }

  public TcKeetProveedoresDepartamentosDto(Long key) {
    this(new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcKeetProveedoresDepartamentosDto(Long idProveedorDepartamento, Long idProveedor, Long idDepartamento, Long idUsuario) {
    setIdProveedorDepartamento(idProveedorDepartamento);
    setIdProveedor(idProveedor);
    setIdDepartamento(idDepartamento);
    setIdUsuario(idUsuario);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdProveedorDepartamento(Long idProveedorDepartamento) {
    this.idProveedorDepartamento = idProveedorDepartamento;
  }

  public Long getIdProveedorDepartamento() {
    return idProveedorDepartamento;
  }

  public void setIdProveedor(Long idProveedor) {
    this.idProveedor = idProveedor;
  }

  public Long getIdProveedor() {
    return idProveedor;
  }

  public void setIdDepartamento(Long idDepartamento) {
    this.idDepartamento = idDepartamento;
  }

  public Long getIdDepartamento() {
    return idDepartamento;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
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
  	return getIdProveedorDepartamento();
  }

  @Override
  public void setKey(Long key) {
  	this.idProveedorDepartamento = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdProveedorDepartamento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDepartamento());
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
		regresar.put("idProveedorDepartamento", getIdProveedorDepartamento());
		regresar.put("idProveedor", getIdProveedor());
		regresar.put("idDepartamento", getIdDepartamento());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdProveedorDepartamento(), getIdProveedor(), getIdDepartamento(), getIdUsuario(), getRegistro()
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
    regresar.append("idProveedorDepartamento~");
    regresar.append(getIdProveedorDepartamento());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdProveedorDepartamento());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetProveedoresDepartamentosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdProveedorDepartamento()!= null && getIdProveedorDepartamento()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetProveedoresDepartamentosDto other = (TcKeetProveedoresDepartamentosDto) obj;
    if (getIdProveedorDepartamento() != other.idProveedorDepartamento && (getIdProveedorDepartamento() == null || !getIdProveedorDepartamento().equals(other.idProveedorDepartamento))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdProveedorDepartamento() != null ? getIdProveedorDepartamento().hashCode() : 0);
    return hash;
  }

}


