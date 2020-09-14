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
@Table(name="tc_keet_controles_bitacora")
public class TcKeetControlesBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_control_estatus")
  private Long idControlEstatus;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_control")
  private Long idControl;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_control_bitacora")
  private Long idControlBitacora;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetControlesBitacoraDto() {
    this(new Long(-1L));
  }

  public TcKeetControlesBitacoraDto(Long key) {
    this(null, null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKeetControlesBitacoraDto(Long idControlEstatus, String justificacion, Long idUsuario, Long idControl, Long idControlBitacora) {
    setIdControlEstatus(idControlEstatus);
    setJustificacion(justificacion);
    setIdUsuario(idUsuario);
    setIdControl(idControl);
    setIdControlBitacora(idControlBitacora);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdControlEstatus(Long idControlEstatus) {
    this.idControlEstatus = idControlEstatus;
  }

  public Long getIdControlEstatus() {
    return idControlEstatus;
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

  public void setIdControl(Long idControl) {
    this.idControl = idControl;
  }

  public Long getIdControl() {
    return idControl;
  }

  public void setIdControlBitacora(Long idControlBitacora) {
    this.idControlBitacora = idControlBitacora;
  }

  public Long getIdControlBitacora() {
    return idControlBitacora;
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
  	return getIdControlBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idControlBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdControlEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdControl());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdControlBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idControlEstatus", getIdControlEstatus());
		regresar.put("justificacion", getJustificacion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idControl", getIdControl());
		regresar.put("idControlBitacora", getIdControlBitacora());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdControlEstatus(), getJustificacion(), getIdUsuario(), getIdControl(), getIdControlBitacora(), getRegistro()
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
    regresar.append("idControlBitacora~");
    regresar.append(getIdControlBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdControlBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetControlesBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdControlBitacora()!= null && getIdControlBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetControlesBitacoraDto other = (TcKeetControlesBitacoraDto) obj;
    if (getIdControlBitacora() != other.idControlBitacora && (getIdControlBitacora() == null || !getIdControlBitacora().equals(other.idControlBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdControlBitacora() != null ? getIdControlBitacora().hashCode() : 0);
    return hash;
  }

}


