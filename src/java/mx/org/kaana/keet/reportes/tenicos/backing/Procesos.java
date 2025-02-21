package mx.org.kaana.keet.reportes.tenicos.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EReportes;

@Named(value = "keetReportesTecnicosProcesos")
@ViewScoped
public class Procesos extends Contratos implements Serializable {

  private static final long serialVersionUID = 1793667741599428879L;

  private Entity nomina;

  public Entity getNomina() {
    return nomina;
  }
  
  @PostConstruct
  @Override
  protected void init() {
    try {      
      super.init();
      this.doLoadSemanas();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch	
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns    = new ArrayList<>();
		Map<String, Object>params= null;
    try {
      params= this.toPrepare();	
      if(!Objects.equals(this.nomina, null)) {
        params.put("inicio", Fecha.formatear(Fecha.FECHA_ESTANDAR, (LocalDate)this.nomina.toDate("inicio")));
        params.put("termino", Fecha.formatear(Fecha.FECHA_ESTANDAR, (LocalDate)this.nomina.toDate("termino")));

        params.put("sortOrder", "order by tc_keet_desarrollos.nombres, tc_keet_contratos.clave desc");
        columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
        columns.add(new Columna("desarrollo", EFormatoDinamicos.MAYUSCULAS));
        columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
        columns.add(new Columna("contrato", EFormatoDinamicos.MAYUSCULAS));
        columns.add(new Columna("noViviendas", EFormatoDinamicos.MILES_SIN_DECIMALES));
        columns.add(new Columna("inicios", EFormatoDinamicos.MILES_SIN_DECIMALES));
        columns.add(new Columna("terminos", EFormatoDinamicos.MILES_SIN_DECIMALES));
        columns.add(new Columna("proceso", EFormatoDinamicos.MILES_SIN_DECIMALES));
        this.lazyModel = new FormatCustomLazy("VistaReportesDto", "procesos", params, columns);
        UIBackingUtilities.resetDataTable();
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally		
  } // doLoad
 
 	public void doLoadSemanas() {
		Map<String, Object>params   = new HashMap<>();
    List<Columna> columns       = new ArrayList<>();
    List<UISelectEntity> semanas= null;
    try {
      columns.add(new Columna("ini", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("fin", EFormatoDinamicos.FECHA_CORTA));
      params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      params.put("ejercicio", Fecha.getAnioActual());
      semanas= UIEntity.build("VistaNominaDto", "semanas", params, columns);
      this.attrs.put("semanas", semanas);
      this.nomina= UIBackingUtilities.toFirstKeySelectEntity(semanas);
      this.attrs.put("semana", this.nomina);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);	
		} // catch				
    finally {
			Methods.clean(params);
			Methods.clean(columns);
		}	// finally
	} 

  public void doUpdateNomina() {
    List<UISelectEntity> semanas= null;
    try {
      semanas= (List<UISelectEntity>)this.attrs.get("semanas");
      if(!Objects.equals(semanas, null) && !semanas.isEmpty()) {
        int index= semanas.indexOf((UISelectEntity)this.attrs.get("semana"));
        if(index>= 0)
          this.nomina= semanas.get(index);
        else
          this.nomina= null;
        this.attrs.put("semana", this.nomina);
      }
    } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);	
		} // catch	    
  }
  
  @Override
  public void doReporte() throws Exception {
  	Parametros comunes           = null;
		Map<String, Object>params    = null;
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;
		try{		
      reporteSeleccion= EReportes.REPORTE_PROCESOS;
      params= this.toPrepare();																																																					
      params.put("sortOrder", "order by tc_keet_desarrollos.nombres, tc_keet_contratos.clave desc");
      comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.reporte= JsfBase.toReporte();	
      parametros= comunes.getComunes();
      parametros.put("ENCUESTA", reporteSeleccion.getTitulo().concat(", semana [").concat(this.nomina.toString("semana")).concat("]"));
      parametros.put("NOMBRE_REPORTE", reporteSeleccion.getTitulo());
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));			
      this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));		
      this.doVerificarReporte();
      this.reporte.doAceptar();			
    } // try
    catch(Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);			
    } // catch	
  } 
  
}