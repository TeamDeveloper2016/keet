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
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.procesos.reportes.beans.Modelo;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.keet.comun.Catalogos;
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

@Named(value = "keetCostosDestajos")
@ViewScoped 
public class Destajos extends IBaseFilter implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Destajos.class);
  private static final long serialVersionUID= 8793667741599428332L;
  private static final String COLUMN_DATA_FILE_ESPECIAL= "EMPRESA,DESARROLLO,CONSECUTIVO,CLAVE,CONTRATO,CLIENTE,USUARIO,TIPO,ESTATUS,PROVEEDOR,TOTAL,REQUISICION,CODIGO,NOMBRE,CANTIDAD,COSTO,IVA,SUB TOTAL,IMPUESTOS,IMPORTE,FECHA";  
  
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
	
  public String getGeneral() {
    String contratos= Numero.formatear(Numero.MILES_SIN_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("contratos"));
    String costos   = Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("costos"));
    String destajos = Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("destajos"));
    return "Contratos: <strong>"+ contratos+ "</strong>  | suma costos: <strong>"+ costos+ "</strong>  |  destajos: <strong> "+ destajos+ "</strong>";  
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
      params.put("sortOrder", "order by tc_keet_desarrollos.id_desarrollo, tc_keet_contratos.clave desc");
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("desarrollo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("contrato", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("noViviendas", EFormatoDinamicos.MONEDA_SAT_DECIMALES));
      columns.add(new Columna("costo", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("destajos", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));      
      this.lazyModel = new FormatCustomLazy("VistaCostosDto", "destajistas", params, columns);
      this.attrs.put("general", this.toTotales("VistaCostosDto", "totales", params));
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
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
  		sb.append("(tc_keet_contratos.id_empresa=").append(((UISelectEntity)this.attrs.get("idEmpresa")).getKey()).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && ((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()>= 1L)
  		sb.append("(tc_keet_desarrollos.id_desarrollo=").append(((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idContrato")) && ((UISelectEntity)this.attrs.get("idContrato")).getKey()>= 1L)
  		sb.append("(tc_keet_contratos.id_contrato=").append(((UISelectEntity)this.attrs.get("idContrato")).getKey()).append(") and ");
		if(!Cadena.isVacio(this.fechaInicio))
		  sb.append("(date_format(tc_keet_contratos.registro, '%Y%m%d')>= '").append(this.fechaInicio.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.fechaTermino))
		  sb.append("(date_format(tc_keet_contratos.registro, '%Y%m%d')<= '").append(this.fechaTermino.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("idContratista")) && !this.attrs.get("idContratista").toString().equals("-1"))
      if(Objects.equals(((UISelectEntity)this.attrs.get("idContratista")).getKey(), Constantes.TOP_OF_ITEMS))
  		  regresar.put("contratistas", Constantes.SQL_FALSO);
      else
  		  regresar.put("contratistas", "tc_keet_contratos_lotes_contratistas.id_empresa_persona= "+ ((UISelectEntity)this.attrs.get("idContratista")).getKey());
    else
      if(!Cadena.isVacio(JsfBase.getParametro("idContratatista_input"))) {
        String codigo= (String)this.attrs.get("idContratatista_input");
        codigo= codigo.replaceAll(Constantes.CLEAN_SQL, "").trim().toUpperCase().replaceAll("(,| |\\t)+", ".*");
        regresar.put("contratistas", "(upper(concat(tc_mantic_personas.nombres, ' ', ifnull(tc_mantic_personas.paterno, ''), ' ', ifnull(tc_mantic_personas.materno, ''))) regexp '.*".concat(codigo).concat(".*' or upper(tc_mantic_personas.rfc) regexp '.*").concat(codigo).concat(".*') and "));
      } // if
      else
        regresar.put("contratistas", Constantes.SQL_VERDADERO);
    if(!Cadena.isVacio(this.attrs.get("idSubcontratista")) && !this.attrs.get("idSubcontratista").toString().equals("-1"))
      if(Objects.equals(((UISelectEntity)this.attrs.get("idSubcontratista")).getKey(), Constantes.TOP_OF_ITEMS))
  		  regresar.put("subContratistas", Constantes.SQL_FALSO);
      else
  		regresar.put("subContratistas", "tc_keet_contratos_lotes_proveedores.id_proveedor="+ ((UISelectEntity)this.attrs.get("idSubcontratista")).getKey());
    else
      if(!Cadena.isVacio(JsfBase.getParametro("idSubcontratatista_input"))) {
        String codigo= (String)this.attrs.get("idSubcontratatista_input");
  			codigo= codigo.replaceAll(Constantes.CLEAN_SQL, "").trim().toUpperCase().replaceAll("(,| |\\t)+", ".*");
		    regresar.put("subContratistas", "(tc_mantic_proveedores.razon_social regexp '.*".concat(codigo).concat(".*') and "));
      } // if
      else
  		  regresar.put("subContratistas", Constantes.SQL_VERDADERO);
    if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	}
	
	protected void toLoadCatalogos() throws Exception {
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
			columns.remove(0);
			this.doLoadDesarrollos();
   		Catalogos.toLoadContratistas(this.attrs, Boolean.TRUE);
      if(!Objects.equals((List<UISelectEntity>)this.attrs.get("contratistas"), null)) {
        UISelectEntity item= new UISelectEntity(Constantes.TOP_OF_ITEMS);
        item.addField("nombre", "NINGUNO");
        item.addField("nombres", "NINGUNO");
        ((List<UISelectEntity>)this.attrs.get("contratistas")).add(item);
      } // if
   		Catalogos.toLoadSubContratistas(this.attrs);
      if(!Objects.equals((List<UISelectEntity>)this.attrs.get("subContratistas"), null)) {
        UISelectEntity item= new UISelectEntity(Constantes.TOP_OF_ITEMS);
        item.addField("nombre", "NINGUNO");
        item.addField("razon_social", "NINGUNO");
        ((List<UISelectEntity>)this.attrs.get("subContratistas")).add(item);
      } // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
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

	public List<UISelectEntity> doCompleteProveedor(String nombreProveedor) {
		List<Columna> columns       = new ArrayList<>();
    Map<String, Object> params  = new HashMap<>();
		List<UISelectEntity> nombres= null;		
    try {
      columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));			
			nombreProveedor= !Cadena.isVacio(nombreProveedor) ? nombreProveedor.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim(): "WXYZ";		
			if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
				params.put("idEmpresa", this.attrs.get("idEmpresa"));
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales()); 
			params.put("razonSocial", nombreProveedor);	
      nombres= (List<UISelectEntity>) UIEntity.build("VistaProveedoresDto", "autoCompletar", params, columns, 20L);
      this.attrs.put("subcontratistas", nombres);
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
		return (List<UISelectEntity>)this.attrs.get("subcontratistas");
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
    regresar.put("contratos", new Value("contratos", 0D));
    regresar.put("costos", new Value("costos", 0D));
    regresar.put("destajos", new Value("destajos", 0D));
    return regresar;
  }

	@Override
	protected void finalize() throws Throwable {
    super.finalize();
	}	// finalize

}
