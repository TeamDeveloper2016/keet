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
@Table(name="tc_keet_clientes_infraestructuras")
public class TcKeetClientesInfraestructurasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_cliente")
  private Long idCliente;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_cliente_infraestructura")
  private Long idClienteInfraestructura;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_tipo_infraestructura")
  private Long idTipoInfraestructura;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetClientesInfraestructurasDto() {
    this(new Long(-1L));
  }

  public TcKeetClientesInfraestructurasDto(Long key) {
    this(null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcKeetClientesInfraestructurasDto(Long idCliente, Long idClienteInfraestructura, Long idUsuario, Long idTipoInfraestructura) {
    setIdCliente(idCliente);
    setIdClienteInfraestructura(idClienteInfraestructura);
    setIdUsuario(idUsuario);
    setIdTipoInfraestructura(idTipoInfraestructura);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public Long getIdCliente() {
    return idCliente;
  }

  public void setIdClienteInfraestructura(Long idClienteInfraestructura) {
    this.idClienteInfraestructura = idClienteInfraestructura;
  }

  public Long getIdClienteInfraestructura() {
    return idClienteInfraestructura;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdTipoInfraestructura(Long idTipoInfraestructura) {
    this.idTipoInfraestructura = idTipoInfraestructura;
  }

  public Long getIdTipoInfraestructura() {
    return idTipoInfraestructura;
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
  	return getIdClienteInfraestructura();
  }

  @Override
  public void setKey(Long key) {
  	this.idClienteInfraestructura = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdCliente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdClienteInfraestructura());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoInfraestructura());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idCliente", getIdCliente());
		regresar.put("idClienteInfraestructura", getIdClienteInfraestructura());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idTipoInfraestructura", getIdTipoInfraestructura());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdCliente(), getIdClienteInfraestructura(), getIdUsuario(), getIdTipoInfraestructura(), getRegistro()
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
    regresar.append("idClienteInfraestructura~");
    regresar.append(getIdClienteInfraestructura());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdClienteInfraestructura());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetClientesInfraestructurasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdClienteInfraestructura()!= null && getIdClienteInfraestructura()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetClientesInfraestructurasDto other = (TcKeetClientesInfraestructurasDto) obj;
    if (getIdClienteInfraestructura() != other.idClienteInfraestructura && (getIdClienteInfraestructura() == null || !getIdClienteInfraestructura().equals(other.idClienteInfraestructura))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdClienteInfraestructura() != null ? getIdClienteInfraestructura().hashCode() : 0);
    return hash;
  }
}