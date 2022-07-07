package mx.org.kaana.sakbe.suministros.backing;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.mantic.comun.IBaseStorage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@Named(value= "sakbeSuministrosHerramienta")
@ViewScoped
public class Herramienta extends Accion implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Herramienta.class);
  private static final long serialVersionUID= 127393488525639367L;
  
}