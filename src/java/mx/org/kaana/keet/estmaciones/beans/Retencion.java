package mx.org.kaana.keet.estmaciones.beans;

import java.io.Serializable;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetEstimacionesDetallesDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 13/03/2022
 *@time 01:35:11 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Retencion extends TcKeetEstimacionesDetallesDto implements Serializable {

  private static final long serialVersionUID = 3309738199148265011L;

  private ESql sql;
  private String nombre;
  private String alias;
  private Double limite;

  public Retencion() {
    this.sql= ESql.INSERT;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getAlias() {
    return alias;
  }

  public void setAlias(String alias) {
    this.alias = alias;
  }

  public Double getLimite() {
    return limite;
  }

  public void setLimite(Double limite) {
    this.limite = limite;
  }

  public ESql getSql() {
    return sql;
  }

  public void setSql(ESql sql) {
    this.sql = sql;
  }
  
}
