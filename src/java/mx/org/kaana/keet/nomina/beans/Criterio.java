package mx.org.kaana.keet.nomina.beans;

import java.io.Serializable;
import java.time.LocalDate;
import mx.org.kaana.kajool.db.comun.sql.Entity;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 15/06/2022
 * @time 11:15:09 AM 
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Criterio implements Serializable {

  private static final long serialVersionUID = 1851743938707869405L;

  private String lote;
  private Long idTipo;
  private Long idExtra;
  private Double porcentaje;
  private Double costo;
  private Double anticipo;
  private LocalDate hoy;
  private Long idNomina;
  private String semana;
  private Long actual;
  private Entity datos;
  //------------------------

  public Criterio(String lote, Long idTipo, Long idExtra, Double porcentaje, Double costo, Double anticipo, Long idNomina, String semana, Long actual, Entity datos) {
    this.lote      = lote; 
    this.idTipo    = idTipo;
    this.idExtra   = idExtra; 
    this.porcentaje= porcentaje; 
    this.costo     = costo; 
    this.anticipo  = anticipo; 
    this.hoy       = LocalDate.now();
    this.idNomina  = idNomina;
    this.semana    = semana;
    this.actual    = actual;
    this.datos     = datos;
  }

  public String getLote() {
    return lote;
  }

  public void setLote(String lote) {
    this.lote = lote;
  }

  public Long getIdTipo() {
    return idTipo;
  }

  public void setIdTipo(Long idTipo) {
    this.idTipo = idTipo;
  }

  public Long getIdExtra() {
    return idExtra;
  }

  public void setIdExtra(Long idExtra) {
    this.idExtra = idExtra;
  }

  public Double getPorcentaje() {
    return porcentaje;
  }

  public void setPorcentaje(Double porcentaje) {
    this.porcentaje = porcentaje;
  }

  public Double getCosto() {
    return costo;
  }

  public void setCosto(Double costo) {
    this.costo = costo;
  }

  public Double getAnticipo() {
    return anticipo;
  }

  public void setAnticipo(Double anticipo) {
    this.anticipo = anticipo;
  }

  public LocalDate getHoy() {
    return hoy;
  }

  public void setHoy(LocalDate hoy) {
    this.hoy = hoy;
  }

  public Long getIdNomina() {
    return idNomina;
  }

  public void setIdNomina(Long idNomina) {
    this.idNomina = idNomina;
  }

  public String getSemana() {
    return semana;
  }

  public void setSemana(String semana) {
    this.semana = semana;
  }

  public Long getActual() {
    return actual;
  }

  public void setActual(Long actual) {
    this.actual = actual;
  }

  public Entity getDatos() {
    return datos;
  }

  public void setDatos(Entity datos) {
    this.datos = datos;
  }

  @Override
  public String toString() {
    return "Criterio{" + "lote=" + lote + ", idTipo=" + idTipo + ", idExtra=" + idExtra + ", porcentaje=" + porcentaje + ", costo=" + costo + ", anticipo=" + anticipo + ", hoy=" + hoy + ", idNomina=" + idNomina + ", semana=" + semana + ", actual=" + actual + ", datos=" + datos + '}';
  }

}
