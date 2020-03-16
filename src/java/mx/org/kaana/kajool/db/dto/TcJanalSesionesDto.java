package mx.org.kaana.kajool.db.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;

@Entity
@Table(name="tc_janal_sesiones")
public class TcJanalSesionesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="termino")
  private LocalDateTime termino;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
  //@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="idSesion_sequence")
  //@SequenceGenerator(name="idSesion_sequence",sequenceName="SEQ_TR_JANAL_SESIONES" , allocationSize=1 )
  @Column (name="id_sesion")
  private Long idSesion;
  @Column (name="cuenta")
  private String cuenta;
  @Column (name="path")
  private String path;
  @Column (name="inicio")
  private LocalDateTime inicio;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="sesion")
  private String sesion;

  public TcJanalSesionesDto() {
    this(new Long(-1L));
  }

  public TcJanalSesionesDto(Long key) {
    this(LocalDateTime.now(), null, new Long(-1L), null, null, LocalDateTime.now(), null);
    setKey(key);
  }

  public TcJanalSesionesDto(LocalDateTime termino, Long idUsuario, Long idSesion, String cuenta, String path, LocalDateTime inicio, String sesion) {
    setTermino(termino);
    setIdUsuario(idUsuario);
    setIdSesion(idSesion);
    setCuenta(cuenta);
    setPath(path);
    setInicio(inicio);
    setRegistro(LocalDateTime.now());
    setSesion(sesion);
  }

  public void setTermino(LocalDateTime termino) {
    this.termino = termino;
  }

  public LocalDateTime getTermino() {
    return termino;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdSesion(Long idSesion) {
    this.idSesion = idSesion;
  }

  public Long getIdSesion() {
    return idSesion;
  }

  public void setCuenta(String cuenta) {
    this.cuenta = cuenta;
  }

  public String getCuenta() {
    return cuenta;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }

  public void setInicio(LocalDateTime inicio) {
    this.inicio = inicio;
  }

  public LocalDateTime getInicio() {
    return inicio;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  public void setSesion(String sesion) {
    this.sesion = sesion;
  }

  public String getSesion() {
    return sesion;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdSesion();
  }

  @Override
  public void setKey(Long key) {
  	this.idSesion = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getTermino());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdSesion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCuenta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPath());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getInicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSesion());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("registroFin", getTermino());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idSesion", getIdSesion());
		regresar.put("cuenta", getCuenta());
		regresar.put("path", getPath());
		regresar.put("inicio", getInicio());
		regresar.put("registro", getRegistro());
		regresar.put("sesion", getSesion());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getTermino(), getIdUsuario(), getIdSesion(), getCuenta(), getPath(), getInicio(), getRegistro(), getSesion()
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
    regresar.append("idSesion~");
    regresar.append(getIdSesion());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdSesion());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcJanalSesionesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdSesion()!= null && getIdSesion()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcJanalSesionesDto other = (TcJanalSesionesDto) obj;
    if (getIdSesion() != other.idSesion && (getIdSesion() == null || !getIdSesion().equals(other.idSesion))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdSesion() != null ? getIdSesion().hashCode() : 0);
    return hash;
  }

}


