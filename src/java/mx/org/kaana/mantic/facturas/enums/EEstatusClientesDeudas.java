package mx.org.kaana.mantic.facturas.enums;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 21/02/2021
 *@time 04:44:56 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public enum EEstatusClientesDeudas {
  
  INICIAL(1L), PARCIALIZADA(2L), LIQUIDADA(3L), SALDADA(4L), CANCELADA(5L);
   
  private Long idClienteDeudaEstatus;

  private EEstatusClientesDeudas(Long idClienteDeudaEstatus) {
    this.idClienteDeudaEstatus = idClienteDeudaEstatus;
  }

  public Long getIdClienteDeudaEstatus() {
    return idClienteDeudaEstatus;
  }
  
}
