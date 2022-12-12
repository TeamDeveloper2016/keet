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
@Table(name="tc_keet_notificaciones")
public class TcKeetNotificacionesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="contacto")
  private String contacto;
  @Column (name="id_activo")
  private Long idActivo;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_notificacion")
  private Long idNotificacion;
  @Column (name="proceso")
  private String proceso;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="empresa")
  private String empresa;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetNotificacionesDto() {
    this(new Long(-1L));
  }

  public TcKeetNotificacionesDto(Long key) {
    this(null, null, new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcKeetNotificacionesDto(String contacto, Long idActivo, Long idNotificacion, String proceso, String observaciones, String empresa, String nombre) {
    setContacto(contacto);
    setIdActivo(idActivo);
    setIdNotificacion(idNotificacion);
    setProceso(proceso);
    setObservaciones(observaciones);
    setEmpresa(empresa);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
  }
	
  public void setContacto(String contacto) {
    this.contacto = contacto;
  }

  public String getContacto() {
    return contacto;
  }

  public void setIdActivo(Long idActivo) {
    this.idActivo = idActivo;
  }

  public Long getIdActivo() {
    return idActivo;
  }

  public void setIdNotificacion(Long idNotificacion) {
    this.idNotificacion = idNotificacion;
  }

  public Long getIdNotificacion() {
    return idNotificacion;
  }

  public void setProceso(String proceso) {
    this.proceso = proceso;
  }

  public String getProceso() {
    return proceso;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setEmpresa(String empresa) {
    this.empresa = empresa;
  }

  public String getEmpresa() {
    return empresa;
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
  	return getIdNotificacion();
  }

  @Override
  public void setKey(Long key) {
  	this.idNotificacion = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getContacto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdActivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getProceso());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEmpresa());
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
		regresar.put("contacto", getContacto());
		regresar.put("idActivo", getIdActivo());
		regresar.put("idNotificacion", getIdNotificacion());
		regresar.put("proceso", getProceso());
		regresar.put("observaciones", getObservaciones());
		regresar.put("empresa", getEmpresa());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getContacto(), getIdActivo(), getIdNotificacion(), getProceso(), getObservaciones(), getEmpresa(), getNombre(), getRegistro()
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
    regresar.append("idNotificacion~");
    regresar.append(getIdNotificacion());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNotificacion());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNotificacionesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNotificacion()!= null && getIdNotificacion()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetNotificacionesDto other = (TcKeetNotificacionesDto) obj;
    if (getIdNotificacion() != other.idNotificacion && (getIdNotificacion() == null || !getIdNotificacion().equals(other.idNotificacion))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNotificacion() != null ? getIdNotificacion().hashCode() : 0);
    return hash;
  }

}


