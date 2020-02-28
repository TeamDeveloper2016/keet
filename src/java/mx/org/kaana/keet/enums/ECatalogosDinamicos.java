package mx.org.kaana.keet.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.mantic.db.dto.TcManticCierresEstatusDto;

public enum ECatalogosDinamicos {

	CIERRES_ESTATUS (TcManticCierresEstatusDto.class, "Mantenimiento a estatus de cierres", true, "121c242736c542db7a8dd01563e47387"); // 7ce4	
	
	private static final Map<Long, ECatalogosDinamicos> lookup= new HashMap<>();
	private Class clase;
	private String titulo;
	private Boolean estatus;
	private String cifrado;

	private ECatalogosDinamicos(Class clase, String titulo, Boolean estatus, String cifrado) {
		this.clase  = clase;
		this.titulo = titulo;
		this.estatus= estatus;
		this.cifrado= cifrado;
	} // ECatalogosDinamicos

	static {
    for (ECatalogosDinamicos item: EnumSet.allOf(ECatalogosDinamicos.class)) 
      lookup.put(item.getKey(), item);    
  } // static
	
	public Long getKey(){
		return this.ordinal() + 1L;
	} // getKey

	public Class getClase() {
		return clase;
	}	// getPaquete

	public String getTitulo() {
		return titulo;
	} // getTitulo

	public Boolean getEstatus() {
		return estatus;
	} // getEstatus

	public String getCifrado() {
		return cifrado;
	}	// getCifrado
	
	public static ECatalogosDinamicos fromId(Long id) {
    return lookup.get(id);
  } // fromIdEstatusEgreso	
}