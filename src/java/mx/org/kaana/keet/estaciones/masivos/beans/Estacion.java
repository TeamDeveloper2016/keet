package mx.org.kaana.keet.estaciones.masivos.beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 5/05/2020
 *@time 11:38:25 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Estacion extends TcKeetEstacionesDto implements Cloneable, Serializable {

	private static final long serialVersionUID=-6170305461966609460L;

	@Override
	public Class toHbmClass() {
		return TcKeetEstacionesDto.class;
	}
	
	@Override
	public Estacion clone() throws CloneNotSupportedException {
		Estacion clone= new Estacion();
		clone.setIdPlantilla(this.getIdPlantilla());
		clone.setNivel(this.getNivel());
		clone.setClave(this.getClave());
		clone.setUltimo(this.getUltimo());
		clone.setCodigo(this.getCodigo());
		clone.setNombre(this.getNombre());
		clone.setDescripcion(this.getDescripcion());
		clone.setIdEmpaqueUnidadMedida(this.getIdEmpaqueUnidadMedida());
		clone.setInicio(this.getInicio());
		clone.setTermino(this.getTermino());
		clone.setCantidad(this.getCantidad());
		clone.setCosto(this.getCosto());
		clone.setIdEstacionEstatus(this.getIdEstacionEstatus());
		clone.setIdUsuario(this.getIdUsuario());
		clone.setRegistro(LocalDateTime.now());
		return clone;
	}
	
}
