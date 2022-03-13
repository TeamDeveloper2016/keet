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
@Table(name="tc_keet_estimaciones_detalles")
public class TcKeetEstimacionesDetallesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_estimacion")
  private Long idEstimacion;
  @Column (name="id_tipo_retencion")
  private Long idTipoRetencion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_estimacion_detalle")
  private Long idEstimacionDetalle;
  @Column (name="porcentaje")
  private Double porcentaje;
  @Column (name="importe")
  private Double importe;
  @Column (name="id_deduccion")
  private Long idDeduccion;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetEstimacionesDetallesDto() {
    this(new Long(-1L));
  }

  public TcKeetEstimacionesDetallesDto(Long key) {
    this(null, null, null, new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcKeetEstimacionesDetallesDto(Long idEstimacion, Long idTipoRetencion, Long idUsuario, Long idEstimacionDetalle, Double porcentaje, Double importe, Long idDeduccion) {
    setIdEstimacion(idEstimacion);
    setIdTipoRetencion(idTipoRetencion);
    setIdUsuario(idUsuario);
    setIdEstimacionDetalle(idEstimacionDetalle);
    setPorcentaje(porcentaje);
    setImporte(importe);
    setIdDeduccion(idDeduccion);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdEstimacion(Long idEstimacion) {
    this.idEstimacion = idEstimacion;
  }

  public Long getIdEstimacion() {
    return idEstimacion;
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

  public void setIdEstimacionDetalle(Long idEstimacionDetalle) {
    this.idEstimacionDetalle = idEstimacionDetalle;
  }

  public Long getIdEstimacionDetalle() {
    return idEstimacionDetalle;
  }

  public void setPorcentaje(Double porcentaje) {
    this.porcentaje = porcentaje;
  }

  public Double getPorcentaje() {
    return porcentaje;
  }

  public void setImporte(Double importe) {
    this.importe = importe;
  }

  public Double getImporte() {
    return importe;
  }

  public void setIdDeduccion(Long idDeduccion) {
    this.idDeduccion = idDeduccion;
  }

  public Long getIdDeduccion() {
    return idDeduccion;
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
  	return getIdEstimacionDetalle();
  }

  @Override
  public void setKey(Long key) {
  	this.idEstimacionDetalle = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdEstimacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoRetencion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEstimacionDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPorcentaje());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDeduccion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idEstimacion", getIdEstimacion());
		regresar.put("idTipoRetencion", getIdTipoRetencion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idEstimacionDetalle", getIdEstimacionDetalle());
		regresar.put("porcentaje", getPorcentaje());
		regresar.put("importe", getImporte());
		regresar.put("idDeduccion", getIdDeduccion());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdEstimacion(), getIdTipoRetencion(), getIdUsuario(), getIdEstimacionDetalle(), getPorcentaje(), getImporte(), getIdDeduccion(), getRegistro()
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
    regresar.append("idEstimacionDetalle~");
    regresar.append(getIdEstimacionDetalle());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdEstimacionDetalle());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetEstimacionesDetallesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdEstimacionDetalle()!= null && getIdEstimacionDetalle()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetEstimacionesDetallesDto other = (TcKeetEstimacionesDetallesDto) obj;
    if (getIdEstimacionDetalle() != other.idEstimacionDetalle && (getIdEstimacionDetalle() == null || !getIdEstimacionDetalle().equals(other.idEstimacionDetalle))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdEstimacionDetalle() != null ? getIdEstimacionDetalle().hashCode() : 0);
    return hash;
  }

}


