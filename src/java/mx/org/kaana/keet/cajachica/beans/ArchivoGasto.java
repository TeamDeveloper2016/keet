package mx.org.kaana.keet.cajachica.beans;

import java.time.LocalDateTime;
import mx.org.kaana.keet.db.dto.TcKeetGastosArchivosDto;

public class ArchivoGasto extends TcKeetGastosArchivosDto{
	
	private static final long serialVersionUID = 7733521648482345931L;
	private Long idArchivo;		
	private String consecutivo;
	private Double importe;
	private Long articulos;

	public ArchivoGasto(Long idArchivo, String consecutivo, Double importe, Long articulos, Long idGasto, String archivo, LocalDateTime eliminado, String ruta, Long tamanio, Long idUsuario, Long idTipoArchivo, String alias, Long idGastoArchivo, String nombre) {
		super(idGasto, archivo, eliminado, ruta, tamanio, idUsuario, idTipoArchivo, alias, idGastoArchivo, nombre);
		this.idArchivo  = idArchivo;
		this.consecutivo= consecutivo;
		this.importe    = importe;
		this.articulos  = articulos;
	}
	
	public Long getIdArchivo() {
		return idArchivo;
	}
	
	public void setIdArchivo(Long idArchivo) {
		this.idArchivo = idArchivo;
	}

	public String getConsecutivo() {
		return consecutivo;
	}

	public void setConsecutivo(String consecutivo) {
		this.consecutivo = consecutivo;
	}

	public Double getImporte() {
		return importe;
	}

	public void setImporte(Double importe) {
		this.importe = importe;
	}

	public Long getArticulos() {
		return articulos;
	}

	public void setArticulos(Long articulos) {
		this.articulos = articulos;
	}	
}