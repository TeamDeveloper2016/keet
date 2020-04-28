package mx.org.kaana.keet.nomina.reglas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
import mx.org.kaana.keet.db.dto.TcKeetNominasPersonasDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasRubrosDto;
import mx.org.kaana.keet.nomina.enums.ENominaEstatus;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 27/04/2020
 *@time 07:38:52 PM 
 *author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Transaccion extends IBaseTnx {

	private static final Log LOG= LogFactory.getLog(Transaccion.class);
	
	private Long idNomina;
	private Autentifica autentifica;
	private Long idEmpresaPersona;
	private Long idProveedor;
	private String messageError;
	private TcKeetNominasDto nomina;
	
	public Transaccion(Long idNomina) {
		this(idNomina, new Autentifica());
	}

	public Transaccion(Long idNomina, Autentifica autentifica) {
		this(idNomina, autentifica, -1L, -1L);
	}
	
	public Transaccion(Long idNomina, Autentifica autentifica, Long idEmpresaPersona, Long idProveedor) {
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
			this.nomina= (TcKeetNominasDto)DaoFactory.getInstance().findById(TcKeetNominasDto.class, this.idNomina);
			switch(accion) {
				case AGREGAR:
					switch(this.nomina.getIdNominaEstatus().intValue()) {
						case 1:  // INICIADA
							this.procesar(sesion, "todos");
							break;
						case 2:  // ENPROCESO
							this.procesar(sesion, "complementaria");
							break;
						case 3:  // CALCULADA
							this.reprocesar(sesion);
							break;
					} // switch
					break;
			} // switch
		} // try
		catch (Exception e) {			
      Error.mensaje(e);
			throw new Exception(this.messageError.concat("<br/>")+ e);		
		} // catch		
		finally {
			Methods.clean(params);
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
			TcKeetNominasBitacoraDto bitacora= new TcKeetNominasBitacoraDto(
				null, // String justificacion, 
				this.nomina.getIdNominaEstatus(), // Long idNominaEstatus, 
				this.autentifica.getPersona().getIdUsuario(), // Long idUsuario, 
				-1L, // Long idNominaBitacora, 
				this.idNomina // Long idNomina			 
			);
			DaoFactory.getInstance().insert(sesion, bitacora);
			DaoFactory.getInstance().update(sesion, this.nomina);
		} // if
	}
	
	private void procesar(Session sesion, String proceso) throws Exception {
		Map<String, Object> params       = null;
		TcKeetNominasPersonasDto empleado= null;
		try {
			params= new HashMap<>();
			// SI ES UN PROCESO AUTOMATICO TOMAR LAS SUCURSALES HACIENDO UNA CONSULTA O LLEANDO EL AUTENTIFCA CON EL USUARIO DEFAULT
			params.put("sucursales", this.autentifica.getEmpresa().getSucursales());
			params.put("idNomina", this.idNomina);
			List<Entity> personal= DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaDto", proceso, params);
			int count= 1;
			for (Entity persona: personal) {
				empleado= this.existPersona(sesion, persona.toLong("idEmpresaPersona"));
				if(empleado== null) {
					empleado= new TcKeetNominasPersonasDto(
						0D, // Double neto, 
						persona.toLong("idEmpresaPersona") , // Long idEmpresaPersona, 
						0D, // Double deducciones,
						-1L, // Long idNominaPersona, 
						0D, // Double aportaciones, 
						0D, // Double percepciones, 
						this.idNomina // Long idNomina
					);
					this.calculos(sesion, empleado);
		    	// this.commit();
			  } // if
				LOG.info("["+ count+ " de "+ personal.size()+ "] Procesando: "+ persona.toString("clave")+ ", "+ empleado);
				if(count== 1 && this.nomina.getIdNominaEstatus()< ENominaEstatus.ENPROCESO.getIdKey())
					this.bitacora(sesion, ENominaEstatus.INICIADA.getIdKey());
			  count++;
			} // for
			if(count== 1 && this.nomina.getIdNominaEstatus()< ENominaEstatus.CALCULADA.getIdKey())
			  this.bitacora(sesion, ENominaEstatus.ENPROCESO.getIdKey());
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}
	
	private void reprocesar(Session sesion) throws Exception {
		Map<String, Object> params       = null;
		TcKeetNominasPersonasDto empleado= null;
		try {
			params= new HashMap<>();
			// SI ES UN PROCESO AUTOMATICO TOMAR LAS SUCURSALES HACIENDO UNA CONSULTA O LLEANDO EL AUTENTIFCA CON EL USUARIO DEFAULT
			params.put("sucursales", this.autentifica.getEmpresa().getSucursales());
			params.put("idNomina", this.idNomina);
			List<Entity> personal= DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaDto", "todos", params);
			int count= 1;
			this.cleanNomina(sesion);
			for (Entity persona: personal) {
				empleado= this.existPersona(sesion, persona.toLong("idEmpresaPersona"));
				if(empleado== null) {
					empleado= new TcKeetNominasPersonasDto(
						0D, // Double neto, 
						persona.toLong("idEmpresaPersona") , // Long idEmpresaPersona, 
						0D, // Double deducciones,
						-1L, // Long idNominaPersona, 
						0D, // Double aportaciones, 
						0D, // Double percepciones, 
						this.idNomina // Long idNomina
					);
					this.calculos(sesion, empleado);
		    	// this.commit();
			  } // if
				LOG.info("["+ count+ " de "+ personal.size()+ "] Procesando: "+ persona.toString("clave")+ ", "+ empleado);
				if(count== 1) {
					this.nomina.setIdNominaEstatus(ENominaEstatus.INICIADA.getIdKey());
					this.bitacora(sesion, ENominaEstatus.INICIADA.getIdKey());
				} // if
			  count++;
			} // for
			this.bitacora(sesion, ENominaEstatus.ENPROCESO.getIdKey());
		} // try
		finally {
			Methods.clean(params);
		} // finally		
	}
	
	private void persona(Session sesion) throws Exception {
		Map<String, Object> params       = null;
		TcKeetNominasPersonasDto empleado= null;
		try {
			params= new HashMap<>();
			// SI ES UN PROCESO AUTOMATICO TOMAR LAS SUCURSALES HACIENDO UNA CONSULTA O LLEANDO EL AUTENTIFCA CON EL USUARIO DEFAULT
			params.put("sucursales", this.autentifica.getEmpresa().getSucursales());
			params.put("idNomina", this.idNomina);
			params.put("idEmpresaPersona", this.idEmpresaPersona);
			List<Entity> personas= DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaDto", "persona", params);
			int count= 1;
			this.cleanPersona(sesion, this.idEmpresaPersona);
			for (Entity persona: personas) {
				empleado= this.existPersona(sesion, persona.toLong("idEmpresaPersona"));
				if(empleado== null) {
					empleado= new TcKeetNominasPersonasDto(
						0D, // Double neto, 
						persona.toLong("idEmpresaPersona") , // Long idEmpresaPersona, 
						0D, // Double deducciones,
						-1L, // Long idNominaPersona, 
						0D, // Double aportaciones, 
						0D, // Double percepciones, 
						this.idNomina // Long idNomina
					);
					this.calculos(sesion, empleado);
			  } // if
				LOG.info("["+ count+ " de "+ personas.size()+ "] Procesando: "+ persona.toString("clave")+ ", "+ empleado);
			  count++;
			} // for
		} // try
		finally {
			Methods.clean(params);
		} // finally		
	}
	
	private void proveedor(Session sesion) throws Exception {
		Map<String, Object> params          = null;
		TcKeetNominasProveedoresDto proveedor= null;
		try {
			params= new HashMap<>();
			// SI ES UN PROCESO AUTOMATICO TOMAR LAS SUCURSALES HACIENDO UNA CONSULTA O LLEANDO EL AUTENTIFCA CON EL USUARIO DEFAULT
			params.put("sucursales", this.autentifica.getEmpresa().getSucursales());
			params.put("idNomina", this.idNomina);
			params.put("idProveedor", this.idProveedor);
			List<Entity> personas= DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaDto", "proveedor", params);
			int count= 1;
			this.cleanPersona(sesion, this.idProveedor);
			for (Entity persona: personas) {
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
					this.calculos(sesion, proveedor);
			  } // if
				LOG.info("["+ count+ " de "+ personas.size()+ "] Procesando: "+ persona.toString("clave")+ ", "+ proveedor);
			  count++;
			} // for
		} // try
		finally {
			Methods.clean(params);
		} // finally		
	}
	
	public void calculos(Session sesion, TcKeetNominasPersonasDto empleado) throws Exception {
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			
			DaoFactory.getInstance().insert(sesion, empleado);
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}
	
	public void calculos(Session sesion, TcKeetNominasProveedoresDto proveedor) throws Exception {
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			
			DaoFactory.getInstance().insert(sesion, proveedor);
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}
	
}
