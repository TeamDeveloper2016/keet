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
@Table(name="tc_keet_ingresos_bitacora")
public class TcKeetIngresosBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_ingreso_bitacora")
  private Long idIngresoBitacora;
  @Column (name="id_ingreso_estatus")
  private Long idIngresoEstatus;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_ingreso")
  private Long idIngreso;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetIngresosBitacoraDto() {
    this(new Long(-1L));
  }

  public TcKeetIngresosBitacoraDto(Long key) {
    this(new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcKeetIngresosBitacoraDto(Long idIngresoBitacora, String justificacion, Long idUsuario, Long idIngreso, Long idIngresoEstatus) {
    setIdIngresoBitacora(idIngresoBitacora);
    setIdIngresoEstatus(idIngresoEstatus);
    setJustificacion(justificacion);
    setIdIngreso(idIngreso);
    setIdUsuario(idUsuario);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdIngresoBitacora(Long idIngresoBitacora) {
    this.idIngresoBitacora = idIngresoBitacora;
  }

  public Long getIdIngresoBitacora() {
    return idIngresoBitacora;
  }

  public void setIdIngresoEstatus(Long idIngresoEstatus) {
    this.idIngresoEstatus = idIngresoEstatus;
  }

  public Long getIdIngresoEstatus() {
    return idIngresoEstatus;
  }

  public void setJustificacion(String justificacion) {
    this.justificacion = justificacion;
  }

  public String getJustificacion() {
    return justificacion;
  }

  public void setIdIngreso(Long idIngreso) {
    this.idIngreso = idIngreso;
  }

  public Long getIdIngreso() {
    return idIngreso;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
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
  	return getIdIngresoBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idIngresoBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdIngresoBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdIngresoEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdIngreso());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idIngresoBitacora", getIdIngresoBitacora());
		regresar.put("idIngresoEstatus", getIdIngresoEstatus());
		regresar.put("justificacion", getJustificacion());
		regresar.put("idIngreso", getIdIngreso());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdIngresoBitacora(), getIdIngresoEstatus(), getJustificacion(), getIdIngreso(), getIdUsuario(), getRegistro()
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
    regresar.append("idIngresoBitacora~");
    regresar.append(getIdIngresoBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdIngresoBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetIngresosBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdIngresoBitacora()!= null && getIdIngresoBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetIngresosBitacoraDto other = (TcKeetIngresosBitacoraDto) obj;
    if (getIdIngresoBitacora() != other.idIngresoBitacora && (getIdIngresoBitacora() == null || !getIdIngresoBitacora().equals(other.idIngresoBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdIngresoBitacora() != null ? getIdIngresoBitacora().hashCode() : 0);
    return hash;
  }

}


