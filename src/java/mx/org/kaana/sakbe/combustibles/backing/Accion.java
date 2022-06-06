package mx.org.kaana.sakbe.combustibles.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.reglas.Transaccion;
import mx.org.kaana.mantic.comun.IBaseStorage;
import mx.org.kaana.mantic.enums.ETipoMediosPago;
import mx.org.kaana.sakbe.combustibles.beans.Combustible;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.TabChangeEvent;


@Named(value= "sakbeComprasAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Accion.class);
  private static final long serialVersionUID= 327393488565639367L;
	
  private EAccion accion;
  private Combustible combustible;

	public String getAgregar() {
		return this.accion.equals(EAccion.AGREGAR)? "none": "";
	}

  public Combustible getCombustible() {
    return combustible;
  }

  public void setCombustible(Combustible combustible) {
    this.combustible = combustible;
  }
	
	@PostConstruct
  @Override
  protected void init() {		
    try {
      this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.attrs.put("idCombustible", JsfBase.getFlashAttribute("idCombustible")== null? -1L: JsfBase.getFlashAttribute("idCombustible"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("isBanco", Boolean.FALSE);
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  public void doLoad() {
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      switch (this.accion) {
        case AGREGAR:											
          this.combustible= new Combustible(-1L);
    			this.attrs.put("sinIva", false);
          break;
        case MODIFICAR:			
        case CONSULTAR:											
          this.combustible= null;
          break;
      } // switch
			this.toLoadCatalog();
      this.toLoadBancos();
      this.toLoadTiposMediosPagos();
      this.toLoadTiposCombustibles();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad  

	private void toLoadCatalog() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("empresas", (List<UISelectEntity>) UIEntity.build("TcManticEmpresasDto", "empresas", params, columns));
 			List<UISelectEntity> empresas= (List<UISelectEntity>)this.attrs.get("empresas");
			if(!empresas.isEmpty()) {
				this.attrs.put("idPedidoSucursal", empresas.get(0));
				if(this.accion.equals(EAccion.AGREGAR))
  				this.combustible.setIkEmpresa(empresas.get(0));
			  else 
				  this.combustible.setIkEmpresa(empresas.get(empresas.indexOf(this.combustible.getIkEmpresa())));
			} // if	
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} // toLoadCatalog

	public void doTabChange(TabChangeEvent event) {
    switch (event.getTab().getTitle()) {
      case "General":
        break;
      default:
        break;
    }
	} // doTabChange
  
	public String doAceptar() {  
    Transaccion transaccion = null;
    String regresar         = null;
    try {			
			// transaccion = new Transaccion(this.combustible);
			if (transaccion.ejecutar(this.accion)) {
				if(this.accion.equals(EAccion.AGREGAR)) {
    			regresar= this.doCancelar();
    			UIBackingUtilities.execute("jsArticulos.back('gener\\u00F3 el ticket de compra', '"+ this.combustible.getConsecutivo()+ "');");
				} // if	
				else
 				  if(!this.accion.equals(EAccion.CONSULTAR)) 
    			  JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el ticket de compra"), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el ticket de compra.", ETipoMensaje.ALERTA);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idCombustible", this.combustible.getIdCombustible());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar
 
	private void toLoadTiposMediosPagos() {
		List<UISelectEntity> tiposMediosPagos= null;
		Map<String, Object>params            = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_cobro_caja=1");
			tiposMediosPagos= UIEntity.build("TcManticTiposMediosPagosDto", "row", params);
			this.attrs.put("tiposMediosPagos", tiposMediosPagos);
      if(!tiposMediosPagos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          this.combustible.setIkTipoMedioPago(tiposMediosPagos.get(0));
        else  
          this.combustible.setIkTipoMedioPago(tiposMediosPagos.get(tiposMediosPagos.indexOf(this.combustible.getIkTipoMedioPago())));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadTiposMediosPagos
  
	private void toLoadBancos() {
		List<UISelectEntity> bancos= null;
		Map<String, Object> params = null;
		List<Columna> columns      = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			columns= new ArrayList<>();
			columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			bancos= UIEntity.seleccione("TcManticBancosDto", "row", params, columns, Constantes.SQL_TODOS_REGISTROS, "nombre");
			this.attrs.put("bancos", bancos);
      if(!bancos.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          this.combustible.setIkBanco(bancos.get(0));
        else  
          this.combustible.setIkBanco(bancos.get(bancos.indexOf(this.combustible.getIkBanco())));
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadBancos  
  
	public void doCheckTipoMedioPago() {
		Long tipoMedioPago= null;
		try {
      UIBackingUtilities.execute(
        "janal.renovate('contenedorGrupos\\\\:idBanco', {validaciones: 'libre', mascara: 'libre'});"
      );		
			tipoMedioPago= this.combustible.getIdTipoMedioPago();
			this.attrs.put("isBanco", !ETipoMediosPago.EFECTIVO.getIdTipoMedioPago().equals(tipoMedioPago));
      if(!ETipoMediosPago.EFECTIVO.getIdTipoMedioPago().equals(tipoMedioPago)) 
        UIBackingUtilities.execute(
          "janal.renovate('contenedorGrupos\\\\:idBanco', {validaciones: 'requerido', mascara: 'libre'});"
        );		
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doCheckTipoMedioPago
 
	private void toLoadTiposCombustibles() {
		List<UISelectEntity> tiposCombustibles= null;
		Map<String, Object>params             = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			tiposCombustibles= UIEntity.build("TcSakbeTiposCombustiblesDto", "row", params);
			this.attrs.put("tiposCombustibles", tiposCombustibles);
      if(!tiposCombustibles.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          this.combustible.setIkTipoCombustible(tiposCombustibles.get(0));
        else  
          this.combustible.setIkTipoCombustible(tiposCombustibles.get(tiposCombustibles.indexOf(this.combustible.getIkTipoCombustible())));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // toLoadTiposCombustibles  
  
	public void doAutoSaveOrden() {
	  this.toSaveRecord();	
	} // doAutoSaveOrden
	
	@Override
	public void toSaveRecord() {
    Transaccion transaccion= null;
    try {			
			// transaccion = new Transaccion(this.combustible);
			if (transaccion.ejecutar(EAccion.MOVIMIENTOS)) {
   			UIBackingUtilities.execute("jsArticulos.back('guard\\u00F3 orden de compra', '"+ this.combustible.getConsecutivo()+ "');");
				this.accion= EAccion.MODIFICAR;
				this.attrs.put("autoSave", Global.format(EFormatoDinamicos.FECHA_HORA, Fecha.getRegistro()));
			} // if	
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
	} // toSaveRecord

	public void doGlobalEvent(Boolean isViewException) {
		LOG.error("ESTO ES UN MENSAJE GLOBAL INVOCADO POR UNA EXCEPCION QUE NO FUE ATRAPADA");
		if(isViewException && this.combustible!= null && this.combustible.isComplete())
		  this.toSaveRecord();
	} // doGlobalEvent
  
}