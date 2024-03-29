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
@Table(name="tc_keet_prototipos_dias")
public class TcKeetPrototiposDiasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_prototipo_dia")
  private Long idPrototipoDia;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_partida")
  private Long idPartida;
  @Column (name="dias")
  private Long dias;
  @Column (name="id_prototipo")
  private Long idPrototipo;
  @Column (name="orden")
  private Long orden;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetPrototiposDiasDto() {
    this(new Long(-1L));
  }

  public TcKeetPrototiposDiasDto(Long key) {
    this(new Long(-1L), null, null, null, null, null);
    setKey(key);
  }

  public TcKeetPrototiposDiasDto(Long idPrototipoDia, Long idUsuario, Long idPartida, Long dias, Long idPrototipo, Long orden) {
    setIdPrototipoDia(idPrototipoDia);
    setIdUsuario(idUsuario);
    setIdPartida(idPartida);
    setDias(dias);
    setIdPrototipo(idPrototipo);
    setOrden(orden);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdPrototipoDia(Long idPrototipoDia) {
    this.idPrototipoDia = idPrototipoDia;
  }

  public Long getIdPrototipoDia() {
    return idPrototipoDia;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdPartida(Long idPartida) {
    this.idPartida = idPartida;
  }

  public Long getIdPartida() {
    return idPartida;
  }

  public void setDias(Long dias) {
    this.dias = dias;
  }

  public Long getDias() {
    return dias;
  }

  public void setIdPrototipo(Long idPrototipo) {
    this.idPrototipo = idPrototipo;
  }

  public Long getIdPrototipo() {
    return idPrototipo;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
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
  	return getIdPrototipoDia();
  }

  @Override
  public void setKey(Long key) {
  	this.idPrototipoDia = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdPrototipoDia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPartida());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDias());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrototipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idPrototipoDia", getIdPrototipoDia());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idPartida", getIdPartida());
		regresar.put("dias", getDias());
		regresar.put("idPrototipo", getIdPrototipo());
		regresar.put("orden", getOrden());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdPrototipoDia(), getIdUsuario(), getIdPartida(), getDias(), getIdPrototipo(), getOrden(), getRegistro()
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
    regresar.append("idPrototipoDia~");
    regresar.append(getIdPrototipoDia());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPrototipoDia());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPrototiposDiasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPrototipoDia()!= null && getIdPrototipoDia()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPrototiposDiasDto other = (TcKeetPrototiposDiasDto) obj;
    if (getIdPrototipoDia() != other.idPrototipoDia && (getIdPrototipoDia() == null || !getIdPrototipoDia().equals(other.idPrototipoDia))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPrototipoDia() != null ? getIdPrototipoDia().hashCode() : 0);
    return hash;
  }

}


