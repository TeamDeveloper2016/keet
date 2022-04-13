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
@Table(name="tc_keet_contratos_vecimientos")
public class TcKeetContratosVecimientosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_tipo_medio_pago")
  private Long idTipoMedioPago;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_contrato")
  private Long idContrato;
  @Column (name="observaciones")
  private String observaciones;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_vencimiento")
  private Long idContratoVencimiento;
  @Column (name="pagare")
  private String pagare;
  @Column (name="importe")
  private Double importe;
  @Column (name="referencia")
  private String referencia;
  @Column (name="id_banco")
  private Long idBanco;
  @Column (name="fecha_pago")
  private LocalDate fechaPago;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetContratosVecimientosDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosVecimientosDto(Long key) {
    this(null, null, null, null, new Long(-1L), null, null, null, null, LocalDate.now());
    setKey(key);
  }

  public TcKeetContratosVecimientosDto(Long idTipoMedioPago, Long idUsuario, Long idContrato, String observaciones, Long idContratoVencimiento, String pagare, Double importe, String referencia, Long idBanco, LocalDate fechaPago) {
    setIdTipoMedioPago(idTipoMedioPago);
    setIdUsuario(idUsuario);
    setIdContrato(idContrato);
    setObservaciones(observaciones);
    setIdContratoVencimiento(idContratoVencimiento);
    setPagare(pagare);
    setImporte(importe);
    setReferencia(referencia);
    setIdBanco(idBanco);
    setFechaPago(fechaPago);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdTipoMedioPago(Long idTipoMedioPago) {
    this.idTipoMedioPago = idTipoMedioPago;
  }

  public Long getIdTipoMedioPago() {
    return idTipoMedioPago;
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

  public void setIdContratoVencimiento(Long idContratoVencimiento) {
    this.idContratoVencimiento = idContratoVencimiento;
  }

  public Long getIdContratoVencimiento() {
    return idContratoVencimiento;
  }

  public void setPagare(String pagare) {
    this.pagare = pagare;
  }

  public String getPagare() {
    return pagare;
  }

  public void setImporte(Double importe) {
    this.importe = importe;
  }

  public Double getImporte() {
    return importe;
  }

  public void setReferencia(String referencia) {
    this.referencia = referencia;
  }

  public String getReferencia() {
    return referencia;
  }

  public void setIdBanco(Long idBanco) {
    this.idBanco = idBanco;
  }

  public Long getIdBanco() {
    return idBanco;
  }

  public void setFechaPago(LocalDate fechaPago) {
    this.fechaPago = fechaPago;
  }

  public LocalDate getFechaPago() {
    return fechaPago;
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
  	return getIdContratoVencimiento();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoVencimiento = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdTipoMedioPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoVencimiento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPagare());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getReferencia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdBanco());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idTipoMedioPago", getIdTipoMedioPago());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idContrato", getIdContrato());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idContratoVencimiento", getIdContratoVencimiento());
		regresar.put("pagare", getPagare());
		regresar.put("importe", getImporte());
		regresar.put("referencia", getReferencia());
		regresar.put("idBanco", getIdBanco());
		regresar.put("fechaPago", getFechaPago());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdTipoMedioPago(), getIdUsuario(), getIdContrato(), getObservaciones(), getIdContratoVencimiento(), getPagare(), getImporte(), getReferencia(), getIdBanco(), getFechaPago(), getRegistro()
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
    regresar.append("idContratoVencimiento~");
    regresar.append(getIdContratoVencimiento());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoVencimiento());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosVecimientosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoVencimiento()!= null && getIdContratoVencimiento()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosVecimientosDto other = (TcKeetContratosVecimientosDto) obj;
    if (getIdContratoVencimiento() != other.idContratoVencimiento && (getIdContratoVencimiento() == null || !getIdContratoVencimiento().equals(other.idContratoVencimiento))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoVencimiento() != null ? getIdContratoVencimiento().hashCode() : 0);
    return hash;
  }

}


