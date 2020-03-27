package mx.org.kaana.keet.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EArchivosContratos {
	
	DOCUMENTOS   ("VistaContratosArchivosDto"    , "proyectos"),
	GENERADORES  ("VistaContratosGeneradoresDto" , "generadores"),
	PRESUPUESTOS ("VistaContratosPresupuestosDto", "contratos");
	
	private static final Map<Long, EArchivosContratos> lookup= new HashMap<>();	
	private String unit;
	private String idXml;	
	private String path;	

	private EArchivosContratos(String unit, String path) {
		this(unit, path, "importados");
	}
	
	private EArchivosContratos(String unit, String path, String idXml) {				
		this.unit = unit;
		this.idXml= idXml;
		this.path = path;
	} // EArchivosContratos

	static {
    for (EArchivosContratos item: EnumSet.allOf(EArchivosContratos.class)) 
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
		
	public static EArchivosContratos fromId(Long id) {
    return lookup.get(id);
  } // fromIdEstatusEgreso	
}