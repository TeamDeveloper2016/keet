package mx.org.kaana.jobs;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 22-sep-2015
 *@time 9:11:42
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.procesos.acceso.beans.Autentifica;
import mx.org.kaana.keet.nomina.enums.ENominaEstatus;
import mx.org.kaana.keet.nomina.reglas.Calculos;
import mx.org.kaana.libs.formato.BouncyEncryption;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Apertura implements Job, Serializable {

	private static final Log LOG              = LogFactory.getLog(Apertura.class);
	private static final long serialVersionUID= 7505746848602636876L;

	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {
		Calculos calculos         = null;
    Map<String, Object> params= new HashMap<>();    
		try {
      Boolean process= Boolean.FALSE;      
      switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
        case "cafu":
          process= Objects.equals(Calendar.getInstance().get(Calendar.DAY_OF_WEEK), Calendar.THURSDAY);
          //process= Boolean.TRUE;
          break;
        case "gylvi":
          break;
        case "triana":
          break;
      } // swtich
      LOG.error("ENTRO A CORRER LA NOMINA DE FORMA AUTOMATICA ["+ process+ "]");
      if(process) {
        params.put("idTipoNomina", 1L);      
        Entity ultima= (Entity)DaoFactory.getInstance().toEntity("VistaNominaDto", "ultima", params);
        if(ultima!= null && !ultima.isEmpty() && Objects.equals(ultima.toLong("idNominaEstatus"), ENominaEstatus.INICIADA.getIdKey())) {
          Autentifica autentifica= this.toLoadUser();
          String path= this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath().substring(0, this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath().indexOf("/WEB-INF/"));
          calculos= new Calculos(ultima.toLong("idNomina"), autentifica, new String[] {"1", "2", "4"}, path, Boolean.TRUE);
          if(calculos.ejecutar(EAccion.PROCESAR)) 
            LOG.error("La nómina se corrio de forma correcta [".concat(ultima.toString("nomina")).concat("] !"));
        } // if  
        else
          LOG.error("La nomina NO tiene el estatus correcto [".concat(ultima!= null && !ultima.isEmpty()? ultima.toString("nomina"): "1900-00").concat("] !"));
      } // if  
      else
        LOG.error("No aplica para este servidor [".concat(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")).concat("]"));
    } // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch	
    finally {
      Methods.clean(params);
    } // finally
	} // execute
  
  private Autentifica toLoadUser() throws Exception {
    String password     = BouncyEncryption.decrypt(Configuracion.getInstance().getPropiedad("sistema.autenticar.contrasenia"));
    Autentifica regresar= new Autentifica();
    if (regresar.tieneAccesoBD(Configuracion.getInstance().getPropiedad("sistema.autenticar.cuenta"), password, "127.0.0.1")) {
      regresar.loadSucursales();
      LOG.warn("Acceso automático con autentifica [".concat(regresar.toString()).concat("]"));
    } // if  
    return regresar;
  } // toLoadUser
  
  public static void main(String ... args) throws JobExecutionException {
//     Apertura apertura= new Apertura();
//     apertura.execute(null);
  }
  
}

