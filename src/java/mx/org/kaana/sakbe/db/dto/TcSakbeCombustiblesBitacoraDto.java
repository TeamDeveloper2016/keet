package mx.org.kaana.sakbe.db.dto;

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
@Table(name="tc_sakbe_combustibles_bitacora")
public class TcSakbeCombustiblesBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="justificacion")
  private String justificacion;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_combustible_bitacora")
  private Long idCombustibleBitacora;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_combustible_estatus")
  private Long idCombustibleEstatus;
  @Column (name="id_combustible")
  private Long idCombustible;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcSakbeCombustiblesBitacoraDto() {
    this(new Long(-1L));
  }

  public TcSakbeCombustiblesBitacoraDto(Long key) {
    this(null, new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcSakbeCombustiblesBitacoraDto(String justificacion, Long idCombustibleBitacora, Long idUsuario, Long idCombustibleEstatus, Long idCombustible) {
    setJustificacion(justificacion);
    setIdCombustibleBitacora(idCombustibleBitacora);
    setIdUsuario(idUsuario);
    setIdCombustibleEstatus(idCombustibleEstatus);
    setIdCombustible(idCombustible);
    setRegistro(LocalDateTime.now());
  }
	
  public void setJustificacion(String justificacion) {
    this.justificacion = justificacion;
  }

  public String getJustificacion() {
    return justificacion;
  }

  public void setIdCombustibleBitacora(Long idCombustibleBitacora) {
    this.idCombustibleBitacora = idCombustibleBitacora;
  }

  public Long getIdCombustibleBitacora() {
    return idCombustibleBitacora;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdCombustibleEstatus(Long idCombustibleEstatus) {
    this.idCombustibleEstatus = idCombustibleEstatus;
  }

  public Long getIdCombustibleEstatus() {
    return idCombustibleEstatus;
  }

  public void setIdCombustible(Long idCombustible) {
    this.idCombustible = idCombustible;
  }

  public Long getIdCombustible() {
    return idCombustible;
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
  	return getIdCombustibleBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idCombustibleBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCombustibleBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCombustibleEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCombustible());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("justificacion", getJustificacion());
		regresar.put("idCombustibleBitacora", getIdCombustibleBitacora());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idCombustibleEstatus", getIdCombustibleEstatus());
		regresar.put("idCombustible", getIdCombustible());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getJustificacion(), getIdCombustibleBitacora(), getIdUsuario(), getIdCombustibleEstatus(), getIdCombustible(), getRegistro()
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
    regresar.append("idCombustibleBitacora~");
    regresar.append(getIdCombustibleBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdCombustibleBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeCombustiblesBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdCombustibleBitacora()!= null && getIdCombustibleBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcSakbeCombustiblesBitacoraDto other = (TcSakbeCombustiblesBitacoraDto) obj;
    if (getIdCombustibleBitacora() != other.idCombustibleBitacora && (getIdCombustibleBitacora() == null || !getIdCombustibleBitacora().equals(other.idCombustibleBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdCombustibleBitacora() != null ? getIdCombustibleBitacora().hashCode() : 0);
    return hash;
  }

}


