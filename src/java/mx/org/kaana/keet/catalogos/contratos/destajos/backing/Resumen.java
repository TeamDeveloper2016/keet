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
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.keet.catalogos.contratos.destajos.comun.IBaseReporteDestajos;
import mx.org.kaana.keet.catalogos.desarrollos.beans.RegistroDesarrollo;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.keet.nomina.beans.Nomina;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value = "keetCatalogosContratosDestajosResumen")
@ViewScoped
public class Resumen extends IBaseReporteDestajos implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;			
  private static final Log LOG = LogFactory.getLog(Resumen.class);
  
	private RegistroDesarrollo registroDesarrollo;		
	private Nomina ultima;  
	private FormatLazyModel lazyResumen;
  private String costoResumen;
	
	public RegistroDesarrollo getRegistroDesarrollo() {
		return registroDesarrollo;
	}

	public void setRegistroDesarrollo(RegistroDesarrollo registroDesarrollo) {
		this.registroDesarrollo = registroDesarrollo;
	}			

  public FormatLazyModel getLazyResumen() {
    return lazyResumen;
  }

  public String getCostoResumen() {
    return costoResumen;
  }

  @PostConstruct
  @Override
  protected void init() {		
    EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;
    try {
			this.initBase();
      this.costoResumen= "$ 0.00";
			opcion      = (EOpcionesResidente)JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long)JsfBase.getFlashAttribute("idDesarrollo");			
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("opcionAdicional", JsfBase.getFlashAttribute("opcionAdicional"));
			this.attrs.put("idDesarrollo", idDesarrollo);
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());				
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
			contratos= UIEntity.todos("VistaTableroDto", "contratos", params, "clave");
			this.attrs.put("contratos", contratos);
			this.attrs.put("contrato", UIBackingUtilities.toFirstKeySelectEntity(contratos));
      this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
    finally {
      Methods.clean(params);
    } // finally
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
    if(contrato!= null && contrato.getKey()> 0L)
		  regresar.append("tc_keet_contratos.id_contrato= ").append(contrato.getKey()).append(" and ");
    return regresar.length()> 0? regresar.substring(0, regresar.length()- 4): Constantes.SQL_VERDADERO;
  }

  @Override
  public void doLoad() {
    List<Columna> columns    = null;
		Map<String, Object>params= new HashMap<>();
    try {
      params.put("sortOrder", "order by tt_keet_temporal.id_persona, tt_keet_temporal.clave, tt_keet_temporal.lote");
      params.put("loNuevoPersona", this.ultima.getIdCompleta()== 0L? "or tc_keet_contratos_destajos_contratistas.id_nomina is null": "");
      params.put("loNuevoProveedor", this.ultima.getIdCompleta()== 0L? "or tc_keet_contratos_destajos_proveedores.id_nomina is null": "");
      params.put("idNomina", this.ultima.getIdNominaEstatus()== 5L? -1: this.ultima.getIdNomina());
      params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
      params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
      columns= new ArrayList<>();
      columns.add(new Columna("porcentaje", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      this.lazyResumen= new FormatCustomLazy("VistaNominaConsultasDto", "destajoResumen", params, columns);
      Entity costo= (Entity)DaoFactory.getInstance().toEntity("VistaNominaConsultasDto", "costoResumen", params);
      if(costo!= null && !costo.isEmpty())
        this.costoResumen= Global.format(EFormatoDinamicos.MONEDA_CON_DECIMALES, costo.toDouble("total"));
      else
        this.costoResumen= "$ 0.00";
      UIBackingUtilities.resetDataTable("resumen");
      this.attrs.put("resumen", true);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally	    
  } // doLoad	
	
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
      this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);      
    } // catch	
  }
  
	@Override
  public void doReporte(String tipo, boolean sendMail) throws Exception {    
  } // doReporte 	

	public String doColorNomina(Entity row) {
		return !row.toString("porcentaje").equals("100.00")? "janal-tr-error": Cadena.isVacio(row.toLong("idNomina"))? "janal-tr-diferencias": "";
	}
 
}