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
@Table(name="tc_keet_errores_estaciones")
public class TcKeetErroresEstacionesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_estacion_estatus")
  private Long idEstacionEstatus;
  @Column (name="costo")
  private Double costo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_raiz")
  private Long idRaiz;
  @Column (name="id_estacion")
  private Long idEstacion;
  @Column (name="alias")
  private String alias;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_error_estacion")
  private Long idErrorEstacion;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetErroresEstacionesDto() {
    this(new Long(-1L));
  }

  public TcKeetErroresEstacionesDto(Long key) {
    this(null, null, null, null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKeetErroresEstacionesDto(Long idEstacionEstatus, Double costo, Long idUsuario, Long idRaiz, Long idEstacion, String alias, Long idErrorEstacion) {
    setIdEstacionEstatus(idEstacionEstatus);
    setCosto(costo);
    setIdUsuario(idUsuario);
    setIdRaiz(idRaiz);
    setIdEstacion(idEstacion);
    setAlias(alias);
    setIdErrorEstacion(idErrorEstacion);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdEstacionEstatus(Long idEstacionEstatus) {
    this.idEstacionEstatus = idEstacionEstatus;
  }

  public Long getIdEstacionEstatus() {
    return idEstacionEstatus;
  }

  public void setCosto(Double costo) {
    this.costo = costo;
  }

  public Double getCosto() {
    return costo;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdRaiz(Long idRaiz) {
    this.idRaiz = idRaiz;
  }

  public Long getIdRaiz() {
    return idRaiz;
  }

  public void setIdEstacion(Long idEstacion) {
    this.idEstacion = idEstacion;
  }

  public Long getIdEstacion() {
    return idEstacion;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getAlias() {
    return alias;
  }

  public void setIdErrorEstacion(Long idErrorEstacion) {
    this.idErrorEstacion = idErrorEstacion;
  }

  public Long getIdErrorEstacion() {
    return idErrorEstacion;
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
  	return getIdErrorEstacion();
  }

  @Override
  public void setKey(Long key) {
  	this.idErrorEstacion = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdEstacionEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCosto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdRaiz());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEstacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAlias());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdErrorEstacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idEstacionEstatus", getIdEstacionEstatus());
		regresar.put("costo", getCosto());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idRaiz", getIdRaiz());
		regresar.put("idEstacion", getIdEstacion());
		regresar.put("alias", getAlias());
		regresar.put("idErrorEstacion", getIdErrorEstacion());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdEstacionEstatus(), getCosto(), getIdUsuario(), getIdRaiz(), getIdEstacion(), getAlias(), getIdErrorEstacion(), getRegistro()
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
    regresar.append("idErrorEstacion~");
    regresar.append(getIdErrorEstacion());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdErrorEstacion());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetErroresEstacionesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdErrorEstacion()!= null && getIdErrorEstacion()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetErroresEstacionesDto other = (TcKeetErroresEstacionesDto) obj;
    if (getIdErrorEstacion() != other.idErrorEstacion && (getIdErrorEstacion() == null || !getIdErrorEstacion().equals(other.idErrorEstacion))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdErrorEstacion() != null ? getIdErrorEstacion().hashCode() : 0);
    return hash;
  }

}


