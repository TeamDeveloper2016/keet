package mx.org.kaana.sakbe.catalogos.maquinaria.backing;

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
import mx.org.kaana.keet.catalogos.contratos.enums.EContratosEstatus;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.sakbe.catalogos.maquinaria.reglas.Transaccion;
import mx.org.kaana.mantic.comun.IBaseStorage;
import mx.org.kaana.mantic.inventarios.comun.IBaseImportar;
import mx.org.kaana.sakbe.catalogos.maquinaria.beans.Maquinaria;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.event.TabChangeEvent;


@Named(value= "sakbeCatalogosMaquinariaAccion")
@ViewScoped
public class Accion extends IBaseImportar implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Accion.class);
  private static final long serialVersionUID= 327393488565639367L;
	
  private EAccion accion;
  private Maquinaria maquinaria;
  
	public String getAgregar() {
		return this.accion.equals(EAccion.AGREGAR)? "none": "";
	}

  public Maquinaria getMaquinaria() {
    return maquinaria;
  }

  public void setMaquinaria(Maquinaria maquinaria) {
    this.maquinaria = maquinaria;
  }
  
	@PostConstruct
  @Override
  protected void init() {		
    try {
      this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
      this.attrs.put("idMaquinaria", JsfBase.getFlashAttribute("idMaquinaria")== null? -1L: JsfBase.getFlashAttribute("idMaquinaria"));
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
      params.put("idMaquinaria", this.attrs.get("idMaquinaria"));
      switch (this.accion) {
        case AGREGAR:											
          this.maquinaria= new Maquinaria(-1L);
          break;
        case MODIFICAR:			
        case CONSULTAR:											
          this.maquinaria= (Maquinaria)DaoFactory.getInstance().toEntity(Maquinaria.class, "TcSakbeMaquinariasDto", "igual", params);
          this.maquinaria.setIkEmpresa(new UISelectEntity(this.maquinaria.getIdEmpresa()));
          this.maquinaria.setIkDesarrollo(new UISelectEntity(this.maquinaria.getIdDesarrollo()));
          this.maquinaria.setIkMaquinariaGrupo(new UISelectEntity(this.maquinaria.getIdMaquinariaGrupo()));
          this.maquinaria.setIkTipoMaquinaria(new UISelectEntity(this.maquinaria.getIdTipoMaquinaria()));
          this.maquinaria.setIkTipoCombustible(new UISelectEntity(this.maquinaria.getIdTipoCombustible()));
          break;
      } // switch
			this.toLoadCatalogos();
      this.doLoadDesarrollos();
      this.toLoadTiposCombustibles();
      this.toLoadMaquinariasGrupos();
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
  				this.maquinaria.setIkEmpresa(empresas.get(0));
			  else 
				  this.maquinaria.setIkEmpresa(empresas.get(empresas.indexOf(this.maquinaria.getIkEmpresa())));
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

	public void doLoadDesarrollos() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
  		params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);			
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
      List<UISelectEntity> desarrollos= (List<UISelectEntity>) UIEntity.seleccione("TcKeetDesarrollosDto", "row", params, columns, "nombres");
			this.attrs.put("desarrollos", desarrollos);
			if(desarrollos!= null && !desarrollos.isEmpty())
        if(this.accion.equals(EAccion.AGREGAR))
          this.maquinaria.setIkDesarrollo(desarrollos.get(0));
        else  
          this.maquinaria.setIkDesarrollo(desarrollos.get(desarrollos.indexOf(this.maquinaria.getIkDesarrollo())));
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	} // doLoadDesarrollos
  
	private void toLoadTiposCombustibles() {
		List<UISelectEntity> tiposCombustibles= null;
		Map<String, Object>params             = new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			tiposCombustibles= UIEntity.seleccione("TcSakbeTiposCombustiblesDto", "row", params, "nombre");
			this.attrs.put("tiposCombustibles", tiposCombustibles);
      if(tiposCombustibles!=null && !tiposCombustibles.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          this.maquinaria.setIkTipoCombustible(tiposCombustibles.get(0));
        else  
          this.maquinaria.setIkTipoCombustible(tiposCombustibles.get(tiposCombustibles.indexOf(this.maquinaria.getIkTipoCombustible())));
		} // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
		finally{
			Methods.clean(params);
		} // finally
	} // toLoadTiposCombustibles  
  
	private void toLoadMaquinariasGrupos() {
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
      this.toLoadTiposMaquinarias();
    } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} // toLoadMaquinariasGrupos 
  
	private void toLoadTiposMaquinarias() {
    try {
      UISelectEntity anterior= this.maquinaria.getIkTipoMaquinaria();
      this.doLoadTiposMaquinarias();
      List<UISelectEntity> tiposMaquinarias= (List<UISelectEntity>)this.attrs.get("tiposMaquinarias");
      if(tiposMaquinarias!= null && !tiposMaquinarias.isEmpty() && !Objects.equals(this.accion, EAccion.AGREGAR)) 
        this.maquinaria.setIkTipoMaquinaria(tiposMaquinarias.get(tiposMaquinarias.indexOf(anterior)));
    } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
  }
  
	public void doLoadTiposMaquinarias() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
  		params.put("idMaquinariaGrupo", this.maquinaria.getIdMaquinariaGrupo());			
			columns= new ArrayList<>();
      columns.add(new Columna("grupo", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
		  List<UISelectEntity> tiposMaquinarias= (List<UISelectEntity>)UIEntity.seleccione("TcSakbeTiposMaquinariasDto", "grupo", params, columns, "grupo");
		  this.attrs.put("tiposMaquinarias", tiposMaquinarias);
		  this.maquinaria.setIkTipoMaquinaria(UIBackingUtilities.toFirstKeySelectEntity(tiposMaquinarias));
    } // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally      
	} // toLoadTiposMaquinarias 
  
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
  	JsfBase.setFlashAttribute("idMaquinaria", this.maquinaria.getIdMaquinaria());
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