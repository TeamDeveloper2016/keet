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
@Table(name="tc_keet_tipos_retenciones")
public class TcKeetTiposRetencionesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_tipo_retencion")
  private Long idTipoRetencion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="alias")
  private String alias;
  @Column (name="porcentaje")
  private Double porcentaje;
  @Column (name="limite")
  private Double limite;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetTiposRetencionesDto() {
    this(new Long(-1L));
  }

  public TcKeetTiposRetencionesDto(Long key) {
    this(null, new Long(-1L), null, null, null, null, null);
    setKey(key);
  }

  public TcKeetTiposRetencionesDto(String descripcion, Long idTipoRetencion, Long idUsuario, String alias, Double porcentaje, Double limite, String nombre) {
    setDescripcion(descripcion);
    setIdTipoRetencion(idTipoRetencion);
    setIdUsuario(idUsuario);
    setAlias(alias);
    setPorcentaje(porcentaje);
    setLimite(limite);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdTipoRetencion(Long idTipoRetencion) {
    this.idTipoRetencion = idTipoRetencion;
  }

  public Long getIdTipoRetencion() {
    return idTipoRetencion;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public String getAlias() {
    return alias;
  }

  public void setPorcentaje(Double porcentaje) {
    this.porcentaje = porcentaje;
  }

  public Double getPorcentaje() {
    return porcentaje;
  }

  public void setLimite(Double limite) {
    this.limite = limite;
  }

  public Double getLimite() {
    return limite;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
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
  	return getIdTipoRetencion();
  }

  @Override
  public void setKey(Long key) {
  	this.idTipoRetencion = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoRetencion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAlias());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPorcentaje());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLimite());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("idTipoRetencion", getIdTipoRetencion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("alias", getAlias());
		regresar.put("porcentaje", getPorcentaje());
		regresar.put("limite", getLimite());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdTipoRetencion(), getIdUsuario(), getAlias(), getPorcentaje(), getLimite(), getNombre(), getRegistro()
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
    regresar.append("idTipoRetencion~");
    regresar.append(getIdTipoRetencion());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdTipoRetencion());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetTiposRetencionesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdTipoRetencion()!= null && getIdTipoRetencion()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetTiposRetencionesDto other = (TcKeetTiposRetencionesDto) obj;
    if (getIdTipoRetencion() != other.idTipoRetencion && (getIdTipoRetencion() == null || !getIdTipoRetencion().equals(other.idTipoRetencion))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdTipoRetencion() != null ? getIdTipoRetencion().hashCode() : 0);
    return hash;
  }

}


