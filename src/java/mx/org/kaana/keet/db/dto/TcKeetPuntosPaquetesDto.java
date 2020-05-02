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
@Table(name="tc_keet_puntos_paquetes")
public class TcKeetPuntosPaquetesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_punto_paquete")
  private Long idPuntoPaquete;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_punto_control")
  private Long idPuntoControl;
  @Column (name="id_punto_grupo")
  private Long idPuntoGrupo;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetPuntosPaquetesDto() {
    this(new Long(-1L));
  }

  public TcKeetPuntosPaquetesDto(Long key) {
    this(new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcKeetPuntosPaquetesDto(Long idPuntoPaquete, Long idUsuario, Long idPuntoControl, Long idPuntoGrupo) {
    setIdPuntoPaquete(idPuntoPaquete);
    setIdUsuario(idUsuario);
    setIdPuntoControl(idPuntoControl);
    setIdPuntoGrupo(idPuntoGrupo);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdPuntoPaquete(Long idPuntoPaquete) {
    this.idPuntoPaquete = idPuntoPaquete;
  }

  public Long getIdPuntoPaquete() {
    return idPuntoPaquete;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdPuntoControl(Long idPuntoControl) {
    this.idPuntoControl = idPuntoControl;
  }

  public Long getIdPuntoControl() {
    return idPuntoControl;
  }

  public void setIdPuntoGrupo(Long idPuntoGrupo) {
    this.idPuntoGrupo = idPuntoGrupo;
  }

  public Long getIdPuntoGrupo() {
    return idPuntoGrupo;
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
  	return getIdPuntoPaquete();
  }

  @Override
  public void setKey(Long key) {
  	this.idPuntoPaquete = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdPuntoPaquete());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPuntoControl());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPuntoGrupo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idPuntoPaquete", getIdPuntoPaquete());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idPuntoControl", getIdPuntoControl());
		regresar.put("idPuntoGrupo", getIdPuntoGrupo());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdPuntoPaquete(), getIdUsuario(), getIdPuntoControl(), getIdPuntoGrupo(), getRegistro()
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
    regresar.append("idPuntoPaquete~");
    regresar.append(getIdPuntoPaquete());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPuntoPaquete());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPuntosPaquetesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPuntoPaquete()!= null && getIdPuntoPaquete()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPuntosPaquetesDto other = (TcKeetPuntosPaquetesDto) obj;
    if (getIdPuntoPaquete() != other.idPuntoPaquete && (getIdPuntoPaquete() == null || !getIdPuntoPaquete().equals(other.idPuntoPaquete))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPuntoPaquete() != null ? getIdPuntoPaquete().hashCode() : 0);
    return hash;
  }

}


