package mx.org.kaana.keet.prestamos.proveedor.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.keet.db.dto.TcKeetAnticiposLotesDto;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.prestamos.reglas.MotorBusqueda;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class RegistroAnticipo implements Serializable {

	private static final long serialVersionUID = -4494045939892074909L;
	private Anticipo prestamo;
	private Long idAnticipo;
	private List<Documento> documentos;
  private List<Double> pagos;
  private UISelectEntity ikEmpresa;
	private UISelectEntity ikDesarrollo;
	private UISelectEntity ikContrato;
	private List<TcKeetAnticiposLotesDto> lotes;

	public RegistroAnticipo() {
		this(-1L);
	}

	public RegistroAnticipo(Long idAnticipo) {	
		this.idAnticipo= idAnticipo;
		this.documentos= new ArrayList<>();
		this.pagos     = new ArrayList<>();
		this.lotes     = new ArrayList<>();
		this.initCollections(idAnticipo);
	} // RegistroPrototipo
	
	public UISelectEntity getIkEmpresa() {
		return ikEmpresa;
	}

	public void setIkEmpresa(UISelectEntity ikEmpresa) {
		this.ikEmpresa=ikEmpresa;
		if(this.ikEmpresa!= null)
			this.prestamo.setIdEmpresa(this.ikEmpresa.getKey());
	}	
  
  public UISelectEntity getIkDesarrollo() {
		return ikDesarrollo;
	} 

	public void setIkDesarrollo(UISelectEntity ikDesarrollo) {
		this.ikDesarrollo = ikDesarrollo;
		if(this.ikDesarrollo!= null)
			this.prestamo.setIdDesarrollo(this.ikDesarrollo.getKey());
	} 
  
	public UISelectEntity getIkContrato() {
		return ikContrato;
	} 

	public void setIkContrato(UISelectEntity ikContrato) {
		this.ikContrato = ikContrato;
		if(this.ikContrato!= null)
			this.prestamo.setIdContrato(this.ikContrato.getKey());
	} 

  public List<TcKeetAnticiposLotesDto> getLotes() {
    return lotes;
  }
  
	private void initCollections(Long idAnticipo) {
		MotorBusqueda motor= null;
		try {
			if(idAnticipo> 0L) {
			  motor= new MotorBusqueda(idAnticipo);
				this.prestamo= motor.toAnticipo();
				this.pagos   = motor.toPagosAnticipos();
        this.setIkEmpresa(new UISelectEntity(this.prestamo.getIdEmpresa()));
        this.setIkDesarrollo(new UISelectEntity(this.prestamo.getIdDesarrollo()));
        this.setIkContrato(new UISelectEntity(this.prestamo.getIdContrato()));
			} // if
      else {
				this.prestamo= new Anticipo();
        if(JsfBase.getAutentifica().getEmpresa().isMatriz())
          this.setIkEmpresa(new UISelectEntity(JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende()));
        else
          this.setIkEmpresa(new UISelectEntity(JsfBase.getAutentifica().getEmpresa().getIdEmpresa()));
        this.setIkDesarrollo(new UISelectEntity(-1L));
        this.setIkContrato(new UISelectEntity(-1L));
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

  public List<Double> getPagos() {
    return pagos;
  }

  public void setPagos(List<Double> pagos) {
    this.pagos = pagos;
  }

}
