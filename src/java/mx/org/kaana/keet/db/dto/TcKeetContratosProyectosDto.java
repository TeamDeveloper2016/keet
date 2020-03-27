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
@Table(name="tc_keet_contratos_proyectos")
public class TcKeetContratosProyectosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="id_cliente_contrato")
  private Long idClienteContrato;
  @Column (name="clave")
  private String clave;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_proyecto")
  private Long idContratoProyecto;
  @Column (name="valor")
  private Double valor;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetContratosProyectosDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosProyectosDto(Long key) {
    this(null, null, null, null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcKeetContratosProyectosDto(String descripcion, Long idClienteContrato, String clave, Long idUsuario, Long idContratoProyecto, Double valor, String nombre) {
    setDescripcion(descripcion);
    setIdClienteContrato(idClienteContrato);
    setClave(clave);
    setIdUsuario(idUsuario);
    setIdContratoProyecto(idContratoProyecto);
    setValor(valor);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdClienteContrato(Long idClienteContrato) {
    this.idClienteContrato = idClienteContrato;
  }

  public Long getIdClienteContrato() {
    return idClienteContrato;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getClave() {
    return clave;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdContratoProyecto(Long idContratoProyecto) {
    this.idContratoProyecto = idContratoProyecto;
  }

  public Long getIdContratoProyecto() {
    return idContratoProyecto;
  }

  public void setValor(Double valor) {
    this.valor = valor;
  }

  public Double getValor() {
    return valor;
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
  	return getIdContratoProyecto();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoProyecto = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdClienteContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoProyecto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getValor());
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
		regresar.put("idClienteContrato", getIdClienteContrato());
		regresar.put("clave", getClave());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idContratoProyecto", getIdContratoProyecto());
		regresar.put("valor", getValor());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdClienteContrato(), getClave(), getIdUsuario(), getIdContratoProyecto(), getValor(), getNombre(), getRegistro()
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
    regresar.append("idContratoProyecto~");
    regresar.append(getIdContratoProyecto());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoProyecto());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosProyectosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoProyecto()!= null && getIdContratoProyecto()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosProyectosDto other = (TcKeetContratosProyectosDto) obj;
    if (getIdContratoProyecto() != other.idContratoProyecto && (getIdContratoProyecto() == null || !getIdContratoProyecto().equals(other.idContratoProyecto))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoProyecto() != null ? getIdContratoProyecto().hashCode() : 0);
    return hash;
  }

}


