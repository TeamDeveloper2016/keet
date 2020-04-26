package mx.org.kaana.keet.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EEstacionesEstatus {
	
	INICIAR    ("INICIAR"   , "circulo-rojo"),
	EN_PROCESO ("EN PROCESO", "circulo-amarilla"),
	TERMINADO  ("TERMINADO" , "circulo-verde"),
	CANCELADO  ("CANCELADO" , "circulo-naranja");	
	
	private static final Map<Long, EEstacionesEstatus> lookup= new HashMap<>();		
	private String nombre;	
	private String semaforo;	
	
	static {
    for (EEstacionesEstatus item: EnumSet.allOf(EEstacionesEstatus.class)) 
      lookup.put(item.getKey(), item);    
  } // static
	
	private EEstacionesEstatus(String nombre, String semaforo) {
		this.nombre  = nombre;
		this.semaforo= semaforo;
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
	
	public static EEstacionesEstatus fromId(Long id) {
    return lookup.get(id);
  } // fromIdEstatusEgreso	
}