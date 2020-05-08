package mx.org.kaana.keet.nomina.reglas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.procesos.acceso.beans.Autentifica;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetContratosDestajosContratistasDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosDestajosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasDetallesDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasPeriodosDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasPersonasDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasRubrosDto;
import mx.org.kaana.keet.nomina.enums.ENominaEstatus;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 27/04/2020
 *@time 07:38:52 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Transaccion extends IBaseTnx {

	private static final Log LOG= LogFactory.getLog(Transaccion.class);
	
	private Long idNomina;
	private Autentifica autentifica;
	private Long idEmpresaPersona;
	private Long idProveedor;
	private String messageError;
	private TcKeetNominasDto nomina;
	private Nomina calculos;
	private Factura factura;
	private TcKeetNominasBitacoraDto bitacora;
	
	public Transaccion(Long idNomina) {
		this(idNomina, new Autentifica());
	}

	public Transaccion(Long idNomina, Autentifica autentifica) {
		this(idNomina, autentifica, -1L, -1L);
	}

	public Transaccion(Long idNomina, Autentifica autentifica, TcKeetNominasBitacoraDto bitacora) {
		this.idNomina= idNomina;
		this.autentifica= autentifica;
		this.bitacora= bitacora;
	}

	public Transaccion(TcKeetNominasDto nomina, Autentifica autentifica) {
		this(-1L, autentifica, -1L, -1L);
		this.nomina= nomina;
	}

	public Transaccion(Long idNomina, Long idEmpresaPersona, Autentifica autentifica) {
		this(idNomina, autentifica, idEmpresaPersona, -1L);
	}
	
	public Transaccion(Long idNomina, Autentifica autentifica, Long idProveedor) {
		this(idNomina, autentifica, -1L, idProveedor);
	}
	
	private Transaccion(Long idNomina, Autentifica autentifica, Long idEmpresaPersona, Long idProveedor) {
		this.idNomina= idNomina;
		this.autentifica= autentifica;
		this.idEmpresaPersona= idEmpresaPersona;
		this.idProveedor= idProveedor;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
		boolean regresar          = true;
		Map<String, Object> params= null;
    try {
      this.messageError= "Ocurrio un error en el proceso de calculo de la nómina.";
			params= new HashMap<>();
			if(this.idNomina== -1L) {
				DaoFactory.getInstance().insert(sesion, this.nomina);
				this.idNomina= this.nomina.getIdNomina();
			} // if
			else
			  this.nomina= (TcKeetNominasDto)DaoFactory.getInstance().findById(TcKeetNominasDto.class, this.idNomina);
			this.calculos= new Nomina(sesion, this.nomina);
			this.factura = new Factura(sesion, this.nomina);
			switch(accion) {
				case AGREGAR:
					switch(this.nomina.getIdNominaEstatus().intValue()) {
						case 1:  // INICIADA
							this.procesarPersonas(sesion, "todos");
							break;
						case 2:  // ENPROCESO
							this.procesarPersonas(sesion, "algunos");
							break;
						case 3:  // CALCULADA
							this.reprocesarPersonas(sesion);
							break;
					} // switch
					break;
				case REPROCESAR:
					this.persona(sesion);
					break;
				case DEPURAR:
					this.proveedor(sesion);
					break;
				case JUSTIFICAR:
				  if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
					  this.nomina.setIdNominaEstatus(this.bitacora.getIdNominaEstatus());
						regresar= DaoFactory.getInstance().update(sesion, this.nomina)>= 1L;
						// CAMBIAR EL ESTATUS A TODOS LOS INCIDENTES Y REGISTAR EN SUS RESPECTIVA BITACORA 
            this.closeIncidentes(sesion);	
						if(this.nomina.getIdTipoNomina()== 1L) {
							params.put("idNominaPeriodo", this.nomina.getIdNominaPeriodo());
							TcKeetNominasPeriodosDto periodo= (TcKeetNominasPeriodosDto)DaoFactory.getInstance().toEntity(sesion, TcKeetNominasPeriodosDto.class, "TcKeetNominasPeriodosDto", "siguiente", params);
							TcKeetNominasDto siguiente= new TcKeetNominasDto(
								0D, // Double neto, 
								1L, // Long idNominaEstatus, 
								0D, // Double deducciones, 
								this.nomina.getIdTipoNomina(), // Long idTipoNomina, 
								0L, // Long personas, 
								0D, // Double aportaciones, 
								-1L, // Long idNomina, 
								periodo.getTermino().plusDays(-1), // LocalDate fechaPago, 
								0L, // Long proveedores, 
								0D, // Double total, 
								periodo.getTermino().plusDays(-2), // LocalDate fechaDispersion, 
								periodo.getKey(), // Long idNominaPeriodo, 
								0D, // Double iva, 
								JsfBase.getIdUsuario(), // Long idUsuario, 
								0D, // Double subtotal, 
								"", // String observaciones, 
								JsfBase.getAutentifica().getEmpresa().getIdEmpresa(), // Long idEmpresa, 
								0D // Double percepciones
							);
							DaoFactory.getInstance().insert(sesion, siguiente);
							this.bitacora= new TcKeetNominasBitacoraDto(
								"APERTURA DE NOMINA AUTOMATICO", // String justificacion, 
								1l, // Long idNominaEstatus, 
								JsfBase.getIdUsuario(), // Long idUsuario, 
								-1L, // Long idNominaBitacora, 
								siguiente.getIdNomina()// Long idNomina
							);		
							DaoFactory.getInstance().insert(sesion, bitacora);
						} // if
					} // if
					break;
			} // switch
		} // try
		catch (Exception e) {			
      Error.mensaje(e);
			throw new Exception(this.messageError.concat("<br/>")+ e);		
		} // catch		
		finally {
			Methods.clean(params);
			this.nomina  = null;
			this.calculos= null;
			this.factura = null;
		} // finally
		return regresar;	
  }

	private TcKeetNominasPersonasDto existPersona(Session sesion, Long idEmpresaPersona) throws Exception {
		TcKeetNominasPersonasDto regresar= null;
    Map<String, Object> params= null;
		try {		
			params= new HashMap<>();
			params.put("idNomina", this.idNomina);
			params.put("idEmpresaPersona", idEmpresaPersona);
			regresar= (TcKeetNominasPersonasDto)DaoFactory.getInstance().toEntity(sesion, TcKeetNominasPersonasDto.class, "TcKeetNominasPersonasDto", "identically", params);
    } // try
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
	
	private TcKeetNominasProveedoresDto existProveedor(Session sesion, Long idProveedor) throws Exception {
		TcKeetNominasProveedoresDto regresar= null;
    Map<String, Object> params= null;
		try {		
			params= new HashMap<>();
			params.put("idNomina", this.idNomina);
			params.put("idProveedor", idProveedor);
			regresar= (TcKeetNominasProveedoresDto)DaoFactory.getInstance().toEntity(sesion, TcKeetNominasProveedoresDto.class, "TcKeetNominasProveedoresDto", "identically", params);
    } // try
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
	
	private void cleanNomina(Session sesion) throws Exception {
    Map<String, Object> params= null;
		try {		
			params= new HashMap<>();
			params.put("idNomina", this.idNomina);
			DaoFactory.getInstance().deleteAll(sesion, TcKeetNominasDetallesDto.class, "nomina", params);
			DaoFactory.getInstance().deleteAll(sesion, TcKeetNominasPersonasDto.class, "nomina", params);
			DaoFactory.getInstance().updateAll(sesion, TcKeetContratosDestajosContratistasDto.class, params, "nomina");
			DaoFactory.getInstance().updateAll(sesion, TcManticIncidentesDto.class, params, "nomina");
			
			DaoFactory.getInstance().deleteAll(sesion, TcKeetNominasRubrosDto.class, "nomina", params);
			DaoFactory.getInstance().deleteAll(sesion, TcKeetNominasProveedoresDto.class, "nomina", params);
			DaoFactory.getInstance().updateAll(sesion, TcKeetContratosDestajosProveedoresDto.class, params, "nomina");
			sesion.flush();
    } // try
		finally {
			Methods.clean(params);
		} // finally
	}
	
	private void cleanPersona(Session sesion, Long idEmpresaPersona) throws Exception {
    Map<String, Object> params= null;
		try {		
			params= new HashMap<>();
			params.put("idNomina", this.idNomina);
			params.put("idEmpresaPersona", idEmpresaPersona);
			DaoFactory.getInstance().updateAll(sesion, TcManticIncidentesDto.class, params, "persona");
			DaoFactory.getInstance().deleteAll(sesion, TcKeetNominasDetallesDto.class, "persona", params);
			DaoFactory.getInstance().deleteAll(sesion, TcKeetNominasPersonasDto.class, "persona", params);
			DaoFactory.getInstance().updateAll(sesion, TcKeetContratosDestajosContratistasDto.class, params, "persona");
    } // try
		finally {
			Methods.clean(params);
		} // finally
	}
	
	private void cleanProveedor(Session sesion, Long idProveedor) throws Exception {
    Map<String, Object> params= null;
		try {		
			params= new HashMap<>();
			params.put("idNomina", this.idNomina);
			params.put("idProveedor", idProveedor);
			DaoFactory.getInstance().deleteAll(sesion, TcKeetNominasRubrosDto.class, "proveedor", params);
			DaoFactory.getInstance().deleteAll(sesion, TcKeetNominasProveedoresDto.class, "proveedor", params);
			DaoFactory.getInstance().updateAll(sesion, TcKeetContratosDestajosProveedoresDto.class, params, "proveedor");
    } // try
		finally {
			Methods.clean(params);
		} // finally
	}
	
	private void bitacora(Session sesion, Long idNominaEstatus) throws Exception {
		if(Objects.equals(this.nomina.getIdNominaEstatus(), ENominaEstatus.INICIADA.getIdKey()))
			this.nomina.setIdNominaEstatus(ENominaEstatus.ENPROCESO.getIdKey());
		else
		  if(Objects.equals(this.nomina.getIdNominaEstatus(), ENominaEstatus.ENPROCESO.getIdKey()))
			  this.nomina.setIdNominaEstatus(ENominaEstatus.CALCULADA.getIdKey());
		if(!Objects.equals(this.nomina.getIdNominaEstatus(), idNominaEstatus)) {
			TcKeetNominasBitacoraDto estatus= new TcKeetNominasBitacoraDto(
				null, // String justificacion, 
				this.nomina.getIdNominaEstatus(), // Long idNominaEstatus, 
				this.autentifica.getPersona().getIdUsuario(), // Long idUsuario, 
				-1L, // Long idNominaBitacora, 
				this.idNomina // Long idNomina			 
			);
			DaoFactory.getInstance().insert(sesion, estatus);
			DaoFactory.getInstance().update(sesion, this.nomina);
		} // if
	}
	
	private void procesarPersonas(Session sesion, String proceso) throws Exception {
		Map<String, Object> params       = null;
		TcKeetNominasPersonasDto empleado= null;
		Monitoreo monitoreo              = this.autentifica.getMonitoreo();
		try {
			monitoreo.comenzar(0L);
			params= new HashMap<>();
			// SI ES UN PROCESO AUTOMATICO TOMAR LAS SUCURSALES HACIENDO UNA CONSULTA O LLEANDO EL AUTENTIFCA CON EL USUARIO DEFAULT
			params.put("sucursales", this.autentifica.getEmpresa().getSucursales());
			params.put("idNomina", this.idNomina);
			List<Entity> personal= DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaDto", proceso, params);
			if(personal!= null && !personal.isEmpty()) {
  			monitoreo.setTotal(new Long(personal.size()));
	  		monitoreo.setId("NOMINA DEL PERSONAL");				
				int count= 1;
				for (Entity persona: personal) {
					empleado= this.existPersona(sesion, persona.toLong("idEmpresaPersona"));
					if(empleado== null) {
						empleado= new TcKeetNominasPersonasDto(
							persona.toDouble("sueldoSemanal"), // Double neto, 
							persona.toLong("idEmpresaPersona") , // Long idEmpresaPersona, 
							0D, // Double deducciones,
							-1L, // Long idNominaPersona, 
							0D, // Double aportaciones, 
							0D, // Double percepciones, 
							this.idNomina // Long idNomina
						);
						this.calculos(sesion, monitoreo, empleado);
						// this.commit();
					} // if
					LOG.info("["+ count+ " de "+ personal.size()+ "] Procesando: "+ persona.toString("clave")+ ", "+ empleado);
					if(count== 1 && this.nomina.getIdNominaEstatus()< ENominaEstatus.ENPROCESO.getIdKey())
						this.bitacora(sesion, ENominaEstatus.INICIADA.getIdKey());
					count++;
					if(!monitoreo.isCorriendo())
						break;
				} // for
				this.toTakeOutPersonas(sesion);
				this.reprocesarProveedores(sesion, monitoreo);
				if(count== 1 && this.nomina.getIdNominaEstatus()< ENominaEstatus.CALCULADA.getIdKey())
					this.bitacora(sesion, ENominaEstatus.ENPROCESO.getIdKey());
			} // if
		} // try
		finally {
      monitoreo.terminar();
			monitoreo.setProgreso(0L);			
			Methods.clean(params);
		} // finally
	}
	
	private void reprocesarPersonas(Session sesion) throws Exception {
		Map<String, Object> params       = null;
		TcKeetNominasPersonasDto empleado= null;
		Monitoreo monitoreo              = this.autentifica.getMonitoreo();
		try {
			monitoreo.comenzar(0L);
			params= new HashMap<>();
			// SI ES UN PROCESO AUTOMATICO TOMAR LAS SUCURSALES HACIENDO UNA CONSULTA O LLEANDO EL AUTENTIFCA CON EL USUARIO DEFAULT
			params.put("sucursales", this.autentifica.getEmpresa().getSucursales());
			params.put("idNomina", this.idNomina);
			List<Entity> personal= DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaDto", "todos", params);
			if(personal!= null && !personal.isEmpty()) {
			  Long proveedores= DaoFactory.getInstance().toSize(sesion, "VistaNominaDto", "proveedores", params);
  			monitoreo.setTotal(proveedores+ personal.size());
	  		monitoreo.setId("NOMINA DEL PERSONAL");				
				int count= 1;
				this.cleanNomina(sesion);
				for (Entity persona: personal) {
					empleado= this.existPersona(sesion, persona.toLong("idEmpresaPersona"));
					if(empleado== null) {
						empleado= new TcKeetNominasPersonasDto(
							persona.toDouble("sueldoSemanal"), // Double neto, 
							persona.toLong("idEmpresaPersona") , // Long idEmpresaPersona, 
							0D, // Double deducciones,
							-1L, // Long idNominaPersona, 
							0D, // Double aportaciones, 
							0D, // Double percepciones, 
							this.idNomina // Long idNomina
						);
						this.calculos(sesion, monitoreo, empleado);
						// 
					} // if
					LOG.info("["+ count+ " de "+ personal.size()+ "] Procesando: "+ persona.toString("clave")+ ", "+ empleado);
					if(count== 1) {
						this.nomina.setIdNominaEstatus(ENominaEstatus.INICIADA.getIdKey());
						this.bitacora(sesion, ENominaEstatus.INICIADA.getIdKey());
					} // if
					count++;
					if(!monitoreo.isCorriendo())
						break;
				} // for
				sesion.flush();
				this.toTakeOutPersonas(sesion);
				this.reprocesarProveedores(sesion, monitoreo);
				this.bitacora(sesion, ENominaEstatus.ENPROCESO.getIdKey());
			} // if
		} // try
		finally {
      monitoreo.terminar();
			monitoreo.setProgreso(0L);			
			Methods.clean(params);
		} // finally		
	}
	
	private void reprocesarProveedores(Session sesion, Monitoreo monitoreo) throws Exception {
		Map<String, Object> params           = null;
		TcKeetNominasProveedoresDto proveedor= null;
		try {
			params= new HashMap<>();
			// SI ES UN PROCESO AUTOMATICO TOMAR LAS SUCURSALES HACIENDO UNA CONSULTA O LLEANDO EL AUTENTIFCA CON EL USUARIO DEFAULT
			params.put("sucursales", this.autentifica.getEmpresa().getSucursales());
			params.put("idNomina", this.idNomina);
			List<Entity> personal= DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaDto", "proveedores", params);
			if(personal!= null && !personal.isEmpty()) {
				int count= 1;
				for (Entity persona: personal) {
					proveedor= this.existProveedor(sesion, persona.toLong("idProveedor"));
					if(proveedor== null) {
						proveedor= new TcKeetNominasProveedoresDto(
							persona.toLong("idProveedor") , // Long idProveedor,
							0D, // Double total, 
							-1L, // Long idNominaProveedor, 
							0D, // Double iva, 
							0D, // Double subtotal, 
							this.idNomina // Long idNomina
						);
						this.calculos(sesion, monitoreo, proveedor);
					} // if
					LOG.info("["+ count+ " de "+ personal.size()+ "] Procesando: "+ persona.toString("clave")+ ", "+ proveedor);
					count++;
					if(!monitoreo.isCorriendo())
						break;
				} // for
				sesion.flush();
				this.toTakeOutProveedores(sesion);
			} //if
		} // try
		finally {
			Methods.clean(params);
		} // finally		
	}
	
	private void proveedor(Session sesion) throws Exception {
		Map<String, Object> params           = null;
		TcKeetNominasProveedoresDto proveedor= null;
		Monitoreo monitoreo                  = this.autentifica.getMonitoreo();
		try {
			monitoreo.comenzar(0L);
			params= new HashMap<>();
			// SI ES UN PROCESO AUTOMATICO TOMAR LAS SUCURSALES HACIENDO UNA CONSULTA O LLEANDO EL AUTENTIFCA CON EL USUARIO DEFAULT
			params.put("sucursales", this.autentifica.getEmpresa().getSucursales());
			params.put("idNomina", this.idNomina);
			params.put("idProveedor", this.idProveedor);
			this.cleanProveedor(sesion, this.idProveedor);
			List<Entity> personal= DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaDto", "proveedor", params);
			if(personal!= null && !personal.isEmpty()) {
  			monitoreo.setTotal(new Long(personal.size()));
	  		monitoreo.setId("NOMINA DEL LOS SUBCONTRATISTAS");				
				int count= 1;
				for (Entity persona: personal) {
					proveedor= this.existProveedor(sesion, persona.toLong("idProveedor"));
					if(proveedor== null) {
						proveedor= new TcKeetNominasProveedoresDto(
							persona.toLong("idProveedor") , // Long idProveedor,
							0D, // Double total, 
							-1L, // Long idNominaProveedor, 
							0D, // Double iva, 
							0D, // Double subtotal, 
							this.idNomina // Long idNomina
						);
						this.calculos(sesion, monitoreo, proveedor);
					} // if
					LOG.info("["+ count+ " de "+ personal.size()+ "] Procesando: "+ persona.toString("clave")+ ", "+ proveedor);
					count++;
				} // for
				this.toTakeOutProveedores(sesion);
			} // if
		} // try
		finally {
      monitoreo.terminar();
			monitoreo.setProgreso(0L);			
			Methods.clean(params);
		} // finally		
	}
	
	private void persona(Session sesion) throws Exception {
		Map<String, Object> params       = null;
		TcKeetNominasPersonasDto empleado= null;
		Monitoreo monitoreo              = this.autentifica.getMonitoreo();
		try {
			monitoreo.comenzar(0L);
			params= new HashMap<>();
			// SI ES UN PROCESO AUTOMATICO TOMAR LAS SUCURSALES HACIENDO UNA CONSULTA O LLEANDO EL AUTENTIFCA CON EL USUARIO DEFAULT
			params.put("sucursales", this.autentifica.getEmpresa().getSucursales());
			params.put("idNomina", this.idNomina);
			params.put("idEmpresaPersona", this.idEmpresaPersona);
			List<Entity> personal= DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaDto", "persona", params);
			if(personal!= null && !personal.isEmpty()) {
  			monitoreo.setTotal(new Long(personal.size()));
	  		monitoreo.setId("NOMINA DEL PERSONAL");				
				int count= 1;
				this.cleanPersona(sesion, this.idEmpresaPersona);
				for (Entity persona: personal) {
					empleado= this.existPersona(sesion, persona.toLong("idEmpresaPersona"));
					if(empleado== null) {
						empleado= new TcKeetNominasPersonasDto(
							persona.toDouble("sueldoSemanal"), // Double neto, 
							persona.toLong("idEmpresaPersona") , // Long idEmpresaPersona, 
							0D, // Double deducciones,
							-1L, // Long idNominaPersona, 
							0D, // Double aportaciones, 
							0D, // Double percepciones, 
							this.idNomina // Long idNomina
						);
						this.calculos(sesion, monitoreo, empleado);
					} // if
					LOG.info("["+ count+ " de "+ personal.size()+ "] Procesando: "+ persona.toString("clave")+ ", "+ empleado);
					count++;
				} // for
				this.toTakeOutPersonas(sesion);
			} // if
		} // try
		finally {
      monitoreo.terminar();
			monitoreo.setProgreso(0L);			
			Methods.clean(params);
		} // finally		
	}
		
	public void calculos(Session sesion, Monitoreo monitoreo, TcKeetNominasPersonasDto empleado) throws Exception {
		monitoreo.incrementar();
		this.calculos.process(empleado);
	}
	
	public void calculos(Session sesion, Monitoreo monitoreo, TcKeetNominasProveedoresDto proveedor) throws Exception {
		monitoreo.incrementar();
		this.factura.process(proveedor);
	}

	private void closeIncidentes(Session sesion) throws Exception {
    Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			params.put("idNomina", this.nomina.getIdNomina());
			List<TcManticIncidentesDto> incidentes= (List<TcManticIncidentesDto>)DaoFactory.getInstance().toEntitySet(sesion, TcManticIncidentesDto.class, "VistaNominaDto", "aplicar", params);
			if(incidentes!= null && !incidentes.isEmpty()) {		
				for (TcManticIncidentesDto incidente: incidentes) {
					incidente.setIdIncidenteEstatus(3L);
					DaoFactory.getInstance().update(sesion, incidente);
					TcManticIncidentesBitacoraDto estatus= new TcManticIncidentesBitacoraDto(
						"CAMBIO AUTOMATICO POR NOMINA", // String justificacion, 
						incidente.getIdIncidente(), // Long idIncidente, 
						-1L, // Long idIncidenteBitacora, 
						this.autentifica.getPersona().getIdUsuario(), // Long idUsuario, 
						3L // Long idIncidenteEstatus
					);
					DaoFactory.getInstance().insert(sesion, estatus);
				} // for
      } // if
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}

	private void toTakeOutPersonas(Session sesion) throws Exception {
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			params.put("idNomina", this.nomina.getIdNomina());
			Entity entity= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcKeetNominasDto", "personas", params);
			this.nomina.setPersonas(entity.toLong("personas"));
			this.nomina.setAportaciones(entity.toDouble("aportaciones"));
			this.nomina.setDeducciones(entity.toDouble("deducciones"));
			this.nomina.setPercepciones(entity.toDouble("percepciones"));
			this.nomina.setNeto(entity.toDouble("neto"));
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}
	
	private void toTakeOutProveedores(Session sesion) throws Exception {
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			params.put("idNomina", this.nomina.getIdNomina());
			Entity entity= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcKeetNominasDto", "proveedores", params);
			this.nomina.setProveedores(entity.toLong("proveedores"));
			this.nomina.setSubtotal(entity.toDouble("subtotal"));
			this.nomina.setIva(entity.toDouble("iva"));
			this.nomina.setTotal(entity.toDouble("total"));
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}
	
}
