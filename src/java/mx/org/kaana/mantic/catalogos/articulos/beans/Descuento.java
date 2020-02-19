package mx.org.kaana.mantic.catalogos.articulos.beans;

import java.time.LocalDate;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.mantic.db.dto.TcManticArticulosDescuentosDto;

public class Descuento extends TcManticArticulosDescuentosDto{
	
	private static final long serialVersionUID = 4981342777621580602L;
	private ESql sqlAccion;
	private Boolean nuevo;
	private LocalDate vigenciaIni;
	private LocalDate vigenciaFin;

	public Descuento() {
		this(-1L);
	}

	public Descuento(Long key) {
		this(key, ESql.UPDATE);
	}
	
	public Descuento(Long key, ESql sqlAccion) {
		this(key, sqlAccion, false);
	}
	
	public Descuento(Long key, ESql sqlAccion, Boolean nuevo) {
		super(key);
		this.sqlAccion  = sqlAccion;
		this.nuevo      = nuevo;
		this.vigenciaIni= LocalDate.now();
		this.vigenciaFin= LocalDate.now();
	}

	public ESql getSqlAccion() {
		return sqlAccion;
	}

	public void setSqlAccion(ESql sqlAccion) {
		this.sqlAccion = sqlAccion;
	}

	public Boolean getNuevo() {
		return nuevo;
	}

	public void setNuevo(Boolean nuevo) {
		this.nuevo = nuevo;
	}

	public LocalDate getVigenciaIni() {
		return vigenciaIni;
	}

	public void setVigenciaIni(LocalDate vigenciaIni) {
		this.vigenciaIni = vigenciaIni;
	}

	public LocalDate getVigenciaFin() {
		return vigenciaFin;
	}

	public void setVigenciaFin(LocalDate vigenciaFin) {
		this.vigenciaFin = vigenciaFin;
	}
}