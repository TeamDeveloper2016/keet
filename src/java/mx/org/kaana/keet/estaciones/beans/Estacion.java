package mx.org.kaana.keet.estaciones.beans;

import mx.org.kaana.kajool.enums.EBooleanos;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class Estacion extends TcKeetEstacionesDto{
	
	private UISelectEntity ikEmpaqueUnidadMedida;
	private boolean pantilla;

	public Estacion() {
		this(new UISelectEntity(-1L), false);
	}

	public Estacion(UISelectEntity ikEmpaqueUnidadMedida, boolean pantilla) {
		super();
		this.ikEmpaqueUnidadMedida = ikEmpaqueUnidadMedida;
		this.pantilla = pantilla;
		setIdPlantilla(this.pantilla? EBooleanos.SI.getIdBooleano():EBooleanos.NO.getIdBooleano());
	}
		
	public UISelectEntity getIkEmpaqueUnidadMedida() {
		return ikEmpaqueUnidadMedida;
	}

	public void setIkEmpaqueUnidadMedida(UISelectEntity ikEmpaqueUnidadMedida) {
		this.ikEmpaqueUnidadMedida = ikEmpaqueUnidadMedida;
		if(this.ikEmpaqueUnidadMedida!= null)
			setIdEmpaqueUnidadMedida(this.ikEmpaqueUnidadMedida.getKey());
	}

	public boolean isPantilla() {
		return getIdPlantilla()!= null && getIdPlantilla().equals(EBooleanos.SI.getIdBooleano());
	}

	public void setPantilla(boolean pantilla) {
		this.pantilla = pantilla;
		setIdPlantilla(this.pantilla? EBooleanos.SI.getIdBooleano():EBooleanos.NO.getIdBooleano());
	}
	
	
	
	
		
		

	
}
