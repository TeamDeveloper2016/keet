package mx.org.kaana.jobs;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 22-sep-2015
 *@time 9:11:42
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.procesos.acceso.beans.Autentifica;
import mx.org.kaana.keet.db.dto.TcKeetNominasBitacoraDto;
import mx.org.kaana.keet.nomina.enums.ENominaEstatus;
import mx.org.kaana.keet.nomina.reglas.Transaccion;
import mx.org.kaana.libs.formato.BouncyEncryption;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Cierre implements Job, Serializable {

	private static final Log LOG              =LogFactory.getLog(Cierre.class);
	private static final long serialVersionUID=7505746848602636876L;

	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {
    Map<String, Object> params= new HashMap<>();    
		try {
      Boolean process= Boolean.FALSE;      
      switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
        case "cafu":
          break;
        case "gylvi":
          process= Boolean.TRUE;      
          break;
        case "triana":
          break;
      } // swtich
      if(process) {
        params.put("idTipoNomina", 1L);      
        Entity nomina= (Entity)DaoFactory.getInstance().toEntity("VistaNominaDto", "ultima", params);
        if(nomina!= null && !nomina.isEmpty() && Objects.equals(nomina.toLong("idNominaEstatus"), ENominaEstatus.CALCULADA.getIdKey())) {
          Autentifica autentifica          = this.toLoadUser();
          TcKeetNominasBitacoraDto bitacora= new TcKeetNominasBitacoraDto(
            "PROCESO DE CIERRE AUTOMATICO DE NÓMINA", // String justificacion, 
            ENominaEstatus.TERMINADA.getIdKey(), // Long idNominaEstatus, 
            autentifica.getPersona().getIdUsuario(), // Long idUsuario, 
            -1L, // Long idNominaBitacora, 
            nomina.toLong("idNomina") // Long idNomina
          );
          String path= this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath().substring(0, this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath().indexOf("/WEB-INF/"));
          Transaccion transaccion= new Transaccion(nomina.toLong("idNomina"), autentifica, bitacora, path, Boolean.TRUE);
          if(transaccion.ejecutar(EAccion.JUSTIFICAR)) 
            LOG.info("La nómina se cerró de forma correcta [".concat(nomina.toString("nomina")).concat("] !"));
        } // if  
        else
          LOG.info("La nómina no tiene el estatus para cerrarse [".concat(nomina!= null && !nomina.isEmpty()? nomina.toString("nomina"): "1900-00").concat("] !"));
      } // if  
      else
        LOG.error("Entró a cerrar la nómina de forma automatica, pero no aplica para este servidor [".concat(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")).concat("]"));
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
    Cierre cierre= new Cierre();
    cierre.execute(null);
  }
  
}

