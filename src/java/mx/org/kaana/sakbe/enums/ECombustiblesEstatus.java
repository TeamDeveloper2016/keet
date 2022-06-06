package mx.org.kaana.sakbe.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ECombustiblesEstatus {
	
	ELABORADO  ("ELABORADO", "circulo-azul",    "blue"),
	ACEPTADO   ("ACEPTADO",  "circulo-verde" ,  "green"),
	EN_PROCESO ("EN PROCESO","circulo-amarillo","yellow"),
	TERMINADO  ("TERMINADO", "circulo-lila",    "purple"),
	CANCELADO  ("CANCELADO", "circulo-rojo",    "red"),	
	ELIMINADO  ("ELIMINADO", "circulo-gris",    "gray"),
	SALDADO    ("SALDADO",   "circulo-cafe",    "brown");	
	
	private static final Map<Long, ECombustiblesEstatus> lookup= new HashMap<>();		
	private String nombre;	
	private String semaforo;	
	private String color;	
	
	static {
    for (ECombustiblesEstatus item: EnumSet.allOf(ECombustiblesEstatus.class)) 
      lookup.put(item.getKey(), item);    
  } // static
	
	private ECombustiblesEstatus(String nombre, String semaforo, String color) {
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
	
	public static ECombustiblesEstatus fromId(Long id) {
    return lookup.get(id);
  } // fromId
  
}