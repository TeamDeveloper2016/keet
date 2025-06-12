package mx.org.kaana.keet.catalogos.contratos.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Contratos {
  
  private static final Log LOG= LogFactory.getLog(Contratos.class);

  public static void main(String[] args) {
    Map<String, Object> params= new HashMap<>();
    StringBuilder sb          = new StringBuilder();
    StringBuilder depurar     = new StringBuilder();
    StringBuilder info        = new StringBuilder();
    String codigo             = null;
    Long estatus              = null;
    try {      
      // for (int x= 124; x< 142; x++) {
      for (int x= 60; x<= 88; x++) {
        params.put("idContrato", x);
        Entity contrato= (Entity)DaoFactory.getInstance().toEntity("TcKeetContratosDto", "byId", params);
        if(!Objects.equals(contrato, null) && !contrato.isEmpty()) {
          List<Entity> lotes= (List<Entity>)DaoFactory.getInstance().toEntitySet("TcKeetContratosLotesDto", "byContrato", params);
          if(!Objects.equals(lotes, null) && !lotes.isEmpty()) {
            for (Entity lote: lotes) {
              String clave= Cadena.rellenar(contrato.toLong("idEmpresa").toString(), 3, '0', true)+ 
                            Cadena.rellenar(contrato.toLong("ejercicio").toString(), 4, '0', true)+ 
                            Cadena.rellenar(contrato.toLong("orden").toString(), 3, '0', true)+ 
                            Cadena.rellenar(lote.toLong("orden").toString(), 3, '0', true);
              params.put("clave", clave);      
              depurar.append("-- lote[").append(clave).append("]\n");
              List<Entity> conceptos= (List<Entity>)DaoFactory.getInstance().toEntitySet("TcKeetContratosDto", "parche", params);
              if(!Objects.equals(conceptos, null) && !conceptos.isEmpty()) {
                codigo= null;
                sb.append("[").append(x).append("|").append(contrato.toString("nombre")).append("] ").append(clave).append(" > ").append(conceptos.size()).append("\n");
                for (Entity item: conceptos) {
                  // LOG.info(item.toString("nombre")+ " | "+ item.toString("codigo")+ " | "+ item.toLong("idEstacionEstatus")+ " | "+ item.toDouble("costo"));
                  if(Objects.equals(codigo, null) || Objects.equals(codigo, item.toString("codigo"))) {
                    if(!Objects.equals(codigo, null)) {
                      if((Objects.equals(estatus, 2L) || Objects.equals(estatus, 3L)) && (Objects.equals(item.toLong("idEstacionEstatus"), 2L) || Objects.equals(item.toLong("idEstacionEstatus"), 3L))) {
                        depurar.append("update tc_keet_estaciones set id_estacion_estatus= 6 where id_estacion_estatus= ").append(item.toLong("idEstacionEstatus")).append(" and id_estacion= ").append(item.toLong("idEstacion")).append(";\n");
                        info.append(item.toLong("idEstacion")).append("|").append(item.toLong("idEstacionEstatus")).append("|").append(item.toString("codigo")).append("|").append(item.toString("nombre")).append("|").append(item.toDouble("costo")).append("|").append(item.toTimestamp("registro")).append("\n");
                      } // if  
                      else
                        if(Objects.equals(item.toLong("idEstacionEstatus"), 1L)) {
                          depurar.append("update tc_keet_estaciones set costo= 0, id_estacion_estatus= 5 where id_estacion_estatus= 1 and id_estacion= ").append(item.toLong("idEstacion")).append(";\n");
                          info.append(item.toLong("idEstacion")).append("|").append(item.toLong("idEstacionEstatus")).append("|").append(item.toString("codigo")).append("|").append(item.toString("nombre")).append("|").append(item.toDouble("costo")).append("|").append(item.toTimestamp("registro")).append("\n");
                        } // if  
                    } // if
                  } // if
                  codigo = item.toString("codigo");
                  estatus= item.toLong("idEstacionEstatus");
                } // for
              } // for
            } // for
          } // if
        } // if
      } // for x
      LOG.info(depurar.toString());
      LOG.info(sb.toString());
      LOG.info(info.toString());
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
