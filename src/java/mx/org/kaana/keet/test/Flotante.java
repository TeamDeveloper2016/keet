package mx.org.kaana.keet.test;

import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.libs.formato.Numero;

public class Flotante {

  public static void main(String ... args) {
    Double iva= 3.16D;
    System.out.println(iva.intValue());
    Estaciones estaciones= new Estaciones();
    System.out.println(estaciones.toKey("0012021002002000000000000", 4));
    System.out.println("*HOLA\\n*".replaceAll("(\\*|\\\\n)+", ""));
    
    System.out.println(Numero.redondea(1.1, 1)+ " | "+ Numero.redondea(1.4, 1));
  }
  
}
