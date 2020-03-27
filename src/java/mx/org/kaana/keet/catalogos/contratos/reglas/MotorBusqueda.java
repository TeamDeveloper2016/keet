package mx.org.kaana.keet.catalogos.contratos.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.catalogos.contratos.beans.Lote;
import mx.org.kaana.keet.catalogos.contratos.beans.Contrato;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

public class MotorBusqueda implements Serializable{
	
	private static final long serialVersionUID = -2951697223110542896L;
	private Long idContrato;

	public MotorBusqueda(Long idContrato) {
		this.idContrato = idContrato;
	}
	
	public Contrato toContrato() throws Exception{
		Contrato regresar       = null;
		Map<String, Object>params= null;
		try {
		  params= new HashMap<>();
			params.put("idContrato", this.idContrato);
			regresar= (Contrato) DaoFactory.getInstance().toEntity(Contrato.class, "TcKeetContratosDto", "byId", params);
			if(regresar!= null && regresar.isValid()){
				regresar.setIkProyecto(new UISelectEntity(regresar.getIdProyecto()));
			} // if			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toPrototipo	
	
	public List<Lote> toLotes() throws Exception{
		List<Lote> regresar= null;
		Map<String, Object>params         = null;
		try {
		  params= new HashMap<>();
			params.put("idContrato", this.idContrato);
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
	} // toConstructivos
}