package mx.org.kaana.sakbe.db.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.LocalDate;
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
@Table(name="tc_sakbe_combustibles")
public class TcSakbeCombustiblesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="id_tipo_medio_pago")
  private Long idTipoMedioPago;
  @Column (name="ticket")
  private String ticket;
  @Column (name="lugar")
  private String lugar;
  @Column (name="saldo")
  private Double saldo;
  @Column (name="id_tipo_combustible")
  private Long idTipoCombustible;
  @Column (name="id_combustible_estatus")
  private Long idCombustibleEstatus;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="fecha")
  private LocalDate fecha;
  @Column (name="precio_litro")
  private Double precioLitro;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="litros")
  private Double litros;
  @Column (name="orden")
  private Long orden;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_combustible")
  private Long idCombustible;
	@Column (name="observaciones")
  private String observaciones;
	@Column (name="id_banco")
  private Long idBanco;
	@Column (name="referencia")
  private String referencia;
	@Column (name="total")
  private double total;

  public TcSakbeCombustiblesDto() {
    this(new Long(-1L));
  }

  public TcSakbeCombustiblesDto(Long key) {
    this(null, null, null, null, null, null, null, null, null, LocalDate.now(), null, null, null, null, new Long(-1L), null, -1L, null, 0D);
    setKey(key);
  }

  public TcSakbeCombustiblesDto(Long idEmpresa, Long idTipoMedioPago, String ticket, String lugar, Double saldo, Long idTipoCombustible, Long idCombustibleEstatus, Long ejercicio, String consecutivo, LocalDate fecha, Double precioLitro, Long idUsuario, Double litros, Long orden, Long idCombustible, String observaciones, Long idBanco, String referencia, Double total) {
    setIdEmpresa(idEmpresa);
    setIdTipoMedioPago(idTipoMedioPago);
    setTicket(ticket);
    setLugar(lugar);
    setSaldo(saldo);
    setIdTipoCombustible(idTipoCombustible);
    setIdCombustibleEstatus(idCombustibleEstatus);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
    setConsecutivo(consecutivo);
    setFecha(fecha);
    setPrecioLitro(precioLitro);
    setIdUsuario(idUsuario);
    setLitros(litros);
    setOrden(orden);
    setIdCombustible(idCombustible);
    setObservaciones(observaciones);
    setIdBanco(idBanco);
    setReferencia(referencia);
    setTotal(total);
  }

  public Long getIdEmpresa() {
    return idEmpresa;
  }

  public void setIdEmpresa(Long idEmpresa) {
    this.idEmpresa = idEmpresa;
  }
	
  public void setIdTipoMedioPago(Long idTipoMedioPago) {
    this.idTipoMedioPago = idTipoMedioPago;
  }

  public Long getIdTipoMedioPago() {
    return idTipoMedioPago;
  }

  public void setTicket(String ticket) {
    this.ticket = ticket;
  }

  public String getTicket() {
    return ticket;
  }

  public void setLugar(String lugar) {
    this.lugar = lugar;
  }

  public String getLugar() {
    return lugar;
  }

  public void setSaldo(Double saldo) {
    this.saldo = saldo;
  }

  public Double getSaldo() {
    return saldo;
  }

  public void setIdTipoCombustible(Long idTipoCombustible) {
    this.idTipoCombustible = idTipoCombustible;
  }

  public Long getIdTipoCombustible() {
    return idTipoCombustible;
  }

  public void setIdCombustibleEstatus(Long idCombustibleEstatus) {
    this.idCombustibleEstatus = idCombustibleEstatus;
  }

  public Long getIdCombustibleEstatus() {
    return idCombustibleEstatus;
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

  public void setFecha(LocalDate fecha) {
    this.fecha = fecha;
  }

  public LocalDate getFecha() {
    return fecha;
  }

  public void setPrecioLitro(Double precioLitro) {
    this.precioLitro = precioLitro;
  }

  public Double getPrecioLitro() {
    return precioLitro;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setLitros(Double litros) {
    this.litros = litros;
  }

  public Double getLitros() {
    return litros;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setIdCombustible(Long idCombustible) {
    this.idCombustible = idCombustible;
  }

  public Long getIdCombustible() {
    return idCombustible;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public Long getIdBanco() {
    return idBanco;
  }

  public void setIdBanco(Long idBanco) {
    this.idBanco = idBanco;
  }

  public String getReferencia() {
    return referencia;
  }

  public void setReferencia(String referencia) {
    this.referencia = referencia;
  }

  public double getTotal() {
    return total;
  }

  public void setTotal(double total) {
    this.total = total;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdCombustible();
  }

  @Override
  public void setKey(Long key) {
  	this.idCombustible = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoMedioPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTicket());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLugar());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSaldo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoCombustible());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCombustibleEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFecha());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPrecioLitro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLitros());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCombustible());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdBanco());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getReferencia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTotal());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("idTipoMedioPago", getIdTipoMedioPago());
		regresar.put("ticket", getTicket());
		regresar.put("lugar", getLugar());
		regresar.put("saldo", getSaldo());
		regresar.put("idTipoCombustible", getIdTipoCombustible());
		regresar.put("idCombustibleEstatus", getIdCombustibleEstatus());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("fecha", getFecha());
		regresar.put("precioLitro", getPrecioLitro());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("litros", getLitros());
		regresar.put("orden", getOrden());
		regresar.put("idCombustible", getIdCombustible());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idBanco", getIdBanco());
		regresar.put("referencia", getReferencia());
		regresar.put("total", getTotal());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getIdEmpresa(), getIdTipoMedioPago(), getTicket(), getLugar(), getSaldo(), getIdTipoCombustible(), getIdCombustibleEstatus(), getEjercicio(), getRegistro(), getConsecutivo(), getFecha(), getPrecioLitro(), getIdUsuario(), getLitros(), getOrden(), getIdCombustible(), getObservaciones(), getIdBanco(), getReferencia(), getTotal()
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
    regresar.append("idCombustible~");
    regresar.append(getIdCombustible());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdCombustible());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeCombustiblesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdCombustible()!= null && getIdCombustible()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcSakbeCombustiblesDto other = (TcSakbeCombustiblesDto) obj;
    if (getIdCombustible() != other.idCombustible && (getIdCombustible() == null || !getIdCombustible().equals(other.idCombustible))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdCombustible() != null ? getIdCombustible().hashCode() : 0);
    return hash;
  }

}


