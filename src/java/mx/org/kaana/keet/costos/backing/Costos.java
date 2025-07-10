package mx.org.kaana.keet.costos.backing;

import java.io.InputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletContext;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.procesos.reportes.beans.Modelo;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.archivo.Xls;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Numero;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named(value = "keetCostosCostos")
@ViewScoped
public class Costos extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428879L;
  private static final Log LOG = LogFactory.getLog(Costos.class);
  private static final String COLUMN_DATA_FILE_DESTAJOS  = "EMPRESA,DESARROLLO,CLAVE,CONTRATO,VIVIENDAS,COSTO,TIPO,TRABAJADOR,NOMINA,LOTE,CODIGO,CONCEPTO,PORCENTAJE,DESTAJO,REGISTRO";  
  private static final String COLUMN_DATA_FILE_MATERIALES= "EMPRESA,DESARROLLO,CONSECUTIVO,CLAVE,CONTRATO,CLIENTE,USUARIO,TIPO,ESTATUS,PROVEEDOR,TOTAL,REQUISICION,CODIGO,NOMBRE,CANTIDAD,COSTO,IVA,SUB TOTAL,IMPUESTOS,IMPORTE,FECHA";  
  private static final String COLUMN_DATA_FILE_ESTIMADOS = "EMPRESA,DESARROLLO,CLAVE,CONTRATO,VIVIENDAS,COSTO,CONSECUTIVO,NORMAL,EXTRAS,RETENCIONES,COBRADO,PORCENTAJE,REGISTRO";  
  private static final String COLUMN_DATA_FILE_COBRADO   = "EMPRESA,DESARROLLO,CLAVE,CONTRATO,VIVIENDAS,COSTO,CONSECUTIVO,NORMAL,EXTRAS,RETENCIONES,FOLIO,COBRADO,REGISTRO";  
  private static final String COLUMN_DATA_FILE_CAJA_CHICA= "EMPRESA,DESARROLLO,CONSECUTIVO,NOMBRE,CANTIDAD,COSTO,IMPORTE,REGISTRO";  

	private Reporte reporte;
  private List<Entity> model;
  private List<Entity> subTotales;
  private Entity totales;
  private List<Long> fraccionamientos;
  private StringBuilder seguimiento;
  private Map<String, Object> exportar;
  private List<Entity> cajaChicas;
	private LocalDate fechaInicio;
	private LocalDate fechaTermino;
  
  public List<Entity> getModel() {
    return model;
  }

  public List<Entity> getSubTotales() {
    return subTotales;
  }

  public Entity getTotales() {
    return totales;
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
  
  public StreamedContent getDestajos() {
		StreamedContent regresar = null;
		Xls xls                  = null;
		String template          = "DESTAJOS";
		try {
      this.exportar.put("contratistas", Constantes.SQL_VERDADERO);
      this.exportar.put("subContratistas", Constantes.SQL_VERDADERO);
      this.exportar.put("sortOrder", "order by tc_mantic_ordenes_compras.consecutivo desc");
      String salida  = EFormatos.XLS.toPath().concat(Archivo.toFormatNameFile(template).concat(".")).concat(EFormatos.XLS.name().toLowerCase());
      String fileName= JsfBase.getRealPath("").concat(salida);
      xls= new Xls(fileName, new Modelo(this.exportar, "VistaCostosDto", "especial", template), COLUMN_DATA_FILE_DESTAJOS);	
      if(xls.procesar()) {
        String contentType= EFormatos.XLS.getContent();
        InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(salida);  
        regresar          = new DefaultStreamedContent(stream, contentType, Archivo.toFormatNameFile(template).concat(".").concat(EFormatos.XLS.name().toLowerCase()));				
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  }
  
  public StreamedContent getMateriales() {
		StreamedContent regresar = null;
		Xls xls                  = null;
		String template          = "MATERIALES";
		try {
      this.exportar.put("idEmpresa", ((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
      this.exportar.put("sortOrder", "order by tc_mantic_ordenes_compras.consecutivo desc");
      String salida  = EFormatos.XLS.toPath().concat(Archivo.toFormatNameFile(template).concat(".")).concat(EFormatos.XLS.name().toLowerCase());
      String fileName= JsfBase.getRealPath("").concat(salida);
      xls= new Xls(fileName, new Modelo(this.exportar, "VistaOrdenesComprasDto", "especial", template), COLUMN_DATA_FILE_MATERIALES);	
      if(xls.procesar()) {
        String contentType= EFormatos.XLS.getContent();
        InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(salida);  
        regresar          = new DefaultStreamedContent(stream, contentType, Archivo.toFormatNameFile(template).concat(".").concat(EFormatos.XLS.name().toLowerCase()));				
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  }
  
  public StreamedContent getEstimados() {
		StreamedContent regresar = null;
		Xls xls                  = null;
		String template          = "ESTIMADOS";
		try {
      this.exportar.put("sortOrder", "order by tc_keet_desarrollos.id_desarrollo, tc_keet_contratos.clave, tc_keet_estimaciones.id_estimacion desc");
      String salida  = EFormatos.XLS.toPath().concat(Archivo.toFormatNameFile(template).concat(".")).concat(EFormatos.XLS.name().toLowerCase());
      String fileName= JsfBase.getRealPath("").concat(salida);
      xls= new Xls(fileName, new Modelo(this.exportar, "VistaCostosDto", "pagos", template), COLUMN_DATA_FILE_ESTIMADOS);	
      if(xls.procesar()) {
        String contentType= EFormatos.XLS.getContent();
        InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(salida);  
        regresar          = new DefaultStreamedContent(stream, contentType, Archivo.toFormatNameFile(template).concat(".").concat(EFormatos.XLS.name().toLowerCase()));				
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  }
  
  public StreamedContent getCobrado() {
		StreamedContent regresar = null;
		Xls xls                  = null;
		String template          = "COBRADO";
		try {
      this.exportar.put("sortOrder", "order by tc_keet_desarrollos.id_desarrollo, tc_keet_contratos.clave, tc_keet_estimaciones.id_estimacion, tc_mantic_clientes_pagos.id_cliente_pago desc");
      String salida  = EFormatos.XLS.toPath().concat(Archivo.toFormatNameFile(template).concat(".")).concat(EFormatos.XLS.name().toLowerCase());
      String fileName= JsfBase.getRealPath("").concat(salida);
      xls= new Xls(fileName, new Modelo(this.exportar, "VistaCostosDto", "cobrado", template), COLUMN_DATA_FILE_COBRADO);	
      if(xls.procesar()) {
        String contentType= EFormatos.XLS.getContent();
        InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(salida);  
        regresar          = new DefaultStreamedContent(stream, contentType, Archivo.toFormatNameFile(template).concat(".").concat(EFormatos.XLS.name().toLowerCase()));				
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  }
  
  public StreamedContent getCajaChica() {
		StreamedContent regresar = null;
		Xls xls                  = null;
		String template          = "CAJACHICA";
		try {
      this.exportar.put("sortOrder", "order by tc_mantic_empresas.id_empresa, tc_keet_desarrollos.nombres, tc_keet_gastos_detalles.importe desc");
      String salida  = EFormatos.XLS.toPath().concat(Archivo.toFormatNameFile(template).concat(".")).concat(EFormatos.XLS.name().toLowerCase());
      String fileName= JsfBase.getRealPath("").concat(salida);
      xls= new Xls(fileName, new Modelo(this.exportar, "VistaCostosDto", "desatado", template), COLUMN_DATA_FILE_CAJA_CHICA);	
      if(xls.procesar()) {
        String contentType= EFormatos.XLS.getContent();
        InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(salida);  
        regresar          = new DefaultStreamedContent(stream, contentType, Archivo.toFormatNameFile(template).concat(".").concat(EFormatos.XLS.name().toLowerCase()));				
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  }
  
  @PostConstruct
  @Override
  protected void init() {
    try {
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());			
      this.attrs.put("total", "$0.00");
      this.attrs.put("titulo", "");
      this.fraccionamientos= new ArrayList<>();
      this.seguimiento     = new StringBuilder();
      this.toLoadEstatus();
			this.toLoadEmpresas();
      this.toLoadCajaChica();
      this.exportar= new HashMap();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
    List<Columna> columns    = new ArrayList<>();
		Map<String, Object>params= this.toPrepare();
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("contrato", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("noViviendas", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
      columns.add(new Columna("estimaciones", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
      columns.add(new Columna("porcentaje", EFormatoDinamicos.NUMERO_CON_DECIMALES));
      columns.add(new Columna("desviacion", EFormatoDinamicos.NUMERO_CON_DECIMALES));
      columns.add(new Columna("iniciadas", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
      columns.add(new Columna("enProceso", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
      columns.add(new Columna("terminadas", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
      columns.add(new Columna("otros", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
      columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("estimado", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("facturado", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("pagado", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("retenciones", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("extras", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("egresos", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("calculo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      if(!Objects.equals(this.subTotales, null)) {
        Methods.clean(this.subTotales);
        Methods.clean(this.totales);
      } // if  
      this.subTotales= new ArrayList<>();
      this.totales   = null;
      this.model     = (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaCostosDto", "lazy", params, Constantes.SQL_TODOS_REGISTROS);
      if(!Objects.equals(this.model, null) && !this.model.isEmpty()) {
        UIBackingUtilities.toFormatEntitySet(this.model, columns);
        this.toDefineDesarrollos();
        this.toAcumularDesarrollos();
        UIBackingUtilities.toFormatEntitySet(this.subTotales, columns);
        UIBackingUtilities.toFormatEntity(this.totales, columns);
      } // if
      else {
        this.model  = new ArrayList<>();
        this.totales= new Entity();
      }  
      UIBackingUtilities.resetDataTable();
      this.lazyModel= null;
      this.attrs.put("detalle", Boolean.FALSE);
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

  private void toDefineDesarrollos() {
    try {      
      this.fraccionamientos.clear();
      for (Entity desarrollo: this.model) {
        if(!Objects.equals(desarrollo.toLong("idDesarrollo"), -1L) && fraccionamientos.indexOf(desarrollo.toLong("idDesarrollo"))< 0) 
          this.fraccionamientos.add(desarrollo.toLong("idDesarrollo"));
      } // for
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb= new StringBuilder();
    sb.append("tc_mantic_empresas.id_empresa=").append(this.attrs.get("idEmpresa")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("idCliente")) && !Objects.equals(((UISelectEntity)this.attrs.get("idCliente")).getKey(), -1L))
		  sb.append("tc_keet_proyectos.id_cliente= ").append(this.attrs.get("idCliente")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && !Objects.equals(((UISelectEntity)this.attrs.get("idDesarrollo")).getKey(), -1L))
		  sb.append("tc_keet_proyectos.id_desarrollo=").append(this.attrs.get("idDesarrollo")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("idContrato")) && !Objects.equals(((UISelectEntity)this.attrs.get("idContrato")).getKey(), -1L))
		  sb.append("tc_keet_contratos.id_contrato= ").append(this.attrs.get("idContrato")).append(" and ");
		if(!Cadena.isVacio(this.attrs.get("idEstatus")) && !Objects.equals(((UISelectEntity)this.attrs.get("idEstatus")).getKey(), -1L) && ((UISelectEntity)this.attrs.get("idEstatus")).getKey()< 97L)
		  sb.append("tc_keet_contratos.id_contrato_estatus= ").append(this.attrs.get("idEstatus")).append(" and ");
		if(!Cadena.isVacio(this.fechaInicio)) 
		  sb.append("(date_format(tc_keet_contratos.registro, '%Y%m%d')>= '").append(this.fechaInicio.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.fechaTermino)) 
		  sb.append("(date_format(tc_keet_contratos.registro, '%Y%m%d')<= '").append(this.fechaTermino.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	}
  
	private void toLoadEmpresas() {
		Map<String, Object>params= new HashMap<>();
		List<Columna> columns    = new ArrayList<>();
    List<UISelectEntity> empresas= null;
		try {
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      empresas= (List<UISelectEntity>)UIEntity.build("TcManticEmpresasDto", "empresas", params, columns);
      this.attrs.put("empresas", empresas);
			this.attrs.put("idEmpresa", UIBackingUtilities.toFirstKeySelectEntity(empresas));
      this.doLoadClientes();
      this.doLoadSemanas();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally {
			Methods.clean(params);
		}	// finally	
	} 
  
	private void toLoadCajaChica() {
		Map<String, Object>params= new HashMap<>();
		List<Columna> columns    = new ArrayList<>();
		try {
			params.put(Constantes.SQL_CONDICION, "(tc_mantic_empresas.id_empresa= "+ this.attrs.get("idEmpresa")+ ")");			
      columns.add(new Columna("desarrollo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
      this.cajaChicas= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaCostosDto", "cajaChica", params);
      if(!Objects.equals(this.cajaChicas, null) && !this.cajaChicas.isEmpty()) {
        Double importe= 0D;
        for (Entity item: this.cajaChicas) {
          importe+= item.toDouble("importe");
        } // for
        Entity total= this.cajaChicas.get(0).clone();
        total.setKey(-1L);
        total.get("desarrollo").setData("TOTAL");
        total.get("idDesarrollo").setData(-1L);
        total.get("importe").setData(importe);
        this.cajaChicas.add(total);
        UIBackingUtilities.toFormatEntitySet(this.cajaChicas, columns);
      } // if
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally {
			Methods.clean(params);
		}	// finally	
	} 
 
  public String doValueCajaChica(Long idDesarrollo) {
    String regresar= "0.00";
    try {      
      int index= this.cajaChicas.indexOf(new Entity(idDesarrollo));
      if(index>= 0) 
        regresar= this.cajaChicas.get(index).toString("importe");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    return regresar;
  }
  
	public void doLoadClientes() {
		UISelectEntity empresa   = null;
		Map<String, Object>params= new HashMap<>();
		List<Columna> columns    = new ArrayList<>();
    List<UISelectEntity> clientes= null;
	  try {
			empresa= (UISelectEntity)this.attrs.get("idEmpresa");
			params.put("sucursales", empresa.getKey());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("contratos", EFormatoDinamicos.MILES_SIN_DECIMALES));
      clientes= UIEntity.seleccione("VistaCostosDto", "clientes", params, columns, "clave");
      this.attrs.put("clientes", clientes);
			this.attrs.put("idCliente", UIBackingUtilities.toFirstKeySelectEntity(clientes));
      if(!Objects.equals(clientes, null) && !clientes.isEmpty()) 
			  this.attrs.put("idCliente", UIBackingUtilities.toFirstKeySelectEntity(clientes));
      else
        this.attrs.put("idCliente", new UISelectEntity(-1L));
      this.doLoadDesarrollos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
			Methods.clean(columns);
			Methods.clean(params);
		}	// finally	
	} 
  
	public void doLoadDesarrollos() {
    Map<String, Object> params= new HashMap<>();
		List<Columna> columns     = new ArrayList<>();
		UISelectEntity cliente    = null;
    List<UISelectEntity> desarrollos= null;
    try {
			cliente= (UISelectEntity) this.attrs.get("idCliente");
			params.put("operador", "<=");
      params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
      params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_cliente= "+ cliente.getKey());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      desarrollos= (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave");
      this.attrs.put("desarrollos", desarrollos);			
			this.attrs.put("idDesarrollo", UIBackingUtilities.toFirstKeySelectEntity(desarrollos));			
      this.doLoadContratos();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} 
       
  public void doLoadContratos() {
    Map<String, Object> params   = new HashMap<>();
    List<Columna> columns        = new ArrayList<>();
		UISelectEntity empresa       = (UISelectEntity)this.attrs.get("idEmpresa");
		UISelectEntity desarrollo    = (UISelectEntity)this.attrs.get("idDesarrollo");
		List<UISelectEntity>desarrollos= (List<UISelectEntity>)this.attrs.get("desarrollos");
		List<UISelectEntity>contratos  = null;
    try {
			columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("anticipo", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("fondoGarantia", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
			columns.add(new Columna("vence", EFormatoDinamicos.FECHA_CORTA));
      if(Objects.equals(desarrollo.getKey(), -1L)) {
        StringBuilder sb= new StringBuilder();
        for (UISelectEntity item: desarrollos) {
          if(!Objects.equals(item.getKey(), -1L)) 
            sb.append(item.getKey()).append(", "); 
        } // for
        if(sb.length()> 2) 
          sb.delete(sb.length()- 2, sb.length());
        else
          sb.append("-1");
        params.put("idDesarrollo", sb.toString());
      } // if  
      else 
        params.put("idDesarrollo", desarrollo.getKey());
      params.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato_estatus<= "+ EContratosEstatus.TERMINADO.getKey()+ " and tc_keet_contratos.id_empresa= "+ empresa.getKey());
      contratos= (List<UISelectEntity>) UIEntity.seleccione("VistaCostosDto", "desarrollos", params, columns, "clave");
      this.attrs.put("contratos", contratos);
      this.attrs.put("idContrato", UIBackingUtilities.toFirstKeySelectEntity(contratos));
      if(!Objects.equals(contratos, null) && !contratos.isEmpty() && contratos.size()> 1) {
        contratos.get(0).getValue("noViviendas").setData("");
        contratos.get(0).getValue("costo").setData(0D);
        contratos.get(0).getValue("valor").setData(0D);
      } // if
      this.doCalcular();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
  }
 
	public void doLoadSemanas() {
		Map<String, Object>params= new HashMap<>();
    List<Columna> columns    = new ArrayList<>();
		List<UISelectEntity>semanas= null;
    try {
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      params.put("ejercicio", Fecha.getAnioActual());
      semanas= (List<UISelectEntity>)UIEntity.build("VistaNominaDto", "semanas", params, columns);
      this.attrs.put("semanas", semanas);
      this.attrs.put("idSemana", UIBackingUtilities.toFirstKeySelectEntity(semanas));
      if(JsfBase.isPostBack())
        this.doCalcular();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);	
		} // catch				
    finally {
			Methods.clean(params);
			Methods.clean(columns);
		}	// finally
	} 
 
	private void toLoadEstatus() {
		Map<String, Object>params   = new HashMap<>();
		List<UISelectEntity> estatus= null;		
		try {
			params.put(Constantes.SQL_CONDICION, "id_contrato_estatus in (5, 6, 7, 8, 9, 10)");
			estatus= (List<UISelectEntity>)UIEntity.build("TcKeetContratosEstatusDto", params);			
			this.attrs.put("estatus", estatus);
      if(!Objects.equals(estatus, null) && !estatus.isEmpty()) {
        UISelectEntity item= new UISelectEntity(99L);
        item.addField("nombre", "TODOS");
        estatus.add(item);
			  this.attrs.put("idEstatus", UIBackingUtilities.toFirstKeySelectEntity(estatus));		
      } // if
      else 
        this.attrs.put("idEstatus", new UISelectEntity(-1L));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} 
  
  public void doCalcular() {
 		UISelectEntity idEstatus      = (UISelectEntity)this.attrs.get("idEstatus");
 		UISelectEntity idContrato     = (UISelectEntity)this.attrs.get("idContrato");
    List<UISelectEntity> contratos= (List<UISelectEntity>)this.attrs.get("contratos");
    Map<String, Object> params    = new HashMap<>();
    try {      
      seguimiento.delete(0, seguimiento.length());
      if(!Objects.equals(contratos, null) && !contratos.isEmpty() && contratos.size()> 1) {
        double sum= 0D;
        for (UISelectEntity item: contratos) {
          if(item.containsKey("valor"))
            if(
               (idEstatus.getKey()< 90L && Objects.equals(item.toLong("idContratoEstatus"), idEstatus.getKey())) || 
               Objects.equals(99L, idEstatus.getKey()) || 
               (Objects.equals(98L, idEstatus.getKey()) && Objects.equals(item.toLong("idContrato"), idContrato.getKey()))
              ) {
              sum+= item.toDouble("valor");
              seguimiento.append(item.toLong("idContrato")).append(", ");
            } // if  
        } // for
        if(seguimiento.length()> 1)
          seguimiento.delete(seguimiento.length()- 2, seguimiento.length());
        else
          seguimiento.append("-1");
        this.attrs.put("total", Numero.formatear(Numero.MONEDA_CON_DECIMALES, sum));
      } // if
      else {
        this.attrs.put("total", "$ 0.00");
        seguimiento.append("-1");
      } // else  
      this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
 
  public int toIndex(Long idDesarrollo) {
    return this.fraccionamientos.indexOf(idDesarrollo);
  }
 
  private void toAcumularDesarrollos() {
    Long idDesarrollo= -1L;
    Entity row       = null;
    try {  
      for (Entity item: this.model) {
        // ESTO LIMPIA LOS CONTRATOS GENERALES
        this.remove(item);
        if(Objects.equals(this.totales, null)) {
          this.totales= item.clone();
          this.clean(this.totales);
        } // if
        else
          this.acumular(this.totales, item);
        if(Objects.equals(idDesarrollo, -1L) || !Objects.equals(idDesarrollo, item.toLong("idDesarrollo"))) {
          if(!Objects.equals(idDesarrollo, -1L)) 
            this.subTotales.add(row);
          row= item.clone();
          this.clean(row);
          idDesarrollo= item.toLong("idDesarrollo");
        } // if  
        else
          this.acumular(row, item);
      } // for
      this.subTotales.add(row);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  private void clean(Entity row) {
		for (String key: row.keySet()) {
      if(!"|idKey|empresa|idDesarrollo|desarrollo|idContrato|clave|contrato|registro|".contains(key)) {
  			Value value= row.get(key);
        value.setData(Numero.getDouble(Cadena.eliminar(value.toString(), ','), 0D));
      } // if
		} // for
  }
  
  private void remove(Entity row) {
    LOG.info("CONTRATO: "+ row.toString("contrato"));
    if(row.toString("contrato").contains("GENERAL") || row.toString("contrato").contains("ADMINISTRATIVO"))
      for (String key: row.keySet()) {
        if("|noViviendas|iniciadas|enProceso|terminadas|otros|dias|".contains(key)) {
          Value value= row.get(key);
          value.setData(0L);
        } // if
      } // for
  }
  
  private void acumular(Entity item, Entity value) {
    for (String key: value.keySet()) {
      if(!"|idKey|empresa|idDesarrollo|desarrollo|idContrato|clave|contrato|registro|".contains(key)) {
        Double numero= Numero.getDouble(Cadena.eliminar(value.get(key).toString(), ','), 0D);
        item.get(key).setData(item.get(key).toDouble()+ numero);
      } // if
    } // for
  }
 
  public void doLoadDetalle(Entity row) {
    List<Columna> columns    = new ArrayList<>();
		Map<String, Object>params= new HashMap<>();
    try {
      params.put(Constantes.SQL_CONDICION, row.toLong("idContrato"));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("noViviendas", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
      columns.add(new Columna("egresos", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("materiales", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("manoDeObra", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("destajos", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("porElDia", EFormatoDinamicos.MILES_CON_DECIMALES));
      this.lazyModel= new FormatCustomLazy("VistaCostosDto", "egresos", params, columns);
      UIBackingUtilities.resetDataTable("detalle");
      this.attrs.put("detalle", Boolean.TRUE);
      this.attrs.put("titulo", row.toString("contrato"));
      this.exportar.clear();
      params.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato= "+ row.toLong("idContrato"));
      this.exportar.putAll(params);
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
  
  public void doLoadDetalle(Long idDesarrollo, String titulo) {
    List<Columna> columns    = new ArrayList<>();
		Map<String, Object>params= new HashMap<>();
		StringBuilder sb         = new StringBuilder();
    try {
      params.put("idDesarrollo", idDesarrollo);
      sb.append("(tc_keet_desarrollos.id_desarrollo=").append(idDesarrollo).append(") and ");
      sb.append("(tc_keet_contratos.id_empresa=").append(this.attrs.get("idEmpresa")).append(") and ");
  		if(!Cadena.isVacio(this.attrs.get("idEstatus")) && !Objects.equals(((UISelectEntity)this.attrs.get("idEstatus")).getKey(), -1L) && ((UISelectEntity)this.attrs.get("idEstatus")).getKey()< 97L)
        sb.append("tc_keet_contratos.id_contrato_estatus= ").append(((UISelectEntity)this.attrs.get("idEstatus")).getKey()).append(" and ");
      if(!Cadena.isVacio(this.attrs.get("idContrato")) && !Objects.equals(((UISelectEntity)this.attrs.get("idContrato")).getKey(), -1L))
        sb.append("tc_keet_contratos.id_contrato= ").append(this.attrs.get("idContrato")).append(" and ");
      if(sb.length()== 0)
        params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      else	
        params.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("noViviendas", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
      columns.add(new Columna("egresos", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("materiales", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("manoDeObra", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("destajos", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("porElDia", EFormatoDinamicos.MILES_CON_DECIMALES));
      this.lazyModel= new FormatCustomLazy("VistaCostosDto", "salida", params, columns);
      UIBackingUtilities.resetDataTable("detalle");
      this.attrs.put("detalle", Boolean.TRUE);
      this.attrs.put("titulo", titulo);
      this.exportar.clear();
      this.exportar.putAll(params);
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
  
  public void doLoadGeneral() {
    List<Columna> columns    = new ArrayList<>();
		Map<String, Object>params= this.toPrepare();
    try {
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("noViviendas", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
      columns.add(new Columna("egresos", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("materiales", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("manoDeObra", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("destajos", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("porElDia", EFormatoDinamicos.MILES_CON_DECIMALES));
      this.lazyModel= new FormatCustomLazy("VistaCostosDto", "general", params, columns);
      UIBackingUtilities.resetDataTable("detalle");
      this.attrs.put("detalle", Boolean.TRUE);
      this.attrs.put("titulo", this.toLookOption(((UISelectEntity)this.attrs.get("idEmpresa")).getKey(), "nombre", (List<UISelectEntity>)this.attrs.get("empresas")));
      this.exportar.clear();
      this.exportar.putAll(params);
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
  
	public String doColor(Entity row) {
    String regresar           = "";
    Map<String, Object> params= new HashMap<>();
    try {      
      params.put(Constantes.SQL_CONDICION, row.toLong("idDesarrollo"));      
      params.put("idNomina", ((UISelectEntity)this.attrs.get("idSemana")).toLong("idNomina"));
      if(Objects.equals(row.toString("estimado"), "0.00") && !Objects.equals(row.toString("egresos"), "0.00") && !row.toString("contrato").contains("EXTRA"))
        regresar= "janal-tr-nuevo";
      else {
        Entity entity= (Entity)DaoFactory.getInstance().toEntity("VistaCostosDto", "estimaciones", params);
        if(!Objects.equals(entity, null) && !entity.isEmpty()) 
          regresar= (Objects.equals(entity.toDouble("estimado"), 0D) && (
                  !Objects.equals(entity.toDouble("destajos"), 0D) || 
                  !Objects.equals(entity.toDouble("materiales"), 0D) || 
                  !Objects.equals(entity.toDouble("porElDia"), 0D)
                  ))? "janal-tr-nuevo": "";
      } // if  
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
 
	public void doReporte() throws Exception {
		Parametros comunes           = null;
		Map<String, Object>params    = this.toPrepare();
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = EReportes.ANALISIS_COSTOS;
		try {		
      comunes     = new Parametros(((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
      parametros  = comunes.getComunes();
      this.reporte= JsfBase.toReporte();	
      parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getTitulo().toUpperCase());
      parametros.put("NOMBRE_REPORTE", reporteSeleccion.getTitulo());
      parametros.put("REPORTE_TITULO", reporteSeleccion.getTitulo());
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("/resources/janal/img/sistema/"));
      this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));					
      this.doVerificarReporte();
      this.attrs.put("reporteName", this.reporte.getArchivo());
      this.reporte.doAceptar();			
    } // try
    catch(Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);			
    } // catch	
  } 
  
	public void doVerificarReporte() {
		if(this.reporte.getTotal()> 0L)
			UIBackingUtilities.execute("start(" + this.reporte.getTotal() + ")");		
    else {
			UIBackingUtilities.execute("generalHide()");		
			JsfBase.addMessage("Generar", "No se encontraron registros para el reporte", ETipoMensaje.ALERTA);
		} // else
	} 
 
  private String toLookOption(Long idKey, String key, List<UISelectEntity> items) {
    String regresar= "";
    try {      
      if(!Objects.equals(items, null) && !items.isEmpty()) {
        int index= items.indexOf(new UISelectEntity(idKey));
        if(index>= 0 && items.get(index).containsKey(key))
          regresar= items.get(index).toString(key);
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    return regresar;
  }
 
  public void doTotal(Entity row) {
    try {      
      this.exportar.clear();
      this.exportar.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato= "+ row.toLong("idContrato"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }  
  
  public void doTotal(Long idDesarrollo) {
    StringBuilder sb= new StringBuilder();
    try {      
      this.exportar.clear();
      this.exportar.putAll(this.toPrepare());
      sb.append("(tc_keet_desarrollos.id_desarrollo=").append(idDesarrollo).append(") and ");
      sb.append("(tc_keet_contratos.id_empresa=").append(this.attrs.get("idEmpresa")).append(") and ");
  		if(!Cadena.isVacio(this.attrs.get("idEstatus")) && !Objects.equals(((UISelectEntity)this.attrs.get("idEstatus")).getKey(), -1L) && ((UISelectEntity)this.attrs.get("idEstatus")).getKey()< 97L)
        sb.append("tc_keet_contratos.id_contrato_estatus= ").append(((UISelectEntity)this.attrs.get("idEstatus")).getKey()).append(" and ");
      if(!Cadena.isVacio(this.attrs.get("idContrato")) && !Objects.equals(((UISelectEntity)this.attrs.get("idContrato")).getKey(), -1L))
        sb.append("tc_keet_contratos.id_contrato= ").append(this.attrs.get("idContrato")).append(" and ");
      if(!Cadena.isVacio(this.fechaInicio)) 
        sb.append("(date_format(tc_keet_contratos.registro, '%Y%m%d')>= '").append(this.fechaInicio.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
      if(!Cadena.isVacio(this.fechaTermino)) 
        sb.append("(date_format(tc_keet_contratos.registro, '%Y%m%d')<= '").append(this.fechaTermino.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
      if(sb.length()== 0)
        this.exportar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      else	
        this.exportar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doTotal() {
    try {      
      this.exportar.clear();
      this.exportar.putAll(this.toPrepare());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
 
  public void doCajaChica(Long idDesarrollo) {
    try {      
      this.exportar.clear();
      if(Objects.equals(idDesarrollo, -1L))
        this.exportar.put(Constantes.SQL_CONDICION, "(tc_mantic_empresas.id_empresa= "+ this.attrs.get("idEmpresa")+ ")");
      else
        this.exportar.put(Constantes.SQL_CONDICION, "(tc_mantic_empresas.id_empresa= "+ this.attrs.get("idEmpresa")+ ") and (tc_keet_desarrollos.id_desarrollo= "+ idDesarrollo+ ")");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
}