package mx.org.kaana.keet.nomina.backing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.nomina.reglas.Transaccion;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.nomina.beans.Nomina;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.comun.IBaseImportar;
import org.primefaces.event.TabChangeEvent;

@Named(value= "keetNominasAccion")
@ViewScoped
public class Accion extends IBaseImportar implements Serializable {

	private static final Log LOG=LogFactory.getLog(Accion.class);
  private static final long serialVersionUID= 318633488565639323L;

	@PostConstruct
  @Override
  protected void init() {		
		this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
		this.attrs.put("idNomina", JsfBase.getFlashAttribute("idNomina")== null? -1L: JsfBase.getFlashAttribute("idNomina"));
		this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
		this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
		this.attrs.put("nomina", new Nomina());
		this.loadCatalogs();
  } // init
  
	public void doTabChange(TabChangeEvent event) {
		// if(event.getTab().getTitle().equals("Archivos")) 
		//	this.doLoadArhivos("VistaCargasMasivasDto", "importados", this.attrs);
	} // doTabChange		
	
	public String doCancelar() {   
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar
	
	public String doAceptar() {
		String regresar= null;
    try {
			Transaccion transaccion= new Transaccion(((UISelectEntity)this.attrs.get("idNomina")).getKey(), JsfBase.getAutentifica());
			if(transaccion.ejecutar(EAccion.AGREGAR))
				JsfBase.addMessage("Se procesó la nómina.", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("Ocurrió un error en el proceso de nómina.", ETipoMensaje.ALERTA);	
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    return regresar;
	} // doAceptar	
  
  public void doCompleto() {
		// JsfBase.addMessage("Detalle del mensaje", "Se proceso correctamente el catalogo !.", ETipoMensaje.INFORMACION);		
	} // doCompleto

	private void loadCatalogs() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			params= new HashMap<>();
		  params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.attrs.put("tipos", UIEntity.seleccione("TcKeetTiposNominasDto", "row", params, "nombre"));
      this.attrs.put("idTipoNomina", new UISelectEntity(-1L));
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
	
	public void doLoadNominas() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			params= new HashMap<>();
		  params.put("idTipoNomina", this.attrs.get("idTipoNomina"));
		  params.put("ejercicio", Fecha.getAnioActual()- 1);
		  params.put("sucursales", this.attrs.get("sucursales"));
      this.attrs.put("nominas", UIEntity.seleccione("VistaNominaDto", "nominas", params, "nombre"));
      this.attrs.put("idNomina", new UISelectEntity(-1L));
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
	
	public void doLoadNomina() {
    Map<String, Object> params= new HashMap<>();
    try {
			params= new HashMap<>();
		  params.put("idNomina", ((UISelectEntity)this.attrs.get("idNomina")).getKey());
      this.attrs.put("nomina", DaoFactory.getInstance().toEntity(Nomina.class, "VistaNominaDto", "nomina", params));
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
