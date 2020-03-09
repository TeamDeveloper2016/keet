package mx.org.kaana.keet.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.keet.db.dto.TcKeetDepartamentosDto;
import mx.org.kaana.keet.db.dto.TcKeetDivisionesDto;
import mx.org.kaana.keet.db.dto.TcKeetEspecialidadesDto;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.db.dto.TcKeetFamiliasDto;
import mx.org.kaana.keet.db.dto.TcKeetGruposConstructivosDto;
import mx.org.kaana.keet.db.dto.TcKeetTiposAtributosDto;
import mx.org.kaana.keet.db.dto.TcKeetTiposBancariosDto;
import mx.org.kaana.keet.db.dto.TcKeetTiposConceptosDto;
import mx.org.kaana.keet.db.dto.TcKeetTiposFachadasDto;
import mx.org.kaana.keet.db.dto.TcKeetTiposInfraestructurasDto;
import mx.org.kaana.keet.db.dto.TcKeetTiposNominasDto;
import mx.org.kaana.keet.db.dto.TcKeetTiposParentescosDto;
import mx.org.kaana.keet.db.dto.TcKeetTiposPresupuestosDto;
import mx.org.kaana.keet.db.dto.TcKeetTiposRelacionesDto;
import mx.org.kaana.keet.db.dto.TcKeetTiposViviendasDto;
import mx.org.kaana.mantic.db.dto.TcManticCierresEstatusDto;

public enum ECatalogosDinamicos {
	
	CIERRES_ESTATUS (TcManticCierresEstatusDto.class     , "Mantenimiento a estatus de cierres"       , true , false, "121c242736c542db7a8dd01563e47387", "dinamico"), // 7ce4	
	DEPARTAMENTOS   (TcKeetDepartamentosDto.class        , "Mantenimiento a departamentos"            , false, false, "aaa7adb3bb43de6efa72f075f116"),
	DIVISIONES      (TcKeetDivisionesDto.class           , "Mantenimiento a divisiones"               , false, false, "cd5be572f4050718275e81"),
	ESPECIALIDADES  (TcKeetEspecialidadesDto.class       , "Mantenimiento a especialidades"           , false, true , "2e3acc53d756d656e31f53a4dd708a"),
	ESTACIONES			(TcKeetEstacionesDto.class           , "Mantenimiento a estaciones"               , false, true, "d251e574fa73f50b186ff2"),
	FAMILIAS				(TcKeetFamiliasDto.class             , "Mantenimiento a familias"                 , false, false, "bf4fd151d56ff17d91"),
	CONSTRUCTIVOS   (TcKeetGruposConstructivosDto.class  , "Mantenimiento a constructivos"            , false, true , "848e9ca4b840db7f83e51f45a1a7"),
	ATRIBUTOS				(TcKeetTiposAtributosDto.class       , "Mantenimiento a atributos"                , false, false, "74fc102c3a31cf6af87e"),
	BANCARIOS				(TcKeetTiposBancariosDto.class       , "Mantenimiento a tipos bancarios"          , false, false, "9c9799a9a3a9b2b544a5"),
	CONCEPTOS				(TcKeetTiposConceptosDto.class       , "Mantenimiento a tipos conceptos"          , false, false, "4cc657eb60e8708a98d9"),
	FACHADAS				(TcKeetTiposFachadasDto.class        , "Mantenimiento a tipos fachadas"           , false, false, "9daaaaa3a4a8a5a8ba"),
	INFRAESTRUCTURAS(TcKeetTiposInfraestructurasDto.class, "Mantenimiento a tipos de infraestructuras", false, false, "67f909011c101c2134b43a4d94ba44c456"),
	NOMINAS					(TcKeetTiposNominasDto.class         , "Mantenimiento a tipos de nominas"         , false, false, "64fb0a192d363edf"),
	PARENTESCOS			(TcKeetTiposParentescosDto.class     , "Mantenimiento a tipos parentescos"        , false, false, "021b1d21293ac743d72948ab"),
	PRESUPUESTOS		(TcKeetTiposPresupuestosDto.class    , "Mantenimiento a tipos presupuestos"       , false, false, "a2bb4fda61fb051418599ddc05"),
	RELACIONES			(TcKeetTiposRelacionesDto.class      , "Mantenimiento a tipos relaciones"         , false, false, "2ec94ed459d152ef7ff67a"),
	VIVIENDAS				(TcKeetTiposViviendasDto.class       , "Mantenimiento a tipos de viviendas"       , false, false, "c353dd7a8c9ca3a9abcc");
	
	private static final Map<Long, ECatalogosDinamicos> lookup= new HashMap<>();
	private Class clase;
	private String titulo;
	private Boolean estatus;
	private Boolean clave;
	private String cifrado;
	private String idXml;

	private ECatalogosDinamicos(Class clase, String titulo, Boolean estatus, Boolean clave, String cifrado) {
		this(clase, titulo, estatus, clave, cifrado, "row");
	}
	
	private ECatalogosDinamicos(Class clase, String titulo, Boolean estatus, Boolean clave, String cifrado, String idXml) {
		this.clase  = clase;
		this.titulo = titulo;
		this.estatus= estatus;
		this.clave  = clave;
		this.cifrado= cifrado;
		this.idXml  = idXml;
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

	public Boolean getClave() {
		return clave;
	}	
	
	public String getRuta(){
		return "/Paginas/Keet/Catalogos/Dinamicos/filtro.jsf?unit=".concat(getCifrado());
	}

	public String getIdXml() {
		return idXml;
	}	
	
	public static ECatalogosDinamicos fromId(Long id) {
    return lookup.get(id);
  } // fromIdEstatusEgreso	
}