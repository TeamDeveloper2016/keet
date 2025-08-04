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
@Table(name="tc_keet_gastos")
public class TcKeetGastosDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_gasto")
  private Long idGasto;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="articulos")
  private Long articulos;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_empresa_persona")
  private Long idEmpresaPersona;
  @Column (name="id_abono")
  private Long idAbono;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="orden")
  private Long orden;
  @Column (name="id_caja_chica_cierre")
  private Long idCajaChicaCierre;
  @Column (name="importe")
  private Double importe;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="id_gasto_estatus")
  private Long idGastoEstatus;
  @Column (name="revisado")
  private Long revisado;
  @Column (name="id_tipo_medio_pago")
  private Long idTipoMedioPago;
  @Column (name="id_contrato")
  private Long idContrato;
  @Column (name="id_desarrollo")
  private Long idDesarrollo;
  @Column (name="registro")
  private LocalDateTime registro;

  public TcKeetGastosDto() {
    this(new Long(-1L));
  }

  public TcKeetGastosDto(Long key) {
    this(new Long(-1L), null, null, null, null, null, null, null, null, null, null, null, 2L, 1L, null, null);
    setKey(key);
  }

  public TcKeetGastosDto(Long idGasto, String consecutivo, Long articulos, Long idUsuario, Long idEmpresaPersona, Long idAbono, Long idEmpresa, Long orden, Long idCajaChicaCierre, Double importe, Long ejercicio, Long idGastoEstatus, Long revisado, Long idTipoMedioPago, Long idContrato, Long idDesarrollo) {
    setIdGasto(idGasto);
    setConsecutivo(consecutivo);
    setArticulos(articulos);
    setIdUsuario(idUsuario);
    setIdEmpresaPersona(idEmpresaPersona);
    setIdAbono(idAbono);
    setIdEmpresa(idEmpresa);
    setOrden(orden);
    setIdCajaChicaCierre(idCajaChicaCierre);
    setImporte(importe);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
		setIdGastoEstatus(idGastoEstatus);
		setRevisado(revisado);
    this.idTipoMedioPago= idTipoMedioPago;
    this.idContrato= idContrato;
    this.idDesarrollo= idDesarrollo;
  }
	
  public void setIdGasto(Long idGasto) {
    this.idGasto = idGasto;
  }

  public Long getIdGasto() {
    return idGasto;
  }

  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public String getConsecutivo() {
    return consecutivo;
  }

  public void setArticulos(Long articulos) {
    this.articulos = articulos;
  }

  public Long getArticulos() {
    return articulos;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdEmpresaPersona(Long idEmpresaPersona) {
    this.idEmpresaPersona = idEmpresaPersona;
  }

  public Long getIdEmpresaPersona() {
    return idEmpresaPersona;
  }

  public void setIdAbono(Long idAbono) {
    this.idAbono = idAbono;
  }

  public Long getIdAbono() {
    return idAbono;
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

  public void setIdCajaChicaCierre(Long idCajaChicaCierre) {
    this.idCajaChicaCierre = idCajaChicaCierre;
  }

  public Long getIdCajaChicaCierre() {
    return idCajaChicaCierre;
  }

  public void setImporte(Double importe) {
    this.importe = importe;
  }

  public Double getImporte() {
    return importe;
  }

  public void setEjercicio(Long ejercicio) {
    this.ejercicio = ejercicio;
  }

  public Long getEjercicio() {
    return ejercicio;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

	public Long getIdGastoEstatus() {
		return idGastoEstatus;
	}

	public void setIdGastoEstatus(Long idGastoEstatus) {
		this.idGastoEstatus = idGastoEstatus;
	}	

	public Long getRevisado() {
		return revisado;
	}

	public void setRevisado(Long revisado) {
		this.revisado = revisado;
	}	

  public Long getIdTipoMedioPago() {
    return idTipoMedioPago;
  }

  public void setIdTipoMedioPago(Long idTipoMedioPago) {
    this.idTipoMedioPago = idTipoMedioPago;
  }

  public Long getIdContrato() {
    return idContrato;
  }

  public void setIdContrato(Long idContrato) {
    this.idContrato = idContrato;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }
	
  @Transient
  @Override
  public Long getKey() {
  	return getIdGasto();
  }

  @Override
  public void setKey(Long key) {
  	this.idGasto = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getIdGasto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getArticulos());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAbono());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdCajaChicaCierre());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getImporte());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdGastoEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRevisado());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoMedioPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDesarrollo());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("idGasto", getIdGasto());
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("articulos", getArticulos());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idEmpresaPersona", getIdEmpresaPersona());
		regresar.put("idAbono", getIdAbono());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("orden", getOrden());
		regresar.put("idCajaChicaCierre", getIdCajaChicaCierre());
		regresar.put("importe", getImporte());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("registro", getRegistro());
		regresar.put("idGastoEstatus", getIdGastoEstatus());
		regresar.put("revisado", getRevisado());
		regresar.put("idTipoMedioPago", getIdTipoMedioPago());
		regresar.put("idDesarrollo", getIdDesarrollo());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[]{
			getIdGasto(), getConsecutivo(), getArticulos(), getIdUsuario(), getIdEmpresaPersona(), getIdAbono(), getIdEmpresa(), getOrden(), getIdCajaChicaCierre(), getImporte(), getEjercicio(), getRegistro(), getIdGastoEstatus(), getRevisado(), getIdTipoMedioPago(), getIdDesarrollo()
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
    regresar.append("idGasto~");
    regresar.append(getIdGasto());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdGasto());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcKeetGastosDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdGasto()!= null && getIdGasto()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcKeetGastosDto other = (TcKeetGastosDto) obj;
    if (getIdGasto() != other.idGasto && (getIdGasto() == null || !getIdGasto().equals(other.idGasto))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdGasto() != null ? getIdGasto().hashCode() : 0);
    return hash;
  }
  
}