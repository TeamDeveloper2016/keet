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
  @Column (name="motor")
  private String motor;
  @Column (name="pro_en_tarjeta")
  private String proEnTarjeta;
  @Column (name="color")
  private String color;
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
  @Column (name="comprado")
  private String comprado;
  @Column (name="marca")
  private String marca;
  @Column (name="total")
  private Double total;
  @Column (name="factura")
  private String factura;
  @Column (name="pro_real")
  private String proReal;
  @Column (name="iva")
  private Double iva;
  @Column (name="litros")
  private Double litros;
  @Column (name="pro_en_factura")
  private String proEnFactura;
  @Column (name="constancia")
  private String constancia;
  @Column (name="tarjeta")
  private String tarjeta;
  @Column (name="id_maquinaria_estatus")
  private Long idMaquinariaEstatus;
  @Column (name="placa")
  private String placa;
  @Column (name="id_original")
  private Long idOriginal;
  @Column (name="clave")
  private String clave;
  @Column (name="modelo")
  private Long modelo;
  @Column (name="tipo")
  private String tipo;
  @Column (name="registro")
  private LocalDateTime registro;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_maquinaria")
  private Long idMaquinaria;
  @Column (name="id_constancia")
  private Long idConstancia;
  @Column (name="facturado")
  private String facturado;
  @Column (name="rendimiento")
  private Double rendimiento;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="subtotal")
  private Double subtotal;
  @Column (name="entidad")
  private String entidad;
  @Column (name="serie")
  private String serie;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="ultima_tarjeta")
  private String ultimaTarjeta;
  @Column (name="responsable")
  private String responsable;

  public TcSakbeMaquinariasDto() {
    this(new Long(-1L));
  }

  public TcSakbeMaquinariasDto(Long key) {
    this(null, null, null, null, null, LocalDate.now(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, new Long(-1L), null, null, null, null, null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcSakbeMaquinariasDto(String motor, String proEnTarjeta, String color, String pedimento, Long idTipoMaquinaria, LocalDate fechaFactura, String nombre, Long idTipoCombustible, String poliza, String comprado, String marca, Double total, String factura, String proReal, Double iva, Double litros, String proEnFactura, String constancia, String tarjeta, Long idMaquinariaEstatus, String placa, Long idOriginal, String clave, Long modelo, String tipo, Long idMaquinaria, Long idConstancia, String facturado, Double rendimiento, Long idUsuario, Double subtotal, String entidad, String serie, String observaciones, Long idEmpresa, String ultimaTarjeta, String responsable) {
    setMotor(motor);
    setProEnTarjeta(proEnTarjeta);
    setColor(color);
    setPedimento(pedimento);
    setIdTipoMaquinaria(idTipoMaquinaria);
    setFechaFactura(fechaFactura);
    setNombre(nombre);
    setIdTipoCombustible(idTipoCombustible);
    setPoliza(poliza);
    setComprado(comprado);
    setMarca(marca);
    setTotal(total);
    setFactura(factura);
    setProReal(proReal);
    setIva(iva);
    setLitros(litros);
    setProEnFactura(proEnFactura);
    setConstancia(constancia);
    setTarjeta(tarjeta);
    setIdMaquinariaEstatus(idMaquinariaEstatus);
    setPlaca(placa);
    setIdOriginal(idOriginal);
    setClave(clave);
    setModelo(modelo);
    setTipo(tipo);
    setRegistro(LocalDateTime.now());
    setIdMaquinaria(idMaquinaria);
    setIdConstancia(idConstancia);
    setFacturado(facturado);
    setRendimiento(rendimiento);
    setIdUsuario(idUsuario);
    setSubtotal(subtotal);
    setEntidad(entidad);
    setSerie(serie);
    setObservaciones(observaciones);
    setIdEmpresa(idEmpresa);
    setUltimaTarjeta(ultimaTarjeta);
    setResponsable(responsable);
  }
	
  public void setMotor(String motor) {
    this.motor = motor;
  }

  public String getMotor() {
    return motor;
  }

  public void setProEnTarjeta(String proEnTarjeta) {
    this.proEnTarjeta = proEnTarjeta;
  }

  public String getProEnTarjeta() {
    return proEnTarjeta;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getColor() {
    return color;
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

  public void setComprado(String comprado) {
    this.comprado = comprado;
  }

  public String getComprado() {
    return comprado;
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

  public void setProReal(String proReal) {
    this.proReal = proReal;
  }

  public String getProReal() {
    return proReal;
  }

  public void setIva(Double iva) {
    this.iva = iva;
  }

  public Double getIva() {
    return iva;
  }

  public void setLitros(Double litros) {
    this.litros = litros;
  }

  public Double getLitros() {
    return litros;
  }

  public void setProEnFactura(String proEnFactura) {
    this.proEnFactura = proEnFactura;
  }

  public String getProEnFactura() {
    return proEnFactura;
  }

  public void setConstancia(String constancia) {
    this.constancia = constancia;
  }

  public String getConstancia() {
    return constancia;
  }

  public void setTarjeta(String tarjeta) {
    this.tarjeta = tarjeta;
  }

  public String getTarjeta() {
    return tarjeta;
  }

  public void setIdMaquinariaEstatus(Long idMaquinariaEstatus) {
    this.idMaquinariaEstatus = idMaquinariaEstatus;
  }

  public Long getIdMaquinariaEstatus() {
    return idMaquinariaEstatus;
  }

  public void setPlaca(String placa) {
    this.placa = placa;
  }

  public String getPlaca() {
    return placa;
  }

  public void setIdOriginal(Long idOriginal) {
    this.idOriginal = idOriginal;
  }

  public Long getIdOriginal() {
    return idOriginal;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getClave() {
    return clave;
  }

  public void setModelo(Long modelo) {
    this.modelo = modelo;
  }

  public Long getModelo() {
    return modelo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public String getTipo() {
    return tipo;
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

  public void setIdConstancia(Long idConstancia) {
    this.idConstancia = idConstancia;
  }

  public Long getIdConstancia() {
    return idConstancia;
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

  public void setEntidad(String entidad) {
    this.entidad = entidad;
  }

  public String getEntidad() {
    return entidad;
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

  public String getUltimaTarjeta() {
    return ultimaTarjeta;
  }

  public void setUltimaTarjeta(String ultimaTarjeta) {
    this.ultimaTarjeta = ultimaTarjeta;
  }

  public String getResponsable() {
    return responsable;
  }

  public void setResponsable(String responsable) {
    this.responsable = responsable;
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
		regresar.append(getMotor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getProEnTarjeta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getColor());
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
		regresar.append(getComprado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getMarca());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFactura());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getProReal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIva());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLitros());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getProEnFactura());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConstancia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTarjeta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdMaquinariaEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPlaca());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdOriginal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getModelo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdMaquinaria());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdConstancia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFacturado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRendimiento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSubtotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEntidad());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSerie());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getUltimaTarjeta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getResponsable());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("motor", getMotor());
		regresar.put("proEnTarjeta", getProEnTarjeta());
		regresar.put("color", getColor());
		regresar.put("pedimento", getPedimento());
		regresar.put("idTipoMaquinaria", getIdTipoMaquinaria());
		regresar.put("fechaFactura", getFechaFactura());
		regresar.put("nombre", getNombre());
		regresar.put("idTipoCombustible", getIdTipoCombustible());
		regresar.put("poliza", getPoliza());
		regresar.put("comprado", getComprado());
		regresar.put("marca", getMarca());
		regresar.put("total", getTotal());
		regresar.put("factura", getFactura());
		regresar.put("proReal", getProReal());
		regresar.put("iva", getIva());
		regresar.put("litros", getLitros());
		regresar.put("proEnFactura", getProEnFactura());
		regresar.put("constancia", getConstancia());
		regresar.put("tarjeta", getTarjeta());
		regresar.put("idMaquinariaEstatus", getIdMaquinariaEstatus());
		regresar.put("placa", getPlaca());
		regresar.put("idOriginal", getIdOriginal());
		regresar.put("clave", getClave());
		regresar.put("modelo", getModelo());
		regresar.put("tipo", getTipo());
		regresar.put("registro", getRegistro());
		regresar.put("idMaquinaria", getIdMaquinaria());
		regresar.put("idConstancia", getIdConstancia());
		regresar.put("facturado", getFacturado());
		regresar.put("rendimiento", getRendimiento());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("subtotal", getSubtotal());
		regresar.put("entidad", getEntidad());
		regresar.put("serie", getSerie());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("ultimaTarjeta", getUltimaTarjeta());
		regresar.put("responsable", getResponsable());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getMotor(), getProEnTarjeta(), getColor(), getPedimento(), getIdTipoMaquinaria(), getFechaFactura(), getNombre(), getIdTipoCombustible(), getPoliza(), getComprado(), getMarca(), getTotal(), getFactura(), getProReal(), getIva(), getLitros(), getProEnFactura(), getConstancia(), getTarjeta(), getIdMaquinariaEstatus(), getPlaca(), getIdOriginal(), getClave(), getModelo(), getTipo(), getRegistro(), getIdMaquinaria(), getIdConstancia(), getFacturado(), getRendimiento(), getIdUsuario(), getSubtotal(), getEntidad(), getSerie(), getObservaciones(), getIdEmpresa(), getUltimaTarjeta(), getResponsable()
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


