package mx.org.kaana.mantic.catalogos.proveedores.beans;

import java.io.Serializable;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TrKeetArticuloProveedorClienteDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class ProveedorArticuloCliente extends TrKeetArticuloProveedorClienteDto implements Serializable{

	private static final long serialVersionUID = 7894536715985257448L;
	private UISelectEntity uiCliente;
	private UISelectEntity uiArticulo;
	private String nombre;
	private String cliente;
	private ESql sqlAccion;
	private Boolean nuevo;

	public ProveedorArticuloCliente() {
		this(-1L);
	}

	public ProveedorArticuloCliente(Long key) {
		this(key, ESql.UPDATE);
	}
	
	public ProveedorArticuloCliente(Long key, ESql sqlAccion) {
		this(key, sqlAccion, false);
	}
	
	public ProveedorArticuloCliente(Long key, ESql sqlAccion, Boolean nuevo) {
		this(key, sqlAccion, nuevo, "", "", null, null);
	}
	
	public ProveedorArticuloCliente(Long key, ESql sqlAccion, Boolean nuevo, String nombre, String cliente, UISelectEntity uiCliente, UISelectEntity uiArticulo) {
		super(key);
		this.sqlAccion = sqlAccion;
		this.nuevo     = nuevo;
		this.nombre    = nombre;
		this.cliente   = cliente;
		this.uiCliente = uiCliente;
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

	public String getCliente() {
		return cliente;
	}

	public void setCliente(String cliente) {
		this.cliente = cliente;
	}	

	public UISelectEntity getUiCliente() {
		return uiCliente;
	}

	public void setUiCliente(UISelectEntity uiCliente) {
		this.uiCliente = uiCliente;
		if(this.uiCliente!= null)
			setIdCliente(this.uiCliente.getKey());
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