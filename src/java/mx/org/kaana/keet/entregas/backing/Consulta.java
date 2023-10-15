package mx.org.kaana.keet.entregas.backing;

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
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.contratos.destajos.comun.IBaseReporteDestajos;
import mx.org.kaana.keet.catalogos.desarrollos.beans.RegistroDesarrollo;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value = "keetEntregasConsulta")
@ViewScoped
public class Consulta extends IBaseReporteDestajos implements Serializable {

  private static final long serialVersionUID= 1193667741599428879L;			
  private static final Log LOG = LogFactory.getLog(Consulta.class);
  
	private List<Entity> lotes;
	
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
			opcion      = (EOpcionesResidente)JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long)JsfBase.getFlashAttribute("idDesarrollo");			
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("idDesarrollo", idDesarrollo);
      this.attrs.put("idContrato", JsfBase.getFlashAttribute("idContrato"));				
      this.attrs.put("idCasa", JsfBase.getFlashAttribute("idCasa"));				
      this.attrs.put("idProceso", JsfBase.getFlashAttribute("idProceso"));				
      this.attrs.put("idSubProceso", JsfBase.getFlashAttribute("idSubProceso"));				
      this.attrs.put("idFirstContrato", Objects.equals(this.attrs.get("idContrato"), null));
      this.attrs.put("idFirstCasa", Objects.equals(this.attrs.get("idCasa"), null));
      this.attrs.put("idFirstProceso", Objects.equals(this.attrs.get("idProceso"), null));
      this.attrs.put("idFirstSubProceso", Objects.equals(this.attrs.get("idSubProceso"), null));
      this.attrs.put("idFirstTime", Boolean.TRUE);
			this.toLoadCatalogos();			
      this.attrs.put("idFirstTime", Boolean.FALSE);
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
			RegistroDesarrollo desarrollo= new RegistroDesarrollo((Long)this.attrs.get("idDesarrollo"));      
      if(!Objects.equals(desarrollo, null))
        this.attrs.put("desarrollo", desarrollo.getDesarrollo().getClave().concat(" ").concat(Constantes.SEPARADOR).concat(" ").concat(desarrollo.getDesarrollo().getNombres()));
      this.toLoadContratos();
      this.toLoadProcesos();
		} // try
    finally {
			Methods.clean(params);
			Methods.clean(columns);
		} // finally
	} 
	
	private void toLoadContratos() {
    Map<String, Object> params    = new HashMap<>();
		List<UISelectEntity> contratos= null;
    try {      
      params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
			contratos= UIEntity.seleccione("VistaTableroDto", "contratos", params, "clave");
			this.attrs.put("contratos", contratos);
      if((Boolean)this.attrs.get("idFirstContrato") || Objects.equals(contratos.size(), 1))
			  this.attrs.put("idContrato", UIBackingUtilities.toFirstKeySelectEntity(contratos));
      else
        this.attrs.put("idFirstContrato", Boolean.TRUE);
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
  
  public void doLoadCasas() {
		Map<String, Object>params = new HashMap<>();
		List<UISelectEntity> casas= null;
    try {   
      params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      params.put("idContrato", ((UISelectEntity)this.attrs.get("idContrato")).getKey());
      casas= UIEntity.seleccione("VistaContratosLotesDto", "lotes", params, Constantes.SQL_TODOS_REGISTROS, "codigo");
      this.attrs.put("casas", casas);
      if((Boolean)this.attrs.get("idFirstCasa") || Objects.equals(casas.size(), 1))
        this.attrs.put("idCasa", UIBackingUtilities.toFirstKeySelectEntity(casas));
      else
        this.attrs.put("idFirstCasa", Boolean.TRUE);
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

  public String toLoadCondicion() {
    StringBuilder regresar   = new StringBuilder();
		UISelectEntity idContrato= (UISelectEntity)this.attrs.get("idContrato");
		UISelectEntity idCasa    = (UISelectEntity)this.attrs.get("idCasa");
		Long idEstatus           = (Long)this.attrs.get("idEstatus");
    if(idContrato!= null && idContrato.getKey()> 0L)
		  regresar.append("(tc_keet_contratos.id_contrato= ").append(idContrato.getKey()).append(") and ");
    if(idCasa!= null && idCasa.getKey()> 0L)
      regresar.append("(tc_keet_contratos_lotes.id_contrato_lote=").append(idCasa.getKey()).append(") and ");
    if(idEstatus!= null)
      switch(idEstatus.intValue()) {
        case -1:
          break;
        case 1:
          regresar.append("(tt_keet_entregas.id_completo= 2) and ");
          break;
        case 2:
          regresar.append("(tt_keet_entregas.id_completo= 1) and ");
          break;
        case 3:
          regresar.append("(tt_keet_entregas.id_entrega is null) and ");
          break;
      } // switch
    regresar.append("(tc_keet_proyectos.id_desarrollo=").append(this.attrs.get("idDesarrollo")).append(") and ");
    return regresar.length()> 0? regresar.substring(0, regresar.length()- 4): Constantes.SQL_VERDADERO;
  }
  
  @Override
  public void doLoad() {
    List<Columna> columns    = new ArrayList<>();		
		Map<String, Object>params= new HashMap<>();
    try {   
   		Long idProceso   = (Long)this.attrs.get("idProceso");
	  	Long idSubProceso= (Long)this.attrs.get("idSubProceso");
      if(!Objects.equals(idProceso, null) && !Objects.equals(idProceso, -1L) && !Objects.equals(idSubProceso, null) && !Objects.equals(idSubProceso, -1L)) {
        params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
        params.put("idProceso", this.attrs.get("idProceso"));
        params.put("idSubProceso", this.attrs.get("idSubProceso"));
        params.put(Constantes.SQL_CONDICION, this.toLoadCondicion());
        columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));                  
        columns.add(new Columna("fecha", EFormatoDinamicos.FECHA_CORTA));                  
        this.lotes= DaoFactory.getInstance().toEntitySet("VistaProcesosDto", "seguimiento", params);		
        if(!this.lotes.isEmpty()) 
          UIBackingUtilities.toFormatEntitySet(this.lotes, columns);	
      } // else
      else
        JsfBase.addMessage("Se tiene que seleccionar primero un paquete !", ETipoMensaje.ERROR);      		
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {      
      Methods.clean(columns);
      Methods.clean(params);
    } // finally		
  } 
	
	public String doConceptos(String page) {
    String regresar    = null;    		
		Entity seleccionado= null;
    try {			
      seleccionado= (Entity) this.attrs.get("seleccionado");			
      JsfBase.setFlashAttribute("opcionResidente", (EOpcionesResidente)this.attrs.get("opcionResidente"));												
      JsfBase.setFlashAttribute("seleccionado", seleccionado);												
      JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));
      JsfBase.setFlashAttribute("idContrato", this.attrs.get("idContrato"));
      JsfBase.setFlashAttribute("idCasa", this.attrs.get("idCasa"));
      JsfBase.setFlashAttribute("idProceso", this.attrs.get("idProceso"));
      JsfBase.setFlashAttribute("idSubProceso", this.attrs.get("idSubProceso"));
      JsfBase.setFlashAttribute("retorno", "/Paginas/Keet/Entregas/consulta");
      regresar= page.concat(Constantes.REDIRECIONAR);										
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } 
	
	public String doCancelar() {
    String regresar          = null;    
		EOpcionesResidente opcion= null;		
    try {			
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("opcion", opcion);			
			JsfBase.setFlashAttribute("opcionResidente", opcion);						
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));			
			JsfBase.setFlashAttribute("idDesarrolloProcess", this.attrs.get("idDesarrollo"));
  		regresar= opcion.getRetorno().concat(Constantes.REDIRECIONAR_AMPERSON);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } 

  @Override
  public void doReporte(String nombre, boolean sendMail) throws Exception {
    throw new UnsupportedOperationException("Not supported yet."); 
  }
 
  private void toLoadProcesos() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
      List<UISelectItem> procesos= UISelect.seleccione("VistaProcesosDto", "desarrollos", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("procesos", procesos);
      if(procesos!= null && !procesos.isEmpty()) 
        if((Boolean)this.attrs.get("idFirstProceso") || Objects.equals(procesos.size(), 1))
          this.attrs.put("idProceso", procesos.get(0).getValue());
        else
          this.attrs.put("idFirstProceso", Boolean.TRUE);
      else
        this.attrs.put("idProceso", -1L);
      this.doLoadSubprocesos();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  }  
  
  public void doLoadSubprocesos() {
    Map<String, Object> params= new HashMap<>();
    try {
			params.put(Constantes.SQL_CONDICION, "tc_keet_sub_procesos.id_proceso= "+ this.attrs.get("idProceso"));
      List<UISelectItem> subProcesos= UISelect.seleccione("TcKeetSubProcesosDto", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
      this.attrs.put("subprocesos", subProcesos);
      if(subProcesos!= null && !subProcesos.isEmpty()) 
        if((Boolean)this.attrs.get("idFirstSubProceso") || Objects.equals(subProcesos.size(), 1))
          this.attrs.put("idSubProceso", subProcesos.get(0).getValue());
        else
          this.attrs.put("idFirstSubProceso", Boolean.TRUE);
      else
        this.attrs.put("idSubProceso", -1L);
      if(!(Boolean)this.attrs.get("idFirstCasa"))
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
 
  
}