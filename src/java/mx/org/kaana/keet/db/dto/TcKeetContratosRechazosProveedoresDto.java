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
@Table(name="tc_keet_contratos_rechazos_proveedores")
public class TcKeetContratosRechazosProveedoresDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_rechazo_proveedor")
  private Long idContratoRechazoProveedor;
  @Column (name="id_contrato_destajo_proveedor")
  private Long idContratoDestajoProveedor;
  @Column (name="id_punto_paquete")
  private Long idPuntoPaquete;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="registro")
  private LocalDateTime registro;
	@Column (name="latitud")
  private String latitud;
  @Column (name="longitud")
  private String longitud;
  @Column (name="distancia")
  private Double distancia;

  public TcKeetContratosRechazosProveedoresDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosRechazosProveedoresDto(Long key) {
    this(new Long(-1L), null, null, null, null, null, null, 0D);
    setKey(key);
  }

  public TcKeetContratosRechazosProveedoresDto(Long idContratoRechazoProveedor, Long idContratoDestajoProveedor, Long idPuntoPaquete, Long idUsuario, String observaciones, String latitud, String longitud, Double distancia) {
    setIdContratoRechazoProveedor(idContratoRechazoProveedor);
    setIdContratoDestajoProveedor(idContratoDestajoProveedor);
    setIdPuntoPaquete(idPuntoPaquete);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setRegistro(LocalDateTime.now());
		setLatitud(latitud);
		setLongitud(longitud);
		setDistancia(distancia);
  }
	
  public void setIdContratoRechazoProveedor(Long idContratoRechazoProveedor) {
    this.idContratoRechazoProveedor = idContratoRechazoProveedor;
  }

  public Long getIdContratoRechazoProveedor() {
    return idContratoRechazoProveedor;
  }

  public void setIdContratoDestajoProveedor(Long idContratoDestajoProveedor) {
    this.idContratoDestajoProveedor = idContratoDestajoProveedor;
  }

  public Long getIdContratoDestajoProveedor() {
    return idContratoDestajoProveedor;
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
  	return getIdContratoRechazoProveedor();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoRechazoProveedor = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdContratoRechazoProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoDestajoProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPuntoPaquete());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
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
		regresar.put("idContratoRechazoProveedor", getIdContratoRechazoProveedor());
		regresar.put("idContratoDestajoProveedor", getIdContratoDestajoProveedor());
		regresar.put("idPuntoPaquete", getIdPuntoPaquete());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("registro", getRegistro());
		regresar.put("latitud", getLatitud());
		regresar.put("longitud", getLongitud());
		regresar.put("distancia", getDistancia());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
			getIdContratoRechazoProveedor(), getIdContratoDestajoProveedor(), getIdPuntoPaquete(), getIdUsuario(), getObservaciones(), getRegistro(), getLatitud(), getLongitud(), getDistancia()
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
    regresar.append("idContratoRechazoProveedor~");
    regresar.append(getIdContratoRechazoProveedor());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoRechazoProveedor());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosRechazosProveedoresDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoRechazoProveedor()!= null && getIdContratoRechazoProveedor()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosRechazosProveedoresDto other = (TcKeetContratosRechazosProveedoresDto) obj;
    if (getIdContratoRechazoProveedor() != other.idContratoRechazoProveedor && (getIdContratoRechazoProveedor() == null || !getIdContratoRechazoProveedor().equals(other.idContratoRechazoProveedor))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoRechazoProveedor() != null ? getIdContratoRechazoProveedor().hashCode() : 0);
    return hash;
  }
}