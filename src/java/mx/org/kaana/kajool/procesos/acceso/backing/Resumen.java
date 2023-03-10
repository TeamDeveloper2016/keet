package mx.org.kaana.kajool.procesos.acceso.backing;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
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
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.keet.nomina.reglas.Egresos;
import mx.org.kaana.keet.nomina.reglas.Estimados;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.echarts.beans.Colors;
import mx.org.kaana.libs.echarts.beans.Title;
import mx.org.kaana.libs.echarts.json.ItemSelected;
import mx.org.kaana.libs.echarts.kind.BarModel;
import mx.org.kaana.libs.echarts.model.Multiple;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 20/03/2021
 * @time 12:27:03 PM
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value = "kajoolResumen")
@ViewScoped
public class Resumen extends Respaldos implements Serializable {

  private static final long serialVersionUID= 5323749709626263805L;
  private static final Log LOG              = LogFactory.getLog(Resumen.class);

  private List<Entity> semanas;
	private Reporte reporte;

  public List<Entity> getSemanas() {
    return semanas;
  }
  
  @PostConstruct
  @Override
  protected void init() {
    try {      
      this.attrs.put("hoy", Fecha.getHoyCorreo());
      this.attrs.put("individual", Boolean.TRUE);
      this.attrs.put("pathPivote", "/".concat((Configuracion.getInstance().getEtapaServidor().name().toLowerCase())).concat("/images/"));
      this.doLoad();
			if(JsfBase.isAdminEncuestaOrAdmin())
			  this.checkDownloadBackup();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

  @Override
  public void doLoad() {
    try {
      this.toLoadEmpresas();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } // doLoad
  
	protected void toLoadEmpresas() {
		Map<String, Object>params= null;
		List<Columna> columns    = null;
		try {
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			params= new HashMap<>();
			columns= new ArrayList<>();		
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns));
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("empresas")));
      this.doLoadDesarrollos();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally				
	} // toLoadEmpresas
  
  public void doLoadDesarrollos() {
		List<UISelectEntity>desarrollos= null;
    Map<String, Object> params     = null;
    try {
      params = new HashMap<>();
      params.put("idEmpresa", ((UISelectEntity)attrs.get("idEmpresa")).getKey());
  		desarrollos= UIEntity.build("TcKeetDesarrollosDto", "empresa", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS);
      this.attrs.put("desarrollos", desarrollos);
      this.attrs.put("idDesarrollo", desarrollos!= null? UIBackingUtilities.toFirstKeySelectEntity(desarrollos): new UISelectEntity(-1L));
      this.doLoadContratos();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
 		finally {
			Methods.clean(params);
		} // finally
 }

  public void doLoadContratos() {
		List<UISelectEntity>contratos= null;
    List<Columna> columns        = null;
    Map<String, Object> params   = new HashMap<>();
    try {
			columns= new ArrayList<>();
			columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("anticipo", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("fondoGarantia", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("vence", EFormatoDinamicos.FECHA_CORTA));
      params.put("idDesarrollo", ((UISelectEntity)this.attrs.get("idDesarrollo")).getKey());
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
  		contratos= UIEntity.build("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS);
      this.attrs.put("contratos", contratos);
      UISelectEntity idContrato= contratos!= null? UIBackingUtilities.toFirstKeySelectEntity(contratos): new UISelectEntity(-1L);
      this.attrs.put("idContrato", idContrato);
      if(idContrato.size()> 1)
        UIBackingUtilities.toFormatUIEntitySet(contratos, columns);
      this.doLoadDataConfronta();      
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
  }

  private void toLoadNomina() {
    List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    Entity idNomina           = null;
    try {
			// columns= ;
			columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_NOMBRE_DIA));
			columns.add(new Columna("termino", EFormatoDinamicos.FECHA_NOMBRE_DIA));
			columns.add(new Columna("anticipo", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("fondoGarantia", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("retenciones", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("facturar", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("estimado", EFormatoDinamicos.MILES_CON_DECIMALES));
      params.put("idContrato", ((UISelectEntity)this.attrs.get("idContrato")).getKey());      
      idNomina= (Entity)DaoFactory.getInstance().toEntity("VistaEstimacionesDto", "semana", params);
      if(idNomina!= null && !idNomina.isEmpty()) 
        UIBackingUtilities.toFormatEntity(idNomina, columns);
      this.attrs.put("idNomina", idNomina);
      this.semanas= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaEstimacionesDto", "semanas", params);
      if(semanas!= null && !semanas.isEmpty()) {
        columns.clear();
  			columns.add(new Columna("estimado", EFormatoDinamicos.MILES_CON_DECIMALES));
	  		columns.add(new Columna("facturado", EFormatoDinamicos.MILES_CON_DECIMALES));
	  		columns.add(new Columna("pagado", EFormatoDinamicos.MILES_CON_DECIMALES));
        UIBackingUtilities.toFormatEntitySet(semanas, columns);
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
  }
  
  private void toLoadAcumulado() {
    List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    Entity acumulado          = null;
    try {
			columns= new ArrayList<>();
			columns.add(new Columna("estimado", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("facturado", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("anticipo", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("fondoGarantia", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("pagado", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("vencido", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("porEstimar", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("porAnticipo", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("porFondo", EFormatoDinamicos.MILES_CON_DECIMALES));
      params.put("idContrato", ((UISelectEntity)this.attrs.get("idContrato")).getKey());      
      Value vencido= (Value)DaoFactory.getInstance().toField("VistaEstimacionesDto", "vencido", params, "vencido");
      if(vencido== null || vencido.getData()== null)
        vencido= new Value("vencido", 0D);
      acumulado= (Entity)DaoFactory.getInstance().toEntity("VistaEstimacionesDto", "acumulado", params);
      if(acumulado!= null && !acumulado.isEmpty()) {
        acumulado.put("vencido", vencido);
        UIBackingUtilities.toFormatEntity(acumulado, columns);
      } // if  
      this.attrs.put("acumulado", acumulado);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
			Methods.clean(columns);
    } // finally
  }
  
	public void doLoadDataConfronta() {
    String contrato           = "";
    Map<String, Object> params= new HashMap<>();
    try {   
      params.put("idContrato", ((UISelectEntity)this.attrs.get("idContrato")).getKey());    
      List<UISelectEntity> contratos= (List<UISelectEntity>)attrs.get("contratos");
      if(contratos!= null && !contratos.isEmpty()) {
        int index= contratos.indexOf((UISelectEntity)this.attrs.get("idContrato"));
        if(index>= 0) {
          this.attrs.put("idContrato", contratos.get(index));
          contrato= contratos.get(index).toString("nombre")+ "-"+ contratos.get(index).toString("etapa");
        } // if  
      } // if
      UIBackingUtilities.execute("jsEcharts.refresh({items: {json: {contrato: '".concat(contrato).concat("'}}});"));
      this.toLoadNomina();
      this.toLoadAcumulado();
			Multiple multiple= new Multiple(DaoFactory.getInstance().toEntitySet("VistaEstimacionesDto", "pagado", params));
      if(multiple.getData()!= null && !multiple.getData().isEmpty()) {
     		BarModel model= new BarModel(new Title(), multiple);
        model.remove();
        model.toChangeLineModel();
        model.toCustomFontSize(14); 
        model.toCustomColorSerie(new String[] {"#F2C438", "#689F38"});
        model.getLegend().setY("85%");
        model.getxAxis().getAxisLabel().getTextStyle().setFontSize(12);
        model.getxAxis().getAxisLabel().setFormatter("function(value) {return jsEcharts.upperCase(value);}");
        model.toCustomFormatLabel("function (params) {return jsEcharts.format(params, 'double');}");
        model.getTooltip().setFormatter("function (params) {return jsEcharts.tooltip(params, 'double');}");
        model.getTooltip().getTextStyle().setColor(Colors.COLOR_WHITE);
        this.attrs.put("confronta", model.toJson());
      } // if
      else
        this.attrs.put("confronta", "{}");
      UIBackingUtilities.execute("jsEcharts.update('confronta', {json:".concat((String)this.attrs.get("confronta")).concat("});"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }

	public void doRefreshEChartWith(ItemSelected itemSelected) {
		LOG.info(itemSelected);
    List<Columna> columns     = null;    
    Map<String, Object> params= null;
    try {  
      columns = new ArrayList<>();
      params = new HashMap<>();
      switch(itemSelected.getChart()) {
        case "":
          break;
      } // switch
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
  }

	public void doRefreshEChartSingle(String id, String group) {
		LOG.info("id: ".concat(id).concat(" group: ").concat(group));
		try {
      
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	}	

  public void doEnviar() {
    Map<String, Object> params = new HashMap<>();
    Map<String, Object> actores= new HashMap<>();
    String nomina              = "";
    String periodo             = "";
    try {      
      Entity idNomina= (Entity)this.attrs.get("idNomina");
      if(idNomina!= null && !idNomina.isEmpty()) {
        nomina = idNomina.toString("semana");
        periodo= idNomina.toString("inicio").concat(" al ").concat(idNomina.toString("termino"));
      } // if  
      Cuentas cuentas= new Cuentas("resumen");
      actores.putAll(cuentas.all());
      this.toReporte(Boolean.TRUE);
      Cafu notificar= new Cafu("", "", this.reporte.getAlias(), nomina, periodo);
      for (String actor: actores.keySet()) {
        notificar.setNombre(Cadena.nombrePersona(actor));
        notificar.setCelular((String)actores.get(actor));
        LOG.info("Enviando mensaje de whatsapp al celular: "+ actor);
        notificar.doSendEstadoCuenta();
      } // for
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(actores);
    } // finally
  } 
  
  public void doReporte() {
    this.toReporte(Boolean.FALSE);
  }
  
  private void toReporte(Boolean email) {
		Map<String, Object>params    = new HashMap<>();
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;
    try {   
      params.put("idContrato", ((UISelectEntity)this.attrs.get("idContrato")).getKey());	
      if((Boolean)this.attrs.get("individual")) {
        params.put("idDesarrollo", ((UISelectEntity)this.attrs.get("idDesarrollo")).getKey());	
        params.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato="+ ((UISelectEntity)this.attrs.get("idContrato")).getKey());	
      } // if  
      else {
        params.put("idDesarrollo", -1L);	
        params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);	
      } // if  
      reporteSeleccion= EReportes.CONTRATO_RESUMEN;
      this.reporte= JsfBase.toReporte();	
      parametros= (new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa())).getComunes();
      parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getTitulo().toUpperCase());
      parametros.put("REPORTE_TITULO", reporteSeleccion.getTitulo());			
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));			
      parametros.put("REPORTE_SQL_SEMANA", Dml.getInstance().getSelect("VistaEstimacionesDto", "semana", params));
      parametros.put("REPORTE_SQL_ACUMULADO", Dml.getInstance().getSelect("VistaEstimacionesDto", "acumulado", params));
      parametros.put("REPORTE_SQL_CONFRONTA", Dml.getInstance().getSelect("VistaEstimacionesDto", "pagado", params));
      parametros.put(Constantes.TILDE.concat("SUBREPORTE_SEMANA"), "/Paginas/Contenedor/Reportes/semana.jasper");
      parametros.put(Constantes.TILDE.concat("SUBREPORTE_ACUMULADO"), "/Paginas/Contenedor/Reportes/acumulado.jasper");
      parametros.put(Constantes.TILDE.concat("SUBREPORTE_CONFRONTA"), "/Paginas/Contenedor/Reportes/confronta.jasper");
      this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));					
			if(email) 
        this.reporte.doAceptarSimple();			
			else {				
				this.doVerificarReporte();
				this.attrs.put("reporteName", this.reporte.getArchivo());
				this.reporte.doAceptar();			
			} // else		      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  } 
  
  public void doVerificarReporte() {
		if(this.reporte.getTotal()> 0L)
			UIBackingUtilities.execute("start(" + this.reporte.getTotal() + ")");		
    else {
			UIBackingUtilities.execute("generalHide()");		
			JsfBase.addMessage("Generar reporte", "No se encontraron registros para el reporte", ETipoMensaje.ALERTA);
		} // else
	} // doVerificarReporte	  

  public StreamedContent getResumen() {
		StreamedContent regresar = null;		
    Egresos egresos          = null;
		try {
	  	egresos    = new Egresos(-1L, ((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
      String name= egresos.execute();
      String contentType= EFormatos.XLS.getContent();
      InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(EFormatos.XLS.toPath().concat(name));  
      regresar          = new DefaultStreamedContent(stream, contentType, name);				
		} // try 
		catch (Exception e) {
			Error.mensaje(e);
		} // catch		
    return regresar;		
	} // getResumen
  
  public StreamedContent getPagados() {
		StreamedContent regresar = null;		
    Egresos egresos          = null;
		try {
	  	egresos    = new Egresos(-1L, ((UISelectEntity)this.attrs.get("idContrato")).getKey());
      String name= egresos.execute();
      String contentType= EFormatos.XLS.getContent();
      InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(EFormatos.XLS.toPath().concat(name));  
      regresar          = new DefaultStreamedContent(stream, contentType, name);				
		} // try 
		catch (Exception e) {
			Error.mensaje(e);
		} // catch		
    return regresar;		
	} // getPagados
  
	public StreamedContent getEstimados() {
		StreamedContent regresar= null;		
    Estimados estimados     = null;
		try {
	  	estimados  = new Estimados(((UISelectEntity)this.attrs.get("idContrato")).getKey());
      String name= estimados.execute();
      String contentType= EFormatos.XLS.getContent();
      InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(EFormatos.XLS.toPath().concat(name));  
      regresar          = new DefaultStreamedContent(stream, contentType, name);				
		} // try 
		catch (Exception e) {
			Error.mensaje(e);
		} // catch		
    return regresar;		
	} // getEstimados
  
}
