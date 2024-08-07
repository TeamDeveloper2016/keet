package mx.org.kaana.keet.prestamos.proveedor.beans;

import mx.org.kaana.keet.db.dto.TcKeetAnticiposDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class Anticipo extends TcKeetAnticiposDto {

	private static final long serialVersionUID = 7175957623819992557L;
	private UISelectEntity ikDeudor;

	public Anticipo() {
		this(new UISelectEntity(-1L));
		this.setImporte(500D);
	}

	public Anticipo(UISelectEntity ikDeudor) {
		super();
		this.ikDeudor= ikDeudor;
	}

	public UISelectEntity getIkDeudor() {
		return ikDeudor;
	}

	public void setIkDeudor(UISelectEntity ikDeudor) {
		this.ikDeudor = ikDeudor;
		if(this.ikDeudor!= null)
			setIdMoroso(this.ikDeudor.getKey());
	}	

}