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
@Table(name="tc_keet_contratos_destajos_contratistas")
public class TcKeetContratosDestajosContratistasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_estacion_estatus")
  private Long idEstacionEstatus;
  @Column (name="costo")
  private Double costo;
  @Column (name="periodo")
  private Long periodo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="semana")
  private Long semana;
  @Column (name="id_estacion")
  private Long idEstacion;
  @Column (name="porcentaje")
  private Double porcentaje;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_destajo_contratista")
  private Long idContratoDestajoContratista;
  @Column (name="id_contrato_lote_contratista")
  private Long idContratoLoteContratista;
  @Column (name="id_nomina")
  private Long idNomina;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetContratosDestajosContratistasDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosDestajosContratistasDto(Long key) {
    this(null, null, null, null, null, null, null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcKeetContratosDestajosContratistasDto(Long idEstacionEstatus, Double costo, Long periodo, Long idUsuario, Long semana, Long idEstacion, Double porcentaje, Long idContratoDestajoContratista, Long idContratoLoteContratista, Long idNomina) {
    setIdEstacionEstatus(idEstacionEstatus);
    setCosto(costo);
    setPeriodo(periodo);
    setIdUsuario(idUsuario);
    setSemana(semana);
    setIdEstacion(idEstacion);
    setPorcentaje(porcentaje);
    setIdContratoDestajoContratista(idContratoDestajoContratista);
    setIdContratoLoteContratista(idContratoLoteContratista);
    setIdNomina(idNomina);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdEstacionEstatus(Long idEstacionEstatus) {
    this.idEstacionEstatus = idEstacionEstatus;
  }

  public Long getIdEstacionEstatus() {
    return idEstacionEstatus;
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

  public void setIdEstacion(Long idEstacion) {
    this.idEstacion = idEstacion;
  }

  public Long getIdEstacion() {
    return idEstacion;
  }

  public void setPorcentaje(Double porcentaje) {
    this.porcentaje = porcentaje;
  }

  public Double getPorcentaje() {
    return porcentaje;
  }

  public void setIdContratoDestajoContratista(Long idContratoDestajoContratista) {
    this.idContratoDestajoContratista = idContratoDestajoContratista;
  }

  public Long getIdContratoDestajoContratista() {
    return idContratoDestajoContratista;
  }

  public void setIdContratoLoteContratista(Long idContratoLoteContratista) {
    this.idContratoLoteContratista = idContratoLoteContratista;
  }

  public Long getIdContratoLoteContratista() {
    return idContratoLoteContratista;
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
  	return getIdContratoDestajoContratista();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoDestajoContratista = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdEstacionEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCosto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPeriodo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSemana());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEstacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPorcentaje());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoDestajoContratista());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLoteContratista());
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
		regresar.put("idEstacionEstatus", getIdEstacionEstatus());
		regresar.put("costo", getCosto());
		regresar.put("periodo", getPeriodo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("semana", getSemana());
		regresar.put("idEstacion", getIdEstacion());
		regresar.put("porcentaje", getPorcentaje());
		regresar.put("idContratoDestajoContratista", getIdContratoDestajoContratista());
		regresar.put("idContratoLoteContratista", getIdContratoLoteContratista());
		regresar.put("idNomina", getIdNomina());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdEstacionEstatus(), getCosto(), getPeriodo(), getIdUsuario(), getSemana(), getIdEstacion(), getPorcentaje(), getIdContratoDestajoContratista(), getIdContratoLoteContratista(), getIdNomina(), getRegistro()
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
    regresar.append("idContratoDestajoContratista~");
    regresar.append(getIdContratoDestajoContratista());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoDestajoContratista());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosDestajosContratistasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoDestajoContratista()!= null && getIdContratoDestajoContratista()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosDestajosContratistasDto other = (TcKeetContratosDestajosContratistasDto) obj;
    if (getIdContratoDestajoContratista() != other.idContratoDestajoContratista && (getIdContratoDestajoContratista() == null || !getIdContratoDestajoContratista().equals(other.idContratoDestajoContratista))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoDestajoContratista() != null ? getIdContratoDestajoContratista().hashCode() : 0);
    return hash;
  }

}


