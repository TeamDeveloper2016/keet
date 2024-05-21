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
@Table(name="tc_keet_requisiciones_ordenes")
public class TcKeetRequisicionesOrdenesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_requisicion_detalle")
  private Long idRequisicionDetalle;
  @Column (name="id_eliminado")
  private Long idEliminado;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="observaciones")
  private String observaciones;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_requisicion_orden")
  private Long idRequisicionOrden;
  @Column (name="id_orden_compra")
  private Long idOrdenCompra;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetRequisicionesOrdenesDto() {
    this(-1L);
  }

  public TcKeetRequisicionesOrdenesDto(Long key) {
    this(-1L, 2L, -1L, null, -1L, -1L);
    setKey(key);
  }

  public TcKeetRequisicionesOrdenesDto(Long idRequisicionDetalle, Long idEliminado, Long idUsuario, String observaciones, Long idRequisicionOrden, Long idOrdenCompra) {
    setIdRequisicionDetalle(idRequisicionDetalle);
    setIdEliminado(idEliminado);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setIdRequisicionOrden(idRequisicionOrden);
    setIdOrdenCompra(idOrdenCompra);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdRequisicionDetalle(Long idRequisicionDetalle) {
    this.idRequisicionDetalle = idRequisicionDetalle;
  }

  public Long getIdRequisicionDetalle() {
    return idRequisicionDetalle;
  }

  public void setIdEliminado(Long idEliminado) {
    this.idEliminado = idEliminado;
  }

  public Long getIdEliminado() {
    return idEliminado;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdRequisicionOrden(Long idRequisicionOrden) {
    this.idRequisicionOrden = idRequisicionOrden;
  }

  public Long getIdRequisicionOrden() {
    return idRequisicionOrden;
  }

  public void setIdOrdenCompra(Long idOrdenCompra) {
    this.idOrdenCompra = idOrdenCompra;
  }

  public Long getIdOrdenCompra() {
    return idOrdenCompra;
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
  	return getIdRequisicionOrden();
  }

  @Override
  public void setKey(Long key) {
  	this.idRequisicionOrden = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdRequisicionDetalle());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEliminado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdRequisicionOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdOrdenCompra());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idRequisicionDetalle", getIdRequisicionDetalle());
		regresar.put("idEliminado", getIdEliminado());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idRequisicionOrden", getIdRequisicionOrden());
		regresar.put("idOrdenCompra", getIdOrdenCompra());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getIdRequisicionDetalle(), getIdEliminado(), getIdUsuario(), getObservaciones(), getIdRequisicionOrden(), getIdOrdenCompra(), getRegistro()
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
    regresar.append("idRequisicionOrden~");
    regresar.append(getIdRequisicionOrden());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdRequisicionOrden());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetRequisicionesOrdenesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdRequisicionOrden()!= null && getIdRequisicionOrden()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetRequisicionesOrdenesDto other = (TcKeetRequisicionesOrdenesDto) obj;
    if (getIdRequisicionOrden() != other.idRequisicionOrden && (getIdRequisicionOrden() == null || !getIdRequisicionOrden().equals(other.idRequisicionOrden))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdRequisicionOrden() != null ? getIdRequisicionOrden().hashCode() : 0);
    return hash;
  }

}


