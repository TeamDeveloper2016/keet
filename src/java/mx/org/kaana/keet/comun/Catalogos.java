package mx.org.kaana.keet.comun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 10/04/2020
 *@time 02:06:58 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public final class Catalogos {
	
	public static void toLoadEspecialidades(Map<String, Object> attrs) throws Exception {
		Map<String, Object> params      = null;		
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);			
			attrs.put("departamentos", UISelect.seleccione("TcKeetDepartamentosDto", "especialidades", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS));
		} // try
		finally {
			Methods.clean(params);
		} // finally		
	} // toLoadDepartamentos
	
	public static void toLoadDepartamentos(Map<String, Object> attrs) throws Exception {
		Map<String, Object> params      = null;		
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);			
			attrs.put("departamentos", UISelect.seleccione("TcKeetDepartamentosDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS));
		} // try
		finally {
			Methods.clean(params);
		} // finally		
	} // toLoadDepartamentos
	
	public static void toLoadPuestos(Map<String, Object> attrs) throws Exception {
		List<UISelectItem> puestos= null;
    Map<String, Object> params= null;
    try {
      params = new HashMap<>();
      params.put(Constantes.SQL_CONDICION, "id_empresa=" + attrs.get("idEmpresa"));
      puestos = UISelect.seleccione("TcManticPuestosDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS, Constantes.SQL_TODOS_REGISTROS);
			if(!puestos.isEmpty()) {
				attrs.put("puestos", puestos);
				attrs.put("idPuesto", UIBackingUtilities.toFirstKeySelectItem(puestos));
			} // if
    } // try
    finally {
      Methods.clean(params);
    } // finally
	} // toLoadPuestos
	
	public static List<UISelectEntity> toContratistasPorElDia() throws Exception {
		List<Columna> columns        = null;
		List<UISelectEntity> regresar= null;
		try {
			columns= new ArrayList<>();
			columns.add(new Columna("nombres", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
		  regresar= UIEntity.seleccione("VistaPersonasDto", "contratistas", Collections.EMPTY_MAP, columns, Constantes.SQL_TODOS_REGISTROS, "nombre");
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

	public static void toLoadContratistas(Map<String, Object> attrs) throws Exception {
		List<UISelectEntity>contratistas= toContratistasPorElDia();
		attrs.put("contratistas", contratistas);
		attrs.put("idContratista", UIBackingUtilities.toFirstKeySelectEntity(contratistas));
	}

}
