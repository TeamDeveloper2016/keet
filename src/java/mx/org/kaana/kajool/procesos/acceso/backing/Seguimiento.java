package mx.org.kaana.kajool.procesos.acceso.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.procesos.acceso.enums.ESecuencia;
import mx.org.kaana.kajool.procesos.comun.Comun;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.echarts.beans.Colors;
import mx.org.kaana.libs.echarts.beans.Title;
import mx.org.kaana.libs.echarts.enums.EBarOritentation;
import mx.org.kaana.libs.echarts.json.ItemSelected;
import mx.org.kaana.libs.echarts.kind.BarModel;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.echarts.kind.DonutModel;
import mx.org.kaana.libs.echarts.kind.StackModel;
import mx.org.kaana.libs.echarts.model.Datas;
import mx.org.kaana.libs.echarts.model.Multiple;
import mx.org.kaana.libs.echarts.model.Simple;
import mx.org.kaana.libs.echarts.model.Stacked;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 20/03/2021
 * @time 12:27:03 PM
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value = "kajoolSeguimiento")
@ViewScoped
public class Seguimiento extends Comun implements Serializable {

  private static final long serialVersionUID= 5323749709626263802L;
  private static final Log LOG              = LogFactory.getLog(Seguimiento.class);
  private static final Integer TOP_NOMINA   = 3;

  private Long pivoteDesarrollo;
  private Long pivoteContratista;
  private StringBuilder nominasDesarrollo;
  private StringBuilder nominasContratista;
	private List<Entity> contratistas;
	private List<Entity> contratista;
  private List<Entity> desarrollos;
  private List<Entity> desarrollo;

  public List<Entity> getContratistas() {
    return contratistas;
  }

  public List<Entity> getDesarrollos() {
    return desarrollos;
  }

  public List<Entity> getContratista() {
    return contratista;
  }

  public void setContratista(List<Entity> contratista) {
    this.contratista = contratista;
  }

  public List<Entity> getDesarrollo() {
    return desarrollo;
  }

  public void setDesarrollo(List<Entity> desarrollo) {
    this.desarrollo = desarrollo;
  }

  @PostConstruct
  @Override
  protected void init() {
    try {      
      this.attrs.put("hoy", Fecha.getHoyCorreo());
      this.pivoteDesarrollo = -1L;
      this.pivoteContratista= -1L;
      this.nominasDesarrollo = new StringBuilder();
      this.nominasContratista= new StringBuilder();
      this.attrs.put("personalDesarrollo", new ArrayList<>());
      this.attrs.put("personalContratista", new ArrayList<>());
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
      this.toLoadPersonal();
      this.toLoadDesarrollos();
      this.toLoadMovimientos();
      this.toLoadContratistas();
      this.toLoadNominas(ESecuencia.IGUAL, "");
      this.toLoadSueldos();
      this.toLoadDestajos();
      this.toLoadDepartamentos();
    } // try
    catch (Exception e) {
      JsfBase.addMessageError(e);
    } // catch
  } // doLoad
  
  private void toLoadNombres() {
    try {      
      this.desarrollo= new ArrayList<>();
      this.desarrollos= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaSeguimientoDto", "nombresDesarrollos", Collections.EMPTY_MAP);
      this.contratista= new ArrayList<>();
      this.contratistas= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaSeguimientoDto", "nombresContratistas", Collections.EMPTY_MAP);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
    
  private void toLoadPersonal() {
    try {      
      Datas datas= new Datas("Personal", DaoFactory.getInstance().toEntitySet("VistaSeguimientoDto", "personal", Collections.EMPTY_MAP));
      Double personal= 0D;
      for (Entity item: datas.getData()) {
        personal+= item.toDouble("value");
      } // for
      DonutModel donut= new DonutModel("Personal activo", "65%", "30%", new Title(), datas);
      donut.getLegend().setY("80%");
			donut.toCustomFormatLabel("function (params) {return jsEcharts.format(params, 'percent');}");
      donut.getSeries().get(0).setCenter(Arrays.asList(new String[] {"50%", "45%"}));
      donut.toCustomDonut(String.valueOf(personal.intValue()), "25px", "40%");
      donut.setColor(Arrays.asList(new String[] {"#008000", "#ffff00", "#ffb300", "#607d8b"}));
      this.attrs.put("personal", donut.toJson());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
 
  private void toLoadDesarrollos() {
    try {      
			Stacked stacked = new Stacked(DaoFactory.getInstance().toEntitySet("VistaSeguimientoDto", "desarrollos", Collections.EMPTY_MAP));
  		StackModel stack= new StackModel(new Title(), stacked);
      stack.remove();
      stack.toCustomFontSize(14);
      stack.getLegend().setY("85%");
      stack.getxAxis().getAxisLabel().getTextStyle().setFontSize(12);
			stack.getxAxis().getAxisLabel().setFormatter("function(value) {return jsEcharts.label(value);}");
			stack.toCustomFormatLabel("function (params) {return jsEcharts.format(params, 'integer');}");
			stack.getTooltip().setFormatter("function (params) {return jsEcharts.tooltip(params, 'integer');}");
      stack.getTooltip().getTextStyle().setColor(Colors.COLOR_WHITE);
  		this.attrs.put("desarrollo", stack.toJson());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  private void toLoadMovimientos() {
    Map<String, Object> params = new HashMap<>();
    try {      
      params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());      
      Entity nomina= (Entity)DaoFactory.getInstance().toEntity("VistaNominaDto", "periodo", params);
      params.put("inicio", Fecha.formatear(Fecha.FECHA_ESTANDAR, nomina.toDate("inicio")));      
      params.put("termino", Fecha.formatear(Fecha.FECHA_ESTANDAR, nomina.toDate("termino")));
      this.pivoteDesarrollo = nomina.toLong("idNomina");
      this.pivoteContratista= nomina.toLong("idNomina");
      this.attrs.put("nomina", nomina.toLong("ejercicio")+ "-"+ nomina.toLong("orden"));
      this.attrs.put("inicio", Fecha.formatear(Fecha.FECHA_CORTA, nomina.toDate("inicio")));
      this.attrs.put("termino", Fecha.formatear(Fecha.FECHA_CORTA, nomina.toDate("termino")));
      Datas datas= new Datas("Movimientos", DaoFactory.getInstance().toEntitySet("VistaSeguimientoDto", "movimientos", params));
      Double personal= 0D;
      for (Entity item: datas.getData()) {
        personal+= item.toDouble("value");
      } // for
      DonutModel donut= new DonutModel("Movimientos del personal", "65%", "30%", new Title(), datas);
      donut.getLegend().setY("80%");
			donut.toCustomFormatLabel("function (params) {return jsEcharts.format(params, 'percent');}");
      donut.getSeries().get(0).setCenter(Arrays.asList(new String[] {"50%", "45%"}));
      donut.toCustomDonut(String.valueOf(personal.intValue()), "25px", "40%");
      donut.setColor(Arrays.asList(new String[] {"#008000", "#ff0000", "#ffb300", "#607d8b"}));
      this.attrs.put("movimientos", donut.toJson());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  private void toLoadContratistas() {
    try {      
			Stacked stacked = new Stacked(DaoFactory.getInstance().toEntitySet("VistaSeguimientoDto", "contratistas", Collections.EMPTY_MAP));
  		StackModel stack= new StackModel(new Title(), stacked);
      stack.remove();
      stack.toCustomFontSize(14);
      stack.getLegend().setY("85%");
      stack.getxAxis().getAxisLabel().getTextStyle().setFontSize(12);
			stack.getxAxis().getAxisLabel().setFormatter("function(value) {return jsEcharts.label(value);}");
			stack.toCustomFormatLabel("function (params) {return jsEcharts.format(params, 'integer');}");
			stack.getTooltip().setFormatter("function (params) {return jsEcharts.tooltip(params, 'integer');}");
      stack.getTooltip().getTextStyle().setColor(Colors.COLOR_WHITE);
  		this.attrs.put("contratista", stack.toJson());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

  private void toLoadSueldos() {
    Map<String, Object> params = new HashMap<>();
    try {      
      params.put("nominas", this.nominasDesarrollo.toString());
      if(this.desarrollo== null || this.desarrollo.isEmpty())
        params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      else {
        String sinDesarrollo= null;
        StringBuilder sb= new StringBuilder("tc_keet_desarrollos.id_desarrollo in (");
        for (Entity item : this.desarrollo) {
          sb.append(item.getKey()).append(", ");
          if(item.getKey().equals(0L))
            sinDesarrollo= " or (tc_keet_desarrollos.id_desarrollo is null))";
        } // for
        sb.delete(sb.length()- 2, sb.length()).append(")");
        params.put(Constantes.SQL_CONDICION, Cadena.isVacio(sinDesarrollo)? sb.toString(): "(".concat(sb.toString()).concat(sinDesarrollo));
      } // else  
			Multiple multiple= new Multiple(DaoFactory.getInstance().toEntitySet("VistaSeguimientoDto", "sueldos", params));
      if(multiple.getData()!= null && !multiple.getData().isEmpty()) {
        BarModel sueldos = new BarModel(new Title(), multiple);
        sueldos.remove();
        sueldos.toCustomFontSize(14);
        sueldos.getLegend().setY("85%");
        sueldos.getxAxis().getAxisLabel().getTextStyle().setFontSize(12);
  			sueldos.getxAxis().getAxisLabel().setFormatter("function(value) {return jsEcharts.label(value);}");
        sueldos.toCustomFormatLabel("function (params) {return jsEcharts.format(params, 'integer');}");
        sueldos.getTooltip().setFormatter("function (params) {return jsEcharts.tooltip(params, 'integer');}");
        sueldos.getTooltip().getTextStyle().setColor(Colors.COLOR_WHITE);
        this.attrs.put("sueldos", sueldos.toJson());
      } // if
      else {
        JsfBase.addMessage("Informativo", "Ya no hay mas nóminas !");      
        this.attrs.put("sueldos", "{}");
      }  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally 
  }
  
  private void toLoadDestajos() {
    Map<String, Object> params = new HashMap<>();
    try {      
      params.put("nominas", this.nominasContratista.toString());
      if(this.contratista== null || this.contratista.isEmpty())
        params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      else {
        StringBuilder sb    = new StringBuilder("tr_mantic_empresa_personal.id_empresa_persona in (");
        for (Entity item : this.contratista) {
          sb.append(item.getKey()).append(", ");
        } // for
        sb.delete(sb.length()- 2, sb.length()).append(")");
        params.put(Constantes.SQL_CONDICION, sb.toString());
      } // else  
			Multiple multiple= new Multiple(DaoFactory.getInstance().toEntitySet("VistaSeguimientoDto", "destajos", params));
      if(multiple.getData()!= null && !multiple.getData().isEmpty()) {
        BarModel destajos= new BarModel(new Title(), multiple);
        destajos.remove();
        destajos.toCustomFontSize(14);
        destajos.getLegend().setY("85%");
        destajos.getxAxis().getAxisLabel().getTextStyle().setFontSize(12);
        destajos.getxAxis().getAxisLabel().setFormatter("function(value) {return jsEcharts.label(value);}");
        destajos.toCustomFormatLabel("function (params) {return jsEcharts.format(params, 'integer');}");
        destajos.getTooltip().setFormatter("function (params) {return jsEcharts.tooltip(params, 'integer');}");
        destajos.getTooltip().getTextStyle().setColor(Colors.COLOR_WHITE);
        this.attrs.put("destajos", destajos.toJson());
      } // if  
      else {
        JsfBase.addMessage("Informativo", "Ya no hay mas nóminas !");      
        this.attrs.put("destajos", "{}");
      }  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally 
  }

  private void toLoadDepartamentos() {
    try {      
			Simple simple= new Simple("Personal", DaoFactory.getInstance().toEntitySet("VistaSeguimientoDto", "departamento", Collections.EMPTY_MAP));
      BarModel departamento= new BarModel(new Title(), simple);
      departamento.remove();
      departamento.toCustomFontSize(14);
      departamento.setLegend(null);
      departamento.getxAxis().getAxisLabel().getTextStyle().setFontSize(12);
      departamento.getxAxis().getAxisLabel().setFormatter("function(value) {return jsEcharts.label(value);}");
      departamento.toCustomFormatLabel("function (params) {return jsEcharts.format(params, 'integer');}");
      departamento.getTooltip().setFormatter("function (params) {return jsEcharts.tooltip(params, 'integer');}");
      departamento.getTooltip().getTextStyle().setColor(Colors.COLOR_WHITE);
      this.attrs.put("departamento", departamento.toJson());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

  private void toLoadPagado(Map<String, Object> params) {
    try {      
			Simple simple= new Simple("Contratista", DaoFactory.getInstance().toEntitySet("VistaSeguimientoDto", "pagado", params));
      if(simple.getData()!= null && !simple.getData().isEmpty()) {
        BarModel departamento= new BarModel(new Title(), simple);
        departamento.remove();
        departamento.toCustomFontSize(14);
        departamento.setLegend(null);
        departamento.getxAxis().getAxisLabel().getTextStyle().setFontSize(12);
        departamento.getxAxis().getAxisLabel().setFormatter("function(value) {return jsEcharts.label(value);}");
        departamento.toCustomFormatLabel("function (params) {return jsEcharts.format(params, 'integer');}");
        departamento.getTooltip().setFormatter("function (params) {return jsEcharts.tooltip(params, 'integer');}");
        departamento.getTooltip().getTextStyle().setColor(Colors.COLOR_WHITE);
        this.attrs.put("pagado", departamento.toJson());
      } // else  
      else {
        JsfBase.addMessage("Informativo", "No hay nómina de contratistas !");      
        this.attrs.put("pagado", "{}");
      }  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

  private void toLoadNominas(ESecuencia token, String type) {
    Map<String, Object> params = new HashMap<>();
    try {      
      params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      switch(type) {
        case "desarrollo":
          params.put("idNomina", this.pivoteDesarrollo);
          this.nominasDesarrollo.delete(0, this.nominasDesarrollo.length());
          break;
        case "contratista":
          params.put("idNomina", this.pivoteContratista);
          this.nominasContratista.delete(0, this.nominasContratista.length());
          break;
        default:
          params.put("idNomina", this.pivoteDesarrollo);
          this.nominasDesarrollo.delete(0, this.nominasDesarrollo.length());
          break;
      } // switch
      params.put("equals", token.getOperador());
      params.put("orden", token.getOrden());
      List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaNominaDto", "secuencia", params);
      if(items== null || items.isEmpty() || ESecuencia.MAYOR.equals(token)) {
        if(items== null || items.isEmpty())
          JsfBase.addMessage("Informativo", "Ya no hay mas nóminas !");      
        else
          params.put("idNomina", items.get(0).toLong("idNomina"));
        params.put("equals", ESecuencia.IGUAL.getOperador());
        params.put("orden", ESecuencia.IGUAL.getOrden());
        items= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaNominaDto", "secuencia", params);
      } // if
      int count= 0;
      for (Entity item : items) {
        if(count== 0) {
          this.attrs.put("pivote".concat(Cadena.letraCapital(type)), item.toString("nomina"));          
          if(Cadena.isVacio(type)) {
            this.pivoteDesarrollo = item.toLong("idNomina");
            this.pivoteContratista= item.toLong("idNomina");
            this.attrs.put("pivoteDesarrollo", item.toString("nomina"));
            this.attrs.put("pivoteContratista", item.toString("nomina"));
          } // if
          else {
            switch(type) {
              case "desarrollo":
                this.pivoteDesarrollo= item.toLong("idNomina");
                break;
              case "contratista":
                this.pivoteContratista= item.toLong("idNomina");
                break;
            } // switch
          } // else
        } // if  
        switch(type) {
          case "desarrollo":
            this.nominasDesarrollo.append(item.toLong("idNomina")).append(count< TOP_NOMINA- 1? ",": "");
            break;
          case "contratista":
            this.nominasContratista.append(item.toLong("idNomina")).append(count< TOP_NOMINA- 1? ",": "");
            break;
          default:
            this.nominasDesarrollo.append(item.toLong("idNomina")).append(count< TOP_NOMINA- 1? ",": "");
            this.nominasContratista.append(item.toLong("idNomina")).append(count< TOP_NOMINA- 1? ",": "");
            break;
        } // switch
        count++;
        if(count>= TOP_NOMINA)
          break;
      } // for
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  public void doNextNomina(String type) {
    this.toLoadNominas(ESecuencia.MAYOR, type);  
    this.toUpdateScroll(type);
    UIBackingUtilities.execute("jsEcharts.refresh({items: {json: {pivoteDesarrollo: '"+ ((String)this.attrs.get("pivoteDesarrollo"))+ "', pivoteContratista: '"+ ((String)this.attrs.get("pivoteContratista"))+ "'}}});");
  }
  
  public void doBackNomina(String type) {
    this.toLoadNominas(ESecuencia.MENOR, type);  
    this.toUpdateScroll(type);
    UIBackingUtilities.execute("jsEcharts.refresh({items: {json: {pivoteDesarrollo: '"+ ((String)this.attrs.get("pivoteDesarrollo"))+ "', pivoteContratista: '"+ ((String)this.attrs.get("pivoteContratista"))+ "'}}});");
  }

  public void toUpdateScroll(String type) {
    switch(type) {
      case "desarrollo":
        this.toLoadSueldos();
        UIBackingUtilities.execute("jsEcharts.update('sueldos', {group:'00', json:".concat((String)this.attrs.get("sueldos")).concat("});"));
        break;
      case "contratista":
        this.toLoadDestajos();
        UIBackingUtilities.execute("jsEcharts.update('destajos', {group:'00', json:".concat((String)this.attrs.get("destajos")).concat("});"));
        break;
    } // swtich
  }
          
  public void doUpdateContratista() {
    try {      
      if(this.contratista!= null && !this.contratista.isEmpty()) {
        this.toLoadDestajos();
        UIBackingUtilities.execute("jsEcharts.update('destajos', {group:'00', json:".concat((String)this.attrs.get("destajos")).concat("});"));
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
          
  public void doUpdateDesarrollo() {
    try {      
      if(this.desarrollo!= null && !this.desarrollo.isEmpty()) {
        this.toLoadSueldos();
        UIBackingUtilities.execute("jsEcharts.update('sueldos', {group:'00', json:".concat((String)this.attrs.get("sueldos")).concat("});"));
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
      if("|desarrollo|contratista|destajos|departamento|sueldos|".indexOf(itemSelected.getChart())> 0) {
        columns = new ArrayList<>();
        columns.add(new Columna("fecha", EFormatoDinamicos.FECHA_CORTA));
        if("|destajos|departamento|".indexOf(itemSelected.getChart())> 0) 
          columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
        params = new HashMap<>();
        params.put(itemSelected.getChart(), itemSelected.getName());
        params.put("nomina", itemSelected.getSeriesName());
        if("|sueldos|".indexOf(itemSelected.getChart())> 0) {
          this.toLoadPagado(params);
          UIBackingUtilities.execute("jsEcharts.update('pagado', {group:'00', json:".concat((String)this.attrs.get("pagado")).concat("});"));
        } //
        else {
          List<Entity> items = (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaSeguimientoDto", "personal".concat(Cadena.letraCapital(itemSelected.getChart())), params);
          if(items!= null)
            UIBackingUtilities.toFormatEntitySet(items, columns);
          this.attrs.put("personal".concat(Cadena.letraCapital(itemSelected.getChart())), items);  
          UIBackingUtilities.update("tablaPersonal".concat(Cadena.letraCapital(itemSelected.getChart())));
        } // else  
        UIBackingUtilities.execute("onOffSwitchTable('"+ itemSelected.getChart()+ "', true);");
        if("|destajos|sueldos|".indexOf(itemSelected.getChart())> 0) 
          UIBackingUtilities.execute("jsEcharts.refresh({items: {json: {nombre"+ Cadena.letraCapital(itemSelected.getChart())+ ":'("+ itemSelected.getSeriesName()+ ") "+ itemSelected.getName()+ "'}}});");
        else
          UIBackingUtilities.execute("jsEcharts.refresh({items: {json: {nombre"+ Cadena.letraCapital(itemSelected.getChart())+ ":'"+ itemSelected.getName()+ "'}}});");
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
  
}
