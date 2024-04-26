package mx.org.kaana.keet.estimacion.backing;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.pagina.JsfBase;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 25/04/2025
 *@time 10:21:12 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "keetEstimacionesEvidencias")
@ViewScoped
public class Evidencias extends Accion implements Serializable {

  private static final long serialVersionUID= 327393488565639167L;

	@PostConstruct
  @Override
  protected void init() {		
    try {
      super.init();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public String doAceptar() { 
    this.accion= EAccion.MODIFICAR;
    return super.doAceptar();
  } // doAccion
 
}