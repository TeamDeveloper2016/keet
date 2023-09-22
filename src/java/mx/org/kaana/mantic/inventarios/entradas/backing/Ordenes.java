package mx.org.kaana.mantic.inventarios.entradas.backing;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.formato.Error;

@Named(value = "manticInventariosEntradasOrdenes")
@ViewScoped
public class Ordenes extends mx.org.kaana.mantic.compras.ordenes.backing.Filtro implements Serializable { 

	private static final long serialVersionUID= 1268701967796774741L;
  
  @Override
  public String doNotaEntrada() {
		JsfBase.setFlashAttribute("ordenCompra", this.attrs.get("ordenCompra"));
		JsfBase.setFlashAttribute("idNotaEntrada", null);
		return "/Paginas/Mantic/Inventarios/Entradas/ordenes".concat(Constantes.REDIRECIONAR);
	}

  @Override
  public String doNotasEntradas(String accion) {
    EAccion eaccion= null;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
			JsfBase.setFlashAttribute("accion", eaccion);		
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Inventarios/Entradas/ordenes");		
			JsfBase.setFlashAttribute("idOrdenCompra", ((Entity)this.attrs.get("seleccionado")).getKey());
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return "/Paginas/Mantic/Inventarios/Entradas/almacen?zOyOxDwIvGuCt=zNyLxMwAvCuEtAs".concat(Constantes.REDIRECIONAR_AMPERSON);
  } 
  
}
