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
@Table(name="tc_sakbe_suministros_bitacora")
public class TcSakbeSuministrosBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_suministro_estatus")
  private Long idSuministroEstatus;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_suministro_bitacora")
  private Long idSuministroBitacora;
  @Column (name="id_suministro")
  private Long idSuministro;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcSakbeSuministrosBitacoraDto() {
    this(new Long(-1L));
  }

  public TcSakbeSuministrosBitacoraDto(Long key) {
    this(null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcSakbeSuministrosBitacoraDto(String justificacion, Long idUsuario, Long idSuministroEstatus, Long idSuministroBitacora, Long idSuministro) {
    setJustificacion(justificacion);
    setIdUsuario(idUsuario);
    setIdSuministroEstatus(idSuministroEstatus);
    setIdSuministroBitacora(idSuministroBitacora);
    setIdSuministro(idSuministro);
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

  public void setIdSuministroEstatus(Long idSuministroEstatus) {
    this.idSuministroEstatus = idSuministroEstatus;
  }

  public Long getIdSuministroEstatus() {
    return idSuministroEstatus;
  }

  public void setIdSuministroBitacora(Long idSuministroBitacora) {
    this.idSuministroBitacora = idSuministroBitacora;
  }

  public Long getIdSuministroBitacora() {
    return idSuministroBitacora;
  }

  public void setIdSuministro(Long idSuministro) {
    this.idSuministro = idSuministro;
  }

  public Long getIdSuministro() {
    return idSuministro;
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
  	return getIdSuministroBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idSuministroBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdSuministroEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdSuministroBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdSuministro());
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
		regresar.put("idSuministroEstatus", getIdSuministroEstatus());
		regresar.put("idSuministroBitacora", getIdSuministroBitacora());
		regresar.put("idSuministro", getIdSuministro());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getJustificacion(), getIdUsuario(), getIdSuministroEstatus(), getIdSuministroBitacora(), getIdSuministro(), getRegistro()
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
    regresar.append("idSuministroBitacora~");
    regresar.append(getIdSuministroBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdSuministroBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeSuministrosBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdSuministroBitacora()!= null && getIdSuministroBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcSakbeSuministrosBitacoraDto other = (TcSakbeSuministrosBitacoraDto) obj;
    if (getIdSuministroBitacora() != other.idSuministroBitacora && (getIdSuministroBitacora() == null || !getIdSuministroBitacora().equals(other.idSuministroBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdSuministroBitacora() != null ? getIdSuministroBitacora().hashCode() : 0);
    return hash;
  }

}


