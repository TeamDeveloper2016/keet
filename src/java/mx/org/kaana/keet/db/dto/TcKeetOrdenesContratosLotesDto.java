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
@Table(name="tc_keet_ordenes_contratos_lotes")
public class TcKeetOrdenesContratosLotesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_orden_contrato_lote")
  private Long idOrdenContratoLote;
  @Column (name="id_familia")
  private Long idFamilia;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_orden_compra")
  private Long idOrdenCompra;
  @Column (name="id_contrato_lote")
  private Long idContratoLote;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetOrdenesContratosLotesDto() {
    this(new Long(-1L));
  }

  public TcKeetOrdenesContratosLotesDto(Long key) {
    this(new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcKeetOrdenesContratosLotesDto(Long idOrdenContratoLote, Long idFamilia, Long idUsuario, Long idOrdenCompra, Long idContratoLote) {
    setIdOrdenContratoLote(idOrdenContratoLote);
    setIdFamilia(idFamilia);
    setIdUsuario(idUsuario);
    setIdOrdenCompra(idOrdenCompra);
    setIdContratoLote(idContratoLote);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdOrdenContratoLote(Long idOrdenContratoLote) {
    this.idOrdenContratoLote = idOrdenContratoLote;
  }

  public Long getIdOrdenContratoLote() {
    return idOrdenContratoLote;
  }

  public void setIdFamilia(Long idFamilia) {
    this.idFamilia = idFamilia;
  }

  public Long getIdFamilia() {
    return idFamilia;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdOrdenCompra(Long idOrdenCompra) {
    this.idOrdenCompra = idOrdenCompra;
  }

  public Long getIdOrdenCompra() {
    return idOrdenCompra;
  }

  public void setIdContratoLote(Long idContratoLote) {
    this.idContratoLote = idContratoLote;
  }

  public Long getIdContratoLote() {
    return idContratoLote;
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
  	return getIdOrdenContratoLote();
  }

  @Override
  public void setKey(Long key) {
  	this.idOrdenContratoLote = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdOrdenContratoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdFamilia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdOrdenCompra());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoLote());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idOrdenContratoLote", getIdOrdenContratoLote());
		regresar.put("idFamilia", getIdFamilia());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idOrdenCompra", getIdOrdenCompra());
		regresar.put("idContratoLote", getIdContratoLote());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdOrdenContratoLote(), getIdFamilia(), getIdUsuario(), getIdOrdenCompra(), getIdContratoLote(), getRegistro()
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
    regresar.append("idOrdenContratoLote~");
    regresar.append(getIdOrdenContratoLote());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdOrdenContratoLote());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetOrdenesContratosLotesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdOrdenContratoLote()!= null && getIdOrdenContratoLote()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetOrdenesContratosLotesDto other = (TcKeetOrdenesContratosLotesDto) obj;
    if (getIdOrdenContratoLote() != other.idOrdenContratoLote && (getIdOrdenContratoLote() == null || !getIdOrdenContratoLote().equals(other.idOrdenContratoLote))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdOrdenContratoLote() != null ? getIdOrdenContratoLote().hashCode() : 0);
    return hash;
  }

}


