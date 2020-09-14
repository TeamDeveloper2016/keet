package mx.org.kaana.mantic.enums;

import mx.org.kaana.kajool.procesos.reportes.reglas.IExportacionXls;

public enum EExportacionXls implements IExportacionXls {

	ARTICULOS   ("VistaArticulosDto", "exportar", "Articulos", "/Paginas/Mantic/Catalogos/Articulos/filtro", ""),
	CONTEOS     ("VistaArticulosDto", "conteo", "Conteos", "/Paginas/Mantic/Inventarios/Almacenes/filtro", ""),
	PERSONAS    ("VistaPersonasDto", "exportar", "Pesonal", "/Paginas/Mantic/Catalogos/Empleados/filtro", ""),
	ESTACIONES  ("VistaContratosLotesDto", "exportar", "Estaciones", "/Paginas/Keet/Estaciones/Masivos/importar", ""),
	INCIDENCIAS ("VistaIncidentesDto", "principal", "Incidencias", "/Paginas/Keet/Catalogos/Contratos/Personal/exportar", ""),
	NOMINA      ("VistaNominaDto", "detalle", "Nomina", "/Paginas/Keet/Nominas/filtro", ""),
	NOMINA_PERSONA   ("VistaNominaConsultasDto", "persona", "Personas", "/Paginas/Keet/Nominas/personas", ""),
	DESTAJO_PERSONA  ("VistaNominaConsultasDto", "destajo", "Contratista", "/Paginas/Keet/Nominas/personas", ""),
	DESTAJO_PROVEEDOR("VistaNominaConsultasDto", "proveedor", "SubContratista", "/Paginas/Keet/Nominas/proveedores", ""),
	MATERIALES       ("VistaContratosLotesMaterialesDto", "exportar", "Materiales", "/Paginas/Keet/Estaciones/Masivos/importar", ""),
	PRECIOS          ("VistaComprasAlmacenDto", "precios", "Precios", "/Paginas/Keet/Estaciones/Masivos/importar", ""),
	PRECIOS_CONVENIO ("VistaComprasAlmacenDto", "convenios", "PreciosConvenio", "/Paginas/Keet/Estaciones/Masivos/importar", ""),
	CONTROLES  ("VistaControlesLotesDto", "exportar", "Controles", "/Paginas/Keet/Estaciones/Masivos/importar", "");
  
  private final String proceso;
  private final String idXml;
  private final String nombreArchivo;
  private final String paginaRegreso;
  private final String patron;

  private EExportacionXls(String proceso, String idXml, String nombreArchivo, String paginaRegreso, String patron) {
    this.proceso      = proceso;
    this.idXml        = idXml;
    this.nombreArchivo= nombreArchivo;
    this.paginaRegreso= paginaRegreso;
    this.patron       = patron;
  }

  @Override
  public String getProceso() {
    return proceso;
  }

  @Override
  public String getIdXml() {
    return idXml;
  }

  @Override
  public String getNombreArchivo() {
    return nombreArchivo;
  }

  @Override
  public String getPaginaRegreso() {
    return paginaRegreso;
  }

	@Override
  public String getPatron() {
    return patron;
  }	
}
