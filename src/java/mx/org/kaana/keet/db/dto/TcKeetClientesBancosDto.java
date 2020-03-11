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
@Table(name="tc_keet_clientes_bancos")
public class TcKeetClientesBancosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_cliente_banco")
  private Long idClienteBanco;
  @Column (name="id_cliente")
  private Long idCliente;
  @Column (name="clave_referencia")
  private String claveReferencia;
  @Column (name="convenio_cuenta")
  private String convenioCuenta;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_tipo_cuenta")
  private Long idTipoCuenta;
  @Column (name="id_banco")
  private Long idBanco;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetClientesBancosDto() {
    this(new Long(-1L));
  }

  public TcKeetClientesBancosDto(Long key) {
    this(new Long(-1L), null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKeetClientesBancosDto(Long idClienteBanco, Long idCliente, String claveReferencia, String convenioCuenta, Long idUsuario, String observaciones, Long idTipoCuenta, Long idBanco) {
    setIdClienteBanco(idClienteBanco);
    setIdCliente(idCliente);
    setClaveReferencia(claveReferencia);
    setConvenioCuenta(convenioCuenta);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setIdTipoCuenta(idTipoCuenta);
    setIdBanco(idBanco);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdClienteBanco(Long idClienteBanco) {
    this.idClienteBanco = idClienteBanco;
  }

  public Long getIdClienteBanco() {
    return idClienteBanco;
  }

  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public Long getIdCliente() {
    return idCliente;
  }

  public void setClaveReferencia(String claveReferencia) {
    this.claveReferencia = claveReferencia;
  }

  public String getClaveReferencia() {
    return claveReferencia;
  }

  public void setConvenioCuenta(String convenioCuenta) {
    this.convenioCuenta = convenioCuenta;
  }

  public String getConvenioCuenta() {
    return convenioCuenta;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdTipoCuenta(Long idTipoCuenta) {
    this.idTipoCuenta = idTipoCuenta;
  }

  public Long getIdTipoCuenta() {
    return idTipoCuenta;
  }

  public void setIdBanco(Long idBanco) {
    this.idBanco = idBanco;
  }

  public Long getIdBanco() {
    return idBanco;
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
  	return getIdClienteBanco();
  }

  @Override
  public void setKey(Long key) {
  	this.idClienteBanco = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdClienteBanco());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCliente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClaveReferencia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConvenioCuenta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoCuenta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdBanco());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idClienteBanco", getIdClienteBanco());
		regresar.put("idCliente", getIdCliente());
		regresar.put("claveReferencia", getClaveReferencia());
		regresar.put("convenioCuenta", getConvenioCuenta());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idTipoCuenta", getIdTipoCuenta());
		regresar.put("idBanco", getIdBanco());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdClienteBanco(), getIdCliente(), getClaveReferencia(), getConvenioCuenta(), getIdUsuario(), getObservaciones(), getIdTipoCuenta(), getIdBanco(), getRegistro()
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
    regresar.append("idClienteBanco~");
    regresar.append(getIdClienteBanco());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdClienteBanco());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetClientesBancosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdClienteBanco()!= null && getIdClienteBanco()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetClientesBancosDto other = (TcKeetClientesBancosDto) obj;
    if (getIdClienteBanco() != other.idClienteBanco && (getIdClienteBanco() == null || !getIdClienteBanco().equals(other.idClienteBanco))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdClienteBanco() != null ? getIdClienteBanco().hashCode() : 0);
    return hash;
  }

}


