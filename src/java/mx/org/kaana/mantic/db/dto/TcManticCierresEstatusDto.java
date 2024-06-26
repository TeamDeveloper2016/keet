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
@Table(name="tc_mantic_cierres_estatus")
public class TcManticCierresEstatusDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_justificacion")
  private Long idJustificacion;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_cierre_estatus")
  private Long idCierreEstatus;
  @Column (name="estatus_asociados")
  private String estatusAsociados;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcManticCierresEstatusDto() {
    this(new Long(-1L));
  }

  public TcManticCierresEstatusDto(Long key) {
    this(1L, new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcManticCierresEstatusDto(Long idJustificacion, Long idCierreEstatus, String estatusAsociados, String descripcion, String nombre) {
    setIdJustificacion(idJustificacion);
    setIdCierreEstatus(idCierreEstatus);
    setEstatusAsociados(estatusAsociados);
    setDescripcion(descripcion);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdJustificacion(Long idJustificacion) {
    this.idJustificacion = idJustificacion;
  }

  public Long getIdJustificacion() {
    return idJustificacion;
  }

  public void setIdCierreEstatus(Long idCierreEstatus) {
    this.idCierreEstatus = idCierreEstatus;
  }

  public Long getIdCierreEstatus() {
    return idCierreEstatus;
  }

  public void setEstatusAsociados(String estatusAsociados) {
    this.estatusAsociados = estatusAsociados;
  }

  public String getEstatusAsociados() {
    return estatusAsociados;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
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
  	return getIdCierreEstatus();
  }

  @Override
  public void setKey(Long key) {
  	this.idCierreEstatus = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCierreEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEstatusAsociados());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDescripcion());
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
		regresar.put("idJustificacion", getIdJustificacion());
		regresar.put("idCierreEstatus", getIdCierreEstatus());
		regresar.put("estatusAsociados", getEstatusAsociados());
		regresar.put("descripcion", getDescripcion());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdJustificacion(), getIdCierreEstatus(), getEstatusAsociados(), getDescripcion(), getNombre(), getRegistro()
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
    regresar.append("idCierreEstatus~");
    regresar.append(getIdCierreEstatus());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdCierreEstatus());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticCierresEstatusDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdCierreEstatus()!= null && getIdCierreEstatus()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticCierresEstatusDto other = (TcManticCierresEstatusDto) obj;
    if (getIdCierreEstatus() != other.idCierreEstatus && (getIdCierreEstatus() == null || !getIdCierreEstatus().equals(other.idCierreEstatus))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdCierreEstatus() != null ? getIdCierreEstatus().hashCode() : 0);
    return hash;
  }
}