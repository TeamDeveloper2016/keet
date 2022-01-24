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
@Table(name="tc_mantic_incidentes")
public class TcManticIncidentesDto implements IBaseDto, Serializable {
		
  private static final long serialVersionUID=1L;
  @Column (name="consecutivo")
  private String consecutivo;
  @Column (name="id_tipo_incidente")
  private Long idTipoIncidente;
  @Column (name="id_empresa_persona")
  private Long idEmpresaPersona;
  @Id
  @GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
	@Column (name="id_incidente")
  private Long idIncidente;
  @Column (name="inicio")
  private LocalDate inicio;
  @Column (name="id_usuario")
  private Long idUsuario;
  @Column (name="id_incidente_estatus")
  private Long idIncidenteEstatus;
  @Column (name="observaciones")
  private String observaciones;
  @Column (name="orden")
  private Long orden;
  @Column (name="termino")
  private LocalDate termino;
  @Column (name="ejercicio")
  private Long ejercicio;
  @Column (name="registro")
  private LocalDateTime registro;
	@Column (name="id_desarrollo")
  private Long idDesarrollo;
	@Column (name="id_nomina")
  private Long idNomina;
	@Column (name="costo")
  private Double costo;
	@Column (name="id_prestamo")
  private Long idPrestamo;

  public TcManticIncidentesDto() {
    this(new Long(-1L));
  }

  public TcManticIncidentesDto(Long key) {
    this(null, null, null, new Long(-1L), LocalDate.now(), null, null, null, null, LocalDate.now(), null, null, 0D, null, null);
    setKey(key);
  }

  public TcManticIncidentesDto(String consecutivo, Long idTipoIncidente, Long idEmpresaPersona, Long idIncidente, LocalDate inicio, Long idUsuario, Long idIncidenteEstatus, String observaciones, Long orden, LocalDate termino, Long ejercicio, Long idDesarrollo, Double costo, Long idNomina, Long idPrestamo) {
    setConsecutivo(consecutivo);
    setIdTipoIncidente(idTipoIncidente);
    setIdEmpresaPersona(idEmpresaPersona);
    setIdIncidente(idIncidente);
    setInicio(inicio);
    setIdUsuario(idUsuario);
    setIdIncidenteEstatus(idIncidenteEstatus);
    setObservaciones(observaciones);
    setOrden(orden);
    setTermino(termino);
    setEjercicio(ejercicio);
    setRegistro(LocalDateTime.now());
		setIdDesarrollo(idDesarrollo);
		setCosto(costo);
		this.idNomina= idNomina;
		this.idPrestamo= idPrestamo;
  }
	
  public void setConsecutivo(String consecutivo) {
    this.consecutivo = consecutivo;
  }

  public String getConsecutivo() {
    return consecutivo;
  }

  public void setIdTipoIncidente(Long idTipoIncidente) {
    this.idTipoIncidente = idTipoIncidente;
  }

  public Long getIdTipoIncidente() {
    return idTipoIncidente;
  }

  public void setIdEmpresaPersona(Long idEmpresaPersona) {
    this.idEmpresaPersona = idEmpresaPersona;
  }

  public Long getIdEmpresaPersona() {
    return idEmpresaPersona;
  }

  public void setIdIncidente(Long idIncidente) {
    this.idIncidente = idIncidente;
  }

  public Long getIdIncidente() {
    return idIncidente;
  }

  public void setInicio(LocalDate inicio) {
    this.inicio = inicio;
  }

  public LocalDate getInicio() {
    return inicio;
  }

  public void setIdUsuario(Long idUsuario) {
    this.idUsuario = idUsuario;
  }

  public Long getIdUsuario() {
    return idUsuario;
  }

  public void setIdIncidenteEstatus(Long idIncidenteEstatus) {
    this.idIncidenteEstatus = idIncidenteEstatus;
  }

  public Long getIdIncidenteEstatus() {
    return idIncidenteEstatus;
  }

  public void setObservaciones(String observaciones) {
    this.observaciones = observaciones;
  }

  public String getObservaciones() {
    return observaciones;
  }

  public void setOrden(Long orden) {
    this.orden = orden;
  }

  public Long getOrden() {
    return orden;
  }

  public void setTermino(LocalDate termino) {
    this.termino = termino;
  }

  public LocalDate getTermino() {
    return termino;
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

	public Long getIdDesarrollo() {
		return idDesarrollo;
	}

	public void setIdDesarrollo(Long idDesarrollo) {
		this.idDesarrollo = idDesarrollo;
	}	

	public Double getCosto() {
		return costo;
	}

	public void setCosto(Double costo) {
		this.costo = costo;
	}

	public Long getIdNomina() {
		return idNomina;
	}

	public void setIdNomina(Long idNomina) {
		this.idNomina=idNomina;
	}

  public Long getIdPrestamo() {
    return idPrestamo;
  }

  public void setIdPrestamo(Long idPrestamo) {
    this.idPrestamo = idPrestamo;
  }
	
  @Transient
  @Override
  public Long getKey() {
  	return getIdIncidente();
  }

  @Override
  public void setKey(Long key) {
  	this.idIncidente = key;
  }

  @Override
  public String toString() {
    StringBuilder regresar= new StringBuilder();
    regresar.append("[");
		regresar.append(getConsecutivo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdTipoIncidente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdEmpresaPersona());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdDesarrollo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdIncidente());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getInicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdUsuario());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdIncidenteEstatus());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getObservaciones());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getOrden());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getTermino());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getEjercicio());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getCosto());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdNomina());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getIdPrestamo());
		regresar.append(Constantes.SEPARADOR);
		regresar.append(getRegistro());
    regresar.append("]");
  	return regresar.toString();
  }

  @Override
  public Map toMap() {
    Map regresar = new HashMap();
		regresar.put("consecutivo", getConsecutivo());
		regresar.put("idTipoIncidente", getIdTipoIncidente());
		regresar.put("idEmpresaPersona", getIdEmpresaPersona());
		regresar.put("idDesarrollo", getIdDesarrollo());
		regresar.put("idIncidente", getIdIncidente());
		regresar.put("vigenciaInicio", getInicio());
		regresar.put("idUsuario", getIdUsuario());
		regresar.put("idIncidenteEstatus", getIdIncidenteEstatus());
		regresar.put("observaciones", getObservaciones());
		regresar.put("orden", getOrden());
		regresar.put("vigenciaFin", getTermino());
		regresar.put("ejercicio", getEjercicio());
		regresar.put("costo", getCosto());		
		regresar.put("idNomina", getIdNomina());		
		regresar.put("idPrestamo", getIdPrestamo());		
		regresar.put("registro", getRegistro());		
  	return regresar;
  }

  @Override
  public Object[] toArray() {
    Object[] regresar = new Object[] {
			getConsecutivo(), getIdTipoIncidente(), getIdEmpresaPersona(), getIdIncidente(), getInicio(), getIdUsuario(), getIdIncidenteEstatus(), getObservaciones(), getOrden(), getTermino(), getEjercicio(), getIdDesarrollo(), getCosto(), getIdNomina(), getRegistro(), getIdPrestamo()
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
    regresar.append("idIncidente~");
    regresar.append(getIdIncidente());
    regresar.append("|");
    return regresar.toString();
  }

  @Override
  public String toKeys() {
    StringBuilder regresar= new StringBuilder();
    regresar.append(getIdIncidente());
    return regresar.toString();
  }

  @Override
  public Class toHbmClass() {
    return TcManticIncidentesDto.class;
  }

  @Override
  public boolean isValid() {
  	return getIdIncidente()!= null && getIdIncidente()!=-1L;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final TcManticIncidentesDto other = (TcManticIncidentesDto) obj;
    if (getIdIncidente() != other.idIncidente && (getIdIncidente() == null || !getIdIncidente().equals(other.idIncidente))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdIncidente() != null ? getIdIncidente().hashCode() : 0);
    return hash;
  }

  public TcManticIncidentesDto toClon() {
    TcManticIncidentesDto regresar= new TcManticIncidentesDto(
      this.consecutivo, // String consecutivo, 
      this.idTipoIncidente, // Long idTipoIncidente, 
      this.idEmpresaPersona, // Long idEmpresaPersona, 
      -1L, // Long idIncidente, 
      this.inicio, // LocalDate inicio, 
      this.idUsuario, // Long idUsuario, 
      this.idIncidenteEstatus, // Long idIncidenteEstatus, 
      this.observaciones, // String observaciones, 
      this.orden, // Long orden, 
      this.termino, // LocalDate termino, 
      this.ejercicio, // Long ejercicio, 
      this.idDesarrollo, // Long idDesarrollo, 
      this.costo, // Double costo, 
      this.idNomina, // Long idNomina, 
      this.idPrestamo // Long idPrestamo            
    );
    return regresar;
  }
  
}