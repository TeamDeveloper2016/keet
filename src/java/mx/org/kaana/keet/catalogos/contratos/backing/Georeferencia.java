package mx.org.kaana.keet.catalogos.contratos.backing;

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
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.keet.catalogos.desarrollos.beans.RegistroDesarrollo;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;


@Named(value = "keetCatalogosContratosGeoreferencia")
@ViewScoped
public class Georeferencia extends IBaseFilter implements Serializable {

	private static final long serialVersionUID      = -1527541903767470918L;
	protected static final String COORDENADA_CENTRAL= "21.8818,-102.291";
  private RegistroDesarrollo registroDesarrollo;	
	private MapModel model;
	
	public RegistroDesarrollo getRegistroDesarrollo() {
		return registroDesarrollo;
	}

	public MapModel getModel() {
		return model;
	}

	@PostConstruct
  @Override
  protected void init() {		
    EOpcionesResidente opcion= null;    
		Long idDesarrollo        = null;
    try {
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");			
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");			
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("opcionResidente", opcion);						
			this.attrs.put("idDesarrollo", idDesarrollo);
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());						
			this.registroDesarrollo= new RegistroDesarrollo((Long)this.attrs.get("idDesarrollo"));
			this.attrs.put("pathPivote", "/".concat((Configuracion.getInstance().getEtapaServidor().name().toLowerCase())).concat("/images/"));
			this.attrs.put("domicilio", toDomicilio());				
			this.attrs.put("mostrarDetalle", true);				
			this.loadContratos();
			this.doLoad();
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	
	private String toDomicilio() {
		StringBuilder regresar= null;
		try {
			regresar= new StringBuilder();
			regresar.append(this.registroDesarrollo.getDomicilio().getCalle()).append(" , ");
			if(!Cadena.isVacio(this.registroDesarrollo.getDomicilio().getNumeroExterior()))
				regresar.append(this.registroDesarrollo.getDomicilio().getNumeroExterior()).append(" , ");
			if(!Cadena.isVacio(this.registroDesarrollo.getDomicilio().getNumeroInterior()))
				regresar.append(this.registroDesarrollo.getDomicilio().getNumeroInterior()).append(" , ");
			regresar.append(this.registroDesarrollo.getDomicilio().getAsentamiento()).append(" , C.P. ");
			regresar.append(this.registroDesarrollo.getDomicilio().getCodigoPostal());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar.toString();
	} // toDomicilio

	private void loadContratos() {
		List<UISelectEntity>contratos= null;
		Map<String, Object>params    = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "tc_keet_proyectos.id_desarrollo=".concat(this.attrs.get("idDesarrollo").toString()));
			params.put("sortOrder", "order by tc_keet_contratos.registro desc");
			contratos= UIEntity.seleccione("VistaContratosDto", "lazy", params, "clave");
			this.attrs.put("contratos", contratos);
			this.attrs.put("contrato", UIBackingUtilities.toFirstKeySelectEntity(contratos));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	}
	
	@Override
  public void doLoad() {
		List<Entity> lotes        = null;
		Marker marker             = null;
		String icon               = null;
		Map<String, Object> params= null;
		UISelectEntity contrato   = null;
    try {
			this.attrs.put("mostrarDetalle", true);				
			this.attrs.put("index", 0);				
			this.model= new DefaultMapModel();
			this.attrs.put("coordenadaCentral", COORDENADA_CENTRAL);
			contrato= (UISelectEntity) this.attrs.get("contrato");
			params= new HashMap<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo").toString());
			params.put(Constantes.SQL_CONDICION, contrato.getKey()>=1L ? "tc_keet_contratos_lotes.id_contrato=".concat(contrato.getKey().toString()) : Constantes.SQL_VERDADERO);			
      lotes= DaoFactory.getInstance().toEntitySet("VistaGeoreferenciaLotesDto", "lotes", params, Constantes.SQL_TODOS_REGISTROS);
			if(!lotes.isEmpty()) {				
				for(Entity lote: lotes) {
					icon  = this.toIcon(lote);
					marker= new Marker(new LatLng(Double.valueOf(lote.toString("latitud")), Double.valueOf(lote.toString("longitud"))), "Contrato: ".concat(lote.toString("clave")).concat(", Lote: ").concat(lote.toString("codigo")), lote, icon);
					this.model.addOverlay(marker);
				} // for
				this.attrs.put("coordenadaCentral", lotes.get(0).toString("latitud").concat(",").concat(lotes.get(0).toString("longitud")));				
			} // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad
	
	private String toIcon(Entity mzaLote) throws Exception {
		String regresar          = null;
		String imagen            = null;
		String color             = null;
		Map<String, Object>params= null;
		Entity estatus           = null;
    Integer porcentaje       = 0;
		try {
			imagen= JsfBase.getContext().concat("/javax.faces.resource/icon/mapa/").concat("janal-{color}-{orden}.png").concat(".jsf?ln=janal");
			params= new HashMap<>();			
			params.put("clave", this.toClaveEstacion(mzaLote));
			estatus= (Entity) DaoFactory.getInstance().toEntity("VistaGeoreferenciaLotesDto", "estatusManzanaLote", params);
			if(estatus.toString("total")!= null) {
        porcentaje= new Integer(String.valueOf((estatus.toLong("terminado") * 100)/estatus.toLong("total")));
        /* AQUI COLOCAR LOS COLORES BASADOS EN EL PORCENTAJE DE AVANCE */
				this.attrs.put("porcentaje", porcentaje);
        if(porcentaje== 0)
     			color= "red";
        else  
          if(porcentaje> 0 && porcentaje<= 20)
            color= "cyan";
          else
            if(porcentaje>= 21 && porcentaje<= 80)
              color= "orange";
            else
              if(porcentaje>= 81 && porcentaje<= 99)
                color= "yellow";
              else
                color= "green"; 
			} // if	
			params.clear();
			params.put("color", color);
			params.put("orden", mzaLote.toString("orden"));
      mzaLote.put("color", new Value("color", color));
      mzaLote.put("porcentaje", new Value("porcentaje", porcentaje));
			regresar= Cadena.replaceParams(imagen, params);
		} // try
		finally {
			Methods.clean(params);
		} // finally		
		return regresar;
	} // toIcon
	
	private String toClaveEstacion(Entity lote) {
		StringBuilder regresar= null;
		try {			
			regresar= new StringBuilder();
			regresar.append(Cadena.rellenar(this.attrs.get("idEmpresa").toString(), 3, '0', true));
			regresar.append(Fecha.getAnioActual());
			regresar.append(Cadena.rellenar(lote.toString("contrato"), 3, '0', true));
			regresar.append(Cadena.rellenar(lote.toString("orden"), 3, '0', true));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar.toString();
	} // toClaveEstacion
	
	public void onTabChange(TabChangeEvent event) {
		try {
			if(event.getTab().getTitle().equals("Ubicación")) {
				this.attrs.put("mostrarDetalle", true);				
				doLoad();
			} // if
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
	} // onTabChange
	
	public void onMarkerSelect(OverlaySelectEvent event) {
		Marker marker= null;	
		try {
			marker= (Marker) event.getOverlay();
			this.attrs.put("mostrarDetalle", false);
			this.attrs.put("index", 1);				
			loadEvidencias((Entity) marker.getData());
			loadResidentes();
			loadContratistas((Entity) marker.getData());
			loadAvances((Entity) marker.getData());
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		                    
  } // onMarkerSelect
	
	private void loadEvidencias(Entity seleccionado) {
		List<Columna> columns    = null;
		Map<String, Object>params= null;
		try {
			this.attrs.put("loteSeleccionado", seleccionado);
			columns= new ArrayList<>();
      columns.add(new Columna("nombrePersona", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombreUsuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("observaciones", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));						
			params= new HashMap<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo").toString());
			params.put("idContratoLote", seleccionado.getKey());
		  this.attrs.put("importados", UIEntity.build("VistaCapturaDestajosDto", "allImportadosContratoLote", params, columns));
			this.doLoadFiles();
		} // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally		
	} // loadEvidencias
	
	private void doLoadFiles() {
		List<Entity>importados= null;		
		String dns            = null;
		String url            = null;
		try {
			dns= Configuracion.getInstance().getPropiedad("sistema.dns.".concat(Configuracion.getInstance().getEtapaServidor().name().toLowerCase()));			
			importados= (List<Entity>) this.attrs.get("importados");
			for(Entity importado: importados) {
				url= dns.substring(0, dns.indexOf(JsfBase.getContext())).concat(this.attrs.get("pathPivote").toString()).concat(importado.toString("ruta")).concat(importado.toString("archivo"));
				importado.put("url", new Value("url", url));
			} // for
			this.attrs.put("importados", importados);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
			throw e;
		} // catch		
	} // doLoadFiles
	
	private void loadResidentes() {
		List<Entity>residentes   = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			residentes= DaoFactory.getInstance().toEntitySet("VistaGeoreferenciaLotesDto", "residentes", params);
			this.attrs.put("residentes", residentes);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadResidentes
	
	private void loadContratistas(Entity seleccionado) {
		List<Entity>contratistas = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put("idContratoLote", seleccionado.getKey());
			contratistas= DaoFactory.getInstance().toEntitySet("VistaGeoreferenciaLotesDto", "contratistasAsignado", params);
			this.attrs.put("contratistas", contratistas);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadContratistas
	
	private void loadAvances(Entity seleccionado) {
		Map<String, Object>params= null;
    List<Columna> columns    = null;				
    try {      			
			params= this.toPrepare(seleccionado);
      columns= new ArrayList<>();      
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("descripcion", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("costo", EFormatoDinamicos.MONEDA_CON_DECIMALES));                  
	    this.lazyModel= new FormatLazyModel("VistaGeoreferenciaLotesDto", "avances", params, columns);			
			UIBackingUtilities.resetDataTable("tablaAvances");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {      
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	} // loadAvances
	
	private Map<String, Object> toPrepare(Entity seleccionado) {
		Map<String, Object> regresar= null;
		try {
			regresar= new HashMap<>();			
			regresar.put("clave", toClaveEstacion(seleccionado));
			regresar.put("estatus", EEstacionesEstatus.EN_PROCESO.getKey() + "," + EEstacionesEstatus.TERMINADO.getKey());						
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toPrepare
	
	public String doCancelar() {
    String regresar          = null;    
		EOpcionesResidente opcion= null;		
    try {
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("idDesarrolloProcess", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("opcion", opcion);			
			if(this.attrs.get("retorno")!= null)
				regresar= this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR_AMPERSON);			
			else
				regresar= opcion.getRetorno().concat(Constantes.REDIRECIONAR_AMPERSON);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
	
	public String doCapturaAvances(Entity visitado) {
		String regresar          = null;    
		EOpcionesResidente opcion= null;
		Long idDepartamento      = null;
    try {
			if(visitado.toString("puesto").equals("SUBCONTRATISTA"))
				idDepartamento= Long.valueOf(visitado.toString("idsDepartamento").split(",")[0]);			
			else
				idDepartamento= Long.valueOf(visitado.toString("idsDepartamento"));			
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));			
			JsfBase.setFlashAttribute("figura", new UISelectEntity(visitado));
			JsfBase.setFlashAttribute("idDepartamento", idDepartamento);
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("idDesarrolloProcess", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("opcionResidente", opcion);						
			JsfBase.setFlashAttribute("opcionAdicional", EOpcionesResidente.GEOREFERENCIA);			
			regresar= "/Paginas/Keet/Catalogos/Contratos/Destajos/filtro.jsf".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
	} // doCapturaAvances
  
}