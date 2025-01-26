package mx.org.kaana.mantic.catalogos.personas.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.db.dto.TcKeetDeudoresDto;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.mantic.catalogos.clientes.beans.Domicilio;
import mx.org.kaana.mantic.catalogos.clientes.reglas.Transaccion;
import mx.org.kaana.mantic.catalogos.personas.reglas.MotorBusqueda;
import mx.org.kaana.mantic.db.dto.TcManticPersonasDto;
import mx.org.kaana.mantic.db.dto.TrManticEmpresaPersonalDto;

public class RegistroPersona implements Serializable {

	private static final long serialVersionUID= -8922183204418674058L;
	private Long idPersona;
	private TcManticPersonasDto persona;
	private TcKeetDeudoresDto deudor;
	private TrManticEmpresaPersonalDto empresaPersona;
	private List<PersonaDomicilio> personasDomicilio;
	private PersonaDomicilio personaDomicilioSeleccion;
	private List<PersonaBeneficiario> personasBeneficiarios;
	private PersonaBeneficiario personaBeneficiarioSeleccion;
	private PersonaBeneficiario personaBeneficiario;
	private PersonaBeneficiario beneficiarioPivote;
	private List<PersonaAlimenticia> personasAlimenticias;
	private PersonaAlimenticia personaAlimenticiaSeleccion;
	private PersonaAlimenticia personaAlimenticia;
	private PersonaAlimenticia alimenticiaPivote;
	private List<PersonaTipoContacto> personasTiposContacto;
	private PersonaTipoContacto personaTipoContactoSeleccion;	
	private List<PersonaBanco> personasBancos;
	private PersonaBanco personaBancoSeleccion;	
	private List<IBaseDto> deleteList;
	private ContadoresListas contadores;
	private Long countIndice;
	private Domicilio domicilio;
	private Domicilio domicilioPivote;
	private Long idAgrupacion;
	private Long idPuesto;
	private Long idContratista;
	private Long idEmpresa;
	private boolean activo;
  private Object[] departamentos;
  private List<Especialidad> especialidades;
	
	public RegistroPersona() {
		this(-1L, new TcManticPersonasDto(), new ArrayList<PersonaDomicilio>(), new ArrayList<PersonaTipoContacto>(), new Domicilio(), new ArrayList<PersonaBanco>(), new ArrayList<PersonaBeneficiario>(), new TrManticEmpresaPersonalDto(), new PersonaBeneficiario(), new PersonaBeneficiario(), new TcKeetDeudoresDto(), new ArrayList<PersonaAlimenticia>(), new PersonaAlimenticia(), new PersonaAlimenticia());
	}
	
	public RegistroPersona(Long idPersona) {
		this.idPersona          = idPersona;
		this.contadores         = new ContadoresListas();
		this.countIndice        = 0L;
		this.idAgrupacion       = -1L;
		this.idPuesto           = -1L;
    this.idContratista      = -1L;
		this.idEmpresa          = -1L;
		this.deleteList         = new ArrayList<>();
		this.domicilio          = new Domicilio();
		this.domicilioPivote    = new Domicilio();
		this.beneficiarioPivote = new PersonaBeneficiario();
		this.personaBeneficiario= new PersonaBeneficiario();
		this.alimenticiaPivote  = new PersonaAlimenticia();
		this.personaAlimenticia = new PersonaAlimenticia();
		this.init();		
	}
	
	public RegistroPersona(Long idPersona, TcManticPersonasDto persona, List<PersonaDomicilio> personasDomicilio, List<PersonaTipoContacto> personasTiposContacto, Domicilio domicilio, List<PersonaBanco> personasBancos, List<PersonaBeneficiario> personasBeneficiarios, TrManticEmpresaPersonalDto empresaPersona, PersonaBeneficiario beneficiarioPivote, PersonaBeneficiario personaBeneficiario, TcKeetDeudoresDto deudor, List<PersonaAlimenticia> personasAlimenticias, PersonaAlimenticia alimenticiaPivote, PersonaAlimenticia personaAlimenticia) {
		this.idPersona            = idPersona;
		this.persona              = persona;
		this.personasDomicilio    = personasDomicilio;
		this.personasTiposContacto= personasTiposContacto;
		this.deleteList           = new ArrayList<>();
		this.contadores           = new ContadoresListas();
		this.countIndice          = 0L;
		this.domicilio            = domicilio;
		this.domicilioPivote      = domicilio;
		this.idAgrupacion         = -1L;
		this.idPuesto             = -1L;
    this.idContratista        = -1L;
		this.idEmpresa            = -1L;
		this.personasBeneficiarios= personasBeneficiarios;
		this.personasBancos       = personasBancos;
		this.empresaPersona       = empresaPersona;
		this.beneficiarioPivote   = beneficiarioPivote;
		this.personaBeneficiario  = personaBeneficiario;
		this.personasAlimenticias = personasAlimenticias;
		this.alimenticiaPivote    = alimenticiaPivote;
		this.personaAlimenticia   = personaAlimenticia;
		this.activo               = true;
		this.deudor               = deudor;
    this.especialidades       = new ArrayList<>();
    this.departamentos        = new Object[] {};
	}

	public Long getIdPersona() {
		return idPersona;
	}

	public void setIdPersona(Long idPersona) {
		this.idPersona = idPersona;
	}

	public TcManticPersonasDto getPersona() {
		return persona;
	}

	public void setPersona(TcManticPersonasDto persona) {
		this.persona = persona;
	}

	public List<PersonaDomicilio> getPersonasDomicilio() {
		return personasDomicilio;
	}

	public void setPersonasDomicilio(List<PersonaDomicilio> personasDomicilio) {
		this.personasDomicilio = personasDomicilio;
	}

	public PersonaDomicilio getPersonaDomicilioSeleccion() {
		return personaDomicilioSeleccion;
	}

	public void setPersonaDomicilioSeleccion(PersonaDomicilio personaDomicilioSeleccion) {
		this.personaDomicilioSeleccion = personaDomicilioSeleccion;
	}

	public List<PersonaTipoContacto> getPersonasTiposContacto() {
		return personasTiposContacto;
	}

	public void setPersonasTiposContacto(List<PersonaTipoContacto> personasTiposContacto) {
		this.personasTiposContacto = personasTiposContacto;
	}

	public PersonaTipoContacto getPersonaTipoContactoSeleccion() {
		return personaTipoContactoSeleccion;
	}

	public void setPersonaTipoContactoSeleccion(PersonaTipoContacto personaTipoContactoSeleccion) {
		this.personaTipoContactoSeleccion = personaTipoContactoSeleccion;
	}

	public List<IBaseDto> getDeleteList() {
		return deleteList;
	}

	public void setDeleteList(List<IBaseDto> deleteList) {
		this.deleteList = deleteList;
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

  public Long getIdAgrupacion() {
    return idAgrupacion;
  }

  public void setIdAgrupacion(Long idAgrupacion) {
    this.idAgrupacion = idAgrupacion;
  }

	public Long getIdPuesto() {
		return idPuesto;
	}

	public void setIdPuesto(Long idPuesto) {
		this.idPuesto = idPuesto;
	}	

  public Long getIdContratista() {
    return idContratista;
  }

  public void setIdContratista(Long idContratista) {
    this.idContratista = idContratista;
  }

	public Long getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Long idEmpresa) {
		this.idEmpresa = idEmpresa;
	}	

	public List<PersonaBeneficiario> getPersonasBeneficiarios() {
		return personasBeneficiarios;
	}

	public void setPersonasBeneficiarios(List<PersonaBeneficiario> personasBeneficiarios) {
		this.personasBeneficiarios = personasBeneficiarios;
	}

	public PersonaBeneficiario getPersonaBeneficiarioSeleccion() {
		return personaBeneficiarioSeleccion;
	}

	public void setPersonaBeneficiarioSeleccion(PersonaBeneficiario personaBeneficiarioSeleccion) {
		this.personaBeneficiarioSeleccion = personaBeneficiarioSeleccion;
	}

	public List<PersonaBanco> getPersonasBancos() {
		return personasBancos;
	}

	public void setPersonasBancos(List<PersonaBanco> personasBancos) {
		this.personasBancos = personasBancos;
	}

	public PersonaBanco getPersonaBancoSeleccion() {
		return personaBancoSeleccion;
	}

	public void setPersonaBancoSeleccion(PersonaBanco personaBancoSeleccion) {
		this.personaBancoSeleccion = personaBancoSeleccion;
	}	

	public TrManticEmpresaPersonalDto getEmpresaPersona() {
		return empresaPersona;
	}

	public void setEmpresaPersona(TrManticEmpresaPersonalDto empresaPersona) {
		this.empresaPersona = empresaPersona;
	}	

	public PersonaBeneficiario getBeneficiarioPivote() {
		return beneficiarioPivote;
	}

	public void setBeneficiarioPivote(PersonaBeneficiario beneficiarioPivote) {
		this.beneficiarioPivote = beneficiarioPivote;
	}	

	public PersonaBeneficiario getPersonaBeneficiario() {
		return personaBeneficiario;
	}

	public void setPersonaBeneficiario(PersonaBeneficiario personaBeneficiario) {
		this.personaBeneficiario = personaBeneficiario;
	}	

	public boolean isActivo() {
		return Objects.equals(this.empresaPersona.getIdActivo(), 1L);
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
		this.empresaPersona.setIdActivo(this.activo ? 1L : 2L);
	}	

	public TcKeetDeudoresDto getDeudor() {
		return deudor;
	}

	public void setDeudor(TcKeetDeudoresDto deudor) {
		this.deudor = deudor;
	}	

  public Object[] getDepartamentos() {
    return departamentos;
  }

  public void setDepartamentos(Object[] departamentos) {
    this.departamentos = departamentos;
  }

  public List<Especialidad> getEspecialidades() {
    return especialidades;
  }

  public List<PersonaAlimenticia> getPersonasAlimenticias() {
    return personasAlimenticias;
  }

  public void setPersonasAlimenticias(List<PersonaAlimenticia> personasAlimenticias) {
    this.personasAlimenticias = personasAlimenticias;
  }

  public PersonaAlimenticia getPersonaAlimenticiaSeleccion() {
    return personaAlimenticiaSeleccion;
  }

  public void setPersonaAlimenticiaSeleccion(PersonaAlimenticia personaAlimenticiaSeleccion) {
    this.personaAlimenticiaSeleccion = personaAlimenticiaSeleccion;
  }

  public PersonaAlimenticia getPersonaAlimenticia() {
    return personaAlimenticia;
  }

  public void setPersonaAlimenticia(PersonaAlimenticia personaAlimenticia) {
    this.personaAlimenticia = personaAlimenticia;
  }

  public PersonaAlimenticia getAlimenticiaPivote() {
    return alimenticiaPivote;
  }

  public void setAlimenticiaPivote(PersonaAlimenticia alimenticiaPivote) {
    this.alimenticiaPivote = alimenticiaPivote;
  }
	
  
	private void init() {
		int countDomicilio   = 0;
		int countBeneficiario= 0;
		int countAlimenticia = 0;
		MotorBusqueda motor  = null;
		try {
			motor= new MotorBusqueda(this.idPersona);
			this.persona          = motor.toPersona();									
			this.empresaPersona   = motor.toDetallePersona();
			this.deudor           = motor.toDeudor(this.empresaPersona.getIdEmpresaPersona());
			this.personasDomicilio= motor.toPersonasDomicilio(true);
			for(PersonaDomicilio personaDomicilio: this.personasDomicilio) {
				countDomicilio++;
				personaDomicilio.setConsecutivo(Long.valueOf(countDomicilio));
			} // for				
			this.personasBeneficiarios= motor.toPersonasBeneficiarios();
			for(PersonaBeneficiario beneficiario: this.personasBeneficiarios) {
				beneficiario.setConsecutivo(Long.valueOf(countBeneficiario++));
			} // for				
			this.personasTiposContacto= motor.toPersonasTipoContacto();		
			this.personasBancos= motor.toPersonasBancos();		
			this.idAgrupacion= motor.toCategoriaPersona();
			this.idPuesto= motor.toPuestoPersona();
      this.idContratista= motor.toContratistaPersona();
			this.idEmpresa= motor.toEmpresaPersona();
      this.especialidades= motor.toPersonaDepartamentos(this.empresaPersona.getIdEmpresaPersona());
      if(this.especialidades== null || this.especialidades.isEmpty()) 
        this.departamentos= new Object[] {};
			this.personasAlimenticias= motor.toPersonasAlimenticias();
			for(PersonaAlimenticia alimenticia: this.personasAlimenticias) {
				alimenticia.setConsecutivo(Long.valueOf(countAlimenticia++));
			} // for				
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
	} // init
	
	public void doAgregarPersonaDomicilio() {
		PersonaDomicilio personaDomicilio= null;
		try {								
			personaDomicilio= new PersonaDomicilio(this.contadores.getTotalPersonasDomicilios() + this.countIndice, ESql.INSERT, true);	
			setValuesPersonaDomicilio(personaDomicilio, false);			
			this.personasDomicilio.add(personaDomicilio);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarPersonaDomicilio
	
	public void doEliminarPersonaDomicilio() {
		try {			
			if(this.personasDomicilio.remove(this.personaDomicilioSeleccion)) {
				if(!this.personaDomicilioSeleccion.getNuevo())
					addDeleteList(this.personaDomicilioSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el domicilio", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el domicilio", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarPersonaDomicilio	
	
	public void doConsultarPersonaDomicilio() {
		PersonaDomicilio pivote= null;
		try {			
			pivote= this.personasDomicilio.get(this.personasDomicilio.indexOf(this.personaDomicilioSeleccion));
			pivote.setModificar(true);
			this.domicilioPivote= new Domicilio();
			this.domicilioPivote.setIdTipoDomicilio(pivote.getIdTipoDomicilio());
			this.domicilioPivote.setPrincipal(Objects.equals(pivote.getIdPrincipal(), 1L) || Objects.equals(pivote.getIdPrincipal(), null));	
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
	} // doConsultarPersonaDomicilio
	
	public void doActualizarPersonaDomicilio() {
		PersonaDomicilio pivote= null;
		try {			
			pivote= this.personasDomicilio.get(this.personasDomicilio.indexOf(this.personaDomicilioSeleccion));			
			pivote.setModificar(false);
			setValuesPersonaDomicilio(pivote, true);						
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch				
	} // doActualizarPersonaDomicilio
	
	private void setValuesPersonaDomicilio(PersonaDomicilio personaDomicilio, boolean actualizar) throws Exception{
		try {
			if(this.domicilio.getPrincipal()) {
				for(PersonaDomicilio record: this.personasDomicilio)
					record.setIdPrincipal(0L);
			} // if
			personaDomicilio.setIdPrincipal(this.domicilio.getPrincipal() ? 1L : 2L);			
			if(this.domicilio.getDomicilio()!= null) {
				personaDomicilio.setDomicilio(this.domicilio.getDomicilio());
				personaDomicilio.setIdDomicilio(this.domicilio.getDomicilio().getKey());
			} // if
			personaDomicilio.setIdUsuario(JsfBase.getIdUsuario());
			personaDomicilio.setIdTipoDomicilio(this.domicilio.getIdTipoDomicilio());
			if(!actualizar)
				personaDomicilio.setConsecutivo(this.personasDomicilio.size() + 1L);
			personaDomicilio.setIdEntidad(this.domicilio.getIdEntidad());
			personaDomicilio.setIdMunicipio(this.domicilio.getIdMunicipio());
			personaDomicilio.setIdLocalidad(this.domicilio.getLocalidad());
			personaDomicilio.setCodigoPostal(this.domicilio.getCodigoPostal());
			personaDomicilio.setCalle(this.domicilio.getCalle());
			personaDomicilio.setExterior(this.domicilio.getNumeroExterior());
			personaDomicilio.setInterior(this.domicilio.getNumeroInterior());
			personaDomicilio.setEntreCalle(this.domicilio.getEntreCalle());
			personaDomicilio.setyCalle(this.domicilio.getYcalle());
			personaDomicilio.setColonia(this.domicilio.getAsentamiento());
			personaDomicilio.setNuevoCp(this.domicilio.getCodigoPostal()!= null && !Cadena.isVacio(this.domicilio.getCodigoPostal()));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // setValuesPersonaDomicilio
	
	public void doAgregarPersonaBeneficiario() {
		PersonaBeneficiario beneficiario= null;
		try {								
			beneficiario= new PersonaBeneficiario(this.contadores.getTotalPersonasBeneficiarios()+ this.countIndice, ESql.INSERT, true);	
			setValuesPersonaBeneficiario(beneficiario, false);			
			this.personasBeneficiarios.add(beneficiario);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} 
	
	public void doEliminarPersonaBeneficiario() {
		try {			
			if(this.personasBeneficiarios.remove(this.personaBeneficiarioSeleccion)) {
				if(!this.personaBeneficiarioSeleccion.getNuevo())
					addDeleteList(this.personaBeneficiarioSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el beneficiario", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el beneficiario", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} 
	
	public void doConsultarPersonaBeneficiario() {
		PersonaBeneficiario pivote= null;
		try {			
			pivote= this.personasBeneficiarios.get(this.personasBeneficiarios.indexOf(this.personaBeneficiarioSeleccion));
			pivote.setModificar(true);
			this.beneficiarioPivote= new PersonaBeneficiario();
			this.beneficiarioPivote.setNombre(pivote.getNombre());			
			this.beneficiarioPivote.setPaterno(pivote.getPaterno());
			this.beneficiarioPivote.setMaterno(pivote.getMaterno());
			this.beneficiarioPivote.setFechaNacimiento(pivote.getFechaNacimiento());
			this.beneficiarioPivote.setIdTipoParentesco(pivote.getIdTipoParentesco());	
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} 
	
	public void doActualizarPersonaBeneficiario() {
		PersonaBeneficiario pivote= null;
		try {			
			pivote= this.personasBeneficiarios.get(this.personasBeneficiarios.indexOf(this.personaBeneficiarioSeleccion));			
			pivote.setModificar(false);
			setValuesPersonaBeneficiario(pivote, true);						
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch				
	} 
	
	private void setValuesPersonaBeneficiario(PersonaBeneficiario personaBeneficiario, boolean actualizar) throws Exception{
		try {			
			personaBeneficiario.setIdUsuario(JsfBase.getIdUsuario());			
			if(!actualizar)
				personaBeneficiario.setConsecutivo(this.personasBeneficiarios.size()+ 1L);
			personaBeneficiario.setNombre(this.personaBeneficiario.getNombre());			
			personaBeneficiario.setPaterno(this.personaBeneficiario.getPaterno());
			personaBeneficiario.setMaterno(this.personaBeneficiario.getMaterno());
			personaBeneficiario.setFechaNacimiento(this.personaBeneficiario.getFechaNacimiento());
			personaBeneficiario.setIdTipoParentesco(this.personaBeneficiario.getIdTipoParentesco());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} 
	
	public void doAgregarClienteTipoContacto() {
		PersonaTipoContacto personaTipoContacto= null;
		try {					
			personaTipoContacto= new PersonaTipoContacto(this.contadores.getTotalPersonasTipoContacto()+ this.countIndice, ESql.INSERT, true, null);				
			personaTipoContacto.setOrden(this.personasTiposContacto.size() + 1L);
			this.personasTiposContacto.add(personaTipoContacto);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	}
	
	public void doAgregarPersonaBanco() {
		PersonaBanco personaBanco= null;
		try {					
			personaBanco= new PersonaBanco(this.contadores.getTotalPersonasBancos()+ this.countIndice, ESql.INSERT, true, null);							
			this.personasBancos.add(personaBanco);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarClienteTipoContacto
	
	public void doEliminarClienteTipoContacto() {
		try {			
			if(this.personasTiposContacto.remove(this.personaTipoContactoSeleccion)) {
				if(!this.personaTipoContactoSeleccion.getNuevo())
					this.addDeleteList(this.personaTipoContactoSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el tipo de contacto", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el tipo de contacto", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarClienteTipoContacto
	
	public void doEliminarPersonaBanco() {
		try {			
			if(this.personasBancos.remove(this.personaBancoSeleccion)) {
				if(!this.personaBancoSeleccion.getNuevo())
					this.addDeleteList(this.personaBancoSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el banco", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar el banco", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarPersonaBanco
  
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
	
	public void doSeleccionarPrincipal(PersonaBanco principal) {
		try {
			for(PersonaBanco perBanco: this.personasBancos) {
				if(!Objects.equals(perBanco, principal)) {
					perBanco.setIdPrincipal(2L);
					perBanco.setPrincipal(false);
				} // if					
			} // for
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	}
  
	public void doAgregarPersonaAlimenticia() {
		PersonaAlimenticia alimenticia= null;
		try {								
			alimenticia= new PersonaAlimenticia(this.contadores.getTotalPersonasAlimenticias()+ this.countIndice, ESql.INSERT, true);	
			setValuesPersonaAlimenticia(alimenticia, false);			
			this.personasAlimenticias.add(alimenticia);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} 
	
	public void doEliminarPersonaAlimenticia() {
		try {			
			if(this.personasAlimenticias.remove(this.personaAlimenticiaSeleccion)) {
				if(!this.personaAlimenticiaSeleccion.getNuevo())
					this.addDeleteList(this.personaAlimenticiaSeleccion);
				JsfBase.addMessage("Se eliminó correctamente la pensión alimenticia", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue posible eliminar la pensión alimenticia", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} 
	
	public void doConsultarPersonaAlimenticia() {
		PersonaAlimenticia pivote= null;
		try {			
			pivote= this.personasAlimenticias.get(this.personasAlimenticias.indexOf(this.personaAlimenticiaSeleccion));
			pivote.setModificar(true);
			this.alimenticiaPivote= new PersonaAlimenticia();
			this.alimenticiaPivote.setNombre(pivote.getNombre());			
			this.alimenticiaPivote.setPaterno(pivote.getPaterno());
			this.alimenticiaPivote.setMaterno(pivote.getMaterno());
			this.alimenticiaPivote.setPorcentaje(pivote.getPorcentaje());	
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} 
	
	public void doActualizarPersonaAlimenticia() {
		PersonaAlimenticia pivote= null;
		try {			
			pivote= this.personasAlimenticias.get(this.personasAlimenticias.indexOf(this.personaAlimenticiaSeleccion));			
			pivote.setModificar(false);
			setValuesPersonaAlimenticia(pivote, true);						
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch				
	} 
	
	private void setValuesPersonaAlimenticia(PersonaAlimenticia personaAlimenticia, boolean actualizar) throws Exception{
		try {			
			personaAlimenticia.setIdUsuario(JsfBase.getIdUsuario());			
			if(!actualizar)
				personaAlimenticia.setConsecutivo(this.personasAlimenticias.size() + 1L);
			personaAlimenticia.setNombre(this.personaAlimenticia.getNombre());			
			personaAlimenticia.setPaterno(this.personaAlimenticia.getPaterno());
			personaAlimenticia.setMaterno(this.personaAlimenticia.getMaterno());
			personaAlimenticia.setPorcentaje(this.personaAlimenticia.getPorcentaje());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} 
  
}
