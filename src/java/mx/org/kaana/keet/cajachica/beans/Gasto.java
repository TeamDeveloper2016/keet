package mx.org.kaana.keet.cajachica.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;

public class Gasto implements Serializable{
	
	private static final long serialVersionUID = 1597828955111977704L;
	private Long idCajaChicaCierre;	
	private List<Articulo> articulos;

	public Gasto() {
		this(-1L, new ArrayList<>());
	}
	
	public Gasto(Long idCajaChicaCierre, List<Articulo> articulos) {
		this.idCajaChicaCierre= idCajaChicaCierre;
		this.articulos        = articulos;
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

	public Long getTotalArticulos(){
		if(this.articulos!= null)
			return new Long(this.articulos.size());
		else
			return 0L;
	}
}
