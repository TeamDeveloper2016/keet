package mx.org.kaana.keet.cajachica.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.keet.enums.EEstatusGastos;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCajaChicaConsulta")
@ViewScoped
public class Consulta extends IBaseFilter implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;				
	private List<Entity> gastos;	
	private List<Entity> acumulados;	

	public List<Entity> getGastos() {
		return gastos;
	}

  public List<Entity> getAcumulados() {
    return acumulados;
  }

  @PostConstruct
  @Override
  protected void init() {		
    EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;				
    try {			
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");						
			this.attrs.put("opcionResidente", opcion);			
			this.attrs.put("idDesarrollo", idDesarrollo);
			this.attrs.put("disponibles", JsfBase.getFlashAttribute("disponibles"));			
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));			
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());				      
			this.loadCatalogos();
			this.doLoad();
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void loadCatalogos() throws Exception {
		Map<String, Object>params= new HashMap<>();
		Entity desarrollo        = null;				
		try {
			params.put("operador", "<=");
      params.put("idContratoEstatus", EContratosEstatus.TERMINADO.getKey());
			params.put(Constantes.SQL_CONDICION, "tc_keet_desarrollos.id_desarrollo=".concat(this.attrs.get("idDesarrollo").toString()));
			desarrollo= (Entity) DaoFactory.getInstance().toEntity("VistaDesarrollosDto", "lazy", params);
			this.attrs.put("desarrollo", desarrollo);						
			loadCortes();							
		} // try
    finally {
			Methods.clean(params);
		} // finally
	} // loadCatalogos			
	
	private void loadCortes() throws Exception{
		List<UISelectEntity>cortes= null;
		Map<String, Object>params = null;
		List<Columna>campos       = null;
		try {
			params= new HashMap<>();
			campos= new ArrayList<>();
			campos.add(new Columna("disponible", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			campos.add(new Columna("acumulado", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			campos.add(new Columna("saldo", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			cortes= UIEntity.seleccione("VistaGastosDto", "cortes", params, campos, 30L, "consecutivo");
			this.attrs.put("cortes", cortes);
			this.attrs.put("corte", UIBackingUtilities.toFirstKeySelectEntity(cortes));
			loadCajaChicaDefault();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
			Methods.clean(campos);
		} // finally
	} // loadCortes
	
	private void loadCajaChica() throws Exception{
		Map<String, Object>params= null;
		Entity cajaChica         = null;		
		List<Columna>campos      = null;
    try {    			
			campos= new ArrayList<>();
			campos.add(new Columna("saldo", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			campos.add(new Columna("acumulado", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			campos.add(new Columna("disponible", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			campos.add(new Columna("pendiente", EFormatoDinamicos.NUMERO_CON_DECIMALES));			
			params= new HashMap<>();
			params.put("idCajaChica", ((Entity)this.attrs.get("corte")).getKey());
			params.put("idDesarrollo", this.attrs.get("idDesarrollo").toString());
			cajaChica= (Entity) DaoFactory.getInstance().toEntity("VistaGastosDto", "findDesarrolloCajaChica", params);
			UIBackingUtilities.toFormatEntity(cajaChica, campos);
			this.attrs.put("cajaChica", cajaChica);		
		} // try
		catch (Exception e) {			
			throw e;  
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // loadCajaChica
	
	private void loadCajaChicaDefault() throws Exception{
		Map<String, Object>params= null;
		Entity cajaChica         = null;		
		List<Columna>campos      = null;
    try {    			
			campos= new ArrayList<>();
			campos.add(new Columna("saldo", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			campos.add(new Columna("acumulado", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			campos.add(new Columna("disponible", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			campos.add(new Columna("pendiente", EFormatoDinamicos.NUMERO_CON_DECIMALES));			
			params= new HashMap<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo").toString());
			cajaChica= (Entity) DaoFactory.getInstance().toEntity("VistaCajaChicaDto", "findDesarrollo", params);
			UIBackingUtilities.toFormatEntity(cajaChica, campos);
			this.attrs.put("cajaChica", cajaChica);		
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	} // loadCajaChica
	
	public void doLoadGasto() throws Exception {		
		this.attrs.put("conceptoCriterio", new UISelectEntity(-1L));
		this.attrs.put("corte", new UISelectEntity(-1L));
		loadCajaChicaDefault();
		doLoad();
	} // doLoadIndi
	
	public void doLoadConcepto() throws Exception {
		this.attrs.put("gastoCriterio", new UISelectEntity(-1L));
		this.attrs.put("corte", new UISelectEntity(-1L));
		loadCajaChicaDefault();
		doLoad();
	} // doLoadIndi
	
	public void doLoadCorte() throws Exception {
		this.attrs.put("gastoCriterio", new UISelectEntity(-1L));
		this.attrs.put("conceptoCriterio", new UISelectEntity(-1L));
		if(((Entity)this.attrs.get("corte")).getKey()<= 0)
			loadCajaChicaDefault();
		else
			loadCajaChica();
		doLoad();
	} // doLoadIndi
	
  @Override
  public void doLoad() {
		Map<String, Object>params= null;		
		List<Columna> campos     = null;
    try {   						      		
			campos= new ArrayList<>();
			campos.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
			campos.add(new Columna("importe", EFormatoDinamicos.MILES_CON_DECIMALES));
			params= this.toPrepare();										
			params.put("idCajaChica", ((Entity)this.attrs.get("cajaChica")).getKey());
			this.gastos= DaoFactory.getInstance().toEntitySet("VistaGastosDto", "gastos", params);				
			this.attrs.put("totalRegistros", this.gastos.size());			
			if(!this.gastos.isEmpty()) { 
				UIBackingUtilities.toFormatEntitySet(this.gastos, campos);
				this.toEstatusVale();
			} // if
			this.acumulados= DaoFactory.getInstance().toEntitySet("VistaGastosDto", "acumulados", params);				
      campos.clear();
			campos.add(new Columna("gastos", EFormatoDinamicos.MILES_SIN_DECIMALES));
			campos.add(new Columna("total", EFormatoDinamicos.MILES_CON_DECIMALES));
			campos.add(new Columna("cancelado", EFormatoDinamicos.MILES_CON_DECIMALES));
			if(!this.acumulados.isEmpty()) { 
				UIBackingUtilities.toFormatEntitySet(this.acumulados, campos);
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {      
      Methods.clean(params);
      Methods.clean(campos);
    } // finally		
  } // doLoad			
	
	private Map<String, Object> toPrepare(){
		List<UISelectEntity> conceptos= null;
		UISelectEntity concepto       = null;
		Map<String, Object>regresar   = null;		
		StringBuilder condicion       = null;
		try {
			regresar= new HashMap<>();			
			condicion= new StringBuilder();	
			if(!Cadena.isVacio(this.attrs.get("disponibles")) && Boolean.valueOf(this.attrs.get("disponibles").toString())){
				condicion.append("tc_keet_gastos.id_gasto_estatus=").append(EEstatusGastos.DISPONIBLE.getKey()).append(" and ");
				this.attrs.put("disponibles", false);
			} // if
			if(((UISelectEntity)this.attrs.get("gastoCriterio"))!= null && ((UISelectEntity)this.attrs.get("gastoCriterio")).getKey()>= 1L)
				condicion.append("tc_keet_gastos.id_gasto=").append(((UISelectEntity)this.attrs.get("gastoCriterio")).getKey()).append(" and ");
			if(((UISelectEntity)this.attrs.get("conceptoCriterio"))!= null && ((UISelectEntity)this.attrs.get("conceptoCriterio")).getKey()>= 1L){
				conceptos= (List<UISelectEntity>) this.attrs.get("conceptos");
				concepto= (UISelectEntity) this.attrs.get("conceptoCriterio");
				condicion.append("tc_keet_gastos_detalles.nombre like '%").append(conceptos.get(conceptos.indexOf(concepto)).toString("nombre")).append("%' and ");			
			} // if
			regresar.put(Constantes.SQL_CONDICION, Cadena.isVacio(condicion) ? Constantes.SQL_VERDADERO : condicion.substring(0, condicion.length()-4));						
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		return regresar;
	} // toPrepare	
	
	private void toEstatusVale() throws Exception {		
		for(Entity gasto: this.gastos){
			gasto.put("iconEstatus", new Value("iconEstatus", EEstatusGastos.fromId(gasto.toLong("idGastoEstatus")).getSemaforo()));				
			gasto.put("iconEstatusDes", new Value("iconEstatusDes", EEstatusGastos.fromId(gasto.toLong("idGastoEstatus")).getNombre()));				
		} // for		
	} // toEstatusManzanaLote	
	
	public String doResumen() {
    String regresar    = null;    			
		Entity seleccionado= null;
    try {												
			seleccionado= ((Entity)this.attrs.get("seleccionado"));
			JsfBase.setFlashAttribute("opcionResidente", (EOpcionesResidente)this.attrs.get("opcionResidente"));																		
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));													
			JsfBase.setFlashAttribute("idGasto", seleccionado.getKey());										
			JsfBase.setFlashAttribute("consecutivo", seleccionado.toString("consecutivo"));										
			JsfBase.setFlashAttribute("retorno", "consulta");				
			if(seleccionado.toLong("idGastoEstatus").equals(EEstatusGastos.DISPONIBLE.getKey()))
				regresar= "accion".concat(Constantes.REDIRECIONAR);
			else
				regresar= "resumen".concat(Constantes.REDIRECIONAR);										
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doEntrega		
	
	public String doConsultar() {    
    doResumen();
    return "importar".concat(Constantes.REDIRECIONAR);
  } // doConsultar
	
	public String doCancelar() {
    String regresar          = null;    
		EOpcionesResidente opcion= null;		
    try {			
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("idDesarrolloProcess", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("opcion", opcion);						
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));			
			JsfBase.setFlashAttribute("opcionResidente", opcion);							
			if(Cadena.isVacio(this.attrs.get("retorno")))
				regresar= opcion.getRetorno().concat(Constantes.REDIRECIONAR_AMPERSON);			
			else
				regresar= this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
	
	public List<UISelectEntity> doCompleteGasto(String gasto) {
		List<UISelectEntity> regresar= null;		
		Map<String, Object> params   = null;
		List<Columna> campos         = null;		
		try {			
			campos= new ArrayList<>();
			campos.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
			campos.add(new Columna("importe", EFormatoDinamicos.NUMERO_CON_DECIMALES));
			params= new HashMap<>();
			params.put("consecutivo", gasto);
			params.put("idCajaChica", ((Entity)this.attrs.get("cajaChica")).getKey());			
			regresar= UIEntity.build("VistaGastosDto", "gastosConsecutivo", params, campos, 30L);						
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
			Methods.clean(campos);
		} // finally
		return regresar;
	} // doCompleteGasto	
	
	public List<UISelectEntity> doCompleteConcepto(String concepto){
		List<UISelectEntity> regresar= null;		
		Map<String, Object> params   = null;
		List<Columna> campos         = null;		
		try {			
			campos= new ArrayList<>();
			campos.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));			
			params= new HashMap<>();
			params.put("nombre", concepto);			
			regresar= UIEntity.build("VistaGastosDto", "conceptos", params, campos, 30L);						
			this.attrs.put("conceptos", regresar);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
			Methods.clean(campos);
		} // finally
		return regresar;
	} // doCompleteVale	
}