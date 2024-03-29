package mx.org.kaana.keet.controles.backing;

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
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.enums.EReportes;

@Named(value = "keetControlesControl")
@ViewScoped
public class Control extends IBaseReporteDestajos implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;			
  
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
		UISelectEntity figura    = null;
    try {
			this.initBase();
      this.costoTotal= "$ 0.00";
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");			
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("opcionAdicional", JsfBase.getFlashAttribute("opcionAdicional"));
			this.attrs.put("idDesarrollo", idDesarrollo);
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());				
      this.attrs.put("destajos", false);				
      this.attrs.put("persona", false);				
			if(JsfBase.getFlashAttribute("idDesarrolloProcess")!= null) {
				figura        = (UISelectEntity) JsfBase.getFlashAttribute("figura");
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
		Map<String, Object>params= new HashMap<>();
		try {
			this.registroDesarrollo= new RegistroDesarrollo((Long)this.attrs.get("idDesarrollo"));      
			this.attrs.put("domicilio", toDomicilio());			
      params.put("idTipoNomina", "1");
      this.ultima= (Nomina)DaoFactory.getInstance().toEntity(Nomina.class, "VistaNominaDto", "ultima", params);			
      this.toLoadContratos();
		} // try
    finally {
			Methods.clean(params);
		} // finally
	} // loadCatalogos	
	
	private void toLoadEspecialidades() {
		List<UISelectItem>especialidades= null;
		Map<String, Object>params       = new HashMap<>();
		try {
      if(this.attrs.get("casa")!= null && ((UISelectEntity)this.attrs.get("casa")).getKey()>= 0)
        this.attrs.put("casa", new UISelectEntity(-1L));
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
			especialidades= UISelect.build("VistaCapturaDestajosDto", "residentes", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
			this.attrs.put("especialidades", especialidades);
      if(especialidades!= null && !especialidades.isEmpty()) {
        if(this.attrs.get("idDepartamento")!= null) {
          int index= especialidades.indexOf(new UISelectItem(this.attrs.get("idDepartamento")));
          if(index>= 0)
			      this.attrs.put("especialidad", this.attrs.get("idDepartamento"));
        } // if  
        else
			    this.attrs.put("especialidad", UIBackingUtilities.toFirstKeySelectItem(especialidades));
      } // if  
      this.doLoadFiguras();
		} // try
		finally {
			Methods.clean(params);
		} // finally
	} // loadEspecialidades
	
	public void doLoadFiguras() {
		List<UISelectEntity> figuras= null;
		Map<String, Object>params   = new HashMap<>();
		List<Columna> columns       = new ArrayList<>();
		try {
      if(this.attrs.get("casa")!= null && ((UISelectEntity)this.attrs.get("casa")).getKey()>= 0)
        this.attrs.put("casa", new UISelectEntity(-1L));
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			params.put("idDepartamento", this.attrs.get("especialidad"));
			columns.add(new Columna("nombreCompleto", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("puesto", EFormatoDinamicos.MAYUSCULAS));
			figuras= UIEntity.build("VistaCapturaDestajosDto", "residentesAsociados", params, columns);
			this.attrs.put("figuras", figuras);
			this.attrs.put("destajos", false);
			this.attrs.put("persona", false);
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
	} // doLoadFiguras
	
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
	
  @Override
  public void doLoad() {
		Map<String, Object>params   = new HashMap<>();
    List<Columna> columns       = new ArrayList<>();		
		List<UISelectEntity> figuras= (List<UISelectEntity>) this.attrs.get("figuras");
		UISelectEntity figura       = null;
    try {   
			int index= figuras.indexOf((UISelectEntity) this.attrs.get("figura"));
      if(index>= 0)
			  figura= figuras.get(index);
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			params.put("idFigura", figura.getKey()> 0 ? figura.getKey().toString().substring(4) : figura.getKey());
      params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));                  
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));    
			this.attrs.put("idTipoFiguraCorreo", figura.toLong("tipo"));
			this.attrs.put("idFiguraCorreo", figura.getKey()> 0? figura.getKey().toString().substring(4): figura.getKey());
			this.attrs.put("figuraNombreCompletoCorreo", figura.toString("nombreCompleto"));
	    this.lotes= DaoFactory.getInstance().toEntitySet("VistaCapturaDestajosDto", "lotesResidentes", params);		
			if(!this.lotes.isEmpty()) { 
			  UIBackingUtilities.toFormatEntitySet(this.lotes, columns);	
				this.lotes.add(0, this.toLoteDefault(this.lotes.get(0).toLong("idEmpresa"), this.lotes.get(0).toLong("ejercicio")));
				this.toEstatusManzanaLote();
			} //
			this.attrs.put("persona", figura.toLong("tipo").equals(1L));
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
	
	private Entity toLoteDefault(Long idEmpresa, Long ejercicio) {
		Entity regresar= new Entity(Constantes.USUARIO_INACTIVO);
		regresar.put("idEmpresa", new Value("idEmpresa", idEmpresa));
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
		regresar.put("ejercicio", new Value("ejercicio", ejercicio));
		return regresar;
	} // toLoteDefault
	
	private void toEstatusManzanaLote() throws Exception {
		Map<String, Object>params= new HashMap<>();
		Entity estatus           = null;
		try {
			for(Entity mzaLote: this.lotes) {
				params.clear();
				params.put("idDepartamento", this.attrs.get("especialidad"));
				params.put("clave", this.toClaveEstacion(mzaLote));
				estatus= (Entity) DaoFactory.getInstance().toEntity("VistaCapturaDestajosDto", "estatusControlManzanaLote", params);
				if(estatus.toString("total")!= null) {
					if(estatus.toLong("total").equals(estatus.toLong("terminado"))) {
						mzaLote.put("iconEstatus", new Value("iconEstatus", EEstacionesEstatus.TERMINADO.getSemaforo()));
						mzaLote.put("idControlEstatus", new Value("idControlEstatus", EEstacionesEstatus.TERMINADO.getKey()));
          } // if  
					else 
            if(estatus.toLong("total").equals(estatus.toLong("iniciado"))) {
						  mzaLote.put("iconEstatus", new Value("iconEstatus", EEstacionesEstatus.INICIAR.getSemaforo()));
						  mzaLote.put("idControlEstatus", new Value("idControlEstatus", EEstacionesEstatus.INICIAR.getKey()));
            } // if  
            else {
						  mzaLote.put("iconEstatus", new Value("iconEstatus", EEstacionesEstatus.EN_PROCESO.getSemaforo()));
						  mzaLote.put("idControlEstatus", new Value("idControlEstatus", EEstacionesEstatus.EN_PROCESO.getKey()));
            } // else  
				} // if
        else {
					mzaLote.put("iconEstatus", new Value("iconEstatus", ""));
					mzaLote.put("idControlEstatus", new Value("idControlEstatus", -1L));
        } // else  
			} // for
		} // try
		finally {
			Methods.clean(params);
		} // finally		
	} // toEstatusManzanaLote
	
	private String toClaveEstacion(Entity lote) {
		StringBuilder regresar= new StringBuilder();
		try {			
			regresar.append(Cadena.rellenar(lote.toString("idEmpresa"), 3, '0', true));
			regresar.append(lote.toString("ejercicio"));
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
		Entity seleccionado         = (Entity) this.attrs.get("seleccionado");
		List<UISelectEntity> figuras= (List<UISelectEntity>) this.attrs.get("figuras");
		UISelectEntity figura       = null;
    try {			
      int index= figuras.indexOf((UISelectEntity) this.attrs.get("figura"));
      if(index>= 0)
			  figura= figuras.get(index);
			JsfBase.setFlashAttribute("opcionResidente", (EOpcionesResidente)this.attrs.get("opcionResidente"));												
			if(this.attrs.get("opcionAdicional")!= null)
				JsfBase.setFlashAttribute("opcionAdicional", (EOpcionesResidente)this.attrs.get("opcionAdicional"));												
			JsfBase.setFlashAttribute("seleccionado", seleccionado);												
			JsfBase.setFlashAttribute("figura", figura);									
			JsfBase.setFlashAttribute("idDepartamento", Long.valueOf(this.attrs.get("especialidad").toString()));									
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));				
			JsfBase.setFlashAttribute("georreferencia", new Point(Numero.getDouble(seleccionado.toString("latitud"), 21.890563), Numero.getDouble(seleccionado.toString("longitud"), -102.252030)));				
			JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Controles/control");			
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
    List<Columna> columns    = new ArrayList<>();
		Map<String, Object>params= new HashMap<>();
    try {
			List<UISelectEntity> figuras= (List<UISelectEntity>) this.attrs.get("figuras");
			UISelectEntity figura= figuras.get(figuras.indexOf((UISelectEntity) this.attrs.get("figura")));
			params.put("sortOrder", "order by tc_keet_contratos.etapa, tc_keet_contratos_lotes.manzana, tc_keet_contratos_lotes.lote");
		  params.put("loNuevo", "or tc_keet_contratos_destajos_residentes.id_nomina is null");
		  params.put("idNomina", this.ultima.getIdNominaEstatus()== 4L? -1: this.ultima.getIdNomina());
			params.put("idEmpresaPersona", figura.getKey().toString().substring(4));
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
      columns.add(new Columna("porcentaje", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      this.lazyDestajo= new FormatCustomLazy("VistaNominaConsultasDto", "destajoResidente", params, columns);
      Entity costo= (Entity)DaoFactory.getInstance().toEntity("VistaNominaConsultasDto", "costoResidente", params);
      if(costo!= null && !costo.isEmpty())
        this.costoTotal= Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, costo.toDouble("total"));
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
  
	public void doReporte(String tipo) throws Exception {
		doReporte(tipo, false);
	} // doReporte	
	
	@Override
  public void doReporte(String tipo, boolean sendMail) throws Exception {    
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;    
    Map<String, Object>params    = new HashMap<>();
    Parametros comunes           = null;
    boolean isCompleto           = tipo.equals("COMPLETO");
    try {
			List<UISelectEntity> figuras= (List<UISelectEntity>) this.attrs.get("figuras");
      UISelectEntity figura= figuras.get(figuras.indexOf((UISelectEntity) this.attrs.get("figura")));			
      comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      parametros= comunes.getComunes();
      if(isCompleto) {
        reporteSeleccion= EReportes.DESTAJOS_TOTALES_RESIDENTE;  /* falta */
        parametros.put("REPORTE_TIPO_PERSONA", "RESIDENTE DE OBRA"); 
        parametros.put("REPORTE_FIGURA", figura.toString("nombreCompleto"));
      } // if
      else {
        reporteSeleccion= EReportes.DESTAJOS_CAT_RESIDENTE;  
        parametros.put("REPORTE_DESARROLLO", "[".concat(getRegistroDesarrollo().getDesarrollo().getClave()).concat("] ".concat(getRegistroDesarrollo().getDesarrollo().getDescripcion())));
        parametros.put("REPORTE_DESARROLLO_DOMICILIO", getRegistroDesarrollo().getDomicilio().getCalle().concat(" # ").concat(getRegistroDesarrollo().getDomicilio().getNumeroExterior()));
        parametros.put("REPORTE_DESARROLLO_CP", getRegistroDesarrollo().getDomicilio().getCodigoPostal()); 
        parametros.put("REPORTE_FIGURA", figura.toString("puesto").concat(": ").concat(figura.toString("nombreCompleto")));
      } // else
      int index= ((List<UISelectItem>)this.attrs.get("especialidades")).indexOf(new UISelectItem(Long.valueOf(this.attrs.get("especialidad").toString())));
      parametros.put("REPORTE_DEPARTAMENTO", ((List<UISelectItem>)this.attrs.get("especialidades")).get(index).getLabel());
      parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getNombre().toUpperCase());
      parametros.put("REPORTE_TITULO", reporteSeleccion.getTitulo());
      parametros.put("NOMBRE_REPORTE", reporteSeleccion.getNombre());
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("").concat("resources/iktan/icon/acciones/"));
      params.put("sortOrder", "order by tc_keet_desarrollos.nombres, tc_keet_contratos.etapa, tc_keet_contratos_lotes.manzana, tc_keet_contratos_lotes.lote");
		  params.put("loNuevo", "or tc_keet_contratos_destajos_residentes.id_nomina is null");
		  params.put("idNomina", this.ultima.getIdNominaEstatus()== 4L? -1: this.ultima.getIdNomina());
			params.put("idEmpresaPersona", figura.getKey().toString().substring(4));
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			this.attrs.put("tituloCorreo", reporteSeleccion.getTitulo());
      this.reporte= JsfBase.toReporte();
      this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, params, parametros));		
      if(sendMail)
        this.reporte.doAceptarSimple();			
			else{
				if(doVerificarReporte())
					this.reporte.doAceptar();			
			} // else			
    } // try
    catch(Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);			
    } // catch	
  } // doReporte 	

	public String doColorNomina(Entity row) {
		return Cadena.isVacio(row.toLong("idNomina"))? "": "janal-tr-diferencias";
	}
 
	private void toLoadContratos() {
    Map<String, Object> params    = new HashMap<>();
		List<UISelectEntity> contratos= null;
    try {      
      params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			contratos= UIEntity.seleccione("VistaTableroDto", "contratos", params, "clave");
			this.attrs.put("contratos", contratos);
			this.attrs.put("contrato", UIBackingUtilities.toFirstKeySelectEntity(contratos));
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
  
  public String toLoadCondicion() {
    StringBuilder regresar = new StringBuilder();
		UISelectEntity contrato= (UISelectEntity)this.attrs.get("contrato");
		UISelectEntity lote    = (UISelectEntity)this.attrs.get("casa");
    if(contrato!= null && contrato.getKey()> 0L)
		  regresar.append("tc_keet_contratos.id_contrato= ").append(contrato.getKey()).append(" and ");
   if(lote!= null && lote.getKey()> 0L)
      regresar.append("tc_keet_contratos_lotes.id_contrato_lote=").append(lote.getKey()).append( " and ");
    return regresar.length()> 0? regresar.substring(0, regresar.length()- 4): Constantes.SQL_VERDADERO;
  }  
  
  public void doLoadCasas() {
		Map<String, Object> params  = new HashMap<>();
		List<UISelectEntity> figuras= (List<UISelectEntity>) this.attrs.get("figuras");
		List<UISelectEntity> casas  = null;
		UISelectEntity figura       = (UISelectEntity)this.attrs.get("figura");
    try {   
      int index= figuras.indexOf(figura);
      if(index>= 0) {
        params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
        params.put("idFigura", figura.getKey()> 0? figura.getKey().toString().substring(4): figura.getKey());
        params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
        casas= UIEntity.seleccione("VistaCapturaDestajosDto", "lotesResidentes", params, Constantes.SQL_TODOS_REGISTROS, "descripcionLote");
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
  
  public void doLoadManzanas() {
    List<UISelectEntity> contratos= null;    
		UISelectEntity contrato       = null;
    try {   
			contratos= (List<UISelectEntity>)this.attrs.get("contratos");
			contrato = (UISelectEntity)this.attrs.get("contrato");
      int index= contratos.indexOf(contrato);
      if(index>= 0)
        this.attrs.put("contrato", contratos.get(index));
       this.toLoadEspecialidades();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
}