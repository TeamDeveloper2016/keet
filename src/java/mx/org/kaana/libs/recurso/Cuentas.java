package mx.org.kaana.libs.recurso;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Encriptar;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.reflection.Methods;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 9/12/2022
 *@time 08:47:38 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Cuentas implements Serializable {

  private static final long serialVersionUID = -2762819723827706823L;
  
  private String proceso;
  private Boolean encriptado;

  public Cuentas(String proceso) {
    this(proceso, Boolean.TRUE);
  }

  public Cuentas(String proceso, Boolean encriptado) {
    this.proceso = proceso;
    this.encriptado = encriptado;
  }

  public String getProceso() {
    return proceso;
  }

  public Boolean getEncriptado() {
    return encriptado;
  }
  
  private Map<String, Object> all(String empresa) {
    Map<String, Object> regresar= new HashMap<>();
    Map<String, Object> params  = new HashMap<>();
    List<Entity> cuentas        = null;
    try {      
      params.put("empresa", empresa);
      params.put("proceso", this.proceso);
      cuentas= (List<Entity>)DaoFactory.getInstance().toEntitySet("TcKeetNotificacionesDto", "cuentas", params);
      Encriptar encriptar= new Encriptar();
      if(cuentas!= null && !cuentas.isEmpty()) 
        for (Entity item: cuentas) 
          if(this.encriptado)
            regresar.put(item.toString("nombre"), encriptar.desencriptar(item.toString("contacto")));
          else
            regresar.put(item.toString("nombre"), item.toString("contacto"));
      encriptar= null;
    } // try
    catch (Exception e) {
      Error.mensaje(e);
    } // catch	
    finally {
      Methods.clean(cuentas);
      Methods.clean(params);
    } // finally
    return regresar;
  }

  public Map<String, Object> all() {
    return all(Configuracion.getInstance().getPropiedad("sistema.empresa.principal"));
  }
  
  public Map<String, Object> admin() {
    return all("imox");
  }
  

}
