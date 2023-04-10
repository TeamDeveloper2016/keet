package mx.org.kaana.keet.compras.beans;

import java.io.Serializable;
import java.util.Objects;
import mx.org.kaana.keet.db.dto.TcKeetOrdenesMaterialesDto;
import mx.org.kaana.libs.pagina.JsfBase;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 8/04/2023
 *@time 08:50:37 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Material extends TcKeetOrdenesMaterialesDto implements Serializable {

  private static final long serialVersionUID = -8548824096384382152L;

  protected Long idContrato;
  protected String contrato;
  protected String clave;
  protected Long idPrototipo;
  protected String prototipo;
  protected Double cuantos;
  protected Double costo;
  protected Double factor;
  protected Double diferencia;
  protected Double comprados;
  protected Double total;
  protected Boolean modificado;
  
  public Material(String descripcion, Double precioUnitario, String codigo, Long idUsuario, Long idOrdenCompra, Long idContratoLote, Double cantidad, Long idOrdenMaterial, Long idArticulo) {
    super(descripcion, precioUnitario, codigo, idUsuario, idOrdenCompra, idContratoLote, cantidad, idOrdenMaterial, idArticulo);
    this.idContrato = -1L;
    this.clave      = "";
    this.contrato   = "";
    this.idPrototipo= -1L;
    this.prototipo  = "";
    this.cuantos    = cantidad;
    this.costo      = precioUnitario;   
    this.factor     = 0D;
    this.comprados  = 0D;
    this.diferencia = 0D;
    this.total      = 0D;
    this.modificado = Boolean.FALSE;
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

  public Long getIdPrototipo() {
    return idPrototipo;
  }

  public void setIdPrototipo(Long idPrototipo) {
    this.idPrototipo = idPrototipo;
  }

  public String getPrototipo() {
    return prototipo;
  }

  public void setPrototipo(String prototipo) {
    this.prototipo = prototipo;
  }
  
  public Double getCuantos() {
    return cuantos;
  }

  public void setCuantos(Double cuantos) {
    this.cuantos = cuantos;
  }

  public Double getCosto() {
    return costo;
  }

  public void setCosto(Double costo) {
    this.costo = costo;
  }

  public Double getFactor() {
    return factor;
  }

  public void setFactor(Double factor) {
    this.factor = factor;
  }

  public Double getDiferencia() {
    return diferencia;
  }

  public void setDiferencia(Double diferencia) {
    this.diferencia = diferencia;
  }

  public Double getComprados() {
    return comprados;
  }

  public void setComprados(Double comprados) {
    this.comprados = comprados;
  }
  
  public Double getTotal() {
    return total;
  }

  public void setTotal(Double total) {
    this.total = total;
  }

  public Boolean getModificado() {
    return modificado;
  }

  public void setModificado(Boolean modificado) {
    this.modificado = modificado;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 79 * hash + Objects.hashCode(this.getIdArticulo());
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) 
      return true;
    if (obj == null) 
      return false;
    if (getClass() != obj.getClass()) 
      return false;
    final Individual other = (Individual)obj;
    if (!Objects.equals(this.getIdArticulo(), other.getIdArticulo())) 
      return false;
    return true;
  }
   
  @Override
  public Class toHbmClass() {
    return TcKeetOrdenesMaterialesDto.class;
  }

}
