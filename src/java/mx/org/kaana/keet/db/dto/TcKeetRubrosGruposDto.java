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
@Table(name="tc_keet_rubros_grupos")
public class TcKeetRubrosGruposDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_rubro_grupo")
  private Long idRubroGrupo;
  @Column (name="id_punto_grupo")
  private Long idPuntoGrupo;
  @Column (name="id_rubro")
  private Long idRubro;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetRubrosGruposDto() {
    this(new Long(-1L));
  }

  public TcKeetRubrosGruposDto(Long key) {
    this(null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcKeetRubrosGruposDto(Long idUsuario, Long idRubroGrupo, Long idPuntoGrupo, Long idRubro) {
    setIdUsuario(idUsuario);
    setIdRubroGrupo(idRubroGrupo);
    setIdPuntoGrupo(idPuntoGrupo);
    setIdRubro(idRubro);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdRubroGrupo(Long idRubroGrupo) {
    this.idRubroGrupo = idRubroGrupo;
  }

  public Long getIdRubroGrupo() {
    return idRubroGrupo;
  }

  public void setIdPuntoGrupo(Long idPuntoGrupo) {
    this.idPuntoGrupo = idPuntoGrupo;
  }

  public Long getIdPuntoGrupo() {
    return idPuntoGrupo;
  }

  public void setIdRubro(Long idRubro) {
    this.idRubro = idRubro;
  }

  public Long getIdRubro() {
    return idRubro;
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
  	return getIdRubroGrupo();
  }

  @Override
  public void setKey(Long key) {
  	this.idRubroGrupo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdRubroGrupo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPuntoGrupo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdRubro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idRubroGrupo", getIdRubroGrupo());
		regresar.put("idPuntoGrupo", getIdPuntoGrupo());
		regresar.put("idRubro", getIdRubro());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdUsuario(), getIdRubroGrupo(), getIdPuntoGrupo(), getIdRubro(), getRegistro()
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
    regresar.append("idRubroGrupo~");
    regresar.append(getIdRubroGrupo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdRubroGrupo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetRubrosGruposDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdRubroGrupo()!= null && getIdRubroGrupo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetRubrosGruposDto other = (TcKeetRubrosGruposDto) obj;
    if (getIdRubroGrupo() != other.idRubroGrupo && (getIdRubroGrupo() == null || !getIdRubroGrupo().equals(other.idRubroGrupo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdRubroGrupo() != null ? getIdRubroGrupo().hashCode() : 0);
    return hash;
  }

}


