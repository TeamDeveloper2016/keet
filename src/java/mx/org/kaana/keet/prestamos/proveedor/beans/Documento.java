package mx.org.kaana.keet.prestamos.proveedor.beans;

import mx.org.kaana.keet.db.dto.TcKeetAnticiposArchivosDto;


public class Documento extends TcKeetAnticiposArchivosDto {
	private static final long serialVersionUID = 5328765921129293801L;
	private Long idArchivo;

	
	public Documento(String archivo, String ruta, Long tamanio, Long idUsuarios, Long idTipoArchivo, String observaciones, Long idAnticipoArchivo, String alias, Long idAnticipoPago, String nombre, Long idArchivo) {
		super(archivo, ruta, tamanio, idUsuarios, idTipoArchivo, 1L, observaciones, alias, idAnticipoPago, nombre, idAnticipoArchivo);
		this.idArchivo= idArchivo;
	}

	public Long getIdArchivo() {
		return idArchivo;
	}

	public void setIdArchivo(Long idArchivo) {
		this.idArchivo = idArchivo;
	}	
	
	@Override
	public Class toHbmClass() {
		return TcKeetAnticiposArchivosDto.class;
	}	
  
}