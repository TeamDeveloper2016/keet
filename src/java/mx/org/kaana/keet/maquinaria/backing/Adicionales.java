package mx.org.kaana.keet.maquinaria.backing;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;

@Named(value = "keetMaquinariaAdicionales")
@ViewScoped
public class Adicionales extends mx.org.kaana.keet.catalogos.contratos.backing.Adicionales implements Serializable {

  private static final long serialVersionUID = 327393488565139367L;

	@PostConstruct
  @Override
  protected void init() {		
    try {
      super.init();
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 
  
}