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
@Table(name="tc_keet_entregas_detalles")
public class TcKeetEntregasDetallesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_completo")
  private Long idCompleto;
  @Column (name="codigo")
  private String codigo;
  @Column (name="total")
  private Double total;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_entrega")
  private Long idEntrega;
  @Column (name="observaciones")
  private String observaciones;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_entrega_detalle")
  private Long idEntregaDetalle;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Column (name="nombre")
  private String nombre;
  @Column (name="id_recibe")
  private Long idRecibe;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetEntregasDetallesDto() {
    this(new Long(-1L));
  }

  public TcKeetEntregasDetallesDto(Long key) {
    this(null, null, null, null, null, null, new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcKeetEntregasDetallesDto(Long idCompleto, String codigo, Double total, Long idUsuario, Long idEntrega, String observaciones, Long idEntregaDetalle, Double cantidad, Long idArticulo, String nombre, Long idRecibe) {
    setIdCompleto(idCompleto);
    setCodigo(codigo);
    setTotal(total);
    setIdUsuario(idUsuario);
    setIdEntrega(idEntrega);
    setObservaciones(observaciones);
    setIdEntregaDetalle(idEntregaDetalle);
    setCantidad(cantidad);
    setIdArticulo(idArticulo);
    setNombre(nombre);
    setIdRecibe(idRecibe);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdCompleto(Long idCompleto) {
    this.idCompleto = idCompleto;
  }

  public Long getIdCompleto() {
    return idCompleto;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getTotal() {
    return total;
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

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdEntregaDetalle(Long idEntregaDetalle) {
    this.idEntregaDetalle = idEntregaDetalle;
  }

  public Long getIdEntregaDetalle() {
    return idEntregaDetalle;
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

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
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
  	return getIdEntregaDetalle();
  }

  @Override
  public void setKey(Long key) {
  	this.idEntregaDetalle = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdCompleto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEntrega());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEntregaDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
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
		regresar.put("idCompleto", getIdCompleto());
		regresar.put("codigo", getCodigo());
		regresar.put("total", getTotal());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idEntrega", getIdEntrega());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEntregaDetalle", getIdEntregaDetalle());
		regresar.put("cantidad", getCantidad());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("nombre", getNombre());
		regresar.put("idRecibe", getIdRecibe());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdCompleto(), getCodigo(), getTotal(), getIdUsuario(), getIdEntrega(), getObservaciones(), getIdEntregaDetalle(), getCantidad(), getIdArticulo(), getNombre(), getIdRecibe(), getRegistro()
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
    regresar.append("idEntregaDetalle~");
    regresar.append(getIdEntregaDetalle());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdEntregaDetalle());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetEntregasDetallesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdEntregaDetalle()!= null && getIdEntregaDetalle()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetEntregasDetallesDto other = (TcKeetEntregasDetallesDto) obj;
    if (getIdEntregaDetalle() != other.idEntregaDetalle && (getIdEntregaDetalle() == null || !getIdEntregaDetalle().equals(other.idEntregaDetalle))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdEntregaDetalle() != null ? getIdEntregaDetalle().hashCode() : 0);
    return hash;
  }

}


