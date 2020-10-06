package mx.org.kaana.keet.catalogos.contratos.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.catalogos.contratos.reglas.Transaccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.catalogos.contratos.reglas.MotorBusqueda;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class RegistroContrato implements Serializable {
	
	private static final long serialVersionUID = 6253504536549861564L;
	private Long idContrato;
	private Contrato contrato;
	private List<Documento> documentos;
	private List<Presupuesto> presupuestos;
	private List<Generador> generadores;
	private List<ContratoPersonal> contratoPersonas;
	private ContratoPersonal contratoPersona;	
	private ContadoresListas contadores;
	private Long countIndice;

	public RegistroContrato() {
		this(-1L, new Contrato(), new ArrayList<ContratoPersonal>(), new ContratoPersonal(),  new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	} // RegistroProveedor

	public RegistroContrato(Contrato contrato) {
		this(contrato.getIdContrato(), contrato, new ArrayList<ContratoPersonal>(), new ContratoPersonal(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}	
	
	public RegistroContrato(Long idContrato) {
		this.countIndice = 0L;
		this.idContrato  = idContrato;
		this.contadores  = new ContadoresListas();				
		this.documentos  = new ArrayList<>();
		this.presupuestos= new ArrayList<>();
		this.generadores = new ArrayList<>();
		init();
	} // RegistroProveedor

	public RegistroContrato(Long idContrato, Contrato contrato, List<ContratoPersonal> contratoPersonas, ContratoPersonal contratoPersona, List<Documento> documentos, List<Presupuesto> presupuestos, List<Generador> generadores) {
		this.idContrato      = idContrato;
		this.contrato        = contrato;				
		this.contadores      = new ContadoresListas(); 
		this.countIndice     = 0L;				
		this.contratoPersonas= contratoPersonas;
		this.contratoPersona = contratoPersona;
		this.documentos      = documentos;
		this.presupuestos    = presupuestos;
		this.generadores     = generadores;
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
	
	public List<Documento> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<Documento> documentos) {
		this.documentos = documentos;
	}	

	public List<Presupuesto> getPresupuestos() {
		return presupuestos;
	}

	public void setPresupuestos(List<Presupuesto> presupuestos) {
		this.presupuestos = presupuestos;
	}

	public List<Generador> getGeneradores() {
		return generadores;
	}

	public void setGeneradores(List<Generador> generadores) {
		this.generadores = generadores;
	}	
	
	private void init(){
		MotorBusqueda motor= null;
		try {
			motor= new MotorBusqueda(this.idContrato);
			this.contrato= motor.toContrato();
			this.contrato.setLotes(motor.toLotes());
			this.contratoPersonas= new ArrayList<>();
		} // try
		catch (Exception e) {			
			Error.mensaje(e);
		} // catch		
	} // initCollections
	
	public void doAgregarContratoPersona(UISelectEntity seleccionado){
		ContratoPersonal empleado= null;
		try {					
			empleado= new ContratoPersonal(this.contadores.getTotalContratoPersona() + this.countIndice, ESql.INSERT, true);							
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
	
	public void doEliminarContratoPersona(){
		try {			
			if(this.contratoPersonas.remove(this.contratoPersona)){
				if(!this.contratoPersona.getNuevo())
					addDeleteList(this.contratoPersona);
				JsfBase.addMessage("Se eliminó correctamente el empleado.", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el empleado.", ETipoMensaje.INFORMACION);
		} // try // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarContratoPersona
	
	private void addDeleteList(IBaseDto dto) throws Exception{
		Transaccion transaccion= null;
		try {
			transaccion= new Transaccion(dto);
			transaccion.ejecutar(EAccion.DEPURAR);			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // addDeleteList
}
