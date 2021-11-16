package mx.org.kaana.mantic.comun;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 20/05/2021
 *@time 10:28:29 AM
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public abstract class IBaseEsperados extends IBaseArticulos implements Serializable {
  
  private static final long serialVersionUID = -6717864784860948962L;
  private static final Log LOG = LogFactory.getLog(IBaseEsperados.class);
  
  @Override
  protected void toMoveData(UISelectEntity articulo, Integer index) throws Exception {
		Articulo temporal= this.adminOrden.getArticulos().get(index);
		Map<String, Object> params= new HashMap<>();
		try {
			if(articulo.size()> 1) {
				this.doSearchArticulo(articulo.toLong("idArticulo"), index);
				params.put("idArticulo", articulo.toLong("idArticulo"));
				params.put("idProveedor", this.adminOrden.getIdProveedor());
				params.put("idAlmacen", this.adminOrden.getIdAlmacen());
				temporal.setKey(articulo.toLong("idArticulo"));
				temporal.setIdArticulo(articulo.toLong("idArticulo"));
				temporal.setIdProveedor(this.adminOrden.getIdProveedor());
				temporal.setIdRedondear(articulo.toLong("idRedondear"));
				// verificar el codigo principal del articulo y recuperar el valor del multiplo paras las ordenes de compra
				Entity codigo= (Entity)DaoFactory.getInstance().toEntity("TcManticArticulosCodigosDto", "codigo", params);
				if(codigo== null || codigo.isEmpty()) {
  				temporal.setCodigo(articulo.containsKey("codigo")? articulo.toString("codigo"): "");
					temporal.setMultiplo(1L);
				} // if
				else {
				  temporal.setCodigo(codigo.toString("codigo"));
				  temporal.setMultiplo(codigo.toLong("multiplo"));
					temporal.setCantidad(Double.valueOf(temporal.getMultiplo()));
				}	// else
				if(Cadena.isVacio(articulo.toString("propio")))
					LOG.warn("El articulo ["+ articulo.toLong("idArticulo")+" ] no tiene codigo asignado '"+ articulo.toString("nombre")+ "'");
				temporal.setPropio(articulo.toString("propio"));
				temporal.setNombre(articulo.toString("nombre"));
				temporal.setOrigen(articulo.toString("origen"));
				temporal.setValor(articulo.toDouble(this.precio));
				// SI VIENE EN LA CONSULTA EL CAMPO DE PORCENTAJE ES LA SUMA DE LA COLUMNA DE DESCUENTO Y EXTRA SEPARADA POR COMA
				// ASIGNARLA PARA CALCULAR EL COSTO REAL DEL ARTICULO
				if(articulo.containsKey("porcentajes")) {
					temporal.setMorado(articulo.toString("morado"));
					temporal.setPorcentajes(articulo.toString("porcentajes"));
				} // if	
				// SI VIENE DE IMPORTAR EL ARTICULO DE UN XML ENTONCES CONSIDERAR EL COSTO DE LA FACTURA CON RESPECTO AL DEL CATALOGOD E ARTICULOS
				if(articulo.containsKey("costo")) 
  				temporal.setCosto(articulo.toDouble("costo"));
			  else
				  temporal.setCosto(articulo.toDouble(this.precio));
				temporal.setIva(articulo.toDouble("iva"));				
				temporal.setSat(articulo.get("sat").getData()!= null ? articulo.toString("sat") : "");				
				temporal.setDescuento(this.adminOrden.getDescuento());
				temporal.setExtras(this.adminOrden.getExtras());				
				// SON ARTICULOS QUE ESTAN EN LA FACTURA MAS NO EN LA ORDEN DE COMPRA
				if(articulo.containsKey("descuento")) 
				  temporal.setDescuento(articulo.toString("descuento"));
				if(articulo.containsKey("cantidad")) {
				  temporal.setEsperados(articulo.toDouble("cantidad"));
				  temporal.setSolicitados(articulo.toDouble("cantidad"));
				} // if	
				if(temporal.getEsperados()< 1D)					
					temporal.setEsperados(1D);
				temporal.setCuantos(0D);
				temporal.setUltimo(this.attrs.get("ultimo")!= null);
				temporal.setSolicitado(this.attrs.get("solicitado")!= null);
				temporal.setUnidadMedida(articulo.toString("unidadMedida"));
				temporal.setPrecio(articulo.toDouble("precio"));				
				
				// RECUPERA EL STOCK DEL ALMACEN MAS SABER SI YA FUE HUBO UN CONTEO O NO
				Entity inventario= (Entity)DaoFactory.getInstance().toEntity("TcManticInventariosDto", "stock", params);
				if(inventario!= null && inventario.size()> 0) {
				  temporal.setStock(inventario.toDouble("stock"));
				  temporal.setIdAutomatico(inventario.toLong("idAutomatico"));
				} // if
				// Esto es para cuando se agregan articulos de forma directa del archivo XML
				if(articulo.containsKey("disponible")) 
  				temporal.setDisponible(articulo.toBoolean("disponible"));
				if(index== this.adminOrden.getArticulos().size()- 1) {
					this.adminOrden.getArticulos().add(new Articulo(-1L));
  				this.adminOrden.toAddUltimo(this.adminOrden.getArticulos().size()- 1);
					UIBackingUtilities.execute("jsArticulos.update("+ (this.adminOrden.getArticulos().size()- 1)+ ");");
				} // if	
				if(articulo.containsKey("facturado")) 
					temporal.setFacturado(articulo.toBoolean("facturado"));
				UIBackingUtilities.execute("jsArticulos.callback('"+ articulo.getKey()+ "');");
				this.adminOrden.toCalculate(index);
				if(this.attrs.get("paginator")== null || !(boolean)this.attrs.get("paginator"))
				  this.attrs.put("paginator", this.adminOrden.getArticulos().size()> Constantes.REGISTROS_LOTE_TOPE);
				//if(this instanceof IBaseStorage)
 				//	((IBaseStorage)this).toSaveRecord();
			} // if	
			else
				temporal.setNombre("<span class='janal-color-orange'>EL ARTICULO NO EXISTE EN EL CATALOGO !</span>");
		} // try
		finally {
			Methods.clean(params);
		}
	}  
  
}
