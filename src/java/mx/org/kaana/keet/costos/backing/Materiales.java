package mx.org.kaana.keet.costos.backing;

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
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.procesos.reportes.beans.Modelo;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.archivo.Archivo;
import mx.org.kaana.libs.archivo.Xls;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@Named(value = "keetCostosMateriales")
@ViewScoped 
public class Materiales extends IBaseFilter implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Materiales.class);
  private static final long serialVersionUID= 8793667741599428332L;
  private static final String COLUMN_DATA_FILE_ESPECIAL= "EMPRESA,DESARROLLO,CONSECUTIVO,CLAVE,CONTRATO,CLIENTE,USUARIO,TIPO,ESTATUS,PROVEEDOR,TOTAL,REQUISICION,CODIGO,NOMBRE,CANTIDAD,COSTO,IVA,SUB TOTAL,IMPUESTOS,IMPORTE,FECHA";  
  
	private Reporte reporte;
	private LocalDate fechaInicio;
	private LocalDate fechaTermino;
  
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
  
  public String getGeneral() {
    String partidas= Numero.formatear(Numero.MILES_SIN_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("partidas"));
    String total   = Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("total"));
    return "Suma importe: <strong>"+ total+ "</strong> | partidas: <strong>"+ partidas+ "</strong>";  
  }
  
  @PostConstruct
  @Override
  protected void init() {
    Map<String, Object> params = new HashMap<>();
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.attrs.put("general", this.toEmptyTotales());
			this.toLoadCatalogos();
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
      params.put("sortOrder", "order by tc_keet_contratos.id_contrato, tc_mantic_ordenes_detalles.nombre");
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("desarrollo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("contrato", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cantidad", EFormatoDinamicos.MILES_SIN_DECIMALES));
      columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("ordenes", EFormatoDinamicos.MILES_SIN_DECIMALES));
      columns.add(new Columna("partidas", EFormatoDinamicos.MILES_SIN_DECIMALES));
      this.lazyModel= new FormatCustomLazy("VistaCostosDto", "materiales", params, columns);
      this.attrs.put("general", this.toTotales("VistaOrdenesComprasDto", "totales", params));
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

	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb= new StringBuilder();
		if(!Cadena.isVacio(this.attrs.get("idOrdenCompra")) && !this.attrs.get("idOrdenCompra").toString().equals("-1"))
  		sb.append("(tc_mantic_ordenes_compras.id_orden_compra=").append(this.attrs.get("idOrdenCompra")).append(") and ");
    if(this.attrs.get("nombre")!= null && ((UISelectEntity)this.attrs.get("nombre")).getKey()> 0L) 
      sb.append("tc_mantic_ordenes_detalles.id_articulo=").append(((UISelectEntity)this.attrs.get("nombre")).getKey()).append(" and ");
    else 
      if(!Cadena.isVacio(JsfBase.getParametro("nombre_input"))) { 
        String nombre= JsfBase.getParametro("nombre_input").replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*");
        sb.append("(tc_mantic_ordenes_detalles.nombre regexp '.*").append(nombre).append(".*' or tc_mantic_ordenes_detalles.nombre regexp '.*").append(nombre).append(".*') and ");
      } // if	
		if(!Cadena.isVacio(this.fechaInicio))
		  sb.append("(date_format(tc_mantic_ordenes_compras.registro, '%Y%m%d')>= '").append(this.fechaInicio.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.fechaTermino))
		  sb.append("(date_format(tc_mantic_ordenes_compras.registro, '%Y%m%d')<= '").append(this.fechaTermino.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("idCliente")) && !this.attrs.get("idCliente").toString().equals("-1"))
  		sb.append("(tc_mantic_clientes.id_cliente= ").append(this.attrs.get("idCliente")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idProveedor")) && !this.attrs.get("idProveedor").toString().equals("-1"))
  		sb.append("(tc_mantic_proveedores.id_proveedor= ").append(this.attrs.get("idProveedor")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idEstatus")) && !this.attrs.get("idEstatus").toString().equals("-1") && ((UISelectEntity)this.attrs.get("idEstatus")).getKey()< 97L)
  		sb.append("(tc_keet_contratos.id_contrato_estatus= ").append(this.attrs.get("idEstatus")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && ((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()>= 1L)
  		sb.append("(tc_mantic_ordenes_compras.id_desarrollo=").append(((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idContrato")) && ((UISelectEntity)this.attrs.get("idContrato")).getKey()>= 1L)
  		sb.append("(tc_mantic_ordenes_compras.id_contrato=").append(((UISelectEntity)this.attrs.get("idContrato")).getKey()).append(") and ");
    sb.append("(tc_mantic_ordenes_compras.id_orden_estatus in (3, 4, 5, 6, 7)) and ");
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
      this.attrs.put("clientes", (List<UISelectEntity>) UIEntity.seleccione("VistaOrdenesComprasDto", "clientes", params, columns, "clave"));
			this.attrs.put("idCliente", new UISelectEntity("-1"));
      this.attrs.put("proveedores", (List<UISelectEntity>) UIEntity.seleccione("VistaOrdenesComprasDto", "proveedores", params, columns, "clave", Constantes.SQL_TODOS_REGISTROS));
			this.attrs.put("idProveedor", new UISelectEntity("-1"));
			this.doLoadDesarrollos();
      this.toLoadEstatus();
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
	} 
	
  @Override
	public void doGlobalEvent(Boolean isViewException) {
		LOG.error("ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA ["+ isViewException+ "]");
	}
	
	public String toColor(Entity row) {
//    if(Objects.equals(row.toLong("partidas"), 0L))
//      UIBackingUtilities.execute("janal.warn('".concat(row.toString("consecutivo")).concat("', 'La orden de compra [").concat(row.toString("consecutivo")).concat(")] Esta incorrecta !');"));
		return ""; // Objects.equals(row.toLong("partidas"), 0L)? "janal-tr-diferencias": "";
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

  private Entity toTotales(String proceso, String idXml, Map<String, Object> params) {
    Entity regresar= null;
    try {      
      regresar= (Entity)DaoFactory.getInstance().toEntity(proceso, idXml, params);
      if(Objects.equals(regresar, null) || regresar.isEmpty()) 
        regresar= this.toEmptyTotales();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    return regresar;
  }
  
  private Entity toEmptyTotales() {
    Entity regresar= new Entity(-1L);
    regresar.addField("partidas", 0D);
    regresar.addField("total", 0D);
    return regresar;
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
      this.attrs.put("articulos", articulos);
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
  
}
