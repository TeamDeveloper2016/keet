package mx.org.kaana.keet.catalogos.proyectos.beans;

import mx.org.kaana.keet.db.dto.TcKeetProyectosGeneradoresDto;


public class Generador extends TcKeetProyectosGeneradoresDto{
	private String plano;

	public Generador() {
	  super();
	}

	public Generador(String plano, Long idTipoGenerador, String archivo, String ruta, Long tamanio, Long idUsuario, Long idProyecto, Long idTipoArchivo, Long idProyectoGenerador, String observaciones, String alias, String nombre) {
		super(idTipoGenerador, archivo, ruta, tamanio, idUsuario, idProyecto, idTipoArchivo, idProyectoGenerador, observaciones, alias, nombre);
		this.plano = plano;
	}
	
	

	public String getPlano() {
		return plano;
	}

	public void setPlano(String plano) {
		this.plano = plano;
	}
	
	
}
