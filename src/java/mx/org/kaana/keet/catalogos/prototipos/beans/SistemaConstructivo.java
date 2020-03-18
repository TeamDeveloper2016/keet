package mx.org.kaana.keet.catalogos.prototipos.beans;

import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetPrototiposConstructivosDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class SistemaConstructivo extends TcKeetPrototiposConstructivosDto{

	private static final long serialVersionUID = -425045157859715857L;
	private UISelectEntity ikConstructivo; 
	private ESql accion;
	private Boolean nuevo;	

	public SistemaConstructivo() {
		this(new UISelectEntity(-1L));
	}

	public SistemaConstructivo(UISelectEntity ikConstructivo) {
		this(ESql.INSERT, ikConstructivo);
	}

	public SistemaConstructivo(ESql accion, UISelectEntity ikConstructivo) {
		this(-1L, accion, true, ikConstructivo);
	}
	
	public SistemaConstructivo(Long key, ESql accion, Boolean nuevo, UISelectEntity ikConstructivo) {
		super(key);
		this.nuevo         = nuevo;
		this.accion        = accion;
		this.ikConstructivo= ikConstructivo;
	}
	
	public UISelectEntity getIkConstructivo() {
		return ikConstructivo;
	}

	public ESql getAccion() {
		return accion;
	}

	public void setAccion(ESql accion) {
		this.accion = accion;
	}	

	public void setIkConstructivo(UISelectEntity ikConstructivo) {
		this.ikConstructivo = ikConstructivo;
		if(this.ikConstructivo!= null)
			this.setIdConstructivo(this.ikConstructivo.getKey());		  		
	} // setIkConstructivo

	public Boolean getNuevo() {
		return nuevo;
	}

	public void setNuevo(Boolean nuevo) {
		this.nuevo = nuevo;
	}	
}