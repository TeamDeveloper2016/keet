package mx.org.kaana.mantic.db.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.LocalDate;
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
@Table(name="tr_mantic_empresa_personal")
public class TrManticEmpresaPersonalDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="clave")
  private String clave;
  @Column (name="id_puesto")
  private Long idPuesto;
  @Column (name="id_contrato")
  private Long idContrato;
  @Column (name="factor_infonavit")
  private Double factorInfonavit;
  @Column (name="diario_imss")
  private Double diarioImss;
  @Column (name="id_contratista")
  private Long idContratista;
  @Column (name="nss")
  private String nss;
  @Column (name="registro")
  private LocalDateTime registro;
  @Column (name="id_persona")
  private Long idPersona;
  @Column (name="id_activo")
  private Long idActivo;
  @Column (name="infonavit")
  private String infonavit;
  @Column (name="contratacion")
  private LocalDate contratacion;
  @Column (name="baja")
  private LocalDate baja;
  @Column (name="id_departamento")
  private Long idDepartamento;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_empresa_persona")
  private Long idEmpresaPersona;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="id_empresa")
  private Long idEmpresa;
  @Column (name="ingreso")
  private LocalDate ingreso;
  @Column (name="sueldo_semanal")
  private Double sueldoSemanal;
  @Column (name="sueldo_mensual")
  private Double sueldoMensual;
  @Column (name="sueldo_imss")
  private Double sueldoImss;
  @Column (name="id_nomina")
  private Long idNomina;
	@Column (name="id_seguro")
  private Long idSeguro;
	@Column (name="sobre_sueldo")
  private Double sobreSueldo;
	@Column (name="id_limpiar")
  private Long idLimpiar;
	@Column (name="id_por_dia")
  private Long idPorDia;
	@Column (name="id_tipo_medio_pago")
  private Long idTipoMedioPago;
	@Column (name="id_agrupacion")
  private Long idAgrupacion;

  public TrManticEmpresaPersonalDto() {
    this(new Long(-1L));
  }

  public TrManticEmpresaPersonalDto(Long key) {
    this(null, null, 2L, null, 0D, null, null, null, null, null, LocalDate.now(), null, null, null, new Long(-1L), null, null, LocalDate.now(), 0D, 0D, 0D, 2L, 2L, 0D, 1L, 2L, 1L, 1L);
    setKey(key);
  }

  public TrManticEmpresaPersonalDto(String clave, Long idPuesto, Long idContrato, Double factorInfonavit, Double diarioImss, Long idContratista, String nss, Long idPersona, Long idActivo, String infonavit, LocalDate contratacion, LocalDate baja, Long idDepartamento, Long idUsuario, Long idEmpresaPersona, String observaciones, Long idEmpresa, LocalDate ingreso, Double sueldoSemanal, Double sueldoMensual, Double sueldoImss, Long idNomina, Long idSeguro, Double sobreSueldo, Long idLimpiar, Long idPorDia, Long idTipoMedioPago, Long idAgrupacion) {
    setClave(clave);
    setIdPuesto(idPuesto);
    setIdContrato(idContrato);
    setFactorInfonavit(factorInfonavit);
    setDiarioImss(diarioImss);
    setIdContratista(idContratista);
    setNss(nss);
    setRegistro(LocalDateTime.now());
    setIdPersona(idPersona);
    setIdActivo(idActivo);
    setInfonavit(infonavit);
    setContratacion(contratacion);
    setBaja(baja);
    setIdDepartamento(idDepartamento);
    setIdUsuario(idUsuario);
    setIdEmpresaPersona(idEmpresaPersona);
    setObservaciones(observaciones);
    setIdEmpresa(idEmpresa);
    setIngreso(ingreso);
    setSueldoSemanal(sueldoSemanal);
    setSueldoMensual(sueldoMensual);
    setSueldoImss(sueldoImss);
		setIdNomina(idNomina);
		setIdSeguro(idSeguro);	
    this.sobreSueldo= sobreSueldo;
    this.idLimpiar= idLimpiar;
    this.idPorDia= idPorDia;
    this.idTipoMedioPago= idTipoMedioPago;
    this.idAgrupacion= idAgrupacion;
  }
	
  public void setClave(String clave) {
    this.clave = clave;
  }

  public String getClave() {
    return clave;
  }

  public void setIdPuesto(Long idPuesto) {
    this.idPuesto = idPuesto;
  }

  public Long getIdPuesto() {
    return idPuesto;
  }

	public Long getIdContrato() {
		return idContrato;
	}

	public void setIdContrato(Long idContrato) {
		this.idContrato=idContrato;
	}

  public void setFactorInfonavit(Double factorInfonavit) {
    this.factorInfonavit = factorInfonavit;
  }

  public Double getFactorInfonavit() {
    return factorInfonavit;
  }

  public void setDiarioImss(Double diarioImss) {
    this.diarioImss = diarioImss;
  }

  public Double getDiarioImss() {
    return diarioImss;
  }

  public void setIdContratista(Long idContratista) {
    this.idContratista = idContratista;
  }

  public Long getIdContratista() {
    return idContratista;
  }

  public void setNss(String nss) {
    this.nss = nss;
  }

  public String getNss() {
    return nss;
  }

  public void setRegistro(LocalDateTime registro) {
    this.registro = registro;
  }

  public LocalDateTime getRegistro() {
    return registro;
  }

  public void setIdPersona(Long idPersona) {
    this.idPersona = idPersona;
  }

  public Long getIdPersona() {
    return idPersona;
  }

  public void setIdActivo(Long idActivo) {
    this.idActivo = idActivo;
  }

  public Long getIdActivo() {
    return idActivo;
  }

  public void setInfonavit(String infonavit) {
    this.infonavit = infonavit;
  }

  public String getInfonavit() {
    return infonavit;
  }

  public void setContratacion(LocalDate contratacion) {
    this.contratacion = contratacion;
  }

  public LocalDate getContratacion() {
    return contratacion;
  }

	public LocalDate getBaja() {
		return baja;
	}

	public void setBaja(LocalDate baja) {
		this.baja = baja;
	}	
	
  public void setIdDepartamento(Long idDepartamento) {
    this.idDepartamento = idDepartamento;
  }

  public Long getIdDepartamento() {
    return idDepartamento;
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

  public void setIngreso(LocalDate ingreso) {
    this.ingreso = ingreso;
  }

  public LocalDate getIngreso() {
    return ingreso;
  }

  public void setSueldoSemanal(Double sueldoSemanal) {
    this.sueldoSemanal = sueldoSemanal;
  }

  public Double getSueldoSemanal() {
    return sueldoSemanal;
  }

  public void setSueldoMensual(Double sueldoMensual) {
    this.sueldoMensual = sueldoMensual;
  }

  public Double getSueldoMensual() {
    return sueldoMensual;
  }

  public void setSueldoImss(Double sueldoImss) {
    this.sueldoImss = sueldoImss;
  }

  public Double getSueldoImss() {
    return sueldoImss;
  }

	public Long getIdNomina() {
		return idNomina;
	}

	public void setIdNomina(Long idNomina) {
		this.idNomina=idNomina;
	}

	public Long getIdSeguro() {
		return idSeguro;
	}

	public void setIdSeguro(Long idSeguro) {
		this.idSeguro = idSeguro;
	}	

  public Double getSobreSueldo() {
    return sobreSueldo;
  }

  public void setSobreSueldo(Double sobreSueldo) {
    this.sobreSueldo = sobreSueldo;
  }

  public Long getIdLimpiar() {
    return idLimpiar;
  }

  public void setIdLimpiar(Long idLimpiar) {
    this.idLimpiar = idLimpiar;
  }

  public Long getIdPorDia() {
    return idPorDia;
  }

  public void setIdPorDia(Long idPorDia) {
    this.idPorDia = idPorDia;
  }

  public Long getIdTipoMedioPago() {
    return idTipoMedioPago;
  }

  public void setIdTipoMedioPago(Long idTipoMedioPago) {
    this.idTipoMedioPago = idTipoMedioPago;
  }

  public Long getIdAgrupacion() {
    return idAgrupacion;
  }

  public void setIdAgrupacion(Long idAgrupacion) {
    this.idAgrupacion = idAgrupacion;
  }

  @Transient
  @Override
  public Long getKey() {
  	return getIdEmpresaPersona();
  }

  @Override
  public void setKey(Long key) {
  	this.idEmpresaPersona = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getClave());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPuesto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContrato());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getFactorInfonavit());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getDiarioImss());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdContratista());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getNss());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdActivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getInfonavit());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getContratacion());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getBaja());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDepartamento());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresa());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIngreso());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSueldoSemanal());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSueldoMensual());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSueldoImss());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNomina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdSeguro());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getSobreSueldo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdLimpiar());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPorDia());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoMedioPago());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdAgrupacion());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("clave", getClave());
		regresar.put("idPuesto", getIdPuesto());
		regresar.put("idContrato", getIdContrato());
		regresar.put("factorInfonavit", getFactorInfonavit());
		regresar.put("diarioImss", getDiarioImss());
		regresar.put("idContratista", getIdContratista());
		regresar.put("nss", getNss());
		regresar.put("registro", getRegistro());
		regresar.put("idPersona", getIdPersona());
		regresar.put("idActivo", getIdActivo());
		regresar.put("infonavit", getInfonavit());
		regresar.put("contratacion", getContratacion());
		regresar.put("baja", getBaja());
		regresar.put("idDepartamento", getIdDepartamento());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idEmpresaPersona", getIdEmpresaPersona());
		regresar.put("observaciones", getObservaciones());
		regresar.put("idEmpresa", getIdEmpresa());
		regresar.put("ingreso", getIngreso());
		regresar.put("sueldoSemanal", getSueldoSemanal());
		regresar.put("sueldoMensual", getSueldoMensual());
		regresar.put("sueldoImss", getSueldoImss());
		regresar.put("idNomina", getIdNomina());
		regresar.put("idSeguro", getIdSeguro());
		regresar.put("sobreSueldo", getSobreSueldo());
		regresar.put("idLimpiar", getIdLimpiar());
		regresar.put("idPorDia", getIdPorDia());
		regresar.put("idTipoMedioPago", getIdTipoMedioPago());
		regresar.put("idAgrupacion", getIdAgrupacion());
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
			getClave(), getIdPuesto(), getIdContrato(), getFactorInfonavit(), getDiarioImss(), getIdContratista(), getNss(), getRegistro(), getIdPersona(), getIdActivo(), getInfonavit(), getContratacion(), getBaja(), getIdDepartamento(), getIdUsuario(), getIdEmpresaPersona(), getObservaciones(), getIdEmpresa(), getIngreso(), getSueldoSemanal(), getSueldoMensual(), getSueldoImss(), getIdNomina(), getIdSeguro(), getSobreSueldo(), getIdLimpiar(), getIdPorDia(), getIdTipoMedioPago(), getIdAgrupacion()
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
    regresar.append("idEmpresaPersona~");
    regresar.append(getIdEmpresaPersona());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdEmpresaPersona());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TrManticEmpresaPersonalDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdEmpresaPersona()!= null && getIdEmpresaPersona()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TrManticEmpresaPersonalDto other = (TrManticEmpresaPersonalDto) obj;
    if (getIdEmpresaPersona() != other.idEmpresaPersona && (getIdEmpresaPersona() == null || !getIdEmpresaPersona().equals(other.idEmpresaPersona))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdEmpresaPersona() != null ? getIdEmpresaPersona().hashCode() : 0);
    return hash;
  }
  
}