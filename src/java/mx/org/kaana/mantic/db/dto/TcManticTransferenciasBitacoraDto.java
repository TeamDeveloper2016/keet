package mx.org.kaana.mantic.db.dto;

import java.io.Serializable;
import java.sql.Blob;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Lob;
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
@Table(name="tc_mantic_transferencias_bitacora")
public class TcManticTransferenciasBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_transferencia_bitacora")
  private Long idTransferenciaBitacora;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_transporto")
  private Long idTransporto;
  @Column (name="id_transferencia_estatus")
  private Long idTransferenciaEstatus;
  @Column (name="id_transferencia")
  private Long idTransferencia;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcManticTransferenciasBitacoraDto() {
    this(new Long(-1L));
  }

  public TcManticTransferenciasBitacoraDto(Long key) {
    this(new Long(-1L), null, null, null, null, null);
    setKey(key);
  }

  public TcManticTransferenciasBitacoraDto(Long idTransferenciaBitacora, String justificacion, Long idUsuario, Long idTransporto, Long idTransferenciaEstatus, Long idTransferencia) {
    setIdTransferenciaBitacora(idTransferenciaBitacora);
    setJustificacion(justificacion);
    setIdUsuario(idUsuario);
    setIdTransporto(idTransporto);
    setIdTransferenciaEstatus(idTransferenciaEstatus);
    setIdTransferencia(idTransferencia);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdTransferenciaBitacora(Long idTransferenciaBitacora) {
    this.idTransferenciaBitacora = idTransferenciaBitacora;
  }

  public Long getIdTransferenciaBitacora() {
    return idTransferenciaBitacora;
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

  public void setIdTransporto(Long idTransporto) {
    this.idTransporto = idTransporto;
  }

  public Long getIdTransporto() {
    return idTransporto;
  }

  public void setIdTransferenciaEstatus(Long idTransferenciaEstatus) {
    this.idTransferenciaEstatus = idTransferenciaEstatus;
  }

  public Long getIdTransferenciaEstatus() {
    return idTransferenciaEstatus;
  }

  public void setIdTransferencia(Long idTransferencia) {
    this.idTransferencia = idTransferencia;
  }

  public Long getIdTransferencia() {
    return idTransferencia;
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
  	return getIdTransferenciaBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idTransferenciaBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdTransferenciaBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTransporto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTransferenciaEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTransferencia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idTransferenciaBitacora", getIdTransferenciaBitacora());
		regresar.put("justificacion", getJustificacion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idTransporto", getIdTransporto());
		regresar.put("idTransferenciaEstatus", getIdTransferenciaEstatus());
		regresar.put("idTransferencia", getIdTransferencia());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdTransferenciaBitacora(), getJustificacion(), getIdUsuario(), getIdTransporto(), getIdTransferenciaEstatus(), getIdTransferencia(), getRegistro()
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
    regresar.append("idTransferenciaBitacora~");
    regresar.append(getIdTransferenciaBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdTransferenciaBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticTransferenciasBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdTransferenciaBitacora()!= null && getIdTransferenciaBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticTransferenciasBitacoraDto other = (TcManticTransferenciasBitacoraDto) obj;
    if (getIdTransferenciaBitacora() != other.idTransferenciaBitacora && (getIdTransferenciaBitacora() == null || !getIdTransferenciaBitacora().equals(other.idTransferenciaBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdTransferenciaBitacora() != null ? getIdTransferenciaBitacora().hashCode() : 0);
    return hash;
  }

}


