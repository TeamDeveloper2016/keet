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
@Table(name="tc_keet_desarrollos_almacenes")
public class TcKeetDesarrollosAlmacenesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_desarrollo_almacen")
  private Long idDesarrolloAlmacen;
  @Column (name="id_desarrollo")
  private Long idDesarrollo;
  @Column (name="id_almacen")
  private Long idAlmacen;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetDesarrollosAlmacenesDto() {
    this(new Long(-1L));
  }

  public TcKeetDesarrollosAlmacenesDto(Long key) {
    this(new Long(-1L), null, null);
    setKey(key);
  }

  public TcKeetDesarrollosAlmacenesDto(Long idDesarrolloAlmacen, Long idDesarrollo, Long idAlmacen) {
    setIdDesarrolloAlmacen(idDesarrolloAlmacen);
    setIdDesarrollo(idDesarrollo);
    setIdAlmacen(idAlmacen);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdDesarrolloAlmacen(Long idDesarrolloAlmacen) {
    this.idDesarrolloAlmacen = idDesarrolloAlmacen;
  }

  public Long getIdDesarrolloAlmacen() {
    return idDesarrolloAlmacen;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setIdAlmacen(Long idAlmacen) {
    this.idAlmacen = idAlmacen;
  }

  public Long getIdAlmacen() {
    return idAlmacen;
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
  	return getIdDesarrolloAlmacen();
  }

  @Override
  public void setKey(Long key) {
  	this.idDesarrolloAlmacen = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdDesarrolloAlmacen());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAlmacen());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idDesarrolloAlmacen", getIdDesarrolloAlmacen());
		regresar.put("idDesarrollo", getIdDesarrollo());
		regresar.put("idAlmacen", getIdAlmacen());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdDesarrolloAlmacen(), getIdDesarrollo(), getIdAlmacen(), getRegistro()
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
    regresar.append("idDesarrolloAlmacen~");
    regresar.append(getIdDesarrolloAlmacen());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdDesarrolloAlmacen());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetDesarrollosAlmacenesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdDesarrolloAlmacen()!= null && getIdDesarrolloAlmacen()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetDesarrollosAlmacenesDto other = (TcKeetDesarrollosAlmacenesDto) obj;
    if (getIdDesarrolloAlmacen() != other.idDesarrolloAlmacen && (getIdDesarrolloAlmacen() == null || !getIdDesarrolloAlmacen().equals(other.idDesarrolloAlmacen))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdDesarrolloAlmacen() != null ? getIdDesarrolloAlmacen().hashCode() : 0);
    return hash;
  }

}


