package mx.org.kaana.mantic.catalogos.almacenes.transferencias.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.kajool.template.backing.Reporte;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.reportes.reglas.Parametros;
import mx.org.kaana.mantic.comun.ParametrosReporte;
import mx.org.kaana.mantic.db.dto.TcManticFaltantesDto;
import mx.org.kaana.mantic.db.dto.TcManticTransferenciasDto;
import mx.org.kaana.mantic.enums.EReportes;
import mx.org.kaana.mantic.enums.ETipoMovimiento;
import mx.org.kaana.mantic.libs.pagina.IFilterImportar;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 19/06/2018
 *@time 07:51:53 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value = "manticCatalogosAlmacenesTransferenciasDiferencias")
@ViewScoped
public class Diferencias extends IFilterImportar implements Serializable {

  private static final long serialVersionUID = 8793667741599428311L;
  private Reporte reporte;
	
	private TcManticTransferenciasDto orden;
	protected FormatLazyModel lazyNotas;

	public FormatLazyModel getLazyNotas() {
		return lazyNotas;
	}

	public TcManticTransferenciasDto getOrden() {
		return orden;
	}
	
  @PostConstruct
  @Override
  protected void init() {
    try {
      this.attrs.put("idTransferencia", JsfBase.getFlashAttribute("idTransferencia"));
      this.attrs.put("tipoDiferencia", 0);
      this.orden= (TcManticTransferenciasDto)DaoFactory.getInstance().findById(TcManticTransferenciasDto.class, (Long)JsfBase.getFlashAttribute("idTransferencia"));
		  this.doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
 
  @Override
  public void doLoad() {
    List<Columna> columns= null;
    try {
      columns = new ArrayList<>();
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cantidad", EFormatoDinamicos.NUMERO_CON_DECIMALES));      
      columns.add(new Columna("cantidades", EFormatoDinamicos.NUMERO_CON_DECIMALES));      
      this.attrs.put("sortOrder", "order by nombre");
			switch((Integer)this.attrs.get("tipoDiferencia")) {
				case 0: // TODOS
					this.attrs.put("seleccionado", null);
					this.attrs.put(Constantes.SQL_CONDICION, Constantes.SQL_VERDADERO);
					break;
				case 1: // DIFERENCIA POR PRECIO
					break;
				case 2: // DIFERENCIA POR CANTIDAD
					this.attrs.put(Constantes.SQL_CONDICION, " tc_mantic_transferencias_detalles.cantidades!= 0 and tc_mantic_transferencias_detalles.cantidad!= tc_mantic_transferencias_detalles.cantidades");
					break;
				case 3: // PARTIDAS NO SOLICITADAS
					this.attrs.put("seleccionado", null);
					this.attrs.put(Constantes.SQL_CONDICION, Constantes.SQL_FALSO);
					break;
				case 4: // PARTIDAS NO SURTIDAS
					this.attrs.put("seleccionado", null);
					this.attrs.put(Constantes.SQL_CONDICION, " tc_mantic_transferencias_detalles.cantidad= tc_mantic_transferencias_detalles.cantidades");
					break;
			} // switch
      this.lazyModel = new FormatCustomLazy("VistaConfrontasDto", "confronta", this.attrs, columns);
      UIBackingUtilities.resetDataTable();
			this.attrs.put("seleccionado", null);
			this.doRowSelectEvent();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(columns);
    } // finally		
  } // doLoad

	public String doRegresar() {
		JsfBase.setFlashAttribute("idTransferencia", this.attrs.get("idTransferencia"));
		JsfBase.setFlashAttribute("idConfronta", this.attrs.get("idConfronta"));
		if(this.attrs.get("idConfronta")== null)
  		return "filtro".concat(Constantes.REDIRECIONAR);
		else
  		return "/Paginas/Mantic/Catalogos/Almacenes/Transferencias/Confrontas/filtro".concat(Constantes.REDIRECIONAR);
	}

	public String doTransferencia() {
		JsfBase.setFlashAttribute("idTransferencia", this.attrs.get("idTransferencia"));
		return "/Paginas/Mantic/Catalogos/Almacenes/Transferencias/filtro".concat(Constantes.REDIRECIONAR);
	}

	public String doConfronta() {
		JsfBase.setFlashAttribute("idConfronta", this.attrs.get("idConfronta"));
		return "/Paginas/Mantic/Catalogos/Almacenes/Confrontas/filtro".concat(Constantes.REDIRECIONAR);
	}

	public String doMovimientos() {
		JsfBase.setFlashAttribute("tipo", ETipoMovimiento.TRANSFERENCIAS);
		JsfBase.setFlashAttribute(ETipoMovimiento.TRANSFERENCIAS.getIdKey(), ((Entity)this.attrs.get("filtrado")).toLong(ETipoMovimiento.TRANSFERENCIAS.getIdKey()));
		JsfBase.setFlashAttribute("regreso", "/Paginas/Mantic/Catalogos/Almacenes/Transferencias/diferencias");
		return "/Paginas/Mantic/Compras/Ordenes/movimientos".concat(Constantes.REDIRECIONAR);
	}

	public void doRowSelectEvent() {
		Entity seleccionado= (Entity)this.attrs.get("seleccionado");
    List<Columna> columns     = null;
	  Map<String, Object> params= null;
		try {
			params=new HashMap<>();
      columns = new ArrayList<>();
      columns.add(new Columna("propio", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("cantidad", EFormatoDinamicos.NUMERO_CON_DECIMALES));      
      columns.add(new Columna("porcentaje", EFormatoDinamicos.NUMERO_SAT_DECIMALES));
		  params.put(Constantes.SQL_CONDICION, " ");
			params.put("idTransferencia", this.attrs.get("idTransferencia"));
			if(seleccionado!= null && ((Integer)this.attrs.get("tipoDiferencia")== 1 || (Integer)this.attrs.get("tipoDiferencia")== 2)) 
				params.put(Constantes.SQL_CONDICION, " and tc_mantic_confrontas_detalles.id_articulo= "+ seleccionado.toLong("idArticulo"));
			else
				if((Integer)this.attrs.get("tipoDiferencia")!= 0 && (Integer)this.attrs.get("tipoDiferencia")!= 3)
  			  params.put("idTransferencia", -1L);
			if((Integer)this.attrs.get("tipoDiferencia")== 3)
  		  params.put(Constantes.SQL_CONDICION, " and tc_mantic_confrontas_detalles.id_transferencia_detalle is null ");
			params.put("sortOrder", "order by tc_mantic_confrontas.consecutivo, tc_mantic_transferencias_detalles.nombre");
      this.lazyNotas = new FormatLazyModel("VistaConfrontasDto", "consulta", params, columns);
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally	
	}
  
  public void doReporte(String nombre) throws Exception{
		Parametros comunes = null;
		Map<String, Object>parametros= null;
		EReportes reporteSeleccion   = null;
		try{		
      reporteSeleccion= EReportes.valueOf(nombre);
      if(!reporteSeleccion.equals(EReportes.ORDENES_COMPRA)){
        comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa(), Long.valueOf(this.attrs.get("idAlmacen").toString()), Long.valueOf(this.attrs.get("idProveedor").toString()), -1L);
      }
      else
        comunes= new Parametros(JsfBase.getAutentifica().getEmpresa().getIdEmpresa());
      this.reporte= JsfBase.toReporte();	
      parametros= comunes.getComunes();
      parametros.put("ENCUESTA", JsfBase.getAutentifica().getEmpresa().getNombre().toUpperCase());
      parametros.put("NOMBRE_REPORTE", reporteSeleccion.getTitulo());
      parametros.put("REPORTE_ICON", JsfBase.getRealPath("/resources/janal/img/sistema/"));
      this.reporte.toAsignarReporte(new ParametrosReporte(reporteSeleccion, this.attrs, parametros));		
      this.doVerificarReporte();
      this.reporte.doAceptar();			
    } // try
    catch(Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);			
    } // catch	
} // doReporte
	
	public void doVerificarReporte() {
		if(this.reporte.getTotal()> 0L)
			UIBackingUtilities.execute("start(" + this.reporte.getTotal() + ")");		
		else{
			UIBackingUtilities.execute("generalHide()");		
			JsfBase.addMessage("Generar reporte","No se encontraron registros para el reporte", ETipoMensaje.ALERTA);
		} // else
	} // doVerificarReporte		
	
	public String doOrdenColor(Entity row) {
		return !row.toDouble("unidades").equals(0D)? "janal-tr-diferencias": "";
	} 
	
	public String doNotaColor(Entity row) {
		return row.toString("nuevo").equals("*")? "janal-tr-nuevo": "";
	} 

  public void doChangeArticulos() {
		this.doLoad();
	}	

  public void doFaltanteArticulo() {
		try {
			Entity faltante= (Entity)this.attrs.get("faltante");
			if(faltante!= null) {
				TcManticFaltantesDto existe= (TcManticFaltantesDto)DaoFactory.getInstance().findFirst(TcManticFaltantesDto.class, "existe", faltante.toMap());
				if(existe== null) {
					existe= new TcManticFaltantesDto(JsfBase.getIdUsuario(), -1L, "", faltante.toDouble("solicitar"), 1L, faltante.toLong("idArticulo"), faltante.toLong("idEmpresa"));
					if(DaoFactory.getInstance().insert(existe)> 0L)
						JsfBase.addMessage("Agregado:", "El articulo fue agregado a la relaci�n de faltantes. !", ETipoMensaje.INFORMACION);
				}	// if
				else {
					existe.setCantidad(existe.getCantidad()+ faltante.toDouble("solicitar"));
					if(DaoFactory.getInstance().update(existe)> 0L) 
						JsfBase.addMessage("Agregado:", "El articulo fue actualizado en la relaci�n de faltantes. !", ETipoMensaje.INFORMACION);
				} // else	
			} // if	
		} // try
	  catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
	}	
	
}
