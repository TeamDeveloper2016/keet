package mx.org.kaana.keet.ingresos.beans;

import java.io.Serializable;
import java.time.LocalDate;
import mx.org.kaana.mantic.db.dto.TcManticFacturasDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 11/09/2021
 *@time 12:47:28 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Factura extends TcManticFacturasDto implements Serializable {

  private static final long serialVersionUID = 5490576357349038274L;
  
  private LocalDate vencimiento;

  public Factura() {
    this.vencimiento= LocalDate.now();
  }
  
  public LocalDate getVencimiento() {
    return vencimiento;
  }

  public void setVencimiento(LocalDate vencimiento) {
    this.vencimiento = vencimiento;
  }

  @Override
  public Class toHbmClass() {
    return TcManticFacturasDto.class; 
  }
  
}
