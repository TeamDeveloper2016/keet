package mx.org.kaana.mantic.compras.ordenes.reglas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.mantic.compras.ordenes.beans.OrdenCompra;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.comun.IAdminArticulos;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 8/05/2018
 *@time 03:09:42 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public final class AdminOrdenes extends IAdminArticulos  implements Serializable {

	private static final long serialVersionUID=-5550539230224591510L;
	private static final Log LOG=LogFactory.getLog(AdminOrdenes.class);

	private OrdenCompra orden;

	public AdminOrdenes(OrdenCompra orden) throws Exception {
		this.orden  = orden;
		if(this.orden.isValid()) {
  	  this.setArticulos((List<Articulo>)DaoFactory.getInstance().toEntitySet(Articulo.class, "VistaOrdenesComprasDto", "detalle", orden.toMap(), -1L));
      this.orden.setIkEmpresa(new UISelectEntity(new Entity(this.orden.getIdEmpresa())));
      this.orden.setIkAlmacen(new UISelectEntity(new Entity(this.orden.getIdAlmacen())));
      this.orden.setIkProveedor(new UISelectEntity(new Entity(this.orden.getIdProveedor())));
      this.orden.setIkProveedorPago(new UISelectEntity(new Entity(this.orden.getIdProveedorPago())));
      this.orden.setIkDesarrollo(new UISelectEntity(new Entity(this.orden.getIdDesarrollo())));
      this.orden.setIkCliente(new UISelectEntity(new Entity(this.orden.getIdCliente())));
      this.orden.setIkContrato(new UISelectEntity(new Entity(this.orden.getIdContrato())));
      this.orden.setIkBanco(new UISelectEntity(new Entity(this.orden.getIdBanco())));
      this.orden.setIkTipoMedioPago(new UISelectEntity(new Entity(this.orden.getIdTipoMedioPago())));
      this.orden.setIkTipoPago(new UISelectEntity(new Entity(this.orden.getIdTipoPago())));
      this.orden.setIkAlmacenista(new UISelectEntity(new Entity(this.orden.getIdAlmacenista())));
      this.orden.setIkEmpresaTipoContacto(new UISelectEntity(new Entity(this.orden.getIdEmpresaTipoContacto())));
      this.orden.setIkTipoOrden(new UISelectEntity(new Entity(this.orden.getIdTipoOrden())));
		}	// if
		else {
		  this.setArticulos(new ArrayList<>());
			this.orden.setConsecutivo(this.toConsecutivo("0"));
			this.orden.setIdUsuario(JsfBase.getAutentifica().getPersona().getIdUsuario());
			this.orden.setIdEmpresa(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.orden.setIkEmpresa(new UISelectEntity(new Entity(this.orden.getIdEmpresa())));
      this.orden.setIkAlmacen(new UISelectEntity(-1L));
      this.orden.setIkProveedor(new UISelectEntity(-1L));
      this.orden.setIkDesarrollo(new UISelectEntity(-1L));
      this.orden.setIkCliente(new UISelectEntity(-1L));
      this.orden.setIkContrato(new UISelectEntity(-1L));
      this.orden.setIkBanco(new UISelectEntity(-1L));
      this.orden.setIkTipoMedioPago(new UISelectEntity(-1L));
      this.orden.setIkTipoPago(new UISelectEntity(-1L));
      this.orden.setIkAlmacenista(new UISelectEntity(-1L));
      this.orden.setIkEmpresaTipoContacto(new UISelectEntity(-1L));
      this.orden.setIkTipoOrden(new UISelectEntity(1L));
		} // else	
		this.toStartCalculate();
	}

	@Override
	public Long getIdAlmacen() {
		return this.orden.getIdAlmacen();
	}

	@Override
	public Long getIdProveedor() {
		return this.orden.getIdProveedor();
	}

	@Override
	public IBaseDto getOrden() {
		return orden;
	}

	@Override
	public void setOrden(IBaseDto orden) {
		this.orden= (OrdenCompra)orden;
	}

	@Override
	public Double getTipoDeCambio() {
		return this.orden.getTipoDeCambio();
	}
	
	@Override
	public String getDescuento() {
		return this.orden.getDescuento();
	}
	
	@Override
	public String getExtras() {
		return this.orden.getExtras();
	}
	
	@Override
	public Long getIdSinIva() {
		return this.orden.getIdSinIva();
	}
	
	@Override
	public void setIdSinIva(Long idSinIva) {
		this.orden.setIdSinIva(idSinIva);
	}

	@Override
	public void setDescuento(String descuento) {
		this.orden.setDescuento(descuento);
	}	
  
}
