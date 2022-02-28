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
@Table(name="tc_keet_prototipos_habiles")
public class TcKeetPrototiposHabilesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_prototipo_habil")
  private Long idPrototipoHabil;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_nombre_dia")
  private Long idNombreDia;
  @Column (name="id_prototipo")
  private Long idPrototipo;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetPrototiposHabilesDto() {
    this(new Long(-1L));
  }

  public TcKeetPrototiposHabilesDto(Long key) {
    this(new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcKeetPrototiposHabilesDto(Long idPrototipoHabil, Long idUsuario, Long idNombreDia, Long idPrototipo) {
    setIdPrototipoHabil(idPrototipoHabil);
    setIdUsuario(idUsuario);
    setIdNombreDia(idNombreDia);
    setIdPrototipo(idPrototipo);
    setRegistro(LocalDateTime.now());
  }

  public Long getIdPrototipoHabil() {
    return idPrototipoHabil;
  }

  public void setIdPrototipoHabil(Long idPrototipoHabil) {
    this.idPrototipoHabil = idPrototipoHabil;
  }

  public Long getIdNombreDia() {
    return idNombreDia;
  }

  public void setIdNombreDia(Long idNombreDia) {
    this.idNombreDia = idNombreDia;
  }
	
  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
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
  	return getIdPrototipoHabil();
  }

  @Override
  public void setKey(Long key) {
  	this.idPrototipoHabil = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdPrototipoHabil());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNombreDia());
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
		regresar.put("idPrototipoDia", getIdPrototipoHabil());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idPartida", getIdNombreDia());
		regresar.put("idPrototipo", getIdPrototipo());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getIdPrototipoHabil(), getIdUsuario(), getIdNombreDia(), getIdPrototipo(), getRegistro()
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
    regresar.append("idPrototipoHabil~");
    regresar.append(getIdPrototipoHabil());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPrototipoHabil());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPrototiposHabilesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPrototipoHabil()!= null && getIdPrototipoHabil()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPrototiposHabilesDto other = (TcKeetPrototiposHabilesDto) obj;
    if (getIdPrototipoHabil() != other.getIdPrototipoHabil() && (getIdPrototipoHabil() == null || !getIdPrototipoHabil().equals(other.getIdPrototipoHabil()))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPrototipoHabil() != null ? getIdPrototipoHabil().hashCode() : 0);
    return hash;
  }

}


