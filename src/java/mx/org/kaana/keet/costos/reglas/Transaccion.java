package mx.org.kaana.keet.costos.reglas;

import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetGastosDetallesDto;
import mx.org.kaana.keet.db.dto.TcKeetGastosDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private static final Log LOG= LogFactory.getLog(Transaccion.class);
  
  private Entity row;
	private String messageError;

	public Transaccion(Entity row) {
		this.row= row;
	}	

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar= Boolean.TRUE;
		try {
      this.messageError= "";
			switch(accion) {
				case MODIFICAR:
         	TcKeetGastosDetallesDto item= (TcKeetGastosDetallesDto) DaoFactory.getInstance().findById(sesion, TcKeetGastosDetallesDto.class, this.row.getKey());
          if(!Objects.equals(item, null) && !Objects.equals(item.getNombre(), this.row.toString("nombre"))) {
            item.setNombre(this.row.toString("nombre"));
            DaoFactory.getInstance().update(sesion, item);
          } // if
					break;								
			} // switch
		} // try
		catch (Exception e) {			
      if(e!= null)
        if(e.getCause()!= null)
          this.messageError= this.messageError.concat("<br/>").concat(e.getCause().toString());
        else
          this.messageError= this.messageError.concat("<br/>").concat(e.getMessage());
			throw new Exception(this.messageError);
		} // catch		
		return regresar;
	}	

}