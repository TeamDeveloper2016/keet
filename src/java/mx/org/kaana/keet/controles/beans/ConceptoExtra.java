package mx.org.kaana.keet.controles.beans;

import java.io.Serializable;

public class ConceptoExtra extends Revision implements Serializable{

	private static final long serialVersionUID= 6911382265102010131L;	
	private Long idControl;
	private Long idPuntoGrupo;
	private Long idRubro;
	private String descripcion;
	private Double importe;

	public ConceptoExtra() {
		this(-1L, -1L, -1L, "", 0D);
	}

	public ConceptoExtra(Long idControl, Long idPuntoGrupo, Long idRubro, String descripcion, Double importe) {
		super();
		this.idControl   = idControl;
		this.idPuntoGrupo= idPuntoGrupo;
		this.idRubro     = idRubro;
		this.descripcion = descripcion;
		this.importe     = importe;
	}

  @Override
	public Long getIdControl() {
		return idControl;
	}

  @Override
	public void setIdControl(Long idControl) {
		this.idControl = idControl;
	}

	public Long getIdPuntoGrupo() {
		return idPuntoGrupo;
	}

	public void setIdPuntoGrupo(Long idPuntoGrupo) {
		this.idPuntoGrupo = idPuntoGrupo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Double getImporte() {
		return importe;
	}

	public void setImporte(Double importe) {
		this.importe = importe;
	}

	public Long getIdRubro() {
		return idRubro;
	}

	public void setIdRubro(Long idRubro) {
		this.idRubro = idRubro;
	}	
  
}