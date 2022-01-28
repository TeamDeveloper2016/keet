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
@Table(name="tc_keet_anticipos")
public class TcKeetAnticiposDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_anticipo")
  private Long idAnticipo;
  @Column (name="semanas")
  private Long semanas;
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
  @Column (name="id_anticipo_estatus")
  private Long idAnticipoEstatus;
  @Column (name="id_afecta_nomina")
  private Long idAfectaNomina;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_moroso")
  private Long idMoroso;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="orden")
  private Long orden;
  @Column (name="iva")
  private Double iva;
  @Column (name="total")
  private Double total;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="id_desarrollo")
  private Long idDesarrollo;
  @Column (name="id_contrato")
  private Long idContrato;
  @Column (name="fondo_garantia")
  private Double fondoGarantia;
  @Column (name="subtotal")
  private Double subtotal;

  public TcKeetAnticiposDto() {
    this(new Long(-1L));
  }

  public TcKeetAnticiposDto(Long key) {
    this(new Long(-1L), 1L, null, null, null, null, null, null, null, null, null, null, 0D, 0D, null, null, null, 0D, 0D);
    setKey(key);
  }

  public TcKeetAnticiposDto(Long idAnticipo, Long semanas, Double saldo, Double importe, Long ejercicio, String consecutivo, Long idAnticipoEstatus, Long idAfectaNomina, Long idUsuario, Long idMoroso, String observaciones, Long orden, Double iva, Double total, Long idEmpresa, Long idContrato, Long idDesarrollo, Double fondoGarantia, Double subtotal) {
    setIdAnticipo(idAnticipo);
    setSemanas(semanas);
    setSaldo(saldo);
    setImporte(importe);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
    setConsecutivo(consecutivo);
    setIdAnticipoEstatus(idAnticipoEstatus);
    setIdAfectaNomina(idAfectaNomina);
    setIdUsuario(idUsuario);
    setIdMoroso(idMoroso);
    setObservaciones(observaciones);
    setOrden(orden);
    setIva(iva);
    setTotal(total);
    setIdEmpresa(idEmpresa);
    setIdContrato(idContrato);
    setIdDesarrollo(idDesarrollo);
    setFondoGarantia(fondoGarantia);
    setSubtotal(subtotal);
  }
	
  public void setIdAnticipo(Long idAnticipo) {
    this.idAnticipo = idAnticipo;
  }

  public Long getIdAnticipo() {
    return idAnticipo;
  }

  public void setSemanas(Long semanas) {
    this.semanas = semanas;
  }

  public Long getSemanas() {
    return semanas;
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

  public void setIdAnticipoEstatus(Long idAnticipoEstatus) {
    this.idAnticipoEstatus = idAnticipoEstatus;
  }

  public Long getIdAnticipoEstatus() {
    return idAnticipoEstatus;
  }

  public void setIdAfectaNomina(Long idAfectaNomina) {
    this.idAfectaNomina = idAfectaNomina;
  }

  public Long getIdAfectaNomina() {
    return idAfectaNomina;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdMoroso(Long idMoroso) {
    this.idMoroso = idMoroso;
  }

  public Long getIdMoroso() {
    return idMoroso;
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

  public Double getIva() {
    return iva;
  }

  public void setIva(Double iva) {
    this.iva = iva;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Long getIdEmpresa() {
    return idEmpresa;
  }

  public void setIdEmpresa(Long idEmpresa) {
    this.idEmpresa = idEmpresa;
  }
  
  public Long getIdContrato() {
    return idContrato;
  }

  public void setIdContrato(Long idContrato) {
    this.idContrato = idContrato;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }

  public Double getFondoGarantia() {
    return fondoGarantia;
  }

  public void setFondoGarantia(Double fondoGarantia) {
    this.fondoGarantia = fondoGarantia;
  }

  public Double getSubtotal() {
    return subtotal;
  }

  public void setSubtotal(Double subtotal) {
    this.subtotal = subtotal;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdAnticipo();
  }

  @Override
  public void setKey(Long key) {
  	this.idAnticipo = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdAnticipo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSemanas());
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
		regresar.append(getIdAnticipoEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAfectaNomina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdMoroso());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIva());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTotal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFondoGarantia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSubtotal());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idAnticipo", getIdAnticipo());
		regresar.put("semanas", getSemanas());
		regresar.put("saldo", getSaldo());
		regresar.put("importe", getImporte());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idAnticipoEstatus", getIdAnticipoEstatus());
		regresar.put("idAfectaNomina", getIdAfectaNomina());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idMoroso", getIdMoroso());
		regresar.put("observaciones", getObservaciones());
		regresar.put("orden", getOrden());
		regresar.put("iva", getIva());
		regresar.put("total", getTotal());
		regresar.put("idDesarrollo", getIdDesarrollo());
		regresar.put("idContrato", getIdContrato());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("fondoGarantia", getFondoGarantia());
		regresar.put("subtotal", getSubtotal());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getIdAnticipo(), getSemanas(), getSaldo(), getImporte(), getEjercicio(), getRegistro(), getConsecutivo(), getIdAnticipoEstatus(), getIdAfectaNomina(), getIdUsuario(), getIdMoroso(), getObservaciones(), getOrden(), getIva(), getTotal(), getIdDesarrollo(), getIdContrato(), getIdEmpresa(), getFondoGarantia(), getSubtotal()
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
    regresar.append("idAnticipo~");
    regresar.append(getIdAnticipo());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAnticipo());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetAnticiposDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAnticipo()!= null && getIdAnticipo()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetAnticiposDto other = (TcKeetAnticiposDto) obj;
    if (getIdAnticipo() != other.idAnticipo && (getIdAnticipo() == null || !getIdAnticipo().equals(other.idAnticipo))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAnticipo() != null ? getIdAnticipo().hashCode() : 0);
    return hash;
  }

}


