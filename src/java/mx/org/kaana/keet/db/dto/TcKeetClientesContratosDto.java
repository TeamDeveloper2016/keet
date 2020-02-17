package mx.org.kaana.keet.db.dto;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
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
@Table(name="tc_keet_clientes_contratos")
public class TcKeetClientesContratosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="clave")
  private Long clave;
  @Column (name="id_cliente")
  private Long idCliente;
  @Column (name="id_domicilio")
  private Long idDomicilio;
  @Column (name="recepcion")
  private Date recepcion;
  @Column (name="aceptacion")
  private Date aceptacion;
  @Column (name="nombre")
  private String nombre;
  @Column (name="firma")
  private Date firma;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private Timestamp registro;
  @Column (name="consecutivo")
  private String consecutivo;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_cliente_contrato")
  private Long idClienteContrato;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="orden")
  private Long orden;

  public TcKeetClientesContratosDto() {
    this(new Long(-1L));
  }

  public TcKeetClientesContratosDto(Long key) {
    this(null, null, null, new Date(Calendar.getInstance().getTimeInMillis()), new Date(Calendar.getInstance().getTimeInMillis()), null, new Date(Calendar.getInstance().getTimeInMillis()), null, null, new Long(-1L), null, null, null, null);
    setKey(key);
  }

  public TcKeetClientesContratosDto(Long clave, Long idCliente, Long idDomicilio, Date recepcion, Date aceptacion, String nombre, Date firma, Long ejercicio, String consecutivo, Long idClienteContrato, Long idUsuario, String observaciones, Long idEmpresa, Long orden) {
    setClave(clave);
    setIdCliente(idCliente);
    setIdDomicilio(idDomicilio);
    setRecepcion(recepcion);
    setAceptacion(aceptacion);
    setNombre(nombre);
    setFirma(firma);
    setEjercicio(ejercicio);
    setRegistro(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    setConsecutivo(consecutivo);
    setIdClienteContrato(idClienteContrato);
    setIdUsuario(idUsuario);
    setObservaciones(observaciones);
    setIdEmpresa(idEmpresa);
    setOrden(orden);
  }
	
  public void setClave(Long clave) {
    this.clave = clave;
  }

  public Long getClave() {
    return clave;
  }

  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public Long getIdCliente() {
    return idCliente;
  }

  public void setIdDomicilio(Long idDomicilio) {
    this.idDomicilio = idDomicilio;
  }

  public Long getIdDomicilio() {
    return idDomicilio;
  }

  public void setRecepcion(Date recepcion) {
    this.recepcion = recepcion;
  }

  public Date getRecepcion() {
    return recepcion;
  }

  public void setAceptacion(Date aceptacion) {
    this.aceptacion = aceptacion;
  }

  public Date getAceptacion() {
    return aceptacion;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getNombre() {
    return nombre;
  }

  public void setFirma(Date firma) {
    this.firma = firma;
  }

  public Date getFirma() {
    return firma;
  }

  public void setEjercicio(Long ejercicio) {
    this.ejercicio = ejercicio;
  }

  public Long getEjercicio() {
    return ejercicio;
  }

  public void setRegistro(Timestamp registro) {
    this.registro = registro;
  }

  public Timestamp getRegistro() {
    return registro;
  }

  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public String getConsecutivo() {
    return consecutivo;
  }

  public void setIdClienteContrato(Long idClienteContrato) {
    this.idClienteContrato = idClienteContrato;
  }

  public Long getIdClienteContrato() {
    return idClienteContrato;
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

  public void setIdEmpresa(Long idEmpresa) {
    this.idEmpresa = idEmpresa;
  }

  public Long getIdEmpresa() {
    return idEmpresa;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdClienteContrato();
  }

  @Override
  public void setKey(Long key) {
  	this.idClienteContrato = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCliente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDomicilio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRecepcion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getAceptacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNombre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFirma());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdClienteContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("clave", getClave());
		regresar.put("idCliente", getIdCliente());
		regresar.put("idDomicilio", getIdDomicilio());
		regresar.put("recepcion", getRecepcion());
		regresar.put("aceptacion", getAceptacion());
		regresar.put("nombre", getNombre());
		regresar.put("firma", getFirma());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idClienteContrato", getIdClienteContrato());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("orden", getOrden());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
    getClave(), getIdCliente(), getIdDomicilio(), getRecepcion(), getAceptacion(), getNombre(), getFirma(), getEjercicio(), getRegistro(), getConsecutivo(), getIdClienteContrato(), getIdUsuario(), getObservaciones(), getIdEmpresa(), getOrden()
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
    regresar.append("idClienteContrato~");
    regresar.append(getIdClienteContrato());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdClienteContrato());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetClientesContratosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdClienteContrato()!= null && getIdClienteContrato()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetClientesContratosDto other = (TcKeetClientesContratosDto) obj;
    if (getIdClienteContrato() != other.idClienteContrato && (getIdClienteContrato() == null || !getIdClienteContrato().equals(other.idClienteContrato))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdClienteContrato() != null ? getIdClienteContrato().hashCode() : 0);
    return hash;
  }

}


