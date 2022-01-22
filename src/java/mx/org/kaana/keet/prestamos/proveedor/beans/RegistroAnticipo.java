package mx.org.kaana.keet.prestamos.proveedor.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.prestamos.reglas.MotorBusqueda;

public class RegistroAnticipo implements Serializable {

	private static final long serialVersionUID = -4494045939892074909L;
	private Anticipo prestamo;
	private Long idAnticipo;
	private List<Documento> documentos;
  private List<Double> pagos;

	public RegistroAnticipo() {
		this(-1L);
	}

  public List<Double> getPagos() {
    return pagos;
  }

  public void setPagos(List<Double> pagos) {
    this.pagos = pagos;
  }

	public RegistroAnticipo(Long idAnticipo) {	
		this.idAnticipo= idAnticipo;
		this.documentos= new ArrayList<>();
		this.pagos     = new ArrayList<>();
		this.initCollections(idAnticipo);
	} // RegistroPrototipo
	
	private void initCollections(Long idAnticipo) {
		MotorBusqueda motor= null;
		try {
			if(idAnticipo> 0L) {
			  motor= new MotorBusqueda(idAnticipo);
				this.prestamo= motor.toAnticipo();
				this.pagos   = motor.toPagos();
			} // if
      else {
				this.prestamo= new Anticipo();
        for (int x= 0; x< this.prestamo.getSemanas().intValue(); x++) {
          this.pagos.add(0D);
        } // for
      } // else  
		} // try
		catch (Exception e) {			
			Error.mensaje(e);			
		} // catch		
	} // initCollections

	public Long getIdAnticipo() {
		return idAnticipo;
	}

	public void setIdAnticipo(Long idAnticipo) {
		this.idAnticipo = idAnticipo;
	}

	public Anticipo getPrestamo() {
		return prestamo;
	}

	public void setPrestamo(Anticipo prestamo) {
		this.prestamo = prestamo;
	}

	public List<Documento> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<Documento> documentos) {
		this.documentos = documentos;
	}

}
