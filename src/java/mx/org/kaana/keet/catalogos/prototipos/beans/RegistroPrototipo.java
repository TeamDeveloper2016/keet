package mx.org.kaana.keet.catalogos.prototipos.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.catalogos.prototipos.reglas.MotorBusqueda;
import mx.org.kaana.keet.db.dto.TcKeetPrototiposArchivosDto;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.keet.catalogos.prototipos.reglas.Transaccion;

public class RegistroPrototipo implements Serializable {

	private static final long serialVersionUID = 4966382666424549056L;	
	private Long idPrototipo;
	private Prototipo prototipo;
	private List<SistemaConstructivo> constructivos;
	private SistemaConstructivo constructivoSeleccion;
	private List<TcKeetPrototiposArchivosDto> documentos;
	private Long countIndice;
	private ContadoresListas contadores;

	public RegistroPrototipo() {
		this(-1L, new Prototipo(), new ArrayList<SistemaConstructivo>(), new ArrayList<>());
	} // RegistroPrototipo
	
	public RegistroPrototipo(Long idPrototipo, Prototipo prototipo, List<SistemaConstructivo> constructivos, List<TcKeetPrototiposArchivosDto> documentos) {
		this.prototipo    = prototipo;
		this.constructivos= constructivos;
		this.documentos   = documentos;		
		this.countIndice  = 0L;
		this.contadores   = new ContadoresListas();
	} // RegistroPrototipo
	
	public RegistroPrototipo(Long idPrototipo){	
		this.idPrototipo= idPrototipo;
		this.contadores = new ContadoresListas();
		this.countIndice= 0L;
		initCollections(idPrototipo);
	} // RegistroPrototipo
	
	private void initCollections(Long idPrototipo){
		MotorBusqueda motor= null;
		try {
			motor= new MotorBusqueda(idPrototipo);
			this.prototipo    = motor.toPrototipo();
			this.constructivos= motor.toConstructivos(); 
		} // try
		catch (Exception e) {			
			Error.mensaje(e);			
		} // catch		
	} // initCollections

	public Long getIdPrototipo() {
		return idPrototipo;
	}

	public void setIdPrototipo(Long idPrototipo) {
		this.idPrototipo = idPrototipo;
	}

	public Prototipo getPrototipo() {
		return prototipo;
	}

	public void setPrototipo(Prototipo prototipo) {
		this.prototipo = prototipo;
	}

	public List<SistemaConstructivo> getConstructivos() {
		return constructivos;
	}

	public void setConstructivos(List<SistemaConstructivo> constructivos) {
		this.constructivos = constructivos;
	}	

	public List<TcKeetPrototiposArchivosDto> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<TcKeetPrototiposArchivosDto> documentos) {
		this.documentos = documentos;
	}

	public SistemaConstructivo getConstructivoSeleccion() {
		return constructivoSeleccion;
	}

	public void setConstructivoSeleccion(SistemaConstructivo constructivoSeleccion) {
		this.constructivoSeleccion = constructivoSeleccion;
	}
	
	public void doAgregarConstructivo(){
		SistemaConstructivo sistemaConstructivo= null;
		try {					
			sistemaConstructivo= new SistemaConstructivo(this.contadores.getTotalConstructivos() + this.countIndice, ESql.INSERT, true, null);							
			this.constructivos.add(sistemaConstructivo);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
		finally{			
			this.countIndice++;
		} // finally
	} // doAgregarClienteTipoContacto
	
	public void doEliminarConstructivo(){
		try {			
			if(this.constructivos.remove(this.constructivoSeleccion)){
				if(!this.constructivoSeleccion.getNuevo())
					addDeleteList(this.constructivoSeleccion);
				JsfBase.addMessage("Se eliminó correctamente el tipo de contacto", ETipoMensaje.INFORMACION);
			} // if
			else
				JsfBase.addMessage("No fue porsible eliminar el tipo de contacto", ETipoMensaje.INFORMACION);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarClienteTipoContacto
	
	private void addDeleteList(IBaseDto dto) throws Exception{
		Transaccion transaccion= null;
		try {
			transaccion= new Transaccion(dto);
			transaccion.ejecutar(EAccion.DEPURAR);			
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // addDeleteList
}
