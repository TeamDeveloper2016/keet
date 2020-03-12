package mx.org.kaana.kajool.test;

import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SqlAction {

	private static final Log LOG=LogFactory.getLog(SqlAction.class);
	
	public static void main(String[] args) {
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
//			Sino no= new Sino();
//			LOG.info(no);
      Sino si= (Sino)DaoFactory.getInstance().toEntity(Sino.class, "TcJanalAyudasDto", "row", params);
			LOG.info(si);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
	}

}
