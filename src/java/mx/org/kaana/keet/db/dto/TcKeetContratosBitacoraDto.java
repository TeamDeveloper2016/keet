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
@Table(name="tc_keet_contratos_bitacora")
public class TcKeetContratosBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_contrato_estatus")
  private Long idContratoEstatus;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_bitacora")
  private Long idContratoBitacora;
  @Column (name="id_contrato_lote")
  private Long idContratoLote;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetContratosBitacoraDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosBitacoraDto(Long key) {
    this(null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKeetContratosBitacoraDto(String justificacion,Long idContratoEstatus, Long idUsuario, Long idContratoBitacora, Long idContratoLote) {
    setIdContratoEstatus(idContratoEstatus);
    setJustificacion(justificacion);
    setIdUsuario(idUsuario);
    setIdContratoBitacora(idContratoBitacora);
    setIdContratoLote(idContratoLote);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdContratoEstatus(Long idContratoEstatus) {
    this.idContratoEstatus = idContratoEstatus;
  }

  public Long getIdContratoEstatus() {
    return idContratoEstatus;
  }

  public void setJustificacion(String justificacion) {
    this.justificacion = justificacion;
  }

  public String getJustificacion() {
    return justificacion;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdContratoBitacora(Long idContratoBitacora) {
    this.idContratoBitacora = idContratoBitacora;
  }

  public Long getIdContratoBitacora() {
    return idContratoBitacora;
  }

  public void setIdContratoLote(Long idContratoLote) {
    this.idContratoLote = idContratoLote;
  }

  public Long getIdContratoLote() {
    return idContratoLote;
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
  	return getIdContratoBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdContratoEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idContratoEstatus", getIdContratoEstatus());
		regresar.put("justificacion", getJustificacion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idContratoBitacora", getIdContratoBitacora());
		regresar.put("idContratoLote", getIdContratoLote());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdContratoEstatus(), getJustificacion(), getIdUsuario(), getIdContratoBitacora(), getIdContratoLote(), getRegistro()
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
    regresar.append("idContratoBitacora~");
    regresar.append(getIdContratoBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoBitacora()!= null && getIdContratoBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosBitacoraDto other = (TcKeetContratosBitacoraDto) obj;
    if (getIdContratoBitacora() != other.idContratoBitacora && (getIdContratoBitacora() == null || !getIdContratoBitacora().equals(other.idContratoBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoBitacora() != null ? getIdContratoBitacora().hashCode() : 0);
    return hash;
  }

}


