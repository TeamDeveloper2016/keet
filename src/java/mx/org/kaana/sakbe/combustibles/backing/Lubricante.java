package mx.org.kaana.sakbe.combustibles.backing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.comun.IBaseStorage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


@Named(value= "sakbeCombustiblesLubricante")
@ViewScoped
public class Lubricante extends Accion implements IBaseStorage, Serializable {

	private static final Log LOG              = LogFactory.getLog(Lubricante.class);
  private static final long serialVersionUID= 327393488565639327L;

  @Override
	protected void toLoadTiposCombustibles() {
		List<UISelectEntity> tiposCombustibles= null;
		Map<String, Object>params             = new HashMap<>();
		try {
			params.put("idTipoInsumo", 2L);
			tiposCombustibles= UIEntity.build("TcSakbeTiposCombustiblesDto", "tipo", params);
			this.attrs.put("tiposCombustibles", tiposCombustibles);
      if(!tiposCombustibles.isEmpty()) 
        if(this.accion.equals(EAccion.AGREGAR))
          this.combustible.setIkTipoCombustible(tiposCombustibles.get(0));
        else  
          this.combustible.setIkTipoCombustible(tiposCombustibles.get(tiposCombustibles.indexOf(this.combustible.getIkTipoCombustible())));
		} // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
		finally{
			Methods.clean(params);
		} // finally
	} // toLoadTiposCombustibles  
  
}