package mx.org.kaana.mantic.compras.ordenes.beans;

import java.util.List;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class OrdenCompraProcess {

	private OrdenCompra ordenCompra;
	private List<Articulo> articulos;
	private List lotes;
	private List familias;

	public OrdenCompraProcess() {
		this(null, null, null, null);
	}
	
	public OrdenCompraProcess(OrdenCompra ordenCompra, List<Articulo> articulos, List lotes, List familias) {
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

	public List getLotes() {
		return lotes;
	}

	public void setLotes(List lotes) {
		this.lotes = lotes;
	}

	public List getFamilias() {
		return familias;
	}

	public void setFamilias(List familias) {
		this.familias = familias;
	}	
  
}