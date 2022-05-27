package mx.org.kaana.keet.nomina.reglas;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.procesos.acceso.beans.Autentifica;
import mx.org.kaana.kajool.procesos.acceso.beans.Sucursal;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.keet.db.dto.TcKeetAnticiposDto;
import mx.org.kaana.keet.db.dto.TcKeetAnticiposPagosDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosDestajosContratistasDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosDestajosProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.db.dto.TcKeetIncidentesBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetIncidentesDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasDesarrollosDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasDetallesDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasPeriodosDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasPersonasDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasProveedoresDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasRubrosDto;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosDto;
import mx.org.kaana.keet.db.dto.TcKeetPrestamosPagosDto;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.keet.nomina.enums.ECodigosIncidentes;
import mx.org.kaana.keet.nomina.enums.ENominaEstatus;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Encriptar;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.wassenger.Cafu;
import mx.org.kaana.mantic.catalogos.personas.beans.PersonaTipoContacto;
import mx.org.kaana.mantic.catalogos.proveedores.beans.ProveedorTipoContacto;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.correos.beans.Attachment;
import mx.org.kaana.mantic.correos.enums.ECorreos;
import mx.org.kaana.mantic.correos.reglas.IBaseAttachment;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticIncidentesDto;
import mx.org.kaana.mantic.db.dto.TrManticEmpresaPersonalDto;
import mx.org.kaana.mantic.db.dto.TrManticPersonaTipoContactoDto;
import mx.org.kaana.mantic.db.dto.TrManticProveedorTipoContactoDto;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.enums.ETiposContactos;
import mx.org.kaana.mantic.facturas.beans.Correo;
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

public class Transaccion extends mx.org.kaana.keet.prestamos.pagos.reglas.Transaccion {

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
	private Long idFigura;
	private Long idTipoFigura;
	private Correo correo;
	private String texto;
	
	public Transaccion(Long idNomina) {
		this(idNomina, new Autentifica());
	}

	public Transaccion(Long idNomina, Autentifica autentifica) {
		this(idNomina, autentifica, -1L, -1L);
	}

	public Transaccion(Long idNomina, Autentifica autentifica, TcKeetNominasBitacoraDto bitacora) {
    super(new TcKeetPrestamosPagosDto());
		this.idNomina= idNomina;
		this.autentifica= autentifica;
		this.bitacora= bitacora;
	}

	public Transaccion(TcKeetNominasDto nomina, Autentifica autentifica) {
		this(nomina.getIdNomina(), autentifica, -1L, -1L);
		this.nomina= nomina;
	}

	public Transaccion(Long idNomina, Long idEmpresaPersona, Autentifica autentifica) {
		this(idNomina, autentifica, idEmpresaPersona, -1L);
	}
	
	public Transaccion(Long idNomina, Autentifica autentifica, Long idProveedor) {
		this(idNomina, autentifica, -1L, idProveedor);
	}
	
	private Transaccion(Long idNomina, Autentifica autentifica, Long idEmpresaPersona, Long idProveedor) {
    super(new TcKeetPrestamosPagosDto());
		this.idNomina        = idNomina;
		this.autentifica     = autentifica;
		this.idEmpresaPersona= idEmpresaPersona;
		this.idProveedor     = idProveedor;
	}

	public Transaccion(Long idFigura, Long idTipoFigura, Correo correo) {
    super(new TcKeetPrestamosPagosDto());
		this.idFigura    = idFigura;
		this.idTipoFigura= idTipoFigura;
		this.correo      = correo;
	}	
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
		boolean regresar= true;
    try {
      this.messageError= "Ocurrio un error en el proceso de calculo de la nómina.";
			if(!accion.equals(EAccion.COMPLEMENTAR)) {
				if(this.idNomina== -1L) {
					DaoFactory.getInstance().insert(sesion, this.nomina);
					this.idNomina= this.nomina.getIdNomina();
				} // if
				else
          if(this.nomina!=null && this.nomina.isValid())
            DaoFactory.getInstance().update(sesion, this.nomina);
          else
					  this.nomina= (TcKeetNominasDto)DaoFactory.getInstance().findById(TcKeetNominasDto.class, this.idNomina);
				this.calculos= new Nomina(sesion, this.nomina, (TcKeetNominasPeriodosDto)DaoFactory.getInstance().findById(TcKeetNominasPeriodosDto.class, this.nomina.getIdNominaPeriodo()));
				this.factura = new Factura(sesion, this.nomina);
			} // if
      else
        if(this.nomina!=null && this.nomina.isValid())
          DaoFactory.getInstance().update(sesion, this.nomina);
			switch(accion) {
				case AGREGAR:
					switch(this.nomina.getIdNominaEstatus().intValue()) {
						case 1:  // INICIADA
							this.procesarPersonas(sesion, "todos");
              this.notificarCorteNomina(sesion);
 							break;
						case 2:  // ENPROCESO
							this.reprocesarPersonas(sesion); // this.procesarPersonas(sesion, "algunos");
 							break;
						case 3:  // CALCULADA
							this.reprocesarPersonas(sesion);
							break;
					} // switch
          this.notificarResumenDestajos(sesion);
					this.toAddNewNomina(sesion);
					break;
				case CALCULAR:
					this.reprocesarPersonas(sesion);
          this.notificarResumenDestajos(sesion);
					this.toAddNewNomina(sesion);
					break;
				case REPROCESAR:
					this.persona(sesion);
					break;
				case DEPURAR:
					this.proveedor(sesion);
					break;
				case JUSTIFICAR:
				  if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
            this.idNomina= this.nomina.getIdNomina();
					  this.nomina.setIdNominaEstatus(this.bitacora.getIdNominaEstatus());
						regresar= DaoFactory.getInstance().update(sesion, this.nomina)>= 1L;
            this.notificarCorreo(sesion);
            this.notificarResidentes(sesion);
            this.cierre(sesion);
						// CAMBIAR EL ESTATUS A TODOS LOS INCIDENTES Y REGISTAR EN SUS RESPECTIVA BITACORA 
            this.closeIncidentes(sesion);	
						this.toOpenNewNomina(sesion);
            // REGISTAR EL ABONO DE LOS ANTICIPO DE LOS CONTRATISAS Y SUBCONTRATISTAS
            this.toAddPagoAnticipo(sesion);
						// FALTA HACER EL PROCESO DE MOVER LOS SALDOS A LA NUEVA SEMANA (DE QUE NO RECUERDO)
            
            // QUITAR LOS SOBRE SUELDOS DE LOS EMPLEADOS
            this.toCleanSobreSueldos(sesion);
            // MOVER EL PERSONAL POR DESARROLLO A UNA TABLA HISTORICA
            this.toMovePersonalDesarrollo(sesion);
					} // if
					break;
				case COMPLEMENTAR:
					if(this.idTipoFigura.equals(1L))
						regresar= this.agregarPersonaContacto(sesion);
					else
						regresar= this.agregarProveedorContacto(sesion);
					break;
				case TRANSFORMACION:
          this.notificarCorreo(sesion);
					break;
				case MOVIMIENTOS:
          this.notificarResidentes(sesion);
					break;
				case RESTAURAR:
          this.notificarResumenDestajos(sesion);
					break;
			} // switch
		} // try
		catch (Exception e) {			
      Error.mensaje(e);
			throw new Exception(this.messageError.concat("<br/>")+ e);		
		} // catch		
		finally {
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
			DaoFactory.getInstance().updateAll(sesion, TcKeetIncidentesDto.class, params, "nomina");
			
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
			DaoFactory.getInstance().updateAll(sesion, TcKeetIncidentesDto.class, params, "persona");
			DaoFactory.getInstance().deleteAll(sesion, TcKeetNominasRubrosDto.class, "proveedor", params);
			DaoFactory.getInstance().deleteAll(sesion, TcKeetNominasProveedoresDto.class, "proveedor", params);
			DaoFactory.getInstance().updateAll(sesion, TcKeetContratosDestajosProveedoresDto.class, params, "proveedor");
    } // try
		finally {
			Methods.clean(params);
		} // finally
	}
	
	private void bitacora(Session sesion, Long idNominaEstatus) throws Exception {
		Long ikNominaEstatus= this.nomina.getIdNominaEstatus();
		if(Objects.equals(this.nomina.getIdNominaEstatus(), ENominaEstatus.INICIADA.getIdKey()))
			this.nomina.setIdNominaEstatus(ENominaEstatus.ENPROCESO.getIdKey());
		else
		  if(Objects.equals(this.nomina.getIdNominaEstatus(), ENominaEstatus.ENPROCESO.getIdKey()))
			  this.nomina.setIdNominaEstatus(ENominaEstatus.CALCULADA.getIdKey());
		if(!Objects.equals(ikNominaEstatus, idNominaEstatus)) {
			TcKeetNominasBitacoraDto estatus= new TcKeetNominasBitacoraDto(
				"PROCESO AUTOMATICO DE CALCULO", // String justificacion, 
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
			// params.put(Constantes.SQL_CONDICION, Objects.equals(this.nomina.getIdCompleta(), 2L)? " (tr_mantic_empresa_personal.id_contratista is null and tr_mantic_empresa_personal.id_puesto!= 6) ": Constantes.SQL_VERDADERO);
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			List<Entity> personal= DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaDto", proceso, params, Constantes.SQL_TODOS_REGISTROS);
			if(personal!= null && !personal.isEmpty()) {
        this.texto= Global.format(EFormatoDinamicos.MILES_SIN_DECIMALES, personal.size())+ " persona(s) ";
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
						this.bitacora(sesion, ENominaEstatus.ENPROCESO.getIdKey());
					count++;
					if(!monitoreo.isCorriendo())
						break;
				} // for
				this.toTakeOutPersonas(sesion);
        // if(Objects.equals(this.nomina.getIdCompleta(), 1L))
				this.reprocesarProveedores(sesion, monitoreo);
				if(count> 0 && this.nomina.getIdNominaEstatus()< ENominaEstatus.CALCULADA.getIdKey()) {
					this.bitacora(sesion, ENominaEstatus.CALCULADA.getIdKey());
        } // if
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
			// params.put(Constantes.SQL_CONDICION, Objects.equals(this.nomina.getIdCompleta(), 2L)? " (tr_mantic_empresa_personal.id_contratista is null and tr_mantic_empresa_personal.id_puesto!= 6) ": Constantes.SQL_VERDADERO);
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			List<Entity> personal= DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaDto", "todos", params, Constantes.SQL_TODOS_REGISTROS);
			if(personal!= null && !personal.isEmpty()) {
			  Long proveedores= DaoFactory.getInstance().toSize(sesion, "VistaNominaDto", "proveedores", params);
        // if(Objects.equals(this.nomina.getIdCompleta(), 1L))
        //   proveedores= DaoFactory.getInstance().toSize(sesion, "VistaNominaDto", "proveedores", params);
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
			List<Entity> personal= DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaDto", "proveedores", params, Constantes.SQL_TODOS_REGISTROS);
			if(personal!= null && !personal.isEmpty()) {
        this.texto= this.texto+ " y "+ Global.format(EFormatoDinamicos.MILES_SIN_DECIMALES, personal.size())+ " proveedor(es)";
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
							this.idNomina, // Long idNomina
              0D, // Double fondoGarantia,
              0D, // Double destajo      
              persona.toDouble("fondoGarantia"), // Double porcentajeFondo
              0D // anticipo
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
			List<Entity> personal= DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaDto", "proveedor", params, Constantes.SQL_TODOS_REGISTROS);
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
							this.idNomina, // Long idNomina
              0D, // Double fondoGarantia
              0D, // Double destajo      
              persona.toDouble("fondoGarantia"), // Double porcentajeFondo
              0D // anticipo
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
			List<Entity> personal= DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaDto", "persona", params, Constantes.SQL_TODOS_REGISTROS);
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
		this.calculos.process(empleado);
		monitoreo.incrementar();
	}
	
	public void calculos(Session sesion, Monitoreo monitoreo, TcKeetNominasProveedoresDto proveedor) throws Exception {
		this.factura.process(proveedor);
		monitoreo.incrementar();
	}

	private void closeIncidentes(Session sesion) throws Exception {
    Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			params.put("idNomina", this.nomina.getIdNomina());
			List<TcManticIncidentesDto> prestamos= (List<TcManticIncidentesDto>)DaoFactory.getInstance().toEntitySet(sesion, TcManticIncidentesDto.class, "VistaNominaDto", "aplicar", params, Constantes.SQL_TODOS_REGISTROS);
			if(prestamos!= null && !prestamos.isEmpty()) {		
				for (TcManticIncidentesDto item: prestamos) {
					item.setIdIncidenteEstatus(3L);
					DaoFactory.getInstance().update(sesion, item);
					TcManticIncidentesBitacoraDto estatus= new TcManticIncidentesBitacoraDto(
						"CAMBIO AUTOMATICO POR NOMINA", // String justificacion, 
						item.getIdIncidente(), // Long idIncidente, 
						-1L, // Long idIncidenteBitacora, 
						this.autentifica.getPersona().getIdUsuario(), // Long idUsuario, 
						3L // Long idIncidenteEstatus
					);
					DaoFactory.getInstance().insert(sesion, estatus);
          // SE TIENE GENERAR EL ABONO PARA EL PAGO DEL ANTICIPO PORQUE YA FUE COBRADO EN LA NOMINA QUE CIERRA
          if(Objects.equals(item.getIdTipoIncidente(), ECodigosIncidentes.ABONO.idTipoIncidente())) {
            TcKeetPrestamosDto prestamo= (TcKeetPrestamosDto)DaoFactory.getInstance().findById(sesion, TcKeetPrestamosDto.class, item.getIdPrestamo());
            this.prestamosPagos.setIdPrestamoPago(-1L);
            this.prestamosPagos.setIdPrestamo(prestamo.getIdPrestamo());
            this.prestamosPagos.setConsecutivo(prestamo.getConsecutivo());
            this.prestamosPagos.setPago(item.getCosto());
            this.prestamosPagos.setIdAfectaNomina(2L);
            this.prestamosPagos.setObservaciones("PAGO NOMINA ["+ this.calculos.getPeriodo().getEjercicio()+ "-"+ this.calculos.getPeriodo().getOrden()+ "]");
            super.toRegistrar(sesion);
          } // if
				} // for
      } // if
      TcKeetAnticiposPagosDto anticiposPagos= new TcKeetAnticiposPagosDto();
      mx.org.kaana.keet.prestamos.proveedor.pagos.reglas.Transaccion pago= new mx.org.kaana.keet.prestamos.proveedor.pagos.reglas.Transaccion(anticiposPagos);
			List<TcKeetIncidentesDto> anticipos= (List<TcKeetIncidentesDto>)DaoFactory.getInstance().toEntitySet(sesion, TcKeetIncidentesDto.class, "VistaNominaDto", "anticipos", params, Constantes.SQL_TODOS_REGISTROS);
			if(anticipos!= null && !anticipos.isEmpty()) {		
				for (TcKeetIncidentesDto item: anticipos) {
					item.setIdIncidenteEstatus(3L);
					DaoFactory.getInstance().update(sesion, item);
					TcKeetIncidentesBitacoraDto estatus= new TcKeetIncidentesBitacoraDto(
						"CAMBIO AUTOMATICO POR NOMINA", // String justificacion, 
						item.getIdIncidente(), // Long idIncidente, 
						-1L, // Long idIncidenteBitacora, 
						this.autentifica.getPersona().getIdUsuario(), // Long idUsuario, 
						3L // Long idIncidenteEstatus
					);
					DaoFactory.getInstance().insert(sesion, estatus);
          // SE TIENE GENERAR EL ABONO PARA EL PAGO DEL PRESTAMO PORQUE YA FUE COBRADO EN LA NOMINA QUE CIERRA
          if(Objects.equals(item.getIdTipoIncidente(), ECodigosIncidentes.ABONO.idTipoIncidente())) {
            TcKeetAnticiposDto anticipo= (TcKeetAnticiposDto)DaoFactory.getInstance().findById(sesion, TcKeetAnticiposDto.class, item.getIdAnticipo());
            anticiposPagos.setIdAnticipoPago(-1L);
            anticiposPagos.setConsecutivo(anticipo.getConsecutivo());
            anticiposPagos.setIdAnticipo(item.getIdAnticipo());
            anticiposPagos.setPago(item.getCosto());
            anticiposPagos.setIdAfectaNomina(2L);
            anticiposPagos.setObservaciones("PAGO NOMINA ["+ this.calculos.getPeriodo().getEjercicio()+ "-"+ this.calculos.getPeriodo().getOrden()+ "]");
            pago.toRegistrar(sesion);
          } // if
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
			Entity entity= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcKeetNominasPersonasDto", "personas", params);
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
			Entity entity= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcKeetNominasProveedoresDto", "proveedores", params);
      if(entity!= null && !entity.isEmpty()) {
        this.nomina.setProveedores(entity.toLong("proveedores"));
        this.nomina.setSubtotal(entity.toDouble("subtotal"));
        this.nomina.setIva(entity.toDouble("iva"));
        this.nomina.setTotal(entity.toDouble("total"));
      } // if
      else {
        this.nomina.setProveedores(0L);
        this.nomina.setSubtotal(0D);
        this.nomina.setIva(0D);
        this.nomina.setTotal(0D);
      } // if
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}

	private void toAddNewNomina(Session sesion) throws Exception {
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			if(this.nomina.getIdTipoNomina()== 1L) {
				params.put("sucursales", this.autentifica.getEmpresa().getSucursales());
				Value value= DaoFactory.getInstance().toField(sesion, "TcKeetNominasDto", "existe", params, "idNominaEstatus");
				if(value== null || value.getData()== null) {
					params.put("idNominaPeriodo", this.nomina.getIdNominaPeriodo());
					TcKeetNominasPeriodosDto periodo= (TcKeetNominasPeriodosDto)DaoFactory.getInstance().toEntity(sesion, TcKeetNominasPeriodosDto.class, "TcKeetNominasPeriodosDto", "siguiente", params);
					TcKeetNominasDto siguiente= new TcKeetNominasDto(
						0D, // Double neto, 
						5L, // Long idNominaEstatus, 
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
						0D, // Double percepciones
            2L // Long idCompleta
					);
					DaoFactory.getInstance().insert(sesion, siguiente);
					this.bitacora= new TcKeetNominasBitacoraDto(
						"CREAR NOMINA TEMPORAL", // String justificacion, 
						this.nomina.getIdNominaEstatus(), // Long idNominaEstatus, 
						JsfBase.getIdUsuario(), // Long idUsuario, 
						-1L, // Long idNominaBitacora, 
						siguiente.getIdNomina()// Long idNomina
					);		
					DaoFactory.getInstance().insert(sesion, bitacora);
					// REALIZAR EL PROCESO DE ACTUALIZACION DE SALDOS DE LA TABLA DE ESTACIONES
					TcKeetNominasPeriodosDto anterior= (TcKeetNominasPeriodosDto)DaoFactory.getInstance().toEntity(sesion, TcKeetNominasPeriodosDto.class, "TcKeetNominasPeriodosDto", "igual", params);
					params.put("anterior", anterior.getOrden());
					params.put("semana", periodo.getOrden());
					Estaciones estaciones= new Estaciones();
					// RECORRER TODAS LAS EMPRESAS PORQUE LA CLAVE DE LA ESTACION TIENE EL ID DE LA EMPRESA
					for (Sucursal sucursal: this.autentifica.getSucursales()) {
						estaciones.setKeyLevel(sucursal.getIdEmpresa().toString(), 0); // idEmpresa
						estaciones.setKeyLevel(periodo.getEjercicio().toString(), 1); // ejercicio
						params.put("clave", estaciones.toKey(2));
  				  DaoFactory.getInstance().updateAll(sesion, TcKeetEstacionesDto.class, params);
					} // for
				} // if			
			} // if			
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	}
	
	private void toOpenNewNomina(Session sesion) throws Exception {
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			if(this.nomina.getIdTipoNomina()== 1L) {
				params.put("sucursales", this.autentifica.getEmpresa().getSucursales());
				TcKeetNominasDto open= (TcKeetNominasDto)DaoFactory.getInstance().toEntity(sesion, TcKeetNominasDto.class, "TcKeetNominasDto", "existe", params);
				if(open!= null) {
					open.setIdNominaEstatus(1L);
					DaoFactory.getInstance().update(sesion, open);
					this.bitacora= new TcKeetNominasBitacoraDto(
						"APERTURA DE NOMINA AUTOMATICO", // String justificacion, 
						open.getIdNominaEstatus(), // Long idNominaEstatus, 
						JsfBase.getIdUsuario(), // Long idUsuario, 
						-1L, // Long idNominaBitacora, 
						open.getIdNomina()// Long idNomina
					);		
					DaoFactory.getInstance().insert(sesion, bitacora);
				} // if			
			} // if			
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	}
	
	private boolean agregarPersonaContacto(Session sesion) throws Exception{
		boolean regresar                       = true;
		List<PersonaTipoContacto> correos      = null;
		TrManticPersonaTipoContactoDto contacto= null;
		int count                              = 0;
		Long records                           = 1L;
		try {
			correos= toPersonasTipoContacto(sesion);
			if(!correos.isEmpty()){
				for(PersonaTipoContacto tipoContacto: correos){
					if(tipoContacto.getValor().equals(this.correo.getDescripcion()))
						count++;
				} // for				
				records= correos.size() + 1L;
			} // if
			if(count== 0){
				contacto= new TrManticPersonaTipoContactoDto();
				contacto.setIdPersona(this.idFigura);
				contacto.setIdTipoContacto(ETiposContactos.CORREO.getKey());
				contacto.setIdUsuario(JsfBase.getIdUsuario());
				contacto.setValor(this.correo.getDescripcion());
				contacto.setOrden(records);
				regresar= DaoFactory.getInstance().insert(sesion, contacto)>= 1L;
			} // else
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // agregarContacto
	
	private boolean agregarProveedorContacto(Session sesion) throws Exception{
		boolean regresar                         = true;
		List<ProveedorTipoContacto> correos      = null;
		TrManticProveedorTipoContactoDto contacto= null;
		int count                                = 0;
		Long records                             = 1L;
		try {
			correos= toProveedorTipoContacto(sesion);
			if(!correos.isEmpty()){
				for(ProveedorTipoContacto tipoContacto: correos){
					if(tipoContacto.getValor().equals(this.correo.getDescripcion()))
						count++;
				} // for				
				records= correos.size() + 1L;
			} // if
			if(count== 0){
				contacto= new TrManticProveedorTipoContactoDto();
				contacto.setIdProveedor(this.idFigura);
				contacto.setIdTipoContacto(ETiposContactos.CORREO.getKey());
				contacto.setIdUsuario(JsfBase.getIdUsuario());
				contacto.setValor(this.correo.getDescripcion());
				contacto.setOrden(records);
				regresar= DaoFactory.getInstance().insert(sesion, contacto)>= 1L;
			} // else
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // agregarContacto
	
	public List<PersonaTipoContacto> toPersonasTipoContacto(Session sesion) throws Exception {
		List<PersonaTipoContacto> regresar= null;
		Map<String, Object>params    = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_persona=" + this.idFigura + " and id_tipo_contacto=" + ETiposContactos.CORREO.getKey());
			regresar= DaoFactory.getInstance().toEntitySet(sesion, PersonaTipoContacto.class, "TrManticPersonaTipoContactoDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toPersonasTipoContacto
	
	public List<ProveedorTipoContacto> toProveedorTipoContacto(Session sesion) throws Exception {
		List<ProveedorTipoContacto> regresar= null;
		Map<String, Object>params    = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_proveedor=" + this.idFigura + " and id_tipo_contacto=" + ETiposContactos.CORREO.getKey());
			regresar= DaoFactory.getInstance().toEntitySet(sesion, ProveedorTipoContacto.class, "TrManticProveedorTipoContactoDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toProveedorTipoContacto

  private void notificarCorreo(Session sesion) throws Exception {
    List<Columna> columns     = null;		
    Map<String, Object> params= new HashMap<>();
    StringBuilder sb          = new StringBuilder();
    Reporte jasper            = null; 
		try {
      columns= new ArrayList<>();      
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));                  
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));    
      params.put("idNomina", this.idNomina);
      List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "VistaTableroDto", "notificar", params);
      if(items!= null && !items.isEmpty()) {
        UIBackingUtilities.toFormatEntitySet(items, columns);
        Long idDesarrollo= -1L;
        jasper           = new Reporte();	
        jasper.init();      
        for (Entity item : items) {
          if(Objects.equals(idDesarrollo, -1L) || !Objects.equals(idDesarrollo, item.toLong("idDesarrollo"))) {
            params.put("idDesarrollo", item.toLong("idDesarrollo"));
            List<Entity> correos= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaConsultasDto", "residentesTipoContacto", params);
            sb.delete(0, sb.length());
            if(correos!= null && !correos.isEmpty())
              for (Entity email: correos) {
                if(Objects.equals(email.toLong("idTipoContacto"), ETiposContactos.CORREO.getKey()) || Objects.equals(email.toLong("idTipoContacto"), ETiposContactos.CORREO_PERSONAL.getKey())) 
                  sb.append(email.toString("valor")).append(", ");
              } // for
            if(sb.length()> 2)
              sb.delete(sb.length()- 2, sb.length());
            idDesarrollo= item.toLong("idDesarrollo");
          } // for
          List<Entity> correos= null;
          if(Objects.equals(item.toLong("idTipoFigura"), 1L)) {
            params.put(Constantes.SQL_CONDICION, "id_persona="+ item.getKey());
            correos= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "TrManticPersonaTipoContactoDto", "row", params);
          } // if
          else {
            params.put(Constantes.SQL_CONDICION, "id_proveedor="+ item.getKey());
            correos= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "TrManticProveedorTipoContactoDto", "row", params);
          } // else
          StringBuilder emails= new StringBuilder();
          if(correos!= null && !correos.isEmpty())
            for (Entity email: correos) {
              if(Objects.equals(email.toLong("idTipoContacto"), ETiposContactos.CORREO.getKey()) || Objects.equals(email.toLong("idTipoContacto"), ETiposContactos.CORREO_PERSONAL.getKey())) 
                emails.append(email.toString("valor")).append(", ");
            } // for
          emails.append(sb.toString());
          if(emails.length()> 0) {
            String contacto= emails.toString().trim();
            if(contacto.endsWith(","))
              contacto= contacto.substring(0, contacto.length()- 1);
            this.toSendMail(sesion, jasper, contacto, item);
          } // if  
        } // for
      } // if  
    } // try
    catch (Exception e) {
     throw e;
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(columns);
      jasper= null;
    } // finally
  }
  
	public void toSendMail(Session sesion, Reporte jasper, String correos, Entity sujeto) throws Exception {		
		Map<String, Object> params= null;
		String titulo             = "Destajos realizados de la nómina "+ sujeto.toString("nomina")+ " periodo "+ sujeto.toString("inicio")+ " al "+ sujeto.toString("termino");
		List<Attachment> files    = null; 
		IBaseAttachment notificar = null;
		Attachment attachments    = null;
		try {
			params= new HashMap<>();
			params.put("header", "...");
			params.put("footer", "...");
			params.put("empresa", JsfBase.getAutentifica().getEmpresa().getNombre());			
			params.put("personalDestajo", sujeto.toString("contratista"));
			params.put("correo", ECorreos.DESTAJOS.getEmail());		
      params.put("solucion", Configuracion.getInstance().getEmpresa("titulo"));
      params.put("url", Configuracion.getInstance().getPropiedadServidor("sistema.dns"));
			this.toReporte(sesion, jasper, sujeto);
			params.put("tipo", "Reporte - "+ titulo);			
			attachments= new Attachment(jasper.getNombre(), false);
			files= new ArrayList<>();
			files.add(attachments);
			files.add(new Attachment("logo", ECorreos.DESTAJOS.getImages().concat(Configuracion.getInstance().getEmpresa("logo")), true));
			params.put("attach", attachments.getId());
      try {
        if(!Cadena.isVacio(correos)) {
          notificar= new IBaseAttachment(ECorreos.DESTAJOS, ECorreos.DESTAJOS.getEmail(), correos, ECorreos.DESTAJOS.getBackup(), Configuracion.getInstance().getEmpresa("titulo").concat(" - ").concat(titulo), params, files);
          notificar.send();
          LOG.info("Enviando correo a la cuenta: "+ correos);
        } // if	
      } // try
      finally {
        if(attachments.getFile().exists()) 
          LOG.info("Eliminando archivo temporal: "+ jasper.getNombre());				  
      } // finally	
			if(correos.length()> 0)
		    JsfBase.addMessage("Se envió el correo de forma exitosa.", ETipoMensaje.INFORMACION);
			else
		    JsfBase.addMessage("No se selecciono ningún correo, por favor verifiquelo e intente de nueva cuenta.", ETipoMensaje.ALERTA);
		} // try // try
		catch(Exception e) {
			throw e;
		} // catch
		finally {
			Methods.clean(files);
		} // finally
	} // toSendMail

  public String toReporte(Session sesion, Reporte jasper, Entity figura) throws Exception {    
    String regresar              = null;
		Map<String, Object>parametros= null;
		EReportes seleccion          = null;    
    Map<String, Object>params    = null;
    Parametros comunes           = null;
    try {
      params = new HashMap<>();  
      comunes= new Parametros(this.autentifica.getEmpresa().getIdEmpresa());
      parametros= comunes.getComunes();
      seleccion= figura.toLong("idTipoFigura").equals(1L)? EReportes.DESTAJOS_TOTALES_CONTRATISTA: EReportes.DESTAJOS_TOTALES_SUBCONTRATISTA;  
      parametros.put("REPORTE_TIPO_PERSONA", figura.toLong("idTipoFigura").equals(1L)? "DESTAJO CONTRATISTA": "DESTAJO SUBCONTRATISTA"); 
      parametros.put("REPORTE_FIGURA", figura.toString("contratista"));
      parametros.put("REPORTE_DEPARTAMENTO", figura.toString("departamento"));
      parametros.put("ENCUESTA", this.autentifica.getEmpresa().getNombre().toUpperCase());
      parametros.put("REPORTE_TITULO", seleccion.getTitulo());
      parametros.put("NOMBRE_REPORTE", seleccion.getNombre());
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("/resources/janal/img/sistema/"));
      parametros.put("REPORTE_EMPRESA_LOGO", this.toLookForEmpresaLogo(this.autentifica.getEmpresa().getIdEmpresa()));
      params.put("sortOrder", "order by tc_keet_contratos.clave, tc_keet_contratos_lotes.manzana, tc_keet_contratos_lotes.lote");
      params.put("loNuevo", "");
      params.put("idNomina", this.idNomina);
      params.put("idEmpresaPersona", figura.toLong("idEmpresaPersona"));
      params.put("idProveedor", figura.getKey());
      params.put(Constantes.SQL_CONDICION, "tc_keet_desarrollos.id_desarrollo= "+ figura.toLong("idDesarrollo"));
      String nombre= figura.toLong("idTipoFigura")+ Cadena.rellenar(""+ figura.getKey(), 5, '0', true)+ "-"+ Cadena.rellenar(""+ figura.toLong("idDesarrollo"), 3, '0', true)+ "-"+ figura.toString("nomina");
      jasper.toAsignarReporte(new ParametrosReporte(seleccion, params, parametros), nombre);		
      regresar= jasper.getAlias();
      String name= JsfBase.getRealPath(jasper.getNombre());
      File file= new File(name);
      if(file.exists())
        file.delete();
      jasper.toProcess(sesion);
      LOG.info("Reporte generado: "+ name);
    } // try
    catch(Exception e) {
     throw e;
    } // catch	
    return regresar;
  } // toReporte 	  
  
  private void notificarResidentes(Session sesion) throws Exception {
    List<Columna> columns           = null;		
    Map<String, Object> params      = new HashMap<>();
    Map<String, Object> residentes  = new HashMap<>();
    Reporte jasper                  = null; 
    Map<String, Object> contratistas= new HashMap<>();
    String desarrollo               = "";
		try {
      columns= new ArrayList<>();      
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));                  
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));    
      params.put("idNomina", this.idNomina);
      List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "VistaTableroDto", "notificar", params);
      if(items!= null && !items.isEmpty()) {
        UIBackingUtilities.toFormatEntitySet(items, columns);
        Long idDesarrollo= -1L;
        jasper           = new Reporte();	
        jasper.init();      
        for (Entity item: items) {
          if(Objects.equals(idDesarrollo, -1L) || !Objects.equals(idDesarrollo, item.toLong("idDesarrollo"))) {
            // NOTIFICAR A TODOS LOS RESIDENTES CON LOS REPORTES GENERADOS DE LOS CONTRATISTAS
            if(!Objects.equals(idDesarrollo, -1L)) 
              this.toNotificarResidentes(sesion, residentes, contratistas, item, desarrollo);
            params.put("idDesarrollo", item.toLong("idDesarrollo"));
            List<Entity> celulares= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaConsultasDto", "residentesTipoContacto", params);
            residentes.clear();
            // VERIFICAR SI SE RECUPERA EL NOMBRE DEL RESIDENTE PARA ENVIAR LOS MENSAJES ACUMULADOS DE TODOS LOS CONTRATISTAS
            if(celulares!= null && !celulares.isEmpty())
              for (Entity celular: celulares) {
                if(Objects.equals(celular.toLong("idPreferido"), 1L) && (Objects.equals(celular.toLong("idTipoContacto"), ETiposContactos.CELULAR.getKey()) || Objects.equals(celular.toLong("idTipoContacto"), ETiposContactos.CELULAR_NEGOCIO.getKey()) || Objects.equals(celular.toLong("idTipoContacto"), ETiposContactos.CELULAR_PERSONAL.getKey()))) 
                  residentes.put(celular.toString("residente"), celular.toString("valor"));
              } // for
            contratistas.clear();
            idDesarrollo= item.toLong("idDesarrollo");
            desarrollo  = item.toString("desarrollo");
          } // for
          List<Entity> celulares= null;
          if(Objects.equals(item.toLong("idTipoFigura"), 1L)) {
            params.put(Constantes.SQL_CONDICION, "id_persona="+ item.getKey());
            celulares= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "TrManticPersonaTipoContactoDto", "row", params);
          } // if
          else {
            params.put(Constantes.SQL_CONDICION, "id_proveedor="+ item.getKey());
            celulares= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "TrManticProveedorTipoContactoDto", "row", params);
          } // else
          String celular= null;
          if(celulares!= null && !celulares.isEmpty())
            for (Entity telefono: celulares) {
              if(Objects.equals(telefono.toLong("idPreferido"), 1L) && (Objects.equals(telefono.toLong("idTipoContacto"), ETiposContactos.CELULAR.getKey()) || Objects.equals(telefono.toLong("idTipoContacto"), ETiposContactos.CELULAR_NEGOCIO.getKey()) || Objects.equals(telefono.toLong("idTipoContacto"), ETiposContactos.CELULAR_PERSONAL.getKey()))) 
                celular= telefono.toString("valor");
            } // for
          contratistas.put(item.toString("contratista"), this.toSendMessage(sesion, jasper, celular, item));
        } // for
        // NOTIFICAR A TODOS LOS RESIDENTES CON LOS REPORTES GENERADOS DE LOS CONTRATISTAS
        this.toNotificarResidentes(sesion, residentes, contratistas, items.get(0), desarrollo);
      } // if  
    } // try
    catch (Exception e) {
      throw e;  
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(columns);
      Methods.clean(contratistas);
      Methods.clean(residentes);
      jasper= null;
    } // finally
  }

	public String toSendMessage(Session sesion, Reporte jasper, String contratista, Entity sujeto) throws Exception {		
    String regresar= "";
		Cafu notificar = null;
		try {
			regresar= this.toReporte(sesion, jasper, sujeto);
      if(contratista!= null) {
        try {
          notificar= new Cafu(sujeto.toString("contratista"), contratista, regresar, sujeto.toString("nomina"), "*"+ sujeto.toString("inicio")+ "* al *"+ sujeto.toString("termino")+ "*");
          LOG.info("Enviando mensaje por whatsapp al celular: "+ contratista);
          notificar.doSendDestajo(sesion);
        } // try
        finally {
          LOG.info("Eliminando archivo temporal: "+ jasper.getNombre());				  
        } // finally	
        if(contratista.length()> 0)
          JsfBase.addMessage("Se envió el mensaje de whatsapp de forma exitosa ["+ contratista+ "] !", ETipoMensaje.INFORMACION);
        else
          JsfBase.addMessage("No se selecciono ningún celular, por favor verifiquelo e intente de nueva cuenta.", ETipoMensaje.ALERTA);
      } // if  
		} // try // try
		catch(Exception e) {
			throw e;
		} // catch
    return regresar;
	} // toSendWessage
  
	public void toNotificarResidentes(Session sesion, Map<String, Object> residentes, Map<String, Object> contratistas, Entity periodo, String desarrollo) throws Exception {		
		Cafu notificar = null;
		try {
      // CAMBIAR POR UNA COLECCION CON EL NOMBRE DEL RESIENTE Y SU CELULAR
      if(residentes!= null && !residentes.isEmpty()) {
        Encriptar encriptar= new Encriptar();
        residentes.put("Alejandro Jiménez García", encriptar.desencriptar("cd4b3e3924191b057b8187"));
        switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
          case "cafu":
            residentes.put("Carlos Alberto Calderon Solano", encriptar.desencriptar("dc58cd49352018057c9fff"));
            residentes.put("Irma de Lourdes Hernandez Romo", encriptar.desencriptar("150075e05dc2b3a69fea2b"));
            break;
          case "gylvi":
            residentes.put("Luis Cesar Lopez Manzur", encriptar.desencriptar("89f468ef6bec68d249b0d1"));
            residentes.put("Jordi Alfonso Fariña Quiroz", encriptar.desencriptar("b8a5989f9b9e999e93fa00"));
            break;
          case "triana":
            residentes.put("Jesús Fernando Villalpando Cisneros", encriptar.desencriptar("c2bfb2a5999c9b9f99fe01"));
            residentes.put("José Refugio Villalpando Vargas", encriptar.desencriptar("69d448cf47cdb4a495fa1e"));
            break;
        } // swtich
        notificar= new Cafu(periodo.toString("nomina"), "*"+ periodo.toString("inicio")+ "* al *"+ periodo.toString("termino")+ "*", contratistas);
        notificar.setDesarrollo(desarrollo);
        for (String residente: residentes.keySet()) {
          notificar.setNombre(Cadena.nombrePersona(residente));
          notificar.setCelular((String)residentes.get(residente));
          LOG.info("Enviando mensaje de whatsapp al celular: "+ residente);
          notificar.doSendResidentes(sesion);
        } // for
        if(!residentes.isEmpty())
          JsfBase.addMessage("Se envió el mensaje de whatsapp de forma exitosa ["+ residentes.toString()+ "] !", ETipoMensaje.INFORMACION);
        else
          JsfBase.addMessage("No se selecciono ningún celular, por favor verifiquelo e intente de nueva cuenta.", ETipoMensaje.ALERTA);
      } // if  
		} // try 
		catch(Exception e) {
			throw e;
		} // catch
	} // toNotificarResidentes

  public void notificarCorteNomina(Session sesion) throws Exception {
    try {
      String group= Cafu.IMOX_GROUP_GYLVI;
      switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
        case "cafu":
          group= Cafu.IMOX_GROUP_CAFU;
          break;
        case "gylvi":
          group= Cafu.IMOX_GROUP_GYLVI;
          break;
        case "triana":
          group= Cafu.IMOX_GROUP_TRIANA;
          break;
      } // switch
			Semanas semanas= new Semanas();
			TcKeetNominasPeriodosDto periodo= semanas.getSemanaEnCursoDto();
      Cafu notificar= new Cafu("compañero(s)", group, this.texto, 
        periodo.getEjercicio()+ "-"+ periodo.getOrden(), 
        "*"+ 
        Global.format(EFormatoDinamicos.FECHA_CORTA, periodo.getInicio())+ 
        "* al *"+ 
        Global.format(EFormatoDinamicos.FECHA_CORTA, periodo.getTermino())+ 
        "*");
      LOG.info("Enviando mensaje por whatsapp al grupo de CAFU");
      notificar.doSendCorteNomina(sesion);
		} // try 
		catch(Exception e) {
			throw e;
		} // catch
  }
  
  public void cierre(Session sesion) throws Exception {
    try {
      String group= Cafu.IMOX_GROUP_GYLVI;
      switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
        case "cafu":
          group= Cafu.IMOX_GROUP_CAFU;
          break;
        case "gylvi":
          group= Cafu.IMOX_GROUP_GYLVI;
          break;
        case "triana":
          group= Cafu.IMOX_GROUP_TRIANA;
          break;
      } // switch
			Semanas semanas= new Semanas();
			TcKeetNominasPeriodosDto periodo= semanas.getSemanaEnCursoDto();
      Cafu notificar= new Cafu("compañero(s)", group, "", 
        periodo.getEjercicio()+ "-"+ periodo.getOrden(), 
        "*"+ 
        Global.format(EFormatoDinamicos.FECHA_CORTA, periodo.getInicio())+ 
        "* al *"+ 
        Global.format(EFormatoDinamicos.FECHA_CORTA, periodo.getTermino())+ 
        "*");
      LOG.info("Enviando mensaje por whatsapp al grupo de CAFU");
      notificar.doSendCierreNomina(sesion);
		} // try 
		catch(Exception e) {
			throw e;
		} // catch
  }

  private void toCleanSobreSueldos(Session sesion) throws Exception {
    try {
      DaoFactory.getInstance().updateAll(sesion, TrManticEmpresaPersonalDto.class, Collections.EMPTY_MAP);
		} // try 
		catch(Exception e) {
			throw e;
		} // catch
  }  
  
  private void toMovePersonalDesarrollo(Session sesion) throws Exception {
    Map<String, Object> params = null;
    try {      
      params = new HashMap<>();      
			params.put("idNomina", this.idNomina);
      List<Entity> personal= DaoFactory.getInstance().toEntitySet(sesion, "TcKeetNominasPersonasDto", "desarrollo", params);
			if(personal!= null && !personal.isEmpty()) {      
        for (Entity item : personal) {
          Long idDesarrollo= null;
    			params.put("idEmpresaPersona", item.toLong("idEmpresaPersona"));
          Entity existe= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcKeetContratosPersonalDto", "desarrollo", params);
          if(existe!= null && !existe.isEmpty()) 
            idDesarrollo= existe.toLong("idDesarrollo");
          TcKeetNominasDesarrollosDto persona= new TcKeetNominasDesarrollosDto(idDesarrollo, item.toLong("idEmpresaPersona"), -1L, this.idNomina);
          DaoFactory.getInstance().insert(sesion, persona);
        } // for
      } // for
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }  

  private void toAddPagoAnticipo(Session sesion) throws Exception {
    Map<String, Object> params = null;
    try {      
      params = new HashMap<>();      
      params.put("idNomina", this.idNomina);
      List<Entity> contratistas= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaConsultasDto", "anticiposContratistas", params);
      if(contratistas!= null && !contratistas.isEmpty()) {
        for (Entity item: contratistas) {
          TcKeetPrestamosDto anticipo= (TcKeetPrestamosDto)DaoFactory.getInstance().findById(sesion, TcKeetPrestamosDto.class, item.toLong("idPrestamo"));
          this.prestamosPagos.setIdPrestamoPago(-1L);
          this.prestamosPagos.setConsecutivo(anticipo.getConsecutivo());
          this.prestamosPagos.setIdPrestamo(anticipo.getIdPrestamo());
          this.prestamosPagos.setPago(item.toDouble("anticipo"));
          this.prestamosPagos.setIdAfectaNomina(2L);
          this.prestamosPagos.setObservaciones("PAGO NOMINA ["+ this.calculos.getPeriodo().getEjercicio()+ "-"+ this.calculos.getPeriodo().getOrden()+ "]");
          super.toRegistrar(sesion);
        } // for
      } // if
      List<Entity> proveedores= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaConsultasDto", "anticiposProveedores", params);
      if(proveedores!= null && !proveedores.isEmpty()) {
        TcKeetAnticiposPagosDto pago= new TcKeetAnticiposPagosDto();
        mx.org.kaana.keet.prestamos.proveedor.pagos.reglas.Transaccion transaccion= new mx.org.kaana.keet.prestamos.proveedor.pagos.reglas.Transaccion(pago);
        for (Entity item: proveedores) {
          TcKeetAnticiposDto anticipo= (TcKeetAnticiposDto)DaoFactory.getInstance().findById(sesion, TcKeetAnticiposDto.class, item.toLong("idAnticipo"));
          pago.setIdAnticipoPago(-1L);
          pago.setConsecutivo(anticipo.getConsecutivo());
          pago.setIdAnticipo(anticipo.getIdAnticipo());
          pago.setPago(item.toDouble("anticipo"));
          pago.setIdAfectaNomina(2L);
          pago.setObservaciones("PAGO NOMINA ["+ this.calculos.getPeriodo().getEjercicio()+ "-"+ this.calculos.getPeriodo().getOrden()+ "]");
          transaccion.toRegistrar(sesion);
        } // for
      } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }  

  protected String toLookForEmpresaLogo(Long idEmpresa) {
    String regresar           = null;
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idEmpresa", idEmpresa);      
      Value value = DaoFactory.getInstance().toField("TcManticEmpresasDto", "logo", params, "imagen");
      if(value!= null && value.getData()!= null)
        regresar= JsfBase.getRealPath(Constantes.RUTA_IMAGENES).concat(value.toString());
      else
        regresar= JsfBase.getRealPath(Constantes.RUTA_IMAGENES).concat(Configuracion.getInstance().getEmpresa("logo"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  } 

  private void notificarResumenDestajos(Session sesion) throws Exception {
    List<Columna> columns           = null;		
    Map<String, Object> params      = new HashMap<>();
    Reporte jasper                  = null; 
    Map<String, Object> contratistas= new LinkedHashMap<>();
		try {
      columns= new ArrayList<>();      
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));                  
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));    
      params.put("idNomina", this.idNomina);
      List<Entity> items= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "VistaTableroDto", "notificar", params);
      if(items!= null && !items.isEmpty()) {
        UIBackingUtilities.toFormatEntitySet(items, columns);
        Long idDesarrollo= -1L;
        jasper           = new Reporte();	
        jasper.init();      
        for (Entity item: items) {
          if(Objects.equals(idDesarrollo, -1L) || !Objects.equals(idDesarrollo, item.toLong("idDesarrollo"))) {
            contratistas.put("Desarrollo_"+ item.toLong("idDesarrollo"), "*".concat(item.toString("desarrollo")).concat("*\\n"));
            idDesarrollo= item.toLong("idDesarrollo");
          } // for
          contratistas.put(Cadena.rellenar(""+ item.toLong("idDesarrollo"), 3, '0', true)+ "-"+ item.toString("contratista"), this.toReporte(sesion, jasper, item));
        } // for
        // NOTIFICAR A TODOS LOS SUPERVISORES CON LOS REPORTES GENERADOS DE TODOS LOS CONTRATISTAS O SUBCONTRATISTAS
        this.toNotificarSupervisor(sesion, contratistas, items.get(0));
      } // if  
    } // try
    catch (Exception e) {
      throw e;  
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(columns);
      Methods.clean(contratistas);
      jasper= null;
    } // finally
  }

  public void toNotificarSupervisor(Session sesion, Map<String, Object> contratistas, Entity periodo) throws Exception {		
		Cafu notificar                = null;
    Map<String, Object> residentes= new HashMap<>();
		try {
      LOG.error("-------------- ENTRO AL PROCESO DE NOTIFICAR --------------------------");
      LOG.error("Con un total de contratistas: "+ contratistas.size());
      // CAMBIAR POR UNA COLECCION CON EL NOMBRE DEL RESIENTE Y SU CELULAR
      Encriptar encriptar= new Encriptar();
      switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
        case "cafu":
          residentes.put("Grupo CAFU", Cafu.IMOX_GROUP_CAFU);
          break;
        case "gylvi":
          residentes.put("Grupo GYLVI", Cafu.IMOX_GROUP_GYLVI);
          break;
        case "triana":
          residentes.put("Grupo TRIANA", Cafu.IMOX_GROUP_TRIANA);
          break;
        default:
          residentes.put("Grupo CAFU", Cafu.IMOX_GROUP_CAFU);
          break;
      } // switch
      residentes.put("Alejandro Jiménez García", encriptar.desencriptar("cd4b3e3924191b057b8187"));
      switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
        case "cafu":
          residentes.put("Carlos Alberto Calderon Solano", encriptar.desencriptar("dc58cd49352018057c9fff"));
          residentes.put("Irma de Lourdes Hernandez Romo", encriptar.desencriptar("150075e05dc2b3a69fea2b"));
          break;
        case "gylvi":
          residentes.put("Luis Cesar Lopez Manzur", encriptar.desencriptar("89f468ef6bec68d249b0d1"));
          residentes.put("Jordi Alfonso Fariña Quiroz", encriptar.desencriptar("b8a5989f9b9e999e93fa00"));
          break;
        case "triana":
          residentes.put("Jesús Fernando Villalpando Cisneros", encriptar.desencriptar("c2bfb2a5999c9b9f99fe01"));
          residentes.put("José Refugio Villalpando Vargas", encriptar.desencriptar("69d448cf47cdb4a495fa1e"));
          break;
      } // swtich
      notificar= new Cafu(periodo.toString("nomina"), "*"+ periodo.toString("inicio")+ "* al *"+ periodo.toString("termino")+ "*", contratistas);
      for (String residente: residentes.keySet()) {
        notificar.setNombre(Cadena.nombrePersona(residente));
        notificar.setCelular((String)residentes.get(residente));
        LOG.info("Enviando mensaje de whatsapp al celular: "+ residente);
        notificar.doSendSupervisor(sesion);
      } // for
      if(!residentes.isEmpty())
        JsfBase.addMessage("Se envió el mensaje de whatsapp de forma exitosa ["+ residentes.toString()+ "] !", ETipoMensaje.INFORMACION);
      else
        JsfBase.addMessage("No se selecciono ningún celular, por favor verifiquelo e intente de nueva cuenta.", ETipoMensaje.ALERTA);
		} // try 
		catch(Exception e) {
			throw e;
		} // catch
    finally {
      Methods.clean(residentes);
    } // finally
	} // toNotificarSupervisor
    
}
