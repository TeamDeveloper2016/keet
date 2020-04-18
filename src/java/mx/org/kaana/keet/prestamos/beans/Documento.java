package mx.org.kaana.keet.prestamos.beans;

import mx.org.kaana.keet.db.dto.TcKeetPrestamosArchivosDto;


public class Documento extends TcKeetPrestamosArchivosDto {

	private static final long serialVersionUID = 5328765921129293800L;
	private Long idArchivo;
	private String especialidad;
	private String plano;	
	
	public Documento(Long idPlano, String archivo, String ruta, Long tamanio, Long idUsuarios, Long idTipoArchivo, String observaciones, Long idPrototipoArchivo, String alias, Long idPrototipo, String nombre, String especialidad, String plano, Long idArchivo) {
		super(archivo, ruta, tamanio, idUsuarios, idTipoArchivo, idPrototipo, idPrototipo, observaciones, alias, idPrototipoArchivo, nombre);
		this.especialidad= especialidad;
		this.plano       = plano;
		this.idArchivo   = idArchivo;
	}

	public String getEspecialidad() {
		return especialidad;
	}

	public void setEspecialidad(String especialidad) {
		this.especialidad=especialidad;
	}

	public String getPlano() {
		return plano;
	}

	public void setPlano(String plano) {
		this.plano=plano;
	}

	public Long getIdArchivo() {
		return idArchivo;
	}

	public void setIdArchivo(Long idArchivo) {
		this.idArchivo = idArchivo;
	}	
	
	@Override
	public Class toHbmClass() {
		return TcKeetPrestamosArchivosDto.class;
	}	
}