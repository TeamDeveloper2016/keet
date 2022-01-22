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
@Table(name="tc_keet_anticipos_bitacora")
public class TcKeetAnticiposBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_anticipo")
  private Long idAnticipo;
  @Column (name="id_anticipo_estatus")
  private Long idAnticipoEstatus;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_anticipo_bitacora")
  private Long idAnticipoBitacora;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetAnticiposBitacoraDto() {
    this(new Long(-1L));
  }

  public TcKeetAnticiposBitacoraDto(Long key) {
    this(null, null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKeetAnticiposBitacoraDto(Long idAnticipo, Long idAnticipoEstatus, String justificacion, Long idUsuario, Long idAnticipoBitacora) {
    setIdAnticipo(idAnticipo);
    setIdAnticipoEstatus(idAnticipoEstatus);
    setJustificacion(justificacion);
    setIdUsuario(idUsuario);
    setIdAnticipoBitacora(idAnticipoBitacora);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdAnticipo(Long idAnticipo) {
    this.idAnticipo = idAnticipo;
  }

  public Long getIdAnticipo() {
    return idAnticipo;
  }

  public void setIdAnticipoEstatus(Long idAnticipoEstatus) {
    this.idAnticipoEstatus = idAnticipoEstatus;
  }

  public Long getIdAnticipoEstatus() {
    return idAnticipoEstatus;
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

  public void setIdAnticipoBitacora(Long idAnticipoBitacora) {
    this.idAnticipoBitacora = idAnticipoBitacora;
  }

  public Long getIdAnticipoBitacora() {
    return idAnticipoBitacora;
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
  	return getIdAnticipoBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idAnticipoBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdAnticipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAnticipoEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAnticipoBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idAnticipo", getIdAnticipo());
		regresar.put("idAnticipoEstatus", getIdAnticipoEstatus());
		regresar.put("justificacion", getJustificacion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idAnticipoBitacora", getIdAnticipoBitacora());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdAnticipo(), getIdAnticipoEstatus(), getJustificacion(), getIdUsuario(), getIdAnticipoBitacora(), getRegistro()
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
    regresar.append("idAnticipoBitacora~");
    regresar.append(getIdAnticipoBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAnticipoBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetAnticiposBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAnticipoBitacora()!= null && getIdAnticipoBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetAnticiposBitacoraDto other = (TcKeetAnticiposBitacoraDto) obj;
    if (getIdAnticipoBitacora() != other.idAnticipoBitacora && (getIdAnticipoBitacora() == null || !getIdAnticipoBitacora().equals(other.idAnticipoBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAnticipoBitacora() != null ? getIdAnticipoBitacora().hashCode() : 0);
    return hash;
  }

}


