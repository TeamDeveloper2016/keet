package mx.org.kaana.mantic.catalogos.articulos.beans;

import java.time.LocalDate;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.mantic.db.dto.TrManticArticuloProveedorDto;

public class ArticuloProveedor extends TrManticArticuloProveedorDto{

	private static final long serialVersionUID = -9204076933249586658L;
	private ESql sqlAccion;
	private Boolean nuevo;
	private LocalDate compra;

	public ArticuloProveedor() {
		this(-1L);
	}

	public ArticuloProveedor(Long key) {
		this(key, ESql.UPDATE);
	}
	
	public ArticuloProveedor(Long key, ESql sqlAccion) {
		this(key, sqlAccion, false);
	}
	
	public ArticuloProveedor(Long key, ESql sqlAccion, Boolean nuevo) {
		super(key);
		this.sqlAccion= sqlAccion;
		this.nuevo    = nuevo;
		this.compra   = LocalDate.now();
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

	public LocalDate getCompra() {
		return compra;
	}

	public void setCompra(LocalDate compra) {
		this.compra = compra;
	}
}
