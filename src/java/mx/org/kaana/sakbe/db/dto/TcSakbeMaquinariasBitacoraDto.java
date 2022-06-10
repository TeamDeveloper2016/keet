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
@Table(name="tc_sakbe_maquinarias_bitacora")
public class TcSakbeMaquinariasBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_maquinaria")
  private Long idMaquinaria;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_maquinaria_estatus")
  private Long idMaquinariaEstatus;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_maquinaria_bitacora")
  private Long idMaquinariaBitacora;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcSakbeMaquinariasBitacoraDto() {
    this(new Long(-1L));
  }

  public TcSakbeMaquinariasBitacoraDto(Long key) {
    this(null, null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcSakbeMaquinariasBitacoraDto(Long idMaquinaria, String justificacion, Long idUsuario, Long idMaquinariaEstatus, Long idMaquinariaBitacora) {
    setIdMaquinaria(idMaquinaria);
    setJustificacion(justificacion);
    setIdUsuario(idUsuario);
    setIdMaquinariaEstatus(idMaquinariaEstatus);
    setIdMaquinariaBitacora(idMaquinariaBitacora);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdMaquinaria(Long idMaquinaria) {
    this.idMaquinaria = idMaquinaria;
  }

  public Long getIdMaquinaria() {
    return idMaquinaria;
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

  public void setIdMaquinariaEstatus(Long idMaquinariaEstatus) {
    this.idMaquinariaEstatus = idMaquinariaEstatus;
  }

  public Long getIdMaquinariaEstatus() {
    return idMaquinariaEstatus;
  }

  public void setIdMaquinariaBitacora(Long idMaquinariaBitacora) {
    this.idMaquinariaBitacora = idMaquinariaBitacora;
  }

  public Long getIdMaquinariaBitacora() {
    return idMaquinariaBitacora;
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
  	return getIdMaquinariaBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idMaquinariaBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdMaquinaria());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdMaquinariaEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdMaquinariaBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idMaquinaria", getIdMaquinaria());
		regresar.put("justificacion", getJustificacion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idMaquinariaEstatus", getIdMaquinariaEstatus());
		regresar.put("idMaquinariaBitacora", getIdMaquinariaBitacora());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdMaquinaria(), getJustificacion(), getIdUsuario(), getIdMaquinariaEstatus(), getIdMaquinariaBitacora(), getRegistro()
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
    regresar.append("idMaquinariaBitacora~");
    regresar.append(getIdMaquinariaBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdMaquinariaBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcSakbeMaquinariasBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdMaquinariaBitacora()!= null && getIdMaquinariaBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcSakbeMaquinariasBitacoraDto other = (TcSakbeMaquinariasBitacoraDto) obj;
    if (getIdMaquinariaBitacora() != other.idMaquinariaBitacora && (getIdMaquinariaBitacora() == null || !getIdMaquinariaBitacora().equals(other.idMaquinariaBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdMaquinariaBitacora() != null ? getIdMaquinariaBitacora().hashCode() : 0);
    return hash;
  }

}


