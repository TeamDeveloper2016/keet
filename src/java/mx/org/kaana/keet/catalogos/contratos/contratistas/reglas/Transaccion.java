package mx.org.kaana.keet.catalogos.contratos.contratistas.reglas;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.beans.SelectionItem;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesContratistasDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesProveedoresDto;
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
	private Long tipo;
	
	public Transaccion(Long idContratoLote, List<SelectionItem> empleados) {		
		this.idContratoLote= idContratoLote;		
		this.empleados     = empleados;
	}	

	public Transaccion(Long idEmpresaPersona, String[] lotes, Long tipo) {
		this.idEmpresaPersona= idEmpresaPersona;
		this.lotes           = lotes;
		this.tipo            = tipo;
	}	
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar         = true;
		Map<String, Object>params= null;
		IBaseDto dto             = null;
		Long idUsuario           = -1L;
		try {
			switch(accion){
				case PROCESAR:									
					idUsuario= JsfBase.getIdUsuario();
					for(SelectionItem item: this.empleados){
						if(item.getTipo().equals(1L))
							dto= loadContratista(Long.valueOf(item.getKey().substring(4)), idUsuario, true);
						else
							dto= loadSubContratista(Long.valueOf(item.getKey().substring(4)), idUsuario, true);
						DaoFactory.getInstance().insert(sesion, dto);
					} // for					
					break;				
				case DEPURAR:
					for(SelectionItem item: this.empleados){
						params= new HashMap<>();
						params.put("idContratoLote", this.idContratoLote);
						params.put("idEmpresaPersona", Long.valueOf(item.getKey().substring(4)));
						params.put("idProveedor", Long.valueOf(item.getKey().substring(4)));
						DaoFactory.getInstance().execute(ESql.DELETE, sesion, item.getTipo().equals(1L) ? "TcKeetContratosLotesContratistasDto" : "TcKeetContratosLotesProveedoresDto", "contratoLotePersona", params);					
					} // for
					break;				
				case REPROCESAR:
					idUsuario= JsfBase.getIdUsuario();
					for(String lote: this.lotes){
						if(this.tipo.equals(1L))
							dto= loadContratista(Long.valueOf(lote), idUsuario, false);						
						else
							dto= loadSubContratista(Long.valueOf(lote), idUsuario, false);
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
	
	private IBaseDto loadContratista(Long key, Long idUsuario, boolean contratista){
		TcKeetContratosLotesContratistasDto regresar= null;
		try {
			regresar= new TcKeetContratosLotesContratistasDto();							
			if(contratista){
				regresar.setIdContratoLote(this.idContratoLote);
				regresar.setIdEmpresaPersona(key);
				regresar.setObservaciones("ASIGNACION DEL CONTRATISTA AL LOTE [ContratoLote [" + this.idContratoLote + "]]");
			} // if
			else{
				regresar.setIdContratoLote(key);
				regresar.setIdEmpresaPersona(this.idEmpresaPersona);				
				regresar.setObservaciones("ASIGNACION DEL CONTRATISTA AL LOTE");
			} // else
			regresar.setIdUsuario(idUsuario);
			regresar.setIdTrabajo(2L);						
			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadContratista
	
	private IBaseDto loadSubContratista(Long key, Long idUsuario, boolean subcontratista){
		TcKeetContratosLotesProveedoresDto regresar= null;
		try {
			regresar= new TcKeetContratosLotesProveedoresDto();							
			if(subcontratista){
				regresar.setIdProveedor(key);
				regresar.setIdContratoLote(this.idContratoLote);				
				regresar.setObservaciones("ASIGNACION DEL CONTRATISTA AL LOTE [ContratoLote [" + this.idContratoLote + "]]");
			} // if
			else{
				regresar.setIdProveedor(this.idEmpresaPersona);
				regresar.setIdContratoLote(key);				
				regresar.setObservaciones("ASIGNACION DEL CONTRATISTA AL LOTE");
			} // else
			regresar.setIdUsuario(idUsuario);
			regresar.setIdTrabajo(2L);									
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadSubContratista
}