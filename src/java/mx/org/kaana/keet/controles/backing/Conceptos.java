package mx.org.kaana.keet.controles.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.controles.enums.EColoresControles;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetControlesConceptos")
@ViewScoped
public class Conceptos extends IBaseFilter implements Serializable {

	private static final long serialVersionUID = 2847354766000406350L;  
	private FormatLazyModel lazyModelExtras;

	public FormatLazyModel getLazyModelExtras() {
		return lazyModelExtras;
	}

	public void setLazyModelExtras(FormatLazyModel lazyModelExtras) {
		this.lazyModelExtras = lazyModelExtras;
	}
		
  @PostConstruct
  @Override
  protected void init() {		
    try {
			this.attrs.put("isAdmin", JsfBase.isAdminEncuestaOrAdmin());						
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());	
			this.attrs.put("georreferencia", JsfBase.getFlashAttribute("georreferencia"));
			this.attrs.put("opcionAdicional", JsfBase.getFlashAttribute("opcionAdicional"));
			this.attrs.put("opcionResidente", JsfBase.getFlashAttribute("opcionResidente"));
			this.attrs.put("figura", JsfBase.getFlashAttribute("figura"));      
			this.attrs.put("seleccionadoPivote", JsfBase.getFlashAttribute("seleccionado"));      			
			this.attrs.put("idDesarrollo", JsfBase.getFlashAttribute("idDesarrollo"));      
			this.attrs.put("idDepartamento", JsfBase.getFlashAttribute("idDepartamento"));      			
			this.attrs.put("nombreConcepto", JsfBase.getFlashAttribute("nombreConcepto")!= null? JsfBase.getFlashAttribute("nombreConcepto"): "");      			
			this.loadCatalogos();						
			this.doLoad();
			if(JsfBase.isAdminEncuestaOrAdmin())
				this.doLoadExtras();
    } // try 
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void loadCatalogos() {
		Entity contrato          = null;
		Entity contratoLote      = null;
		Map<String, Object>params= new HashMap<>();
		try {
      Entity item= (Entity)this.attrs.get("seleccionadoPivote");
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato=".concat(item.toString("idContrato")));
			contrato= (Entity) DaoFactory.getInstance().toEntity("VistaContratosLotesDto", "referencia", params);
			this.attrs.put("contrato", contrato);
			params.clear();
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos_lotes.id_contrato_lote=".concat(item.toString("idContratoLote")));
			contratoLote= (Entity) DaoFactory.getInstance().toEntity("TcKeetContratosLotesDto", "row", params);
			this.attrs.put("contratoLote", contratoLote);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally	
	} // loadCatalogos		
	
  @Override
  public void doLoad() {
		Map<String, Object>params= this.toPrepare();
    List<Columna> columns    = new ArrayList<>();				
    try {      			
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("costo", EFormatoDinamicos.MONEDA_CON_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
	    this.lazyModel= new FormatLazyModel("VistaCapturaDestajosDto", "controles", params, columns);			
			UIBackingUtilities.resetDataTable();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {      
      Methods.clean(columns);
      Methods.clean(params);
    } // finally		
  } // doLoad	
	  
  public void doLoadExtras() {
		Map<String, Object>params= this.toPrepare();
    List<Columna> columns    = new ArrayList<>();				
    try {      			
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("costo", EFormatoDinamicos.MONEDA_CON_DECIMALES));                  
	    this.lazyModelExtras= new FormatLazyModel("VistaCapturaDestajosDto", "controlesExtras", params, columns);			
			UIBackingUtilities.resetDataTable("tablaExtras");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {      
      Methods.clean(columns);
      Methods.clean(params);
    } // finally		
  } 
	
	private Map<String, Object> toPrepare() {
		Map<String, Object> regresar= new HashMap<>();
		try {
			regresar.put("idDepartamento", this.attrs.get("idDepartamento"));
			regresar.put("clave", toClaveEstacion());
			regresar.put("estatus", EEstacionesEstatus.INICIAR.getKey() + "," + EEstacionesEstatus.EN_PROCESO.getKey() + "," + EEstacionesEstatus.TERMINADO.getKey());			
			regresar.put("nombreConcepto", this.attrs.get("nombreConcepto").toString().toUpperCase());			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toPrepare
	
	private String toClaveEstacion() {
		StringBuilder regresar= new StringBuilder();
		try {			
			regresar.append(Cadena.rellenar(((Entity)this.attrs.get("seleccionadoPivote")).toString("idEmpresa"), 3, '0', true));
			regresar.append(((Entity)this.attrs.get("seleccionadoPivote")).toString("ejercicio"));
			regresar.append(Cadena.rellenar(((Entity)this.attrs.get("seleccionadoPivote")).toString("ordenContrato"), 3, '0', true));
			regresar.append(Cadena.rellenar(((Entity)this.attrs.get("seleccionadoPivote")).toString("orden"), 3, '0', true));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar.toString();
	} // toClaveEstacion
	
	public String doPuntosRevision(Entity seleccionado) {
    String regresar= null;    				
    try {						
			this.toSetFlash(seleccionado);
			regresar= "puntos".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina
	
	public String doImportar(Entity seleccionado) {
    String regresar= null;    				
    try {						
			toSetFlash(seleccionado);
			regresar= "importar".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina
	
	public String doRechazar(Entity seleccionado) {
    String regresar= null;    				
    try {						
			this.toSetFlash(seleccionado);
			regresar= "rechazos".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina
	
	public String doRechazarExtras(Entity seleccionado) {
    String regresar= null;    				
    try {						
			this.toSetFlash(seleccionado);
			regresar= "rechazosExtras".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina
	
	public String doCapturarExtra() {
    String regresar= null;    				
    try {						
			this.toSetFlash(null);
			regresar= "extra".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } 
	
	private void toSetFlash(Entity seleccionado){
		JsfBase.setFlashAttribute("claveEstacion", toClaveEstacion());									
		JsfBase.setFlashAttribute("opcionResidente", this.attrs.get("opcionResidente"));									
		JsfBase.setFlashAttribute("figura", this.attrs.get("figura"));									
		JsfBase.setFlashAttribute("seleccionado", this.attrs.get("seleccionadoPivote"));									
		JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));									
		JsfBase.setFlashAttribute("idDepartamento", this.attrs.get("idDepartamento"));									
		if(seleccionado!= null)
			JsfBase.setFlashAttribute("concepto", seleccionado);	
		JsfBase.setFlashAttribute("georreferencia", this.attrs.get("georreferencia"));
		JsfBase.setFlashAttribute("opcionAdicional", this.attrs.get("opcionAdicional"));			
		JsfBase.setFlashAttribute("nombreConcepto", this.attrs.get("nombreConcepto"));			
	} 
	
	public String doCancelar() {
    String regresar          = null;    
		EOpcionesResidente opcion= null;		
    try {			
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("idDesarrolloProcess", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("figura", this.attrs.get("figura"));
			JsfBase.setFlashAttribute("idDepartamento", this.attrs.get("idDepartamento"));									
			JsfBase.setFlashAttribute("opcionResidente", opcion);			
			JsfBase.setFlashAttribute("opcionAdicional", this.attrs.get("opcionAdicional"));			
			regresar= "control".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } 
  
	public String toColor(Entity row) {
    String color= "";
    for (EColoresControles item: EnumSet.allOf(EColoresControles.class))
      if (row.toString("codigo").startsWith(item.getStart())) {
        color= item.getColor();
        break;
      } // if        
		return color;
	} 
  
}