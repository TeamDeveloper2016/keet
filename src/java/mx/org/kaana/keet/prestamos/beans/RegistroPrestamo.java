package mx.org.kaana.keet.prestamos.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.prestamos.reglas.MotorBusqueda;

public class RegistroPrestamo implements Serializable {

	private static final long serialVersionUID = -4494045939892074909L;
	private Prestamo prestamo;
	private Long idPrestamo;
	private List<Documento> documentos;

	public RegistroPrestamo() {
		this(-1L);
	}


	public RegistroPrestamo(Long idPrestamo){	
		this.idPrestamo= idPrestamo;
		this.documentos = new ArrayList<>();
		initCollections(idPrestamo);
	} // RegistroPrototipo
	
	private void initCollections(Long idPrestamo){
		MotorBusqueda motor= null;
		try {
			if(idPrestamo> 0L){
			  motor= new MotorBusqueda(idPrestamo);
				this.prestamo= motor.toPrestamo();
			} // if
			else{
				this.prestamo= new Prestamo();
			} // else
		} // try
		catch (Exception e) {			
			Error.mensaje(e);			
		} // catch		
	} // initCollections

	public Long getIdPrototipo() {
		return idPrestamo;
	}

	public void setIdPrototipo(Long idPrestamo) {
		this.idPrestamo = idPrestamo;
	}

	public Prestamo getPrestamo() {
		return prestamo;
	}

	public void setPrestamo(Prestamo prestamo) {
		this.prestamo = prestamo;
	}

	public List<Documento> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<Documento> documentos) {
		this.documentos = documentos;
	}


}
