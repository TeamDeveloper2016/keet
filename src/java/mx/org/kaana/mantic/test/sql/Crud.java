package mx.org.kaana.mantic.test.sql;

import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.dto.TcJanalAyudasDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.reflection.Methods;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 19/02/2020
 *@time 11:06:05 PM 
 *@author Team Developer 2016 <team.developer2016@gmail.com>
 */
public class Crud {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
      Entity entity=  (Entity)DaoFactory.getInstance().toEntity("select tc_janal_booleanos.id_booleano as id_key, tc_janal_booleanos.* from tc_janal_booleanos");
			System.out.println(entity);
			
			TcJanalAyudasDto dto= new TcJanalAyudasDto("HOLA", -1L, "ESTO ES UN DEMO");
			DaoFactory.getInstance().insert(dto);
			System.out.println(dto);
			
			Map<String, Object> params=null;
			try {
				params=new HashMap<>();
				params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
				dto= (TcJanalAyudasDto)DaoFactory.getInstance().toEntity(TcJanalAyudasDto.class, "TcJanalAyudasDto", "row", params);
				System.out.println(dto);
			} // try
			catch (Exception e) {
				Error.mensaje(e);
				throw e;
			} // catch
			finally {
				Methods.clean(params);
			} // finally
		}

}
