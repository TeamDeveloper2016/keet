package mx.org.kaana.keet.controles.beans;

import mx.org.kaana.kajool.enums.EBooleanos;
import mx.org.kaana.keet.db.dto.TcKeetControlesDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class Control extends TcKeetControlesDto {

  private static final long serialVersionUID = 3915818583681454772L;
	
	private UISelectEntity ikEmpaqueUnidadMedida;
	private boolean pantilla;
	private static int TAMANIO_NIVEL= 3; 

	public Control() {
		this(new UISelectEntity(-1L), false);
	}

	public Control(UISelectEntity ikEmpaqueUnidadMedida, boolean pantilla) {
		super();
		this.ikEmpaqueUnidadMedida = ikEmpaqueUnidadMedida;
		this.pantilla = pantilla;
		setIdPlantilla(this.pantilla? EBooleanos.SI.getIdBooleano():EBooleanos.NO.getIdBooleano());
		this.setIdControlEstatus(1L);
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
    final TcKeetControlesDto other = (TcKeetControlesDto) obj;
    if (getIdControl()!= other.getIdControl() && (getIdControl() == null || !getIdControl().equals(other.getIdControl()))) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 67 * hash + (getIdControl() != null ? getIdControl().hashCode() : 0);
    return hash;
  }	
	
}
