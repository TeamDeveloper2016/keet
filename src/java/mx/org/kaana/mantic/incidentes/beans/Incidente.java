package mx.org.kaana.mantic.incidentes.beans;

import java.io.Serializable;
import java.time.LocalDate;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesDto;

public class Incidente implements Serializable{

	private static final long serialVersionUID= 5491973442869961621L;	
	private Long idIncidente;
	private Long idEmpresaPersona;
	private Long idTipoIncidente;
	private Long idIncidenteEstatus;
	private LocalDate vigenciaInicio;
	private LocalDate vigenciaFin;
	private String observaciones;
	private Long idDesarrollo;
	private String nombre;
	private String tipoIncidente;
	private String nombreUsuario;
	private String estatus;
	private String puesto;
	private ESql accion;
	private String estatusAsociados;
	private Double costo;

	public Incidente() {
		this(-1L, -1L, -1L, -1L, LocalDate.now(), LocalDate.now(), "", -1L, null, null, null, null, null, ESql.UPDATE, null, null);
	}

	public Incidente(TcManticIncidentesDto dto){
		this(dto.getIdIncidente(), dto.getIdEmpresaPersona(), dto.getIdTipoIncidente(), dto.getIdIncidenteEstatus(), dto.getVigenciaInicio(), dto.getVigenciaFin(), dto.getObservaciones(), dto.getIdDesarrollo(), null, null, null, null, null, ESql.UPDATE, null, dto.getCosto());
	}
	
	public Incidente(Long idIncidente, Long idEmpresaPersona, Long idTipoIncidente, Long idIncidenteEstatus, LocalDate vigenciaInicio, LocalDate vigenciaFin, String observaciones, Long idDesarrollo, String nombre, String tipoIncidente, String nombreUsuario, String estatus, String puesto, ESql accion, String estatusAsociados, Double costo) {
		this.idIncidente       = idIncidente;
		this.idEmpresaPersona  = idEmpresaPersona;
		this.idTipoIncidente   = idTipoIncidente;
		this.idIncidenteEstatus= idIncidenteEstatus;
		this.vigenciaInicio    = vigenciaInicio;
		this.vigenciaFin       = vigenciaFin;
		this.observaciones     = observaciones;
		this.idDesarrollo      = idDesarrollo;				
		this.estatusAsociados	 = estatusAsociados;	
	}	

	public Long getIdIncidente() {
		return idIncidente;
	}

	public void setIdIncidente(Long idIncidente) {
		this.idIncidente = idIncidente;
	}
	
	public Long getIdEmpresaPersona() {
		return idEmpresaPersona;
	}

	public void setIdEmpresaPersona(Long idEmpresaPersona) {
		this.idEmpresaPersona = idEmpresaPersona;
	}

	public Long getIdTipoIncidente() {
		return idTipoIncidente;
	}

	public void setIdTipoIncidente(Long idTipoIncidente) {
		this.idTipoIncidente = idTipoIncidente;
	}

	public Long getIdIncidenteEstatus() {
		return idIncidenteEstatus;
	}

	public void setIdIncidenteEstatus(Long idIncidenteEstatus) {
		this.idIncidenteEstatus = idIncidenteEstatus;
	}

	public LocalDate getVigenciaInicio() {
		return vigenciaInicio;
	}

	public void setVigenciaInicio(LocalDate vigenciaInicio) {
		this.vigenciaInicio = vigenciaInicio;
	}

	public LocalDate getVigenciaFin() {
		return vigenciaFin;
	}

	public void setVigenciaFin(LocalDate vigenciaFin) {
		this.vigenciaFin = vigenciaFin;
	}
	
	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}	

	public Long getIdDesarrollo() {
		return idDesarrollo;
	}

	public void setIdDesarrollo(Long idDesarrollo) {
		this.idDesarrollo = idDesarrollo;
	}	

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getTipoIncidente() {
		return tipoIncidente;
	}

	public void setTipoIncidente(String tipoIncidente) {
		this.tipoIncidente = tipoIncidente;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getEstatus() {
		return estatus;
	}

	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}

	public String getPuesto() {
		return puesto;
	}

	public void setPuesto(String puesto) {
		this.puesto = puesto;
	}	

	public ESql getAccion() {
		return accion;
	}

	public void setAccion(ESql accion) {
		this.accion = accion;
	}	

	public String getEstatusAsociados() {
		return estatusAsociados;
	}

	public void setEstatusAsociados(String estatusAsociados) {
		this.estatusAsociados = estatusAsociados;
	}	

	public Double getCosto() {
		return costo;
	}

	public void setCosto(Double costo) {
		this.costo = costo;
	}

	@Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Incidente other = (Incidente) obj;
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
}