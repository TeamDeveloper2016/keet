package mx.org.kaana.keet.catalogos.contratos.beans;

import java.io.Serializable;
import java.util.Objects;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.catalogos.contratos.reglas.IDatoContrato;
import mx.org.kaana.keet.db.dto.TcKeetContratosEtapasDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 28/08/2023
 *@time 04:01:38 PM 
*@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Etapa extends TcKeetContratosEtapasDto implements IDatoContrato, Serializable {

  private static final long serialVersionUID = -3231400763871407061L;

  private String etapa;
  private ESql sql;

  public Etapa() {
    this(-1L);  
  }
  
  public Etapa(Long idContrato) {
    this.setIdContrato(idContrato);
    this.sql= ESql.INSERT;
  }  
  
  @Override
  public ESql getSql() {
    return sql;
  }

  @Override
  public void setSql(ESql sql) {
    this.sql = sql;
  }
  
  @Override
  public void setDescripcion(String descripcion) {
    super.setDescripcion(descripcion);
  }
  
  @Override
  public String getDescripcion() {
    return super.getDescripcion();
  }

  public String getEtapa() {
    return etapa;
  }

  public void setEtapa(String etapa) {
    this.etapa = etapa;
  }

  @Override
  public int hashCode() {
    int hash = 5;
    hash = 59 * hash + Objects.hashCode(this.getIdContrato());
    hash = 59 * hash + Objects.hashCode(this.getIdEtapa());
    hash = 59 * hash + Objects.hashCode(this.getDescripcion());
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
    final Etapa other = (Etapa) obj;
    if (!Objects.equals(this.getDescripcion(), other.getDescripcion())) {
      return false;
    }
    if (!Objects.equals(this.getIdContrato(), other.getIdContrato())) {
      return false;
    }
    if (!Objects.equals(this.getIdEtapa(), other.getIdEtapa())) {
      return false;
    }
    return true;
  }
  
}
