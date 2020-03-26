package mx.org.kaana.keet.catalogos.desarrollos.beans;

import java.io.Serializable;
import mx.org.kaana.keet.catalogos.desarrollos.reglas.MotorBusqueda;

public class RegistroDesarrollo implements Serializable {
	
	private Domicilio domicilio;
	private Desarrollo desarrollo;

	public RegistroDesarrollo() {
		this(-1L);
	}
	
	public RegistroDesarrollo(Long idDesarrollo) {
		this.desarrollo = init(idDesarrollo);
	}

	public Desarrollo getDesarrollo() {
		return desarrollo;
	}

	public void setDesarrollo(Desarrollo desarrollo) {
		this.desarrollo = desarrollo;
	}

	public Domicilio getDomicilio() {
		return domicilio;
	}

	public void setDomicilio(Domicilio domicilio) {
		this.domicilio = domicilio;
	}
	
	private Desarrollo init(Long idDesarrollo) {
    Desarrollo regresar= null;
		MotorBusqueda motor= null;
		try {
			if(idDesarrollo> 0L) {
				motor= new MotorBusqueda(idDesarrollo);
				regresar= motor.toDesarrollo();
				this.domicilio= motor.toDomicilio(regresar);
			} // if
			else{				
				regresar= new Desarrollo();
				this.domicilio= new Domicilio();
			} // else
		} // try
		catch (Exception e) {			
			mx.org.kaana.libs.formato.Error.mensaje(e);			
		} // catch		
		return regresar;
	} // Desarrollo
	
}
