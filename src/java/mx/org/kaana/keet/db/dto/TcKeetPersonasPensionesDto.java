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
@Table(name="tc_keet_personas_pensiones")
public class TcKeetPersonasPensionesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="materno")
  private String materno;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_persona_pension")
  private Long idPersonaPension;
  @Column (name="paterno")
  private String paterno;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_empresa_persona")
  private Long idEmpresaPersona;
  @Column (name="porcentaje")
  private Double porcentaje;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetPersonasPensionesDto() {
    this(new Long(-1L));
  }

  public TcKeetPersonasPensionesDto(Long key) {
    this(null, new Long(-1L), null, null, null, null, null);
    setKey(key);
  }

  public TcKeetPersonasPensionesDto(String materno, Long idPersonaPension, String paterno, Long idUsuario, Long idEmpresaPersona, Double porcentaje, String nombre) {
    setMaterno(materno);
    setIdPersonaPension(idPersonaPension);
    setPaterno(paterno);
    setIdUsuario(idUsuario);
    setIdEmpresaPersona(idEmpresaPersona);
    setPorcentaje(porcentaje);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
  }
	
  public void setMaterno(String materno) {
    this.materno = materno;
  }

  public String getMaterno() {
    return materno;
  }

  public void setIdPersonaPension(Long idPersonaPension) {
    this.idPersonaPension = idPersonaPension;
  }

  public Long getIdPersonaPension() {
    return idPersonaPension;
  }

  public void setPaterno(String paterno) {
    this.paterno = paterno;
  }

  public String getPaterno() {
    return paterno;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdEmpresaPersona(Long idEmpresaPersona) {
    this.idEmpresaPersona = idEmpresaPersona;
  }

  public Long getIdEmpresaPersona() {
    return idEmpresaPersona;
  }

  public void setPorcentaje(Double porcentaje) {
    this.porcentaje = porcentaje;
  }

  public Double getPorcentaje() {
    return porcentaje;
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
  	return getIdPersonaPension();
  }

  @Override
  public void setKey(Long key) {
  	this.idPersonaPension = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getMaterno());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPersonaPension());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPaterno());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPorcentaje());
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
		regresar.put("materno", getMaterno());
		regresar.put("idPersonaPension", getIdPersonaPension());
		regresar.put("paterno", getPaterno());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idEmpresaPersona", getIdEmpresaPersona());
		regresar.put("porcentaje", getPorcentaje());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getMaterno(), getIdPersonaPension(), getPaterno(), getIdUsuario(), getIdEmpresaPersona(), getPorcentaje(), getNombre(), getRegistro()
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
    regresar.append("idPersonaPension~");
    regresar.append(getIdPersonaPension());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPersonaPension());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPersonasPensionesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPersonaPension()!= null && getIdPersonaPension()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPersonasPensionesDto other = (TcKeetPersonasPensionesDto) obj;
    if (getIdPersonaPension() != other.idPersonaPension && (getIdPersonaPension() == null || !getIdPersonaPension().equals(other.idPersonaPension))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPersonaPension() != null ? getIdPersonaPension().hashCode() : 0);
    return hash;
  }

}


