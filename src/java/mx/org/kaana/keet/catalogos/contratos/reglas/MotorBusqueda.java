package mx.org.kaana.keet.catalogos.contratos.reglas;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.catalogos.contratos.beans.Lote;
import mx.org.kaana.keet.catalogos.contratos.beans.Contrato;
import mx.org.kaana.keet.catalogos.contratos.beans.ContratoPersonal;
import mx.org.kaana.keet.catalogos.contratos.contratistas.beans.ContratistaLote;
import mx.org.kaana.keet.db.dto.TcKeetDiasFestivosDto;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.incidentes.beans.Incidente;

public class MotorBusqueda implements Serializable{
	
	private static final long serialVersionUID = -2951697223110542896L;
	private Long idPivote;

	public MotorBusqueda(Long idPivote) {
		this.idPivote= idPivote;
	}
	
	public Contrato toContrato() throws Exception{
		Contrato regresar        = null;
		Map<String, Object>params= null;
		try {
		  params= new HashMap<>();
			params.put("idContrato", this.idPivote);
			regresar= (Contrato) DaoFactory.getInstance().toEntity(Contrato.class, "TcKeetContratosDto", "byId", params);
			if(regresar!= null && regresar.isValid())
				regresar.setIkProyecto(new UISelectEntity(regresar.getIdProyecto()));			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toContrato	
	
	public List<Lote> toLotes() throws Exception{
		List<Lote> regresar      = null;
		Map<String, Object>params= null;
		try {
		  params= new HashMap<>();
			params.put("idContrato", this.idPivote);
			regresar= DaoFactory.getInstance().toEntitySet(Lote.class, "TcKeetContratosLotesDto", "byContrato", params);			
			for(Lote item: regresar)
				item.setAccion(ESql.UPDATE);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toLotes
	
	public List<ContratoPersonal> toPersonas() throws Exception{
		return toPersonasCondicion("tc_keet_contratos_personal.id_contratos_personal", Constantes.SQL_VERDADERO);
	} // toPersonas
	
	public List<ContratoPersonal> toPersonasAsignadas(String condicion) throws Exception{
		return toPersonasCondicion("tr_mantic_empresa_personal.id_empresa_persona", condicion);
	} // toPersonasAsignadas
	
	private List<ContratoPersonal> toPersonasCondicion(String campoLlave, String condicion) throws Exception{
		List<ContratoPersonal> regresar= null;
		Map<String, Object>params      = null;
		try {
		  params= new HashMap<>();
			params.put("campoLlave", campoLlave);			
			params.put("idDesarrollo", this.idPivote);			
			params.put(Constantes.SQL_CONDICION, condicion);			
			regresar= DaoFactory.getInstance().toEntitySet(ContratoPersonal.class, "VistaContratosDto", "personalAsignado", params);      
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toPersonasAsignadas
	
	public List<ContratoPersonal> toPersonasDisponibles() throws Exception{
		return toPersonasDisponibles(Constantes.SQL_VERDADERO);
	} // toPersonasDisponibles
	
	public List<ContratoPersonal> toPersonasDisponibles(String condicion) throws Exception{
		List<ContratoPersonal> regresar= null;
		Map<String, Object>params      = null;
		try {
		  params= new HashMap<>();
			params.put("idDesarrollo", this.idPivote);			
			params.put("condicion", condicion);
			params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			regresar= DaoFactory.getInstance().toEntitySet(ContratoPersonal.class, "VistaContratosDto", "personalDisponible", params);      
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toPersonasDisponibles	
	
	public ContratoPersonal toPersonaIncidencia(boolean contrato) throws Exception{
		ContratoPersonal regresar= null;
		Map<String, Object>params= null;
		try {
		  params= new HashMap<>();
			params.put(contrato ? "idContratoPersona" : "idEmpresaPersona", this.idPivote);						
			regresar= (ContratoPersonal) DaoFactory.getInstance().toEntity(ContratoPersonal.class, "VistaContratosDto", contrato ? "findContratoPersona" : "findPersonaAdmin", params);      
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toPersonasDisponibles	
	
	public List<Incidente> toIncidencias(Long grupo) throws Exception{
		List<Incidente> regresar= null;
		Map<String, Object>params= null;
		try {
		  params= new HashMap<>();
			params.put("idContratoPersona", this.idPivote);						
			params.put("grupo", grupo);						
			regresar= DaoFactory.getInstance().toEntitySet(Incidente.class, "VistaIncidentesDto", "personalDesarrollo", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toInicidencias
	
	public List<Incidente> toAllIncidencias() throws Exception{
		List<Incidente> regresar= null;
		Map<String, Object>params= null;
		try {
		  params= new HashMap<>();
			params.put("idContratoPersona", this.idPivote);									
			regresar= DaoFactory.getInstance().toEntitySet(Incidente.class, "VistaIncidentesDto", "allPersonalDesarrollo", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toInicidencias
	
	public List<TcKeetDiasFestivosDto> toAllDiasFeriados() throws Exception{
		List<TcKeetDiasFestivosDto> regresar= null;
		Map<String, Object>params= null;
		try {
		  params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "ejercicio='"+Fecha.getAnioActual()+"'");									
			regresar= DaoFactory.getInstance().toEntitySet(TcKeetDiasFestivosDto.class, "TcKeetDiasFestivosDto", "row", params, Constantes.SQL_TODOS_REGISTROS);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toInicidencias
	
	public List<ContratistaLote> toContratistasDisponibles(String condicion, String condicionProveedor) throws Exception{
		List<ContratistaLote> regresar= null;
		Map<String, Object>params      = null;
		try {
		  params= new HashMap<>();
			params.put("idContratoLote", this.idPivote);			
			params.put(Constantes.SQL_CONDICION, condicion);
			params.put("condicionProveedor", condicionProveedor);
			params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			regresar= DaoFactory.getInstance().toEntitySet(ContratistaLote.class, "VistaContratosDto", "contratistasDisponible", params);      
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toContratistasDisponibles	
	
	public List<ContratistaLote> toContratistasAsignados(String condicion, String condicionProveedor) throws Exception{
		List<ContratistaLote> regresar= null;
		Map<String, Object>params      = null;
		try {
		  params= new HashMap<>();			
			params.put("idContratoLote", this.idPivote);			
			params.put(Constantes.SQL_CONDICION, condicion);	
			params.put("condicionProveedor", condicionProveedor);
			params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			regresar= DaoFactory.getInstance().toEntitySet(ContratistaLote.class, "VistaContratosDto", "contratistasAsignado", params);      
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toContratistasAsignados
}