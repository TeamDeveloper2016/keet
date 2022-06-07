package mx.org.kaana.sakbe.suministros.backing;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.pagina.UIBackingUtilities;

@Named(value = "sakbeSuministrosVisor")
@ViewScoped 
public class Visor extends Filtro implements Serializable {

  private static final long serialVersionUID= 8793267741599428338L;
  
  @Override
  protected void reset() {
    UIBackingUtilities.resetDataGrid();
  }

  @Override
  public String toPagina() {
		return "/Paginas/Sakbe/Suministros/visor";		
  } // toPagina()
  
}
