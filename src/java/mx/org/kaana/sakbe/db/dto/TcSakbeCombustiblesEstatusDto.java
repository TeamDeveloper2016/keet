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
@Table(name="tc_sakbe_combustibles_estatus")
public class TcSakbeCombustiblesEstatusDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="estatus_asociados")
  private String estatusAsociados;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_combustible_estatus")
  private Long idCombustibleEstatus;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcSakbeCombustiblesEstatusDto() {
    this(new Long(-1L));
  }

  public TcSakbeCombustiblesEstatusDto(Long key) {
    this(null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcSakbeCombustiblesEstatusDto(String descripcion, Long idUsuario, String estatusAsociados, Long idCombustibleEstatus, String nombre) {
    setDescripcion(descripcion);
    setIdUsuario(idUsuario);
    setEstatusAsociados(estatusAsociados);
    setIdCombustibleEstatus(idCombustibleEstatus);
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

  public void setEstatusAsociados(String estatusAsociados) {
    this.estatusAsociados = estatusAsociados;
  }

  public String getEstatusAsociados() {
    return estatusAsociados;
  }

  public void setIdCombustibleEstatus(Long idCombustibleEstatus) {
    this.idCombustibleEstatus = idCombustibleEstatus;
  }

  public Long getIdCombustibleEstatus() {
    return idCombustibleEstatus;
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
  	return getIdCombustibleEstatus();
  }

  @Override
  public void setKey(Long key) {
  	this.idCombustibleEstatus = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEstatusAsociados());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCombustibleEstatus());
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
		regresar.put("estatusAsociados", getEstatusAsociados());
		regresar.put("idCombustibleEstatus", getIdCombustibleEstatus());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdUsuario(), getEstatusAsociados(), getIdCombustibleEstatus(), getNombre(), getRegistro()
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
    regresar.append("idCombustibleEstatus~");
    regresar.append(getIdCombustibleEstatus());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdCombustibleEstatus());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeCombustiblesEstatusDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdCombustibleEstatus()!= null && getIdCombustibleEstatus()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcSakbeCombustiblesEstatusDto other = (TcSakbeCombustiblesEstatusDto) obj;
    if (getIdCombustibleEstatus() != other.idCombustibleEstatus && (getIdCombustibleEstatus() == null || !getIdCombustibleEstatus().equals(other.idCombustibleEstatus))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdCombustibleEstatus() != null ? getIdCombustibleEstatus().hashCode() : 0);
    return hash;
  }

}


