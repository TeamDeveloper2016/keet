package mx.org.kaana.keet.catalogos.contratos.personal.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.catalogos.desarrollos.beans.RegistroDesarrollo;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosContratosPersonalRegistro")
@ViewScoped
public class Registro extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;		
	private RegistroDesarrollo registroDesarrollo;	

	public RegistroDesarrollo getRegistroDesarrollo() {
		return registroDesarrollo;
	}

	public void setRegistroDesarrollo(RegistroDesarrollo registroDesarrollo) {
		this.registroDesarrollo = registroDesarrollo;
	}	
	
  @PostConstruct
  @Override
  protected void init() {
		EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;
    try {
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");			
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("idDesarrollo", idDesarrollo);
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());			
			loadCatalogos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
  
	private void loadCatalogos(){
		try {
			this.registroDesarrollo= new RegistroDesarrollo((Long)this.attrs.get("idDesarrollo"));      
			this.attrs.put("domicilio", toDomicilio());
			loadContratos();
			loadDepartamentos();
			loadPuestos();
			loadContratistas();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // loadCatalogos
	
	private void loadContratos(){
		List<UISelectItem> contratos= null;
		Map<String, Object> params  = null;		
		try {
			params= new HashMap<>();
			params.put("idDesarrollo", this.registroDesarrollo.getDesarrollo().getIdDesarrollo());			
			contratos= UISelect.seleccione("VistaContratosDto", "findDesarrollo", params, "clave", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("contratos", contratos);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally		
	} // loadDepartamentos
	
	private void loadDepartamentos(){
		List<UISelectItem> departamentos= null;
		Map<String, Object> params      = null;		
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);			
			departamentos= UISelect.seleccione("TcKeetDepartamentosDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("departamentos", departamentos);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally		
	} // loadDepartamentos
	
	private void loadPuestos() {
		List<UISelectItem> puestos= null;
    Map<String, Object> params= null;
    try {
      params = new HashMap<>();
      params.put(Constantes.SQL_CONDICION, "id_empresa=" + this.attrs.get("idEmpresa"));
      puestos = UISelect.seleccione("TcManticPuestosDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
			if(!puestos.isEmpty()) {
				this.attrs.put("puestos", puestos);
				this.attrs.put("idPuesto", UIBackingUtilities.toFirstKeySelectItem(puestos));
			} // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
	} // loadPuestos
	
	private void loadContratistas(){
		List<UISelectEntity>contratistas= null;		
		List<Columna> campos            = null;
		try {
			campos= new ArrayList<>();
			campos.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			contratistas= UIEntity.seleccione("VistaPersonasDto", "contratistas", Collections.EMPTY_MAP, campos, Constantes.SQL_TODOS_REGISTROS, "nombres");
			this.attrs.put("contratistas", contratistas);
			this.attrs.put("idContratista", UIBackingUtilities.toFirstKeySelectEntity(contratistas));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // loadContratistas
	
  public void doLoad() {    
    try {
			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch    
  } // doLoad		
	
	private String toDomicilio(){
		StringBuilder regresar= null;
		try {
			regresar= new StringBuilder();
			regresar.append(this.registroDesarrollo.getDomicilio().getCalle()).append(" , ");
			if(!Cadena.isVacio(this.registroDesarrollo.getDomicilio().getNumeroExterior()))
				regresar.append(this.registroDesarrollo.getDomicilio().getNumeroExterior()).append(" , ");
			if(!Cadena.isVacio(this.registroDesarrollo.getDomicilio().getNumeroInterior()))
				regresar.append(this.registroDesarrollo.getDomicilio().getNumeroInterior()).append(" , ");
			regresar.append(this.registroDesarrollo.getDomicilio().getAsentamiento()).append(" , C.P. ");
			regresar.append(this.registroDesarrollo.getDomicilio().getCodigoPostal());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar.toString();
	} // toDomicilio
	
	public String doCancelar() {
    String regresar          = null;    
		EOpcionesResidente opcion= null;
    try {
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("idDesarrolloProcess", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("opcion", opcion);			
			regresar= opcion.getRetorno();			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina	
}