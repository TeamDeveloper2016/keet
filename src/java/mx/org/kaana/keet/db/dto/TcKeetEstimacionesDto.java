package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
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
@Table(name="tc_keet_estimaciones")
public class TcKeetEstimacionesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_venta")
  private Long idVenta;
  @Column (name="facturar")
  private Double facturar;
  @Column (name="saldo")
  private Double saldo;
  @Column (name="importe")
  private Double importe;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="id_estimacion_estatus")
  private Long idEstimacionEstatus;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_contrato")
  private Long idContrato;
  @Column (name="observaciones")
  private String observaciones;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
  @Column (name="id_estimacion")
  private Long idEstimacion;
  @Column (name="orden")
  private Long orden;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="id_nomina")
  private Long idNomina;
  @Column (name="fecha_pago")
  private LocalDate fechaPago;
  @Column (name="pagado")
  private LocalDate pagado;
  @Column (name="folio")
  private String folio;
  @Column (name="id_extra")
  private Long idExtra;
  

  public TcKeetEstimacionesDto() {
    this(new Long(-1L));
  }

  public TcKeetEstimacionesDto(Long key) {
    this(null, null, null, null, null, null, null, null, null, null, null, null, null, -1L, LocalDate.now(), LocalDate.now(), null, 2L);
    setKey(key);
  }

  public TcKeetEstimacionesDto(Long idVenta, Double facturar, Double saldo, Double importe, Long ejercicio, String consecutivo, Long idEstimacionEstatus, Long idUsuario, Long idContrato, String observaciones, Long idEstimacion, Long orden, Long idEmpresa, Long idNomina, LocalDate fechaPago, LocalDate pagado, String folio, Long idExtra) {
    setIdVenta(idVenta);
    setFacturar(facturar);
    setSaldo(saldo);
    setImporte(importe);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
    setConsecutivo(consecutivo);
    setIdEstimacionEstatus(idEstimacionEstatus);
    setIdUsuario(idUsuario);
    setIdContrato(idContrato);
    setObservaciones(observaciones);
    setIdEstimacion(idEstimacion);
    setOrden(orden);
    setIdEmpresa(idEmpresa);
    setIdNomina(idNomina);
    setFechaPago(fechaPago);
    setPagado(pagado);
    setFolio(folio);
    setIdExtra(idExtra);
  }
	
  public void setIdVenta(Long idVenta) {
    this.idVenta = idVenta;
  }

  public Long getIdVenta() {
    return idVenta;
  }

  public void setFacturar(Double facturar) {
    this.facturar = facturar;
  }

  public Double getFacturar() {
    return facturar;
  }

  public void setSaldo(Double saldo) {
    this.saldo = saldo;
  }

  public Double getSaldo() {
    return saldo;
  }

  public void setImporte(Double importe) {
    this.importe = importe;
  }

  public Double getImporte() {
    return importe;
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

  public void setIdEstimacionEstatus(Long idEstimacionEstatus) {
    this.idEstimacionEstatus = idEstimacionEstatus;
  }

  public Long getIdEstimacionEstatus() {
    return idEstimacionEstatus;
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

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdEstimacion(Long idEstimacion) {
    this.idEstimacion = idEstimacion;
  }

  public Long getIdEstimacion() {
    return idEstimacion;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public Long getIdEmpresa() {
    return idEmpresa;
  }

  public void setIdEmpresa(Long idEmpresa) {
    this.idEmpresa = idEmpresa;
  }

  public Long getIdNomina() {
    return idNomina;
  }

  public void setIdNomina(Long idNomina) {
    this.idNomina = idNomina;
  }

  public LocalDate getFechaPago() {
    return fechaPago;
  }

  public void setFechaPago(LocalDate fechaPago) {
    this.fechaPago = fechaPago;
  }

  public LocalDate getPagado() {
    return pagado;
  }

  public void setPagado(LocalDate pagado) {
    this.pagado = pagado;
  }

  public String getFolio() {
    return folio;
  }

  public void setFolio(String folio) {
    this.folio = folio;
  }

  public Long getIdExtra() {
    return idExtra;
  }

  public void setIdExtra(Long idExtra) {
    this.idExtra = idExtra;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdEstimacion();
  }

  @Override
  public void setKey(Long key) {
  	this.idEstimacion = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdVenta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFacturar());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSaldo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEstimacionEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEstimacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNomina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPagado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFolio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdExtra());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idVenta", getIdVenta());
		regresar.put("facturar", getFacturar());
		regresar.put("saldo", getSaldo());
		regresar.put("importe", getImporte());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idEstimacionEstatus", getIdEstimacionEstatus());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idContrato", getIdContrato());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEstimacion", getIdEstimacion());
		regresar.put("orden", getOrden());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("idNomina", getIdNomina());
		regresar.put("fechaPago", getFechaPago());
		regresar.put("pagado", getPagado());
		regresar.put("folio", getFolio());
		regresar.put("idExtra", getIdExtra());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getIdVenta(), getFacturar(), getSaldo(), getImporte(), getEjercicio(), getRegistro(), getConsecutivo(), getIdEstimacionEstatus(), getIdUsuario(), getIdContrato(), getObservaciones(), getIdEstimacion(), getOrden(), getIdEmpresa(), getIdNomina(), getFechaPago(), getPagado(), getFolio(), getIdExtra()
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
    regresar.append("idEstimacion~");
    regresar.append(getIdEstimacion());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdEstimacion());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetEstimacionesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdEstimacion()!= null && getIdEstimacion()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetEstimacionesDto other = (TcKeetEstimacionesDto) obj;
    if (getIdEstimacion() != other.idEstimacion && (getIdEstimacion() == null || !getIdEstimacion().equals(other.idEstimacion))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdEstimacion() != null ? getIdEstimacion().hashCode() : 0);
    return hash;
  }

}


