package mx.org.kaana.keet.nomina.beans;

import java.io.Serializable;
import java.util.Objects;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 24/07/2023
 *@time 08:18:52 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Corte implements Serializable {
  
  private static final long serialVersionUID = 5998563328450165517L;
  
  private Long idContrato;
  private Long idNomina;
  private Double costo;
  private Double destajos;
  private Double subContratados;
  private Double porElDia;
  private Double destajo;
  private Double porDia;

  public Corte(Long idContrato, Long idNomina) {
    this(idContrato, idNomina, 0D, 0D, 0D, 0D, 0D, 0D);
  }

  public Corte(Long idContrato, Long idNomina, Double destajo, Double porDia, Double costo, Double destajos, Double subContratados, Double porElDia) {
    this.idContrato = idContrato;
    this.idNomina = idNomina;
    this.destajo = destajo;
    this.porDia = porDia;
    this.costo = costo;
    this.destajos = destajos;
    this.subContratados = subContratados;
    this.porElDia = porElDia;
  }

  public Long getIdContrato() {
    return idContrato;
  }

  public void setIdContrato(Long idContrato) {
    this.idContrato = idContrato;
  }

  public Long getIdNomina() {
    return idNomina;
  }

  public void setIdNomina(Long idNomina) {
    this.idNomina = idNomina;
  }

  public Double getDestajo() {
    return destajo;
  }

  public void setDestajo(Double destajo) {
    this.destajo = destajo;
  }

  public Double getPorDia() {
    return porDia;
  }

  public void setPorDia(Double porDia) {
    this.porDia = porDia;
  }

  public void addDestajo(Double destajo) {
    this.destajo+= destajo;
  }
  
  public void addPorDia(Double porDia) {
    this.porDia+= porDia;
  }

  public Double getCosto() {
    return costo;
  }

  public void setCosto(Double costo) {
    this.costo = costo;
  }

  public Double getDestajos() {
    return destajos;
  }

  public void setDestajos(Double destajos) {
    this.destajos = destajos;
  }

  public Double getSubContratados() {
    return subContratados;
  }

  public void setSubContratados(Double subContratados) {
    this.subContratados = subContratados;
  }

  public Double getPorElDia() {
    return porElDia;
  }

  public void setPorElDia(Double porElDia) {
    this.porElDia = porElDia;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 79 * hash + Objects.hashCode(this.idContrato);
    hash = 79 * hash + Objects.hashCode(this.idNomina);
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
    final Corte other = (Corte) obj;
    if (!Objects.equals(this.idContrato, other.idContrato)) {
      return false;
    }
    if (!Objects.equals(this.idNomina, other.idNomina)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Corte{" + "idContrato=" + idContrato + ", idNomina=" + idNomina + ", costo=" + costo + ", destajos=" + destajos + ", subContratados=" + subContratados + ", porElDia=" + porElDia + ", destajo=" + destajo + ", porDia=" + porDia + '}';
  }
   
}
