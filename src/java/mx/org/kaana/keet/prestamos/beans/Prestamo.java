package mx.org.kaana.keet.prestamos.beans;

import mx.org.kaana.keet.db.dto.TcKeetPrestamosDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class Prestamo extends TcKeetPrestamosDto{

	private static final long serialVersionUID = 7175957623819992557L;
	private UISelectEntity ikDeudor;

	public Prestamo() {
		this(new UISelectEntity(-1L));
	}

	public Prestamo(UISelectEntity ikDeudor) {
		super();
		this.ikDeudor= ikDeudor;
	}

	public UISelectEntity getIkDeudor() {
		return ikDeudor;
	}

	public void setIkDeudor(UISelectEntity ikDeudor) {
		this.ikDeudor = ikDeudor;
		if(this.ikDeudor!= null)
			setIdDeudor(this.ikDeudor.getKey());
	}	

}