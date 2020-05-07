package mx.org.kaana.keet.catalogos.rubros.beans;

import mx.org.kaana.keet.db.dto.TcKeetRubrosDto;
import mx.org.kaana.libs.pagina.UISelectEntity;


public class Rubro extends TcKeetRubrosDto{

	private static final long serialVersionUID = 2814730894955813434L;
	private UISelectEntity ikEmpaqueUnidadMedida;

	public Rubro() {
		this(new UISelectEntity(-1L));
	}

	public Rubro(UISelectEntity ikEmpaqueUnidadMedida) {
		super();
		this.ikEmpaqueUnidadMedida = ikEmpaqueUnidadMedida;
	}
		
	public UISelectEntity getIkEmpaqueUnidadMedida() {
		return ikEmpaqueUnidadMedida;
	}

	public void setIkEmpaqueUnidadMedida(UISelectEntity ikEmpaqueUnidadMedida) {
		this.ikEmpaqueUnidadMedida = ikEmpaqueUnidadMedida;
		if(this.ikEmpaqueUnidadMedida!= null)
			setIdEmpaqueUnidadMedida(this.ikEmpaqueUnidadMedida.getKey());
	}

	
}
