
package mx.org.kaana.keet.catalogos.contratos.beans;

import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class Lote extends TcKeetContratosLotesDto{
	
	private UISelectEntity ikPrototipo;
	private UISelectEntity ikFachada;
	private ESql accion;


	public Lote() {
		this(ESql.UPDATE, -1L);
	}

	public Lote(ESql accion, Long key) {
		super(key);
		this.accion = accion;
	}
	
	
	public UISelectEntity getIkPrototipo() {
		return ikPrototipo;
	}

	public UISelectEntity getIkFachada() {
		return ikFachada;
	}


	public void setIkPrototipo(UISelectEntity ikPrototipo) {
		this.ikPrototipo = ikPrototipo;
		if(this.ikPrototipo!= null)
			this.setIdPrototipo(this.ikPrototipo.getKey());
	}

	public void setIkFachada(UISelectEntity ikFachada) {
		this.ikFachada = ikFachada;
		if(this.ikFachada!= null)
			this.setIdTipoFachada(this.ikFachada.getKey());
	}

	public ESql getAccion() {
		return accion;
	}

	public void setAccion(ESql accion) {
		this.accion = accion;
	}

	public boolean isMostar() {
		return !this.accion.equals(ESql.DELETE);
	}


	@Override
	public int hashCode() {
		int hash = 3;
    hash = 41 * hash + (getManzana() != null ? getManzana().hashCode() : 0);
    return hash;
	}
	
	@Override
  public Class toHbmClass() {
    return TcKeetContratosLotesDto.class;
  }
	
	
	
	
}
