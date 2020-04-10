package mx.org.kaana.keet.comun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 10/04/2020
 *@time 02:06:58 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public final class Catalogos {
	
	public static List<UISelectEntity> toContratistasPorElDia() throws Exception {
		List<Columna> columns        = null;
		List<UISelectEntity> regresar= null;
		try {
			columns= new ArrayList<>();
			columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
		  regresar= UIEntity.seleccione("VistaPersonasDto", "contratistas", Collections.EMPTY_MAP, columns, Constantes.SQL_TODOS_REGISTROS, "nombres");
			if(regresar!= null) {
				Entity sinContratista=  new Entity(999L);
				sinContratista.put("nombre", new Value("nombre", "POR EL DIA"));
				sinContratista.put("nombres", new Value("nombres", "SIN CONTRATISTA"));
				sinContratista.put("puesto", new Value("puesto", "POR EL DIA"));
				// sinContratista
				regresar.add(1, new UISelectEntity(sinContratista));
			} // if
		} // try
		finally {
			Methods.clean(columns);
		} // finally
		return regresar;
	}

}
