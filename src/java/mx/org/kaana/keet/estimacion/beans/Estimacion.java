package mx.org.kaana.keet.estimacion.beans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import mx.org.kaana.keet.db.dto.TcKeetEstimacionesDto;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.formato.Fecha;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 13/03/2022
 *@time 01:28:25 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Estimacion extends TcKeetEstimacionesDto implements Serializable {

  private static final long serialVersionUID = 8162532023571094891L;

	private Long idDesarrollo;
	private Long idCliente;
	private UISelectEntity ikEmpresa;
	private UISelectEntity ikDesarrollo;
	private UISelectEntity ikCliente;
	private UISelectEntity ikContrato;
	private UISelectEntity ikNomina;
  private List<Retencion> retenciones;

  public Estimacion() {
    super(null, 0D, 0D, 0D, new Long(Fecha.getAnioActual()), "", 1L, -1L, -1L, "", -1L, 1L, -1L, -1L, LocalDate.now(), LocalDate.now().plusDays(7), "", 2L);
  }
  
	public UISelectEntity getIkEmpresa() {
		return ikEmpresa;
	}

	public void setIkEmpresa(UISelectEntity ikEmpresa) {
		this.ikEmpresa=ikEmpresa;
		if(this.ikEmpresa!= null)
		  this.setIdEmpresa(this.ikEmpresa.getKey());
	}

  public UISelectEntity getIkDesarrollo() {
    return ikDesarrollo;
  }

  public void setIkDesarrollo(UISelectEntity ikDesarrollo) {
    this.ikDesarrollo = ikDesarrollo;
  }

	public UISelectEntity getIkCliente() {
		return ikCliente;
	}

	public void setIkCliente(UISelectEntity ikCliente) {
		this.ikCliente=ikCliente;
	}

  public UISelectEntity getIkContrato() {
    return ikContrato;
  }

  public void setIkContrato(UISelectEntity ikContrato) {
    this.ikContrato = ikContrato;
		if(this.ikContrato!= null)
		  this.setIdContrato(this.ikContrato.getKey());
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }

  public Long getIdCliente() {
    return idCliente;
  }

  public void setIdCliente(Long idCliente) {
    this.idCliente = idCliente;
  }

  public UISelectEntity getIkNomina() {
    return ikNomina;
  }

  public void setIkNomina(UISelectEntity ikNomina) {
    this.ikNomina = ikNomina;
		if(this.ikNomina!= null)
		  this.setIdNomina(this.ikNomina.getKey());
  }

  public List<Retencion> getRetenciones() {
    return retenciones;
  }

  public void setRetenciones(List<Retencion> rentenciones) {
    this.retenciones = rentenciones;
  }

}
