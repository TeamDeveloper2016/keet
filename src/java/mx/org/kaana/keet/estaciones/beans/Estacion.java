package mx.org.kaana.keet.estaciones.beans;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EBooleanos;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

public class Estacion extends TcKeetEstacionesDto {
	
	private UISelectEntity ikEmpaqueUnidadMedida;
	private boolean pantilla;
	private static int TAMANIO_NIVEL= 3; 

	public Estacion() {
		this(new UISelectEntity(-1L), false);
	}

	public Estacion(UISelectEntity ikEmpaqueUnidadMedida, boolean pantilla) {
		super();
		this.ikEmpaqueUnidadMedida = ikEmpaqueUnidadMedida;
		this.pantilla = pantilla;
		setIdPlantilla(this.pantilla? EBooleanos.SI.getIdBooleano():EBooleanos.NO.getIdBooleano());
		this.setIdEstacionEstatus(1L);
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
    if (obj == null) {
      return false;
    }
    final TcKeetEstacionesDto other = (TcKeetEstacionesDto) obj;
    if (getIdEstacion() != other.getIdEstacion() && (getIdEstacion() == null || !getIdEstacion().equals(other.getIdEstacion()))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdEstacion() != null ? getIdEstacion().hashCode() : 0);
    return hash;
  }	
		

	
}
