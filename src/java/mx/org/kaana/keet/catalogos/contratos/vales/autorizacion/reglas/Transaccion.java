package mx.org.kaana.keet.catalogos.contratos.vales.autorizacion.reglas;

import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetValesBitacoraDto;
import mx.org.kaana.keet.db.dto.TcKeetValesDto;
import mx.org.kaana.keet.enums.EEstatusVales;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private static final Log LOG= LogFactory.getLog(Transaccion.class);		
	private Long idVale;
	private Long idCargo;
	private boolean ok;
	private String justificacion;

	public Transaccion(Long idVale, boolean ok, String justificacion, Long idCargo) {
		this.idVale       = idVale;
		this.ok           = ok;
		this.justificacion= justificacion;
		this.idCargo      = idCargo;
	}	// Transaccion
	
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar         = true;
		Map<String, Object>params= null;		
		try {
			switch(accion){
				case PROCESAR:												
					regresar= procesarVale(sesion);					
					break;						
			} // switch
		} // try
		catch (Exception e) {			
			throw new Exception((e!= null? e.getCause().toString(): ""));
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
		return regresar;
	}	// ejecutar	
	
	private boolean procesarVale(Session sesion) throws Exception{
		boolean regresar      = false;		
		TcKeetValesDto valeDto= null;
		Long idEstatus        = -1L;
		try {						
			valeDto= (TcKeetValesDto) DaoFactory.getInstance().findById(sesion, TcKeetValesDto.class, this.idVale);				
			valeDto.setIdCargoEmpleado(idCargo);
			idEstatus= this.ok ? EEstatusVales.DISPONIBLE.getKey() : EEstatusVales.RECHAZADO.getKey();
			valeDto.setIdValeEstatus(idEstatus);
			if(DaoFactory.getInstance().update(sesion, valeDto)>= 1L)
				regresar= registrarBitacora(sesion, idEstatus);								
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
		return regresar;
	} // procesarVale		
	
	private boolean registrarBitacora(Session sesion, Long idEstatus) throws Exception{
		boolean regresar               = false;
		TcKeetValesBitacoraDto bitacora= null;
		try {
			bitacora= new TcKeetValesBitacoraDto();
			bitacora.setIdUsuario(JsfBase.getIdUsuario());
			bitacora.setIdVale(this.idVale);
			bitacora.setIdValeEstatus(idEstatus);
			bitacora.setJustificacion("AUTORIZACION/RECHAZO DEL VALE: ".concat(Cadena.isVacio(this.justificacion) ? EEstatusVales.fromId(idEstatus).getNombre() : this.justificacion));
			regresar= DaoFactory.getInstance().insert(sesion, bitacora)>= 1L;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // registrarBitacora					
}