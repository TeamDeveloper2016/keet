package mx.org.kaana.keet.test.contrato;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.reflection.Methods;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 8/01/2022
 * @time 02:45:23 PM
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public class Fechas {

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    Map<String, Object> params = null;
    try {      
      // 001,2021,002,001001001000000
      List<String> sentencias= new ArrayList<>();
      params = new HashMap<>();      
      List<Entity> contratos= (List<Entity>)DaoFactory.getInstance().toEntitySet("select id_contrato as id_key, ejercicio, orden, no_viviendas as viviendas from tc_keet_contratos where id_contrato in (2,3,4) order by orden desc");
      if(contratos!= null)
        for (Entity contrato: contratos) {
          for (int x = 1; x<= contrato.toInteger("viviendas"); x++) {
            String numero = Cadena.rellenar(String.valueOf(contrato.toLong("orden")), 3, '0', true);
            String orden  = Cadena.rellenar(String.valueOf(x), 3, '0', true);
            String clave  = "001".concat(contrato.toString("ejercicio")).concat(numero).concat(orden);
            List<Entity> estaciones= (List<Entity>)DaoFactory.getInstance().toEntitySet("select id_estacion as id_key, clave, codigo, nombre, entrega from tc_keet_estaciones where tc_keet_estaciones.clave like '"+ clave+ "%' and tc_keet_estaciones.nivel= 5 order by tc_keet_estaciones.clave");
            if(estaciones!= null) {
              for (Entity estacion: estaciones) {
                System.out.println(estacion);
                String cadena= estacion.toString("clave").substring(0, 16);
                Entity fecha= (Entity)DaoFactory.getInstance().toEntity("select id_estacion as id_key, max(entrega) as entrega from tc_keet_estaciones where tc_keet_estaciones.clave like '"+ cadena+ "%' and tc_keet_estaciones.nivel= 6 order by tc_keet_estaciones.clave");
                if(fecha!= null && fecha.toDate("entrega")!= null) {
                  sentencias.add("update tc_keet_estaciones set entrega='"+ fecha.toDate("entrega")+ "' where id_estacion= "+ estacion.toLong("idKey")+ ";");
                } // if  
              } // for
              Entity fecha= (Entity)DaoFactory.getInstance().toEntity("select id_estacion as id_key, max(entrega) as entrega from tc_keet_estaciones where tc_keet_estaciones.clave like '"+ clave+ "%' and tc_keet_estaciones.nivel= 6 order by tc_keet_estaciones.clave");
              if(fecha!= null && fecha.toDate("entrega")!= null) {
                String lote= Cadena.rellenar(clave, 25, '0', false);
                sentencias.add("update tc_keet_estaciones set entrega='"+ fecha.toDate("entrega")+ "' where clave= '"+ lote+ "';");
              } // if  
            } // if  
          } // for
          // break;
        } // for
      for (String item : sentencias) {
        System.out.println(item);
      } // for
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }

}
