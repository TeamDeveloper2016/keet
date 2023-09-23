package mx.org.kaana.mantic.catalogos.almacenes.confrontas.backing;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.pagina.JsfBase;

@Named(value= "manticCatalogosAlmacenesConfrontasProceso")
@ViewScoped
public class Proceso extends Accion implements Serializable {

  private static final long serialVersionUID= 317393488565639367L;

  @Override
  protected void init() {		
    super.init();
    this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "almacenista": JsfBase.getFlashAttribute("retorno"));
  }
}