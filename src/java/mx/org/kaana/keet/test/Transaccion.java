package mx.org.kaana.keet.test;

import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import static mx.org.kaana.kajool.enums.EAccion.AGREGAR;
import static mx.org.kaana.kajool.enums.EAccion.ELIMINAR;
import static mx.org.kaana.kajool.enums.EAccion.MODIFICAR;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetTiposAtributosDto;
import mx.org.kaana.libs.pagina.KajoolBaseException;
import mx.org.kaana.libs.pagina.UIMessage;
import org.hibernate.Session;
import org.apache.log4j.Logger;

public class Transaccion extends IBaseTnx {

  private static final Logger LOG = Logger.getLogger(Transaccion.class);
	private TcKeetTiposAtributosDto dto;

	public Transaccion(TcKeetTiposAtributosDto dto) {
		this.dto=dto;
	}
 
  @Override
  protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
    boolean regresar	= true;
    try {
      switch(accion) {
        case AGREGAR :
          if(DaoFactory.getInstance().insert(sesion, this.dto) <=0)
						throw new KajoolBaseException(UIMessage.toMessage("error_agregar"));
        break;
        case MODIFICAR :
					this.bitacora(sesion, "PROBANDO", this.dto);
          regresar= DaoFactory.getInstance().update(sesion, this.dto)> 0;
        break;
        case ELIMINAR :
          regresar= DaoFactory.getInstance().delete(sesion, this.dto)> 0;
        break;
      } // switch
      LOG.info("Transaccion ".concat(accion.name()).concat(" para ? ["+ regresar+ "]."));
    } // try   
    catch (Exception e) {
      throw e;
    } // catch
    return regresar;
  }
} 