package mx.org.kaana.keet.prestamos.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosLotesDto;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.prestamos.reglas.MotorBusqueda;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class RegistroPrestamo implements Serializable {

	private static final long serialVersionUID = -4494045939892074909L;
	private Prestamo prestamo;
	private Long idPrestamo;
	private List<Documento> documentos;
  private List<Double> pagos;
  private UISelectEntity ikEmpresa;
	private UISelectEntity ikDesarrollo;
	private UISelectEntity ikContrato;
	private List<TcKeetPrestamosLotesDto> lotes;
  private Double importe;
  private Long idDeudor;
  private List<Long> registros;

	public RegistroPrestamo() {
		this(-1L);
	}

	public RegistroPrestamo(Long idPrestamo){	
		this.idPrestamo= idPrestamo;
		this.documentos= new ArrayList<>();
    this.pagos     = new ArrayList<>();
		this.lotes     = new ArrayList<>();
    this.importe   = 0D;
    this.idDeudor  = -1L;
		this.registros = new ArrayList<>();
		this.initCollections(idPrestamo);
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

  public List<TcKeetPrestamosLotesDto> getLotes() {
    return lotes;
  }

  public Double getImporte() {
    return importe;
  }

  public Long getIdDeudor() {
    return idDeudor;
  }

  public List<Long> getRegistros() {
    return registros;
  }
  
	private void initCollections(Long idPrestamo) {
		MotorBusqueda motor= null;
		try {
			if(idPrestamo> 0L) {
			  motor= new MotorBusqueda(idPrestamo);
				this.prestamo= motor.toPrestamo();
				this.pagos   = motor.toPagosPrestamos();
        this.setIkEmpresa(new UISelectEntity(this.prestamo.getIdEmpresa()));
        this.setIkDesarrollo(new UISelectEntity(this.prestamo.getIdDesarrollo()));
        this.setIkContrato(new UISelectEntity(this.prestamo.getIdContrato()));
        this.importe = this.getPrestamo().getImporte();
        this.idDeudor= this.getPrestamo().getIdDeudor();
			} // if
      else {
				this.prestamo= new Prestamo();
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

	public Long getIdPrestamo() {
		return idPrestamo;
	}

	public void setIdPrestamo(Long idPrestamo) {
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

  public List<Double> getPagos() {
    return pagos;
  }

  public void setPagos(List<Double> pagos) {
    this.pagos = pagos;
  }
  
}
