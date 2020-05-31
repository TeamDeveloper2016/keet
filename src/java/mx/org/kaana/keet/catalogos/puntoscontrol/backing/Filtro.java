package mx.org.kaana.keet.catalogos.puntoscontrol.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.template.backing.Reporte;
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

@Named(value = "keetCatalogosPuntosControlFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;
  protected Reporte reporte;

	public Reporte getReporte() {
		return reporte;
	}

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			this.attrs.put("sortOrder", "order by tc_keet_puntos_grupos.registro desc"); 
			if(JsfBase.getFlashAttribute("idPuntoGrupoProcess")!= null){
				this.attrs.put("idPuntoGrupoProcess", JsfBase.getFlashAttribute("idPuntoGrupoProcess"));
				doLoad();
				this.attrs.put("idPuntoGrupoProcess", null);
			} // if
			this.attrs.put("departamentos", UIEntity.seleccione("TcKeetDepartamentosDto", "especialidades",  "nombre"));
			//
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
      columns= new ArrayList<>();
      columns.add(new Columna("paquete", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("departamento", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      this.lazyModel = new FormatCustomLazy("VistaPuntosControlDto", params, columns);
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

  public String doAccion(String accion) {
    EAccion eaccion= null;
		String regresar= null; 
    try {
			regresar= "accion".concat(Constantes.REDIRECIONAR);
      eaccion = EAccion.valueOf(accion.toUpperCase());
      JsfBase.setFlashAttribute("accion", eaccion);      
      JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(accion.toUpperCase()));      
      JsfBase.setFlashAttribute("idPuntoGrupo", eaccion.equals(EAccion.AGREGAR) ? -1L : ((Entity) this.attrs.get("seleccionado")).getKey());
			JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/PuntosControl/filtro");
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
    UISelectEntity cliente        = (UISelectEntity)this.attrs.get("cliente");
    List<UISelectEntity>provedores= (List<UISelectEntity>)this.attrs.get("clientes");
		if(this.attrs.get("idPuntoGrupoProcess")!= null && !Cadena.isVacio(this.attrs.get("idPuntoGrupoProcess")))
			sb.append("tc_keet_puntos_grupos.id_punto_grupo=").append(this.attrs.get("idPuntoGrupoProcess")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("nombre")))
			sb.append("(tc_keet_puntos_grupos.descripcion like '%").append(this.attrs.get("nombre")).append("%') and ");
    if(!Cadena.isVacio(this.attrs.get("departamento")) && ((UISelectEntity)this.attrs.get("departamento")).getKey()>= 1L)				
			sb.append("(tc_keet_departamentos.id_departamento in (").append(((UISelectEntity)this.attrs.get("departamento")).getKey()).append(")) and ");
		if(!Cadena.isVacio(this.attrs.get("numeroPuntoControl")))
			sb.append("(tc_keet_puntos_controles.puntos_control >= ").append(this.attrs.get("numeroPuntoControl")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("nombresPuntoControl")))
			sb.append("(tc_keet_puntos_controles.nombres like '%").append(this.attrs.get("nombresPuntoControl")).append("%') and ");
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	} // toPrepare

  public String doMasivo() {
    JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/PuntosControl/filtro"); 
    return "/Paginas/Mantic/Catalogos/Masivos/importar".concat(Constantes.REDIRECIONAR);
	}
  
  public void doReporte(String nombre) throws Exception {    
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;    
    Map<String, Object>params    = null;
    Parametros comunes           = null;
		try {		  
      comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      reporteSeleccion= EReportes.valueOf(nombre);
      params = this.toPrepare();
      this.reporte= JsfBase.toReporte();
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
  
}