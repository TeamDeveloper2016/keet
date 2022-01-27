package mx.org.kaana.keet.nomina.beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import mx.org.kaana.keet.db.dto.TcKeetNominasRubrosDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 29/04/2020
 *@time 09:27:24 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Rubro extends TcKeetNominasRubrosDto implements Cloneable, Serializable {

	private static final long serialVersionUID=-7582735668635861130L;

	public Rubro() {
		this(null);
	}
	
	public Rubro(String codigo) {
		this.setCodigo(codigo);
	}

	@Override
	public int hashCode() {
		int hash=7;
		hash=79*hash+Objects.hashCode(this.getCodigo());
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this==obj) {
			return true;
		}
		if (obj==null) {
			return false;
		}
		if (getClass()!=obj.getClass()) {
			return false;
		}
		final Rubro other=(Rubro) obj;
		if (!Objects.equals(this.getCodigo(), other.getCodigo())) {
			return false;
		}
		return true;
	}
		
	@Override
	public Class toHbmClass() {
		return TcKeetNominasRubrosDto.class;
	}
	
	@Override
	public Rubro clone() throws CloneNotSupportedException {  
		Rubro clone= new Rubro();
		clone.setCodigo(this.getCodigo());
		clone.setIdNominaProveedor(this.getIdNominaProveedor());
		clone.setIdNominaRubro(this.getIdNominaRubro());
		clone.setIdRubro(this.getIdRubro());
		clone.setNombre(this.getNombre());
		clone.setSat(this.getSat());
		clone.setCantidad(this.getCantidad());
		clone.setDestajo(this.getDestajo());
		clone.setAnticipo(this.getAnticipo());
		clone.setSubtotal(this.getSubtotal());
		clone.setIva(this.getIva());
		clone.setTotal(this.getTotal());
		clone.setRegistro(LocalDateTime.now());
    return clone;  
  }  
}
