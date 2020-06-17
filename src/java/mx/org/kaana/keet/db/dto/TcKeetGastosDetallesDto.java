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
@Table(name="tc_keet_gastos_detalles")
public class TcKeetGastosDetallesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="codigo")
  private String codigo;
  @Column (name="unidad_medida")
  private String unidadMedida;
  @Column (name="costo")
  private Double costo;
  @Column (name="nombre")
  private String nombre;
  @Column (name="importe")
  private Double importe;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="id_gasto")
  private Long idGasto;
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
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_gasto_detalle")
  private Long idGastoDetalle;

  public TcKeetGastosDetallesDto() {
    this(new Long(-1L));
  }

  public TcKeetGastosDetallesDto(Long key) {
    this(null, null, null, null, null, null, null, null, null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKeetGastosDetallesDto(String codigo, String unidadMedida, Double costo, String nombre, Double importe, Long idGasto, Double iva, Double impuestos, Double subTotal, Double cantidad, Long idArticulo, Long idGastoDetalle) {
    setCodigo(codigo);
    setUnidadMedida(unidadMedida);
    setCosto(costo);
    setNombre(nombre);
    setImporte(importe);
    setRegistro(LocalDateTime.now());
    setIdGasto(idGasto);
    setIva(iva);
    setImpuestos(impuestos);
    setSubTotal(subTotal);
    setCantidad(cantidad);
    setIdArticulo(idArticulo);
    setIdGastoDetalle(idGastoDetalle);
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

  public void setIdGasto(Long idGasto) {
    this.idGasto = idGasto;
  }

  public Long getIdGasto() {
    return idGasto;
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

  public void setIdGastoDetalle(Long idGastoDetalle) {
    this.idGastoDetalle = idGastoDetalle;
  }

  public Long getIdGastoDetalle() {
    return idGastoDetalle;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdGastoDetalle();
  }

  @Override
  public void setKey(Long key) {
  	this.idGastoDetalle = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getUnidadMedida());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCosto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdGasto());
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
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdGastoDetalle());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("codigo", getCodigo());
		regresar.put("unidadMedida", getUnidadMedida());
		regresar.put("costo", getCosto());
		regresar.put("nombre", getNombre());
		regresar.put("importe", getImporte());
		regresar.put("registro", getRegistro());
		regresar.put("idGasto", getIdGasto());
		regresar.put("iva", getIva());
		regresar.put("impuestos", getImpuestos());
		regresar.put("subTotal", getSubTotal());
		regresar.put("cantidad", getCantidad());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("idGastoDetalle", getIdGastoDetalle());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
			getCodigo(), getUnidadMedida(), getCosto(), getNombre(), getImporte(), getRegistro(), getIdGasto(), getIva(), getImpuestos(), getSubTotal(), getCantidad(), getIdArticulo(), getIdGastoDetalle()
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
    regresar.append("idGastoDetalle~");
    regresar.append(getIdGastoDetalle());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdGastoDetalle());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetGastosDetallesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdGastoDetalle()!= null && getIdGastoDetalle()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetGastosDetallesDto other = (TcKeetGastosDetallesDto) obj;
    if (getIdGastoDetalle() != other.idGastoDetalle && (getIdGastoDetalle() == null || !getIdGastoDetalle().equals(other.idGastoDetalle))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdGastoDetalle() != null ? getIdGastoDetalle().hashCode() : 0);
    return hash;
  }
}