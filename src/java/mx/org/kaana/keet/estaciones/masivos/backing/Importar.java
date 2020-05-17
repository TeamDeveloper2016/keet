package mx.org.kaana.keet.estaciones.masivos.backing;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.kajool.procesos.reportes.beans.ExportarXls;
import mx.org.kaana.kajool.procesos.reportes.beans.Modelo;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.kajool.reglas.comun.FormatCustomLazy;
import mx.org.kaana.kajool.reglas.comun.FormatLazyModel;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.recurso.Configuracion;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.comun.IBaseImportar;
import mx.org.kaana.keet.estaciones.masivos.reglas.Transaccion;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.mantic.catalogos.masivos.enums.ECargaMasiva;
import mx.org.kaana.mantic.db.dto.TcManticMasivasArchivosDto;
import mx.org.kaana.mantic.enums.EExportacionXls;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;

@Named(value= "keetEstacionesMasivosImportar")
@ViewScoped
public class Importar extends IBaseImportar implements Serializable {

	private static final Log LOG=LogFactory.getLog(Importar.class);
  private static final long serialVersionUID= 318633488565639363L;

  private TcManticMasivasArchivosDto masivo;
	protected FormatLazyModel lazyModel;
	private ECargaMasiva categoria;

	public TcManticMasivasArchivosDto getMasivo() {
		return masivo;
	}

	public FormatLazyModel getLazyModel() {
		return lazyModel;
	}

	public ECargaMasiva getCategoria() {
		return categoria;
	}
	
	@PostConstruct
  @Override
  protected void init() {		
    try {
      this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresaDepende());
			this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno")== null? "filtro": JsfBase.getFlashAttribute("retorno"));
			this.attrs.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			this.attrs.put("ikContratoLote", JsfBase.getFlashAttribute("idContratoLote"));
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_MASIVO);
			this.attrs.put("idLimpiar", 1L);
			this.attrs.put("procesados", 0L);
			if(JsfBase.getFlashAttribute("idTipoMasivo")!= null)
				switch(((Long)JsfBase.getFlashAttribute("idTipoMasivo")).intValue()) {
					case 9:
						this.categoria= ECargaMasiva.ESTACIONES;
						this.toLoadContratosLotes();
						break;
					case 10:
						this.categoria= ECargaMasiva.PERSONAL;
						break;
					case 11:
						this.categoria= ECargaMasiva.PLANTILLAS;
						this.toLoadPlantillas();
						toLoadPrototipos();
						break;
				} // switch
			else {
				this.categoria= ECargaMasiva.ESTACIONES;
				this.toLoadContratosLotes();
				toLoadPrototipos();
			} // if
			this.attrs.put("xls", ""); 
			this.attrs.put("tuplas", 0L);
			this.masivo = new TcManticMasivasArchivosDto(
				-1L, // Long idMasivaArchivo, 
				Configuracion.getInstance().getPropiedadSistemaServidor("masivos"), // String ruta, 
				categoria.getId(), // Long idTipoMasivo, 
				1L, // Long idMasivaEstatus, 
				null, // String nombre, 
				0L, // Long tamanio, 
			  JsfBase.getIdUsuario(), // Long idUsuario, 
				8L, // Long idTipoArchivo, 
				0L, // Long tuplas, 
				"", // String observaciones, 
				null, // String alias, 
				JsfBase.getAutentifica().getEmpresa().getIdEmpresa(),
				1L,
				null
			);
			this.attrs.put("idTipoMasivo", this.masivo.getIdTipoMasivo());
  		this.toCheckRequerido();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
  
	public void doTabChange(TabChangeEvent event) {
		this.attrs.put("idTipoMasivo", this.masivo.getIdTipoMasivo());
		if(event.getTab().getTitle().equals("Archivos")) 
			this.doLoadArhivos("VistaCargasMasivasDto", "importados", this.attrs);
	} // doTabChange		
	
	protected void doLoadArhivos(String proceso, String idXml, Map<String, Object> params) {
		List<Columna> columns= null;
		try {
			columns= new ArrayList<>();
      columns.add(new Columna("ruta", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("usuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("observaciones", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA));
		  this.lazyModel= new FormatCustomLazy(proceso, idXml, params, columns);
			UIBackingUtilities.resetDataTable();
		} // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(columns);
    } // finally
  } // doLoadArhivos
	
	public void doFileUpload(FileUploadEvent event) {
		String tuplas= "0";
		try {
  		this.attrs.put("procesados", 0);
      this.doFileUploadMasivo(event, this.masivo.getRegistro().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), Configuracion.getInstance().getPropiedadSistemaServidor("masivos"), this.masivo, this.categoria);
			tuplas= Global.format(EFormatoDinamicos.MILES_SIN_DECIMALES, this.masivo.getTuplas());
			this.attrs.put("tuplas", tuplas);
		} // try
		catch(Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		if(this.masivo.getIdTipoMasivo()== 1L) {
	    UIBackingUtilities.execute("janal.show([{summary: 'Procesar:', detail: 'Filas encontradas en el archivo ["+ tuplas+ "].'}], 'info');"); 
		} // if	
	} // doFileUpload	
	
	public void doViewFile() {
		this.doViewFile(Configuracion.getInstance().getPropiedadSistemaServidor("listaprecios"));
	}
	
  public String doCancelar() {   
    return ((String)this.attrs.get("retorno")).concat(Constantes.REDIRECIONAR);
  } // doCancelar
	
	public String doAceptar() {
		String regresar        = null;
		Transaccion transaccion= null;
		long tuplas            = this.masivo.getTuplas();
		try {
			UISelectEntity idContratoLote= ((UISelectEntity)this.attrs.get("idContratoLote"));
			this.masivo.setArchivo(this.getXls().getOriginal());
		  this.masivo.setObservaciones((String)this.attrs.get("observaciones"));
      transaccion= new Transaccion(this.masivo, this.categoria, idContratoLote.getKey(), this.masivo.getIdTipoMasivo()== 9L? (Long)this.attrs.get("idLimpiar"): (Long)this.attrs.get("idEliminar"));
      if(tuplas> 0L && transaccion.ejecutar(EAccion.PROCESAR)) {

      } // if
      else
    		JsfBase.addMessage("Error:", "Ocurrio un error en la cargar masiva del archivo !", ETipoMensaje.ERROR);		
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
		if(tuplas> 0L &&  transaccion!= null) {
			this.attrs.put("procesados", transaccion.getProcesados());
			UIBackingUtilities.execute("janal.alert('Se termin\\u00F3 de procesar el archivo !\\u000DTotal de registros: "+ tuplas+ "\\u000DRegistros procesados: "+ transaccion.getProcesados()+ 
				(tuplas!= transaccion.getProcesados()? "\\u000D\\u000DEl total de filas del archivo es diferente al procesado, favor de verificarlo": "")+ "')");
		} // if	
    return regresar;
	} // doAceptar	
  
  public void doCompleto() {
		// JsfBase.addMessage("Detalle del mensaje", "Se proceso correctamente el catalogo !.", ETipoMensaje.INFORMACION);		
	} // doCompleto

	public void doChangeTipo() {
		// this.attrs.put("lotes", null);
		switch(this.masivo.getIdTipoMasivo().intValue()) {
			case 9: 
				this.categoria= ECargaMasiva.ESTACIONES;
				break;
			case 10: 
				this.categoria= ECargaMasiva.PERSONAL;
				break;
			case 11: 
				this.categoria= ECargaMasiva.PLANTILLAS;
				break;
		} // switch
		if(this.masivo!= null && this.masivo.isValid()) {
			this.attrs.put("procesados", 0);
			this.setXls(null);
			this.attrs.put("xls", ""); 
			this.attrs.put("tuplas", 0L);
			this.masivo = new TcManticMasivasArchivosDto(
				-1L, // Long idMasivaArchivo, 
				null, // String ruta, 
				categoria.getId(), // Long idTipoMasivo, 
				1L, // Long idMasivaEstatus, 
				null, // String nombre, 
				0L, // Long tamanio, 
				JsfBase.getIdUsuario(), // Long idUsuario, 
				8L, // Long idTipoArchivo, 
				0L, // Long tuplas, 
				null, // String observaciones, 
				null, // String alias, 
				JsfBase.getAutentifica().getEmpresa().getIdEmpresa(),
				1L,
				null
			);
		} // if
		this.toCheckRequerido();
	} // doChangeTipo

	private void toCheckRequerido() {
		if(this.masivo.getIdTipoMasivo().intValue()== ECargaMasiva.ESTACIONES.getId())
      UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:idContratoLote', {validaciones: 'requerido', mascara: 'libre'});");			
		else
      UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:idContratoLote', {validaciones: 'libre', mascara: 'libre'});");		
	}
	
	public String doMovimientos() {
		JsfBase.setFlashAttribute("idMasivaArchivo", ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("regreso", "importar");
		return "movimientos".concat(Constantes.REDIRECIONAR);
	} // doMovimientos
	
	public String doDetalles() {
		JsfBase.setFlashAttribute("idMasivaArchivo", ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("regreso", "importar");
		return "detalles".concat(Constantes.REDIRECIONAR);
	} // doDetalles	

	private void toLoadContratosLotes() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("lote", EFormatoDinamicos.MAYUSCULAS));
 			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
 			params.put("ejercicio", Fecha.getAnioActual()- 1);
			List<UISelectEntity> lotes= UIEntity.build("VistaContratosLotesDto", "lotes", params, columns);
  		this.attrs.put("lotes", lotes);
			if(this.attrs.get("ikContratoLote")!= null) {
				int index= lotes.indexOf(new UISelectEntity((Long)this.attrs.get("ikContratoLote")));
				if(index>= 0)
					this.attrs.put("idContratoLote", lotes.get(index));
				else
					this.attrs.put("idContratoLote", lotes.get(0));
			  this.attrs.put("ikContratoLote", null);
			} // if
			else
  		  this.attrs.put("idContratoLote", lotes.get(0));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}
	
	private void toLoadPrototipos() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
 			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			List<UISelectEntity> lotes= UIEntity.build("TcKeetPrototiposDto", "byEmpresa", params, columns);
  		this.attrs.put("plantillas", lotes);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}
	
	private void toLoadPlantillas() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("lote", EFormatoDinamicos.MAYUSCULAS));
 			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
 			params.put("ejercicio", Fecha.getAnioActual()- 1);
			List<UISelectEntity> plantillas= UIEntity.build("VistaContratosLotesDto", "lotes", params, columns);
  		this.attrs.put("plantillas", plantillas);
			if(this.attrs.get("ikContratoLote")!= null) {
				int index= plantillas.indexOf(new UISelectEntity((Long)this.attrs.get("ikContratoLote")));
				if(index>= 0)
					this.attrs.put("idContratoLote", plantillas.get(index));
				else
					this.attrs.put("idContratoLote", plantillas.get(0));
			  this.attrs.put("ikContratoLote", null);
			} // if
			else
  		  this.attrs.put("idContratoLote", plantillas.get(0));
    } // try
    catch (Exception e) {
      Error.mensaje(e);
			JsfBase.addMessageError(e);
    } // catch   
    finally {
      Methods.clean(columns);
      Methods.clean(params);
    } // finally
	}
	
	public String doExportar() {
		String regresar           = null;
		Map<String, Object> params= null;
		try {
			params=new HashMap<>();
			UISelectEntity idContratoLote= (UISelectEntity)this.attrs.get("idContratoLote");
			if(idContratoLote!= null) {
				List<UISelectEntity> lotes= (List<UISelectEntity>)this.attrs.get("lotes");
				idContratoLote= lotes.get(lotes.indexOf(idContratoLote));
				Estaciones estaciones=new Estaciones();
				estaciones.setKeyLevel(idContratoLote.toString("idEmpresa"), 0); // idEmpresa
				estaciones.setKeyLevel(idContratoLote.toString("ejercicio"), 1); // ejercicio
				estaciones.setKeyLevel(idContratoLote.toString("contrato"), 2); // orden del contrato
				estaciones.setKeyLevel(idContratoLote.toString("orden"), 3); // orden de contrato lote
				params.put("manzana", idContratoLote.toString("manzana"));
				params.put("lote", idContratoLote.toString("lote"));
				params.put("clave", estaciones.toKey(4));
        JsfBase.setFlashAttribute(Constantes.REPORTE_REFERENCIA, new ExportarXls(new Modelo((Map<String, Object>) ((HashMap)params).clone(), EExportacionXls.ESTACIONES.getProceso(), EExportacionXls.ESTACIONES.getIdXml(), EExportacionXls.ESTACIONES.getNombreArchivo()), EExportacionXls.ESTACIONES, "MANZANA,LOTE,CODIGO,NOMBRE,CANTIDAD,COSTO S/IVA,UNIDAD MEDIDA"));
			  JsfBase.getAutentifica().setMonitoreo(new Monitoreo());
			  regresar = "/Paginas/Reportes/excel".concat(Constantes.REDIRECIONAR);				
			} // if
			else
				JsfBase.addMessage("Error:", "No se tiene un lote seleccionado !", ETipoMensaje.ALERTA);			
		} // try
		catch (Exception e) {
			Error.mensaje(e);
			JsfBase.addMessageError(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	}
	
}
