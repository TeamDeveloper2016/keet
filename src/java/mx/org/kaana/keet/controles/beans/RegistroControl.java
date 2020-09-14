package mx.org.kaana.keet.controles.beans;

import java.io.Serializable;
import mx.org.kaana.keet.controles.reglas.MotorBusqueda;

public class RegistroControl implements Serializable {

  private static final long serialVersionUID = 3559918887268879155L;
	
	private Control control;

	public RegistroControl() {
		this(-1L);
	}

	public RegistroControl(Long idControl) {
		this.control = init(idControl);
	}

	public Control getControl() {
		return control;
	}

	public void setEstacion(Control control) {
		this.control = control;
	}
	
	private Control init(Long idControl) {
    Control regresar= null;
		MotorBusqueda motor= null;
		try {
			if(idControl> 0L) {
				motor= new MotorBusqueda(idControl);
				regresar= motor.toControl();
			} // if
			else{				
				regresar= new Control();
			} // else
		} // try
		catch (Exception e) {			
			mx.org.kaana.libs.formato.Error.mensaje(e);			
		} // catch		
		return regresar;
	} // init
	
}
