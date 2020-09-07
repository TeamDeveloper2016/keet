package mx.org.kaana.mantic.compras.ordenes.beans;

import java.util.List;

public class OrdenCompraProcess {

	private OrdenCompra ordenCompra;
	private List<Articulo> articulos;
	private List<String> lotes;
	private List<String> familias;

	public OrdenCompraProcess() {
		this(null, null, null, null);
	}
	
	public OrdenCompraProcess(OrdenCompra ordenCompra, List<Articulo> articulos, List<String> lotes, List<String> familias) {
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

	public List<String> getLotes() {
		return lotes;
	}

	public void setLotes(List<String> lotes) {
		this.lotes = lotes;
	}

	public List<String> getFamilias() {
		return familias;
	}

	public void setFamilias(List<String> familias) {
		this.familias = familias;
	}	
  
}