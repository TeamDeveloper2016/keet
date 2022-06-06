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
@Table(name="tr_sakbe_maquinaria_desarrollo")
public class TrSakbeMaquinariaDesarrolloDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_maquinaria")
  private Long idMaquinaria;
  @Column (name="id_desarrollo")
  private Long idDesarrollo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_maquinaria_desarrollo")
  private Long idMaquinariaDesarrollo;
  @Column (name="registro")
  private LocalDateTime registro;

  public TrSakbeMaquinariaDesarrolloDto() {
    this(new Long(-1L));
  }

  public TrSakbeMaquinariaDesarrolloDto(Long key) {
    this(null, null, null, new Long(-1L));
    setKey(key);
  }

  public TrSakbeMaquinariaDesarrolloDto(Long idMaquinaria, Long idDesarrollo, Long idUsuario, Long idMaquinariaDesarrollo) {
    setIdMaquinaria(idMaquinaria);
    setIdDesarrollo(idDesarrollo);
    setIdUsuario(idUsuario);
    setIdMaquinariaDesarrollo(idMaquinariaDesarrollo);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdMaquinaria(Long idMaquinaria) {
    this.idMaquinaria = idMaquinaria;
  }

  public Long getIdMaquinaria() {
    return idMaquinaria;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdMaquinariaDesarrollo(Long idMaquinariaDesarrollo) {
    this.idMaquinariaDesarrollo = idMaquinariaDesarrollo;
  }

  public Long getIdMaquinariaDesarrollo() {
    return idMaquinariaDesarrollo;
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
  	return getIdMaquinariaDesarrollo();
  }

  @Override
  public void setKey(Long key) {
  	this.idMaquinariaDesarrollo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdMaquinaria());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdMaquinariaDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idMaquinaria", getIdMaquinaria());
		regresar.put("idDesarrollo", getIdDesarrollo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idMaquinariaDesarrollo", getIdMaquinariaDesarrollo());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdMaquinaria(), getIdDesarrollo(), getIdUsuario(), getIdMaquinariaDesarrollo(), getRegistro()
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
    regresar.append("idMaquinariaDesarrollo~");
    regresar.append(getIdMaquinariaDesarrollo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdMaquinariaDesarrollo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TrSakbeMaquinariaDesarrolloDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdMaquinariaDesarrollo()!= null && getIdMaquinariaDesarrollo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TrSakbeMaquinariaDesarrolloDto other = (TrSakbeMaquinariaDesarrolloDto) obj;
    if (getIdMaquinariaDesarrollo() != other.idMaquinariaDesarrollo && (getIdMaquinariaDesarrollo() == null || !getIdMaquinariaDesarrollo().equals(other.idMaquinariaDesarrollo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdMaquinariaDesarrollo() != null ? getIdMaquinariaDesarrollo().hashCode() : 0);
    return hash;
  }

}


