package mx.org.kaana.sakbe.catalogos.insumos.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.sakbe.catalogos.insumos.reglas.Transaccion;
import mx.org.kaana.mantic.comun.IBaseStorage;
import mx.org.kaana.mantic.inventarios.comun.IBaseImportar;
import mx.org.kaana.sakbe.catalogos.insumos.beans.TipoCombustible;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.TabChangeEvent;


@Named(value= "sakbeCatalogosInsumosAccion")
@ViewScoped
public class Accion extends IBaseImportar implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Accion.class);
  private static final long serialVersionUID= 327393484565639367L;
	
  private EAccion accion;
  private TipoCombustible combustible;
  
	public String getAgregar() {
		return this.accion.equals(EAccion.AGREGAR)? "none": "";
	}

  public TipoCombustible getCombustible() {
    return combustible;
  }

  public void setCombustible(TipoCombustible combustible) {
    this.combustible = combustible;
  }
  
	@PostConstruct
  @Override
  protected void init() {		
    try {
      this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.attrs.put("idTipoCombustible", JsfBase.getFlashAttribute("idTipoCombustible")== null? -1L: JsfBase.getFlashAttribute("idTipoCombustible"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
			this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  @Override
  public void doLoad() {
    Map<String, Object> params= new HashMap<>();
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      params.put("idTipoCombustible", this.attrs.get("idTipoCombustible"));
      switch (this.accion) {
        case AGREGAR:											
          this.combustible= new TipoCombustible(-1L);
          break;
        case MODIFICAR:			
        case CONSULTAR:											
          this.combustible= (TipoCombustible)DaoFactory.getInstance().toEntity(TipoCombustible.class, "TcSakbeTiposCombustiblesDto", "igual", params);
          this.combustible.setIkTipoInsumo(new UISelectEntity(this.combustible.getIdTipoInsumo()));
          break;
      } // switch
			this.toLoadCatalogos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  } // doLoad  

	private void toLoadCatalogos() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
  		params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);			
			columns= new ArrayList<>();
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
		  List<UISelectEntity> insumos= (List<UISelectEntity>) UIEntity.seleccione("TcSakbeTiposInsumosDto", "row", params, columns, "nombre");
			this.attrs.put("insumos", insumos);			
			this.attrs.put("idTipoInsumo", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("insumos")));			
      if(insumos!= null && !insumos.isEmpty()) 
        if(Objects.equals(this.accion, EAccion.AGREGAR))
  			  this.combustible.setIkTipoInsumo(insumos.get(0));
        else
  			  this.combustible.setIkTipoInsumo(insumos.get(insumos.indexOf(this.combustible.getIkTipoInsumo())));
    } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
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
    Transaccion transaccion= null;
    String regresar        = null;
		try {
			transaccion = new Transaccion(this.combustible);
			if (transaccion.ejecutar(this.accion)) {
			  if(!this.accion.equals(EAccion.CONSULTAR)) 
   			  JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el insumo"), ETipoMensaje.INFORMACION);
   			regresar= this.doCancelar();
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el insumo", ETipoMensaje.ALERTA);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idTipoCombustible", this.combustible.getIdTipoCombustible());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar
 
	public void doAutoSaveOrden() {
	  this.toSaveRecord();	
	} // doAutoSaveOrden

	@Override
	public void toSaveRecord() {
    Transaccion transaccion= null;
    try {			
			transaccion= new Transaccion(this.combustible);
			if (transaccion.ejecutar(EAccion.AGREGAR)) {
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