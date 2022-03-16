package mx.org.kaana.keet.catalogos.contratos.beans;

import java.io.Serializable;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetContratosRetencionesDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 16/03/2022
 *@time 01:35:11 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Retencion extends TcKeetContratosRetencionesDto implements Serializable {

  private static final long serialVersionUID = 3309738199148265011L;

  private ESql sql;
  private String alias;
  private Boolean activo;

  public Retencion() {
    this.sql= ESql.INSERT;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public ESql getSql() {
    return sql;
  }

  public void setSql(ESql sql) {
    this.sql = sql;
  }

  public Boolean getActivo() {
    return activo;
  }

  public void setActivo(Boolean activo) {
    this.activo = activo;
  }
  
}
