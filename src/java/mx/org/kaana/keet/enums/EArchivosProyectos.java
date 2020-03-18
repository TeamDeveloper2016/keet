package mx.org.kaana.keet.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EArchivosProyectos {
	
	DOCUMENTOS   ("VistaProyectosArchivosDto"    , "proyectos"),
	GENERADORES  ("VistaProyectosGeneradoresDto" , "generadores"),
	PRESUPUESTOS ("VistaProyectosPresupuestosDto", "contratos");
	
	private static final Map<Long, EArchivosProyectos> lookup= new HashMap<>();	
	private String unit;
	private String idXml;	
	private String path;	

	private EArchivosProyectos(String unit, String path) {
		this(unit, path, "getImportados");
	}
	
	private EArchivosProyectos(String unit, String path, String idXml) {				
		this.unit = unit;
		this.idXml= idXml;
		this.path = path;
	} // EArchivosProyectos

	static {
    for (EArchivosProyectos item: EnumSet.allOf(EArchivosProyectos.class)) 
      lookup.put(item.getKey(), item);    
  } // static
	
	public Long getKey(){
		return this.ordinal() + 1L;
	} // getKey

	public String getUnit() {
		return unit;
	}	
	
	public String getIdXml() {
		return idXml;
	}	

	public String getPath() {
		return path;
	}
		
	public static EArchivosProyectos fromId(Long id) {
    return lookup.get(id);
  } // fromIdEstatusEgreso	
}