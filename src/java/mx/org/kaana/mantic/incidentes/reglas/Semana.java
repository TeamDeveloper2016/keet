package mx.org.kaana.mantic.incidentes.reglas;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import mx.org.kaana.mantic.incidentes.beans.Dia;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 30/04/2022
 *@time 05:23:12 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Semana extends ArrayList<Dia> implements Serializable {

  private static final long serialVersionUID = -8203127693041044565L;
  private static final int dias= 7;
  
  private LocalDate fecha;

  public Semana(LocalDate fecha) {
    this.fecha= fecha;
    for (int x= 0; x< dias; x++) {
      this.add(new Dia(this.fecha));
      this.fecha= this.fecha.plusDays(1L);
    } // for
  }

}
