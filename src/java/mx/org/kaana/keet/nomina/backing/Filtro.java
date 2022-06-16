package mx.org.kaana.keet.nomina.backing;

import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletContext;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.hibernate.SessionFactoryFacade;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.procesos.reportes.beans.ExportarXls;
import mx.org.kaana.kajool.procesos.reportes.beans.Modelo;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.keet.db.dto.TcKeetNominasBitacoraDto;
import mx.org.kaana.keet.nomina.reglas.Egresos;
import mx.org.kaana.keet.nomina.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.archivo.Xls;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.wassenger.Cafu;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EExportacionXls;
import mx.org.kaana.mantic.enums.EReportes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named(value = "keetNominasFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

	private static final long serialVersionUID= 6319984968937774153L;
  private static final Log LOG = LogFactory.getLog(Filtro.class);
  
  private static final String COLUMN_DATA_FILE_FALTAS= "DESARROLLO,EMPLEADO,FECHA,ESTATUS,REGISTRO";  
  private static final String COLUMN_DATA_FILE_NOMINA= "NOMINA,CLAVE,NOMBRE COMPLETO,RFC,CURP,ACTIVO,DESARROLLO,CONTRATO,TOTAL";  
  
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

	private void toLoadEmpresas() {
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
      // this.doLoadDesarrollos();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally{
			Methods.clean(params);
		}	// finally	
	} // loadEmpresas

	public void doLoadDesarrollos() {
		List<Columna> columns     = null;
    Map<String, Object> params= null;
//		UISelectEntity empresa    = null;
    try {
			params= new HashMap<>();			
//			empresa= (UISelectEntity) this.attrs.get("idEmpresa");
//			if(empresa.getKey()>= 1L)
//        params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa=" + empresa.getKey());
//			else
			params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa in (" + JsfBase.getAutentifica().getEmpresa().getSucursales() + ")");			
			params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
      columns= new ArrayList<>();
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
	} // doLoadDesarrollos	  
  
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
//		if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && !((UISelectEntity)this.attrs.get("idDesarrollo")).getKey().equals(-1L))
//		  sb.append("(tc_mantic_requisiciones.id_desarrollo= ").append(((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()).append(") and ");		
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
			this.toLoadEmpresas();
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
				JsfBase.addMessage("Cambio estatus", "Se realizó el cambio de estatus de forma correcta", ETipoMensaje.INFORMACION);			
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
			JsfBase.setFlashAttribute(Constantes.REPORTE_REFERENCIA, new ExportarXls(new Modelo((Map<String, Object>) ((HashMap)params).clone(), EExportacionXls.NOMINA.getProceso(), EExportacionXls.NOMINA.getIdXml(), EExportacionXls.NOMINA.getNombreArchivo()), EExportacionXls.NOMINA, "SUCURSAL,NOMINA,TIPO,CLAVE,APODO,NOMBRE_COMPLETO,RFC,CURP,ACTIVO,IMPORTE"));
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
      if(reporteSeleccion.equals(EReportes.LISTADO_NOMINA)) {
        params = this.toPrepare();
        params.put("sortOrder", "order by tc_keet_nominas.id_nomina desc");
      }
      else 
        params = this.toPrepare();
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

  public void doNotifica() {
		Entity seleccionado      = null;
		Transaccion transaccion  = null;
	  try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
 			transaccion = new Transaccion(seleccionado.getKey());
			if(transaccion.ejecutar(EAccion.TRANSFORMACION)) 			
			  JsfBase.addMessage("Notificar", "Se notificó por corre de forma correcta", ETipoMensaje.INFORMACION);			
      else
			  JsfBase.addMessage("Notificar", "Error", ETipoMensaje.INFORMACION);			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doWhatsup() {
		Entity seleccionado      = null;
		Transaccion transaccion  = null;
	  try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
 			transaccion = new Transaccion(seleccionado.getKey());
			if(transaccion.ejecutar(EAccion.MOVIMIENTOS)) 			
			  JsfBase.addMessage("Notificar", "Se notificó por whatsapp de forma correcta", ETipoMensaje.INFORMACION);			
      else
			  JsfBase.addMessage("Notificar", "Error", ETipoMensaje.INFORMACION);			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doListado() {
		Entity seleccionado      = null;
		Transaccion transaccion  = null;
	  try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
 			transaccion = new Transaccion(seleccionado.getKey(), JsfBase.getAutentifica());
			if(transaccion.ejecutar(EAccion.RESTAURAR)) 			
			  JsfBase.addMessage("Notificar", "Se notificó por whatsapp de forma correcta", ETipoMensaje.INFORMACION);			
      else
			  JsfBase.addMessage("Notificar", "Error", ETipoMensaje.INFORMACION);			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doNotificar() {
    String nombres[]  = {"ARMANDO CALDERON OROZCO"};
    String celulares[]= {"4424312527"};
    Cafu message= new Cafu("Alejandro Jiménez García", "449-209-05-86");
    for (int x= 0; x < nombres.length; x++) {
      message.setNombre(Cadena.nombrePersona(nombres[x]));
      message.setCelular(celulares[x]);
      message.doSendMessage();
    } // for
  }
  
  public StreamedContent getFaltas() {
		StreamedContent regresar= null;
		Xls xls                 = null;
		Map<String, Object>params= null;
		String template         = "FALTAS";
		try {
	  	params=new HashMap<>();
			String salida  = EFormatos.XLS.toPath().concat(Archivo.toFormatNameFile(template).concat(".")).concat(EFormatos.XLS.name().toLowerCase());
  		String fileName= JsfBase.getRealPath("").concat(salida);
      xls= new Xls(fileName, new Modelo(params, "VistaIncidenciasDto", "faltas", template), COLUMN_DATA_FILE_FALTAS);	
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
  
  private void toExecute() throws Exception {
    Session sesion         = null;
    Transaction transaction= null;
		try {
			sesion= SessionFactoryFacade.getInstance().getSession(-1L);
			transaction= sesion.beginTransaction();
			sesion.clear();
      DaoFactory.getInstance().execute(sesion, "set @sql = null");
      DaoFactory.getInstance().execute(sesion, "select\n" +
"  group_concat(distinct\n" +
"    concat(\n" +
"      'ifnull(sum(case when contrato= ''',\n" +
"      contrato,\n" +
"      ''' then destajo end), 0) as `',\n" +
"      contrato, '`'\n" +
"    )\n" +
"  ) into @sql\n" +
"from (\n" +
"		select \n" +
"			tr_mantic_empresa_personal.id_empresa_persona,\n" +
"			tc_keet_desarrollos.id_desarrollo,\n" +
"			concat(tc_keet_nominas_periodos.ejercicio, '-', if(tc_keet_nominas_periodos.orden< 10, '0', ''), tc_keet_nominas_periodos.orden) as nomina,\n" +
"			tr_mantic_empresa_personal.clave,\n" +
"			concat(tc_mantic_personas.nombres, ' ', ifnull(tc_mantic_personas.paterno, ' '), ' ', ifnull(tc_mantic_personas.materno, ' ')) as nombre_completo,\n" +
"			tc_mantic_personas.rfc,\n" +
"			tc_mantic_personas.curp,\n" +
"			if(tr_mantic_empresa_personal.id_activo= 1, 'SI', 'NO') as activo,\n" +
"			concat(tc_keet_desarrollos.nombres, '-', tc_keet_contratos.nombre) as contrato,\n" +
"			sum(tc_keet_contratos_destajos_contratistas.costo) as destajo\n" +
"		from\n" +
"			tc_keet_contratos_destajos_contratistas \n" +
"		inner join\n" +
"			tc_keet_contratos_lotes_contratistas on tc_keet_contratos_destajos_contratistas.id_contrato_lote_contratista= tc_keet_contratos_lotes_contratistas.id_contrato_lote_contratista     \n" +
"		inner join\n" +
"			tr_mantic_empresa_personal on tr_mantic_empresa_personal.id_empresa_persona= tc_keet_contratos_lotes_contratistas.id_empresa_persona\n" +
"		inner join \n" +
"			tc_mantic_personas on tr_mantic_empresa_personal.id_persona= tc_mantic_personas.id_persona\n" +
"		inner join\n" +
"			tc_keet_contratos_lotes on tc_keet_contratos_lotes_contratistas.id_contrato_lote= tc_keet_contratos_lotes.id_contrato_lote     \n" +
"		inner join\n" +
"			tc_keet_prototipos on tc_keet_contratos_lotes.id_prototipo= tc_keet_prototipos.id_prototipo\n" +
"		inner join\n" +
"			tc_keet_contratos on tc_keet_contratos_lotes.id_contrato= tc_keet_contratos.id_contrato     \n" +
"		inner join\n" +
"			tc_keet_proyectos on tc_keet_contratos.id_proyecto= tc_keet_proyectos.id_proyecto     \n" +
"		inner join\n" +
"			tc_keet_desarrollos on tc_keet_proyectos.id_desarrollo= tc_keet_desarrollos.id_desarrollo     \n" +
"		inner join\n" +
"			tc_keet_estaciones on tc_keet_contratos_destajos_contratistas.id_estacion= tc_keet_estaciones.id_estacion     \n" +
"		inner join\n" +
"			tc_keet_rubros on tc_keet_estaciones.codigo= tc_keet_rubros.codigo\n" +
"		inner join\n" +
"			tc_keet_rubros_grupos on tc_keet_rubros.id_rubro= tc_keet_rubros_grupos.id_rubro\n" +
"		inner join\n" +
"			tc_keet_puntos_grupos on tc_keet_rubros_grupos.id_punto_grupo= tc_keet_puntos_grupos.id_punto_grupo and tc_keet_puntos_grupos.id_departamento= tr_mantic_empresa_personal.id_departamento\n" +
"		left join\n" +
"			tc_keet_nominas on tc_keet_contratos_destajos_contratistas.id_nomina= tc_keet_nominas.id_nomina     \n" +
"		left join\n" +
"			tc_keet_nominas_periodos on tc_keet_nominas.id_nomina_periodo= tc_keet_nominas_periodos.id_nomina_periodo      \n" +
"		where \n" +
"			(tc_keet_contratos_destajos_contratistas.id_nomina= 54)\n" +
"			and tc_keet_contratos_destajos_contratistas.id_estacion_estatus in (2, 3)\n" +
"			and tr_mantic_empresa_personal.id_puesto= 6\n" +
"		group by\n" +
"			tr_mantic_empresa_personal.id_empresa_persona,\n" +
"			tc_keet_desarrollos.id_desarrollo,\n" +
"			tc_keet_desarrollos.nombres\n" +
"		order by\n" +
"			tr_mantic_empresa_personal.id_empresa_persona,\n" +
"			tc_keet_desarrollos.id_desarrollo,\n" +
"			tc_keet_contratos.nombre\n" +
") as tt_keet_temporal");
      DaoFactory.getInstance().execute(sesion, "set @sql = concat(\"select nomina, clave, nombre_completo, rfc, curp, activo, \", @sql, \" from (\n" +
"select \n" +
"			tr_mantic_empresa_personal.id_empresa_persona,\n" +
"			tc_keet_desarrollos.id_desarrollo,\n" +
"			concat(tc_keet_nominas_periodos.ejercicio, '-', if(tc_keet_nominas_periodos.orden< 10, '0', ''), tc_keet_nominas_periodos.orden) as nomina,\n" +
"			tr_mantic_empresa_personal.clave,\n" +
"			concat(tc_mantic_personas.nombres, ' ', ifnull(tc_mantic_personas.paterno, ' '), ' ', ifnull(tc_mantic_personas.materno, ' ')) as nombre_completo,\n" +
"			tc_mantic_personas.rfc,\n" +
"			tc_mantic_personas.curp,\n" +
"			if(tr_mantic_empresa_personal.id_activo= 1, 'SI', 'NO') as activo,\n" +
"			concat(tc_keet_desarrollos.nombres, ' ', tc_keet_contratos.nombre) as contrato,\n" +
"			sum(tc_keet_contratos_destajos_contratistas.costo) as destajo\n" +
"		from\n" +
"			tc_keet_contratos_destajos_contratistas \n" +
"		inner join\n" +
"			tc_keet_contratos_lotes_contratistas on tc_keet_contratos_destajos_contratistas.id_contrato_lote_contratista= tc_keet_contratos_lotes_contratistas.id_contrato_lote_contratista     \n" +
"		inner join\n" +
"			tr_mantic_empresa_personal on tr_mantic_empresa_personal.id_empresa_persona= tc_keet_contratos_lotes_contratistas.id_empresa_persona\n" +
"		inner join \n" +
"			tc_mantic_personas on tr_mantic_empresa_personal.id_persona= tc_mantic_personas.id_persona\n" +
"		inner join\n" +
"			tc_keet_contratos_lotes on tc_keet_contratos_lotes_contratistas.id_contrato_lote= tc_keet_contratos_lotes.id_contrato_lote     \n" +
"		inner join\n" +
"			tc_keet_prototipos on tc_keet_contratos_lotes.id_prototipo= tc_keet_prototipos.id_prototipo\n" +
"		inner join\n" +
"			tc_keet_contratos on tc_keet_contratos_lotes.id_contrato= tc_keet_contratos.id_contrato     \n" +
"		inner join\n" +
"			tc_keet_proyectos on tc_keet_contratos.id_proyecto= tc_keet_proyectos.id_proyecto     \n" +
"		inner join\n" +
"			tc_keet_desarrollos on tc_keet_proyectos.id_desarrollo= tc_keet_desarrollos.id_desarrollo     \n" +
"		inner join\n" +
"			tc_keet_estaciones on tc_keet_contratos_destajos_contratistas.id_estacion= tc_keet_estaciones.id_estacion     \n" +
"		inner join\n" +
"			tc_keet_rubros on tc_keet_estaciones.codigo= tc_keet_rubros.codigo\n" +
"		inner join\n" +
"			tc_keet_rubros_grupos on tc_keet_rubros.id_rubro= tc_keet_rubros_grupos.id_rubro\n" +
"		inner join\n" +
"			tc_keet_puntos_grupos on tc_keet_rubros_grupos.id_punto_grupo= tc_keet_puntos_grupos.id_punto_grupo and tc_keet_puntos_grupos.id_departamento= tr_mantic_empresa_personal.id_departamento\n" +
"		left join\n" +
"			tc_keet_nominas on tc_keet_contratos_destajos_contratistas.id_nomina= tc_keet_nominas.id_nomina     \n" +
"		left join\n" +
"			tc_keet_nominas_periodos on tc_keet_nominas.id_nomina_periodo= tc_keet_nominas_periodos.id_nomina_periodo      \n" +
"		where \n" +
"			(tc_keet_contratos_destajos_contratistas.id_nomina= 54)\n" +
"			and tc_keet_contratos_destajos_contratistas.id_estacion_estatus in (2, 3)\n" +
"			and tr_mantic_empresa_personal.id_puesto= 6\n" +
"		group by\n" +
"			tr_mantic_empresa_personal.id_empresa_persona,\n" +
"			tc_keet_desarrollos.id_desarrollo,\n" +
"			tc_keet_desarrollos.nombres\n" +
"		order by\n" +
"			tr_mantic_empresa_personal.id_empresa_persona,\n" +
"			tc_keet_desarrollos.id_desarrollo,\n" +
"			tc_keet_contratos.nombre) as tt_keet_temporal\n" +
" group by id_empresa_persona order by curp\")");     
      DaoFactory.getInstance().execute(sesion, "prepare stmt from @sql");
      DaoFactory.getInstance().execute(sesion, "execute stmt");
      ScrollableResults list= DaoFactory.getInstance().toSqlScrollable(sesion, "execute stmt", Constantes.SQL_MAXIMO_REGISTROS);
      if(list!= null)
        while(list.next()) {
          LOG.info(list.get(0));
        } // for
      DaoFactory.getInstance().execute(sesion, "deallocate prepare stmt");
			transaction.commit();
		} // try
		catch (Exception e) {
			if (transaction!= null)
				transaction.rollback();
			throw e;
		} // catch
		finally {
			if (sesion!= null) {
				sesion.close();
			} // if
			transaction= null;
			sesion     = null;
		} // finally    
  }
  
	public StreamedContent getDocumento() {
		StreamedContent regresar = null;		
		Entity seleccionado      = null;				
		Xls xls                  = null;
		Map<String, Object>params= new HashMap<>();
		String template          = "CONTRATISTAS";
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");						
      params.put("idNomina", seleccionado.toLong("idNomina"));
      params.put("idPuesto", 6L);
      params.put("loNuevo", "");
			String salida  = EFormatos.XLS.toPath().concat(Archivo.toFormatNameFile(template).concat(".")).concat(EFormatos.XLS.name().toLowerCase());
  		String fileName= JsfBase.getRealPath("").concat(salida);
      xls= new Xls(fileName, new Modelo(params, "VistaNominaConsultasDto", "monitoreo", template), COLUMN_DATA_FILE_NOMINA);	
			if(xls.procesar()) {
		    String contentType= EFormatos.XLS.getContent();
        InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(salida);  
		    regresar          = new DefaultStreamedContent(stream, contentType, Archivo.toFormatNameFile(template).concat(".").concat(EFormatos.XLS.name().toLowerCase()));				
			} // if
		} // try 
		catch (Exception e) {
			Error.mensaje(e);
		} // catch		
    finally {
      Methods.clean(params);
    } // finally
    return regresar;		
	} // getDocumento
  
	public StreamedContent getEgresos() {
		StreamedContent regresar = null;		
		Entity seleccionado      = null;				
    Egresos egresos          = null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");						
	  	egresos     = new Egresos(seleccionado.toLong("idNomina"));
      String name= egresos.execute();
      String contentType= EFormatos.XLS.getContent();
      InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(EFormatos.XLS.toPath().concat(name));  
      regresar          = new DefaultStreamedContent(stream, contentType, name);				
		} // try 
		catch (Exception e) {
			Error.mensaje(e);
		} // catch		
    return regresar;		
	} // getEgresos
  
	public StreamedContent getGlobal() {
		StreamedContent regresar = null;		
    Egresos egresos          = null;
		try {
	  	egresos    = new Egresos();
      String name= egresos.execute();
      String contentType= EFormatos.XLS.getContent();
      InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(EFormatos.XLS.toPath().concat(name));  
      regresar          = new DefaultStreamedContent(stream, contentType, name);				
		} // try 
		catch (Exception e) {
			Error.mensaje(e);
		} // catch		
    return regresar;		
	} // getGlobal
  
}
	