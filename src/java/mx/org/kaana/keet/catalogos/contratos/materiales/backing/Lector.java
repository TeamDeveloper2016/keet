package mx.org.kaana.keet.catalogos.contratos.materiales.backing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.enums.EEstatusVales;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;

@Named(value = "keetCatalogosContratosMaterialesLector")
@ViewScoped
public class Lector extends IBaseFilter implements Serializable {  

	private static final long serialVersionUID = 5461370708574838218L;

  @PostConstruct
  @Override
  protected void init() {		    
		EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;		
    try {
			this.attrs.put("isAdmin", JsfBase.isAdminEncuestaOrAdmin());						
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());							
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");						
			this.attrs.put("opcionResidente", opcion);			
			this.attrs.put("idDesarrollo", idDesarrollo);      			
			this.attrs.put("codigoQr", "");    						
			this.attrs.put("mostrarEntrega", false);
			this.attrs.put("mostrarConsulta", false);
			this.attrs.put("existe", Boolean.FALSE);
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init		
	
  @Override
  public void doLoad() {		
		String[] codigo           = null;
		Entity seleccionado       = null;
		Map<String, Object> params= null;
		Entity figura             = null;
    try {   						      		
			codigo= this.attrs.get("codigoQr").toString().split("-");
		  this.attrs.put("existe", codigo== null || codigo.length< 3);
			if(codigo!= null && codigo.length> 2) {
				this.attrs.put("consecutivo", codigo[1]);
				this.attrs.put("empleado", codigo[3]);			
				params= new HashMap<>();
				params.put("idDesarrollo", this.attrs.get("idDesarrollo"));
				params.put("condicionContratista", Constantes.SQL_VERDADERO);
				params.put("condicionSubcontratista", Constantes.SQL_VERDADERO);
				params.put(Constantes.SQL_CONDICION, "tc_keet_vales.consecutivo='".concat(this.attrs.get("consecutivo").toString()).concat("'"));
				seleccionado= (Entity) DaoFactory.getInstance().toEntity("VistaEntregaMaterialesDto", "vales", params);
				this.attrs.put("existe", seleccionado== null);
				if(seleccionado!= null) {
					this.attrs.put("perfil", seleccionado.toLong("figura").equals(1L) ? "Contratista" : "Subcontratista");			
					this.attrs.put("manzanaLote", seleccionado.toString("descripcionLote"));			
					this.attrs.put("seleccionado", seleccionado);
					params.clear();
					params.put("id", seleccionado.toLong("idFigura"));
					figura= (Entity) DaoFactory.getInstance().toEntity("VistaEntregaMaterialesDto", seleccionado.toLong("figura").equals(1L) ? "contratista" : "subcontratista", params);
					this.attrs.put("figura", new UISelectEntity(figura));
					this.attrs.put("estatus", EEstatusVales.fromId(seleccionado.toLong("idValeEstatus")).name());			
					this.attrs.put("mostrarEntrega", seleccionado.toLong("idValeEstatus").equals(EEstatusVales.DISPONIBLE.getKey()) || seleccionado.toLong("idValeEstatus").equals(EEstatusVales.INCOMPLETO.getKey()));
					this.attrs.put("mostrarConsulta", seleccionado.toLong("idValeEstatus").equals(EEstatusVales.ENTREGADO.getKey()) || seleccionado.toLong("idValeEstatus").equals(EEstatusVales.DISPONIBLE.getKey()) || seleccionado.toLong("idValeEstatus").equals(EEstatusVales.INCOMPLETO.getKey()));
				} // if
			} // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch    
  } // doLoad			
	
	public String doEntrega() {
    String regresar= null;    				
    try {						
			if(this.attrs.get("seleccionado")!= null && this.attrs.get("figura")!= null){
				JsfBase.setFlashAttribute("opcionResidente", (EOpcionesResidente)this.attrs.get("opcionResidente"));
				JsfBase.setFlashAttribute("seleccionado", (Entity) this.attrs.get("seleccionado"));
				JsfBase.setFlashAttribute("figura", (UISelectEntity) this.attrs.get("figura"));
				JsfBase.setFlashAttribute("idDepartamento", ((UISelectEntity) this.attrs.get("figura")).toLong("idDepartamento"));
				JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));
				JsfBase.setFlashAttribute("flujo", "filtro");
				regresar= "entrega".concat(Constantes.REDIRECIONAR);
			} // if
			else
				JsfBase.addMessage("No se ha detectado ningun vale.", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doEntrega
	
	public String doConsultar() {
    String regresar= null;    				
    try {			
			if(this.attrs.get("seleccionado")!= null && this.attrs.get("figura")!= null){
				doEntrega();
				regresar= "resumen".concat(Constantes.REDIRECIONAR);										
			} // if
			else
				JsfBase.addMessage("No se ha detectado ningun vale.", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doConsultar
	
	public String doCancelar() {
    String regresar          = null;    
		EOpcionesResidente opcion= null;		
    try {			
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));						
			JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));			
			JsfBase.setFlashAttribute("opcionResidente", opcion);									
			regresar= "filtro".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar						
}