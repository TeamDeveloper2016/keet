package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 10/10/2016
 *@time 11:58:22 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Entity
@Table(name="tc_keet_contratos_destajos_residentes")
public class TcKeetContratosDestajosResidentesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_control_estatus")
  private Long idControlEstatus;
  @Column (name="costo")
  private Double costo;
  @Column (name="periodo")
  private Long periodo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="semana")
  private Long semana;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_destajo_residente")
  private Long idContratoDestajoResidente;
  @Column (name="id_contrato_lote_residente")
  private Long idContratoLoteResidente;
  @Column (name="id_control")
  private Long idControl;
  @Column (name="porcentaje")
  private Double porcentaje;
  @Column (name="id_nomina")
  private Long idNomina;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetContratosDestajosResidentesDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosDestajosResidentesDto(Long key) {
    this(null, null, null, null, null, new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcKeetContratosDestajosResidentesDto(Long idControlEstatus, Double costo, Long periodo, Long idUsuario, Long semana, Long idContratoDestajoResidente, Long idContratoLoteResidente, Long idControl, Double porcentaje, Long idNomina) {
    setIdControlEstatus(idControlEstatus);
    setCosto(costo);
    setPeriodo(periodo);
    setIdUsuario(idUsuario);
    setSemana(semana);
    setIdContratoDestajoResidente(idContratoDestajoResidente);
    setIdContratoLoteResidente(idContratoLoteResidente);
    setIdControl(idControl);
    setPorcentaje(porcentaje);
    setIdNomina(idNomina);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdControlEstatus(Long idControlEstatus) {
    this.idControlEstatus = idControlEstatus;
  }

  public Long getIdControlEstatus() {
    return idControlEstatus;
  }

  public void setCosto(Double costo) {
    this.costo = costo;
  }

  public Double getCosto() {
    return costo;
  }

  public void setPeriodo(Long periodo) {
    this.periodo = periodo;
  }

  public Long getPeriodo() {
    return periodo;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setSemana(Long semana) {
    this.semana = semana;
  }

  public Long getSemana() {
    return semana;
  }

  public void setIdContratoDestajoResidente(Long idContratoDestajoResidente) {
    this.idContratoDestajoResidente = idContratoDestajoResidente;
  }

  public Long getIdContratoDestajoResidente() {
    return idContratoDestajoResidente;
  }

  public void setIdContratoLoteResidente(Long idContratoLoteResidente) {
    this.idContratoLoteResidente = idContratoLoteResidente;
  }

  public Long getIdContratoLoteResidente() {
    return idContratoLoteResidente;
  }

  public void setIdControl(Long idControl) {
    this.idControl = idControl;
  }

  public Long getIdControl() {
    return idControl;
  }

  public void setPorcentaje(Double porcentaje) {
    this.porcentaje = porcentaje;
  }

  public Double getPorcentaje() {
    return porcentaje;
  }

  public void setIdNomina(Long idNomina) {
    this.idNomina = idNomina;
  }

  public Long getIdNomina() {
    return idNomina;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdContratoDestajoResidente();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoDestajoResidente = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdControlEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCosto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPeriodo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSemana());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoDestajoResidente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLoteResidente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdControl());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPorcentaje());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNomina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idControlEstatus", getIdControlEstatus());
		regresar.put("costo", getCosto());
		regresar.put("periodo", getPeriodo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("semana", getSemana());
		regresar.put("idContratoDestajoResidente", getIdContratoDestajoResidente());
		regresar.put("idContratoLoteResidente", getIdContratoLoteResidente());
		regresar.put("idControl", getIdControl());
		regresar.put("porcentaje", getPorcentaje());
		regresar.put("idNomina", getIdNomina());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdControlEstatus(), getCosto(), getPeriodo(), getIdUsuario(), getSemana(), getIdContratoDestajoResidente(), getIdContratoLoteResidente(), getIdControl(), getPorcentaje(), getIdNomina(), getRegistro()
    };
    return regresar;
  }

  @Override
  public Object toValue(String name) {
    return Methods.getValue(this, name);
  }

  @Override
  public String toAllKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("|");
    regresar.append("idContratoDestajoResidente~");
    regresar.append(getIdContratoDestajoResidente());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoDestajoResidente());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosDestajosResidentesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoDestajoResidente()!= null && getIdContratoDestajoResidente()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosDestajosResidentesDto other = (TcKeetContratosDestajosResidentesDto) obj;
    if (getIdContratoDestajoResidente() != other.idContratoDestajoResidente && (getIdContratoDestajoResidente() == null || !getIdContratoDestajoResidente().equals(other.idContratoDestajoResidente))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoDestajoResidente() != null ? getIdContratoDestajoResidente().hashCode() : 0);
    return hash;
  }

}


