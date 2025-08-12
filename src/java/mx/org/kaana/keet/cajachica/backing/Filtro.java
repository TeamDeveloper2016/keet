package mx.org.kaana.keet.cajachica.backing;

import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDate;
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
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.procesos.reportes.beans.Modelo;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.keet.enums.EEstatusCajasChicas;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.keet.cajachica.reglas.Transaccion;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.archivo.Xls;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EReportes;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named(value = "keetCajaChicaFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID= 8793667741599428332L;	
	private static final String DATA_FILE     = "DESARROLLO,CONSECUTIVO,RESIDENTE,NOMBRE,IMPORTE,REGISTRO";

	private FormatLazyModel lazyModelGastos;
	private FormatLazyModel lazyModelMateriales;
	private LocalDate fechaInicio;
	private LocalDate fechaFin;
  protected Reporte reporte;

	public Reporte getReporte() {
		return reporte;
	}

	public FormatLazyModel getLazyModelGastos() {
		return lazyModelGastos;
	}	

	public FormatLazyModel getLazyModelMateriales() {
		return lazyModelMateriales;
	}
	
	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}
	
  public StreamedContent getGastos() {
		StreamedContent regresar= null;		
		Xls xls                 = null;
    String template         = "GS";
		try {
			String salida  = EFormatos.XLS.toPath().concat(Archivo.toFormatNameFile(template).concat(".")).concat(EFormatos.XLS.name().toLowerCase());
  		String fileName= JsfBase.getRealPath("").concat(salida);
      xls= new Xls(fileName, new Modelo(this.attrs, "VistaCierresCajasChicasDto", "exportar", template), DATA_FILE);	
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
    return regresar;		
	} 
  
  @PostConstruct
  @Override
  protected void init() {
    try {
			this.fechaInicio= LocalDate.of(Fecha.getAnioActual(), 1, 1);
      if(Objects.equals(Fecha.getMesActual(), 12))
			  this.fechaFin= LocalDate.of(Fecha.getAnioActual()+ 1, 1, 15);
      else
        this.fechaFin= LocalDate.of(Fecha.getAnioActual(), Fecha.getMesActual()+ 1, 15);
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());   
			this.attrs.put("pathPivote", (Configuracion.getInstance().getEtapaServidor().name().toLowerCase()).concat("/").concat("gastos").concat("/"));						
			this.loadCatalog();      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
 
	private void loadCatalog() {		
    try {			
			this.toLoadEmpresas();
			this.doLoadDesarrollos();
			this.toLoadEstatus();
			this.toLoadEjercicios();
    } // try
    catch (Exception e) {
      throw e;
    } // catch       
	} // loadCatalog
	
	private void toLoadEmpresas() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));			
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));			
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // loadEmpresas	
	
	public void doLoadDesarrollos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
//		UISelectEntity empresa    = null;
    try {
//			empresa= (UISelectEntity) this.attrs.get("idEmpresa");
//			if(empresa.getKey()>= 1L)
//        params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa=" + empresa.getKey());
//			else
			params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa in (" + JsfBase.getAutentifica().getEmpresa().getSucursales() + ")");			
			params.put("operador", "<=");
      params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("desarrollos", (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave"));			
			this.attrs.put("desarrollo", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("desarrollos")));			
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} 	
	
	private void toLoadEstatus() {		
		Map<String, Object>params    = new HashMap<>();
		List<UISelectItem> allEstatus= null;
		try {			
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			allEstatus= UISelect.seleccione("TcKeetCajasChicasCierresEstatusDto", "estatus", params, "nombre", EFormatoDinamicos.MAYUSCULAS);			
			this.attrs.put("allEstatus", allEstatus);
			this.attrs.put("estatus", UIBackingUtilities.toFirstKeySelectItem(allEstatus));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadEstatus
	
	private void toLoadEjercicios() {		
		List<UISelectItem> ejercicios= null;
		Map<String, Object>params    = new HashMap<>();
		try {						
			ejercicios= UISelect.build("TcKeetGastosDto", "ejercicios", Collections.EMPTY_MAP, "ejercicio", EFormatoDinamicos.MAYUSCULAS);			
			this.attrs.put("ejercicios", ejercicios);
			this.attrs.put("ejercicio", UIBackingUtilities.toFirstKeySelectItem(ejercicios));
      this.doLoadSemanas();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally {
			Methods.clean(params);
		} // finally	
	} 
  
	public void doLoadSemanas() {		
		List<UISelectItem> semanas= null;
		Map<String, Object>params = new HashMap<>();
		try {						
      params.put("ejercicio", this.attrs.get("ejercicio"));
			semanas= UISelect.build("VistaCierresCajasChicasDto", "semanas", params, "orden", EFormatoDinamicos.MAYUSCULAS);
      if(semanas== null)
			  semanas= new ArrayList<>();
			this.attrs.put("semanas", semanas);
			this.attrs.put("semana", UIBackingUtilities.toFirstKeySelectItem(semanas));
      this.fechaInicio= LocalDate.of(((Long)this.attrs.get("ejercicio")).intValue(), 1, 1);  
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally {
			Methods.clean(params);
		} // finally	
	} 
  
  @Override
  public void doLoad() {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= null;
    try {			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("inicial", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("gastado", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("disponible", EFormatoDinamicos.MILES_CON_DECIMALES));            
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_HORA_CORTA));      
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));      
      columns.add(new Columna("final", EFormatoDinamicos.FECHA_CORTA));      
			params= this.toPrepare();
      params.put("sortOrder", "order by tc_keet_desarrollos.nombres");
      this.lazyModel = new FormatCustomLazy("VistaCierresCajasChicasDto", params, columns);
			this.lazyModelGastos= null;
			this.lazyModelMateriales= null;
      UIBackingUtilities.resetDataTable();			
      UIBackingUtilities.resetDataTable("tablaGastos");			
      UIBackingUtilities.resetDataTable("tablaMateriales");			
			this.attrs.put("seleccionadoCaja", null);
			this.attrs.put("seleccionadoGasto", null);
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
	
	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb= new StringBuilder();						
		sb.append("(date_format(tc_keet_cajas_chicas_cierres.registro, '%Y%m%d')>= date_format('").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, this.fechaInicio)).append("', '%Y%m%d')) and ");			
		sb.append("(date_format(tc_keet_cajas_chicas_cierres.termino, '%Y%m%d')<= date_format('").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, this.fechaFin)).append("', '%Y%m%d')) and ");			
		if(!Cadena.isVacio(this.attrs.get("estatus")) && !this.attrs.get("estatus").toString().equals("-1"))
  		sb.append("(tc_keet_cajas_chicas_cierres.id_caja_chica_cierre_estatus= ").append(this.attrs.get("estatus")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !((UISelectEntity)this.attrs.get("idEmpresa")).getKey().equals(-1L))
		  sb.append("(tc_mantic_clientes.id_empresa= ").append(this.attrs.get("idEmpresa")).append(") and ");		
		if(!Cadena.isVacio(this.attrs.get("desarrollo")) && !((UISelectEntity)this.attrs.get("desarrollo")).getKey().equals(-1L))
		  sb.append("(tc_keet_desarrollos.id_desarrollo= ").append(((UISelectEntity)this.attrs.get("desarrollo")).getKey()).append(") and ");		
		if(!Cadena.isVacio(this.attrs.get("ejercicio")) && !this.attrs.get("ejercicio").equals("-1"))
		  sb.append("(tc_keet_cajas_chicas_cierres.ejercicio= '").append(this.attrs.get("ejercicio")).append("') and ");		
		if(!Cadena.isVacio(this.attrs.get("semana")) && !this.attrs.get("semana").equals("-1"))
		  sb.append("(tc_keet_nominas_periodos.id_nomina_periodo= ").append(this.attrs.get("semana")).append(") and ");		
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	} // toPrepare

  public void doGastos(Entity seleccionado) {
		Map<String, Object>params= new HashMap<>();
		List<Columna>columns     = new ArrayList<>();
		String dns               = null;
		String url               = null;
		try {
			dns= Configuracion.getInstance().getPropiedad("sistema.dns.".concat(Configuracion.getInstance().getEtapaServidor().name().toLowerCase()));			
      if(dns.contains(JsfBase.getContext()))
			  url= dns.substring(0, dns.indexOf(JsfBase.getContext())).concat("/").concat((String)this.attrs.get("pathPivote"));
      else
			  url= dns.concat((String)this.attrs.get("pathPivote"));
      columns.add(new Columna("residente", EFormatoDinamicos.MAYUSCULAS));      
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));      
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));      
			params.put("idCajaChicaCierre", seleccionado.getKey());
			params.put("pathImage", url);
			this.lazyModelGastos= new FormatCustomLazy("VistaCierresCajasChicasDto", "gastos", params, columns);
			this.lazyModelMateriales= null;
			UIBackingUtilities.resetDataTable("tablaGastos");			
      UIBackingUtilities.resetDataTable("tablaMateriales");			
			this.attrs.put("seleccionadoCaja", seleccionado);
			this.attrs.put("seleccionadoGasto", null);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
	} // doGastos
  
	public void doMateriales(Entity seleccionado) {
		Map<String, Object>params= new HashMap<>();
		List<Columna>columns     = new ArrayList<>();
		try {			
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));      
      columns.add(new Columna("codigo", EFormatoDinamicos.MAYUSCULAS));      
      columns.add(new Columna("cantidad", EFormatoDinamicos.NUMERO_SIN_DECIMALES));      
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));      
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));      
			params.put(Constantes.SQL_CONDICION, "tc_keet_gastos_detalles.id_gasto=" + seleccionado.getKey());
			this.lazyModelMateriales= new FormatCustomLazy("TcKeetGastosDetallesDto", "row", params, columns);
      UIBackingUtilities.resetDataTable("tablaMateriales");			
			this.attrs.put("seleccionadoGasto", seleccionado);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
	} // doGastos
	
  public String doCierreGlobal() {
		try {
			JsfBase.setFlashAttribute("retorno", "filtro");
	  } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
		} // catch
		return "pagar".concat(Constantes.REDIRECIONAR);    
  }
  
  public String doCierre() {
		Entity seleccionado= null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");
			JsfBase.setFlashAttribute("idCajaChicaCierre", seleccionado.getKey());
			JsfBase.setFlashAttribute("retorno", "filtro");
			JsfBase.setFlashAttribute("opcionResidente", EOpcionesResidente.CONSULTA_GASTO);
			JsfBase.setFlashAttribute("idDesarrollo", seleccionado.toLong("idDesarrollo"));
	  } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
		} // catch
		return "cierre".concat(Constantes.REDIRECIONAR);
	} // doAperturarCaja
  
	public String doAbono() {
		Entity seleccionado= null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");
			JsfBase.setFlashAttribute("idCajaChicaCierre", seleccionado.getKey());
			JsfBase.setFlashAttribute("retorno", "filtro");
			JsfBase.setFlashAttribute("opcionResidente", EOpcionesResidente.CONSULTA_GASTO);
			JsfBase.setFlashAttribute("idDesarrollo", seleccionado.toLong("idDesarrollo"));
	  } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
		} // catch
		return "abonar".concat(Constantes.REDIRECIONAR);
	} // doAperturarCaja
  
	public String doRechazarGasto() {
		Entity seleccionado= null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionadoGasto");
			JsfBase.setFlashAttribute("opcionResidente", EOpcionesResidente.CONSULTA_GASTO);
			JsfBase.setFlashAttribute("idDesarrollo", seleccionado.toLong("idDesarrollo"));
			JsfBase.setFlashAttribute("idGasto", seleccionado.getKey());
			JsfBase.setFlashAttribute("idRechazaGasto", true);
			JsfBase.setFlashAttribute("retorno", "filtro");
	  } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
		} // catch
		return "resumen".concat(Constantes.REDIRECIONAR);
	} 
	
	public String doImportar() {
		Entity seleccionado= null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionadoGasto");
			JsfBase.setFlashAttribute("opcionResidente", EOpcionesResidente.CONSULTA_GASTO);
			JsfBase.setFlashAttribute("idDesarrollo", seleccionado.toLong("idDesarrollo"));
			JsfBase.setFlashAttribute("idGasto", seleccionado.getKey());
			JsfBase.setFlashAttribute("retorno", "filtro");
	  } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
		} // catch
		return "importar".concat(Constantes.REDIRECIONAR);
	} 
	
	public String toColor(Entity row) {
		return EEstatusCajasChicas.fromId(row.toLong("idCajaChicaCierreEstatus")).getSemaforo();
	} // toColor
	
	public void doRevisar() {
		Entity seleccionado    = null;
		Transaccion transaccion= null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionadoGasto");
			transaccion= new Transaccion(seleccionado.getKey(), true);
			if(transaccion.ejecutar(EAccion.REGISTRAR))
				JsfBase.addMessage("Revisar gasto", "Se reviso el gasto de forma correcta", ETipoMensaje.INFORMACION);			
			else
				JsfBase.addMessage("Revisar gasto", "Ocurrió un error al revisar el gasto", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doRevisar
  
	public void doReporte(Entity row) throws Exception {    
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;    
    Map<String, Object>params    = new HashMap<>();
    Parametros comunes           = null;
		try {		  
      comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      reporteSeleccion= EReportes.CAJA_CHICA;
      params.put("operador", "");
      params.put("idNominaPeriodo", Cadena.isVacio(row)? -1L: row.toLong("idNominaPeriodo"));
      params.put("idGastoEstatus", "2, 4");
      this.reporte= JsfBase.toReporte();
      parametros  = comunes.getComunes();
      parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getNombre().toUpperCase());
      parametros.put("REPORTE_TITULO", reporteSeleccion.getTitulo());
      parametros.put("NOMBRE_REPORTE", reporteSeleccion.getTitulo());
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));
      parametros.put("REPORTE_FIGURA", "CORTE GENERAL");
      parametros.put("REPORTE_DEPARTAMENTO", JsfBase.getAutentifica().getPersona().getNombreCompleto());
      if(Cadena.isVacio(row))
        parametros.put("REPORTE_PERIODO", "");
      else
        parametros.put("REPORTE_PERIODO", row.toString("inicio")+ " al "+ row.toString("final"));
      this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));		
      if(this.doVerificarReporte())
        this.reporte.doAceptar();			
    } // try
    catch(Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);			
    } // catch	
  } // doReporte 
	
	public boolean doVerificarReporte() {
    boolean regresar = false;
		if(this.reporte.getTotal()> 0L) {
			UIBackingUtilities.execute("start(" + this.reporte.getTotal() + ")");	
      regresar = true;
    }
		else {
			UIBackingUtilities.execute("generalHide();");		
			JsfBase.addMessage("Reporte", "No se encontraron registros para el reporte", ETipoMensaje.ERROR);
      regresar = false;
		} // else
    return regresar;
	} // doVerificarReporte	
  
}