package mx.org.kaana.mantic.compras.ordenes.beans;

import java.util.List;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class OrdenCompraProcess {

	private OrdenCompra ordenCompra;
	private List<Articulo> articulos;
	private List<UISelectEntity> lotes;
	private List<UISelectEntity> familias;

	public OrdenCompraProcess() {
		this(null, null, null, null);
	}
	
	public OrdenCompraProcess(OrdenCompra ordenCompra, List<Articulo> articulos, List<UISelectEntity> lotes, List<UISelectEntity> familias) {
		this.ordenCompra= ordenCompra;
		this.articulos  = articulos;
		this.lotes      = lotes;
		this.familias   = familias;
	}

	public OrdenCompra getOrdenCompra() {
		return ordenCompra;
	}

	public void setOrdenCompra(OrdenCompra ordenCompra) {
		this.ordenCompra = ordenCompra;
	}

	public List<Articulo> getArticulos() {
		return articulos;
	}

	public void setArticulos(List<Articulo> articulos) {
		this.articulos = articulos;
	}

	public List<UISelectEntity> getLotes() {
		return lotes;
	}

	public void setLotes(List<UISelectEntity> lotes) {
		this.lotes = lotes;
	}

	public List<UISelectEntity> getFamilias() {
		return familias;
	}

	public void setFamilias(List<UISelectEntity> familias) {
		this.familias = familias;
	}	
  
}