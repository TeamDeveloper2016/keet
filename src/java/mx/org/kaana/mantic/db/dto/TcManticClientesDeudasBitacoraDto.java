package mx.org.kaana.mantic.db.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
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
@Table(name="tc_mantic_clientes_deudas_bitacora")
public class TcManticClientesDeudasBitacoraDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_cliente_deuda_bitacora")
  private Long idClienteDeudaBitacora;
  @Column (name="id_cliente_deuda_estatus")
  private Long idClienteDeudaEstatus;
  @Column (name="justificacion")
  private String justificacion;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_cliente_deuda")
  private Long idClienteDeuda;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcManticClientesDeudasBitacoraDto() {
    this(new Long(-1L));
  }

  public TcManticClientesDeudasBitacoraDto(Long key) {
    this(new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcManticClientesDeudasBitacoraDto(Long idClienteDeudaBitacora, Long idClienteDeudaEstatus, String justificacion, Long idUsuario, Long idClienteDeuda) {
    setIdClienteDeudaBitacora(idClienteDeudaBitacora);
    setIdClienteDeudaEstatus(idClienteDeudaEstatus);
    setJustificacion(justificacion);
    setIdUsuario(idUsuario);
    setIdClienteDeuda(idClienteDeuda);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdClienteDeudaBitacora(Long idClienteDeudaBitacora) {
    this.idClienteDeudaBitacora = idClienteDeudaBitacora;
  }

  public Long getIdClienteDeudaBitacora() {
    return idClienteDeudaBitacora;
  }

  public void setIdClienteDeudaEstatus(Long idClienteDeudaEstatus) {
    this.idClienteDeudaEstatus = idClienteDeudaEstatus;
  }

  public Long getIdClienteDeudaEstatus() {
    return idClienteDeudaEstatus;
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

  public void setIdClienteDeuda(Long idClienteDeuda) {
    this.idClienteDeuda = idClienteDeuda;
  }

  public Long getIdClienteDeuda() {
    return idClienteDeuda;
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
  	return getIdClienteDeudaBitacora();
  }

  @Override
  public void setKey(Long key) {
  	this.idClienteDeudaBitacora = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdClienteDeudaBitacora());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdClienteDeudaEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getJustificacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdClienteDeuda());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idClienteDeudaBitacora", getIdClienteDeudaBitacora());
		regresar.put("idClienteDeudaEstatus", getIdClienteDeudaEstatus());
		regresar.put("justificacion", getJustificacion());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idClienteDeuda", getIdClienteDeuda());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getIdClienteDeudaBitacora(), getIdClienteDeudaEstatus(), getJustificacion(), getIdUsuario(), getIdClienteDeuda(), getRegistro()
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
    regresar.append("idClienteDeudaBitacora~");
    regresar.append(getIdClienteDeudaBitacora());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdClienteDeudaBitacora());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticClientesDeudasBitacoraDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdClienteDeudaBitacora()!= null && getIdClienteDeudaBitacora()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticClientesDeudasBitacoraDto other = (TcManticClientesDeudasBitacoraDto) obj;
    if (getIdClienteDeudaBitacora() != other.idClienteDeudaBitacora && (getIdClienteDeudaBitacora() == null || !getIdClienteDeudaBitacora().equals(other.idClienteDeudaBitacora))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdClienteDeudaBitacora() != null ? getIdClienteDeudaBitacora().hashCode() : 0);
    return hash;
  }

}


