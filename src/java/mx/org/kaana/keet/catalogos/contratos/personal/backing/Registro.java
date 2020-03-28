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
import mx.org.kaana.kajool.beans.SelectionItem;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.contratos.beans.ContratoPersonal;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.catalogos.contratos.personal.reglas.Transaccion;
import mx.org.kaana.keet.catalogos.contratos.reglas.MotorBusqueda;
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
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

@Named(value = "keetCatalogosContratosPersonalRegistro")
@ViewScoped
public class Registro extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;		
	private RegistroDesarrollo registroDesarrollo;	
	protected List<SelectionItem> allEmpleados;	
	protected List<SelectionItem> movimientosAdd;	
	protected List<SelectionItem> movimientosRemove;	
	protected List<SelectionItem> temporalOrigen;
	protected List<SelectionItem> temporalDestino;	
	protected DualListModel model;
	
	public RegistroDesarrollo getRegistroDesarrollo() {
		return registroDesarrollo;
	}

	public void setRegistroDesarrollo(RegistroDesarrollo registroDesarrollo) {
		this.registroDesarrollo = registroDesarrollo;
	}	

	public DualListModel getModel() {
		return model;
	}

	public void setModel(DualListModel model) {
		this.model = model;
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
			this.attrs.put("isResidente", JsfBase.isResidente());
			inicializaContenido();			
			loadCatalogos();
    } // try // try
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
	
	private void inicializaContenido(){
		this.allEmpleados= new ArrayList<>();
		this.movimientosAdd= new ArrayList<>();
		this.movimientosRemove= new ArrayList<>();
		this.temporalOrigen= new ArrayList<>();
		this.temporalDestino= new ArrayList<>();
		this.model= new DualListModel<>();				
	}
	
  public void doLoadByContrato() {    
		this.attrs.put("controlBuqueda", Boolean.FALSE);
		inicializaContenido();
		doLoad();
	}	 // doLoadByContrato
	
  public void doLoad() {    		
		List<ContratoPersonal>disponibles= null;
		List<ContratoPersonal>asignados  = null;
		List<SelectionItem>sDisponibles  = null;
		List<SelectionItem>sAsignados    = null;
		MotorBusqueda motorBusqueda      = null;
		String condicion                 = null;
    try {
			if(this.attrs.get("idContrato")!= null && !Cadena.isVacio(this.attrs.get("idContrato")) && Long.valueOf((String)this.attrs.get("idContrato"))>= 1L){								
				motorBusqueda= new MotorBusqueda(Long.valueOf((String)this.attrs.get("idContrato")));
				condicion= this.toPrepare();
				disponibles= motorBusqueda.toPersonasDisponibles(condicion);
				sDisponibles= toListSelectionIten(disponibles);
				asignados= motorBusqueda.toPersonasAsignadas();
				sAsignados= toListSelectionIten(asignados);
				this.temporalOrigen= sDisponibles;
				this.temporalDestino= sAsignados;				
				loadAllEmpleados(sAsignados, sDisponibles);								
				this.model.setSource(sDisponibles);
				this.model.setTarget(validateControlBusquedaAsignados(sAsignados));
			} // if
			else{				
				inicializaContenido();
				JsfBase.addMessage("Es necesario seleccionar un contrato.");
			} // else
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch    
		finally{
			this.attrs.put("controlBuqueda", Boolean.TRUE);
		} // finally
  } // doLoad		
	
	private List<SelectionItem> validateControlBusquedaAsignados(List<SelectionItem> sAsignados){
		if((Boolean)this.attrs.get("controlBuqueda")){
			if(!this.movimientosAdd.isEmpty()){						
				for(SelectionItem item: this.movimientosAdd){
					if(!sAsignados.contains(item))
						sAsignados.add(0, this.allEmpleados.get(this.allEmpleados.indexOf(item)));
				} // for
			} // if
			if(!this.movimientosRemove.isEmpty()){						
				for(SelectionItem item: this.movimientosRemove){
					if(sAsignados.contains(item)){
						String descripcion= this.allEmpleados.get(this.allEmpleados.indexOf(item)).getItem();
						if(!((boolean)this.attrs.get("isResidente")) || (((boolean)this.attrs.get("isResidente")) && !descripcion.contains("RESIDENTE")))
							sAsignados.remove(item);
					} // if
				} // for
			} // if			
		} // if
		return sAsignados;
	} // validateControlBusquedaAsignados
	
	private void loadAllEmpleados(List<SelectionItem> sAsignados, List<SelectionItem> sDisponibles){
		if(this.allEmpleados.isEmpty()){
			this.allEmpleados.addAll(sAsignados);
			this.allEmpleados.addAll(sDisponibles);
		} // else
		else{
			for(SelectionItem item: sAsignados){
				if(!this.allEmpleados.contains(item))
					this.allEmpleados.add(item);
			} // for
			for(SelectionItem item: sDisponibles){
				if(!this.allEmpleados.contains(item))
					this.allEmpleados.add(item);
			} // for
		} // else
	} // loadAllEmpleados
	
	private String toPrepare(){
		StringBuilder condicion= null;
		String regresar        = null;
		try {
			condicion= new StringBuilder();
			if(this.attrs.get("idPuesto")!= null && !Cadena.isVacio(this.attrs.get("idPuesto")) && Long.valueOf((String)this.attrs.get("idPuesto"))>= 1L)
				condicion.append("tc_mantic_puestos.id_puesto=").append(this.attrs.get("idPuesto")).append(" and ");
			if(this.attrs.get("idDepartamento")!= null && !Cadena.isVacio(this.attrs.get("idDepartamento")) && Long.valueOf((String)this.attrs.get("idDepartamento"))>= 1L)
				condicion.append("tc_keet_departamentos.id_departamento=").append(this.attrs.get("idDepartamento")).append(" and ");
			if(this.attrs.get("idContratista")!= null && !Cadena.isVacio(this.attrs.get("idContratista")) && ((UISelectEntity)this.attrs.get("idContratista")).getKey() >= 1L)
				condicion.append("tr_mantic_empresa_personal.id_contratista=").append(this.attrs.get("idContratista")).append(" and ");
			regresar= Cadena.isVacio(condicion) ? Constantes.SQL_VERDADERO : condicion.substring(0, condicion.length()-4);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toPrepare
	
	private List<SelectionItem> toListSelectionIten(List<ContratoPersonal> entities){
		List<SelectionItem> regresar= null;
		try {
			regresar= new ArrayList<>();
			for(ContratoPersonal ent: entities)
				regresar.add(new SelectionItem(ent.getIdEmpresaPersona().toString(), "[".concat(ent.getPuesto()).concat("] ").concat(ent.getNombres()).concat(" ").concat(ent.getPaterno()).concat(" ").concat(ent.getMaterno())));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toListSelectionIten
	
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
	
	public void onTransfer(TransferEvent event) {				
		List<SelectionItem> transfers= null;
		try {
			transfers= (List<SelectionItem>) event.getItems();
			if(event.isAdd()){
				for(SelectionItem item: transfers){
					if(!this.movimientosAdd.contains(item))
						this.movimientosAdd.add(item);
					if(!this.model.getTarget().contains(item))
						this.model.getTarget().add(item);
				} // for			
			} // if
			else{
				for(SelectionItem item: transfers){
					if(!this.movimientosRemove.contains(item))
						this.movimientosRemove.add(item);
					if(this.model.getTarget().contains(item))
						this.model.getTarget().remove(item);
				} // for
			} // else
		} // try // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch					
	} // onTransfer 	
	
	public String doAceptar(){
		String regresar        = null;
		Transaccion transaccion= null;
		try {
			transaccion= new Transaccion((Long)this.attrs.get("idDesarrollo"), Long.valueOf((String)this.attrs.get("idContrato")), this.model.getTarget());
			if(transaccion.ejecutar(EAccion.PROCESAR)){
				regresar= doCancelar();
				JsfBase.addMessage("Registro de empleados en el desarrollo.", "Se registraron de forma correcta los empleados.", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("Registro de empleados en el desarrollo.", "Ocurrió un error al registrar los empleados.", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		return regresar;
	} // doAceptar
	
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