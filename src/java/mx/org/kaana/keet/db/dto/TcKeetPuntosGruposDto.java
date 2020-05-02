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
@Table(name="tc_keet_puntos_grupos")
public class TcKeetPuntosGruposDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="id_departamento")
  private Long idDepartamento;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_punto_grupo")
  private Long idPuntoGrupo;
  @Column (name="grupo")
  private Long grupo;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetPuntosGruposDto() {
    this(new Long(-1L));
  }

  public TcKeetPuntosGruposDto(Long key) {
    this(null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKeetPuntosGruposDto(String descripcion, Long idDepartamento, Long idUsuario, Long idPuntoGrupo, Long grupo) {
    setDescripcion(descripcion);
    setIdDepartamento(idDepartamento);
    setIdUsuario(idUsuario);
    setIdPuntoGrupo(idPuntoGrupo);
    setGrupo(grupo);
    setRegistro(LocalDateTime.now());
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdDepartamento(Long idDepartamento) {
    this.idDepartamento = idDepartamento;
  }

  public Long getIdDepartamento() {
    return idDepartamento;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdPuntoGrupo(Long idPuntoGrupo) {
    this.idPuntoGrupo = idPuntoGrupo;
  }

  public Long getIdPuntoGrupo() {
    return idPuntoGrupo;
  }

  public void setGrupo(Long grupo) {
    this.grupo = grupo;
  }

  public Long getGrupo() {
    return grupo;
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
  	return getIdPuntoGrupo();
  }

  @Override
  public void setKey(Long key) {
  	this.idPuntoGrupo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDepartamento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPuntoGrupo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getGrupo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("idDepartamento", getIdDepartamento());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idPuntoGrupo", getIdPuntoGrupo());
		regresar.put("grupo", getGrupo());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdDepartamento(), getIdUsuario(), getIdPuntoGrupo(), getGrupo(), getRegistro()
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
    regresar.append("idPuntoGrupo~");
    regresar.append(getIdPuntoGrupo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPuntoGrupo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPuntosGruposDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPuntoGrupo()!= null && getIdPuntoGrupo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPuntosGruposDto other = (TcKeetPuntosGruposDto) obj;
    if (getIdPuntoGrupo() != other.idPuntoGrupo && (getIdPuntoGrupo() == null || !getIdPuntoGrupo().equals(other.idPuntoGrupo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPuntoGrupo() != null ? getIdPuntoGrupo().hashCode() : 0);
    return hash;
  }

}


