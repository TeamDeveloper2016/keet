package mx.org.kaana.keet.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EDiasSemana {
		
	LUNES	   ("LUN", "LUNES"),
	MARTES   ("MAR", "MARTES"),
	MIERCOLES("MIE", "MIERCOLES"),
	JUEVES   ("JUE", "JUEVES"),
	VIERNES  ("VIE", "VIERNES"),
	SABADO   ("SAB", "SABADO"),
	DOMINGO  ("DOM", "DOMINGO");
	
	private static final Map<Long, EDiasSemana> lookup      = new HashMap<>();	
	private static final Map<String, EDiasSemana> lookupName= new HashMap<>();	
	private String clave;
	private String nombre;			

	private EDiasSemana(String clave, String nombre) {
		this.clave = clave;
		this.nombre= nombre;
	}	

	static {
    for (EDiasSemana item: EnumSet.allOf(EDiasSemana.class)) {
      lookup.put(item.getKey(), item);    
      lookupName.put(item.getNombre(), item);    
		} //for
  } // static
	
	public Long getKey(){
		return this.ordinal() + 1L;
	} // getKey

	public String getClave() {
		return clave;
	}

	public String getNombre() {
		return nombre;
	}	
		
	public static EDiasSemana fromId(Long id) {
    return lookup.get(id);
  } // fromIdEstatusEgreso	
	
	public static EDiasSemana fromName(String name) {
    return lookupName.get(name);
  } // fromIdEstatusEgreso	
  
}