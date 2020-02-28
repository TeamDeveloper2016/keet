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
@Table(name="tc_keet_prototipos_constructivos")
public class TcKeetPrototiposConstructivosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_prototipo_constructivo")
  private Long idPrototipoConstructivo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_constructivo")
  private Long idConstructivo;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_prototipo")
  private Long idPrototipo;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetPrototiposConstructivosDto() {
    this(new Long(-1L));
  }

  public TcKeetPrototiposConstructivosDto(Long key) {
    this(new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcKeetPrototiposConstructivosDto(Long idPrototipoConstructivo, Long idUsuario, Long idConstructivo, String observaciones, Long idPrototipo) {
    setIdPrototipoConstructivo(idPrototipoConstructivo);
    setIdUsuario(idUsuario);
    setIdConstructivo(idConstructivo);
    setObservaciones(observaciones);
    setIdPrototipo(idPrototipo);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdPrototipoConstructivo(Long idPrototipoConstructivo) {
    this.idPrototipoConstructivo = idPrototipoConstructivo;
  }

  public Long getIdPrototipoConstructivo() {
    return idPrototipoConstructivo;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdConstructivo(Long idConstructivo) {
    this.idConstructivo = idConstructivo;
  }

  public Long getIdConstructivo() {
    return idConstructivo;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdPrototipo(Long idPrototipo) {
    this.idPrototipo = idPrototipo;
  }

  public Long getIdPrototipo() {
    return idPrototipo;
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
  	return getIdPrototipoConstructivo();
  }

  @Override
  public void setKey(Long key) {
  	this.idPrototipoConstructivo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdPrototipoConstructivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdConstructivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrototipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idPrototipoConstructivo", getIdPrototipoConstructivo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idConstructivo", getIdConstructivo());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idPrototipo", getIdPrototipo());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdPrototipoConstructivo(), getIdUsuario(), getIdConstructivo(), getObservaciones(), getIdPrototipo(), getRegistro()
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
    regresar.append("idPrototipoConstructivo~");
    regresar.append(getIdPrototipoConstructivo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPrototipoConstructivo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPrototiposConstructivosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPrototipoConstructivo()!= null && getIdPrototipoConstructivo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPrototiposConstructivosDto other = (TcKeetPrototiposConstructivosDto) obj;
    if (getIdPrototipoConstructivo() != other.idPrototipoConstructivo && (getIdPrototipoConstructivo() == null || !getIdPrototipoConstructivo().equals(other.idPrototipoConstructivo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPrototipoConstructivo() != null ? getIdPrototipoConstructivo().hashCode() : 0);
    return hash;
  }

}


