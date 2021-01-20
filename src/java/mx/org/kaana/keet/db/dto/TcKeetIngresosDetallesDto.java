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
@Table(name="tc_keet_ingresos_detalles")
public class TcKeetIngresosDetallesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descuentos")
  private Double descuentos;
  @Column (name="codigo")
  private String codigo;
  @Column (name="id_ingreso")
  private Long idIngreso;
  @Column (name="unidad_medida")
  private String unidadMedida;
  @Column (name="costo")
  private Double costo;
  @Column (name="descuento")
  private String descuento;
  @Column (name="sat")
  private String sat;
  @Column (name="extras")
  private String extras;
  @Column (name="utilidad")
  private Double utilidad;
  @Column (name="nombre")
  private String nombre;
  @Column (name="importe")
  private Double importe;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="precio")
  private Double precio;
  @Column (name="iva")
  private Double iva;
  @Column (name="impuestos")
  private Double impuestos;
  @Column (name="unitario_sin_iva")
  private Double unitarioSinIva;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_ingreso_detalle")
  private Long idIngresoDetalle;
  @Column (name="sub_total")
  private Double subTotal;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="id_articulo")
  private Long idArticulo;

  public TcKeetIngresosDetallesDto() {
    this(new Long(-1L));
  }

  public TcKeetIngresosDetallesDto(Long key) {
    this(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcKeetIngresosDetallesDto(Double descuentos, String codigo, Long idIngreso, String unidadMedida, Double costo, String descuento, String sat, String extras, Double utilidad, String nombre, Double importe, Double precio, Double iva, Double impuestos, Double unitarioSinIva, Long idIngresoDetalle, Double subTotal, Double cantidad, Long idArticulo) {
    setDescuentos(descuentos);
    setCodigo(codigo);
    setIdIngreso(idIngreso);
    setUnidadMedida(unidadMedida);
    setCosto(costo);
    setDescuento(descuento);
    setSat(sat);
    setExtras(extras);
    setUtilidad(utilidad);
    setNombre(nombre);
    setImporte(importe);
    setRegistro(LocalDateTime.now());
    setPrecio(precio);
    setIva(iva);
    setImpuestos(impuestos);
    setUnitarioSinIva(unitarioSinIva);
    setIdIngresoDetalle(idIngresoDetalle);
    setSubTotal(subTotal);
    setCantidad(cantidad);
    setIdArticulo(idArticulo);
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

  public void setIdIngreso(Long idIngreso) {
    this.idIngreso = idIngreso;
  }

  public Long getIdIngreso() {
    return idIngreso;
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

  public void setSat(String sat) {
    this.sat = sat;
  }

  public String getSat() {
    return sat;
  }

  public void setExtras(String extras) {
    this.extras = extras;
  }

  public String getExtras() {
    return extras;
  }

  public void setUtilidad(Double utilidad) {
    this.utilidad = utilidad;
  }

  public Double getUtilidad() {
    return utilidad;
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

  public void setPrecio(Double precio) {
    this.precio = precio;
  }

  public Double getPrecio() {
    return precio;
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

  public void setUnitarioSinIva(Double unitarioSinIva) {
    this.unitarioSinIva = unitarioSinIva;
  }

  public Double getUnitarioSinIva() {
    return unitarioSinIva;
  }

  public void setIdIngresoDetalle(Long idIngresoDetalle) {
    this.idIngresoDetalle = idIngresoDetalle;
  }

  public Long getIdIngresoDetalle() {
    return idIngresoDetalle;
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
  	return getIdIngresoDetalle();
  }

  @Override
  public void setKey(Long key) {
  	this.idIngresoDetalle = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescuentos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdIngreso());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getUnidadMedida());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCosto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDescuento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSat());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getExtras());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getUtilidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPrecio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIva());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImpuestos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getUnitarioSinIva());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdIngresoDetalle());
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
		regresar.put("descuentos", getDescuentos());
		regresar.put("codigo", getCodigo());
		regresar.put("idIngreso", getIdIngreso());
		regresar.put("unidadMedida", getUnidadMedida());
		regresar.put("costo", getCosto());
		regresar.put("descuento", getDescuento());
		regresar.put("sat", getSat());
		regresar.put("extras", getExtras());
		regresar.put("utilidad", getUtilidad());
		regresar.put("nombre", getNombre());
		regresar.put("importe", getImporte());
		regresar.put("registro", getRegistro());
		regresar.put("precio", getPrecio());
		regresar.put("iva", getIva());
		regresar.put("impuestos", getImpuestos());
		regresar.put("unitarioSinIva", getUnitarioSinIva());
		regresar.put("idIngresoDetalle", getIdIngresoDetalle());
		regresar.put("subTotal", getSubTotal());
		regresar.put("cantidad", getCantidad());
		regresar.put("idArticulo", getIdArticulo());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescuentos(), getCodigo(), getIdIngreso(), getUnidadMedida(), getCosto(), getDescuento(), getSat(), getExtras(), getUtilidad(), getNombre(), getImporte(), getRegistro(), getPrecio(), getIva(), getImpuestos(), getUnitarioSinIva(), getIdIngresoDetalle(), getSubTotal(), getCantidad(), getIdArticulo()
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
    regresar.append("idIngresoDetalle~");
    regresar.append(getIdIngresoDetalle());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdIngresoDetalle());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetIngresosDetallesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdIngresoDetalle()!= null && getIdIngresoDetalle()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetIngresosDetallesDto other = (TcKeetIngresosDetallesDto) obj;
    if (getIdIngresoDetalle() != other.idIngresoDetalle && (getIdIngresoDetalle() == null || !getIdIngresoDetalle().equals(other.idIngresoDetalle))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdIngresoDetalle() != null ? getIdIngresoDetalle().hashCode() : 0);
    return hash;
  }

}


