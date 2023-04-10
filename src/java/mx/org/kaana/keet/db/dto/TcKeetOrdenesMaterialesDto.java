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
@Table(name="tc_keet_ordenes_materiales")
public class TcKeetOrdenesMaterialesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="nombre")
  private String nombre;
  @Column (name="precio_unitario")
  private Double precioUnitario;
  @Column (name="codigo")
  private String codigo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_orden_compra")
  private Long idOrdenCompra;
  @Column (name="id_contrato_lote")
  private Long idContratoLote;
  @Column (name="cantidad")
  private Double cantidad;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_orden_material")
  private Long idOrdenMaterial;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetOrdenesMaterialesDto() {
    this(new Long(-1L));
  }

  public TcKeetOrdenesMaterialesDto(Long key) {
    this(null, null, null, null, null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKeetOrdenesMaterialesDto(String nombre, Double precioUnitario, String codigo, Long idUsuario, Long idOrdenCompra, Long idContratoLote, Double cantidad, Long idOrdenMaterial, Long idArticulo) {
    setNombre(nombre);
    setPrecioUnitario(precioUnitario);
    setCodigo(codigo);
    setIdUsuario(idUsuario);
    setIdOrdenCompra(idOrdenCompra);
    setIdContratoLote(idContratoLote);
    setCantidad(cantidad);
    setIdOrdenMaterial(idOrdenMaterial);
    setIdArticulo(idArticulo);
    setRegistro(LocalDateTime.now());
  }
	
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setPrecioUnitario(Double precioUnitario) {
    this.precioUnitario = precioUnitario;
  }

  public Double getPrecioUnitario() {
    return precioUnitario;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdOrdenCompra(Long idOrdenCompra) {
    this.idOrdenCompra = idOrdenCompra;
  }

  public Long getIdOrdenCompra() {
    return idOrdenCompra;
  }

  public void setIdContratoLote(Long idContratoLote) {
    this.idContratoLote = idContratoLote;
  }

  public Long getIdContratoLote() {
    return idContratoLote;
  }

  public void setCantidad(Double cantidad) {
    this.cantidad = cantidad;
  }

  public Double getCantidad() {
    return cantidad;
  }

  public void setIdOrdenMaterial(Long idOrdenMaterial) {
    this.idOrdenMaterial = idOrdenMaterial;
  }

  public Long getIdOrdenMaterial() {
    return idOrdenMaterial;
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
  	return getIdOrdenMaterial();
  }

  @Override
  public void setKey(Long key) {
  	this.idOrdenMaterial = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPrecioUnitario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdOrdenCompra());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdOrdenMaterial());
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
		regresar.put("nombre", getNombre());
		regresar.put("precioUnitario", getPrecioUnitario());
		regresar.put("codigo", getCodigo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idOrdenCompra", getIdOrdenCompra());
		regresar.put("idContratoLote", getIdContratoLote());
		regresar.put("cantidad", getCantidad());
		regresar.put("idOrdenMaterial", getIdOrdenMaterial());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getNombre(), getPrecioUnitario(), getCodigo(), getIdUsuario(), getIdOrdenCompra(), getIdContratoLote(), getCantidad(), getIdOrdenMaterial(), getIdArticulo(), getRegistro()
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
    regresar.append("idOrdenMaterial~");
    regresar.append(getIdOrdenMaterial());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdOrdenMaterial());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetOrdenesMaterialesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdOrdenMaterial()!= null && getIdOrdenMaterial()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetOrdenesMaterialesDto other = (TcKeetOrdenesMaterialesDto) obj;
    if (getIdOrdenMaterial() != other.idOrdenMaterial && (getIdOrdenMaterial() == null || !getIdOrdenMaterial().equals(other.idOrdenMaterial))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdOrdenMaterial() != null ? getIdOrdenMaterial().hashCode() : 0);
    return hash;
  }

}


