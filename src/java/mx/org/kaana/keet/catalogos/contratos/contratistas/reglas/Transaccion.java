package mx.org.kaana.keet.catalogos.contratos.contratistas.reglas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.beans.SelectionItem;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesContratistasDto;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private static final Log LOG= LogFactory.getLog(Transaccion.class);
	private Long idContratoLote;	
	private List<SelectionItem> empleados;		
	private Long idEmpresaPersona;
	private String[] lotes;
	
	public Transaccion(Long idContratoLote, List<SelectionItem> empleados) {		
		this.idContratoLote= idContratoLote;		
		this.empleados     = empleados;
	}	

	public Transaccion(Long idEmpresaPersona, String[] lotes) {
		this.idEmpresaPersona = idEmpresaPersona;
		this.lotes = lotes;
	}	
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar                       = true;
		Map<String, Object>params              = null;
		TcKeetContratosLotesContratistasDto dto= null;
		Long idUsuario                         = -1L;
		try {
			switch(accion){
				case PROCESAR:									
					idUsuario= JsfBase.getIdUsuario();
					for(SelectionItem item: this.empleados){
						dto= new TcKeetContratosLotesContratistasDto();							
						dto.setIdContratoLote(this.idContratoLote);
						dto.setIdEmpresaPersona(Long.valueOf(item.getKey()));
						dto.setIdUsuario(idUsuario);
						dto.setIdTrabajo(2L);						
						dto.setObservaciones("Asignación de empleado al lote [ContratoLote [" + this.idContratoLote + "]]");
						DaoFactory.getInstance().insert(sesion, dto);
					} // for					
					break;				
				case DEPURAR:
					for(SelectionItem item: this.empleados){
						params= new HashMap<>();
						params.put("idContratoLote", this.idContratoLote);
						params.put("idEmpresaPersona", Long.valueOf(item.getKey()));
						DaoFactory.getInstance().execute(ESql.DELETE, sesion, "TcKeetContratosLotesContratistasDto", "contratoLotePersona", params);					
					} // for
					break;				
				case REPROCESAR:
					idUsuario= JsfBase.getIdUsuario();
					for(String lote: this.lotes){
						dto= new TcKeetContratosLotesContratistasDto();							
						dto.setIdContratoLote(Long.valueOf(lote));
						dto.setIdEmpresaPersona(this.idEmpresaPersona);
						dto.setIdUsuario(idUsuario);
						dto.setIdTrabajo(2L);						
						dto.setObservaciones("Asignación de empleado al lote.");
						if(DaoFactory.getInstance().toEntity(sesion, "TcKeetContratosLotesContratistasDto", "existe", dto.toMap())== null)
							DaoFactory.getInstance().insert(sesion, dto);
					} // for					
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