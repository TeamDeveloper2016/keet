package mx.org.kaana.keet.catalogos.dinamicos.backing;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.beans.SelectionItem;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Encriptar;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.keet.catalogos.dinamicos.reglas.Transaccion;
import mx.org.kaana.keet.enums.ECatalogosDinamicos;
import org.primefaces.model.DualListModel;

@Named(value = "keetCatalogosDinamicosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639367L;  
	private DualListModel<SelectionItem> selectionItems;

	public DualListModel<SelectionItem> getSelectionItems() {
		return selectionItems;
	}

	public void setSelectionItems(DualListModel<SelectionItem> selectionItems) {
		this.selectionItems = selectionItems;
	}	
	
  @PostConstruct
  @Override
  protected void init() {		
		ECatalogosDinamicos catalogoDinamico= null;
    try {
			catalogoDinamico= (ECatalogosDinamicos) JsfBase.getFlashAttribute("catalogoDinamico");
      this.attrs.put("catalogoDinamico", catalogoDinamico);
			this.attrs.put("titulo", catalogoDinamico.getTitulo());						
      this.attrs.put("isClave", catalogoDinamico.getClave());
      this.attrs.put("estatus", JsfBase.getFlashAttribute("estatus"));
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("unit", JsfBase.getFlashAttribute("unit"));      
      this.attrs.put("idXml", JsfBase.getFlashAttribute("idXml"));      
      this.attrs.put("idKey", JsfBase.getFlashAttribute("idKey"));			
      this.doLoad();      
			if(catalogoDinamico.getEstatus())
				this.selectionItems= new DualListModel<>(loadEstatus(false), loadEstatus(true));     						
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	public void doLoad() {
		ECatalogosDinamicos catalogoDinamico= null;
    EAccion eaccion        = null;
		IBaseDto dto           = null;
    Long idKey             = -1L;
		Constructor constructor= null;				
    try {
			catalogoDinamico= (ECatalogosDinamicos) this.attrs.get("catalogoDinamico");
      eaccion= (EAccion) this.attrs.get("accion");			
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:
					constructor= catalogoDinamico.getClase().getConstructor(new Class[]{});
					dto= (IBaseDto) constructor.newInstance();          
          break;
        case MODIFICAR:
        case CONSULTAR:
        case COPIAR:
        case ACTIVAR:
          idKey= (Long) (this.attrs.get("idKey"));          
					dto  = DaoFactory.getInstance().findById(catalogoDinamico.getClase(), idKey);					
          break;
      } // switch
			this.attrs.put("dto", dto);			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // doLoad
	
	private List<SelectionItem> loadEstatus(boolean asignados){
		List<SelectionItem> regresar= null;		
		EAccion eaccion             = null;
		List<UISelectItem> estatus  = null;
		Map<String, Object>params   = null;		
		try {						
			eaccion= (EAccion) this.attrs.get("accion");
			if(eaccion.equals(EAccion.AGREGAR) && asignados)
				regresar= new ArrayList<>();
			else{
				regresar= new ArrayList<>();				
				params= new HashMap<>();
				switch(eaccion){
					case AGREGAR:
						params.put(Constantes.SQL_CONDICION, "id_key not in (".concat(this.attrs.get("idKey").toString()).concat(")"));						
						break;
					default:			
						if(asignados)
							params.put(Constantes.SQL_CONDICION, "id_key in (".concat(((String)(((IBaseDto)this.attrs.get("dto")).toValue("estatusAsociados")))).concat(")"));																			
						else
							params.put(Constantes.SQL_CONDICION, "id_key not in (".concat(((String)(((IBaseDto)this.attrs.get("dto")).toValue("estatusAsociados")))).concat(")"));																			
						break;
				} // switch						
				estatus= UISelect.build(this.attrs.get("unit").toString(), this.attrs.get("idXml").toString(), params, "nombre", " ", EFormatoDinamicos.MAYUSCULAS);
				for(UISelectItem item: estatus){
					if (eaccion.equals(EAccion.AGREGAR) || asignados)
						regresar.add(new SelectionItem(item.getValue().toString(), item.getLabel()));					
					else if(!asignados && !((Long)item.getValue()).equals((Long)this.attrs.get("idKey")))
						regresar.add(new SelectionItem(item.getValue().toString(), item.getLabel()));											
				} // for				
			} // else
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		return regresar;
	} // loadEstatus	

  public String doAceptar() {
		ECatalogosDinamicos catalogoDinamico= null;
    Transaccion transaccion= null;
    String regresar        = null;
		EAccion eaccion        = null;
		IBaseDto dto           = null;		
		String ids             = null;
    try {
			catalogoDinamico= (ECatalogosDinamicos) this.attrs.get("catalogoDinamico");
			eaccion= (EAccion) this.attrs.get("accion");
			dto= (IBaseDto) this.attrs.get("dto");
			if(catalogoDinamico.getEstatus()){
				ids= "";
				for(SelectionItem id: this.selectionItems.getTarget())
					ids= ids.concat(id.getKey().concat(","));
				Methods.setValue(dto, "estatusAsociados", new Object[]{ids.substring(0, ids.length()-1)});			
			} // if
      transaccion = new Transaccion(dto);			
      if (transaccion.ejecutar(eaccion)) {
        regresar = doCancelar();
        JsfBase.addMessage("Se realizó el registro de forma correcta.", ETipoMensaje.INFORMACION);
      } // if
      else 
        JsfBase.addMessage("Ocurrió un error al realizar el registro", ETipoMensaje.ERROR);      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {
		ECatalogosDinamicos catalogoDinamico= null;
		String regresar    = null;
		Encriptar encriptar= null;		
		try {
			encriptar= new Encriptar();
			catalogoDinamico= (ECatalogosDinamicos) this.attrs.get("catalogoDinamico");
			JsfBase.setFlashAttribute("unit", encriptar.encriptar(catalogoDinamico.name()));			
			regresar= "filtro".concat(Constantes.REDIRECIONAR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		    
    return regresar;
 } // doCancela	
}
