package mx.org.kaana.jobs;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 11-abr-2022
 *@time 11:04:42
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Encriptar;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.wassenger.Cafu;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Garantias implements Job, Serializable {

	private static final Log LOG              =LogFactory.getLog(Garantias.class);
	private static final long serialVersionUID=7505746848602636876L;
	private static final String BODY_GARANTIA = "Contrato: \\nNombre *{contrato}* con periodo de {periodo} del desarrollo *{desarrollo}* por un importe de *${importe}* el cuál venció *{vence}*\\n\\n";

	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {
    List<Entity> garantias     = null;
    List<Columna> columns      = null;    
    Map<String, Object> params = new HashMap<>();
    Map<String, Object> actores= new HashMap<>();
		try {      
      Encriptar encriptar= new Encriptar();
      params.put("fecha", Fecha.getHoyEstandar());      
      columns = new ArrayList<>();
      columns.add(new Columna("desarrollo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("fondoGarantia", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("vence", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
      LOG.debug("BUSCAR FONDOS DE GARANTIAS QUE YA EXPIRARON Y NO SE HA PAGADO");
      garantias= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaEstimacionesDto", "garantias", params, Constantes.SQL_TODOS_REGISTROS);
      if(garantias!= null && !garantias.isEmpty()) {
        UIBackingUtilities.toFormatEntitySet(garantias, columns);
        actores.put("Alejandro Jiménez García", encriptar.desencriptar("cd4b3e3924191b057b8187"));
        switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
          case "cafu":
            actores.put("Carlos Alberto Calderon Solano", encriptar.desencriptar("dc58cd49352018057c9fff"));
            actores.put("Irma de Lourdes Hernandez Romo", encriptar.desencriptar("150075e05dc2b3a69fea2b"));
            break;
          case "gylvi": // AQUI FALTA AGREGAR EL CELULAR DE VIZCAINO
            actores.put("Luis Cesar Lopez Manzur", encriptar.desencriptar("89f468ef6bec68d249b0d1"));
            actores.put("Jordi Alfonso Fariña Quiroz", encriptar.desencriptar("b8a5989f9b9e999e93fa00"));
            break;
          case "triana":
            actores.put("Jesús Fernando Villalpando Cisneros", encriptar.desencriptar("c2bfb2a5999c9b9f99fe01"));
            actores.put("José Refugio Villalpando Vargas", encriptar.desencriptar("69d448cf47cdb4a495fa1e"));
            break;
        } // swtich
        StringBuilder sb= new StringBuilder();
        Cafu notificar= new Cafu();
        for (Entity item: garantias) {
          params.put("contrato", item.toString("nombre"));
          params.put("periodo", "*"+ item.toString("inicio")+ "* al *"+ item.toString("termino")+ "*");
          params.put("desarrollo", item.toString("desarrollo"));
          params.put("importe", item.toString("fondoGarantia"));
          params.put("vence", item.toString("vence"));
          sb.append(Cadena.replaceParams(BODY_GARANTIA, params, true));
        } // for  
        for (String residente: actores.keySet()) {
          notificar.setNombre(Cadena.nombrePersona(residente));
          notificar.setCelular((String)actores.get(residente));
          LOG.info("Enviando mensaje de whatsapp al celular: "+ residente);
          notificar.doSendGarantia(sb.toString());
        } // for
      } // if
	  } // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch	
    finally {
      Methods.clean(params);
      Methods.clean(columns);
      Methods.clean(actores);
    } // finally
	} // execute
  
  public static void main(String ... args) throws JobExecutionException {
    Garantias garantias= new Garantias();
    garantias.execute(null);
  }
  
}

