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
@Table(name="tr_sakbe_maquinaria_grupo")
public class TrSakbeMaquinariaGrupoDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_maquinaria")
  private Long idMaquinaria;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_maquinaria_grupo")
  private Long idMaquinariaGrupo;
  @Column (name="id_tipo_grupo")
  private Long idTipoGrupo;
  @Column (name="registro")
  private LocalDateTime registro;

  public TrSakbeMaquinariaGrupoDto() {
    this(new Long(-1L));
  }

  public TrSakbeMaquinariaGrupoDto(Long key) {
    this(null, null, new Long(-1L), null);
    setKey(key);
  }

  public TrSakbeMaquinariaGrupoDto(Long idMaquinaria, Long idUsuario, Long idMaquinariaGrupo, Long idTipoGrupo) {
    setIdMaquinaria(idMaquinaria);
    setIdUsuario(idUsuario);
    setIdMaquinariaGrupo(idMaquinariaGrupo);
    setIdTipoGrupo(idTipoGrupo);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdMaquinaria(Long idMaquinaria) {
    this.idMaquinaria = idMaquinaria;
  }

  public Long getIdMaquinaria() {
    return idMaquinaria;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdMaquinariaGrupo(Long idMaquinariaGrupo) {
    this.idMaquinariaGrupo = idMaquinariaGrupo;
  }

  public Long getIdMaquinariaGrupo() {
    return idMaquinariaGrupo;
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
  	return getIdMaquinariaGrupo();
  }

  @Override
  public void setKey(Long key) {
  	this.idMaquinariaGrupo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdMaquinaria());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdMaquinariaGrupo());
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
		regresar.put("idMaquinaria", getIdMaquinaria());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idMaquinariaGrupo", getIdMaquinariaGrupo());
		regresar.put("idTipoGrupo", getIdTipoGrupo());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdMaquinaria(), getIdUsuario(), getIdMaquinariaGrupo(), getIdTipoGrupo(), getRegistro()
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
    regresar.append("idMaquinariaGrupo~");
    regresar.append(getIdMaquinariaGrupo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdMaquinariaGrupo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TrSakbeMaquinariaGrupoDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdMaquinariaGrupo()!= null && getIdMaquinariaGrupo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TrSakbeMaquinariaGrupoDto other = (TrSakbeMaquinariaGrupoDto) obj;
    if (getIdMaquinariaGrupo() != other.idMaquinariaGrupo && (getIdMaquinariaGrupo() == null || !getIdMaquinariaGrupo().equals(other.idMaquinariaGrupo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdMaquinariaGrupo() != null ? getIdMaquinariaGrupo().hashCode() : 0);
    return hash;
  }

}


