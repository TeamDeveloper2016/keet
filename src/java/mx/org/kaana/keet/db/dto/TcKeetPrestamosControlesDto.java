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
@Table(name="tc_keet_prestamos_controles")
public class TcKeetPrestamosControlesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_prestamo_pago")
  private Long idPrestamoPago;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_prestamo_control")
  private Long idPrestamoControl;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="importe")
  private Double importe;

  public TcKeetPrestamosControlesDto() {
    this(new Long(-1L));
  }

  public TcKeetPrestamosControlesDto(Long key) {
    this(null, null, null, new Long(-1L), 0D);
    setKey(key);
  }

  public TcKeetPrestamosControlesDto(String justificacion, Long idUsuario, Long idPrestamoPago, Long idPrestamoControl, Double importe) {
    setJustificacion(justificacion);
    setIdUsuario(idUsuario);
    setIdPrestamoPago(idPrestamoPago);
    setIdPrestamoControl(idPrestamoControl);
    setRegistro(LocalDateTime.now());
    setImporte(importe);
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

  public void setIdPrestamoPago(Long idPrestamoPago) {
    this.idPrestamoPago = idPrestamoPago;
  }

  public Long getIdPrestamoPago() {
    return idPrestamoPago;
  }

  public void setIdPrestamoControl(Long idPrestamoControl) {
    this.idPrestamoControl = idPrestamoControl;
  }

  public Long getIdPrestamoControl() {
    return idPrestamoControl;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  public Double getImporte() {
    return importe;
  }

  public void setImporte(Double importe) {
    this.importe = importe;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdPrestamoControl();
  }

  @Override
  public void setKey(Long key) {
  	this.idPrestamoControl = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrestamoPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrestamoControl());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("justificacion", getJustificacion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idPrestamoPago", getIdPrestamoPago());
		regresar.put("idPrestamoControl", getIdPrestamoControl());
		regresar.put("registro", getRegistro());
		regresar.put("importe", getImporte());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getJustificacion(), getIdUsuario(), getIdPrestamoPago(), getIdPrestamoControl(), getRegistro(), getImporte()
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
    regresar.append("idPrestamoControl~");
    regresar.append(getIdPrestamoControl());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPrestamoControl());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPrestamosControlesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPrestamoControl()!= null && getIdPrestamoControl()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPrestamosControlesDto other = (TcKeetPrestamosControlesDto) obj;
    if (getIdPrestamoControl() != other.idPrestamoControl && (getIdPrestamoControl() == null || !getIdPrestamoControl().equals(other.idPrestamoControl))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPrestamoControl() != null ? getIdPrestamoControl().hashCode() : 0);
    return hash;
  }

}


