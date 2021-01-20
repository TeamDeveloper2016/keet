package mx.org.kaana.mantic.db.dto;

import java.io.Serializable;
import java.sql.Blob;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.SequenceGenerator;
import javax.persistence.Lob;
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
@Table(name="tc_mantic_clientes_deudas_estatus")
public class TcManticClientesDeudasEstatusDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_cliente_estatus")
  private Long idClienteEstatus;
  @Column (name="id_justificacion")
  private Long idJustificacion;
  @Column (name="estatus_asociados")
  private String estatusAsociados;
  @Column (name="decripcion")
  private String decripcion;
  @Column (name="nombre")
  private String nombre;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcManticClientesDeudasEstatusDto() {
    this(new Long(-1L));
  }

  public TcManticClientesDeudasEstatusDto(Long key) {
    this(new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcManticClientesDeudasEstatusDto(Long idClienteEstatus, Long idJustificacion, String estatusAsociados, String decripcion, String nombre) {
    setIdClienteEstatus(idClienteEstatus);
    setIdJustificacion(idJustificacion);
    setEstatusAsociados(estatusAsociados);
    setDecripcion(decripcion);
    setNombre(nombre);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdClienteEstatus(Long idClienteEstatus) {
    this.idClienteEstatus = idClienteEstatus;
  }

  public Long getIdClienteEstatus() {
    return idClienteEstatus;
  }

  public void setIdJustificacion(Long idJustificacion) {
    this.idJustificacion = idJustificacion;
  }

  public Long getIdJustificacion() {
    return idJustificacion;
  }

  public void setEstatusAsociados(String estatusAsociados) {
    this.estatusAsociados = estatusAsociados;
  }

  public String getEstatusAsociados() {
    return estatusAsociados;
  }

  public void setDecripcion(String decripcion) {
    this.decripcion = decripcion;
  }

  public String getDecripcion() {
    return decripcion;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
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
  	return getIdClienteEstatus();
  }

  @Override
  public void setKey(Long key) {
  	this.idClienteEstatus = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdClienteEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEstatusAsociados());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDecripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idClienteEstatus", getIdClienteEstatus());
		regresar.put("idJustificacion", getIdJustificacion());
		regresar.put("estatusAsociados", getEstatusAsociados());
		regresar.put("decripcion", getDecripcion());
		regresar.put("nombre", getNombre());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdClienteEstatus(), getIdJustificacion(), getEstatusAsociados(), getDecripcion(), getNombre(), getRegistro()
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
    regresar.append("idClienteEstatus~");
    regresar.append(getIdClienteEstatus());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdClienteEstatus());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticClientesDeudasEstatusDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdClienteEstatus()!= null && getIdClienteEstatus()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticClientesDeudasEstatusDto other = (TcManticClientesDeudasEstatusDto) obj;
    if (getIdClienteEstatus() != other.idClienteEstatus && (getIdClienteEstatus() == null || !getIdClienteEstatus().equals(other.idClienteEstatus))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdClienteEstatus() != null ? getIdClienteEstatus().hashCode() : 0);
    return hash;
  }

}


