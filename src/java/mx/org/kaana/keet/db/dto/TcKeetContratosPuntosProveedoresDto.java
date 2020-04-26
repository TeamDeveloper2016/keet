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
@Table(name="tc_keet_contratos_puntos_proveedores")
public class TcKeetContratosPuntosProveedoresDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_contrato_destajo_proveedor")
  private Long idContratoDestajoProveedor;
  @Column (name="id_punto_paquete")
  private Long idPuntoPaquete;
  @Column (name="id_revisado")
  private Long idRevisado;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_contrato_punto_proveedor")
  private Long idContratoPuntoProveedor;
  @Column (name="factor")
  private Double factor;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetContratosPuntosProveedoresDto() {
    this(new Long(-1L));
  }

  public TcKeetContratosPuntosProveedoresDto(Long key) {
    this(null, null, null, null, new Long(-1L), null);
    setKey(key);
  }

  public TcKeetContratosPuntosProveedoresDto(Long idContratoDestajoProveedor, Long idPuntoPaquete, Long idRevisado, Long idUsuario, Long idContratoPuntoProveedor, Double factor) {
    setIdContratoDestajoProveedor(idContratoDestajoProveedor);
    setIdPuntoPaquete(idPuntoPaquete);
    setIdRevisado(idRevisado);
    setIdUsuario(idUsuario);
    setIdContratoPuntoProveedor(idContratoPuntoProveedor);
    setFactor(factor);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdContratoDestajoProveedor(Long idContratoDestajoProveedor) {
    this.idContratoDestajoProveedor = idContratoDestajoProveedor;
  }

  public Long getIdContratoDestajoProveedor() {
    return idContratoDestajoProveedor;
  }

  public void setIdPuntoPaquete(Long idPuntoPaquete) {
    this.idPuntoPaquete = idPuntoPaquete;
  }

  public Long getIdPuntoPaquete() {
    return idPuntoPaquete;
  }

  public void setIdRevisado(Long idRevisado) {
    this.idRevisado = idRevisado;
  }

  public Long getIdRevisado() {
    return idRevisado;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdContratoPuntoProveedor(Long idContratoPuntoProveedor) {
    this.idContratoPuntoProveedor = idContratoPuntoProveedor;
  }

  public Long getIdContratoPuntoProveedor() {
    return idContratoPuntoProveedor;
  }

  public void setFactor(Double factor) {
    this.factor = factor;
  }

  public Double getFactor() {
    return factor;
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
  	return getIdContratoPuntoProveedor();
  }

  @Override
  public void setKey(Long key) {
  	this.idContratoPuntoProveedor = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdContratoDestajoProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPuntoPaquete());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdRevisado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratoPuntoProveedor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFactor());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idContratoDestajoProveedor", getIdContratoDestajoProveedor());
		regresar.put("idPuntoPaquete", getIdPuntoPaquete());
		regresar.put("idRevisado", getIdRevisado());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idContratoPuntoProveedor", getIdContratoPuntoProveedor());
		regresar.put("factor", getFactor());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getIdContratoDestajoProveedor(), getIdPuntoPaquete(), getIdRevisado(), getIdUsuario(), getIdContratoPuntoProveedor(), getFactor(), getRegistro()
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
    regresar.append("idContratoPuntoProveedor~");
    regresar.append(getIdContratoPuntoProveedor());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdContratoPuntoProveedor());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetContratosPuntosProveedoresDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdContratoPuntoProveedor()!= null && getIdContratoPuntoProveedor()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetContratosPuntosProveedoresDto other = (TcKeetContratosPuntosProveedoresDto) obj;
    if (getIdContratoPuntoProveedor() != other.idContratoPuntoProveedor && (getIdContratoPuntoProveedor() == null || !getIdContratoPuntoProveedor().equals(other.idContratoPuntoProveedor))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdContratoPuntoProveedor() != null ? getIdContratoPuntoProveedor().hashCode() : 0);
    return hash;
  }

}


