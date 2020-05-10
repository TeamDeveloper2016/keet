package mx.org.kaana.keet.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EEstacionesEstatus {
	
	INICIAR    ("INICIAR"   , "circulo-rojo"    , "red"),
	EN_PROCESO ("EN PROCESO", "circulo-amarillo", "yellow"),
	TERMINADO  ("TERMINADO" , "circulo-verde"   , "green"),
	CANCELADO  ("CANCELADO" , "circulo-naranja" , "orange");	
	
	private static final Map<Long, EEstacionesEstatus> lookup= new HashMap<>();		
	private String nombre;	
	private String semaforo;	
	private String color;	
	
	static {
    for (EEstacionesEstatus item: EnumSet.allOf(EEstacionesEstatus.class)) 
      lookup.put(item.getKey(), item);    
  } // static
	
	private EEstacionesEstatus(String nombre, String semaforo, String color) {
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
	
	public static EEstacionesEstatus fromId(Long id) {
    return lookup.get(id);
  } // fromIdEstatusEgreso	
}