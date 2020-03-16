package mx.org.kaana.keet.catalogos.proyectos.beans;

import mx.org.kaana.keet.db.dto.TcKeetProyectosDto;
import mx.org.kaana.libs.pagina.UISelectEntity;


public class Proyecto extends TcKeetProyectosDto{
	
	 private UISelectEntity ikCliente;
	 private UISelectEntity ikDesarrollo;
	 private UISelectEntity ikTipoObra;

	public Proyecto() {
		super();
	}
	 
	public UISelectEntity getIkCliente() {
    return ikCliente;
  }

  public void setIkCliente(UISelectEntity ikCliente) {
    this.ikCliente = ikCliente;
		if(this.ikCliente!= null)
			this.setIdCliente(this.ikCliente.getKey());
  }

	public UISelectEntity getIkDesarrollo() {
		return ikDesarrollo;
	}

	public void setIkDesarrollo(UISelectEntity ikDesarrollo) {
		this.ikDesarrollo = ikDesarrollo;
		if(this.ikDesarrollo!= null)
			this.setIdDesarrollo(this.ikDesarrollo.getKey());
	}

	public UISelectEntity getIkTipoObra() {
		return ikTipoObra;
	}

	public void setIkTipoObra(UISelectEntity ikTipoObra) {
		this.ikTipoObra = ikTipoObra;
		if(this.ikTipoObra!= null)
			this.setIdTiposObras(this.ikTipoObra.getKey());
	}

	
	
}
