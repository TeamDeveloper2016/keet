package mx.org.kaana.sakbe.db.dto;

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
@Table(name="tc_sakbe_servicios")
public class TcSakbeServiciosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_servicio_estatus")
  private Long idServicioEstatus;
  @Column (name="lugar")
  private String lugar;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_servicio")
  private Long idServicio;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="id_maquinaria")
  private Long idMaquinaria;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="total")
  private Double total;
  @Column (name="iva")
  private Double iva;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="subtotal")
  private Double subtotal;
  @Column (name="dias_trancurridos")
  private Double diasTrancurridos;
  @Column (name="aplicado")
  private LocalDateTime aplicado;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="orden")
  private Long orden;
  @Column (name="transcurrido")
  private Double transcurrido;

  public TcSakbeServiciosDto() {
    this(new Long(-1L));
  }

  public TcSakbeServiciosDto(Long key) {
    this(null, null, new Long(-1L), null, null, null, null, null, null, null, null, LocalDateTime.now(), null, null, null);
    setKey(key);
  }

  public TcSakbeServiciosDto(Long idServicioEstatus, String lugar, Long idServicio, Long ejercicio, Long idMaquinaria, String consecutivo, Double total, Double iva, Long idUsuario, Double subtotal, Double diasTrancurridos, LocalDateTime aplicado, String observaciones, Long orden, Double transcurrido) {
    setIdServicioEstatus(idServicioEstatus);
    setLugar(lugar);
    setIdServicio(idServicio);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
    setIdMaquinaria(idMaquinaria);
    setConsecutivo(consecutivo);
    setTotal(total);
    setIva(iva);
    setIdUsuario(idUsuario);
    setSubtotal(subtotal);
    setDiasTrancurridos(diasTrancurridos);
    setAplicado(aplicado);
    setObservaciones(observaciones);
    setOrden(orden);
    setTranscurrido(transcurrido);
  }
	
  public void setIdServicioEstatus(Long idServicioEstatus) {
    this.idServicioEstatus = idServicioEstatus;
  }

  public Long getIdServicioEstatus() {
    return idServicioEstatus;
  }

  public void setLugar(String lugar) {
    this.lugar = lugar;
  }

  public String getLugar() {
    return lugar;
  }

  public void setIdServicio(Long idServicio) {
    this.idServicio = idServicio;
  }

  public Long getIdServicio() {
    return idServicio;
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

  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public String getConsecutivo() {
    return consecutivo;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Double getTotal() {
    return total;
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

  public void setDiasTrancurridos(Double diasTrancurridos) {
    this.diasTrancurridos = diasTrancurridos;
  }

  public Double getDiasTrancurridos() {
    return diasTrancurridos;
  }

  public void setAplicado(LocalDateTime aplicado) {
    this.aplicado = aplicado;
  }

  public LocalDateTime getAplicado() {
    return aplicado;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setTranscurrido(Double transcurrido) {
    this.transcurrido = transcurrido;
  }

  public Double getTranscurrido() {
    return transcurrido;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdServicio();
  }

  @Override
  public void setKey(Long key) {
  	this.idServicio = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdServicioEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLugar());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdServicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdMaquinaria());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIva());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSubtotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDiasTrancurridos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAplicado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTranscurrido());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idServicioEstatus", getIdServicioEstatus());
		regresar.put("lugar", getLugar());
		regresar.put("idServicio", getIdServicio());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("idMaquinaria", getIdMaquinaria());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("total", getTotal());
		regresar.put("iva", getIva());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("subtotal", getSubtotal());
		regresar.put("diasTrancurridos", getDiasTrancurridos());
		regresar.put("aplicado", getAplicado());
		regresar.put("observaciones", getObservaciones());
		regresar.put("orden", getOrden());
		regresar.put("transcurrido", getTranscurrido());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdServicioEstatus(), getLugar(), getIdServicio(), getEjercicio(), getRegistro(), getIdMaquinaria(), getConsecutivo(), getTotal(), getIva(), getIdUsuario(), getSubtotal(), getDiasTrancurridos(), getAplicado(), getObservaciones(), getOrden(), getTranscurrido()
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
    regresar.append("idServicio~");
    regresar.append(getIdServicio());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdServicio());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeServiciosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdServicio()!= null && getIdServicio()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcSakbeServiciosDto other = (TcSakbeServiciosDto) obj;
    if (getIdServicio() != other.idServicio && (getIdServicio() == null || !getIdServicio().equals(other.idServicio))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdServicio() != null ? getIdServicio().hashCode() : 0);
    return hash;
  }

}


