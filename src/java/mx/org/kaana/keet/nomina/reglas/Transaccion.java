package mx.org.kaana.keet.nomina.reglas;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.procesos.acceso.beans.Autentifica;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetContratosDestajosContratistasDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosDestajosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasDetallesDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasPersonasDto;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
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
	private String messageError;
	
	public Transaccion(Long idNomina) {
		this(idNomina, new Autentifica());
	}

	public Transaccion(Long idNomina, Autentifica autentifica) {
		this(idNomina, autentifica, -1L);
	}
	
	public Transaccion(Long idNomina, Autentifica autentifica, Long idEmpresaPersona) {
		this.idNomina   = idNomina;
		this.autentifica= autentifica;
		this.idEmpresaPersona= idEmpresaPersona;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
		boolean regresar          = true;
		Map<String, Object> params= null;
    try {
      this.messageError= "Ocurrio un error en el proceso de calculo de la nómina.";
			params= new HashMap<>();
			TcKeetNominasDto nomina= (TcKeetNominasDto)DaoFactory.getInstance().findById(TcKeetNominasDto.class, this.idNomina);
			switch(accion) {
				case AGREGAR:
					switch(nomina.getIdNominaEstatus().intValue()) {
						case 1:  // INICIADA
							this.procesar(sesion);
							break;
						case 2:  // ENPROCESO
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

	private TcKeetNominasPersonasDto exist(Session sesion, Long idEmpresaPersona) throws Exception {
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
	
	private void cleanNomina(Session sesion) throws Exception {
    Map<String, Object> params= null;
		try {		
			params= new HashMap<>();
			params.put("idNomina", this.idNomina);
			DaoFactory.getInstance().deleteAll(sesion, TcKeetNominasDetallesDto.class, "nomina", params);
			DaoFactory.getInstance().deleteAll(sesion, TcKeetNominasPersonasDto.class, "nomina", params);
			DaoFactory.getInstance().updateAll(sesion, TcKeetContratosDestajosContratistasDto.class, params, "nomina");
			DaoFactory.getInstance().updateAll(sesion, TcManticIncidentesDto.class, params, "nomina");
			
			DaoFactory.getInstance().deleteAll(sesion, TcKeetNominasDetallesDto.class, "nomina", params);
			DaoFactory.getInstance().deleteAll(sesion, TcKeetNominasPersonasDto.class, "nomina", params);
			DaoFactory.getInstance().updateAll(sesion, TcKeetContratosDestajosProveedoresDto.class, params, "nomina");
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
			DaoFactory.getInstance().deleteAll(sesion, TcKeetNominasDetallesDto.class, "persona", params);
			DaoFactory.getInstance().deleteAll(sesion, TcKeetNominasPersonasDto.class, "persona", params);
			DaoFactory.getInstance().updateAll(sesion, TcKeetContratosDestajosContratistasDto.class, params, "persona");
			DaoFactory.getInstance().updateAll(sesion, TcManticIncidentesDto.class, params, "persona");
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
			DaoFactory.getInstance().deleteAll(sesion, TcKeetNominasDetallesDto.class, "proveedor", params);
			DaoFactory.getInstance().deleteAll(sesion, TcKeetNominasPersonasDto.class, "proveedor", params);
			DaoFactory.getInstance().updateAll(sesion, TcKeetContratosDestajosProveedoresDto.class, params, "proveedor");
    } // try
		finally {
			Methods.clean(params);
		} // finally
	}
	
	private void procesar(Session sesion) throws Exception {
		Map<String, Object> params       = null;
		TcKeetNominasPersonasDto empleado= null;
		try {
			params= new HashMap<>();
			// SI ES UN PROCESO AUTOMATICO TOMAR LAS SUCURSALES HACIENDO UNA CONSULTA O LLEANDO EL AUTENTIFCA CON EL USUARIO DEFAULT
			params.put("sucursales", this.autentifica.getEmpresa().getSucursales());
			List<Entity> personal= DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaDto", "personal", params);
			int count= 1;
			for (Entity persona: personal) {
				empleado= this.exist(sesion, persona.toLong("idEmpresaPersona"));
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
					DaoFactory.getInstance().insert(sesion, empleado);
			  } // if
				LOG.info("["+ count+ " de "+ personal.size()+ "] Procesando: "+ persona.toString("clave")+ ", "+ empleado);
			  count++;
			} // for
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}
	
}
