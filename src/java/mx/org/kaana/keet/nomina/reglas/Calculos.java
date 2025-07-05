package mx.org.kaana.keet.nomina.reglas;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.procesos.acceso.beans.Autentifica;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetNominasBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasPeriodosDto;
import mx.org.kaana.keet.nomina.enums.ENominaEstatus;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.wassenger.Cafu;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 10/01/2025
 *@time 01:31:30 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public final class Calculos extends IBaseTnx {
  
	private static final Log LOG= LogFactory.getLog(Calculos.class);
  
  private final Long idNomina;
	private String messageError;
	private TcKeetNominasDto nomina;
	private TcKeetNominasBitacoraDto bitacora;
	private final Autentifica autentifica;
	private Nomina empleado;
	private Factura subcontratista;
	private String texto;
  private String realPath;
  private String[] idNotificar;  
  private Boolean automatico;
  private Boolean cancelo;
  private Long tuplas;
  
	public Calculos(Long idNomina, Autentifica autentifica, String[] idNotificar, Long tuplas) {
    this(idNomina, autentifica, idNotificar, JsfBase.getRealPath(), Boolean.FALSE, tuplas);
  }
  
	public Calculos(Long idNomina, Autentifica autentifica, String[] idNotificar, String realPath, Boolean automatico) {
    this(idNomina, autentifica, idNotificar, realPath, automatico, -1L);
  } 
  
	public Calculos(Long idNomina, Autentifica autentifica, String[] idNotificar, String realPath, Boolean automatico, Long tuplas) {
		this.idNomina   = idNomina;
		this.autentifica= autentifica;
    this.idNotificar= idNotificar;
    this.automatico = automatico;
    this.tuplas     = tuplas;
    this.cancelo    = Boolean.FALSE;
    this.setRealPath(realPath);
  } 
  
  public void setRealPath(String realPath) {
    this.realPath= realPath;
  }
  
  public Boolean isCancelo() {
    return this.cancelo;
  }
  
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
    Boolean regresar   = Boolean.FALSE;
    Puente puente      = null;
    this.messageError  = "Ocurrio un error en calculo de la nómina";
		Monitoreo monitoreo= this.automatico? new Monitoreo("NOMINA"): JsfBase.toProgressMonitor().progreso("NOMINA");
    try {
      this.nomina= (TcKeetNominasDto)DaoFactory.getInstance().findById(sesion, TcKeetNominasDto.class, this.idNomina);
      if(this.automatico) {
        this.nomina.setIdCompleta(1L);
        this.nomina.setObservaciones("PROCESO DE APERTURA AUTOMATICO DE NÓMINA");
      } // if  
			switch(accion) {
				case PROCESAR:
          LOG.error("LANZO EL PROCESO DE NOMINA <<<< ".concat(this.autentifica.getPersona().getNombreCompleto()).concat(" >>>>>"));
          LOG.error("                           <<<<       "+ Arrays.toString(this.idNotificar)+ "      >>>>>");
  			  monitoreo.setTotal(this.tuplas);
          regresar= this.procesar(sesion, Objects.equals(this.nomina.getIdNominaEstatus(), ENominaEstatus.INICIADA.getIdKey()));
          if(regresar && !monitoreo.isCancelo()) {
            puente  = new Puente(this.nomina, this.autentifica, this.idNotificar, this.realPath, this.automatico);
            regresar= puente.ejecutar(EAccion.NOTIFICAR);
          } // if  
          this.cancelo= monitoreo.isCancelo();
          break;
      } // switch
    } // try
    catch(Exception e) {
      if(e!= null)
        if(e.getCause()!= null)
          this.messageError= this.messageError.concat("<br/>").concat(e.getCause().toString());
        else
          this.messageError= this.messageError.concat("<br/>").concat(e.getMessage());
			throw new Exception(this.messageError);
		} // catch		
		finally {
      monitoreo.terminar();
      if(!this.automatico)
        JsfBase.toProgressMonitor().clean("NOMINA");
			this.nomina= null;
      puente     = null;
		} // finally
    return regresar;
  }

	private Boolean procesar(Session sesion, Boolean notificar) throws Exception {
    Boolean regresar          = Boolean.FALSE;
		Map<String, Object> params= new HashMap<>();
    Puente puente             = null;
    Monitoreo monitoreo       = this.automatico? new Monitoreo("NOMINA"): JsfBase.toProgressMonitor().progreso("NOMINA");
		try {
			params.put("sucursales", this.autentifica.getEmpresa().getSucursales());
			params.put("idNomina", this.idNomina);
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			List<Entity> todos= DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaDto", "general", params, Constantes.SQL_TODOS_REGISTROS);
			if(todos!= null && !todos.isEmpty()) {
  			monitoreo.comenzar(Objects.equals(this.tuplas, -1L)? new Long(todos.size()): this.tuplas);
				if(this.nomina.getIdNominaEstatus()< ENominaEstatus.ENPROCESO.getIdKey()) 
          this.bitacora(sesion, ENominaEstatus.ENPROCESO.getIdKey());
  		  this.empleado      = new Nomina(sesion, this.nomina, (TcKeetNominasPeriodosDto)DaoFactory.getInstance().findById(sesion, TcKeetNominasPeriodosDto.class, this.nomina.getIdNominaPeriodo()));
			  this.subcontratista= new Factura(sesion, this.nomina);
        int empleados      = 0;
        int subcontratistas= 0;
        for (Entity item: todos) {
          LOG.error("PROCESANDO: ["+ item.toString("clave")+ "] "+ (monitoreo.getProgreso()+ 1)+ " de "+ todos.size()+ ", "+ item.toLong("esEmpleado")+ ", "+ monitoreo.getPorcentaje());
          try {
            switch(item.toLong("esEmpleado").intValue()) {
              case 0: // empleado
                puente= new Puente(this.nomina, this.empleado, item.toLong("idPivote"), autentifica, this.idNotificar, this.realPath, this.automatico);
                puente.ejecutar(EAccion.EMPLEADO);
                empleados++;
                break;
              case 1: // proveedor
                puente= new Puente(this.nomina, this.subcontratista, item.toLong("idPivote"), autentifica, this.idNotificar, this.realPath, this.automatico);
                puente.ejecutar(EAccion.PROVEEDOR);
                subcontratistas++;
                break;
            } // swtich
          } // try 
          catch(Exception e) {
            Error.mensaje(e);
          } // catch
          finally {
            puente= null;
          } // finally
          monitoreo.incrementar();
 					if(!monitoreo.isCorriendo())
						break;
        } // for
				if(this.nomina.getIdNominaEstatus()< ENominaEstatus.CALCULADA.getIdKey()) 
					this.bitacora(sesion, ENominaEstatus.CALCULADA.getIdKey());
        this.toTotales(sesion);
        DaoFactory.getInstance().update(sesion, this.nomina);
        this.texto= Global.format(EFormatoDinamicos.MILES_SIN_DECIMALES, empleados)+ " persona(s) y "+ Global.format(EFormatoDinamicos.MILES_SIN_DECIMALES, subcontratistas)+ " proveedor(es)";
        if(notificar)
          this.notificarCorteNomina(sesion, this.empleado.getPeriodo());
        sesion.flush();
      } // if
      regresar= Boolean.TRUE;
		} // try
    catch(Exception e) {
      throw e;
    } // catch
		finally {
			Methods.clean(params);
			this.empleado      = null;
			this.subcontratista= null;
		} // finally
    return regresar;
  }

	private void bitacora(Session sesion, Long idNominaEstatus) throws Exception {
		Long ikNominaEstatus= this.nomina.getIdNominaEstatus();
		if(Objects.equals(this.nomina.getIdNominaEstatus(), ENominaEstatus.INICIADA.getIdKey()))
			this.nomina.setIdNominaEstatus(ENominaEstatus.ENPROCESO.getIdKey());
		else
		  if(Objects.equals(this.nomina.getIdNominaEstatus(), ENominaEstatus.ENPROCESO.getIdKey()))
			  this.nomina.setIdNominaEstatus(ENominaEstatus.CALCULADA.getIdKey());
		if(!Objects.equals(ikNominaEstatus, idNominaEstatus)) {
			this.bitacora= new TcKeetNominasBitacoraDto(
				"PROCESO AUTOMATICO DE CALCULO", // String justificacion, 
				this.nomina.getIdNominaEstatus(), // Long idNominaEstatus, 
				this.autentifica.getPersona().getIdUsuario(), // Long idUsuario, 
				-1L, // Long idNominaBitacora, 
				this.idNomina // Long idNomina			 
			);
			DaoFactory.getInstance().insert(sesion, this.bitacora);
		} // if
	}
  
	private void toTotales(Session sesion) throws Exception {
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("idNomina", this.nomina.getIdNomina());
			Entity empleados= (Entity)DaoFactory.getInstance().toEntity("TcKeetNominasPersonasDto", "personas", params);
      if(empleados!= null && !empleados.isEmpty()) {
        this.nomina.setPersonas(empleados.toLong("personas"));
        this.nomina.setAportaciones(empleados.toDouble("aportaciones"));
        this.nomina.setDeducciones(empleados.toDouble("deducciones"));
        this.nomina.setPercepciones(empleados.toDouble("percepciones"));
        this.nomina.setNeto(empleados.toDouble("neto"));
      } // if  
			Entity proveedores= (Entity)DaoFactory.getInstance().toEntity("TcKeetNominasProveedoresDto", "proveedores", params);
      if(proveedores!= null && !proveedores.isEmpty()) {
        this.nomina.setProveedores(proveedores.toLong("proveedores"));
        this.nomina.setSubtotal(proveedores.toDouble("subtotal"));
        this.nomina.setIva(proveedores.toDouble("iva"));
        this.nomina.setTotal(proveedores.toDouble("total"));
      } // if
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}

  public void notificarCorteNomina(Session sesion, TcKeetNominasPeriodosDto periodo) throws Exception {
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
      Cafu cafu= new Cafu("compañero(s)", group, this.texto, 
        periodo.getEjercicio()+ "-"+ periodo.getOrden(), 
        "*"+ 
        Global.format(EFormatoDinamicos.FECHA_CORTA, periodo.getInicio())+ 
        "* al *"+ 
        Global.format(EFormatoDinamicos.FECHA_CORTA, periodo.getTermino())+ 
        "*",
        this.realPath);
      LOG.info("Enviando whatsapp al grupo CAFU");
      cafu.doSendCorteNomina(sesion);
		} // try 
		catch(Exception e) {
			throw e;
		} // catch
  }
  
}
