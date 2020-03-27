package mx.org.kaana.keet.catalogos.contratos.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.keet.catalogos.contratos.reglas.Transaccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.catalogos.contratos.reglas.MotorBusqueda;

public class RegistroContrato implements Serializable {
	
	private static final long serialVersionUID = 6253504536549861564L;
	private Long idContrato;
	private Contrato contrato;
	private List<Documento> documentos;
	private List<Presupuesto> presupuestos;
	private List<Generador> generadores;

	public RegistroContrato() {
		this(new Contrato(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}	
	
	public RegistroContrato(Contrato contrato) {
		this(contrato, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}	
	

	public RegistroContrato(Contrato contrato, List<Documento> documentos, List<Presupuesto> presupuestos, List<Generador> generadores) {
		this.contrato    = contrato;
		this.documentos  = documentos;
		this.presupuestos= presupuestos;
		this.generadores = generadores;
	}

	public RegistroContrato(Long idContrato) {
		this.idContrato  = idContrato;
		this.documentos  = new ArrayList<>();
		this.presupuestos= new ArrayList<>();
		this.generadores = new ArrayList<>();
		initCollections(idContrato);
	}

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
	
	private void initCollections(Long idContrato){
		MotorBusqueda motor= null;
		try {
			motor= new MotorBusqueda(idContrato);
			this.contrato= motor.toContrato();
			this.contrato.setLotes(motor.toLotes());
		} // try
		catch (Exception e) {			
			Error.mensaje(e);
		} // catch		
	} // initCollections
	
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
