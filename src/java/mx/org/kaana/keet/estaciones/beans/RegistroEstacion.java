package mx.org.kaana.keet.estaciones.beans;

import java.io.Serializable;
import mx.org.kaana.keet.estaciones.reglas.MotorBusqueda;
import mx.org.kaana.libs.formato.Error;

public class RegistroEstacion implements Serializable {

  private static final long serialVersionUID = 1758595942879188961L;
	
	private Estacion estacion;

	public RegistroEstacion() {
		this(-1L);
	}

	public RegistroEstacion(Long idEstacion) {
		this.estacion = this.init(idEstacion);
	}

	public Estacion getEstacion() {
		return estacion;
	}

	public void setEstacion(Estacion estacion) {
		this.estacion = estacion;
	}
	
	private Estacion init(Long idEstacion) {
    Estacion regresar  = null;
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
			Error.mensaje(e);			
		} // catch		
		return regresar;
	} // init

  public Estacion toLoad(Long idEstacion) {
    this.estacion= this.init(idEstacion);
    return this.estacion;
  } 
  
}
