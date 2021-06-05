package mx.org.kaana.keet.cajachica.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.cajachica.reglas.Transaccion;
import mx.org.kaana.keet.enums.EEstatusCajasChicas;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;

@Named(value = "keetCajaChicaPagar")
@ViewScoped
public class Pagar extends IBaseFilter implements Serializable {

	private static final long serialVersionUID= 2847354766000406350L; 
  
	protected FormatLazyModel lazyModelGasto;
	protected FormatLazyModel lazyModelResidentes;
  private String costoTotal;

  public FormatLazyModel getLazyModelGasto() {
    return lazyModelGasto;
  }

  public FormatLazyModel getLazyModelResidentes() {
    return lazyModelResidentes;
  }

  public String getCostoTotal() {
    return this.costoTotal;
//    Double costo = 0D;
//		if(this.lazyDestajo!= null)
//			for (IBaseDto item: (List<IBaseDto>)this.lazyDestajo.getWrappedData()) {
//				Entity row= (Entity)item;
//				costo+= new Double(row.toString("total"));
//			} // for	
//		return Pagar.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, costo);
	}  
  
  @PostConstruct
  @Override
  protected void init() {		
    try {
			this.attrs.put("isAdmin", JsfBase.isAdminEncuestaOrAdmin());						
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
			this.loadEjercicios();
			this.doLoad();											
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
  
  @Override
  public void doLoad() {			
		Map<String, Object>params= new HashMap<>();
    List<Columna> columns    = null;
    try {			
      columns = new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("inicial", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("gastado", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("disponible", EFormatoDinamicos.MILES_CON_DECIMALES));            
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_HORA_CORTA));      
      params.put("sortOrder", "order by tc_keet_desarrollos.nombres");
      if(!Cadena.isVacio(this.attrs.get("semana")))
        params.put(Constantes.SQL_CONDICION, "tc_keet_nominas_periodos.id_nomina_periodo= "+ this.attrs.get("semana")+ " and tc_keet_cajas_chicas_cierres.acumulado> 0");
      else  
        params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);      
      this.lazyModel = new FormatCustomLazy("VistaCierresCajasChicasDto", params, columns);
      UIBackingUtilities.resetDataTable();	
      Entity costo= (Entity)DaoFactory.getInstance().toEntity("VistaCierresCajasChicasDto", "reponer", params);
      if(costo!= null && !costo.isEmpty())
        this.costoTotal= Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, costo.toDouble("total"));
      this.toLoadGastosResidente();
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

	private void loadEjercicios() {		
		List<UISelectItem> ejercicios= null;
		List<UISelectItem> semanas   = null;
		Map<String, Object>params    = new HashMap<>();
		try {						
			ejercicios= UISelect.build("TcKeetGastosDto", "ejercicios", Collections.EMPTY_MAP, "ejercicio", EFormatoDinamicos.MAYUSCULAS);			
			this.attrs.put("ejercicios", ejercicios);
			this.attrs.put("ejercicio", UIBackingUtilities.toFirstKeySelectItem(ejercicios));
			semanas= new ArrayList<>();
      if(ejercicios!= null && ejercicios.size()> 0) {
        params.put("ejercicio", ejercicios.get(0).getValue());
  			semanas= UISelect.build("VistaCierresCajasChicasDto", "semanas", params, "orden", EFormatoDinamicos.MAYUSCULAS);
      } // if  
			this.attrs.put("semanas", semanas);
			this.attrs.put("semana", UIBackingUtilities.toFirstKeySelectItem(semanas));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
		finally {
			Methods.clean(params);
		} // finally	
	} // loadEjercicios
  
	public String doAceptar() {
    String regresar                = null;    		
		Transaccion transaccion        = null;	
		UISelectEntity residente       = null;
		UISelectEntity seleccionado    = null;
		List<UISelectEntity> residentes= null;
    try {									
			seleccionado= (UISelectEntity) this.attrs.get("residente");
			residentes= (List<UISelectEntity>) this.attrs.get("residentes");
			residente= residentes.get(residentes.indexOf(seleccionado));
			transaccion= new Transaccion(Long.valueOf(this.attrs.get("idCajaChicaCierre").toString()), Double.valueOf(this.attrs.get("importe").toString()), this.attrs.get("observaciones").toString(), Long.valueOf(this.attrs.get("idAfectaNomina").toString()), (Long)this.attrs.get("idDesarrollo"), residente.toLong("idEmpresaPersona"));
			if(transaccion.ejecutar(EAccion.ACTIVAR)){
				JsfBase.addMessage("Cierre de caja chica", "Se realizó el cierre de caja chica de forma correcta.", ETipoMensaje.INFORMACION);									
				regresar= "filtro".concat(Constantes.REDIRECIONAR);
			} // if
			else
				JsfBase.addMessage("Cierre de caja chica", "Ocurrió un error al realizar el cierre de caja chica.", ETipoMensaje.ERROR);						
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina  	
		
	public String doCancelar() {
    String regresar= null;    		
    try {									
			regresar= "filtro".concat(Constantes.REDIRECIONAR);						
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
 
	public String toColor(Entity row) {
		return EEstatusCajasChicas.fromId(row.toLong("idCajaChicaCierreEstatus")).getSemaforo();
	} // toColor
 
	public void onRowToggle(ToggleEvent event) {
		Map<String, Object>params = new HashMap<>();
    List<Columna> columns     = null;
    try {
      Entity seleccionado= (Entity) event.getData();
			this.attrs.put("seleccionado", seleccionado);
			if (!event.getVisibility().equals(Visibility.HIDDEN)) {
        columns = new ArrayList<>();
        columns.add(new Columna("residente", EFormatoDinamicos.MAYUSCULAS));
        columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
        columns.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
        params.put("sortOrder", "order by tc_mantic_personas.nombres");
        params.put("idNominaPeriodo", Cadena.isVacio(this.attrs.get("semana"))? -1L: (Long)this.attrs.get("semana"));      
        // params.put("idNominaPeriodo", seleccionado.toLong("idNominaPeriodo"));      
        this.lazyModelGasto= new FormatCustomLazy("VistaCierresCajasChicasDto", "detalle", params, columns);
        UIBackingUtilities.resetDataTable("tablaDetalle");			        
      } // if
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally	
	} // onRowToggle
 
	private void toLoadGastosResidente() {
		Map<String, Object>params = new HashMap<>();
    List<Columna> columns     = null;
    try {
      columns = new ArrayList<>();
      columns.add(new Columna("residente", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
      params.put("sortOrder", "order by tr_mantic_empresa_personal.id_empresa_persona");
      params.put("idNominaPeriodo", Cadena.isVacio(this.attrs.get("semana"))? -1L: (Long)this.attrs.get("semana"));      
      this.lazyModelResidentes= new FormatCustomLazy("VistaCierresCajasChicasDto", "residentes", params, columns);
      UIBackingUtilities.resetDataTable("tablaResidentes");			        
		} // try
		catch (Exception e) {			
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally	
	} // toLoadGastosResidente
 
}