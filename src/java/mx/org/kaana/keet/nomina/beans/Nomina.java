package mx.org.kaana.keet.nomina.beans;

import mx.org.kaana.keet.db.dto.TcKeetNominasPeriodosDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 27/04/2020
 *@time 07:38:52 PM 
 *author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Nomina extends TcKeetNominasPeriodosDto {

	private Long idNominaEstatus;
	private String estatus;
	private String observaciones;

	public Long getIdNominaEstatus() {
		return idNominaEstatus;
	}

	public void setIdNominaEstatus(Long idNominaEstatus) {
		this.idNominaEstatus=idNominaEstatus;
	}

	public String getEstatus() {
		return estatus;
	}

	public void setEstatus(String estatus) {
		this.estatus=estatus;
	}

	public String getObservaciones() {
		return observaciones;
	}

	public void setObservaciones(String observaciones) {
		this.observaciones=observaciones;
	}
	
	@Override
	public Class toHbmClass() {
		return TcKeetNominasPeriodosDto.class;
	}
	
}
