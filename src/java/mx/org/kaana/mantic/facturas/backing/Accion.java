package mx.org.kaana.mantic.facturas.backing;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 15/02/2021
 *@time 09:03:36 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "manticFacturasAccion")
@ViewScoped
public class Accion extends Catalogos {

  private static final long serialVersionUID = 5176930684697433682L;

  public Accion() {
    super();
    this.idTipo= 1L;
  }

  @PostConstruct
  @Override
  protected void init() {
    super.init();
  }  
  
}
