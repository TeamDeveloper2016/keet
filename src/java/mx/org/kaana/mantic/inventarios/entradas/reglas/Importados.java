package mx.org.kaana.mantic.inventarios.entradas.reglas;

import java.io.Serializable;
import java.util.Collections;
import org.hibernate.Session;
import mx.org.kaana.kajool.enums.EAccion;
import static mx.org.kaana.kajool.enums.EAccion.AGREGAR;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.mantic.catalogos.articulos.beans.Importado;
import mx.org.kaana.mantic.db.dto.TcManticNotasEntradasDto;
import org.apache.log4j.Logger;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 7/05/2018
 *@time 03:29:13 PM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Importados extends Transaccion implements Serializable {

  private static final Logger LOG = Logger.getLogger(Importados.class);
	private static final long serialVersionUID=-6063204157451117549L;
 
	private String messageError= "";
  
	public Importados(TcManticNotasEntradasDto orden, Importado xml, Importado pdf) {
		super(orden, Collections.EMPTY_LIST, true, xml, pdf);
	} // Transaccion

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar= true;
		try {
			this.setMessageError("Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" al importar el archivo."));
			switch(accion) {
				case AGREGAR:
     	    this.toUpdateDeleteXml(sesion);	
          this.toProcesarFacturas(sesion, this.orden.getIdNotaEntrada());
					break;
			} // switch
		} // try
		catch (Exception e) {
			regresar= false;
      if(e!= null)
        if(e.getCause()!= null)
          this.messageError= this.messageError.concat("<br/>").concat(e.getCause().toString());
        else
          this.messageError= this.messageError.concat("<br/>").concat(e.getMessage());
			throw new Exception(this.messageError);
		} // catch		
		LOG.info("Se importaron de forma correcta los archivos !");
		return regresar;
	}	// ejecutar

} 