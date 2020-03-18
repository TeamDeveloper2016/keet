package mx.org.kaana.keet.catalogos.proyectos.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.keet.catalogos.proyectos.reglas.Transaccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.catalogos.proyectos.reglas.MotorBusqueda;
import mx.org.kaana.keet.db.dto.TcKeetProyectosArchivosDto;
import mx.org.kaana.keet.db.dto.TcKeetProyectosGeneradoresDto;
import mx.org.kaana.keet.db.dto.TcKeetProyectosPresupuestosDto;

public class RegistroProyecto implements Serializable {
	
	private static final long serialVersionUID = 6253504536549861564L;
	private Long idProyecto;
	private Proyecto proyecto;
	private List<TcKeetProyectosArchivosDto> documentos;
	private List<TcKeetProyectosPresupuestosDto> presupuestos;
	private List<TcKeetProyectosGeneradoresDto> generadores;

	public RegistroProyecto() {
		this(new Proyecto(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
	}	
	
	public RegistroProyecto(Proyecto proyecto, List<TcKeetProyectosArchivosDto> documentos, List<TcKeetProyectosPresupuestosDto> presupuestos, List<TcKeetProyectosGeneradoresDto> generadores) {
		this.proyecto    = proyecto;
		this.documentos  = documentos;
		this.presupuestos= presupuestos;
		this.generadores = generadores;
	}

	public RegistroProyecto(Long idProyecto) {
		this.idProyecto  = idProyecto;
		this.documentos  = new ArrayList<>();
		this.presupuestos= new ArrayList<>();
		this.generadores = new ArrayList<>();
		initCollections(idProyecto);
	}

	public Long getIdProyecto() {
		return idProyecto;
	}

	public void setIdProyecto(Long idProyecto) {
		this.idProyecto = idProyecto;
	}

	public Proyecto getProyecto() {
		return proyecto;
	}

	public void setProyecto(Proyecto proyecto) {
		this.proyecto = proyecto;
	}	

	public List<TcKeetProyectosArchivosDto> getDocumentos() {
		return documentos;
	}

	public void setDocumentos(List<TcKeetProyectosArchivosDto> documentos) {
		this.documentos = documentos;
	}	

	public List<TcKeetProyectosPresupuestosDto> getPresupuestos() {
		return presupuestos;
	}

	public void setPresupuestos(List<TcKeetProyectosPresupuestosDto> presupuestos) {
		this.presupuestos = presupuestos;
	}

	public List<TcKeetProyectosGeneradoresDto> getGeneradores() {
		return generadores;
	}

	public void setGeneradores(List<TcKeetProyectosGeneradoresDto> generadores) {
		this.generadores = generadores;
	}	
	
	private void initCollections(Long idProyecto){
		MotorBusqueda motor= null;
		try {
			motor= new MotorBusqueda(idProyecto);
			this.proyecto= motor.toProyecto();
			this.proyecto.setLotes(motor.toLotes());
		} // try
		catch (Exception e) {			
			Error.mensaje(e);
		} // catch		
	} // initCollections
	
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
