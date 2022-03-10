package mx.org.kaana.kajool.procesos.utilerias.usuariosenlinea.backing;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.personas.reglas.Gestor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Named(value = "kajoolMantenimientoUtileriasUsuarioAccesos")
@ViewScoped
public class Accesos extends IBaseFilter implements Serializable {

	private static final Log LOG              = LogFactory.getLog(Accesos.class);
  private static final long serialVersionUID= 8793667741599428332L;
	private LocalDate fechaInicio;
	private LocalDate fechaFin;

  public LocalDate getFechaInicio() {
    return fechaInicio;
  }

  public void setFechaInicio(LocalDate fechaInicio) {
    this.fechaInicio = fechaInicio;
  }

  public LocalDate getFechaFin() {
    return fechaFin;
  }

  public void setFechaFin(LocalDate fechaFin) {
    this.fechaFin = fechaFin;
  }

  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			this.attrs.put("departamento", new String[] {});
			this.attrs.put("puesto", new String[] {});
			this.toLoadCatalog();
      this.toLoadTiposPersonas();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
 
  @Override
  public void doLoad() {
    List<Columna> columns     = null;
		Map<String, Object> params= this.toPrepare();
    try {
      params.put("sortOrder", "order by tc_mantic_empresas.id_empresa, tc_janal_sesiones.cuenta, tc_janal_sesiones.inicio desc");
      columns = new ArrayList<>();
      columns.add(new Columna("nombreEmpresa", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cuenta", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("usuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("inicio", EFormatoDinamicos.FECHA_HORA));      
      columns.add(new Columna("termino", EFormatoDinamicos.FECHA_HORA));      
      this.lazyModel = new FormatCustomLazy("VistaAccesosDto", params, columns);
      UIBackingUtilities.resetDataTable();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
      Methods.clean(columns);
    } // finally		
  } // doLoad

	private Map<String, Object> toPrepare() {
	  Map<String, Object> regresar= new HashMap<>();	
		StringBuilder sb= new StringBuilder();
		String[] puestos= (String[]) this.attrs.get("puesto");
		String[] departamentos= (String[]) this.attrs.get("departamento");
		if(!Cadena.isVacio(this.attrs.get("nombre"))) {
			String nombre= ((String)this.attrs.get("nombre")).toUpperCase().replaceAll("(,| |\\t)+", ".*.*");
  		sb.append("((upper(concat(tc_mantic_personas.nombres, ' ', tc_mantic_personas.paterno, ' ', tc_mantic_personas.materno)) regexp '.*").append(nombre).append(".*') or (upper(tc_mantic_personas.apodo)= '").append(nombre).append("')) and ");
		} // if
		if(!Cadena.isVacio(this.attrs.get("cuenta")))
  		sb.append("(upper(tc_janal_sesiones.cuenta) like '%").append(this.attrs.get("cuenta")).append("%') and ");
		if(!Cadena.isVacio(this.fechaInicio))
		  sb.append("(date_format(tc_janal_sesiones.registro, '%Y%m%d')>= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, this.fechaInicio)).append("') and ");	
		if(!Cadena.isVacio(this.fechaFin))
		  sb.append("(date_format(tc_janal_sesiones.registro, '%Y%m%d')<= '").append(Fecha.formatear(Fecha.FECHA_ESTANDAR, this.fechaFin)).append("') and ");	
		if(departamentos.length > 0) {
			String allDepartametos= "";
			for(String departamento: departamentos)
				allDepartametos= allDepartametos.concat(departamento).concat(",");
			sb.append("tc_keet_contratistas_departamentos.id_departamento in (").append(allDepartametos.substring(0, allDepartametos.length()-1)).append(") and ");
		} // if		
		if(puestos.length > 0) {
			String allPuestos= "";
			for(String puesto: puestos)
				allPuestos= allPuestos.concat(puesto).concat(",");
			sb.append("tr_mantic_empresa_personal.id_puesto in (").append(allPuestos.substring(0, allPuestos.length()-1)).append(") and ");
		} // if
		if(!Cadena.isVacio(this.attrs.get("idEmpresa")) && !this.attrs.get("idEmpresa").toString().equals("-1"))
		  regresar.put("idEmpresa", this.attrs.get("idEmpresa"));
		else
		  regresar.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getSucursales());
		if(sb.length()== 0)
		  regresar.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
		else	
		  regresar.put(Constantes.SQL_CONDICION, sb.substring(0, sb.length()- 4));
		return regresar;		
	}
	
	private void toLoadCatalog() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
			if(JsfBase.getAutentifica().getEmpresa().isMatriz())
        params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			else
				params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      this.attrs.put("sucursales", (List<UISelectEntity>) UIEntity.seleccione("TcManticEmpresasDto", "empresas", params, columns, "clave"));			
			this.attrs.put("idEmpresa", this.toDefaultSucursal((List<UISelectEntity>)this.attrs.get("sucursales")));
    } // try
    catch (Exception e) {
      throw e;
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    }// finally
	}
	
	protected void toLoadTiposPersonas() throws Exception {
    Gestor gestor = new Gestor();
    gestor.loadTiposPersonas();
    this.attrs.put("puestos", gestor.loadPuestosSimple());
    this.attrs.put("departamentos", gestor.loadDepartamentosSimple());
  } // loadTiposPersonas  
  
  public Long getMinutos(Entity row) {
		Long start= row.toTimestamp("comienza").atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		Long end  = row.toTimestamp("termina").atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    return Fecha.diferenciaMinutos(start, end);
  }	
	
}
