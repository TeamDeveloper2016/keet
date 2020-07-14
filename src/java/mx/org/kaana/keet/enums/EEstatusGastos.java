package mx.org.kaana.keet.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EEstatusGastos {
	
	DISPONIBLE ("DISPONIBLE", "circulo-amarillo", "yellow"),
	ACEPTADO   ("ACEPTADO"  , "circulo-verde"   , "green"),	
	CANCELADO  ("CANCELADO" , "circulo-rojo"    , "red"),
	APLICADO   ("APLICADO"  , "circulo-azul"    , "blue");	
	
	private static final Map<Long, EEstatusGastos> lookup= new HashMap<>();		
	private String nombre;	
	private String semaforo;	
	private String color;	
	
	static {
    for (EEstatusGastos item: EnumSet.allOf(EEstatusGastos.class)) 
      lookup.put(item.getKey(), item);    
  } // static
	
	private EEstatusGastos(String nombre, String semaforo, String color) {
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
	
	public static EEstatusGastos fromId(Long id) {
    return lookup.get(id);
  } // fromId	
}