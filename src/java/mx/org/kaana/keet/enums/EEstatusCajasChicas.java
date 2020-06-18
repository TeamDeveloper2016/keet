package mx.org.kaana.keet.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EEstatusCajasChicas {
	
	INICIADO    ("INICIADO"     , "circulo-azul"    , "blue"),
	PARCIALIZADO("PARCIALIZADO" , "circulo-amarillo", "yellow"),
	TERMINADO   ("TERMINADO"    , "circulo-verde"   , "green"),
	CANCELADO   ("CANCELADO"    , "circulo-rojo"    , "red");	
	
	private static final Map<Long, EEstatusCajasChicas> lookup= new HashMap<>();		
	private String nombre;	
	private String semaforo;	
	private String color;	
	
	static {
    for (EEstatusCajasChicas item: EnumSet.allOf(EEstatusCajasChicas.class)) 
      lookup.put(item.getKey(), item);    
  } // static
	
	private EEstatusCajasChicas(String nombre, String semaforo, String color) {
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
	
	public static EEstatusCajasChicas fromId(Long id) {
    return lookup.get(id);
  } // fromId	
}