package mx.org.kaana.keet.catalogos.rubros.beans;

import mx.org.kaana.keet.db.dto.TcKeetRubrosDto;
import mx.org.kaana.libs.pagina.UISelectEntity;


public class Rubro extends TcKeetRubrosDto{

	private static final long serialVersionUID = 2814730894955813434L;
	private UISelectEntity ikEmpaqueUnidadMedida;
	private UISelectEntity departamento;

	public Rubro() {
		this(new UISelectEntity(-1L), new UISelectEntity(-1L));
	}

	public Rubro(UISelectEntity ikEmpaqueUnidadMedida,UISelectEntity departamento) {
		super();
		this.ikEmpaqueUnidadMedida = ikEmpaqueUnidadMedida;
		this.departamento = departamento;
	}
		
	public UISelectEntity getIkEmpaqueUnidadMedida() {
		return ikEmpaqueUnidadMedida;
	}

	public void setIkEmpaqueUnidadMedida(UISelectEntity ikEmpaqueUnidadMedida) {
		this.ikEmpaqueUnidadMedida = ikEmpaqueUnidadMedida;
		if(this.ikEmpaqueUnidadMedida!= null)
			setIdEmpaqueUnidadMedida(this.ikEmpaqueUnidadMedida.getKey());
	}

	public UISelectEntity getDepartamento() {
		return departamento;
	}

	public void setDepartamento(UISelectEntity departamento) {
		this.departamento = departamento;
	}
}
