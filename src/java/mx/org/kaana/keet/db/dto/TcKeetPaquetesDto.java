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
@Table(name="tc_keet_paquetes")
public class TcKeetPaquetesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_proceso")
  private Long idProceso;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_paquete")
  private Long idPaquete;
  @Column (name="id_desarrollo")
  private Long idDesarrollo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_sub_proceso")
  private Long idSubProceso;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_prototipo")
  private Long idPrototipo;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetPaquetesDto() {
    this(new Long(-1L));
  }

  public TcKeetPaquetesDto(Long key) {
    this(null, new Long(-1L), null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKeetPaquetesDto(Long idProceso, Long idPaquete, Long idDesarrollo, Long idUsuario, Long idSubProceso, String observaciones, Long idPrototipo, Double cantidad, Long idArticulo) {
    setIdProceso(idProceso);
    setIdPaquete(idPaquete);
    setIdDesarrollo(idDesarrollo);
    setIdUsuario(idUsuario);
    setIdSubProceso(idSubProceso);
    setObservaciones(observaciones);
    setIdPrototipo(idPrototipo);
    setCantidad(cantidad);
    setIdArticulo(idArticulo);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdProceso(Long idProceso) {
    this.idProceso = idProceso;
  }

  public Long getIdProceso() {
    return idProceso;
  }

  public void setIdPaquete(Long idPaquete) {
    this.idPaquete = idPaquete;
  }

  public Long getIdPaquete() {
    return idPaquete;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdSubProceso(Long idSubProceso) {
    this.idSubProceso = idSubProceso;
  }

  public Long getIdSubProceso() {
    return idSubProceso;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdPrototipo(Long idPrototipo) {
    this.idPrototipo = idPrototipo;
  }

  public Long getIdPrototipo() {
    return idPrototipo;
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

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdPaquete();
  }

  @Override
  public void setKey(Long key) {
  	this.idPaquete = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdProceso());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPaquete());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdSubProceso());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrototipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idProceso", getIdProceso());
		regresar.put("idPaquete", getIdPaquete());
		regresar.put("idDesarrollo", getIdDesarrollo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idSubProceso", getIdSubProceso());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idPrototipo", getIdPrototipo());
		regresar.put("cantidad", getCantidad());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdProceso(), getIdPaquete(), getIdDesarrollo(), getIdUsuario(), getIdSubProceso(), getObservaciones(), getIdPrototipo(), getCantidad(), getIdArticulo(), getRegistro()
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
    regresar.append("idPaquete~");
    regresar.append(getIdPaquete());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPaquete());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPaquetesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPaquete()!= null && getIdPaquete()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPaquetesDto other = (TcKeetPaquetesDto) obj;
    if (getIdPaquete() != other.idPaquete && (getIdPaquete() == null || !getIdPaquete().equals(other.idPaquete))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPaquete() != null ? getIdPaquete().hashCode() : 0);
    return hash;
  }

}


