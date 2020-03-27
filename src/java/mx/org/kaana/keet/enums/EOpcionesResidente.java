package mx.org.kaana.keet.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EOpcionesResidente {
	
	EMPLEADOS   ("Asignación de personal a los desarrollos", "818f9da3a0a8a0aab83e"    , "/Paginas/Keet/Catalogos/Contratos/Personal/registro.jsf"),
	INCIDENCIAS ("Registro de incidencias del personal"    , "3bcd5dee77f6758d91e81a5d", "/Paginas/Keet/Catalogos/Contratos/Personal/incidencias.jsf");
	
	private static final Map<Long, EOpcionesResidente> lookup= new HashMap<>();	
	private String titulo;
	private String cifrado;
	private String ruta;
	
	private EOpcionesResidente(String titulo, String cifrado, String ruta) {		
		this.titulo = titulo;
		this.cifrado= cifrado;		
		this.ruta   = ruta;		
	} // EOpcionesResidente

	static {
    for (EOpcionesResidente item: EnumSet.allOf(EOpcionesResidente.class)) 
      lookup.put(item.getKey(), item);    
  } // static
	
	public Long getKey(){
		return this.ordinal() + 1L;
	} // getKey	

	public String getTitulo() {
		return titulo;
	} // getTitulo

	public String getCifrado() {
		return cifrado;
	}	// getCifrado
	
	public String getRetorno(){
		return "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=".concat(getCifrado());
	}
	
	public String getRuta(){
		return ruta;
	}
	
	public static EOpcionesResidente fromId(Long id) {
    return lookup.get(id);
  } // fromIdEstatusEgreso	
}