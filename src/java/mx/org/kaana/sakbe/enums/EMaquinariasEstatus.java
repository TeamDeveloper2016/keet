package mx.org.kaana.sakbe.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EMaquinariasEstatus {
	
	EN_SERVICIO     ("EN SERVICIO",      "circulo-azul",    "blue"),
	EN_MANTENIMIENTO("EN MANTENIMIENTO", "circulo-verde" ,  "green"),
	EN_REPARACION   ("EN EN_REPARACION", "circulo-amarillo","yellow"),
	BAJA            ("BAJA",             "circulo-lila",    "purple"),
	EN_GARANTIA     ("EN GARANTIA",      "circulo-rojo",    "red"),	
	LIQUIDADO       ("EN PRESTAMO",      "circulo-gris",    "gray"),
	EN_PRESTAMO     ("SALDADO",          "circulo-cafe",    "brown"),	
	ROBADO          ("ROBADO",           "circulo-cafe",    "brown"),	
	VENIDA          ("VENIDA",           "circulo-cafe",    "brown");
 	
	private static final Map<Long, EMaquinariasEstatus> lookup= new HashMap<>();		
	private String nombre;	
	private String semaforo;	
	private String color;	
	
	static {
    for (EMaquinariasEstatus item: EnumSet.allOf(EMaquinariasEstatus.class)) 
      lookup.put(item.getKey(), item);    
  } // static
	
	private EMaquinariasEstatus(String nombre, String semaforo, String color) {
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
	
	public static EMaquinariasEstatus fromId(Long id) {
    return lookup.get(id);
  } // fromId
  
}