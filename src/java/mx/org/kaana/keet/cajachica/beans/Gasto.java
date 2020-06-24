package mx.org.kaana.keet.cajachica.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;

public class Gasto implements Serializable{
	
	private static final long serialVersionUID = 1597828955111977704L;
	private Long idGasto;
	private Long idCajaChicaCierre;	
	private List<Articulo> articulos;

	public Gasto() {
		this(-1L, new ArrayList<>(), -1L);
	}
	
	public Gasto(Long idCajaChicaCierre, List<Articulo> articulos, Long idGasto) {
		this.idCajaChicaCierre= idCajaChicaCierre;
		this.articulos        = articulos;
		this.idGasto          = idGasto;
	}

	public Long getIdCajaChicaCierre() {
		return idCajaChicaCierre;
	}

	public void setIdCajaChicaCierre(Long idCajaChicaCierre) {
		this.idCajaChicaCierre = idCajaChicaCierre;
	}

	public List<Articulo> getArticulos() {
		return articulos;
	}

	public void setArticulos(List<Articulo> articulos) {
		this.articulos = articulos;
	}

	public Long getIdGasto() {
		return idGasto;
	}

	public void setIdGasto(Long idGasto) {
		this.idGasto = idGasto;
	}	
	
	public Long getTotalArticulos(){
		if(this.articulos!= null)
			return new Long(this.articulos.size());
		else
			return 0L;
	}
}
