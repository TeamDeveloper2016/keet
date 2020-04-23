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
@Table(name="tc_keet_contratos_lotes_proveedores")
public class TcKeetContratosLotesProveedoresDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_proveedor")
  private Long idProveedor;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_contrato_lote")
  private Long idContratoLote;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_trabajo")
  private Long idTrabajo;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_lote_proveedor")
  private Long idContratoLoteProveedor;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetContratosLotesProveedoresDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosLotesProveedoresDto(Long key) {
    this(null, null, null, null, null, new Long(-1L));
    setKey(key);
  }

  public TcKeetContratosLotesProveedoresDto(Long idProveedor, Long idUsuario, Long idContratoLote, String observaciones, Long idTrabajo, Long idContratoLoteProveedor) {
    setIdProveedor(idProveedor);
    setIdUsuario(idUsuario);
    setIdContratoLote(idContratoLote);
    setObservaciones(observaciones);
    setIdTrabajo(idTrabajo);
    setIdContratoLoteProveedor(idContratoLoteProveedor);
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

  public void setIdContratoLote(Long idContratoLote) {
    this.idContratoLote = idContratoLote;
  }

  public Long getIdContratoLote() {
    return idContratoLote;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdTrabajo(Long idTrabajo) {
    this.idTrabajo = idTrabajo;
  }

  public Long getIdTrabajo() {
    return idTrabajo;
  }

  public void setIdContratoLoteProveedor(Long idContratoLoteProveedor) {
    this.idContratoLoteProveedor = idContratoLoteProveedor;
  }

  public Long getIdContratoLoteProveedor() {
    return idContratoLoteProveedor;
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
  	return getIdContratoLoteProveedor();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoLoteProveedor = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTrabajo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLoteProveedor());
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
		regresar.put("idContratoLote", getIdContratoLote());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idTrabajo", getIdTrabajo());
		regresar.put("idContratoLoteProveedor", getIdContratoLoteProveedor());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdProveedor(), getIdUsuario(), getIdContratoLote(), getObservaciones(), getIdTrabajo(), getIdContratoLoteProveedor(), getRegistro()
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
    regresar.append("idContratoLoteProveedor~");
    regresar.append(getIdContratoLoteProveedor());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoLoteProveedor());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosLotesProveedoresDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoLoteProveedor()!= null && getIdContratoLoteProveedor()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosLotesProveedoresDto other = (TcKeetContratosLotesProveedoresDto) obj;
    if (getIdContratoLoteProveedor() != other.idContratoLoteProveedor && (getIdContratoLoteProveedor() == null || !getIdContratoLoteProveedor().equals(other.idContratoLoteProveedor))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoLoteProveedor() != null ? getIdContratoLoteProveedor().hashCode() : 0);
    return hash;
  }
}