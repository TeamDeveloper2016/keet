package mx.org.kaana.keet.catalogos.contratos.contratistas.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.beans.SelectionItem;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.catalogos.contratos.contratistas.beans.ContratistaLote;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.catalogos.contratos.contratistas.reglas.Transaccion;
import mx.org.kaana.keet.catalogos.contratos.reglas.MotorBusqueda;
import mx.org.kaana.keet.comun.Catalogos;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

@Named(value = "keetCatalogosContratosContratistasRegistro")
@ViewScoped
public class Registro extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;			
	protected List<SelectionItem> allEmpleados;	
	protected List<SelectionItem> movimientosAdd;	
	protected List<SelectionItem> movimientosRemove;	
	protected List<SelectionItem> temporalOrigen;
	protected List<SelectionItem> temporalDestino;	
	protected DualListModel model;		

	public DualListModel getModel() {
		return model;
	}

	public void setModel(DualListModel model) {
		this.model = model;
	}	
	
  @PostConstruct
  @Override
  protected void init() {				
    try {						
			this.attrs.put("idContrato", (Long) JsfBase.getFlashAttribute("idContrato"));
			this.attrs.put("idContratoLote", (Long) JsfBase.getFlashAttribute("idContratoLote"));
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());						
			this.inicializaContenido();			
			this.loadCatalogos();
			Catalogos.toLoadEspecialidades(this.attrs);
			this.doLoad();
    } // try 
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
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato=".concat(this.attrs.get("idContrato").toString()));
			contrato= (Entity) DaoFactory.getInstance().toEntity("VistaContratosLotesDto", "principal", params);
			this.attrs.put("contrato", contrato);
			params.clear();
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos_lotes.id_contrato_lote=".concat(this.attrs.get("idContratoLote").toString()));
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
	
	private void inicializaContenido(){
		this.allEmpleados= new ArrayList<>();
		this.movimientosAdd= new ArrayList<>();
		this.movimientosRemove= new ArrayList<>();
		this.temporalOrigen= new ArrayList<>();
		this.temporalDestino= new ArrayList<>();
		this.model= new DualListModel<>();				
	}  
	
  public void doLoad() {    		
		List<ContratistaLote>disponibles= null;
		List<ContratistaLote>asignados  = null;
		List<SelectionItem>sDisponibles  = null;
		List<SelectionItem>sAsignados    = null;
		MotorBusqueda motorBusqueda      = null;
		String condicion                 = null;
		String condicionProveedor        = null;
    try {			
			motorBusqueda= new MotorBusqueda((Long)this.attrs.get("idContratoLote"));
			condicion= this.toPrepare();
			condicionProveedor= this.toPrepareProveedor();
			disponibles= motorBusqueda.toContratistasDisponibles(condicion, condicionProveedor);
			sDisponibles= toListSelectionIten(disponibles);
			asignados= motorBusqueda.toContratistasAsignados(condicion, condicionProveedor);
			sAsignados= toListSelectionIten(asignados);
			this.temporalOrigen= sDisponibles;
			this.temporalDestino= sAsignados;				
			this.loadAllEmpleados(sAsignados, sDisponibles);								
			this.model.setSource(sDisponibles);
			this.model.setTarget(sAsignados);					
			this.attrs.put("totalDisponibles", this.model.getSource().size());
			this.attrs.put("totalAsignados", this.model.getTarget().size());
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch    		
  } // doLoad			
	
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
		if(this.attrs.get("idDepartamento")!= null && !Cadena.isVacio(this.attrs.get("idDepartamento")) && Long.valueOf(this.attrs.get("idDepartamento").toString())>= 1L)
			sb.append("tc_keet_departamentos.id_departamento=").append(this.attrs.get("idDepartamento")).append(" ");		
		return Cadena.isVacio(sb)? Constantes.SQL_VERDADERO: sb.toString();
	} // toPrepare
	
	private String toPrepareProveedor() {
		StringBuilder sb= new StringBuilder();		
		if(this.attrs.get("idDepartamento")!= null && !Cadena.isVacio(this.attrs.get("idDepartamento")) && Long.valueOf(this.attrs.get("idDepartamento").toString())>= 1L)
			sb.append("tc_mantic_proveedores.id_proveedor in (select id_proveedor from tc_keet_proveedores_departamentos where id_departamento=").append(this.attrs.get("idDepartamento")).append(") ");		
		return Cadena.isVacio(sb)? Constantes.SQL_VERDADERO: sb.toString();
	} // toPrepare
	
	private List<SelectionItem> toListSelectionIten(List<ContratistaLote> entities) {
		List<SelectionItem> regresar= new ArrayList<>();
		for(ContratistaLote item: entities)
			regresar.add(new SelectionItem(String.valueOf(item.getIdKey()), "[".concat(item.getDepartamento()).concat("] [").concat(item.getPuesto()).concat("] ").concat(item.getNombres()).concat(" ").concat(item.getPaterno()).concat(" ").concat(item.getMaterno()), item.getIdActivo(), item.getIdNomina(), item.getNss(), item.getTipo()));
		return regresar;
	} // toListSelectionIten	
	
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
			transaccion= new Transaccion((Long)this.attrs.get("idContratoLote"), empleados);
			if(transaccion.ejecutar(accion))
				JsfBase.addMessage("Registro de contratistas en el desarrollo.", "Se ".concat(accion.equals(EAccion.PROCESAR) ? "asignaron" : "desasignaron").concat(" de forma correcta los contratistas."), ETipoMensaje.INFORMACION);			
			else
				JsfBase.addMessage("Registro de contratistas en el desarrollo.", "Ocurrió un error al ".concat(accion.equals(EAccion.PROCESAR) ? "asignar" : "desasignar").concat(" los contratistas."), ETipoMensaje.ERROR);			
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch					
	} // onTransfer 		
	
	public String doCancelar() {
    String regresar= null;    		
    try {			
			JsfBase.setFlashAttribute("idContrato", this.attrs.get("idContrato"));			
			regresar= "lotes".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
}