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
@Table(name="tc_keet_contratos_retenciones")
public class TcKeetContratosRetencionesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_tipo_retencion")
  private Long idTipoRetencion;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_retencion")
  private Long idContratoRetencion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_contrato")
  private Long idContrato;
  @Column (name="porcentaje")
  private Double porcentaje;
  @Column (name="limite")
  private Double limite;
  @Column (name="nombre")
  private String nombre;
  @Column (name="importe")
  private Double importe;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetContratosRetencionesDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosRetencionesDto(Long key) {
    this(null, new Long(-1L), null, null, null, null, null, null);
    setKey(key);
  }

  public TcKeetContratosRetencionesDto(Long idTipoRetencion, Long idContratoRetencion, Long idUsuario, Long idContrato, Double porcentaje, Double limite, String nombre, Double importe) {
    setIdTipoRetencion(idTipoRetencion);
    setIdContratoRetencion(idContratoRetencion);
    setIdUsuario(idUsuario);
    setIdContrato(idContrato);
    setPorcentaje(porcentaje);
    setLimite(limite);
    setNombre(nombre);
    setImporte(importe);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdTipoRetencion(Long idTipoRetencion) {
    this.idTipoRetencion = idTipoRetencion;
  }

  public Long getIdTipoRetencion() {
    return idTipoRetencion;
  }

  public void setIdContratoRetencion(Long idContratoRetencion) {
    this.idContratoRetencion = idContratoRetencion;
  }

  public Long getIdContratoRetencion() {
    return idContratoRetencion;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdContrato(Long idContrato) {
    this.idContrato = idContrato;
  }

  public Long getIdContrato() {
    return idContrato;
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

  public void setImporte(Double importe) {
    this.importe = importe;
  }

  public Double getImporte() {
    return importe;
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
  	return getIdContratoRetencion();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoRetencion = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdTipoRetencion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoRetencion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPorcentaje());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLimite());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idTipoRetencion", getIdTipoRetencion());
		regresar.put("idContratoRetencion", getIdContratoRetencion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idContrato", getIdContrato());
		regresar.put("porcentaje", getPorcentaje());
		regresar.put("limite", getLimite());
		regresar.put("nombre", getNombre());
		regresar.put("importe", getImporte());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdTipoRetencion(), getIdContratoRetencion(), getIdUsuario(), getIdContrato(), getPorcentaje(), getLimite(), getNombre(), getImporte(), getRegistro()
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
    regresar.append("idContratoRetencion~");
    regresar.append(getIdContratoRetencion());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoRetencion());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosRetencionesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoRetencion()!= null && getIdContratoRetencion()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosRetencionesDto other = (TcKeetContratosRetencionesDto) obj;
    if (getIdContratoRetencion() != other.idContratoRetencion && (getIdContratoRetencion() == null || !getIdContratoRetencion().equals(other.idContratoRetencion))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoRetencion() != null ? getIdContratoRetencion().hashCode() : 0);
    return hash;
  }

}


