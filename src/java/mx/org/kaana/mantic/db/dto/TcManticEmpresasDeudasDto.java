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
@Table(name="tc_mantic_empresas_deudas")
public class TcManticEmpresasDeudasDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="id_empresa_estatus")
  private Long idEmpresaEstatus;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_empresa_deuda")
  private Long idEmpresaDeuda;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="saldo")
  private Double saldo;
  @Column (name="pagar")
  private Double pagar;
  @Column (name="id_nota_entrada")
  private Long idNotaEntrada;
  @Column (name="limite")
  private LocalDate limite;
  @Column (name="importe")
  private Double importe;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="id_revisado")
  private Long idRevisado;
  @Column (name="id_completo")
  private Long idCompleto;
  @Column (name="fecha_recepcion")
  private LocalDate fechaRecepcion;
  @Column (name="id_recibio")
  private Long idRecibio;
  @Column (name="id_proveedor_pago")
  private Long idProveedorPago;
  
  
  public TcManticEmpresasDeudasDto() {
    this(new Long(-1L));
  }

  public TcManticEmpresasDeudasDto(Long key) {
    this(null, null, new Long(-1L), null, null, null, null, LocalDate.now(), null, 0D, 2L, 1L, LocalDate.now(), null, null);
    setKey(key);
  }

  public TcManticEmpresasDeudasDto(Long idEmpresaEstatus, Long idUsuario, Long idEmpresaDeuda, String observaciones, Long idEmpresa, Double saldo, Long idNotaEntrada, LocalDate limite, Double importe, Double pagar, Long idRevisado, Long idCompleto, LocalDate fechaRecepcion, Long idRecibio, Long idProveedorPago) {
    setIdEmpresaEstatus(idEmpresaEstatus);
    setIdUsuario(idUsuario);
    setIdEmpresaDeuda(idEmpresaDeuda);
    setObservaciones(observaciones);
    setIdEmpresa(idEmpresa);
    setSaldo(saldo);
    setIdNotaEntrada(idNotaEntrada);
    setLimite(limite);
    setImporte(importe);
    setRegistro(LocalDateTime.now());
		this.pagar= pagar;
    this.idRevisado= idRevisado;
    this.idCompleto= idCompleto;
    this.fechaRecepcion= fechaRecepcion;
    this.idRecibio= idRecibio;
    this.idProveedorPago= idProveedorPago;
  }
	
  public void setIdEmpresaEstatus(Long idEmpresaEstatus) {
    this.idEmpresaEstatus = idEmpresaEstatus;
  }

  public Long getIdEmpresaEstatus() {
    return idEmpresaEstatus;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdEmpresaDeuda(Long idEmpresaDeuda) {
    this.idEmpresaDeuda = idEmpresaDeuda;
  }

  public Long getIdEmpresaDeuda() {
    return idEmpresaDeuda;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setIdEmpresa(Long idEmpresa) {
    this.idEmpresa = idEmpresa;
  }

  public Long getIdEmpresa() {
    return idEmpresa;
  }

  public void setSaldo(Double saldo) {
    this.saldo = saldo;
  }

  public Double getSaldo() {
    return saldo;
  }

  public void setIdNotaEntrada(Long idNotaEntrada) {
    this.idNotaEntrada = idNotaEntrada;
  }

  public Long getIdNotaEntrada() {
    return idNotaEntrada;
  }

  public void setLimite(LocalDate limite) {
    this.limite = limite;
  }

  public LocalDate getLimite() {
    return limite;
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

	public Double getPagar() {
		return pagar;
	}

	public void setPagar(Double pagar) {
		this.pagar=pagar;
	}

  public Long getIdRevisado() {
    return idRevisado;
  }

  public void setIdRevisado(Long idRevisado) {
    this.idRevisado = idRevisado;
  }

  public Long getIdCompleto() {
    return idCompleto;
  }

  public void setIdCompleto(Long idCompleto) {
    this.idCompleto = idCompleto;
  }

  public LocalDate getFechaRecepcion() {
    return fechaRecepcion;
  }

  public void setFechaRecepcion(LocalDate fechaRecepcion) {
    this.fechaRecepcion = fechaRecepcion;
  }

  public Long getIdRecibio() {
    return idRecibio;
  }

  public void setIdRecibio(Long idRecibio) {
    this.idRecibio = idRecibio;
  }

  public Long getIdProveedorPago() {
    return idProveedorPago;
  }

  public void setIdProveedorPago(Long idProveedorPago) {
    this.idProveedorPago = idProveedorPago;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdEmpresaDeuda();
  }

  @Override
  public void setKey(Long key) {
  	this.idEmpresaDeuda = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdEmpresaEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaDeuda());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSaldo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNotaEntrada());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getLimite());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getPagar());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdRevisado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCompleto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFechaRecepcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdRecibio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdProveedorPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idEmpresaEstatus", getIdEmpresaEstatus());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idEmpresaDeuda", getIdEmpresaDeuda());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("saldo", getSaldo());
		regresar.put("idNotaEntrada", getIdNotaEntrada());
		regresar.put("limite", getLimite());
		regresar.put("importe", getImporte());
		regresar.put("pagar", getPagar());
		regresar.put("idRevisado", getIdRevisado());
		regresar.put("idCompleto", getIdCompleto());
		regresar.put("fechaRecepcion", getFechaRecepcion());
		regresar.put("idRecibio", getIdRecibio());
		regresar.put("idProveedorPago", getIdProveedorPago());
		regresar.put("registro", getRegistro());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
      getIdEmpresaEstatus(), getIdUsuario(), getIdEmpresaDeuda(), getObservaciones(), getIdEmpresa(), getSaldo(), getIdNotaEntrada(), getLimite(), getImporte(), getPagar(), getIdRevisado(), getIdCompleto(), getFechaRecepcion(), getIdRecibio(), getIdProveedorPago(), getRegistro()
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
    regresar.append("idEmpresaDeuda~");
    regresar.append(getIdEmpresaDeuda());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdEmpresaDeuda());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticEmpresasDeudasDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdEmpresaDeuda()!= null && getIdEmpresaDeuda()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticEmpresasDeudasDto other = (TcManticEmpresasDeudasDto) obj;
    if (getIdEmpresaDeuda() != other.idEmpresaDeuda && (getIdEmpresaDeuda() == null || !getIdEmpresaDeuda().equals(other.idEmpresaDeuda))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdEmpresaDeuda() != null ? getIdEmpresaDeuda().hashCode() : 0);
    return hash;
  }

}


