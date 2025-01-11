package mx.org.kaana.keet.nomina.reglas;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.procesos.acceso.beans.Autentifica;
import mx.org.kaana.kajool.procesos.acceso.beans.Sucursal;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasPeriodosDto;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.keet.nomina.enums.ENominaEstatus;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.recurso.Cuentas;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.wassenger.Cafu;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.enums.ETiposContactos;
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
  private String[] notificar;  
  private Boolean automatico;
  
	public Calculos(Long idNomina, Autentifica autentifica) {
    this(idNomina, autentifica, new String[] {"1", "4"}, JsfBase.getRealPath(), Boolean.FALSE);
  }
  
	public Calculos(Long idNomina, Autentifica autentifica, String[] notificar, String realPath, Boolean automatico) {
		this.idNomina   = idNomina;
		this.autentifica= autentifica;
    this.notificar  = notificar;
    this.automatico = automatico;
    this.setRealPath(realPath);
  } 
  
  public void setRealPath(String realPath) {
    if(Objects.equals(realPath, null))
      if(!Objects.equals(JsfBase.getRealPath(), null))
        this.realPath= JsfBase.getRealPath();
      else
        this.realPath= "/appservers/apache-tomcat-8.5.20/webapps/ROOT/";
    else
      this.realPath= realPath;
  }
  
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
    Boolean regresar = Boolean.FALSE;
    this.messageError= "Ocurrio un error en calculo de la nómina";
    try {
      this.nomina= (TcKeetNominasDto)DaoFactory.getInstance().findById(sesion, TcKeetNominasDto.class, this.idNomina);
      if(this.automatico) {
        this.nomina.setIdCompleta(1L);
        this.nomina.setObservaciones("PROCESO DE APERTURA AUTOMATICO DE NÓMINA");
      } // if  
			switch(accion) {
				case PROCESAR:
          regresar= this.procesar(sesion, Objects.equals(this.nomina.getIdNominaEstatus(), ENominaEstatus.INICIADA.getIdKey()));
          this.notificarResumenDestajos(sesion);
          this.notificarControl(sesion);
          this.toAddNewNomina(sesion);
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
			this.nomina= null;
		} // finally
    return regresar;
  }

	private Boolean procesar(Session sesion, Boolean notificar) throws Exception {
    Boolean regresar          = Boolean.FALSE;
		Map<String, Object> params= new HashMap<>();
		Monitoreo monitoreo       = this.autentifica.getMonitoreo();
    Puente puente             = null;
		try {
			monitoreo.comenzar(0L);
			params.put("sucursales", this.autentifica.getEmpresa().getSucursales());
			params.put("idNomina", this.idNomina);
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			List<Entity> todos= DaoFactory.getInstance().toEntitySet(sesion, "VistaNominaDto", "general", params, Constantes.SQL_TODOS_REGISTROS);
			if(todos!= null && !todos.isEmpty()) {
  			monitoreo.setTotal(new Long(todos.size()));
	  		monitoreo.setId("NOMINA DEL PERSONAL");				
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
                puente= new Puente(this.nomina, this.empleado, this.idNomina, item.toLong("idPivote"), autentifica);
                puente.ejecutar(EAccion.EMPLEADO);
                empleados++;
                break;
              case 1: // proveedor
                puente= new Puente(this.nomina, this.subcontratista, this.idNomina, item.toLong("idPivote"), autentifica);
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
        monitoreo.terminar();
        this.texto= Global.format(EFormatoDinamicos.MILES_SIN_DECIMALES, empleados)+ " persona(s) y "+ Global.format(EFormatoDinamicos.MILES_SIN_DECIMALES, subcontratistas)+ " proveedor(es)";
        if(notificar)
          this.notificarCorteNomina(sesion, this.empleado.getPeriodo());
      } // if
      regresar= Boolean.TRUE;
		} // try
    catch(Exception e) {
      throw e;
    } // catch
		finally {
      monitoreo.terminar();
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
			Entity empleados= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcKeetNominasPersonasDto", "personas", params);
      if(empleados!= null && !empleados.isEmpty()) {
        this.nomina.setPersonas(empleados.toLong("personas"));
        this.nomina.setAportaciones(empleados.toDouble("aportaciones"));
        this.nomina.setDeducciones(empleados.toDouble("deducciones"));
        this.nomina.setPercepciones(empleados.toDouble("percepciones"));
        this.nomina.setNeto(empleados.toDouble("neto"));
      } // if  
			Entity proveedores= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcKeetNominasProveedoresDto", "proveedores", params);
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
  
  private void notificarResumenDestajos(Session sesion) throws Exception {
    List<Columna> columns           = new ArrayList<>();		
    Map<String, Object> params      = new HashMap<>();
    Reporte jasper                  = null; 
    Map<String, Object> contratistas= new LinkedHashMap<>();
    Map<String, String> empleados   = new HashMap<>();
		try {
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
            empleados.put(item.toString("desarrollo"), this.toListadoNomina(sesion, jasper, item));
          } // for
          contratistas.put(Cadena.rellenar(""+ item.toLong("idDesarrollo"), 3, '0', true)+ "-"+ item.toString("contratista"), this.toReporte(sesion, jasper, item));
        } // for
        // NOTIFICAR A TODOS LOS RESIDENTES CON LOS REPORTES GENERADOS DE TODOS SUS CONTRATISTAS Y/O SUBCONTRATISTAS
        this.toNotificarSupervisor(sesion, contratistas, items.get(0), empleados);
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

  public void toNotificarSupervisor(Session sesion, Map<String, Object> contratistas, Entity periodo, Map<String, String> empleados) throws Exception {		
		Cafu cafu                     = null;
    Map<String, Object> residentes= new HashMap<>();
		try {
      LOG.error("-------------- ENTRO AL PROCESO DE NOTIFICAR --------------------------");
      LOG.error("Con un total de contratistas: "+ contratistas.size());
      // CAMBIAR POR UNA COLECCION CON EL NOMBRE DEL RESIENTE Y SU CELULAR
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

      Cuentas cuentas= new Cuentas("supervisor");
      if(Arrays.toString(this.notificar).contains("1")) 
        residentes.putAll(cuentas.all());
      else
        residentes.putAll(cuentas.admin());
      cafu= new Cafu("", "", periodo.toString("nomina"), "*"+ periodo.toString("inicio")+ "* al *"+ periodo.toString("termino")+ "*", "", "", contratistas, "", this.realPath, empleados);
      for (String residente: residentes.keySet()) {
        cafu.setNombre(Cadena.nombrePersona(residente));
        cafu.setCelular((String)residentes.get(residente));
        LOG.info("Enviando whatsapp: "+ residente);
        cafu.doSendSupervisor(sesion);
      } // for
      if(!this.automatico && residentes.isEmpty())
        JsfBase.addMessage("No se selecciono ningún celular, por favor verifiquelo", ETipoMensaje.ALERTA);
		} // try 
		catch(Exception e) {
			throw e;
		} // catch
    finally {
      Methods.clean(residentes);
    } // finally
	} 
  
  public String toListadoNomina(Session sesion, Reporte jasper, Entity figura) throws Exception {    
    String regresar              = null;
		Map<String, Object>parametros= null;
		EReportes seleccion          = null;    
    Map<String, Object>params    = new HashMap<>();
    Parametros comunes           = null;
    try {
      seleccion = EReportes.LISTADO_NOMINA_CALCULADA;  
      comunes   = new Parametros(this.autentifica.getEmpresa().getIdEmpresa());
      parametros= comunes.getComunes();
      parametros.put("ENCUESTA", this.autentifica.getEmpresa().getNombre().toUpperCase());
      parametros.put("NOMBRE_REPORTE", seleccion.getNombre());
      parametros.put("REPORTE_TITULO", seleccion.getTitulo());
      parametros.put("REPORTE_ICON", this.realPath.concat("resources/iktan/icon/acciones/"));	
      parametros.put("REPORTE_ICON", this.realPath.concat("/resources/janal/img/sistema/"));
      parametros.put("REPORTE_EMPRESA_LOGO", this.toLookForEmpresaLogo(sesion, this.autentifica.getEmpresa().getIdEmpresa()));
      params.put("sortOrder", "order by nombre_empresa, nomina, desarrollo, puesto, nombre_completo asc");
      params.put(Constantes.SQL_CONDICION, "tc_keet_nominas.id_nomina= "+  this.idNomina+ " and tc_keet_contratos_personal.id_desarrollo="+ figura.toLong("idDesarrollo"));
      String nombre= "EMPLEADOS-"+ Cadena.rellenar(""+ figura.toString("desarrollo"), 3, '0', true)+ "-"+ figura.toString("nomina");
      jasper.toAsignarReporte(new ParametrosReporte(seleccion, params, parametros), nombre);		
      regresar= jasper.getAlias();
      String name= this.realPath.concat(this.realPath.endsWith(File.separator)? "": File.separator).concat(jasper.getNombre());
      File file= new File(name);
      if(file.exists())
        file.delete();
    	jasper.toProcess(sesion, this.realPath, this.automatico);
      LOG.info("Reporte generado: "+ name);
    } // try
    catch(Exception e) {
     throw e;
    } // catch	
    return regresar;
  }

  public String toReporte(Session sesion, Reporte jasper, Entity figura) throws Exception {    
    String regresar              = null;
		Map<String, Object>parametros= null;
		EReportes seleccion          = null;    
    Map<String, Object>params    = new HashMap<>();
    Parametros comunes           = null;
    try {
      comunes= new Parametros(this.autentifica.getEmpresa().getIdEmpresa());
      parametros= comunes.getComunes();
      seleccion= figura.toLong("idTipoFigura").equals(1L)? EReportes.DESTAJOS_TOTALES_CONTRATISTA: EReportes.DESTAJOS_TOTALES_SUBCONTRATISTA;  
      parametros.put("REPORTE_TIPO_PERSONA", figura.toLong("idTipoFigura").equals(1L)? "DESTAJO CONTRATISTA": "DESTAJO SUBCONTRATISTA"); 
      parametros.put("REPORTE_FIGURA", figura.toString("contratista"));
      parametros.put("REPORTE_DEPARTAMENTO", figura.toString("departamento"));
      parametros.put("ENCUESTA", this.autentifica.getEmpresa().getNombre().toUpperCase());
      parametros.put("REPORTE_TITULO", seleccion.getTitulo());
      parametros.put("NOMBRE_REPORTE", seleccion.getNombre());
      parametros.put("REPORTE_ICON", this.realPath.concat("/resources/janal/img/sistema/"));
      parametros.put("REPORTE_EMPRESA_LOGO", this.toLookForEmpresaLogo(sesion, this.autentifica.getEmpresa().getIdEmpresa()));
      params.put("sortOrder", "order by tc_keet_contratos.clave, tc_keet_contratos_lotes.manzana, tc_keet_contratos_lotes.lote");
      params.put("loNuevo", "");
      params.put("idNomina", this.idNomina);
      params.put("idEmpresaPersona", figura.toLong("idEmpresaPersona"));
      params.put("idProveedor", figura.getKey());
      params.put(Constantes.SQL_CONDICION, "tc_keet_desarrollos.id_desarrollo= "+ figura.toLong("idDesarrollo"));
      String nombre= figura.toLong("idTipoFigura")+ Cadena.rellenar(""+ figura.getKey(), 5, '0', true)+ "-"+ Cadena.rellenar(""+ figura.toLong("idDesarrollo"), 3, '0', true)+ "-"+ figura.toString("nomina");
      jasper.toAsignarReporte(new ParametrosReporte(seleccion, params, parametros), nombre);		
      regresar= jasper.getAlias();
      String name= this.realPath.concat(this.realPath.endsWith(File.separator)? "": File.separator).concat(jasper.getNombre());
      File file= new File(name);
      if(file.exists())
        file.delete();
    	jasper.toProcess(sesion, this.realPath, this.automatico);
      LOG.info("Reporte generado: "+ name);
    } // try
    catch(Exception e) {
      throw e;
    } // catch	
    return regresar;
  } 

  private String toLookForEmpresaLogo(Session sesion, Long idEmpresa) {
    String regresar           = null;
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put("idEmpresa", idEmpresa);      
      Value value = DaoFactory.getInstance().toField(sesion, "TcManticEmpresasDto", "logo", params, "imagen");
      if(value!= null && value.getData()!= null)
        regresar= this.realPath.concat(Constantes.RUTA_IMAGENES).concat(value.toString());
      else
        regresar= this.realPath.concat(Constantes.RUTA_IMAGENES).concat(Configuracion.getInstance().getEmpresa("logo"));
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

  private void notificarControl(Session sesion) throws Exception {
    List<Columna> columns           = new ArrayList<>();		
    Map<String, Object> params      = new HashMap<>();
    Map<String, Object> residentes  = new HashMap<>();
    Map<String, Object> contratistas= new HashMap<>();
    String control                  = Arrays.toString(this.notificar);
    String desarrollo               = "";
    Boolean particular              = Boolean.FALSE;
    Reporte jasper                  = null; 
		try {
      if(control.contains("2") || control.contains("3") || control.contains("4")) {
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
              if(!Objects.equals(idDesarrollo, -1L) && control.contains("2")) 
                this.toControlResidentes(sesion, residentes, contratistas, item, desarrollo);
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
              particular= control.contains("3");
              params.put(Constantes.SQL_CONDICION, "id_persona="+ item.getKey());
              celulares= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "TrManticPersonaTipoContactoDto", "row", params);
            } // if
            else {
              particular= control.contains("4");
              params.put(Constantes.SQL_CONDICION, "id_proveedor="+ item.getKey());
              celulares= (List<Entity>)DaoFactory.getInstance().toEntitySet(sesion, "TrManticProveedorTipoContactoDto", "row", params);
            } // else
            String celular= null;
            if(celulares!= null && !celulares.isEmpty())
              for (Entity telefono: celulares) {
                if(Objects.equals(telefono.toLong("idPreferido"), 1L) && (Objects.equals(telefono.toLong("idTipoContacto"), ETiposContactos.CELULAR.getKey()) || Objects.equals(telefono.toLong("idTipoContacto"), ETiposContactos.CELULAR_NEGOCIO.getKey()) || Objects.equals(telefono.toLong("idTipoContacto"), ETiposContactos.CELULAR_PERSONAL.getKey()))) 
                  celular= telefono.toString("valor");
              } // for
              // NOTIFICAR AL A LOS CONTRATISTAS Y/O SUBCONTRATISAS CON EL REPORTE DE LOS DESTAJOS
            contratistas.put(item.toString("contratista"), this.toControlMessage(sesion, jasper, celular, item, particular));
          } // for
          // NOTIFICAR A TODOS LOS RESIDENTES CON LOS REPORTES GENERADOS DE LOS CONTRATISTAS
          if(control.contains("2"))
            this.toControlResidentes(sesion, residentes, contratistas, items.get(0), desarrollo);
        } // if  
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
  
	private void toControlResidentes(Session sesion, Map<String, Object> residentes, Map<String, Object> contratistas, Entity periodo, String desarrollo) throws Exception {		
		Cafu cafu = null;
		try {
      // CAMBIAR POR UNA COLECCION CON EL NOMBRE DEL RESIENTE Y SU CELULAR
      if(residentes!= null && !residentes.isEmpty()) {
        cafu= new Cafu("", "", periodo.toString("nomina"), "*"+ periodo.toString("inicio")+ "* al *"+ periodo.toString("termino")+ "*", "", "", contratistas, desarrollo, this.realPath);
        for (String residente: residentes.keySet()) {
          cafu.setNombre(Cadena.nombrePersona(residente));
          cafu.setCelular((String)residentes.get(residente));
          LOG.info("Enviando whatsapp: "+ residente);
          cafu.doSendResidentes(sesion, "[ *CORTE PRELIMINAR DE NÓMINA* ] ".concat(cafu.toSaludo()));
        } // for
      } // if  
		} // try 
		catch(Exception e) {
			throw e;
		} // catch
	} // toControlResidentes
  
	private String toControlMessage(Session sesion, Reporte jasper, String contratista, Entity sujeto, Boolean enviar) throws Exception {		
    String regresar= "";
		Cafu cafu      = null;
		try {
			regresar= this.toReporte(sesion, jasper, sujeto);
      if(contratista!= null && enviar) {
        try {
          cafu= new Cafu(sujeto.toString("contratista"), contratista, regresar, sujeto.toString("nomina"), "*"+ sujeto.toString("inicio")+ "* al *"+ sujeto.toString("termino")+ "*", this.realPath);
          LOG.info("Enviando whatsapp: "+ contratista);
          cafu.doSendDestajo(sesion, "[ *CORTE PRELIMINAR DE NÓMINA* ] ".concat(cafu.toSaludo()));
        } // try
        finally {
          LOG.info("Eliminando archivo: "+ jasper.getNombre());				  
        } // finally	
      } // if  
		} // try 
		catch(Exception e) {
			throw e;
		} // catch
    return regresar;
	} 

	private void toAddNewNomina(Session sesion) throws Exception {
		Map<String, Object> params= new HashMap<>();
		try {
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
						this.autentifica.getPersona().getIdUsuario(), // Long idUsuario, 
						0D, // Double subtotal, 
						"", // String observaciones, 
						this.autentifica.getEmpresa().getIdEmpresa(), // Long idEmpresa, 
						0D, // Double percepciones
            2L // Long idCompleta
					);
					DaoFactory.getInstance().insert(sesion, siguiente);
					this.bitacora= new TcKeetNominasBitacoraDto(
						"CREAR NOMINA TEMPORAL", // String justificacion, 
						this.nomina.getIdNominaEstatus(), // Long idNominaEstatus, 
						this.autentifica.getPersona().getIdUsuario(), // Long idUsuario, 
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
  
}
