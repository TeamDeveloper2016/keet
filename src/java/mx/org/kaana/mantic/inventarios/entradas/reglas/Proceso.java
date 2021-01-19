package mx.org.kaana.mantic.inventarios.entradas.reglas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.operation.IActions;
import mx.org.kaana.kajool.db.comun.operation.Insert;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.beans.Siguiente;
import mx.org.kaana.keet.db.dto.TcKeetNotasDirectosDto;
import mx.org.kaana.keet.db.dto.TcKeetNotasManosObrasDto;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.mantic.catalogos.articulos.beans.Importado;
import mx.org.kaana.mantic.compras.ordenes.beans.Articulo;
import mx.org.kaana.mantic.db.dto.TcManticNotasArchivosDto;
import mx.org.kaana.mantic.db.dto.TcManticNotasBitacoraDto;
import mx.org.kaana.mantic.db.dto.TcManticNotasEntradasDto;
import mx.org.kaana.mantic.inventarios.entradas.beans.NotaEntradaDirecta;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 16/01/2021
 *@time 09:25:30 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Proceso extends Transaccion implements Serializable {

  private static final Log LOG = LogFactory.getLog(Proceso.class);
  private static final long serialVersionUID = -2810073874812776664L;

  private NotaEntradaDirecta directa;
  
  public Proceso(NotaEntradaDirecta directa) {
    super((TcManticNotasEntradasDto)directa);
  }

  public Proceso(NotaEntradaDirecta directa, Importado xml, Importado pdf) {
    super((TcManticNotasEntradasDto)directa, new ArrayList<Articulo>(), false, xml, pdf);
    this.directa= directa;
  }

  @Override
  protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
    boolean regresar          = false;
		TcManticNotasBitacoraDto bitacoraNota= null;
		Map<String, Object> params= new HashMap<>();
		Siguiente consecutivo     = null;
		try {
  		this.orden.setIdOrdenCompra(null);
  		this.orden.setIdDesarrollo(null);
  		this.orden.setIdCliente(null);
  		this.orden.setIdContrato(null);
  		this.orden.setIdAlmacen(null);
  		this.orden.setIdAlmacenista(null);
      if(this.orden.getIdBanco()!= null && this.orden.getIdBanco()< 0)
        this.orden.setIdBanco(null);
      if(this.orden.getIdTipoPago()!= null && this.orden.getIdTipoPago()< 0)
        this.orden.setIdTipoPago(null);
			this.messageError= "Ocurrio un error en ".concat(accion.name().toLowerCase()).concat(" la nota de entrada.");
			switch(accion) {
				case MOVIMIENTOS:
					if(this.orden.isValid()) 
  					regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
					else {
					  consecutivo= this.toSiguiente(sesion);
					  this.orden.setConsecutivo(consecutivo.getConsecutivo());
					  this.orden.setOrden(consecutivo.getOrden());
					  this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
					  regresar= DaoFactory.getInstance().insert(sesion, this.orden)>= 1L;
            this.toFillProyectosEmpleados(sesion);
					  bitacoraNota= new TcManticNotasBitacoraDto(-1L, "", JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), this.orden.getIdNotaEstatus(), this.orden.getConsecutivo(), this.orden.getTotal());
						regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
       	    this.toUpdateDeleteXml(sesion);	
					} // else	
          break;
				case COMPLETO:
					consecutivo= this.toSiguiente(sesion);
					this.orden.setConsecutivo(consecutivo.getConsecutivo());
					this.orden.setOrden(consecutivo.getOrden());
					this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
					this.orden.setIdOrdenCompra(null);					
					regresar= DaoFactory.getInstance().insert(sesion, this.orden)>= 1L;
          this.toFillProyectosEmpleados(sesion);
					bitacoraNota= new TcManticNotasBitacoraDto(-1L, "", JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), this.orden.getIdNotaEstatus(), this.orden.getConsecutivo(), this.orden.getTotal());
					regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
	   	    this.toUpdateDeleteXml(sesion);	
					break;
				case AGREGAR:
					consecutivo= this.toSiguiente(sesion);
					this.orden.setConsecutivo(consecutivo.getConsecutivo());
					this.orden.setOrden(consecutivo.getOrden());
					this.orden.setEjercicio(new Long(Fecha.getAnioActual()));
					regresar= DaoFactory.getInstance().insert(sesion, this.orden)>= 1L;
          this.toFillProyectosEmpleados(sesion);
					bitacoraNota= new TcManticNotasBitacoraDto(-1L, "", JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), this.orden.getIdNotaEstatus(), this.orden.getConsecutivo(), this.orden.getTotal());
					regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
					this.toCheckOrden(sesion);
     	    this.toUpdateDeleteXml(sesion);	
					break;
				case COMPLEMENTAR:
					regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
          this.toFillProyectosEmpleados(sesion);
	   	    this.toUpdateDeleteXml(sesion);	
					break;				
				case MODIFICAR:
  				if(this.aplicar) {
						this.orden.setIdNotaEstatus(3L);
  					bitacoraNota= new TcManticNotasBitacoraDto(-1L, "", JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), this.orden.getIdNotaEstatus(), this.orden.getConsecutivo(), this.orden.getTotal());
	  				regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
					} // if	
					regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
          this.toFillProyectosEmpleados(sesion);
					this.toCheckOrden(sesion);
     	    this.toUpdateDeleteXml(sesion);	
					break;				
				case ELIMINAR:
          params.put("idNotaEntrada", this.orden.getIdNotaEntrada());
          DaoFactory.getInstance().deleteAll(sesion, TcManticNotasArchivosDto.class, params);
					DaoFactory.getInstance().deleteAll(sesion, TcKeetNotasDirectosDto.class, params);
				  DaoFactory.getInstance().deleteAll(sesion, TcKeetNotasManosObrasDto.class, params);
          regresar= DaoFactory.getInstance().delete(sesion, this.orden)>= 1L;
          this.orden.setIdNotaEstatus(2L);
          bitacoraNota= new TcManticNotasBitacoraDto(-1L, "", JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), 2L, this.orden.getConsecutivo(), this.orden.getTotal());
          regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
          this.toCheckOrden(sesion);
          this.toDeleteXmlPdf();	
					break;    
				case JUSTIFICAR:
					if(DaoFactory.getInstance().insert(sesion, this.bitacora)>= 1L) {
						this.orden.setIdNotaEstatus(this.bitacora.getIdNotaEstatus());
						regresar= DaoFactory.getInstance().update(sesion, this.orden)>= 1L;
						if(this.bitacora.getIdNotaEstatus().equals(2L)) {
              params.put("idNotaEntrada", this.orden.getIdNotaEntrada());
              DaoFactory.getInstance().deleteAll(sesion, TcManticNotasArchivosDto.class, params);
							DaoFactory.getInstance().deleteAll(sesion, TcKeetNotasDirectosDto.class, params);
							DaoFactory.getInstance().deleteAll(sesion, TcKeetNotasManosObrasDto.class, params);
							regresar= DaoFactory.getInstance().delete(sesion, this.orden)>= 1L;
							this.orden.setIdNotaEstatus(2L);
							bitacoraNota= new TcManticNotasBitacoraDto(-1L, "", JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), 2L, this.orden.getConsecutivo(), this.orden.getTotal());
							regresar= DaoFactory.getInstance().insert(sesion, bitacoraNota)>= 1L;
              this.toCheckOrden(sesion);
						} // if	
					} // if
					break;
			} // switch
  		LOG.info("Se genero de forma correcta la nota de entrada: "+ this.orden.getConsecutivo());
//      throw new RuntimeException("ESTO ES UN ERROR PROBOCADO POR LA APLICACION"); 
      if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {
      Error.mensaje(e);			
			throw new Exception(this.messageError.concat("<br/>")+ e);
		} // catch		
    return regresar;
  }

  @Override
  protected void toCheckOrden(Session sesion) throws Exception {
		try {
			sesion.flush();
			if(Objects.equals(this.orden.getDeuda(), 0D)) {
        // SI EL SALDO ES CERO ENTONCES CAMBIAR EL ESTATUS DE LA NOTA DE ENTRADA A TERMINADO
  			this.orden.setIdNotaEstatus(3L);
			  TcManticNotasBitacoraDto bitacoraNota= new TcManticNotasBitacoraDto(-1L, "LA NOTA SE SALDO DE FORMA AUTOMATICA", JsfBase.getIdUsuario(), this.orden.getIdNotaEntrada(), this.orden.getIdNotaEstatus(), this.orden.getConsecutivo(), this.orden.getTotal());
				DaoFactory.getInstance().insert(sesion, bitacoraNota);
				DaoFactory.getInstance().update(sesion, this.orden);
      } // if
		} // try
		catch (Exception e) {
			throw e;
		} // catch
	} 

  public void toFillProyectosEmpleados(Session sesion) throws Exception {
    try {  
      for (IActions item: this.directa.getProyectos()) {
        if(item instanceof Insert) {
          ((TcKeetNotasDirectosDto)item.getDto()).setIdNotaEntrada(this.orden.getIdNotaEntrada());
          ((TcKeetNotasDirectosDto)item.getDto()).setIdUsuario(JsfBase.getIdUsuario());
        } // if  
        item.ejecutar(sesion);
      } // if
      for (IActions item: this.directa.getEmpleados()) {
        if(item instanceof Insert) {
          ((TcKeetNotasManosObrasDto)item.getDto()).setIdNotaEntrada(this.orden.getIdNotaEntrada());
          ((TcKeetNotasManosObrasDto)item.getDto()).setIdUsuario(JsfBase.getIdUsuario());
        } // if  
        item.ejecutar(sesion);
      } // if  
    } // try
    catch (Exception e) {
      throw e;
    } // catch	
  }
  
}
