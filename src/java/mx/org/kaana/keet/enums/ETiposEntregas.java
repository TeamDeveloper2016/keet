package mx.org.kaana.keet.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ETiposEntregas {
	
	COMPLETO ("COMPLETO"),
	MENOS    ("MENOS"),
	MAS      ("MAS"),
	NINGUNO  ("NINGUNO");
	
	private static final Map<Long, ETiposEntregas> lookup= new HashMap<>();		
	private String nombre;	
	
	static {
    for (ETiposEntregas item: EnumSet.allOf(ETiposEntregas.class)) 
      lookup.put(item.getKey(), item);    
  } // static
	
	private ETiposEntregas(String nombre) {
		this.nombre= nombre;
	}	
	
	public Long getKey(){
		return this.ordinal()+ 1L;
	} // getKey

	public String getNombre() {
		return nombre;
	}		
	
	public static ETiposEntregas fromId(Long id) {
    return lookup.get(id);
  } // fromId	
}