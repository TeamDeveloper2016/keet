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
@Table(name="tc_keet_anticipos_controles")
public class TcKeetAnticiposControlesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_anticipo_pago")
  private Long idAnticipoPago;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_anticipo_control")
  private Long idAnticipoControl;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="importe")
  private Double importe;

  public TcKeetAnticiposControlesDto() {
    this(new Long(-1L));
  }

  public TcKeetAnticiposControlesDto(Long key) {
    this(null, null, null, new Long(-1L), 0D);
    setKey(key);
  }

  public TcKeetAnticiposControlesDto(String justificacion, Long idUsuario, Long idAnticipoPago, Long idAnticipoControl, Double importe) {
    setJustificacion(justificacion);
    setIdUsuario(idUsuario);
    setIdAnticipoPago(idAnticipoPago);
    setIdAnticipoControl(idAnticipoControl);
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

  public void setIdAnticipoPago(Long idAnticipoPago) {
    this.idAnticipoPago = idAnticipoPago;
  }

  public Long getIdAnticipoPago() {
    return idAnticipoPago;
  }

  public void setIdAnticipoControl(Long idAnticipoControl) {
    this.idAnticipoControl = idAnticipoControl;
  }

  public Long getIdAnticipoControl() {
    return idAnticipoControl;
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
  	return getIdAnticipoControl();
  }

  @Override
  public void setKey(Long key) {
  	this.idAnticipoControl = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAnticipoPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAnticipoControl());
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
		regresar.put("idAnticipoPago", getIdAnticipoPago());
		regresar.put("idAnticipoControl", getIdAnticipoControl());
		regresar.put("registro", getRegistro());
		regresar.put("importe", getImporte());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getJustificacion(), getIdUsuario(), getIdAnticipoPago(), getIdAnticipoControl(), getRegistro(), getImporte()
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
    regresar.append("idAnticipoControl~");
    regresar.append(getIdAnticipoControl());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAnticipoControl());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetAnticiposControlesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAnticipoControl()!= null && getIdAnticipoControl()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetAnticiposControlesDto other = (TcKeetAnticiposControlesDto) obj;
    if (getIdAnticipoControl() != other.idAnticipoControl && (getIdAnticipoControl() == null || !getIdAnticipoControl().equals(other.idAnticipoControl))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAnticipoControl() != null ? getIdAnticipoControl().hashCode() : 0);
    return hash;
  }

}


