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
@Table(name="tc_keet_paquetes_detalles")
public class TcKeetPaquetesDetallesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_paquete")
  private Long idPaquete;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_paquete_detalle")
  private Long idPaqueteDetalle;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetPaquetesDetallesDto() {
    this(new Long(-1L));
  }

  public TcKeetPaquetesDetallesDto(Long key) {
    this(null, null, null, 1D, null, new Long(-1L));
    setKey(key);
  }

  public TcKeetPaquetesDetallesDto(Long idPaquete, Long idUsuario, String observaciones, Double cantidad, Long idArticulo, Long idPaqueteDetalle) {
    setIdPaquete(idPaquete);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setCantidad(cantidad);
    setIdArticulo(idArticulo);
    setIdPaqueteDetalle(idPaqueteDetalle);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdPaquete(Long idPaquete) {
    this.idPaquete = idPaquete;
  }

  public Long getIdPaquete() {
    return idPaquete;
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

  public void setCantidad(Double cantidad) {
    this.cantidad = cantidad;
  }

  public Double getCantidad() {
    return cantidad;
  }

  public void setIdArticulo(Long idArticulo) {
    this.idArticulo = idArticulo;
  }

  public Long getIdArticulo() {
    return idArticulo;
  }

  public void setIdPaqueteDetalle(Long idPaqueteDetalle) {
    this.idPaqueteDetalle = idPaqueteDetalle;
  }

  public Long getIdPaqueteDetalle() {
    return idPaqueteDetalle;
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
  	return getIdPaqueteDetalle();
  }

  @Override
  public void setKey(Long key) {
  	this.idPaqueteDetalle = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdPaquete());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPaqueteDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idPaquete", getIdPaquete());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("cantidad", getCantidad());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("idPaqueteDetalle", getIdPaqueteDetalle());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdPaquete(), getIdUsuario(), getObservaciones(), getCantidad(), getIdArticulo(), getIdPaqueteDetalle(), getRegistro()
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
    regresar.append("idPaqueteDetalle~");
    regresar.append(getIdPaqueteDetalle());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPaqueteDetalle());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPaquetesDetallesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPaqueteDetalle()!= null && getIdPaqueteDetalle()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPaquetesDetallesDto other = (TcKeetPaquetesDetallesDto) obj;
    if (getIdPaqueteDetalle() != other.idPaqueteDetalle && (getIdPaqueteDetalle() == null || !getIdPaqueteDetalle().equals(other.idPaqueteDetalle))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPaqueteDetalle() != null ? getIdPaqueteDetalle().hashCode() : 0);
    return hash;
  }

}


