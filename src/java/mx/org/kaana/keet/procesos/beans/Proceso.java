package mx.org.kaana.keet.procesos.beans;

import java.io.Serializable;
import mx.org.kaana.keet.db.dto.TcKeetSubProcesosDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 25/09/2023
 *@time 11:08:38 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx
 */

public class Proceso extends TcKeetSubProcesosDto implements Serializable {

  private static final long serialVersionUID = 3518579943910403842L;
  
  private String proceso;

  public Proceso() {
    this(-1L);
  }

  public Proceso(Long key) {
    super(key);
  }
  
  public String getProceso() {
    return proceso;
  }

  public void setProceso(String proceso) {
    this.proceso = proceso;
  }

}
