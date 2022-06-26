package mx.org.kaana.keet.nomina.beans;

import java.io.Serializable;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcNominasContratosCostosDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 25/06/2022
 *@time 12:26:50 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Contrato extends TcNominasContratosCostosDto implements Serializable {

  private static final long serialVersionUID = -2438191491652242045L;
  
  private ESql sql;
  private String semana;
  private String desarrollo;
  private String clave;
  private String contrato;
  private String etapa;

  public Contrato() {
    this.sql= ESql.INSERT;
  }

  public String getSemana() {
    return semana;
  }

  public void setSemana(String semana) {
    this.semana = semana;
  }

  public String getDesarrollo() {
    return desarrollo;
  }

  public void setDesarrollo(String desarrollo) {
    this.desarrollo = desarrollo;
  }

  public String getClave() {
    return clave;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getContrato() {
    return contrato;
  }

  public void setContrato(String contrato) {
    this.contrato = contrato;
  }

  public String getEtapa() {
    return etapa;
  }

  public void setEtapa(String etapa) {
    this.etapa = etapa;
  }

  public ESql getSql() {
    return sql;
  }

  public void setSql(ESql sql) {
    this.sql = sql;
  }

  @Override
  public Class toHbmClass() {
    return TcNominasContratosCostosDto.class;
  }

  @Override
  public String toString() {
    return "Contrato{" + "sql=" + sql + ", semana=" + semana + ", desarrollo=" + desarrollo + ", clave=" + clave + ", contrato=" + contrato + ", etapa=" + etapa + '}';
  }

}
