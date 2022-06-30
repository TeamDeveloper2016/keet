package mx.org.kaana.sakbe.suministros.backing;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.mantic.comun.IBaseStorage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@Named(value= "sakbeSuministrosLubricante")
@ViewScoped
public class Lubricante extends Accion implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Lubricante.class);
  private static final long serialVersionUID= 127393488565639367L;
  
}