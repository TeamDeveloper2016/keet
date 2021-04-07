package mx.org.kaana.kajool.procesos.acceso.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.echarts.beans.Colors;
import mx.org.kaana.libs.echarts.beans.Title;
import mx.org.kaana.libs.echarts.json.ItemSelected;
import mx.org.kaana.libs.echarts.kind.BarModel;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.echarts.kind.StackModel;
import mx.org.kaana.libs.echarts.model.Multiple;
import mx.org.kaana.libs.echarts.model.Stacked;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 20/03/2021
 * @time 12:27:03 PM
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value = "kajoolContratos")
@ViewScoped
public class Contratos extends IBaseFilter implements Serializable {

  private static final long serialVersionUID= 5323749709626263802L;
  private static final Log LOG              = LogFactory.getLog(Contratos.class);
	protected static final String COORDENADA_CENTRAL= "21.8818,-102.291";

  private List<Entity> desarrollos;
  private Entity desarrollo;
  private Entity contrato;
	private MapModel model;
	
	public MapModel getModel() {
		return model;
	}

  public List<Entity> getDesarrollos() {
    return desarrollos;
  }

  public Entity getDesarrollo() {
    return desarrollo;
  }

  public void setDesarrollo(Entity desarrollo) {
    this.desarrollo = desarrollo;
  }

  @PostConstruct
  @Override
  protected void init() {
    try {      
      this.attrs.put("hoy", Fecha.getHoyCorreo());
      this.attrs.put("pathPivote", "/".concat((Configuracion.getInstance().getEtapaServidor().name().toLowerCase())).concat("/images/"));
      this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

  @Override
  public void doLoad() {
    try {
      this.toLoadNombres();
      this.toLoadContratos();
      this.toLoadLotes();
      this.toLoadCoordenadas();
    } // try
    catch (Exception e) {
      JsfBase.addMessageError(e);
    } // catch
  } // doLoad
  
  private void toLoadNombres() {
    Map<String, Object> params = null;
    try {      
      params = new HashMap<>();      
      params.put(Constantes.SQL_CONDICION, " and tc_keet_desarrollos.id_desarrollo is not null");      
      this.desarrollos= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaSeguimientoDto", "nombresDesarrollos", params);
      if(this.desarrollos!= null && !this.desarrollos.isEmpty())
        this.desarrollo = this.desarrollos.get(0);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
    
  private void toLoadContratos() {
    Map<String, Object> params = null;
    try {      
      this.attrs.put("nombreDesarrollo", this.desarrollo!= null && this.desarrollo.containsKey("nombre")? (String)this.desarrollo.toString("nombre"): "xyz");
      params = new HashMap<>();      
      params.put("desarrollo", this.attrs.get("nombreDesarrollo"));      
			Multiple multiple = new Multiple(DaoFactory.getInstance().toEntitySet("VistaTableroDto", "contratado", params));
      if(multiple.getData()!= null && !multiple.getData().isEmpty()) {
        BarModel stack= new BarModel(new Title(), multiple);
        stack.remove();
        stack.toCustomFontSize(14);
        stack.getLegend().setY("85%");
        stack.getxAxis().getAxisLabel().getTextStyle().setFontSize(12);
        stack.getxAxis().getAxisLabel().setFormatter("function(value) {return jsEcharts.label(value);}");
        stack.toCustomFormatLabel("function (params) {return jsEcharts.format(params, 'integer');}");
        stack.getTooltip().setFormatter("function (params) {return jsEcharts.tooltip(params, 'integer');}");
        stack.getTooltip().getTextStyle().setColor(Colors.COLOR_WHITE);
        this.attrs.put("contratos", stack.toJson());
        this.contrato= multiple.getData().get(multiple.getData().size()- 1);
      } // if
      else {
        JsfBase.addMessage("Informativo", "No se tienen contratos para el desarrollo ["+ this.attrs.get("nombreDesarrollo")+ "] !");      
        this.attrs.put("contratos", "{}");
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
  
  private void toLoadLotes() {
    Map<String, Object> params = null;
    try {      
      if(this.contrato!= null && !this.contrato.isEmpty()) {
        this.attrs.put("nombreContrato", (String)this.contrato.toString("category"));
        String clave= Cadena.rellenar(this.contrato.toLong("idEmpresa").toString(), 3, '0', true)+ Cadena.rellenar(this.contrato.toLong("ejercicio").toString(), 4, '0', true)+ Cadena.rellenar(this.contrato.toLong("orden").toString(), 3, '0', true);
        params = new HashMap<>();      
        params.put("clave", clave);      
        Stacked multiple = new Stacked(DaoFactory.getInstance().toEntitySet("VistaTableroDto", "avance", params));
        if(multiple.getData()!= null && !multiple.getData().isEmpty()) {
          StackModel stack= new StackModel(new Title(), multiple);
          stack.remove();
          stack.toCustomFontSize(14);
          stack.getLegend().setY("85%");
          stack.getxAxis().getAxisLabel().getTextStyle().setFontSize(12);
          stack.getxAxis().getAxisLabel().setFormatter("function(value) {return jsEcharts.label(value);}");
          stack.toCustomFormatLabel("function (params) {return jsEcharts.format(params, 'integer');}");
          stack.getTooltip().setFormatter("function (params) {return jsEcharts.tooltip(params, 'integer');}");
          stack.getTooltip().getTextStyle().setColor(Colors.COLOR_WHITE);
          stack.toCustomUniqueColorTopTotal("#000000");
          this.attrs.put("lotes", stack.toJson());
        } // if
        else {
          JsfBase.addMessage("Informativo", "No se tienen lotes para el contrato ["+ this.attrs.get("nombreContrato")+ "] !");      
          this.attrs.put("lotes", "{}");
        } // else
      } // if
      else {
        this.attrs.put("nombreContrato", "");
        JsfBase.addMessage("Informativo", "No se tienen lotes para el contrato ["+ this.attrs.get("nombreContrato")+ "] !");      
        this.attrs.put("lotes", "{}");
      } // else
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doUpdateDesarrolloSelect(SelectEvent event) {  
    this.toUpdateDesarrollo();
  }
  
  public void doUpdateDesarrolloUnSelect(UnselectEvent event) {  
    this.toUpdateDesarrollo();
  }
  
  private void toUpdateDesarrollo() {
    try {      
      if(this.desarrollo!= null && !this.desarrollo.isEmpty()) {
        this.toLoadContratos();
        this.toLoadLotes();
        UIBackingUtilities.execute("jsEcharts.update('contratos', {group:'00', json:".concat((String)this.attrs.get("contratos")).concat("});"));
        UIBackingUtilities.execute("jsEcharts.refresh({items: {json: {nombreDesarrollo:'"+ this.attrs.get("nombreDesarrollo")+ "'}}});");
        UIBackingUtilities.execute("jsEcharts.update('lotes', {group:'00', json:".concat((String)this.attrs.get("lotes")).concat("});"));
        UIBackingUtilities.execute("jsEcharts.refresh({items: {json: {nombreContrato:'"+ this.attrs.get("nombreContrato")+ "'}}});");
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

	public void doRefreshEChartSingle(String id, String group) {
		LOG.info("id: ".concat(id).concat(" group: ").concat(group));
		try {
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
	}	

	public void doRefreshEChartWith(ItemSelected itemSelected) {
		LOG.info(itemSelected);
    List<Columna> columns     = null;    
    Map<String, Object> params= null;
    try {  
      columns = new ArrayList<>();
      params = new HashMap<>();
      switch(itemSelected.getChart()) {
        case "contratos": 
          params.put("desarrollo", itemSelected.getName());
          params.put("contrato", itemSelected.getSeriesName());
          params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
          this.contrato= (Entity)DaoFactory.getInstance().toEntity("VistaTableroDto", "contrato", params);
          columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
          columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
          UIBackingUtilities.toFormatEntity(this.contrato, columns);
          this.toLoadLotes();
          this.toLoadCoordenadas();
          UIBackingUtilities.update("mapa");
          UIBackingUtilities.execute("jsEcharts.update('lotes', {group:'00', json:".concat((String)this.attrs.get("lotes")).concat("});"));
          UIBackingUtilities.execute("jsEcharts.refresh({items: {json: {nombreContrato:'"+ this.attrs.get("nombreContrato")+ "'}}});");
          UIBackingUtilities.execute("onOffSwitchTable('lotes', false);");
          break;
        case "lotes": 
          params.put("desarrollo", this.contrato.toString("desarrollo"));
          params.put("contrato", this.contrato.toString("category"));
          params.put(Constantes.SQL_CONDICION, "concat('M', tc_keet_contratos_lotes.manzana, 'L', tc_keet_contratos_lotes.lote)='"+ itemSelected.getName()+ "'");
          this.contrato= (Entity)DaoFactory.getInstance().toEntity("VistaTableroDto", "contrato", params);
          columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
          columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
          UIBackingUtilities.toFormatEntity(this.contrato, columns);
          String clave= Cadena.rellenar(this.contrato.toLong("idEmpresa").toString(), 3, '0', true)+ Cadena.rellenar(this.contrato.toLong("ejercicio").toString(), 4, '0', true)+ Cadena.rellenar(this.contrato.toLong("orden").toString(), 3, '0', true)+ Cadena.rellenar(this.contrato.toLong("secuencia").toString(), 3, '0', true);
          params.put("clave", clave);      
          columns.clear();
          columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
          List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaTableroDto", "detalle".concat(Cadena.letraCapital(itemSelected.getChart())), params);
          if(items!= null)
            UIBackingUtilities.toFormatEntitySet(items, columns);
          this.attrs.put("tablaDetalle".concat(Cadena.letraCapital(itemSelected.getChart())), items);  
          UIBackingUtilities.update("avanceLote");
          UIBackingUtilities.update("detalleLote");
          UIBackingUtilities.update("especificacion");
          UIBackingUtilities.update("tablaDetalle".concat(Cadena.letraCapital(itemSelected.getChart())));
          UIBackingUtilities.execute("onOffSwitchTable('"+ itemSelected.getChart()+ "', true);");
          UIBackingUtilities.execute("onOffSwitchTable('mapas', true);");
          UIBackingUtilities.execute("jsEcharts.refresh({items: {json: {nombre"+ Cadena.letraCapital(itemSelected.getChart())+ ":'"+ itemSelected.getName()+ "'}}});");
          if(!Objects.equals("local", itemSelected.getSeriesId())) {
            this.loadEvidencias(this.contrato);
            this.loadResidentes(this.contrato);
            this.loadContratistas(this.contrato);
            this.loadAvances(this.contrato);
          } // if  
          break;
      } // switch
      if("|contratos|".indexOf(itemSelected.getChart())> 0) {
      } // if 
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

  private void toLoadCoordenadas() {
		List<Entity> lotes        = null;
		Marker marker             = null;
		String icon               = null;
		Map<String, Object> params= null;
		List<Columna> columns     = null;
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
			this.model= new DefaultMapModel();
			this.attrs.put("coordenadaCentral", COORDENADA_CENTRAL);
			params= new HashMap<>();
      if(this.contrato!= null && !this.contrato.isEmpty()) {
        params.put("desarrollo", this.contrato.toString("desarrollo"));
        params.put("contrato", this.contrato.toString("category"));
        lotes= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaTableroDto", "georreferencia", params, Constantes.SQL_TODOS_REGISTROS);
        if(!lotes.isEmpty()) {
          UIBackingUtilities.toFormatEntitySet(lotes, columns);
          for(Entity lote: lotes) {
            icon  = this.toIcon(lote);
            marker= new Marker(new LatLng(Double.valueOf(lote.toString("latitud")), Double.valueOf(lote.toString("longitud"))), "Contrato: ".concat(lote.toString("nombre")).concat(", clave: ").concat(lote.toString("nombre")).concat(", lote: ").concat(lote.toString("codigo")), lote, icon);
            this.model.addOverlay(marker);
          } // for
          this.attrs.put("coordenadaCentral", lotes.get(0).toString("latitud").concat(",").concat(lotes.get(0).toString("longitud")));				
        } // if
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
  } // toLoadCoordenadas
  
  private String toIcon(Entity mzaLote) throws Exception {
		String regresar          = null;
		String imagen            = null;
		String color             = null;
		Map<String, Object>params= null;
		Entity estatus           = null;
		try {
			imagen= JsfBase.getContext().concat("/javax.faces.resource/icon/mapa/").concat("home-{color}-{orden}.png").concat(".jsf?ln=janal");
			color= EEstacionesEstatus.INICIAR.getColor();
			params= new HashMap<>();			
			params.put("clave", this.toClaveEstacion(mzaLote));
			estatus= (Entity) DaoFactory.getInstance().toEntity("VistaGeoreferenciaLotesDto", "estatusManzanaLote", params);
			if(estatus.toString("total")!= null) {
				this.attrs.put("porcentaje", new Integer(String.valueOf((estatus.toLong("terminado") * 100)/estatus.toLong("total"))));
				if(estatus.toLong("total").equals(estatus.toLong("terminado")))
					color= EEstacionesEstatus.TERMINADO.getColor();
				else if(estatus.toLong("total").equals(estatus.toLong("iniciado")))
					color= EEstacionesEstatus.INICIAR.getColor();
				else
					color= EEstacionesEstatus.EN_PROCESO.getColor();
			} // if	
			else
				this.attrs.put("porcentaje", 0);			
			params.clear();
			params.put("color", color);
			params.put("orden", mzaLote.toString("orden"));
			regresar= Cadena.replaceParams(imagen, params);
		} // try
		finally {
			Methods.clean(params);
		} // finally		
		return regresar;
	} // toIcon

	public void onMarkerSelect(OverlaySelectEvent event) {
		Marker marker= null;	
		try {
			marker= (Marker) event.getOverlay();
			this.loadEvidencias((Entity) marker.getData());
			this.loadResidentes((Entity) marker.getData());
			this.loadContratistas((Entity) marker.getData());
			this.loadAvances((Entity) marker.getData());
      ItemSelected item= new ItemSelected();
      item.setChart("lotes");
      item.setName(((Entity)marker.getData()).toString("codigo"));
      item.setSeriesId("local");
      this.doRefreshEChartWith(item);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		                    
  } // onMarkerSelect
  
	private void loadEvidencias(Entity seleccionado) {
		List<Columna> columns    = null;
		Map<String, Object>params= null;
		try {
			this.attrs.put("loteSeleccionado", seleccionado);
			columns= new ArrayList<>();
      columns.add(new Columna("nombrePersona", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombreUsuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("observaciones", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));						
			params= new HashMap<>();
			params.put("idDesarrollo", seleccionado.toLong("idDesarrollo"));
			params.put("idContratoLote", seleccionado.getKey());
		  this.attrs.put("importados", UIEntity.build("VistaCapturaDestajosDto", "allImportadosContratoLote", params, columns));
			this.doLoadFiles();
		} // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally		
	} // loadEvidencias

	private void doLoadFiles() {
		List<Entity>importados= null;		
		String dns            = null;
		String url            = null;
		try {
			dns= Configuracion.getInstance().getPropiedad("sistema.dns.".concat(Configuracion.getInstance().getEtapaServidor().name().toLowerCase()));			
			importados= (List<Entity>) this.attrs.get("importados");
			for(Entity importado: importados) {
				url= dns.substring(0, dns.indexOf(JsfBase.getContext())).concat(this.attrs.get("pathPivote").toString()).concat(importado.toString("ruta")).concat(importado.toString("archivo"));
				importado.put("url", new Value("url", url));
			} // for
			this.attrs.put("importados", importados);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
			throw e;
		} // catch		
	} // doLoadFiles
	
	private void loadResidentes(Entity seleccionado) {
		List<Entity>residentes   = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idDesarrollo", seleccionado.toLong("idDesarrollo"));
			residentes= DaoFactory.getInstance().toEntitySet("VistaGeoreferenciaLotesDto", "residentes", params);
			this.attrs.put("residentes", residentes);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadResidentes
	
	private void loadContratistas(Entity seleccionado) {
		List<Entity>contratistas = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idContratoLote", seleccionado.getKey());
			contratistas= DaoFactory.getInstance().toEntitySet("VistaGeoreferenciaLotesDto", "contratistasAsignado", params);
			this.attrs.put("contratistas", contratistas);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadContratistas
	
	private void loadAvances(Entity seleccionado) {
		Map<String, Object>params= null;
    List<Columna> columns    = null;				
    try {      			
			params = this.toPrepare(seleccionado);
      columns= new ArrayList<>();      
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("costo", EFormatoDinamicos.MONEDA_CON_DECIMALES));                  
	    this.lazyModel= new FormatLazyModel("VistaGeoreferenciaLotesDto", "avances", params, columns);			
			UIBackingUtilities.resetDataTable("tablaAvances");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {      
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} // loadAvances

	private Map<String, Object> toPrepare(Entity seleccionado) {
		Map<String, Object> regresar= null;
		try {
			regresar= new HashMap<>();			
			regresar.put("clave", this.toClaveEstacion(seleccionado));
			regresar.put("estatus", EEstacionesEstatus.EN_PROCESO.getKey() + "," + EEstacionesEstatus.TERMINADO.getKey());						
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toPrepare
  
	private String toClaveEstacion(Entity lote) {
		StringBuilder regresar= null;
		try {			
			regresar= new StringBuilder();
			regresar.append(Cadena.rellenar(lote.toString("idEmpresa"), 3, '0', true));
			regresar.append(Fecha.getAnioActual());
			regresar.append(Cadena.rellenar(lote.toString("contrato"), 3, '0', true));
			regresar.append(Cadena.rellenar(lote.toString("orden"), 3, '0', true));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar.toString();
	} // toClaveEstacion

	public String doCapturaAvances(Entity visitado) {
		String regresar          = null;    
		EOpcionesResidente opcion= null;
		Long idDepartamento      = null;
    try {
			if(visitado.toString("puesto").equals("SUBCONTRATISTA"))
				idDepartamento= Long.valueOf(visitado.toString("idsDepartamento").split(",")[0]);			
			else
				idDepartamento= Long.valueOf(visitado.toString("idsDepartamento"));			
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));			
			JsfBase.setFlashAttribute("figura", new UISelectEntity(visitado));
			JsfBase.setFlashAttribute("idDepartamento", idDepartamento);
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("idDesarrolloProcess", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("opcionResidente", opcion);						
			JsfBase.setFlashAttribute("opcionAdicional", EOpcionesResidente.GEOREFERENCIA);			
			regresar= "/Paginas/Keet/Catalogos/Contratos/Destajos/filtro.jsf".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
	} // doCapturaAvances
  
}
