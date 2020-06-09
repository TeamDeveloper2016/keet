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
@Table(name="tc_keet_cajas_chicas_cierres_bitacora")
public class TcKeetCajasChicasCierresBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_caja_chica_cierre_estatus")
  private Long idCajaChicaCierreEstatus;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_caja_chica_cierre_bitacora")
  private Long idCajaChicaCierreBitacora;
  @Column (name="id_caja_chica_cierre")
  private Long idCajaChicaCierre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetCajasChicasCierresBitacoraDto() {
    this(new Long(-1L));
  }

  public TcKeetCajasChicasCierresBitacoraDto(Long key) {
    this(null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKeetCajasChicasCierresBitacoraDto(String justificacion, Long idUsuario, Long idCajaChicaCierreEstatus, Long idCajaChicaCierreBitacora, Long idCajaChicaCierre) {
    setJustificacion(justificacion);
    setIdUsuario(idUsuario);
    setIdCajaChicaCierreEstatus(idCajaChicaCierreEstatus);
    setIdCajaChicaCierreBitacora(idCajaChicaCierreBitacora);
    setIdCajaChicaCierre(idCajaChicaCierre);
    setRegistro(LocalDateTime.now());
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

  public void setIdCajaChicaCierreEstatus(Long idCajaChicaCierreEstatus) {
    this.idCajaChicaCierreEstatus = idCajaChicaCierreEstatus;
  }

  public Long getIdCajaChicaCierreEstatus() {
    return idCajaChicaCierreEstatus;
  }

  public void setIdCajaChicaCierreBitacora(Long idCajaChicaCierreBitacora) {
    this.idCajaChicaCierreBitacora = idCajaChicaCierreBitacora;
  }

  public Long getIdCajaChicaCierreBitacora() {
    return idCajaChicaCierreBitacora;
  }

  public void setIdCajaChicaCierre(Long idCajaChicaCierre) {
    this.idCajaChicaCierre = idCajaChicaCierre;
  }

  public Long getIdCajaChicaCierre() {
    return idCajaChicaCierre;
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
  	return getIdCajaChicaCierreBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idCajaChicaCierreBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCajaChicaCierreEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCajaChicaCierreBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCajaChicaCierre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("justificacion", getJustificacion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idCajaChicaCierreEstatus", getIdCajaChicaCierreEstatus());
		regresar.put("idCajaChicaCierreBitacora", getIdCajaChicaCierreBitacora());
		regresar.put("idCajaChicaCierre", getIdCajaChicaCierre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getJustificacion(), getIdUsuario(), getIdCajaChicaCierreEstatus(), getIdCajaChicaCierreBitacora(), getIdCajaChicaCierre(), getRegistro()
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
    regresar.append("idCajaChicaCierreBitacora~");
    regresar.append(getIdCajaChicaCierreBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdCajaChicaCierreBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetCajasChicasCierresBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdCajaChicaCierreBitacora()!= null && getIdCajaChicaCierreBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetCajasChicasCierresBitacoraDto other = (TcKeetCajasChicasCierresBitacoraDto) obj;
    if (getIdCajaChicaCierreBitacora() != other.idCajaChicaCierreBitacora && (getIdCajaChicaCierreBitacora() == null || !getIdCajaChicaCierreBitacora().equals(other.idCajaChicaCierreBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdCajaChicaCierreBitacora() != null ? getIdCajaChicaCierreBitacora().hashCode() : 0);
    return hash;
  }

}


