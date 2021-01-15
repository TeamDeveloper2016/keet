package mx.org.kaana.kajool.test.val.reglas;

import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.kajool.test.val.beans.Unidad;
import mx.org.kaana.kajool.test.val.beans.Evaluacion;
import mx.org.kaana.kajool.test.val.beans.Factor;
import mx.org.kaana.kajool.test.val.beans.Reactivo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @company INEGI
 * @project IKTAN (Sistema de seguimiento y control de proyectos)
 * @date 14/01/2021
 * @time 10:15:43 PM
 * @author Alejandro Jimenez Garcia <alejandro.jimenez@inegi.org.mx>
 */
public class Regional {
  private static final Log LOG = LogFactory.getLog(Regional.class);
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
                                                   // 5 Reativos por factor con su propio ponderador suman 100% 
    double[] ponderadoresReactivos1 = new double[] {10D, 10D, 10D, 10D, 10D};
    double[] ponderadoresReactivos2 = new double[] {5D, 5D, 5D, 5D, 5D};
                                                   // 4 Factores con su propio ponderador suman 100%
    double[] ponderadoresFactores   = new double[] {25D, 30D, 30D, 15D};
                                                   // CGOR   DGES   DGEE  DGGSPIJ  DGGMA   DGA   CGI  DGIAI suman 100%
    double[] ponderadoresDirecciones= new double[] {  20D,   15D,   15D,   15D,     5D,   10D,  5D,   5D};  
    
    List<Reactivo> reactivos     = new ArrayList<>();
    List<Factor> factores        = new ArrayList<>();
    List<Evaluacion> evaluaciones= new ArrayList<>();
    for (int x = 0; x< 8; x++) {
      for (int y = 0; y< 4; y++) {
        for (int z = 0; z< 5; z++) {
          // esto genera valores entre 1 y 5 en valor entero, simula las respuestas
          //double respuesta= (int)(1+ (Math.random() * (6- 1)));
          double respuesta= 5D;
          if(y % 2== 0 && z< 4)
            reactivos.add(new Reactivo(respuesta, ponderadoresReactivos2[z]));
          else  
            if(y % 2== 1)
              reactivos.add(new Reactivo(respuesta, ponderadoresReactivos1[z]));         
        } // for
        factores.add(new Factor(reactivos, ponderadoresFactores[y]));         
        reactivos.clear();
      } // for
      evaluaciones.add(new Evaluacion(factores, ponderadoresDirecciones[x]));
      factores.clear();
    } // for
    Unidad DRN= new Unidad(evaluaciones);
    LOG.info("DRN: "+ DRN.execute()+ " Ok ");  
  }

}
