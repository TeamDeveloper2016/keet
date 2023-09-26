package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
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
@Table(name="tc_keet_entregas")
public class TcKeetEntregasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_autoriza")
  private Long idAutoriza;
  @Column (name="fecha")
  private LocalDate fecha;
  @Column (name="total")
  private Double total;
  @Column (name="id_paquete")
  private Long idPaquete;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_entrega")
  private Long idEntrega;
  @Column (name="id_contrato_lote")
  private Long idContratoLote;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Column (name="id_recibe")
  private Long idRecibe;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetEntregasDto() {
    this(new Long(-1L));
  }

  public TcKeetEntregasDto(Long key) {
    this(null, LocalDate.now(), null, null, null, new Long(-1L), null, null, null, null, null);
    setKey(key);
  }

  public TcKeetEntregasDto(Long idAutoriza, LocalDate fecha, Double total, Long idPaquete, Long idUsuario, Long idEntrega, Long idContratoLote, String observaciones, Double cantidad, Long idArticulo, Long idRecibe) {
    setIdAutoriza(idAutoriza);
    setFecha(fecha);
    setTotal(total);
    setIdPaquete(idPaquete);
    setIdUsuario(idUsuario);
    setIdEntrega(idEntrega);
    setIdContratoLote(idContratoLote);
    setObservaciones(observaciones);
    setCantidad(cantidad);
    setIdArticulo(idArticulo);
    setIdRecibe(idRecibe);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdAutoriza(Long idAutoriza) {
    this.idAutoriza = idAutoriza;
  }

  public Long getIdAutoriza() {
    return idAutoriza;
  }

  public void setFecha(LocalDate fecha) {
    this.fecha = fecha;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getTotal() {
    return total;
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

  public void setIdEntrega(Long idEntrega) {
    this.idEntrega = idEntrega;
  }

  public Long getIdEntrega() {
    return idEntrega;
  }

  public void setIdContratoLote(Long idContratoLote) {
    this.idContratoLote = idContratoLote;
  }

  public Long getIdContratoLote() {
    return idContratoLote;
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

  public void setIdRecibe(Long idRecibe) {
    this.idRecibe = idRecibe;
  }

  public Long getIdRecibe() {
    return idRecibe;
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
  	return getIdEntrega();
  }

  @Override
  public void setKey(Long key) {
  	this.idEntrega = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdAutoriza());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFecha());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPaquete());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEntrega());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdRecibe());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idAutoriza", getIdAutoriza());
		regresar.put("fecha", getFecha());
		regresar.put("total", getTotal());
		regresar.put("idPaquete", getIdPaquete());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idEntrega", getIdEntrega());
		regresar.put("idContratoLote", getIdContratoLote());
		regresar.put("observaciones", getObservaciones());
		regresar.put("cantidad", getCantidad());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("idRecibe", getIdRecibe());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdAutoriza(), getFecha(), getTotal(), getIdPaquete(), getIdUsuario(), getIdEntrega(), getIdContratoLote(), getObservaciones(), getCantidad(), getIdArticulo(), getIdRecibe(), getRegistro()
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
    regresar.append("idEntrega~");
    regresar.append(getIdEntrega());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdEntrega());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetEntregasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdEntrega()!= null && getIdEntrega()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetEntregasDto other = (TcKeetEntregasDto) obj;
    if (getIdEntrega() != other.idEntrega && (getIdEntrega() == null || !getIdEntrega().equals(other.idEntrega))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdEntrega() != null ? getIdEntrega().hashCode() : 0);
    return hash;
  }

}


