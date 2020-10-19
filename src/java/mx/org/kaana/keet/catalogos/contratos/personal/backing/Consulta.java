package mx.org.kaana.keet.catalogos.contratos.personal.backing;

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
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.comun.Catalogos;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.incidentes.ETipoIndicente;

@Named(value = "keetCatalogosContratosPersonalConsulta")
@ViewScoped
public class Consulta extends IBaseFilter implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;		
	protected FormatLazyModel totales;	
	protected Reporte reporte;
	
	public Reporte getReporte() {
		return reporte;
	}	// getReporte
	
	public FormatLazyModel getTotales() {
		return totales;
	}

	public void setTotales(FormatLazyModel totales) {
		this.totales=totales;
	}
	
	@PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());						
      this.attrs.put("idSeguro", -1L);						
			this.loadCatalogos();
			this.doLoad();
			this.toTotales();
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
  
	private void loadCatalogos() throws Exception {
    this.doLoadDesarrollos();
		Catalogos.toLoadDepartamentos(this.attrs);
		Catalogos.toLoadPuestos(this.attrs);
		Catalogos.toLoadContratistas(this.attrs);
	} // loadCatalogos	
	
	@Override
  public void doLoad() {   
		Map<String, Object>params= null;
		List<Columna>columns     = null;		
    try {			
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, this.toPrepare());
      params.put("sortOrder", "order by principal.departamento, concat(tc_mantic_personas.nombres,' ', ifnull(tc_mantic_personas.paterno, ''),' ', ifnull(tc_mantic_personas.materno, '')), principal.nombre_completo, principal.puesto");
			params.put("union", "left join");
  		if(!Cadena.isVacio(this.attrs.get("idSinDesarrollo")) && ((Long)this.attrs.get("idSinDesarrollo")).equals(1L)) 
  	    params.put("union", "inner join");
			columns= new ArrayList<>();
			columns.add(new Columna("nombreCompleto", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("departamento", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("puesto", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("desarrollo", EFormatoDinamicos.MAYUSCULAS));
			this.lazyModel= new FormatCustomLazy("VistaContratosDto", "consulta", params, columns);
			UIBackingUtilities.resetDataTable("tabla");	
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch    
		finally{
			this.attrs.put("controlBuqueda", Boolean.TRUE);
		} // finally
  } // doLoad		
	
  private void toTotales() {   
		Map<String, Object>params= null;
		List<Columna>columns     = null;		
    try {			
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			columns= new ArrayList<>();
			columns.add(new Columna("total", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
			columns.add(new Columna("activos", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
			columns.add(new Columna("noActivos", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
			columns.add(new Columna("sinSeguro", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
			this.totales= new FormatLazyModel("VistaContratosDto", "totales", params, columns);
			UIBackingUtilities.resetDataTable("totales");			
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch    
  } // doLoad		
	
	private String toPrepare() {
		StringBuilder sb= new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && !((UISelectEntity)this.attrs.get("idDesarrollo")).getKey().equals(-1L))
		  sb.append("(tc_keet_desarrollos.id_desarrollo= ").append(((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()).append(") and ");		
		if(!Cadena.isVacio(this.attrs.get("idPuesto")) && Long.valueOf(this.attrs.get("idPuesto").toString())>= 1L)
			sb.append("tc_mantic_puestos.id_puesto=").append(this.attrs.get("idPuesto")).append(" and ");
		if(this.attrs.get("idDepartamento")!= null && !Cadena.isVacio(this.attrs.get("idDepartamento")) && Long.valueOf(this.attrs.get("idDepartamento").toString())>= 1L)
			sb.append("tc_keet_departamentos.id_departamento=").append(this.attrs.get("idDepartamento")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("idContratista")) && ((UISelectEntity)this.attrs.get("idContratista")).getKey() >= 1L)			
			if(((UISelectEntity)this.attrs.get("idContratista")).getKey()== 999L)		
				sb.append("tr_mantic_empresa_personal.id_contratista is null and ");
			else
				sb.append("tr_mantic_empresa_personal.id_contratista=").append(this.attrs.get("idContratista")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("idActivo")) && Long.valueOf(this.attrs.get("idActivo").toString())>= 1L)
			sb.append("tr_mantic_empresa_personal.id_activo= ").append(this.attrs.get("idActivo")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("idSeguro")) && Long.valueOf(this.attrs.get("idSeguro").toString())>= 1L)
	    if(Long.valueOf(this.attrs.get("idSeguro").toString())== 1L)		
  			sb.append("(tr_mantic_empresa_personal.id_activo= 1 and tr_mantic_empresa_personal.nss is not null and tr_mantic_empresa_personal.nss!= '') and ");
		  else
	  		sb.append("(tr_mantic_empresa_personal.id_activo= 1 and (tr_mantic_empresa_personal.nss is null or tr_mantic_empresa_personal.nss= '')) and ");
		if(!Cadena.isVacio(this.attrs.get("idSinDesarrollo")) && !((Long)this.attrs.get("idSinDesarrollo")).equals(-1L)) {
       if(((Long)this.attrs.get("idSinDesarrollo"))!= 1L)
 	  		 sb.append("(tc_keet_desarrollos.nombres is null) and ");
    } // if
		if(this.attrs.get("nombre")!= null && !Cadena.isVacio(this.attrs.get("nombre"))) {
			String nombre= ((String)this.attrs.get("nombre")).toUpperCase().replaceAll("(,| |\\t)+", ".*.*");
  		sb.append("(upper(concat(tc_mantic_personas.nombres, ' ', tc_mantic_personas.paterno, ' ', tc_mantic_personas.materno)) regexp '.*").append(nombre).append(".*') and ");
		} // if
		return Cadena.isVacio(sb)? Constantes.SQL_VERDADERO: sb.substring(0, sb.length()- 4);
	} // toPrepare
	
	public String toLegend(Integer index, Entity row) {
		String regresar= row.toString("desarrollo");
	  if(index!= 0)
			if(Cadena.isVacio(row.toLong("idEmpresa")))
				regresar= "TOTAL GENERAL";
		  else
				if(Objects.equals(regresar, "SIN DESARROLLO"))
				  regresar= "TOTAL EMPRESA";
		return regresar;
	}
	
	public void doReporte(String nombre) throws Exception {    
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;    
    Map<String, Object>params    = null;
    Parametros comunes           = null;
		try {		
      params= new HashMap<>();      
      params.put("idPersona", JsfBase.getAutentifica().getPersona().getIdPersona());			
      reporteSeleccion= JsfBase.isAdminEncuestaOrAdmin() ? EReportes.EMPLEADOS : EReportes.EMPLEADOS_DESARROLLO;
      this.reporte= JsfBase.toReporte();
      comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      parametros= comunes.getComunes();
      parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getNombre().toUpperCase());
      parametros.put("REPORTE_TITULO", reporteSeleccion.getTitulo());
      parametros.put("NOMBRE_REPORTE", reporteSeleccion.getTitulo());
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));			
      this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));		
      if(doVerificarReporte())
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
  
  public String doCheckIncidente(Entity row) {
    String regresar          = "";
    Map<String, Object>params= null;
    List<Columna>columns     = null;		
		try {		
      params= new HashMap<>();      
      params.put("idEmpresaPersona", row.toLong("idKey"));
 			columns= new ArrayList<>();
			columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
			columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
      Entity entity= (Entity)DaoFactory.getInstance().toEntity("TcManticIncidentesDto", "incidente", params);
      if(entity!= null && !entity.isEmpty()) {
        UIBackingUtilities.toFormatEntity(entity, columns);
        regresar= "<i class='fa fa-lg ".concat(ETipoIndicente.toIcon(entity.toLong("idTipoIncidente").intValue())).concat("' title='Vigencia: [").concat(entity.toString("inicio")).concat(" al ").concat(entity.toString("termino")).concat("]  Tipo: [").concat(ETipoIndicente.toTitle(entity.toLong("idTipoIncidente").intValue())).concat("]'></i>");
      } // if
    } // try
    catch(Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);			
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
    return regresar;
  } // 
  
	public void doLoadDesarrollos() {
		List<Columna> columns     = null;
    Map<String, Object> params= null;
    try {
			params= new HashMap<>();			
  		params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa in (" + JsfBase.getAutentifica().getEmpresa().getSucursales() + ")");			
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("desarrollos", (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave"));			
			this.attrs.put("idDesarrollo", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("desarrollos")));			
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // doLoadDesarrollos	
  
}