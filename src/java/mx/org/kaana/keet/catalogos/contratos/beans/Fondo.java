package mx.org.kaana.keet.catalogos.contratos.beans;

import java.io.Serializable;
import mx.org.kaana.keet.db.dto.TcKeetContratosVecimientosDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 11/04/2022
 *@time 06:04:47 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Fondo extends TcKeetContratosVecimientosDto implements Serializable {

  private static final long serialVersionUID = -3706865206161337746L;
  
	private UISelectEntity ikTipoMedioPago;
	private UISelectEntity ikBanco;

  public Fondo() {
    this(-1L);
  }

  public Fondo(Long key) {
    super(key);
    this.setIkTipoMedioPago(new UISelectEntity(-1L));
    this.setIkBanco(new UISelectEntity(-1L));
  }
  
 	public UISelectEntity getIkTipoMedioPago() {
		return ikTipoMedioPago;
	} 

	public void setIkTipoMedioPago(UISelectEntity ikTipoMedioPago) {
		this.ikTipoMedioPago = ikTipoMedioPago;
		if(this.ikTipoMedioPago!= null)
			this.setIdTipoMedioPago(this.ikTipoMedioPago.getKey());
	} 
  
	public UISelectEntity getIkBanco() {
		return ikBanco;
	} 

	public void setIkBanco(UISelectEntity ikBanco) {
		this.ikBanco = ikBanco;
		if(this.ikBanco!= null)
			this.setIdBanco(this.ikBanco.getKey());
	} 

}
