package mx.org.kaana.keet.db.dto;

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
@Table(name="tc_keet_ingresos")
public class TcKeetIngresosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descuentos")
  private Double descuentos;
  @Column (name="id_factura")
  private Long idFactura;
  @Column (name="id_desarrollo")
  private Long idDesarrollo;
  @Column (name="id_tipo_pago")
  private Long idTipoPago;
  @Column (name="fecha_recepcion")
  private LocalDate fechaRecepcion;
  @Column (name="fecha_factura")
  private LocalDate fechaFactura;
  @Column (name="total")
  private Double total;
  @Column (name="factura")
  private String factura;
  @Column (name="id_contrato")
  private Long idContrato;
  @Column (name="orden")
  private Long orden;
  @Column (name="id_ingreso_estatus")
  private Long idIngresoEstatus;
  @Column (name="id_tipo_medio_pago")
  private Long idTipoMedioPago;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_ingreso")
  private Long idIngreso;
  @Column (name="id_cliente")
  private Long idCliente;
  @Column (name="id_sincronizado")
  private Long idSincronizado;
  @Column (name="id_banco")
  private Long idBanco;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="impuestos")
  private Double impuestos;
  @Column (name="id_normal")
  private Long idNormal;
  @Column (name="id_uso_cfdi")
  private Long idUsoCfdi;
  @Column (name="sub_total")
  private Double subTotal;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="referencia")
  private String referencia;

  public TcKeetIngresosDto() {
    this(new Long(-1L));
  }

  public TcKeetIngresosDto(Long key) {
    this(null, null, null, null, LocalDate.now(), LocalDate.now(), null, null, null, null, null, null, new Long(-1L), null, null, null, null, null, null, null, null, null, null, null, null, null);
    setKey(key);
  }

  public TcKeetIngresosDto(Double descuentos, Long idFactura, Long idDesarrollo, Long idTipoPago, LocalDate fechaRecepcion, LocalDate fechaFactura, Double total, String factura, Long idContrato, Long orden, Long idIngresoEstatus, Long idTipoMedioPago, Long idIngreso, Long idCliente, Long idSincronizado, Long idBanco, Long ejercicio, String consecutivo, Long idUsuario, Double impuestos, Long idNormal, Long idUsoCfdi, Double subTotal, String observaciones, Long idEmpresa, String referencia) {
    setDescuentos(descuentos);
    setIdFactura(idFactura);
    setIdDesarrollo(idDesarrollo);
    setIdTipoPago(idTipoPago);
    setFechaRecepcion(fechaRecepcion);
    setFechaFactura(fechaFactura);
    setTotal(total);
    setFactura(factura);
    setIdContrato(idContrato);
    setOrden(orden);
    setIdIngresoEstatus(idIngresoEstatus);
    setIdTipoMedioPago(idTipoMedioPago);
    setIdIngreso(idIngreso);
    setIdCliente(idCliente);
    setIdSincronizado(idSincronizado);
    setIdBanco(idBanco);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
    setConsecutivo(consecutivo);
    setIdUsuario(idUsuario);
    setImpuestos(impuestos);
    setIdNormal(idNormal);
    setIdUsoCfdi(idUsoCfdi);
    setSubTotal(subTotal);
    setObservaciones(observaciones);
    setIdEmpresa(idEmpresa);
    setReferencia(referencia);
  }
	
  public void setDescuentos(Double descuentos) {
    this.descuentos = descuentos;
  }

  public Double getDescuentos() {
    return descuentos;
  }

  public void setIdFactura(Long idFactura) {
    this.idFactura = idFactura;
  }

  public Long getIdFactura() {
    return idFactura;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setIdTipoPago(Long idTipoPago) {
    this.idTipoPago = idTipoPago;
  }

  public Long getIdTipoPago() {
    return idTipoPago;
  }

  public void setFechaRecepcion(LocalDate fechaRecepcion) {
    this.fechaRecepcion = fechaRecepcion;
  }

  public LocalDate getFechaRecepcion() {
    return fechaRecepcion;
  }

  public void setFechaFactura(LocalDate fechaFactura) {
    this.fechaFactura = fechaFactura;
  }

  public LocalDate getFechaFactura() {
    return fechaFactura;
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

  public void setIdContrato(Long idContrato) {
    this.idContrato = idContrato;
  }

  public Long getIdContrato() {
    return idContrato;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setIdIngresoEstatus(Long idIngresoEstatus) {
    this.idIngresoEstatus = idIngresoEstatus;
  }

  public Long getIdIngresoEstatus() {
    return idIngresoEstatus;
  }

  public void setIdTipoMedioPago(Long idTipoMedioPago) {
    this.idTipoMedioPago = idTipoMedioPago;
  }

  public Long getIdTipoMedioPago() {
    return idTipoMedioPago;
  }

  public void setIdIngreso(Long idIngreso) {
    this.idIngreso = idIngreso;
  }

  public Long getIdIngreso() {
    return idIngreso;
  }

  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public Long getIdCliente() {
    return idCliente;
  }

  public void setIdSincronizado(Long idSincronizado) {
    this.idSincronizado = idSincronizado;
  }

  public Long getIdSincronizado() {
    return idSincronizado;
  }

  public void setIdBanco(Long idBanco) {
    this.idBanco = idBanco;
  }

  public Long getIdBanco() {
    return idBanco;
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

  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public String getConsecutivo() {
    return consecutivo;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setImpuestos(Double impuestos) {
    this.impuestos = impuestos;
  }

  public Double getImpuestos() {
    return impuestos;
  }

  public void setIdNormal(Long idNormal) {
    this.idNormal = idNormal;
  }

  public Long getIdNormal() {
    return idNormal;
  }

  public void setIdUsoCfdi(Long idUsoCfdi) {
    this.idUsoCfdi = idUsoCfdi;
  }

  public Long getIdUsoCfdi() {
    return idUsoCfdi;
  }

  public void setSubTotal(Double subTotal) {
    this.subTotal = subTotal;
  }

  public Double getSubTotal() {
    return subTotal;
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

  public void setReferencia(String referencia) {
    this.referencia = referencia;
  }

  public String getReferencia() {
    return referencia;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdIngreso();
  }

  @Override
  public void setKey(Long key) {
  	this.idIngreso = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescuentos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdFactura());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaRecepcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaFactura());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFactura());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdIngresoEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoMedioPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdIngreso());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCliente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdSincronizado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdBanco());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImpuestos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNormal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsoCfdi());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSubTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getReferencia());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descuentos", getDescuentos());
		regresar.put("idFactura", getIdFactura());
		regresar.put("idDesarrollo", getIdDesarrollo());
		regresar.put("idTipoPago", getIdTipoPago());
		regresar.put("fechaRecepcion", getFechaRecepcion());
		regresar.put("fechaFactura", getFechaFactura());
		regresar.put("total", getTotal());
		regresar.put("factura", getFactura());
		regresar.put("idContrato", getIdContrato());
		regresar.put("orden", getOrden());
		regresar.put("idIngresoEstatus", getIdIngresoEstatus());
		regresar.put("idTipoMedioPago", getIdTipoMedioPago());
		regresar.put("idIngreso", getIdIngreso());
		regresar.put("idCliente", getIdCliente());
		regresar.put("idSincronizado", getIdSincronizado());
		regresar.put("idBanco", getIdBanco());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("impuestos", getImpuestos());
		regresar.put("idNormal", getIdNormal());
		regresar.put("idUsoCfdi", getIdUsoCfdi());
		regresar.put("subTotal", getSubTotal());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("referencia", getReferencia());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescuentos(), getIdFactura(), getIdDesarrollo(), getIdTipoPago(), getFechaRecepcion(), getFechaFactura(), getTotal(), getFactura(), getIdContrato(), getOrden(), getIdIngresoEstatus(), getIdTipoMedioPago(), getIdIngreso(), getIdCliente(), getIdSincronizado(), getIdBanco(), getEjercicio(), getRegistro(), getConsecutivo(), getIdUsuario(), getImpuestos(), getIdNormal(), getIdUsoCfdi(), getSubTotal(), getObservaciones(), getIdEmpresa(), getReferencia()
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
    regresar.append("idIngreso~");
    regresar.append(getIdIngreso());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdIngreso());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetIngresosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdIngreso()!= null && getIdIngreso()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetIngresosDto other = (TcKeetIngresosDto) obj;
    if (getIdIngreso() != other.idIngreso && (getIdIngreso() == null || !getIdIngreso().equals(other.idIngreso))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdIngreso() != null ? getIdIngreso().hashCode() : 0);
    return hash;
  }

}


