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
@Table(name="tc_keet_prestamos_detalles")
public class TcKeetPrestamosDetallesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_prestamo_detalle")
  private Long idPrestamoDetalle;
  @Column (name="id_incidente")
  private Long idIncidente;
  @Column (name="id_prestamo_pago")
  private Long idPrestamoPago;
  @Column (name="id_complemento")
  private Long idComplemento;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetPrestamosDetallesDto() {
    this(new Long(-1L));
  }

  public TcKeetPrestamosDetallesDto(Long key) {
    this(new Long(-1L), null, null, null);
    setKey(key);
  }

  public TcKeetPrestamosDetallesDto(Long idPrestamoDetalle, Long idIncidente, Long idPrestamoPago, Long idComplemento) {
    setIdPrestamoDetalle(idPrestamoDetalle);
    setIdIncidente(idIncidente);
    setIdPrestamoPago(idPrestamoPago);
    setIdComplemento(idComplemento);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdPrestamoDetalle(Long idPrestamoDetalle) {
    this.idPrestamoDetalle = idPrestamoDetalle;
  }

  public Long getIdPrestamoDetalle() {
    return idPrestamoDetalle;
  }

  public void setIdIncidente(Long idIncidente) {
    this.idIncidente = idIncidente;
  }

  public Long getIdIncidente() {
    return idIncidente;
  }

  public void setIdPrestamoPago(Long idPrestamoPago) {
    this.idPrestamoPago = idPrestamoPago;
  }

  public Long getIdPrestamoPago() {
    return idPrestamoPago;
  }

  public void setIdComplemento(Long idComplemento) {
    this.idComplemento = idComplemento;
  }

  public Long getIdComplemento() {
    return idComplemento;
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
  	return getIdPrestamoDetalle();
  }

  @Override
  public void setKey(Long key) {
  	this.idPrestamoDetalle = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdPrestamoDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdIncidente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrestamoPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdComplemento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idPrestamoDetalle", getIdPrestamoDetalle());
		regresar.put("idIncidente", getIdIncidente());
		regresar.put("idPrestamoPago", getIdPrestamoPago());
		regresar.put("idComplemento", getIdComplemento());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdPrestamoDetalle(), getIdIncidente(), getIdPrestamoPago(), getIdComplemento(), getRegistro()
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
    regresar.append("idPrestamoDetalle~");
    regresar.append(getIdPrestamoDetalle());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdPrestamoDetalle());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetPrestamosDetallesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdPrestamoDetalle()!= null && getIdPrestamoDetalle()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetPrestamosDetallesDto other = (TcKeetPrestamosDetallesDto) obj;
    if (getIdPrestamoDetalle() != other.idPrestamoDetalle && (getIdPrestamoDetalle() == null || !getIdPrestamoDetalle().equals(other.idPrestamoDetalle))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdPrestamoDetalle() != null ? getIdPrestamoDetalle().hashCode() : 0);
    return hash;
  }

}


