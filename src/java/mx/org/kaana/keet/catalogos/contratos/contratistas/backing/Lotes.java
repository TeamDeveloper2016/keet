package mx.org.kaana.keet.catalogos.contratos.contratistas.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosContratosContratistasLotes")
@ViewScoped
public class Lotes extends IBaseFilter implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;			
	private List<Entity> lotes;

	public List<Entity> getLotes() {
		return lotes;
	}

	public void setLotes(List<Entity> lotes) {
		this.lotes = lotes;
	}
	
  @PostConstruct
  @Override
  protected void init() {		
    try {						
      this.attrs.put("idContrato", JsfBase.getFlashAttribute("idContrato"));			
			doLoadContrato();
			doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void doLoadContrato(){
		Entity contrato          = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato=".concat(this.attrs.get("idContrato").toString()));
			contrato= (Entity) DaoFactory.getInstance().toEntity("VistaContratosLotesDto", "principal", params);
			this.attrs.put("contrato", contrato);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // doLoadContrato
	
  @Override
  public void doLoad() {
    List<Columna> columns= null;		
    try {      
      columns= new ArrayList<>();      
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));                  
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));                  
	    this.lotes= DaoFactory.getInstance().toEntitySet("TcKeetContratosLotesDto", "byContratoContratistas", this.attrs);			
			if(!this.lotes.isEmpty()) {
			  UIBackingUtilities.toFormatEntitySet(this.lotes, columns);
				this.lotes.add(0, this.toLoteDefault());
			} // if	
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {      
      Methods.clean(columns);
    } // finally		
  } // doLoad	
	
	private Entity toLoteDefault() {
		Entity regresar= new Entity(Constantes.USUARIO_INACTIVO);
		regresar.put("clave", new Value("clave", "ASIGNACIÓN GENERAL"));
		regresar.put("manzana", new Value("manzana", "00N"));
		regresar.put("lote", new Value("lote", "N"));
		regresar.put("fechaInicio", new Value("fechaInicio", "-"));
		regresar.put("fechaTermino", new Value("fechaTermino", "-"));
		regresar.put("diasConstruccion", new Value("diasConstruccion", "-"));
		regresar.put("contratistas", new Value("contratistas", ""));
		regresar.put("ejercicio", new Value("ejercicio", Fecha.getAnioActual()));
		return regresar;
	} // toLoteDefault
	
	public String doCancelar() {
    String regresar          = null;    		
    try {			
			JsfBase.setFlashAttribute("idContratoProcess", this.attrs.get("idContrato"));			
			regresar= "filtro".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina
	
	public String doRegistrar() {
    String regresar    = null;    		
		Entity seleccionado= null;
    try {			
			seleccionado= (Entity) this.attrs.get("seleccionado");
			JsfBase.setFlashAttribute("idContrato", this.attrs.get("idContrato"));			
			JsfBase.setFlashAttribute("idContratoLote", seleccionado.getKey());			
			if(seleccionado.getKey().equals(Constantes.USUARIO_INACTIVO))
				regresar= "asignacion".concat(Constantes.REDIRECIONAR);
			else
				regresar= "registro".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina
}