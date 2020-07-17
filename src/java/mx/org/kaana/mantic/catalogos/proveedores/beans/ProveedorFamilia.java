package mx.org.kaana.mantic.catalogos.proveedores.beans;

import java.io.Serializable;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetProveedoresFamiliasDto;

public class ProveedorFamilia extends TcKeetProveedoresFamiliasDto implements Serializable{

	private static final long serialVersionUID = 7894536715985257448L;
	private ESql sqlAccion;
	private Boolean nuevo;

	public ProveedorFamilia() {
		this(-1L);
	}

	public ProveedorFamilia(Long key) {
		this(key, ESql.UPDATE);
	}
	
	public ProveedorFamilia(Long key, ESql sqlAccion) {
		this(key, sqlAccion, false);
	}
	
	public ProveedorFamilia(Long key, ESql sqlAccion, Boolean nuevo) {
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