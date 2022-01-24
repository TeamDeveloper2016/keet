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
@Table(name="tc_keet_anticipos_detalles")
public class TcKeetAnticiposDetallesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_incidente")
  private Long idIncidente;
  @Column (name="id_complemento")
  private Long idComplemento;
  @Column (name="id_anticipo_pago")
  private Long idAnticipoPago;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_anticipo_detalle")
  private Long idAnticipoDetalle;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetAnticiposDetallesDto() {
    this(new Long(-1L));
  }

  public TcKeetAnticiposDetallesDto(Long key) {
    this(null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKeetAnticiposDetallesDto(Long idIncidente, Long idComplemento, Long idAnticipoPago, Long idAnticipoDetalle) {
    setIdIncidente(idIncidente);
    setIdComplemento(idComplemento);
    setIdAnticipoPago(idAnticipoPago);
    setIdAnticipoDetalle(idAnticipoDetalle);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdIncidente(Long idIncidente) {
    this.idIncidente = idIncidente;
  }

  public Long getIdIncidente() {
    return idIncidente;
  }

  public void setIdComplemento(Long idComplemento) {
    this.idComplemento = idComplemento;
  }

  public Long getIdComplemento() {
    return idComplemento;
  }

  public void setIdAnticipoPago(Long idAnticipoPago) {
    this.idAnticipoPago = idAnticipoPago;
  }

  public Long getIdAnticipoPago() {
    return idAnticipoPago;
  }

  public void setIdAnticipoDetalle(Long idAnticipoDetalle) {
    this.idAnticipoDetalle = idAnticipoDetalle;
  }

  public Long getIdAnticipoDetalle() {
    return idAnticipoDetalle;
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
  	return getIdAnticipoDetalle();
  }

  @Override
  public void setKey(Long key) {
  	this.idAnticipoDetalle = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdIncidente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdComplemento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAnticipoPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAnticipoDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idIncidente", getIdIncidente());
		regresar.put("idComplemento", getIdComplemento());
		regresar.put("idAnticipoPago", getIdAnticipoPago());
		regresar.put("idAnticipoDetalle", getIdAnticipoDetalle());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdIncidente(), getIdComplemento(), getIdAnticipoPago(), getIdAnticipoDetalle(), getRegistro()
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
    regresar.append("idAnticipoDetalle~");
    regresar.append(getIdAnticipoDetalle());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdAnticipoDetalle());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetAnticiposDetallesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdAnticipoDetalle()!= null && getIdAnticipoDetalle()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetAnticiposDetallesDto other = (TcKeetAnticiposDetallesDto) obj;
    if (getIdAnticipoDetalle() != other.idAnticipoDetalle && (getIdAnticipoDetalle() == null || !getIdAnticipoDetalle().equals(other.idAnticipoDetalle))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdAnticipoDetalle() != null ? getIdAnticipoDetalle().hashCode() : 0);
    return hash;
  }

}


