package mx.org.kaana.sakbe.db.dto;

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
@Table(name="tc_sakbe_servicios_detalles")
public class TcSakbeServiciosDetallesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="transcurridos")
  private Double transcurridos;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_servicio_detalle")
  private Long idServicioDetalle;
  @Column (name="dias_transcurridos")
  private Double diasTranscurridos;
  @Column (name="id_servicio")
  private Long idServicio;
  @Column (name="id_suministro")
  private Long idSuministro;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcSakbeServiciosDetallesDto() {
    this(new Long(-1L));
  }

  public TcSakbeServiciosDetallesDto(Long key) {
    this(null, new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcSakbeServiciosDetallesDto(Double transcurridos, Long idServicioDetalle, Double diasTranscurridos, Long idServicio, Long idSuministro) {
    setTranscurridos(transcurridos);
    setIdServicioDetalle(idServicioDetalle);
    setDiasTranscurridos(diasTranscurridos);
    setIdServicio(idServicio);
    setIdSuministro(idSuministro);
    setRegistro(LocalDateTime.now());
  }
	
  public void setTranscurridos(Double transcurridos) {
    this.transcurridos = transcurridos;
  }

  public Double getTranscurridos() {
    return transcurridos;
  }

  public void setIdServicioDetalle(Long idServicioDetalle) {
    this.idServicioDetalle = idServicioDetalle;
  }

  public Long getIdServicioDetalle() {
    return idServicioDetalle;
  }

  public void setDiasTranscurridos(Double diasTranscurridos) {
    this.diasTranscurridos = diasTranscurridos;
  }

  public Double getDiasTranscurridos() {
    return diasTranscurridos;
  }

  public void setIdServicio(Long idServicio) {
    this.idServicio = idServicio;
  }

  public Long getIdServicio() {
    return idServicio;
  }

  public void setIdSuministro(Long idSuministro) {
    this.idSuministro = idSuministro;
  }

  public Long getIdSuministro() {
    return idSuministro;
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
  	return getIdServicioDetalle();
  }

  @Override
  public void setKey(Long key) {
  	this.idServicioDetalle = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getTranscurridos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdServicioDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDiasTranscurridos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdServicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdSuministro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("transcurridos", getTranscurridos());
		regresar.put("idServicioDetalle", getIdServicioDetalle());
		regresar.put("diasTranscurridos", getDiasTranscurridos());
		regresar.put("idServicio", getIdServicio());
		regresar.put("idSuministro", getIdSuministro());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getTranscurridos(), getIdServicioDetalle(), getDiasTranscurridos(), getIdServicio(), getIdSuministro(), getRegistro()
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
    regresar.append("idServicioDetalle~");
    regresar.append(getIdServicioDetalle());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdServicioDetalle());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeServiciosDetallesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdServicioDetalle()!= null && getIdServicioDetalle()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcSakbeServiciosDetallesDto other = (TcSakbeServiciosDetallesDto) obj;
    if (getIdServicioDetalle() != other.idServicioDetalle && (getIdServicioDetalle() == null || !getIdServicioDetalle().equals(other.idServicioDetalle))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdServicioDetalle() != null ? getIdServicioDetalle().hashCode() : 0);
    return hash;
  }

}


