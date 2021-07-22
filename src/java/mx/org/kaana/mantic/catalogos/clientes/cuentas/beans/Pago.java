package mx.org.kaana.mantic.catalogos.clientes.cuentas.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 18/07/2021
 *@time 01:11:49 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Pago implements Serializable {

  private static final long serialVersionUID = 4325297393712414523L;
  
   private String folio;
   private Double total;
   private Long idTipoMedioPago;
   private String tipoMedioPago;
   private Long idTipoPago;
   private String tipoPago;
   private Long idUsoCfdi;
   private Long idTipoComprobante;
   private String serie;
   private LocalDateTime timbrado;
   private Long idBanco;
   private String referencia;
   private LocalDate fecha;
   private String observaciones;
   private Double importe;

  public Pago() {
    this(null, 0D, -1L, null, -1L, null, -1L, -1L, null, LocalDateTime.now(), -1L, null, LocalDate.now(), null, 0D);
  }

  public Pago(String folio, Double total, Long idTipoMedioPago, String tipoMedioPago, Long idTipoPago, String tipoPago, Long idUsoCfdi, Long idTipoComprobante, String serie, LocalDateTime timbrado, Long idBanco, String referencia, LocalDate fecha, String observaciones, Double importe) {
    this.folio = folio;
    this.total = total;
    this.idTipoMedioPago = idTipoMedioPago;
    this.tipoMedioPago = tipoMedioPago;
    this.idTipoPago = idTipoPago;
    this.tipoPago = tipoPago;
    this.idUsoCfdi = idUsoCfdi;
    this.idTipoComprobante = idTipoComprobante;
    this.serie = serie;
    this.timbrado = timbrado;
    this.idBanco = idBanco;
    this.referencia = referencia;
    this.fecha = fecha;
    this.observaciones = observaciones;
    this.importe= importe;
  }

  
  public String getFolio() {
    return folio;
  }

  public void setFolio(String folio) {
    this.folio = folio;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Long getIdTipoMedioPago() {
    return idTipoMedioPago;
  }

  public void setIdTipoMedioPago(Long idTipoMedioPago) {
    this.idTipoMedioPago = idTipoMedioPago;
  }

  public String getTipoMedioPago() {
    return tipoMedioPago;
  }

  public void setTipoMedioPago(String tipoMedioPago) {
    this.tipoMedioPago = tipoMedioPago;
  }

  public Long getIdTipoPago() {
    return idTipoPago;
  }

  public void setIdTipoPago(Long idTipoPago) {
    this.idTipoPago = idTipoPago;
  }

  public String getTipoPago() {
    return tipoPago;
  }

  public void setTipoPago(String tipoPago) {
    this.tipoPago = tipoPago;
  }

  public Long getIdUsoCfdi() {
    return idUsoCfdi;
  }

  public void setIdUsoCfdi(Long idUsoCfdi) {
    this.idUsoCfdi = idUsoCfdi;
  }

  public Long getIdTipoComprobante() {
    return idTipoComprobante;
  }

  public void setIdTipoComprobante(Long idTipoComprobante) {
    this.idTipoComprobante = idTipoComprobante;
  }

  public String getSerie() {
    return serie;
  }

  public void setSerie(String serie) {
    this.serie = serie;
  }

  public LocalDateTime getTimbrado() {
    return timbrado;
  }

  public void setTimbrado(LocalDateTime timbrado) {
    this.timbrado = timbrado;
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

  public LocalDate getFecha() {
    return fecha;
  }

  public void setFecha(LocalDate fecha) {
    this.fecha = fecha;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public Double getImporte() {
    return importe;
  }

  public void setImporte(Double importe) {
    this.importe = importe;
  }

  public void addImporte(Double importe) {
    this.importe+= importe;
  }
  
  @Override
  public int hashCode() {
    int hash = 3;
    hash = 89 * hash + Objects.hashCode(this.folio);
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
    final Pago other = (Pago) obj;
    if (!Objects.equals(this.folio, other.folio)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Pago{" + "folio=" + folio + ", total=" + total + ", idTipoMedioPago=" + idTipoMedioPago + ", tipoMedioPago=" + tipoMedioPago + ", idTipoPago=" + idTipoPago + ", tipoPago=" + tipoPago + ", idUsoCfdi=" + idUsoCfdi + ", idTipoComprobante=" + idTipoComprobante + ", serie=" + serie + ", timbrado=" + timbrado + '}';
  }
  
}
