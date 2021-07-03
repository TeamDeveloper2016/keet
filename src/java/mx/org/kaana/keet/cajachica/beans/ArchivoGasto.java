package mx.org.kaana.keet.cajachica.beans;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Random;
import mx.org.kaana.keet.db.dto.TcKeetGastosArchivosDto;

public class ArchivoGasto extends TcKeetGastosArchivosDto {
	
	private static final long serialVersionUID = 7733521648482345931L;
	private Long idArchivo;		
	private String consecutivo;
	private Double importe;
	private Long articulos;
	private Long idToken;
  private String url;

	public ArchivoGasto(Long idArchivo, String consecutivo, Double importe, Long articulos, Long idGasto, String archivo, LocalDateTime eliminado, String ruta, Long tamanio, Long idUsuario, Long idTipoArchivo, String alias, Long idGastoArchivo, String nombre, String url) {
		super(idGasto, archivo, eliminado, ruta, tamanio, idUsuario, idTipoArchivo, alias, idGastoArchivo, nombre);
		this.idArchivo  = idArchivo;
		this.consecutivo= consecutivo;
		this.importe    = importe;
		this.articulos  = articulos;
    this.idToken    = new Random().nextLong();
    this.url        = url;
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

  public Long getIdToken() {
    return idToken;
  }

  public void setIdToken(Long idToken) {
    this.idToken = idToken;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 53 * hash + Objects.hashCode(this.idToken);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final ArchivoGasto other = (ArchivoGasto) obj;
    return true;
  }
 
}