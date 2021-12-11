package mx.org.kaana.keet.catalogos.proyectos.beans;

import java.util.Objects;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetProyectosLotesDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class Lote extends TcKeetProyectosLotesDto {

	private static final long serialVersionUID = 1088774352011658013L;	
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
    int hash = 7;
    hash = 97 * hash + Objects.hashCode(this.getManzana());
    hash = 97 * hash + Objects.hashCode(this.getLote());
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final Lote other = (Lote) obj;
    if (!Objects.equals(this.getManzana(), other.getManzana())) {
      return false;
    }
    if (!Objects.equals(this.getLote(), other.getLote())) {
      return false;
    }
    return true;
  }
	
	@Override
  public Class toHbmClass() {
    return TcKeetProyectosLotesDto.class;
  }	
  
}