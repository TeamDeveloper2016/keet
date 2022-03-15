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
@Table(name="tc_keet_estimaciones_bitacora")
public class TcKeetEstimacionesBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_estimacion_estatus")
  private Long idEstimacionEstatus;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_estimacion_bitacora")
  private Long idEstimacionBitacora;
  @Column (name="id_estimacion")
  private Long idEstimacion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="justifiacion")
  private String justifiacion;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetEstimacionesBitacoraDto() {
    this(new Long(-1L));
  }

  public TcKeetEstimacionesBitacoraDto(Long key) {
    this(null, new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcKeetEstimacionesBitacoraDto(Long idEstimacionEstatus, Long idEstimacionBitacora, Long idEstimacion, Long idUsuario, String justifiacion) {
    setIdEstimacionEstatus(idEstimacionEstatus);
    setIdEstimacionBitacora(idEstimacionBitacora);
    setIdEstimacion(idEstimacion);
    setIdUsuario(idUsuario);
    setJustifiacion(justifiacion);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdEstimacionEstatus(Long idEstimacionEstatus) {
    this.idEstimacionEstatus = idEstimacionEstatus;
  }

  public Long getIdEstimacionEstatus() {
    return idEstimacionEstatus;
  }

  public void setIdEstimacionBitacora(Long idEstimacionBitacora) {
    this.idEstimacionBitacora = idEstimacionBitacora;
  }

  public Long getIdEstimacionBitacora() {
    return idEstimacionBitacora;
  }

  public void setIdEstimacion(Long idEstimacion) {
    this.idEstimacion = idEstimacion;
  }

  public Long getIdEstimacion() {
    return idEstimacion;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setJustifiacion(String justifiacion) {
    this.justifiacion = justifiacion;
  }

  public String getJustifiacion() {
    return justifiacion;
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
  	return getIdEstimacionBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idEstimacionBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdEstimacionEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEstimacionBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEstimacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getJustifiacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idEstimacionEstatus", getIdEstimacionEstatus());
		regresar.put("idEstimacionBitacora", getIdEstimacionBitacora());
		regresar.put("idEstimacion", getIdEstimacion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("justifiacion", getJustifiacion());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdEstimacionEstatus(), getIdEstimacionBitacora(), getIdEstimacion(), getIdUsuario(), getJustifiacion(), getRegistro()
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
    regresar.append("idEstimacionBitacora~");
    regresar.append(getIdEstimacionBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdEstimacionBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetEstimacionesBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdEstimacionBitacora()!= null && getIdEstimacionBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetEstimacionesBitacoraDto other = (TcKeetEstimacionesBitacoraDto) obj;
    if (getIdEstimacionBitacora() != other.idEstimacionBitacora && (getIdEstimacionBitacora() == null || !getIdEstimacionBitacora().equals(other.idEstimacionBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdEstimacionBitacora() != null ? getIdEstimacionBitacora().hashCode() : 0);
    return hash;
  }

}


