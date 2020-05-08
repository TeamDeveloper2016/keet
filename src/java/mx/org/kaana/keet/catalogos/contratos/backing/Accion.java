package mx.org.kaana.keet.catalogos.contratos.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.catalogos.contratos.beans.Lote;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.keet.catalogos.contratos.reglas.Transaccion;
import mx.org.kaana.keet.catalogos.contratos.beans.RegistroContrato;
import mx.org.kaana.keet.db.dto.TcKeetProyectosDto;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectItem;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.map.PointSelectEvent;
import org.primefaces.model.map.LatLng;


@Named(value = "keetCatalogosContratosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639367L;
	private RegistroContrato contrato;
	private List<Lote> lotesOrden;
	private List<UISelectItem> fachadas;
	private List<UISelectItem> prototipos;

	public RegistroContrato getContrato() {
		return contrato;
	}

	public void setContrato(RegistroContrato contrato) {
		this.contrato = contrato;
	}

	public List<UISelectItem> getFachadas() {
		return fachadas;
	}

	public void setFachadas(List<UISelectItem> fachadas) {
		this.fachadas = fachadas;
	}

	public List<UISelectItem> getPrototipos() {
		return prototipos;
	}

	public void setPrototipos(List<UISelectItem> prototipos) {
		this.prototipos = prototipos;
	}	
	
	@PostConstruct
  @Override
  protected void init() {		
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idContrato", JsfBase.getFlashAttribute("idContrato"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("mostrarGeo", false);
      loadCombos();
			doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
	
	private void loadCombos() {
		try {
			this.attrs.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.attrs.put("clientes", UIEntity.seleccione("TcManticClientesDto", "sucursales", this.attrs, "clave"));
      this.attrs.put("proyectos", UIEntity.seleccione("TcKeetProyectosDto", "row", this.attrs, "clave"));
      this.attrs.put("tipoObras", UIEntity.seleccione("VistaTiposObrasDto", "catalogo", this.attrs, "tipoObra"));
      this.fachadas= UISelect.seleccione("TcKeetTiposFachadasDto", "row", this.attrs, "nombre");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // loadCombos
	
	public void doLoadPrototipos() {
		try {
			loadPrototipos();
			this.contrato.getContrato().validaPrototipos((List<UISelectItem>)this.attrs.get("prototipos"));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // doLoadPrototipos
	
	private void loadPrototipos() {
	  try {
			this.attrs.put("idCliente", ((TcKeetProyectosDto)DaoFactory.getInstance().findById(TcKeetProyectosDto.class, this.contrato.getContrato().getIdProyecto())).getIdCliente());
      this.prototipos= UISelect.seleccione("TcKeetPrototiposDto", "byCliente", this.attrs, "nombre");
			this.contrato.getContrato().validaPrototipos(this.prototipos);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} // doLoadPrototipos

  public void doLoad() {
    EAccion eaccion= null;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:											
          this.contrato= new RegistroContrato();
          break;
        case MODIFICAR:					
        case CONSULTAR:					
        case SUBIR:					
          this.contrato= new RegistroContrato(Long.valueOf(this.attrs.get("idContrato").toString()));
					loadPrototipos();          
          break;
      } // switch
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad

  public String doAceptar() {  
    Transaccion transaccion= null;
    String regresar        = null;
		EAccion eaccion        = null;
    try {			
			eaccion= (EAccion) this.attrs.get("accion");
      this.contrato.getContrato().setIdUsuario(JsfBase.getIdUsuario());
			transaccion= new Transaccion(this.contrato);
			if (transaccion.ejecutar(eaccion)) {
				regresar =  "filtro".concat(Constantes.REDIRECIONAR);//this.attrs.get("retorno")!=null? this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR): "filtro".concat(Constantes.REDIRECIONAR);
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el contrato de forma correcta."), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el contrato.", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
    return "filtro".concat(Constantes.REDIRECIONAR);
  } // doAccion	
	
	public void onSelect(SelectEvent<Lote> event) {
		try {
			this.lotesOrden= new ArrayList<>();
			for(Lote item: this.contrato.getContrato().getLotes())
				this.lotesOrden.add(item);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch		
	}	

	public void onReorder() {
		try {
			for(int i=0; this.contrato.getContrato().getLotes().size()<i; i++)
				this.contrato.getContrato().getLotes().get(i).setOrden(this.lotesOrden.get(i).getOrden());
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch			
	}

	public void doGeoreferencia(Lote lote){
		try {
			this.attrs.put("loteGeoreferencia", lote);			
			this.attrs.put("mostrarGeo", true);						
			this.attrs.put("latitudAnterior", lote.getLatitud());						
			this.attrs.put("longitudAnterior", lote.getLongitud());						
			UIBackingUtilities.execute(Cadena.isVacio(lote.getLatitud()) || Cadena.isVacio(lote.getLongitud()) ? "executeGeo();" : "executeExistGeo();");			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doGeoreferencia	
	
	public void doExistGeo() {
		Lote lote= null;
		try {
			lote= (Lote) this.attrs.get("loteGeoreferencia");
			this.attrs.put("latitud", lote.getLatitud());
			this.attrs.put("longitud", lote.getLongitud());			
			UIBackingUtilities.execute("existLocalization('".concat(lote.getLatitud()).concat("','").concat(lote.getLongitud()).concat("');"));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doExistGeo
	
	public void doInitGeo(String latitud, String longitud){		
		try {
			this.attrs.put("latitud", latitud);
			this.attrs.put("longitud", longitud);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doInitGeo
	
	public void onPointSelect(PointSelectEvent event) {
		LatLng coordenadas= null;
		Lote lote         = null;
		try {
			coordenadas= event.getLatLng();                  
			lote= (Lote) this.attrs.get("loteGeoreferencia");						
			lote.setLatitud(String.valueOf(coordenadas.getLat()));
			lote.setLongitud(String.valueOf(coordenadas.getLng()));
			this.attrs.put("latitud", lote.getLatitud());
			this.attrs.put("longitud", lote.getLongitud());			
			this.attrs.put("loteGeoreferencia", lote);
			UIBackingUtilities.execute("updateLocalization('".concat(lote.getLatitud()).concat("','").concat(lote.getLongitud()).concat("');"));
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
  } // doMarkerDrag
	
	public void doAceptarGeo() {
		Lote lote= null;
		try {
			lote= (Lote) this.attrs.get("loteGeoreferencia");		
			if(Cadena.isVacio(lote.getLatitud()) || Cadena.isVacio(lote.getLongitud())){
				FacesContext.getCurrentInstance().getExternalContext();
				this.contrato.getContrato().getLotes().get(this.contrato.getContrato().getLotes().indexOf(lote)).setLatitud(this.attrs.get("latitud").toString());
				this.contrato.getContrato().getLotes().get(this.contrato.getContrato().getLotes().indexOf(lote)).setLongitud(this.attrs.get("longitud").toString());
			} // if
			else{
				this.contrato.getContrato().getLotes().get(this.contrato.getContrato().getLotes().indexOf(lote)).setLatitud(lote.getLatitud());
				this.contrato.getContrato().getLotes().get(this.contrato.getContrato().getLotes().indexOf(lote)).setLongitud(lote.getLongitud());
			} // else
			this.attrs.put("mostrarGeo", false);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doAceptarGeo	
	
	public void doCancelarGeo() {		
		Lote lote= null;
		try {
			if(this.attrs.get("latitudAnterior")!= null && this.attrs.get("longitudAnterior")!= null){
				lote= (Lote) this.attrs.get("loteGeoreferencia");		
				this.contrato.getContrato().getLotes().get(this.contrato.getContrato().getLotes().indexOf(lote)).setLatitud(this.attrs.get("latitudAnterior").toString());
				this.contrato.getContrato().getLotes().get(this.contrato.getContrato().getLotes().indexOf(lote)).setLongitud(this.attrs.get("longitudAnterior").toString());			
			} // if
			this.attrs.put("mostrarGeo", false);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doCancelarGeo
}