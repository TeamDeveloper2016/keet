package mx.org.kaana.keet.catalogos.contratos.personal.reglas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.beans.SelectionItem;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetContratosPersonalDto;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private Long idDesarrollo;
	private Long idContrato;
	private List<SelectionItem> empleados;

	public Transaccion(Long idDesarrollo, Long idContrato, List<SelectionItem> empleados) {
		this.idDesarrollo= idDesarrollo;
		this.idContrato  = idContrato;
		this.empleados   = empleados;
	}	
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar              = true;
		Map<String, Object>params     = null;
		TcKeetContratosPersonalDto dto= null;
		Long idUsuario                = -1L;
		try {
			switch(accion){
				case PROCESAR:				
					params= new HashMap<>();
					params.put("idContrato", this.idContrato);
					params.put("idDesarrollo", this.idDesarrollo);
					if(DaoFactory.getInstance().execute(ESql.DELETE, sesion, "TcKeetContratosPersonalDto", "contrato", params)>= 0L){
						idUsuario= JsfBase.getIdUsuario();
						for(SelectionItem item: this.empleados){
							dto= new TcKeetContratosPersonalDto();
							dto.setIdContrato(this.idContrato);
							dto.setIdDesarrollo(this.idDesarrollo);
							dto.setIdEmpresaPersona(Long.valueOf(item.getKey()));
							dto.setIdUsuario(idUsuario);
							dto.setIdVigente(1L);
							dto.setObservaciones("Asignación de empleado al contrato " + this.idContrato);
							DaoFactory.getInstance().insert(sesion, dto);
						} // for
					} // if
					break;				
			} // switch
		} // try
		catch (Exception e) {			
			throw new Exception(e);
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	}	// ejecutar
}