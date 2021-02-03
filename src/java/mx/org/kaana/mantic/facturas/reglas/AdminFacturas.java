package mx.org.kaana.mantic.facturas.reglas;

import com.google.common.base.Objects;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.catalogos.contratos.beans.ContratoDomicilio;
import mx.org.kaana.keet.catalogos.contratos.reglas.MotorBusqueda;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Numero;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.comun.IAdminArticulos;
import mx.org.kaana.mantic.facturas.beans.FacturaFicticia;
import mx.org.kaana.mantic.facturas.beans.Parcial;
import mx.org.kaana.mantic.ventas.beans.ArticuloVenta;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 8/05/2018
 *@time 03:09:42 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public final class AdminFacturas extends IAdminArticulos implements Serializable {

	private static final long serialVersionUID= 8594649943986572245L;
	private static final Log LOG              = LogFactory.getLog(AdminFacturas.class);

	private FacturaFicticia orden;

	public AdminFacturas(FacturaFicticia orden) throws Exception {
		this(orden, true);
	}
	
	public AdminFacturas(FacturaFicticia orden, boolean loadDefault) throws Exception {
		List<ArticuloVenta> articulos= null;
		this.orden= orden;		
		if(this.orden.isValid()) {
			articulos= (List<ArticuloVenta>)DaoFactory.getInstance().toEntitySet(ArticuloVenta.class, "VistaFicticiasDto", "detalle", orden.toMap());
  	  this.setArticulos(articulos);    
			this.validatePrecioArticulo();
      this.orden.setIkSerie(new UISelectEntity(new Entity(this.orden.getIdSerie())));
      this.orden.setIkTipoComprobante(new UISelectEntity(new Entity(this.orden.getIdTipoComprobante())));
      this.orden.setIkEmpresa(new UISelectEntity(new Entity(this.orden.getIdEmpresa())));
      this.orden.setIkDesarrollo(new UISelectEntity(new Entity(this.orden.getIdDesarrollo())));
      this.orden.setIkCliente(new UISelectEntity(new Entity(this.orden.getIdCliente())));
		}	// if
		else	{
		  articulos= new ArrayList<>();
		  this.setArticulos(articulos);			
			this.orden.setIdUsuario(JsfBase.getAutentifica().getPersona().getIdUsuario());
      this.orden.setIkSerie(new UISelectEntity(1L));
      this.orden.setIkTipoComprobante(new UISelectEntity(1L));
      this.orden.setIkEmpresa(new UISelectEntity(JsfBase.getAutentifica().getEmpresa().getIdEmpresa()));
      this.orden.setIkDesarrollo(new UISelectEntity(-1L));
      this.orden.setIkCliente(new UISelectEntity(-1L));
		} // else	
    if(this.orden.getIdContrato()== null) {
      this.orden.setIkContrato(new UISelectEntity(-1L));
      this.orden.setDomicilioContrato(new ContratoDomicilio(-1L, ESql.INSERT));
      this.orden.setParciales(new ArrayList<>());
      this.orden.setDisponibles(new ArrayList<>());
    } // if
    else {
      this.orden.setIkContrato(new UISelectEntity(new Entity(this.orden.getIdContrato())));
      this.orden.setDomicilioContrato(this.toContratoDomicilios(this.orden.getIdContrato()));
      this.orden.setParciales(new ArrayList<>());
      this.toLoadContratoLotes();
    } // else  
		if(loadDefault)
			this.getArticulos().add(new ArticuloVenta(-1L, true));
		this.setIdSinIva(2L);
		Map<String, Object> params=null;
		try {
			params=new HashMap<>();
      params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
 			Value value= DaoFactory.getInstance().toField("TcManticAlmacenesDto", "almacen", params, "idAlmacen");
    	if(value.getData()!= null)
				this.orden.setIdAlmacen(value.toLong());
			else
				this.orden.setIdAlmacen(1L);
		} // try
		finally {
			Methods.clean(params);
		} // finally
		LOG.warn("Forzar que todos los precios capturados ya son netos, por lo tanto se les descuenta el IVA");
		this.toCalculate();
		this.cleanPrecioDescuentoArticulo();
	}
	
	public AdminFacturas(FacturaFicticia orden, List<Entity> tickets) throws Exception {
		this.orden                = orden;		
		Map<String, Object> params= null;
		try {
			params= new HashMap<>();
			// sacar los idVenta de todos los tickets seleccionados
			StringBuilder sb= new StringBuilder(tickets== null || tickets.isEmpty()? "-1, ": "");
			tickets.forEach((item) -> {
				sb.append(item.toString("idVenta")).append(", ");
			}); // for
			params.put("idVenta", sb.substring(0, sb.length()- 2));
			// ejecutar la consulta que recupere todo los articulos de todos los tickets orenados por articulo y cantidad
			List<ArticuloVenta> articulos= (List<ArticuloVenta>)DaoFactory.getInstance().toEntitySet(ArticuloVenta.class, "VistaFicticiasDto", "masivo", params);
			// juntar todos los articulos que son iguales sumar la cantidades y los importes y calcular el precio unitario sin considerar ya los descuentos
			this.setArticulos(new ArrayList<>());
			ArticuloVenta pivote= null;
			if(!articulos.isEmpty()) 
				for (ArticuloVenta articulo: articulos) {
    			params.put("idVentaDetalle", articulo.getIdComodin());
					Value devoluciones= (Value)DaoFactory.getInstance().toField("VistaFicticiasDto", "devoluciones", params, "cantidad");
					articulo.setDescuento("0");
					articulo.setExtras("0");
					articulo.setDescuentos(0D);
					articulo.setExcedentes(0D);
					articulo.setPrecio(0D);
					articulo.setCosto(0D);
					articulo.setValor(0D);
					articulo.setCalculado(0D);
					articulo.setImpuestos(0D);
					articulo.setSubTotal(0D);
					articulo.setIdAplicar(2L);
					articulo.setCostoLibre(true);
					// Verificar si existe alguna devolución de articulo en el ticket asociado y restarlo
					if(articulo.getCantidad()- devoluciones.toLong()> 0D) {
						articulo.setCantidad(articulo.getCantidad()- devoluciones.toLong());
						if(pivote== null || !Objects.equal(pivote.getIdArticulo(), articulo.getIdArticulo())) {
							pivote= articulo;
							this.getArticulos().add(pivote);
						} // if	
						else {
							pivote.setInicial(pivote.getInicial()+ articulo.getInicial());
							pivote.setSolicitados(pivote.getCantidad()+ articulo.getCantidad());
							pivote.setCuantos(pivote.getCantidad()+ articulo.getCantidad());
							pivote.setCantidad(pivote.getCantidad()+ articulo.getCantidad());
							if(devoluciones.toLong()> 0D) {
								double cantidad= articulo.getCantidad()- devoluciones.toLong();
								double unitario= articulo.getImporte()/ cantidad;
								pivote.setImporte(Numero.toRedondearSat(pivote.getImporte()+ (unitario* cantidad)));
								pivote.setTotal(Numero.toRedondearSat(pivote.getTotal()+ (unitario* cantidad)));
							} // if
							else {
								pivote.setImporte(pivote.getImporte()+ articulo.getImporte());
								pivote.setTotal(pivote.getTotal()+ articulo.getTotal());
							} // else
						} // if
					} // if	
				} // for
			// calcular el precio unitario considerando que ya serian precios netos
			for (ArticuloVenta articulo: articulos) {
				articulo.setValor(Numero.toRedondearSat(articulo.getImporte()/ articulo.getCantidad()));
				articulo.setCosto(Numero.toRedondearSat(articulo.getImporte()/ articulo.getCantidad()));
				articulo.setPrecio(Numero.toRedondearSat(articulo.getImporte()/ articulo.getCantidad()));
				articulo.setReal(Numero.toRedondearSat(articulo.getImporte()/ articulo.getCantidad()));
			} // for
			this.orden.setIdUsuario(JsfBase.getAutentifica().getPersona().getIdUsuario());
			this.orden.setIdEmpresa(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
			// this.getArticulos().add(new ArticuloVenta(-1L, true));
			this.setIdSinIva(1L);
      params.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
 			Value value= DaoFactory.getInstance().toField("TcManticAlmacenesDto", "almacen", params, "idAlmacen");
    	if(value.getData()!= null)
				this.orden.setIdAlmacen(value.toLong());
			else
				this.orden.setIdAlmacen(1L);
			this.toCalculate();
      this.orden.setParciales(new ArrayList<>());
      this.orden.setDisponibles(new ArrayList<>());
		} // try
		finally {
			Methods.clean(params);
		} // finally
	}
	
	@Override
	public IBaseDto getOrden() {
		return orden;
	}

	@Override
	public void setOrden(IBaseDto orden) {
		this.orden= (FacturaFicticia)orden;
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
	public Long getIdProveedor() {
		return -1L;
	}

	@Override
	public void setDescuento(String descuento){
		this.orden.setDescuento(descuento);
	}

	@Override
	public Long getIdAlmacen() {
		return this.orden.getIdAlmacen();
	}

	public void toContratoDomicilios() throws Exception {
    this.orden.setDomicilioContrato(this.toContratoDomicilios(this.orden.getIdContrato()));
  }
  
	public ContratoDomicilio toContratoDomicilios(Long idContrato) throws Exception {
		ContratoDomicilio regresar= null;
		try {
      MotorBusqueda motor= new MotorBusqueda(idContrato);
      List<ContratoDomicilio> domicilios= motor.toContratoDomicilios(true);
      if(domicilios!= null && !domicilios.isEmpty()) {
        regresar= domicilios.get(0);
        regresar.setNuevoCp(regresar.getCodigoPostal()!= null && !Cadena.isVacio(regresar.getCodigoPostal()));
      } // if  
      else
        regresar= this.orden.getDomicilioContrato();
		} // try
		catch (Exception e) {		
			throw e;
		} // catch		
		return regresar;
	} // toContratoDomicilio

  public void toLoadContratoLotes() throws Exception {
    Map<String, Object> params = null;
    try {      
      params = new HashMap<>();      
      params.put("idContrato", this.orden.getIdContrato());
      int count= 0;
      while(count< this.orden.getParciales().size()) {
        Parcial item= this.orden.getParciales().get(count);
        if(item.getSqlAccion().equals(ESql.INSERT))
          this.orden.getParciales().remove(item);
        else {
          item.setSqlAccion(ESql.DELETE);
          count++;
        } // else  
      } // while
      this.orden.setDisponibles((List<Parcial>)DaoFactory.getInstance().toEntitySet(Parcial.class, "VistaIngresosDto", "lotes", params));
      count= 0;
      if(this.orden.getDisponibles()!= null && !this.orden.getDisponibles().isEmpty()) {
        while(count< this.orden.getDisponibles().size()) {
          Parcial item= this.orden.getDisponibles().get(count);
          if(this.orden.getParciales().indexOf(item)>= 0) {
            item.getSqlAccion().equals(ESql.UPDATE);
            this.orden.getDisponibles().remove(item);
          } // if  
          else {
            item.setSqlAccion(ESql.INSERT);
            item.setCodigoPostal(this.orden.getDomicilioContrato().getCodigoPostal());
            item.setCalle(this.orden.getDomicilioContrato().getCalle());
            item.setColonia(this.orden.getDomicilioContrato().getColonia());
            item.setNumeroExterior(item.getLote());
            item.setNumeroInterior(this.orden.getDomicilioContrato().getInterior());
            item.setIdLocalidad(this.orden.getDomicilioContrato().getIdLocalidad().getKey());
            item.setPermiso(this.orden.getIkContrato().toString("permiso"));
            if(this.orden.getDomicilioContrato().getIdLocalidad().getKey()> 0L) {
              item.setEntidad(this.orden.getDomicilioContrato().getIdEntidad().toString("descripcion"));
              item.setMunicipio(this.orden.getDomicilioContrato().getIdMunicipio().toString("descripcion"));
              item.setLocalidad(this.orden.getDomicilioContrato().getIdLocalidad().toString("descripcion"));
            } // if
            count++;
          } // else
        } // while
      } // if
    } // try
    catch (Exception e) {
			throw e;
    } // catch	
  } // toLoadContratoLotes
  
}
