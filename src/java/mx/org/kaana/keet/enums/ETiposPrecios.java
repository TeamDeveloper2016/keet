package mx.org.kaana.keet.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum ETiposPrecios {
	
	CONVENIO ("CONVENIO"),
	BASE     ("BASE"),
	LISTA    ("LISTA"),
	ESPECIAL ("ESPECIAL"),
	COMPRA   ("COMPRA"),
	LIBRE    ("LIBRE");
	
	private static final Map<Long, ETiposPrecios> lookup= new HashMap<>();		
	private String nombre;	
	
	static {
    for (ETiposPrecios item: EnumSet.allOf(ETiposPrecios.class)) 
      lookup.put(item.getKey(), item);    
  } // static
	
	private ETiposPrecios(String nombre) {
		this.nombre= nombre;
	}	
	
	public Long getKey(){
		return this.ordinal()+ 1L;
	} // getKey

	public String getNombre() {
		return nombre;
	}		
	
	public static ETiposPrecios fromId(Long id) {
    return lookup.get(id);
  } // fromId	
}