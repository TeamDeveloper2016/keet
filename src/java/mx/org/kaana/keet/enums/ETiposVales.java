package mx.org.kaana.keet.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ETiposVales {
	
	NORMAL    ("NORMAL"),
	ROBO      ("ROBO"),
	REPOSICION("REPOSICION");
	
	private static final Map<Long, ETiposVales> lookup= new HashMap<>();		
	private String nombre;	
	
	static {
    for (ETiposVales item: EnumSet.allOf(ETiposVales.class)) 
      lookup.put(item.getKey(), item);    
  } // static
	
	private ETiposVales(String nombre) {
		this.nombre= nombre;
	}	
	
	public Long getKey(){
		return this.ordinal()+ 1L;
	} // getKey

	public String getNombre() {
		return nombre;
	}		
	
	public static ETiposVales fromId(Long id) {
    return lookup.get(id);
  } // fromId	
}