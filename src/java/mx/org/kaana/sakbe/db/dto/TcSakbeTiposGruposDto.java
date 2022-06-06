package mx.org.kaana.sakbe.db.dto;

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
@Table(name="tc_sakbe_tipos_grupos")
public class TcSakbeTiposGruposDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_tipo_grupo")
  private Long idTipoGrupo;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcSakbeTiposGruposDto() {
    this(new Long(-1L));
  }

  public TcSakbeTiposGruposDto(Long key) {
    this(null, null, new Long(-1L));
    setKey(key);
  }

  public TcSakbeTiposGruposDto(String descripcion, Long idUsuario, Long idTipoGrupo) {
    setDescripcion(descripcion);
    setIdUsuario(idUsuario);
    setIdTipoGrupo(idTipoGrupo);
    setRegistro(LocalDateTime.now());
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdTipoGrupo(Long idTipoGrupo) {
    this.idTipoGrupo = idTipoGrupo;
  }

  public Long getIdTipoGrupo() {
    return idTipoGrupo;
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
  	return getIdTipoGrupo();
  }

  @Override
  public void setKey(Long key) {
  	this.idTipoGrupo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoGrupo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idTipoGrupo", getIdTipoGrupo());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdUsuario(), getIdTipoGrupo(), getRegistro()
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
    regresar.append("idTipoGrupo~");
    regresar.append(getIdTipoGrupo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdTipoGrupo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeTiposGruposDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdTipoGrupo()!= null && getIdTipoGrupo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcSakbeTiposGruposDto other = (TcSakbeTiposGruposDto) obj;
    if (getIdTipoGrupo() != other.idTipoGrupo && (getIdTipoGrupo() == null || !getIdTipoGrupo().equals(other.idTipoGrupo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdTipoGrupo() != null ? getIdTipoGrupo().hashCode() : 0);
    return hash;
  }

}


