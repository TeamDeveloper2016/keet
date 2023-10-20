package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
@Table(name="tc_keet_boletas_detalles")
public class TcKeetBoletasDetallesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="codigo")
  private String codigo;
  @Column (name="precio")
  private Double precio;
  @Column (name="costo")
  private Double costo;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="id_boleta")
  private Long idBoleta;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Column (name="nombre")
  private String nombre;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_boleta_detalle")
  private Long idBoletaDetalle;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetBoletasDetallesDto() {
    this(new Long(-1L));
  }

  public TcKeetBoletasDetallesDto(Long key) {
    this(null, null, null, null, null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKeetBoletasDetallesDto(String codigo, Double precio, Double costo, Double cantidad, Long idBoleta, Long idArticulo, String nombre, Long idBoletaDetalle) {
    setCodigo(codigo);
    setPrecio(precio);
    setCosto(costo);
    setCantidad(cantidad);
    setIdBoleta(idBoleta);
    setIdArticulo(idArticulo);
    setNombre(nombre);
    setIdBoletaDetalle(idBoletaDetalle);
    setRegistro(LocalDateTime.now());
  }
	
  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setPrecio(Double precio) {
    this.precio = precio;
  }

  public Double getPrecio() {
    return precio;
  }

  public void setCosto(Double costo) {
    this.costo = costo;
  }

  public Double getCosto() {
    return costo;
  }

  public void setCantidad(Double cantidad) {
    this.cantidad = cantidad;
  }

  public Double getCantidad() {
    return cantidad;
  }

  public void setIdBoleta(Long idBoleta) {
    this.idBoleta = idBoleta;
  }

  public Long getIdBoleta() {
    return idBoleta;
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

  public void setIdBoletaDetalle(Long idBoletaDetalle) {
    this.idBoletaDetalle = idBoletaDetalle;
  }

  public Long getIdBoletaDetalle() {
    return idBoletaDetalle;
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
  	return getIdBoletaDetalle();
  }

  @Override
  public void setKey(Long key) {
  	this.idBoletaDetalle = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPrecio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCosto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdBoleta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdBoletaDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("codigo", getCodigo());
		regresar.put("precio", getPrecio());
		regresar.put("costo", getCosto());
		regresar.put("cantidad", getCantidad());
		regresar.put("idBoleta", getIdBoleta());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("nombre", getNombre());
		regresar.put("idBoletaDetalle", getIdBoletaDetalle());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getCodigo(), getPrecio(), getCosto(), getCantidad(), getIdBoleta(), getIdArticulo(), getNombre(), getIdBoletaDetalle(), getRegistro()
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
    regresar.append("idBoletaDetalle~");
    regresar.append(getIdBoletaDetalle());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdBoletaDetalle());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetBoletasDetallesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdBoletaDetalle()!= null && getIdBoletaDetalle()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetBoletasDetallesDto other = (TcKeetBoletasDetallesDto) obj;
    if (getIdBoletaDetalle() != other.idBoletaDetalle && (getIdBoletaDetalle() == null || !getIdBoletaDetalle().equals(other.idBoletaDetalle))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdBoletaDetalle() != null ? getIdBoletaDetalle().hashCode() : 0);
    return hash;
  }

}


