package mx.org.kaana.mantic.catalogos.personas.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.procesos.enums.ESemaforos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.clientes.beans.Domicilio;
import mx.org.kaana.mantic.catalogos.personas.beans.Especialidad;
import mx.org.kaana.mantic.catalogos.personas.beans.PersonaAlimenticia;
import mx.org.kaana.mantic.catalogos.personas.beans.PersonaBeneficiario;
import mx.org.kaana.mantic.catalogos.personas.beans.RegistroPersona;
import mx.org.kaana.mantic.catalogos.personas.reglas.MotorBusqueda;
import mx.org.kaana.mantic.catalogos.personas.reglas.Transaccion;
import mx.org.kaana.mantic.db.dto.TcManticDomiciliosDto;
import mx.org.kaana.mantic.enums.ETipoPersona;
import mx.org.kaana.mantic.enums.ETiposContactos;
import mx.org.kaana.mantic.enums.ETiposDomicilios;
import org.primefaces.event.TabChangeEvent;


@Named(value = "manticCatalogosPersonasAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639367L;
	private static final String TEMA   = "sentinel";
	private static final String GERENTE= "GERENTE";
	private RegistroPersona registroPersona;
	private UISelectEntity domicilioBusqueda;
	private FormatLazyModel lazyModelBitacora;

	public RegistroPersona getRegistroPersona() {
		return registroPersona;
	}

	public void setRegistroPersona(RegistroPersona registroPersona) {
		this.registroPersona = registroPersona;
	}		

	public UISelectEntity getDomicilioBusqueda() {
		return domicilioBusqueda;
	}

	public void setDomicilioBusqueda(UISelectEntity domicilioBusqueda) {
		this.domicilioBusqueda = domicilioBusqueda;
	}

	public FormatLazyModel getLazyModelBitacora() {
		return lazyModelBitacora;
	}

	public void setLazyModelBitacora(FormatLazyModel lazyModelBitacora) {
		this.lazyModelBitacora = lazyModelBitacora;
	}	
	
  public Boolean getHabilitar() {
    return Objects.equals(Configuracion.getInstance().getPropiedad("sistema.empresa.principal"), "cafu");
  }
  
  @PostConstruct
  @Override
  protected void init() {		
    try {			
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("tipoPersona", JsfBase.getFlashAttribute("tipoPersona"));
      this.attrs.put("idPersona", JsfBase.getFlashAttribute("idPersona"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("general", this.attrs.get("tipoPersona")== null);
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());					
			this.attrs.put("mostrarPuestos", Long.valueOf(this.attrs.get("tipoPersona").toString()).equals(ETipoPersona.EMPLEADO.getIdTipoPersona()));			
			this.attrs.put("mostrarProveedores", (Long.valueOf(this.attrs.get("tipoPersona").toString()).equals(ETipoPersona.AGENTE_VENTAS.getIdTipoPersona())));			
			this.attrs.put("mostrarClientes", (Long.valueOf(this.attrs.get("tipoPersona").toString()).equals(ETipoPersona.REPRESENTANTE_LEGAL.getIdTipoPersona())));						
			for(ETipoPersona tipoPersona: ETipoPersona.values()){
				if(tipoPersona.getIdTipoPersona().equals(Long.valueOf(this.attrs.get("tipoPersona").toString())))
					this.attrs.put("catalogo", Cadena.reemplazarCaracter(tipoPersona.name().toLowerCase(), '_' , ' '));
			} // for			
			this.doLoad();						
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void loadCollections() throws Exception {		
		this.loadEmpresas();		
		this.loadBancos();
		this.loadTitulos();
		this.loadTiposPersonas();   
		this.loadTiposContactos();
		this.loadTiposDomicilios();
		this.loadEntidades();
		this.loadMunicipios();
		this.loadLocalidades();		
		this.loadDomicilios();
		if((boolean)this.attrs.get("mostrarClientes"))
			this.loadClientes();
		if((boolean)this.attrs.get("mostrarProveedores"))
			this.loadProveedores();
		this.loadEstadosCiviles();
		this.loadTiposParentescos();
    this.toLoadTiposPagos();
	} // loadCollections	
	
	private void toLoadDepartamentos() {
		List<UISelectItem> departamentos= null;
		Map<String, Object> params      = new HashMap<>();		
		try {
      switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
        case "cafu":
          params.put("idAgrupacion", this.registroPersona.getIdAgrupacion());			
          params.put("idPuesto", this.registroPersona.getIdPuesto());			
          departamentos= UIEntity.build("TcKeetDepartamentosDto", "agrupacion", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS);
          break;
        case "gylvi": 
        case "triana":
          params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);			
          departamentos= UIEntity.build("TcKeetDepartamentosDto", "row", params, Collections.EMPTY_LIST, Constantes.SQL_TODOS_REGISTROS);
          break;
      } // swtich
			this.attrs.put("departamentos", departamentos);
      if(!EAccion.AGREGAR.equals((EAccion)this.attrs.get("accion"))) {       
        int count= 0;
        for (Especialidad item: this.registroPersona.getEspecialidades()) {
          int index= departamentos.indexOf(new UISelectEntity(item.getIdDepartamento()));
          if(index>= 0)
            count++;
        } // for
        if(count> 0) {
          this.registroPersona.setDepartamentos(new Object[count]);
          count= 0;
          for (Especialidad item: this.registroPersona.getEspecialidades()) {
            int index= departamentos.indexOf(new UISelectEntity(item.getIdDepartamento()));
            if(index>= 0)
              this.registroPersona.getDepartamentos()[count++]= departamentos.get(index);
          } // for
        } // if  
        if(count== 0)
          this.registroPersona.setDepartamentos(new Object[] {});
      } // if
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally		
	} 
	
	private void loadTiposParentescos(){
		List<UISelectItem> parentescos= null;
		Map<String, Object> params    = new HashMap<>();		
		try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);			
			parentescos= UISelect.build("TcKeetTiposParentescosDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("parentescos", parentescos);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally		
	}
	
	private void loadBancos() {
		List<UISelectItem> bancos = null;
		Map<String, Object> params= new HashMap<>();		
		try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);			
			bancos= UISelect.build("TcManticBancosDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("bancos", bancos);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadBancos
	
	private void loadEmpresas() {
		Map<String, Object>params= new HashMap<>();
		List<Columna> columns    = new ArrayList<>();
		try {
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());			
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));
			this.attrs.put("idEmpresa", UIBackingUtilities.toFirstKeySelectEntity((List<UISelectEntity>)this.attrs.get("sucursales")));			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch				
	} 
	
	public void doLoadCategorias() {
		List<UISelectItem> categorias= null;
    Map<String, Object> params   = new HashMap<>();
    try {
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      categorias = UISelect.build("TcKeetAgrupacionesDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
			if(!categorias.isEmpty()) {
				this.attrs.put("categorias", categorias);
        if(EAccion.AGREGAR.equals((EAccion)this.attrs.get("accion"))) {
				  this.attrs.put("idAgrupacion", UIBackingUtilities.toFirstKeySelectItem(categorias));
          this.registroPersona.setIdAgrupacion((Long)this.attrs.get("idAgrupacion"));
        } // if  
        else
          this.attrs.put("idAgrupacion", categorias.get(categorias.indexOf(new UISelectItem(this.registroPersona.getIdAgrupacion()))));
			} // if
      this.toLoadPuestos();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
	} 
  
	private void toLoadPuestos() {
		List<UISelectItem> puestos= null;
    Map<String, Object> params= new HashMap<>();
    try {
      switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
        case "cafu":
          params.put("idAgrupacion", this.registroPersona.getIdAgrupacion());
          puestos = UISelect.build("TcManticPuestosDto", "agrupacion", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
          break;
        case "gylvi": 
        case "triana":
          params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
          puestos = UISelect.build("TcManticPuestosDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
          break;
      } // swtich
			if(!Objects.equals(puestos, null) && !puestos.isEmpty()) {
				this.attrs.put("puestos", puestos);
        if(EAccion.AGREGAR.equals((EAccion)this.attrs.get("accion"))) {
				  this.attrs.put("idPuesto", UIBackingUtilities.toFirstKeySelectItem(puestos));
          this.registroPersona.setIdPuesto((Long)this.attrs.get("idPuesto"));
        } // if  
        else {
          int index= puestos.indexOf(new UISelectItem(this.registroPersona.getIdPuesto()));
          if(index> 0)
            this.attrs.put("idPuesto", puestos.get(index));
          else {
            this.attrs.put("idPuesto", UIBackingUtilities.toFirstKeySelectItem(puestos));
            this.registroPersona.setIdPuesto((Long)this.attrs.get("idPuesto"));
          } // else
        } // else  
			} // if
      this.toLoadDepartamentos();
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
	} 
	
	private void loadEstadosCiviles() {
		List<UISelectItem> civiles= null;
    Map<String, Object> params= new HashMap<>();
    try {
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      civiles = UISelect.build("TcManticEstadosCivilesDto", "all", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
			if(!civiles.isEmpty()) {
				this.attrs.put("estadosCiviles", civiles);
				this.attrs.put("idEstadoCivil", UIBackingUtilities.toFirstKeySelectItem(civiles));
			} // if
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
	} // loadPuestos
	
	private void loadContratistas() {
		List<UISelectEntity>contratistas= null;		
		List<Columna> columns           = new ArrayList<>();
		try {
			columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			contratistas= UIEntity.seleccione("VistaPersonasDto", "contratistas", Collections.EMPTY_MAP, columns, Constantes.SQL_TODOS_REGISTROS, "nombres");
			this.attrs.put("contratistas", contratistas);
      if(EAccion.AGREGAR.equals((EAccion)this.attrs.get("accion")))
  			this.attrs.put("idContratista", UIBackingUtilities.toFirstKeySelectEntity(contratistas));
      else {
        int index= contratistas.indexOf(new UISelectEntity(this.registroPersona.getIdContratista()));
        if(index>= 0)
          this.attrs.put("idContratista", contratistas.get(index));
        else
    			this.attrs.put("idContratista", UIBackingUtilities.toFirstKeySelectEntity(contratistas));
      } // if
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // loadContratistas
	
  public void doLoad() {
    EAccion eaccion= null;
    Long idPersona = -1L;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
			this.attrs.put("mostrarBitacora", !eaccion.equals(EAccion.AGREGAR));			
      switch (eaccion) {
        case AGREGAR:											
          this.registroPersona= new RegistroPersona();			
					this.registroPersona.getDeudor().setLimite(10000D);
					this.loadCollections();
					if(this.attrs.get("tipoPersona")!= null)
						this.registroPersona.getPersona().setIdTipoPersona(Long.valueOf(this.attrs.get("tipoPersona").toString()));	
					this.registroPersona.getEmpresaPersona().setIdActivo(1L);
    		  this.registroPersona.getEmpresaPersona().setIdContrato(2L);
    		  this.registroPersona.getEmpresaPersona().setIdSeguro(2L);
    		  this.registroPersona.getEmpresaPersona().setIdNomina(2L);
    		  this.registroPersona.getEmpresaPersona().setIdPuesto(26L);
          break;
        case MODIFICAR:					
        case CONSULTAR:					
          idPersona= Long.valueOf(this.attrs.get("idPersona").toString());					
          this.registroPersona= new RegistroPersona(idPersona);					
					this.loadCollections();
					if(!this.registroPersona.getPersonasDomicilio().isEmpty()){
						this.registroPersona.setPersonaDomicilioSeleccion(this.registroPersona.getPersonasDomicilio().get(0));
						this.doConsultarPersonaDomicilio();
					} // if
					if(!this.registroPersona.getPersonasBeneficiarios().isEmpty()){
						this.registroPersona.setPersonaBeneficiarioSeleccion(this.registroPersona.getPersonasBeneficiarios().get(0));	
						this.doConsultarBeneficiario();
					} // if					
					if(!this.registroPersona.getPersonasAlimenticias().isEmpty()){
						this.registroPersona.setPersonaAlimenticiaSeleccion(this.registroPersona.getPersonasAlimenticias().get(0));	
						this.doConsultarAlimenticia();
					} // if					
					this.attrs.put("idEmpresa", new UISelectEntity(this.registroPersona.getIdEmpresa()));					
          if(this.registroPersona.getIdContratista()== null)
            this.registroPersona.setIdContratista(-1L);
          break;
      } // switch
			this.registroPersona.getPersona().setEstilo(TEMA);
			this.registroPersona.getPersona().setIdUsuario(JsfBase.getIdUsuario());
      this.doLoadCategorias();
      this.loadContratistas();
			this.doActualizaIconEstatus();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

	public void doActualizaIconEstatus() {
		String icon= ESemaforos.ROJO.getNombre();
		if(this.registroPersona.isActivo() && !Cadena.isVacio(this.registroPersona.getEmpresaPersona().getNss()) && this.registroPersona.getEmpresaPersona().getIdNomina().equals(2L))
			icon= ESemaforos.AZUL.getNombre();
		else if(this.registroPersona.isActivo() && !Cadena.isVacio(this.registroPersona.getEmpresaPersona().getNss()))
			icon= ESemaforos.VERDE.getNombre();
		else if(this.registroPersona.isActivo() && Cadena.isVacio(this.registroPersona.getEmpresaPersona().getNss()))
			icon= ESemaforos.AMARILLO.getNombre();
		this.attrs.put("iconEstatus", icon);
	} // doActualizaIconEstatus
	
  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
		EAccion eaccion        = null;		
		Entity persona         = null;
    try {					
			this.registroPersona.setIdEmpresa(((UISelectEntity)this.attrs.get("idEmpresa")).getKey());
			if(!Cadena.isVacio(this.attrs.get("idContratista")) && ((UISelectEntity)this.attrs.get("idContratista")).getKey()>= 1L)
				this.registroPersona.getEmpresaPersona().setIdContratista(((UISelectEntity)this.attrs.get("idContratista")).getKey());
			else
				this.registroPersona.getEmpresaPersona().setIdContratista(null);
			eaccion= (EAccion) this.attrs.get("accion");
			transaccion = new Transaccion(this.registroPersona);
			if(Boolean.valueOf(this.attrs.get("mostrarProveedores").toString())) {
				persona= (Entity) this.attrs.get("proveedor");
				transaccion = new Transaccion(this.registroPersona, persona.getKey());
			} // if
			if(Boolean.valueOf(this.attrs.get("mostrarClientes").toString())) {
				persona= (Entity) this.attrs.get("cliente");
				transaccion = new Transaccion(this.registroPersona, persona.getKey());
			} // if
			if (transaccion.ejecutar(eaccion)) {
				JsfBase.setFlashAttribute("idPersona", this.registroPersona.getPersona().getKey());
				regresar = this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agreg�" : "modifico").concat(" la persona de forma correcta.").concat(transaccion.getCuenta()!=null ? "\nCuenta de acceso [" + transaccion.getCuenta() + "]" : ""), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurri� un error al registrar la persona", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
		JsfBase.setFlashAttribute("idPersona", this.registroPersona.getPersona().getKey());
    return Cadena.isVacio(this.attrs.get("retorno"))? "filtro".concat(Constantes.REDIRECIONAR): this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);
  } // doAccion
	
	private void loadTiposPersonas() throws Exception {
		List<UISelectItem>  tiposPersonas= new ArrayList<>();
		for(ETipoPersona tipoPersona: ETipoPersona.values())
			tiposPersonas.add(new UISelectItem(tipoPersona.getIdTipoPersona(), Cadena.reemplazarCaracter(tipoPersona.name(), '_', ' ')));
		this.attrs.put("tiposPersonas", tiposPersonas);
	} // loadTiposPersonas
	
	private void loadTitulos() {
    List<UISelectItem> titulos= null;
    Map<String, Object> params= new HashMap<>();
    EAccion eaccion           = null;
    try {
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      titulos = UISelect.build("TcManticPersonasTitulosDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
      this.attrs.put("titulos", titulos);
      eaccion = (EAccion) this.attrs.get("accion");
      if (eaccion.equals(EAccion.AGREGAR)) 
        this.registroPersona.getPersona().setIdPersonaTitulo((Long) UIBackingUtilities.toFirstKeySelectItem(titulos));			
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // loadTitulos
	
	private void loadTiposContactos() throws Exception {
		List<UISelectItem> tiposContactos= new ArrayList<>();
		for (ETiposContactos tipoContacto : ETiposContactos.values()) {
			tiposContactos.add(new UISelectItem(tipoContacto.getKey(), Cadena.reemplazarCaracter(tipoContacto.name(), '_', ' ')));
		} // for
		this.attrs.put("tiposContactos", tiposContactos);
  } // loadTiposContactos

  private void loadTiposDomicilios() throws Exception {
		List<UISelectItem> tiposDomicilios = new ArrayList<>();
    try {
      for (ETiposDomicilios tipoDomicilio : ETiposDomicilios.values()) {
        tiposDomicilios.add(new UISelectItem(tipoDomicilio.getKey(), Cadena.reemplazarCaracter(tipoDomicilio.name(), '_', ' ')));
      } // for
      this.attrs.put("tiposDomicilios", tiposDomicilios);
    } // try
    catch (Exception e) {
      throw e;
    } // catch
  } // loadTiposDomicilios

  private void loadEntidades() {
    List<UISelectEntity> entidades= null;
    Map<String, Object> params    = new HashMap<>();
		List<Columna>columns          = new ArrayList<>();
    try {
      params.put("idPais", 1);
      params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
      entidades = UIEntity.build("TcJanalEntidadesDto", "comboEntidades", params, columns, Constantes.SQL_TODOS_REGISTROS);
      this.attrs.put("entidades", entidades);    
      this.registroPersona.getDomicilio().setIdEntidad(entidades.get(0));
    } // try
    finally {
      Methods.clean(params);
    } // finally
  } // loadEntidades

	private void toAsignaEntidad(){
		Entity domicilio     = null;
		List<Entity>entidades= null;
		try {
			if(!this.registroPersona.getDomicilio().getIdDomicilio().equals(-1L)){
				domicilio= this.registroPersona.getDomicilio().getDomicilio();
				entidades= (List<Entity>) this.attrs.get("entidades");
				for(Entity entidad: entidades){
					if(entidad.getKey().equals(domicilio.toLong("idEntidad")))
						this.registroPersona.getDomicilio().setIdEntidad(entidad);
				} // for
			} // if
			else
				this.registroPersona.getDomicilio().setIdEntidad(new Entity(-1L));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toAsignaEntidad
	
  private void loadMunicipios() {
    List<UISelectEntity> municipios= null;
    Map<String, Object> params     = null;
		List<Columna>campos            = null;
    try {
			if(!this.registroPersona.getDomicilio().getIdEntidad().getKey().equals(-1L)){
				params = new HashMap<>();
				params.put("idEntidad", this.registroPersona.getDomicilio().getIdEntidad().getKey());
				campos= new ArrayList<>();
				campos.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
				municipios = UIEntity.build("TcJanalMunicipiosDto", "comboMunicipios", params, campos, Constantes.SQL_TODOS_REGISTROS);
				this.attrs.put("municipios", municipios);
				this.registroPersona.getDomicilio().setIdMunicipio(municipios.get(0));
			} // if
			else
				this.registroPersona.getDomicilio().setIdMunicipio(new Entity(-1L));
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // loadMunicipios

  private void toAsignaMunicipio(){
		Entity domicilio      = null;
		List<Entity>municipios= null;
		try {
			if(!this.registroPersona.getDomicilio().getIdMunicipio().getKey().equals(-1L)){
				domicilio= this.registroPersona.getDomicilio().getDomicilio();
				municipios= (List<Entity>) this.attrs.get("municipios");
				for(Entity municipio: municipios){
					if(municipio.getKey().equals(domicilio.toLong("idMunicipio")))
						this.registroPersona.getDomicilio().setIdMunicipio(municipio);
				} // for
			} // if
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toAsignaMunicipio

  private void loadLocalidades() {
    List<UISelectEntity> localidades= null;
    Map<String, Object> params      = null;
		List<Columna>campos             = null;
    try {
			if(!this.registroPersona.getDomicilio().getIdMunicipio().getKey().equals(-1L)){
				params = new HashMap<>();
				params.put("idMunicipio", this.registroPersona.getDomicilio().getIdMunicipio().getKey());
				campos= new ArrayList<>();
				campos.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));
				localidades = UIEntity.build("TcJanalLocalidadesDto", "comboLocalidades", params, campos, Constantes.SQL_TODOS_REGISTROS);
				this.attrs.put("localidades", localidades);
				this.registroPersona.getDomicilio().setLocalidad(localidades.get(0));
				this.registroPersona.getDomicilio().setIdLocalidad(localidades.get(0).getKey());
			} // if
			else{
				this.registroPersona.getDomicilio().setLocalidad(new Entity(-1L));
				this.registroPersona.getDomicilio().setIdLocalidad(-1L);
			} // else
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // loadLocalidades
	
	private void toAsignaLocalidad(){
		Entity domicilio       = null;
		List<Entity>localidades= null;
		try {
			if(!this.registroPersona.getDomicilio().getIdDomicilio().equals(-1L)){
				domicilio= this.registroPersona.getDomicilio().getDomicilio();
				localidades= (List<Entity>) this.attrs.get("localidades");
				for(Entity localidad: localidades){
					if(localidad.getKey().equals(domicilio.toLong("idLocalidad"))){
						this.registroPersona.getDomicilio().setIdLocalidad(localidad.getKey());
						this.registroPersona.getDomicilio().setLocalidad(localidad);
					} // if
				} // for
			} // if			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toAsignaLocalidad

  private void loadCodigosPostales() {
    List<UISelectItem> codigosPostales= null;
    Map<String, Object> params        = null;
    try {
			if(!this.registroPersona.getDomicilio().getIdEntidad().getKey().equals(-1L)){
				params = new HashMap<>();
				params.put(Constantes.SQL_CONDICION, "id_entidad=" + this.registroPersona.getDomicilio().getIdEntidad().getKey());
				codigosPostales = UISelect.seleccione("TcManticCodigosPostalesDto", "row", params, "codigo", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
				this.attrs.put("codigosPostales", codigosPostales);
				if (!codigosPostales.isEmpty()) {
					this.registroPersona.getDomicilio().setCodigoPostal(codigosPostales.get(0).getLabel());
					this.registroPersona.getDomicilio().setIdCodigoPostal((Long) codigosPostales.get(0).getValue());
					this.registroPersona.getDomicilio().setNuevoCp(true);
				} // if
				else 
					this.registroPersona.getDomicilio().setNuevoCp(false);				
			} // if
			else
				this.registroPersona.getDomicilio().setNuevoCp(false);				
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // loadCodigosPostales

	private void toAsignaCodigoPostal(){
		Entity domicilio         = null;
		List<UISelectItem>codigos= null;
		int count                = 0;
		try {
			if(!this.registroPersona.getDomicilio().getIdDomicilio().equals(-1L)){
				domicilio= this.registroPersona.getDomicilio().getDomicilio();
				codigos= (List<UISelectItem>) this.attrs.get("codigosPostales");
				for(UISelectItem codigo: codigos){
					if(codigo.getLabel().equals(domicilio.toString("codigoPostal"))){
						this.registroPersona.getDomicilio().setCodigoPostal(codigo.getLabel());
						this.registroPersona.getDomicilio().setIdCodigoPostal((Long) codigo.getValue());
						this.registroPersona.getDomicilio().setNuevoCp(true);
						count++;
					} // if
				} // for
				if(count==0){
					this.registroPersona.getDomicilio().setNuevoCp(false);
					this.registroPersona.getDomicilio().setIdCodigoPostal(-1L);
					this.registroPersona.getDomicilio().setCodigoPostal("");
				} // if
			} // if
			else{
				this.registroPersona.getDomicilio().setNuevoCp(false);
				this.registroPersona.getDomicilio().setIdCodigoPostal(-1L);
				this.registroPersona.getDomicilio().setCodigoPostal("");
			} // else
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toAsignaCodigoPostal

  public void doLoadDomicilios() {
    try {
      updateCodigoPostal();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoadDomicilios

  private void loadDomicilios() {
		List<UISelectEntity> domicilios= new ArrayList<>();
		try {
			this.attrs.put("domicilios", domicilios);     
			this.registroPersona.getDomicilio().setDomicilio(new Entity(-1L));
      this.registroPersona.getDomicilio().setIdDomicilio(-1L);
		} // try
		catch (Exception e) {		
			throw e;
		} // catch		
	} // loadDomicilios
	
  public void doBusquedaDomicilios() {
    List<UISelectEntity> domicilios= null;
    Map<String, Object> params     = null;
		List<Columna>campos            = null;
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
      this.registroPersona.getDomicilio().setDomicilio(new Entity(-1L));
      this.registroPersona.getDomicilio().setIdDomicilio(-1L);
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
			this.registroPersona.getDomicilio().setDomicilio(domicilio);
      this.registroPersona.getDomicilio().setIdDomicilio(domicilio.getKey());
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
	}
	
  private void updateCodigoPostal() {
    List<UISelectItem> codigosPostales = null;
    try {
      if (this.registroPersona.getDomicilio().getIdCodigoPostal().equals(-1L)) {
        this.registroPersona.getDomicilio().setCodigoPostal("");
        this.registroPersona.getDomicilio().setNuevoCp(false);
      } // if
      else {
        codigosPostales = (List<UISelectItem>) this.attrs.get("codigosPostales");
        for (UISelectItem codigo : codigosPostales) {
          if (codigo.getValue().equals(this.registroPersona.getDomicilio().getIdCodigoPostal())) {
            this.registroPersona.getDomicilio().setCodigoPostal(codigo.getLabel());
            this.registroPersona.getDomicilio().setNuevoCp(true);
          } // if
        } // for
      } // else
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
  } // updateCodigoPostal

  public void doActualizaMunicipios() {
    try {
      loadMunicipios();
      loadLocalidades();      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doActualizaMunicipios

  public void doActualizaLocalidades() {
    try {
      loadLocalidades();      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doActualizaMunicipios

  public void doActualizaCodigosPostales() {
    try {
      loadCodigosPostales();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doActualizaMunicipios

  public void doLoadAtributos() {
		doLoadAtributos(true);
	} // doLoadAtributos
	
  public void doLoadAtributos(boolean all) {    
		List<Entity> domicilios= null;
    try {
			if(all){
				if(!this.registroPersona.getDomicilio().getDomicilio().getKey().equals(-1L)){
					domicilios= (List<Entity>) this.attrs.get("domicilios");
					this.registroPersona.getDomicilio().setDomicilio(domicilios.get(domicilios.indexOf(this.registroPersona.getDomicilio().getDomicilio())));
					this.registroPersona.getDomicilio().setIdDomicilio(domicilios.get(domicilios.indexOf(this.registroPersona.getDomicilio().getDomicilio())).getKey());
				} // if
				else{
					this.registroPersona.getDomicilio().setDomicilio(new Entity(-1L));
					this.registroPersona.getDomicilio().setIdDomicilio(-1L);
				} // else					
				loadEntidades();				
				loadMunicipios();				
				loadLocalidades();				
			} // if
      loadAtributosComplemento();
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
			if (!this.registroPersona.getDomicilio().getIdDomicilio().equals(-1L)) {
        motor = new MotorBusqueda(this.registroPersona.getIdPersona());
        domicilio = motor.toDomicilio(this.registroPersona.getDomicilio().getIdDomicilio());
        this.registroPersona.getDomicilio().setNumeroExterior(domicilio.getNumeroExterior());
        this.registroPersona.getDomicilio().setNumeroInterior(domicilio.getNumeroInterior());
        this.registroPersona.getDomicilio().setCalle(domicilio.getCalle());
        this.registroPersona.getDomicilio().setAsentamiento(domicilio.getAsentamiento());
        this.registroPersona.getDomicilio().setEntreCalle(domicilio.getEntreCalle());
        this.registroPersona.getDomicilio().setYcalle(domicilio.getYcalle());
      } // if
      else {
        clearAtributos();
      } // else
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
	} 
	
  public void clearAtributos() {
    try {
      this.registroPersona.getDomicilio().setNumeroExterior("");
      this.registroPersona.getDomicilio().setNumeroInterior("");
      this.registroPersona.getDomicilio().setCalle("");
      this.registroPersona.getDomicilio().setAsentamiento("");
      this.registroPersona.getDomicilio().setEntreCalle("");
      this.registroPersona.getDomicilio().setYcalle("");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } 

  public void doAgregarCliente() {
    try {
      this.registroPersona.doAgregarPersonaDomicilio();
      this.registroPersona.setDomicilio(new Domicilio());
      loadEntidades();
      doActualizaMunicipios();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doAgregarCliente

  public void doConsultarPersonaDomicilio() {
    Domicilio domicilio= null;    
    try {
      this.registroPersona.doConsultarPersonaDomicilio();
			domicilio = this.registroPersona.getDomicilioPivote();
      this.registroPersona.getDomicilio().setIdDomicilio(domicilio.getIdDomicilio());
			if(domicilio.getDomicilio()!= null)
				this.registroPersona.getDomicilio().setDomicilio(domicilio.getDomicilio());    
			else
				this.registroPersona.getDomicilio().setDomicilio(new Entity());
      this.registroPersona.getDomicilio().setIdEntidad(domicilio.getIdEntidad());	
			this.registroPersona.getDomicilio().getDomicilio().put("idEntidad", new Value("idEntidad", domicilio.getIdEntidad().getKey()));
      this.toAsignaEntidad();
			this.loadMunicipios();
      this.registroPersona.getDomicilio().setIdMunicipio(domicilio.getIdMunicipio());			
			this.registroPersona.getDomicilio().getDomicilio().put("idMunicipio", new Value("idMunicipio", domicilio.getIdMunicipio().getKey()));
      this.toAsignaMunicipio();
			this.loadLocalidades();
      this.registroPersona.getDomicilio().setLocalidad(domicilio.getLocalidad());			
      this.registroPersona.getDomicilio().setIdLocalidad(domicilio.getIdLocalidad());			
			this.registroPersona.getDomicilio().getDomicilio().put("idLocalidad", new Value("idLocalidad", domicilio.getLocalidad().getKey()));
      toAsignaLocalidad();			
      this.registroPersona.getDomicilio().setCalle(domicilio.getCalle());
      this.registroPersona.getDomicilio().setNumeroExterior(domicilio.getNumeroExterior());
      this.registroPersona.getDomicilio().setNumeroInterior(domicilio.getNumeroInterior());
      this.registroPersona.getDomicilio().setAsentamiento(domicilio.getAsentamiento());
      this.registroPersona.getDomicilio().setEntreCalle(domicilio.getEntreCalle());
      this.registroPersona.getDomicilio().setYcalle(domicilio.getYcalle());
      this.registroPersona.getDomicilio().setIdTipoDomicilio(domicilio.getIdTipoDomicilio());
      this.registroPersona.getDomicilio().setPrincipal(domicilio.getPrincipal());
      this.registroPersona.getDomicilio().setCodigoPostal(domicilio.getCodigoPostal());			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 
	
	public void doActualizaDomicilio(){
		try {
			this.registroPersona.doActualizarPersonaDomicilio();
			this.registroPersona.setDomicilio(new Domicilio());
      loadDomicilios();
      doLoadAtributos(true);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	} 
	
	public void doEliminarDomicilio() {
		try {
			this.registroPersona.doEliminarPersonaDomicilio();
			this.registroPersona.setDomicilio(new Domicilio());
      this.loadDomicilios();
      this.doLoadAtributos(true);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	} 
	
	public void doAgregarBeneficiario() {
    try {
      this.registroPersona.doAgregarPersonaBeneficiario();
      this.registroPersona.setPersonaBeneficiario(new PersonaBeneficiario());      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 
	
	public void doActualizaBeneficiario() {
		try {
			this.registroPersona.doActualizarPersonaBeneficiario();
			this.registroPersona.setPersonaBeneficiario(new PersonaBeneficiario());      
      this.toLoadAtributosBeneficiario();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	} 
	
	public void doEliminarBeneficiario() {
		try {
			this.registroPersona.doEliminarPersonaBeneficiario();
			this.registroPersona.setPersonaBeneficiario(new PersonaBeneficiario());      
      this.toLoadAtributosBeneficiario();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	} 
	
	public void doConsultarBeneficiario() {
    PersonaBeneficiario beneficiario= null;    
    try {
      this.registroPersona.doConsultarPersonaBeneficiario();
			beneficiario = this.registroPersona.getBeneficiarioPivote();        
      this.registroPersona.getPersonaBeneficiario().setNombre(beneficiario.getNombre());
      this.registroPersona.getPersonaBeneficiario().setPaterno(beneficiario.getPaterno());
      this.registroPersona.getPersonaBeneficiario().setMaterno(beneficiario.getMaterno());
      this.registroPersona.getPersonaBeneficiario().setFechaNacimiento(beneficiario.getFechaNacimiento());      
      this.registroPersona.getPersonaBeneficiario().setIdTipoParentesco(beneficiario.getIdTipoParentesco());      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 
	
	private void toLoadAtributosBeneficiario() throws Exception{		
		try {
			if (!this.registroPersona.getPersonaBeneficiario().isValid()) 
        this.clearAtributosBeneficiario();      
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
	} 
	
  public void clearAtributosBeneficiario() {
    try {
      this.registroPersona.getPersonaBeneficiario().setNombre("");      
      this.registroPersona.getPersonaBeneficiario().setPaterno("");
      this.registroPersona.getPersonaBeneficiario().setMaterno("");
      this.registroPersona.getPersonaBeneficiario().setFechaNacimiento(LocalDate.now());
      this.registroPersona.getPersonaBeneficiario().setIdTipoParentesco(null);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } 
	
	private void loadProveedores() throws Exception {
    List<UISelectEntity> proveedores= null;
    Map<String, Object> params      = new HashMap<>();
		List<Columna>columns            = new ArrayList<>();
		EAccion eaccion                 = null;
		MotorBusqueda motor             = null;
		Long idProveedor                = null;
    try {
      params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getDependencias());
			columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
      proveedores = UIEntity.build("TcManticProveedoresDto", "sucursales", params, columns, Constantes.SQL_TODOS_REGISTROS);
      this.attrs.put("proveedoresGeneral", proveedores);
			eaccion= (EAccion) this.attrs.get("accion");
			if(eaccion.equals(EAccion.MODIFICAR)){
				motor= new MotorBusqueda(this.registroPersona.getIdPersona(), this.registroPersona.getEmpresaPersona().getIdEmpresaPersona());
				idProveedor= motor.toProveedorAgente();
				for(Entity proveedor: proveedores){
					if(proveedor.getKey().equals(idProveedor))
						this.attrs.put("proveedor", proveedor);
				}	// for								
			} // if
			else
				this.attrs.put("proveedor", proveedores.get(0));
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // loadProveedores
	
	private void loadClientes() throws Exception {
    List<UISelectEntity> clientes= null;
    Map<String, Object> params   = new HashMap<>();
		List<Columna>columns         = new ArrayList<>();
		EAccion eaccion              = null;
		Long idCliente               = null;
		MotorBusqueda motor          = null;
    try {
      params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getDependencias());
			columns.add(new Columna("rfc", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("razonSocial", EFormatoDinamicos.MAYUSCULAS));
      clientes = UIEntity.build("TcManticClientesDto", "sucursales", params, columns, Constantes.SQL_TODOS_REGISTROS);
      this.attrs.put("clientes", clientes);
			eaccion= (EAccion) this.attrs.get("accion");
			if(eaccion.equals(EAccion.MODIFICAR)){
				motor= new MotorBusqueda(this.registroPersona.getIdPersona(), this.registroPersona.getEmpresaPersona().getIdEmpresaPersona());
				idCliente= motor.toProveedorAgente();
				for(Entity cliente: clientes){
					if(cliente.getKey().equals(idCliente))
						this.attrs.put("cliente", cliente);
				}	// for
			} // if				
			else
				this.attrs.put("cliente", clientes.get(0));
    } // try
    catch (Exception e) {
      throw e;
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
  } // loadClientes
	
	public void doActualizaClientesProveedores() {
    try {
      loadProveedores();
			loadClientes();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doActualizaMunicipios
	
	public void onTabChange(TabChangeEvent event) {		
		if(event.getTab().getTitle().equals("General"))
			doActualizaIconEstatus();				
		else if (event.getTab().getTitle().equals("Bitacora"))
			loadBitacora();
		else if (event.getTab().getTitle().equals("Prestaciones"))
			UIBackingUtilities.execute("refreshValidateNss();");
  } // onTabChange
	
	private void loadBitacora() {
		List<Columna> columns    = new ArrayList<>();
		Map<String, Object>params= new HashMap<>();
		try {
			columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
			params.put("idKey", this.registroPersona.getEmpresaPersona().getIdEmpresaPersona());
			params.put("proceso", "Empleados");
			params.put("campo", "idActivo");			
			this.lazyModelBitacora= new FormatLazyModel("TcKeetBitacorasDto", "bitacora", params, columns);
			UIBackingUtilities.resetDataTable("contenedorGrupos:tablaBitacora");
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally {
			Methods.clean(columns);
			Methods.clean(params);
		} // finally
	} 
  
  public void doChangeCategoria() {
    switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
      case "cafu":
        this.toLoadPuestos();
        break;
      case "gylvi": 
      case "triana":
        break;
    } // swtich
  }
 
  public void doChangePuesto() {
    if(!Objects.equals(this.getRegistroPersona().getIdPuesto(), 6L))
      this.getRegistroPersona().getEmpresaPersona().setIdPorDia(2L);
  }
 
	private void toLoadTiposPagos() {
		List<UISelectItem> tiposMediosPagos= null;
		Map<String, Object>params          = new HashMap<>();
		try {
			params.put("intermediario", -1);
			tiposMediosPagos= UISelect.build("TcManticTiposMediosPagosDto", "caja", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);      
			this.attrs.put("tiposMediosPagos", tiposMediosPagos);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
  } 
  
  public void doUpdateSueldoSemanal() {
    try {      
      // Double sueldoImss = this.registroPersona.getEmpresaPersona().getSueldoImss();
      // Double sobreSueldo= this.registroPersona.getEmpresaPersona().getSobreSueldo();
      switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
        case "cafu":
          // this.registroPersona.getEmpresaPersona().setSueldoSemanal(sueldoImss+ sobreSueldo); 
          break;
        case "gylvi": 
        case "triana":
          break;
      } // swtich
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
 
	public void doAgregarAlimenticia() {
    try {
      this.registroPersona.doAgregarPersonaAlimenticia();
      this.registroPersona.setPersonaAlimenticia(new PersonaAlimenticia());      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 
	
	public void doActualizaAlimenticia() {
		try {
			this.registroPersona.doActualizarPersonaAlimenticia();
			this.registroPersona.setPersonaAlimenticia(new PersonaAlimenticia());      
      this.toLoadAtributosAlimenticia();
		} // try 
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	} 
	
	public void doEliminarAlimenticia() {
		try {
			this.registroPersona.doEliminarPersonaAlimenticia();
			this.registroPersona.setPersonaAlimenticia(new PersonaAlimenticia());      
      this.toLoadAtributosAlimenticia();
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
	} 
	
	public void doConsultarAlimenticia() {
    PersonaAlimenticia alimenticia= null;    
    try {
      this.registroPersona.doConsultarPersonaAlimenticia();
			alimenticia = this.registroPersona.getAlimenticiaPivote();        
      this.registroPersona.getPersonaAlimenticia().setNombre(alimenticia.getNombre());
      this.registroPersona.getPersonaAlimenticia().setPaterno(alimenticia.getPaterno());
      this.registroPersona.getPersonaAlimenticia().setMaterno(alimenticia.getMaterno());
      this.registroPersona.getPersonaAlimenticia().setPorcentaje(alimenticia.getPorcentaje());      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 
  
	private void toLoadAtributosAlimenticia() throws Exception {		
		try {
			if (!this.registroPersona.getPersonaAlimenticia().isValid()) 
        this.clearAtributosAlimenticia();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
	} 
 
  public void clearAtributosAlimenticia() {
    try {
      this.registroPersona.getPersonaAlimenticia().setNombre("");      
      this.registroPersona.getPersonaAlimenticia().setPaterno("");
      this.registroPersona.getPersonaAlimenticia().setMaterno("");
      this.registroPersona.getPersonaAlimenticia().setPorcentaje(0D);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } 

}