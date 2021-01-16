package mx.org.kaana.mantic.inventarios.entradas.beans;

import java.io.Serializable;
import mx.org.kaana.keet.db.dto.TcKeetNotasDirectosDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

/**
 *@company KAJOOL
 *@project KAJOOL (Control system polls)
 *@date 15/01/2021
 *@time 06:19:06 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class NotaProyecto extends TcKeetNotasDirectosDto implements Serializable {

  private static final long serialVersionUID = -4126251724619982066L;

	private UISelectEntity ikDesarrollo;
	private UISelectEntity ikCliente;
	private UISelectEntity ikContrato;
  private String desarrollo;
  private String cliente;
  private String contrato;

  public NotaProyecto() {
    this(-1L);
  }

  public NotaProyecto(Long key) {
    super(key);
  }
  
  public UISelectEntity getIkDesarrollo() {
    return ikDesarrollo;
  }

  public void setIkDesarrollo(UISelectEntity ikDesarrollo) {
    this.ikDesarrollo = ikDesarrollo;
		if(this.ikDesarrollo!= null)
		  this.setIdDesarrollo(this.ikDesarrollo.getKey());
  }

	public UISelectEntity getIkCliente() {
		return ikCliente;
	}

	public void setIkCliente(UISelectEntity ikCliente) {
		this.ikCliente=ikCliente;
		if(this.ikCliente!= null)
		  this.setIdCliente(this.ikCliente.getKey());
	}

  public UISelectEntity getIkContrato() {
    return ikContrato;
  }

  public void setIkContrato(UISelectEntity ikContrato) {
    this.ikContrato = ikContrato;
		if(this.ikContrato!= null)
		  this.setIdContrato(this.ikContrato.getKey());
  }
  
  public String getDesarrollo() {
    return desarrollo;
  }

  public void setDesarrollo(String desarrollo) {
    this.desarrollo = desarrollo;
  }

  public String getCliente() {
    return cliente;
  }

  public void setCliente(String cliente) {
    this.cliente = cliente;
  }

  public String getContrato() {
    return contrato;
  }

  public void setContrato(String contrato) {
    this.contrato = contrato;
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNotasDirectosDto.class;
  }

}
