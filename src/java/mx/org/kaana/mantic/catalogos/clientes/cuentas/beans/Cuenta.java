package mx.org.kaana.mantic.catalogos.clientes.cuentas.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 18/07/2021
 *@time 01:11:49 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Cuenta implements Serializable {

  private static final long serialVersionUID = -541004370946588619L;

  private Long idClienteDeuda;
  private Long idCliente;
  private Long idDesarrollo;
  private String desarrollo;
  private String ticket;
  private String consecutivo;
  private Long idContrato;
  private String clave;
  private String contrato;
  private String tipo;
  private Long idManual;
  private Double importe;
  private Double saldo;
  private String persona;
  private LocalDate limite;
  private LocalDateTime registro;
  private Long dias;
  private String razonSocial;
  private Long idClienteDeudaEstatus;
  private Long idVenta;
  private String estatus;
  private Double total;
  private Boolean activo;
  private String factura;

  public Cuenta() {
   this(-1L, -1L, -1L, null, null, null, -1L, null, null, null, -1L, 0D, 0D, null, LocalDate.now(), LocalDateTime.now(), 0L, null, -1L, -1L, null, 0D, Boolean.FALSE, "");
  }
  
  public Cuenta(Long idClienteDeuda, Long idCliente, Long idDesarrollo, String desarrollo, String ticket, String consecutivo, Long idContrato, String clave, String contrato, String tipo, Long idManual, Double importe, Double saldo, String persona, LocalDate limite, LocalDateTime registro, Long dias, String razonSocial, Long idClienteDeudaEstatus, Long idVenta, String estatus, Double total, Boolean activo, String factura) {
    this.idClienteDeuda = idClienteDeuda;
    this.idCliente = idCliente;
    this.idDesarrollo = idDesarrollo;
    this.desarrollo = desarrollo;
    this.ticket = ticket;
    this.consecutivo = consecutivo;
    this.idContrato = idContrato;
    this.clave = clave;
    this.contrato = contrato;
    this.tipo = tipo;
    this.idManual = idManual;
    this.importe = importe;
    this.saldo = saldo;
    this.persona = persona;
    this.limite = limite;
    this.registro = registro;
    this.dias = dias;
    this.razonSocial = razonSocial;
    this.idClienteDeudaEstatus = idClienteDeudaEstatus;
    this.idVenta = idVenta;
    this.estatus = estatus;
    this.total = total;
    this.activo = activo;
    this.factura= factura;
  }

  public Long getIdClienteDeuda() {
    return idClienteDeuda;
  }

  public void setIdClienteDeuda(Long idClienteDeuda) {
    this.idClienteDeuda = idClienteDeuda;
  }

  public Long getIdCliente() {
    return idCliente;
  }

  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }

  public String getDesarrollo() {
    return desarrollo;
  }

  public void setDesarrollo(String desarrollo) {
    this.desarrollo = desarrollo;
  }

  public String getTicket() {
    return ticket;
  }

  public void setTicket(String ticket) {
    this.ticket = ticket;
  }

  public String getConsecutivo() {
    return consecutivo;
  }

  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public Long getIdContrato() {
    return idContrato;
  }

  public void setIdContrato(Long idContrato) {
    this.idContrato = idContrato;
  }

  public String getClave() {
    return clave;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getContrato() {
    return contrato;
  }

  public void setContrato(String contrato) {
    this.contrato = contrato;
  }

  public String getTipo() {
    return tipo;
  }

  public void setTipo(String tipo) {
    this.tipo = tipo;
  }

  public Long getIdManual() {
    return idManual;
  }

  public void setIdManual(Long idManual) {
    this.idManual = idManual;
  }

  public Double getImporte() {
    return importe;
  }

  public void setImporte(Double importe) {
    this.importe = importe;
  }

  public Double getSaldo() {
    return saldo;
  }

  public void setSaldo(Double saldo) {
    this.saldo = saldo;
  }

  public String getPersona() {
    return persona;
  }

  public void setPersona(String persona) {
    this.persona = persona;
  }

  public LocalDate getLimite() {
    return limite;
  }

  public void setLimite(LocalDate limite) {
    this.limite = limite;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public Long getDias() {
    return dias;
  }

  public void setDias(Long dias) {
    this.dias = dias;
  }

  public String getRazonSocial() {
    return razonSocial;
  }

  public void setRazonSocial(String razonSocial) {
    this.razonSocial = razonSocial;
  }

  public Long getIdClienteDeudaEstatus() {
    return idClienteDeudaEstatus;
  }

  public void setIdClienteDeudaEstatus(Long idClienteDeudaEstatus) {
    this.idClienteDeudaEstatus = idClienteDeudaEstatus;
  }

  public Long getIdVenta() {
    return idVenta;
  }

  public void setIdVenta(Long idVenta) {
    this.idVenta = idVenta;
  }

  public String getEstatus() {
    return estatus;
  }

  public void setEstatus(String estatus) {
    this.estatus = estatus;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Boolean getActivo() {
    return activo;
  }

  public void setActivo(Boolean activo) {
    this.activo = activo;
  }

  public String getFactura() {
    return factura;
  }

  public void setFactura(String factura) {
    this.factura = factura;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 41 * hash + Objects.hashCode(this.idClienteDeuda);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Cuenta other = (Cuenta) obj;
    if (!Objects.equals(this.idClienteDeuda, other.idClienteDeuda)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Cuenta{" + "idClienteDeuda=" + idClienteDeuda + ", idCliente=" + idCliente + ", idDesarrollo=" + idDesarrollo + ", desarrollo=" + desarrollo + ", ticket=" + ticket + ", consecutivo=" + consecutivo + ", idContrato=" + idContrato + ", clave=" + clave + ", contrato=" + contrato + ", tipo=" + tipo + ", idManual=" + idManual + ", importe=" + importe + ", saldo=" + saldo + ", persona=" + persona + ", limite=" + limite + ", registro=" + registro + ", dias=" + dias + ", razonSocial=" + razonSocial + ", idClienteDeudaEstatus=" + idClienteDeudaEstatus + ", idVenta=" + idVenta + ", estatus=" + estatus + ", total=" + total + ", activo=" + activo + '}';
  }
 
  public Entity toEntity() {
    Entity regresar= new Entity(this.idClienteDeuda);
    regresar.put("idClienteDeuda", new Value("idClienteDeuda", this.idClienteDeuda));
    regresar.put("idCliente", new Value("idCliente", this.idCliente));
    regresar.put("idVenta", new Value("idVenta", this.idVenta));
    regresar.put("saldo", new Value("saldo", this.saldo));
    regresar.put("importe", new Value("importe", this.importe));
    regresar.put("pago", new Value("pago", this.total));
    regresar.put("activo", new Value("activo", this.activo));
    return regresar;
  }
  
}
