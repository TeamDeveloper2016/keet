package mx.org.kaana.keet.catalogos.contratos.destajos.backing;

import java.io.Serializable;
import java.time.LocalDateTime;
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
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.Codigo;
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.Criterio;
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.Lote;
import mx.org.kaana.keet.catalogos.contratos.destajos.comun.IBaseReporteDestajos;
import mx.org.kaana.keet.catalogos.desarrollos.beans.RegistroDesarrollo;
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.Revision;
import mx.org.kaana.keet.catalogos.contratos.destajos.reglas.Transaccion;
import mx.org.kaana.keet.comun.gps.Point;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.keet.nomina.beans.Nomina;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.libs.wassenger.Cafu;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.enums.ETiposContactos;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@Named(value = "keetCatalogosContratosDestajosFaltan")
@ViewScoped
public class Faltan extends IBaseReporteDestajos implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;			
  private static final Log LOG = LogFactory.getLog(Faltan.class);
  
	private RegistroDesarrollo registroDesarrollo;		
	private FormatLazyModel lazyDestajo;
	private Nomina ultima;  
  private String costoTotal;
  private String costoAnticipo;
  private List<Codigo> model;
  private List<Lote> fields;
	
	public RegistroDesarrollo getRegistroDesarrollo() {
		return registroDesarrollo;
	}

	public void setRegistroDesarrollo(RegistroDesarrollo registroDesarrollo) {
		this.registroDesarrollo = registroDesarrollo;
	}			

	public FormatLazyModel getLazyDestajo() {
		return lazyDestajo;
	}

	public String getCostoTotal() {
    return this.costoTotal;
	}

  public String getCostoAnticipo() {
    return costoAnticipo;
  }

  public List<Codigo> getModel() {
    return model;
  }

  public List<Lote> getFields() {
    return fields;
  }
  
  @PostConstruct
  @Override
  protected void init() {		
    EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;
    try {
      this.model        = new ArrayList<>();
			this.initBase();
      this.fields       = new ArrayList<>();
      this.costoTotal   = "$ 0.00";
      this.costoAnticipo= "$ 0.00";
  		opcion      = (EOpcionesResidente)JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long)JsfBase.getFlashAttribute("idDesarrollo");			
//			opcion      = EOpcionesResidente.PROPUESTA;
//			idDesarrollo= 13L;
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("opcionAdicional", JsfBase.getFlashAttribute("opcionAdicional"));
			this.attrs.put("idDesarrollo", idDesarrollo);
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());				
			this.attrs.put("semana", JsfBase.getFlashAttribute("semana"));
      this.attrs.put("contrato", JsfBase.getFlashAttribute("contrato"));
			this.attrs.put("manzana", JsfBase.getFlashAttribute("manzana"));	
      this.attrs.put("destajos", false);				
      this.attrs.put("persona", false);				
      this.attrs.put("proveedor", false);							
			if(JsfBase.getFlashAttribute("idDesarrolloProcess")!= null) {
				UISelectEntity figura= (UISelectEntity) JsfBase.getFlashAttribute("figura");
				this.attrs.put("idDepartamento", JsfBase.getFlashAttribute("idDepartamento"));
				this.attrs.put("idFigura", figura);
      } // if
			this.toLoadCatalogos();			
      this.attrs.put("idDepartamento", null);
  		this.attrs.put("idFigura", null);
    } // try 
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void toLoadCatalogos() throws Exception {
    List<Columna> columns    = new ArrayList<>();		
		Map<String, Object>params= new HashMap<>();
		try {
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));                  
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));    
			this.registroDesarrollo= new RegistroDesarrollo((Long)this.attrs.get("idDesarrollo"));      
			this.attrs.put("domicilio", this.toDomicilio());			
      params.put("idTipoNomina", "1");
      List<UISelectEntity> semanas= (List<UISelectEntity>)UIEntity.build("VistaNominaDto", "ultima", params, columns);
      this.attrs.put("semanas", semanas);
      if(semanas!= null && !semanas.isEmpty()) {
        if(Cadena.isVacio(this.attrs.get("semana"))) {
          UISelectEntity semana= semanas.get(0);
          this.ultima= new Nomina(semana.toLong("idNomina"), semana.toLong("idNominaEstatus"), 0L);			
          this.attrs.put("semana", semanas.get(0));
        } // if  
        else {
          int index= semanas.indexOf((UISelectEntity)this.attrs.get("semana"));
          if(index>= 0) {
            UISelectEntity semana= semanas.get(index);
            this.ultima= new Nomina(semana.toLong("idNomina"), semana.toLong("idNominaEstatus"), 0L);			
            this.attrs.put("semana", semanas.get(0));
          } // if
          else 
            this.ultima= new Nomina();
        } // if
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
    Map<String, Object> params    = new HashMap<>();
		List<UISelectEntity> contratos= null;
    try {      
      params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			contratos= UIEntity.build("VistaTableroDto", "contratos", params);
			this.attrs.put("contratos", contratos);
      if(Cadena.isVacio(this.attrs.get("contrato"))) 
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
    Map<String, Object> params   = new HashMap<>();
		List<UISelectEntity> manzanas= null;
    try {      
      if(this.attrs.get("casa")!= null && ((UISelectEntity)this.attrs.get("casa")).getKey()>= 0)
        this.attrs.put("casa", new UISelectEntity(-1L));
      params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
			manzanas= UIEntity.seleccione("VistaTableroDto", "manzanas", params, "nombre");
			this.attrs.put("manzanas", manzanas);
      if(Cadena.isVacio(this.attrs.get("manzana"))) 
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
		List<UISelectEntity>especialidades= null;
		Map<String, Object>params         = new HashMap<>();
		List<Columna> columns             = new ArrayList<>();
		try {
      if(this.attrs.get("casa")!= null && ((UISelectEntity)this.attrs.get("casa")).getKey()>= 0)
        this.attrs.put("casa", new UISelectEntity(-1L));
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
			columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
			especialidades= UIEntity.build("VistaCapturaDestajosDto", "especialidades", params, columns);
			this.attrs.put("especialidades", especialidades);
      if(especialidades!= null && !especialidades.isEmpty()) {
        if(this.attrs.get("idDepartamento")!= null) {
          int index= especialidades.indexOf(new UISelectEntity((Long)this.attrs.get("idDepartamento")));
          if(index>= 0)
			      this.attrs.put("especialidad", new UISelectEntity((Long)this.attrs.get("idDepartamento")));
        } // if  
        else
			    this.attrs.put("especialidad", UIBackingUtilities.toFirstKeySelectEntity(especialidades));
      } // if  
      this.doLoadFiguras();
		} // try
		finally {
			Methods.clean(params);
		} // finally
	} 
	
	public void doLoadFiguras() {
		List<UISelectEntity> figuras= null;
		Map<String, Object>params   = new HashMap<>();
		List<Columna> columns       = new ArrayList<>();
		try {
      if(this.attrs.get("casa")!= null && ((UISelectEntity)this.attrs.get("casa")).getKey()>= 0)
        this.attrs.put("casa", new UISelectEntity(-1L));
      
      List<UISelectEntity> especialidades= (List<UISelectEntity>)this.attrs.get("especialidades");
      if(!Objects.equals(especialidades, null)) {
        int index= especialidades.indexOf((UISelectEntity)this.attrs.get("especialidad"));
        if(index>= 0)
          this.attrs.put("especialidad", especialidades.get(index));
        else
          this.attrs.put("especialidad", UIBackingUtilities.toFirstKeySelectEntity(especialidades));
      } // if  
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			params.put("idDepartamento", ((UISelectEntity)this.attrs.get("especialidad")).getKey());
			params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
			columns.add(new Columna("nombreCompleto", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("puesto", EFormatoDinamicos.MAYUSCULAS));
			figuras= UIEntity.build("VistaCapturaDestajosDto", "empleadosAsociados", params, columns);
			this.attrs.put("figuras", figuras);
      if(figuras!= null && !figuras.isEmpty())
        if(this.attrs.get("idFigura")!= null) {
          int index= figuras.indexOf((UISelectEntity)this.attrs.get("idFigura"));
          if(index>= 0)
			      this.attrs.put("figura", figuras.get(index));
        } // if  
        else
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
    List<UISelectEntity> contratos= (List<UISelectEntity>)this.attrs.get("contratos");    
		UISelectEntity contrato       = (UISelectEntity)this.attrs.get("contrato");
    try {   
      int index= contratos.indexOf(contrato);
      if(index>= 0)
        this.attrs.put("contrato", contratos.get(index));
      this.toLoadManzanas();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
	public void doLoadDepartamentos() {
    List<UISelectEntity> manzanas= (List<UISelectEntity>)this.attrs.get("manzanas");    
		UISelectEntity manzana       = (UISelectEntity)this.attrs.get("manzana");
    try {   
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
		StringBuilder regresar= new StringBuilder();
		try {
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
    if(this.attrs.get("contrato") instanceof Entity)
      this.attrs.put("contrato", new UISelectEntity((Entity)this.attrs.get("contrato")));
    if(this.attrs.get("manzana") instanceof Entity)
      this.attrs.put("manzana", new UISelectEntity((Entity)this.attrs.get("manzana")));
    if(this.attrs.get("casa") instanceof Entity)
      this.attrs.put("casa", new UISelectEntity((Entity)this.attrs.get("casa")));
		UISelectEntity contrato= (UISelectEntity)this.attrs.get("contrato");
		UISelectEntity manzana = (UISelectEntity)this.attrs.get("manzana");
		UISelectEntity lote    = (UISelectEntity)this.attrs.get("casa");
    if(contrato!= null && contrato.getKey()> 0L)
		  regresar.append("tc_keet_contratos.id_contrato= ").append(contrato.getKey()).append(" and ");
    if(manzana!= null && manzana.getKey()> 0L && this.attrs.get("manzanas")!= null) {
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
		Map<String, Object>params   = new HashMap<>();
		List<UISelectEntity> figuras= (List<UISelectEntity>) this.attrs.get("figuras");
		UISelectEntity figura       = (UISelectEntity)this.attrs.get("figura");
		List<UISelectEntity> casas  = null;
		String idXml                = null;
    try {   
      int index= figuras.indexOf(figura);
      if(index>= 0) {
			  figura= figuras.get(index);
        switch(Configuracion.getInstance().getPropiedad("sistema.empresa.principal")) {
          case "cafu":
            params.put("estatus", "5");	
            break;
          case "gylvi": 
          case "triana":
          default:
            params.put("estatus", "1, 2, 3, 4, 5, 6, 7, 8, 9, 10");	
            break;
        } // swtich
        params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
        params.put("idFigura", figura.getKey()> 0? figura.getKey().toString().substring(4): figura.getKey());
        params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
        idXml= figura.toLong("tipo").equals(1L)? "lotesContratistas": "lotesSubContratistas";
        casas= UIEntity.seleccione("VistaCapturaDestajosDto", idXml, params, Constantes.SQL_TODOS_REGISTROS, "descripcionLote");
        this.attrs.put("casas", casas);
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
		List<UISelectEntity> figuras= (List<UISelectEntity>) this.attrs.get("figuras");
		UISelectEntity figura       = (UISelectEntity)this.attrs.get("figura");
		Map<String, Object>params   = new HashMap<>();
    List<Entity> casas          = null;
    List<Entity> rubros         = null;
    List<Entity> codigos        = null;
		String idXml                = null;
    String anterior             = "";
    try {
      int index= figuras.indexOf(figura);
      if(index>= 0) {
			  figura= figuras.get(index);
        this.model.clear();
        idXml= figura.toLong("tipo").equals(1L)? "contratistas": "proveedores";
        this.attrs.put("idTipoFiguraCorreo", figura.toLong("tipo"));
        this.attrs.put("idFiguraCorreo", figura.getKey()> 0? figura.getKey().toString().substring(4): figura.getKey());
        this.attrs.put("figuraNombreCompletoCorreo", figura.toString("nombreCompleto"));
        params.put("clave", this.toTokenClave());
        params.put("idDepartamento",  ((UISelectEntity)this.attrs.get("especialidad")).getKey());
        params.put("idFigura", figura.getKey()> 0? figura.getKey().toString().substring(4): figura.getKey());
        params.put("idTipoNomina", "1");
        params.put("nombreConcepto", "");
        params.put("estatus", EEstacionesEstatus.INICIAR.getKey()+ ","+ EEstacionesEstatus.EN_PROCESO.getKey()+ "," + EEstacionesEstatus.TERMINADO.getKey());
        params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
        Entity semana= (Entity)DaoFactory.getInstance().toEntity("VistaNominaDto", "ultima", params);
        if(semana!= null)
          params.put("semana", semana.toString("semana"));
        else
          params.put("semana", "2000-0");
        codigos= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaSeguimientoDto", "codigos", params);
        this.prepare(this.model, codigos);
        this.fields.clear();
        casas= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaSeguimientoDto", idXml, params, Constantes.SQL_TODOS_REGISTROS);
        if(casas!= null && !casas.isEmpty()) {
          for (Entity item: casas) {
            String lote= item.toString("lote").replaceAll("-", "");
            index= this.model.indexOf(new Codigo(item.toString("codigo")));
            if(index>= 0) {
              Codigo concepto= this.model.get(index);
              concepto.put(lote, new Criterio(lote, item.toDate("inicio"), item.toDate("termino"), item.toLong("idEstacionEstatus"), item.toString("estatus"), item.toLong("idNomina"), item.toString("semana"), item.toLong("actual"), item));
              if(!Objects.equals(lote, anterior)) {
                this.fields.add(new Lote(lote, lote, "", "janal-column-center MarAuto Responsive janal-col-120"));
                anterior= lote;
              } // if  
            } // if
            else
              throw new RuntimeException("El concepto ["+ item.toString("codigo")+ "] no existe en la consulta !");
          } // for
          rubros= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaSeguimientoDto", "rubros", params, Constantes.SQL_TODOS_REGISTROS);
          StringBuilder sb= new StringBuilder(Constantes.SEPARADOR);
          for (Entity rubro: rubros) {
            sb.append(rubro.toString("codigo")).append(Constantes.SEPARADOR);
          } // for
          // LIMPIAR LOS CONCEPTOS QUE NO PERTENECEN AL DEPARTAMENTO
          index= 0;
          while (index< this.model.size()) {
            Codigo concepto= this.model.get(index);
            if(!concepto.getCodigo().startsWith("#"))
              if(sb.indexOf(Constantes.SEPARADOR.concat(concepto.getCodigo()).concat(Constantes.SEPARADOR))< 0)
                this.model.remove(index);
              else
                index++;
            else
              index++;
          } // while
          // LIMPIAR LAS ESTACIONES VACIAS    
          index        = 0;
          String codigo= null;
          while (index< this.model.size()) {
            Codigo concepto= this.model.get(index);
            if(codigo!= null && codigo.startsWith("#") && concepto.getCodigo().startsWith("#"))
              this.model.remove(index- 1);
            else
              index++;
            codigo= concepto.getCodigo();
          } // while
          if(this.model.get(this.model.size()- 1).getCodigo().startsWith("#"))
            this.model.remove(this.model.size()- 1);
        } // if
        else   
          this.model= new ArrayList<>();
        UIBackingUtilities.resetDataTable("destajo");
      } // if  
      else 
        this.model= new ArrayList<>();
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
      Methods.clean(params);
    } // finally				
  } // doPartidas	
  
  public String toTokenClave() {
    StringBuilder regresar = new StringBuilder();
		UISelectEntity contrato= (UISelectEntity)this.attrs.get("contrato");
    if(contrato!= null && contrato.getKey()> 0L) {
      List<UISelectEntity> contratos= (List<UISelectEntity>)this.attrs.get("contratos");
      if(contratos!= null) {
        contrato= contratos.get(contratos.indexOf(contrato));
		    regresar.append(Cadena.rellenar(contrato.toString("idEmpresa"), 3, '0', true)).append(contrato.toLong("ejercicio")).append(Cadena.rellenar(contrato.toString("orden"), 3, '0', true));
      } // if
      else
        regresar.append(Cadena.rellenar("", 25, '9', true));
    } // else  
    else
      regresar.append(Cadena.rellenar("", 25, '9', true));
    return regresar.toString();
  }  
  
	public String doCancelar() {
    String regresar                   = null;    
		EOpcionesResidente opcion         = ((EOpcionesResidente)this.attrs.get("opcionResidente"));		
		EOpcionesResidente opcionAdicional= null;		
    try {			
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
    List<Columna> columns    = new ArrayList<>();
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
        columns.add(new Columna("porcentaje", EFormatoDinamicos.MILES_SAT_DECIMALES));
        columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("anticipo", EFormatoDinamicos.MILES_CON_DECIMALES));
        columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
        this.lazyDestajo= new FormatCustomLazy("VistaNominaConsultasDto", figura.toLong("tipo").equals(1L)? "destajoPersona": "destajoProveedor", params, columns);
        Entity costo= (Entity)DaoFactory.getInstance().toEntity("VistaNominaConsultasDto", figura.toLong("tipo").equals(1L)? "costoPersona": "costoProveedor", params);
        if(costo!= null && !costo.isEmpty()) {
          this.costoTotal   = Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, costo.toDouble("total"));
          this.costoAnticipo= Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, costo.toDouble("anticipo"));
        } // if  
        UIBackingUtilities.resetDataTable("tabla");
        this.attrs.put("destajos", true);
      } // if
      else {
        this.attrs.put("destajos", false);
        this.lazyDestajo  = null;
        this.costoTotal   = "$ 0.00";
        this.costoAnticipo= "$ 0.00";
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
    Map<String, Object>params    = new HashMap<>();
    UISelectEntity figura        = null;
    Parametros comunes           = null;
    boolean isCompleto           = false;
    try {
      isCompleto = tipo.equals("COMPLETO");
			List<UISelectEntity> figuras= (List<UISelectEntity>) this.attrs.get("figuras");
      int index= figuras.indexOf((UISelectEntity) this.attrs.get("figura"));
      if(index>= 0) {
        figura= figuras.get(index);			
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
        index= ((List<UISelectEntity>)this.attrs.get("especialidades")).indexOf((UISelectEntity)this.attrs.get("especialidad"));
        parametros.put("REPORTE_EMPRESA_LOGO", this.toLookForEmpresaLogo(JsfBase.getAutentifica().getEmpresa().getIdEmpresa()));
        parametros.put("REPORTE_DEPARTAMENTO", ((List<UISelectEntity>)this.attrs.get("especialidades")).get(index).toString("nombre"));
        parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getNombre().toUpperCase());
        parametros.put("REPORTE_TITULO", reporteSeleccion.getTitulo());
        parametros.put("NOMBRE_REPORTE", reporteSeleccion.getNombre());
        parametros.put("REPORTE_ICON", JsfBase.getRealPath("/resources/janal/img/sistema/"));
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
        else
          this.ultima= new Nomina();
      } // if  
      else
        this.ultima= new Nomina();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
 
  public void doWhatsup() {
    Map<String, Object> params= new HashMap<>();
    String contratista        = null;
    UISelectEntity figura     = null;
    UISelectEntity semana     = null;  
    try {      
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
            LOG.info("Enviando mensaje por whatsapp al celular: "+ contratista);
            notificar.doSendDestajo();
          } // try
          finally {
            LOG.info("Eliminando archivo temporal: "+ this.reporte.getNombre());				  
          } // finally	
          if(contratista.length()<= 0)
            JsfBase.addMessage("No se selecciono ningún celular, por favor verifiquelo e intente de nueva cuenta", ETipoMensaje.ALERTA);
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
 
	public String doColor(Codigo row) {
		return ((String)row.get("codigo")).startsWith("#")? "janal-tr-diferencias": "";
	}
 
  public Boolean isConcepto(Codigo row) {
    return !row.getCodigo().startsWith("#");
  }
 
  public String doDetalle(Integer option, Codigo row, String key) {
    String regresar= null;
    switch(option) {
      case 0: // REGISTRAR AL 100% LOS CONCEPTOS
        regresar= this.toRegistrar(row, key);
        break;
      case 1: // IR LA OPCION NORMAL QUE SE TIENE
        regresar= this.toDetalle(row, key);
        break; 
      case 2: // IR LA PAGINA DE RECHAZAR LOS CONCEPTOS
        regresar= this.toRechazos(row, key);
        break;
    } // switch
    return regresar;
  }
  
  private String toDetalle(Codigo row, String key) {
    String regresar             = null;    		
		List<UISelectEntity> figuras= null;
		UISelectEntity figura       = null;
    try {			
      Criterio criterio= (Criterio)row.get(key);
      if(Objects.equals(criterio.getIdEstacionEstatus(), EEstacionesEstatus.INICIAR.getKey()) || Objects.equals(criterio.getIdEstacionEstatus(), EEstacionesEstatus.EN_PROCESO.getKey())) {
        figuras= (List<UISelectEntity>) this.attrs.get("figuras");
        figura= figuras.get(figuras.indexOf((UISelectEntity) this.attrs.get("figura")));
        JsfBase.setFlashAttribute("opcionResidente", (EOpcionesResidente)this.attrs.get("opcionResidente"));												
        JsfBase.setFlashAttribute("opcionAdicional", (EOpcionesResidente)this.attrs.get("opcionAdicional"));												
        JsfBase.setFlashAttribute("figura", figura);									
        JsfBase.setFlashAttribute("casa", this.attrs.get("casa"));									
        JsfBase.setFlashAttribute("idDepartamento", ((UISelectEntity)this.attrs.get("especialidad")).getKey());
        JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));				
        JsfBase.setFlashAttribute("georreferencia", new Point(21.890563, -102.252030));				
        JsfBase.setFlashAttribute("concepto", criterio.getDatos());
        JsfBase.setFlashAttribute("seleccionado", criterio.getDatos());
        JsfBase.setFlashAttribute("claveEstacion", criterio.getDatos().toString("clave"));
        JsfBase.setFlashAttribute("total", criterio.getDatos().toDouble("importe"));
        JsfBase.setFlashAttribute("anticipo", criterio.getDatos().toDouble("retencion"));
        JsfBase.setFlashAttribute("nombreConcepto", "");			
        JsfBase.setFlashAttribute("semana", this.attrs.get("semana"));			
        JsfBase.setFlashAttribute("contrato", this.attrs.get("contrato"));			
        JsfBase.setFlashAttribute("manzana", this.attrs.get("manzana"));	
        JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Contratos/Destajos/faltan");			
        regresar= "puntos".concat(Constantes.REDIRECIONAR);			
      } // if
      else 
        JsfBase.addMessage("Este concepto ya esta pagado, favor de verificarlo !", ETipoMensaje.ALERTA);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  }
 
  private String toRegistrar(Codigo row, String key) {
		List<UISelectEntity> figuras= null;
		UISelectEntity figura       = null;
    String regresar             = null;    		
    Transaccion transaccion     = null;		
		Map<String, Object>params   = new HashMap<>();
    try {      			
      Criterio criterio= (Criterio)row.get(key);   
      if(Objects.equals(criterio.getIdEstacionEstatus(), EEstacionesEstatus.INICIAR.getKey()) || Objects.equals(criterio.getIdEstacionEstatus(), EEstacionesEstatus.EN_PROCESO.getKey())) {
        params.put("idDepartamento", ((UISelectEntity)this.attrs.get("especialidad")).getKey());
        params.put("idPuntoGrupo", criterio.getDatos().toLong("idPuntoGrupo"));
        params.put("idEstacion", criterio.getDatos().toLong("idEstacion"));
        params.put("importe", criterio.getDatos().toDouble("importe"));
        params.put("anticipo", criterio.getDatos().toDouble("retencion"));
        List<Entity> puntos=(List<Entity>)DaoFactory.getInstance().toEntitySet("VistaCapturaDestajosDto", "puntosRevision", params);
        if(!Objects.equals(puntos, null) && puntos.size()> 0) {
          figuras= (List<UISelectEntity>) this.attrs.get("figuras");
          figura = figuras.get(figuras.indexOf((UISelectEntity) this.attrs.get("figura")));
          transaccion= new Transaccion(this.toLoadRevision(figura, criterio, puntos), EEstacionesEstatus.TERMINADO.getKey());
          if(transaccion.ejecutar(EAccion.PROCESAR)) {
            criterio.setEstatus("TERMINADO");
            criterio.setIdEstacionEstatus(EEstacionesEstatus.TERMINADO.getKey());
            criterio.setSemaforo("circulo-verde.png");
            JsfBase.addMessage("Puntos de revisión", "Se registraron de forma correcta los puntos de revision", ETipoMensaje.INFORMACION);
          } // IF  
          else
            JsfBase.addMessage("Puntos de revisión", "Ocurrió un error al registrar los puntos de revision", ETipoMensaje.ERROR);
        } // if
      } // if
      else 
        JsfBase.addMessage("Este concepto ya esta pagado, favor de verificarlo !", ETipoMensaje.ALERTA);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    finally {      
      Methods.clean(params);
    } // finally			
    return regresar;
  }
  
	private Revision toLoadRevision(Entity figura, Criterio criterio, List<Entity> puntos) {
		Revision regresar= new Revision();
		Long idFigura    = -1L;
		try {
			idFigura= figura.toLong("tipo").equals(1L)? criterio.getDatos().toLong("idContratoLoteContratista"): criterio.getDatos().toLong("idContratoLoteProveedor");
			regresar.setIdFigura(idFigura);
			regresar.setTipo(figura.toLong("tipo"));
			regresar.setIdEstacion(criterio.getDatos().toLong("idEstacion"));
      // AQUI VAN LOS PUNTOS QUE FUERON MARCADOS CON SUS PORCENTAJES
			regresar.setPuntosRevision(puntos.toArray(new Entity[0]));
			regresar.setLatitud("21.890563"); 
			regresar.setLongitud("-102.252030");
			regresar.setMetros(0D);
			regresar.setIdContratoLote(criterio.getDatos().toLong("idContratoLote"));
			regresar.setClave(criterio.getDatos().toString("clave"));
			regresar.setIdDepartamento(((UISelectEntity)this.attrs.get("especialidad")).getKey());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} 
  
  private String toRechazos(Codigo row, String key) {
    String regresar             = null;    		
		List<UISelectEntity> figuras= null;
		UISelectEntity figura       = null;
    try {			
      Criterio criterio= (Criterio)row.get(key);
      if(Objects.equals(criterio.getIdEstacionEstatus(), EEstacionesEstatus.EN_PROCESO.getKey()) || Objects.equals(criterio.getIdEstacionEstatus(), EEstacionesEstatus.TERMINADO.getKey())) {
        figuras= (List<UISelectEntity>) this.attrs.get("figuras");
        figura= figuras.get(figuras.indexOf((UISelectEntity) this.attrs.get("figura")));
        JsfBase.setFlashAttribute("opcionResidente", (EOpcionesResidente)this.attrs.get("opcionResidente"));												
        JsfBase.setFlashAttribute("opcionAdicional", (EOpcionesResidente)this.attrs.get("opcionAdicional"));												
        JsfBase.setFlashAttribute("figura", figura);									
        JsfBase.setFlashAttribute("casa", this.attrs.get("casa"));									
        JsfBase.setFlashAttribute("idDepartamento", ((UISelectEntity)this.attrs.get("especialidad")).getKey());
        JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));				
        JsfBase.setFlashAttribute("georreferencia", new Point(21.890563, -102.252030));				
        JsfBase.setFlashAttribute("concepto", criterio.getDatos());
        JsfBase.setFlashAttribute("seleccionado", criterio.getDatos());
        JsfBase.setFlashAttribute("claveEstacion", criterio.getDatos().toString("clave"));
        JsfBase.setFlashAttribute("total", criterio.getDatos().toDouble("importe"));
        JsfBase.setFlashAttribute("anticipo", criterio.getDatos().toDouble("retencion"));
        JsfBase.setFlashAttribute("nombreConcepto", "");			
        JsfBase.setFlashAttribute("semana", this.attrs.get("semana"));			
        JsfBase.setFlashAttribute("contrato", this.attrs.get("contrato"));			
        JsfBase.setFlashAttribute("manzana", this.attrs.get("manzana"));	
        JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Contratos/Destajos/faltan");			
        regresar= "rechazos".concat(Constantes.REDIRECIONAR);			
      } // if
      else 
        JsfBase.addMessage("Este concepto NO tiene puntos de revision pagados, favor de verificarlo !", ETipoMensaje.ALERTA);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  }
  
}