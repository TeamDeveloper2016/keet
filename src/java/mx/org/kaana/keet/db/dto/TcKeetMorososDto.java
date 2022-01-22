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
@Table(name="tc_keet_morosos")
public class TcKeetMorososDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_proveedor")
  private Long idProveedor;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_moroso")
  private Long idMoroso;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="saldo")
  private Double saldo;
  @Column (name="limite")
  private Double limite;
  @Column (name="disponible")
  private Double disponible;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetMorososDto() {
    this(new Long(-1L));
  }

  public TcKeetMorososDto(Long key) {
    this(null, null, new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcKeetMorososDto(Long idProveedor, Long idUsuario, Long idMoroso, String observaciones, Double saldo, Double limite, Double disponible) {
    setIdProveedor(idProveedor);
    setIdUsuario(idUsuario);
    setIdMoroso(idMoroso);
    setObservaciones(observaciones);
    setSaldo(saldo);
    setLimite(limite);
    setDisponible(disponible);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdProveedor(Long idProveedor) {
    this.idProveedor = idProveedor;
  }

  public Long getIdProveedor() {
    return idProveedor;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdMoroso(Long idMoroso) {
    this.idMoroso = idMoroso;
  }

  public Long getIdMoroso() {
    return idMoroso;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setSaldo(Double saldo) {
    this.saldo = saldo;
  }

  public Double getSaldo() {
    return saldo;
  }

  public void setLimite(Double limite) {
    this.limite = limite;
  }

  public Double getLimite() {
    return limite;
  }

  public void setDisponible(Double disponible) {
    this.disponible = disponible;
  }

  public Double getDisponible() {
    return disponible;
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
  	return getIdMoroso();
  }

  @Override
  public void setKey(Long key) {
  	this.idMoroso = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdMoroso());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSaldo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLimite());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDisponible());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idProveedor", getIdProveedor());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idMoroso", getIdMoroso());
		regresar.put("observaciones", getObservaciones());
		regresar.put("saldo", getSaldo());
		regresar.put("limite", getLimite());
		regresar.put("disponible", getDisponible());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdProveedor(), getIdUsuario(), getIdMoroso(), getObservaciones(), getSaldo(), getLimite(), getDisponible(), getRegistro()
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
    regresar.append("idMoroso~");
    regresar.append(getIdMoroso());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdMoroso());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetMorososDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdMoroso()!= null && getIdMoroso()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetMorososDto other = (TcKeetMorososDto) obj;
    if (getIdMoroso() != other.idMoroso && (getIdMoroso() == null || !getIdMoroso().equals(other.idMoroso))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdMoroso() != null ? getIdMoroso().hashCode() : 0);
    return hash;
  }

}


