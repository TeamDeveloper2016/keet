package mx.org.kaana.keet.catalogos.contratos.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.keet.catalogos.contratos.beans.Contrato;
import mx.org.kaana.keet.catalogos.contratos.beans.ContratoPersonal;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.reflection.Methods;

public class MotorBusqueda implements Serializable{
	
	private static final long serialVersionUID = -2951697223110542896L;
	private Long idContrato;

	public MotorBusqueda(Long idContrato) {
		this.idContrato = idContrato;
	}
	
	public Contrato toContrato() throws Exception{
		Contrato regresar        = null;
		Map<String, Object>params= null;
		try {
		  params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_contrato=" + this.idContrato);
			regresar= (Contrato) DaoFactory.getInstance().toEntity(Contrato.class, "TcKeetContratosDto", "row", params);			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toContrato
	
	public List<ContratoPersonal> toPersonas() throws Exception{
		List<ContratoPersonal> regresar= null;
		Map<String, Object>params      = null;
		try {
		  params= new HashMap<>();
			params.put("idContrato", this.idContrato);
			regresar= DaoFactory.getInstance().toEntitySet(ContratoPersonal.class, "VistaContratosPersonalDto", "row", params);      
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toConstructivos
}
