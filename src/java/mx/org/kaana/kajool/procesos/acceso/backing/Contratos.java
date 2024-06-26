package mx.org.kaana.kajool.procesos.acceso.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.echarts.beans.Colors;
import mx.org.kaana.libs.echarts.beans.Title;
import mx.org.kaana.libs.echarts.json.ItemSelected;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.echarts.kind.StackModel;
import mx.org.kaana.libs.echarts.model.Stacked;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.formato.Periodo;
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
public class Contratos extends Respaldos implements Serializable {

  private static final long serialVersionUID= 5323749709626263802L;
  private static final Log LOG              = LogFactory.getLog(Contratos.class);
	protected static final String COORDENADA_CENTRAL= "21.8818,-102.291";

  private List<Entity> desarrollos;
  private List<Entity> totales;
  private Entity desarrollo;
  private List<Entity> nominas;
  private Entity nomina;
  private Entity contrato;
	private MapModel model;
  private LocalDate fechaPivote;
  private Integer residentePivote;
  private List<Entity> residentes;
	
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

  public List<Entity> getNominas() {
    return nominas;
  }

  public Entity getNomina() {
    return nomina;
  }

  public void setNomina(Entity nomina) {
    this.nomina = nomina;
  }

  public String getFechaPivote() {
    return Fecha.formatear(Fecha.DIA_FECHA, this.fechaPivote).toUpperCase();
  }

  @PostConstruct
  @Override
  protected void init() {
    try {      
      this.fechaPivote= LocalDate.now();
      this.residentePivote= 0;
      this.attrs.put("nombreResidentePivote", "");
      this.attrs.put("hoy", Fecha.getHoyCorreo());
      this.attrs.put("pathPivote", "/".concat((Configuracion.getInstance().getEtapaServidor().name().toLowerCase())).concat("/images/"));
      this.totales= new ArrayList<>();
      this.doLoad();
			if(JsfBase.isAdminEncuestaOrAdmin())
			  this.checkDownloadBackup();
      DaoFactory.getInstance().updateAll(TcKeetEstacionesDto.class, Collections.EMPTY_MAP, "cargos");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

  @Override
  public void doLoad() {
    try {
      this.toLoadResidentes();
      this.toLoadNombres();
      this.toLoadContratos();
      this.toLoadLotes();
      this.toLoadCoordenadas();
      this.toLoadHorarios();
      this.toLoadContratistas();
      this.toLoadNominaContratistas();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } // doLoad
  
  private void toLoadResidentes() {
    Map<String, Object> params= new HashMap<>();
    try {      
			params.put("idContratoLote", -1L);			
      params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      params.put(Constantes.SQL_CONDICION, "tr_mantic_empresa_personal.id_activo= 1");
      this.residentes= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaContratosDto", "residentesDisponible", params);
      if(this.residentes!= null && !this.residentes.isEmpty())
        this.attrs.put("nombreResidentePivote", this.residentes.get(this.residentePivote).toString("nombreCompleto"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally  
  }
  
  private void toLoadNombres() {
    List<Columna> columns     = new ArrayList<>();		
    Map<String, Object> params= new HashMap<>();
    try {      
      if(JsfBase.isResidente())
        params.put(Constantes.SQL_CONDICION, " and tc_keet_desarrollos.id_desarrollo in ("+ this.toLoadDesarrollosResidentes()+ ")");     
      else
        params.put(Constantes.SQL_CONDICION, " and tc_keet_desarrollos.id_desarrollo is not null");      
      this.desarrollos= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaSeguimientoDto", "nombresDesarrollos", params);
      if(this.desarrollos!= null && !this.desarrollos.isEmpty())
        this.desarrollo = this.desarrollos.get(0);
      params.put("idTipoNomina", "1");
      this.nominas= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaNominaDto", "ultima", params, 12L);
      if(this.nominas!= null && !this.nominas.isEmpty()) {
        columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));                  
        columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));  
        UIBackingUtilities.toFormatEntitySet(this.nominas, columns);
        this.nomina= this.nominas.get(this.nominas.size()> 1? 1: 0);
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
    
  protected void toLoadContratos() {
    Map<String, Object> params = new HashMap<>();
    try {      
      this.attrs.put("nombreDesarrollo", this.desarrollo!= null && this.desarrollo.containsKey("nombre")? (String)this.desarrollo.toString("nombre"): "xyz");
      params.put("desarrollo", this.attrs.get("nombreDesarrollo"));      
			Stacked multiple = new Stacked(this.toLoadLotesContratos(DaoFactory.getInstance().toEntitySet("VistaTableroDto", "contratados", params)));
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
        stack.toCustomColorSerie(Arrays.asList(new String[] {"#29c0c6"}));
        this.attrs.put("contratos", stack.toJson());
        this.contrato= multiple.getData().get(multiple.getData().size()- 1);
        this.toLoadGlobal();
      } // if
      else {
        // JsfBase.addMessage("Informativo", "No se tienen contratos para el desarrollo ["+ this.attrs.get("nombreDesarrollo")+ "] !");      
        this.attrs.put("contratos", "{}");
        this.attrs.put("global", "{}");
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
  
  private void toLoadContratistas() {
    Map<String, Object> params = new HashMap<>();
    try {      
      this.attrs.put("nombreNomina", this.nomina!= null && this.nomina.containsKey("nomina")? (String)this.nomina.toString("nomina"): "0000-0");
      if(this.nomina!= null && !this.nomina.isEmpty()) {
   		  params.put("loNuevo", this.nomina.toLong("idNominaEstatus")!= 4L && this.nomina.toLong("idNominaEstatus")!= 5L? "or tc_keet_contratos_destajos_contratistas.id_nomina is null": "");
        params.put("idNomina", this.nomina.toLong("idNomina"));      
        params.put("idDesarrollo", this.desarrollo!= null? this.desarrollo.getKey(): -1L);
        params.put("nomina", this.attrs.get("nombreNomina"));
        params.put("corte", "concat(tc_mantic_personas.nombres, ' ', tc_mantic_personas.paterno, ' ', tc_mantic_personas.materno)");
        Stacked multiple = new Stacked(DaoFactory.getInstance().toEntitySet("VistaTableroDto", "costoPersona", params));
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
          stack.toCustomColorSerie(Arrays.asList(new String[] {"#3a64df"}));
          this.attrs.put("contratistas", stack.toJson());
          Double total= 0D;
          for(Entity item: multiple.getData()) {
            total+= item.toDouble("value");
          } // for
          this.attrs.put("totalContratistas", "TOTAL: "+ Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, Numero.toRedondearSat(total)));
        } // if
        else {
          // JsfBase.addMessage("Informativo", "No se tienen n�mina para los contratistas ["+ this.attrs.get("nombreNomina")+ "] !");      
          this.attrs.put("contratistas", "{}");
          this.attrs.put("totalContratistas", "TOTAL: $ 0.00");
        } // else
        this.toLoadNominaContratoContratistas();
      } // if
      this.toLoadProveedores();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  private void toLoadNominaContratistas() {
    Map<String, Object> params = new HashMap<>();
    try {      
      if(this.nomina!= null && !this.nomina.isEmpty()) {
        params.put("idDesarrollo", this.desarrollo!= null? this.desarrollo.getKey(): -1L);
        params.put("contrato", this.attrs.get("nombreContrato")); 
        Stacked multiple = new Stacked(DaoFactory.getInstance().toEntitySet("VistaTableroDto", "costoContratoPersona", params));
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
          stack.toCustomColorSerie(Arrays.asList(new String[] {"#9969ca"}));
          this.attrs.put("nominaContratistas", stack.toJson());
          Double total= 0D;
          for(Entity item: multiple.getData()) {
            total+= item.toDouble("value");
          } // for
          // this.attrs.put("totalContratistas", "TOTAL: "+ Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, Numero.toRedondearSat(total)));
        } // if
        else {
          this.attrs.put("nominaContratistas", "{}");
          // this.attrs.put("totalContratistas", "TOTAL: $ 0.00");
        } // else
      } // if
      this.toLoadNominaProveedores();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  private void toLoadNominaContratoContratistas() {
    Map<String, Object> params = new HashMap<>();
    try {      
      params.put("loNuevo", this.nomina.toLong("idNominaEstatus")!= 4L && this.nomina.toLong("idNominaEstatus")!= 5L? "or tc_keet_contratos_destajos_contratistas.id_nomina is null": "");
      params.put("idNomina", this.nomina.toLong("idNomina"));      
      params.put("idDesarrollo", this.desarrollo!= null? this.desarrollo.getKey(): -1L);
      params.put("nomina", this.attrs.get("nombreNomina"));
      params.put("corte", "tc_keet_contratos.nombre");
      Stacked multiple = new Stacked(DaoFactory.getInstance().toEntitySet("VistaTableroDto", "costoPersona", params));
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
        stack.toCustomColorSerie(Arrays.asList(new String[] {"#82de7e"}));
        this.attrs.put("contratoContratistas", stack.toJson());
        Double total= 0D;
        for(Entity item: multiple.getData()) {
          total+= item.toDouble("value");
        } // for
        this.attrs.put("totalContratoContratistas", "TOTAL: "+ Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, Numero.toRedondearSat(total)));
      } // if
      else {
        // JsfBase.addMessage("Informativo", "No se tienen n�mina para los contratos ["+ this.attrs.get("nombreNomina")+ "] !");      
        this.attrs.put("contratoContratistas", "{}");
        this.attrs.put("totalContratoContratistas", "TOTAL: $ 0.00");
      } // else
      this.toLoadProveedores();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  private void toLoadProveedores() {
    Map<String, Object> params = new HashMap<>();
    try {      
      if(this.nomina!= null && !this.nomina.isEmpty()) {
   		  params.put("loNuevo", this.nomina.toLong("idNominaEstatus")!= 4L && this.nomina.toLong("idNominaEstatus")!= 5L? "or tc_keet_contratos_destajos_proveedores.id_nomina is null": "");
        params.put("idNomina", this.nomina.toLong("idNomina"));      
        params.put("idDesarrollo", this.desarrollo!= null? this.desarrollo.getKey(): -1L);
        params.put("nomina", this.attrs.get("nombreNomina"));
        params.put("corte", "tc_mantic_proveedores.razon_social");
        Stacked multiple = new Stacked(DaoFactory.getInstance().toEntitySet("VistaTableroDto", "costoProveedor", params));
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
          stack.toCustomColorSerie(Arrays.asList(new String[] {"#82de7e"}));
          //stack.toCustomColorSerie(Arrays.asList(new String[] {"#3a64df"}));
          this.attrs.put("proveedores", stack.toJson());
          Double total= 0D;
          for(Entity item: multiple.getData()) {
            total+= item.toDouble("value");
          } // for
          this.attrs.put("totalProveedores", "TOTAL: "+ Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, Numero.toRedondearSat(total)));
        } // if
        else {
          // JsfBase.addMessage("Informativo", "No se tienen n�mina para sub-contratistas ["+ this.attrs.get("nombreNomina")+ "] !");      
          this.attrs.put("proveedores", "{}");
          this.attrs.put("totalProveedores", "TOTAL: $ 0.00");
        } // else
        this.toLoadNominaContratoProveedores();
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  private void toLoadNominaProveedores() {
    Map<String, Object> params = new HashMap<>();
    try {      
      if(this.nomina!= null && !this.nomina.isEmpty()) {
        params.put("idDesarrollo", this.desarrollo!= null? this.desarrollo.getKey(): -1L);
        params.put("contrato", this.attrs.get("nombreContrato")); 
        Stacked multiple = new Stacked(DaoFactory.getInstance().toEntitySet("VistaTableroDto", "costoContratoProveedor", params));
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
          stack.toCustomColorSerie(Arrays.asList(new String[] {"#9969ca"}));
          this.attrs.put("nominaProveedores", stack.toJson());
          Double total= 0D;
          for(Entity item: multiple.getData()) {
            total+= item.toDouble("value");
          } // for
          // this.attrs.put("totalProveedores", "TOTAL: "+ Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, Numero.toRedondearSat(total)));
        } // if
        else {
          this.attrs.put("nominaProveedores", "{}");
          // this.attrs.put("totalProveedores", "TOTAL: $ 0.00");
        } // else
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
  private void toLoadNominaContratoProveedores() {
    Map<String, Object> params = new HashMap<>();
    try {      
      params.put("loNuevo", this.nomina.toLong("idNominaEstatus")!= 4L && this.nomina.toLong("idNominaEstatus")!= 5L? "or tc_keet_contratos_destajos_proveedores.id_nomina is null": "");
      params.put("idNomina", this.nomina.toLong("idNomina"));      
      params.put("idDesarrollo", this.desarrollo!= null? this.desarrollo.getKey(): -1L);
      params.put("nomina", this.attrs.get("nombreNomina"));
      params.put("corte", "tc_keet_contratos.nombre");
      Stacked multiple = new Stacked(DaoFactory.getInstance().toEntitySet("VistaTableroDto", "costoProveedor", params));
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
        stack.toCustomColorSerie(Arrays.asList(new String[] {"#f4f35c"}));
        this.attrs.put("contratoProveedores", stack.toJson());
        Double total= 0D;
        for(Entity item: multiple.getData()) {
          total+= item.toDouble("value");
        } // for
        this.attrs.put("totalContratoProveedores", "TOTAL: "+ Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, Numero.toRedondearSat(total)));
      } // if
      else {
        // JsfBase.addMessage("Informativo", "No se tienen n�mina para sub-contratistas ["+ this.attrs.get("nombreNomina")+ "] !");      
        this.attrs.put("contratoProveedores", "{}");
        this.attrs.put("totalContratoProveedores", "TOTAL: $ 0.00");
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
  
  protected void toLoadGlobal() {
    try {      
			Stacked multiple = new Stacked(this.totales);
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
        stack.toCustomColorSerie(Arrays.asList(new String[] {"#f68c6b"}));
        this.attrs.put("global", stack.toJson());
      } // if
      else {
        // JsfBase.addMessage("Informativo", "No se tienen lotes para el desarrollo ["+ this.attrs.get("nombreDesarrollo")+ "] !");      
        this.attrs.put("global", "{}");
      } // else
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  protected void toLoadLotes() {
    Map<String, Object> params = new HashMap<>();
    try {      
      if(this.contrato!= null && !this.contrato.isEmpty()) {
        this.attrs.put("nombreContrato", (String)this.contrato.toString("serie"));
        String clave= Cadena.rellenar(this.contrato.toLong("idEmpresa").toString(), 3, '0', true)+ Cadena.rellenar(this.contrato.toLong("ejercicio").toString(), 4, '0', true)+ Cadena.rellenar(this.contrato.toLong("orden").toString(), 3, '0', true);
        params.put("clave", clave);     
        Stacked multiple = new Stacked(this.toLoadLotesPorcentajes(DaoFactory.getInstance().toEntitySet("VistaTableroDto", "avance", params)));
        if(multiple.getData()!= null && !multiple.getData().isEmpty()) {
          StackModel stack= new StackModel(new Title(), multiple);
          stack.remove();
          stack.toCustomFontSize(14);
          stack.getLegend().setY("85%");
          stack.getxAxis().getAxisLabel().getTextStyle().setFontSize(12);
          stack.getxAxis().getAxisLabel().setFormatter("function(value) {return jsEcharts.label(value);}");
          stack.toCustomFormatLabel("function (params) {return jsEcharts.format(params, 'one-decimal');}");
          stack.getTooltip().setFormatter("function (params) {return jsEcharts.tooltip(params, 'one-decimal');}");
          stack.getTooltip().getTextStyle().setColor(Colors.COLOR_WHITE);
          stack.toCustomUniqueColorTopTotal("#000000");
          stack.toCustomColorSerie(Arrays.asList(new String[] {"#c3f05f"}));
          this.attrs.put("lotes", stack.toJson());
        } // if
        else {
          // JsfBase.addMessage("Informativo", "No se tienen lotes para el contrato ["+ this.attrs.get("nombreContrato")+ "] !");      
          this.attrs.put("lotes", "{}");
        } // else
      } // if
      else {
        // JsfBase.addMessage("Informativo", "No se tienen lotes para el contrato ["+ this.attrs.get("nombreContrato")+ "] !");      
        this.attrs.put("nombreContrato", "");
        this.attrs.put("lotes", "{}");
      } // else
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  private void toLoadHorarios() {
    Map<String, Object> params = new HashMap<>();
    try {      
      if(this.contrato!= null && !this.contrato.isEmpty()) {
//        params.put("fecha", Fecha.formatear(Fecha.FECHA_ESTANDAR, this.fechaPivote));   
//        if(JsfBase.isResidente())
//          params.put(Constantes.SQL_CONDICION, " and tc_janal_usuarios.id_usuario= "+ JsfBase.getIdUsuario());     
//        else
//          params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);      
        params.put("desarrollo", this.contrato.toString("desarrollo"));  
        if(this.residentes!= null && !this.residentes.isEmpty())
          if(JsfBase.isResidente())
            params.put("idPersona", JsfBase.getIdUsuario());
          else
            params.put("idPersona", this.residentes.get(this.residentePivote).toLong("idPersona"));
        else 
          params.put("idPersona", JsfBase.getIdUsuario());
        Periodo periodo= new Periodo();
        periodo.addDias(-8);
        params.put("fecha", periodo.toString());   
        // Stacked multiple = new Stacked(this.toLoadLotesPorcentajes(DaoFactory.getInstance().toEntitySet("VistaTableroDto", "horarios", params)));
        Stacked multiple = new Stacked(DaoFactory.getInstance().toEntitySet("VistaTableroDto", "periodo", params));
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
          stack.toCustomColorSerie(Arrays.asList(new String[] {"#ed4958"}));
          this.attrs.put("residentes", stack.toJson());
        } // if
        else {
          // JsfBase.addMessage("Informativo", "No se realizaron registro de avance ["+ this.getFechaPivote()+ "] !");      
          this.attrs.put("residentes", "{}");
        } // else
      } // if
      else {
        // JsfBase.addMessage("Informativo", "No se realizaron registro de avance ["+ this.getFechaPivote()+ "] !");      
        this.attrs.put("nombreContrato", "");
        this.attrs.put("residentes", "{}");
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
        UIBackingUtilities.execute("jsEcharts.update('lotes', {group:'00', json:".concat((String)this.attrs.get("lotes")).concat("});"));
        this.toLoadContratistas();
        this.toLoadNominaContratistas();
        UIBackingUtilities.execute("jsEcharts.update('nominaContratistas', {group:'00', json:".concat((String)this.attrs.get("nominaContratistas")).concat("});"));
        UIBackingUtilities.execute("jsEcharts.update('nominaProveedores', {group:'00', json:".concat((String)this.attrs.get("nominaProveedores")).concat("});"));
        UIBackingUtilities.execute("jsEcharts.refresh({items: {json: {nombreDesarrollo:'"+ this.attrs.get("nombreDesarrollo")+ "', nombreContrato:'"+ this.attrs.get("nombreContrato")+ "', totalProveedores: '"+ this.attrs.get("totalProveedores")+ "', totalSubContratistas: '"+ this.attrs.get("totalSubContratistas")+ "'}}});");
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
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {  
      switch(itemSelected.getChart()) {
        case "contratos": 
          params.put("desarrollo", this.contrato.toString("desarrollo"));
          params.put("contrato", itemSelected.getName());
          params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
          Entity tmp= (Entity)DaoFactory.getInstance().toEntity("VistaTableroDto", "contrato", params);
          if(tmp!= null) {
            this.contrato= tmp;
            columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
            columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
            UIBackingUtilities.toFormatEntity(this.contrato, columns);
            this.toLoadLotes();
            this.toLoadCoordenadas();
          } // if  
//          else
//            JsfBase.addMessage("Informativo", "No se tienen lotes para el contrato !");      
          UIBackingUtilities.update("mapa");
          UIBackingUtilities.execute("jsEcharts.update('lotes', {group:'00', json:".concat((String)this.attrs.get("lotes")).concat("});"));
          UIBackingUtilities.execute("jsEcharts.refresh({items: {json: {nombreContrato:'"+ this.attrs.get("nombreContrato")+ "'}}});");
          UIBackingUtilities.execute("onOffSwitchTable('mapa', false);");
          this.toLoadNominaContratistas();
          UIBackingUtilities.execute("jsEcharts.update('nominaContratistas', {group:'00', json:".concat((String)this.attrs.get("nominaContratistas")).concat("});"));
          UIBackingUtilities.execute("jsEcharts.update('nominaProveedores', {group:'00', json:".concat((String)this.attrs.get("nominaProveedores")).concat("});"));
          break;
        case "lotes": 
          params.put("desarrollo", this.contrato.toString("desarrollo"));
          params.put("contrato", this.contrato.toString("serie"));
          params.put(Constantes.SQL_CONDICION, "concat('M', tc_keet_contratos_lotes.manzana, 'L', tc_keet_contratos_lotes.lote)='"+ itemSelected.getName()+ "'");
          this.contrato= (Entity)DaoFactory.getInstance().toEntity("VistaTableroDto", "contrato", params);
          columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
          columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
          UIBackingUtilities.toFormatEntity(this.contrato, columns);
          String clave= Cadena.rellenar(this.contrato.toLong("idEmpresa").toString(), 3, '0', true)+ Cadena.rellenar(this.contrato.toLong("ejercicio").toString(), 4, '0', true)+ Cadena.rellenar(this.contrato.toLong("orden").toString(), 3, '0', true)+ Cadena.rellenar(this.contrato.toLong("secuencia").toString(), 3, '0', true);
          params.put("clave", clave);      
          params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);                
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
          UIBackingUtilities.execute("onOffSwitchTable('mapa', true);");
          UIBackingUtilities.execute("jsEcharts.refresh({items: {json: {nombre"+ Cadena.letraCapital(itemSelected.getChart())+ ":'"+ itemSelected.getName()+ "-"+ this.contrato.toLong("secuencia")+ "'}}});");
          if(!Objects.equals("local", itemSelected.getSeriesId())) {
            this.loadEvidencias(this.contrato);
            this.loadAvances(this.contrato);
            this.toChagenIconColor(this.contrato);
          } // if  
          break;
      } // switch
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
		Map<String, Object> params= new HashMap<>();
		List<Columna> columns     = new ArrayList<>();
    try {
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
			this.model= new DefaultMapModel();
			this.attrs.put("coordenadaCentral", COORDENADA_CENTRAL);
      if(this.contrato!= null && !this.contrato.isEmpty()) {
        params.put("desarrollo", this.contrato.toString("desarrollo"));
        params.put("contrato", this.contrato.toString("serie"));
        params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
        lotes= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaTableroDto", "georreferencia", params, Constantes.SQL_TODOS_REGISTROS);
        if(!lotes.isEmpty()) {
          UIBackingUtilities.toFormatEntitySet(lotes, columns);
          for(Entity lote: lotes) {
            icon  = this.toIcon(lote);
            marker= new Marker(new LatLng(Double.valueOf(lote.toString("latitud")), Double.valueOf(lote.toString("longitud"))), "Contrato: ".concat(lote.toString("nombre")).concat(", avance: ").concat(String.valueOf(lote.toInteger("porcentaje"))).concat("%, lote: ").concat(lote.toString("codigo")), lote, icon);
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
		String color             = "red";
		Map<String, Object>params= new HashMap<>();
		Entity estatus           = null;
    Integer porcentaje       = 0;
		try {
			imagen= JsfBase.getContext().concat("/javax.faces.resource/icon/mapa/").concat("janal-{color}-{orden}.png").concat(".jsf?ln=janal");
			params.put("clave", this.toClaveEstacion(mzaLote));
			estatus= (Entity) DaoFactory.getInstance().toEntity("VistaGeoreferenciaLotesDto", "estatusManzanaLote", params);
			if(estatus.toString("total")!= null) {
        porcentaje= new Integer(String.valueOf((estatus.toLong("terminado") * 100)/estatus.toLong("total")));
        /* AQUI COLOCAR LOS COLORES BASADOS EN EL PORCENTAJE DE AVANCE */
				this.attrs.put("porcentaje", porcentaje);
        if(porcentaje== 0)
     			color= "red";
        else  
          if(porcentaje> 0 && porcentaje<= 20)
            color= "cyan";
          else
            if(porcentaje>= 21 && porcentaje<= 80)
              color= "orange";
            else
              if(porcentaje>= 81 && porcentaje<= 99)
                color= "yellow";
              else
                color= "green"; 
			} // if	
			params.clear();
			params.put("color", color);
			params.put("orden", mzaLote.toString("orden"));
      mzaLote.put("color", new Value("color", color));
      mzaLote.put("porcentaje", new Value("porcentaje", porcentaje));
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
			this.loadAvances((Entity) marker.getData());
      this.toChagenIconColor((Entity) marker.getData());
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
		List<Columna> columns    = new ArrayList<>();
		Map<String, Object>params= new HashMap<>();
		try {
			this.attrs.put("loteSeleccionado", seleccionado);
      columns.add(new Columna("nombrePersona", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombreUsuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("observaciones", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));						
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
        if(dns.contains(JsfBase.getContext()))
				  url= dns.substring(0, dns.indexOf(JsfBase.getContext())).concat(this.attrs.get("pathPivote").toString()).concat(importado.toString("ruta")).concat(importado.toString("nombre").startsWith("sin-foto")? importado.toString("nombre"): importado.toString("archivo"));
        else
				  url= dns.concat(this.attrs.get("pathPivote").toString()).concat(importado.toString("ruta")).concat(importado.toString("nombre").startsWith("sin-foto")? importado.toString("nombre"): importado.toString("archivo"));
				importado.put("url", new Value("url", url));
			} // for
			this.attrs.put("importados", importados);
      UIBackingUtilities.update("evidencias");
      UIBackingUtilities.execute("reloadEvidencias();");
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
			throw e;
		} // catch		
	} // doLoadFiles
	
	private void loadAvances(Entity seleccionado) {
		Map<String, Object>params= null;
    List<Columna> columns    = new ArrayList<>();				
    try {      			
			params = this.toPrepare(seleccionado);
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
		Map<String, Object> regresar= new HashMap<>();
		try {
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
			regresar.append(lote.toLong("ejercicio"));
			regresar.append(Cadena.rellenar(lote.toString("contrato"), 3, '0', true));
			regresar.append(Cadena.rellenar(lote.toString("secuencia"), 3, '0', true));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar.toString();
	} 

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

  protected String toLoadDesarrollosResidentes() {
    StringBuilder regresar    = new StringBuilder();
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idEmpresaPersona", JsfBase.getAutentifica().getEmpresa().getIdEmpresaPersonal());      
      List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaTableroDto", "residenteDesarrollo", params);
      if(items!= null && !items.isEmpty())
        for (Entity item : items) {
          regresar.append(item.toLong("idDesarrollo")).append(", ");
        } // for
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar.substring(0, regresar.length()- 2);
  }

  public List<Entity> toLoadLotesContratos(List<Entity> items) {
    Map<String, Entity> list= new HashMap<>();
    Entity index            = null;
    String nombre           = null;
    try {      
      if(items!= null && !items.isEmpty()) {
        items.remove(items.size()- 1);
        for (Entity item: items) {
          if(item.toString("serie")== null) {
            item.getValue("serie").setData(item.toString("desarrollo"));
            item.getValue("orden").setData(0L);
            item.getValue("clave").setData("");
            nombre= item.toString("desarrollo");
          } // if  
//          index= list.get(item.toString("serie"));
//          if(index== null) {
//            index= item.clone();
//            index.getValue("category").setData("CONTRATADOS");
//            list.put(item.toString("serie"), index);
//          } // if
//          else 
//            index.getValue("value").setData(item.toDouble("value")+ index.toDouble("value"));
        } // for
//        for (Entity item: list.values()) {
//          items.add(item);
//        } // for
        this.totales.clear();
        int count= 0;
        while(count< items.size()) {
          if(Objects.equals(items.get(count).toString("serie"), nombre)) {
            this.totales.add(items.get(count));
            items.remove(count);
          } // if
          else
            count++;
        } // while
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(list);
    } // finally
    return items;
  }
  
  public List<Entity> toLoadLotesCostoPorcentajes(List<Entity> items) {
    Map<String, Double> list = new HashMap<>();
    Map<String, Double> falta= new HashMap<>();
    Double index             = null;
    try {      
      if(items!= null && !items.isEmpty()) {
        for (Entity item: items) {
          if(Objects.equals(item.toLong("idEstacionEstatus"), 2L))
            falta.put(item.toString("serie"), item.toDouble("costo")- item.toDouble("value"));
          index= list.get(item.toString("serie"));
          if(index== null) 
            list.put(item.toString("serie"), item.toDouble("costo"));
          else 
            list.put(item.toString("serie"), index+ (item.toDouble("value")> item.toDouble("costo")? item.toDouble("value"): item.toDouble("costo")));
        } // for
        int count= 0;
        for (Entity item: items) {
          index= list.get(item.toString("serie"));
          if(index!= null) {
            Double saldo= 0D;
            if(falta.containsKey(item.toString("serie")) && Objects.equals(item.toLong("idEstacionEstatus"), 1L)) {
              saldo= falta.get(item.toString("serie"));
              falta.remove(item.toString("serie"));
            } // if  
            Double value= item.toDouble("value")+ saldo;
            item.getValue("value").setData(Numero.toRedondearSat(value* 100/ index));
          } // if
          count++;
        } // for
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(list);
      Methods.clean(falta);
    } // finally
    return items;
  }

  public List<Entity> toLoadLotesPorcentajes(List<Entity> items) {
    Map<String, Double> list= new HashMap<>();
    Double index            = null;
    try {      
      if(items!= null && !items.isEmpty()) {
        for (Entity item: items) {
          index= list.get(item.toString("serie"));
          if(index== null) 
            list.put(item.toString("serie"), item.toDouble("value"));
          else 
            list.put(item.toString("serie"), index+ item.toDouble("value"));
        } // for
        for (Entity item: items) {
          index= list.get(item.toString("serie"));
          if(index!= null) {
            Double value= item.toDouble("value");
            item.getValue("value").setData(Numero.toRedondearSat(value* 100/ index));
          } // if
        } // for
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(list);
    } // finally
    return items;
  }

  private void toMoveIndexResidente(Integer index) {
    if(this.residentes!= null && !this.residentes.isEmpty()) {
      if((this.residentePivote+ index)>= this.residentes.size())
        this.residentePivote= 0;
      else
        if((this.residentePivote+ index)< 0)
          this.residentePivote= this.residentes.size()- 1;
        else
          this.residentePivote+= index; 
      this.attrs.put("nombreResidentePivote", this.residentes.get(this.residentePivote).toString("nombreCompleto"));
    } // if  
  } 
  
  public void doNextNomina(String type) {
    // this.fechaPivote= this.fechaPivote.plusDays(1);
    this.toMoveIndexResidente(1);
    this.toLoadHorarios();
    // UIBackingUtilities.execute("jsEcharts.refresh({items: {json: {fechaPivote: '"+ (this.getFechaPivote())+ "'}}});");
    UIBackingUtilities.execute("jsEcharts.refresh({items: {json: {nombreResidentePivote: '"+ (this.attrs.get("nombreResidentePivote"))+ "'}}});");
    UIBackingUtilities.execute("jsEcharts.update('residentes', {group:'00', json:".concat((String)this.attrs.get("residentes")).concat("});"));
  }
  
  public void doBackNomina(String type) {
//    this.fechaPivote= this.fechaPivote.minusDays(1);
    this.toMoveIndexResidente(-1);
    this.toLoadHorarios();
//    UIBackingUtilities.execute("jsEcharts.refresh({items: {json: {fechaPivote: '"+ (this.getFechaPivote())+ "'}}});");
    UIBackingUtilities.execute("jsEcharts.refresh({items: {json: {nombreResidentePivote: '"+ (this.attrs.get("nombreResidentePivote"))+ "'}}});");
    UIBackingUtilities.execute("jsEcharts.update('residentes', {group:'00', json:".concat((String)this.attrs.get("residentes")).concat("});"));
  }

  public void doUpdateNominaSelect(SelectEvent event) {  
    this.toUpdateNomina();
  }
  
  public void doUpdateNominaUnSelect(UnselectEvent event) {  
    this.toUpdateNomina();
  }
  
  private void toUpdateNomina() {
    try {      
      if(this.nomina!= null && !this.nomina.isEmpty()) { 
        this.toLoadContratistas();
        UIBackingUtilities.execute("jsEcharts.update('contratistas', {group:'00', json:".concat((String)this.attrs.get("contratistas")).concat("});"));
        UIBackingUtilities.execute("jsEcharts.update('proveedores', {group:'00', json:".concat((String)this.attrs.get("proveedores")).concat("});"));
        UIBackingUtilities.execute("jsEcharts.update('contratoContratistas', {group:'00', json:".concat((String)this.attrs.get("contratoContratistas")).concat("});"));
        UIBackingUtilities.execute("jsEcharts.update('contratoProveedores', {group:'00', json:".concat((String)this.attrs.get("contratoProveedores")).concat("});"));
        UIBackingUtilities.execute("jsEcharts.refresh({items: {json: {nombreNomina:'"+ this.attrs.get("nombreNomina")+ "', totalContratistas: '"+ this.attrs.get("totalContratistas")+ "', totalProveedores: '"+ this.attrs.get("totalProveedores")+ "', totalContratoContratistas: '"+ this.attrs.get("totalContratoContratistas")+ "', totalContratoProveedores: '"+ this.attrs.get("totalContratoProveedores")+ "'}}});");
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

	public String doColor(Entity row) {
		return row.toString("codigo").startsWith("#")? "janal-tr-diferencias": "";
	}

  private void toChagenIconColor(Entity entity) throws Exception {
    String icon  = null;
    Entity marker= null;
		String imagen= JsfBase.getContext().concat("/javax.faces.resource/icon/mapa/").concat("janal-{color}-{orden}.png").concat(".jsf?ln=janal");
    Map<String, Object> params= new HashMap<>();
    try {      
      for (Marker item: this.model.getMarkers()) {
        marker= (Entity)item.getData();
  			params.put("color", marker.toString("color"));
	  		params.put("orden", marker.toLong("orden"));
        if(Objects.equals(entity.toString("codigo"), marker.toString("codigo"))) {
  	  		params.put("color", "blue");
          icon= Cadena.replaceParams(imagen, params);
          this.attrs.put("porcentaje", marker.toInteger("porcentaje"));
          this.attrs.put("coordenadaCentral", String.valueOf(item.getLatlng().getLat()).concat(",").concat(String.valueOf(item.getLatlng().getLng())));          
        } // if
        else 
          icon= Cadena.replaceParams(imagen, params);        
        item.setIcon(icon);
      } // for
      UIBackingUtilities.update("mapa");
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
 
  protected void doLoadDetalle(Long idDesarrollo, String manzana, String lote, Long idEstado) {
    List<Columna> columns     = new ArrayList<>();    
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idDesarrollo", idDesarrollo);
      params.put("manzana", manzana);
      params.put("lote", lote);
      this.contrato= (Entity)DaoFactory.getInstance().toEntity("VistaSeguimientoDto", "unico", params);
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
      UIBackingUtilities.toFormatEntity(this.contrato, columns);
      String clave= Cadena.rellenar(this.contrato.toLong("idEmpresa").toString(), 3, '0', true)+ Cadena.rellenar(this.contrato.toLong("ejercicio").toString(), 4, '0', true)+ Cadena.rellenar(this.contrato.toLong("orden").toString(), 3, '0', true)+ Cadena.rellenar(this.contrato.toLong("secuencia").toString(), 3, '0', true);
      params.put("clave", clave);      
      switch(idEstado.intValue()) {
        case 1:
          params.put(Constantes.SQL_CONDICION, "tc_keet_estaciones.id_estacion_estatus in (2, 3)");
          break;
        case 2:
          params.put(Constantes.SQL_CONDICION, "tc_keet_estaciones.id_estacion_estatus in (1, 2)");
          break;
        default:
          params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
          break;
      } // switch
      columns.clear();
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
      List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaTableroDto", "detalleLotes", params);
      if(items!= null)
        UIBackingUtilities.toFormatEntitySet(items, columns);
      this.attrs.put("tablaDetalleLotes", items);  
      this.toLoadPorcentaje(this.contrato);
      this.loadEvidencias(this.contrato);
      this.attrs.put("nombreLotes", "M".concat(manzana).concat("L").concat(lote));  
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
  
  private void toLoadPorcentaje(Entity lote) {
    Map<String, Object> params= new HashMap<>();
		Entity estatus            = null;
    String color              = "red";
    Integer porcentaje        = 0;
    try {      
			params.put("clave", this.toClaveEstacion(lote));
			estatus= (Entity) DaoFactory.getInstance().toEntity("VistaGeoreferenciaLotesDto", "estatusManzanaLote", params);
			if(estatus.toString("total")!= null) {
        porcentaje= new Integer(String.valueOf((estatus.toLong("terminado") * 100)/estatus.toLong("total")));
        if(porcentaje== 0)
     			color= "red";
        else  
          if(porcentaje> 0 && porcentaje<= 20)
            color= "cyan";
          else
            if(porcentaje>= 21 && porcentaje<= 80)
              color= "orange";
            else
              if(porcentaje>= 81 && porcentaje<= 99)
                color= "yellow";
              else
                color= "green"; 
      } // if       
			this.attrs.put("porcentaje", porcentaje);
			this.attrs.put("color", color);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
}
