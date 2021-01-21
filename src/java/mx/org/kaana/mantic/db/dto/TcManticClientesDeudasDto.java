package mx.org.kaana.mantic.db.dto;

import java.io.Serializable;
import java.time.LocalDate;
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
@Table(name="tc_mantic_clientes_deudas")
public class TcManticClientesDeudasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_cliente_deuda_estatus")
  private Long idClienteDeudaEstatus;
  @Column (name="id_cliente")
  private Long idCliente;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_cliente_deuda")
  private Long idClienteDeuda;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="saldo")
  private Double saldo;
  @Column (name="limite")
  private LocalDate limite;
  @Column (name="id_venta")
  private Long idVenta;
  @Column (name="importe")
  private Double importe;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcManticClientesDeudasDto() {
    this(new Long(-1L));
  }

  public TcManticClientesDeudasDto(Long key) {
    this(null, null, null, new Long(-1L), null, null, LocalDate.now(), null, null);
    setKey(key);
  }

  public TcManticClientesDeudasDto(Long idClienteEstatus, Long idCliente, Long idUsuario, Long idClienteDeuda, String observaciones, Double saldo, LocalDate limite, Long idVenta, Double importe) {
    setIdClienteDeudaEstatus(idClienteEstatus);
    setIdCliente(idCliente);
    setIdUsuario(idUsuario);
    setIdClienteDeuda(idClienteDeuda);
    setObservaciones(observaciones);
    setSaldo(saldo);
    setLimite(limite);
    setIdVenta(idVenta);
    setImporte(importe);
    setRegistro(LocalDateTime.now());
  }
	
  public void setIdClienteDeudaEstatus(Long idClienteDeudaEstatus) {
    this.idClienteDeudaEstatus = idClienteDeudaEstatus;
  }

  public Long getIdClienteDeudaEstatus() {
    return idClienteDeudaEstatus;
  }

  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public Long getIdCliente() {
    return idCliente;
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

  public void setLimite(LocalDate limite) {
    this.limite = limite;
  }

  public LocalDate getLimite() {
    return limite;
  }

  public void setIdVenta(Long idVenta) {
    this.idVenta = idVenta;
  }

  public Long getIdVenta() {
    return idVenta;
  }

  public void setImporte(Double importe) {
    this.importe = importe;
  }

  public Double getImporte() {
    return importe;
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
  	return getIdClienteDeuda();
  }

  @Override
  public void setKey(Long key) {
  	this.idClienteDeuda = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdClienteDeudaEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCliente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdClienteDeuda());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSaldo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLimite());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdVenta());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idClienteEstatus", getIdClienteDeudaEstatus());
		regresar.put("idCliente", getIdCliente());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idClienteDeuda", getIdClienteDeuda());
		regresar.put("observaciones", getObservaciones());
		regresar.put("saldo", getSaldo());
		regresar.put("limite", getLimite());
		regresar.put("idVenta", getIdVenta());
		regresar.put("importe", getImporte());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
      getIdClienteDeudaEstatus(), getIdCliente(), getIdUsuario(), getIdClienteDeuda(), getObservaciones(), getSaldo(), getLimite(), getIdVenta(), getImporte(), getRegistro()
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
    regresar.append("idClienteDeuda~");
    regresar.append(getIdClienteDeuda());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdClienteDeuda());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticClientesDeudasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdClienteDeuda()!= null && getIdClienteDeuda()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticClientesDeudasDto other = (TcManticClientesDeudasDto) obj;
    if (getIdClienteDeuda() != other.idClienteDeuda && (getIdClienteDeuda() == null || !getIdClienteDeuda().equals(other.idClienteDeuda))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdClienteDeuda() != null ? getIdClienteDeuda().hashCode() : 0);
    return hash;
  }

}


