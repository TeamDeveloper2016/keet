package mx.org.kaana.keet.catalogos.prototipos.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.catalogos.prototipos.reglas.MotorBusqueda;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.keet.catalogos.prototipos.reglas.Transaccion;
import mx.org.kaana.keet.db.dto.TcKeetNombresDiasDto;
import mx.org.kaana.keet.enums.EDiasSemana;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class RegistroPrototipo implements Serializable {

	private static final long serialVersionUID = 4966382666424549056L;	
	private Long idPrototipo;
	private Prototipo prototipo;
	private List<SistemaConstructivo> constructivos;
	private SistemaConstructivo constructivoSeleccion;
	private List<Documento> documentos;
	private List<String> dias;
	private List<String> diasDefault;
	private String[] diasSeleccionados;
	private Long countIndice;
	private ContadoresListas contadores;

	public RegistroPrototipo() {
		this(-1L, new Prototipo(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	} // RegistroPrototipo
	
	public RegistroPrototipo(Long idPrototipo, Prototipo prototipo, List<SistemaConstructivo> constructivos, List<Documento> documentos, List<String> dias) {
		this.prototipo    = prototipo;
		this.constructivos= constructivos;
		this.documentos   = documentos;		
		this.dias         = dias;
		this.countIndice  = 0L;
		this.contadores   = new ContadoresListas();
		loadAllDefault();
		loadDiasDefault();
	} // RegistroPrototipo
	
	public RegistroPrototipo(Long idPrototipo){	
		this.idPrototipo= idPrototipo;
		this.documentos = new ArrayList<>();
		this.contadores = new ContadoresListas();
		this.countIndice= 0L;
		initCollections(idPrototipo);
	} // RegistroPrototipo
	
	private void initCollections(Long idPrototipo){
		MotorBusqueda motor= null;
		try {
			motor= new MotorBusqueda(idPrototipo);
			this.prototipo        = motor.toPrototipo();
			this.constructivos    = motor.toConstructivos(); 
			this.loadAllDefault();
			this.dias= new ArrayList<>();
			for(TcKeetNombresDiasDto dia: motor.toDias())
				this.dias.add(dia.getNombre());			
			this.diasSeleccionados= this.dias.toArray(new String[0]);
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

	public List<Documento> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<Documento> documentos) {
		this.documentos = documentos;
	}

	public SistemaConstructivo getConstructivoSeleccion() {
		return constructivoSeleccion;
	}

	public void setConstructivoSeleccion(SistemaConstructivo constructivoSeleccion) {
		this.constructivoSeleccion = constructivoSeleccion;
	}

	public List<String> getDias() {
		return dias;
	}

	public void setDias(List<String> dias) {
		this.dias = dias;
	}	

	public String[] getDiasSeleccionados() {
		return diasSeleccionados;
	}

	public void setDiasSeleccionados(String[] diasSeleccionados) {
		this.diasSeleccionados = diasSeleccionados;
	}	

	public List<String> getDiasDefault() {
		return diasDefault;
	}

	public void setDiasDefault(List<String> diasDefault) {
		this.diasDefault = diasDefault;
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
				JsfBase.addMessage("No fue posible eliminar el tipo de contacto", ETipoMensaje.INFORMACION);
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

	public void selectConstructivo(List<UISelectEntity> elementos) {
		try {
			for(SistemaConstructivo item: this.constructivos)
			  item.setIkConstructivo(elementos.get(elementos.indexOf(new UISelectEntity(new Entity(item.getIdConstructivo())))));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // selectConstructivo
	
	private void loadDiasDefault(){
		List<TcKeetNombresDiasDto> diasDefault= null;
		List<String> diasList                 = null;
		Map<String, Object>params             = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			diasDefault= DaoFactory.getInstance().toEntitySet(TcKeetNombresDiasDto.class, "TcKeetNombresDiasDto", "row", params, Constantes.SQL_TODOS_REGISTROS);						
			diasList= new ArrayList<>();
			for(TcKeetNombresDiasDto dto: diasDefault){
				if(!dto.getIdNombreDia().equals(EDiasSemana.DOMINGO.getKey()))
					diasList.add(dto.getNombre());
			} // for
			if(!diasList.isEmpty())
				this.diasSeleccionados= diasList.toArray(new String[0]);
		} // try
		catch (Exception e) {			
			Error.mensaje(e);			
		} // catch		
	} // loadDiasDefault
	
	private void loadAllDefault(){		
		Map<String, Object>params       = null;
		List<TcKeetNombresDiasDto>pivote=null;
		try {
			this.diasDefault= new ArrayList<>();
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
			pivote= DaoFactory.getInstance().toEntitySet(TcKeetNombresDiasDto.class, "TcKeetNombresDiasDto", "row", params, Constantes.SQL_TODOS_REGISTROS);									
			for(TcKeetNombresDiasDto dia: pivote){
				this.diasDefault.add(dia.getNombre());
			} // for
		} // try
		catch (Exception e) {			
			Error.mensaje(e);			
		} // catch		
	} // loadDiasDefault
}
