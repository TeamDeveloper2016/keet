package mx.org.kaana.keet.catalogos.subcontratistas.backing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.mantic.catalogos.proveedores.beans.RegistroProveedor;

@Named(value = "keetCatalogosSubcontratistasAccion")
@ViewScoped
public class Accion extends mx.org.kaana.mantic.catalogos.proveedores.backing.Accion implements Serializable {

  private static final long serialVersionUID = 327393488565639362L;

  @Override
  public void doLoad() {
    EAccion eaccion = null;
    Long idProveedor = -1L;
    try {
      eaccion = (EAccion) this.attrs.get("accion");
      this.attrs.put("nombreAccion", Cadena.letraCapital(eaccion.name()));
      switch (eaccion) {
        case AGREGAR:
          this.registroProveedor = new RegistroProveedor();
          this.registroProveedor.getProveedor().setIdSubcontratista(1L);
					this.loadCollections();
          break;
        case MODIFICAR:
        case CONSULTAR:
          idProveedor = Long.valueOf(this.attrs.get("idProveedor").toString());
          this.registroProveedor = new RegistroProveedor(idProveedor);
					this.loadCollections();
					if(!this.registroProveedor.getProveedoresDomicilio().isEmpty()) {
						this.registroProveedor.setProveedorDomicilioSeleccion(this.registroProveedor.getProveedoresDomicilio().get(0));
						this.doConsultarProveedorDomicilio();
					} // if
					if(!this.registroProveedor.getPersonasTiposContacto().isEmpty()) {
						this.registroProveedor.setPersonaTipoContacto(this.registroProveedor.getPersonasTiposContacto().get(0));
						this.registroProveedor.doConsultarAgente();
					} // if
					this.attrs.put("idEmpresa", new UISelectEntity(this.registroProveedor.getProveedor().getIdEmpresa()));
          break;
      } // switch      
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } 

}