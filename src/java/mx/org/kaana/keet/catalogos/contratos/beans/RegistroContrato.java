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
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.mantic.catalogos.clientes.beans.Domicilio;

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
 	private Domicilio domicilio;
	private Domicilio domicilioPivote;
	private List<ContratoDomicilio> contratoDomicilios;
	private ContratoDomicilio contratoDomicilioSelecion;

	public RegistroContrato() {
		this(-1L, new Contrato(), new Domicilio(), new ArrayList<ContratoDomicilio>(), new ArrayList<ContratoPersonal>(), new ContratoPersonal(),  new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	} // RegistroProveedor

	public RegistroContrato(Contrato contrato) {
		this(contrato.getIdContrato(), contrato, new Domicilio(), new ArrayList<ContratoDomicilio>(), new ArrayList<ContratoPersonal>(), new ContratoPersonal(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}	
	
	public RegistroContrato(Long idContrato) {
		this.countIndice = 0L;
		this.idContrato  = idContrato;
		this.contadores  = new ContadoresListas();				
		this.documentos  = new ArrayList<>();
		this.presupuestos= new ArrayList<>();
		this.generadores = new ArrayList<>();
		this.domicilio   = new Domicilio();
		this.domicilioPivote   = new Domicilio();
    this.contratoDomicilios= new ArrayList<>();
		this.init();
	} // RegistroProveedor

	public RegistroContrato(Long idContrato, Contrato contrato, Domicilio domicilio, List<ContratoDomicilio> contratoDomicilios, List<ContratoPersonal> contratoPersonas, ContratoPersonal contratoPersona, List<Documento> documentos, List<Presupuesto> presupuestos, List<Generador> generadores) {
		this.idContrato      = idContrato;
		this.contrato        = contrato;				
		this.contadores      = new ContadoresListas(); 
		this.countIndice     = 0L;				
		this.contratoPersonas= contratoPersonas;
		this.contratoPersona = contratoPersona;
		this.documentos      = documentos;
		this.presupuestos    = presupuestos;
		this.generadores     = generadores;
    this.domicilio       = domicilio;
		this.domicilioPivote = domicilio;
    this.contratoDomicilios= contratoDomicilios;
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

  public Domicilio getDomicilio() {
    return domicilio;
  }

  public void setDomicilio(Domicilio domicilio) {
    this.domicilio = domicilio;
  }

  public Domicilio getDomicilioPivote() {
    return domicilioPivote;
  }

  public void setDomicilioPivote(Domicilio domicilioPivote) {
    this.domicilioPivote = domicilioPivote;
  }

  public List<ContratoDomicilio> getContratoDomicilios() {
    return contratoDomicilios;
  }

  public void setContratoDomicilios(List<ContratoDomicilio> contratoDomicilios) {
    this.contratoDomicilios = contratoDomicilios;
  }

  public ContratoDomicilio getContratoDomicilioSelecion() {
    return contratoDomicilioSelecion;
  }

  public void setContratoDomicilioSelecion(ContratoDomicilio contratoDomicilioSelecion) {
    this.contratoDomicilioSelecion = contratoDomicilioSelecion;
  }

	private void init() {
		MotorBusqueda motor= null;
		try {
			motor= new MotorBusqueda(this.idContrato);
			this.contrato= motor.toContrato();
      this.contrato.toLoadRetenciones();
			this.contrato.setLotes(motor.toLotes());
			this.contratoPersonas= new ArrayList<>();
      this.initCollections(motor);
		} // try
		catch (Exception e) {			
			Error.mensaje(e);
		} // catch		
	} // initCollections
	
	private void initCollections(MotorBusqueda motor) throws Exception {
		int count= 0;
		try {
			this.contratoDomicilios= motor.toContratoDomicilios(true);
			for(ContratoDomicilio item: this.contratoDomicilios) {
				count++;
				item.setConsecutivo(Long.valueOf(count));
			} // for				
		} // try
		catch (Exception e) {
			Error.mensaje(e);			
			throw e;
		} // catch		
	} // initCollections
  
	public void doAgregarContratoPersona(UISelectEntity seleccionado) {
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
		finally {			
			this.countIndice++;
		} // finally
	} // doAgregarProveedorMaterial
	
	public void doEliminarContratoPersona() {
		try {			
			if(this.contratoPersonas.remove(this.contratoPersona)) {
				if(!this.contratoPersona.getNuevo())
					this.addDeleteList(this.contratoPersona);
				JsfBase.addMessage("Se eliminó correctamente el empleado", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el empleado", ETipoMensaje.INFORMACION);
		} // try // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarContratoPersona
	
	private void addDeleteList(IBaseDto dto) throws Exception {
		Transaccion transaccion= null;
		try {
			transaccion= new Transaccion(dto);
			transaccion.ejecutar(EAccion.DEPURAR);			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // addDeleteList

	public void doConsultarContratoDomicilios() {
		ContratoDomicilio pivote= null;
		try {			
			pivote= this.contratoDomicilios.get(this.contratoDomicilios.indexOf(this.contratoDomicilioSelecion));
			pivote.setModificar(true);
			this.domicilioPivote= new Domicilio();
			this.domicilioPivote.setIdTipoDomicilio(pivote.getIdTipoDomicilio());
			this.domicilioPivote.setPrincipal(pivote.getIdPrincipal().equals(1L));				
			if(pivote.getDomicilio() != null) {
				this.domicilioPivote.setIdDomicilio(pivote.getDomicilio().getKey());
				this.domicilioPivote.setDomicilio(pivote.getDomicilio());
			} // if
			this.domicilioPivote.setIdEntidad(pivote.getIdEntidad());
			this.domicilioPivote.setIdMunicipio(pivote.getIdMunicipio());
			this.domicilioPivote.setLocalidad(pivote.getIdLocalidad());
			this.domicilioPivote.setIdLocalidad(pivote.getIdLocalidad().getKey());
			this.domicilioPivote.setCodigoPostal(pivote.getCodigoPostal());
			this.domicilioPivote.setCalle(pivote.getCalle());
			this.domicilioPivote.setNumeroExterior(pivote.getExterior());
			this.domicilioPivote.setNumeroInterior(pivote.getInterior());
			this.domicilioPivote.setAsentamiento(pivote.getColonia());
			this.domicilioPivote.setEntreCalle(pivote.getEntreCalle());
			this.domicilioPivote.setYcalle(pivote.getyCalle());
			this.domicilioPivote.setNuevoCp(pivote.getCodigoPostal()!= null && !Cadena.isVacio(pivote.getCodigoPostal()));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} // doConsultarConstrtoDomicilios

  public void doAgregarContratoDomicilio() {
		try {								
			this.contratoDomicilioSelecion= new ContratoDomicilio(this.countIndice, ESql.INSERT, true);	
			setValuesContratoDomicilio(this.contratoDomicilioSelecion, false);			
			this.contratoDomicilios.add(this.contratoDomicilioSelecion);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally {			
			this.countIndice++;
		} // finally
	} // doAgregarContratoDomicilio
	
	public void doEliminarContratoDomicilio() {
		try {			
			if(this.contratoDomicilios.remove(this.contratoDomicilioSelecion)) {
				if(!this.contratoDomicilioSelecion.getNuevo())
					addDeleteList(this.contratoDomicilioSelecion);
				JsfBase.addMessage("Se eliminó correctamente el domicilio", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el domicilio", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarContratoDomicilio
	
	public void doConsultarContratoDomicilio() {
		ContratoDomicilio pivote= null;
		try {			
			pivote= this.contratoDomicilios.get(this.contratoDomicilios.indexOf(this.contratoDomicilioSelecion));
			pivote.setModificar(true);
			this.domicilioPivote= new Domicilio();
			this.domicilioPivote.setIdTipoDomicilio(pivote.getIdTipoDomicilio());
			this.domicilioPivote.setPrincipal(pivote.getIdPrincipal().equals(1L));				
			if(pivote.getDomicilio() != null) {
				this.domicilioPivote.setIdDomicilio(pivote.getDomicilio().getKey());
				this.domicilioPivote.setDomicilio(pivote.getDomicilio());
			} // if
			this.domicilioPivote.setIdEntidad(pivote.getIdEntidad());
			this.domicilioPivote.setIdMunicipio(pivote.getIdMunicipio());
			this.domicilioPivote.setLocalidad(pivote.getIdLocalidad());
			this.domicilioPivote.setIdLocalidad(pivote.getIdLocalidad().getKey());
			this.domicilioPivote.setCodigoPostal(pivote.getCodigoPostal());
			this.domicilioPivote.setCalle(pivote.getCalle());
			this.domicilioPivote.setNumeroExterior(pivote.getExterior());
			this.domicilioPivote.setNumeroInterior(pivote.getInterior());
			this.domicilioPivote.setAsentamiento(pivote.getColonia());
			this.domicilioPivote.setEntreCalle(pivote.getEntreCalle());
			this.domicilioPivote.setYcalle(pivote.getyCalle());
			this.domicilioPivote.setNuevoCp(pivote.getCodigoPostal()!= null && !Cadena.isVacio(pivote.getCodigoPostal()));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} // doConsultarContratoDomicilio
	
	public void doActualizarContratoDomicilio() {
		ContratoDomicilio pivote= null;
		try {			
			pivote= this.contratoDomicilios.get(this.contratoDomicilios.indexOf(this.contratoDomicilioSelecion));			
			pivote.setModificar(false);
			setValuesContratoDomicilio(pivote, true);						
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch				
	} // doActualizarContratoDomicilio
	
	private void setValuesContratoDomicilio(ContratoDomicilio contratoDomicilio, boolean actualizar) throws Exception {
		try {
			if(this.domicilio.getPrincipal()) {
				for(ContratoDomicilio record: this.contratoDomicilios)
					record.setIdPrincipal(0L);
			} // if
			contratoDomicilio.setIdPrincipal(this.domicilio.getPrincipal() ? 1L : 2L);
			if(this.domicilio.getDomicilio()!= null) {
				contratoDomicilio.setDomicilio(this.domicilio.getDomicilio());
				contratoDomicilio.setIdDomicilio(this.domicilio.getDomicilio().getKey());
			} // if
			contratoDomicilio.setIdUsuario(JsfBase.getIdUsuario());
			contratoDomicilio.setIdTipoDomicilio(this.domicilio.getIdTipoDomicilio());
			if(!actualizar)
				contratoDomicilio.setConsecutivo(this.contratoDomicilios.size() + 1L);
			contratoDomicilio.setIdEntidad(this.domicilio.getIdEntidad());
			contratoDomicilio.setIdMunicipio(this.domicilio.getIdMunicipio());
			contratoDomicilio.setIdLocalidad(this.domicilio.getLocalidad());
			contratoDomicilio.setCodigoPostal(this.domicilio.getCodigoPostal());
			contratoDomicilio.setCalle(this.domicilio.getCalle());
			contratoDomicilio.setExterior(this.domicilio.getNumeroExterior());
			contratoDomicilio.setInterior(this.domicilio.getNumeroInterior());
			contratoDomicilio.setEntreCalle(this.domicilio.getEntreCalle());
			contratoDomicilio.setyCalle(this.domicilio.getYcalle());
			contratoDomicilio.setColonia(this.domicilio.getAsentamiento());
			contratoDomicilio.setNuevoCp(this.domicilio.getCodigoPostal()!= null && !Cadena.isVacio(this.domicilio.getCodigoPostal()));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // setValuesContratoDomicilio
  
}
