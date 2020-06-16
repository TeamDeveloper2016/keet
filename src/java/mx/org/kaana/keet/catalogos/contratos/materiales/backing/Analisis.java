package mx.org.kaana.keet.catalogos.contratos.materiales.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.echarts.beans.Title;
import mx.org.kaana.libs.echarts.enums.EBarOritentation;
import mx.org.kaana.libs.echarts.kind.BarModel;
import mx.org.kaana.libs.echarts.model.Multiple;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosContratosMaterialesAnalisis")
@ViewScoped
public class Analisis extends IBaseFilter implements Serializable {

	private static final long serialVersionUID = 6319984968937774158L;
	private LocalDate inicio;
	private LocalDate termino;
	private List<Entity> lazy;

	public LocalDate getInicio() {
		return inicio;
	}

	public void setInicio(LocalDate inicio) {
		this.inicio=inicio;
	}

	public LocalDate getTermino() {
		return termino;
	}

	public void setTermino(LocalDate termino) {
		this.termino=termino;
	}

	public List<Entity> getLazy() {
		return lazy;
	}

  @PostConstruct
  @Override
  protected void init() {
    try {
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("idAcumula", "cantidad");
			this.attrs.put("cuantos", 5L);
    	this.attrs.put("titulo", "Costo");
    	this.attrs.put("campo", "costo");
    	this.attrs.put("columna", "costo");
			this.attrs.put("indicador", "{}");
			this.loadCatalogs();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
		List<Columna> columns    = null;
		Map<String, Object>params= null;
    try {
      params= this.toPrepare();	
			columns= new ArrayList<>();
			columns.add(new Columna((String)this.attrs.get("columna"), EFormatoDinamicos.MILES_CON_DECIMALES));
      this.lazy= DaoFactory.getInstance().toEntitySet("VistaComprasAlmacenDto", "analisis", params, (Long)this.attrs.get("cuantos"));
			UIBackingUtilities.toFormatEntitySet(this.lazy, columns);
			this.doRefreshEChartSingle("indicador", "keet");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally		
  } // doLoad
	
	public void doRefreshEChartSingle(String id, String group) {
		try {
  		Multiple simple = new Multiple(this.lazy);
			StringBuilder sb= new StringBuilder();
			sb.append("jsEcharts.add({");
			if(this.lazy!= null && !this.lazy.isEmpty()) {
				BarModel model= new BarModel(new Title(), simple, EBarOritentation.VERTICAL);
				model.removeMarks();
				model.removeLines();
				model.getGrid().setTop("6%");
				model.getxAxis().getAxisLabel().setFontSize(14);
				model.getyAxis().getAxisLabel().setFontSize(14);
				model.toCustomFontSize(14);
				model.getTooltip().getTextStyle().setColor("#FFFFFF");
				if("costo".equals((String)this.attrs.get("campo"))) {
  				model.toCustomFormatLabel("function (params) {return jsEcharts.format(params, 'money');}");
				  model.getTooltip().setFormatter("function (params) {return jsEcharts.tooltip(params, 'money');}");
				} // if	
				else {
  				model.toCustomFormatLabel("function (params) {return jsEcharts.format(params, 'double');}");
					model.getTooltip().setFormatter("function (params) {return jsEcharts.tooltip(params, 'double');}");
				} // else	
				model.getLegend().setFormatter("function (params) {return jsEcharts.legend(params);}");
				model.setBackgroundColor("#FBFCFD");
				model.setColor(Arrays.asList("#33CC33", "#FF00FF", "#33CCCC", "#FF5050", "#3366FF", "#FFFF00", "#00FF00", "#FF33CC", "#00CCFF", "#FF6600", "#6666FF", "#CCFF33", "#FF3399", "#00FF99", "#0099FF", "#FF9933", "#99FF33", "#9966FF", "#00FFCC", "#FF0066", "#0066FF", "#CC66FF", "#66FF33", "#FFCC00", "#33CC33", "#FF00FF", "#33CCCC", "#FF5050", "#3366FF", "#FFFF00", "#00FF00", "#FF33CC", "#00CCFF", "#FF6600", "#6666FF", "#CCFF33", "#FF3399", "#00FF99", "#0099FF", "#FF9933", "#99FF33", "#9966FF", "#00FFCC", "#FF0066", "#0066FF", "#CC66FF", "#66FF33", "#FFCC00", "#33CC33", "#FF00FF", "#33CCCC", "#FF5050", "#3366FF"));
				model.toCustomColorSerie("#33CC33", "#FF00FF", "#33CCCC", "#FF5050", "#3366FF", "#FFFF00", "#00FF00", "#FF33CC", "#00CCFF", "#FF6600", "#6666FF", "#CCFF33", "#FF3399", "#00FF99", "#0099FF", "#FF9933", "#99FF33", "#9966FF", "#00FFCC", "#FF0066", "#0066FF", "#CC66FF", "#66FF33", "#FFCC00", "#33CC33", "#FF00FF", "#33CCCC", "#FF5050", "#3366FF", "#FFFF00", "#00FF00", "#FF33CC", "#00CCFF", "#FF6600", "#6666FF", "#CCFF33", "#FF3399", "#00FF99", "#0099FF", "#FF9933", "#99FF33", "#9966FF", "#00FFCC", "#FF0066", "#0066FF", "#CC66FF", "#66FF33", "#FFCC00", "#33CC33", "#FF00FF", "#33CCCC", "#FF5050", "#3366FF");
				sb.append(id).append(": {group: '").append(group).append("', json:").append(model.toJson()).append(", title: '").append("Compras de materiales ").append(id.toUpperCase()).append("'}");
			} // if
			else 
				sb.append(id).append(": {group: '").append(group).append("', json:").append("{}").append(", title: '").append("Materiales ").append(id.toUpperCase()).append("'}");
			sb.append("});");
			UIBackingUtilities.execute(sb.toString());
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
	}	
	
	private void loadEmpresas() {
		Map<String, Object>params= null;
		List<Columna> columns    = null;
		try {
			params = new HashMap<>();
			columns= new ArrayList<>();			
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally{
			Methods.clean(params);
		}	// finally	
	} // loadEmpresas

	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb            = new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()>= 1L)				
			sb.append("(tc_keet_contratos.id_empresa in (").append(((UISelectEntity)this.attrs.get("idEmpresa")).getKey()).append(")) and ");
		else
			sb.append("(tc_keet_contratos.id_empresa in (").append(JsfBase.getAutentifica().getEmpresa().getSucursales()).append(")) and ");
  	regresar.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  	regresar.put("idTipoVale", 1L);
  	regresar.put("idDesarrollo", this.attrs.get("idDesarrollo"));
  	regresar.put("columna", this.attrs.get("columna"));
  	this.attrs.put("titulo", Cadena.letraCapital((String)this.attrs.get("columna")));
  	this.attrs.put("campo", this.attrs.get("columna"));
  	regresar.put("cuantos", this.attrs.get("cuantos"));
		if(!Cadena.isVacio(this.attrs.get("ejercicio")) && ((UISelectEntity)this.attrs.get("ejercicio")).getKey()>= 1L)	{			
			sb.append("date_format(tc_keet_vales.registro, '%Y')= '").append(this.attrs.get("ejercicio")).append("' and ");
		} // if	
		else {
			if(!Cadena.isVacio(this.inicio))
				sb.append("date_format(tc_keet_vales.registro, '%Y%m%d')>= '").append(this.inicio.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("' and ");
			if(!Cadena.isVacio(this.termino))
				sb.append("date_format(tc_keet_vales.registro, '%Y%m%d')<= '").append(this.termino.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("' and ");
		} // if
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	} // toPrepare

	private void loadCatalogs() {
		Map<String, Object>params= null;
		try {
			this.loadEmpresas();
			params= new HashMap<>();
		  params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("ejercicios", UIEntity.seleccione("VistaNominaDto", "ejercicios", params, "ejercicio"));
      this.attrs.put("ejercicio", new UISelectEntity(-1L));
			List<UISelectEntity> desarrollos= UIEntity.build("TcKeetDesarrollosDto", "row", params);
      this.attrs.put("desarrollos", desarrollos);
      this.attrs.put("idDesarrollo", UIBackingUtilities.toFirstKeySelectEntity(desarrollos));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);	
		} // catch				
    finally {
			Methods.clean(params);
		}	// finally
	} // loadCatalogs

}
	