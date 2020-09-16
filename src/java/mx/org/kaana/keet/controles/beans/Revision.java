package mx.org.kaana.keet.controles.beans;

import java.io.Serializable;
import mx.org.kaana.kajool.db.comun.sql.Entity;

public class Revision implements Serializable{
	
	private static final long serialVersionUID = -6825011863521725695L;
	private Long idFigura;
	private Long tipo;
	private Long idControl;
	private Entity[] puntosRevision;
	private String latitud;
	private String longitud;
	private Double metros;
	private Long idContratoLote;
	private String observaciones;
	private String clave;
	private Long idDepartamento;

	public Revision() {
		this(null, null, null, null, null, null, null, null, null, null);
	}

	public Revision(Long idFigura, Long tipo, Long idControl, Entity[] puntosRevision, String latitud, String longitud, Double metros, Long idContratoLote, String clave, Long idDepartamento) {
		this(idFigura, tipo, idControl, puntosRevision, latitud, longitud, metros, idContratoLote, "", clave, idDepartamento);
	}
	
	public Revision(Long idFigura, Long tipo, Long idControl, Entity[] puntosRevision, String latitud, String longitud, Double metros, Long idContratoLote, String observaciones, String clave, Long idDepartamento) {
		this.idFigura      = idFigura;
		this.tipo          = tipo;
		this.idControl     = idControl;
		this.puntosRevision= puntosRevision;
		this.latitud       = latitud;
		this.longitud      = longitud;
		this.metros        = metros;
		this.idContratoLote= idContratoLote;
		this.observaciones = observaciones;
		this.clave         = clave;
		this.idDepartamento= idDepartamento;
	}

	public Long getIdFigura() {
		return idFigura;
	}

	public void setIdFigura(Long idFigura) {
		this.idFigura = idFigura;
	}

	public Long getTipo() {
		return tipo;
	}

	public void setTipo(Long tipo) {
		this.tipo = tipo;
	}

	public Long getIdControl() {
		return idControl;
	}

	public void setIdControl(Long idControl) {
		this.idControl = idControl;
	}

	public Entity[] getPuntosRevision() {
		return puntosRevision;
	}

	public void setPuntosRevision(Entity[] puntosRevision) {
		this.puntosRevision = puntosRevision;
	}

	public String getLatitud() {
		return latitud;
	}

	public void setLatitud(String latitud) {
		this.latitud = latitud;
	}

	public String getLongitud() {
		return longitud;
	}

	public void setLongitud(String longitud) {
		this.longitud = longitud;
	}

	public Double getMetros() {
		return metros;
	}

	public void setMetros(Double metros) {
		this.metros = metros;
	}

	public Long getIdContratoLote() {
		return idContratoLote;
	}

	public void setIdContratoLote(Long idContratoLote) {
		this.idContratoLote = idContratoLote;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public Long getIdDepartamento() {
		return idDepartamento;
	}

	public void setIdDepartamento(Long idDepartamento) {
		this.idDepartamento = idDepartamento;
	}	
  
}