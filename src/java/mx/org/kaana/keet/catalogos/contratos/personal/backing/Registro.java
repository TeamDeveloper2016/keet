package mx.org.kaana.keet.catalogos.contratos.personal.backing;

import java.io.Serializable;
import java.util.ArrayList;
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
import mx.org.kaana.keet.catalogos.contratos.beans.ContratoPersonal;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.catalogos.contratos.personal.reglas.Transaccion;
import mx.org.kaana.keet.catalogos.contratos.reglas.MotorBusqueda;
import mx.org.kaana.keet.catalogos.desarrollos.beans.RegistroDesarrollo;
import mx.org.kaana.keet.comun.Catalogos;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
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
			doLoad();
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
  
	private void loadCatalogos() {
		try {
			this.registroDesarrollo= new RegistroDesarrollo((Long)this.attrs.get("idDesarrollo"));      
			this.attrs.put("domicilio", toDomicilio());			
			this.loadDepartamentos();
			this.loadPuestos();
			this.loadContratistas();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // loadCatalogos
	
	private void loadDepartamentos() {
		List<UISelectItem> departamentos= null;
		Map<String, Object> params      = null;		
		try {
			params= new HashMap<>();
			if(JsfBase.isAdminEncuestaOrAdmin())
				params.put(Constantes.SQL_CONDICION, "id_oficina in (2,3)");
			else
				params.put(Constantes.SQL_CONDICION, "id_oficina in (2)");			
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
	
	private void inicializaContenido(){
		this.allEmpleados= new ArrayList<>();
		this.movimientosAdd= new ArrayList<>();
		this.movimientosRemove= new ArrayList<>();
		this.temporalOrigen= new ArrayList<>();
		this.temporalDestino= new ArrayList<>();
		this.model= new DualListModel<>();				
	}  
	
  public void doLoad() {    		
		List<ContratoPersonal>disponibles= null;
		List<ContratoPersonal>asignados  = null;
		List<SelectionItem>sDisponibles  = null;
		List<SelectionItem>sAsignados    = null;
		MotorBusqueda motorBusqueda      = null;
		String condicion                 = null;
    try {			
			motorBusqueda= new MotorBusqueda((Long)this.attrs.get("idDesarrollo"));
			condicion= this.toPrepare();
			disponibles= motorBusqueda.toPersonasDisponibles(condicion);
			sDisponibles= toListSelectionIten(disponibles);
			asignados= motorBusqueda.toPersonasAsignadas(condicion);
			sAsignados= toListSelectionIten(asignados);
			this.temporalOrigen= sDisponibles;
			this.temporalDestino= sAsignados;				
			this.loadAllEmpleados(sAsignados, sDisponibles);								
			this.model.setSource(sDisponibles);
			this.model.setTarget(sAsignados);		
			//this.model.setTarget(this.validateControlBusquedaAsignados(sAsignados));		
			this.attrs.put("totalDisponibles", this.model.getSource().size());
			this.attrs.put("totalAsignados", this.model.getTarget().size());
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch    		
  } // doLoad		
	
	private List<SelectionItem> validateControlBusquedaAsignados(List<SelectionItem> sAsignados){		
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
	
	private String toPrepare() {
		StringBuilder sb= new StringBuilder();
		if(this.attrs.get("idPuesto")!= null && !Cadena.isVacio(this.attrs.get("idPuesto")) && Long.valueOf(this.attrs.get("idPuesto").toString())>= 1L)
			sb.append("tc_mantic_puestos.id_puesto=").append(this.attrs.get("idPuesto")).append(" and ");		
		if(this.attrs.get("idDepartamento")!= null && !Cadena.isVacio(this.attrs.get("idDepartamento")) && Long.valueOf(this.attrs.get("idDepartamento").toString())>= 1L)
			sb.append("tc_keet_departamentos.id_departamento=").append(this.attrs.get("idDepartamento")).append(" and ");
		else			
			sb.append("tc_keet_departamentos.id_oficina in (2,3) and ");
		if(this.attrs.get("idContratista")!= null && !Cadena.isVacio(this.attrs.get("idContratista")) && ((UISelectEntity)this.attrs.get("idContratista")).getKey() >= 1L)
      if(((UISelectEntity)this.attrs.get("idContratista")).getKey()== 999L)		
			  sb.append("tr_mantic_empresa_personal.id_contratista is null and ");
			else
  			sb.append("tr_mantic_empresa_personal.id_contratista=").append(this.attrs.get("idContratista")).append(" and ");
		return Cadena.isVacio(sb)? Constantes.SQL_VERDADERO: sb.substring(0, sb.length()-4);
	} // toPrepare
	
	private List<SelectionItem> toListSelectionIten(List<ContratoPersonal> entities) {
		List<SelectionItem> regresar= new ArrayList<>();
		for(ContratoPersonal item: entities)
			regresar.add(new SelectionItem(String.valueOf(item.getIdEmpresaPersona()), item.getDescripcion()));
		return regresar;
	} // toListSelectionIten
	
	private String toDomicilio() {
		StringBuilder regresar= new StringBuilder();
		regresar.append(this.registroDesarrollo.getDomicilio().getCalle()).append(" , ");
		if(!Cadena.isVacio(this.registroDesarrollo.getDomicilio().getNumeroExterior()))
			regresar.append(this.registroDesarrollo.getDomicilio().getNumeroExterior()).append(" , ");
		if(!Cadena.isVacio(this.registroDesarrollo.getDomicilio().getNumeroInterior()))
			regresar.append(this.registroDesarrollo.getDomicilio().getNumeroInterior()).append(" , ");
		regresar.append(this.registroDesarrollo.getDomicilio().getAsentamiento()).append(" , C.P. ");
		regresar.append(this.registroDesarrollo.getDomicilio().getCodigoPostal());
		return regresar.toString();
	} // toDomicilio
	
	public void onTransfer(TransferEvent event) {
		List<SelectionItem> transfers= null;
		List<SelectionItem> empleados= null;
		Transaccion transaccion      = null;
		EAccion accion               = null;
		try {
			transfers= (List<SelectionItem>) event.getItems();
			empleados= new ArrayList<>();
			for(SelectionItem item: transfers){
				if(this.allEmpleados.contains(item))
					empleados.add(this.allEmpleados.get(this.allEmpleados.indexOf(item)));
			} // for
			accion= event.isAdd() ? EAccion.PROCESAR : EAccion.DEPURAR;
			transaccion= new Transaccion((Long)this.attrs.get("idDesarrollo"), empleados);
			if(transaccion.ejecutar(accion))
				JsfBase.addMessage("Registro de empleados en el desarrollo.", "Se ".concat(accion.equals(EAccion.PROCESAR) ? "asignaron" : "desasignaron").concat(" de forma correcta los empleados."), ETipoMensaje.INFORMACION);			
			else
				JsfBase.addMessage("Registro de empleados en el desarrollo.", "Ocurrió un error al ".concat(accion.equals(EAccion.PROCESAR) ? "asignar" : "desasignar").concat(" los empleados."), ETipoMensaje.ERROR);
			/*if(event.isAdd()){				
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
			} // else*/
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
			transaccion= new Transaccion((Long)this.attrs.get("idDesarrollo"), this.model.getTarget());
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
			regresar= opcion.getRetorno().concat(Constantes.REDIRECIONAR_AMPERSON);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina		
}