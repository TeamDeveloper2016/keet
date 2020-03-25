package mx.org.kaana.keet.catalogos.proyectos.beans;

import mx.org.kaana.keet.db.dto.TcKeetProyectosPresupuestosDto;

public class Presupuesto  extends TcKeetProyectosPresupuestosDto{

	private static final long serialVersionUID = -4338190652925272088L;	
	private Long idArchivo;
	private String presupuesto;

	public Presupuesto() {
		super();
	}

	public Presupuesto(String presupuesto, String archivo, String ruta, Long tamanio, Long idUsuarios, Long idProyecto, Long idTipoArchivo, String observaciones, Long idProyectoPresupuesto, Long idTipoPresupuesto, String alias, String nombre, Long idArchivo) {
		super(archivo, ruta, tamanio, idUsuarios, idProyecto, idTipoArchivo, observaciones, idProyectoPresupuesto, idTipoPresupuesto, alias, nombre);
		this.presupuesto= presupuesto;
		this.idArchivo  = idArchivo;						
	}
	
	public String getPresupuesto() {
		return presupuesto;
	}

	public void setPresupuesto(String presupuesto) {
		this.presupuesto = presupuesto;
	}

	public Long getIdArchivo() {
		return idArchivo;
	}

	public void setIdArchivo(Long idArchivo) {
		this.idArchivo = idArchivo;
	}
}