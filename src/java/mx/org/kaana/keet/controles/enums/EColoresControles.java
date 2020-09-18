package mx.org.kaana.keet.controles.enums;

/**
 *@company KAANA
 *@project 
 *@date 17/09/2020
 *@time 06:24:32 PM 
 *@author Team developer <team.developer@gmail.com>
 */

public enum EColoresControles {
  
   URBA_ESTRUCTURA("RECEP01", "janal-color-black"), ESTRUCTURA_OBRA_GRIS("RECEP02", "janal-color-purple"), OBRA_GRIS_TERMINADOS("RECEP03", "janal-janal-blue"), TERMINADOS_ENTREGADOS("RECEP04", "janal-color-green");
  
   private String start;
   private String color;

  private EColoresControles(String start, String color) {
    this.start = start;
    this.color = color;
  }

  public String getStart() {
    return start;
  }

  public String getColor() {
    return color;
  }
   
}
