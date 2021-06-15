package mx.org.kaana.keet.nomina.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.procesos.reportes.beans.ExportarXls;
import mx.org.kaana.kajool.procesos.reportes.beans.Modelo;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.keet.db.dto.TcKeetNominasBitacoraDto;
import mx.org.kaana.keet.nomina.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EExportacionXls;
import mx.org.kaana.mantic.enums.EReportes;

@Named(value = "keetNominasFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

	private static final long serialVersionUID = 6319984968937774153L;
	private LocalDate fecha;
  protected Reporte reporte;

	public Reporte getReporte() {
		return reporte;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

  @PostConstruct
  @Override
  protected void init() {
    try {
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			if(JsfBase.getFlashAttribute("idNomina")!= null){
				this.attrs.put("idNomina", JsfBase.getFlashAttribute("idNomina"));
				this.doLoad();
				this.attrs.put("idNomina", null);
			} // if
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
			params.put("sortOrder", "order by tc_keet_nominas.id_nomina desc");
      columns= new ArrayList<>();
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("proveedores", EFormatoDinamicos.MILES_SIN_DECIMALES));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("personas", EFormatoDinamicos.MILES_SIN_DECIMALES));
      columns.add(new Columna("neto", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("global", EFormatoDinamicos.MONEDA_CON_DECIMALES));
      this.lazyModel = new FormatCustomLazy("VistaNominaDto", params, columns);
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

  public String doAccion(String accion) {
    String regresar= null;
		EAccion eaccion= null;
    try {
      eaccion = EAccion.valueOf(accion.toUpperCase());
      JsfBase.setFlashAttribute("accion", eaccion);      
      JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(accion.toUpperCase())); 
			JsfBase.setFlashAttribute("idNomina",  eaccion.equals(EAccion.AGREGAR)? -1L: ((Entity)this.attrs.get("seleccionado")).getKey());
      JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Nominas/filtro");
			switch (eaccion){
				case AGREGAR:
				  regresar= "accion".concat(Constantes.REDIRECIONAR);
					break;
				case CALCULAR:
				  regresar= "progreso".concat(Constantes.REDIRECIONAR);
					break;
				case CONSULTAR: // personas
				  regresar= "personas".concat(Constantes.REDIRECIONAR);
					break;
				case LISTAR: // proveedores
				  regresar= "proveedores".concat(Constantes.REDIRECIONAR);
					break;
			} // switch
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar  = new HashMap<>();	
		StringBuilder sb              = new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()>= 1L)				
			sb.append("(tc_keet_nominas.id_empresa in (").append(((UISelectEntity)this.attrs.get("idEmpresa")).getKey()).append(")) and ");
		else
			sb.append("(tc_keet_nominas.id_empresa in (").append(JsfBase.getAutentifica().getEmpresa().getSucursales()).append(")) and ");
  	regresar.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
		if(this.attrs.get("idNomina")!= null && !Cadena.isVacio(this.attrs.get("idNomina")))
			sb.append("tc_keet_nominas.id_nomina=").append(this.attrs.get("idNomina")).append(" and ");
		if(!Cadena.isVacio(this.fecha)) {
  		sb.append("date_format(tc_keet_nominas_periodos.inicio, '%Y%m%d')<= '").append(this.fecha.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("' and ");
	  	sb.append("date_format(tc_keet_nominas_periodos.termino, '%Y%m%d')>= '").append(this.fecha.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("' and ");
		} // if
		if(!Cadena.isVacio(this.attrs.get("ejercicio")) && ((UISelectEntity)this.attrs.get("ejercicio")).getKey()>= 1L)				
			sb.append("tc_keet_nominas_periodos.ejercicio = ").append(((UISelectEntity)this.attrs.get("ejercicio")).getKey()).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("semana")) && ((UISelectEntity)this.attrs.get("semana")).getKey()>= 1L)				
			sb.append("tc_keet_nominas_periodos.orden = ").append(((UISelectEntity)this.attrs.get("semana")).getKey()).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("idTipoNomina")) && ((UISelectEntity)this.attrs.get("idTipoNomina")).getKey()>= 1L)				
			sb.append("tc_keet_nominas.id_tipo_nomina= ").append(((UISelectEntity)this.attrs.get("idTipoNomina")).getKey()).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("estatus")) && ((UISelectEntity)this.attrs.get("estatus")).getKey()>= 1L)				
			sb.append("tc_keet_nominas.id_nomina_estatus = ").append(((UISelectEntity)this.attrs.get("estatus")).getKey()).append(" and ");
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	} // toPrepare

	private void loadCatalogs() {
		Map<String, Object>params= null;
    List<Columna> columns    = null;
    try {
      columns= new ArrayList<>();
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
			this.loadEmpresas();
			params= new HashMap<>();
		  params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("ejercicios", UIEntity.seleccione("VistaNominaDto", "ejercicios", params, "ejercicio"));
      this.attrs.put("ejercicio", new UISelectEntity(-1L));
      this.attrs.put("semanas", UIEntity.seleccione("VistaNominaDto", "semanas", params, columns, "semana"));
      this.attrs.put("semana", new UISelectEntity(-1L));
      this.attrs.put("tipos", UIEntity.seleccione("TcKeetTiposNominasDto", "row", params, "nombre"));
      this.attrs.put("idTipoNomina", new UISelectEntity(-1L));
      this.attrs.put("catalogo", UIEntity.seleccione("TcKeetNominasEstatusDto", "todos", params, "nombre"));
      this.attrs.put("estatus", new UISelectEntity(-1L));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);	
		} // catch				
    finally {
			Methods.clean(params);
			Methods.clean(columns);
		}	// finally
	} // loadCatalogs

	public void doLoadEstatus() {
		Entity seleccionado          = null;
		Map<String, Object>params    = null;
		List<UISelectItem> allEstatus= null;		
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			params= new HashMap<>();			
			params.put("estatusAsociados", seleccionado.toString("estatusAsociados"));
			allEstatus= UISelect.build("TcKeetNominasEstatusDto", "estatus", params, "nombre", EFormatoDinamicos.MAYUSCULAS);			
			this.attrs.put("allEstatus", allEstatus);
			this.attrs.put("idEstatus", UIBackingUtilities.toFirstKeySelectItem(allEstatus));		
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadEstatus
	
	public void doActualizarEstatus() {
		Transaccion transaccion= null;
		Entity seleccionado    = null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			TcKeetNominasBitacoraDto bitacora= new TcKeetNominasBitacoraDto(
				(String) this.attrs.get("justificacion"), // String justificacion, 
				Long.valueOf((String)this.attrs.get("idEstatus")), // Long idNominaEstatus, 
				JsfBase.getIdUsuario(), // Long idUsuario, 
				-1L, // Long idNominaBitacora, 
				seleccionado.getKey() // Long idNomina
			);
			transaccion= new Transaccion(seleccionado.getKey(), JsfBase.getAutentifica(), bitacora);
			if(transaccion.ejecutar(EAccion.JUSTIFICAR)) 			
				JsfBase.addMessage("Cambio estatus", "Se realizo el cambio de estatus de forma correcta", ETipoMensaje.INFORMACION);			
			else
				JsfBase.addMessage("Cambio estatus", "Ocurrio un error al realizar el cambio de estatus", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally {
			this.attrs.put("justificacion", "");			
		} // finally
	}	// doActualizaEstatus
	
  public String doExportar() {
		String regresar           = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			Entity entity= (Entity)this.attrs.get("seleccionado");
			params.put("sortOrder", "order by tipo, nomina, clave");
			params.put("idNomina", entity.toLong("idNomina"));
			params.put("nombre", "");
			JsfBase.setFlashAttribute(Constantes.REPORTE_REFERENCIA, new ExportarXls(new Modelo((Map<String, Object>) ((HashMap)params).clone(), EExportacionXls.NOMINA.getProceso(), EExportacionXls.NOMINA.getIdXml(), EExportacionXls.NOMINA.getNombreArchivo()), EExportacionXls.NOMINA, "SUCURSAL,NOMINA,TIPO,CLAVE,APODO,NOMBRE_COMPLETO,IMPORTE"));
			JsfBase.getAutentifica().setMonitoreo(new Monitoreo());
			regresar = "/Paginas/Reportes/excel".concat(Constantes.REDIRECIONAR);				
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
	
	public void doReporte(String nombre) throws Exception {    
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;    
    Map<String, Object>params    = null;
    Parametros comunes           = null;
		try {		  
      comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      reporteSeleccion= EReportes.valueOf(nombre);
      if(reporteSeleccion.equals(EReportes.LISTADO_NOMINA)){
        params = this.toPrepare();
        params.put("sortOrder", "order by tc_keet_nominas.id_nomina desc");
      }
      else {
        params = this.toPrepare();
      }
      this.reporte= JsfBase.toReporte();
      parametros= comunes.getComunes();
      parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getNombre().toUpperCase());
      parametros.put("REPORTE_TITULO", reporteSeleccion.getTitulo());
      parametros.put("NOMBRE_REPORTE", reporteSeleccion.getTitulo());
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));
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
	