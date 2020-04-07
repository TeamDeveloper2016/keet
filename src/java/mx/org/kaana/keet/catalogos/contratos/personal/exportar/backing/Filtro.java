package mx.org.kaana.keet.catalogos.contratos.personal.exportar.backing;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.procesos.reportes.beans.ExportarXls;
import mx.org.kaana.kajool.procesos.reportes.beans.Modelo;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
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
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesDto;
import mx.org.kaana.mantic.enums.EEstatusIncidentes;
import mx.org.kaana.mantic.enums.EExportacionXls;
import mx.org.kaana.mantic.incidentes.beans.Incidente;
import mx.org.kaana.mantic.incidentes.reglas.Transaccion;

@Named(value= "keetCatalogosContratosPersonalExportarFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

	private static final long serialVersionUID = 6445195086151257263L;  
	private LocalDate fechaInicio;
	private LocalDate fechaFin;

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
	
  @PostConstruct
  @Override
  protected void init() {
    try {    	
      this.attrs.put("codigo", "");                  
			this.fechaInicio= LocalDate.now();
			this.fechaFin= LocalDate.now();
			this.toLoadCatalog();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns     = null;
		Map<String, Object> params= this.toPrepare();
    try {
      columns = new ArrayList<>();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombreUsuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("vigenciaInicio", EFormatoDinamicos.FECHA_HORA_CORTA));
      columns.add(new Columna("vigenciaFin", EFormatoDinamicos.FECHA_HORA_CORTA));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      params.put("sortOrder", "order by tc_mantic_incidentes.id_desarrollo, tc_mantic_incidentes.consecutivo");
      this.lazyModel = new FormatCustomLazy("VistaIncidentesDto", "principal", params, columns);
      UIBackingUtilities.resetDataTable();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(columns);
    } // finally		
  } // doLoad
	
	private void toLoadCatalog() {
		List<Columna> columns     = null;
    Map<String, Object> params= null;
		List<UISelectItem> estatus= null;
    try {
			columns= new ArrayList<>();			
			params = new HashMap<>();
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));
			this.attrs.put("idEmpresa", new UISelectEntity("-1"));      
			estatus= new ArrayList<>();
			for(EEstatusIncidentes eIncidente: EEstatusIncidentes.values())
				estatus.add(new UISelectItem(eIncidente.getIdEstatusInicidente(), eIncidente.name()));
			estatus.add(0, new UISelectItem(-1L, "TODOS"));
			this.attrs.put("estatus", estatus);
			this.attrs.put("idEstatus", UIBackingUtilities.toFirstKeySelectItem(estatus));
			this.loadTiposIncidentes();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}

	private Map<String, Object> toPrepare() {
		Map<String, Object> regresar= new HashMap<>();
		StringBuilder sb            = null;
		try {
			sb= new StringBuilder("");						
			if(this.attrs.get("idEstatus")!= null && Long.valueOf(this.attrs.get("idEstatus").toString())>0L)
				sb.append("tc_mantic_incidentes.id_incidente_estatus=").append(this.attrs.get("idEstatus")).append(" and ");						
			if(this.attrs.get("idTipoIncidente")!= null && Long.valueOf(this.attrs.get("idTipoIncidente").toString())>0L)
				sb.append("tc_mantic_incidentes.id_tipo_incidente=").append(this.attrs.get("idTipoIncidente")).append(" and ");						
			if(!Cadena.isVacio(JsfBase.getParametro("orden_input")))
				sb.append("upper(tc_mantic_incidentes.orden) like upper('%").append(JsfBase.getParametro("orden_input")).append("%') and ");						
			if(this.attrs.get("nombre")!= null && ((UISelectEntity)this.attrs.get("nombre")).getKey()> 0L) 
				sb.append("tc_mantic_incidentes.id_empresa_persona=").append(((UISelectEntity)this.attrs.get("nombre")).getKey()).append(" and ");						
  		else if(!Cadena.isVacio(JsfBase.getParametro("nombre_input"))) { 
				String nombre= JsfBase.getParametro("nombre_input").replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*");
				sb.append("(tc_mantic_personas.nombre regexp '.*").append(nombre).append(".*') and ");				
			} // else if			  						
			sb.append("tc_mantic_incidentes.vigencia_inicio >= '").append(this.fechaInicio.toString()).append("' and ");				
			sb.append("tc_mantic_incidentes.vigencia_fin <= '").append(this.fechaFin.toString()).append("' and ");										
			if(Cadena.isVacio(sb.toString()))
				regresar.put("condicion", Constantes.SQL_VERDADERO);
			else
			  regresar.put("condicion", sb.substring(0, sb.length()- 4));			
		  if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && this.attrs.get("idEmpresa").toString().equals("-1"))
			  regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getDependencias());
			else
			  regresar.put("idEmpresa", ((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toCondicion

	public List<UISelectEntity> doCompleteOrden(String query) {
		this.attrs.put("orden", query);
    this.doUpdateOrdenes();		
		return (List<UISelectEntity>)this.attrs.get("ordenes");
	}	// doCompleteOrden
	
	public void doUpdateOrdenes() {
		List<Columna> columns     = null;
    Map<String, Object> params= null;
    try {
			params= new HashMap<>();
			columns= new ArrayList<>();
      columns.add(new Columna("orden", EFormatoDinamicos.MAYUSCULAS));      
			String orden= (String)this.attrs.get("orden"); 
			orden= !Cadena.isVacio(orden) ? orden.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim(): "WXYZ";			
			if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
				params.put("idEmpresa", this.attrs.get("idEmpresa"));
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());  		
  		params.put("orden", orden);			
      this.attrs.put("ordenes", (List<UISelectEntity>) UIEntity.build("VistaIncidentesDto", "ordenes", params, columns, 20L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}	// doUpdateCodigos
	
	public List<UISelectEntity> doCompleteNombreEmpleado(String query) {
		this.attrs.put("nombreEmpleado", query);
    this.doUpdateNombresEmpleados();		
		return (List<UISelectEntity>)this.attrs.get("nombres");
	}	// doCompleteNombreEmpleado
	
	public void doUpdateNombresEmpleados() {
		List<Columna> columns       = null;
    Map<String, Object> params  = new HashMap<>();
		List<UISelectEntity> nombres= null;		
    try {
			columns= new ArrayList<>();      
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));			
			String nombreEmpleado= (String)this.attrs.get("nombreEmpleado"); 
			nombreEmpleado= !Cadena.isVacio(nombreEmpleado) ? nombreEmpleado.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim(): "WXYZ";		
			if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
				params.put("idEmpresa", this.attrs.get("idEmpresa"));
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales()); 
			params.put("nombreEmpleado", nombreEmpleado);	
      nombres= (List<UISelectEntity>) UIEntity.build("VistaPersonasDto", "rowAutocomplete", params, columns, 20L);
      this.attrs.put("nombres", nombres);
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}	// doUpdateArticulo	
	
	private void loadTiposIncidentes() {
		Map<String, Object> params= new HashMap<>();
		params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		List<UISelectItem> tiposIncidentes= UISelect.build("TcManticTiposIncidentesDto", "row", params, "nombre", " ", EFormatoDinamicos.MAYUSCULAS);
		tiposIncidentes.add(0, new UISelectItem(-1L, "TODOS"));
		this.attrs.put("incidentes", tiposIncidentes);
	} // loadTiposInicidentes	
	
	public void doLoadEstatus() {
		Entity seleccionado          = null;
		Map<String, Object>params    = null;
		List<UISelectItem> allEstatus= null;		
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			params= new HashMap<>();			
			params.put("estatusAsociados", seleccionado.toString("estatusAsociados"));
			allEstatus= UISelect.build("TcManticIncidentesEstatusDto", "estatus", params, "nombre", EFormatoDinamicos.MAYUSCULAS);			
			this.attrs.put("allEstatus", allEstatus);
			this.attrs.put("estatusDlg", UIBackingUtilities.toFirstKeySelectItem(allEstatus));		
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
		Transaccion transaccion  = null;
		Entity seleccionado      = null;
		Incidente incidente      = null;
		TcManticIncidentesDto dto= null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			dto= (TcManticIncidentesDto) DaoFactory.getInstance().findById(TcManticIncidentesDto.class, seleccionado.getKey());
			incidente= new Incidente(dto);
			incidente.setIdIncidenteEstatus(Long.valueOf(this.attrs.get("estatusDlg").toString()));
			transaccion= new Transaccion(incidente, (String)this.attrs.get("justificacion"));
			if(transaccion.ejecutar(EAccion.ASIGNAR)) 			
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
		String regresar          = null;		
		Map<String, Object>params= null;
		try {									   
			params= this.toPrepare();
			params.put("sortOrder", "order by tc_mantic_incidentes.vigencia_inicio");
			JsfBase.setFlashAttribute(Constantes.REPORTE_REFERENCIA, new ExportarXls(new Modelo((Map<String, Object>) ((HashMap)params).clone(), EExportacionXls.INCIDENCIAS), EExportacionXls.INCIDENCIAS, 
				"EJERCICIO,CONSECUTIVO,VIGENCIA_INICIO,VIGENCIA_FIN,NOMBRE,PUESTO,TIPO_INCIDENTE,ESTATUS,CLAVE_DESARROLLO,NOMBRE_DESARROLLO,REGISTRO"));
			JsfBase.getAutentifica().setMonitoreo(new Monitoreo());
			regresar = "/Paginas/Reportes/excel".concat(Constantes.REDIRECIONAR);
		} // try
		catch (Exception e){
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // doExportar  
}