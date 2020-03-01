package mx.org.kaana.keet.test;

import java.time.LocalDateTime;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.keet.db.dto.TcKeetTiposAtributosDto;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Bitacora {

	private static final Log LOG=LogFactory.getLog(Bitacora.class);
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
			TcKeetTiposAtributosDto dto= (TcKeetTiposAtributosDto)DaoFactory.getInstance().findById(TcKeetTiposAtributosDto.class, 1L);
      dto.setDescripcion("PROBANDO QUE ES GERUNDIO");
      dto.setRegistro(LocalDateTime.now());
			Transaccion tnx= new Transaccion(dto);
			LOG.info(tnx.ejecutar(EAccion.MODIFICAR));
			LOG.info("ok");
    }

}
