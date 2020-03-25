package mx.org.kaana.keet.catalogos.proyectos.beans;

import java.time.LocalDateTime;
import mx.org.kaana.keet.db.dto.TcKeetProyectosArchivosDto;

public class Documento extends TcKeetProyectosArchivosDto{

	private static final long serialVersionUID = 3161986027049012674L;	
	private Long idArchivo;
	private String especialidad;
	private String plano;

	public Documento() {
		super();
	}

	public Documento(String especialidad, String plano, Long idPlano, String archivo, String ruta, Long idProyecto, String nombre, Long idProyectoArchivo, LocalDateTime eliminado, Long tamanio, Long idUsuario, Long idTipoArchivo, String observaciones, String alias, Long idPrototipo, Long idArchivo) {
		super(idPlano, archivo, ruta, idProyecto, nombre, idProyectoArchivo, eliminado, tamanio, idUsuario, idTipoArchivo, observaciones, alias, idPrototipo);
		this.especialidad= especialidad;
		this.plano       = plano;
		this.idArchivo   = idArchivo;
	}
	
	public String getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(String especialidad) {
		this.especialidad = especialidad;
	}

	public String getPlano() {
		return plano;
	}

	public void setPlano(String plano) {
		this.plano = plano;
	}

	public Long getIdArchivo() {
		return idArchivo;
	}

	public void setIdArchivo(Long idArchivo) {
		this.idArchivo = idArchivo;
	}		
}