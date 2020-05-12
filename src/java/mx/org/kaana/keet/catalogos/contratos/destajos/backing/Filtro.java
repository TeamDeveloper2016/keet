package mx.org.kaana.keet.catalogos.contratos.destajos.backing;

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
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.catalogos.desarrollos.beans.RegistroDesarrollo;
import mx.org.kaana.keet.comun.gps.Point;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.keet.nomina.beans.Nomina;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosContratosDestajosFiltro")
@ViewScoped
public class Filtro extends IBaseFilter implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;			
	private RegistroDesarrollo registroDesarrollo;		
	private List<Entity> lotes;
	private FormatLazyModel lazyDestajo;
	private Nomina ultima;
	
	public RegistroDesarrollo getRegistroDesarrollo() {
		return registroDesarrollo;
	}

	public void setRegistroDesarrollo(RegistroDesarrollo registroDesarrollo) {
		this.registroDesarrollo = registroDesarrollo;
	}			

	public List<Entity> getLotes() {
		return lotes;
	}

	public void setLotes(List<Entity> lotes) {
		this.lotes = lotes;
	}

	public FormatLazyModel getLazyDestajo() {
		return lazyDestajo;
	}

	public void setLazyDestajo(FormatLazyModel lazyDestajo) {
		this.lazyDestajo=lazyDestajo;
	}
	
  @PostConstruct
  @Override
  protected void init() {		
    EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;
		Long idDepartamento      = null;
		UISelectEntity figura    = null;
    try {
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");			
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("opcionAdicional", JsfBase.getFlashAttribute("opcionAdicional"));
			this.attrs.put("idDesarrollo", idDesarrollo);
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());				
      this.attrs.put("destajos", false);				
      this.attrs.put("persona", false);				
      this.attrs.put("proveedor", false);							
			this.loadCatalogos();			
			if(JsfBase.getFlashAttribute("idDesarrolloProcess")!= null){
				idDepartamento= (Long) JsfBase.getFlashAttribute("idDepartamento");
				figura= (UISelectEntity) JsfBase.getFlashAttribute("figura");
				this.attrs.put("especialidad", idDepartamento);
				this.doLoadFiguras();				
				this.attrs.put("figura", ((List<UISelectEntity>)this.attrs.get("figuras")).get(((List<UISelectEntity>)this.attrs.get("figuras")).indexOf(figura)));
				doLoad();
			} // if
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void loadCatalogos() throws Exception {
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			this.registroDesarrollo= new RegistroDesarrollo((Long)this.attrs.get("idDesarrollo"));      
			this.attrs.put("domicilio", toDomicilio());			
			this.loadEspecialidades();			
			this.doLoadFiguras();
      params.put("idTipoNomina", "1");
      this.ultima= (Nomina)DaoFactory.getInstance().toEntity(Nomina.class, "VistaNominaDto", "ultima", params);			
		} // try
    finally {
			Methods.clean(params);
		} // finally
	} // loadCatalogos	
	
	private void loadEspecialidades() {
		List<UISelectItem>especialidades= null;
		Map<String, Object>params       = null;
		try {
			params= new HashMap<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			especialidades= UISelect.seleccione("VistaCapturaDestajosDto", "especialidades", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
			this.attrs.put("especialidades", especialidades);
			this.attrs.put("especialidad", UIBackingUtilities.toFirstKeySelectItem(especialidades));
		} // try
		finally {
			Methods.clean(params);
		} // finally
	} // loadEspecialidades
	
	public void doLoadFiguras(){
		List<UISelectEntity> figuras= null;
		Map<String, Object>params   = null;
		List<Columna> campos        = null;
		try {
			params= new HashMap<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			params.put("idDepartamento", this.attrs.get("especialidad"));
			campos= new ArrayList<>();
			campos.add(new Columna("nombreCompleto", EFormatoDinamicos.MAYUSCULAS));
			campos.add(new Columna("puesto", EFormatoDinamicos.MAYUSCULAS));
			figuras= UIEntity.seleccione("VistaCapturaDestajosDto", "empleadosAsociados", params, campos, "puesto");
			this.attrs.put("figuras", figuras);
			this.attrs.put("figura", UIBackingUtilities.toFirstKeySelectEntity(figuras));
			this.attrs.put("destajos", false);
			this.attrs.put("persona", false);
			this.attrs.put("proveedor", false);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
			Methods.clean(campos);
		} // finally
	} // doLoadFiguras
	
	private String toDomicilio(){
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
	
  @Override
  public void doLoad() {
		Map<String, Object>params   = null;
    List<Columna> columns       = null;		
		List<UISelectEntity> figuras= null;
		UISelectEntity figura       = null;
		String idXml                = null;
    try {   
			figuras= (List<UISelectEntity>) this.attrs.get("figuras");
			figura= figuras.get(figuras.indexOf((UISelectEntity) this.attrs.get("figura")));
			params= new HashMap<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			params.put("idFigura", figura.getKey() > 0 ? figura.getKey().toString().substring(4) : figura.getKey());
      columns= new ArrayList<>();      
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("fechaInicio", EFormatoDinamicos.FECHA_CORTA));                  
      columns.add(new Columna("fechaTermino", EFormatoDinamicos.FECHA_CORTA));    
			idXml= figura.toLong("tipo").equals(1L) ? "lotesContratistas" : "lotesSubContratistas";
	    this.lotes= DaoFactory.getInstance().toEntitySet("VistaCapturaDestajosDto", idXml, params);			
			if(!this.lotes.isEmpty()){ 
			  UIBackingUtilities.toFormatEntitySet(this.lotes, columns);	
				this.lotes.add(0, this.toLoteDefault());
				toEstatusManzanaLote();
			} //
			this.attrs.put("persona", figura.toLong("tipo").equals(1L));
			this.attrs.put("proveedor", figura.toLong("tipo").equals(2L));
			this.attrs.put("destajos", false);
			this.attrs.put("figura", figura);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {      
      Methods.clean(columns);
      Methods.clean(params);
    } // finally		
  } // doLoad	
	
	private Entity toLoteDefault() {
		Entity regresar= new Entity(Constantes.USUARIO_INACTIVO);
		regresar.put("clave", new Value("clave", "EVIDENCIAS"));
		regresar.put("manzana", new Value("manzana", "00N"));
		regresar.put("lote", new Value("lote", "N"));
		regresar.put("fechaInicio", new Value("fechaInicio", "-"));
		regresar.put("fechaTermino", new Value("fechaTermino", "-"));
		regresar.put("diasConstruccion", new Value("diasConstruccion", "-"));
		regresar.put("contratistas", new Value("contratistas", ""));
		regresar.put("orden", new Value("orden", ""));
		regresar.put("ordenContrato", new Value("ordenContrato", ""));
		regresar.put("claveContrato", new Value("claveContrato", ""));
		return regresar;
	} // toLoteDefault
	
	private void toEstatusManzanaLote() throws Exception {
		Map<String, Object>params= null;
		Entity estatus           = null;
		try {
			params= new HashMap<>();
			for(Entity mzaLote: this.lotes){
				params.clear();
				params.put("idDepartamento", this.attrs.get("especialidad"));
				params.put("clave", toClaveEstacion(mzaLote));
				estatus= (Entity) DaoFactory.getInstance().toEntity("VistaCapturaDestajosDto", "estatusManzanaLote", params);
				if(estatus.toString("total")!= null){
					if(estatus.toLong("total").equals(estatus.toLong("terminado")))
						mzaLote.put("iconEstatus", new Value("iconEstatus", EEstacionesEstatus.TERMINADO.getSemaforo()));
					else if(estatus.toLong("total").equals(estatus.toLong("iniciado")))
						mzaLote.put("iconEstatus", new Value("iconEstatus", EEstacionesEstatus.INICIAR.getSemaforo()));
					else
						mzaLote.put("iconEstatus", new Value("iconEstatus", EEstacionesEstatus.EN_PROCESO.getSemaforo()));
				} // if
				else
					mzaLote.put("iconEstatus", new Value("iconEstatus", ""));
			} // for
		} // try
		finally {
			Methods.clean(params);
		} // finally		
	} // toEstatusManzanaLote
	
	private String toClaveEstacion(Entity lote){
		StringBuilder regresar= null;
		try {			
			regresar= new StringBuilder();
			regresar.append(Cadena.rellenar(this.attrs.get("idEmpresa").toString(), 3, '0', true));
			regresar.append(Fecha.getAnioActual());
			regresar.append(Cadena.rellenar(lote.toString("ordenContrato"), 3, '0', true));
			regresar.append(Cadena.rellenar(lote.toString("orden"), 3, '0', true));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar.toString();
	} // toClaveEstacion
	
	public String doConceptos() {
    String regresar             = null;    		
		Entity seleccionado         = null;
		List<UISelectEntity> figuras= null;
		UISelectEntity figura       = null;
    try {			
			figuras= (List<UISelectEntity>) this.attrs.get("figuras");
			figura= figuras.get(figuras.indexOf((UISelectEntity) this.attrs.get("figura")));
			seleccionado= (Entity) this.attrs.get("seleccionado");			
			JsfBase.setFlashAttribute("opcionResidente", (EOpcionesResidente)this.attrs.get("opcionResidente"));												
			JsfBase.setFlashAttribute("seleccionado", seleccionado);												
			JsfBase.setFlashAttribute("figura", figura);									
			JsfBase.setFlashAttribute("idDepartamento", Long.valueOf(this.attrs.get("especialidad").toString()));									
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));				
			JsfBase.setFlashAttribute("georreferencia", new Point(Numero.getDouble(seleccionado.toString("latitud"), 21.890563), Numero.getDouble(seleccionado.toString("longitud"), -102.252030)));				
			if(seleccionado.getKey().equals(Constantes.USUARIO_INACTIVO))				
				regresar= "galeria".concat(Constantes.REDIRECIONAR);			
			else
				regresar= "conceptos".concat(Constantes.REDIRECIONAR);										
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina
	
	public String doCancelar() {
    String regresar                   = null;    
		EOpcionesResidente opcion         = null;		
		EOpcionesResidente opcionAdicional= null;		
    try {			
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("idDesarrolloProcess", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("opcion", opcion);			
			if(this.attrs.get("opcionAdicional")!= null)
				opcionAdicional= ((EOpcionesResidente)this.attrs.get("opcionAdicional"));										
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));			
			JsfBase.setFlashAttribute("opcionResidente", opcion);						
			if(opcionAdicional!= null)				
				regresar= opcionAdicional.getRetorno().concat(Constantes.REDIRECIONAR);			
			else
				regresar= opcion.getRetorno().concat(Constantes.REDIRECIONAR_AMPERSON);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
	
	public void doDestajos() {
    List<Columna> columns       = null;
		Map<String, Object>params   = new HashMap<>();
    try {
      UISelectEntity figura= (UISelectEntity) this.attrs.get("figura");						
			params.put("sortOrder", "order by tc_keet_contratos.etapa, tc_keet_contratos_lotes.manzana, tc_keet_contratos_lotes.lote");
		  params.put("idNomina", this.ultima.getIdNominaEstatus()== 4L? -1: this.ultima.getIdNomina());
			params.put("idEmpresaPersona", figura.getKey().toString().substring(4));
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
      columns= new ArrayList<>();
      columns.add(new Columna("costo", EFormatoDinamicos.MILES_SIN_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_CORTA));
      this.lazyDestajo= new FormatCustomLazy("VistaNominaConsultasDto", figura.toLong("tipo").equals(1L)? "destajoPersona": "destajoProveedor", params, columns);
      UIBackingUtilities.resetDataTable("tabla");
			this.attrs.put("destajos", true);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally				
	}

	public String doColorNomina(Entity row) {
		return Cadena.isVacio(row.toLong("idNomina"))? "": "janal-tr-diferencias";
	}
}