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
@Table(name="tc_keet_contratos_rechazos_residentes")
public class TcKeetContratosRechazosResidentesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_rechazo_residente")
  private Long idContratoRechazoResidente;
  @Column (name="latitud")
  private String latitud;
  @Column (name="longitud")
  private String longitud;
  @Column (name="id_punto_paquete")
  private Long idPuntoPaquete;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_contrato_destajo_residente")
  private Long idContratoDestajoResidente;
  @Column (name="distancia")
  private Double distancia;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetContratosRechazosResidentesDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosRechazosResidentesDto(Long key) {
    this(new Long(-1L), null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKeetContratosRechazosResidentesDto(Long idContratoRechazoResidente, String latitud, String longitud, Long idPuntoPaquete, Long idUsuario, Long idContratoDestajoResidente, Double distancia, String observaciones) {
    setIdContratoRechazoResidente(idContratoRechazoResidente);
    setLatitud(latitud);
    setLongitud(longitud);
    setIdPuntoPaquete(idPuntoPaquete);
    setIdUsuario(idUsuario);
    setIdContratoDestajoResidente(idContratoDestajoResidente);
    setDistancia(distancia);
    setObservaciones(observaciones);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdContratoRechazoResidente(Long idContratoRechazoResidente) {
    this.idContratoRechazoResidente = idContratoRechazoResidente;
  }

  public Long getIdContratoRechazoResidente() {
    return idContratoRechazoResidente;
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

  public void setIdPuntoPaquete(Long idPuntoPaquete) {
    this.idPuntoPaquete = idPuntoPaquete;
  }

  public Long getIdPuntoPaquete() {
    return idPuntoPaquete;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdContratoDestajoResidente(Long idContratoDestajoResidente) {
    this.idContratoDestajoResidente = idContratoDestajoResidente;
  }

  public Long getIdContratoDestajoResidente() {
    return idContratoDestajoResidente;
  }

  public void setDistancia(Double distancia) {
    this.distancia = distancia;
  }

  public Double getDistancia() {
    return distancia;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
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
  	return getIdContratoRechazoResidente();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoRechazoResidente = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdContratoRechazoResidente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLatitud());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLongitud());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPuntoPaquete());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoDestajoResidente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDistancia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idContratoRechazoResidente", getIdContratoRechazoResidente());
		regresar.put("latitud", getLatitud());
		regresar.put("longitud", getLongitud());
		regresar.put("idPuntoPaquete", getIdPuntoPaquete());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idContratoDestajoResidente", getIdContratoDestajoResidente());
		regresar.put("distancia", getDistancia());
		regresar.put("observaciones", getObservaciones());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdContratoRechazoResidente(), getLatitud(), getLongitud(), getIdPuntoPaquete(), getIdUsuario(), getIdContratoDestajoResidente(), getDistancia(), getObservaciones(), getRegistro()
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
    regresar.append("idContratoRechazoResidente~");
    regresar.append(getIdContratoRechazoResidente());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoRechazoResidente());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosRechazosResidentesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoRechazoResidente()!= null && getIdContratoRechazoResidente()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosRechazosResidentesDto other = (TcKeetContratosRechazosResidentesDto) obj;
    if (getIdContratoRechazoResidente() != other.idContratoRechazoResidente && (getIdContratoRechazoResidente() == null || !getIdContratoRechazoResidente().equals(other.idContratoRechazoResidente))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoRechazoResidente() != null ? getIdContratoRechazoResidente().hashCode() : 0);
    return hash;
  }

}


