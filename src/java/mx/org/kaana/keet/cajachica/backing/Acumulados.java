package mx.org.kaana.keet.cajachica.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;

@Named(value = "keetCajaChicaAcumulados")
@ViewScoped
public class Acumulados extends IBaseFilter implements Serializable {

  private static final long serialVersionUID= 8793667741599428331L;	

	private LocalDate fechaInicio;
	private LocalDate fechaFin;

	public LocalDate getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(LocalDate fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public LocalDate getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(LocalDate fechaFin) {
		this.fechaFin = fechaFin;
	}
	
  @PostConstruct
  @Override
  protected void init() {
    try {
			this.fechaInicio= LocalDate.of(Fecha.getAnioActual()- 1, Fecha.getMesActual()+ 1, 1);
      this.fechaFin   = LocalDate.of(Fecha.getAnioActual(), Fecha.getMesActual()+ 1, 1);
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());   
			this.toLoadCatalogos();      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 
 
	private void toLoadCatalogos() {		
    try {			
			this.toLoadEmpresas();
			this.doLoadDesarrollos();
			this.toLoadEstatus();
    } // try
    catch (Exception e) {
      throw e;
    } // catch       
	} 
	
	private void toLoadEmpresas() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
    try {
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));			
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));			
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
			this.attrs.put("desarrollo", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("desarrollos")));			
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} 	
	
	private void toLoadEstatus() {		
		Map<String, Object>params    = new HashMap<>();
		List<UISelectItem> allEstatus= null;
		try {			
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			allEstatus= UISelect.seleccione("TcKeetGastosEstatusDto", "estatus", params, "nombre", EFormatoDinamicos.MAYUSCULAS);			
			this.attrs.put("allEstatus", allEstatus);
			this.attrs.put("estatus", UIBackingUtilities.toFirstKeySelectItem(allEstatus));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} 
	
  @Override
  public void doLoad() {
    List<Columna> columns     = new ArrayList<>();
		Map<String, Object> params= null;
    try {			
      columns.add(new Columna("consecutivo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("desarrollo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("residente", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("estatus", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("articulos", EFormatoDinamicos.MILES_SIN_DECIMALES));
      columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("fecha", EFormatoDinamicos.FECHA_CORTA));      
			params= this.toPrepare();
      params.put("sortOrder", "order by tc_keet_desarrollos.nombres");
      this.lazyModel = new FormatCustomLazy("VistaCierresCajasChicasDto", "particular", params, columns);
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
		if(!Cadena.isVacio(this.fechaInicio))
	  	sb.append("(date_format(tc_keet_cajas_chicas_cierres.registro, '%Y%m%d')>= date_format('").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, this.fechaInicio)).append("', '%Y%m%d')) and ");			
		if(!Cadena.isVacio(this.fechaFin))
  		sb.append("(date_format(tc_keet_cajas_chicas_cierres.termino, '%Y%m%d')<= date_format('").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, this.fechaFin)).append("', '%Y%m%d')) and ");			
		if(!Cadena.isVacio(this.attrs.get("estatus")) && !this.attrs.get("estatus").toString().equals("-1"))
  		sb.append("(tc_keet_gastos_estatus.id_gasto_estatus= ").append(this.attrs.get("estatus")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !((UISelectEntity)this.attrs.get("idEmpresa")).getKey().equals(-1L))
		  sb.append("(tc_mantic_clientes.id_empresa= ").append(this.attrs.get("idEmpresa")).append(") and ");		
		if(!Cadena.isVacio(this.attrs.get("desarrollo")) && !((UISelectEntity)this.attrs.get("desarrollo")).getKey().equals(-1L))
		  sb.append("(tc_keet_desarrollos.id_desarrollo= ").append(((UISelectEntity)this.attrs.get("desarrollo")).getKey()).append(") and ");		
		if(!Cadena.isVacio(this.attrs.get("nombre")) && !this.attrs.get("nombre").equals("-1"))
		  sb.append("(tc_keet_gastos.id_empresa_persona= ").append(this.attrs.get("nombre")).append(") and ");
    else
      if(!Cadena.isVacio(JsfBase.getParametro("nombre_input"))) {
        String codigo= (String)this.attrs.get("nombre_input");
  			codigo= codigo.replaceAll(Constantes.CLEAN_SQL, "").trim().toUpperCase().replaceAll("(,| |\\t)+", ".*");
		    sb.append("(upper(concat(tc_mantic_personas.nombres, ' ', ifnull(tc_mantic_personas.paterno, ''), ' ', ifnull(tc_mantic_personas.materno, ''))) regexp '.*").append(codigo).append(".*' or upper(tc_mantic_personas.rfc) regexp '.*").append(codigo).append(".*') and ");
      } // if
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	} 
 
	public List<UISelectEntity> doCompleteNombreEmpleado(String nombreEmpleado) {
		List<Columna> columns        = new ArrayList<>();
    Map<String, Object> params   = new HashMap<>();
		List<UISelectEntity> personas= null;		
    try {
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));			
      if(Cadena.isVacio(nombreEmpleado))
        nombreEmpleado= "WXYZ";
      else 
  			nombreEmpleado= nombreEmpleado.toUpperCase().replaceAll(Constantes.CLEAN_SQL, "").trim().replaceAll("(,| |\\t)+", ".*.*");
			if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
				params.put("idEmpresa", this.attrs.get("idEmpresa"));
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales()); 
			params.put("nombreEmpleado", nombreEmpleado);	
      personas= (List<UISelectEntity>) UIEntity.build("VistaPersonasDto", "autoCompletar", params, columns, 20L);
      this.attrs.put("nombres", personas);
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
    return (List<UISelectEntity>)this.attrs.get("nombres");
	}	

  public void doLoadNombres() {
    UISelectEntity persona= (UISelectEntity)this.attrs.get("nombre");  
    this.attrs.put("nombre", new UISelectEntity(-1L));
    if(persona!= null) {
      List<UISelectEntity> personas= (List<UISelectEntity>)this.attrs.get("nombres");
      if(personas!= null && !persona.isEmpty()) {
        int index= personas.indexOf(persona);
        if(index>= 0) 
          this.attrs.put("nombre", personas.get(index));
      } // if
    } // if
  }
  
}