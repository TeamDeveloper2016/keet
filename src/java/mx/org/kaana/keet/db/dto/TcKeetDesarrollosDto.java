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
@Table(name="tc_keet_desarrollos")
public class TcKeetDesarrollosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="clave")
  private String clave;
  @Column (name="latitud")
  private String latitud;
  @Column (name="longitud")
  private String longitud;
  @Column (name="id_cliente")
  private Long idCliente;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_desarrollo")
  private Long idDesarrollo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_domicilio")
  private Long idDomicilio;
  @Column (name="nombres")
  private String nombres;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetDesarrollosDto() {
    this(new Long(-1L));
  }

  public TcKeetDesarrollosDto(Long key) {
    this(null, null, null, null, null, new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcKeetDesarrollosDto(String descripcion, String clave, String latitud, String longitud, Long idCliente, Long idDesarrollo, Long idUsuario, Long idDomicilio, String nombres) {
    setDescripcion(descripcion);
    setClave(clave);
    setLatitud(latitud);
    setLongitud(longitud);
    setIdCliente(idCliente);
    setIdDesarrollo(idDesarrollo);
    setIdUsuario(idUsuario);
    setIdDomicilio(idDomicilio);
    setNombres(nombres);
    setRegistro(LocalDateTime.now());
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getClave() {
    return clave;
  }

  public void setLatitud(String latitud) {
    this.latitud = latitud;
  }

  public String getLatitud() {
    return latitud;
  }

  public void setLongitud(String longitud) {
    this.longitud = longitud;
  }

  public String getLongitud() {
    return longitud;
  }

  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public Long getIdCliente() {
    return idCliente;
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

  public void setIdDomicilio(Long idDomicilio) {
    this.idDomicilio = idDomicilio;
  }

  public Long getIdDomicilio() {
    return idDomicilio;
  }

  public void setNombres(String nombres) {
    this.nombres = nombres;
  }

  public String getNombres() {
    return nombres;
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
  	return getIdDesarrollo();
  }

  @Override
  public void setKey(Long key) {
  	this.idDesarrollo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLatitud());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLongitud());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCliente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDomicilio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombres());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("clave", getClave());
		regresar.put("latitud", getLatitud());
		regresar.put("longitud", getLongitud());
		regresar.put("idCliente", getIdCliente());
		regresar.put("idDesarrollo", getIdDesarrollo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idDomicilio", getIdDomicilio());
		regresar.put("nombres", getNombres());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getDescripcion(), getClave(), getLatitud(), getLongitud(), getIdCliente(), getIdDesarrollo(), getIdUsuario(), getIdDomicilio(), getNombres(), getRegistro()
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
    regresar.append("idDesarrollo~");
    regresar.append(getIdDesarrollo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdDesarrollo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetDesarrollosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdDesarrollo()!= null && getIdDesarrollo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetDesarrollosDto other = (TcKeetDesarrollosDto) obj;
    if (getIdDesarrollo() != other.idDesarrollo && (getIdDesarrollo() == null || !getIdDesarrollo().equals(other.idDesarrollo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdDesarrollo() != null ? getIdDesarrollo().hashCode() : 0);
    return hash;
  }

}


