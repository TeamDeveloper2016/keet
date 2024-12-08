package mx.org.kaana.keet.reportes.tenicos.backing;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletContext;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.nomina.reglas.Egresos;
import mx.org.kaana.keet.nomina.reglas.ManoObra;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EReportes;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named(value = "keetReportesTecnicosControl")
@ViewScoped
public class Control extends Contratos implements Serializable {

  private static final long serialVersionUID = 1793667741599428879L;

  @PostConstruct
  @Override
  protected void init() {
    try {      
      super.init();
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
      params.put("sortOrder", "order by tc_keet_desarrollos.nombres, tc_keet_contratos.clave desc");
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("desarrollo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("contrato", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("noViviendas", EFormatoDinamicos.MILES_SIN_DECIMALES));
      columns.add(new Columna("costo", EFormatoDinamicos.MILES_SIN_DECIMALES));
      columns.add(new Columna("destajos", EFormatoDinamicos.MILES_SIN_DECIMALES));
      columns.add(new Columna("subcontratados", EFormatoDinamicos.MILES_SIN_DECIMALES));
      columns.add(new Columna("porElDia", EFormatoDinamicos.MILES_SIN_DECIMALES));
      columns.add(new Columna("administrativos", EFormatoDinamicos.MILES_SIN_DECIMALES));
      columns.add(new Columna("materiales", EFormatoDinamicos.MILES_SIN_DECIMALES));
      columns.add(new Columna("indirecto", EFormatoDinamicos.MILES_SIN_DECIMALES));
      this.lazyModel = new FormatCustomLazy("VistaReportesDto", "control", params, columns);
      UIBackingUtilities.resetDataTable();
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
 
  @Override
  public void doReporte() throws Exception {
  	Parametros comunes           = null;
		Map<String, Object>params    = null;
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;
		try{		
      reporteSeleccion= EReportes.REPORTE_CONTROL;
      params= this.toPrepare();																																																					
      params.put("sortOrder", "order by tc_keet_desarrollos.nombres, tc_keet_contratos.clave desc");
      comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.reporte= JsfBase.toReporte();	
      parametros= comunes.getComunes();
      parametros.put("ENCUESTA", reporteSeleccion.getTitulo());
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
 
  public StreamedContent getContrato() {
		StreamedContent regresar= null;		
    ManoObra manoObra       = null;
    Entity ultima           = null;
    Map<String, Object> params= new HashMap<>();
		Entity seleccionado      = null;				
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");						
      params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());      
      params.put("idTipoNomina", 1);      
      params.put("ejercicio", Fecha.getAnioActual());      
      ultima= (Entity)DaoFactory.getInstance().toEntity("VistaNominaDto", "ejercicio", params);
      if(ultima!= null && !ultima.isEmpty()) {
        manoObra   = new ManoObra(ultima.toLong("idNomina"), seleccionado.toLong("idDesarrollo"), seleccionado.toLong("idContrato"));
        String name= manoObra.execute();
        String contentType= EFormatos.XLS.getContent();
        InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(EFormatos.XLS.toPath().concat(name));  
        regresar          = new DefaultStreamedContent(stream, contentType, name);				
      } // if  
		} // try 
		catch (Exception e) {
			Error.mensaje(e);
		} // catch		
    finally {
      Methods.clean(params);
    } // finally
    return regresar;		
	} // getContrato
  
}