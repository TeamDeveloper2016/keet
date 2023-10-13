package mx.org.kaana.keet.entregas.beans;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetEntregasDetallesDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 09/10/2023
 *@time 20:42:38 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx
 */

public class Material extends TcKeetEntregasDetallesDto implements Serializable {

  private static final long serialVersionUID = 3518579943910403842L;

  private Long idPaquete;  
  private Double cuantos;  
  private ESql sql;

  public Material() {
    this((new Random().nextLong())* -1L);
  }

  public Material(Long idArticulo) {
    super();
    this.setIdArticulo(idArticulo);
    this.sql= ESql.INSERT;
  }

  public Long getIdPaquete() {
    return idPaquete;
  }

  public void setIdPaquete(Long idPaquete) {
    this.idPaquete = idPaquete;
  }

  public Double getCuantos() {
    return cuantos;
  }

  public void setCuantos(Double cuantos) {
    this.cuantos = cuantos;
  }

  public ESql getSql() {
    return sql;
  }

  public void setSql(ESql sql) {
    this.sql = sql;
  }
  
  @Override
  public int hashCode() {
    int hash = 7;
    hash = 79* hash+ Objects.hashCode(this.getIdArticulo());
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
    final Material other = (Material) obj;
    if (!Objects.equals(this.getIdArticulo(), other.getIdArticulo())) 
      return false;
    return true;
  }
  
}
