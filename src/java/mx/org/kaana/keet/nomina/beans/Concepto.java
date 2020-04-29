package mx.org.kaana.keet.nomina.beans;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import mx.org.kaana.keet.db.dto.TcKeetNominasDetallesDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 29/04/2020
 *@time 12:58:42 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Concepto extends TcKeetNominasDetallesDto implements Cloneable, Serializable {

	private static final long serialVersionUID=5389281368534240096L;

  private String celda;
  private Integer columna;	

	public Concepto() {
		this(null);
	}
	
	public Concepto(String celda) {
		this.celda= celda;
	}

	public String getCelda() {
		return celda;
	}

	public void setCelda(String celda) {
		this.celda=celda;
	}

	public Integer getColumna() {
		return columna;
	}

	public void setColumna(Integer columna) {
		this.columna=columna;
	}

	@Override
	public int hashCode() {
		int hash=7;
		hash=53*hash+Objects.hashCode(this.celda);
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
		final Concepto other=(Concepto) obj;
		if (!Objects.equals(this.celda, other.celda)) {
			return false;
		}
		return true;
	}
	
	@Override
	public Class toHbmClass() {
		return TcKeetNominasDetallesDto.class;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {  
		Concepto clone= new Concepto();
		clone.setCelda(this.getCelda());
		clone.setColumna(this.getColumna());
		clone.setValor(this.getValor());
		clone.setIdNominaDetalle(this.getIdNominaDetalle());
		clone.setFormula(this.getFormula());
		clone.setIdNominaConcepto(this.getIdNominaConcepto());
		clone.setIdNominaPersona(this.getIdNominaPersona());
		clone.setNombre(this.getNombre());
		clone.setClave(this.getClave());
		clone.setRegistro(this.getRegistro());
    return clone;  
  }  

}
