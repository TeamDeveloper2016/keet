package mx.org.kaana.keet.test.contrato;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.Concepto;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 12/01/2022
 * @time 11:33:34 AM
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public class Colecciones {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    List<Concepto> conceptos= new ArrayList();
    for (int x = 0; x < 10; x++) {
      Concepto concepto= new Concepto((int)(Math.random()* 100), "&A"+ x, "ESTO ES UN EJEMPLO");
      conceptos.add(concepto);
    } // for
    Collections.sort(conceptos);
    for (Concepto item : conceptos) {
      System.out.println(item);
    } // for
    System.out.println("Existe &A0: "+ conceptos.indexOf(new Concepto("&A0")));
  }

}
