package mx.org.kaana.sakbe.catalogos.tipos.backing;

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
import mx.org.kaana.sakbe.catalogos.tipos.reglas.Transaccion;
import mx.org.kaana.mantic.comun.IBaseStorage;
import mx.org.kaana.mantic.inventarios.comun.IBaseImportar;
import mx.org.kaana.sakbe.catalogos.tipos.beans.TipoMaquinaria;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.TabChangeEvent;


@Named(value= "sakbeCatalogosTiposAccion")
@ViewScoped
public class Accion extends IBaseImportar implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Accion.class);
  private static final long serialVersionUID= 327393484565639367L;
	
  private EAccion accion;
  private TipoMaquinaria maquinaria;
  
	public String getAgregar() {
		return this.accion.equals(EAccion.AGREGAR)? "none": "";
	}

  public TipoMaquinaria getMaquinaria() {
    return maquinaria;
  }

  public void setMaquinaria(TipoMaquinaria maquinaria) {
    this.maquinaria = maquinaria;
  }
  
	@PostConstruct
  @Override
  protected void init() {		
    try {
      this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.attrs.put("idTipoMaquinaria", JsfBase.getFlashAttribute("idTipoMaquinaria")== null? -1L: JsfBase.getFlashAttribute("idTipoMaquinaria"));
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
      params.put("idTipoMaquinaria", this.attrs.get("idTipoMaquinaria"));
      switch (this.accion) {
        case AGREGAR:											
          this.maquinaria= new TipoMaquinaria(-1L);
          break;
        case MODIFICAR:			
        case CONSULTAR:											
          this.maquinaria= (TipoMaquinaria)DaoFactory.getInstance().toEntity(TipoMaquinaria.class, "TcSakbeTiposMaquinariasDto", "igual", params);
          this.maquinaria.setIkMaquinariaGrupo(new UISelectEntity(this.maquinaria.getIdMaquinariaGrupo()));
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
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
		  List<UISelectEntity> maquinariasGrupos= (List<UISelectEntity>) UIEntity.seleccione("TcSakbeMaquinariasGruposDto", "row", params, columns, "nombre");
			this.attrs.put("maquinariasGrupos", maquinariasGrupos);			
			this.attrs.put("idMaquinariasGrupo", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("maquinariasGrupos")));			
      if(maquinariasGrupos!= null && !maquinariasGrupos.isEmpty()) 
        if(Objects.equals(this.accion, EAccion.AGREGAR))
  			  this.maquinaria.setIkMaquinariaGrupo(maquinariasGrupos.get(0));
        else
  			  this.maquinaria.setIkMaquinariaGrupo(maquinariasGrupos.get(maquinariasGrupos.indexOf(this.maquinaria.getIkMaquinariaGrupo())));
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
			transaccion = new Transaccion(this.maquinaria);
			if (transaccion.ejecutar(this.accion)) {
			  if(!this.accion.equals(EAccion.CONSULTAR)) 
   			  JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" la maquinaria"), ETipoMensaje.INFORMACION);
   			regresar= this.doCancelar();
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar la maquinaria", ETipoMensaje.ALERTA);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
  	JsfBase.setFlashAttribute("idTipoMaquinaria", this.maquinaria.getIdTipoMaquinaria());
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar
 
	public void doAutoSaveOrden() {
	  this.toSaveRecord();	
	} // doAutoSaveOrden

	@Override
	public void toSaveRecord() {
    Transaccion transaccion= null;
    try {			
			transaccion= new Transaccion(this.maquinaria);
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
		if(isViewException && this.maquinaria!= null && this.maquinaria.isComplete())
		  this.toSaveRecord();
	} // doGlobalEvent

}