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
@Table(name="tc_sakbe_maquinarias_estatus")
public class TcSakbeMaquinariasEstatusDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="id_usuarios")
  private Long idUsuarios;
  @Column (name="estatus_asociados")
  private String estatusAsociados;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_maquinaria_estatus")
  private Long idMaquinariaEstatus;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcSakbeMaquinariasEstatusDto() {
    this(new Long(-1L));
  }

  public TcSakbeMaquinariasEstatusDto(Long key) {
    this(null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcSakbeMaquinariasEstatusDto(String descripcion, Long idUsuarios, String estatusAsociados, Long idMaquinariaEstatus, String nombre) {
    setDescripcion(descripcion);
    setIdUsuarios(idUsuarios);
    setEstatusAsociados(estatusAsociados);
    setIdMaquinariaEstatus(idMaquinariaEstatus);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdUsuarios(Long idUsuarios) {
    this.idUsuarios = idUsuarios;
  }

  public Long getIdUsuarios() {
    return idUsuarios;
  }

  public void setEstatusAsociados(String estatusAsociados) {
    this.estatusAsociados = estatusAsociados;
  }

  public String getEstatusAsociados() {
    return estatusAsociados;
  }

  public void setIdMaquinariaEstatus(Long idMaquinariaEstatus) {
    this.idMaquinariaEstatus = idMaquinariaEstatus;
  }

  public Long getIdMaquinariaEstatus() {
    return idMaquinariaEstatus;
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
  	return getIdMaquinariaEstatus();
  }

  @Override
  public void setKey(Long key) {
  	this.idMaquinariaEstatus = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuarios());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEstatusAsociados());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdMaquinariaEstatus());
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
		regresar.put("idUsuarios", getIdUsuarios());
		regresar.put("estatusAsociados", getEstatusAsociados());
		regresar.put("idMaquinariaEstatus", getIdMaquinariaEstatus());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdUsuarios(), getEstatusAsociados(), getIdMaquinariaEstatus(), getNombre(), getRegistro()
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
    regresar.append("idMaquinariaEstatus~");
    regresar.append(getIdMaquinariaEstatus());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdMaquinariaEstatus());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeMaquinariasEstatusDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdMaquinariaEstatus()!= null && getIdMaquinariaEstatus()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcSakbeMaquinariasEstatusDto other = (TcSakbeMaquinariasEstatusDto) obj;
    if (getIdMaquinariaEstatus() != other.idMaquinariaEstatus && (getIdMaquinariaEstatus() == null || !getIdMaquinariaEstatus().equals(other.idMaquinariaEstatus))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdMaquinariaEstatus() != null ? getIdMaquinariaEstatus().hashCode() : 0);
    return hash;
  }

}


