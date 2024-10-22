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
@Table(name="tc_keet_notas_facturas_detalles")
public class TcKeetNotasFacturasDetallesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="codigo")
  private String codigo;
  @Column (name="id_nota_factura")
  private Long idNotaFactura;
  @Column (name="unidad_medida")
  private String unidadMedida;
  @Column (name="descuento")
  private String descuento;
  @Column (name="sat")
  private String sat;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nota_factura_detalle")
  private Long idNotaFacturaDetalle;
  @Column (name="unitario")
  private Double unitario;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="total")
  private Double total;
  @Column (name="clave_unidad")
  private String claveUnidad;
  @Column (name="iva")
  private Double iva;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="subtotal")
  private Double subtotal;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="identificador")
  private String identificador;

  public TcKeetNotasFacturasDetallesDto() {
    this(new Long(-1L));
  }

  public TcKeetNotasFacturasDetallesDto(Long key) {
    this(null, null, null, null, null, null, new Long(-1L), null, null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKeetNotasFacturasDetallesDto(String descripcion, String codigo, Long idNotaFactura, String unidadMedida, String descuento, String sat, Long idNotaFacturaDetalle, Double unitario, Double total, String claveUnidad, Double iva, Long idUsuario, Double subtotal, Double cantidad, String identificador) {
    setDescripcion(descripcion);
    setCodigo(codigo);
    setIdNotaFactura(idNotaFactura);
    setUnidadMedida(unidadMedida);
    setDescuento(descuento);
    setSat(sat);
    setIdNotaFacturaDetalle(idNotaFacturaDetalle);
    setUnitario(unitario);
    setRegistro(LocalDateTime.now());
    setTotal(total);
    setClaveUnidad(claveUnidad);
    setIva(iva);
    setIdUsuario(idUsuario);
    setSubtotal(subtotal);
    setCantidad(cantidad);
    setIdentificador(identificador);
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setIdNotaFactura(Long idNotaFactura) {
    this.idNotaFactura = idNotaFactura;
  }

  public Long getIdNotaFactura() {
    return idNotaFactura;
  }

  public void setUnidadMedida(String unidadMedida) {
    this.unidadMedida = unidadMedida;
  }

  public String getUnidadMedida() {
    return unidadMedida;
  }

  public void setDescuento(String descuento) {
    this.descuento = descuento;
  }

  public String getDescuento() {
    return descuento;
  }

  public void setSat(String sat) {
    this.sat = sat;
  }

  public String getSat() {
    return sat;
  }

  public void setIdNotaFacturaDetalle(Long idNotaFacturaDetalle) {
    this.idNotaFacturaDetalle = idNotaFacturaDetalle;
  }

  public Long getIdNotaFacturaDetalle() {
    return idNotaFacturaDetalle;
  }

  public void setUnitario(Double unitario) {
    this.unitario = unitario;
  }

  public Double getUnitario() {
    return unitario;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getTotal() {
    return total;
  }

  public void setClaveUnidad(String claveUnidad) {
    this.claveUnidad = claveUnidad;
  }

  public String getClaveUnidad() {
    return claveUnidad;
  }

  public void setIva(Double iva) {
    this.iva = iva;
  }

  public Double getIva() {
    return iva;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setSubtotal(Double subtotal) {
    this.subtotal = subtotal;
  }

  public Double getSubtotal() {
    return subtotal;
  }

  public void setCantidad(Double cantidad) {
    this.cantidad = cantidad;
  }

  public Double getCantidad() {
    return cantidad;
  }

  public void setIdentificador(String identificador) {
    this.identificador = identificador;
  }

  public String getIdentificador() {
    return identificador;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdNotaFacturaDetalle();
  }

  @Override
  public void setKey(Long key) {
  	this.idNotaFacturaDetalle = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaFactura());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getUnidadMedida());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDescuento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSat());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaFacturaDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getUnitario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClaveUnidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIva());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSubtotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdentificador());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("codigo", getCodigo());
		regresar.put("idNotaFactura", getIdNotaFactura());
		regresar.put("unidadMedida", getUnidadMedida());
		regresar.put("descuento", getDescuento());
		regresar.put("sat", getSat());
		regresar.put("idNotaFacturaDetalle", getIdNotaFacturaDetalle());
		regresar.put("unitario", getUnitario());
		regresar.put("registro", getRegistro());
		regresar.put("total", getTotal());
		regresar.put("claveUnidad", getClaveUnidad());
		regresar.put("iva", getIva());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("subtotal", getSubtotal());
		regresar.put("cantidad", getCantidad());
		regresar.put("identificador", getIdentificador());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getCodigo(), getIdNotaFactura(), getUnidadMedida(), getDescuento(), getSat(), getIdNotaFacturaDetalle(), getUnitario(), getRegistro(), getTotal(), getClaveUnidad(), getIva(), getIdUsuario(), getSubtotal(), getCantidad(), getIdentificador()
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
    regresar.append("idNotaFacturaDetalle~");
    regresar.append(getIdNotaFacturaDetalle());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNotaFacturaDetalle());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNotasFacturasDetallesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNotaFacturaDetalle()!= null && getIdNotaFacturaDetalle()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetNotasFacturasDetallesDto other = (TcKeetNotasFacturasDetallesDto) obj;
    if (getIdNotaFacturaDetalle() != other.idNotaFacturaDetalle && (getIdNotaFacturaDetalle() == null || !getIdNotaFacturaDetalle().equals(other.idNotaFacturaDetalle))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNotaFacturaDetalle() != null ? getIdNotaFacturaDetalle().hashCode() : 0);
    return hash;
  }

}


