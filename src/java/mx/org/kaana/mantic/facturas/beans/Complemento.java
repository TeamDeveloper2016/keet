package mx.org.kaana.mantic.facturas.beans;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 21/02/2021
 *@time 03:59:56 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Complemento extends ClienteFactura implements Serializable {

  private static final long serialVersionUID = -4988295393851747394L;
 
  private LocalDateTime fechaPago;
  private String moneda;
  private Double total;

  public LocalDateTime getFechaPago() {
    return fechaPago;
  }

  public void setFechaPago(LocalDateTime fechaPago) {
    this.fechaPago = fechaPago;
  }

  public String getMoneda() {
    return moneda;
  }

  public void setMoneda(String moneda) {
    this.moneda = moneda;
  }

  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }
    
}
