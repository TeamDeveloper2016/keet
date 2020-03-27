package mx.org.kaana.keet.catalogos.contratos.beans;

import mx.org.kaana.keet.db.dto.TcKeetContratosGeneradoresDto;

public class Generador extends TcKeetContratosGeneradoresDto{

	private static final long serialVersionUID = 5065390128207513571L;
	private Long idArchivo;
	private String plano;

	public Generador() {
	  super();
	}

	public Generador(String plano, Long idTipoGenerador, String archivo, String ruta, Long tamanio, Long idUsuario, Long idContrato, Long idTipoArchivo, Long idContratoGenerador, String observaciones, String alias, String nombre, Long idArchivo) {
    super(idTipoGenerador, idContratoGenerador, archivo, ruta, tamanio, idUsuario, idTipoArchivo, idContrato, observaciones, alias, nombre);
		this.plano    = plano;
		this.idArchivo= idArchivo;
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