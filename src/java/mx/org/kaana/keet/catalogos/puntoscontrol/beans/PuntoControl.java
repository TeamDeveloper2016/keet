package mx.org.kaana.keet.catalogos.puntoscontrol.beans;

import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetPuntosControlesDto;


public class PuntoControl extends TcKeetPuntosControlesDto{
	
	private ESql accion;


	public PuntoControl() {
		this(ESql.UPDATE, -1L);
	}
	
	public PuntoControl(ESql accion, Long key) {
		super(key);
		this.accion = accion;
	}

	public PuntoControl(ESql accion, String descripcion, Long idUsuario, Long idPuntoControl, Long orden, Double factor, String nombre) {
		super(descripcion, idUsuario, idPuntoControl, orden, factor, nombre);
		this.accion = accion;
	}

	public ESql getAccion() {
		return accion;
	}

	public void setAccion(ESql accion) {
		this.accion = accion;
	}
	
	
	public boolean isVisible(){
		return !this.accion.equals(ESql.DELETE);
	}
	
	
	
}
