package mx.org.kaana.keet.catalogos.contratos.destajos.backing;

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
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value = "keetCatalogosContratosDestajosConceptos")
@ViewScoped
public class Conceptos extends IBaseFilter implements Serializable {

	private static final long serialVersionUID = 2847354766000406350L;  
  private static final Log LOG = LogFactory.getLog(Conceptos.class);
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
    EOpcionesResidente opcion= null;
    try {
      switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
        case "gylvi": 
    			this.attrs.put("isAdmin", Boolean.TRUE);						
          break;
        case "cafu":
        case "triana":
        default:
    			this.attrs.put("isAdmin", JsfBase.isAdminEncuestaOrAdmin());						
          break;
      } // swtich
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());	
			this.attrs.put("georreferencia", JsfBase.getFlashAttribute("georreferencia"));
			this.attrs.put("opcionAdicional", JsfBase.getFlashAttribute("opcionAdicional"));
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("figura", (Entity)JsfBase.getFlashAttribute("figura"));      
			this.attrs.put("casa", JsfBase.getFlashAttribute("casa"));      
			this.attrs.put("seleccionadoPivote", (Entity)JsfBase.getFlashAttribute("seleccionado"));
			this.attrs.put("idDesarrollo", (Long)JsfBase.getFlashAttribute("idDesarrollo"));      
			this.attrs.put("idDepartamento", (Long)JsfBase.getFlashAttribute("idDepartamento"));
			this.attrs.put("nombreConcepto", JsfBase.getFlashAttribute("nombreConcepto")!= null? JsfBase.getFlashAttribute("nombreConcepto"): "");      			
			this.attrs.put("semana", JsfBase.getFlashAttribute("semana"));
      this.attrs.put("contrato", JsfBase.getFlashAttribute("contrato"));
			this.attrs.put("manzana", JsfBase.getFlashAttribute("manzana"));	
			this.loadCatalogos();						
			this.doLoad();
			if(JsfBase.isAdminEncuestaOrAdmin())
				this.doLoadExtras();
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void loadCatalogos() {
		Entity contrato          = null;
		Entity contratoLote      = null;
		Map<String, Object>params= null;
		try {
      Entity seleccion= (Entity)this.attrs.get("seleccionadoPivote");
      if(seleccion!= null && seleccion.containsKey("idContrato")) {
			  params= new HashMap<>();
			  params.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato= ".concat(seleccion.toString("idContrato")));
			  contrato= (Entity) DaoFactory.getInstance().toEntity("VistaContratosLotesDto", "principal", params);
			  this.attrs.put("contratos", contrato);
			  params.put(Constantes.SQL_CONDICION, "tc_keet_contratos_lotes.id_contrato_lote= "+ seleccion.getKey());
			  contratoLote= (Entity) DaoFactory.getInstance().toEntity("TcKeetContratosLotesDto", "row", params);
			  this.attrs.put("contratoLote", contratoLote);
      } // if
      else {
			  this.attrs.put("contratos", new Entity());
        this.attrs.put("contratoLote", new Entity());
      } // if
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
		Map<String, Object>params= null;
    List<Columna> columns    = null;				
    try {      			
			params= this.toPrepare();
      columns= new ArrayList<>();      
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("costo", EFormatoDinamicos.MONEDA_CON_DECIMALES));                  
      columns.add(new Columna("anticipo", EFormatoDinamicos.MONEDA_CON_DECIMALES));                  
	    this.lazyModel= new FormatLazyModel("VistaCapturaDestajosDto", "conceptos", params, columns);			
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
		Map<String, Object>params= null;
    List<Columna> columns    = null;				
    UISelectEntity figura    = (UISelectEntity)this.attrs.get("figura");
    try {      			
			params= this.toPrepare();
      columns= new ArrayList<>();      
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("costo", EFormatoDinamicos.MONEDA_CON_DECIMALES));    
      params.put("idProveedor", -1L);
      params.put("idEmpresaPersona", -1L);
      if(figura!= null)
        if(Objects.equals(figura.toLong("tipo"), 1L))
          params.put("idEmpresaPersona", new Long(figura.getKey().toString().substring(4)));
        else
          params.put("idProveedor", new Long(figura.getKey().toString().substring(4)));
      else
        JsfBase.addMessage("Figura", "No se tiene un contratista o subcontratita seleccionado");
	    this.lazyModelExtras= new FormatLazyModel("VistaCapturaDestajosDto", "conceptosExtras", params, columns);			
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
  } // doLoad	
	
	private Map<String, Object> toPrepare(){
		Map<String, Object> regresar= null;
		try {
			regresar= new HashMap<>();
			regresar.put("idDepartamento", this.attrs.get("idDepartamento"));
			regresar.put("clave", this.toClaveEstacion());
			regresar.put("estatus", EEstacionesEstatus.INICIAR.getKey() + "," + EEstacionesEstatus.EN_PROCESO.getKey() + "," + EEstacionesEstatus.TERMINADO.getKey());			
			regresar.put("nombreConcepto", this.attrs.get("nombreConcepto").toString().toUpperCase());			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toPrepare
	
	private String toClaveEstacion() {
		StringBuilder regresar= null;
		try {			
			regresar= new StringBuilder();
      if(this.attrs.get("seleccionadoPivote")!= null && !((Entity)this.attrs.get("seleccionadoPivote")).isEmpty()) {
  			regresar.append(Cadena.rellenar(((Entity)this.attrs.get("seleccionadoPivote")).toString("idEmpresa"), 3, '0', true));
			  regresar.append(((Entity)this.attrs.get("seleccionadoPivote")).toString("ejercicio"));
			  regresar.append(Cadena.rellenar(((Entity)this.attrs.get("seleccionadoPivote")).toString("ordenContrato"), 3, '0', true));
			  regresar.append(Cadena.rellenar(((Entity)this.attrs.get("seleccionadoPivote")).toString("orden"), 3, '0', true));
      } // if  
      else {
  			regresar.append(Cadena.rellenar(String.valueOf(JsfBase.getAutentifica().getEmpresa().getIdEmpresa()), 3, '0', true));
			  regresar.append(Fecha.getAnioActual());
			  regresar.append(Cadena.rellenar("9", 6, '9', true));
      } // else 
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
			this.toSetFlash(seleccionado);
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
  } // doPagina
	
	private void toSetFlash(Entity seleccionado) {
		JsfBase.setFlashAttribute("claveEstacion", this.toClaveEstacion());									
		JsfBase.setFlashAttribute("opcionResidente", this.attrs.get("opcionResidente"));									
		JsfBase.setFlashAttribute("figura", this.attrs.get("figura"));									
		JsfBase.setFlashAttribute("seleccionado", this.attrs.get("seleccionadoPivote"));									
		JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));									
		JsfBase.setFlashAttribute("idDepartamento", this.attrs.get("idDepartamento"));									
		if(seleccionado!= null) {
			JsfBase.setFlashAttribute("concepto", seleccionado);	
  		JsfBase.setFlashAttribute("total", seleccionado.containsKey("importe")? seleccionado.toDouble("importe"): 0L);
  		JsfBase.setFlashAttribute("anticipo", seleccionado.containsKey("retencion")? seleccionado.toDouble("retencion"): 0L);
    } // if  
    else {
      JsfBase.setFlashAttribute("total", 0D);
      JsfBase.setFlashAttribute("anticipo", 0D);
    } // else
		JsfBase.setFlashAttribute("georreferencia", this.attrs.get("georreferencia"));
		JsfBase.setFlashAttribute("opcionAdicional", this.attrs.get("opcionAdicional"));			
		JsfBase.setFlashAttribute("nombreConcepto", this.attrs.get("nombreConcepto"));			
  	JsfBase.setFlashAttribute("semana", this.attrs.get("semana"));			
  	JsfBase.setFlashAttribute("contrato", this.attrs.get("contratos"));			
	  JsfBase.setFlashAttribute("manzana", this.attrs.get("manzana"));	
	} // toSetFlash
	
	public String doCancelar() {
    String regresar          = null;    
		EOpcionesResidente opcion= null;		
    try {			
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("idDesarrolloProcess", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("figura", this.attrs.get("figura"));
			JsfBase.setFlashAttribute("casa", this.attrs.get("casa"));
			JsfBase.setFlashAttribute("idDepartamento", this.attrs.get("idDepartamento"));									
			JsfBase.setFlashAttribute("opcionResidente", opcion);			
			JsfBase.setFlashAttribute("opcionAdicional", this.attrs.get("opcionAdicional"));			
			JsfBase.setFlashAttribute("semana", this.attrs.get("semana"));			
			JsfBase.setFlashAttribute("contrato", this.attrs.get("contrato"));			
			JsfBase.setFlashAttribute("manzana", this.attrs.get("manzana"));	
			regresar= "filtro".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
}