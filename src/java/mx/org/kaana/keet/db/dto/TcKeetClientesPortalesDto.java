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
@Table(name="tc_keet_clientes_portales")
public class TcKeetClientesPortalesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="pagina")
  private String pagina;
  @Column (name="id_cliente")
  private Long idCliente;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="cuenta")
  private String cuenta;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="contrasenia")
  private String contrasenia;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_cliente_portal")
  private Long idClientePortal;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetClientesPortalesDto() {
    this(new Long(-1L));
  }

  public TcKeetClientesPortalesDto(Long key) {
    this(null, null, null, null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKeetClientesPortalesDto(String pagina, Long idCliente, Long idUsuario, String cuenta, String observaciones, String contrasenia, Long idClientePortal) {
    setPagina(pagina);
    setIdCliente(idCliente);
    setIdUsuario(idUsuario);
    setCuenta(cuenta);
    setObservaciones(observaciones);
    setContrasenia(contrasenia);
    setIdClientePortal(idClientePortal);
    setRegistro(LocalDateTime.now());
  }
	
  public void setPagina(String pagina) {
    this.pagina = pagina;
  }

  public String getPagina() {
    return pagina;
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

  public void setCuenta(String cuenta) {
    this.cuenta = cuenta;
  }

  public String getCuenta() {
    return cuenta;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setContrasenia(String contrasenia) {
    this.contrasenia = contrasenia;
  }

  public String getContrasenia() {
    return contrasenia;
  }

  public void setIdClientePortal(Long idClientePortal) {
    this.idClientePortal = idClientePortal;
  }

  public Long getIdClientePortal() {
    return idClientePortal;
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
  	return getIdClientePortal();
  }

  @Override
  public void setKey(Long key) {
  	this.idClientePortal = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getPagina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCliente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCuenta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getContrasenia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdClientePortal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("pagina", getPagina());
		regresar.put("idCliente", getIdCliente());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("cuenta", getCuenta());
		regresar.put("observaciones", getObservaciones());
		regresar.put("contrasenia", getContrasenia());
		regresar.put("idClientePortal", getIdClientePortal());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getPagina(), getIdCliente(), getIdUsuario(), getCuenta(), getObservaciones(), getContrasenia(), getIdClientePortal(), getRegistro()
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
    regresar.append("idClientePortal~");
    regresar.append(getIdClientePortal());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdClientePortal());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetClientesPortalesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdClientePortal()!= null && getIdClientePortal()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetClientesPortalesDto other = (TcKeetClientesPortalesDto) obj;
    if (getIdClientePortal() != other.idClientePortal && (getIdClientePortal() == null || !getIdClientePortal().equals(other.idClientePortal))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdClientePortal() != null ? getIdClientePortal().hashCode() : 0);
    return hash;
  }

}


