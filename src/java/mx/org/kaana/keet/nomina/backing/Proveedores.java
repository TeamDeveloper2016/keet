package mx.org.kaana.keet.nomina.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.procesos.reportes.beans.ExportarXls;
import mx.org.kaana.kajool.procesos.reportes.beans.Modelo;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.catalogos.contratos.destajos.comun.IBaseReporteDestajos;
import mx.org.kaana.keet.comun.Catalogos;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EExportacionXls;
import mx.org.kaana.mantic.enums.EReportes;

@Named(value = "keetNominasProveedores")
@ViewScoped
public class Proveedores extends IBaseReporteDestajos implements Serializable {

	private static final long serialVersionUID = 6319984968937774153L;
	private FormatLazyModel lazyDetalle;  

	public FormatLazyModel getLazyDetalle() {
		return lazyDetalle;
	}

  public String getCostoTotal() {
    Double costo = 0D;
		if(this.lazyDetalle!= null)
			for (IBaseDto item: (List<IBaseDto>)this.lazyDetalle.getWrappedData()) {
				Entity row= (Entity)item;
				costo+= new Double(row.toString("costo"));
			} // for	
		return Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, costo);
	}

	@PostConstruct
  @Override
  protected void init() {
    try {
			this.initBase();
			this.attrs.put("idTipoFiguraCorreo", 2L);			
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			Long idNomina= (Long)JsfBase.getFlashAttribute("idNomina");
			this.loadCatalogs();
			this.attrs.put("nomina", false);
			if(!Cadena.isVacio(idNomina)) {
				Entity entity= new Entity(idNomina);
			  entity.put("idNomina", new Value("idNomina", idNomina));
        entity.put("nomina", new Value("nomina", (String)JsfBase.getFlashAttribute("nomina")));
				entity.put("nombreCompleto", new Value("nombreCompleto", (String)JsfBase.getFlashAttribute("nombreCompleto")));
				entity.put("idProveedor", new Value("idProveedor", (Long)JsfBase.getFlashAttribute("idProveedor")));
				this.attrs.put("idNomina", new UISelectEntity(idNomina));
				this.attrs.put("idProveedor", new UISelectEntity(JsfBase.getFlashAttribute("idProveedor")== null? -1L: (Long)JsfBase.getFlashAttribute("idProveedor")));
				this.attrs.put("seleccionado", entity);
				this.doLoad();
				if(!Cadena.isVacio(entity.toLong("idProveedor")) && entity.toLong("idProveedor")!= -1L)
					this.doLoadDetalle(0);
				this.attrs.put("idNomina", new UISelectEntity(-1L));
				this.attrs.put("idProveedor", new UISelectEntity(-1L));
				this.attrs.put("factor", 0);
		  }
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns    = new ArrayList<>();
		Map<String, Object>params= null;
    try {
      params= this.toPrepare();	
			params.put("sortOrder", "order by nomina desc");
      columns.add(new Columna("subtotal", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("iva", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
      this.lazyModel = new FormatCustomLazy("VistaNominaConsultasDto", "proveedores", params, columns);
      UIBackingUtilities.resetDataTable();
			this.attrs.put("nomina", false);
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
		Map<String, Object>params= new HashMap<>();
		List<Columna> columns    = new ArrayList<>();
		try {
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

  public void doAccion() {
		
  } // doAccion

	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar  = new HashMap<>();	
		StringBuilder sb              = new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()>= 1L)				
			sb.append("(tc_keet_nominas.id_empresa in (").append(((UISelectEntity)this.attrs.get("idEmpresa")).getKey()).append(")) and ");
		else
			sb.append("(tc_keet_nominas.id_empresa in (").append(JsfBase.getAutentifica().getEmpresa().getSucursales()).append(")) and ");
  	regresar.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
		if(!Cadena.isVacio(this.attrs.get("idNomina")) && ((UISelectEntity)this.attrs.get("idNomina")).getKey()>= 1L)
			sb.append("tc_keet_nominas.id_nomina=").append(this.attrs.get("idNomina")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("idProveedor")) && ((UISelectEntity)this.attrs.get("idProveedor")).getKey()>= 1L)
			sb.append("tc_mantic_proveedores.id_proveedor=").append(this.attrs.get("idProveedor")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("ejercicio")) && ((UISelectEntity)this.attrs.get("ejercicio")).getKey()>= 1L)				
			sb.append("tc_keet_nominas_periodos.ejercicio = ").append(((UISelectEntity)this.attrs.get("ejercicio")).getKey()).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("semana")) && ((UISelectEntity)this.attrs.get("semana")).getKey()>= 1L)				
			sb.append("tc_keet_nominas_periodos.orden = ").append(((UISelectEntity)this.attrs.get("semana")).getKey()).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("idTipoNomina")) && ((UISelectEntity)this.attrs.get("idTipoNomina")).getKey()>= 1L)				
			sb.append("tc_keet_nominas.id_tipo_nomina= ").append(((UISelectEntity)this.attrs.get("idTipoNomina")).getKey()).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("estatus")) && ((UISelectEntity)this.attrs.get("estatus")).getKey()>= 1L)				
			sb.append("tc_keet_nominas.id_nomina_estatus = ").append(((UISelectEntity)this.attrs.get("estatus")).getKey()).append(" and ");
		if(this.attrs.get("idDepartamento")!= null && !Cadena.isVacio(this.attrs.get("idDepartamento")) && Long.valueOf(this.attrs.get("idDepartamento").toString())>= 1L)
			sb.append("departamentos.id_departamentos like '%|").append(this.attrs.get("idDepartamento")).append("|%' and ");
		if(this.attrs.get("nombre")!= null && !Cadena.isVacio(this.attrs.get("nombre"))) {
			String nombre= ((String)this.attrs.get("nombre")).toUpperCase().replaceAll("(,| |\\t)+", ".*.*");
  		sb.append("(upper(concat(tc_mantic_proveedores.razon_social, ' ', ifnull(tc_mantic_proveedores.nombre_comercial, ' '), ' ', ifnull(tc_mantic_proveedores.grupo, ' '))) regexp '.*").append(nombre).append(".*') and ");
		} // if
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	} // toPrepare

	private void loadCatalogs() {
		Map<String, Object>params= new HashMap<>();
		try {
			this.loadEmpresas();
		  params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.attrs.put("catalogo", UIEntity.seleccione("TcKeetNominasEstatusDto", "row", params, "nombre"));
      this.attrs.put("estatus", new UISelectEntity(-1L));
			Catalogos.toLoadNominas(this.attrs);
			Catalogos.toLoadEjercicios(this.attrs);
			Catalogos.toLoadSemanas(this.attrs);
			Catalogos.toLoadTiposNominas(this.attrs);
			Catalogos.toLoadDepartamentos(this.attrs);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);	
		} // catch				
    finally {
			Methods.clean(params);
		}	// finally
	} // loadCatalogs

  public String doExportar() {
		String regresar           = null;
		Map<String, Object> params= new HashMap<>();
		try {
			Entity entity= (Entity)this.attrs.get("seleccionado");
			params.put("sortOrder", "order by tc_keet_nominas_rubros.lote, tc_keet_nominas_rubros.codigo");
			params.put("factor", this.attrs.get("factor"));
			params.put("idNomina", entity.toLong("idNomina"));
			params.put("nomina", entity.toString("nomina"));
			params.put("nombreCompleto", entity.toString("nombreCompleto"));
			params.put("idProveedor", entity.toLong("idProveedor"));
			JsfBase.setFlashAttribute(Constantes.REPORTE_REFERENCIA, new ExportarXls(new Modelo((Map<String, Object>) ((HashMap)params).clone(), EExportacionXls.DESTAJO_PROVEEDOR.getProceso(), EExportacionXls.DESTAJO_PROVEEDOR.getIdXml(), EExportacionXls.DESTAJO_PROVEEDOR.getNombreArchivo()), EExportacionXls.DESTAJO_PROVEEDOR, "NOMINA,RAZON SOCIAL,DESARROLLO,CONTRATO,ETAPA,LOTE,CODIGO,CONCEPTO,SUBTOTAL,IVA,TOTAL"));
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
		doReporte(nombre, false);
	} // doReporte	
	
	@Override
	public void doReporte(String nombre, boolean sendMail) throws Exception {    
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;    
    Map<String, Object>params    = new HashMap<>();
    Parametros comunes           = null;
    Entity entity                = null;
		try {		  
      reporteSeleccion= EReportes.valueOf(nombre);
      entity= (Entity)this.attrs.get("seleccionado");
      if(reporteSeleccion.equals(EReportes.NOMINA_SUBCONTRATISTA)){
        params.put("sortOrder", "order by tc_keet_nominas_rubros.lote, tc_keet_nominas_rubros.codigo");
        params.put("idNomina", entity.toLong("idNomina"));
        params.put("idProveedor", entity.toLong("idProveedor"));		
        params.put("idNominaProveedor", entity.toLong("idNominaProveedor"));
        comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa(), entity.toLong("idProveedor"));
      }
      else {
        params = this.toPrepare();
        if(reporteSeleccion.equals(EReportes.LISTADO_NOMINA_PROVEEDORES))
          params.put("sortOrder", "order by	nombre_empresa, nomina, departamentos.departamento, tc_mantic_proveedores.razon_social desc");
        comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      }
			this.attrs.put("tituloCorreo", reporteSeleccion.getTitulo());
      this.reporte= JsfBase.toReporte();
      parametros= comunes.getComunes();
      parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getNombre().toUpperCase());
      parametros.put("REPORTE_TITULO", reporteSeleccion.getTitulo());
      parametros.put("NOMBRE_REPORTE", reporteSeleccion.getNombre());
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));
      this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));		
      if(sendMail)
        this.reporte.doAceptarSimple();			
			else{
				if(doVerificarReporte())
					this.reporte.doAceptar();			
			} // else		
    } // try
    catch(Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);			
    } // catch	
  } // doReporte 			
	
  public void doLoadDetalle(Integer factor) {
    List<Columna> columns    = new ArrayList<>();
		Map<String, Object>params= new HashMap<>();
    try {
      this.attrs.put("factor", factor);
			Entity entity= (Entity)this.attrs.get("seleccionado");
			params.put("sortOrder", "order by tc_keet_nominas_rubros.lote, tc_keet_nominas_rubros.codigo");
			params.put("factor", factor);
			params.put("idNomina", entity.toLong("idNomina"));
			params.put("nomina", entity.toString("nomina"));
			params.put("nombreCompleto", entity.toString("nombreCompleto"));
			params.put("idProveedor", entity.toLong("idProveedor"));
			this.attrs.put("idFiguraCorreo", entity.toLong("idProveedor"));
			this.attrs.put("figuraNombreCompletoCorreo", entity.toString("nombreCompleto"));
      columns.add(new Columna("anticipo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("fondoGarantia", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("subtotal", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("iva", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));
      this.lazyDetalle= new FormatCustomLazy("VistaNominaConsultasDto", "proveedor", params, columns);
      UIBackingUtilities.resetDataTable("detalle");
			this.attrs.put("nomina", true);
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