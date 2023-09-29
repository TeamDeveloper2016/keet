package mx.org.kaana.keet.paquetes.beans;

import java.io.Serializable;
import java.util.Objects;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetPaquetesDetallesDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 25/09/2023
 *@time 11:08:38 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx
 */

public class Material extends TcKeetPaquetesDetallesDto implements Serializable {

  private static final long serialVersionUID = 3518579943910403842L;
  
  private String codigo;
  private String nombre;
  private ESql sql;

  public Material() {
    this(-1L);
  }

  public Material(Long idArticulo) {
    super();
    this.setIdArticulo(idArticulo);
    this.sql= ESql.INSERT;
  }

  public String getCodigo() {
    return codigo;
  }

  public void setCodigo(String codigo) {
    this.codigo = codigo;
  }
  
  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
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
