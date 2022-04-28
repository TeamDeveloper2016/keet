package mx.org.kaana.keet.estmaciones.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Periodo;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EReportes;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;

@Named(value = "keetEstimacionesSaldos")
@ViewScoped
public class Saldos extends IBaseFilter implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Saldos.class);
  private static final long serialVersionUID= 8793667741599428879L;
  private Reporte reporte;
  protected FormatLazyModel lazyModelDetalle;
  private LocalDate fechaInicio;
  private LocalDate fechaTermino;
  private LocalDate vigenciaIni;
  private LocalDate vigenciaFin;
	private FormatLazyModel historialPagos;
	private List<Entity> pagosRealizados;
	private Long individual;

  public FormatLazyModel getLazyModelDetalle() {
    return lazyModelDetalle;
  }

  public LocalDate getFechaInicio() {
    return fechaInicio;
  }

  public void setFechaInicio(LocalDate fechaInicio) {
    this.fechaInicio = fechaInicio;
  }

  public LocalDate getFechaTermino() {
    return fechaTermino;
  }

  public void setFechaTermino(LocalDate fechaTermino) {
    this.fechaTermino = fechaTermino;
  }

  public LocalDate getVigenciaIni() {
    return vigenciaIni;
  }

  public void setVigenciaIni(LocalDate vigenciaIni) {
    this.vigenciaIni = vigenciaIni;
  }

  public LocalDate getVigenciaFin() {
    return vigenciaFin;
  }

  public void setVigenciaFin(LocalDate vigenciaFin) {
    this.vigenciaFin = vigenciaFin;
  }
  
  public FormatLazyModel getHistorialPagos() {
    return historialPagos;
  }

  public List<Entity> getPagosRealizados() {
    return pagosRealizados;
  }

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("idEmpresa", new UISelectEntity(JsfBase.getAutentifica().getEmpresa().getIdEmpresa()));
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
				this.toLoadSucursales();
      if(JsfBase.getFlashAttribute("idContratoProcess")!= null) {
        this.individual= (Long)JsfBase.getFlashAttribute("idClienteProcess");
        this.doLoad();
      } // if  
      this.toLoadCatalog();
      this.attrs.put("ikContrato", -1L);
      this.attrs.put("ikEstimacion", -1L);
      this.individual= -1L;
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns     = null;
	  Map<String, Object> params= null;	
    try {
  	  params = this.toPrepare();	
      params.put("sortOrder", "order by	tc_mantic_clientes.razon_social");
      columns= new ArrayList<>();
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));      
      columns.add(new Columna("contratos", EFormatoDinamicos.MILES_SIN_DECIMALES));      
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));      
      columns.add(new Columna("anticipo", EFormatoDinamicos.MILES_CON_DECIMALES));      
      columns.add(new Columna("estimaciones", EFormatoDinamicos.MILES_CON_DECIMALES));    
      columns.add(new Columna("saldo", EFormatoDinamicos.MILES_CON_DECIMALES));    
      columns.add(new Columna("amortizacion", EFormatoDinamicos.MILES_CON_DECIMALES));    
      columns.add(new Columna("fondoGarantia", EFormatoDinamicos.MILES_CON_DECIMALES));    
      columns.add(new Columna("retenciones", EFormatoDinamicos.MILES_CON_DECIMALES));    
			this.lazyModel = new FormatCustomLazy("VistaEstimacionesDto", "clientes", params, columns);
      UIBackingUtilities.resetDataTable();	
      this.lazyModelDetalle= null;
      this.pagosRealizados = null;
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

	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb            = new StringBuilder("");
	  UISelectEntity cliente      = (UISelectEntity)this.attrs.get("cliente");
		List<UISelectEntity>clientes= (List<UISelectEntity>)this.attrs.get("clientes");
		if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && !this.attrs.get("idDesarrollo").toString().equals("-1"))
  		sb.append("(tc_keet_desarrollos.id_desarrollo= ").append(this.attrs.get("idDesarrollo")).append(") and ");
    if(Objects.equals(this.individual, -1L)) {
      if(!Cadena.isVacio(this.attrs.get("idCliente")) && !this.attrs.get("idCliente").toString().equals("-1"))
        sb.append("tc_mantic_clientes.id_cliente= ").append(this.attrs.get("idCliente")).append(" and ");
      if(clientes!= null && cliente!= null && clientes.indexOf(cliente)>= 0) 
        sb.append("tc_mantic_clientes.razon_social like '%").append(clientes.get(clientes.indexOf(cliente)).toString("razonSocial")).append("%' and ");			
      else if(!Cadena.isVacio(JsfBase.getParametro("razonSocial_input")))
        sb.append("tc_mantic_clientes.razon_social like '%").append(JsfBase.getParametro("razonSocial_input")).append("%' and ");						
    } // if
    else {
      sb.append("tc_mantic_clientes.id_cliente= ").append(this.individual).append(" and ");
      if(!Cadena.isVacio(this.attrs.get("ikContrato")) && !Objects.equals((Long)this.attrs.get("ikContrato"), -1L))
        sb.append("tc_keet_contratos.id_contrato= ").append((Long)this.attrs.get("ikContrato")).append(" and ");
      if(!Cadena.isVacio(this.attrs.get("ikEstimacion")) && !Objects.equals((Long)this.attrs.get("ikEstimacion"), -1L))
        sb.append("tc_keet_estimaciones.id_estimacion= ").append((Long)this.attrs.get("ikEstimacion")).append(" and ");
    } // if  
  	if(!Cadena.isVacio(this.attrs.get("consecutivo")))
  		sb.append("(tc_keet_contratos.consecutivo like '%").append(this.attrs.get("consecutivo")).append("%') and ");
  	if(!Cadena.isVacio(this.attrs.get("nombre")))
  		sb.append("(tc_keet_contratos.nombre like '%").append(this.attrs.get("nombre")).append("%') and ");
		if(!Cadena.isVacio(this.fechaInicio))
		  sb.append("(date_format(tc_keet_contratos.registro, '%Y%m%d')>= '").append(this.fechaInicio.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.fechaTermino))
		  sb.append("(date_format(tc_keet_contratos.registro, '%Y%m%d')<= '").append(this.fechaTermino.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("vencidos")) && this.attrs.get("vencidos").toString().equals("1"))
  		sb.append("(now()> tc_keet_contratos.vence) and ");
		if(!Cadena.isVacio(this.attrs.get("dias")))
  		sb.append("((datediff(tc_keet_contratos.limite, now())* -1)>= ").append(this.attrs.get("dias")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("montoInicio")))
		  sb.append("(tc_keet_contratos.costo>= ").append((Double)this.attrs.get("montoInicio")).append(") and ");			
		if(!Cadena.isVacio(this.attrs.get("montoTermino")))
		  sb.append("(tc_keet_contratos.costo<= ").append((Double)this.attrs.get("montoTermino")).append(") and ");			
    UISelectEntity estatus= (UISelectEntity) this.attrs.get("idContratoEstatus");
    if(estatus!= null)
      if(Objects.equals(0L, estatus.getKey()))
        sb.append("tc_keet_contratos.id_contrato_estatus in (1, 2) and ");
      else
        if(!Objects.equals(-1L, estatus.getKey()))
          sb.append("(tc_keet_contratos.id_contrato_estatus= ").append(estatus.getKey()).append(") and ");
    if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))			
		  regresar.put("idEmpresa", this.attrs.get("idEmpresa").toString());
		else
		  regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	} // toPrepare	

  public void doLoadPagosRealizados(Entity row) {
    List<Columna> columns     = null;    
    Map<String, Object> params= null;
    try {      
      this.attrs.put("seleccionado", row);
      params = new HashMap<>();      
      params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());      
      params.put("idContrato", row.toLong("idContrato"));      
      columns = new ArrayList<>();
      columns.add(new Columna("factura", EFormatoDinamicos.MILES_CON_DECIMALES));      
      columns.add(new Columna("facturar", EFormatoDinamicos.MILES_CON_DECIMALES));    
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));      
      columns.add(new Columna("saldo", EFormatoDinamicos.MILES_CON_DECIMALES));    
      columns.add(new Columna("amortizacion", EFormatoDinamicos.MILES_CON_DECIMALES));    
      columns.add(new Columna("fondoGarantia", EFormatoDinamicos.MILES_CON_DECIMALES));    
      columns.add(new Columna("retenciones", EFormatoDinamicos.MILES_CON_DECIMALES));    
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));    
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));  
      this.pagosRealizados = (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaEstimacionesDto", "estimacion", params);
      if(this.pagosRealizados!= null && !this.pagosRealizados.isEmpty()) 
        UIBackingUtilities.toFormatEntitySet(this.pagosRealizados, columns);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
      Methods.clean(columns);
	  } // finally
  } 
  
 	protected void toLoadCatalog() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      List<UISelectEntity> estatus= (List<UISelectEntity>) UIEntity.seleccione("TcKeetContratosEstatusDto", "row", params, columns, "nombre");
      if(estatus!= null) {
        Entity entity= new Entity(0L);
        entity.put("nombre", new Value("nombre", "PENDIENTES"));
        UISelectEntity pendientes= new UISelectEntity(entity);
        estatus.add(1, pendientes);
      } // if
      this.attrs.put("allEstatus", estatus);
			this.attrs.put("idContratoEstatus", UIBackingUtilities.toFirstKeySelectEntity(estatus));
			this.doLoadDesarrollos();
			this.doLoadContratos();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // toLoadCatalog

	public void doClientes() {
		List<UISelectEntity> clientes= null;
    Map<String, Object> params   = null;
		List<Columna> columns        = null;
    try {
			columns= new ArrayList<>();
			if(this.attrs.get("busqueda")!= null && this.attrs.get("busqueda").toString().length()> 3) {
				params = new HashMap<>();      
				params.put(Constantes.SQL_CONDICION, "upper(razon_social) like upper('%".concat((String)this.attrs.get("busqueda")).concat("%')"));
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
				columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));			
				columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));			
				clientes = UIEntity.build("VistaClientesDto", "findRazonSocial", params, columns, Constantes.SQL_TODOS_REGISTROS);      
				this.attrs.put("clientes", clientes);      
				this.attrs.put("resultados", clientes.size());      
			} // if
			else 
				JsfBase.addMessage("Cliente", "Favor de teclear por lo menos 3 caracteres.", ETipoMensaje.ALERTA);
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
	} 

	private void toLoadSucursales() {
		List<UISelectEntity> sucursales= null;
		Map<String, Object>params      = null;
		List<Columna> columns          = null;
		try {
			columns= new ArrayList<>();
			params= new HashMap<>();
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			sucursales=(List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave");
			this.attrs.put("sucursales", sucursales);			
			this.attrs.put("idEmpresa", new UISelectEntity(-1L));			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} // toLoadSucursales
	
  public void doEstadoCuenta(String nombre, Entity row) throws Exception {
    this.individual= row.toLong("idCliente");
    this.doReporte(nombre);
    this.individual= -1L;    
  }

  public void doContrato(String nombre, Entity row) throws Exception {
    this.individual= row.toLong("idCliente");
    this.attrs.put("ikContrato", row.toLong("idContrato"));
    this.doReporte(nombre);
    this.attrs.put("ikContrato", -1L);
    this.individual= -1L;    
  }
  
  public void doEstimaciones(String nombre, Entity row) throws Exception {
    this.individual= row.toLong("idCliente");
    this.attrs.put("ikEstimacion", row.toLong("idEstimacion"));
    this.doReporte(nombre);
    this.attrs.put("ikEstimacion", -1L);
    this.individual= -1L;    
  }
  
  public void doReporte(String nombre) throws Exception {
  	Parametros comunes           = null;
		Map<String, Object>params    = null;
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;
		try{		
      reporteSeleccion= EReportes.valueOf(nombre);
      params= this.toPrepare();																																																					
      params.put("sortOrder", "order by	tc_mantic_clientes.id_cliente, tc_keet_contratos.id_contrato, tc_keet_estimaciones.id_estimacion desc");
      comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.reporte= JsfBase.toReporte();	
      parametros= comunes.getComunes();
      parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getNombre().toUpperCase());
      parametros.put("NOMBRE_REPORTE", reporteSeleccion.getTitulo());
      parametros.put("REPORTE_TITULO", reporteSeleccion.getTitulo());
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));			
      this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));		
      this.doVerificarReporte();
      this.reporte.doAceptar();			
    } // try
    catch(Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);			
    } // catch	
  } // doReporte
  
  public boolean doVerificarReporte() {
    boolean regresar = this.reporte.getTotal()> 0L;
		if(regresar) 
			UIBackingUtilities.execute("start(" + this.reporte.getTotal() + ")");		
    else {
			UIBackingUtilities.execute("generalHide();");		
			JsfBase.addMessage("Reporte", "No se encontraron registros para el reporte", ETipoMensaje.ERROR);
		} // else
    return regresar;
	} // doVerificarReporte	
	
	public String toColor(Entity row) {
		return "";
	}
	
	public List<UISelectEntity> doCompleteCliente(String codigo) {
 		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
		boolean buscaPorCodigo    = false;
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			if(!Cadena.isVacio(codigo)) {
  			codigo= new String(codigo).replaceAll(Constantes.CLEAN_SQL, "").trim();
				buscaPorCodigo= codigo.startsWith(".");
				if(buscaPorCodigo)
					codigo= codigo.trim().substring(1);
				codigo= codigo.toUpperCase().replaceAll("(,| |\\t)+", ".*.*");
			} // if	
			else
				codigo= "WXYZ";
  		params.put("codigo", codigo);
			if(buscaPorCodigo)
        this.attrs.put("clientes", UIEntity.build("VistaCuentasPorCobrarDto", "porCodigo", params, columns, 40L));
			else
        this.attrs.put("clientes", UIEntity.build("VistaCuentasPorCobrarDto", "porNombre", params, columns, 40L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
		return (List<UISelectEntity>)this.attrs.get("clientes");
	} // doCompleteCliente			
	
	public String doAccion() {
		String regresar= null; 
		try {
			JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Estimaciones/saldos");					
			JsfBase.setFlashAttribute("idContrato", ((Entity)this.attrs.get("seleccionadoDetalle")).toLong("idContrato"));			
      JsfBase.setFlashAttribute("idCliente", ((Entity) this.attrs.get("seleccionadoDetalle")).toLong("idCliente"));
      return "/Paginas/Keet/Catalogos/Contratos/garantias".concat(Constantes.REDIRECIONAR);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return regresar;
  } // doAccion 
  
  public void doLoadDetalle() {
    List<Columna> columns     = null;
	  Map<String, Object> params= null;	
    try {
  	  params = this.toPrepare();
      Entity entity= this.attrs.get("seleccionado")== null? (Entity)this.attrs.get("rowSeleccionado"): (Entity)this.attrs.get("seleccionado");
			params.put("sortOrder", "order by tc_keet_contratos.registro desc");
			params.put("idCliente", entity.toLong("idCliente"));
      columns= new ArrayList<>();
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));      
      columns.add(new Columna("anticipo", EFormatoDinamicos.MILES_CON_DECIMALES));      
      columns.add(new Columna("estimaciones", EFormatoDinamicos.MILES_CON_DECIMALES));    
      columns.add(new Columna("saldo", EFormatoDinamicos.MILES_CON_DECIMALES));    
      columns.add(new Columna("amortizacion", EFormatoDinamicos.MILES_CON_DECIMALES));    
      columns.add(new Columna("fondoGarantia", EFormatoDinamicos.MILES_CON_DECIMALES));    
      columns.add(new Columna("retenciones", EFormatoDinamicos.MILES_CON_DECIMALES));    
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));    
			this.lazyModelDetalle = new FormatCustomLazy("VistaEstimacionesDto", "contratos", params, columns);
      UIBackingUtilities.resetDataTable();		
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally		   
  }
 
	public void onRowToggle(ToggleEvent event) {
		try {
			this.attrs.put("rowSeleccionado", (Entity) event.getData());
			if (!event.getVisibility().equals(Visibility.HIDDEN)) 
				this.doLoadDetalle();
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // onRowToggle
 
	public void doLoadDesarrollos() {
		List<Columna> columns     = null;
    Map<String, Object> params= null;
//		UISelectEntity empresa    = null;
    try {
			params= new HashMap<>();			
//			empresa= (UISelectEntity) this.attrs.get("idEmpresa");
//			if(empresa.getKey()>= 1L)
//        params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa=" + empresa.getKey());
//			else
			params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa in (" + JsfBase.getAutentifica().getEmpresa().getSucursales() + ")");			
			params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
      columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("desarrollos", (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "nombres"));			
			this.attrs.put("idDesarrollo", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("desarrollos")));			
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} // doLoadDesarrollos
	
	public void doLoadContratos() {
		List<Columna> columns    = null;
		Map<String, Object>params= new HashMap<>();
		UISelectEntity empresa   = null;
    List<UISelectEntity> clientes = null;
    List<UISelectEntity> contratos= null;
	  try {
			columns= new ArrayList<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			empresa= (UISelectEntity)this.attrs.get("idEmpresa");
			if(empresa!= null && empresa.getKey()> 0L) 
			  params.put("sucursales", empresa.getKey());
			else
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && !this.attrs.get("idDesarrollo").toString().equals("-1")) {
        params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			  contratos= UIEntity.build("VistaContratosDto", "findDesarrollo", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS);
        clientes = UIEntity.build("VistaIngresosDto", "clientes", params, columns, Constantes.SQL_TODOS_REGISTROS);
      } // if  
      else {
        contratos= UIEntity.seleccione("VistaContratosDto", "byEmpresa", params, "clave");
        clientes = UIEntity.seleccione("TcManticClientesDto", "sucursales", params, columns, "razonSocial");
      } // else   
      this.attrs.put("clientes", clientes);
      if(clientes!= null && !clientes.isEmpty())
        this.attrs.put("idCliente", clientes.get(0));
      this.attrs.put("contratos", contratos);
      if(contratos!= null && !contratos.isEmpty())
        this.attrs.put("idContrato", contratos.get(0));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} // doLoadContratos  
 
  public void doLoadHistorial(Entity row) {
    List<Columna> columns     = null;
	  Map<String, Object> params= null;	
    try {
  	  params= new HashMap<>();
      Periodo periodo= new Periodo();
      periodo.addMeses(-12);
      params.put(Constantes.SQL_CONDICION, "date_format(tc_mantic_clientes_pagos.registro, '%Y%m%d')>= '".concat(periodo.toString()).concat("'"));
			params.put("idCliente", row.toLong("idCliente"));
      params.put("sortOrder", "order by tc_mantic_ventas.ticket desc, tc_mantic_clientes_pagos.registro desc");
      columns= new ArrayList<>();
      columns.add(new Columna("ticket", EFormatoDinamicos.MAYUSCULAS));      
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));    
      columns.add(new Columna("pago", EFormatoDinamicos.MILES_CON_DECIMALES));      
      columns.add(new Columna("persona", EFormatoDinamicos.MAYUSCULAS));    
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));    
      columns.add(new Columna("fechaPago", EFormatoDinamicos.FECHA_CORTA));    
			this.historialPagos = new FormatCustomLazy("VistaClientesDto", "historial", params, columns);
      UIBackingUtilities.resetDataTable("tablaHistorial");		
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally		   
  }
 
  public void doUpdateDocumentos() {
    Map<String, Object> params= null;
    try {      
      params = new HashMap<>();      
      Entity seleccionado= (Entity)this.attrs.get("seleccionadoDetalle");
 			params.put("idDesarrollo", seleccionado.toLong("idDesarrollo"));
			params.put("idCliente", seleccionado.toLong("idCliente"));
			List<UISelectEntity> contratos= UIEntity.seleccione("VistaContratosDto", "findCliente", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
      this.attrs.put("documentos", contratos);
      this.attrs.put("contrato", UIBackingUtilities.toFirstKeySelectEntity(contratos));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
 
  public void doMontoUpdate() {
	  if(this.attrs.get("montoInicio")!= null && this.attrs.get("montoTermino")== null)
			this.attrs.put("montoTermino", this.attrs.get("montoInicio"));
	  if(this.attrs.get("montoTermino")!= null && this.attrs.get("montoInicio")== null)
			this.attrs.put("montoInicio", this.attrs.get("montoTermino"));
	}  
 
  public String doExtras() {
    JsfBase.setFlashAttribute("idContrato", ((Entity) this.attrs.get("seleccionadoDetalle")).toLong("idContrato"));
    JsfBase.setFlashAttribute("idCliente", ((Entity) this.attrs.get("seleccionadoDetalle")).toLong("idCliente"));
    JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Contratos/filtro");
    return "/Paginas/Keet/Catalogos/Contratos/extras".concat(Constantes.REDIRECIONAR);
  }
  
}