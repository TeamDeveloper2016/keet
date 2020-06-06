package mx.org.kaana.keet.catalogos.contratos.vales.especiales.backing;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.catalogos.contratos.vales.beans.Vale;
import mx.org.kaana.keet.catalogos.contratos.vales.especiales.reglas.Transaccion;
import mx.org.kaana.keet.db.dto.TcKeetValesDto;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.pagina.UISelect;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.compras.ordenes.enums.EOrdenes;
import mx.org.kaana.mantic.enums.ETipoVenta;
import mx.org.kaana.mantic.ventas.backing.Accion;
import mx.org.kaana.mantic.ventas.beans.ArticuloVenta;

@Named(value = "keetCatalogosContratosValesEspecialesConceptos")
@ViewScoped
public class Conceptos extends Accion implements Serializable {

	private static final long serialVersionUID= 2847354766000406350L;  		
	private static final String FLUJO_INICIO  = "filtro";	
	
  @PostConstruct
  @Override
  protected void init() {		
    EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;
		Entity figura            = null;
		Entity seleccionado      = null;
		Long idDepartamento      = null;
		String flujo             = null;
    try {
			this.attrs.put("isAdmin", JsfBase.isAdminEncuestaOrAdmin());						
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());	
			this.attrs.put("georreferencia", JsfBase.getFlashAttribute("georreferencia"));
			this.attrs.put("opcionAdicional", JsfBase.getFlashAttribute("opcionAdicional"));
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");			
			figura= (Entity) JsfBase.getFlashAttribute("figura");	
			seleccionado= (Entity) JsfBase.getFlashAttribute("seleccionado");	
			idDepartamento= (Long)JsfBase.getFlashAttribute("idDepartamento");	
			flujo= (String) JsfBase.getFlashAttribute("flujo");
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("figura", figura);      
			this.attrs.put("seleccionadoPivote", seleccionado);      			
			this.attrs.put("idDesarrollo", idDesarrollo);      
			this.attrs.put("idDepartamento", idDepartamento);      			
			this.attrs.put("nombreConcepto", "");    
			this.attrs.put("totalMateriales", 0L);
			loadCatalogos();					
			loadTiposVales();
			initPadre();
			if(flujo.equals(FLUJO_INICIO))
				this.attrs.put("accion", EAccion.AGREGAR);							
			else{
				this.attrs.put("accion", EAccion.MODIFICAR);
				this.attrs.put("idVale", JsfBase.getFlashAttribute("idVale"));
				this.attrs.put("qr", JsfBase.getFlashAttribute("qr"));
				doLoad();
			} // else			
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

	private void initPadre() throws Exception{
		try {
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());			
			this.attrs.put("isMatriz", JsfBase.getAutentifica().getEmpresa().isMatriz());			
			setPrecio(ETipoVenta.MENUDEO.getNombreCampo());
			setTipoOrden(EOrdenes.NORMAL);						
			this.attrs.put("idVenta", -1L);
      this.attrs.put("accion", EAccion.AGREGAR);      
      this.attrs.put("idCliente", -1L);			
      this.attrs.put("isPesos", false);
			this.attrs.put("sinIva", true);
			this.attrs.put("buscaPorCodigo", true);
			this.attrs.put("activeLogin", false);
			this.attrs.put("autorized", false);
			this.attrs.put("expirada", false);
			this.attrs.put("isIndividual", true);
			this.attrs.put("descuentoIndividual", 0);
			this.attrs.put("descuentoGlobal", 0);
			this.attrs.put("tipoDescuento", MENUDEO);						
			this.attrs.put("busquedaTicketAbierto", "");
			this.attrs.put("decuentoAutorizadoActivo", false);
			this.attrs.put("tipoDecuentoAutorizadoActivo", MENUDEO);
			this.attrs.put("ticketLock", -1L);						
			super.doLoad();
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // initPadre
	
	private void loadCatalogos(){
		Entity contrato          = null;
		Entity contratoLote      = null;
		Map<String, Object>params= null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos.id_contrato=".concat(((Entity)this.attrs.get("seleccionadoPivote")).toString("idContrato")));
			contrato= (Entity) DaoFactory.getInstance().toEntity("VistaContratosLotesDto", "principal", params);
			this.attrs.put("contrato", contrato);
			params.clear();
			params.put(Constantes.SQL_CONDICION, "tc_keet_contratos_lotes.id_contrato_lote=".concat(((Entity)this.attrs.get("seleccionadoPivote")).getKey().toString()));
			contratoLote= (Entity) DaoFactory.getInstance().toEntity("TcKeetContratosLotesDto", "row", params);
			this.attrs.put("contratoLote", contratoLote);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
		finally{
			Methods.clean(params);
		} // finally	
	} // loadCatalogos				
	
	private void loadTiposVales(){
		List<UISelectItem> tiposVales= null;
		Map<String, Object>params    = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_tipo_vale != 1");
			tiposVales= UISelect.seleccione("TcKeetTiposValesDto", "row", params, "nombre", EFormatoDinamicos.MAYUSCULAS);
			this.attrs.put("tiposVales", tiposVales);
			this.attrs.put("tipoVale", UIBackingUtilities.toFirstKeySelectItem(tiposVales));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally{
			Methods.clean(params);
		} // finally
	} // loadTiposvales
	
  @Override
  public void doLoad() {			
		TcKeetValesDto vale         = null;
		List<ArticuloVenta> material= null;
    try {    			
			vale= (TcKeetValesDto) DaoFactory.getInstance().findById(TcKeetValesDto.class, Long.valueOf(this.attrs.get("idVale").toString()));
			this.attrs.put("justificacion", vale.getJustificacion());
			this.attrs.put("tipoVale", vale.getIdTipoVale());
			material=(List<ArticuloVenta>)DaoFactory.getInstance().toEntitySet(ArticuloVenta.class, "VistaValesEspecialesDto", "detalle", vale.toMap());
			material.add(new ArticuloVenta(-1L));
			this.getAdminOrden().setArticulos(material);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch    
  } // doLoad	  	
	
	private String toClaveEstacion(){
		StringBuilder regresar= null;
		try {			
			regresar= new StringBuilder();
			regresar.append(Cadena.rellenar(this.attrs.get("idEmpresa").toString(), 3, '0', true));
			regresar.append(Fecha.getAnioActual());
			regresar.append(Cadena.rellenar(((Entity)this.attrs.get("seleccionadoPivote")).toString("ordenContrato"), 3, '0', true));
			regresar.append(Cadena.rellenar(((Entity)this.attrs.get("seleccionadoPivote")).toString("orden"), 3, '0', true));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar.toString();
	} // toClaveEstacion				
	
	@Override
	public String doAceptar() {
    String regresar        = null;    		
		Transaccion transaccion= null;		
		EAccion accion         = null;
    try {											
			if(!this.getAdminOrden().getArticulos().isEmpty() && getAdminOrden().getArticulos().size()>0 && getAdminOrden().getArticulos().get(0).isValid()){
				accion= (EAccion) this.attrs.get("accion");
				transaccion= accion.equals(EAccion.AGREGAR) ? new Transaccion(loadVale()) : new Transaccion(loadVale(), Long.valueOf(this.attrs.get("idVale").toString()));
				if(transaccion.ejecutar(accion)){
					JsfBase.addMessage("Captura de material", "Se realizó la captura de material de forma correcta.", ETipoMensaje.INFORMACION);
					if(accion.equals(EAccion.AGREGAR)){
						JsfBase.setFlashAttribute("idVale", transaccion.getIdVale());
						JsfBase.setFlashAttribute("qr", transaccion.getQr());				
					} // if
					else{
						JsfBase.setFlashAttribute("idVale", this.attrs.get("idVale"));
						JsfBase.setFlashAttribute("qr", this.attrs.get("qr"));				
					} // else
					toSetFlash();
					regresar= "resumen".concat(Constantes.REDIRECIONAR);
				} // if
				else
					JsfBase.addMessage("Captura de materiales", "Ocurrió un error al realizar la captura de material.", ETipoMensaje.ERROR);			
			}
			else
				JsfBase.addMessage("Captura de materiales", "No se han capturado materiales.", ETipoMensaje.ERROR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doPagina  
	
	private Vale loadVale(){
		Vale regresar      = null;
		Entity figura      = null;
		Long idFigura      = -1L;
		Entity seleccionado= null;
		try {
			regresar= new Vale();
			figura= (Entity) this.attrs.get("figura");
			seleccionado= (Entity) this.attrs.get("seleccionadoPivote");
			idFigura= figura.toLong("tipo").equals(1L) ? seleccionado.toLong("idContratoLoteContratista") : seleccionado.toLong("idContratoLoteProveedor");
			regresar.setIdFigura(idFigura);
			regresar.setIdTipoVale(Long.valueOf(this.attrs.get("tipoVale").toString()));			
			regresar.setTipoFigura(figura.toLong("tipo"));
			regresar.setNombreFigura(figura.toString("nombreCompleto"));
			regresar.setArticulos(this.getAdminOrden().getArticulos());
			regresar.setIdDesarrollo((Long)this.attrs.get("idDesarrollo"));
			regresar.setJustificacion(this.attrs.get("justificacion").toString());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // loadVale
	
	private void toSetFlash(){
		JsfBase.setFlashAttribute("claveEstacion", toClaveEstacion());									
		JsfBase.setFlashAttribute("opcionResidente", this.attrs.get("opcionResidente"));									
		JsfBase.setFlashAttribute("figura", this.attrs.get("figura"));									
		JsfBase.setFlashAttribute("seleccionado", this.attrs.get("seleccionadoPivote"));									
		JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));									
		JsfBase.setFlashAttribute("idDepartamento", this.attrs.get("idDepartamento"));											
		JsfBase.setFlashAttribute("georreferencia", this.attrs.get("georreferencia"));
		JsfBase.setFlashAttribute("opcionAdicional", this.attrs.get("opcionAdicional"));			
	} // toSetFlash
	
	@Override
	public String doCancelar() {
    String regresar          = null;    
		EOpcionesResidente opcion= null;				
		Transaccion transaccion  = null;
		boolean respuesta        = true;
    try {			
			if(this.attrs.get("idVale")!= null){
				transaccion= new Transaccion(Long.valueOf(this.attrs.get("idVale").toString()));
				respuesta= transaccion.ejecutar(EAccion.DEPURAR);
			} // if
			if(respuesta){
				opcion= ((EOpcionesResidente)this.attrs.get("opcionResidente"));
				JsfBase.setFlashAttribute("idDesarrollo", this.attrs.get("idDesarrollo"));
				JsfBase.setFlashAttribute("idDesarrolloProcess", this.attrs.get("idDesarrollo"));
				JsfBase.setFlashAttribute("figura", this.attrs.get("figura"));
				JsfBase.setFlashAttribute("idDepartamento", this.attrs.get("idDepartamento"));									
				JsfBase.setFlashAttribute("opcionResidente", opcion);			
				JsfBase.setFlashAttribute("opcionAdicional", this.attrs.get("opcionAdicional"));			
				regresar= "filtro".concat(Constantes.REDIRECIONAR);			
			} // if
			else
				JsfBase.addMessage("Cancelar vale", "Ocurrió un error al cacelar el vale", ETipoMensaje.ERROR);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar		
	
	@Override
	public void doSearchArticulo(Long idArticulo, Integer index) {}
}