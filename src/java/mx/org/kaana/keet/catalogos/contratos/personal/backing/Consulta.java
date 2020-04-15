package mx.org.kaana.keet.catalogos.contratos.personal.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.comun.Catalogos;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UISelectEntity;

@Named(value = "keetCatalogosContratosPersonalConsulta")
@ViewScoped
public class Consulta extends IBaseFilter implements Serializable {

  private static final long serialVersionUID= 8793667741599428879L;		
	protected FormatLazyModel totales;	

	public FormatLazyModel getTotales() {
		return totales;
	}

	public void setTotales(FormatLazyModel totales) {
		this.totales=totales;
	}
	
	@PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());						
			this.loadCatalogos();
			this.doLoad();
			this.toTotales();
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
  
	private void loadCatalogos() throws Exception {
		Catalogos.toLoadDepartamentos(this.attrs);
		Catalogos.toLoadPuestos(this.attrs);
		Catalogos.toLoadContratistas(this.attrs);
	} // loadCatalogos	
	
	@Override
  public void doLoad() {   
		Map<String, Object>params= null;
		List<Columna>columns     = null;		
    try {			
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, this.toPrepare());
			columns= new ArrayList<>();
			columns.add(new Columna("nombreCompleto", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("departamento", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("puesto", EFormatoDinamicos.MAYUSCULAS));
			this.lazyModel= new FormatLazyModel("VistaContratosDto", "consulta", params, columns);
			UIBackingUtilities.resetDataTable("tabla");	
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch    
		finally{
			this.attrs.put("controlBuqueda", Boolean.TRUE);
		} // finally
  } // doLoad		
	
  private void toTotales() {   
		Map<String, Object>params= null;
		List<Columna>columns     = null;		
    try {			
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			columns= new ArrayList<>();
			columns.add(new Columna("total", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
			columns.add(new Columna("activos", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
			columns.add(new Columna("noActivos", EFormatoDinamicos.NUMERO_SIN_DECIMALES));
			this.totales= new FormatLazyModel("VistaContratosDto", "totales", params, columns);
			UIBackingUtilities.resetDataTable("totales");			
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch    
  } // doLoad		
	
	private String toPrepare() {
		StringBuilder sb= new StringBuilder();
		if(this.attrs.get("idPuesto")!= null && !Cadena.isVacio(this.attrs.get("idPuesto")) && Long.valueOf(this.attrs.get("idPuesto").toString())>= 1L)
			sb.append("tc_mantic_puestos.id_puesto=").append(this.attrs.get("idPuesto")).append(" and ");
		if(this.attrs.get("idDepartamento")!= null && !Cadena.isVacio(this.attrs.get("idDepartamento")) && Long.valueOf(this.attrs.get("idDepartamento").toString())>= 1L)
			sb.append("tc_keet_departamentos.id_departamento=").append(this.attrs.get("idDepartamento")).append(" and ");
		if(this.attrs.get("idContratista")!= null && !Cadena.isVacio(this.attrs.get("idContratista")) && ((UISelectEntity)this.attrs.get("idContratista")).getKey() >= 1L)			
			if(((UISelectEntity)this.attrs.get("idContratista")).getKey()== 999L)		
				sb.append("tr_mantic_empresa_personal.id_contratista is null and ");
			else
				sb.append("tr_mantic_empresa_personal.id_contratista=").append(this.attrs.get("idContratista")).append(" and ");
		if(this.attrs.get("nombre")!= null && !Cadena.isVacio(this.attrs.get("nombre"))) {
			String nombre= ((String)this.attrs.get("nombre")).toUpperCase().replaceAll("(,| |\\t)+", ".*.*");
  		sb.append("(upper(concat(tc_mantic_personas.nombres, ' ', tc_mantic_personas.paterno, ' ', tc_mantic_personas.materno)) regexp '.*").append(nombre).append(".*') and ");
		} // if
		return Cadena.isVacio(sb)? Constantes.SQL_VERDADERO: sb.substring(0, sb.length()- 4);
	} // toPrepare
	
	public String toLegend(Integer index, Entity row) {
		String regresar= row.toString("desarrollo");
	  if(index!= 0)
			if(Cadena.isVacio(row.toLong("idEmpresa")))
				regresar= "TOTAL GENERAL";
		  else
				if(Objects.equals(regresar, "SIN DESARROLLO"))
				  regresar= "TOTAL EMPRESA";
		return regresar;
	}
	
}