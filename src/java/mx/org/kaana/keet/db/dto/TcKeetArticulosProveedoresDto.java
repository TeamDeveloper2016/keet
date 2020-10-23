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
@Table(name="tc_keet_articulos_proveedores")
public class TcKeetArticulosProveedoresDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_proveedor")
  private Long idProveedor;
  @Column (name="precio_lista")
  private Double precioLista;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_articulo_proveedor")
  private Long idArticuloProveedor;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Column (name="precio_especial")
  private Double precioEspecial;
  @Column (name="precio_base")
  private Double precioBase;
  @Column (name="actualizado")
  private LocalDateTime actualizado;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetArticulosProveedoresDto() {
    this(new Long(-1L));
  }

  public TcKeetArticulosProveedoresDto(Long key) {
    this(-1L, 0D, null, new Long(-1L), -1L, 0D, 0D, LocalDateTime.now());
    setKey(key);
  }

  public TcKeetArticulosProveedoresDto(Long idProveedor, Double precioLista, Long idUsuario, Long idArticuloProveedor, Long idArticulo, Double precioEspecial, Double precioBase, LocalDateTime actualizado) {
    setIdProveedor(idProveedor);
    setPrecioLista(precioLista);
    setIdUsuario(idUsuario);
    setIdArticuloProveedor(idArticuloProveedor);
    setIdArticulo(idArticulo);
    setPrecioEspecial(precioEspecial);
    setPrecioBase(precioBase);
    setActualizado(actualizado);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdProveedor(Long idProveedor) {
    this.idProveedor = idProveedor;
  }

  public Long getIdProveedor() {
    return idProveedor;
  }

  public void setPrecioLista(Double precioLista) {
    this.precioLista = precioLista;
  }

  public Double getPrecioLista() {
    return precioLista;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdArticuloProveedor(Long idArticuloProveedor) {
    this.idArticuloProveedor = idArticuloProveedor;
  }

  public Long getIdArticuloProveedor() {
    return idArticuloProveedor;
  }

  public void setIdArticulo(Long idArticulo) {
    this.idArticulo = idArticulo;
  }

  public Long getIdArticulo() {
    return idArticulo;
  }

  public void setPrecioEspecial(Double precioEspecial) {
    this.precioEspecial = precioEspecial;
  }

  public Double getPrecioEspecial() {
    return precioEspecial;
  }

  public void setPrecioBase(Double precioBase) {
    this.precioBase = precioBase;
  }

  public Double getPrecioBase() {
    return precioBase;
  }

  public void setActualizado(LocalDateTime actualizado) {
    this.actualizado = actualizado;
  }

  public LocalDateTime getActualizado() {
    return actualizado;
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
  	return getIdArticuloProveedor();
  }

  @Override
  public void setKey(Long key) {
  	this.idArticuloProveedor = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPrecioLista());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticuloProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPrecioEspecial());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPrecioBase());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getActualizado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idProveedor", getIdProveedor());
		regresar.put("precioLista", getPrecioLista());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idArticuloProveedor", getIdArticuloProveedor());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("precioEspecial", getPrecioEspecial());
		regresar.put("precioBase", getPrecioBase());
		regresar.put("actualizado", getActualizado());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdProveedor(), getPrecioLista(), getIdUsuario(), getIdArticuloProveedor(), getIdArticulo(), getPrecioEspecial(), getPrecioBase(), getActualizado(), getRegistro()
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
    regresar.append("idArticuloProveedor~");
    regresar.append(getIdArticuloProveedor());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdArticuloProveedor());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetArticulosProveedoresDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdArticuloProveedor()!= null && getIdArticuloProveedor()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetArticulosProveedoresDto other = (TcKeetArticulosProveedoresDto) obj;
    if (getIdArticuloProveedor() != other.idArticuloProveedor && (getIdArticuloProveedor() == null || !getIdArticuloProveedor().equals(other.idArticuloProveedor))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdArticuloProveedor() != null ? getIdArticuloProveedor().hashCode() : 0);
    return hash;
  }

}


