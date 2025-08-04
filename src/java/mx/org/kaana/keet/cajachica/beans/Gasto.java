package mx.org.kaana.keet.cajachica.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;

public class Gasto implements Serializable {
	
	private static final long serialVersionUID = 1597828955111977704L;
	private Long idGasto;
	private Long idCajaChicaCierre;	
	private Long idTipoMedioPago;	
	private Long idDesarrollo;	
	private Long idContrato;	
	private List<Articulo> articulos;

	public Gasto() {
		this(-1L, new ArrayList<>(), -1L, 1L, -1L);
	}
	
	public Gasto(Long idCajaChicaCierre, List<Articulo> articulos, Long idGasto, Long idTipoMedioPago, Long idContrato) {
		this.idCajaChicaCierre= idCajaChicaCierre;
		this.articulos        = articulos;
		this.idGasto          = idGasto;
    this.idTipoMedioPago  = idTipoMedioPago;
    this.idContrato       = idContrato;
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

  public Long getIdTipoMedioPago() {
    return idTipoMedioPago;
  }

  public void setIdTipoMedioPago(Long idTipoMedioPago) {
    this.idTipoMedioPago = idTipoMedioPago;
  }

  public Long getIdContrato() {
    return idContrato;
  }

  public void setIdContrato(Long idContrato) {
    this.idContrato = idContrato;
  }

  public Long getIdDesarrollo() {
    return idDesarrollo;
  }

  public void setIdDesarrollo(Long idDesarrollo) {
    this.idDesarrollo = idDesarrollo;
  }
 
}
