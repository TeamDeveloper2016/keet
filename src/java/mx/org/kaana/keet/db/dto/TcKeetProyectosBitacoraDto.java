package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.sql.Blob;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Lob;
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
@Table(name="tc_keet_proyectos_bitacora")
public class TcKeetProyectosBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_proyecto_estatus")
  private Long idProyectoEstatus;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_proyecto_bitacora")
  private Long idProyectoBitacora;
  @Column (name="id_proyecto_lote")
  private Long idProyectoLote;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetProyectosBitacoraDto() {
    this(new Long(-1L));
  }

  public TcKeetProyectosBitacoraDto(Long key) {
    this(null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKeetProyectosBitacoraDto(String justificacion, Long idProyectoEstatus, Long idUsuario, Long idProyectoBitacora, Long idProyectoLote) {
    setJustificacion(justificacion);
    setIdProyectoEstatus(idProyectoEstatus);
    setIdUsuario(idUsuario);
    setIdProyectoBitacora(idProyectoBitacora);
    setIdProyectoLote(idProyectoLote);
    setRegistro(LocalDateTime.now());
  }
	
  public void setJustificacion(String justificacion) {
    this.justificacion = justificacion;
  }

  public String getJustificacion() {
    return justificacion;
  }

  public void setIdProyectoEstatus(Long idProyectoEstatus) {
    this.idProyectoEstatus = idProyectoEstatus;
  }

  public Long getIdProyectoEstatus() {
    return idProyectoEstatus;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdProyectoBitacora(Long idProyectoBitacora) {
    this.idProyectoBitacora = idProyectoBitacora;
  }

  public Long getIdProyectoBitacora() {
    return idProyectoBitacora;
  }

  public void setIdProyectoLote(Long idProyectoLote) {
    this.idProyectoLote = idProyectoLote;
  }

  public Long getIdProyectoLote() {
    return idProyectoLote;
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
  	return getIdProyectoBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idProyectoBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProyectoEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProyectoBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProyectoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("justificacion", getJustificacion());
		regresar.put("idProyectoEstatus", getIdProyectoEstatus());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idProyectoBitacora", getIdProyectoBitacora());
		regresar.put("idProyectoLote", getIdProyectoLote());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getJustificacion(), getIdProyectoEstatus(), getIdUsuario(), getIdProyectoBitacora(), getIdProyectoLote(), getRegistro()
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
    regresar.append("idProyectoBitacora~");
    regresar.append(getIdProyectoBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdProyectoBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetProyectosBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdProyectoBitacora()!= null && getIdProyectoBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetProyectosBitacoraDto other = (TcKeetProyectosBitacoraDto) obj;
    if (getIdProyectoBitacora() != other.idProyectoBitacora && (getIdProyectoBitacora() == null || !getIdProyectoBitacora().equals(other.idProyectoBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdProyectoBitacora() != null ? getIdProyectoBitacora().hashCode() : 0);
    return hash;
  }

}


