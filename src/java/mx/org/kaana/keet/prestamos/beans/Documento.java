package mx.org.kaana.keet.prestamos.beans;

import mx.org.kaana.keet.db.dto.TcKeetPrestamosArchivosDto;


public class Documento extends TcKeetPrestamosArchivosDto {
	private static final long serialVersionUID = 5328765921129293800L;
	private Long idArchivo;

	
	public Documento(String archivo, String ruta, Long tamanio, Long idUsuarios, Long idTipoArchivo, String observaciones, Long idPrestamoArchivo, String alias, Long idPrestamoPago, String nombre, Long idArchivo) {
		super(archivo, ruta, tamanio, idUsuarios, idTipoArchivo, idPrestamoPago, 1L, observaciones, alias, idPrestamoArchivo, nombre);
		this.idArchivo   = idArchivo;
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