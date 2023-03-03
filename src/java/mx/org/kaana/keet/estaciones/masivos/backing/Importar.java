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
import mx.org.kaana.libs.formato.Cadena;
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
			this.attrs.put("idLimpiar", 2L);
			this.attrs.put("procesados", 0L);
			this.attrs.put("idTipoMasivo", JsfBase.getFlashAttribute("idTipoMasivo")!= null? JsfBase.getFlashAttribute("idTipoMasivo"): ECargaMasiva.ESTACIONES.getId());
			if(ECargaMasiva.ESTACIONES.getId().equals((Long)this.attrs.get("idTipoMasivo")))
				this.categoria= ECargaMasiva.PRECIOS_CONVENIO;
			else
				this.categoria= ECargaMasiva.ESTACIONES;
			this.attrs.put("xls", ""); 
			this.attrs.put("tuplas", 0L);
			this.masivo = new TcManticMasivasArchivosDto(
				-1L, // Long idMasivaArchivo, 
				Configuracion.getInstance().getPropiedadSistemaServidor("masivos"), // String ruta, 
				this.categoria.getId(), // Long idTipoMasivo, 
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
  		this.toCheckRequerido();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init
  
	public void doTabChange(TabChangeEvent event) {
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
		UISelectEntity idSelect= null;
		try {
			this.masivo.setArchivo(this.getXls().getOriginal());
		  this.masivo.setObservaciones((String)this.attrs.get("observaciones"));
      if(ECargaMasiva.ESTACIONES.equals(this.categoria) || ECargaMasiva.MATERIALES.equals(this.categoria) || ECargaMasiva.CONTROLES.equals(this.categoria)) {
				LOG.error(this.attrs.get("izContratoLote"));
				List<UISelectEntity> lotes= (List<UISelectEntity>)this.attrs.get("lotes");
				Object[] items= (Object[])this.attrs.get("izContratoLote");
				int count= 1;
				for (Object item : items) {
					idSelect= lotes.get(lotes.indexOf(item));
					this.attrs.put("seleccionados", count+ " de "+ items.length);
					this.attrs.put("lote", idSelect.toString("clave")+ "-"+ idSelect.toString("codigo"));
					transaccion= new Transaccion(this.masivo, this.categoria, idSelect.getKey(), this.masivo.getIdTipoMasivo()== 9L || this.masivo.getIdTipoMasivo()== 12L || this.masivo.getIdTipoMasivo()== 16L? (Long)this.attrs.get("idLimpiar"): (Long)this.attrs.get("idEliminar"));
					if(tuplas> 0L && transaccion.ejecutar(EAccion.PROCESAR)) {
					} // if
					else
						JsfBase.addMessage("Error:", "Ocurrio un error en la cargar masiva del archivo !", ETipoMensaje.ERROR);		
					count++;
				} // for
				this.attrs.put("seleccionados", "0 de 0");
				this.attrs.put("lote", "");
			} // if
			else {
				switch (this.categoria) {
					case PLANTILLAS:
						idSelect= ((UISelectEntity)this.attrs.get("idPlantilla"));
						transaccion= new Transaccion(this.masivo, idSelect.getKey(), this.categoria, this.masivo.getIdTipoMasivo()== 9L? (Long)this.attrs.get("idLimpiar"): (Long)this.attrs.get("idEliminar"));
						break;
					case PRECIOS:
						transaccion= new Transaccion(this.masivo, this.categoria, ((UISelectEntity)this.attrs.get("idProveedor")).getKey(), -1L, -1L);
						break;
					case PRECIOS_CONVENIO:
						transaccion= new Transaccion(this.masivo, this.categoria, ((UISelectEntity)this.attrs.get("idProveedor")).getKey(), -1L, ((UISelectEntity)this.attrs.get("idCliente")).getKey());
						break;
				} // swtich
				if(tuplas> 0L && transaccion!= null && transaccion.ejecutar(EAccion.PROCESAR)) {
				} // if
				else
					JsfBase.addMessage("Error:", "Ocurrio un error en la cargar masiva del archivo !", ETipoMensaje.ERROR);		
			} // if	
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
		switch(this.masivo.getIdTipoMasivo().intValue()) {
			case 9: 
				this.categoria= ECargaMasiva.ESTACIONES;
		    this.toLoadContratosLotes();
				break;
			case 10: 
				this.categoria= ECargaMasiva.PERSONAL;
				break;
			case 11: 
				this.categoria= ECargaMasiva.PLANTILLAS;
		    this.toLoadPrototipos();
				break;
			case 12: 
				this.categoria= ECargaMasiva.MATERIALES;
		    this.toLoadContratosLotes();
				break;
			case 13: 
				this.categoria= ECargaMasiva.PRECIOS;
				this.toLoadProveedores();
				break;
			case 14: 
				this.categoria= ECargaMasiva.PRECIOS_CONVENIO;
				this.toLoadProveedores();
				this.toLoadClientes();
				break;
			case 15: 
				this.categoria= ECargaMasiva.MATERIAL;
				break;
			case 16: 
				this.categoria= ECargaMasiva.CONTROLES;
		    this.toLoadContratosLotes();
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
				this.categoria.getId(), // Long idTipoMasivo, 
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
    UIBackingUtilities.execute(
			"janal.renovate('contenedorGrupos\\\\:idContratoLote', {validaciones: 'libre', mascara: 'libre'});"+
			"janal.renovate('contenedorGrupos\\\\:idPlantilla', {validaciones: 'libre', mascara: 'libre'});"+
			"janal.renovate('contenedorGrupos\\\\:idProveedor', {validaciones: 'libre', mascara: 'libre'});"+
			"janal.renovate('contenedorGrupos\\\\:idCliente', {validaciones: 'libre', mascara: 'libre'});"
	  );		
		UIBackingUtilities.update("catalogo @(.involucrados) @(.importado) @(.janal-upload-frame)");
		switch(this.masivo.getIdTipoMasivo().intValue()) {
			case 9: 
				this.categoria= ECargaMasiva.ESTACIONES;
        UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:idContratoLote', {validaciones: 'requerido', mascara: 'libre'});");			
				break;
			case 10: 
				this.categoria= ECargaMasiva.PERSONAL;
				break;
			case 11: 
				this.categoria= ECargaMasiva.PLANTILLAS;
        UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:idPlantilla', {validaciones: 'requerido', mascara: 'libre'});");			
				break;
			case 12: 
				this.categoria= ECargaMasiva.MATERIALES;
        UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:idContratoLote', {validaciones: 'requerido', mascara: 'libre'});");			
				break;
			case 13: 
				this.categoria= ECargaMasiva.PRECIOS;
        UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:idProveedor', {validaciones: 'requerido', mascara: 'libre'});");			
				break;
			case 14: 
				this.categoria= ECargaMasiva.PRECIOS_CONVENIO;
        UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:idProveedor', {validaciones: 'requerido', mascara: 'libre'});janal.renovate('contenedorGrupos\\\\:idCliente', {validaciones: 'requerido', mascara: 'libre'});");			
				break;
			case 16: 
				this.categoria= ECargaMasiva.CONTROLES;
        UIBackingUtilities.execute("janal.renovate('contenedorGrupos\\\\:idContratoLote', {validaciones: 'requerido', mascara: 'libre'});");			
				break;
		} // switch
	}
	
	public String doMovimientos() {
		JsfBase.setFlashAttribute("idMasivaArchivo", ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("retorno", this.attrs.get("regreso"));
		JsfBase.setFlashAttribute("regreso", "/Paginas/Keet/Estaciones/Masivos/importar");
		return "/Paginas/Keet/Estaciones/Masivos/movimientos".concat(Constantes.REDIRECIONAR);
	} // doMovimientos
	
	public String doDetalles() {
		JsfBase.setFlashAttribute("idMasivaArchivo", ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("retorno", this.attrs.get("regreso"));
		JsfBase.setFlashAttribute("regreso", "/Paginas/Keet/Estaciones/Masivos/importar");
		return "/Paginas/Keet/Estaciones/Masivos/detalles".concat(Constantes.REDIRECIONAR);
	} // doDetalles	

	private void toLoadContratosLotes() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
		List<UISelectEntity> contratos= null;
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("nombre", EFormatoDinamicos.MAYUSCULAS));
 			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
 			params.put("ejercicio", Fecha.getAnioActual()- 1);
			if(this.attrs.get("contratos")== null)
			  contratos= UIEntity.build("VistaContratosLotesDto", "contratos", params, columns, Constantes.SQL_TODOS_REGISTROS);
			else
				contratos= (List<UISelectEntity>)this.attrs.get("contratos");
  		this.attrs.put("contratos", contratos);
      if(contratos!= null && !contratos.isEmpty())
        this.attrs.put("idContrato", contratos.get(0));
      else
        this.attrs.put("idContrato", new UISelectEntity(-1L));
      params.put("idContrato", ((UISelectEntity)this.attrs.get("idContrato")).getKey());
      this.doLoadLotes();
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
			if(this.attrs.get("plantillas")== null)
    		this.attrs.put("plantillas", UIEntity.build("TcKeetPrototiposDto", "byEmpresa", params, columns));
	    this.attrs.put("idPlantilla", new UISelectEntity("-1"));
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
	
  public void toLoadProveedores() {
    Map<String, Object> params= null;
    try {
      Entity entity= new Entity(Constantes.TOP_OF_ITEMS);
      entity.add("clave", "TODOS");
      entity.add("rfc", "TODOS");
      entity.add("razonSocial", "TODOS");
      UISelectEntity todos= new UISelectEntity(entity);
      params = new HashMap();
      params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			if(this.attrs.get("proveedores")== null) {
        List<UISelectEntity> proveedores= UIEntity.build("TcManticProveedoresDto", "sucursales", params);
        proveedores.add(todos);
        this.attrs.put("proveedores", proveedores);
      } // if  
      this.attrs.put("idProveedor", todos);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch
    finally {
      Methods.clean(params);
    } // finally
  } // doLoadProveedores	
	
	public void toLoadClientes() {
    Map<String, Object> params= null;
		try {
      Entity entity= new Entity(Constantes.TOP_OF_ITEMS);
      entity.add("clave", "TODOS");
      entity.add("rfc", "TODOS");
      entity.add("razonSocial", "TODOS");
      UISelectEntity todos= new UISelectEntity(entity);
      params = new HashMap();
      params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			if(this.attrs.get("clientes")== null) {
        List<UISelectEntity> clientes= UIEntity.seleccione("TcManticClientesDto", "sucursales", params, "clave");
        clientes.add(todos);
        this.attrs.put("clientes", clientes);
      } // if  
      this.attrs.put("idCliente", todos);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(params);
    } // finally
	} // toLoadClientes
	
	public String doExportar() {
		String regresar                = null;
		String nivlePrototipo          = null;
		Map<String, Object> params     = null;
		Estaciones estaciones          = null;
		List<UISelectEntity> plantillas= null;
		List<UISelectEntity> lotes     = null;
		UISelectEntity idContratoLote  = null;
		UISelectEntity prototipo       = null;
		Object[] items                 = null; 
		try {
			params    = new HashMap<>();
			estaciones= new Estaciones();
			switch (this.categoria) {
				case ESTACIONES:
					lotes= (List<UISelectEntity>)this.attrs.get("lotes");
				  items= (Object[])this.attrs.get("izContratoLote");
          if(lotes!= null && !lotes.isEmpty() && items!= null) 
					  idContratoLote= lotes.get(lotes.indexOf(items[0]));
					if(idContratoLote!= null) {
						estaciones.setKeyLevel(idContratoLote.toString("idEmpresa"), 0); // idEmpresa
						estaciones.setKeyLevel(idContratoLote.toString("ejercicio"), 1); // ejercicio
						estaciones.setKeyLevel(idContratoLote.toString("contrato"), 2); // orden del contrato
						estaciones.setKeyLevel(idContratoLote.toString("orden"), 3); // orden de contrato lote
						params.put("manzana", idContratoLote.toString("manzana"));
						params.put("lote", idContratoLote.toString("lote"));
						params.put("clave", estaciones.toKey(4));
						JsfBase.setFlashAttribute(Constantes.REPORTE_REFERENCIA, new ExportarXls(new Modelo((Map<String, Object>) ((HashMap)params).clone(), EExportacionXls.ESTACIONES.getProceso(), EExportacionXls.ESTACIONES.getIdXml(), EExportacionXls.ESTACIONES.getNombreArchivo()), EExportacionXls.ESTACIONES, "MANZANA,LOTE,CODIGO,NOMBRE,CANTIDAD,COSTO S/IVA,UNIDAD MEDIDA,INICIO,TERMINO"));
						JsfBase.getAutentifica().setMonitoreo(new Monitoreo());
					} // if
			    else
				    JsfBase.addMessage("Error:", "No se tiene un lote seleccionado !", ETipoMensaje.ALERTA);	
				break;
				case PLANTILLAS:
					  prototipo= ((UISelectEntity)this.attrs.get("idPlantilla"));
					  plantillas= (List<UISelectEntity>)this.attrs.get("plantillas");
						prototipo= plantillas.get(plantillas.indexOf(prototipo));
						nivlePrototipo= Cadena.rellenar(prototipo.getKey().toString(), 3, '0', true);
						nivlePrototipo= nivlePrototipo.substring(nivlePrototipo.length()-3, nivlePrototipo.length());
						estaciones.setKeyLevel(prototipo.toString("idEmpresa"), 0);       // idEmpresa
						estaciones.setKeyLevel(String.valueOf(Fecha.getAnioActual()), 1); // ejercicio
						estaciones.setKeyLevel("999", 2);                                 // 999
						estaciones.setKeyLevel(nivlePrototipo, 3);                        // idPrototipo a 3 digitos
						params.put("clave", estaciones.toKey(4));
						JsfBase.setFlashAttribute(Constantes.REPORTE_REFERENCIA, new ExportarXls(new Modelo((Map<String, Object>) ((HashMap)params).clone(), EExportacionXls.ESTACIONES.getProceso(), EExportacionXls.ESTACIONES.getIdXml(), EExportacionXls.ESTACIONES.getNombreArchivo()), EExportacionXls.ESTACIONES, "MANZANA,LOTE,CODIGO,NOMBRE,CANTIDAD,COSTO S/IVA,UNIDAD MEDIDA"));
						JsfBase.getAutentifica().setMonitoreo(new Monitoreo());
					break;
				case MATERIALES:
					lotes= (List<UISelectEntity>)this.attrs.get("lotes");
				  items= (Object[])this.attrs.get("izContratoLote");
          if(lotes!= null && !lotes.isEmpty() && items!= null) 
					  idContratoLote= lotes.get(lotes.indexOf(items[0]));
					if(idContratoLote!= null) {
						estaciones.setKeyLevel(idContratoLote.toString("idEmpresa"), 0); // idEmpresa
						estaciones.setKeyLevel(idContratoLote.toString("ejercicio"), 1); // ejercicio
						estaciones.setKeyLevel(idContratoLote.toString("contrato"), 2); // orden del contrato
						estaciones.setKeyLevel(idContratoLote.toString("orden"), 3); // orden de contrato lote
						params.put("manzana", idContratoLote.toString("manzana"));
						params.put("lote", idContratoLote.toString("lote"));
						params.put("clave", estaciones.toKey(4));
						JsfBase.setFlashAttribute(Constantes.REPORTE_REFERENCIA, new ExportarXls(new Modelo((Map<String, Object>) ((HashMap)params).clone(), EExportacionXls.MATERIALES.getProceso(), EExportacionXls.MATERIALES.getIdXml(), EExportacionXls.MATERIALES.getNombreArchivo()), EExportacionXls.MATERIALES, "MANZANA,LOTE,CODIGO,NOMBRE,CANTIDAD,COSTO S/IVA,UNIDAD MEDIDA"));
						JsfBase.getAutentifica().setMonitoreo(new Monitoreo());
					} // if
			    else
				    JsfBase.addMessage("Error:", "No se tiene un lote seleccionado !", ETipoMensaje.ALERTA);	
		  		break;
				case PRECIOS:
					params.put("idProveedor", this.attrs.get("idProveedor"));
					JsfBase.setFlashAttribute(Constantes.REPORTE_REFERENCIA, new ExportarXls(new Modelo((Map<String, Object>) ((HashMap)params).clone(), EExportacionXls.PRECIOS.getProceso(), EExportacionXls.PRECIOS.getIdXml(), EExportacionXls.PRECIOS.getNombreArchivo()), EExportacionXls.PRECIOS, "RFC,RAZON SOCIAL,CLAVE,AUXILIAR,MATERIAL,PRECIO BASE,PRECIO LISTA,PRECIO ESPECIAL"));
					JsfBase.getAutentifica().setMonitoreo(new Monitoreo());
  				break;
				case PRECIOS_CONVENIO:
					params.put("idProveedor", this.attrs.get("idProveedor"));
					params.put("idCliente", this.attrs.get("idCliente"));
					JsfBase.setFlashAttribute(Constantes.REPORTE_REFERENCIA, new ExportarXls(new Modelo((Map<String, Object>) ((HashMap)params).clone(), EExportacionXls.PRECIOS_CONVENIO.getProceso(), EExportacionXls.PRECIOS_CONVENIO.getIdXml(), EExportacionXls.PRECIOS_CONVENIO.getNombreArchivo()), EExportacionXls.PRECIOS_CONVENIO, "RFC PROVEEDOR,PROVEEDOR,RFC CLIENTE,CLIENTE,CLAVE,AUXILIAR,MATERIAL,PRECIO CONVENIO"));
					JsfBase.getAutentifica().setMonitoreo(new Monitoreo());
	  			break;
				case CONTROLES:
					lotes= (List<UISelectEntity>)this.attrs.get("lotes");
				  items= (Object[])this.attrs.get("izContratoLote");
          if(lotes!= null && !lotes.isEmpty() && items!= null) 
					  idContratoLote= lotes.get(lotes.indexOf(items[0]));
					if(idContratoLote!= null) {
						estaciones.setKeyLevel(idContratoLote.toString("idEmpresa"), 0); // idEmpresa
						estaciones.setKeyLevel(idContratoLote.toString("ejercicio"), 1); // ejercicio
						estaciones.setKeyLevel(idContratoLote.toString("contrato"), 2); // orden del contrato
						estaciones.setKeyLevel(idContratoLote.toString("orden"), 3); // orden de contrato lote
						params.put("manzana", idContratoLote.toString("manzana"));
						params.put("lote", idContratoLote.toString("lote"));
						params.put("clave", estaciones.toKey(4));
						JsfBase.setFlashAttribute(Constantes.REPORTE_REFERENCIA, new ExportarXls(new Modelo((Map<String, Object>) ((HashMap)params).clone(), EExportacionXls.CONTROLES.getProceso(), EExportacionXls.CONTROLES.getIdXml(), EExportacionXls.CONTROLES.getNombreArchivo()), EExportacionXls.CONTROLES, "MANZANA,LOTE,CODIGO,NOMBRE,CANTIDAD,COSTO S/IVA,UNIDAD MEDIDA,INICIO,TERMINO"));
						JsfBase.getAutentifica().setMonitoreo(new Monitoreo());
					} // if
			    else
				    JsfBase.addMessage("Error:", "No se tiene un lote seleccionado !", ETipoMensaje.ALERTA);	
				break;
			} // swtich
			regresar = "/Paginas/Reportes/excel".concat(Constantes.REDIRECIONAR);				
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

  public void doLoadLotes() {
		List<Columna> columns     = null;
    Map<String, Object> params= new HashMap<>();
		List<UISelectEntity> lotes= null;
    try {
			columns= new ArrayList<>();
      columns.add(new Columna("clave", EFormatoDinamicos.MAYUSCULAS));
			columns.add(new Columna("lote", EFormatoDinamicos.MAYUSCULAS));
 			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
 			params.put("ejercicio", Fecha.getAnioActual()- 1);
      params.put("idContrato", ((UISelectEntity)this.attrs.get("idContrato")).getKey());
  	  lotes= UIEntity.build("VistaContratosLotesDto", "lotes", params, columns, Constantes.SQL_TODOS_REGISTROS);
  		this.attrs.put("lotes", lotes);
			if(this.attrs.get("ikContratoLote")!= null) {
				int index= lotes.indexOf(new UISelectEntity((Long)this.attrs.get("ikContratoLote")));
				if(index>= 0)
					this.attrs.put("izContratoLote", new Object[] {lotes.get(index)});
				else
					this.attrs.put("izContratoLote", new Object[] {lotes.get(0)});
			  this.attrs.put("ikContratoLote", null);
			} // if
			else
  		  this.attrs.put("izContratoLote", new Object[] {lotes.get(0)});
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
  
}
