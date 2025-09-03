package mx.org.kaana.keet.costos.backing;

import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import mx.org.kaana.kajool.procesos.reportes.beans.Modelo;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.archivo.Xls;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named(value = "keetCostosRetenciones")
@ViewScoped 
public class Retenciones extends IBaseFilter implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Retenciones.class);
  private static final long serialVersionUID= 8793667741599428332L;
  private static final String COLUMN_DATA_FILE_ESPECIAL= "EMPRESA,DESARROLLO,CLAVE,CONTRATO,VIVIENDAS,COSTO,CONSECUTIVO,TOTAL,NORMAL,EXTRAS,COBRADO,PORCENTAJE,RETENCIONES,POR COBRAR,REGISTRO";  
  
	private LocalDate fechaInicio;
	private LocalDate fechaTermino;
  private FormatLazyModel lazyDetalle;

  public LocalDate getFechaInicio() {
    return fechaInicio;
  }

  public void setFechaInicio(LocalDate fechaInicio) {
    this.fechaInicio = fechaInicio;
  }

  public LocalDate getFechaTermino() {
    return fechaTermino;
  }

  public void setFechaTermino(LocalDate fechaTermino) {
    this.fechaTermino = fechaTermino;
  }
	
  public FormatLazyModel getLazyDetalle() {
    return lazyDetalle;
  }
  
  public String getGeneral() {
    String costos       = Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("costo"));
    String facturas     = Numero.formatear(Numero.MILES_SIN_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("facturas"));
    String facturado    = Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("facturado"));
    String pagado       = Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("pagado"));
    String calculo      = Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("calculo"));
    String fondoGarantia= Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("fondoGarantia"));
    String retenciones  = Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("retenciones"));
    return "Contratos: <strong>"+ costos+ "</strong>  |  facturas: <strong> "+ facturas+ "</strong>  |  facturado: <strong> "+ facturado+ "</strong>  |  pagado: <strong> "+ pagado+ "</strong>  |  fondo garantía: <strong> "+ fondoGarantia+ "</strong>  |  retenciones: <strong> "+ retenciones+ "</strong>";  
  }
 
  public StreamedContent getEspecial() {
		StreamedContent regresar = null;
		Xls xls                  = null;
		Map<String, Object>params= this.toPrepare();
		String template          = "ESPECIAL";
		try {
      params.put("sortOrder", "order by tc_keet_desarrollos.id_desarrollo, tc_keet_contratos.clave, tt_keet_estimaciones.id_estimacion desc");
      String salida  = EFormatos.XLS.toPath().concat(Archivo.toFormatNameFile(template).concat(".")).concat(EFormatos.XLS.name().toLowerCase());
      String fileName= JsfBase.getRealPath("").concat(salida);
      xls= new Xls(fileName, new Modelo(params, "VistaCostosDto", "pagos", template), COLUMN_DATA_FILE_ESPECIAL);	
      if(xls.procesar()) {
        String contentType= EFormatos.XLS.getContent();
        InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(salida);  
        regresar          = new DefaultStreamedContent(stream, contentType, Archivo.toFormatNameFile(template).concat(".").concat(EFormatos.XLS.name().toLowerCase()));				
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
  
  @PostConstruct
  @Override
  protected void init() {
    Map<String, Object> params= new HashMap<>();
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.attrs.put("general", this.toEmptyTotales());
			this.toLoadCatalogos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // init
 
  @Override
  public void doLoad() {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= this.toPrepare();
    try {
      params.put("sortOrder", "order by tc_keet_desarrollos.id_desarrollo, tc_keet_contratos.clave desc");
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("desarrollo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("contrato", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("noViviendas", EFormatoDinamicos.MILES_SIN_DECIMALES));
      columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("facturas", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("facturado", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("pagado", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("calculo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("fondoGarantia", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("retenciones", EFormatoDinamicos.MILES_CON_DECIMALES));
      this.lazyModel = new FormatCustomLazy("VistaCostosDto", "garantias", params, columns);
      this.attrs.put("general", this.toTotales("VistaCostosDto", "fondos", params));
      UIBackingUtilities.resetDataTable();
      this.lazyDetalle= null;
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

	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb= new StringBuilder();
		StringBuilder sc= new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !Objects.equals(((UISelectEntity)this.attrs.get("idEmpresa")).getKey(), -1L))
  		sb.append("(tc_keet_contratos.id_empresa=").append(((UISelectEntity)this.attrs.get("idEmpresa")).getKey()).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && ((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()>= 1L)
  		sb.append("(tc_keet_desarrollos.id_desarrollo=").append(((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idContrato")) && ((UISelectEntity)this.attrs.get("idContrato")).getKey()>= 1L)
  		sb.append("(tc_keet_contratos.id_contrato=").append(((UISelectEntity)this.attrs.get("idContrato")).getKey()).append(") and ");
		if(!Cadena.isVacio(this.fechaInicio))
		  sc.append("(date_format(tc_keet_estimaciones.fecha_estimacion, '%Y%m%d')>= '").append(this.fechaInicio.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.fechaTermino))
		  sc.append("(date_format(tc_keet_estimaciones.fecha_estimacion, '%Y%m%d')<= '").append(this.fechaTermino.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("idEstatus")) && !Objects.equals(((UISelectEntity)this.attrs.get("idEstatus")).getKey(), -1L))
		  if(Objects.equals(((UISelectEntity)this.attrs.get("idEstatus")).getKey(), Constantes.TOP_OF_ITEMS))
  		  sb.append("(tt_keet_estimaciones.facturado- tt_keet_pagados.pagado- tt_keet_estimaciones_detalles.fondo_garantia- tt_keet_estimaciones_detalles.retenciones> 0) and ");
      else
  		  sb.append("(tc_keet_contratos.id_contrato_estatus= ").append(this.attrs.get("idEstatus")).append(") and ");
    regresar.put("limites", sc.length()== 0? Constantes.SQL_VERDADERO: sc.substring(0, sc.length()- 4));
    regresar.put(Constantes.SQL_CONDICION, sb.length()== 0? Constantes.SQL_VERDADERO: sb.substring(0, sb.length()- 4));
		return regresar;		
	}
	
	protected void toLoadCatalogos() throws Exception {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));			
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));
			columns.remove(0);
			this.doLoadDesarrollos();
      this.toLoadEstatus();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}
	
	public void doLoadDesarrollos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
		  params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa in (" + JsfBase.getAutentifica().getEmpresa().getSucursales() + ")");			
			params.put("operador", "<=");
			params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("desarrollos", (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave"));			
			this.attrs.put("idDesarrollo", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("desarrollos")));			
      this.doLoadContratos();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} 
	
  @Override
	public void doGlobalEvent(Boolean isViewException) {
		LOG.error("ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA ["+ isViewException+ "]");
	}
	
	public String doColor(Entity row) {
		return ""; // Objects.equals(row.toLong("partidas"), 0L)? "janal-tr-diferencias": "";
	} 

	public void doLoadContratos() {
		List<UISelectEntity> contratos= null;
		Map<String, Object>params     = new HashMap<>();
		try {
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			contratos= UIEntity.seleccione("VistaContratosDto", "desarrollos", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
			this.attrs.put("contratos", contratos);
			this.attrs.put("idContrato", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("contratos")));			
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} 

  private Entity toTotales(String proceso, String idXml, Map<String, Object> params) {
    Entity regresar= null;
    try {      
      regresar= (Entity)DaoFactory.getInstance().toEntity(proceso, idXml, params);
      if(Objects.equals(regresar, null) || regresar.isEmpty()) 
        regresar= this.toEmptyTotales();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    return regresar;
  }
  
  private Entity toEmptyTotales() {
    Entity regresar= new Entity(-1L);
    regresar.addField("costo", 0D);
    regresar.addField("facturas", 0D);
    regresar.addField("facturado", 0D);
    regresar.addField("pagado", 0D);
    regresar.addField("calculo", 0D);
    regresar.addField("fondo_garantia", 0D);
    regresar.addField("retenciones", 0D);
    return regresar;
  }

	private void toLoadEstatus() {
		Map<String, Object>params   = new HashMap<>();
		List<UISelectEntity> estatus= null;		
		try {
			params.put(Constantes.SQL_CONDICION, "id_contrato_estatus in (5, 6, 7, 8, 9, 10)");
			estatus= (List<UISelectEntity>)UIEntity.todos("TcKeetContratosEstatusDto", params, "nombre");			
			this.attrs.put("estatus", estatus);
      if(!Objects.equals(estatus, null) && !estatus.isEmpty()) {
        UISelectEntity item= new UISelectEntity(Constantes.TOP_OF_ITEMS);
        item.addField("nombre", "POR COBRAR");
        estatus.add(0, item);
			  this.attrs.put("idEstatus", UIBackingUtilities.toFirstKeySelectEntity(estatus));		
      } // if
      else 
        this.attrs.put("idEstatus", new UISelectEntity(-1L));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} 

  public void doView(Entity row) {
    try {
      this.attrs.put("seleccionado", row);
      this.doDetalle();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch      
  }
  
  public void doDetalle() {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= this.toPrepare();
    Entity row                = (Entity)this.attrs.get("seleccionado");
    try {
      params.put("idContrato", row.toLong("idContrato"));
      params.put("sortOrder", "order by tc_keet_estimaciones_detalles.id_tipo_retencion, tc_keet_estimaciones_detalles.registro desc");
      columns.add(new Columna("retencion", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estimado", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("facturar", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("porcentaje", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("fechaPago", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("vence", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));
      this.lazyDetalle= new FormatCustomLazy("VistaCostosDto", "retenciones", params, columns);
      UIBackingUtilities.resetDataTable("detalle");
      this.attrs.put("seleccionado", row);
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
  
}
