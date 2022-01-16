package mx.org.kaana.keet.catalogos.contratos.destajos.backing;

import com.google.common.base.Objects;
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
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.Concepto;
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.Criterio;
import mx.org.kaana.keet.catalogos.contratos.destajos.beans.Lote;
import mx.org.kaana.keet.catalogos.contratos.destajos.comun.IBaseReporteDestajos;
import mx.org.kaana.keet.catalogos.desarrollos.beans.RegistroDesarrollo;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value = "keetCatalogosContratosDestajosSeguimiento")
@ViewScoped
public class Seguimiento extends IBaseReporteDestajos implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;			
  private static final Log LOG = LogFactory.getLog(Seguimiento.class);
  
	private RegistroDesarrollo registroDesarrollo;		
  private List<Concepto> model;
  private List<Lote> fields;
	
	public RegistroDesarrollo getRegistroDesarrollo() {
		return registroDesarrollo;
	}

	public void setRegistroDesarrollo(RegistroDesarrollo registroDesarrollo) {
		this.registroDesarrollo = registroDesarrollo;
	}			

  public List<Concepto> getModel() {
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
      this.model  = new ArrayList<>();
			this.initBase();
      this.fields = new ArrayList<>();
			opcion      = (EOpcionesResidente)JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long)JsfBase.getFlashAttribute("idDesarrollo");			
			//opcion      = EOpcionesResidente.SEGUIMIENTO;
			//idDesarrollo= 1L;			
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

  public String toTokenClave() {
    StringBuilder regresar = new StringBuilder();
		UISelectEntity contrato= (UISelectEntity)this.attrs.get("contrato");
    if(contrato!= null && contrato.getKey()> 0L) {
      List<UISelectEntity> contratos= (List<UISelectEntity>)this.attrs.get("contratos");
      if(contratos!= null) {
        contrato= contratos.get(contratos.indexOf(contrato));
		    regresar.append(Cadena.rellenar(String.valueOf(contrato.toLong("idEmpresa")), 3, '0', true)).append(contrato.toLong("ejercicio")).append(Cadena.rellenar(String.valueOf(contrato.getKey()), 3, '0', true));
      } // if
      else
        regresar.append(Cadena.rellenar("", 25, '9', true));
    } // else  
    else
      regresar.append(Cadena.rellenar("", 25, '9', true));
    return regresar.toString();
  }

  @Override
  public void doLoad() {
    List<Columna> columns    = null;
		Map<String, Object>params= new HashMap<>();
    List<Entity> lotes       = null;
    List<Entity> codigos     = null;
    String anterior          = "";
    try {
      this.model.clear();
      this.attrs.put("detalle", Boolean.FALSE);
      params.put("clave", this.toTokenClave());
      codigos= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaSeguimientoDto", "codigos", params);
      if(codigos!= null && !codigos.isEmpty()) {
        int count= 0;
        for (Entity codigo: codigos) {
          Concepto concepto= new Concepto(count++, codigo.toString("codigo"), codigo.toString("nombre"));
          this.model.add(concepto);
        } // for
      } // if
      this.fields.clear();
      lotes= (List<Entity>)DaoFactory.getInstance().toEntitySet("VistaSeguimientoDto", "lotes", params, Constantes.SQL_TODOS_REGISTROS);
      if(lotes!= null && !lotes.isEmpty()) {
        for (Entity item: lotes) {
          String lote= item.toString("lote").replaceAll("-", "");
          int index= this.model.indexOf(new Concepto(item.toString("codigo")));
          if(index>= 0) {
            Concepto concepto= this.model.get(index);
            concepto.put(lote, new Criterio(lote, item.toDate("inicio"), item.toDate("termino"), item.toDate("entrega"), item.toLong("idEstacionEstatus"), item.toString("estatus")));
            if(!Objects.equal(lote, anterior)) {
              this.fields.add(new Lote(lote, lote, "", "janal-column-center MarAuto Responsive janal-wid-5"));
              anterior= lote;
            } // if  
          } // if
          else
            throw new RuntimeException("El concepto ["+ item.toString("codigo")+ "] no existe en la consulta!");
        } // for
      } // if
      else   
        this.model= new ArrayList<>();
      UIBackingUtilities.resetDataTable("tabla");
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
	
	@Override
  public void doReporte(String tipo, boolean sendMail) throws Exception {    
  } // doReporte 	

	public String doColorNomina(Entity row) {
		return !row.toString("porcentaje").equals("100.00")? "janal-tr-error": Cadena.isVacio(row.toLong("idNomina"))? "": "janal-tr-diferencias";
	}
  
  public void doDetalle(Concepto row) {
    List<Columna> columns    = null;
		Map<String, Object>params= new HashMap<>();
    try {
      params.put("sortOrder", "order by tt_keet_temporal.id_persona, tt_keet_temporal.clave, tt_keet_temporal.lote");
      params.put("loNuevoPersona", "or tc_keet_estaciones.codigo= '".concat(row.getCodigo()).concat("'"));
      params.put("loNuevoProveedor", "or tc_keet_estaciones.codigo= '".concat(row.getCodigo()).concat("'"));
      params.put("idNomina", -1L);
      params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
      params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
      columns= new ArrayList<>();
      columns.add(new Columna("porcentaje", EFormatoDinamicos.MILES_SAT_DECIMALES));
      columns.add(new Columna("costo", EFormatoDinamicos.MILES_CON_DECIMALES));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));
      this.lazyModel= new FormatCustomLazy("VistaNominaConsultasDto", "destajoResumen", params, columns);
      UIBackingUtilities.resetDataTable("detalle");
      this.attrs.put("detalle", Boolean.TRUE);
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
 
	public String doColor(Concepto row) {
		return ((String)row.get("codigo")).startsWith("#")? "janal-tr-diferencias": "";
	}
  
}