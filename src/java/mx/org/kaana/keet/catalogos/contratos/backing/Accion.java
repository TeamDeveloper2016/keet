package mx.org.kaana.keet.catalogos.contratos.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.jobs.Contratos;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.contratos.beans.ContratoDomicilio;
import mx.org.kaana.keet.catalogos.contratos.beans.Lote;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.keet.catalogos.contratos.reglas.Transaccion;
import mx.org.kaana.keet.catalogos.contratos.beans.RegistroContrato;
import mx.org.kaana.keet.db.dto.TcKeetProyectosDto;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.keet.catalogos.contratos.reglas.MotorBusqueda;
import mx.org.kaana.keet.catalogos.contratos.beans.Retencion;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.mantic.catalogos.clientes.beans.Domicilio;
import mx.org.kaana.mantic.db.dto.TcManticDomiciliosDto;
import mx.org.kaana.mantic.enums.ETipoMediosPago;
import mx.org.kaana.mantic.enums.ETiposDomicilios;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.model.map.LatLng;

@Named(value = "keetCatalogosContratosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639367L;
	private RegistroContrato contrato;
	private List<Lote> lotesOrden;
	private List<UISelectItem> fachadas;
	private List<UISelectItem> prototipos;
  private UISelectEntity domicilioBusqueda;
  private EAccion accion;
  
	public RegistroContrato getContrato() {
		return contrato;
	}

	public void setContrato(RegistroContrato contrato) {
		this.contrato = contrato;
	}

	public List<UISelectItem> getFachadas() {
		return fachadas;
	}

	public void setFachadas(List<UISelectItem> fachadas) {
		this.fachadas = fachadas;
	}

	public List<UISelectItem> getPrototipos() {
		return prototipos;
	}

	public void setPrototipos(List<UISelectItem> prototipos) {
		this.prototipos = prototipos;
	}	

  public UISelectEntity getDomicilioBusqueda() {
    return domicilioBusqueda;
  }

  public void setDomicilioBusqueda(UISelectEntity domicilioBusqueda) {
    this.domicilioBusqueda = domicilioBusqueda;
  }
	
	@PostConstruct
  @Override
  protected void init() {		
    try {
      this.accion= JsfBase.getFlashAttribute("accion")== null? EAccion.AGREGAR: (EAccion)JsfBase.getFlashAttribute("accion");
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("idContrato", JsfBase.getFlashAttribute("idContrato"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("mostrarGeo", Boolean.FALSE);
 			this.attrs.put("cpNuevo", Boolean.FALSE);						
 			this.attrs.put("total", 0D);
      this.attrs.put("diferencia", 0D);
      this.toLoadCombos();
			this.doLoad();
      this.doLoadItemsDomicilio();
      this.toLoadTiposPagos();
      this.toLoadBancos();
      this.doCheckMedioPago();
      switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
        case "cafu":
          this.attrs.put("visualizar", Boolean.TRUE);	
          break;
        case "gylvi": 
        case "triana":
        default:
          this.attrs.put("visualizar", Boolean.FALSE);	
          break;
      } // swtich
      if((Boolean)this.attrs.get("visualizar")) {
        Contratos contratos= new Contratos();
        this.attrs.put("comentarios", contratos.toCheckContrato((Long)this.attrs.get("idContrato"), Boolean.FALSE));
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	
	private void toLoadCombos() {
		try {
			this.attrs.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.attrs.put("clientes", UIEntity.seleccione("TcManticClientesDto", "sucursales", this.attrs, "clave"));
      this.attrs.put("proyectos", UIEntity.seleccione("TcKeetProyectosDto", "row", this.attrs, "clave"));
      this.attrs.put("tipoObras", UIEntity.seleccione("VistaTiposObrasDto", "catalogo", this.attrs, "tipoObra"));
      this.fachadas= UISelect.seleccione("TcKeetTiposFachadasDto", "row", this.attrs, "nombre", EFormatoDinamicos.MAYUSCULAS);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // loadCombos
	
	public void doLoadPrototipos() {
		try {
			this.loadPrototipos();
			this.contrato.getContrato().validaPrototipos((List<UISelectItem>)this.attrs.get("prototipos"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // doLoadPrototipos
	
	private void loadPrototipos() {
	  try {
			this.attrs.put("idCliente", ((TcKeetProyectosDto)DaoFactory.getInstance().findById(TcKeetProyectosDto.class, this.contrato.getContrato().getIdProyecto())).getIdCliente());
      this.prototipos= UISelect.seleccione("TcKeetPrototiposDto", "byCliente", this.attrs, "nombre", EFormatoDinamicos.MAYUSCULAS);
			this.contrato.getContrato().validaPrototipos(this.prototipos);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // doLoadPrototipos

  public void doLoad() {
    try {
      this.attrs.put("nombreAccion", Cadena.letraCapital(this.accion.name()));
      switch (this.accion) {
        case AGREGAR:											
          this.contrato= new RegistroContrato();
          this.toLoadCollections();
          this.toAgregarContrato();
          break;
        case MODIFICAR:					
        case CONSULTAR:					
        case SUBIR:				
          this.attrs.put("cpNuevo", true);
          this.contrato= new RegistroContrato((Long)this.attrs.get("idContrato"));
          this.toLoadCollections();
					this.doCompleteCodigoPostal(this.contrato.getDomicilio().getCodigoPostal());
					this.asignaCodigoPostal();
					if(this.contrato.getContratoDomicilios().isEmpty())
            this.toAgregarContrato();
          else {
						this.contrato.setContratoDomicilioSelecion(this.contrato.getContratoDomicilios().get(0));
						this.doConsultarContratoDomicilios();
					} // if
          break;
      } // switch
      this.doUpdateGlobal();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

	private void toLoadCollections() {
		this.loadPrototipos();          
		this.toLoadTiposDomicilios();	
		this.toLoadDomicilios();
		this.doLoadEntidades();		
  }
  
	public void doLoadItemsDomicilio() {		
		int count= 0;
		try {
			this.attrs.put("renderedDomicilio", true);
			for(ContratoDomicilio item: this.contrato.getContratoDomicilios()) {
				if(item.getKey()>= 0L)
					count++;
			} // for
			if(count== 0) {
				this.attrs.put("renderedDomicilio", false);
				this.attrs.put("mensajeDomicilio", "Un DOMICILIO registrado.");								
			} // if
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doCreateMessage
  
  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
    try {			
      this.contrato.getContrato().setIdUsuario(JsfBase.getIdUsuario());
      this.contrato.doActualizarContratoDomicilio();
			transaccion= new Transaccion(this.contrato);
			if (transaccion.ejecutar(this.accion)) {
				regresar =  this.doCancelar();
				JsfBase.addMessage("Se ".concat(this.accion.equals(EAccion.AGREGAR)? "agregó": "modificó").concat(" el contrato de forma correcta !"), ETipoMensaje.INFORMACION);
        transaccion.ejecutar(EAccion.DESTRANSFORMACION);

        // VERIFICAR SI EL CONTRATO CON LAS MODIFICACIONES QUEDO CORRECTO
        if((Boolean)this.attrs.get("visualizar")) {        
          Contratos contratos= new Contratos();
          contratos.toCheckContrato((Long)this.attrs.get("idContrato"), Boolean.TRUE);
        } // if  
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el contrato", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
		if(this.contrato.getContrato().getIdContrato()>= 1L)
			JsfBase.setFlashAttribute("idContratoProcess", this.contrato.getContrato().getIdContrato());
    return "filtro".concat(Constantes.REDIRECIONAR);
  } // doAccion	
	
	public void onSelect(SelectEvent<Lote> event) {
		try {
			this.lotesOrden= new ArrayList<>();
			for(Lote item: this.contrato.getContrato().getLotes())
				this.lotesOrden.add(item);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	}	

	public void onReorder() {
		try {
			for(int i=0; this.contrato.getContrato().getLotes().size()<i; i++)
				this.contrato.getContrato().getLotes().get(i).setOrden(this.lotesOrden.get(i).getOrden());
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch			
	}

	public void doGeoreferencia(Lote lote) {
		try {
			this.attrs.put("loteGeoreferencia", lote);			
			this.attrs.put("mostrarGeo", true);						
			this.attrs.put("latitudAnterior", lote.getLatitud());						
			this.attrs.put("longitudAnterior", lote.getLongitud());						
			UIBackingUtilities.execute(Cadena.isVacio(lote.getLatitud()) || Cadena.isVacio(lote.getLongitud()) ? "executeGeo();" : "executeExistGeo();");			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doGeoreferencia	
	
	public void doExistGeo() {
		Lote lote= null;
		try {
			lote= (Lote) this.attrs.get("loteGeoreferencia");
			this.attrs.put("latitud", lote.getLatitud());
			this.attrs.put("longitud", lote.getLongitud());			
			UIBackingUtilities.execute("existLocalization('".concat(lote.getLatitud()).concat("','").concat(lote.getLongitud()).concat("');"));
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
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doInitGeo
	
	public void onBlurPointSelect(String geo) {
		LatLng coordenadas    = null;
		Lote lote             = null;
		String[] georeferencia= null;
		try {	
			georeferencia= Cadena.eliminaCaracter(geo, '@').split(",");
			coordenadas= new LatLng(Double.valueOf(georeferencia[0]), Double.valueOf(georeferencia[1]));
			lote= (Lote) this.attrs.get("loteGeoreferencia");						
			lote.setLatitud(String.valueOf(coordenadas.getLat()));
			lote.setLongitud(String.valueOf(coordenadas.getLng()));
			this.attrs.put("latitud", lote.getLatitud());
			this.attrs.put("longitud", lote.getLongitud());			
			this.attrs.put("loteGeoreferencia", lote);
			UIBackingUtilities.execute("updateLocalization('".concat(lote.getLatitud()).concat("','").concat(lote.getLongitud()).concat("');"));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
  } // onBlurPointSelect
	
	public void onPointSelect(PointSelectEvent event) {
		LatLng coordenadas= null;
		Lote lote         = null;
		try {
			coordenadas= event.getLatLng();                  
			lote= (Lote) this.attrs.get("loteGeoreferencia");						
			lote.setLatitud(String.valueOf(coordenadas.getLat()));
			lote.setLongitud(String.valueOf(coordenadas.getLng()));
			this.attrs.put("latitud", lote.getLatitud());
			this.attrs.put("longitud", lote.getLongitud());			
			this.attrs.put("loteGeoreferencia", lote);
			UIBackingUtilities.execute("updateLocalization('".concat(lote.getLatitud()).concat("','").concat(lote.getLongitud()).concat("');"));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
  } // onPointSelect
	
	public void doAceptarGeo() {
		Lote lote= null;
		try {
			lote= (Lote) this.attrs.get("loteGeoreferencia");		
			if(Cadena.isVacio(lote.getLatitud()) || Cadena.isVacio(lote.getLongitud())) {
				FacesContext.getCurrentInstance().getExternalContext();
				this.contrato.getContrato().getLotes().get(this.contrato.getContrato().getLotes().indexOf(lote)).setLatitud(this.attrs.get("latitud").toString());
				this.contrato.getContrato().getLotes().get(this.contrato.getContrato().getLotes().indexOf(lote)).setLongitud(this.attrs.get("longitud").toString());
			} // if
			else{
				this.contrato.getContrato().getLotes().get(this.contrato.getContrato().getLotes().indexOf(lote)).setLatitud(lote.getLatitud());
				this.contrato.getContrato().getLotes().get(this.contrato.getContrato().getLotes().indexOf(lote)).setLongitud(lote.getLongitud());
			} // else
			this.attrs.put("mostrarGeo", false);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doAceptarGeo	
	
	public void doCancelarGeo() {		
		Lote lote= null;
		try {
			if(this.attrs.get("latitudAnterior")!= null && this.attrs.get("longitudAnterior")!= null) {
				lote= (Lote) this.attrs.get("loteGeoreferencia");		
				this.contrato.getContrato().getLotes().get(this.contrato.getContrato().getLotes().indexOf(lote)).setLatitud(this.attrs.get("latitudAnterior").toString());
				this.contrato.getContrato().getLotes().get(this.contrato.getContrato().getLotes().indexOf(lote)).setLongitud(this.attrs.get("longitudAnterior").toString());			
			} // if
			this.attrs.put("mostrarGeo", false);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doCancelarGeo
  
  public void doBusquedaDomicilios() {
    List<UISelectEntity> domicilios= null;
    Map<String, Object> params     = new HashMap<>();
		List<Columna>campos            = new ArrayList<>();
    try {
      params.put(Constantes.SQL_CONDICION, "upper(calle) like upper('%".concat(this.attrs.get("calle").toString()).concat("%')"));
			campos.add(new Columna("calle", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("numeroExterior", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("numeroInterior", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("asentamiento", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("entidad", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("municipio", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("domicilio", EFormatoDinamicos.MAYUSCULAS));
      domicilios = UIEntity.build("VistaDomiciliosCatalogosDto", "domicilios", params, campos, Constantes.SQL_TODOS_REGISTROS);
      this.contrato.getDomicilio().setDomicilio(new Entity(-1L));
      this.contrato.getDomicilio().setIdDomicilio(-1L);
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
		List<UISelectEntity> domicilios        = new ArrayList<>();
		List<UISelectEntity> domiciliosBusqueda= null;
		UISelectEntity domicilio               = null;
		try {
			domiciliosBusqueda=(List<UISelectEntity>) this.attrs.get("domiciliosBusqueda");
			domicilio= domiciliosBusqueda.get(domiciliosBusqueda.indexOf(this.domicilioBusqueda));
			domicilios.add(domicilio);
			this.attrs.put("domicilios", domicilios);			
			this.contrato.getDomicilio().setDomicilio(domicilio);
      this.contrato.getDomicilio().setIdDomicilio(domicilio.getKey());
			this.toAsignaEntidad();
			this.doLoadMunicipios();
			this.toAsignaMunicipio();
			this.toAsignaLocalidad();			
			this.toLoadComplementos();			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doAsignaDomicilio
 
	public List<UISelectEntity> doCompleteCodigoPostal(String query) {		
		if(this.contrato.getDomicilio().getIdEntidad().getKey()>= 1L && !Cadena.isVacio(query)) {
			this.attrs.put("condicionCodigoPostal", query);
			this.doLoadCodigosPostales();		
			return (List<UISelectEntity>)this.attrs.get("allCodigosPostales");
		} // if
		else{
			this.contrato.getDomicilio().setNuevoCp(false);
			this.contrato.getDomicilio().setIdCodigoPostal(-1L);
			this.contrato.getDomicilio().setCodigoPostal("");
			return new ArrayList<>();
		} // else		
	}	// doCompleteCliente
  
	public void asignaCodigoPostal() {
		List<UISelectEntity> codigosPostales= null;
		try {
			codigosPostales= (List<UISelectEntity>) this.attrs.get("allCodigosPostales");
			if(codigosPostales!= null && !codigosPostales.isEmpty()) {
				this.contrato.getDomicilio().setCodigoPostal(codigosPostales.get(0).toString("codigo"));
				this.contrato.getDomicilio().setNuevoCp(true);
				this.contrato.getDomicilio().setIdCodigoPostal(codigosPostales.get(0).getKey());
				this.attrs.put("codigoSeleccionado", codigosPostales.get(0));
			} // if
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // asignaCodigoPostal
  
  public void doLoadCodigosPostales() {
    List<UISelectItem> codigosPostales = null;
    Map<String, Object> params = new HashMap<>();
    try {
			if(!this.contrato.getDomicilio().getIdEntidad().getKey().equals(-1L)) {
				params.put(Constantes.SQL_CONDICION, "id_entidad=" + this.contrato.getDomicilio().getIdEntidad().getKey());
				codigosPostales = UISelect.build("TcManticCodigosPostalesDto", "row", params, "codigo", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
				this.attrs.put("codigosPostales", codigosPostales);
				if (!codigosPostales.isEmpty()) {
					this.contrato.getDomicilio().setCodigoPostal(codigosPostales.get(0).getLabel());
					this.contrato.getDomicilio().setIdCodigoPostal((Long) codigosPostales.get(0).getValue());
					this.contrato.getDomicilio().setNuevoCp(true);
				} // if
				else 
					this.contrato.getDomicilio().setNuevoCp(false);				
			} // if
			else
				this.contrato.getDomicilio().setNuevoCp(false);				
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // doLoadCodigosPostales  
  
  public void doLoadEntidades() {
    List<UISelectEntity> entidades= null;
		List<Columna>columns          = new ArrayList<>();
    Map<String, Object> params    = new HashMap<>();
    try {
      params.put("idPais", 1);
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
      entidades= UIEntity.build("TcJanalEntidadesDto", "comboEntidades", params, columns, Constantes.SQL_TODOS_REGISTROS);
      this.attrs.put("entidades", entidades);
      this.contrato.getDomicilio().setIdEntidad(entidades.get(0));
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
    this.doLoadMunicipios();
  } // doLoadEntidades
  
	private void toAsignaEntidad() {
		Entity domicilio     = null;
		List<Entity>entidades= null;
		try {
			if(!this.contrato.getDomicilio().getIdDomicilio().equals(-1L)) {
				domicilio= this.contrato.getDomicilio().getDomicilio();
				entidades= (List<Entity>) this.attrs.get("entidades");
				for(Entity entidad: entidades) {
					if(entidad.getKey().equals(domicilio.toLong("idEntidad"))) {
						this.contrato.getDomicilio().setIdEntidad(entidad);
            break;
          } // if  
				} // for
			} // if
			else
				this.contrato.getDomicilio().setIdEntidad(new Entity(-1L));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toAsignaEntidad

  public void doLoadMunicipios() {
    List<UISelectEntity> municipios= null;
    Map<String, Object> params= new HashMap<>();
		List<Columna>columns      = new ArrayList<>();
    try {
			if(!this.contrato.getDomicilio().getIdEntidad().getKey().equals(-1L)) {
				params.put("idEntidad", this.contrato.getDomicilio().getIdEntidad().getKey());
				columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
				municipios = UIEntity.build("TcJanalMunicipiosDto", "comboMunicipios", params, columns, Constantes.SQL_TODOS_REGISTROS);
				this.attrs.put("municipios", municipios);
				this.contrato.getDomicilio().setIdMunicipio(municipios.get(0));
			} // if
			else
				this.contrato.getDomicilio().setIdMunicipio(new Entity(-1L));
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
    this.doLoadLocalidades();
  } // doLoadMunicipios
	
	private void toAsignaMunicipio() {
		Entity domicilio      = null;
		List<Entity>municipios= null;
		try {
			if(!this.contrato.getDomicilio().getIdMunicipio().getKey().equals(-1L)) {
				domicilio= this.contrato.getDomicilio().getDomicilio();
				municipios= (List<Entity>) this.attrs.get("municipios");
				for(Entity municipio: municipios) {
					if(municipio.getKey().equals(domicilio.toLong("idMunicipio"))) {
						this.contrato.getDomicilio().setIdMunicipio(municipio);
            break;
          } // if  
				} // for
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toAsignaMunicipio

  public void doLoadLocalidades() {
    List<UISelectEntity> localidades= null;
    Map<String, Object> params= new HashMap<>();
		List<Columna>campos       = new ArrayList<>();
    try {
			if(!this.contrato.getDomicilio().getIdMunicipio().getKey().equals(-1L)) {
				params.put("idMunicipio", this.contrato.getDomicilio().getIdMunicipio().getKey());
				campos.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
				localidades = UIEntity.build("TcJanalLocalidadesDto", "comboLocalidades", params, campos, Constantes.SQL_TODOS_REGISTROS);
				this.attrs.put("localidades", localidades);
				this.contrato.getDomicilio().setLocalidad(localidades.get(0));
				this.contrato.getDomicilio().setIdLocalidad(localidades.get(0).getKey());
			} // if
			else{
				this.contrato.getDomicilio().setLocalidad(new Entity(-1L));
				this.contrato.getDomicilio().setIdLocalidad(-1L);
			} // else
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // doLoadLocalidades
	
	private void toAsignaLocalidad() {
		Entity domicilio       = null;
		List<Entity>localidades= null;
		try {
			if(!this.contrato.getDomicilio().getIdDomicilio().equals(-1L)) {
				domicilio= this.contrato.getDomicilio().getDomicilio();
				localidades= (List<Entity>) this.attrs.get("localidades");
				for(Entity localidad: localidades) {
					if(localidad.getKey().equals(domicilio.toLong("idLocalidad"))) {
						this.contrato.getDomicilio().setIdLocalidad(localidad.getKey());
						this.contrato.getDomicilio().setLocalidad(localidad);
            break;
					} // if
				} // for
			} // if			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toAsignaLocalidad
 
	private void toLoadComplementos() throws Exception {
		MotorBusqueda motor            = null;
		TcManticDomiciliosDto domicilio= null;
		try {
			if (!this.contrato.getDomicilio().getIdDomicilio().equals(-1L)) {
        motor = new MotorBusqueda(this.contrato.getIdContrato());
        domicilio = motor.toDomicilio(this.contrato.getDomicilio().getIdDomicilio());
        this.contrato.getDomicilio().setCodigoPostal(domicilio.getCodigoPostal());
        this.contrato.getDomicilio().setNumeroExterior(domicilio.getNumeroExterior());
        this.contrato.getDomicilio().setNumeroInterior(domicilio.getNumeroInterior());
        this.contrato.getDomicilio().setCalle(domicilio.getCalle());
        this.contrato.getDomicilio().setAsentamiento(domicilio.getAsentamiento());
        this.contrato.getDomicilio().setEntreCalle(domicilio.getEntreCalle());
        this.contrato.getDomicilio().setYcalle(domicilio.getYcalle());
        this.contrato.getDomicilio().setPrincipal(true);
      } // if
      else {
        this.clearAtributos();
      } // else
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
	} // toLoadComplementos  
 
  public void clearAtributos() {
    try {
      this.contrato.getDomicilio().setNumeroExterior("");
      this.contrato.getDomicilio().setNumeroInterior("");
      this.contrato.getDomicilio().setCalle("");
      this.contrato.getDomicilio().setAsentamiento("");
      this.contrato.getDomicilio().setEntreCalle("");
      this.contrato.getDomicilio().setYcalle("");
      this.contrato.getDomicilio().setPrincipal(true);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } // clearAtributos
 
  public void doLoadAtributos(boolean all) {    
		List<Entity> domicilios= null;
    try {
			if(all){
				if(!this.contrato.getDomicilio().getDomicilio().getKey().equals(-1L)){
					domicilios= (List<Entity>) this.attrs.get("domicilios");
					this.contrato.getDomicilio().setDomicilio(domicilios.get(domicilios.indexOf(this.contrato.getDomicilio().getDomicilio())));
					this.contrato.getDomicilio().setIdDomicilio(domicilios.get(domicilios.indexOf(this.contrato.getDomicilio().getDomicilio())).getKey());
				} // if
				else{
					this.contrato.getDomicilio().setDomicilio(new Entity(-1L));
					this.contrato.getDomicilio().setIdDomicilio(-1L);
				} // else		
				this.doLoadEntidades();				
			} // if
      this.toLoadComplementos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } // doLoadAtributos
 
  private void toLoadDomicilios() {
		List<UISelectEntity> domicilios= null;
		try {
			domicilios= new ArrayList<>();
			this.attrs.put("domicilios", domicilios);     
			this.contrato.getDomicilio().setDomicilio(new Entity(-1L, "SELECCIONE"));
      this.contrato.getDomicilio().setIdDomicilio(-1L);
		} // try
		catch (Exception e) {		
			throw e;
		} // catch		
	} // loadDomicilios
 
  private void toLoadTiposDomicilios() {
    List<UISelectItem> tiposDomicilios = null;
    try {
      tiposDomicilios = new ArrayList<>();
      for (ETiposDomicilios tipoDomicilio : ETiposDomicilios.values()) {
        tiposDomicilios.add(new UISelectItem(tipoDomicilio.getKey(), Cadena.reemplazarCaracter(tipoDomicilio.name(), '_', ' ')));
      } // for
      this.attrs.put("tiposDomicilios", tiposDomicilios);
    } // try
    catch (Exception e) {
      throw e;
    } // catch		    
  } // toLoadTiposDomicilios
 
  public void doConsultarContratoDomicilios() {
    Domicilio domicilio= null;    
    try {
      this.contrato.doConsultarContratoDomicilios();
			domicilio = this.contrato.getDomicilioPivote();
      this.contrato.getDomicilio().setIdDomicilio(domicilio.getIdDomicilio());
      if(domicilio.getDomicilio()!= null)
				this.contrato.getDomicilio().setDomicilio(domicilio.getDomicilio());    
			else
				this.contrato.getDomicilio().setDomicilio(new Entity());
      this.contrato.getDomicilio().setIdEntidad(domicilio.getIdEntidad());	
			this.contrato.getDomicilio().getDomicilio().put("idEntidad", new Value("idEntidad", domicilio.getIdEntidad().getKey()));
      this.toAsignaEntidad();
			this.doLoadMunicipios();
      this.contrato.getDomicilio().setIdMunicipio(domicilio.getIdMunicipio());			
			this.contrato.getDomicilio().getDomicilio().put("idMunicipio", new Value("idMunicipio", domicilio.getIdMunicipio().getKey()));
      this.toAsignaMunicipio();
      this.contrato.getDomicilio().setLocalidad(domicilio.getLocalidad());			
      this.contrato.getDomicilio().setIdLocalidad(domicilio.getIdLocalidad());			
			this.contrato.getDomicilio().getDomicilio().put("idLocalidad", new Value("idLocalidad", domicilio.getLocalidad().getKey()));
      this.toAsignaLocalidad();		
      this.contrato.getDomicilio().setCalle(domicilio.getCalle());
      this.contrato.getDomicilio().setNumeroExterior(domicilio.getNumeroExterior());
      this.contrato.getDomicilio().setNumeroInterior(domicilio.getNumeroInterior());
      this.contrato.getDomicilio().setAsentamiento(domicilio.getAsentamiento());
      this.contrato.getDomicilio().setEntreCalle(domicilio.getEntreCalle());
      this.contrato.getDomicilio().setYcalle(domicilio.getYcalle());
      this.contrato.getDomicilio().setIdTipoDomicilio(domicilio.getIdTipoDomicilio());
      this.contrato.getDomicilio().setPrincipal(domicilio.getPrincipal());
			this.contrato.getDomicilio().setCodigoPostal(domicilio.getCodigoPostal());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doConsultarContratoDomicilio	  
 
	public void doInicializaCodigo() {
		try {
			this.contrato.getDomicilio().setIdCodigoPostal(-1L);
			this.contrato.getDomicilio().setCodigoPostal("");
			if((Boolean)this.attrs.get("cpNuevo")) {
				this.contrato.getDomicilio().setNuevoCp(true);		
				this.attrs.put("codigoSeleccionado", new UISelectEntity(-1L));
			} // 				
			else
				this.contrato.getDomicilio().setNuevoCp(false);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);		
		} // catch		
	} // doInicializaCodigo
 
  private void updateCodigoPostal() {
    List<UISelectItem> codigosPostales = null;
    try {
      if (this.contrato.getDomicilio().getIdCodigoPostal().equals(-1L)) {
        this.contrato.getDomicilio().setCodigoPostal("");
        this.contrato.getDomicilio().setNuevoCp(false);
      } // if
      else {
        codigosPostales = (List<UISelectItem>) this.attrs.get("codigosPostales");
        for (UISelectItem codigo : codigosPostales) {
          if (codigo.getValue().equals(this.contrato.getDomicilio().getIdCodigoPostal())) {
            this.contrato.getDomicilio().setCodigoPostal(codigo.getLabel());
            this.contrato.getDomicilio().setNuevoCp(true);
          } // if
        } // for
      } // else
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
  } // updateCodigoPostal
  
  private void toAgregarContrato() {
    try {
      this.contrato.doAgregarContratoDomicilio();
      this.contrato.setDomicilio(new Domicilio());
      this.updateCodigoPostal();
      this.doLoadAtributos(true);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // toAgregarContrato
 
  public void doLookForCodigoPostal() {
		Map<String, Object>params= new HashMap<>();
    try {
      String codigoPostal= this.contrato.getDomicilio().getCodigoPostal();
      if(!Cadena.isVacio(codigoPostal)) {
        params.put("codigo", codigoPostal);			
        Value value= (Value)DaoFactory.getInstance().toField("TcManticCodigosPostalesDto", "unico", params, "idCodigoPostal");
        if(value!= null && value.getData()!= null)
          this.contrato.getDomicilio().setIdCodigoPostal(value.toLong());
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
		finally {
			Methods.clean(params);
		} // finally
  }
 
  public String toColor(Lote row) {
    return row.isMostar()? "": "janal-display-none";
  }
 
  public void doCalculateCosto() {
    // if(this.contrato.getContrato().getCosto()<= 0D) {
      double costo= 0D;
      for (Lote item: this.contrato.getContrato().getLotes()) {
        costo+= item.getCosto();
      } // for
      this.contrato.getContrato().setCosto(Numero.toRedondearSat(costo));
    // } // if
  }
  
	private void toLoadTiposPagos() {
		List<UISelectEntity> tiposMediosPagos= null;
		Map<String, Object>params= new HashMap<>();
		try {
			params.put(Constantes.SQL_CONDICION, "id_cobro_caja= 1");
			tiposMediosPagos= UIEntity.build("TcManticTiposMediosPagosDto", "row", params);
			this.attrs.put("tiposMediosPagos", tiposMediosPagos);
      if(Objects.equals(this.accion, EAccion.AGREGAR)) 
  			this.contrato.getContrato().setIkTipoMedioPago(UIBackingUtilities.toFirstKeySelectEntity(tiposMediosPagos));
      else {
        int index= tiposMediosPagos.indexOf(this.contrato.getContrato().getIkTipoMedioPago());
        if(index>= 0)
    			this.contrato.getContrato().setIkTipoMedioPago(tiposMediosPagos.get(index));
        else 
    			this.contrato.getContrato().setIkTipoMedioPago(UIBackingUtilities.toFirstKeySelectEntity(tiposMediosPagos));
      } // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toLoadTiposPagos
	  
	private void toLoadBancos() {
		List<UISelectEntity> bancos= null;
		Map<String, Object> params = null;
		List<Columna> campos       = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			campos= new ArrayList<>();
			campos.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
			bancos= UIEntity.build("TcManticBancosDto", "row", params, campos, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("bancos", bancos);
      if(Objects.equals(this.accion, EAccion.AGREGAR)) 
  			this.contrato.getContrato().setIkBanco(UIBackingUtilities.toFirstKeySelectEntity(bancos));
      else {
        int index= bancos.indexOf(this.contrato.getContrato().getIkBanco());
        if(index>= 0)
    			this.contrato.getContrato().setIkBanco(bancos.get(index));
        else
    			this.contrato.getContrato().setIkBanco(UIBackingUtilities.toFirstKeySelectEntity(bancos));
      } // if
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // toLoadBancos  
  
  public void doCalculateAnticipo(Boolean porcentaje) {
    if(this.contrato.getContrato().getCosto()> 0D) {
      if(porcentaje) 
        this.contrato.getContrato().setAnticipo(Numero.toRedondearSat(this.contrato.getContrato().getPorcentajeAnticipo()/ 100* this.contrato.getContrato().getCosto()));
      else
        this.contrato.getContrato().setPorcentajeAnticipo(Numero.toRedondearSat(this.contrato.getContrato().getAnticipo()* 100/ this.contrato.getContrato().getCosto()));
      if(this.contrato.getContrato().getPorcentajeAnticipo()> 0D && this.contrato.getContrato().getRetenciones()!= null && !this.contrato.getContrato().getRetenciones().isEmpty()) {
        for (Retencion item: this.contrato.getContrato().getRetenciones()) {
          if(Objects.equals(item.getIdTipoRetencion(), 1L)) {
            item.setPorcentaje(this.contrato.getContrato().getPorcentajeAnticipo());
            this.doRowUpdateCuenta(item, Boolean.TRUE);
          } // if  
        } // for
      } // if
    } // if
  }
 
  public void doCalculateFondoGarantia(Boolean porcentaje) {
    if(this.contrato.getContrato().getCosto()> 0D) {
      if(porcentaje)
        this.contrato.getContrato().setFondoGarantia(Numero.toRedondearSat(this.contrato.getContrato().getPorcentajeFondo()/ 100* this.contrato.getContrato().getCosto()));
      else
        this.contrato.getContrato().setPorcentajeFondo(Numero.toRedondearSat(this.contrato.getContrato().getFondoGarantia()* 100/ this.contrato.getContrato().getCosto()));
      if(this.contrato.getContrato().getPorcentajeFondo()> 0D && this.contrato.getContrato().getRetenciones()!= null && !this.contrato.getContrato().getRetenciones().isEmpty()) {
        for (Retencion item: this.contrato.getContrato().getRetenciones()) {
          if(Objects.equals(item.getIdTipoRetencion(), 2L)) {
            item.setPorcentaje(this.contrato.getContrato().getPorcentajeFondo());
            this.doRowUpdateCuenta(item, Boolean.TRUE);
          } // if  
        } // for
      } // if
    } // if
  }
 
	public void doCheckMedioPago() {
		Long tipoMedioPago= this.contrato.getContrato().getIkTipoMedioPago().getKey();
		try {
			this.attrs.put("mostrarBanco", !ETipoMediosPago.EFECTIVO.getIdTipoMedioPago().equals(tipoMedioPago));
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	} // doCheckMedioPago  
 
  public void doRowUpdateCuenta(Retencion row, Boolean porcentaje)  {
    try { 
      if(row!= null) {
        if(this.contrato.getContrato().getCosto()== null || this.contrato.getContrato().getCosto()<= 0D)
          this.contrato.getContrato().setCosto(row.getImporte());
        if(porcentaje) {
          row.setPorcentaje(Numero.toRedondearSat(row.getPorcentaje()));
          row.setImporte(Numero.toRedondearSat(row.getPorcentaje()* this.contrato.getContrato().getCosto()/ 100D));
        } // if
        else {
          row.setImporte(Numero.toRedondearSat(row.getImporte()));
          row.setPorcentaje(Numero.toRedondearSat(row.getImporte()* 100D/ this.contrato.getContrato().getCosto()));
        } // else
        this.doUpdateAccion(row);
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doUpdateAccion(Retencion row) {
    if(row!= null && Objects.equals(row.getSql(), ESql.SELECT))
      row.setSql(ESql.UPDATE);
  }
 
  public void doUpdateCosto() {
    if(this.contrato.getContrato().getCosto()== null || this.contrato.getContrato().getCosto()> 0D) {
       this.contrato.getContrato().setAnticipo(Numero.toRedondearSat(this.contrato.getContrato().getPorcentajeAnticipo()/ 100* this.contrato.getContrato().getCosto()));
       this.contrato.getContrato().setFondoGarantia(Numero.toRedondearSat(this.contrato.getContrato().getPorcentajeFondo()/ 100* this.contrato.getContrato().getCosto()));
       if(this.contrato.getContrato().getRetenciones()!= null && !this.contrato.getContrato().getRetenciones().isEmpty()) {
        for (Retencion item: this.contrato.getContrato().getRetenciones()) {
          this.doRowUpdateCuenta(item, Boolean.TRUE);
        } // for
      } // if
    } // if
  }
 
  public void doTabChange(TabChangeEvent event) {
		// if(event.getTab().getTitle().equals("Retenciones"))
			// UIBackingUtilities.execute("");
	}
 
  public void doUpdateGlobal() {
    Double total= this.contrato.getContrato().getMateriales()+ 
            this.contrato.getContrato().getDestajos()+
            this.contrato.getContrato().getSubcontratados()+
            this.contrato.getContrato().getPorElDia()+
            this.contrato.getContrato().getAdministrativos()+
            this.contrato.getContrato().getMaquinaria()+ 
            this.contrato.getContrato().getIndirecto()+ 
            this.contrato.getContrato().getUtilidad();
    this.attrs.put("total", total);
    this.attrs.put("diferencia", total- this.contrato.getContrato().getCosto());
  }
  
}