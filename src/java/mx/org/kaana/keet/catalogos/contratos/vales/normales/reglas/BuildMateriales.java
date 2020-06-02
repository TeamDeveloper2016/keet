package mx.org.kaana.keet.catalogos.contratos.vales.normales.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.keet.catalogos.contratos.vales.beans.MaterialVale;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.reflection.Methods;

public class BuildMateriales implements Serializable{
	
	private static final long serialVersionUID= -9070658805380011120L;
	private String clave;
	private Long idDepartamento;
	private String nombreConcepto;

	public BuildMateriales(String clave) {
		this(clave, -1L);
	} // BuildMateriales
	
	public BuildMateriales(String clave, Long idDepartamento) {
		this(clave, idDepartamento, "");
	} // BuildMateriales
	
	public BuildMateriales(String clave, Long idDepartamento, String nombreConcepto) {
		this.clave         = clave;
		this.idDepartamento= idDepartamento;
		this.nombreConcepto= nombreConcepto;
	} // BuildMateriales
	
	public List<MaterialVale> toPartidas() throws Exception{
		List<MaterialVale> regresar= null;
		Map<String, Object>params  = null;
		try {
			params= new HashMap<>();
			params.put("clave", this.clave);
			regresar= DaoFactory.getInstance().toEntitySet(MaterialVale.class, "VistaCapturaMaterialesDto", "conceptosPadre", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	} // toPartidas
	
	public List<MaterialVale> toConceptos(String clavePadre) throws Exception{
		List<MaterialVale> regresar= null;
		Map<String, Object>params  = null;
		Estaciones estaciones      = null;
		try {
			estaciones= new Estaciones();
			params= new HashMap<>();
			params.put("clave", estaciones.toKey(clavePadre, 5));
			params.put("idDepartamento", this.idDepartamento);
			params.put("nombreConcepto", this.nombreConcepto);
			params.put("estatus", EEstacionesEstatus.INICIAR.getKey() + "," + EEstacionesEstatus.EN_PROCESO.getKey() + "," + EEstacionesEstatus.TERMINADO.getKey());
			regresar= DaoFactory.getInstance().toEntitySet(MaterialVale.class, "VistaCapturaMaterialesDto", "conceptos", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	}	// toConceptos
	
	public List<MaterialVale> toConceptosRegistrados(Long idVale) throws Exception{
		List<MaterialVale> regresar= null;
		Map<String, Object>params  = null;		
		try {			
			params= new HashMap<>();			
			params.put("idVale", idVale);
			regresar= DaoFactory.getInstance().toEntitySet(MaterialVale.class, "VistaCapturaMaterialesDto", "conceptosRegistrados", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	}	// toConceptos
	
	public List<MaterialVale> toMateriales(String clavePadre, Long nivel) throws Exception{
		return toMateriales(clavePadre, nivel, null);
	}	// toConceptos
	
	public List<MaterialVale> toMateriales(String clavePadre, Long nivel, Long idVale) throws Exception{
		List<MaterialVale> regresar= null;
		Map<String, Object>params  = null;
		Estaciones estaciones      = null;
		try {
			estaciones= new Estaciones();
			params= new HashMap<>();
			params.put("clave", estaciones.toKey(clavePadre, nivel.intValue()));						
			if(idVale!= null){
				params.put("idVale", idVale);			
				regresar= DaoFactory.getInstance().toEntitySet(MaterialVale.class, "VistaCapturaMaterialesDto", "materialesVale", params, Constantes.SQL_TODOS_REGISTROS);
			} // if
			else
				regresar= DaoFactory.getInstance().toEntitySet(MaterialVale.class, "VistaCapturaMaterialesDto", "materiales", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	}	// toConceptos
}