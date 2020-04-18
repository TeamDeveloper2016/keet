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
@Table(name="tc_keet_prestamos_bitacora")
public class TcKeetPrestamosBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_prestamo")
  private Long idPrestamo;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_prestamo_bitacora")
  private Long idPrestamoBitacora;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_prestamo_estatus")
  private Long idPrestamoEstatus;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetPrestamosBitacoraDto() {
    this(new Long(-1L));
  }

  public TcKeetPrestamosBitacoraDto(Long key) {
    this(null, null, new Long(-1L), null, null);
    setKey(key);
  }

  public TcKeetPrestamosBitacoraDto(String justificacion, Long idPrestamo, Long idPrestamoBitacora, Long idUsuario, Long idPrestamoEstatus) {
    setJustificacion(justificacion);
    setIdPrestamo(idPrestamo);
    setIdPrestamoBitacora(idPrestamoBitacora);
    setIdUsuario(idUsuario);
    setIdPrestamoEstatus(idPrestamoEstatus);
    setRegistro(LocalDateTime.now());
  }
	
  public void setJustificacion(String justificacion) {
    this.justificacion = justificacion;
  }

  public String getJustificacion() {
    return justificacion;
  }

  public void setIdPrestamo(Long idPrestamo) {
    this.idPrestamo = idPrestamo;
  }

  public Long getIdPrestamo() {
    return idPrestamo;
  }

  public void setIdPrestamoBitacora(Long idPrestamoBitacora) {
    this.idPrestamoBitacora = idPrestamoBitacora;
  }

  public Long getIdPrestamoBitacora() {
    return idPrestamoBitacora;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdPrestamoEstatus(Long idPrestamoEstatus) {
    this.idPrestamoEstatus = idPrestamoEstatus;
  }

  public Long getIdPrestamoEstatus() {
    return idPrestamoEstatus;
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
  	return getIdPrestamoBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idPrestamoBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrestamo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrestamoBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrestamoEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("justificacion", getJustificacion());
		regresar.put("idPrestamo", getIdPrestamo());
		regresar.put("idPrestamoBitacora", getIdPrestamoBitacora());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idPrestamoEstatus", getIdPrestamoEstatus());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getJustificacion(), getIdPrestamo(), getIdPrestamoBitacora(), getIdUsuario(), getIdPrestamoEstatus(), getRegistro()
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
    regresar.append("idPrestamoBitacora~");
    regresar.append(getIdPrestamoBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPrestamoBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPrestamosBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPrestamoBitacora()!= null && getIdPrestamoBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPrestamosBitacoraDto other = (TcKeetPrestamosBitacoraDto) obj;
    if (getIdPrestamoBitacora() != other.idPrestamoBitacora && (getIdPrestamoBitacora() == null || !getIdPrestamoBitacora().equals(other.idPrestamoBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPrestamoBitacora() != null ? getIdPrestamoBitacora().hashCode() : 0);
    return hash;
  }

}


