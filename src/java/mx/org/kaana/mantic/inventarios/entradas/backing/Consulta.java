package mx.org.kaana.mantic.inventarios.entradas.backing;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;

@Named(value = "manticInventariosEntradasConsulta")
@ViewScoped
public class Consulta extends Filtro implements Serializable { 

	private static final long serialVersionUID=1368701967796774741L;
	
  @Override
  public String doAccion(String accion) {
		String regresar= "/Paginas/Mantic/Inventarios/Entradas/almacen";
    EAccion eaccion   = null;
		Long idNotaEntrada= -1L;
		Long idOrdenCompra= -1L;
		try {
			eaccion= EAccion.valueOf(accion.toUpperCase());
  		Long idNotaTipo= eaccion.equals(EAccion.COMPLETO)? 1L: -1L;
			if(this.attrs.get("seleccionado")!= null) {
			  idNotaEntrada= ((Entity)this.attrs.get("seleccionado")).getKey();
			  idOrdenCompra= ((Entity)this.attrs.get("seleccionado")).toLong("idOrdenCompra");
			  idNotaTipo   = ((Entity)this.attrs.get("seleccionado")).toLong("idNotaTipo");
			} // if
      switch(idNotaTipo.intValue()) {
        case -1:// NORMAL
        case 2: // NORMAL
          JsfBase.setFlashAttribute("accion", eaccion);	
          if((eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR)) && idNotaTipo.equals(2L)) 
            regresar= regresar.concat("?zOyOxDwIvGuCt=zNyLxMwAvCuEtAs");
          else
            regresar= regresar.concat("?zOyOxDwIvGuCt=zAyIxRwEvCuTtDs");
          JsfBase.setFlashAttribute("idOrdenCompra", (eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR) || idNotaTipo.equals(2L)) && idOrdenCompra!= null? idOrdenCompra: -1L);
          break;
      } // switch     
			JsfBase.setFlashAttribute("retorno", "/Paginas/Mantic/Inventarios/Entradas/consulta");		
			JsfBase.setFlashAttribute("idNotaEntrada", eaccion.equals(EAccion.MODIFICAR) || eaccion.equals(EAccion.CONSULTAR)? idNotaEntrada: -1L);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch
		return regresar.concat(Constantes.REDIRECIONAR_AMPERSON);
  } // doAccion 

  @Override
	public String doOrdenCompra() {
		JsfBase.setFlashAttribute("idOrdenCompra", this.attrs.get("idOrdenCompra"));
		return "/Paginas/Mantic/Inventarios/Entradas/ordenes".concat(Constantes.REDIRECIONAR);
	}
  
}
