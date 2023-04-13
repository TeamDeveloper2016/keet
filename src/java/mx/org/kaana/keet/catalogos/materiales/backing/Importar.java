package mx.org.kaana.keet.catalogos.materiales.backing;

import java.io.Serializable;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.enums.ETipoMensaje;
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
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.catalogos.comun.IBaseImportar;
import mx.org.kaana.keet.catalogos.materiales.reglas.Transaccion;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.mantic.catalogos.masivos.enums.ECargaMasiva;
import mx.org.kaana.mantic.db.dto.TcManticMasivasArchivosDto;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.TabChangeEvent;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 01/03/2023
 *@time 07:51:53 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

@Named(value= "keetCatalogosMaterialesImportar")
@ViewScoped
public class Importar extends IBaseImportar implements Serializable {

	private static final Log LOG=LogFactory.getLog(Importar.class);
  private static final long serialVersionUID= 318633488565639361L;

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
			this.attrs.put("formatos", Constantes.PATRON_IMPORTAR_MASIVO);
			this.attrs.put("idLimpiar", 2L);
			this.attrs.put("procesados", 0L);
			this.attrs.put("idTipoMasivo", JsfBase.getFlashAttribute("idTipoMasivo")!= null? JsfBase.getFlashAttribute("idTipoMasivo"): ECargaMasiva.PLANEACION.getId());
			this.categoria= ECargaMasiva.PLANEACION;
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
				JsfBase.getAutentifica().getEmpresa().getIdEmpresa(), // Long idEmpresa, 
				1L, // Long idPrincipal
				null // String archivo
			);
      this.toLoadContratos();
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
      this.masivo.setIdMasivaArchivo(-1L);
			this.masivo.setArchivo(this.getXls().getOriginal());
		  this.masivo.setObservaciones((String)this.attrs.get("observaciones"));
      idSelect= ((UISelectEntity)this.attrs.get("idContrato"));
      transaccion= new Transaccion(this.masivo, idSelect.getKey(), this.categoria, (Long)this.attrs.get("idLimpiar"), this.getXls().getIdArchivo());
      if(tuplas> 0L && transaccion.ejecutar(EAccion.PROCESAR)) {
        this.attrs.put("procesados", transaccion.getProcesados());
        UIBackingUtilities.execute("janal.alert('Se termin\\u00F3 de procesar el archivo !\\u000DTotal de registros: "+ tuplas+ "\\u000DRegistros procesados: "+ transaccion.getProcesados()+ 
          (tuplas!= transaccion.getProcesados()? "\\u000D\\u000DEl total de filas del archivo es diferente al procesado, favor de verificarlo": "")+ "')");
      } // if
      else
        JsfBase.addMessage("Error:", "Ocurrio un error en la cargar masiva del archivo !", ETipoMensaje.ERROR);		
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch
    return regresar;
	} // doAceptar	
  
  public void doCompleto() {
		// JsfBase.addMessage("Detalle del mensaje", "Se proceso correctamente el catalogo !", ETipoMensaje.INFORMACION);		
	} // doCompleto

	public String doMovimientos() {
		JsfBase.setFlashAttribute("idMasivaArchivo", ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("retorno", this.attrs.get("regreso"));
		JsfBase.setFlashAttribute("regreso", "/Paginas/Keet/Catalogos/Materiales/importar");
		return "/Paginas/Keet/Estaciones/Masivos/movimientos".concat(Constantes.REDIRECIONAR);
	} // doMovimientos
	
	public String doDetalles() {
		JsfBase.setFlashAttribute("idMasivaArchivo", ((Entity)this.attrs.get("seleccionado")).getKey());
		JsfBase.setFlashAttribute("retorno", this.attrs.get("regreso"));
		JsfBase.setFlashAttribute("regreso", "/Paginas/Keet/Catalogos/Materiales/importar");
		return "/Paginas/Keet/Estaciones/Masivos/detalles".concat(Constantes.REDIRECIONAR);
	} // doDetalles	

	public String doExportar() {
		String regresar           = null;
		Map<String, Object> params= new HashMap<>();
		try {
      
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

	private void toLoadContratos() {
		List<Columna> columns     = new ArrayList<>();
    Map<String, Object> params= new HashMap<>();
		List<UISelectEntity> contratos= null;
    try {
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
