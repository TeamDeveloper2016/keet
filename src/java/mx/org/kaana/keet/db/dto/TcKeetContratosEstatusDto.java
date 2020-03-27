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
@Table(name="tc_keet_contratos_estatus")
public class TcKeetContratosEstatusDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_estatus")
  private Long idContratoEstatus;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="id_justificacion")
  private Long idJustificacion;
  @Column (name="estatus_asociados")
  private String estatusAsociados;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetContratosEstatusDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosEstatusDto(Long key) {
    this(new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcKeetContratosEstatusDto(Long idContratoEstatus, String descripcion, Long idJustificacion, String estatusAsociados, String nombre) {
    setIdContratoEstatus(idContratoEstatus);
    setDescripcion(descripcion);
    setIdJustificacion(idJustificacion);
    setEstatusAsociados(estatusAsociados);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdContratoEstatus(Long idContratoEstatus) {
    this.idContratoEstatus = idContratoEstatus;
  }

  public Long getIdContratoEstatus() {
    return idContratoEstatus;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdJustificacion(Long idJustificacion) {
    this.idJustificacion = idJustificacion;
  }

  public Long getIdJustificacion() {
    return idJustificacion;
  }

  public void setEstatusAsociados(String estatusAsociados) {
    this.estatusAsociados = estatusAsociados;
  }

  public String getEstatusAsociados() {
    return estatusAsociados;
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
  	return getIdContratoEstatus();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoEstatus = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdContratoEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEstatusAsociados());
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
		regresar.put("idContratoEstatus", getIdContratoEstatus());
		regresar.put("descripcion", getDescripcion());
		regresar.put("idJustificacion", getIdJustificacion());
		regresar.put("estatusAsociados", getEstatusAsociados());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdContratoEstatus(), getDescripcion(), getIdJustificacion(), getEstatusAsociados(), getNombre(), getRegistro()
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
    regresar.append("idContratoEstatus~");
    regresar.append(getIdContratoEstatus());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoEstatus());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosEstatusDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoEstatus()!= null && getIdContratoEstatus()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosEstatusDto other = (TcKeetContratosEstatusDto) obj;
    if (getIdContratoEstatus() != other.idContratoEstatus && (getIdContratoEstatus() == null || !getIdContratoEstatus().equals(other.idContratoEstatus))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoEstatus() != null ? getIdContratoEstatus().hashCode() : 0);
    return hash;
  }

}


