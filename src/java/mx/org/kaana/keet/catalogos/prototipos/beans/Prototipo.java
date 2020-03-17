package mx.org.kaana.keet.catalogos.prototipos.beans;

import mx.org.kaana.keet.db.dto.TcKeetPrototiposDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class Prototipo extends TcKeetPrototiposDto{

	private static final long serialVersionUID= -8169606512160959150L;
	private UISelectEntity ikCliente;
	private UISelectEntity ikPlano;

	public Prototipo() {
		this(new UISelectEntity(-1L), new UISelectEntity(-1L));
	}

	public Prototipo(UISelectEntity ikCliente, UISelectEntity ikPlano) {
		super();
		this.ikCliente= ikCliente;
		this.ikPlano  = ikPlano;
	}

	public UISelectEntity getIkCliente() {
		return ikCliente;
	}

	public void setIkCliente(UISelectEntity ikCliente) {
		this.ikCliente = ikCliente;
		if(this.ikCliente!= null)
			setIdCliente(this.ikCliente.getKey());
	}	

	public UISelectEntity getIkPlano() {
		return ikPlano;
	}

	public void setIkPlano(UISelectEntity ikPlano) {
		this.ikPlano = ikPlano;
	}	
}