package mx.org.kaana.keet.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EEstatusVales {
	
	DISPONIBLE ("DISPONIBLE", "circulo-rojo"    , "red"),
	RECHAZADO  ("RECHAZADO" , "circulo-naranja" , "orange"),
	ENTREGADO  ("ENTREGADO" , "circulo-verde"   , "green"),
	CANCELADO  ("CANCELADO" , "circulo-rojo"    , "red"),
	INCOMPLETO ("INCOMPLETO", "circulo-amarillo", "yellow"),
	TERMINADO  ("TERMINADO" , "circulo-verde"   , "green"),
	PENDIENTE  ("TERMINADO" , "circulo-rojo"    , "red");	
	
	private static final Map<Long, EEstatusVales> lookup= new HashMap<>();		
	private String nombre;	
	private String semaforo;	
	private String color;	
	
	static {
    for (EEstatusVales item: EnumSet.allOf(EEstatusVales.class)) 
      lookup.put(item.getKey(), item);    
  } // static
	
	private EEstatusVales(String nombre, String semaforo, String color) {
		this.nombre  = nombre;
		this.semaforo= semaforo;
		this.color   = color;
	}	
	
	public Long getKey(){
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
	
	public static EEstatusVales fromId(Long id) {
    return lookup.get(id);
  } // fromId	
}