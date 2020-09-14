package mx.org.kaana.keet.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EOpcionesResidente {
	
	EMPLEADOS       ("Asignación de empleados a los desarrollos", "818f9da3a0a8a0aab83e", "/Paginas/Keet/Catalogos/Contratos/Personal/registro.jsf"),
	INCIDENCIAS     ("Registro de incidencias de empleados", "3bcd5dee77f6758d91e81a5d", "/Paginas/Keet/Catalogos/Contratos/Personal/empleados.jsf"),
	DIAS_FERIADOS   ("Registro de dias feriados de empleados", "4cd962ed0e202f3acd2556aacc73", "/Paginas/Keet/Catalogos/Contratos/Personal/empleados.jsf"),
	DESTAJOS        ("Registrar un avance para el destajo realizado por los contratistas o subcontratistas", "e864e872898798a9bd", "/Paginas/Keet/Catalogos/Contratos/Destajos/filtro.jsf"),
	GEOREFERENCIA   ("Georeferencia", "21212738c546c545d62e4cbf3c2e", "", "/Paginas/Keet/Catalogos/Contratos/georeferencia.jsf"),
	VALES           ("Generación de vales", "708f919397a7", "/Paginas/Keet/Catalogos/Contratos/Vales/Normales/filtro.jsf"),
	VALES_ESPECIALES("Generación de vales especiales", "051212101a203635c9285d90ec7d808499", "/Paginas/Keet/Catalogos/Contratos/Vales/Especiales/filtro.jsf"),
	REGISTRO_GASTO  ("Registrar un gasto de caja chica", "223dc345c158e57684d1295a98bf46", "/Paginas/Keet/CajaChica/accion.jsf"),
	CONSULTA_GASTO  ("Consultar gastos generados", "b0ba4bd764f408121243bacb2a2d35", "/Paginas/Keet/CajaChica/consulta.jsf"),
	//CAJA
	ABONO_CAJA_CHICA("Abono a caja chica", "afb9ba44dd60f674f40d7eed041f212528", "/Paginas/Keet/CajaChica/abonar.jsf"),
	//ALMACENISTA	
	MATERIALES      ("Entrega de materiales", "c95ede7d8d92929da8df03", "/Paginas/Keet/Catalogos/Contratos/Materiales/filtro.jsf"),
	AUTORIZACION    ("Autorización de vales especiales", "e661f7021e3dcf64e41a5392d5", "/Paginas/Keet/Catalogos/Contratos/Vales/Autorizacion/filtro.jsf"),
	CONTROLES       ("Registrar un avance en el seguimiento a los lotes por los residentes de obra", "ec798999a2b940c249ae", "/Paginas/Keet/Controles/control.jsf");
	
	private static final Map<Long, EOpcionesResidente> lookup= new HashMap<>();	
	private String titulo;
	private String cifrado;
	private String ruta;
	private String retorno;
	
	private EOpcionesResidente(String titulo, String cifrado, String ruta) {		
		this(titulo, cifrado, ruta, "/Paginas/Keet/Catalogos/Contratos/Personal/filtro.jsf?opcion=".concat(cifrado));
	}
	
	private EOpcionesResidente(String titulo, String cifrado, String ruta, String retorno) {		
		this.titulo = titulo;
		this.cifrado= cifrado;		
		this.ruta   = ruta;		
		this.retorno= retorno;
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
		return retorno;
	}
	
	public String getRuta(){
		return ruta;
	}
	
	public static EOpcionesResidente fromId(Long id) {
    return lookup.get(id);
  } // fromIdEstatusEgreso	
  
}