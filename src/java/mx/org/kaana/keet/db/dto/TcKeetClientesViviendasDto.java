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
@Table(name="tc_keet_clientes_viviendas")
public class TcKeetClientesViviendasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_cliente")
  private Long idCliente;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_tipo_vivienda")
  private Long idTipoVivienda;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_cliente_vivienda")
  private Long idClienteVivienda;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetClientesViviendasDto() {
    this(new Long(-1L));
  }

  public TcKeetClientesViviendasDto(Long key) {
    this(null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKeetClientesViviendasDto(Long idCliente, Long idUsuario, Long idTipoVivienda, Long idClienteVivienda) {
    setIdCliente(idCliente);
    setIdUsuario(idUsuario);
    setIdTipoVivienda(idTipoVivienda);
    setIdClienteVivienda(idClienteVivienda);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public Long getIdCliente() {
    return idCliente;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdTipoVivienda(Long idTipoVivienda) {
    this.idTipoVivienda = idTipoVivienda;
  }

  public Long getIdTipoVivienda() {
    return idTipoVivienda;
  }

  public void setIdClienteVivienda(Long idClienteVivienda) {
    this.idClienteVivienda = idClienteVivienda;
  }

  public Long getIdClienteVivienda() {
    return idClienteVivienda;
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
  	return getIdClienteVivienda();
  }

  @Override
  public void setKey(Long key) {
  	this.idClienteVivienda = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdCliente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoVivienda());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdClienteVivienda());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idCliente", getIdCliente());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idTipoVivienda", getIdTipoVivienda());
		regresar.put("idClienteVivienda", getIdClienteVivienda());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdCliente(), getIdUsuario(), getIdTipoVivienda(), getIdClienteVivienda(), getRegistro()
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
    regresar.append("idClienteVivienda~");
    regresar.append(getIdClienteVivienda());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdClienteVivienda());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetClientesViviendasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdClienteVivienda()!= null && getIdClienteVivienda()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetClientesViviendasDto other = (TcKeetClientesViviendasDto) obj;
    if (getIdClienteVivienda() != other.idClienteVivienda && (getIdClienteVivienda() == null || !getIdClienteVivienda().equals(other.idClienteVivienda))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdClienteVivienda() != null ? getIdClienteVivienda().hashCode() : 0);
    return hash;
  }
}