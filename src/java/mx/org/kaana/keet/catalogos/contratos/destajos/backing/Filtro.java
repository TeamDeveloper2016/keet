package mx.org.kaana.keet.catalogos.contratos.destajos.backing;

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
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.catalogos.contratos.destajos.comun.IBaseReporteDestajos;
import mx.org.kaana.keet.catalogos.desarrollos.beans.RegistroDesarrollo;
import mx.org.kaana.keet.comun.gps.Point;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.keet.nomina.beans.Nomina;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.wassenger.Cafu;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.enums.ETiposContactos;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.LocalDateTime;

@Named(value = "keetCatalogosContratosDestajosFiltro")
@ViewScoped
public class Filtro extends IBaseReporteDestajos implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;			
  private static final Log LOG = LogFactory.getLog(Filtro.class);
  
	private RegistroDesarrollo registroDesarrollo;		
	private List<Entity> lotes;
	private FormatLazyModel lazyDestajo;
	private Nomina ultima;  
  private String costoTotal;
	
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
	
	public String getCostoTotal() {
    return this.costoTotal;
//    Double costo = 0D;
//		if(this.lazyDestajo!= null)
//			for (IBaseDto item: (List<IBaseDto>)this.lazyDestajo.getWrappedData()) {
//				Entity row= (Entity)item;
//				costo+= new Double(row.toString("total"));
//			} // for	
//		return Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, costo);
	}
	
  @PostConstruct
  @Override
  protected void init() {		
    EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;
		Long idDepartamento      = null;
		UISelectEntity figura    = null;
    try {
			this.initBase();
      this.costoTotal= "$ 0.00";
			opcion      = (EOpcionesResidente)JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long)JsfBase.getFlashAttribute("idDesarrollo");			
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("opcionAdicional", JsfBase.getFlashAttribute("opcionAdicional"));
			this.attrs.put("idDesarrollo", idDesarrollo);
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());				
      this.attrs.put("destajos", false);				
      this.attrs.put("persona", false);				
      this.attrs.put("proveedor", false);							
			this.toLoadCatalogos();			
			if(JsfBase.getFlashAttribute("idDesarrolloProcess")!= null) {
				idDepartamento= (Long) JsfBase.getFlashAttribute("idDepartamento");
				figura= (UISelectEntity) JsfBase.getFlashAttribute("figura");
				this.attrs.put("especialidad", idDepartamento);
				this.attrs.put("figura", ((List<UISelectEntity>)this.attrs.get("figuras")).get(((List<UISelectEntity>)this.attrs.get("figuras")).indexOf(figura)));
			} // if
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void toLoadCatalogos() throws Exception {
    List<Columna> columns    = null;		
		Map<String, Object>params= null;
		try {
      columns= new ArrayList<>();      
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));                  
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));    
			params= new HashMap<>();
			this.registroDesarrollo= new RegistroDesarrollo((Long)this.attrs.get("idDesarrollo"));      
			this.attrs.put("domicilio", this.toDomicilio());			
      params.put("idTipoNomina", "1");
      List<UISelectEntity> semanas= (List<UISelectEntity>)UIEntity.build("VistaNominaDto", "ultima", params, columns);
      this.attrs.put("semanas", semanas);
      if(semanas!= null && !semanas.isEmpty()) {
        UISelectEntity semana= semanas.get(0);
        this.ultima= new Nomina(semana.toLong("idNomina"), semana.toLong("idNominaEstatus"), 0L);			
        this.attrs.put("semana", semanas.get(0));
      } // if  
      else {
        this.attrs.put("semana", new UISelectEntity(-1L));
        this.ultima= new Nomina();			
      } // else
      this.toLoadContratos();
		} // try
    finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
	} // toLadCatalogos	
	
	private void toLoadContratos() {
    Map<String, Object> params    = null;
		List<UISelectEntity> contratos= null;
    try {      
      params = new HashMap<>();      
      params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			contratos= UIEntity.seleccione("VistaTableroDto", "contratos", params, "clave");
			this.attrs.put("contratos", contratos);
			this.attrs.put("contrato", UIBackingUtilities.toFirstKeySelectEntity(contratos));
      this.toLoadManzanas();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
	private void toLoadManzanas() {
    Map<String, Object> params   = null;
		List<UISelectEntity> manzanas= null;
    try {      
      if(this.attrs.get("casa")!= null && ((UISelectEntity)this.attrs.get("casa")).getKey()>= 0)
        this.attrs.put("casa", new UISelectEntity(-1L));
      params = new HashMap<>();      
      params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
			manzanas= UIEntity.seleccione("VistaTableroDto", "manzanas", params, "nombre");
			this.attrs.put("manzanas", manzanas);
			this.attrs.put("manzana", UIBackingUtilities.toFirstKeySelectEntity(manzanas));
      this.toLoadEspecialidades();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
  }
  
	private void toLoadEspecialidades() {
		List<UISelectItem>especialidades= null;
		Map<String, Object>params       = null;
		try {
      if(this.attrs.get("casa")!= null && ((UISelectEntity)this.attrs.get("casa")).getKey()>= 0)
        this.attrs.put("casa", new UISelectEntity(-1L));
			params= new HashMap<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
			especialidades= UISelect.build("VistaCapturaDestajosDto", "especialidades", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
			this.attrs.put("especialidades", especialidades);
			this.attrs.put("especialidad", UIBackingUtilities.toFirstKeySelectItem(especialidades));
      this.doLoadFiguras();
		} // try
		finally {
			Methods.clean(params);
		} // finally
	} // loadEspecialidades
	
	public void doLoadFiguras() {
		List<UISelectEntity> figuras= null;
		Map<String, Object>params   = null;
		List<Columna> columns       = null;
		try {
      if(this.attrs.get("casa")!= null && ((UISelectEntity)this.attrs.get("casa")).getKey()>= 0)
        this.attrs.put("casa", new UISelectEntity(-1L));
			params= new HashMap<>();
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			params.put("idDepartamento", this.attrs.get("especialidad"));
			params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
			columns= new ArrayList<>();
			columns.add(new Columna("nombreCompleto", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("puesto", EFormatoDinamicos.MAYUSCULAS));
			figuras= UIEntity.build("VistaCapturaDestajosDto", "empleadosAsociados", params, columns);
			this.attrs.put("figuras", figuras);
      this.attrs.put("figura", UIBackingUtilities.toFirstKeySelectEntity(figuras));
      this.doLoadCasas();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
	} 
	
  public void doLoadManzanas() {
    List<UISelectEntity> contratos= null;    
		UISelectEntity contrato       = null;
    try {   
			contratos= (List<UISelectEntity>)this.attrs.get("contratos");
			contrato = (UISelectEntity)this.attrs.get("contrato");
      int index= contratos.indexOf(contrato);
      if(index>= 0)
        this.attrs.put("contrato", contratos.get(index));
      this.toLoadManzanas();
      this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
	public void doLoadDepartamentos() {
    List<UISelectEntity> manzanas= null;    
		UISelectEntity manzana       = null;
    try {   
			manzanas= (List<UISelectEntity>)this.attrs.get("manzanas");
			manzana = (UISelectEntity)this.attrs.get("manzana");
      int index= manzanas.indexOf(manzana);
      if(index>= 0)
        this.attrs.put("manzana", manzanas.get(index));
      this.toLoadEspecialidades();
      this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
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

  public String toLoadCondicion() {
    StringBuilder regresar = new StringBuilder();
		UISelectEntity contrato= (UISelectEntity)this.attrs.get("contrato");
		UISelectEntity manzana = (UISelectEntity)this.attrs.get("manzana");
		UISelectEntity lote    = (UISelectEntity)this.attrs.get("casa");
    if(contrato!= null && contrato.getKey()> 0L)
		  regresar.append("tc_keet_contratos.id_contrato= ").append(contrato.getKey()).append(" and ");
    if(manzana!= null && manzana.getKey()> 0L) {
			List<UISelectEntity> manzanas= (List<UISelectEntity>)this.attrs.get("manzanas");
      int index= manzanas.indexOf(manzana);
      if(index>= 0)
         manzana= manzanas.get(index);
      this.attrs.put("manzana", manzanas.get(index));
      regresar.append("tc_keet_contratos_lotes.manzana= '").append(manzana.toString("manzana")).append("' and ");
    } // if  
    if(lote!= null && lote.getKey()> 0L)
      regresar.append("tc_keet_contratos_lotes.id_contrato_lote=").append(lote.getKey()).append( " and ");
    return regresar.length()> 0? regresar.substring(0, regresar.length()- 4): Constantes.SQL_VERDADERO;
  }
  
  public void doLoadCasas() {
		Map<String, Object>params   = null;
		List<UISelectEntity> figuras= null;
		List<UISelectEntity> casas  = null;
		UISelectEntity figura       = null;
		String idXml                = null;
    try {   
			figuras= (List<UISelectEntity>) this.attrs.get("figuras");
      figura = (UISelectEntity)this.attrs.get("figura");
      int index= figuras.indexOf(figura);
      if(index>= 0) {
			  figura= figuras.get(index);
        params  = new HashMap<>();
        params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
        params.put("idFigura", figura.getKey()> 0? figura.getKey().toString().substring(4): figura.getKey());
        params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
        idXml= figura.toLong("tipo").equals(1L)? "lotesContratistas": "lotesSubContratistas";
        casas= UIEntity.seleccione("VistaCapturaDestajosDto", idXml, params, Constantes.SQL_TODOS_REGISTROS, "descripcionLote");
        this.attrs.put("casas", casas);
//        if(casas!= null && casas.size()> 0)
//          this.attrs.put("casa", casas.size()- 1);
//        else
          this.attrs.put("casa", UIBackingUtilities.toFirstKeySelectEntity(casas));
      } // if  
      else {
        this.attrs.put("casas", new ArrayList<>());
        this.attrs.put("casa", new UISelectEntity(-1L));
      } // else
      this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {      
      Methods.clean(params);
    } // finally		
  } // doLoad	
	
  @Override
  public void doLoad() {
		Map<String, Object>params   = null;
    List<Columna> columns       = null;		
		List<UISelectEntity> figuras= null;
		List<UISelectEntity> casas  = null;
		UISelectEntity figura       = null;
		String idXml                = null;
    try {   
			figuras= (List<UISelectEntity>) this.attrs.get("figuras");
      figura = (UISelectEntity)this.attrs.get("figura");
      int index= figuras.indexOf(figura);
      if(index>= 0) {
			  figura= figuras.get(index);
        params  = new HashMap<>();
        params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
        params.put("idFigura", figura.getKey()> 0? figura.getKey().toString().substring(4): figura.getKey());
        params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
        columns= new ArrayList<>();      
        columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));                  
        columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));                  
        columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));    
        idXml= figura.toLong("tipo").equals(1L)? "lotesContratistas": "lotesSubContratistas";
        this.attrs.put("idTipoFiguraCorreo", figura.toLong("tipo"));
        this.attrs.put("idFiguraCorreo", figura.getKey()> 0? figura.getKey().toString().substring(4): figura.getKey());
        this.attrs.put("figuraNombreCompletoCorreo", figura.toString("nombreCompleto"));
        this.lotes= DaoFactory.getInstance().toEntitySet("VistaCapturaDestajosDto", idXml, params);		
        casas= UIEntity.seleccione("VistaCapturaDestajosDto", idXml, params, Constantes.SQL_TODOS_REGISTROS, "descripcionLote");
        this.attrs.put("lotes", casas);
        if(!this.lotes.isEmpty()) { 
          UIBackingUtilities.toFormatEntitySet(this.lotes, columns);	
          this.lotes.add(0, this.toLoteDefault());
          this.toEstatusManzanaLote();
        } // if
      } // if  
      else {
        this.lotes= new ArrayList<>();		
        this.attrs.put("lotes", new ArrayList<>());
      } // else
			this.attrs.put("persona", figura== null || figura.size()== 1? false: figura.toLong("tipo").equals(1L));
			this.attrs.put("proveedor", figura== null || figura.size()== 1? false: figura.toLong("tipo").equals(2L));
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
		regresar.put("inicio", new Value("inicio", "-"));
		regresar.put("termino", new Value("termino", "-"));
		regresar.put("diasConstruccion", new Value("diasConstruccion", "-"));
		regresar.put("contratistas", new Value("contratistas", ""));
		regresar.put("orden", new Value("orden", ""));
		regresar.put("latitud", new Value("latitud", 21.890563));
		regresar.put("longitud", new Value("longitud", -102.252030));
		regresar.put("ordenContrato", new Value("ordenContrato", ""));
		regresar.put("claveContrato", new Value("claveContrato", ""));
		regresar.put("ejercicio", new Value("ejercicio", Fecha.getAnioActual()));
		return regresar;
	} // toLoteDefault
	
	private void toEstatusManzanaLote() throws Exception {
		Map<String, Object>params= null;
		Entity estatus           = null;
		try {
			params= new HashMap<>();
			for(Entity mzaLote: this.lotes) {
				params.clear();
				params.put("idDepartamento", this.attrs.get("especialidad"));
				params.put("clave", this.toClaveEstacion(mzaLote));
				estatus= (Entity) DaoFactory.getInstance().toEntity("VistaCapturaDestajosDto", "estatusManzanaLote", params);
				if(estatus.toString("total")!= null) {
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
	
	private String toClaveEstacion(Entity lote) {
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
			if(this.attrs.get("opcionAdicional")!= null)
				JsfBase.setFlashAttribute("opcionAdicional", (EOpcionesResidente)this.attrs.get("opcionAdicional"));												
			JsfBase.setFlashAttribute("seleccionado", seleccionado);												
			JsfBase.setFlashAttribute("figura", figura);									
			JsfBase.setFlashAttribute("idDepartamento", Long.valueOf(this.attrs.get("especialidad").toString()));									
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));				
			JsfBase.setFlashAttribute("georreferencia", new Point(Numero.getDouble(seleccionado.toString("latitud"), 21.890563), Numero.getDouble(seleccionado.toString("longitud"), -102.252030)));				
			JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Contratos/Destajos/filtro");			
			JsfBase.setFlashAttribute("nombreConcepto", "");			
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
  } // doConceptos
	
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
				regresar= opcion!= null? opcion.getRetorno().concat(Constantes.REDIRECIONAR_AMPERSON): null;			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
	
	public void doDestajos() {
    List<Columna> columns    = null;
		Map<String, Object>params= new HashMap<>();
    try {
			List<UISelectEntity> figuras= (List<UISelectEntity>) this.attrs.get("figuras");
      UISelectEntity figura= (UISelectEntity) this.attrs.get("figura");	
      int index= figuras.indexOf(figura);
      if(index>= 0) {
        figura= figuras.get(index);
        params.put("sortOrder", "order by tc_keet_contratos.clave, tc_keet_contratos_lotes.manzana, tc_keet_contratos_lotes.lote");
        params.put("loNuevo", this.ultima.getIdCompleta()== 0L? figura.toLong("tipo").equals(1L)? "or tc_keet_contratos_destajos_contratistas.id_nomina is null": "or tc_keet_contratos_destajos_proveedores.id_nomina is null": "");
        params.put("idNomina", this.ultima.getIdNominaEstatus()== 5L? -1: this.ultima.getIdNomina());
        params.put("idEmpresaPersona", figura.getKey().toString().substring(4));
        params.put("idProveedor", figura.getKey().toString().substring(4));
        params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
        params.put("nomina", "PreNomina");
        params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
        columns= new ArrayList<>();
        columns.add(new Columna("porcentaje", EFormatoDinamicos.MILES_SAT_DECIMALES));
        columns.add(new Columna("costo", EFormatoDinamicos.MILES_SIN_DECIMALES));
        columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
        this.lazyDestajo= new FormatCustomLazy("VistaNominaConsultasDto", figura.toLong("tipo").equals(1L)? "destajoPersona": "destajoProveedor", params, columns);
        Entity costo= (Entity)DaoFactory.getInstance().toEntity("VistaNominaConsultasDto", figura.toLong("tipo").equals(1L)? "costoPersona": "costoProveedor", params);
        if(costo!= null && !costo.isEmpty())
          this.costoTotal= Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, costo.toDouble("total"));
        UIBackingUtilities.resetDataTable("tabla");
        this.attrs.put("destajos", true);
      } // if
      else {
        this.attrs.put("destajos", false);
        this.lazyDestajo= null;
        this.costoTotal = "$ 0.00";
        JsfBase.addMessage("No se puede consultar el destajo, no hay contratista ó sub-contratista !");
      } // else
      this.attrs.put("figura", figura);
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
  
	public void doReporte(String tipo) throws Exception {
		this.doReporte(tipo, false);
	} // doReporte	
	
	@Override
  public void doReporte(String tipo, boolean sendMail) throws Exception {    
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;    
    Map<String, Object>params    = null;
    UISelectEntity figura        = null;
    Parametros comunes           = null;
    boolean isCompleto           = false;
    try {
      isCompleto = tipo.equals("COMPLETO");
			List<UISelectEntity> figuras= (List<UISelectEntity>) this.attrs.get("figuras");
      int index= figuras.indexOf((UISelectEntity) this.attrs.get("figura"));
      if(index>= 0) {
        figura= figuras.get(index);			
        params = new HashMap<>();  
        comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
        parametros= comunes.getComunes();
        if(isCompleto) {
          reporteSeleccion= figura.toLong("tipo").equals(1L)? EReportes.DESTAJOS_TOTALES_CONTRATISTA: EReportes.DESTAJOS_TOTALES_SUBCONTRATISTA;  
          parametros.put("REPORTE_TIPO_PERSONA", figura.toLong("tipo").equals(1L)? "DESTAJO CONTRATISTA": "DESTAJO SUBCONTRATISTA"); 
          parametros.put("REPORTE_FIGURA", figura.toString("nombreCompleto"));
        } // if
        else {
          reporteSeleccion= figura.toLong("tipo").equals(1L)? EReportes.DESTAJOS_CAT_CONTRATISTA: EReportes.DESTAJOS_CAT_SUBCONTRATISTA;  
          parametros.put("REPORTE_DESARROLLO", "[".concat(getRegistroDesarrollo().getDesarrollo().getClave()).concat("] ".concat(getRegistroDesarrollo().getDesarrollo().getDescripcion())));
          parametros.put("REPORTE_DESARROLLO_DOMICILIO", getRegistroDesarrollo().getDomicilio().getCalle().concat(" # ").concat(getRegistroDesarrollo().getDomicilio().getNumeroExterior()));
          parametros.put("REPORTE_DESARROLLO_CP", getRegistroDesarrollo().getDomicilio().getCodigoPostal()); 
          parametros.put("REPORTE_FIGURA", figura.toString("puesto").concat(": ").concat(figura.toString("nombreCompleto")));
        } // else
        index= ((List<UISelectItem>)this.attrs.get("especialidades")).indexOf(new UISelectItem(Long.valueOf(this.attrs.get("especialidad").toString())));
        parametros.put("REPORTE_DEPARTAMENTO", ((List<UISelectItem>)this.attrs.get("especialidades")).get(index).getLabel());
        parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getNombre().toUpperCase());
        parametros.put("REPORTE_TITULO", reporteSeleccion.getTitulo());
        parametros.put("NOMBRE_REPORTE", reporteSeleccion.getNombre());
        parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));
        params.put("sortOrder", "order by tc_keet_desarrollos.nombres, tc_keet_contratos.clave, tc_keet_contratos_lotes.manzana, tc_keet_contratos_lotes.lote");
        params.put("loNuevo", this.ultima.getIdCompleta()== 0L? figura.toLong("tipo").equals(1L)? "or tc_keet_contratos_destajos_contratistas.id_nomina is null": "or tc_keet_contratos_destajos_proveedores.id_nomina is null": "");
        params.put("idNomina", this.ultima.getIdNominaEstatus()== 5L? -1: this.ultima.getIdNomina());
        params.put("idEmpresaPersona", figura.getKey().toString().substring(4));
        params.put("idProveedor", figura.getKey().toString().substring(4));
        params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
        params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
        this.attrs.put("tituloCorreo", reporteSeleccion.getTitulo());
        this.reporte= JsfBase.toReporte();
        this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));		
        if(sendMail)
          this.reporte.doAceptarSimple();			
        else {
          if(this.doVerificarReporte())
            this.reporte.doAceptar();			
        } // else			
      } // 
      else
        JsfBase.addMessage("No se puede generar el reporte del destajo, no hay contratista ó sub-contratista !");
    } // try
    catch(Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);			
    } // catch	
  } // doReporte 	

	public String doColorNomina(Entity row) {
		return !row.toString("porcentaje").equals("100.00")? "janal-tr-error": Cadena.isVacio(row.toLong("idNomina"))? "": "janal-tr-diferencias";
	}
 
  public void doUpdateNomina() {
    try {      
      List<UISelectEntity> semanas= (List<UISelectEntity>)this.attrs.get("semanas");
      if(semanas!= null && !semanas.isEmpty()) {
        int index= semanas.indexOf((UISelectEntity)this.attrs.get("semana"));
        if(index>= 0) {
          UISelectEntity semana= semanas.get(index);
          this.ultima= new Nomina(semana.toLong("idNomina"), semana.toLong("idNominaEstatus"), new Long(index));			
          this.attrs.put("semana", semanas.get(index));
        } // if  
      } // if  
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
 
  public void doWhatsup() {
    Map<String, Object> params= null;
    String contratista        = null;
    UISelectEntity figura     = null;
    UISelectEntity semana     = null;  
    try {      
      params = new HashMap<>();      
      List<UISelectEntity> figuras= (List<UISelectEntity>)this.attrs.get("figuras");
      int index= figuras.indexOf((UISelectEntity)this.attrs.get("figura"));
      if(index>= 0) {
        figura= figuras.get(index);			
        List<Entity> celulares= null;
        if(Objects.equals(figura.toLong("tipo"), 1L)) {
          params.put(Constantes.SQL_CONDICION, "id_persona="+ figura.toLong("idPersona"));
          celulares= (List<Entity>)DaoFactory.getInstance().toEntitySet("TrManticPersonaTipoContactoDto", "row", params);
        } // if
        else {
          params.put(Constantes.SQL_CONDICION, "id_proveedor="+ figura.toLong("idPersona"));
          celulares= (List<Entity>)DaoFactory.getInstance().toEntitySet("TrManticProveedorTipoContactoDto", "row", params);
        } // else
        if(celulares!= null && !celulares.isEmpty())
          for (Entity telefono: celulares) {
            if(Objects.equals(telefono.toLong("idPreferido"), 1L) && (Objects.equals(telefono.toLong("idTipoContacto"), ETiposContactos.CELULAR.getKey()) || Objects.equals(telefono.toLong("idTipoContacto"), ETiposContactos.CELULAR_NEGOCIO.getKey()) || Objects.equals(telefono.toLong("idTipoContacto"), ETiposContactos.CELULAR_PERSONAL.getKey()))) 
              contratista= telefono.toString("valor");
          } // for      
        this.doReporte("DESARROLLO", true);
        if(contratista!= null) {
          List<UISelectEntity> semanas= (List<UISelectEntity>)this.attrs.get("semanas");
          if(semanas!= null && !semanas.isEmpty())  {
            index= semanas.indexOf((UISelectEntity)this.attrs.get("semana"));
            if(index>= 0) 
              semana= semanas.get(index);
            else {
              semana= new UISelectEntity(-1L);
              semana.put("nomina", new Value("nomina", "0000-00"));
              semana.put("inicio", new Value("inicio", LocalDateTime.now()));
              semana.put("termino", new Value("termino", LocalDateTime.now()));
            } // if
          } // if
          try {
            Cafu notificar= new Cafu(
              figura.toString("nombreCompleto"), 
              contratista, 
              this.reporte.getAlias(), 
              semana.toString("nomina"), 
              "*"+ semana.toString("inicio")+ "* al *"+ semana.toString("termino")+ "*"
            );
            LOG.info("Enviando mensaje por whatsup al celular: "+ contratista);
            notificar.doSendDestajo();
          } // try
          finally {
            LOG.info("Eliminando archivo temporal: "+ this.reporte.getNombre());				  
          } // finally	
          if(contratista.length()> 0)
            JsfBase.addMessage("Se envió el mensaje de whatsup de forma exitosa ["+ contratista+ "] !", ETipoMensaje.INFORMACION);
          else
            JsfBase.addMessage("No se selecciono ningún celular, por favor verifiquelo e intente de nueva cuenta.", ETipoMensaje.ALERTA);
        } // if  
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
  
}