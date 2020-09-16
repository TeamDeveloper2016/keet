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
@Table(name="tc_keet_contratos_puntos_residentes")
public class TcKeetContratosPuntosResidentesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="latitud")
  private String latitud;
  @Column (name="longitud")
  private String longitud;
  @Column (name="id_punto_paquete")
  private Long idPuntoPaquete;
  @Column (name="id_revisado")
  private Long idRevisado;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_contrato_destajo_residente")
  private Long idContratoDestajoResidente;
  @Column (name="distancia")
  private Double distancia;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_punto_residente")
  private Long idContratoPuntoResidente;
  @Column (name="factor")
  private Double factor;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetContratosPuntosResidentesDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosPuntosResidentesDto(Long key) {
    this(null, null, null, null, null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKeetContratosPuntosResidentesDto(String latitud, String longitud, Long idPuntoPaquete, Long idRevisado, Long idUsuario, Long idContratoDestajoResidente, Double distancia, Long idContratoPuntoResidente, Double factor) {
    setLatitud(latitud);
    setLongitud(longitud);
    setIdPuntoPaquete(idPuntoPaquete);
    setIdRevisado(idRevisado);
    setIdUsuario(idUsuario);
    setIdContratoDestajoResidente(idContratoDestajoResidente);
    setDistancia(distancia);
    setIdContratoPuntoResidente(idContratoPuntoResidente);
    setFactor(factor);
    setRegistro(LocalDateTime.now());
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

  public void setIdRevisado(Long idRevisado) {
    this.idRevisado = idRevisado;
  }

  public Long getIdRevisado() {
    return idRevisado;
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

  public void setIdContratoPuntoResidente(Long idContratoPuntoResidente) {
    this.idContratoPuntoResidente = idContratoPuntoResidente;
  }

  public Long getIdContratoPuntoResidente() {
    return idContratoPuntoResidente;
  }

  public void setFactor(Double factor) {
    this.factor = factor;
  }

  public Double getFactor() {
    return factor;
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
  	return getIdContratoPuntoResidente();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoPuntoResidente = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getLatitud());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLongitud());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPuntoPaquete());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdRevisado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoDestajoResidente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDistancia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoPuntoResidente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFactor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("latitud", getLatitud());
		regresar.put("longitud", getLongitud());
		regresar.put("idPuntoPaquete", getIdPuntoPaquete());
		regresar.put("idRevisado", getIdRevisado());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idContratoDestajoResidente", getIdContratoDestajoResidente());
		regresar.put("distancia", getDistancia());
		regresar.put("idContratoPuntoResidente", getIdContratoPuntoResidente());
		regresar.put("factor", getFactor());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getLatitud(), getLongitud(), getIdPuntoPaquete(), getIdRevisado(), getIdUsuario(), getIdContratoDestajoResidente(), getDistancia(), getIdContratoPuntoResidente(), getFactor(), getRegistro()
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
    regresar.append("idContratoPuntoResidente~");
    regresar.append(getIdContratoPuntoResidente());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoPuntoResidente());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosPuntosResidentesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoPuntoResidente()!= null && getIdContratoPuntoResidente()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosPuntosResidentesDto other = (TcKeetContratosPuntosResidentesDto) obj;
    if (getIdContratoPuntoResidente() != other.idContratoPuntoResidente && (getIdContratoPuntoResidente() == null || !getIdContratoPuntoResidente().equals(other.idContratoPuntoResidente))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoPuntoResidente() != null ? getIdContratoPuntoResidente().hashCode() : 0);
    return hash;
  }

}


