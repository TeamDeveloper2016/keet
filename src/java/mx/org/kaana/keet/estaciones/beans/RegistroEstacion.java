package mx.org.kaana.keet.estaciones.beans;

import java.io.Serializable;
import mx.org.kaana.keet.estaciones.reglas.MotorBusqueda;

public class RegistroEstacion implements Serializable {
	
	private Estacion estacion;

	public RegistroEstacion() {
		this(-1L);
	}

	public RegistroEstacion(Long idEstacion) {
		this.estacion = init(idEstacion);
	}

	public Estacion getEstacion() {
		return estacion;
	}

	public void setEstacion(Estacion estacion) {
		this.estacion = estacion;
	}
	
	private Estacion init(Long idEstacion) {
    Estacion regresar= null;
		MotorBusqueda motor= null;
		try {
			if(idEstacion> 0L) {
				motor= new MotorBusqueda(idEstacion);
				regresar= motor.toEstacion();
			} // if
			else{				
				regresar= new Estacion();
			} // else
		} // try
		catch (Exception e) {			
			mx.org.kaana.libs.formato.Error.mensaje(e);			
		} // catch		
		return regresar;
	} // init
	
}
