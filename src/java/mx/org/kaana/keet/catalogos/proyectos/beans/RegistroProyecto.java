package mx.org.kaana.keet.catalogos.proyectos.beans;

import java.io.Serializable;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.catalogos.proyectos.reglas.MotorBusqueda;

public class RegistroProyecto implements Serializable {
	
	private static final long serialVersionUID = 6253504536549861564L;
	private Long idProyecto;
	private Proyecto proyecto;

	public RegistroProyecto() {
		this(new Proyecto());
	}	
	
	public RegistroProyecto(Proyecto proyecto) {
		this.proyecto = proyecto;
	}

	public RegistroProyecto(Long idProyecto) {
		this.idProyecto = idProyecto;
		initCollections(idProyecto);
	}

	public Long getIdProyecto() {
		return idProyecto;
	}

	public void setIdProyecto(Long idProyecto) {
		this.idProyecto = idProyecto;
	}

	public Proyecto getProyecto() {
		return proyecto;
	}

	public void setProyecto(Proyecto proyecto) {
		this.proyecto = proyecto;
	}	
	
	private void initCollections(Long idProyecto){
		MotorBusqueda motor= null;
		try {
			motor= new MotorBusqueda(idProyecto);
			this.proyecto= motor.toProyecto();
			this.proyecto.setLotes(motor.toLotes());
		} // try
		catch (Exception e) {			
			Error.mensaje(e);
		} // catch		
	} // initCollections
}
