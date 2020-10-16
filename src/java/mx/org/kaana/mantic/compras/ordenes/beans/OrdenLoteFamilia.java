package mx.org.kaana.mantic.compras.ordenes.beans;

import java.util.Objects;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 15/10/2020
 *@time 06:27:02 PM 
 *@author Team Developer 2016 <team.developer2016@gmail.com>
 */

public class OrdenLoteFamilia {

  private Long idOrdenContratoLote;
  private Long idFamilia;
  private Long idContratoLote;
  private Long idOrdenCompra;
  private Boolean existe;

  public OrdenLoteFamilia() {
    this(-1L, -1L, -1L, -1L);
  }

  public OrdenLoteFamilia(Long idOrdenContratoLote, Long idFamilia, Long idContratoLote, Long idOrdenCompra) {
    this.idOrdenContratoLote = idOrdenContratoLote;
    this.idFamilia = idFamilia;
    this.idContratoLote = idContratoLote;
    this.idOrdenCompra = idOrdenCompra;
    this.existe= Boolean.FALSE;
  }

  public Long getIdOrdenContratoLote() {
    return idOrdenContratoLote;
  }

  public void setIdOrdenContratoLote(Long idOrdenContratoLote) {
    this.idOrdenContratoLote = idOrdenContratoLote;
  }

  public Long getIdFamilia() {
    return idFamilia;
  }

  public void setIdFamilia(Long idFamilia) {
    this.idFamilia = idFamilia;
  }

  public Long getIdContratoLote() {
    return idContratoLote;
  }

  public void setIdContratoLote(Long idContratoLote) {
    this.idContratoLote = idContratoLote;
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
    hash = 67 * hash + Objects.hashCode(this.idContratoLote);
    hash = 67 * hash + Objects.hashCode(this.idOrdenCompra);
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
    final OrdenLoteFamilia other = (OrdenLoteFamilia) obj;
    if (!Objects.equals(this.idFamilia, other.idFamilia)) {
      return false;
    }
    if (!Objects.equals(this.idContratoLote, other.idContratoLote)) {
      return false;
    }
    if (!Objects.equals(this.idOrdenCompra, other.idOrdenCompra)) {
      return false;
    }
    return true;
  }

}
