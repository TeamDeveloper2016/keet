package mx.org.kaana.sakbe.db.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;
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
@Table(name="tc_sakbe_maquinarias")
public class TcSakbeMaquinariasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="clave")
  private String clave;
  @Column (name="original")
  private Long original;
  @Column (name="pedimento")
  private String pedimento;
  @Column (name="id_tipo_maquinaria")
  private Long idTipoMaquinaria;
  @Column (name="fecha_factura")
  private LocalDate fechaFactura;
  @Column (name="nombre")
  private String nombre;
  @Column (name="id_tipo_combustible")
  private Long idTipoCombustible;
  @Column (name="poliza")
  private String poliza;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private LocalDateTime registro;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_maquinaria")
  private Long idMaquinaria;
  @Column (name="marca")
  private String marca;
  @Column (name="total")
  private Double total;
  @Column (name="factura")
  private String factura;
  @Column (name="facturado")
  private String facturado;
  @Column (name="rendimiento")
  private Double rendimiento;
  @Column (name="iva")
  private Double iva;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="subtotal")
  private Double subtotal;
  @Column (name="litros")
  private Double litros;
  @Column (name="serie")
  private String serie;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="inventario")
  private String inventario;

  public TcSakbeMaquinariasDto() {
    this(new Long(-1L));
  }

  public TcSakbeMaquinariasDto(Long key) {
    this(null, null, null, null, LocalDate.now(), null, null, null, null, new Long(-1L), null, null, null, null, null, null, null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcSakbeMaquinariasDto(String clave, Long original, String pedimento, Long idTipoMaquinaria, LocalDate fechaFactura, String nombre, Long idTipoCombustible, String poliza, Long ejercicio, Long idMaquinaria, String marca, Double total, String factura, String facturado, Double rendimiento, Double iva, Long idUsuario, Double subtotal, Double litros, String serie, String observaciones, Long idEmpresa, String inventario) {
    setClave(clave);
    setOriginal(original);
    setPedimento(pedimento);
    setIdTipoMaquinaria(idTipoMaquinaria);
    setFechaFactura(fechaFactura);
    setNombre(nombre);
    setIdTipoCombustible(idTipoCombustible);
    setPoliza(poliza);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
    setIdMaquinaria(idMaquinaria);
    setMarca(marca);
    setTotal(total);
    setFactura(factura);
    setFacturado(facturado);
    setRendimiento(rendimiento);
    setIva(iva);
    setIdUsuario(idUsuario);
    setSubtotal(subtotal);
    setLitros(litros);
    setSerie(serie);
    setObservaciones(observaciones);
    setIdEmpresa(idEmpresa);
    setInventario(inventario);
  }
	
  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getClave() {
    return clave;
  }

  public void setOriginal(Long original) {
    this.original = original;
  }

  public Long getOriginal() {
    return original;
  }

  public void setPedimento(String pedimento) {
    this.pedimento = pedimento;
  }

  public String getPedimento() {
    return pedimento;
  }

  public void setIdTipoMaquinaria(Long idTipoMaquinaria) {
    this.idTipoMaquinaria = idTipoMaquinaria;
  }

  public Long getIdTipoMaquinaria() {
    return idTipoMaquinaria;
  }

  public void setFechaFactura(LocalDate fechaFactura) {
    this.fechaFactura = fechaFactura;
  }

  public LocalDate getFechaFactura() {
    return fechaFactura;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setIdTipoCombustible(Long idTipoCombustible) {
    this.idTipoCombustible = idTipoCombustible;
  }

  public Long getIdTipoCombustible() {
    return idTipoCombustible;
  }

  public void setPoliza(String poliza) {
    this.poliza = poliza;
  }

  public String getPoliza() {
    return poliza;
  }

  public void setEjercicio(Long ejercicio) {
    this.ejercicio = ejercicio;
  }

  public Long getEjercicio() {
    return ejercicio;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  public void setIdMaquinaria(Long idMaquinaria) {
    this.idMaquinaria = idMaquinaria;
  }

  public Long getIdMaquinaria() {
    return idMaquinaria;
  }

  public void setMarca(String marca) {
    this.marca = marca;
  }

  public String getMarca() {
    return marca;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getTotal() {
    return total;
  }

  public void setFactura(String factura) {
    this.factura = factura;
  }

  public String getFactura() {
    return factura;
  }

  public void setFacturado(String facturado) {
    this.facturado = facturado;
  }

  public String getFacturado() {
    return facturado;
  }

  public void setRendimiento(Double rendimiento) {
    this.rendimiento = rendimiento;
  }

  public Double getRendimiento() {
    return rendimiento;
  }

  public void setIva(Double iva) {
    this.iva = iva;
  }

  public Double getIva() {
    return iva;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setSubtotal(Double subtotal) {
    this.subtotal = subtotal;
  }

  public Double getSubtotal() {
    return subtotal;
  }

  public void setLitros(Double litros) {
    this.litros = litros;
  }

  public Double getLitros() {
    return litros;
  }

  public void setSerie(String serie) {
    this.serie = serie;
  }

  public String getSerie() {
    return serie;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdEmpresa(Long idEmpresa) {
    this.idEmpresa = idEmpresa;
  }

  public Long getIdEmpresa() {
    return idEmpresa;
  }

  public void setInventario(String inventario) {
    this.inventario = inventario;
  }

  public String getInventario() {
    return inventario;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdMaquinaria();
  }

  @Override
  public void setKey(Long key) {
  	this.idMaquinaria = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOriginal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPedimento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoMaquinaria());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaFactura());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoCombustible());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPoliza());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdMaquinaria());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getMarca());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFactura());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFacturado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRendimiento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIva());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSubtotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLitros());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSerie());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getInventario());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("clave", getClave());
		regresar.put("original", getOriginal());
		regresar.put("pedimento", getPedimento());
		regresar.put("idTipoMaquinaria", getIdTipoMaquinaria());
		regresar.put("fechaFactura", getFechaFactura());
		regresar.put("nombre", getNombre());
		regresar.put("idTipoCombustible", getIdTipoCombustible());
		regresar.put("poliza", getPoliza());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("idMaquinaria", getIdMaquinaria());
		regresar.put("marca", getMarca());
		regresar.put("total", getTotal());
		regresar.put("factura", getFactura());
		regresar.put("facturado", getFacturado());
		regresar.put("rendimiento", getRendimiento());
		regresar.put("iva", getIva());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("subtotal", getSubtotal());
		regresar.put("litros", getLitros());
		regresar.put("serie", getSerie());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("inventario", getInventario());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getClave(), getOriginal(), getPedimento(), getIdTipoMaquinaria(), getFechaFactura(), getNombre(), getIdTipoCombustible(), getPoliza(), getEjercicio(), getRegistro(), getIdMaquinaria(), getMarca(), getTotal(), getFactura(), getFacturado(), getRendimiento(), getIva(), getIdUsuario(), getSubtotal(), getLitros(), getSerie(), getObservaciones(), getIdEmpresa(), getInventario()
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
    regresar.append("idMaquinaria~");
    regresar.append(getIdMaquinaria());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdMaquinaria());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeMaquinariasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdMaquinaria()!= null && getIdMaquinaria()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcSakbeMaquinariasDto other = (TcSakbeMaquinariasDto) obj;
    if (getIdMaquinaria() != other.idMaquinaria && (getIdMaquinaria() == null || !getIdMaquinaria().equals(other.idMaquinaria))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdMaquinaria() != null ? getIdMaquinaria().hashCode() : 0);
    return hash;
  }

}

