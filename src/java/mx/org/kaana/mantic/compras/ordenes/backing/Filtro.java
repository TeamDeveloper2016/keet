package mx.org.kaana.mantic.compras.ordenes.backing;

import java.io.InputStream;
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
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.ServletContext;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.procesos.reportes.beans.Modelo;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.archivo.Xls;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.wassenger.Cafu;
import mx.org.kaana.mantic.catalogos.proveedores.beans.ProveedorTipoContacto;
import mx.org.kaana.mantic.catalogos.proveedores.reglas.MotorBusqueda;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.compras.ordenes.reglas.Transaccion;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.correos.beans.Attachment;
import mx.org.kaana.mantic.correos.enums.ECorreos;
import mx.org.kaana.mantic.correos.reglas.IBaseAttachment;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticOrdenesComprasDto;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.enums.ETipoMovimiento;
import mx.org.kaana.mantic.enums.ETiposContactos;
import mx.org.kaana.mantic.facturas.beans.Correo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named(value = "manticComprasOrdenesFiltro")
@ViewScoped 
public class Filtro extends IBaseFilter implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Filtro.class);
  private static final long serialVersionUID= 8793667741599428332L;
  private static final String COLUMN_DATA_FILE_ESPECIAL= "EMPRESA,DESARROLLO,CONSECUTIVO,CLAVE,CONTRATO,CLIENTE,USUARIO,TIPO,ESTATUS,PROVEEDOR,TOTAL,REQUISICION,CODIGO,NOMBRE,CANTIDAD,COSTO,IVA,SUB TOTAL,IMPUESTOS,IMPORTE,FECHA";  
  private static final String COLUMN_DATA_FILE_COMPRAS = "EJERCICIO,EMPRESA,CONTRATO,PRESUPUESTO,MATERIALES,PROVEEDOR,ESTATUS,ORDENES,TOTAL,CODIGO";  
  private static final String COLUMN_DATA_FILE_DETAIL  = "DESARROLLO,CLAVE,CONTRATO,PROVEEDOR,ARTICULO,CANTIDAD,IMPORTE";  
  
	private Reporte reporte;
	private List<Correo> correos;
	private List<Correo> selectedCorreos;	
	private Correo correo;
	private LocalDate fechaInicio;
	private LocalDate fechaTermino;
	private List<Correo> celulares;
	private List<Correo> selectedCelulares;	
	private Correo celular;
  
  private FormatLazyModel lazyDetalle;

	public FormatLazyModel getLazyDetalle() {
		return lazyDetalle;
	}		
  
	public List<Correo> getCorreos() {
		return correos;
	}

	public List<Correo> getSelectedCorreos() {
		return selectedCorreos;
	}

	public void setSelectedCorreos(List<Correo> selectedCorreos) {
		this.selectedCorreos = selectedCorreos;
	}	

	public Correo getCorreo() {
		return correo;
	}

	public void setCorreo(Correo correo) {
		this.correo = correo;
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
	
  public List<Correo> getCelulares() {
    return celulares;
  }

  public void setCelulares(List<Correo> celulares) {
    this.celulares = celulares;
  }

  public List<Correo> getSelectedCelulares() {
    return selectedCelulares;
  }

  public void setSelectedCelulares(List<Correo> selectedCelulares) {
    this.selectedCelulares = selectedCelulares;
  }

  public Correo getCelular() {
    return celular;
  }

  public void setCelular(Correo celular) {
    this.celular = celular;
  }
	
  public StreamedContent getCompras() {
		StreamedContent regresar= null;
		Xls xls                 = null;
		Map<String, Object>params= new HashMap<>();
		String template         = "COMPRAS";
		try {
      String salida  = EFormatos.XLS.toPath().concat(Archivo.toFormatNameFile(template).concat(".")).concat(EFormatos.XLS.name().toLowerCase());
      String fileName= JsfBase.getRealPath("").concat(salida);
      xls= new Xls(fileName, new Modelo(params, "VistaOrdenesComprasDto", "comprasProveedores", template), COLUMN_DATA_FILE_COMPRAS);	
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
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
  
  public StreamedContent getEspecial() {
		StreamedContent regresar = null;
		Xls xls                  = null;
		Map<String, Object>params= this.toPrepare();
		String template          = "ESPECIAL";
		try {
      params.put("sortOrder", "order by tc_mantic_ordenes_compras.consecutivo desc");
      String salida  = EFormatos.XLS.toPath().concat(Archivo.toFormatNameFile(template).concat(".")).concat(EFormatos.XLS.name().toLowerCase());
      String fileName= JsfBase.getRealPath("").concat(salida);
      xls= new Xls(fileName, new Modelo(params, "VistaOrdenesComprasDto", "especial", template), COLUMN_DATA_FILE_ESPECIAL);	
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
    finally {
      Methods.clean(params);
    } // finally
    return regresar;
  }
  
  public StreamedContent getAcumulado() {
		StreamedContent regresar= null;
		Xls xls                 = null;
		Map<String, Object>params= new HashMap<>();
		String template         = "ACUMULADO";
		try {
		  if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && ((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()>= 1L) {
        params.put("idDesarrollo", ((UISelectEntity)this.attrs.get("idDesarrollo")).getKey());
        params.put("idContrato", ((UISelectEntity)this.attrs.get("idContrato")).getKey());
        String salida  = EFormatos.XLS.toPath().concat(Archivo.toFormatNameFile(template).concat(".")).concat(EFormatos.XLS.name().toLowerCase());
        String fileName= JsfBase.getRealPath("").concat(salida);
        xls= new Xls(fileName, new Modelo(params, "VistaOrdenesComprasDto", "acumulados", template), COLUMN_DATA_FILE_DETAIL);	
        if(xls.procesar()) {
          String contentType= EFormatos.XLS.getContent();
          InputStream stream= ((ServletContext)FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream(salida);  
          regresar          = new DefaultStreamedContent(stream, contentType, Archivo.toFormatNameFile(template).concat(".").concat(EFormatos.XLS.name().toLowerCase()));				
        } // if
      } // if
      else
        JsfBase.addMessage("Se debe de seleccionar un desarrollo!");
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
  
  @PostConstruct
  @Override
  protected void init() {
    Map<String, Object> params = new HashMap<>();
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.attrs.put("idOrdenCompra", JsfBase.getFlashAttribute("idOrdenCompra"));
			this.toLoadCatalog();
      if(this.attrs.get("idOrdenCompra")!= null) {
        params.put("idOrdenCompra", this.attrs.get("idOrdenCompra"));
        Entity item= (Entity)DaoFactory.getInstance().toEntity("VistaOrdenesComprasDto", "control", params);
        if(item!= null && !item.isEmpty()) 
          UIBackingUtilities.execute("janal.show([{summary: 'Error:', detail: 'La orden de compra se guardo de forma incorrecta !'}], 'error');"); 
			  this.doLoad();
      } // if  
      this.attrs.put("activa", Boolean.FALSE);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // init
 
  @Override
  public void doLoad() {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= this.toPrepare();
    try {
      params.put("sortOrder", "order by tc_mantic_ordenes_compras.consecutivo desc");
      columns.add(new Columna("proveedor", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("almacen", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("total", EFormatoDinamicos.MONEDA_SAT_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));      
      this.lazyModel = new FormatCustomLazy("VistaOrdenesComprasDto", params, columns);
      UIBackingUtilities.resetDataTable();
			this.attrs.put("idOrdenCompra", null);
      this.lazyDetalle= null;
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

  public String doAccion(String accion) {
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Compras/Ordenes/filtro");		
			JsfBase.setFlashAttribute("idOrdenCompra", eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR)? ((Entity)this.attrs.get("seleccionado")).getKey() : -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return "/Paginas/Mantic/Compras/Ordenes/accion".concat(Constantes.REDIRECIONAR);
  } // doAccion  
	
  public String doNotasEntradas(String accion) {
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Inventarios/Entradas/filtro");		
			JsfBase.setFlashAttribute("idOrdenCompra", ((Entity)this.attrs.get("seleccionado")).getKey());
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return "/Paginas/Mantic/Inventarios/Entradas/accion?zOyOxDwIvGuCt=zNyLxMwAvCuEtAs".concat(Constantes.REDIRECIONAR_AMPERSON);
  } // doNotasEntradas  
	
  public void doEliminar() {
		Transaccion transaccion = null;
		Entity seleccionado     = null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
			transaccion= new Transaccion((TcManticOrdenesComprasDto)DaoFactory.getInstance().findById(TcManticOrdenesComprasDto.class, seleccionado.getKey()));
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar", "La orden de compra se ha eliminado correctamente", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("Eliminar", "Ocurri� un error al eliminar la orden de compra", ETipoMensaje.ALERTA);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
  } // doEliminar

	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb= new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idOrdenCompra")) && !this.attrs.get("idOrdenCompra").toString().equals("-1"))
  		sb.append("(tc_mantic_ordenes_compras.id_orden_compra=").append(this.attrs.get("idOrdenCompra")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("consecutivo")))
  		sb.append("(tc_mantic_ordenes_compras.consecutivo like '%").append(this.attrs.get("consecutivo")).append("%') and ");
		if(!Cadena.isVacio(this.fechaInicio))
		  sb.append("(date_format(tc_mantic_ordenes_compras.registro, '%Y%m%d')>= '").append(this.fechaInicio.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.fechaTermino))
		  sb.append("(date_format(tc_mantic_ordenes_compras.registro, '%Y%m%d')<= '").append(this.fechaTermino.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("idCliente")) && !this.attrs.get("idCliente").toString().equals("-1"))
  		sb.append("(tc_mantic_clientes.id_cliente= ").append(this.attrs.get("idCliente")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idProveedor")) && !this.attrs.get("idProveedor").toString().equals("-1"))
  		sb.append("(tc_mantic_proveedores.id_proveedor= ").append(this.attrs.get("idProveedor")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idOrdenEstatus")) && !this.attrs.get("idOrdenEstatus").toString().equals("-1"))
  		sb.append("(tc_mantic_ordenes_compras.id_orden_estatus= ").append(this.attrs.get("idOrdenEstatus")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && ((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()>= 1L)
  		sb.append("(tc_mantic_ordenes_compras.id_desarrollo=").append(((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idContrato")) && ((UISelectEntity)this.attrs.get("idContrato")).getKey()>= 1L)
  		sb.append("(tc_mantic_ordenes_compras.id_contrato=").append(((UISelectEntity)this.attrs.get("idContrato")).getKey()).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
		  regresar.put("idEmpresa", this.attrs.get("idEmpresa"));
		else
		  regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	}
	
	protected void toLoadCatalog() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));			
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));
      this.attrs.put("clientes", (List<UISelectEntity>) UIEntity.seleccione("VistaOrdenesComprasDto", "clientes", params, columns, "clave"));
			this.attrs.put("idCliente", new UISelectEntity("-1"));
      this.attrs.put("proveedores", (List<UISelectEntity>) UIEntity.seleccione("VistaOrdenesComprasDto", "proveedores", params, columns, "clave", Constantes.SQL_TODOS_REGISTROS));
			this.attrs.put("idProveedor", new UISelectEntity("-1"));
			columns.remove(0);
      this.attrs.put("catalogo", (List<UISelectEntity>) UIEntity.seleccione("TcManticOrdenesEstatusDto", "row", params, columns, "nombre"));
			this.attrs.put("idOrdenEstatus", new UISelectEntity("-1"));
			this.doLoadDesarrollos();
    } // try
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}
	
	public void doLoadDesarrollos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
		  params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa in (" + JsfBase.getAutentifica().getEmpresa().getSucursales() + ")");			
			params.put("operador", "<=");
			params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("desarrollos", (List<UISelectEntity>) UIEntity.seleccione("VistaDesarrollosDto", "lazy", params, columns, "clave"));			
			this.attrs.put("idDesarrollo", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("desarrollos")));			
      this.doLoadContratos();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // doLoadDesarrollos
	
	public void doReporte(String nombre) throws Exception {
		this.doReporte(nombre, false);
	} // doReporte
	
	private void doReporte(String nombre, boolean email) throws Exception {
		Parametros comunes           = null;
		Map<String, Object>params    = this.toPrepare();
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;
    Entity seleccionado          = null;
		try{		
      seleccionado = ((Entity)this.attrs.get("seleccionado"));
      params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());	
      params.put("sortOrder", "order by tc_mantic_ordenes_compras.id_empresa, tc_mantic_ordenes_compras.ejercicio, tc_mantic_ordenes_compras.orden");
      reporteSeleccion= EReportes.valueOf(nombre);
      if(!reporteSeleccion.equals(EReportes.ORDENES_COMPRA)) {
        params.put("idOrdenCompra", ((Entity)this.attrs.get("seleccionado")).getKey());
        comunes= new Parametros(seleccionado.toLong("idEmpresa"), seleccionado.toLong("idAlmacen"), seleccionado.toLong("idProveedor"), -1L);
      }
      else
        comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.reporte= JsfBase.toReporte();	
      parametros= comunes.getComunes();
      parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getTitulo().toUpperCase());
      parametros.put("NOMBRE_REPORTE", reporteSeleccion.getTitulo());
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("/resources/janal/img/sistema/"));
      if(reporteSeleccion.equals(EReportes.ORDEN_DETALLE)) 
        parametros.put("REPORTE_FIRMA", JsfBase.getRealPath("/Paginas/Mantic/Catalogos/Empleados/Firmas/"));
      this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));					
			if(email) 
        this.reporte.doAceptarSimple();			
			else {				
				this.doVerificarReporte();
				this.attrs.put("reporteName", this.reporte.getArchivo());
				this.reporte.doAceptar();			
			} // else		      
    } // try
    catch(Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);			
    } // catch	
  } // doReporte
	
	public void doVerificarReporte() {
		if(this.reporte.getTotal()> 0L)
			UIBackingUtilities.execute("start(" + this.reporte.getTotal() + ")");		
    else {
			UIBackingUtilities.execute("generalHide()");		
			JsfBase.addMessage("Generar reporte", "No se encontraron registros para el reporte", ETipoMensaje.ALERTA);
		} // else
	} // doVerificarReporte		
	
	public void doLoadEstatus() {
		Entity seleccionado          = null;
		Map<String, Object>params    = new HashMap<>();
		List<UISelectItem> allEstatus= null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			params.put(Constantes.SQL_CONDICION, "id_orden_estatus in (".concat(seleccionado.toString("estatusAsociados")).concat(")"));
			allEstatus= UISelect.build("TcManticOrdenesEstatusDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);			
			this.attrs.put("allEstatus", allEstatus);
			this.attrs.put("estatus", allEstatus.get(0));
			this.attrs.put("activa", !Objects.equals(seleccionado.toLong("idTipoOrden"), 1L) && !Objects.equals(seleccionado.toLong("idTipoOrden"), 5L) && Objects.equals(seleccionado.toLong("idOrdenEstatus"), 1L));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // doLoadEstatus
	
	public void doActualizarEstatus() {
		Transaccion transaccion            = null;
		TcManticOrdenesBitacoraDto bitacora= null;
		Entity seleccionado                = null;
    Entity exists                      = null;
    Boolean continuar                  = Boolean.TRUE;
		Map<String, Object>params          = new HashMap<>();
    Long idOrdenCodigo                 = -1L;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
      if(!Objects.equals(seleccionado.toLong("idTipoOrden"), 1L) && !Objects.equals(seleccionado.toLong("idTipoOrden"), 5L) && Objects.equals((Long)this.attrs.get("estatus"), 3L)) {
        params.put("codigo", this.attrs.get("codigo"));
        exists= (Entity)DaoFactory.getInstance().toEntity("TcKeetOrdenesCodigosDto", "existe", params);
        if(exists!= null && !exists.isEmpty()) {
          if(Objects.equals(exists.toLong("idOrdenCompra"), null)) 
            idOrdenCodigo= exists.toLong("idOrdenCodigo");
          else {
            this.attrs.put("texto", "EL C�DIGO YA FUE UTILIZADO [ ".concat((String)this.attrs.get("codigo")).concat(" ]"));
            continuar= Boolean.FALSE;
          } // if  
        } // if  
        else {
          this.attrs.put("texto", "EL C�DIGO NO EXISTE [ ".concat((String)this.attrs.get("codigo")).concat(" ]"));
          continuar= Boolean.FALSE;          
        } // else  
      } // if
      if(continuar) {
        TcManticOrdenesComprasDto orden= (TcManticOrdenesComprasDto)DaoFactory.getInstance().findById(TcManticOrdenesComprasDto.class, seleccionado.getKey());
        bitacora    = new TcManticOrdenesBitacoraDto((Long)this.attrs.get("estatus"), (String)this.attrs.get("justificacion"), JsfBase.getIdUsuario(), seleccionado.getKey(), -1L, orden.getConsecutivo(), orden.getTotal());
        transaccion = new Transaccion(orden, bitacora, idOrdenCodigo);
        if(transaccion.ejecutar(EAccion.JUSTIFICAR)) {
          JsfBase.addMessage("Cambio estatus", "Se realizo el cambio de estatus de forma correcta", ETipoMensaje.INFORMACION);
          // SI CAMBIA EL ESTATUS A SOLICITADA LANZA EL DIALOGO PARA MANDAR POR CORREO LA ORDEN DE COMPRA A LOS PROVEEDORES
          if(Objects.equals(orden.getIdOrdenEstatus(), 3L)) {
            this.doLoadMails();
            UIBackingUtilities.update("dialogoCorreos");
            UIBackingUtilities.execute("PF('dlgCorreos').show();");
          } // if  
    			this.attrs.put("texto", "");
    			this.attrs.put("codigo", "");
	    		this.attrs.put("justificacion", "");
          UIBackingUtilities.execute("PF('dlgEstatus').hide();");
        } // if  
        else
          JsfBase.addMessage("Cambio estatus", "Ocurrio un error al realizar el cambio de estatus", ETipoMensaje.ERROR);
      } // if  
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
	}	// doActualizaEstatus
	
	public String doDiferencias() {
		JsfBase.setFlashAttribute("idOrdenCompra",((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("idAlmacen",((Entity)this.attrs.get("seleccionado")).get("idAlmacen"));
		JsfBase.setFlashAttribute("idProveedor",((Entity)this.attrs.get("seleccionado")).get("idProveedor"));
		return "diferencias".concat(Constantes.REDIRECIONAR);
	}
	
	public String doMovimientos() {
		JsfBase.setFlashAttribute("tipo", ETipoMovimiento.ORDENES_COMPRAS);
		JsfBase.setFlashAttribute(ETipoMovimiento.ORDENES_COMPRAS.getIdKey(), ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("regreso", "/Paginas/Mantic/Compras/Ordenes/filtro");
		return "movimientos".concat(Constantes.REDIRECIONAR);
	}

  public String doNotaEntrada() {
		JsfBase.setFlashAttribute("ordenCompra", this.attrs.get("ordenCompra"));
		JsfBase.setFlashAttribute("idNotaEntrada", null);
		return "/Paginas/Mantic/Inventarios/Entradas/filtro".concat(Constantes.REDIRECIONAR);
	}

	public String doEstructura(){
		JsfBase.setFlashAttribute("idOrdenCompra",((Entity)this.attrs.get("seleccionado")).getKey());		
		return "estructura".concat(Constantes.REDIRECIONAR);
	} // doEstructura
	
	public void doGlobalEvent(Boolean isViewException) {
		LOG.error("ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA ["+ isViewException+ "]");
	}
	
	public void doLoadMails() {
		Entity seleccionado= null;
		MotorBusqueda motor= null; 
		List<ProveedorTipoContacto>contactos= null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");			
			motor= new MotorBusqueda(seleccionado.toLong("idProveedor"));
			contactos= motor.toProveedoresTipoContacto();
			this.correos= new ArrayList<>();
			for(ProveedorTipoContacto contacto: contactos) {
				if(contacto.getIdTipoContacto().equals(ETiposContactos.CORREO.getKey()))
					this.correos.add(new Correo(contacto.getIdProveedorTipoContacto(), contacto.getValor(), contacto.getIdPreferido()));				
			} // for
			this.correos.add(new Correo(-1L, "", 2L));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	} // doLoadMails
	
  public void doLoadPhones() {
		Entity seleccionado= null;
		MotorBusqueda motor= null; 
		List<ProveedorTipoContacto>contactos= null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");			
			motor       = new MotorBusqueda(seleccionado.toLong("idProveedor"));
			contactos   = motor.toAllProveedoresTipoContacto();
			this.celulares= new ArrayList<>();
			for(ProveedorTipoContacto contacto: contactos) {
				if(contacto.getIdTipoContacto().equals(ETiposContactos.CELULAR.getKey()) || contacto.getIdTipoContacto().equals(ETiposContactos.CELULAR_NEGOCIO.getKey()) || contacto.getIdTipoContacto().equals(ETiposContactos.CELULAR_PERSONAL.getKey()))
					this.celulares.add(new Correo(contacto.getIdProveedorTipoContacto(), contacto.getValor(), contacto.getIdPreferido()));				
			} // for
			this.celulares.add(new Correo(-1L, "", 2L, Boolean.TRUE));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
    finally {
      Methods.clean(contactos);
    } // finally
	} // doLoadPhones
	  
	public void doAgregarCorreo() {
		Entity seleccionado    = null;
		Transaccion transaccion= null;
		try {
			if(!Cadena.isVacio(this.correo.getDescripcion())){
				seleccionado= (Entity)this.attrs.get("seleccionado");
				transaccion= new Transaccion(this.correo, seleccionado.toLong("idProveedor"));
				if(transaccion.ejecutar(EAccion.COMPLEMENTAR))
					JsfBase.addMessage("Se agrego el correo electronico correctamente !");
				else
					JsfBase.addMessage("Ocurri� un error al agregar el correo electronico");
			} // if
			else
				JsfBase.addMessage("Es necesario capturar un correo electronico !");
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doAgregarCorreo
	
	public void doAgregarCelular() {
		Entity seleccionado    = null;
		Transaccion transaccion= null;
		try {
			if(!Cadena.isVacio(this.celular.getDescripcion())){
				seleccionado= (Entity)this.attrs.get("seleccionado");
				transaccion= new Transaccion(seleccionado.toLong("idProveedor"), seleccionado.toString("proveedor"), this.celular);
				if(transaccion.ejecutar(EAccion.COMPLETO))
					JsfBase.addMessage("Se agreg�/modific� el celular correctamente !");
				else
					JsfBase.addMessage("Ocurri� un error al agregar/modificar el celular");
			} // if
			else
				JsfBase.addMessage("Es necesario capturar un celular !");
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doAgregarCelular
  
	public void doEnviarCorreoOrden() {
		StringBuilder sb= new StringBuilder("");
		if(this.selectedCorreos!= null && !this.selectedCorreos.isEmpty()) {
			for(Correo mail: this.selectedCorreos) {
				if(!Cadena.isVacio(mail.getDescripcion()))
					sb.append(mail.getDescripcion()).append(", ");
			} // for
		} // if
    if(sb.length()<= 1)
      sb.append("  ");
		Map<String, Object> params= new HashMap<>();
		//String[] emails= {"jimenez76@yahoo.com", (sb.length()> 0? sb.substring(0, sb.length()- 2): "")};
    String[] emails= null;
    switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
      case "cafu":
        sb.append("auxadministrativo@cafuconstrucciones.com, ");
        emails= sb.substring(0, sb.length()- 2).split("[,]");
        break;
      case "gylvi":
		    emails= sb.substring(0, sb.length()- 2).split("[,]");
        break;
      case "triana":
     		emails= sb.substring(0, sb.length()- 2).split("[,]");
        break;
    } // swtich   
		List<Attachment> files= new ArrayList<>(); 
		try {
			Entity seleccionado= (Entity)this.attrs.get("seleccionado");
			params.put("header", "...");
			params.put("footer", "...");
			params.put("empresa", JsfBase.getAutentifica().getEmpresa().getNombre());
			params.put("tipo", "Orden de compra");			
			params.put("razonSocial", seleccionado.toString("proveedor"));
			params.put("correo", ECorreos.ORDENES_COMPRA.getEmail());		
      params.put("solucion", Configuracion.getInstance().getEmpresa("titulo"));
      params.put("url", Configuracion.getInstance().getPropiedadServidor("sistema.dns"));
			this.doReporte("ORDEN_DETALLE", true);
			Attachment attachments= new Attachment(this.reporte.getNombre(), Boolean.FALSE);
			files.add(attachments);
			files.add(new Attachment("logo", ECorreos.ORDENES_COMPRA.getImages().concat(Configuracion.getInstance().getEmpresa("logo")), Boolean.TRUE));
			params.put("attach", attachments.getId());
			for (String item: emails) {
				try {
					if(!Cadena.isVacio(item)) {
					  IBaseAttachment notificar= new IBaseAttachment(ECorreos.ORDENES_COMPRA, ECorreos.ORDENES_COMPRA.getEmail(), item, ECorreos.ORDENES_COMPRA.getBackup(), Configuracion.getInstance().getEmpresa("titulo").concat(" - Orden de compra"), params, files);
					  LOG.info("Enviando correo a la cuenta: "+ item);
					  notificar.send();
					} // if	
				} // try
				finally {
				  if(attachments.getFile().exists()) {
   	  	    LOG.info("Eliminando archivo temporal: "+ attachments.getAbsolute());
				    // user.getFile().delete();
				  } // if	
				} // finally	
			} // for
	  	LOG.info("Se envio el correo de forma exitosa");
			if(sb.length()> 0)
		    JsfBase.addMessage("Se envi� el correo de forma exitosa", ETipoMensaje.INFORMACION);
			else
		    JsfBase.addMessage("No se selecciono ning�n correo, por favor verifiquelo e intente de nueva cuenta", ETipoMensaje.ALERTA);
		} // try // try
		catch(Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(files);
		} // finally
	} // doEnviarCorreoOrden
  
  public void doSendWhatsup() {
    StringBuilder sb= new StringBuilder();
    try {      
      Entity seleccionado= (Entity)this.attrs.get("seleccionado");			
      if(this.selectedCelulares!= null && !this.selectedCelulares.isEmpty()) {
        for(Correo phone: this.selectedCelulares) {
          if(!Cadena.isVacio(phone.getDescripcion()))
            sb.append(phone.getDescripcion()).append(", ");
        } // for
      } // if
      if(sb.length()> 0) {
        this.doReporte("ORDEN_DETALLE", true); 
        switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
          case "cafu":
            // sb.append("4491501250, ");
            break;
          case "gylvi":
            break;
          case "triana":
            break;
        } // swtich        
        Cafu notificar= new Cafu(seleccionado.toString("proveedor"), "celular", this.reporte.getAlias(), "ticket", "fecha");
        String[] phones= sb.substring(0, sb.length()- 2).split("[,]");
        for (String phone: phones) {
          notificar.setCelular(phone, Boolean.TRUE);
          LOG.info("Enviando mensaje por whatsapp al celular: "+ phone);
          notificar.setCorreo(ECorreos.COMPRAS.getEmail());
          notificar.doSendOrdenCompra();
        } // for
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

  public void doChangeEstatus() {
		Entity seleccionado= null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
      if(!Objects.equals(seleccionado.toLong("idTipoOrden"), 1L) && !Objects.equals((Long)this.attrs.get("estatus"), 1L))
        this.attrs.put("texto", "");
      this.attrs.put("activa", Objects.equals((Long)this.attrs.get("estatus"), 3L));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }

	public String toColor(Entity row) {
    if(Objects.equals(row.toLong("partidas"), 0L))
      UIBackingUtilities.execute("janal.warn('".concat(row.toString("consecutivo")).concat("', 'La orden de compra [").concat(row.toString("consecutivo")).concat(")] Esta incorrecta !');"));
		return Objects.equals(row.toLong("partidas"), 0L)? "janal-tr-diferencias": "";
	} // toColor

	public void doLoadContratos() {
		List<UISelectEntity> contratos= null;
		Map<String, Object>params     = new HashMap<>();
		try {
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			contratos= UIEntity.seleccione("VistaContratosDto", "desarrollos", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS, "clave");
			this.attrs.put("contratos", contratos);
			this.attrs.put("idContrato", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("contratos")));			
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} 

  public void doDetalle(Entity row) {
		Map<String, Object>params= new HashMap<>();
		List<Columna>columns     = new ArrayList<>();
		try {
			if(row!= null && !row.isEmpty()) {
        this.attrs.put("seleccionado", row);
    		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
          params.put("idEmpresa", this.attrs.get("idEmpresa"));
        else
          params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
        params.put("idOrdenCompra", row.getKey());
        params.put("sortOrder", "order by tc_mantic_ordenes_detalles.propio");
				columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
				columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
        columns.add(new Columna("cantidad", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
				columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
				this.lazyDetalle= new FormatLazyModel("VistaOrdenesComprasDto", "desglose", params, columns);
				UIBackingUtilities.resetDataTable("tablaDetalle");
			} // if
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
  }
    
	@Override
	protected void finalize() throws Throwable {
    super.finalize();
		Methods.clean(this.correos);
		Methods.clean(this.selectedCorreos);
		Methods.clean(this.celulares);
		Methods.clean(this.selectedCelulares);
	}	// finalize
  
}
