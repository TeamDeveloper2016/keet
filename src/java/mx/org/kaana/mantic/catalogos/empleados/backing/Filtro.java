package mx.org.kaana.mantic.catalogos.empleados.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.procesos.reportes.beans.ExportarXls;
import mx.org.kaana.kajool.procesos.reportes.beans.Modelo;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.keet.comun.Catalogos;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EExportacionXls;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.enums.ETipoPersona;

@Named(value = "manticCatalogosEmpleadosFiltro")
@ViewScoped
public class Filtro extends mx.org.kaana.mantic.catalogos.personas.backing.Filtro implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;

	protected Reporte reporte;
	
	public Reporte getReporte() {
		return reporte;
	}	// getReporte
	
  @PostConstruct
  @Override
  protected void init() {
    try {			
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("nombres", "");
			this.attrs.put("paterno", "");
			this.attrs.put("materno", "");
			this.attrs.put("rfc", "");
			this.attrs.put("curp", "");
      this.attrs.put("sortOrder", "order by tc_mantic_personas.nombres");     
      this.attrs.put("idTipoPersona", ETipoPersona.EMPLEADO.getIdTipoPersona());
			this.loadEmpresas();
			this.loadTiposPersonas();
			this.loadContratistas();
			if(JsfBase.getFlashAttribute("idPersona")!= null){
				this.attrs.put("idPersona", JsfBase.getFlashAttribute("idPersona"));
				this.doLoad();
				this.attrs.put("idPersona", null);
			} // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
  
	private void loadEmpresas() {
		Map<String, Object>params= null;
		List<Columna> columns    = null;
		try {
			params= new HashMap<>();
			columns= new ArrayList<>();			
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch				
	} // loadEmpresas
	
	private void loadContratistas() {
		List<UISelectEntity>contratistas= null;		
		try {
			contratistas= Catalogos.toContratistasPorElDia();
			this.attrs.put("contratistas", contratistas);
			this.attrs.put("idContratista", UIBackingUtilities.toFirstKeySelectEntity(contratistas));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // loadContratistas
	
	@Override
  public void doLoad() {
    List<Columna> campos     = null;
		Map<String, Object>params= null;
    try {
			params= this.toPrepare();
      campos = new ArrayList<>();
      campos.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      campos.add(new Columna("materno", EFormatoDinamicos.MAYUSCULAS));
      campos.add(new Columna("paterno", EFormatoDinamicos.MAYUSCULAS));
      campos.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      campos.add(new Columna("curp", EFormatoDinamicos.MAYUSCULAS));
      campos.add(new Columna("sexo", EFormatoDinamicos.MAYUSCULAS));
      campos.add(new Columna("tipoPersona", EFormatoDinamicos.MAYUSCULAS));           
      this.lazyModel = new FormatCustomLazy("VistaPersonasDto", "filter", params, campos);
      UIBackingUtilities.resetDataTable();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(campos);
      Methods.clean(params);
    } // finally		
  } // doLoad

	private Map<String, Object> toPrepare(){
		StringBuilder sb= new StringBuilder("");			
		if(!Cadena.isVacio(this.attrs.get("salarioMenor")))
			sb.append("tr_mantic_empresa_personal.sueldo_mensual<").append(this.attrs.get("salarioMenor")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("salarioMayor")))
			sb.append("tr_mantic_empresa_personal.sueldo_mensual>").append(this.attrs.get("salarioMayor")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("clave")))
			sb.append("tr_mantic_empresa_personal.clave like '%").append(this.attrs.get("clave")).append("%' and ");
		if(!Cadena.isVacio(this.attrs.get("idPersona")))
			sb.append("tc_mantic_personas.id_persona=").append(this.attrs.get("idPersona")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("nombres")))
			sb.append("tc_mantic_personas.nombres like '%").append(this.attrs.get("nombres")).append("%' and ");
		if(!Cadena.isVacio(this.attrs.get("paterno")))
			sb.append("tc_mantic_personas.paterno like '%").append(this.attrs.get("paterno")).append("%' and ");
		if(!Cadena.isVacio(this.attrs.get("materno")))
			sb.append("tc_mantic_personas.materno like '%").append(this.attrs.get("materno")).append("%' and ");
		if(!Cadena.isVacio(this.attrs.get("rfc")))
			sb.append("tc_mantic_personas.rfc like '%").append(this.attrs.get("rfc")).append("%' and ");
		if(!Cadena.isVacio(this.attrs.get("curp")))
			sb.append("tc_mantic_personas.curp like '%").append(this.attrs.get("curp")).append("%' and ");			
		sb.append("tc_mantic_personas.id_tipo_persona in (").append(this.attrs.get("idTipoPersona")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idContratista")) && ((UISelectEntity)this.attrs.get("idContratista")).getKey()>= 1L)
      if(((UISelectEntity)this.attrs.get("idContratista")).getKey()== 999L)		
			  sb.append("tr_mantic_empresa_personal.id_contratista is null and ");
			else
			  sb.append("tr_mantic_empresa_personal.id_contratista=").append(((UISelectEntity)this.attrs.get("idContratista")).getKey()).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()>= 1L)				
			sb.append("tr_mantic_empresa_personal.id_empresa in (").append(((UISelectEntity)this.attrs.get("idEmpresa")).getKey()).append(") and ");
		else
			sb.append("tr_mantic_empresa_personal.id_empresa in (").append(JsfBase.getAutentifica().getEmpresa().getSucursales()).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idActivo")))
			sb.append("tr_mantic_empresa_personal.id_activo in (").append(this.attrs.get("idActivo")).append(") and ");			
		if(!Cadena.isVacio(this.attrs.get("idSeguro")))
			sb.append("tr_mantic_empresa_personal.id_seguro in (").append(this.attrs.get("idSeguro")).append(") and ");			
		if(!Cadena.isVacio(this.attrs.get("departamento")) && Long.valueOf(this.attrs.get("departamento").toString())>=1)
			sb.append("tr_mantic_empresa_personal.id_departamento=").append(this.attrs.get("departamento")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("puesto")) && Long.valueOf(this.attrs.get("puesto").toString())>=1)
			sb.append("tc_mantic_puestos.id_puesto=").append(this.attrs.get("puesto")).append(" and ");
		Map<String, Object> regresar= new HashMap<>();
		regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;
	} // toPrepare  
	
	@Override
	public String doAccion(String accion) {
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("tipoPersona", ETipoPersona.EMPLEADO.getIdTipoPersona());		
			JsfBase.setFlashAttribute("retorno", "filtro");		
			JsfBase.setFlashAttribute("idPersona", (eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR)) ? ((Entity)this.attrs.get("seleccionado")).getKey() : -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return "accion".concat(Constantes.REDIRECIONAR);
  } // doAccion
	
	public String doExportar() {
		String regresar          = null;		
		Map<String, Object>params= null;
		try {									   
			params= this.toPrepare();
			params.put("sortOrder", "order by tr_mantic_empresa_personal.id_empresa, tc_keet_departamentos.nombre, tc_mantic_puestos.nombre, tc_mantic_personas.rfc");
			JsfBase.setFlashAttribute(Constantes.REPORTE_REFERENCIA, new ExportarXls(new Modelo((Map<String, Object>) ((HashMap)params).clone(), EExportacionXls.PERSONAS.getProceso(), EExportacionXls.PERSONAS.getIdXml(), EExportacionXls.PERSONAS.getNombreArchivo()), EExportacionXls.PERSONAS, 
				"SUCURSAL,CODIGO NOMINA,DEPARTAMENTO,PUESTO,RFC,CURP,NOMBRE,1ER APELLIDO,2DO APELLIDO,FECHA CONTRATACION,ACTIVO,SEGURO,NSS,INFONAVIT,FACTOR INFONAVIT,DEPOSITO SEMANAL,SUELDO IMSS SEMANAL"));
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
  
	public void doReporte(String nombre) throws Exception {    
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;    
    Map<String, Object>params    = null;
		try {		
      params= new HashMap<>();      
      //params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());	
      reporteSeleccion= EReportes.valueOf(nombre);
      this.reporte= JsfBase.toReporte();	
      parametros= new HashMap<>();
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
}
