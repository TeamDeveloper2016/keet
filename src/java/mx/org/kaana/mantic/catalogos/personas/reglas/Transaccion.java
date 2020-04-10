package mx.org.kaana.mantic.catalogos.personas.reglas;

import com.google.common.base.Objects;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.procesos.usuarios.reglas.RandomCuenta;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.db.dto.TcKeetPersonasBancosDto;
import mx.org.kaana.keet.db.dto.TcKeetPersonasBeneficiariosDto;
import mx.org.kaana.keet.enums.ETiposIncidentes;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.BouncyEncryption;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.personas.beans.PersonaBanco;
import mx.org.kaana.mantic.catalogos.personas.beans.PersonaBeneficiario;
import mx.org.kaana.mantic.catalogos.personas.beans.PersonaDomicilio;
import mx.org.kaana.mantic.catalogos.personas.beans.PersonaTipoContacto;
import mx.org.kaana.mantic.catalogos.personas.beans.RegistroPersona;
import mx.org.kaana.mantic.db.dto.TcManticDomiciliosDto;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesDto;
import mx.org.kaana.mantic.db.dto.TcManticPersonasDto;
import mx.org.kaana.mantic.db.dto.TrManticClienteRepresentanteDto;
import mx.org.kaana.mantic.db.dto.TrManticEmpresaPersonalDto;
import mx.org.kaana.mantic.db.dto.TrManticPersonaDomicilioDto;
import mx.org.kaana.mantic.db.dto.TrManticPersonaTipoContactoDto;
import mx.org.kaana.mantic.db.dto.TrManticProveedorAgenteDto;
import mx.org.kaana.mantic.enums.EEstatusIncidentes;
import mx.org.kaana.mantic.enums.ETipoPersona;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx{

	private IBaseDto dto;
	private RegistroPersona persona;	
	private String messageError;
	private String cuenta;	
	private Long idPersonaAdicional;

	public Transaccion(IBaseDto dto) {
		this.dto = dto;
	}
	
	public Transaccion(RegistroPersona persona) {
		this(persona, null);
	} // Transaccion	
	
	public Transaccion(RegistroPersona persona, Long idPersonaAdicional) {
		this.persona           = persona;				
		this.idPersonaAdicional= idPersonaAdicional;
	} // Transaccion

	public String getCuenta() {
		return cuenta;
	}		
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar= false;
		try {
			this.messageError= "Ocurrio un error al ".concat(accion.name().toLowerCase()).concat(" el registro de la persona");
			switch(accion){
				case AGREGAR:
					regresar = procesarPersona(sesion);					
					break;
				case MODIFICAR:
					regresar = actualizarPersona(sesion);					
					break;				
				case ELIMINAR:
					regresar = eliminarPersona(sesion);					
					break;
				case DEPURAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.dto)>= 1L;
					break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {			
			throw new Exception(this.messageError.concat("<br/>")+ e);
		} // catch		
		return regresar;
	}	// ejecutar
	
	private void toCuenta(){
		RandomCuenta random= null;
		try {
			random= new RandomCuenta(this.persona.getPersona().getNombres(), this.persona.getPersona().getPaterno(), this.persona.getPersona().getMaterno(), -1L);
      this.cuenta= random.getCuentaGenerada();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
	} // toCuenta
	
	private boolean procesarPersona(Session sesion) throws Exception {
    boolean regresar= false;
    Long idPersona  = -1L;
    try {
      this.messageError = "Error al registrar el articulo";
      if (eliminarRegistros(sesion)) {
				toCuenta();
				this.persona.getPersona().setContrasenia(BouncyEncryption.encrypt(this.persona.getPersona().getPaterno()));
				this.persona.getPersona().setCuenta(this.cuenta);
        this.persona.getPersona().setIdUsuario(JsfBase.getIdUsuario());
        idPersona = DaoFactory.getInstance().insert(sesion, this.persona.getPersona());
				if(registraPersonaEmpresa(sesion, idPersona)){
					if (registraPersonasBeneficiarios(sesion, this.persona.getEmpresaPersona().getIdEmpresaPersona())) {
						if (registraPersonasDomicilios(sesion, idPersona)) {
							if(registraPersonasTipoContacto(sesion, idPersona)){
								regresar= registraPersonasBancos(sesion, this.persona.getEmpresaPersona().getIdEmpresaPersona());
								if(this.persona.getPersona().getIdTipoPersona().equals(ETipoPersona.AGENTE_VENTAS.getIdTipoPersona()))
									regresar= registrarProveedor(sesion, idPersona);
								if(this.persona.getPersona().getIdTipoPersona().equals(ETipoPersona.REPRESENTANTE_LEGAL.getIdTipoPersona()))
									regresar= registrarCliente(sesion, idPersona);
							} // if
						} // if
					} // if
        } // if
      } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    return regresar;
  } // procesarCliente
	
	private boolean actualizarPersona(Session sesion) throws Exception {
    boolean regresar= false;
    Long idPersona  = -1L;
    try {
      idPersona= this.persona.getIdPersona();
			this.cuenta= this.persona.getPersona().getCuenta();
      if (registraPersonasDomicilios(sesion, idPersona)) {
				if (registraPersonasBeneficiarios(sesion, this.persona.getEmpresaPersona().getIdEmpresaPersona())) {
					if (registraPersonasTipoContacto(sesion, idPersona)) {
						if (registraPersonasBancos(sesion, this.persona.getEmpresaPersona().getIdEmpresaPersona())) {
							if (actualizaPuestoPersona(sesion, idPersona)) {								
								regresar = DaoFactory.getInstance().update(sesion, this.persona.getPersona()) >= 1L;
								if(this.persona.getPersona().getIdTipoPersona().equals(ETipoPersona.AGENTE_VENTAS.getIdTipoPersona()))
									regresar= actualizaProveedor(sesion);
								if(this.persona.getPersona().getIdTipoPersona().equals(ETipoPersona.REPRESENTANTE_LEGAL.getIdTipoPersona()))
									regresar= actualizaCliente(sesion);
							} // if
						} // if
					} // if
				} // if
      } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    return regresar;
  } // actualizarCliente

  private boolean eliminarPersona(Session sesion) throws Exception {
    boolean regresar          = false;
    Map<String, Object> params= null;
    try {
      params = new HashMap<>();
      params.put("idPersona", this.persona.getIdPersona());
      if (DaoFactory.getInstance().deleteAll(sesion, TrManticPersonaDomicilioDto.class, params) > -1L) {
				if (DaoFactory.getInstance().deleteAll(sesion, TcKeetPersonasBeneficiariosDto.class, params) > -1L) {
					if (DaoFactory.getInstance().deleteAll(sesion, TrManticPersonaTipoContactoDto.class, params) > -1L) {
						if (DaoFactory.getInstance().deleteAll(sesion, TcKeetPersonasBancosDto.class, params) > -1L) {
							if (DaoFactory.getInstance().deleteAll(sesion, TrManticEmpresaPersonalDto.class, params) > -1L) {
								regresar = DaoFactory.getInstance().delete(sesion, TcManticPersonasDto.class, this.persona.getIdPersona()) >= 1L;
							} // if
						} // if
					} // if
				} // if
      } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  } // eliminarCliente
	
	private boolean registraPersonaEmpresa(Session sesion, Long idPersona) throws Exception{
		boolean regresar     = false;		
		Long idEmpresaPersona= -1L;
		try {
			this.persona.getEmpresaPersona().setIdPersona(idPersona);			
			this.persona.getEmpresaPersona().setIdEmpresa(this.persona.getIdEmpresa());						
			this.persona.getEmpresaPersona().setIdPuesto(this.persona.getIdPuesto());						
			this.persona.getEmpresaPersona().setIdUsuario(JsfBase.getIdUsuario());												
			this.persona.getEmpresaPersona().setObservaciones("Alta de empleado nuevo.");												
			idEmpresaPersona= DaoFactory.getInstance().insert(sesion, this.persona.getEmpresaPersona());
			if(idEmpresaPersona>= 1L)
				regresar= registrarIncidencia(sesion, idEmpresaPersona);			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registraPersonaEmpresa
	
	private boolean registrarIncidencia(Session sesion, Long idEmpresaPersona) throws Exception{
		return registrarIncidencia(sesion, idEmpresaPersona, ETiposIncidentes.ALTA.getKey(), "Alta de empleado");
	} // registrarIncidencia
	
	private boolean registrarIncidencia(Session sesion, Long idEmpresaPersona, Long idTipoIncidencia, String observaciones) throws Exception{
		Boolean regresar                = false;
		TcManticIncidentesDto incidencia= null;
		Siguiente consecutivo           = null;
		try {
			incidencia= new TcManticIncidentesDto();
			consecutivo= this.toSiguiente(sesion);			
			incidencia.setConsecutivo(consecutivo.getConsecutivo());			
			incidencia.setOrden(consecutivo.getOrden());			
			incidencia.setEjercicio(Long.valueOf(Fecha.getAnioActual()));			
			incidencia.setIdEmpresaPersona(idEmpresaPersona);
			incidencia.setIdIncidenteEstatus(EEstatusIncidentes.REGISTRADA.getIdEstatusInicidente());
			incidencia.setIdTipoIncidente(idTipoIncidencia);
			incidencia.setIdUsuario(JsfBase.getIdUsuario());
			incidencia.setObservaciones(observaciones);
			incidencia.setVigenciaInicio(LocalDate.now());
			incidencia.setVigenciaFin(LocalDate.now());
			if(DaoFactory.getInstance().insert(sesion, incidencia)>= 1L)
				regresar= registrarBitacora(sesion, incidencia.getIdIncidente(), EEstatusIncidentes.REGISTRADA.getIdEstatusInicidente(), observaciones);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarIncidencia
	
	private boolean registrarBitacora(Session sesion, Long idIncidente, Long idEstatus, String observaciones) throws Exception{
		boolean regresar                 = false;
		TcManticIncidentesBitacoraDto dto= null;
		try {
			dto= new TcManticIncidentesBitacoraDto();
			dto.setIdIncidente(idIncidente);
			dto.setIdIncidenteEstatus(idEstatus);
			dto.setIdUsuario(JsfBase.getIdUsuario());
			dto.setJustificacion(observaciones);
			regresar= DaoFactory.getInstance().insert(sesion, dto)>= 1L;
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		return regresar;
	} // registrarBitacora
	
	private Siguiente toSiguiente(Session sesion) throws Exception {
		Siguiente regresar        = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			params.put("ejercicio", this.getCurrentYear());			
			params.put("operador", this.getCurrentSign());
			Value next= DaoFactory.getInstance().toField(sesion, "TcManticIncidentesDto", "siguiente", params, "siguiente");
			if(next.getData()!= null)
				regresar= new Siguiente(next.toLong());
			else
				regresar= new Siguiente(Configuracion.getInstance().isEtapaDesarrollo()? 900001L: 1L); 
		} // try		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toSiguiente
	
	private boolean actualizaPuestoPersona(Session sesion, Long idPersona) throws Exception{
		boolean regresar                          = false;
		TrManticEmpresaPersonalDto empresaPersonal= null;
		TrManticEmpresaPersonalDto oldData        = null;
		Map<String, Object>params                 = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_persona=" + idPersona);
			empresaPersonal= (TrManticEmpresaPersonalDto) DaoFactory.getInstance().findFirst(TrManticEmpresaPersonalDto.class, "row", params);
			if(empresaPersonal!= null && empresaPersonal.isValid()){						
				empresaPersonal.setIdActivo(this.persona.getEmpresaPersona().getIdActivo());
				empresaPersonal.setClave(this.persona.getEmpresaPersona().getClave());
				empresaPersonal.setIdContrato(this.persona.getEmpresaPersona().getIdContrato());
				empresaPersonal.setFechaIngreso(this.persona.getEmpresaPersona().getFechaIngreso());
				empresaPersonal.setFechaContratacion(this.persona.getEmpresaPersona().getFechaContratacion());
				empresaPersonal.setSueldoSemanal(this.persona.getEmpresaPersona().getSueldoSemanal());
				empresaPersonal.setSueldoMensual(this.persona.getEmpresaPersona().getSueldoMensual());
				empresaPersonal.setNss(this.persona.getEmpresaPersona().getNss());
				empresaPersonal.setDiarioImss(this.persona.getEmpresaPersona().getDiarioImss());
				empresaPersonal.setSueldoImss(this.persona.getEmpresaPersona().getSueldoImss());
				empresaPersonal.setInfonavit(this.persona.getEmpresaPersona().getInfonavit());
				empresaPersonal.setFactorInfonavit(this.persona.getEmpresaPersona().getFactorInfonavit());
				empresaPersonal.setIdDepartamento(this.persona.getEmpresaPersona().getIdDepartamento());
				empresaPersonal.setIdPuesto(this.persona.getIdPuesto());
				empresaPersonal.setIdEmpresa(this.persona.getIdEmpresa());
				empresaPersonal.setIdNomina(this.persona.getEmpresaPersona().getIdNomina());
				empresaPersonal.setIdSeguro(this.persona.getEmpresaPersona().getIdSeguro());
				bitacora(sesion, "Empleados", empresaPersonal);
				oldData= (TrManticEmpresaPersonalDto) DaoFactory.getInstance().findById(sesion, empresaPersonal.getClass(), empresaPersonal.getKey());
				if(registrarIncidenciaCambios(sesion, empresaPersonal, oldData))
					regresar= DaoFactory.getInstance().update(sesion, empresaPersonal)>= 1L;				
			} // if			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // actualizaPuestoPersona
	
	private boolean registrarIncidenciaCambios(Session sesion, TrManticEmpresaPersonalDto empresaPersonal, TrManticEmpresaPersonalDto empresaPersonalOld) throws Exception{
		boolean regresar       = true;		
		Map<String, Object> old= null;
		Map<String, Object> tmp= null;
		try {			
			old= empresaPersonalOld.toMap();
			tmp= empresaPersonal.toMap();
			for(String key: old.keySet()) {
				if(!Objects.equal(old.get(key),tmp.get(key))) {
					if(key.equals("idActivo"))
						regresar= registrarIncidencia(sesion, empresaPersonal.getIdEmpresaPersona(), empresaPersonal.getIdActivo().equals(1L) ? ETiposIncidentes.REINGRESO.getKey() : ETiposIncidentes.BAJA.getKey(), empresaPersonal.getIdActivo().equals(1L) ? "Reingreso de empleado." : "Baja de empleado.");						
				} // if
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarBitacoraCambios
	
	private boolean registraPersonasDomicilios(Session sesion, Long idPersona) throws Exception {
    TrManticPersonaDomicilioDto dto= null;
    ESql sqlAccion= null;
    int count= 0;
		int countPrincipal = 0;
    boolean validate= false;
    boolean regresar= false;
    try {
			if(this.persona.getPersonasDomicilio().size()== 1)
					this.persona.getPersonasDomicilio().get(0).setIdPrincipal(1L);
      for (PersonaDomicilio personaDomicilio : this.persona.getPersonasDomicilio()) {
				if(personaDomicilio.getIdPrincipal().equals(1L))
					countPrincipal++;
				else
					personaDomicilio.setIdPrincipal(2L);
				if(countPrincipal== 0 && this.persona.getPersonasDomicilio().size()-1 == count)
					personaDomicilio.setIdPrincipal(1L);
        personaDomicilio.setIdPersona(idPersona);
        personaDomicilio.setIdUsuario(JsfBase.getIdUsuario());
				personaDomicilio.setIdDomicilio(toIdDomicilio(sesion, personaDomicilio));		
        dto = (TrManticPersonaDomicilioDto) personaDomicilio;
        sqlAccion = personaDomicilio.getSqlAccion();
        switch (sqlAccion) {
          case INSERT:
            dto.setIdPersonaDomicilio(-1L);
            validate = registrar(sesion, dto);						
            break;
          case UPDATE:
            validate = actualizar(sesion, dto);
            break;
        } // switch				
        if (validate) {
          count++;
        }
      } // for		
      regresar = count == this.persona.getPersonasDomicilio().size();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar los domicilios, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraClientesDomicilios
	
	private boolean registraPersonasBeneficiarios(Session sesion, Long idEmpresaPersona) throws Exception {
    TcKeetPersonasBeneficiariosDto dto= null;
    ESql sqlAccion  = null;
    int count       = 0;
    boolean validate= false;
    boolean regresar= false;
    try {			
      for (PersonaBeneficiario personaBeneficiario : this.persona.getPersonasBeneficiarios()) {								
        personaBeneficiario.setIdEmpresaPersona(idEmpresaPersona);
        personaBeneficiario.setIdUsuario(JsfBase.getIdUsuario());				
        dto = (TcKeetPersonasBeneficiariosDto) personaBeneficiario;
        sqlAccion = personaBeneficiario.getSqlAccion();
        switch (sqlAccion) {
          case INSERT:
            dto.setIdPersonaBeneficiario(-1L);
            validate = registrar(sesion, dto);
            break;
          case UPDATE:
            validate = actualizar(sesion, dto);
            break;
        } // switch
        if (validate)
          count++;        
      } // for		
      regresar= count == this.persona.getPersonasBeneficiarios().size();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar los beneficiarios, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraClientesDomicilios

  private boolean registraPersonasTipoContacto(Session sesion, Long idPersona) throws Exception {
    TrManticPersonaTipoContactoDto dto = null;
    ESql sqlAccion = null;
    int count = 0;
    boolean validate = false;
    boolean regresar = false;
    try {
      for (PersonaTipoContacto personaTipoContacto : this.persona.getPersonasTiposContacto()) {
        personaTipoContacto.setIdPersona(idPersona);
        personaTipoContacto.setIdUsuario(JsfBase.getIdUsuario());
        dto = (TrManticPersonaTipoContactoDto) personaTipoContacto;
        sqlAccion = personaTipoContacto.getSqlAccion();
        switch (sqlAccion) {
          case INSERT:
            dto.setIdPersonaTipoContacto(-1L);
            validate = registrar(sesion, dto);
            break;
          case UPDATE:
            validate = actualizar(sesion, dto);
            break;
        } // switch
        if (validate) 
          count++;        
      } // for		
      regresar = count == this.persona.getPersonasTiposContacto().size();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar los tipos de contacto, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraClientesTipoContacto
	
  private boolean registraPersonasBancos(Session sesion, Long idEmpresaPersona) throws Exception {
    TcKeetPersonasBancosDto dto= null;
    ESql sqlAccion             = null;    
    boolean validate           = false;
    boolean regresar           = false;
		int countPrincipal         = 0;
		int count                  = 0;
    try {
			if(this.persona.getPersonasBancos().size()== 1)
				this.persona.getPersonasBancos().get(0).setIdPrincipal(1L);
      for (PersonaBanco personaBanco : this.persona.getPersonasBancos()) {
				if(personaBanco.getIdPrincipal().equals(1L))
					countPrincipal++;
				if(countPrincipal== 0 && this.persona.getPersonasBancos().size()-1 == count)
					personaBanco.setIdPrincipal(1L);
        personaBanco.setIdEmpresaPersona(idEmpresaPersona);
        personaBanco.setIdUsuario(JsfBase.getIdUsuario());
        dto = (TcKeetPersonasBancosDto) personaBanco;
        sqlAccion = personaBanco.getSqlAccion();
        switch (sqlAccion) {
          case INSERT:
            dto.setIdPersonaBanco(-1L);
            validate = registrar(sesion, dto);
            break;
          case UPDATE:
            validate = actualizar(sesion, dto);
            break;
        } // switch
        if (validate) 
          count++;        
      } // for		
      regresar= (count == this.persona.getPersonasBancos().size());
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al registrar los bancos del empleado, verifique que no haya duplicados";
    } // finally
    return regresar;
  } // registraClientesTipoContacto
	
	private boolean eliminarRegistros(Session sesion) throws Exception {
    boolean regresar = true;
    int count = 0;
    try {
      for (IBaseDto dto : this.persona.getDeleteList()) {
        if (DaoFactory.getInstance().delete(sesion, dto) >= 1L) 
          count++;        
      } // for
      regresar = count == this.persona.getDeleteList().size();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      this.messageError = "Error al eliminar registros";
    } // finally
    return regresar;
  } // eliminarRegistros

  private boolean registrar(Session sesion, IBaseDto dto) throws Exception {
    return DaoFactory.getInstance().insert(sesion, dto) >= 1L;
  } // registrar

  private boolean actualizar(Session sesion, IBaseDto dto) throws Exception {
    return DaoFactory.getInstance().update(sesion, dto) >= 1L;
  } // actualizar
	
	private boolean registrarCliente(Session sesion, Long idPersona) throws Exception{
		boolean regresar= false;
		TrManticClienteRepresentanteDto dto= null;
		try {
			dto= new TrManticClienteRepresentanteDto();
			dto.setIdCliente(this.idPersonaAdicional);
			dto.setIdPrincipal(1L);
			dto.setIdRepresentante(idPersona);
			dto.setIdUsuario(JsfBase.getIdUsuario());
			regresar= registrar(sesion, dto);
		} // try // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarCliente
	
	private boolean registrarProveedor(Session sesion, Long idPersona) throws Exception{
		boolean regresar= false;
		TrManticProveedorAgenteDto dto= null;
		try {
			dto= new TrManticProveedorAgenteDto();
			dto.setIdProveedor(this.idPersonaAdicional);
			dto.setIdAgente(idPersona);
			dto.setIdPrincipal(1L);
			dto.setIdUsuario(JsfBase.getIdUsuario());
			regresar= registrar(sesion, dto);
		} // try // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarProveedor
	
	private boolean actualizaProveedor(Session sesion) throws Exception{
		List<TrManticProveedorAgenteDto> proveedores= null;
		Map<String, Object>params= null;
		boolean regresar= true;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_agente=" + this.persona.getIdPersona());
			proveedores= DaoFactory.getInstance().toEntitySet(sesion, TrManticProveedorAgenteDto.class, "TrManticProveedorAgenteDto", "row", params, Constantes.SQL_TODOS_REGISTROS);							
			if(!proveedores.isEmpty()){
				for(TrManticProveedorAgenteDto dtoProv: proveedores){
					if(this.idPersonaAdicional.equals(dtoProv.getIdProveedor()))
						dtoProv.setIdPrincipal(1L);
					else
						dtoProv.setIdPrincipal(2L);
					DaoFactory.getInstance().update(sesion, dtoProv);
				} // for
			} // if
		} // try // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally	
		return regresar;
	} // actualizaProveedor
	
	private boolean actualizaCliente(Session sesion) throws Exception{
		List<TrManticClienteRepresentanteDto> clientes= null;
		Map<String, Object>params= null;
		boolean regresar= true;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_representante=" + this.persona.getIdPersona());
			clientes= DaoFactory.getInstance().toEntitySet(sesion, TrManticClienteRepresentanteDto.class, "TrManticClienteRepresentanteDto", "row", params, Constantes.SQL_TODOS_REGISTROS);							
			if(!clientes.isEmpty()){
				for(TrManticClienteRepresentanteDto dtoProv: clientes){
					if(this.idPersonaAdicional.equals(dtoProv.getIdCliente()))
						dtoProv.setIdPrincipal(1L);
					else
						dtoProv.setIdPrincipal(2L);
					DaoFactory.getInstance().update(sesion, dtoProv);
				} // for
			} // if
		} // try // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally	
		return regresar;
	} // actualizaCliente
	
	private Long toIdDomicilio(Session sesion, PersonaDomicilio personaDomicilio) throws Exception{		
		Entity entityDomicilio= null;
		Long regresar= -1L;
		try {
			entityDomicilio= toDomicilio(sesion, personaDomicilio);
			if(entityDomicilio!= null)
				regresar= entityDomicilio.getKey();
			else
				regresar= insertDomicilio(sesion, personaDomicilio);					
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarDomicilio	
	
	private Long insertDomicilio(Session sesion, PersonaDomicilio personaDomicilio) throws Exception{
		TcManticDomiciliosDto domicilio= null;
		Long regresar= -1L;
		try {
			domicilio= new TcManticDomiciliosDto();
			domicilio.setIdLocalidad(personaDomicilio.getIdLocalidad().getKey());
			domicilio.setAsentamiento(personaDomicilio.getColonia());
			domicilio.setCalle(personaDomicilio.getCalle());
			domicilio.setCodigoPostal(personaDomicilio.getCodigoPostal());
			domicilio.setEntreCalle(personaDomicilio.getEntreCalle());
			domicilio.setIdUsuario(JsfBase.getIdUsuario());
			domicilio.setNumeroExterior(personaDomicilio.getExterior());
			domicilio.setNumeroInterior(personaDomicilio.getInterior());
			domicilio.setYcalle(personaDomicilio.getyCalle());
			regresar= DaoFactory.getInstance().insert(sesion, domicilio);
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		return regresar;
	} // insertDomicilio
	
	private Entity toDomicilio(Session sesion, PersonaDomicilio personaDomicilio) throws Exception{
		Entity regresar= null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idLocalidad", personaDomicilio.getIdLocalidad().getKey());
			params.put("codigoPostal", personaDomicilio.getCodigoPostal());
			params.put("calle", personaDomicilio.getCalle());
			params.put("numeroExterior", personaDomicilio.getExterior());
			params.put("numeroInterior", personaDomicilio.getInterior());
			params.put("asentamiento", personaDomicilio.getColonia());
			params.put("entreCalle", personaDomicilio.getEntreCalle());
			params.put("yCalle", personaDomicilio.getyCalle());
			regresar= (Entity) DaoFactory.getInstance().toEntity(sesion, "TcManticDomiciliosDto", "domicilioExiste", params);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toDomicilio
}