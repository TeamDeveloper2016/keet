package mx.org.kaana.keet.ingresos.beans;

import java.io.Serializable;
import mx.org.kaana.keet.db.dto.TcKeetContratosDeduccionesDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 21/07/2021
 *@time 11:47:08 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Retencion extends TcKeetContratosDeduccionesDto implements Serializable {

  private static final long serialVersionUID = 8747773782762554803L;

  private Boolean activo;
  private Double importe;

  public Retencion() {
    this(-1L);
  }

  public Retencion(Long key) {
    this(null, key, null, null, null, null, null, null, 0D, 5D);
  }

  public Retencion(String descripcion, Long idContratoRetencion, Long idUsuario, Long idContrato, String alias, Long orden, String nombre, String campo, Double porecentaje, Double limite) {
    super(descripcion, idContratoRetencion, idUsuario, idContrato, alias, orden, nombre, campo, porecentaje, limite);
    this.importe= 0D;
    this.activo = Boolean.FALSE;
  }
  
  public Boolean getActivo() {
    return activo;
  }

  public void setActivo(Boolean activo) {
    this.activo = activo;
  }

  public Double getImporte() {
    return importe;
  }

  public void setImporte(Double importe) {
    this.importe = importe;
  }
 
  @Override
  public Class toHbmClass() {
    return TcKeetContratosDeduccionesDto.class;
  }
  
}
