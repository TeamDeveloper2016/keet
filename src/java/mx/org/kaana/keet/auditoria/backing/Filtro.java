package mx.org.kaana.keet.auditoria.backing;

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
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.keet.nomina.beans.Nomina;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
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
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

@Named(value = "keetAuditoriaFiltro")
@ViewScoped
public class Filtro extends IBaseReporteDestajos implements Serializable {

  private static final long serialVersionUID= 8793667741599428872L;			
  private static final Log LOG = LogFactory.getLog(Filtro.class);
	protected static final String COORDENADA_CENTRAL= "21.8818,-102.291";
  
	private RegistroDesarrollo registroDesarrollo;		
	private List<Entity> lotes;
	private FormatLazyModel lazyDestajo;
	private Nomina ultima;  
	private MapModel model;
  private Entity contrato;
	
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

	public MapModel getModel() {
		return model;
	}
  
  @PostConstruct
  @Override
  protected void init() {		
    EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;
    try {
			this.initBase();
			opcion      = (EOpcionesResidente)JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long)JsfBase.getFlashAttribute("idDesarrollo");			
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("opcionAdicional", JsfBase.getFlashAttribute("opcionAdicional"));
			this.attrs.put("idDesarrollo", idDesarrollo);
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());				
      this.attrs.put("destajos", false);				
      this.attrs.put("first", Boolean.FALSE);
			this.toLoadCatalogos();			
    } // try 
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
      this.doLoadCasas();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
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
      this.attrs.put("manzana", new UISelectEntity(-1L));
      this.toLoadManzanas();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
  public void doLoadCasas() {
		Map<String, Object>params = null;
		List<UISelectEntity> casas= null;
    try {   
      this.attrs.put("casa", new UISelectEntity(-1L));
      params  = new HashMap<>();
      params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
      params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
      casas= UIEntity.seleccione("VistaGeoreferenciaLotesDto", "lotesTodos", params, Constantes.SQL_TODOS_REGISTROS, "codigo");
      this.attrs.put("casas", casas);
      if(casas!= null && casas.size()> 0)
        this.attrs.put("casa", UIBackingUtilities.toFirstKeySelectEntity(casas));
      else {
        this.attrs.put("casas", new ArrayList<>());
        this.attrs.put("casa", new UISelectEntity(-1L));
      } // else
      if((Boolean)this.attrs.get("first"))
        this.doLoad();
      this.attrs.put("first", Boolean.TRUE);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {      
      Methods.clean(params);
    } // finally		
  } // doLoadCasas	
  
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
  
  @Override
  public void doLoad() {
		Map<String, Object>params   = null;
    List<Columna> columns       = null;		
		List<UISelectEntity> casas  = null;
    try {   
      params = new HashMap<>();
      params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
      params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
      columns= new ArrayList<>();      
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));                  
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));    
      this.lotes= DaoFactory.getInstance().toEntitySet("VistaGeoreferenciaLotesDto", "lotesTodos", params);		
      casas= UIEntity.seleccione("VistaGeoreferenciaLotesDto", "lotesTodos", params, Constantes.SQL_TODOS_REGISTROS, "codigo");
      this.attrs.put("lotes", casas);
      if(!this.lotes.isEmpty()) { 
        UIBackingUtilities.toFormatEntitySet(this.lotes, columns);	
        this.toEstatusManzanaLote();
      } // if  
      else {
        this.lotes= new ArrayList<>();		
        this.attrs.put("lotes", new ArrayList<>());
      } // else
			this.attrs.put("destajos", false);
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
	
	private void toEstatusManzanaLote() throws Exception {
		Map<String, Object>params= null;
		Entity estatus           = null;
		String color             = "rojo";
    Integer porcentaje       = 0;
		try {
			params= new HashMap<>();
			for(Entity mzaLote: this.lotes) {
		    color     = "rojo";
        porcentaje= 0;
				params.clear();
        String clave= Cadena.rellenar(mzaLote.toLong("idEmpresa").toString(), 3, '0', true)+ Cadena.rellenar(mzaLote.toLong("ejercicio").toString(), 4, '0', true)+ Cadena.rellenar(mzaLote.toLong("ordenContrato").toString(), 3, '0', true)+ Cadena.rellenar(mzaLote.toLong("orden").toString(), 3, '0', true);
				params.put("clave", clave);
				estatus= (Entity) DaoFactory.getInstance().toEntity("VistaCapturaDestajosDto", "estatusLote", params);
				if(estatus.toString("total")!= null) {
					if(estatus.toLong("total").equals(estatus.toLong("terminado")))
						mzaLote.put("iconEstatus", new Value("iconEstatus", EEstacionesEstatus.TERMINADO.getSemaforo()));
					else if(estatus.toLong("total").equals(estatus.toLong("iniciado")))
						mzaLote.put("iconEstatus", new Value("iconEstatus", EEstacionesEstatus.INICIAR.getSemaforo()));
					else
						mzaLote.put("iconEstatus", new Value("iconEstatus", EEstacionesEstatus.EN_PROCESO.getSemaforo()));
          porcentaje= new Integer(String.valueOf((estatus.toLong("terminado") * 100)/estatus.toLong("total")));
          /* AQUI COLOCAR LOS COLORES BASADOS EN EL PORCENTAJE DE AVANCE */
          this.attrs.put("porcentaje", porcentaje);
          if(porcentaje== 0)
            color= "rojo";
          else  
            if(porcentaje> 0 && porcentaje<= 20)
              color= "magenta";
            else
              if(porcentaje>= 21 && porcentaje<= 80)
                color= "naranja";
              else
                if(porcentaje>= 81 && porcentaje<= 99)
                  color= "amarillo";
                else
                  color= "verde"; 
				} // if
				else
					mzaLote.put("iconEstatus", new Value("iconEstatus", ""));
        mzaLote.put("color", new Value("color", color));
        mzaLote.put("porcentaje", new Value("porcentaje", porcentaje));
			} // for
		} // try
		finally {
			Methods.clean(params);
		} // finally		
	} // toEstatusManzanaLote
	
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
	
	public void doDestajos(Entity seleccionado) {
    List<Columna> columns    = null;
		Map<String, Object>params= new HashMap<>();
    try {
      this.attrs.put("seleccionado", seleccionado);
      this.contrato= seleccionado;
      String clave= Cadena.rellenar(seleccionado.toLong("idEmpresa").toString(), 3, '0', true)+ Cadena.rellenar(seleccionado.toLong("ejercicio").toString(), 4, '0', true)+ Cadena.rellenar(seleccionado.toLong("ordenContrato").toString(), 3, '0', true)+ Cadena.rellenar(seleccionado.toLong("orden").toString(), 3, '0', true);
      params.put("clave", clave);      
      params.put("lote", seleccionado.toString("codigo"));      
      columns= new ArrayList<>();
      columns.add(new Columna("costo", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("cargo", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("porcentaje", EFormatoDinamicos.MILES_SAT_DECIMALES));
      this.lazyDestajo= new FormatCustomLazy("VistaTableroDto", "detalleLotes", params, columns);
      UIBackingUtilities.resetDataTable("tabla");
      this.attrs.put("destajos", true);
      this.toLoadCoordenadas();
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
        parametros.put("REPORTE_ICON", JsfBase.getRealPath("/resources/janal/img/sistema/"));
        parametros.put("REPORTE_EMPRESA_LOGO", this.toLookForEmpresaLogo(JsfBase.getAutentifica().getEmpresa().getIdEmpresa()));
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

	public String doColor(Entity row) {
		return Objects.equals(row.toLong("nivel"), 5L)? "janal-tr-diferencias": "";
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
 
  private void toLoadCoordenadas() {
		List<Entity> items        = null;
		Marker marker             = null;
		String icon               = null;
		Map<String, Object> params= null;
		List<Columna> columns     = null;
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));
			this.model= new DefaultMapModel();
			this.attrs.put("coordenadaCentral", COORDENADA_CENTRAL);
			params= new HashMap<>();
      if(this.contrato!= null && !this.contrato.isEmpty()) {
        params.put("desarrollo", this.contrato.toString("desarrollo"));
        params.put("contrato", this.contrato.toString("nombreContrato"));
        params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
        items= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaTableroDto", "georreferencia", params, Constantes.SQL_TODOS_REGISTROS);
        if(!items.isEmpty()) {
          UIBackingUtilities.toFormatEntitySet(items, columns);
          this.attrs.put("coordenadaCentral", null);
          for(Entity lote: items) {
            icon  = this.toIcon(lote);
            marker= new Marker(new LatLng(Double.valueOf(lote.toString("latitud")), Double.valueOf(lote.toString("longitud"))), "Contrato: ".concat(lote.toString("nombre")).concat(", avance: ").concat(String.valueOf(lote.toInteger("porcentaje"))).concat("%, lote: ").concat(lote.toString("codigo")), lote, icon);
            this.model.addOverlay(marker);
          } // for
          if(Cadena.isVacio(this.attrs.get("coordenadaCentral")))
            this.attrs.put("coordenadaCentral", lotes.get(0).toString("latitud").concat(",").concat(lotes.get(0).toString("longitud")));
        } // if
      } // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally
  } // toLoadCoordenadas
  
  private String toIcon(Entity mzaLote) throws Exception {
		String regresar          = null;
		String imagen            = null;
		String color             = "red";
		Map<String, Object>params= null;
		Entity estatus           = null;
    Integer porcentaje       = 0;
		try {
			imagen= JsfBase.getContext().concat("/javax.faces.resource/icon/mapa/").concat("janal-{color}-{orden}.png").concat(".jsf?ln=janal");
			params= new HashMap<>();			
      String clave= Cadena.rellenar(mzaLote.toLong("idEmpresa").toString(), 3, '0', true)+ Cadena.rellenar(mzaLote.toLong("ejercicio").toString(), 4, '0', true)+ Cadena.rellenar(mzaLote.toLong("ordenContrato").toString(), 3, '0', true)+ Cadena.rellenar(mzaLote.toLong("orden").toString(), 3, '0', true);
			params.put("clave", clave);
			estatus= (Entity) DaoFactory.getInstance().toEntity("VistaGeoreferenciaLotesDto", "estatusManzanaLote", params);
			if(estatus.toString("total")!= null) {
        porcentaje= new Integer(String.valueOf((estatus.toLong("terminado") * 100)/estatus.toLong("total")));
        /* AQUI COLOCAR LOS COLORES BASADOS EN EL PORCENTAJE DE AVANCE */
				this.attrs.put("porcentaje", porcentaje);
        if(Objects.equals(this.contrato.toString("codigo"), mzaLote.toString("codigo"))) { 
  	  		color= "blue";
          this.attrs.put("coordenadaCentral", mzaLote.toString("latitud").concat(",").concat(mzaLote.toString("longitud")));          
        } // else  
        else
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

	public void onMarkerSelect(OverlaySelectEvent event) {
		Marker marker= null;	
		try {
			marker= (Marker) event.getOverlay();
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		                    
  } // onMarkerSelect
  
}