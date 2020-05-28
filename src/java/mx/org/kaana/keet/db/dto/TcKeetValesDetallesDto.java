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
@Table(name="tc_keet_vales_detalles")
public class TcKeetValesDetallesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="surtido")
  private Double surtido;
  @Column (name="costo")
  private Double costo;
  @Column (name="entregrado")
  private LocalDateTime entregrado;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="id_vale")
  private Long idVale;
  @Column (name="id_articulo")
  private Long idArticulo;
  @Column (name="id_material")
  private Long idMaterial;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_vale_detalle")
  private Long idValeDetalle;
  @Column (name="diferencia")
  private Double diferencia;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetValesDetallesDto() {
    this(new Long(-1L));
  }

  public TcKeetValesDetallesDto(Long key) {
    this(null, null, LocalDateTime.now(), null, null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKeetValesDetallesDto(Double surtido, Double costo, LocalDateTime entregrado, Double cantidad, Long idVale, Long idArticulo, Long idMaterial, Long idValeDetalle, Double diferencia) {
    setSurtido(surtido);
    setCosto(costo);
    setEntregrado(entregrado);
    setCantidad(cantidad);
    setIdVale(idVale);
    setIdArticulo(idArticulo);
    setIdMaterial(idMaterial);
    setIdValeDetalle(idValeDetalle);
    setDiferencia(diferencia);
    setRegistro(LocalDateTime.now());
  }
	
  public void setSurtido(Double surtido) {
    this.surtido = surtido;
  }

  public Double getSurtido() {
    return surtido;
  }

  public void setCosto(Double costo) {
    this.costo = costo;
  }

  public Double getCosto() {
    return costo;
  }

  public void setEntregrado(LocalDateTime entregrado) {
    this.entregrado = entregrado;
  }

  public LocalDateTime getEntregrado() {
    return entregrado;
  }

  public void setCantidad(Double cantidad) {
    this.cantidad = cantidad;
  }

  public Double getCantidad() {
    return cantidad;
  }

  public void setIdVale(Long idVale) {
    this.idVale = idVale;
  }

  public Long getIdVale() {
    return idVale;
  }

  public void setIdArticulo(Long idArticulo) {
    this.idArticulo = idArticulo;
  }

  public Long getIdArticulo() {
    return idArticulo;
  }

  public void setIdMaterial(Long idMaterial) {
    this.idMaterial = idMaterial;
  }

  public Long getIdMaterial() {
    return idMaterial;
  }

  public void setIdValeDetalle(Long idValeDetalle) {
    this.idValeDetalle = idValeDetalle;
  }

  public Long getIdValeDetalle() {
    return idValeDetalle;
  }

  public void setDiferencia(Double diferencia) {
    this.diferencia = diferencia;
  }

  public Double getDiferencia() {
    return diferencia;
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
  	return getIdValeDetalle();
  }

  @Override
  public void setKey(Long key) {
  	this.idValeDetalle = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getSurtido());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCosto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEntregrado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCantidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdVale());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArticulo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdMaterial());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdValeDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDiferencia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("surtido", getSurtido());
		regresar.put("costo", getCosto());
		regresar.put("entregrado", getEntregrado());
		regresar.put("cantidad", getCantidad());
		regresar.put("idVale", getIdVale());
		regresar.put("idArticulo", getIdArticulo());
		regresar.put("idMaterial", getIdMaterial());
		regresar.put("idValeDetalle", getIdValeDetalle());
		regresar.put("diferencia", getDiferencia());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getSurtido(), getCosto(), getEntregrado(), getCantidad(), getIdVale(), getIdArticulo(), getIdMaterial(), getIdValeDetalle(), getDiferencia(), getRegistro()
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
    regresar.append("idValeDetalle~");
    regresar.append(getIdValeDetalle());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdValeDetalle());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetValesDetallesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdValeDetalle()!= null && getIdValeDetalle()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetValesDetallesDto other = (TcKeetValesDetallesDto) obj;
    if (getIdValeDetalle() != other.idValeDetalle && (getIdValeDetalle() == null || !getIdValeDetalle().equals(other.idValeDetalle))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdValeDetalle() != null ? getIdValeDetalle().hashCode() : 0);
    return hash;
  }
}