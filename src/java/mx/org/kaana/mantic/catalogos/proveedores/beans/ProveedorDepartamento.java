package mx.org.kaana.mantic.catalogos.proveedores.beans;

import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetProveedoresDepartamentosDto;

public class ProveedorDepartamento extends TcKeetProveedoresDepartamentosDto{
	
	private static final long serialVersionUID = -6866655507736705178L;
	private ESql sqlAccion;
	private Boolean nuevo;

	public ProveedorDepartamento() {
		this(-1L);
	}

	public ProveedorDepartamento(Long key) {
		this(key, ESql.UPDATE);
	}
	
	public ProveedorDepartamento(Long key, ESql sqlAccion) {
		this(key, sqlAccion, false);
	}
	
	public ProveedorDepartamento(Long key, ESql sqlAccion, Boolean nuevo) {
		super(key);
		this.sqlAccion= sqlAccion;
		this.nuevo    = nuevo;
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
}
