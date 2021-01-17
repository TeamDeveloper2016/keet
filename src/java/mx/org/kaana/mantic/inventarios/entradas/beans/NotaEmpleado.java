package mx.org.kaana.mantic.inventarios.entradas.beans;

import java.io.Serializable;
import mx.org.kaana.keet.db.dto.TcKeetNotasManosObrasDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

/**
 *@company KAJOOL
 *@project KAJOOL (Control system polls)
 *@date 15/01/2021
 *@time 06:21:57 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class NotaEmpleado extends TcKeetNotasManosObrasDto implements Serializable {

  private static final long serialVersionUID = -3508288794503649110L;

	private UISelectEntity ikDesarrollo;
	private UISelectEntity ikCliente;
	private UISelectEntity ikContrato;
	private UISelectEntity ikEmpresaPersona;
  private String desarrollo;
  private String cliente;
  private String contrato;
  private String empleado;

  public NotaEmpleado() {
    this(-1L);
  }

  public NotaEmpleado(Long key) {
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

  public UISelectEntity getIkEmpresaPersona() {
    return ikEmpresaPersona;
  }

  public void setIkEmpresaPersona(UISelectEntity ikEmpresaPersona) {
    this.ikEmpresaPersona = ikEmpresaPersona;
		if(this.ikEmpresaPersona!= null)
		  this.setIdEmpresaPersona(this.ikEmpresaPersona.getKey());
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

  public String getEmpleado() {
    return empleado;
  }

  public void setEmpleado(String empleado) {
    this.empleado = empleado;
  }

  @Override
  public Class toHbmClass() {
    return TcKeetNotasManosObrasDto.class;
  }

  public boolean isEqual(NotaEmpleado empleado) {
    return ((this.getIdDesarrollo()== null && empleado.getIdDesarrollo()== null) || (this.getIdDesarrollo().equals(empleado.getIdDesarrollo()))) &&
           ((this.getIdCliente()== null && empleado.getIdCliente()== null) || (this.getIdCliente().equals(empleado.getIdCliente()))) &&
           ((this.getIdContrato()== null && empleado.getIdContrato()== null) || (this.getIdContrato().equals(empleado.getIdContrato()))) &&
           ((this.getIdEmpresaPersona()== null && empleado.getIdEmpresaPersona()== null) || (this.getIdEmpresaPersona().equals(empleado.getIdEmpresaPersona())));
  }
  
}
