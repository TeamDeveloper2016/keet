package mx.org.kaana.keet.catalogos.contratos.backing;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.keet.catalogos.desarrollos.beans.RegistroDesarrollo;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;


@Named(value = "keetCatalogosContratosGeoreferencia")
@ViewScoped
public class Georeferencia extends IBaseAttribute implements Serializable {

	private static final long serialVersionUID      = -1527541903767470918L;
	protected static final String COORDENADA_CENTRAL= "21.8818,-102.291";
  private RegistroDesarrollo registroDesarrollo;	
	private MapModel model;
	
	public RegistroDesarrollo getRegistroDesarrollo() {
		return registroDesarrollo;
	}

	public void setRegistroDesarrollo(RegistroDesarrollo registroDesarrollo) {
		this.registroDesarrollo = registroDesarrollo;
	}			

	public MapModel getModel() {
		return model;
	}

	public void setModel(MapModel model) {
		this.model = model;
	}	
	
	@PostConstruct
  @Override
  protected void init() {		
    EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;
    try {
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");			
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("opcionResidente", opcion);			
			this.attrs.put("idDesarrollo", idDesarrollo);
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());						
			this.registroDesarrollo= new RegistroDesarrollo((Long)this.attrs.get("idDesarrollo"));      
			this.attrs.put("domicilio", toDomicilio());						
			doLoad();
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	
	private String toDomicilio(){
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

  public void doLoad() {
		List<Entity> lotes= null;
		Marker marker     = null;
		String icon       = null;
    try {
			this.attrs.put("coordenadaCentral", COORDENADA_CENTRAL);
			this.model= new DefaultMapModel();			
      lotes= DaoFactory.getInstance().toEntitySet("VistaGeoreferenciaLotesDto", "lotes", Cadena.getMapValues("idDesarrollo~".concat(this.attrs.get("idDesarrollo").toString())), Constantes.SQL_TODOS_REGISTROS);
			if(!lotes.isEmpty()){
				icon= JsfBase.getContext().concat("/javax.faces.resource/icon/mapa/").concat("home-green-grad.png").concat(".jsf?ln=janal");
				for(Entity lote: lotes){
					marker= new Marker(new LatLng(Double.valueOf(lote.toString("latitud")), Double.valueOf(lote.toString("longitud"))), "Contrato: ".concat(lote.toString("clave")).concat(", Lote: ").concat(lote.toString("codigo")), lote, icon);
					this.model.addOverlay(marker);
				} // for
				this.attrs.put("coordenadaCentral", lotes.get(0).toString("latitud").concat(",").concat(lotes.get(0).toString("longitud")));				
			} // if
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

	public String doCancelar() {
    String regresar          = null;    
		EOpcionesResidente opcion= null;
    try {
			opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));
			JsfBase.setFlashAttribute("idDesarrolloProcess", this.attrs.get("idDesarrollo"));
			JsfBase.setFlashAttribute("opcion", opcion);			
			regresar= this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR_AMPERSON);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
}