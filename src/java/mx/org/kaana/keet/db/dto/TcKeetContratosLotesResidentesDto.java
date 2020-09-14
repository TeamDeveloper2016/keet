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
@Table(name="tc_keet_contratos_lotes_residentes")
public class TcKeetContratosLotesResidentesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_lote_residente")
  private Long idContratoLoteResidente;
  @Column (name="id_empresa_persona")
  private Long idEmpresaPersona;
  @Column (name="id_contrato_lote")
  private Long idContratoLote;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_trabajo")
  private Long idTrabajo;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetContratosLotesResidentesDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosLotesResidentesDto(Long key) {
    this(null, new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcKeetContratosLotesResidentesDto(Long idUsuario, Long idContratoLoteResidente, Long idEmpresaPersona, Long idContratoLote, String observaciones, Long idTrabajo) {
    setIdUsuario(idUsuario);
    setIdContratoLoteResidente(idContratoLoteResidente);
    setIdEmpresaPersona(idEmpresaPersona);
    setIdContratoLote(idContratoLote);
    setObservaciones(observaciones);
    setIdTrabajo(idTrabajo);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdContratoLoteResidente(Long idContratoLoteResidente) {
    this.idContratoLoteResidente = idContratoLoteResidente;
  }

  public Long getIdContratoLoteResidente() {
    return idContratoLoteResidente;
  }

  public void setIdEmpresaPersona(Long idEmpresaPersona) {
    this.idEmpresaPersona = idEmpresaPersona;
  }

  public Long getIdEmpresaPersona() {
    return idEmpresaPersona;
  }

  public void setIdContratoLote(Long idContratoLote) {
    this.idContratoLote = idContratoLote;
  }

  public Long getIdContratoLote() {
    return idContratoLote;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdTrabajo(Long idTrabajo) {
    this.idTrabajo = idTrabajo;
  }

  public Long getIdTrabajo() {
    return idTrabajo;
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
  	return getIdContratoLoteResidente();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoLoteResidente = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLoteResidente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTrabajo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idContratoLoteResidente", getIdContratoLoteResidente());
		regresar.put("idEmpresaPersona", getIdEmpresaPersona());
		regresar.put("idContratoLote", getIdContratoLote());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idTrabajo", getIdTrabajo());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdUsuario(), getIdContratoLoteResidente(), getIdEmpresaPersona(), getIdContratoLote(), getObservaciones(), getIdTrabajo(), getRegistro()
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
    regresar.append("idContratoLoteResidente~");
    regresar.append(getIdContratoLoteResidente());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoLoteResidente());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosLotesResidentesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoLoteResidente()!= null && getIdContratoLoteResidente()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosLotesResidentesDto other = (TcKeetContratosLotesResidentesDto) obj;
    if (getIdContratoLoteResidente() != other.idContratoLoteResidente && (getIdContratoLoteResidente() == null || !getIdContratoLoteResidente().equals(other.idContratoLoteResidente))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoLoteResidente() != null ? getIdContratoLoteResidente().hashCode() : 0);
    return hash;
  }

}


