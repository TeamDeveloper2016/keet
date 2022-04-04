package mx.org.kaana.keet.catalogos.contratos.beans;

import java.io.Serializable;
import java.util.Objects;
import java.util.Random;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetContratosGarantiasDto;
import mx.org.kaana.libs.pagina.JsfBase;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 3/04/2022
 *@time 01:45:59 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Garantia extends TcKeetContratosGarantiasDto implements Serializable {
  
  private static final long serialVersionUID = -7360439132690962055L;
  
  private ESql sql;
  private String igual;

  public Garantia() {
    this(-1L);
  }
  
  public Garantia(Long idContrato) {
    super("", JsfBase.getIdUsuario(), idContrato, 0D, (new Random().nextLong())* -1L);
    this.sql= ESql.INSERT;
  }
 
  public ESql getSql() {
    return sql;
  }

  public void setSql(ESql sql) {
    this.sql = sql;
  }

  public String getIgual() {
    return igual;
  }

  public void setIgual(String igual) {
    this.igual = igual;
  }

  @Override
  public void setDescripcion(String descripcion) {
    super.setDescripcion(descripcion);
    this.igual= descripcion;
  }

  
  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + Objects.hashCode(this.igual);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Garantia other = (Garantia) obj;
    if (!Objects.equals(this.igual, other.igual)) {
      return false;
    }
    return true;
  }
  
}
