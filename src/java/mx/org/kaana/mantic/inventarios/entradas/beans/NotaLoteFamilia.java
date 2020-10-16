package mx.org.kaana.mantic.inventarios.entradas.beans;

import java.io.Serializable;
import java.util.Objects;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 15/10/2020
 *@time 06:27:02 PM 
 *@author Team Developer 2016 <team.developer2016@gmail.com>
 */

public final class NotaLoteFamilia implements Serializable {

  private Long idNotaContratoLote;
  private Long idFamilia;
  private Long idContratoLote;
  private Long idNotaEntrada;
  private Boolean existe;

  public NotaLoteFamilia() {
    this(-1L, -1L, -1L, -1L);
  }
  
  public NotaLoteFamilia(Long idNotaContratoLote, Long idFamilia, Long idContratoLote, Long idNotaEntrada) {
    this.idNotaContratoLote = idNotaContratoLote;
    this.idFamilia = idFamilia;
    this.idContratoLote = idContratoLote;
    this.idNotaEntrada = idNotaEntrada;
    this.existe = Boolean.FALSE;
  }

  public Long getIdNotaContratoLote() {
    return idNotaContratoLote;
  }

  public void setIdNotaContratoLote(Long idNotaContratoLote) {
    this.idNotaContratoLote = idNotaContratoLote;
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

  public Long getIdNotaEntrada() {
    return idNotaEntrada;
  }

  public void setIdNotaEntrada(Long idNotaEntrada) {
    this.idNotaEntrada = idNotaEntrada;
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
    hash = 53 * hash + Objects.hashCode(this.idFamilia);
    hash = 53 * hash + Objects.hashCode(this.idContratoLote);
    hash = 53 * hash + Objects.hashCode(this.idNotaEntrada);
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
    final NotaLoteFamilia other = (NotaLoteFamilia) obj;
    if (!Objects.equals(this.idFamilia, other.idFamilia)) {
      return false;
    }
    if (!Objects.equals(this.idContratoLote, other.idContratoLote)) {
      return false;
    }
    if (!Objects.equals(this.idNotaEntrada, other.idNotaEntrada)) {
      return false;
    }
    return true;
  }

}
