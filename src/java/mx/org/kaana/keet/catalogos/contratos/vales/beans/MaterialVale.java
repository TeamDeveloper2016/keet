package mx.org.kaana.keet.catalogos.contratos.vales.beans;

import java.io.Serializable;
import mx.org.kaana.libs.formato.Numero;

public class MaterialVale implements Serializable{
	
	private static final long serialVersionUID= 7151119507704920758L;
	private Long idKey;
	protected Long idMaterial;
	private Long nivel;
	private String clave;
	private Long idEmpaqueUnidadMedida;
	private Long idEstacionEstatus;
	private Double costo;	
	private String codigo;
	private String nombre;
	private Long totalDetalle;
	private Long idArticulo;
	private Double cantidad;
	private Double precio;
	private String unidadMedida;
	private Double stock;

	public MaterialVale() {
		this(-1L, -1L, -1L, null, -1L, -1L, 0D, null, null, -1L, -1L, 0D, 0D, null, 0D);
	}

	public MaterialVale(Long idKey, Long idMaterial, Long nivel, String clave, Long idEmpaqueUnidadMedida, Long idEstacionEstatus, Double costo, String codigo, String nombre, Long totalDetalle, Long idArticulo, Double cantidad, Double precio, String unidadMedida, Double stock) {
		this.idKey                = idKey;
		this.idMaterial           = idMaterial;
		this.nivel                = nivel;
		this.clave                = clave;
		this.idEmpaqueUnidadMedida= idEmpaqueUnidadMedida;
		this.idEstacionEstatus    = idEstacionEstatus;
		this.costo                = costo;		
		this.codigo               = codigo;
		this.nombre               = nombre;
		this.totalDetalle         = totalDetalle;
		this.idArticulo           = idArticulo;
		this.cantidad             = cantidad;
		this.precio               = precio;
		this.stock                = stock;
	}

	public Long getIdKey() {
		return idKey;
	}

	public void setIdKey(Long idKey) {
		this.idKey = idKey;
	}

	public Long getIdMaterial() {
		return idMaterial;
	}

	public void setIdMaterial(Long idMaterial) {
		this.idMaterial = idMaterial;
	}

	public Long getNivel() {
		return nivel;
	}

	public void setNivel(Long nivel) {
		this.nivel = nivel;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public Long getIdEmpaqueUnidadMedida() {
		return idEmpaqueUnidadMedida;
	}

	public void setIdEmpaqueUnidadMedida(Long idEmpaqueUnidadMedida) {
		this.idEmpaqueUnidadMedida = idEmpaqueUnidadMedida;
	}

	public Long getIdEstacionEstatus() {
		return idEstacionEstatus;
	}

	public void setIdEstacionEstatus(Long idEstacionEstatus) {
		this.idEstacionEstatus = idEstacionEstatus;
	}

	public Double getCosto() {
		return costo;
	}

	public void setCosto(Double costo) {
		this.costo = costo;
	}	

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Long getTotalDetalle() {
		return totalDetalle;
	}

	public void setTotalDetalle(Long totalDetalle) {
		this.totalDetalle = totalDetalle;
	}
	
	public boolean isRegistrado(){
		return this.totalDetalle!= null &&  this.totalDetalle > 0L;
	}

	public Long getIdArticulo() {
		return idArticulo;
	}

	public void setIdArticulo(Long idArticulo) {
		this.idArticulo = idArticulo;
	}	

	public Double getCantidad() {
		return cantidad;
	}
	
	public String getCantidadDecimales() {
		return Numero.formatear(Numero.NUMERO_CON_DECIMALES, this.cantidad);
	}

	public void setCantidad(Double cantidad) {
		this.cantidad = cantidad;
	}	

	public Double getPrecio() {
		return precio;
	}

	public void setPrecio(Double precio) {
		this.precio = precio;
	}	
	
	public String getCostoDecimales() {
		return Numero.formatear(Numero.NUMERO_SAT_DECIMALES, this.costo);
	}

	public String getUnidadMedida() {
		return unidadMedida;
	}

	public void setUnidadMedida(String unidadMedida) {
		this.unidadMedida = unidadMedida;
	}	

	public Double getStock() {
		return stock;
	}

	public void setStock(Double stock) {
		this.stock = stock;
	}
	
	@Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } // if
    if (getClass() != obj.getClass()) {
      return false;
    } // if
    final MaterialVale other = (MaterialVale) obj;
    if (getIdArticulo() != other.idArticulo && (getIdArticulo() == null || !getIdArticulo().equals(other.idArticulo))) {
      return false;
    } // if
    return true;
  } // equals

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdArticulo()!= null ? getIdArticulo().hashCode() : 0);
    return hash;
  } // hashCode
}