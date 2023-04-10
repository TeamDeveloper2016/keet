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
@Table(name="tc_keet_ordenes_codigos")
public class TcKeetOrdenesCodigosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="utilizado")
  private LocalDateTime utilizado;
  @Column (name="codigo")
  private String codigo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_orden_compra")
  private Long idOrdenCompra;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_orden_codigo")
  private Long idOrdenCodigo;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetOrdenesCodigosDto() {
    this(new Long(-1L));
  }

  public TcKeetOrdenesCodigosDto(Long key) {
    this(LocalDateTime.now(), null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKeetOrdenesCodigosDto(LocalDateTime utilizado, String codigo, Long idUsuario, Long idOrdenCompra, Long idOrdenCodigo) {
    setUtilizado(utilizado);
    setCodigo(codigo);
    setIdUsuario(idUsuario);
    setIdOrdenCompra(idOrdenCompra);
    setIdOrdenCodigo(idOrdenCodigo);
    setRegistro(LocalDateTime.now());
  }
	
  public void setUtilizado(LocalDateTime utilizado) {
    this.utilizado = utilizado;
  }

  public LocalDateTime getUtilizado() {
    return utilizado;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdOrdenCompra(Long idOrdenCompra) {
    this.idOrdenCompra = idOrdenCompra;
  }

  public Long getIdOrdenCompra() {
    return idOrdenCompra;
  }

  public void setIdOrdenCodigo(Long idOrdenCodigo) {
    this.idOrdenCodigo = idOrdenCodigo;
  }

  public Long getIdOrdenCodigo() {
    return idOrdenCodigo;
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
  	return getIdOrdenCodigo();
  }

  @Override
  public void setKey(Long key) {
  	this.idOrdenCodigo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getUtilizado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdOrdenCompra());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdOrdenCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("utilizado", getUtilizado());
		regresar.put("codigo", getCodigo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idOrdenCompra", getIdOrdenCompra());
		regresar.put("idOrdenCodigo", getIdOrdenCodigo());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getUtilizado(), getCodigo(), getIdUsuario(), getIdOrdenCompra(), getIdOrdenCodigo(), getRegistro()
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
    regresar.append("idOrdenCodigo~");
    regresar.append(getIdOrdenCodigo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdOrdenCodigo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetOrdenesCodigosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdOrdenCodigo()!= null && getIdOrdenCodigo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetOrdenesCodigosDto other = (TcKeetOrdenesCodigosDto) obj;
    if (getIdOrdenCodigo() != other.idOrdenCodigo && (getIdOrdenCodigo() == null || !getIdOrdenCodigo().equals(other.idOrdenCodigo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdOrdenCodigo() != null ? getIdOrdenCodigo().hashCode() : 0);
    return hash;
  }

}


