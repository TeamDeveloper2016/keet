package mx.org.kaana.mantic.catalogos.clientes.beans;

import java.io.Serializable;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetClientesViviendasDto;

public class ClienteVivienda extends TcKeetClientesViviendasDto implements Serializable{

	private static final long serialVersionUID = 7894536715985257448L;
	private ESql sqlAccion;
	private Boolean nuevo;

	public ClienteVivienda() {
		this(-1L);
	}

	public ClienteVivienda(Long key) {
		this(key, ESql.UPDATE);
	}
	
	public ClienteVivienda(Long key, ESql sqlAccion) {
		this(key, sqlAccion, false);
	}
	
	public ClienteVivienda(Long key, ESql sqlAccion, Boolean nuevo) {
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