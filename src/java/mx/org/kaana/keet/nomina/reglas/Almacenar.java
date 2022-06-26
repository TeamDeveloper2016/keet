package mx.org.kaana.keet.nomina.reglas;

import java.time.LocalDateTime;
import java.util.List;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.nomina.beans.Contrato;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 25/06/2022
 *@time 08:13:52 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Almacenar extends IBaseTnx {

	private static final Log LOG= LogFactory.getLog(Almacenar.class);

  private List<Contrato> contratos;
	private String messageError;
	
	public Almacenar(List<Contrato> contratos) {
    this.contratos= contratos;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
		boolean regresar= true;
    try {
      this.messageError= "Ocurrio un error en el proceso de actualización";
			switch(accion) {
				case GENERAR:
          this.toFillRetenciones(sesion);
					break;
			} // switch
		} // try
		catch (Exception e) {			
      Error.mensaje(e);
			throw new Exception(this.messageError.concat("<br/>")+ e);		
		} // catch		
		return regresar;	
  }

  private void toFillRetenciones(Session sesion) throws Exception {
    try {   
      for (Contrato item: this.contratos) {
        switch(item.getSql()) {
          case INSERT:
            item.setIdUsuario(JsfBase.getIdUsuario());
            DaoFactory.getInstance().insert(sesion, item);
            break;
          case UPDATE:
            item.setRegistro(LocalDateTime.now());
            DaoFactory.getInstance().update(sesion, item);
            break;
          case SELECT:
            break;
        } // switch
      } // for
    } // try
    catch (Exception e) {
			throw e;
    } // catch	
  }  

}
