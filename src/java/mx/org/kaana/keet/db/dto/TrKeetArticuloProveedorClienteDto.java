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
@Table(name="tr_keet_articulo_proveedor_cliente")
public class TrKeetArticuloProveedorClienteDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="precio_convenio")
  private Double precioConvenio;
  @Column (name="id_proveedor")
  private Long idProveedor;
  @Column (name="id_cliente")
  private Long idCliente;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_articulo_proveedor_cliente")
  private Long idArticuloProveedorCliente;
  @Column (name="precio_anterior")
  private Double precioAnterior;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Column (name="actualizado")
  private LocalDateTime actualizado;
  @Column (name="registro")
  private LocalDateTime registro;

  public TrKeetArticuloProveedorClienteDto() {
    this(new Long(-1L));
  }

  public TrKeetArticuloProveedorClienteDto(Long key) {
    this(null, null, null, null, new Long(-1L), null, null, LocalDateTime.now());
    setKey(key);
  }

  public TrKeetArticuloProveedorClienteDto(Double precioConvenio, Long idProveedor, Long idCliente, Long idUsuario, Long idArticuloProveedorCliente, Double precioAnterior, Long idArticulo, LocalDateTime actualizado) {
    setPrecioConvenio(precioConvenio);
    setIdProveedor(idProveedor);
    setIdCliente(idCliente);
    setIdUsuario(idUsuario);
    setIdArticuloProveedorCliente(idArticuloProveedorCliente);
    setPrecioAnterior(precioAnterior);
    setIdArticulo(idArticulo);
    setActualizado(actualizado);
    setRegistro(LocalDateTime.now());
  }
	
  public void setPrecioConvenio(Double precioConvenio) {
    this.precioConvenio = precioConvenio;
  }

  public Double getPrecioConvenio() {
    return precioConvenio;
  }

  public void setIdProveedor(Long idProveedor) {
    this.idProveedor = idProveedor;
  }

  public Long getIdProveedor() {
    return idProveedor;
  }

  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public Long getIdCliente() {
    return idCliente;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdArticuloProveedorCliente(Long idArticuloProveedorCliente) {
    this.idArticuloProveedorCliente = idArticuloProveedorCliente;
  }

  public Long getIdArticuloProveedorCliente() {
    return idArticuloProveedorCliente;
  }

  public void setPrecioAnterior(Double precioAnterior) {
    this.precioAnterior = precioAnterior;
  }

  public Double getPrecioAnterior() {
    return precioAnterior;
  }

  public void setIdArticulo(Long idArticulo) {
    this.idArticulo = idArticulo;
  }

  public Long getIdArticulo() {
    return idArticulo;
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
  	return getIdArticuloProveedorCliente();
  }

  @Override
  public void setKey(Long key) {
  	this.idArticuloProveedorCliente = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getPrecioConvenio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCliente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticuloProveedorCliente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPrecioAnterior());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
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
		regresar.put("precioConvenio", getPrecioConvenio());
		regresar.put("idProveedor", getIdProveedor());
		regresar.put("idCliente", getIdCliente());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idArticuloProveedorCliente", getIdArticuloProveedorCliente());
		regresar.put("precioAnterior", getPrecioAnterior());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("actualizado", getActualizado());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getPrecioConvenio(), getIdProveedor(), getIdCliente(), getIdUsuario(), getIdArticuloProveedorCliente(), getPrecioAnterior(), getIdArticulo(), getActualizado(), getRegistro()
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
    regresar.append("idArticuloProveedorCliente~");
    regresar.append(getIdArticuloProveedorCliente());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdArticuloProveedorCliente());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TrKeetArticuloProveedorClienteDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdArticuloProveedorCliente()!= null && getIdArticuloProveedorCliente()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TrKeetArticuloProveedorClienteDto other = (TrKeetArticuloProveedorClienteDto) obj;
    if (getIdArticuloProveedorCliente() != other.idArticuloProveedorCliente && (getIdArticuloProveedorCliente() == null || !getIdArticuloProveedorCliente().equals(other.idArticuloProveedorCliente))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdArticuloProveedorCliente() != null ? getIdArticuloProveedorCliente().hashCode() : 0);
    return hash;
  }

}


