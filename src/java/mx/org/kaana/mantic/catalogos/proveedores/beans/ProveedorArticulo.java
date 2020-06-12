package mx.org.kaana.mantic.catalogos.proveedores.beans;

import java.io.Serializable;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetArticulosProveedoresDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class ProveedorArticulo extends TcKeetArticulosProveedoresDto implements Serializable{

	private static final long serialVersionUID = 7894536715985257448L;
	private UISelectEntity uiArticulo;
	private String nombre;
	private ESql sqlAccion;
	private Boolean nuevo;

	public ProveedorArticulo() {
		this(-1L);
	}

	public ProveedorArticulo(Long key) {
		this(key, ESql.UPDATE);
	}
	
	public ProveedorArticulo(Long key, ESql sqlAccion) {
		this(key, sqlAccion, false);
	}
	
	public ProveedorArticulo(Long key, ESql sqlAccion, Boolean nuevo) {
		this(key, sqlAccion, nuevo, "", null);
	}
	
	public ProveedorArticulo(Long key, ESql sqlAccion, Boolean nuevo, String nombre, UISelectEntity uiArticulo) {
		super(key);
		this.sqlAccion = sqlAccion;
		this.nuevo     = nuevo;
		this.nombre    = nombre;
		this.uiArticulo= uiArticulo;
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

	public UISelectEntity getUiArticulo() {
		return uiArticulo;
	}

	public void setUiArticulo(UISelectEntity uiArticulo) {
		this.uiArticulo = uiArticulo;
		if(this.uiArticulo!= null)
			setIdArticulo(this.uiArticulo.getKey());
	}	
}