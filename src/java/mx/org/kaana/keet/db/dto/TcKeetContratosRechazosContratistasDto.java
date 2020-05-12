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
@Table(name="tc_keet_contratos_rechazos_contratistas")
public class TcKeetContratosRechazosContratistasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_punto_paquete")
  private Long idPuntoPaquete;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="observaciones")
  private String observaciones;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_rechazo_contratista")
  private Long idContratoRechazoContratista;
  @Column (name="id_contrato_destajo_contratista")
  private Long idContratoDestajoContratista;
  @Column (name="registro")
  private LocalDateTime registro;
	@Column (name="latitud")
  private String latitud;
  @Column (name="longitud")
  private String longitud;
  @Column (name="distancia")
  private Double distancia;

  public TcKeetContratosRechazosContratistasDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosRechazosContratistasDto(Long key) {
    this(null, null, null, new Long(-1L), null, null, null, 0D);
    setKey(key);
  }

  public TcKeetContratosRechazosContratistasDto(Long idPuntoPaquete, Long idUsuario, String observaciones, Long idContratoRechazoContratista, Long idContratoDestajoContratista, String latitud, String longitud, Double distancia) {
    setIdPuntoPaquete(idPuntoPaquete);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setIdContratoRechazoContratista(idContratoRechazoContratista);
    setIdContratoDestajoContratista(idContratoDestajoContratista);
    setRegistro(LocalDateTime.now());
		setLatitud(latitud);
		setLongitud(longitud);
		setDistancia(distancia);
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

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdContratoRechazoContratista(Long idContratoRechazoContratista) {
    this.idContratoRechazoContratista = idContratoRechazoContratista;
  }

  public Long getIdContratoRechazoContratista() {
    return idContratoRechazoContratista;
  }

  public void setIdContratoDestajoContratista(Long idContratoDestajoContratista) {
    this.idContratoDestajoContratista = idContratoDestajoContratista;
  }

  public Long getIdContratoDestajoContratista() {
    return idContratoDestajoContratista;
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
	
  @Transient
  @Override
  public Long getKey() {
  	return getIdContratoRechazoContratista();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoRechazoContratista = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdPuntoPaquete());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoRechazoContratista());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoDestajoContratista());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLatitud());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLongitud());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDistancia());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idPuntoPaquete", getIdPuntoPaquete());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idContratoRechazoContratista", getIdContratoRechazoContratista());
		regresar.put("idContratoDestajoContratista", getIdContratoDestajoContratista());
		regresar.put("registro", getRegistro());
		regresar.put("latitud", getLatitud());
		regresar.put("longitud", getLongitud());
		regresar.put("distancia", getDistancia());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
			getIdPuntoPaquete(), getIdUsuario(), getObservaciones(), getIdContratoRechazoContratista(), getIdContratoDestajoContratista(), getRegistro(), getLatitud(), getLongitud(), getDistancia()
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
    regresar.append("idContratoRechazoContratista~");
    regresar.append(getIdContratoRechazoContratista());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoRechazoContratista());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosRechazosContratistasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoRechazoContratista()!= null && getIdContratoRechazoContratista()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosRechazosContratistasDto other = (TcKeetContratosRechazosContratistasDto) obj;
    if (getIdContratoRechazoContratista() != other.idContratoRechazoContratista && (getIdContratoRechazoContratista() == null || !getIdContratoRechazoContratista().equals(other.idContratoRechazoContratista))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoRechazoContratista() != null ? getIdContratoRechazoContratista().hashCode() : 0);
    return hash;
  }
}