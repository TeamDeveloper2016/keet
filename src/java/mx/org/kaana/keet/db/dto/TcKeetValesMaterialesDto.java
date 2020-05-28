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
@Table(name="tc_keet_vales_materiales")
public class TcKeetValesMaterialesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_vale_material")
  private Long idValeMaterial;
  @Column (name="id_vale")
  private Long idVale;
  @Column (name="id_material")
  private Long idMaterial;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetValesMaterialesDto() {
    this(new Long(-1L));
  }

  public TcKeetValesMaterialesDto(Long key) {
    this(new Long(-1L), null, null);
    setKey(key);
  }

  public TcKeetValesMaterialesDto(Long idValeMaterial, Long idVale, Long idMaterial) {
    setIdValeMaterial(idValeMaterial);
    setIdVale(idVale);
    setIdMaterial(idMaterial);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdValeMaterial(Long idValeMaterial) {
    this.idValeMaterial = idValeMaterial;
  }

  public Long getIdValeMaterial() {
    return idValeMaterial;
  }

  public void setIdVale(Long idVale) {
    this.idVale = idVale;
  }

  public Long getIdVale() {
    return idVale;
  }

  public void setIdMaterial(Long idMaterial) {
    this.idMaterial = idMaterial;
  }

  public Long getIdMaterial() {
    return idMaterial;
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
  	return getIdValeMaterial();
  }

  @Override
  public void setKey(Long key) {
  	this.idValeMaterial = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdValeMaterial());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdVale());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdMaterial());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idValeMaterial", getIdValeMaterial());
		regresar.put("idVale", getIdVale());
		regresar.put("idMaterial", getIdMaterial());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdValeMaterial(), getIdVale(), getIdMaterial(), getRegistro()
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
    regresar.append("idValeMaterial~");
    regresar.append(getIdValeMaterial());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdValeMaterial());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetValesMaterialesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdValeMaterial()!= null && getIdValeMaterial()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetValesMaterialesDto other = (TcKeetValesMaterialesDto) obj;
    if (getIdValeMaterial() != other.idValeMaterial && (getIdValeMaterial() == null || !getIdValeMaterial().equals(other.idValeMaterial))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdValeMaterial() != null ? getIdValeMaterial().hashCode() : 0);
    return hash;
  }
}