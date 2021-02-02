package mx.org.kaana.mantic.facturas.beans;

import java.io.Serializable;
import java.util.Objects;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetVentasParcialesDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 1/02/2021
 *@time 12:53:01 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public final class Parcial extends TcKeetVentasParcialesDto implements Serializable {

  private static final long serialVersionUID = 1928302496423918939L;

  private ESql sqlAccion;
  private Long idContratoLote;
  private String entidad;
  private String municipio;
  private String localidad;
  private String prototipo;
  private String manzana;
  private String lote;
  private String claveLote;
  private String calleLote;
  private String numeroLote;

  public Parcial() {
    this(-1L);
  }

  public Parcial(Long key) {
    this(key, ESql.INSERT);
    this.setIdContratoLote(key);
  }
  
  public Parcial(Long key, ESql sqlAccion) {
    super(key);
    this.setIdContratoLote(key);
    this.sqlAccion= sqlAccion;
  }

  public ESql getSqlAccion() {
    return sqlAccion;
  }

  public void setSqlAccion(ESql sqlAccion) {
    this.sqlAccion = sqlAccion;
  }

  @Override
  public Long getIdContratoLote() {
    return this.idContratoLote;
  }

  public String getEntidad() {
    return entidad;
  }

  public void setEntidad(String entidad) {
    this.entidad = entidad;
  }

  public String getMunicipio() {
    return municipio;
  }

  public void setMunicipio(String municipio) {
    this.municipio = municipio;
  }

  public String getLocalidad() {
    return localidad;
  }

  public void setLocalidad(String localidad) {
    this.localidad = localidad;
  }

  public String getPrototipo() {
    return prototipo;
  }

  public void setPrototipo(String prototipo) {
    this.prototipo = prototipo;
  }

  public String getManzana() {
    return manzana;
  }

  public void setManzana(String manzana) {
    this.manzana = manzana;
  }

  public String getLote() {
    return lote;
  }

  public void setLote(String lote) {
    this.lote = lote;
  }

  public String getClaveLote() {
    return claveLote;
  }

  public void setClaveLote(String claveLote) {
    this.claveLote = claveLote;
  }

  public String getCalleLote() {
    return calleLote;
  }

  public void setCalleLote(String calleLote) {
    this.calleLote = calleLote;
  }

  public String getNumeroLote() {
    return numeroLote;
  }

  public void setNumeroLote(String numeroLote) {
    this.numeroLote = numeroLote;
  }

  @Override
  public void setIdContratoLote(Long idContratoLote) {
    this.idContratoLote = idContratoLote;
    super.setIdContratoLote(idContratoLote);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 97 * hash + Objects.hashCode(this.idContratoLote);
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
    final Parcial other = (Parcial) obj;
    if (!Objects.equals(this.idContratoLote, other.idContratoLote)) {
      return false;
    }
    return true;
  }

  @Override
  public Class toHbmClass() {
    return TcKeetVentasParcialesDto.class;
  }

  @Override
  public String toString() {
    return String.valueOf(idContratoLote);
  }
  
}
