package mx.org.kaana.keet.estmaciones.beans;

import java.io.Serializable;
import java.util.List;
import mx.org.kaana.keet.db.dto.TcKeetEstimacionesDto;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.mantic.db.dto.TcManticClientesDto;

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
  private TcManticClientesDto cliente;
  private List<Retencion> retenciones;

  public Estimacion() {
    super(-1L, 0D, 0D, 0D, new Long(Fecha.getAnioActual()), "", 1L, -1L, -1L, "", -1L, 1L, -1L);
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

  public TcManticClientesDto getCliente() {
    return cliente;
  }

  public void setCliente(TcManticClientesDto cliente) {
    this.cliente = cliente;
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

  public List<Retencion> getRetenciones() {
    return retenciones;
  }

  public void setRetenciones(List<Retencion> rentenciones) {
    this.retenciones = rentenciones;
  }

}
