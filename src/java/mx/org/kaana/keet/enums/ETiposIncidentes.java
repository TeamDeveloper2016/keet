package mx.org.kaana.keet.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ETiposIncidentes {
	
	FALTA            ("FALTA"           , "incidencia-rojo"),
	VACACIONES       ("VACACIONES"      , "incidencia-verde"),
	PERMISO          ("PERMISO"         , "incidencia-amarilla"),
	LICENCIA_MEDICA  ("LICENCIA MEDICA" , "incidencia-naranja"),
	LICENCIA_MATERNA ("LICENCIA MATERNA", "incidencia-naranja"),
	LICENCIA_PATERNA ("LICENCIA PATERNA", "incidencia-naranja"),
	OMISION_ENTRADA  ("OMISION ENTRADA" , "incidencia-azul"),
	OMISION_SALIDA   ("OMISION SALIDA"  , "incidencia-azul"),
	ONOMASTICO       ("ONOMASTICO"      , "incidencia-verde"),
	ALTA             ("ALTA"            , "incidencia-verde"),
	BAJA             ("BAJA"            , "incidencia-rojo"),
	REINGRESO        ("REINGRESO"       , "incidencia-verde");
	
	private static final Map<Long, ETiposIncidentes> lookup= new HashMap<>();		
	private String nombre;	
	private String styleClass;	
	
	static {
    for (ETiposIncidentes item: EnumSet.allOf(ETiposIncidentes.class)) 
      lookup.put(item.getKey(), item);    
  } // static
	
	private ETiposIncidentes(String nombre, String styleClass) {
		this.nombre    = nombre;
		this.styleClass= styleClass;
	}	
	
	public Long getKey(){
		return this.ordinal() + 1L;
	} // getKey

	public String getNombre() {
		return nombre;
	}		

	public String getStyleClass() {
		return styleClass;
	}
	
	public static ETiposIncidentes fromId(Long id) {
    return lookup.get(id);
  } // fromIdEstatusEgreso	
}