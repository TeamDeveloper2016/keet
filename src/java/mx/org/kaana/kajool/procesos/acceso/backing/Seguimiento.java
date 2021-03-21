package mx.org.kaana.kajool.procesos.acceso.backing;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.procesos.comun.Comun;
import mx.org.kaana.libs.echarts.beans.Title;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.echarts.kind.DonutModel;
import mx.org.kaana.libs.echarts.kind.StackModel;
import mx.org.kaana.libs.echarts.model.Datas;
import mx.org.kaana.libs.echarts.model.Stacked;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.mozilla.javascript.edu.emory.mathcs.backport.java.util.Arrays;

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
	private String mes[] = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

  @PostConstruct
  @Override
  protected void init() {
    try {      
      this.attrs.put("hoy", Fecha.getHoyCorreo());
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
      this.toLoadPersonal();
      this.toLoadDesarrollos();
      this.toLoadMovimientos();
      this.toLoadContratistas();
    } // try
    catch (Exception e) {
      JsfBase.addMessageError(e);
    } // catch
  } // doLoad
  
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
			stack.toCustomFormatLabel("function (params) {return jsEcharts.format(params, 'integer');}");
			stack.getTooltip().setFormatter("function (params) {return jsEcharts.tooltip(params, 'integer');}");
  		this.attrs.put("desarrollos", stack.toJson());
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
      donut.setColor(Arrays.asList(new String[] {"#008000", "#ffff00", "#ffb300", "#607d8b"}));
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
			stack.toCustomFormatLabel("function (params) {return jsEcharts.format(params, 'integer');}");
			stack.getTooltip().setFormatter("function (params) {return jsEcharts.tooltip(params, 'integer');}");
  		this.attrs.put("contratistas", stack.toJson());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
}