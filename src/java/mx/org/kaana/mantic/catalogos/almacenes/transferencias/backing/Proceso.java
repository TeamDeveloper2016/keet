package mx.org.kaana.mantic.catalogos.almacenes.transferencias.backing;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.pagina.JsfBase;

@Named(value= "manticCatalogosAlmacenesTransferenciasProceso")
@ViewScoped
public class Proceso extends Normal implements Serializable {

  private static final long serialVersionUID= 322393488565639317L;
  
  @Override
  protected void init() {		
    super.init();
    this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "almacenista": JsfBase.getFlashAttribute("retorno"));
  }
  
  
}