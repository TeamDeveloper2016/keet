package mx.org.kaana.keet.catalogos.prototipos.backing;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.catalogos.prototipos.beans.Prototipos;
import mx.org.kaana.keet.catalogos.prototipos.beans.SistemaConstructivo;
import mx.org.kaana.keet.catalogos.prototipos.reglas.AdminSistemaConstructivo;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.keet.catalogos.prototipos.reglas.Transaccion;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;


@Named(value = "keetCatalogosPrototiposAccion")
@ViewScoped
public class Accion extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 327393488565639367L;
	private Prototipos prototipo;

  public Prototipos getPrototipo() {
    return prototipo;
  }

  public void setPrototipo(Prototipos prototipo) {
    this.prototipo = prototipo;
  }

	
	@PostConstruct
  @Override
  protected void init() {		
    try {
      if(JsfBase.getFlashAttribute("accion")== null)
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
      this.attrs.put("accion", JsfBase.getFlashAttribute("accion"));
      this.attrs.put("idPrototipo", JsfBase.getFlashAttribute("idPrototipo"));
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
      this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
      this.attrs.put("clientes", UIEntity.seleccione("TcManticClientesDto", "sucursales", this.attrs, "clave"));
      this.attrs.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      this.attrs.put("constructivos", UIEntity.seleccione("VistaPrototiposDto", "constructivosSelect",this.attrs, "descripcion"));			
			doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  public void doLoad() {
    EAccion eaccion    = null;
    Long idPrototipo= -1L;
    try {
      eaccion= (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:											
          this.prototipo= new Prototipos();
          break;
        case MODIFICAR:					
        case CONSULTAR:					
          idPrototipo= (Long)this.attrs.get("idPrototipo");
          this.prototipo= (Prototipos)DaoFactory.getInstance().toEntity(Prototipos.class,"TcKeetPrototiposDto","byId", this.attrs);
					this.prototipo.setIkCliente(((List<UISelectEntity>)this.attrs.get("clientes")).get(((List<UISelectEntity>)this.attrs.get("clientes")).indexOf(this.prototipo.getIdCliente())));
					this.prototipo.setIkSistemasConstructivos(new AdminSistemaConstructivo(DaoFactory.getInstance().toEntitySet(SistemaConstructivo.class,"VistaPrototiposDto", "constructivosById", this.attrs)));
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
      this.prototipo.setIdUsuario(JsfBase.getIdUsuario());
			transaccion = new Transaccion(this.prototipo);
			if (transaccion.ejecutar(eaccion)) {
				regresar = this.attrs.get("retorno").toString().concat(Constantes.REDIRECIONAR);
				//UIBackingUtilities.execute("janal.alert('Se agrego el proyecto');");
				JsfBase.addMessage("Se ".concat(eaccion.equals(EAccion.AGREGAR) ? "agregó" : "modificó").concat(" el protoripo de forma correcta."), ETipoMensaje.INFORMACION);
			} // if
			else 
				JsfBase.addMessage("Ocurrió un error al registrar el proyecto.", ETipoMensaje.ERROR);      			
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    return regresar;
  } // doAccion

  public String doCancelar() {   
    return (String)this.attrs.get("retorno");
  } // doAccion
	
	public void doAddConstructivo() {  
    try {			
			if(this.prototipo.getIkSistemasConstructivos().addSistemaConstructivo(((List<UISelectEntity>)this.attrs.get("constructivos")).get(((List<UISelectEntity>)this.attrs.get("constructivos")).indexOf((UISelectEntity)this.attrs.get("constructivo")))))
				JsfBase.addMessage("Agregado correctamente", ETipoMensaje.INFORMACION);
			else
				JsfBase.addMessage("El elemento ya se encuentra agregado", ETipoMensaje.ALERTA);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } // doAddConstructivo

	public void doRemoveConstructivo() {  
    try {			
			this.prototipo.getIkSistemasConstructivos().removeSistemaConstructivo(((Entity)this.attrs.get("seleccionado")).getKey());
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
  } // doAddConstructivo
	
}