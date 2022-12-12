package mx.org.kaana.jobs;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 11-abr-2022
 *@time 14:11:42
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
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Encriptar;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.recurso.Cuentas;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.wassenger.Cafu;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.xml.Dml;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class Balance implements Job, Serializable {

	private static final Log LOG              =LogFactory.getLog(Balance.class);
	private static final long serialVersionUID=7505746848602636876L;
  
	private Reporte reporte;

	@Override
	public void execute(JobExecutionContext jec) throws JobExecutionException {
    Map<String, Object> params = new HashMap<>();
    Map<String, Object> actores= new HashMap<>();
    List<Columna> columns      = null;
    String nomina              = null;
    String periodo             = null;
    try {      
			columns= new ArrayList<>();
			columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_NOMBRE_DIA));
			columns.add(new Columna("termino", EFormatoDinamicos.FECHA_NOMBRE_DIA));
      params.put("idTipoNomina", 1L);
      Entity idNomina= (Entity)DaoFactory.getInstance().toEntity("VistaNominaDto", "ultima", params);
      if(idNomina!= null && !idNomina.isEmpty()) {
        UIBackingUtilities.toFormatEntity(idNomina, columns);
        nomina = idNomina.toString("semana");
        periodo= idNomina.toString("inicio").concat(" al ").concat(idNomina.toString("termino"));
        Cuentas cuentas= new Cuentas("balance");
        actores.putAll(cuentas.all());
        this.toReporte();
        Cafu notificar= new Cafu("", "", this.reporte.getAlias(), nomina, periodo);
        for (String actor: actores.keySet()) {
          notificar.setNombre(Cadena.nombrePersona(actor));
          notificar.setCelular((String)actores.get(actor));
          LOG.info("Enviando mensaje de whatsapp al celular: "+ actor);
          notificar.doSendEstadoCuenta();
        } // for
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(actores);
			Methods.clean(columns);
    } // finally
	} // execute
  
  private void toReporte() {
		Map<String, Object>params    = new HashMap<>();
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;
    String path                  = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
    try {   
      path= path.substring(0, path.indexOf("WEB-INF/"));
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);	
      switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
        case "cafu":
          params.put("idDesarrollo", -1L);	
          break;
        case "gylvi": 
          params.put("idDesarrollo", 3L);	
          break;
        case "triana":
          params.put("idDesarrollo", -1L);	
          break;
      } // swtich
      reporteSeleccion= EReportes.CONTRATO_RESUMEN;
      this.reporte= new Reporte();	
      parametros= (new Parametros(1L)).getComunes();
      parametros.put("ENCUESTA", Configuracion.getInstance().getPropiedad("sistema.empresa.principal").toUpperCase());
      parametros.put("REPORTE_TITULO", reporteSeleccion.getTitulo());			
      parametros.put("REPORTE_ICON", path.concat("resources/iktan/icon/acciones/"));			
      parametros.put("REPORTE_SQL_SEMANA", Dml.getInstance().getSelect("VistaEstimacionesDto", "semana", params));
      parametros.put("REPORTE_SQL_ACUMULADO", Dml.getInstance().getSelect("VistaEstimacionesDto", "acumulado", params));
      parametros.put("REPORTE_SQL_CONFRONTA", Dml.getInstance().getSelect("VistaEstimacionesDto", "pagado", params));
      parametros.put(Constantes.TILDE.concat("SUBREPORTE_SEMANA"), "/Paginas/Contenedor/Reportes/semana.jasper");
      parametros.put(Constantes.TILDE.concat("SUBREPORTE_ACUMULADO"), "/Paginas/Contenedor/Reportes/acumulado.jasper");
      parametros.put(Constantes.TILDE.concat("SUBREPORTE_CONFRONTA"), "/Paginas/Contenedor/Reportes/confronta.jasper");
      this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));					
      this.reporte.toLocalFiles(path);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }   
  
  public static void main(String ... args) throws JobExecutionException {
    Balance balance= new Balance();
    balance.execute(null);
  }
  
}

