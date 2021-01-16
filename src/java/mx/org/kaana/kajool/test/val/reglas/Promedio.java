package mx.org.kaana.kajool.test.val.reglas;

import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.kajool.test.val.beans.Direccion;
import mx.org.kaana.kajool.test.val.beans.Reactivo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @company INEGI
 * @project IKTAN (Sistema de seguimiento y control de proyectos)
 * @date 15/01/2021
 * @time 11:40:23 AM
 * @author Alejandro Jimenez Garcia <alejandro.jimenez@inegi.org.mx>
 */
public class Promedio {

  private static final Log LOG = LogFactory.getLog(Promedio.class);
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    List<Reactivo> personal= new ArrayList<>();
    personal.add(new Reactivo(15D)); // DGEE Blancas
    personal.add(new Reactivo(15D)); // DGEE Duran
    personal.add(new Reactivo(15D)); // DGEE Perez
//    personal.add(new Reactivo(15D));
    Direccion direccion= new Direccion(personal, 15D);
    LOG.info("DRN "+ direccion.execute()+ " Ok.");        
  }

}
