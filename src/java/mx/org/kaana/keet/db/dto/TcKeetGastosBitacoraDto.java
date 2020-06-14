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
@Table(name="tc_keet_gastos_bitacora")
public class TcKeetGastosBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_gasto")
  private Long idGasto;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_gasto_bitacora")
  private Long idGastoBitacora;
  @Column (name="id_gasto_estatus")
  private Long idGastoEstatus;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetGastosBitacoraDto() {
    this(new Long(-1L));
  }

  public TcKeetGastosBitacoraDto(Long key) {
    this(null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKeetGastosBitacoraDto(Long idGasto, String justificacion, Long idUsuario, Long idGastoBitacora, Long idGastoEstatus) {
    setIdGasto(idGasto);
    setJustificacion(justificacion);
    setIdUsuario(idUsuario);
    setIdGastoBitacora(idGastoBitacora);
    setIdGastoEstatus(idGastoEstatus);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdGasto(Long idGasto) {
    this.idGasto = idGasto;
  }

  public Long getIdGasto() {
    return idGasto;
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

  public void setIdGastoBitacora(Long idGastoBitacora) {
    this.idGastoBitacora = idGastoBitacora;
  }

  public Long getIdGastoBitacora() {
    return idGastoBitacora;
  }

  public void setIdGastoEstatus(Long idGastoEstatus) {
    this.idGastoEstatus = idGastoEstatus;
  }

  public Long getIdGastoEstatus() {
    return idGastoEstatus;
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
  	return getIdGastoBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idGastoBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdGasto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdGastoBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdGastoEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idGasto", getIdGasto());
		regresar.put("justificacion", getJustificacion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idGastoBitacora", getIdGastoBitacora());
		regresar.put("idGastoEstatus", getIdGastoEstatus());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdGasto(), getJustificacion(), getIdUsuario(), getIdGastoBitacora(), getIdGastoEstatus(), getRegistro()
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
    regresar.append("idGastoBitacora~");
    regresar.append(getIdGastoBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdGastoBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetGastosBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdGastoBitacora()!= null && getIdGastoBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetGastosBitacoraDto other = (TcKeetGastosBitacoraDto) obj;
    if (getIdGastoBitacora() != other.idGastoBitacora && (getIdGastoBitacora() == null || !getIdGastoBitacora().equals(other.idGastoBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdGastoBitacora() != null ? getIdGastoBitacora().hashCode() : 0);
    return hash;
  }

}


