package mx.org.kaana.keet.prestamos.proveedor.backing;

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
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.keet.prestamos.proveedor.beans.RegistroAnticipo;
import mx.org.kaana.keet.prestamos.proveedor.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EReportes;

@Named(value = "keetPrestamosProveedorFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

	private static final long serialVersionUID = 6319984968937774153L;
  
	private LocalDate inicio, termino;
  protected Reporte reporte;
	
	public Reporte getReporte() {
		return reporte;
	}	// getReporte

	public LocalDate getInicio() {
		return inicio;
	}

	public void setInicio(LocalDate inicio) {
		this.inicio = inicio;
	}

	public LocalDate getTermino() {
		return termino;
	}

	public void setTermino(LocalDate termino) {
		this.termino = termino;
	}

  @PostConstruct
  @Override
  protected void init() {
    try {
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			cargaCatalogos();
			if(JsfBase.getFlashAttribute("idAnticipoProcess")!= null) {
				this.attrs.put("idAnticipoProcess", JsfBase.getFlashAttribute("idAnticipoProcess"));
				this.doLoad();
				this.attrs.put("idAnticipoProcess", null);
			} // if
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
			params.put("sortOrder", "order by tc_keet_anticipos.consecutivo desc");
      columns= new ArrayList<>();
      columns.add(new Columna("deudor", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("saldo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("saldoTotal", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("limite", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("disponible", EFormatoDinamicos.MILES_CON_DECIMALES));
      this.lazyModel = new FormatCustomLazy("VistaAnticiposDto", params, columns);
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
			params= new HashMap<>();
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
    String regresar        = null;
		EAccion eaccion        = null;
		Transaccion transaccion= null;
    try {
      eaccion = EAccion.valueOf(accion.toUpperCase());
      JsfBase.setFlashAttribute("accion", eaccion);      
      JsfBase.setFlashAttribute("nombreAccion", Cadena.letraCapital(accion.toUpperCase()));      
      JsfBase.setFlashAttribute("idAnticipo", (!eaccion.equals(EAccion.AGREGAR)) ? ((Entity) this.attrs.get("seleccionado")).getKey() : -1L);
      JsfBase.setFlashAttribute("idMoroso", (!eaccion.equals(EAccion.AGREGAR)) ? ((Entity) this.attrs.get("seleccionado")).toLong("idMoroso") : -1L);
      JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Prestamos/Proveedor/filtro");
      JsfBase.setFlashAttribute("isLiquidar", true);
			switch (eaccion) {
				case SUBIR:
				  regresar= "importar".concat(Constantes.REDIRECIONAR);	
				break;
				case CONSULTAR:
				case MODIFICAR:
				case AGREGAR:
				  regresar= "accion".concat(Constantes.REDIRECIONAR);
					break;
				case REGISTRAR:
					JsfBase.setFlashAttribute("isLiquidar", false);
				case COMPLETO:
				  regresar= "/Paginas/Keet/Prestamos/Proveedor/Pagos/accion".concat(Constantes.REDIRECIONAR);
					break;
				case MOVIMIENTOS:
				  regresar= "/Paginas/Keet/Prestamos/Proveedor/Pagos/filtro".concat(Constantes.REDIRECIONAR);
					break;
				case DESACTIVAR:
					transaccion= new Transaccion(new RegistroAnticipo(((Entity) this.attrs.get("seleccionado")).getKey()));
					if (transaccion.ejecutar(eaccion)){
						JsfBase.addMessage("El anticipo se canceló correctamente.");
					} // if
					this.doLoad();
					break;
			} // switch
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public void doEliminar() {
		this.doAccion(EAccion.DESACTIVAR.name()); // cancelar anticipo
  } // doEliminar
	
	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar  = new HashMap<>();	
		StringBuilder sb              = new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()>= 1L)				
			sb.append("(tc_mantic_proveedores.id_empresa in (").append(((UISelectEntity)this.attrs.get("idEmpresa")).getKey()).append(")) and ");
		else
			sb.append("(tc_mantic_proveedores.id_empresa in (").append(JsfBase.getAutentifica().getEmpresa().getSucursales()).append(")) and ");
		if(!Cadena.isVacio(this.attrs.get("deudor")) && ((UISelectEntity)this.attrs.get("deudor")).getKey()>= 1L)				
			sb.append("tc_keet_anticipos.id_moroso=").append(this.attrs.get("deudor")).append(" and ");
		if(this.attrs.get("idAnticipoProcess")!= null && !Cadena.isVacio(this.attrs.get("idAnticipoProcess")))
			sb.append("tc_keet_anticipos.id_anticipo=").append(this.attrs.get("idAnticipoProcess")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("consecutivo")))
			sb.append("(tc_keet_anticipos.consecutivo like '%").append(this.attrs.get("consecutivo")).append("%') and ");
		sb.append("date_format(tc_keet_anticipos.registro, '%Y%m%d')>= '").append(inicio.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("' and ");
		sb.append("date_format(tc_keet_anticipos.registro, '%Y%m%d')<= '").append(termino.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("' and ");
		if(!Cadena.isVacio(this.attrs.get("estatus")) && ((UISelectEntity)this.attrs.get("estatus")).getKey()>= 1L)				
			sb.append("tc_keet_anticipos.id_anticipo_estatus = ").append(((UISelectEntity)this.attrs.get("estatus")).getKey()).append(" and ");
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	} // toPrepare

	private void cargaCatalogos() {
		try {
			this.loadEmpresas();
			this.loadEstatus();
			inicio = LocalDate.of(Fecha.getAnioActual(),1, 1);
      termino= LocalDate.now();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);	
		} // catch				
	} // cargaCatalogos
	
	
	private void loadEstatus() {
		Map<String, Object>params= null;
	  try {
			params = new HashMap<>();
		  params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		  params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.attrs.put("catalogo", UIEntity.seleccione("TcKeetAnticiposEstatusDto", "row", params, "nombre"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
			Methods.clean(params);
		}	// finally
	} // loadEstatus
	
	public List<UISelectEntity> doCompleteDeudor(String deudor) {
 		List<Columna> campos      = null;
		UISelectEntity empresa    = null;
    Map<String, Object> params= new HashMap<>();
    try {
			campos= new ArrayList<>();
			campos.add(new Columna("deudor", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("disponible", EFormatoDinamicos.MILES_CON_DECIMALES));
			empresa = this.attrs.get("idEmpresa")==null? null:(UISelectEntity)this.attrs.get("idEmpresa");
			if(empresa!= null && empresa.getKey()> 0L) 
			  params.put("sucursales", empresa.getKey());
			else
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put("deudor", deudor.toUpperCase() );
      this.attrs.put("deudores", UIEntity.seleccione("VistaMorososDto", "complete", params, "deudor"));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(campos);
      Methods.clean(params);
    }// finally
		return (List<UISelectEntity>)this.attrs.get("deudores");
	}	// doCompleteCliente
  
  public void doReporte(String nombre) throws Exception {    
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;    
    Map<String, Object>params    = null;
    Parametros comunes           = null;
		try {		  
      reporteSeleccion= EReportes.valueOf(nombre);
      params= this.toPrepare();
      if(reporteSeleccion.equals(EReportes.ANTICIPOS_PAGOS)) 
        params.put(Constantes.SQL_CONDICION,"tc_keet_anticipos_pagos.id_anticipo=".concat(((Entity) this.attrs.get("seleccionado")).getKey().toString()));
      params.put("sortOrder", "order by tc_keet_anticipos.consecutivo desc");
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
    boolean regresar = this.reporte.getTotal()> 0L;
		if(regresar) 
			UIBackingUtilities.execute("start(" + this.reporte.getTotal() + ")");	
		else {
			UIBackingUtilities.execute("generalHide();");		
			JsfBase.addMessage("Reporte", "No se encontraron registros para el reporte", ETipoMensaje.ERROR);
		} // else
    return regresar;
	} // doVerificarReporte	
  
}