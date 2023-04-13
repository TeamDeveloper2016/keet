package mx.org.kaana.keet.catalogos.desarrollos.backing;

import java.io.Serializable;
import java.util.ArrayList;
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
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.desarrollos.beans.RegistroDesarrollo;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.keet.catalogos.desarrollos.reglas.Transaccion;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.clientes.reglas.MotorBusqueda;
import mx.org.kaana.mantic.db.dto.TcManticDomiciliosDto;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.model.map.LatLng;


@Named(value = "keetCatalogosDesarrollosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

	private static final long serialVersionUID = 5870484413138653116L;
	private RegistroDesarrollo desarrollo;
	private UISelectEntity domicilioBusqueda;

	public UISelectEntity getDomicilioBusqueda() {
		return domicilioBusqueda;
	}

	public void setDomicilioBusqueda(UISelectEntity domicilioBusqueda) {
		this.domicilioBusqueda = domicilioBusqueda;
	}
	
	public RegistroDesarrollo getDesarrollo() {
		return desarrollo;
	}

	public void setDesarrollo(RegistroDesarrollo desarrollo) {
		this.desarrollo = desarrollo;
	}	
	
	@PostConstruct
  @Override
  protected void init() {			
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idDesarrollo", JsfBase.getFlashAttribute("idDesarrollo"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());      
      this.attrs.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.loadCatalogos();
			this.doLoad();			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void loadCatalogos() {
		List<Columna>campos= null;
		try {
			this.attrs.put("clientes", UIEntity.seleccione("TcManticClientesDto", "sucursales", this.attrs, "clave"));
		} // try
		catch (Exception e) {
			throw e;
		} // catch		
	} // loadCatalogos
	
  public void doLoad() {
    EAccion eaccion= null;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:											
          this.desarrollo= new RegistroDesarrollo();					
          break;
        case MODIFICAR:					
        case CONSULTAR:					
					this.desarrollo= new RegistroDesarrollo((Long)this.attrs.get("idDesarrollo"));
					this.loadAtributosComplemento();	
          this.doExistGeo();
          break;
      } // switch
			this.loadEntidades();		
			this.loadMunicipios();		
			this.loadLocalidades();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
		EAccion eaccion        = null;
    try {			
			eaccion= (EAccion) this.attrs.get("accion");      
			transaccion = new Transaccion(this.desarrollo);
			if (transaccion.ejecutar(eaccion)) {
				JsfBase.setFlashAttribute("idDesarrolloProcess", this.desarrollo.getDesarrollo().getIdDesarrollo());
				regresar = this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);				
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el desarrollo de forma correcta."), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el proyecto", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
		JsfBase.setFlashAttribute("idDesarrolloProcess", this.desarrollo.getDesarrollo().getIdDesarrollo());
    return (String) this.attrs.get("retorno");
  } // doCancelar	
	
	public void doLoadAtributos() {
		doLoadAtributos(true);
	} // doLoadAtributos
	
  public void doLoadAtributos(boolean all) {    
		List<Entity> domicilios= null;
    try {
			if(all) {
				if(!this.desarrollo.getDomicilio().getDomicilio().getKey().equals(-1L)) {
					domicilios= (List<Entity>) this.attrs.get("domicilios");
					this.desarrollo.getDomicilio().setDomicilio(domicilios.get(domicilios.indexOf(this.desarrollo.getDomicilio().getDomicilio())));
					this.desarrollo.getDomicilio().setIdDomicilio(domicilios.get(domicilios.indexOf(this.desarrollo.getDomicilio().getDomicilio())).getKey());
				} // if
				else{
					this.desarrollo.getDomicilio().setDomicilio(new Entity(-1L));
					this.desarrollo.getDomicilio().setIdDomicilio(-1L);
				} // else		
				loadEntidades();				
				loadMunicipios();				
				loadLocalidades();				
			} // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } // doLoadAtributos
	
	private void loadAtributosComplemento() throws Exception{
		MotorBusqueda motor            = null;
		TcManticDomiciliosDto domicilio= null;
		try {
			if (!this.desarrollo.getDomicilio().getIdDomicilio().equals(-1L)) {
        motor = new MotorBusqueda(this.desarrollo.getDomicilio().getIdDomicilio());
        domicilio = motor.toDomicilio(this.desarrollo.getDomicilio().getIdDomicilio());
        this.desarrollo.getDomicilio().setNumeroExterior(domicilio.getNumeroExterior());
        this.desarrollo.getDomicilio().setNumeroInterior(domicilio.getNumeroInterior());
        this.desarrollo.getDomicilio().setCalle(domicilio.getCalle());
        this.desarrollo.getDomicilio().setAsentamiento(domicilio.getAsentamiento());
        this.desarrollo.getDomicilio().setEntreCalle(domicilio.getEntreCalle());
        this.desarrollo.getDomicilio().setYcalle(domicilio.getYcalle());
        this.desarrollo.getDomicilio().setCodigoPostal(domicilio.getCodigoPostal());
        
      } // if
      else {
        this.clearAtributos();
      } // else
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
	} // loadAtributosComplemento
	
  public void clearAtributos() {
    try {
      this.desarrollo.getDomicilio().setNumeroExterior("");
      this.desarrollo.getDomicilio().setNumeroInterior("");
      this.desarrollo.getDomicilio().setCalle("");
      this.desarrollo.getDomicilio().setAsentamiento("");
      this.desarrollo.getDomicilio().setEntreCalle("");
      this.desarrollo.getDomicilio().setYcalle("");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } // doLoadAtributos
	
	private void loadEntidades() {
    List<UISelectEntity> entidades= null;
		List<Columna>campos= null;
    Map<String, Object> params= null;
    try {
      params = new HashMap<>();
      params.put("idPais", 1);
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			campos= new ArrayList<>();
			campos.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
      entidades = UIEntity.build("TcJanalEntidadesDto", "comboEntidades", params, campos, Constantes.SQL_TODOS_REGISTROS);
      this.attrs.put("entidades", entidades);
      this.desarrollo.getDomicilio().setIdEntidad(entidades.get(0));
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // loadEntidades
	
	private void loadMunicipios() {
    List<UISelectEntity> municipios= null;
    Map<String, Object> params= null;
		List<Columna>campos= null;
    try {
			if(!this.desarrollo.getDomicilio().getIdEntidad().getKey().equals(-1L)) {
				params = new HashMap<>();
				params.put("idEntidad", this.desarrollo.getDomicilio().getIdEntidad().getKey());
				campos= new ArrayList<>();
				campos.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
				municipios = UIEntity.build("TcJanalMunicipiosDto", "comboMunicipios", params, campos, Constantes.SQL_TODOS_REGISTROS);
				this.attrs.put("municipios", municipios);
				this.desarrollo.getDomicilio().setIdMunicipio(municipios.get(0));
			} // if
			else
				this.desarrollo.getDomicilio().setIdMunicipio(new Entity(-1L));
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // loadMunicipios
	
	 private void loadLocalidades() {
    List<UISelectEntity> localidades= null;
    Map<String, Object> params= null;
		List<Columna>campos= null;
    try {
			if(!this.desarrollo.getDomicilio().getIdMunicipio().getKey().equals(-1L)) {
				params = new HashMap<>();
				params.put("idMunicipio", this.desarrollo.getDomicilio().getIdMunicipio().getKey());
				campos= new ArrayList<>();
				campos.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
				localidades = UIEntity.build("TcJanalLocalidadesDto", "comboLocalidades", params, campos, Constantes.SQL_TODOS_REGISTROS);
				this.attrs.put("localidades", localidades);
				this.desarrollo.getDomicilio().setLocalidad(localidades.get(0));
				this.desarrollo.getDomicilio().setIdLocalidad(localidades.get(0).getKey());
			} // if
			else{
				this.desarrollo.getDomicilio().setLocalidad(new Entity(-1L));
				this.desarrollo.getDomicilio().setIdLocalidad(-1L);
			} // else
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // loadLocalidades
	 
	public void doActualizaMunicipios() {
    try {
      this.loadMunicipios();
      this.loadLocalidades();      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doActualizaMunicipios

  public void doActualizaLocalidades() {
    try {
      this.loadLocalidades();      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doActualizaMunicipios
	
	public void doBusquedaDomicilios() {
    List<UISelectEntity> domicilios= null;
    Map<String, Object> params= null;
		List<Columna>campos= null;
    try {
      params = new HashMap<>();      
      params.put(Constantes.SQL_CONDICION, "upper(calle) like upper('%".concat(this.attrs.get("calle").toString()).concat("%')"));
			campos= new ArrayList<>();
			campos.add(new Columna("calle", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("numeroExterior", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("numeroInterior", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("asentamiento", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("entidad", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("municipio", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("domicilio", EFormatoDinamicos.MAYUSCULAS));
      domicilios = UIEntity.build("VistaDomiciliosCatalogosDto", "domicilios", params, campos, Constantes.SQL_TODOS_REGISTROS);
      this.desarrollo.getDomicilio().setDomicilio(new Entity(-1L));
      this.desarrollo.getDomicilio().setIdDomicilio(-1L);
			this.attrs.put("domiciliosBusqueda", domicilios);      
			this.attrs.put("resultados", domicilios.size());      
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // doLoadDomicilios

	public void doAsignaDomicilio() {
		List<UISelectEntity> domicilios        = null;
		List<UISelectEntity> domiciliosBusqueda= null;
		UISelectEntity domicilio               = null;
		try {
			domiciliosBusqueda=(List<UISelectEntity>) this.attrs.get("domiciliosBusqueda");
			domicilio= domiciliosBusqueda.get(domiciliosBusqueda.indexOf(this.domicilioBusqueda));
			domicilios= new ArrayList<>();
			domicilios.add(domicilio);
			this.attrs.put("domicilios", domicilios);			
			this.desarrollo.getDomicilio().setDomicilio(domicilio);
      this.desarrollo.getDomicilio().setIdDomicilio(domicilio.getKey());
			this.toAsignaEntidad();
			this.loadMunicipios();
			this.toAsignaMunicipio();
			this.loadLocalidades();
			this.toAsignaLocalidad();			
			this.loadAtributosComplemento();			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doAsignaDomicilio
	
	private void toAsignaCodigoPostal() {
		Entity domicilio= null;
		List<UISelectItem>codigos= null;
		int count=0;
		try {
			if(!this.desarrollo.getDomicilio().getIdDomicilio().equals(-1L)) {
				domicilio= this.desarrollo.getDomicilio().getDomicilio();
				codigos= (List<UISelectItem>) this.attrs.get("codigosPostales");
				for(UISelectItem codigo: codigos) {
					if(codigo.getLabel().equals(domicilio.toString("codigoPostal"))) {
						this.desarrollo.getDomicilio().setCodigoPostal(codigo.getLabel());
						this.desarrollo.getDomicilio().setIdCodigoPostal((Long) codigo.getValue());
						this.desarrollo.getDomicilio().setNuevoCp(true);
						count++;
					} // if
				} // for
				if(count==0) {
					this.desarrollo.getDomicilio().setNuevoCp(false);
					this.desarrollo.getDomicilio().setIdCodigoPostal(-1L);
					this.desarrollo.getDomicilio().setCodigoPostal("");
				} // if
			} // if
			else{
				this.desarrollo.getDomicilio().setNuevoCp(false);
				this.desarrollo.getDomicilio().setIdCodigoPostal(-1L);
				this.desarrollo.getDomicilio().setCodigoPostal("");
			} // else
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toAsignaCodigoPostal
	
	private void toAsignaMunicipio() {
		Entity domicilio= null;
		List<Entity>municipios= null;
		try {
			if(!this.desarrollo.getDomicilio().getIdMunicipio().getKey().equals(-1L)) {
				domicilio= this.desarrollo.getDomicilio().getDomicilio();
				municipios= (List<Entity>) this.attrs.get("municipios");
				for(Entity municipio: municipios) {
					if(municipio.getKey().equals(domicilio.toLong("idMunicipio")))
						this.desarrollo.getDomicilio().setIdMunicipio(municipio);
				} // for
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toAsignaMunicipio
	
	private void toAsignaLocalidad() {
		Entity domicilio= null;
		List<Entity>localidades= null;
		try {
			if(!this.desarrollo.getDomicilio().getIdDomicilio().equals(-1L)) {
				domicilio= this.desarrollo.getDomicilio().getDomicilio();
				localidades= (List<Entity>) this.attrs.get("localidades");
				for(Entity localidad: localidades) {
					if(localidad.getKey().equals(domicilio.toLong("idLocalidad"))) {
						this.desarrollo.getDomicilio().setIdLocalidad(localidad.getKey());
						this.desarrollo.getDomicilio().setLocalidad(localidad);
					} // if
				} // for
			} // if			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toAsignaLocalidad
	
	private void toAsignaEntidad() {
		Entity domicilio= null;
		List<Entity>entidades= null;
		try {
			if(!this.desarrollo.getDomicilio().getIdDomicilio().equals(-1L)) {
				domicilio= this.desarrollo.getDomicilio().getDomicilio();
				entidades= (List<Entity>) this.attrs.get("entidades");
				for(Entity entidad: entidades) {
					if(entidad.getKey().equals(domicilio.toLong("idEntidad")))
						this.desarrollo.getDomicilio().setIdEntidad(entidad);
				} // for
			} // if
			else
				this.desarrollo.getDomicilio().setIdEntidad(new Entity(-1L));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toAsignaEntidad
	
	public void doExistGeo() {
		try {
			this.attrs.put("latitud", this.desarrollo.getDesarrollo().getLatitud());
			this.attrs.put("longitud", this.desarrollo.getDesarrollo().getLongitud());	
			this.attrs.put("coordenadas", this.desarrollo.getDesarrollo().getLatitud().concat(",").concat(this.desarrollo.getDesarrollo().getLongitud()));
			UIBackingUtilities.execute("existLocalization('".concat(this.desarrollo.getDesarrollo().getLatitud()).concat("','").concat(this.desarrollo.getDesarrollo().getLongitud()).concat("');"));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doExistGeo
	
	public void doInitGeo(String latitud, String longitud) {		
		try {
			this.attrs.put("latitud", latitud);
			this.attrs.put("longitud", longitud);		
			this.attrs.put("coordenadas", latitud.concat(",").concat(longitud));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doInitGeo
	
	public void onBlurPointSelect(String geo) {
		LatLng coordenadas    = null;
		String[] georeferencia= null;
		try {	
			georeferencia= Cadena.eliminaCaracter(geo, '@').split(",");
			if(georeferencia.length== 2) {
				coordenadas= new LatLng(Double.valueOf(georeferencia[0]), Double.valueOf(georeferencia[1]));
        this.desarrollo.getDesarrollo().setLatitud(String.valueOf(coordenadas.getLat()));
        this.desarrollo.getDesarrollo().setLongitud(String.valueOf(coordenadas.getLng()));
        this.attrs.put("latitud", this.desarrollo.getDesarrollo().getLatitud());
        this.attrs.put("longitud", this.desarrollo.getDesarrollo().getLongitud());						
				this.attrs.put("coordenadas",  this.desarrollo.getDesarrollo().getLatitud().concat(",").concat(this.desarrollo.getDesarrollo().getLongitud()));
				UIBackingUtilities.execute("updateLocalization('".concat(String.valueOf(coordenadas.getLat())).concat("','").concat(String.valueOf(coordenadas.getLng())).concat("');"));
			} // if
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
  } // onBlurPointSelect
	
	public void onPointSelect(PointSelectEvent event) {
		LatLng coordenadas= null;
		try {
			coordenadas= event.getLatLng();                  
			this.desarrollo.getDesarrollo().setLatitud(String.valueOf(coordenadas.getLat()));
			this.desarrollo.getDesarrollo().setLongitud(String.valueOf(coordenadas.getLng()));
			this.attrs.put("latitud", this.desarrollo.getDesarrollo().getLatitud());
			this.attrs.put("longitud", this.desarrollo.getDesarrollo().getLongitud());						
			this.attrs.put("coordenadas",  this.desarrollo.getDesarrollo().getLatitud().concat(",").concat(this.desarrollo.getDesarrollo().getLongitud()));
			UIBackingUtilities.execute("updateLocalization('".concat(String.valueOf(coordenadas.getLat())).concat("','").concat(String.valueOf(coordenadas.getLng())).concat("');"));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
  } // onPointSelect
	
	public void doAceptarGeo() {
		String[] coordenadas= null;
		try {
			if(!(Cadena.isVacio(this.attrs.get("coordenadas")))) {
				coordenadas= ((String)this.attrs.get("coordenadas")).split(",");
				if(coordenadas.length==2) {
					this.desarrollo.getDesarrollo().setLatitud(coordenadas[0].replaceAll("@", "").trim());
					this.desarrollo.getDesarrollo().setLongitud(coordenadas[1].replaceAll("@", "").trim());
					UIBackingUtilities.execute("updateLocalization('".concat(this.desarrollo.getDesarrollo().getLatitud()).concat("','").concat(this.desarrollo.getDesarrollo().getLongitud()).concat("');"));
				} // if 
				else
					JsfBase.addAlert("Las coordenadas deben estar separadas por una coma");
			} // if
			else
			  JsfBase.addAlert("El campo de coordenadas esta vacio");
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doAceptarGeo	
	
	public void doCancelarGeo() {		
		try {
			if(this.attrs.get("latitudAnterior")!= null && this.attrs.get("longitudAnterior")!= null) {
				this.desarrollo.getDesarrollo().setLatitud(this.attrs.get("latitudAnterior").toString());
				this.desarrollo.getDesarrollo().setLongitud(this.attrs.get("longitudAnterior").toString());			
			} // if
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doCancelarGeo

	public void doTabChange(TabChangeEvent event) {
		if(event.getTab().getTitle().equals("Georeferencia"))
			UIBackingUtilities.execute(Cadena.isVacio(this.desarrollo.getDesarrollo().getLatitud()) || Cadena.isVacio(this.desarrollo.getDesarrollo().getLongitud())? "executeGeo();" : "executeExistGeo();");		
	}	// doTabChange	
}