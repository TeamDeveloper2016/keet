package mx.org.kaana.keet.nomina.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.procesos.reportes.beans.ExportarXls;
import mx.org.kaana.kajool.procesos.reportes.beans.Modelo;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.catalogos.contratos.destajos.comun.IBaseReporteDestajos;
import mx.org.kaana.keet.comun.Catalogos;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EExportacionXls;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.incidentes.ETipoIndicente;
import mx.org.kaana.xml.Dml;

@Named(value = "keetNominasPersonas")
@ViewScoped
public class Personas extends IBaseReporteDestajos implements Serializable {

	private static final long serialVersionUID = 6319984968937774153L;
	private FormatLazyModel lazyDetalle;
	private FormatLazyModel lazyDestajo;  

	public FormatLazyModel getLazyDetalle() {
		return lazyDetalle;
	}

	public void setLazyDetalle(FormatLazyModel lazyDetalle) {
		this.lazyDetalle=lazyDetalle;
	}

	public FormatLazyModel getLazyDestajo() {
		return lazyDestajo;
	}

	public void setLazyDestajo(FormatLazyModel lazyDestajo) {
		this.lazyDestajo=lazyDestajo;
	}
	
  public String getCostoGrupo() {
    Double costo = 0D;
		if(this.lazyModel!= null)
			for (IBaseDto item: (List<IBaseDto>)this.lazyModel.getWrappedData()) {
				Entity row= (Entity)item;
				costo+= row.toDouble("pago");
			} // for	
		return Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, costo);
	}

  public String getCostoTotal() {
		return Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, (Double)this.attrs.get("detajoNominaContratista"));
	}

	@PostConstruct
  @Override
  protected void init() {
    try {
			this.initBase();
			this.attrs.put("idTipoFiguraCorreo", 1L);			
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			Long idNomina= (Long)JsfBase.getFlashAttribute("idNomina");
			this.loadCatalogs();
			this.attrs.put("nomina", false);
			this.attrs.put("destajos", false);
      this.attrs.put("detajoNominaContratista", 0D);
			if(!Cadena.isVacio(idNomina)) {
				Entity entity= new Entity(idNomina);
				entity.put("idNomina", new Value("idNomina", idNomina));
  			entity.put("nomina", new Value("nomina", (String)JsfBase.getFlashAttribute("nomina")));
  			entity.put("nombreCompleto", new Value("nombreCompleto", (String)JsfBase.getFlashAttribute("nombreCompleto")));
  			entity.put("idEmpresaPersona", new Value("idEmpresaPersona", (Long)JsfBase.getFlashAttribute("idEmpresaPersona")));
  			entity.put("idPuesto", new Value("idPuesto", (Long)JsfBase.getFlashAttribute("idPuesto")));
  			entity.put("idPersona", new Value("idPersona", (Long)JsfBase.getFlashAttribute("idPersona")));
				this.attrs.put("idNomina", new UISelectEntity(entity));
				this.attrs.put("idEmpresaPersona", new UISelectEntity(JsfBase.getFlashAttribute("idEmpresaPersona")== null? -1L: (Long)JsfBase.getFlashAttribute("idEmpresaPersona")));
				this.attrs.put("seleccionado", entity);
				this.doLoad();
				if(!Cadena.isVacio(entity.toLong("idEmpresaPersona")) && entity.toLong("idEmpresaPersona")!= -1L)
					this.doLoadDetalle();
				this.attrs.put("idNomina", new UISelectEntity(-1L));
				this.attrs.put("idEmpresaPersona", new UISelectEntity(-1L));
		  }
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns    = null;
		Map<String, Object>params= null;
    try {
      params= this.toPrepare();	
			params.put("sortOrder", "order by nomina desc");
      columns= new ArrayList<>();
      columns.add(new Columna("percepciones", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("deducciones", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("neto", EFormatoDinamicos.MILES_CON_DECIMALES));
      this.lazyModel = new FormatCustomLazy("VistaNominaConsultasDto", "personas", params, columns);
      UIBackingUtilities.resetDataTable();
			this.attrs.put("nomina", false);
			this.attrs.put("destajo", false);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally		
  } // doLoad

	private void loadEmpresas() {
		Map<String, Object>params= null;
		List<Columna> columns    = null;
		try {
			params = new HashMap<>();
			columns= new ArrayList<>();			
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally{
			Methods.clean(params);
		}	// finally	
	} // loadEmpresas

  public String doAccion() {
		String regresar           = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			Entity entity= (Entity)this.attrs.get("seleccionado");
			params.put("sortOrder", "order by tc_keet_nominas_detalles.id_nomina_persona, tc_keet_nominas_conceptos.id_tipo_concepto desc, tc_keet_nominas_conceptos.orden");
			params.put("idNomina", entity.toLong("idNomina"));
			params.put("nomina", entity.toString("nomina"));
			params.put("nombreCompleto", entity.toString("nombreCompleto"));
			params.put("idEmpresaPersona", entity.toLong("idEmpresaPersona"));
			JsfBase.setFlashAttribute(Constantes.REPORTE_REFERENCIA, new ExportarXls(new Modelo((Map<String, Object>) ((HashMap)params).clone(), EExportacionXls.NOMINA_PERSONA.getProceso(), EExportacionXls.NOMINA_PERSONA.getIdXml(), EExportacionXls.NOMINA_PERSONA.getNombreArchivo()), EExportacionXls.NOMINA_PERSONA, "NOMINA,NOMBRE COMPLETO,CONCEPTO,CLAVE,NOMBRE,VALOR,FECHA"));
			JsfBase.getAutentifica().setMonitoreo(new Monitoreo());
			regresar = "/Paginas/Reportes/excel".concat(Constantes.REDIRECIONAR);				
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;		
  } // doAccion

	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar  = new HashMap<>();	
		StringBuilder sb              = new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && ((UISelectEntity)this.attrs.get("idEmpresa")).getKey()>= 1L)				
			sb.append("(tc_keet_nominas.id_empresa in (").append(((UISelectEntity)this.attrs.get("idEmpresa")).getKey()).append(")) and ");
		else
			sb.append("(tc_keet_nominas.id_empresa in (").append(JsfBase.getAutentifica().getEmpresa().getSucursales()).append(")) and ");
		if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && ((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()>= 1L)				
			sb.append("(tc_keet_contratos_personal.id_desarrollo= ").append(((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()).append(") and ");
  	regresar.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
		if(!Cadena.isVacio(this.attrs.get("idNomina")) && ((UISelectEntity)this.attrs.get("idNomina")).getKey()>= 1L)
			sb.append("tc_keet_nominas.id_nomina=").append(this.attrs.get("idNomina")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("idEmpresaPersona")) && ((UISelectEntity)this.attrs.get("idEmpresaPersona")).getKey()>= 1L)
			sb.append("tr_mantic_empresa_personal.id_empresa_persona=").append(this.attrs.get("idEmpresaPersona")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("ejercicio")) && ((UISelectEntity)this.attrs.get("ejercicio")).getKey()>= 1L)				
			sb.append("tc_keet_nominas_periodos.ejercicio = ").append(((UISelectEntity)this.attrs.get("ejercicio")).getKey()).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("semana")) && ((UISelectEntity)this.attrs.get("semana")).getKey()>= 1L)				
			sb.append("tc_keet_nominas_periodos.orden = ").append(((UISelectEntity)this.attrs.get("semana")).getKey()).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("idTipoNomina")) && ((UISelectEntity)this.attrs.get("idTipoNomina")).getKey()>= 1L)				
			sb.append("tc_keet_nominas.id_tipo_nomina= ").append(((UISelectEntity)this.attrs.get("idTipoNomina")).getKey()).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("estatus")) && ((UISelectEntity)this.attrs.get("estatus")).getKey()>= 1L)				
			sb.append("tc_keet_nominas.id_nomina_estatus = ").append(((UISelectEntity)this.attrs.get("estatus")).getKey()).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("idSemaforo")) && ((UISelectEntity)this.attrs.get("idSemaforo")).getKey()>= 2L) {
      Long key= ((UISelectEntity)this.attrs.get("idSemaforo")).getKey();
      switch(key.intValue()) {
        case 1:   // Empleados activos
          sb.append("(tr_mantic_empresa_personal.id_activo= 1) and ");    
          break;
        case 2:   // Activos sin seguro
          sb.append("(tr_mantic_empresa_personal.id_activo= 1 and tr_mantic_empresa_personal.id_seguro= 2) and ");    
          break;
        case 3:   // Activos sin deposito al banco
          sb.append("(tr_mantic_empresa_personal.id_activo= 1 and tr_mantic_empresa_personal.id_nomina= 2) and ");    
          break;
        case 4:   // Empleados con licencia medica 
          // sb.append("(tr_mantic_empresa_personal.id_activo= 1 and tr_mantic_empresa_personal.id_nomina= 2 and tr_mantic_empresa_personal.id_seguro= 2) and ");    
          break;
      } // switch
    } // if  
		if(this.attrs.get("idPuesto")!= null && !Cadena.isVacio(this.attrs.get("idPuesto")) && Long.valueOf(this.attrs.get("idPuesto").toString())>= 1L)
			sb.append("tc_mantic_puestos.id_puesto=").append(this.attrs.get("idPuesto")).append(" and ");
		if(this.attrs.get("idDepartamento")!= null && !Cadena.isVacio(this.attrs.get("idDepartamento")) && Long.valueOf(this.attrs.get("idDepartamento").toString())>= 1L)
			sb.append("tc_keet_departamentos.id_departamento=").append(this.attrs.get("idDepartamento")).append(" and ");
		if(this.attrs.get("idContratista")!= null && !Cadena.isVacio(this.attrs.get("idContratista")) && ((UISelectEntity)this.attrs.get("idContratista")).getKey() >= 1L)			
			if(((UISelectEntity)this.attrs.get("idContratista")).getKey()== 999L)		
				sb.append("tr_mantic_empresa_personal.id_contratista is null and ");
			else
				sb.append("tr_mantic_empresa_personal.id_contratista=").append(this.attrs.get("idContratista")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("netoMayor")))
			sb.append("tc_keet_nominas_personas.neto>=").append(this.attrs.get("netoMayor")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("netoMenor")))
			sb.append("tc_keet_nominas_personas.neto<=").append(this.attrs.get("netoMenor")).append(" and ");
		if(this.attrs.get("nombre")!= null && !Cadena.isVacio(this.attrs.get("nombre"))) {
			String nombre= ((String)this.attrs.get("nombre")).toUpperCase().replaceAll("(,| |\\t)+", ".*.*");
  		sb.append("(upper(concat(tc_mantic_personas.nombres, ' ', tc_mantic_personas.paterno, ' ', ifnull(tc_mantic_personas.materno, ' '), ' ', ifnull(tc_mantic_personas.apodo, ' '))) regexp '.*").append(nombre).append(".*') and ");
		} // if
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	} // toPrepare

	private void loadCatalogs() {
		Map<String, Object>params= null;
		try {
			this.loadEmpresas();
			params= new HashMap<>();
		  params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.attrs.put("catalogo", UIEntity.seleccione("TcKeetNominasEstatusDto", "row", params, "nombre"));
      this.attrs.put("estatus", new UISelectEntity(-1L));
			Catalogos.toLoadNominas(this.attrs);
			Catalogos.toLoadEjercicios(this.attrs);
			Catalogos.toLoadSemanas(this.attrs);
			Catalogos.toLoadTiposNominas(this.attrs);
			Catalogos.toLoadDepartamentos(this.attrs);
			Catalogos.toLoadPuestos(this.attrs);
			Catalogos.toLoadContratistas(this.attrs);
      
      this.attrs.put("desarrollos", UIEntity.seleccione("TcKeetDesarrollosDto", "row", params, "clave"));
      this.attrs.put("idDesarrollo", new UISelectEntity(-1L));
      List<UISelectEntity> semaforos= new ArrayList<>();
      Entity seleccione= new Entity(-1L);
      seleccione.add("color", "circulo-gris");
      seleccione.add("nombre", "SELECCIONE");
      semaforos.add(new UISelectEntity(seleccione));
      Entity verde= new Entity(1L);
      verde.add("color", "circulo-verde");
      verde.add("nombre", "EMPLEADOS ACTIVOS");
      semaforos.add(new UISelectEntity(verde));
      Entity amarillo= new Entity(2L);
      amarillo.add("color", "circulo-amarillo");
      amarillo.add("nombre", "ACTIVOS SIN SEGURO");
      semaforos.add(new UISelectEntity(amarillo));
      Entity azul= new Entity(3L);
      azul.add("color", "circulo-azul");
      azul.add("nombre", "ACTIVOS SIN DEPOSITO AL BANCO");
      semaforos.add(new UISelectEntity(azul));
//      Entity turquesa= new Entity(4L);
//      turquesa.add("color", "circulo-turquesa");
//      turquesa.add("nombre", "ACTIVOS SIN SEGURO/DEPOSITO");
//      semaforos.add(new UISelectEntity(turquesa));
//      Entity magenta= new Entity(5L);
//      magenta.add("color", "circulo-magenta");
//      magenta.add("nombre", "EMPLEADOS CON INCAPACIDAD MEDICA");
//      semaforos.add(new UISelectEntity(magenta));
      this.attrs.put("semaforos", semaforos);
      this.attrs.put("idSemaforo", new UISelectEntity(-1L));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);	
		} // catch				
    finally {
			Methods.clean(params);
		}	// finally
	} // loadCatalogs

  public String doExportar() {
		String regresar           = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			Entity entity= (Entity)this.attrs.get("seleccionado");
			params.put("sortOrder", "order by tc_keet_contratos.etapa, tc_keet_contratos_lotes.manzana, tc_keet_contratos_lotes.lote");
			params.put("idNomina", entity.toLong("idNomina"));
			params.put("nomina", entity.toString("nomina"));
			params.put("nombreCompleto", entity.toString("nombreCompleto"));
			params.put("idEmpresaPersona", entity.toLong("idEmpresaPersona"));
			JsfBase.setFlashAttribute(Constantes.REPORTE_REFERENCIA, new ExportarXls(new Modelo((Map<String, Object>) ((HashMap)params).clone(), EExportacionXls.DESTAJO_PERSONA.getProceso(), EExportacionXls.DESTAJO_PERSONA.getIdXml(), EExportacionXls.DESTAJO_PERSONA.getNombreArchivo()), EExportacionXls.DESTAJO_PERSONA, "NOMINA,NOMBRE COMPLETO,DESARROLLO,CONTRATO,ETAPA,LOTE,CODIGO,CONCEPTO,PORCENTAJE,COSTO"));
			JsfBase.getAutentifica().setMonitoreo(new Monitoreo());
			regresar = "/Paginas/Reportes/excel".concat(Constantes.REDIRECIONAR);				
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
	
	public void doReporte(String nombre) throws Exception {
		doReporte(nombre, false);
	} // doReporte	
	
	@Override
	public void doReporte(String nombre, boolean sendMail) throws Exception {    
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;    
    Map<String, Object>params    = null;
    Parametros comunes           = null;
    Entity entity                = null;
    Map<String,Object>paramsPpal = null;
    try {
      params= new HashMap<>();  
      reporteSeleccion= EReportes.valueOf(nombre);  
      comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      parametros= comunes.getComunes();
      parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getNombre().toUpperCase());
      parametros.put("NOMBRE_REPORTE", reporteSeleccion.getNombre());
      parametros.put("REPORTE_TITULO", reporteSeleccion.getTitulo());
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));	
      this.reporte= JsfBase.toReporte();
      if(!reporteSeleccion.equals(EReportes.LISTADO_NOMINA_PERSONAS)){
        paramsPpal= this.toPrepare();
        entity= (Entity)this.attrs.get("seleccionado");
        paramsPpal.put("sortOrder", "order by nomina desc");
        paramsPpal.put(Constantes.SQL_CONDICION, "tc_keet_nominas_personas.id_nomina= ".concat(entity.toLong("idNomina").toString()).concat(" and tc_keet_nominas_personas.id_empresa_persona= ").concat(entity.toLong("idEmpresaPersona").toString()));      
        params.put("sortOrder", "order by tc_keet_nominas_detalles.id_nomina_persona, tc_keet_nominas_conceptos.id_tipo_concepto desc, tc_keet_nominas_conceptos.orden");
        params.put("idNomina", entity.toLong("idNomina"));
        params.put("nomina", entity.toString("nomina"));
        params.put("nombreCompleto", entity.toString("nombreCompleto"));
        params.put("idEmpresaPersona", entity.toLong("idEmpresaPersona"));
        parametros.put("SQL_UNO", Dml.getInstance().getSelect("VistaNominaConsultasDto", "persona", params));
        params.put("sortOrder", "order by tc_keet_contratos.etapa, tc_keet_contratos_lotes.manzana, tc_keet_contratos_lotes.lote");
        parametros.put("SQL_DOS", Dml.getInstance().getSelect("VistaNominaConsultasDto", "destajo", params));
        parametros.put(Constantes.TILDE.concat("SUBREPORTE_1"), "/Paginas/Keet/Nominas/Reportes/detallePersona_subreport1.jasper");
        parametros.put(Constantes.TILDE.concat("SUBREPORTE_2"), "/Paginas/Keet/Nominas/Reportes/detallePersona_subreport2.jasper");
        this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, paramsPpal, parametros));		
      }
      else{
        params= this.toPrepare();	
        params.put("sortOrder", "order by	nombre_empresa, nomina, puesto,  nombre_completo asc");
        this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));		
      }
      this.attrs.put("tituloCorreo", reporteSeleccion.getTitulo());
      if(sendMail)
        this.reporte.doAceptarSimple();			
			else{
				if(doVerificarReporte())
					this.reporte.doAceptar();			
			} // else
    } // try
    catch(Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);			
    } // catch	
  } // doReporte 		
	
  public void doLoadDetalle() {
    List<Columna> columns    = null;
		Map<String, Object>params= new HashMap<>();
    try {
			Entity entity= (Entity)this.attrs.get("seleccionado");
			params.put("sortOrder", "order by tc_keet_nominas_detalles.id_nomina_persona, tc_keet_nominas_conceptos.id_tipo_concepto desc, tc_keet_nominas_conceptos.orden");
			params.put("idNomina", entity.toLong("idNomina"));
			params.put("nomina", entity.toString("nomina"));
			params.put("nombreCompleto", entity.toString("nombreCompleto"));
			params.put("idEmpresaPersona", entity.toLong("idEmpresaPersona"));
      columns= new ArrayList<>();
      columns.add(new Columna("valor", EFormatoDinamicos.MILES_SIN_DECIMALES));
      columns.add(new Columna("fecha", EFormatoDinamicos.FECHA_CORTA));
      this.lazyDetalle= new FormatCustomLazy("VistaNominaConsultasDto", "persona", params, columns);
      UIBackingUtilities.resetDataTable("detalle");
			Long puesto= entity.toLong("idPuesto");
			this.attrs.put("destajos", puesto== 6L);
			if(puesto== 6L)
			  this.doLoadDestajo();
			this.attrs.put("nomina", true);
      UIBackingUtilities.scrollTo("detalle");
      this.attrs.put("incidencia", this.doCheckIncidente(entity));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally		
  } // doLoadDetalle
	
  public void doLoadDestajo() {
    List<Columna> columns    = null;
		Map<String, Object>params= new HashMap<>();
    try {
			Entity entity= (Entity)this.attrs.get("seleccionado");
			params.put("sortOrder", "order by tc_keet_contratos.etapa, tc_keet_contratos_lotes.manzana, tc_keet_contratos_lotes.lote");
			params.put("idNomina", entity.toLong("idNomina"));
			params.put("nomina", entity.toString("nomina"));
			params.put("nombreCompleto", entity.toString("nombreCompleto"));
			params.put("idEmpresaPersona", entity.toLong("idEmpresaPersona"));
			this.attrs.put("idFiguraCorreo", entity.toLong("idPersona"));
			this.attrs.put("figuraNombreCompletoCorreo", entity.toString("nombreCompleto"));
      columns= new ArrayList<>();
      columns.add(new Columna("porcentaje", EFormatoDinamicos.MILES_SIN_DECIMALES));
      columns.add(new Columna("costo", EFormatoDinamicos.MILES_SIN_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));
      this.lazyDestajo= new FormatCustomLazy("VistaNominaConsultasDto", "destajo", params, columns);
      UIBackingUtilities.resetDataTable("destajo");
      UIBackingUtilities.scrollTo("destajo");
      Value total= (Value)DaoFactory.getInstance().toField("VistaNominaConsultasDto", "detajoNominaContratista", params, "total");
      if(total!= null && total.getData()!= null)
        this.attrs.put("detajoNominaContratista", total.toDouble());
      else
        this.attrs.put("detajoNominaContratista", 0D);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally		
  } // doLoadDestajo	
  
  public String doCheckIncidente(Entity row) {
    String regresar          = "";
    Map<String, Object>params= null;
    List<Columna>columns     = null;		
		try {		
      params= new HashMap<>();      
      params.put("idEmpresaPersona", row.toLong("idEmpresaPersona"));
 			columns= new ArrayList<>();
			columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
			columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
      Entity entity= (Entity)DaoFactory.getInstance().toEntity("TcManticIncidentesDto", "incidente", params);
      if(entity!= null && !entity.isEmpty()) {
        UIBackingUtilities.toFormatEntity(entity, columns);
        regresar= "<i class='fa fa-lg ".concat(ETipoIndicente.toIcon(entity.toLong("idTipoIncidente").intValue())).concat("' title='Vigencia: [").concat(entity.toString("inicio")).concat(" al ").concat(entity.toString("termino")).concat("]  Tipo: [").concat(ETipoIndicente.toTitle(entity.toLong("idTipoIncidente").intValue())).concat("]'></i>");
      } // if
    } // try
    catch(Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);			
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
    return regresar;
  } // 
  
}