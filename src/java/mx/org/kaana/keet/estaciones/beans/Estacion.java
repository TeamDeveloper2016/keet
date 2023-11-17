package mx.org.kaana.keet.estaciones.beans;

import java.io.Serializable;
import mx.org.kaana.kajool.enums.EBooleanos;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class Estacion extends TcKeetEstacionesDto implements Serializable {

  private static final long serialVersionUID = 5830486508098158849L;
	
	private UISelectEntity ikEmpaqueUnidadMedida;
	private boolean pantilla;
	private static int TAMANIO_NIVEL= 3; 

	public Estacion() {
		this(new UISelectEntity(-1L), false);
	}

	public Estacion(UISelectEntity ikEmpaqueUnidadMedida, boolean pantilla) {
		super();
		this.ikEmpaqueUnidadMedida= ikEmpaqueUnidadMedida;
		this.pantilla             = pantilla;
    this.setIdPlantilla(this.pantilla? EBooleanos.SI.getIdBooleano():EBooleanos.NO.getIdBooleano());
		this.setIdEstacionEstatus(EEstacionesEstatus.INICIAR.getKey());
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
	
	 @Override
  public boolean equals(Object obj) {
    if (obj == null) 
      return false;
    final TcKeetEstacionesDto other = (TcKeetEstacionesDto) obj;
    if (getIdEstacion() != other.getIdEstacion() && (getIdEstacion() == null || !getIdEstacion().equals(other.getIdEstacion()))) 
      return false;
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdEstacion() != null ? getIdEstacion().hashCode() : 0);
    return hash;
  }	
	
}
