package mx.org.kaana.kajool.seguridad.listeners;

import java.io.Serializable;
import java.util.HashMap;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.libs.Constantes;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 17/02/2025
 *@time 01:31:54 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class ProgressMonitor extends HashMap<String, Monitoreo> implements Serializable {

  private static final long serialVersionUID = -3285686235543825266L;

  public ProgressMonitor() {
    super.put(Constantes.DEFAULT_MONITOR, new Monitoreo(Constantes.DEFAULT_MONITOR));
  }
  
  public Monitoreo progreso() {
    return this.progreso(Constantes.DEFAULT_MONITOR);
  }
  
  public Monitoreo progreso(String id) {
    Monitoreo regresar= null;
    if(this.containsKey(id))
      regresar= this.get(id);
    else {
      regresar= new Monitoreo(id);
      this.put(id, regresar);
    } // else
    return regresar;
  }

  public void clean(String id) {
    if(this.containsKey(id)) {
      this.get(id).terminar();
      this.remove(id);
    } // if  
  }
  
}
