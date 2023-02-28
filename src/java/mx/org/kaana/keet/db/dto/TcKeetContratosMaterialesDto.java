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
@Table(name="tc_keet_contratos_materiales")
public class TcKeetContratosMaterialesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="codigo")
  private String codigo;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_material")
  private Long idContratoMaterial;
  @Column (name="contrato")
  private String contrato;
  @Column (name="nombre")
  private String nombre;
  @Column (name="expansion")
  private Double expansion;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="precio_unitario")
  private Double precioUnitario;
  @Column (name="prototipo")
  private String prototipo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_contrato")
  private Long idContrato;
  @Column (name="id_prototipo")
  private Long idPrototipo;
  @Column (name="id_archivo")
  private Long idArchivo;
  @Column (name="cantidad")
  private Double cantidad;
  @Column (name="id_articulo")
  private Long idArticulo;

  public TcKeetContratosMaterialesDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosMaterialesDto(Long key) {
    this(null, new Long(-1L), null, null, null, null, null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKeetContratosMaterialesDto(String codigo, Long idContratoMaterial, String contrato, String nombre, Double expansion, Double precioUnitario, String prototipo, Long idUsuario, Long idContrato, Long idPrototipo, Long idArchivo, Double cantidad, Long idArticulo) {
    setCodigo(codigo);
    setIdContratoMaterial(idContratoMaterial);
    setContrato(contrato);
    setNombre(nombre);
    setExpansion(expansion);
    setRegistro(LocalDateTime.now());
    setPrecioUnitario(precioUnitario);
    setPrototipo(prototipo);
    setIdUsuario(idUsuario);
    setIdContrato(idContrato);
    setIdPrototipo(idPrototipo);
    setIdArchivo(idArchivo);
    setCantidad(cantidad);
    setIdArticulo(idArticulo);
  }
	
  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setIdContratoMaterial(Long idContratoMaterial) {
    this.idContratoMaterial = idContratoMaterial;
  }

  public Long getIdContratoMaterial() {
    return idContratoMaterial;
  }

  public void setContrato(String contrato) {
    this.contrato = contrato;
  }

  public String getContrato() {
    return contrato;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setExpansion(Double expansion) {
    this.expansion = expansion;
  }

  public Double getExpansion() {
    return expansion;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  public void setPrecioUnitario(Double precioUnitario) {
    this.precioUnitario = precioUnitario;
  }

  public Double getPrecioUnitario() {
    return precioUnitario;
  }

  public void setPrototipo(String prototipo) {
    this.prototipo = prototipo;
  }

  public String getPrototipo() {
    return prototipo;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdContrato(Long idContrato) {
    this.idContrato = idContrato;
  }

  public Long getIdContrato() {
    return idContrato;
  }

  public void setIdPrototipo(Long idPrototipo) {
    this.idPrototipo = idPrototipo;
  }

  public Long getIdPrototipo() {
    return idPrototipo;
  }

  public void setIdArchivo(Long idArchivo) {
    this.idArchivo = idArchivo;
  }

  public Long getIdArchivo() {
    return idArchivo;
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
  	return getIdContratoMaterial();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoMaterial = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getCodigo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoMaterial());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getExpansion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPrecioUnitario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPrototipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrototipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdArchivo());
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
		regresar.put("codigo", getCodigo());
		regresar.put("idContratoMaterial", getIdContratoMaterial());
		regresar.put("contrato", getContrato());
		regresar.put("nombre", getNombre());
		regresar.put("expansion", getExpansion());
		regresar.put("registro", getRegistro());
		regresar.put("precioUnitario", getPrecioUnitario());
		regresar.put("prototipo", getPrototipo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idContrato", getIdContrato());
		regresar.put("idPrototipo", getIdPrototipo());
		regresar.put("idArchivo", getIdArchivo());
		regresar.put("cantidad", getCantidad());
		regresar.put("idArticulo", getIdArticulo());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getCodigo(), getIdContratoMaterial(), getContrato(), getNombre(), getExpansion(), getRegistro(), getPrecioUnitario(), getPrototipo(), getIdUsuario(), getIdContrato(), getIdPrototipo(), getIdArchivo(), getCantidad(), getIdArticulo()
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
    regresar.append("idContratoMaterial~");
    regresar.append(getIdContratoMaterial());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoMaterial());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosMaterialesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoMaterial()!= null && getIdContratoMaterial()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosMaterialesDto other = (TcKeetContratosMaterialesDto) obj;
    if (getIdContratoMaterial() != other.idContratoMaterial && (getIdContratoMaterial() == null || !getIdContratoMaterial().equals(other.idContratoMaterial))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoMaterial() != null ? getIdContratoMaterial().hashCode() : 0);
    return hash;
  }

}


