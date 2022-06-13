package mx.org.kaana.keet.catalogos.contratos.contratistas.backing;

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
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.catalogos.contratos.contratistas.reglas.Transaccion;
import mx.org.kaana.keet.comun.Catalogos;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosContratosContratistasDepuracion")
@ViewScoped
public class Depuracion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;				
	
  protected List<Entity> lotes;
  protected List<Entity> seleccionados;

  public List<Entity> getLotes() {
    return lotes;
  }
 
  public List<Entity> getSeleccionados() {
    return seleccionados;
  }

  public void setSeleccionados(List<Entity> seleccionados) {
    this.seleccionados = seleccionados;
  }
 
  @PostConstruct
  @Override
  protected void init() {				
    try {						
			this.attrs.put("idContrato", JsfBase.getFlashAttribute("idContrato"));
			this.attrs.put("idContratoLote", JsfBase.getFlashAttribute("idContratoLote"));
			this.attrs.put("contrato", JsfBase.getFlashAttribute("contrato"));
			this.attrs.put("documento", JsfBase.getFlashAttribute("documento"));
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			this.toLoadEspecialidades();
			this.doLoadContratistas();
      this.seleccionados= new ArrayList<>();
    } // try 
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
  
  public void doLoad() {    				
    UISelectEntity figura     = null;
    String idXml              = null;
    Map<String, Object> params= new HashMap<>();
		List<UISelectEntity> contratistas= null;		
    try {
      contratistas= (List<UISelectEntity>)this.attrs.get("contratistas");
      if(contratistas!= null && !contratistas.isEmpty()) {
        int index= contratistas.indexOf((UISelectEntity)this.attrs.get("idContratista"));
        if(index>= 0)
          this.attrs.put("idContratista", contratistas.get(index));
      } // if
      figura= (UISelectEntity)this.attrs.get("idContratista");
      idXml = figura.toLong("tipo").equals(1L)? "lotesContratistas": "lotesSubContratistas";
      params.put("idDesarrollo", ((Entity)this.attrs.get("documento")).toLong("idDesarrollo"));
      params.put("idFigura", figura.getKey()> 0? figura.getKey().toString().substring(4): figura.getKey());
      params.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato= "+ this.attrs.get("idContrato"));
      this.lotes= DaoFactory.getInstance().toEntitySet("VistaCapturaDestajosDto", idXml, params);		
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(params);
    }// finally    
  } // doLoad							
  
	public void toLoadEspecialidades() throws Exception {
		Map<String, Object> params= null;		
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);			
      List<UISelectItem> departamentos= (List<UISelectItem>)UISelect.build("TcKeetDepartamentosDto", "especialidades", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
			this.attrs.put("departamentos", departamentos);
			this.attrs.put("idDepartamentos", UIBackingUtilities.toFirstKeySelectItem(departamentos));
		} // try
		finally {
			Methods.clean(params);
		} // finally		
	} // toLoadEspecialidades
  
  public void doLoadContratistas() {    						
		List<Columna> columns       = null;
    Map<String, Object> params  = null;
		List<UISelectEntity> contratistas= null;		
    try {
			columns= new ArrayList<>();      
			params = this.toPrepare();									
      columns.add(new Columna("nombreCompleto", EFormatoDinamicos.MAYUSCULAS));									
      contratistas= (List<UISelectEntity>) UIEntity.seleccione("VistaContratosDto", "contratistasSubAsociados", params, columns, Constantes.SQL_TODOS_REGISTROS, "departamento");
      this.attrs.put("contratistas", contratistas);
			this.attrs.put("idContratista", UIBackingUtilities.toFirstKeySelectEntity(contratistas));
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally    
  } // doLoadContratistas							
	
	private Map<String, Object> toPrepare() {
    Map<String, Object> regresar= new HashMap<>();
    try {
      StringBuilder sb= new StringBuilder();		
      if(this.attrs.get("idDepartamento")!= null && !Cadena.isVacio(this.attrs.get("idDepartamento")) && Long.valueOf(this.attrs.get("idDepartamento").toString())>= 1L)
        sb.append("tc_keet_departamentos.id_departamento=").append(this.attrs.get("idDepartamento")).append(" and ");		
      if(this.attrs.get("idContratista")!= null && !Cadena.isVacio(this.attrs.get("idContratista")) && Long.valueOf(this.attrs.get("idContratista").toString())>= 1L) {
        sb.append("tc_keet_departamentos.id_departamento=").append(this.attrs.get("idContratista")).append(" and ");		
      } // if  
      regresar.put(Constantes.SQL_CONDICION, Cadena.isVacio(sb)? Constantes.SQL_VERDADERO: sb.substring(0, sb.length()-4));
			regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());						
		} // try
	  catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
		return regresar;
	} // toPrepare
	
	public String doAceptar() {
    String regresar           = null;
		Transaccion transaccion   = null;		
    UISelectEntity figura     = null;
    String[] eliminar         = new String[this.seleccionados.size()];
		List<UISelectEntity> contratistas= null;		
    try {
      if(this.seleccionados.size()> 0) {
        int count= 0;
        for (Entity item: this.seleccionados) {
          eliminar[count]= String.valueOf(item.toString("idContratoLote"));
          count++;
        } // for
        contratistas= (List<UISelectEntity>)this.attrs.get("contratistas");
        if(contratistas!= null && !contratistas.isEmpty()) {
          int index= contratistas.indexOf((UISelectEntity)this.attrs.get("idContratista"));
          if(index>= 0)
            this.attrs.put("idContratista", contratistas.get(index));
        } // if
        figura= (UISelectEntity)this.attrs.get("idContratista");
        transaccion= new Transaccion(Long.valueOf(figura.getKey().toString().substring(4)), eliminar, figura.toLong("tipo"));
        if(transaccion.ejecutar(EAccion.ELIMINAR)) {
          // regresar= this.doCancelar();			
          this.doLoad();
          JsfBase.addMessage("Eliminar de contratista a lotes", "La eliminación se realizó de forma correcta", ETipoMensaje.INFORMACION);
        } // if
        else
          JsfBase.addMessage("Eliminar de contratista a lotes", "Ocurrió un error al realizar la eliminación", ETipoMensaje.ERROR);
      } // if
      else
        JsfBase.addMessage("Informativo", "Se tiene que seleccionar al menos un lote", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doAceptar		
	
	public String doCancelar() {
    String regresar= null;    		
    try {			
			JsfBase.setFlashAttribute("idContrato", this.attrs.get("idContrato"));			
			JsfBase.setFlashAttribute("idContratoLote", this.attrs.get("idContratoLote"));			
			JsfBase.setFlashAttribute("contrato", this.attrs.get("contrato"));			
			JsfBase.setFlashAttribute("documento", this.attrs.get("documento"));			
			regresar= "lotes".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		

  @Override
  protected void finalize() throws Throwable {
    super.finalize(); 
    Methods.clean(this.seleccionados);
  }
 
  
}