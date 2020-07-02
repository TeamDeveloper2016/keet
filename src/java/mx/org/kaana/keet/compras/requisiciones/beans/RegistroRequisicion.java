package mx.org.kaana.keet.compras.requisiciones.beans;

import java.io.Serializable;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.mantic.compras.requisiciones.beans.Requisicion;
import mx.org.kaana.mantic.compras.requisiciones.reglas.MotorBusqueda;

public class RegistroRequisicion implements Serializable {

	private static final long serialVersionUID=-6413309480495985622L;
	
	private Requisicion requisicion;	
	private UISelectEntity proveedor;
	private Long idRequisicion;			
	
	public RegistroRequisicion() {
		this(new Requisicion(), new UISelectEntity(-1L));
	}
	
	public RegistroRequisicion(Requisicion requisicion, UISelectEntity proveedor) {
		this.idRequisicion= requisicion.getIdRequisicion();
		this.requisicion  = requisicion;
		this.proveedor    = proveedor;						
	}

	public RegistroRequisicion(Long idRequisicion) {
		this.idRequisicion= idRequisicion;						
		init();		
	}
	
	public Requisicion getRequisicion() {
		return requisicion;
	}

	public void setRequisicion(Requisicion requisicion) {
		this.requisicion = requisicion;
	}

	public UISelectEntity getProveedor() {
		return proveedor;
	}

	public void setProveedor(UISelectEntity proveedor) {
		this.proveedor = proveedor;
	}		

	private void init(){
		MotorBusqueda motorBusqueda= null;		
		try {
			motorBusqueda= new MotorBusqueda(this.idRequisicion);
			this.requisicion= motorBusqueda.toRequisicion();
			this.proveedor= new UISelectEntity(this.requisicion.getIdProveedor());
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // init		
}
