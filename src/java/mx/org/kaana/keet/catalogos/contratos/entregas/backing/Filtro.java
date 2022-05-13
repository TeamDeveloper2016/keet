package mx.org.kaana.keet.catalogos.contratos.entregas.backing;

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
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.contratos.destajos.comun.IBaseReporteDestajos;
import mx.org.kaana.keet.catalogos.desarrollos.beans.RegistroDesarrollo;
import mx.org.kaana.keet.comun.gps.Point;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value = "keetCatalogosContratosEntregasFiltro")
@ViewScoped
public class Filtro extends IBaseReporteDestajos implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;			
  private static final Log LOG = LogFactory.getLog(Filtro.class);
  
	private RegistroDesarrollo registroDesarrollo;		
	private List<Entity> lotes;
	
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
      this.attrs.put("contrato", JsfBase.getFlashAttribute("contrato"));
			this.attrs.put("manzana", JsfBase.getFlashAttribute("manzana"));	
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
      if(Cadena.isVacio(this.attrs.get("manzana"))) 
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
      this.toLoadManzanas();
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
		Map<String, Object>params = null;
		List<UISelectEntity> casas= null;
    try {   
      params  = new HashMap<>();
      params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
      params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
      casas= UIEntity.seleccione("VistaCapturaDestajosDto", "lotesDisponibles", params, Constantes.SQL_TODOS_REGISTROS, "descripcionLote");
      this.attrs.put("casas", casas);
      this.attrs.put("casa", UIBackingUtilities.toFirstKeySelectEntity(casas));
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
		List<UISelectEntity> casas  = null;
    try {   
      params  = new HashMap<>();
      params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
      params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
      columns= new ArrayList<>();      
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));                  
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_CORTA));                  
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_CORTA));    
      columns.add(new Columna("entrega", EFormatoDinamicos.FECHA_CORTA));    
      columns.add(new Columna("recibio", EFormatoDinamicos.MAYUSCULAS));    
      this.lotes= DaoFactory.getInstance().toEntitySet("VistaCapturaDestajosDto", "lotesDisponibles", params);		
      casas= UIEntity.seleccione("VistaCapturaDestajosDto", "lotesDisponibles", params, Constantes.SQL_TODOS_REGISTROS, "descripcionLote");
      this.attrs.put("lotes", casas);
      if(!this.lotes.isEmpty()) { 
        UIBackingUtilities.toFormatEntitySet(this.lotes, columns);	
        this.toEstatusManzanaLote();
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
  } // doLoad	
	
	private void toEstatusManzanaLote() throws Exception {
		try {
			for(Entity item: this.lotes) {
        if(Objects.equals(1L, item.toLong("entregada")))
          item.put("iconEstatus", new Value("iconEstatus", EEstacionesEstatus.TERMINADO.getSemaforo()));
        else 
          item.put("iconEstatus", new Value("iconEstatus", EEstacionesEstatus.INICIAR.getSemaforo()));
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // toEstatusManzanaLote
	
	public String doEvidencia() {
    String regresar             = null;    		
		Entity seleccionado         = null;
    try {			
			seleccionado= (Entity) this.attrs.get("seleccionado");			
			JsfBase.setFlashAttribute("opcionResidente", (EOpcionesResidente)this.attrs.get("opcionResidente"));												
			if(this.attrs.get("opcionAdicional")!= null)
				JsfBase.setFlashAttribute("opcionAdicional", (EOpcionesResidente)this.attrs.get("opcionAdicional"));												
			JsfBase.setFlashAttribute("idEmpresa", seleccionado.toLong("idEmpresa"));
			JsfBase.setFlashAttribute("idContratoLote", seleccionado.toLong("idContratoLote"));
			JsfBase.setFlashAttribute("seleccionado", seleccionado);												
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));				
			JsfBase.setFlashAttribute("georreferencia", new Point(Numero.getDouble(seleccionado.toString("latitud"), 21.890563), Numero.getDouble(seleccionado.toString("longitud"), -102.252030)));				
			JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Catalogos/Contratos/Entregas/filtro");			
			JsfBase.setFlashAttribute("contrato", this.attrs.get("contrato"));	
			JsfBase.setFlashAttribute("manzana", this.attrs.get("manzana"));	
			regresar= "importar".concat(Constantes.REDIRECIONAR);										
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doEvidencia
	
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
	
	@Override
  public void doReporte(String tipo, boolean sendMail) throws Exception {    
    
  } // doReporte 	

}