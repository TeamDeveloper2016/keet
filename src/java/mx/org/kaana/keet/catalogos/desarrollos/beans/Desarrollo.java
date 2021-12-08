package mx.org.kaana.keet.catalogos.desarrollos.beans;

import java.io.Serializable;
import mx.org.kaana.keet.db.dto.TcKeetDesarrollosDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class Desarrollo extends TcKeetDesarrollosDto implements Serializable {

  private static final long serialVersionUID = 3714740442764839227L;

	private UISelectEntity ikCliente;

	public Desarrollo() {
		this(new UISelectEntity(-1L));
	}

	public Desarrollo(UISelectEntity ikCliente) {
		super();
		this.ikCliente= ikCliente;
	}

	public UISelectEntity getIkCliente() {
		return ikCliente;
	}

	public void setIkCliente(UISelectEntity ikCliente) {
		this.ikCliente = ikCliente;
		if(this.ikCliente!= null)
			setIdCliente(this.ikCliente.getKey());
	}	

}