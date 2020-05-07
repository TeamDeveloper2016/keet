package mx.org.kaana.keet.catalogos.rubros.beans;

import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetRubrosGruposDto;
import mx.org.kaana.libs.pagina.UISelectEntity;


public class RubroGrupo extends TcKeetRubrosGruposDto{
	
	private static final long serialVersionUID = -8612861466457643864L;
	private UISelectEntity ikPuntoGrupo;
	private ESql accion;

	public RubroGrupo() {
		this(ESql.UPDATE, -1L);
	}
	
	public RubroGrupo(ESql accion, Long key) {
	  this(accion, key, new UISelectEntity(-1L));
	}

	public RubroGrupo(ESql accion, Long key, UISelectEntity ikPuntoGrupo) {
		super(key);
		this.accion = accion;
		this.ikPuntoGrupo = ikPuntoGrupo;
	}

	public ESql getAccion() {
		return accion;
	}

	public void setAccion(ESql accion) {
		this.accion = accion;
	}
	
	
	public boolean isVisible(){
		return !this.accion.equals(ESql.DELETE);
	}
	
		
	public UISelectEntity getIkPuntoGrupo() {
		return ikPuntoGrupo;
	}

	public void setIkPuntoGrupo(UISelectEntity ikPuntoGrupo) {
		this.ikPuntoGrupo = ikPuntoGrupo;
		if(this.ikPuntoGrupo!= null)
			setIdPuntoGrupo(this.ikPuntoGrupo.getKey());
	}

	
}
