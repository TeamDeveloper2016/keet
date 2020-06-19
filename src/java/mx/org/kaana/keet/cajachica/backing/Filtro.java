package mx.org.kaana.keet.cajachica.backing;

import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
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

@Named(value = "keetCajaChicaFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 8793667741599428332L;	
	
	
  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());            
			this.loadCatalog();      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
 
	private void loadCatalog() {		
    try {			
			this.loadEmpresas();
			this.doLoadDesarrollos();
			this.loadEstatus();
			this.loadEjercicios();
    } // try
    catch (Exception e) {
      throw e;
    } // catch       
	} // loadCatalog
	
	private void loadEmpresas() {
		List<Columna> columns     = null;
    Map<String, Object> params= null;
    try {
			params= new HashMap<>();
			columns= new ArrayList<>();
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
	} // loadEmpresas	
	
	public void doLoadDesarrollos() {
		List<Columna> columns     = null;
    Map<String, Object> params= null;
		UISelectEntity empresa    = null;
    try {
			params= new HashMap<>();			
			empresa= (UISelectEntity) this.attrs.get("idEmpresa");
			if(empresa.getKey()>= 1L)
        params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa=" + empresa.getKey());
			else
				params.put(Constantes.SQL_CONDICION, "tc_mantic_clientes.id_empresa in (" + JsfBase.getAutentifica().getEmpresa().getSucursales() + ")");			
			columns= new ArrayList<>();
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
    }// finally
	} // loadEmpresas	
	
	private void loadEstatus(){		
		Map<String, Object>params    = null;
		List<UISelectItem> allEstatus= null;
		try {			
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			allEstatus= UISelect.seleccione("TcKeetCajasChicasCierresEstatusDto", "estatus", params, "nombre", EFormatoDinamicos.MAYUSCULAS);			
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
	} // doLoadEstatus
	
	private void loadEjercicios(){		
		List<UISelectItem> ejercicios= null;
		List<UISelectItem> semanas   = null;
		try {						
			ejercicios= UISelect.seleccione("TcKeetGastosDto", "ejercicios", Collections.EMPTY_MAP, "ejercicio", EFormatoDinamicos.MAYUSCULAS);			
			this.attrs.put("ejercicios", ejercicios);
			this.attrs.put("ejercicio", UIBackingUtilities.toFirstKeySelectItem(ejercicios));
			semanas= new ArrayList<>();
			for(int count=0; count<53; count++)
				semanas.add(new UISelectItem(new Long(count+1), "Semana ".concat(String.valueOf(count+1))));
			semanas.add(0, new UISelectItem(-1L, "SELECCIONE"));
			this.attrs.put("semanas", semanas);
			this.attrs.put("semana", UIBackingUtilities.toFirstKeySelectItem(semanas));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doLoadEstatus
	
  @Override
  public void doLoad() {
    List<Columna> columns     = null;
		Map<String, Object> params= null;
    try {			
      columns = new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("inicial", EFormatoDinamicos.NUMERO_CON_DECIMALES));
      columns.add(new Columna("gastado", EFormatoDinamicos.NUMERO_CON_DECIMALES));
      columns.add(new Columna("disponible", EFormatoDinamicos.NUMERO_CON_DECIMALES));            
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_HORA_CORTA));      
			params= this.toPrepare();
      params.put("sortOrder", "");
      this.lazyModel = new FormatCustomLazy("VistaCierresCajasChicasDto", params, columns);
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
		if(!Cadena.isVacio(this.attrs.get("fechaInicio")))
		  sb.append("(date_format(tc_keet_cajas_chicas_cierres.registro, '%Y%m%d')>= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaInicio"))).append("') and ");	
		if(!Cadena.isVacio(this.attrs.get("fechaTermino")))
		  sb.append("(date_format(tc_keet_cajas_chicas_cierres.termino, '%Y%m%d')<= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, (Date)this.attrs.get("fechaTermino"))).append("') and ");			
		if(!Cadena.isVacio(this.attrs.get("estatus")) && !this.attrs.get("estatus").toString().equals("-1"))
  		sb.append("(tc_keet_cajas_chicas_cierres.id_caja_chica_cierre_estatus= ").append(this.attrs.get("estatus")).append(") and ");
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !((UISelectEntity)this.attrs.get("idEmpresa")).getKey().equals(-1L))
		  sb.append("(tc_mantic_clientes.id_empresa= ").append(this.attrs.get("idEmpresa")).append(") and ");		
		if(!Cadena.isVacio(this.attrs.get("desarrollo")) && !((UISelectEntity)this.attrs.get("desarrollo")).getKey().equals(-1L))
		  sb.append("(tc_keet_desarrollos.id_desarrollo= ").append(((UISelectEntity)this.attrs.get("desarrollo")).getKey()).append(") and ");		
		if(!Cadena.isVacio(this.attrs.get("ejercicio")) && !this.attrs.get("ejercicio").equals("-1"))
		  sb.append("(tc_keet_cajas_chicas_cierres.ejercicio= '").append(this.attrs.get("ejercicio")).append("') and ");		
		if(!Cadena.isVacio(this.attrs.get("semana")) && !this.attrs.get("semana").equals("-1"))
		  sb.append("(tc_keet_nominas_periodos.orden= ").append(this.attrs.get("semana")).append(") and ");		
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	} // toPrepare

  public String doAperturarCaja() {
		try {
			JsfBase.setFlashAttribute("abonar", EAccion.REGISTRAR);
	  } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
		} // catch
		return "abonar".concat(Constantes.REDIRECIONAR);
	} // doAperturarCaja
	
	public String toColor(Entity row) {
		return row.toDouble("efectivo")> row.toDouble("limite")? "janal-tr-orange": "";
	} 
}