package mx.org.kaana.keet.catalogos.contratos.materiales.beans;

import java.time.LocalDateTime;
import mx.org.kaana.keet.db.dto.TcKeetValesArchivosDto;

public class ArchivoEvidenciaVale extends TcKeetValesArchivosDto{
	
	private static final long serialVersionUID = 7733521648482345931L;
	private Long idArchivo;	
	private Long tipo;	
	private String clave;

	public ArchivoEvidenciaVale(Long idArchivo, Long tipo, String clave, String archivo, LocalDateTime eliminado, String ruta, Long tamanio, Long idUsuario, Long idTipoArchivo, String observaciones, String alias, Long idValeArchivo, Long idVale, String nombre) {
		super(archivo, eliminado, ruta, tamanio, idUsuario, idTipoArchivo, observaciones, alias, idValeArchivo, idVale, nombre);
		this.idArchivo= idArchivo;
		this.tipo     = tipo;
		this.clave    = clave;
	}	
		
	public Long getIdArchivo() {
		return idArchivo;
	}
	
	public void setIdArchivo(Long idArchivo) {
		this.idArchivo = idArchivo;
	}
	
	public Long getTipo() {
		return tipo;
	}
	
	public void setTipo(Long tipo) {
		this.tipo = tipo;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}	
}