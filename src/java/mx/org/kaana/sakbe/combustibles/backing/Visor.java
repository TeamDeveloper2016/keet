package mx.org.kaana.sakbe.combustibles.backing;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.pagina.UIBackingUtilities;

@Named(value = "sakbeCombustiblesVisor")
@ViewScoped 
public class Visor extends Filtro implements Serializable {

  private static final long serialVersionUID= 8793267741599428338L;
  
  @Override
  protected void reset() {
    UIBackingUtilities.resetDataGrid();
  }

  @Override
  public String toPagina() {
		return "/Paginas/Sakbe/Combustibles/visor";		
  } // toPagina()
  
}
