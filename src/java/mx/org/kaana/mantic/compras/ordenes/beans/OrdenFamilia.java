package mx.org.kaana.mantic.compras.ordenes.beans;

import java.util.Objects;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 12/04/2023
 *@time 00:23:02 AM
 *@author Team Developer 2016 <team.developer2016@gmail.com>
 */

public class OrdenFamilia {

  private Long idOrdenFamilia;
  private Long idFamilia;
  private Long idOrdenCompra;
  private Boolean existe;

  public OrdenFamilia() {
    this(-1L, -1L, -1L);
  }

  public OrdenFamilia(Long idOrdenFamilia, Long idFamilia, Long idOrdenCompra) {
    this.idOrdenFamilia = idOrdenFamilia;
    this.idFamilia = idFamilia;
    this.idOrdenCompra = idOrdenCompra;
    this.existe= Boolean.FALSE;
  }

  public Long getIdOrdenFamilia() {
    return idOrdenFamilia;
  }

  public void setIdOrdenFamilia(Long idOrdenFamilia) {
    this.idOrdenFamilia = idOrdenFamilia;
  }

  public Long getIdFamilia() {
    return idFamilia;
  }

  public void setIdFamilia(Long idFamilia) {
    this.idFamilia = idFamilia;
  }

  public Long getIdOrdenCompra() {
    return idOrdenCompra;
  }

  public void setIdOrdenCompra(Long idOrdenCompra) {
    this.idOrdenCompra = idOrdenCompra;
  }

  public Boolean getExiste() {
    return existe;
  }

  public void setExiste(Boolean existe) {
    this.existe = existe;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + Objects.hashCode(this.idFamilia);
    hash = 67 * hash + Objects.hashCode(this.idOrdenCompra);
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
    final OrdenFamilia other = (OrdenFamilia) obj;
    if (!Objects.equals(this.idFamilia, other.idFamilia)) 
      return false;
    if (!Objects.equals(this.idOrdenCompra, other.idOrdenCompra)) 
      return false;
    return true;
  }

}
