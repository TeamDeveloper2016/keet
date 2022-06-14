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
@Table(name="tc_keet_estaciones_bitacora")
public class TcKeetEstacionesBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_estacion_estatus")
  private Long idEstacionEstatus;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_estacion")
  private Long idEstacion;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_estacion_bitacora")
  private Long idEstacionBitacora;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetEstacionesBitacoraDto() {
    this(new Long(-1L));
  }

  public TcKeetEstacionesBitacoraDto(Long key) {
    this(null, null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKeetEstacionesBitacoraDto(Long idEstacionEstatus, String justificacion, Long idUsuario, Long idEstacion, Long idEstacionBitacora) {
    setIdEstacionEstatus(idEstacionEstatus);
    setJustificacion(justificacion);
    setIdUsuario(idUsuario);
    setIdEstacion(idEstacion);
    setIdEstacionBitacora(idEstacionBitacora);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdEstacionEstatus(Long idEstacionEstatus) {
    this.idEstacionEstatus = idEstacionEstatus;
  }

  public Long getIdEstacionEstatus() {
    return idEstacionEstatus;
  }

  public void setJustificacion(String justificacion) {
    this.justificacion = justificacion;
  }

  public String getJustificacion() {
    return justificacion;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdEstacion(Long idEstacion) {
    this.idEstacion = idEstacion;
  }

  public Long getIdEstacion() {
    return idEstacion;
  }

  public void setIdEstacionBitacora(Long idEstacionBitacora) {
    this.idEstacionBitacora = idEstacionBitacora;
  }

  public Long getIdEstacionBitacora() {
    return idEstacionBitacora;
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
  	return getIdEstacionBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idEstacionBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdEstacionEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEstacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEstacionBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idEstacionEstatus", getIdEstacionEstatus());
		regresar.put("justificacion", getJustificacion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idEstacion", getIdEstacion());
		regresar.put("idEstacionBitacora", getIdEstacionBitacora());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdEstacionEstatus(), getJustificacion(), getIdUsuario(), getIdEstacion(), getIdEstacionBitacora(), getRegistro()
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
    regresar.append("idEstacionBitacora~");
    regresar.append(getIdEstacionBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdEstacionBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetEstacionesBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdEstacionBitacora()!= null && getIdEstacionBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetEstacionesBitacoraDto other = (TcKeetEstacionesBitacoraDto) obj;
    if (getIdEstacionBitacora() != other.idEstacionBitacora && (getIdEstacionBitacora() == null || !getIdEstacionBitacora().equals(other.idEstacionBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdEstacionBitacora() != null ? getIdEstacionBitacora().hashCode() : 0);
    return hash;
  }

}


