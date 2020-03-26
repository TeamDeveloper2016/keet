package mx.org.kaana.keet.catalogos.contratos.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.keet.catalogos.contratos.reglas.MotorBusqueda;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.mantic.catalogos.proveedores.reglas.Transaccion;

public class RegistroContrato implements Serializable{
	
	private static final long serialVersionUID = 4690869520445115664L;
	private Long idContrato;
	private Contrato contrato;		
	private List<ContratoPersonal> contratoPersonas;
	private ContratoPersonal contratoPersona;
	private List<IBaseDto> deleteList;
	private ContadoresListas contadores;
	private Long countIndice;
	
	public RegistroContrato() {
		this(-1L, new Contrato(), new ArrayList<ContratoPersonal>(), new ContratoPersonal());
	} // RegistroProveedor

	public RegistroContrato(Long idContrato) {
		this.countIndice= 0L;
		this.idContrato = idContrato;
		this.contadores = new ContadoresListas();		
		this.deleteList = new ArrayList<>();		
		init();
	} // RegistroProveedor

	public RegistroContrato(Long idContrato, Contrato contrato, List<ContratoPersonal> contratoPersonas, ContratoPersonal contratoPersona) {
		this.idContrato      = idContrato;
		this.contrato        = contrato;		
		this.deleteList      = new ArrayList<>();
		this.contadores      = new ContadoresListas(); 
		this.countIndice     = 0L;				
		this.contratoPersonas= contratoPersonas;
		this.contratoPersona = contratoPersona;
	} // RegistroProveedor	

	public Long getIdContrato() {
		return idContrato;
	}

	public void setIdContrato(Long idContrato) {
		this.idContrato = idContrato;
	}

	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}

	public List<ContratoPersonal> getContratoPersonas() {
		return contratoPersonas;
	}

	public void setContratoPersonas(List<ContratoPersonal> contratoPersonas) {
		this.contratoPersonas = contratoPersonas;
	}

	public ContratoPersonal getContratoPersona() {
		return contratoPersona;
	}

	public void setContratoPersona(ContratoPersonal contratoPersona) {
		this.contratoPersona = contratoPersona;
	}

	public List<IBaseDto> getDeleteList() {
		return deleteList;
	}

	public void setDeleteList(List<IBaseDto> deleteList) {
		this.deleteList = deleteList;
	}
	
	private void init(){
		MotorBusqueda motorBusqueda= null;
		try {
			motorBusqueda= new MotorBusqueda(this.idContrato);
			this.contrato= motorBusqueda.toContrato();								
			this.contratoPersonas= motorBusqueda.toPersonas();
		} // try // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
	} // init		
	
	public void doAgregarProveedorMaterial(UISelectEntity seleccionado){
		ContratoPersonal empleado= null;
		try {					
			empleado= new ContratoPersonal(this.contadores.getTotalContratoPersona()+ this.countIndice, ESql.INSERT, true);							
			empleado.setNombres(seleccionado.toString("nombres"));
			empleado.setPaterno(seleccionado.toString("paterno"));
			empleado.setMaterno(seleccionado.toString("materno"));
			empleado.setCurp(seleccionado.toString("curp"));
			empleado.setRfc(seleccionado.toString("rfc"));
			empleado.setDepartamento(seleccionado.toString("departamento"));
			empleado.setPuesto(seleccionado.toString("puesto"));
			empleado.setIdActivo(seleccionado.toLong("idActivo"));
			this.contratoPersonas.add(empleado);					
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarProveedorMaterial
	
	public void doEliminarProveedorMaterial(){
		try {			
			if(this.contratoPersonas.remove(this.contratoPersona)){
				if(!this.contratoPersona.getNuevo())
					addDeleteList(this.contratoPersona);
				JsfBase.addMessage("Se eliminó correctamente el empleado.", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue porsible eliminar el empleado.", ETipoMensaje.INFORMACION);
		} // try // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarProveedorMaterial
	
	private void addDeleteList(IBaseDto dto) throws Exception{
		Transaccion transaccion= null;
		try {
			transaccion= new Transaccion(dto);
			transaccion.ejecutar(EAccion.DEPURAR);
			//this.deleteList.add(dto);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // addDeleteList
}
