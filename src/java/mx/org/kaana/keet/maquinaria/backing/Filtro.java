package mx.org.kaana.keet.maquinaria.backing;

import java.io.Serializable;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.JsfBase;

@Named(value = "keetMaquinariaFiltro")
@ViewScoped
public class Filtro extends  mx.org.kaana.keet.catalogos.contratos.backing.Filtro implements Serializable {

  private static final long serialVersionUID = 8193667741599428879L;

  public String doAdicionales() {
    JsfBase.setFlashAttribute("idContrato", ((Entity) this.attrs.get("seleccionado")).getKey());
    JsfBase.setFlashAttribute("idCliente", ((Entity) this.attrs.get("seleccionado")).toLong("idCliente"));
    JsfBase.setFlashAttribute("retorno", "filtro");
    return "adicionales".concat(Constantes.REDIRECIONAR);
  }
  
}