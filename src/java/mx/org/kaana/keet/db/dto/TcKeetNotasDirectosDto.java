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
@Table(name="tc_keet_notas_directos")
public class TcKeetNotasDirectosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="descripcion")
  private String descripcion;
  @Column (name="id_cliente")
  private Long idCliente;
  @Column (name="id_desarrollo")
  private Long idDesarrollo;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_contrato")
  private Long idContrato;
  @Column (name="id_nota_entrada")
  private Long idNotaEntrada;
  @Column (name="importe")
  private Double importe;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_nota_directo")
  private Long idNotaDirecto;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetNotasDirectosDto() {
    this(new Long(-1L));
  }

  public TcKeetNotasDirectosDto(Long key) {
    this(null, null, null, null, null, null, 0D, new Long(-1L));
    setKey(key);
  }

  public TcKeetNotasDirectosDto(String descripcion, Long idCliente, Long idDesarrollo, Long idUsuario, Long idContrato, Long idNotaEntrada, Double importe, Long idNotaDirecto) {
    setDescripcion(descripcion);
    setIdCliente(idCliente);
    setIdDesarrollo(idDesarrollo);
    setIdUsuario(idUsuario);
    setIdContrato(idContrato);
    setIdNotaEntrada(idNotaEntrada);
    setImporte(importe);
    setIdNotaDirecto(idNotaDirecto);
    setRegistro(LocalDateTime.now());
  }
	
  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public Long getIdCliente() {
    return idCliente;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdContrato(Long idContrato) {
    this.idContrato = idContrato;
  }

  public Long getIdContrato() {
    return idContrato;
  }

  public void setIdNotaEntrada(Long idNotaEntrada) {
    this.idNotaEntrada = idNotaEntrada;
  }

  public Long getIdNotaEntrada() {
    return idNotaEntrada;
  }

  public void setImporte(Double importe) {
    this.importe = importe;
  }

  public Double getImporte() {
    return importe;
  }

  public void setIdNotaDirecto(Long idNotaDirecto) {
    this.idNotaDirecto = idNotaDirecto;
  }

  public Long getIdNotaDirecto() {
    return idNotaDirecto;
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
  	return getIdNotaDirecto();
  }

  @Override
  public void setKey(Long key) {
  	this.idNotaDirecto = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getDescripcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCliente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaEntrada());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaDirecto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("descripcion", getDescripcion());
		regresar.put("idCliente", getIdCliente());
		regresar.put("idDesarrollo", getIdDesarrollo());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idContrato", getIdContrato());
		regresar.put("idNotaEntrada", getIdNotaEntrada());
		regresar.put("importe", getImporte());
		regresar.put("idNotaDirecto", getIdNotaDirecto());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getDescripcion(), getIdCliente(), getIdDesarrollo(), getIdUsuario(), getIdContrato(), getIdNotaEntrada(), getImporte(), getIdNotaDirecto(), getRegistro()
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
    regresar.append("idNotaDirecto~");
    regresar.append(getIdNotaDirecto());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdNotaDirecto());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNotasDirectosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdNotaDirecto()!= null && getIdNotaDirecto()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetNotasDirectosDto other = (TcKeetNotasDirectosDto) obj;
    if (getIdNotaDirecto() != other.idNotaDirecto && (getIdNotaDirecto() == null || !getIdNotaDirecto().equals(other.idNotaDirecto))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdNotaDirecto() != null ? getIdNotaDirecto().hashCode() : 0);
    return hash;
  }

}


