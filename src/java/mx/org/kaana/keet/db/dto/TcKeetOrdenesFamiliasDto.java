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
@Table(name="tc_keet_ordenes_familias")
public class TcKeetOrdenesFamiliasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_familia")
  private Long idFamilia;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_orden_familia")
  private Long idOrdenFamilia;
  @Column (name="id_orden_compra")
  private Long idOrdenCompra;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetOrdenesFamiliasDto() {
    this(new Long(-1L));
  }

  public TcKeetOrdenesFamiliasDto(Long key) {
    this(null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKeetOrdenesFamiliasDto(Long idFamilia, Long idUsuario, Long idOrdenFamilia, Long idOrdenCompra) {
    setIdFamilia(idFamilia);
    setIdUsuario(idUsuario);
    setIdOrdenFamilia(idOrdenFamilia);
    setIdOrdenCompra(idOrdenCompra);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdFamilia(Long idFamilia) {
    this.idFamilia = idFamilia;
  }

  public Long getIdFamilia() {
    return idFamilia;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdOrdenFamilia(Long idOrdenFamilia) {
    this.idOrdenFamilia = idOrdenFamilia;
  }

  public Long getIdOrdenFamilia() {
    return idOrdenFamilia;
  }

  public void setIdOrdenCompra(Long idOrdenCompra) {
    this.idOrdenCompra = idOrdenCompra;
  }

  public Long getIdOrdenCompra() {
    return idOrdenCompra;
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
  	return getIdOrdenFamilia();
  }

  @Override
  public void setKey(Long key) {
  	this.idOrdenFamilia = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdFamilia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdOrdenFamilia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdOrdenCompra());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idFamilia", getIdFamilia());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idOrdenFamilia", getIdOrdenFamilia());
		regresar.put("idOrdenCompra", getIdOrdenCompra());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdFamilia(), getIdUsuario(), getIdOrdenFamilia(), getIdOrdenCompra(), getRegistro()
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
    regresar.append("idOrdenFamilia~");
    regresar.append(getIdOrdenFamilia());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdOrdenFamilia());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetOrdenesFamiliasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdOrdenFamilia()!= null && getIdOrdenFamilia()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetOrdenesFamiliasDto other = (TcKeetOrdenesFamiliasDto) obj;
    if (getIdOrdenFamilia() != other.idOrdenFamilia && (getIdOrdenFamilia() == null || !getIdOrdenFamilia().equals(other.idOrdenFamilia))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdOrdenFamilia() != null ? getIdOrdenFamilia().hashCode() : 0);
    return hash;
  }

}


