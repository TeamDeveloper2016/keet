package mx.org.kaana.mantic.compras.requisiciones.backing;

import java.io.InputStream;
import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
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
import mx.org.kaana.kajool.procesos.reportes.beans.Definicion;
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
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Periodo;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.compras.requisiciones.beans.RegistroRequisicion;
import mx.org.kaana.mantic.compras.requisiciones.beans.Requisicion;
import mx.org.kaana.mantic.compras.requisiciones.reglas.Transaccion;
import mx.org.kaana.mantic.comun.JuntarReporte;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.db.dto.TcManticRequisicionesBitacoraDto;
import mx.org.kaana.mantic.enums.EReportes;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named(value= "manticComprasRequisicionesFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428332L;
  private static final String COLUMN_DATA_FILE_ESPECIAL= "EMPRESA,DESARROLLO,CONTRATO,CONSECUTIVO,ESTATUS,REGISTRO,OBSERVACIONES,PEDIDO,ENTREGA,NOMBRE,CODIGO,ARTICULO,CANTIDAD,ELIMINADO,ORDEN,ESTADO";  
  
  private FormatLazyModel detalles;
	private LocalDate fechaInicio;
	private LocalDate fechaTermino;
	private Reporte reporte;

  public FormatLazyModel getDetalles() {
    return detalles;
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
	
  public Reporte getReporte() {
		return reporte;
	}	
	
  public StreamedContent getEspecial() {
		StreamedContent regresar = null;
		Xls xls                  = null;
		Map<String, Object>params= this.toPrepare();
		String template          = "ESPECIAL";
		try {
      params.put("sortOrder", "order by tc_mantic_requisiciones.registro desc");
      String salida  = EFormatos.XLS.toPath().concat(Archivo.toFormatNameFile(template).concat(".")).concat(EFormatos.XLS.name().toLowerCase());
      String fileName= JsfBase.getRealPath("").concat(salida);
      xls= new Xls(fileName, new Modelo(params, "VistaRequisicionesDto", "especial", template), COLUMN_DATA_FILE_ESPECIAL);	
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
  
  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.attrs.put("idRequisicion", JsfBase.getFlashAttribute("idRequisicion"));
			this.toLoadCatalogos();      
      if(!Objects.equals(this.attrs.get("idRequisicion"), null))
        this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
 
  @Override
  public void doLoad() {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= this.toPrepare();
    try {
      params.put("sortOrder", "order by tc_mantic_requisiciones.registro desc");
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));      
      columns.add(new Columna("fechaEntregada", EFormatoDinamicos.FECHA_CORTA));      
      columns.add(new Columna("fechaPedido", EFormatoDinamicos.FECHA_CORTA));      
      this.lazyModel = new FormatCustomLazy("VistaRequisicionesDto", params, columns);
      UIBackingUtilities.resetDataTable();
 			this.attrs.put("idRequisicion", null);
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
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Compras/Requisiciones/filtro");		
			JsfBase.setFlashAttribute("idRequisicion", eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR) ? ((Entity)this.attrs.get("seleccionado")).getKey(): -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return "/Paginas/Mantic/Compras/Requisiciones/accion".concat(Constantes.REDIRECIONAR);
  } // doAccion  
	
  public void doEliminar() {
		Transaccion transaccion        = null;
		Entity seleccionado            = (Entity) this.attrs.get("seleccionado");
		RegistroRequisicion requisicion= null;
		try {
			requisicion= new RegistroRequisicion(new Requisicion(seleccionado.getKey()), new ArrayList<>());
			transaccion= new Transaccion(requisicion, this.attrs.get("justificacionEliminar").toString());
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar", "La requisición de compra se ha eliminado correctamente", ETipoMensaje.ERROR);
			else
				JsfBase.addMessage("Eliminar", "Ocurrió un error al eliminar la requisicion de compra", ETipoMensaje.ERROR);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
  } // doEliminar

	protected Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb            = new StringBuilder();
    try {
      // ESTO ES UN PARCHE PARA MOSTRAR SOLO LOS REGISTROS DEL VENDEDOR 31/01/2024
      if(!JsfBase.isEncargado())
        sb.append("(tc_mantic_requisiciones.id_usuario= ").append(JsfBase.getIdUsuario()).append(") and ");
      UISelectEntity estatus= (UISelectEntity) this.attrs.get("idRequisicionEstatus");
      if(!Cadena.isVacio(this.attrs.get("idRequisicion")) && !Objects.equals(this.attrs.get("idRequisicion"), -1L)) 
        sb.append("(tc_mantic_requisiciones.id_requisicion=").append(this.attrs.get("idRequisicion")).append(") and ");
      if(!Cadena.isVacio(this.attrs.get("consecutivo")))
        sb.append("(tc_mantic_requisiciones.consecutivo like '%").append(this.attrs.get("consecutivo")).append("%') and ");
			if(this.attrs.get("nombre")!= null && ((UISelectEntity)this.attrs.get("nombre")).getKey()> 0L) 
				sb.append("tc_mantic_requisiciones_detalles.id_articulo=").append(((UISelectEntity)this.attrs.get("nombre")).getKey()).append(" and ");						
  		else 
	  		if(!Cadena.isVacio(JsfBase.getParametro("nombre_input"))) { 
					String nombre= JsfBase.getParametro("nombre_input").replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*");
		  		sb.append("(tc_mantic_requisiciones_detalles.nombre regexp '.*").append(nombre).append(".*' or tc_mantic_requisiciones_detalles.descripcion regexp '.*").append(nombre).append(".*') and ");				
				} // if	
      if(!Cadena.isVacio(this.attrs.get("fechaInicio")))
        sb.append("(date_format(tc_mantic_requisiciones.fecha_pedido, '%Y%m%d')= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");	
      if(!Cadena.isVacio(this.attrs.get("fechaTermino")))
        sb.append("(date_format(tc_mantic_requisiciones.fecha_entregada, '%Y%m%d')= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaTermino"))).append("') and ");	
      if(estatus!= null && !estatus.getKey().equals(-1L))
        sb.append("(tc_mantic_requisiciones.id_requisicion_estatus= ").append(estatus.getKey()).append(") and ");
      if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && ((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()>= 1L)
        sb.append("(tc_mantic_requisiciones.id_desarrollo= ").append(((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()).append(") and ");
      if(!Cadena.isVacio(this.attrs.get("idContrato")) && ((UISelectEntity)this.attrs.get("idContrato")).getKey()>= 1L)
        sb.append("(tc_mantic_requisiciones.id_contrato= ").append(((UISelectEntity)this.attrs.get("idContrato")).getKey()).append(") and ");
      if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
        regresar.put("idEmpresa", this.attrs.get("idEmpresa"));
      else
        regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
      if(sb.length()== 0)
        regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      else	
        regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
    } // try
    catch(Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    }
		return regresar;		
	}
	
	protected void toLoadCatalogos() {
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
      this.attrs.put("clientes", (List<UISelectEntity>) UIEntity.seleccione("VistaRequisicionesDto", "clientes", params, columns, "clave"));
			this.attrs.put("idCliente", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("clientes")));
			columns.clear();
      this.attrs.put("estatusFiltro", (List<UISelectEntity>) UIEntity.seleccione("TcManticRequisicionesEstatusDto", "row", params, columns, "nombre"));
			this.attrs.put("idRequisicionEstatus", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("estatusFiltro")));
      this.doLoadDesarrollos();
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
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
      Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} 
  
	public void doReporte(String nombre) throws Exception{
    Parametros comunes           = null;
		Map<String, Object>parametros= null;
    Map<String, Object>params    = null;
		EReportes reporteSeleccion   = null;
    Entity seleccionado          = ((Entity)this.attrs.get("seleccionado"));
		try{		
      params= this.toPrepare();	
      if(seleccionado != null)
        params.put("idRequisicion", seleccionado.getKey());
      params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());	
      params.put("sortOrder", "order by tc_mantic_requisiciones.id_empresa, tc_mantic_requisiciones.ejercicio, tc_mantic_requisiciones.orden");
      reporteSeleccion= EReportes.valueOf(nombre);
      comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      //this.reporte.clean();
      this.reporte= JsfBase.toReporte();	
      parametros= comunes.getComunes();
      parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getNombre().toUpperCase());
      parametros.put("NOMBRE_REPORTE", reporteSeleccion.getTitulo());
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));			
      this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));		
      if(doVerificarReporte())
        this.reporte.doAceptar();			
    } // try
    catch(Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);			
    } // catch	
  } // doReporte
  
  public void doImprimirTodos(String nombre) {
    Parametros comunes                  = null;
		Map<String, Object>parametros       = new HashMap<>();
    Map<String, Object>params           = null;
		EReportes reporteSeleccion          = null;
    Entity seleccionado                 = ((Entity)this.attrs.get("seleccionado"));   
    List<Definicion> definiciones       = new ArrayList<Definicion>();
    List<Entity> proveedores            = new ArrayList<>();
		try {
      params= this.toPrepare();	
      if(seleccionado != null)
        params.put("idRequisicion", seleccionado.getKey());
      params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());	
      params.put("sortOrder", "order by tc_mantic_requisiciones.id_empresa, tc_mantic_requisiciones.ejercicio, tc_mantic_requisiciones.orden");
      reporteSeleccion= EReportes.valueOf(nombre);
      this.reporte= JsfBase.toReporte();	
      proveedores = DaoFactory.getInstance().toEntitySet("TcManticRequisicionesProveedoresDto", "proveedores", params);
      for(Entity proveedor:proveedores) {
        comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa(), -1L, proveedor.getKey(), -1L);
        parametros= comunes.getComunes();
        parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getNombre().toUpperCase());
        parametros.put("NOMBRE_REPORTE", reporteSeleccion.getTitulo());
        parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));		
        definiciones.add(new Definicion((Map<String, Object>) ((HashMap) params).clone(), (Map<String, Object>) ((HashMap) parametros).clone(), reporteSeleccion.getProceso(), reporteSeleccion.getIdXml(), reporteSeleccion.getJrxml()));
      }
      this.reporte.toAsignarReportes(new JuntarReporte(definiciones, reporteSeleccion, "/Paginas/Mantic/Compras/Requisiciones/filtro",false, true));
      if(doVerificarReporte())
        this.reporte.doAceptar();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
    finally{
      Methods.clean(proveedores);
    }
	} // doImprimirTodos	
  
  public boolean doVerificarReporte() {
    boolean regresar = false;
		if(this.reporte.getTotal()> 0L) {
			UIBackingUtilities.execute("start(" + this.reporte.getTotal() + ")");		
      regresar = true;
    }
		else{
			UIBackingUtilities.execute("generalHide();");		
			JsfBase.addMessage("Reporte", "No se encontraron registros para el reporte", ETipoMensaje.ERROR);
      regresar = false;
		} // else
    return regresar;
	} 
	
	public void doLoadEstatus(){
		Entity seleccionado          = (Entity)this.attrs.get("seleccionado");
		Map<String, Object>params    = new HashMap<>();
		List<UISelectItem> allEstatus= null;
		try {
			params.put(Constantes.SQL_CONDICION, "id_requisicion_estatus in (".concat(seleccionado.toString("estatusAsociados")).concat(")"));
			allEstatus= UISelect.build("TcManticRequisicionesEstatusDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);			
			this.attrs.put("allEstatus", allEstatus);
			this.attrs.put("estatus", allEstatus.get(0));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} 
	
	public void doSolicitar(Entity row) {
    this.attrs.put("seleccionado", row);
    this.attrs.put("justificacion", "CAMBIO AUTOMATICO");
    this.attrs.put("estatus", "2");
    this.doActualizarEstatus();
  }
  
	public void doActualizarEstatus() {
		Transaccion transaccion                  = null;
		TcManticRequisicionesBitacoraDto bitacora= null;
		Entity seleccionado                      = (Entity)this.attrs.get("seleccionado");
		try {
			bitacora= new TcManticRequisicionesBitacoraDto(-1L, (String)this.attrs.get("justificacion"), JsfBase.getIdUsuario(), Long.valueOf(this.attrs.get("estatus").toString()), seleccionado.getKey());
			transaccion= new Transaccion(bitacora);
			if(transaccion.ejecutar(EAccion.JUSTIFICAR)) {
				JsfBase.addMessage("Cambio estatus", "Se realizo el cambio de estatus de forma correcta", ETipoMensaje.INFORMACION);
        this.attrs.put("idRequisicion", seleccionado.getKey());
        this.doLoad();
      } // if  
			else
				JsfBase.addMessage("Cambio estatus", "Ocurrio un error al realizar el cambio de estatus", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally {
			this.attrs.put("justificacion", "");
		} // finally
	}	

	public void doClonar() {
    Entity row= (Entity)this.attrs.get("seleccionado");
		Transaccion transaccion= null;
		try {
      if(!Objects.equals(row, null)) {
        transaccion= new Transaccion(row.toLong("idRequisicion"));
        if(transaccion.ejecutar(EAccion.GENERAR)) {
          JsfBase.addMessage("Clonar", "Se realizó la copia correctamente", ETipoMensaje.INFORMACION);
          this.attrs.put("idRequisicion", transaccion.getIdRequisicion());
          this.doLoad();
        } // if  
        else
          JsfBase.addMessage("Clonar", "Ocurrio un error al copiar la requisición", ETipoMensaje.ERROR);
      } // if  
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
  }

  public void doLoadDetalle() {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= new HashMap<>();
    try {
      Periodo periodo= new Periodo();
      periodo.addMeses(-3);
      params.put("sortOrder", "order by tc_mantic_requisiciones.id_requisicion desc, tc_mantic_requisiciones_detalles.id_requisicion_detalle");
      params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      params.put("fecha", periodo.toString());
      if(!JsfBase.isAdmin())
        params.put(Constantes.SQL_CONDICION, "(tc_mantic_requisiciones.id_usuario= "+ JsfBase.getIdUsuario()+ ")");
      else
        params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.detalles= new FormatCustomLazy("VistaRequisicionesDto", "consulta", params, columns);
      UIBackingUtilities.resetDataTable("detalle");
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

	public String toColor(Entity row) {
		return Objects.equals(row.toLong("idOrdenCompra"), -1L)? "janal-tr-diferencias": Objects.equals(row.toLong("idEliminado"), 1L)? "janal-tr-error": "";
	} 

  public List<UISelectEntity> doCompleteArticulo(String query) {
		List<Columna> columns         = new ArrayList<>();
    Map<String, Object> params    = new HashMap<>();
		List<UISelectEntity> articulos= null;
    try {
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
  		params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", -1L);
			String search= query; 
			if(!Cadena.isVacio(search)) 
  			search= search.replaceAll(Constantes.CLEAN_SQL, "").trim().toUpperCase().replaceAll("(,| |\\t)+", ".*.*");			
			else
				search= "WXYZ";
  		params.put("codigo", search);			        
      params.put("idArticuloTipo", "1");	      
      articulos= (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", "porNombreTipoArticulo", params, columns, 40L);
      this.attrs.put("articulosFiltro", articulos);
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
		return articulos;
	}	
  
}
