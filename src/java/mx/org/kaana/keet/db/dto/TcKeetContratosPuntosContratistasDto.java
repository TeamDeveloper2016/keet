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
@Table(name="tc_keet_contratos_puntos_contratistas")
public class TcKeetContratosPuntosContratistasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_punto_paquete")
  private Long idPuntoPaquete;
  @Column (name="id_revisado")
  private Long idRevisado;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_punto_contratista")
  private Long idContratoPuntoContratista;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_contrato_destajo_contratista")
  private Long idContratoDestajoContratista;
  @Column (name="factor")
  private Double factor;
  @Column (name="registro")
  private LocalDateTime registro;
	@Column (name="latitud")
  private String latitud;
  @Column (name="longitud")
  private String longitud;
  @Column (name="distancia")
  private Double distancia;
  @Column (name="id_nomina")
  private Long idNomina;

  public TcKeetContratosPuntosContratistasDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosPuntosContratistasDto(Long key) {
    this(null, null, new Long(-1L), null, null, null, null, null, 0D, null);
    setKey(key);
  }

  public TcKeetContratosPuntosContratistasDto(Long idPuntoPaquete, Long idRevisado, Long idContratoPuntoContratista, Long idUsuario, Long idContratoDestajoContratista, Double factor, String latitud, String longitud, Double distancia, Long idNomina) {
    setIdPuntoPaquete(idPuntoPaquete);
    setIdRevisado(idRevisado);
    setIdContratoPuntoContratista(idContratoPuntoContratista);
    setIdUsuario(idUsuario);
    setIdContratoDestajoContratista(idContratoDestajoContratista);
    setFactor(factor);
    setRegistro(LocalDateTime.now());
		setLatitud(latitud);
		setLongitud(longitud);
		this.distancia= distancia;
    this.idNomina= idNomina;
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

  public void setIdContratoPuntoContratista(Long idContratoPuntoContratista) {
    this.idContratoPuntoContratista = idContratoPuntoContratista;
  }

  public Long getIdContratoPuntoContratista() {
    return idContratoPuntoContratista;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdContratoDestajoContratista(Long idContratoDestajoContratista) {
    this.idContratoDestajoContratista = idContratoDestajoContratista;
  }

  public Long getIdContratoDestajoContratista() {
    return idContratoDestajoContratista;
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

	public String getLatitud() {
		return latitud;
	}

	public void setLatitud(String latitud) {
		this.latitud = latitud;
	}

	public String getLongitud() {
		return longitud;
	}

	public void setLongitud(String longitud) {
		this.longitud = longitud;
	}	

	public Double getDistancia() {
		return distancia;
	}

	public void setDistancia(Double distancia) {
		this.distancia=distancia;
	}

  public Long getIdNomina() {
    return idNomina;
  }

  public void setIdNomina(Long idNomina) {
    this.idNomina = idNomina;
  }
	
  @Transient
  @Override
  public Long getKey() {
  	return getIdContratoPuntoContratista();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoPuntoContratista = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdPuntoPaquete());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdRevisado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoPuntoContratista());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoDestajoContratista());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFactor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLatitud());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLongitud());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDistancia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNomina());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idPuntoPaquete", getIdPuntoPaquete());
		regresar.put("idRevisado", getIdRevisado());
		regresar.put("idContratoPuntoContratista", getIdContratoPuntoContratista());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idContratoDestajoContratista", getIdContratoDestajoContratista());
		regresar.put("factor", getFactor());
		regresar.put("registro", getRegistro());
		regresar.put("latitud", getLatitud());
		regresar.put("longitud", getLongitud());
		regresar.put("distancia", getDistancia());
		regresar.put("idNomina", getIdNomina());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
			getIdPuntoPaquete(), getIdRevisado(), getIdContratoPuntoContratista(), getIdUsuario(), getIdContratoDestajoContratista(), getFactor(), getRegistro(), getLatitud(), getLongitud(), getDistancia(), getIdNomina()
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
    regresar.append("idContratoPuntoContratista~");
    regresar.append(getIdContratoPuntoContratista());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoPuntoContratista());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosPuntosContratistasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoPuntoContratista()!= null && getIdContratoPuntoContratista()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosPuntosContratistasDto other = (TcKeetContratosPuntosContratistasDto) obj;
    if (getIdContratoPuntoContratista() != other.idContratoPuntoContratista && (getIdContratoPuntoContratista() == null || !getIdContratoPuntoContratista().equals(other.idContratoPuntoContratista))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoPuntoContratista() != null ? getIdContratoPuntoContratista().hashCode() : 0);
    return hash;
  }
}