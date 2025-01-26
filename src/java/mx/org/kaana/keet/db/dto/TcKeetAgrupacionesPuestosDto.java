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
@Table(name="tc_keet_agrupaciones_puestos")
public class TcKeetAgrupacionesPuestosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_agrupacion")
  private Long idAgrupacion;
  @Column (name="id_puesto")
  private Long idPuesto;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_agrupacion_puesto")
  private Long idAgrupacionPuesto;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetAgrupacionesPuestosDto() {
    this(new Long(-1L));
  }

  public TcKeetAgrupacionesPuestosDto(Long key) {
    this(null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKeetAgrupacionesPuestosDto(Long idAgrupacion, Long idPuesto, Long idUsuario, Long idAgrupacionPuesto) {
    setIdAgrupacion(idAgrupacion);
    setIdPuesto(idPuesto);
    setIdUsuario(idUsuario);
    setIdAgrupacionPuesto(idAgrupacionPuesto);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdAgrupacion(Long idAgrupacion) {
    this.idAgrupacion = idAgrupacion;
  }

  public Long getIdAgrupacion() {
    return idAgrupacion;
  }

  public void setIdPuesto(Long idPuesto) {
    this.idPuesto = idPuesto;
  }

  public Long getIdPuesto() {
    return idPuesto;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdAgrupacionPuesto(Long idAgrupacionPuesto) {
    this.idAgrupacionPuesto = idAgrupacionPuesto;
  }

  public Long getIdAgrupacionPuesto() {
    return idAgrupacionPuesto;
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
  	return getIdAgrupacionPuesto();
  }

  @Override
  public void setKey(Long key) {
  	this.idAgrupacionPuesto = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdAgrupacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPuesto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAgrupacionPuesto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idAgrupacion", getIdAgrupacion());
		regresar.put("idPuesto", getIdPuesto());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idAgrupacionPuesto", getIdAgrupacionPuesto());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdAgrupacion(), getIdPuesto(), getIdUsuario(), getIdAgrupacionPuesto(), getRegistro()
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
    regresar.append("idAgrupacionPuesto~");
    regresar.append(getIdAgrupacionPuesto());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAgrupacionPuesto());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetAgrupacionesPuestosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAgrupacionPuesto()!= null && getIdAgrupacionPuesto()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetAgrupacionesPuestosDto other = (TcKeetAgrupacionesPuestosDto) obj;
    if (getIdAgrupacionPuesto() != other.idAgrupacionPuesto && (getIdAgrupacionPuesto() == null || !getIdAgrupacionPuesto().equals(other.idAgrupacionPuesto))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAgrupacionPuesto() != null ? getIdAgrupacionPuesto().hashCode() : 0);
    return hash;
  }

}


