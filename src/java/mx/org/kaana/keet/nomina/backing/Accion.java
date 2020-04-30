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
import mx.org.kaana.keet.db.dto.TcKeetNominasDto;
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

	public Boolean getActivar() {
		Long idTipoNomina   = ((UISelectEntity)this.attrs.get("idTipoNomina")).getKey();
		Long idNominaEstatus= ((Nomina)this.attrs.get("ultima")).getIdNominaEstatus();
	  return idTipoNomina== -1L || (idTipoNomina== 2L && (idNominaEstatus== 1L || idNominaEstatus== 2L));
	}
	
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
		String regresar        = null;
		Transaccion transaccion= null;
    try {
			Long idNomina= ((UISelectEntity)this.attrs.get("idNomina")).getKey();
			if(idNomina== null || idNomina== -1L) {
				TcKeetNominasDto nomina= new TcKeetNominasDto(
					0D, // Double neto, 
					1L, // Long idNominaEstatus, 
					0D, // Double deducciones, 
					((UISelectEntity)this.attrs.get("idTipoNomina")).getKey(), // Long idTipoNomina, 
					0L, // Long personas, 
					0D, // Double aportaciones, 
					-1L, // Long idNomina, 
					((Nomina)this.attrs.get("ultima")).getTermino().plusDays(-1), // LocalDate fechaPago, 
					0L, // Long proveedores, 
					0D, // Double total, 
					((Nomina)this.attrs.get("ultima")).getTermino().plusDays(-2), // LocalDate fechaDispersion, 
					((Nomina)this.attrs.get("ultima")).getIdNominaPeriodo(), // Long idNominaPeriodo, 
					0D, // Double iva, 
					JsfBase.getIdUsuario(), // Long idUsuario, 
					0D, // Double subtotal, 
					((Nomina)this.attrs.get("nomina")).getObservaciones(), // String observaciones, 
					JsfBase.getAutentifica().getEmpresa().getIdEmpresa(), // Long idEmpresa, 
					0D // Double percepciones
				);
  			transaccion= new Transaccion(nomina, JsfBase.getAutentifica());
			}
			else
  			transaccion= new Transaccion(idNomina, JsfBase.getAutentifica());
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
			params.put("idTipoNomina", "1");
      this.attrs.put("ultima", DaoFactory.getInstance().toEntity(Nomina.class, "VistaNominaDto", "ultima", params));
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
			List<UISelectEntity> nominas= UIEntity.build("VistaNominaDto", "activa", params);
      this.attrs.put("nominas", nominas);
			if(nominas!= null && !nominas.isEmpty())
				this.attrs.put("idNomina", nominas.get(0));
			else
        this.attrs.put("idNomina", new UISelectEntity(-1L));
			this.doLoadNomina();
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
			Nomina nomina= (Nomina)DaoFactory.getInstance().toEntity(Nomina.class, "VistaNominaDto", "nomina", params);
			if(nomina== null)
				nomina= new Nomina();
      this.attrs.put("nomina", nomina);
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
