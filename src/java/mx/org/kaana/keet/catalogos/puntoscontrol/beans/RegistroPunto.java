package mx.org.kaana.keet.catalogos.puntoscontrol.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.catalogos.puntoscontrol.reglas.MotorBusqueda;
import mx.org.kaana.libs.pagina.JsfBase;

public class RegistroPunto implements Serializable {
	private List<PuntoControl> puntosControles;
	private PuntoGrupo puntoGrupo;

	public RegistroPunto() {
		this(-1L);
	}
	
  public RegistroPunto(Long idPuntoGrupo) {
		init(idPuntoGrupo);
	}

	public List<PuntoControl> getPuntosControles() {
		return puntosControles;
	}

	public void setPuntosControles(List<PuntoControl> puntosControles) {
		this.puntosControles = puntosControles;
	}

	public PuntoGrupo getPuntoGrupo() {
		return puntoGrupo;
	}

	public void setPuntoGrupo(PuntoGrupo puntoGrupo) {
		this.puntoGrupo = puntoGrupo;
	}
	
	private void init(Long idPuntoGrupo) {
		MotorBusqueda motor= null;
		try {
			if(idPuntoGrupo> 0L) {
				motor= new MotorBusqueda(idPuntoGrupo);
				this.puntoGrupo= motor.toPuntoGrupo();
				this.puntosControles= motor.toPuntosControles();
			} // if
			else{				
				this.puntoGrupo= new PuntoGrupo();
				this.puntosControles= new ArrayList<>();
			} // else
		} // try
		catch (Exception e) {			
			mx.org.kaana.libs.formato.Error.mensaje(e);				
		} // catch		
	} // init

	
	public void doAgregarPuntoControl(){
		PuntoControl puntoControl= null;
		try {					
			puntoControl= new PuntoControl(ESql.INSERT, this.puntosControles.size()*-1L);							
			this.puntosControles.add(puntoControl);			
		} // try
		catch (Exception e) {
			mx.org.kaana.libs.formato.Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} // doAgregarPuntoControl
	
	public void doEliminarPuntoControl(PuntoControl selecion){
		try {			
			if(selecion.getKey()>0L)
				this.puntosControles.get(this.puntosControles.indexOf(selecion)).setAccion(ESql.DELETE);
			else
				this.puntosControles.remove(selecion);
		} // try
		catch (Exception e) {
			mx.org.kaana.libs.formato.Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarPuntoControl

}