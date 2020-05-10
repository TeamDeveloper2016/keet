package mx.org.kaana.keet.catalogos.rubros.backing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.catalogos.rubros.beans.RegistroRubro;
import mx.org.kaana.keet.catalogos.rubros.beans.RubroGrupo;
import mx.org.kaana.keet.catalogos.rubros.reglas.Transaccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.reflection.Methods;


@Named(value = "keetCatalogosRubrosAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

	private RegistroRubro rubro;
	
	public RegistroRubro getRubro() {
		return rubro;
	}

	public void setRubro(RegistroRubro rubro) {
		this.rubro = rubro;
	}	
	
	@PostConstruct
  @Override
  protected void init() {			
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idRubro", JsfBase.getFlashAttribute("idRubro"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales()); 
      loadCatalogos();
			doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void loadCatalogos(){
		Map<String, Object>params              = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			this.attrs.put("departamentos", UIEntity.seleccione("TcKeetDepartamentosDto", "especialidades",  "nombre"));
			this.attrs.put("unidades", UIEntity.seleccione("VistaEmpaqueUnidadDto", "row", params, "empaque"));
		} // try
		catch (Exception e) {
			throw e;
		} // catch	
		finally{
			Methods.clean(params);
		} // finally
	} // loadCatalogos
	
  public void doLoad() {
    EAccion eaccion= null;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:											
          this.rubro= new RegistroRubro();
          break;
        case MODIFICAR:					
        case CONSULTAR:					
					this.rubro= new RegistroRubro((Long)this.attrs.get("idRubro"));
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
			transaccion = new Transaccion(this.rubro);
			if (transaccion.ejecutar(eaccion)) {
				JsfBase.setFlashAttribute("idRubroProcess", this.rubro.getRubro().getIdRubro());
				regresar = this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);				
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el rubro de control de forma correcta."), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el rubro de control.", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
		JsfBase.setFlashAttribute("idRubroProcess", this.rubro.getRubro().getIdRubro());
    return (String) this.attrs.get("retorno");
  } // doCancelar	
	
	
	public void doCargaPaquetes(RubroGrupo rubroGrupo){
		int contador= 0;
		try {
     for(RubroGrupo item: this.rubro.getRubrosGrupos())
			 if(item.getDepartamento().getKey().equals(rubroGrupo.getDepartamento().getKey()) && item.isVisible())
				 contador++;
		 if(contador<= 1L)
			 rubroGrupo.cargaPuntosGrupos();
		 else
			 JsfBase.addAlert("No es posible seleccionar 2 paquetes diferentes de un mismo departamento, favor de seleccionar otro departamento.");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
	} //doCargaPaquetes
	
}