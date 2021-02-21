package mx.org.kaana.mantic.facturas.enums;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 21/02/2021
 *@time 04:44:56 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public enum ETiposComprobantes {
  FACTURA(1L), NOTA_CREDITO(2L), COMPLEMENTO_PAGO(3L);
   
  private Long idTipoComprobante;

  private ETiposComprobantes(Long idTipoComprobante) {
    this.idTipoComprobante = idTipoComprobante;
  }

  public Long getIdTipoComprobante() {
    return idTipoComprobante;
  }
  
}
