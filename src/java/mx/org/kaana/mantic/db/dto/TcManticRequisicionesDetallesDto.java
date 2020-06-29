package mx.org.kaana.mantic.db.dto;

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
@Table(name="tc_mantic_requisiciones_detalles")
public class TcManticRequisicionesDetallesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_requisicion_detalle")
  private Long idRequisicionDetalle;
  @Column (name="descuentos")
  private Double descuentos;
  @Column (name="codigo")
  private String codigo;
  @Column (name="unidad_medida")
  private String unidadMedida;
  @Column (name="costo")
  private Double costo;
  @Column (name="descuento")
  private String descuento;
  @Column (name="id_tipo_precio")
  private Long idTipoPrecio;
  @Column (name="id_requisicion")
  private Long idRequisicion;
  @Column (name="nombre")
  private String nombre;
  @Column (name="importe")
  private Double importe;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="propio")
  private String propio;
  @Column (name="iva")
  private Double iva;
  @Column (name="impuestos")
  private Double impuestos;
  @Column (name="sub_total")
  private Double subTotal;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="id_articulo")
  private Long idArticulo;

  public TcManticRequisicionesDetallesDto() {
    this(new Long(-1L));
  }

  public TcManticRequisicionesDetallesDto(Long key) {
    this(new Long(-1L), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcManticRequisicionesDetallesDto(Long idRequisicionDetalle, Double descuentos, String codigo, String unidadMedida, Double costo, String descuento, Long idTipoPrecio, Long idRequisicion, String nombre, Double importe, String propio, Double iva, Double impuestos, Double subTotal, Double cantidad, Long idArticulo) {
    setIdRequisicionDetalle(idRequisicionDetalle);
    setDescuentos(descuentos);
    setCodigo(codigo);
    setUnidadMedida(unidadMedida);
    setCosto(costo);
    setDescuento(descuento);
    setIdTipoPrecio(idTipoPrecio);
    setIdRequisicion(idRequisicion);
    setNombre(nombre);
    setImporte(importe);
    setRegistro(LocalDateTime.now());
    setPropio(propio);
    setIva(iva);
    setImpuestos(impuestos);
    setSubTotal(subTotal);
    setCantidad(cantidad);
    setIdArticulo(idArticulo);
  }
	
  public void setIdRequisicionDetalle(Long idRequisicionDetalle) {
    this.idRequisicionDetalle = idRequisicionDetalle;
  }

  public Long getIdRequisicionDetalle() {
    return idRequisicionDetalle;
  }

  public void setDescuentos(Double descuentos) {
    this.descuentos = descuentos;
  }

  public Double getDescuentos() {
    return descuentos;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setUnidadMedida(String unidadMedida) {
    this.unidadMedida = unidadMedida;
  }

  public String getUnidadMedida() {
    return unidadMedida;
  }

  public void setCosto(Double costo) {
    this.costo = costo;
  }

  public Double getCosto() {
    return costo;
  }

  public void setDescuento(String descuento) {
    this.descuento = descuento;
  }

  public String getDescuento() {
    return descuento;
  }

  public void setIdTipoPrecio(Long idTipoPrecio) {
    this.idTipoPrecio = idTipoPrecio;
  }

  public Long getIdTipoPrecio() {
    return idTipoPrecio;
  }

  public void setIdRequisicion(Long idRequisicion) {
    this.idRequisicion = idRequisicion;
  }

  public Long getIdRequisicion() {
    return idRequisicion;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setImporte(Double importe) {
    this.importe = importe;
  }

  public Double getImporte() {
    return importe;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  public void setPropio(String propio) {
    this.propio = propio;
  }

  public String getPropio() {
    return propio;
  }

  public void setIva(Double iva) {
    this.iva = iva;
  }

  public Double getIva() {
    return iva;
  }

  public void setImpuestos(Double impuestos) {
    this.impuestos = impuestos;
  }

  public Double getImpuestos() {
    return impuestos;
  }

  public void setSubTotal(Double subTotal) {
    this.subTotal = subTotal;
  }

  public Double getSubTotal() {
    return subTotal;
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

  @Transient
  @Override
  public Long getKey() {
  	return getIdRequisicionDetalle();
  }

  @Override
  public void setKey(Long key) {
  	this.idRequisicionDetalle = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdRequisicionDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDescuentos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getUnidadMedida());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCosto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDescuento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoPrecio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdRequisicion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPropio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIva());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImpuestos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSubTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idRequisicionDetalle", getIdRequisicionDetalle());
		regresar.put("descuentos", getDescuentos());
		regresar.put("codigo", getCodigo());
		regresar.put("unidadMedida", getUnidadMedida());
		regresar.put("costo", getCosto());
		regresar.put("descuento", getDescuento());
		regresar.put("idTipoPrecio", getIdTipoPrecio());
		regresar.put("idRequisicion", getIdRequisicion());
		regresar.put("nombre", getNombre());
		regresar.put("importe", getImporte());
		regresar.put("registro", getRegistro());
		regresar.put("propio", getPropio());
		regresar.put("iva", getIva());
		regresar.put("impuestos", getImpuestos());
		regresar.put("subTotal", getSubTotal());
		regresar.put("cantidad", getCantidad());
		regresar.put("idArticulo", getIdArticulo());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
		  getIdRequisicionDetalle(), getDescuentos(), getCodigo(), getUnidadMedida(), getCosto(), getDescuento(), getIdTipoPrecio(), getIdRequisicion(), getNombre(), getImporte(), getRegistro(), getPropio(), getIva(), getImpuestos(), getSubTotal(), getCantidad(), getIdArticulo()
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
    regresar.append("idRequisicionDetalle~");
    regresar.append(getIdRequisicionDetalle());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdRequisicionDetalle());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticRequisicionesDetallesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdRequisicionDetalle()!= null && getIdRequisicionDetalle()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticRequisicionesDetallesDto other = (TcManticRequisicionesDetallesDto) obj;
    if (getIdRequisicionDetalle() != other.idRequisicionDetalle && (getIdRequisicionDetalle() == null || !getIdRequisicionDetalle().equals(other.idRequisicionDetalle))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdRequisicionDetalle() != null ? getIdRequisicionDetalle().hashCode() : 0);
    return hash;
  }
}