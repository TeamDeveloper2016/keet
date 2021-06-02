package mx.org.kaana.keet.cajachica.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.cajachica.reglas.Transaccion;
import mx.org.kaana.keet.enums.EEstatusCajasChicas;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;

@Named(value = "keetCajaChicaGlobal")
@ViewScoped
public class Global extends IBaseFilter implements Serializable {

	private static final long serialVersionUID= 2847354766000406350L; 
  
	protected FormatLazyModel lazyModelGasto;

  public FormatLazyModel getLazyModelGasto() {
    return lazyModelGasto;
  }
  
  @PostConstruct
  @Override
  protected void init() {		
    try {
			this.attrs.put("isAdmin", JsfBase.isAdminEncuestaOrAdmin());						
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
			// this.loadResidentes();
			this.doLoad();											
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
  
	private void loadResidentes() {
		List<UISelectEntity> residentes= null;
		List<Columna> campos           = null;
		Map<String, Object> params     = null;
		try {
			params= new HashMap<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			campos= new ArrayList<>();		
			campos.add(new Columna("nombreCompleto", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("departamento", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("puesto", EFormatoDinamicos.MAYUSCULAS));
			// residentes= DaoFactori.seleccione("", "", params, campos, Constantes.SQL_TODOS_REGISTROS, "departamento");
			this.attrs.put("residentes", residentes);
			this.attrs.put("residente", UIBackingUtilities.toFirstKeySelectEntity(residentes));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // loadResidentes
	
  @Override
  public void doLoad() {			
		Map<String, Object>params = new HashMap<>();
    List<Columna> columns     = null;
    try {			
      columns = new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("inicial", EFormatoDinamicos.NUMERO_CON_DECIMALES));
      columns.add(new Columna("gastado", EFormatoDinamicos.NUMERO_CON_DECIMALES));
      columns.add(new Columna("disponible", EFormatoDinamicos.NUMERO_CON_DECIMALES));            
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_HORA_CORTA));      
      params.put("sortOrder", "");
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);      
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
        columns.add(new Columna("importe", EFormatoDinamicos.NUMERO_CON_DECIMALES));
        columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
        params.put("sortOrder", "");
        params.put("idNominaPeriodo", 73);      
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
  
}