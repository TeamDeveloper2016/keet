package mx.org.kaana.sakbe.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ESuministrosEstatus {
	
	ELABORADO  ("ELABORADO", "circulo-azul", "blue"),
	ACEPTADO   ("ACEPTADO",  "circulo-verde","green"),
	TERMINADO  ("TERMINADO", "circulo-lila", "purple"),
	CANCELADO  ("CANCELADO", "circulo-rojo", "red");
	
	private static final Map<Long, ESuministrosEstatus> lookup= new HashMap<>();		
	private String nombre;	
	private String semaforo;	
	private String color;	
	
	static {
    for (ESuministrosEstatus item: EnumSet.allOf(ESuministrosEstatus.class)) 
      lookup.put(item.getKey(), item);    
  } // static
	
	private ESuministrosEstatus(String nombre, String semaforo, String color) {
		this.nombre  = nombre;
		this.semaforo= semaforo;
		this.color   = color;
	}	
	
	public Long getKey() {
		return this.ordinal() + 1L;
	} // getKey

	public String getNombre() {
		return nombre;
	}		

	public String getSemaforo() {
		return semaforo;
	}
	
	public String getColor() {
		return color;
	}
	
	public static ESuministrosEstatus fromId(Long id) {
    return lookup.get(id);
  } // fromId
  
}