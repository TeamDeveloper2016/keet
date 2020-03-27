package mx.org.kaana.keet.catalogos.contratos.beans;

import mx.org.kaana.keet.db.dto.TcKeetContratosPresupuestosDto;

public class Presupuesto  extends TcKeetContratosPresupuestosDto{

	private static final long serialVersionUID = -4338190652925272088L;	
	private Long idArchivo;
	private String presupuesto;

	public Presupuesto() {
		super();
	}

	public Presupuesto(String presupuesto, String archivo, String ruta, Long tamanio, Long idUsuarios, Long idContrato, Long idTipoArchivo, String observaciones, Long idContratoPresupuesto, Long idTipoPresupuesto, String alias, String nombre, Long idArchivo) {
    super(archivo, ruta, tamanio, idUsuarios, idTipoArchivo, idContrato, observaciones, idTipoPresupuesto, alias, nombre, idContratoPresupuesto);
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