package mx.org.kaana.mantic.catalogos.proveedores.beans;

import java.io.Serializable;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetProveedoresMaterialesDto;

public class ProveedorMaterial extends TcKeetProveedoresMaterialesDto implements Serializable{

	private static final long serialVersionUID = 7894536715985257448L;
	private String nombre;
	private String propio;
	private String unidadMedida;
	private ESql sqlAccion;
	private Boolean nuevo;

	public ProveedorMaterial() {
		this(-1L);
	}

	public ProveedorMaterial(Long key) {
		this(key, ESql.UPDATE);
	}
	
	public ProveedorMaterial(Long key, ESql sqlAccion) {
		this(key, sqlAccion, false);
	}
	
	public ProveedorMaterial(Long key, ESql sqlAccion, Boolean nuevo) {		
		this(key, "", "", "", sqlAccion, nuevo);		
	}

	public ProveedorMaterial(Long key, String nombre, String propio, String unidadMedida, ESql sqlAccion, Boolean nuevo) {
		super(key);
		this.nombre      = nombre;
		this.propio      = propio;
		this.unidadMedida= unidadMedida;
		this.sqlAccion   = sqlAccion;
		this.nuevo       = nuevo;
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

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPropio() {
		return propio;
	}

	public void setPropio(String propio) {
		this.propio = propio;
	}

	public String getUnidadMedida() {
		return unidadMedida;
	}

	public void setUnidadMedida(String unidadMedida) {
		this.unidadMedida = unidadMedida;
	}	
}