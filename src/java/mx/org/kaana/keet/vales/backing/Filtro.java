package mx.org.kaana.keet.vales.backing;

import java.io.File;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.keet.db.dto.TcKeetBoletasBitacoraDto;
import mx.org.kaana.keet.vales.beans.Vale;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Encriptar;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Periodo;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.keet.vales.reglas.Transaccion;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.ventas.comun.IBaseTicket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.SelectEvent;

@Named(value= "keetValesFiltro")
@ViewScoped
public class Filtro extends IBaseTicket implements Serializable {

  private static final long serialVersionUID = 8793667141599428332L;
  private static final Log LOG = LogFactory.getLog(Filtro.class);
  
	private Reporte reporte;
	
	public Reporte getReporte() {
		return reporte;
	}	// getReporte

	public void setReporte(Reporte reporte) {
		this.reporte=reporte;
	}
  
  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.attrs.put("idEmpresa", new UISelectEntity(JsfBase.getAutentifica().getEmpresa().getIdEmpresa()));
      this.attrs.put("idBoleta", JsfBase.getFlashAttribute("idBoleta"));
      this.attrs.put("sortOrder", "order by tc_keet_boletas.registro desc");
			this.toLoadCatalog();
      if(this.attrs.get("idBoleta")!= null) {
			  this.doLoad();
				this.attrs.put("idBoleta", null);
			} // if
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
			params.put("sortOrder", this.attrs.get("sortOrder"));
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));      
      this.lazyModel = new FormatCustomLazy("VistaBoletasDto", params, columns);
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
  } // doLoad

  public String doAccion(String accion) {
    EAccion eaccion= null;
		String regresar= "/Paginas/Keet/Vales/accion".concat(Constantes.REDIRECIONAR); 
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Vales/filtro");					
			JsfBase.setFlashAttribute("idBoleta", eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR) ? ((Entity)this.attrs.get("seleccionado")).getKey() : -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return regresar;
  } 
	
  public void doEliminar() {
		Transaccion transaccion   = null;
		Entity seleccionado       = null;
    Map<String, Object> params= new HashMap<>();
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
      params.put("idBoleta", seleccionado.toLong("idBoleta"));      
      Vale vale= (Vale)DaoFactory.getInstance().toEntity(Vale.class, "TcKeetBoletasDto", "detalle", params);
			transaccion = new Transaccion(vale);
			if(transaccion.ejecutar(EAccion.ELIMINAR))
				JsfBase.addMessage("Eliminar", "El vale se ha eliminado correctamente", ETipoMensaje.ERROR);
			else
				JsfBase.addMessage("Eliminar", "Ocurrió un error al eliminar el vale", ETipoMensaje.ERROR);								
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
    finally {
      Methods.clean(params);
    } // finally
  } // doEliminar

	protected Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb            = new StringBuilder();
		UISelectEntity estatus= (UISelectEntity) this.attrs.get("idBoletaEstatus");
		if(!Cadena.isVacio(this.attrs.get("idAlmacen")) && !this.attrs.get("idAlmacen").toString().equals("-1"))
  		sb.append("(tc_keet_boletas.id_almacen=").append(this.attrs.get("idAlmacen")).append(") and ");
		if(!Cadena.isVacio(JsfBase.getParametro("codigo_input"))) 
	 	  sb.append("tc_keet_boletas_detalles.codigo regexp '.*").append(JsfBase.getParametro("codigo_input").replaceAll(Constantes.CLEAN_SQL, "").replaceAll("(,| |\\t)+", ".*.*")).append(".*' and ");
		if(!Cadena.isVacio(this.attrs.get("articulo")) && !Objects.equals(((Entity)this.attrs.get("articulo")).getKey(), -1L))
			sb.append("(tc_keet_boletas_detalles.id_articulo= ").append(((Entity)this.attrs.get("articulo")).getKey()).append(") and ");					
		else 
  		if(!Cadena.isVacio(JsfBase.getParametro("articulo_input")))
    		sb.append("(upper(tc_keet_boletas_detalles.nombre) like upper('%").append(JsfBase.getParametro("articulo_input")).append("%')) and ");
		if(!Cadena.isVacio(this.attrs.get("idBoleta")) && !this.attrs.get("idBoleta").toString().equals("-1"))
  		sb.append("(tc_keet_boletas.id_boleta=").append(this.attrs.get("idBoleta")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("consecutivo")))
  		sb.append("(tc_keet_boletas.consecutivo like '%").append(this.attrs.get("consecutivo")).append("%') and ");
		if(!Cadena.isVacio(this.attrs.get("fechaInicio")))
		  sb.append("(date_format(tc_keet_boletas.registro, '%Y%m%d')>= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("fechaTermino")))
		  sb.append("(date_format(tc_keet_boletas.registro, '%Y%m%d')<= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaTermino"))).append("') and ");	
		if(estatus!= null && !estatus.getKey().equals(-1L))
  		sb.append("(tc_keet_boletas.id_boleta_estatus= ").append(estatus.getKey()).append(") and ");
  	if(!Objects.equals(((UISelectEntity)this.attrs.get("idEmpresa")).getKey(), -1L))
		  regresar.put("idEmpresa", ((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
		else
		  regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
		if(sb.length()== 0) {
      Periodo periodo= new Periodo();
      periodo.addMeses(-2);
		  regresar.put(Constantes.SQL_CONDICION, "date_format(tc_keet_boletas.registro, '%Y%m%d')>= '".concat(periodo.toString()).concat("'"));
    } // if  
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
			columns.clear();
      List<UISelectEntity> estatus= (List<UISelectEntity>) UIEntity.build("TcManticVentasEstatusDto", "row", params, columns);
      this.attrs.put("estatusFiltro", estatus);
			this.attrs.put("idBoletaEstatus", UIBackingUtilities.toFirstKeySelectEntity(estatus));
      this.doLoadAlmacenes();
    } // try
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}
	
	public void doLoadAlmacenes() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
  		params.put("sucursales", ((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      List<UISelectEntity> almacenes= (List<UISelectEntity>) UIEntity.seleccione("TcManticAlmacenesDto", "almacenes", params, columns, "clave");
      this.attrs.put("almacenes", almacenes);
			this.attrs.put("idAlmacen", UIBackingUtilities.toFirstKeySelectEntity(almacenes));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}
  
	public boolean doVerificarReporte() {
    boolean regresar = Boolean.FALSE;
		if(this.reporte.getTotal()> 0L) {
			UIBackingUtilities.execute("start("+ this.reporte.getTotal()+ ")");		
      regresar = Boolean.TRUE;
    } // if
    else {
			UIBackingUtilities.execute("generalHide();");		
			JsfBase.addMessage("Reporte", "No se encontraron registros para el reporte", ETipoMensaje.ERROR);
		} // else
    return regresar;
	} 
	
	public void doLoadEstatus(){
		Entity seleccionado          = null;
		Map<String, Object>params    = new HashMap<>();
		List<UISelectItem> allEstatus= null;
		try {
			seleccionado= (Entity)this.attrs.get("seleccionado");
			params.put(Constantes.SQL_CONDICION, "id_boleta_estatus in (".concat(seleccionado.toString("estatusAsociados")).concat(")"));
			allEstatus= UISelect.build("TcKeetBoletasEstatusDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);			
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
	} // doLoadEstatus
	
	public void doActualizarEstatus() {
		Transaccion transaccion         = null;
		Entity seleccionado             = null;
    Map<String, Object> params      = new HashMap<>();
    TcKeetBoletasBitacoraDto bitacora= null;
		try {
			seleccionado= (Entity) this.attrs.get("seleccionado");			
      params.put("idBoleta", seleccionado.toLong("idBoleta"));      
      Vale vale= (Vale)DaoFactory.getInstance().toEntity(Vale.class, "TcKeetBoletasDto", "detalle", params);
      bitacora = new TcKeetBoletasBitacoraDto((String)this.attrs.get("justificacion"), JsfBase.getIdUsuario(), -1L, Long.valueOf((String)this.attrs.get("estatus")), vale.getIdBoleta());
			transaccion= new Transaccion(vale, bitacora);
			if(transaccion.ejecutar(EAccion.JUSTIFICAR))
				JsfBase.addMessage("Cambio estatus", "Se realizó el cambio de estatus", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("Cambio estatus", "Ocurrio un error al realizar el cambio", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally {
			this.attrs.put("justificacion", "");
		} // finally
	}	
	
	public void doUpdateCodigos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			String search= (String)this.attrs.get("codigoCodigo"); 
			search= !Cadena.isVacio(search) ? search.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim(): "WXYZ";
			params.put("idAlmacen", JsfBase.getAutentifica().getEmpresa().getIdAlmacen());
			if(!Objects.equals(((UISelectEntity)this.attrs.get("idEmpresa")).getKey(), -1L))
				params.put("sucursales", ((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
			else
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", -1L);			
  		params.put("codigo", search);			
      this.attrs.put("codigos", (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", "porCodigo", params, columns, 20L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}	// doUpdateCodigos

	public List<UISelectEntity> doCompleteCodigo(String query) {
		this.attrs.put("codigoCodigo", query);
    this.doUpdateCodigos();		
		return (List<UISelectEntity>)this.attrs.get("codigos");
	}	// doCompleteCodigo

	public void doAsignaCodigo(SelectEvent event) {
		UISelectEntity seleccion    = null;
		List<UISelectEntity> codigos= null;
		try {
			codigos= (List<UISelectEntity>) this.attrs.get("codigos");
			seleccion= codigos.get(codigos.indexOf((UISelectEntity)event.getObject()));
			this.attrs.put("codigoSeleccion", seleccion);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doAsignaCodigo
	
	public void doUpdateArticulos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			String search= (String)this.attrs.get("codigoArticulo"); 
			search= !Cadena.isVacio(search) ? search.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim(): "WXYZ";
			params.put("idAlmacen", JsfBase.getAutentifica().getEmpresa().getIdAlmacen());
			if(!Objects.equals(((UISelectEntity)this.attrs.get("idEmpresa")).getKey(), -1L))
				params.put("sucursales", ((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
			else
				params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
  		params.put("idProveedor", -1L);			
  		params.put("codigo", search.replaceAll("[ ]", "*.*"));			
      this.attrs.put("articulos", (List<UISelectEntity>) UIEntity.build("VistaOrdenesComprasDto", "porNombre", params, columns, 20L));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}	// doUpdateArticulo

	public List<UISelectEntity> doCompleteArticulo(String query) {
		this.attrs.put("codigoArticulo", query);
    this.doUpdateArticulos();		
		return (List<UISelectEntity>)this.attrs.get("articulos");
	}	// doCompleteCodigo

	public void doAsignaArticulo(SelectEvent event) {
		UISelectEntity seleccion      = null;
		List<UISelectEntity> articulos= null;
		try {
			articulos= (List<UISelectEntity>) this.attrs.get("articulos");
			seleccion= articulos.get(articulos.indexOf((UISelectEntity)event.getObject()));
			this.attrs.put("articulosSeleccion", seleccion);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doAsignaArticulo

	public void doReporte() throws Exception {
   this.doReporte(Boolean.FALSE);  
  }
  
	public void doReporte(boolean email) throws Exception {
		Map<String, Object>params    = new HashMap<>();
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;
		try{				
			reporteSeleccion= EReportes.VALE_ALMACEN;
			this.reporte= JsfBase.toReporte();
			params.put("idBoleta", ((Entity)this.attrs.get("seleccionado")).getKey());			
      Parametros comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			parametros= comunes.getComunes();
			parametros.put("REPORTE_EMPRESA", JsfBase.getAutentifica().getEmpresa().getNombreCorto());
		  parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getTitulo().toUpperCase());
			parametros.put("NOMBRE_REPORTE", reporteSeleccion.getTitulo());
			parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));		
			parametros.put("REPORTE_DNS", Configuracion.getInstance().getPropiedadServidor("sistema.dns"));		
      switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
        case "iib":
   			  parametros.put("REPORTE_SUB_TITULO", "GRANOS Y SEMILLAS");		
          break;
        case "kalan":
   			  parametros.put("REPORTE_SUB_TITULO", "LA CALIDAD Y EL SERVICIO NOS DISTINGUE");		
          break;
        case "tsaak":
   			  parametros.put("REPORTE_SUB_TITULO", "LA CALIDAD Y EL SERVICIO NOS DISTINGUE");		
          break;
      } // swtich
			parametros.put("REPORTE_NOTIFICA", Configuracion.getInstance().getEmpresa("celular"));		
      Encriptar encriptado= new Encriptar();
      String codigo= encriptado.encriptar(Fecha.formatear(Fecha.CODIGO_SEGURIDAD, ((Entity)this.attrs.get("seleccionado")).toTimestamp("registro")));
			parametros.put("REPORTE_CODIGO_SEGURIDAD", codigo);			
			if(email) { 
				this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));		
        File file= new File(JsfBase.getRealPath(this.reporte.getNombre()));
        if(!file.exists())
          this.reporte.doAceptarSimple();			
			} // if
			else {				
        this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));		
        if(this.doVerificarReporte())
          this.reporte.doAceptar();			
			} // else
		} // try
		catch(Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
    } // catch	
	} 
  
	@Override
	protected void finalize() throws Throwable {
    super.finalize();		
	}	
	
}
