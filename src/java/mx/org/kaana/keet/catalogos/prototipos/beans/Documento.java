package mx.org.kaana.keet.catalogos.prototipos.beans;

import mx.org.kaana.keet.db.dto.TcKeetPrototiposArchivosDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 22/03/2020
 *@time 11:48:22 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Documento extends TcKeetPrototiposArchivosDto {

	private static final long serialVersionUID = -2319367238109675717L;
	private Long idArchivo;
	private String especialidad;
	private String plano;	
	
	public Documento(Long idPlano, String archivo, String ruta, Long tamanio, Long idUsuarios, Long idTipoArchivo, String observaciones, Long idPrototipoArchivo, String alias, Long idPrototipo, String nombre, String especialidad, String plano, Long idArchivo) {
		super(idPlano, archivo, ruta, tamanio, idUsuarios, idTipoArchivo, observaciones, idPrototipoArchivo, alias, idPrototipo, nombre);
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
		return TcKeetPrototiposArchivosDto.class;
	}	
}