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
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.EFormatos;
import mx.org.kaana.kajool.procesos.reportes.beans.Modelo;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
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
import static mx.org.kaana.keet.nomina.reglas.Egresos.CAFU_PERSONAL_DIA;
import static mx.org.kaana.keet.nomina.reglas.Egresos.CAFU_PERSONAL_OBRA;
import static mx.org.kaana.keet.nomina.reglas.Egresos.GYLVI_PERSONAL_DIA;
import static mx.org.kaana.keet.nomina.reglas.Egresos.GYLVI_PERSONAL_OBRA;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.recurso.Configuracion;

@Named(value = "keetCostosPersonal")
@ViewScoped 
public class Personal extends IBaseFilter implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Personal.class);
  private static final long serialVersionUID= 8793667741599428332L;
  private static final String COLUMN_DATA_FILE_ESPECIAL= "EMPRESA,DESARROLLO,SEMANA,PUESTO,TIPO,NOMBRE,NOMINA,REGISTRO";  
  
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
      String salida  = EFormatos.XLS.toPath().concat(Archivo.toFormatNameFile(template).concat(".")).concat(EFormatos.XLS.name().toLowerCase());
      String fileName= JsfBase.getRealPath("").concat(salida);
      xls= new Xls(fileName, new Modelo(params, "VistaCostosDto", "detalles", template), COLUMN_DATA_FILE_ESPECIAL);	
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
    String personas= Numero.formatear(Numero.MILES_SIN_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("personas"));
    String total   = Numero.formatear(Numero.MILES_CON_DECIMALES, ((Entity)this.attrs.get("general")).toDouble("total"));
    return "Suma total: <strong>"+ total+ "</strong> | personas: <strong>"+ personas+ "</strong>";  
  }
  
  @PostConstruct
  @Override
  protected void init() {
    Map<String, Object> params= new HashMap<>();
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
      params.put("sortOrder", "order by tc_keet_nominas_desarrollos.id_desarrollo, tr_mantic_empresa_personal.id_empresa_persona");
      columns.add(new Columna("empresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("desarrollo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nomina", EFormatoDinamicos.MILES_CON_DECIMALES));
      this.lazyModel= new FormatCustomLazy("VistaCostosDto", "personal", params, columns);
      this.attrs.put("general", this.toTotales("VistaCostosDto", "resumen", params));
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
		if(!Cadena.isVacio(this.attrs.get("idDesarrollo")) && ((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()>= 1L)
      sb.append("(tc_keet_desarrollos.id_desarrollo=").append(((UISelectEntity)this.attrs.get("idDesarrollo")).getKey()).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("ejercicio")) && !Objects.equals((Long)this.attrs.get("ejercicio"), -1L))
		  sb.append("(tc_keet_nominas_periodos.ejercicio= '").append(this.attrs.get("ejercicio")).append("') and ");		
		if(!Cadena.isVacio(this.attrs.get("semana")) && !Objects.equals((Long)this.attrs.get("semana"), -1L))
		  sb.append("(tc_keet_nominas_periodos.id_nomina_periodo= ").append(this.attrs.get("semana")).append(") and ");		
		if(!Cadena.isVacio(this.attrs.get("idPuesto")) && ((UISelectEntity)this.attrs.get("idPuesto")).getKey()>= 1L)
      sb.append("(tc_mantic_puestos.id_puesto=").append(((UISelectEntity)this.attrs.get("idPuesto")).getKey()).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idPersona")) && !this.attrs.get("idPersona").toString().equals("-1"))
  		sb.append("(tc_mantic_personas.id_persona= ").append(this.attrs.get("idPersona")).append(") and ");
    else
      if(!Cadena.isVacio(JsfBase.getParametro("idPersona_input")) && !Objects.equals(JsfBase.getParametro("idPersona_input"), "-1")) { 
        String nombre= JsfBase.getParametro("idPersona_input").replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*");
        sb.append("(upper(concat(tc_mantic_personas.nombres, ' ', ifnull(tc_mantic_personas.paterno, ''), ' ', ifnull(tc_mantic_personas.materno, ''))) regexp '.*").append(nombre).append(".*' or upper(tc_mantic_personas.rfc) regexp '.*").append(nombre).append(".*') and ");
      } // if	
		if(!Cadena.isVacio(this.fechaInicio))
		  sb.append("(date_format(tc_keet_nominas.registro, '%Y%m%d')>= '").append(this.fechaInicio.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.fechaTermino))
		  sb.append("(date_format(tc_keet_nominas.registro, '%Y%m%d')<= '").append(this.fechaTermino.format(DateTimeFormatter.ofPattern("yyyyMMdd"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
  		sb.append("(tc_mantic_empresas.id_empresa=").append(((UISelectEntity)this.attrs.get("idEmpresa")).getKey()).append(") and ");
		else
  		sb.append("(tc_mantic_empresas.id_empresa in (").append(JsfBase.getAutentifica().getEmpresa().getSucursales()).append(")) and ");
    regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
    
    switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
      case "cafu":
        regresar.put("puestosPorDia", CAFU_PERSONAL_DIA);      
        regresar.put("puestosPorObra", CAFU_PERSONAL_OBRA);      
        break;
      case "gylvi":
        regresar.put("puestosPorDia", GYLVI_PERSONAL_DIA);      
        regresar.put("puestosPorObra", GYLVI_PERSONAL_OBRA);      
        break;
      case "triana":
        regresar.put("puestosPorDia", CAFU_PERSONAL_DIA);      
        regresar.put("puestosPorObra", CAFU_PERSONAL_OBRA);      
        break;
      default:  
        regresar.put("puestosPorDia", CAFU_PERSONAL_DIA);      
        regresar.put("puestosPorObra", CAFU_PERSONAL_OBRA);      
        break;
    } // switch
		if(!Cadena.isVacio(this.attrs.get("idTipo")) && !Objects.equals((Long)this.attrs.get("idTipo"), -1L))
		  if(Objects.equals((Long)this.attrs.get("idTipo"), 1L)) // POR EL DÍA
        regresar.put("puestosPorObra", "|-1|");
      else
        regresar.put("puestosPorDia", "|-1|");      
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
			this.doLoadDesarrollos();
      this.toLoadEjercicios();
      this.toLoadPuestos();
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
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} 
	
	private void toLoadEjercicios() {		
		List<UISelectItem> ejercicios= null;
		Map<String, Object>params    = new HashMap<>();
		try {						
			ejercicios= UISelect.seleccione("TcKeetNominasDto", "ejercicios", Collections.EMPTY_MAP, "ejercicio", EFormatoDinamicos.MAYUSCULAS);			
			this.attrs.put("ejercicios", ejercicios);
			this.attrs.put("ejercicio", UIBackingUtilities.toFirstKeySelectItem(ejercicios));
      this.doLoadSemanas();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally {
			Methods.clean(params);
		} // finally	
	}
  
	public void doLoadSemanas() {		
		List<UISelectItem> semanas= null;
		Map<String, Object>params = new HashMap<>();
		try {						
      params.put("ejercicio", this.attrs.get("ejercicio"));
			semanas= UISelect.seleccione("TcKeetNominasDto", "semanas", params, "orden", EFormatoDinamicos.MAYUSCULAS);
      if(semanas== null)
			  semanas= new ArrayList<>();
			this.attrs.put("semanas", semanas);
			this.attrs.put("semana", UIBackingUtilities.toFirstKeySelectItem(semanas));
      // this.fechaInicio= LocalDate.of(((Long)this.attrs.get("ejercicio")).intValue(), 1, 1);  
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally {
			Methods.clean(params);
		} // finally	
	} 

 	private void toLoadPuestos() {
    List<UISelectEntity> puestos= null;
		List<Columna> columns       = new ArrayList<>();
    Map<String, Object> params  = new HashMap<>();
    try {
      params.put(Constantes.SQL_CONDICION, "position(concat('|', tc_mantic_puestos.id_puesto, '|') in ('".concat(CAFU_PERSONAL_OBRA).concat(CAFU_PERSONAL_DIA).concat("'))> 0"));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      puestos= (List<UISelectEntity>) UIEntity.seleccione("TcManticPuestosDto", params, columns, "clave");
      this.attrs.put("puestos", puestos);			
			this.attrs.put("idPuesto", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("puestos")));			
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
    
  public List<UISelectEntity> doCompleteEmpleado(String query) {
    List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
      columns.add(new Columna("empleado", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("departamento", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("puesto", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("apodo", EFormatoDinamicos.MAYUSCULAS));
      params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
      params.put("nombre", query.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*"));
      this.attrs.put("empleados", UIEntity.build("VistaIngresosDto", "persona", params));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
    return (List<UISelectEntity>)this.attrs.get("empleados");
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
    regresar.addField("personas", 0D);
    regresar.addField("total", 0D);
    return regresar;
  }

}
