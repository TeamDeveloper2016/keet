package mx.org.kaana.mantic.facturas.beans;

import java.io.Serializable;
import mx.org.kaana.keet.db.dto.TcKeetVentasPagosDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

/**
 *@company KAJOOL
 *@project KAJOOL (Control system polls)
 *@date 16/02/2021
 *@time 08:46:13 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Documento extends TcKeetVentasPagosDto implements Serializable {

  private static final long serialVersionUID = -6995718761167927034L;
  
  private UISelectEntity ikFactura;
  private String id;
  private String serie;
  private String folio;
  private String metodoPago;
  private String moneda;
  private Double tipoDeCambio;
  private Long idCliente;
  private Double global;

  public Documento() {
    this(-1L);
    this.global= 0D;
  }

  public Documento(Long key) {
    super(key);
    this.ikFactura= new UISelectEntity(-1L);
  }

  public UISelectEntity getIkFactura() {
    return ikFactura;
  }

  public void setIkFactura(UISelectEntity ikFactura) {
    this.ikFactura = ikFactura;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSerie() {
    return serie;
  }

  public void setSerie(String serie) {
    this.serie = serie;
  }

  public String getFolio() {
    return folio;
  }

  public void setFolio(String folio) {
    this.folio = folio;
  }

  public String getMetodoPago() {
    return metodoPago;
  }

  public void setMetodoPago(String metodoPago) {
    this.metodoPago = metodoPago;
  }

  public String getMoneda() {
    return moneda;
  }

  public void setMoneda(String moneda) {
    this.moneda = moneda;
  }

  public Double getTipoDeCambio() {
    return tipoDeCambio;
  }

  public void setTipoDeCambio(Double tipoDeCambio) {
    this.tipoDeCambio = tipoDeCambio;
  }

  public Long getIdCliente() {
    return idCliente;
  }

  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public Double getGlobal() {
    return global;
  }

  public void setGlobal(Double global) {
    this.global = global;
  }

  @Override
  public Class toHbmClass() {
    return TcKeetVentasPagosDto.class;
  }
  
}
