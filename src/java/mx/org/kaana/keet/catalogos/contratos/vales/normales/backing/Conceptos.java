package mx.org.kaana.keet.catalogos.contratos.vales.normales.backing;

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
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.ETipoMensaje;
import mx.org.kaana.keet.catalogos.contratos.vales.beans.DetalleVale;
import mx.org.kaana.keet.catalogos.contratos.vales.normales.reglas.Transaccion;
import mx.org.kaana.keet.catalogos.contratos.vales.beans.MaterialVale;
import mx.org.kaana.keet.catalogos.contratos.vales.beans.Vale;
import mx.org.kaana.keet.catalogos.contratos.vales.normales.reglas.BuildMateriales;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.keet.enums.ETiposVales;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.reflection.Methods;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.CheckboxTreeNode;
import org.primefaces.model.TreeNode;

@Named(value = "keetCatalogosContratosValesNormalesConceptos")
@ViewScoped
public class Conceptos extends IBaseFilter implements Serializable {

	private static final long serialVersionUID= 2847354766000406350L;  	
	private static final Long NIVEL_CONCEPTO  = 6L;
	private static final String FLUJO_INICIO  = "filtro";
  
	private TreeNode treeConceptos;
	private TreeNode[] selectedNodes;
	private List<MaterialVale> materiales;
	private List<DetalleVale> detalle;	
	private List<DetalleVale> padres;	

	public TreeNode getTreeConceptos() {
		return treeConceptos;
	}

	public void setTreeConceptos(TreeNode treeConceptos) {
		this.treeConceptos = treeConceptos;
	}	

	public TreeNode[] getSelectedNodes() {
		return selectedNodes;
	}

	public void setSelectedNodes(TreeNode[] selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	public List<MaterialVale> getMateriales() {
		return materiales;
	}

	public void setMateriales(List<MaterialVale> materiales) {
		this.materiales = materiales;
	}	
	
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
			preLoad();
			if(flujo.equals(FLUJO_INICIO)){
				this.attrs.put("accion", EAccion.AGREGAR);
				doLoad();			
			} // if
			else{
				this.attrs.put("accion", EAccion.MODIFICAR);
				this.attrs.put("idVale", JsfBase.getFlashAttribute("idVale"));
				this.attrs.put("qr", JsfBase.getFlashAttribute("qr"));
				doLoadExistente();
			} // else
    } // try // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

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
	
	private void preLoad(){
		this.materiales= new ArrayList<>();
		this.detalle= new ArrayList<>();
		this.padres= new ArrayList<>();
		this.treeConceptos= new CheckboxTreeNode("root", new MaterialVale(), null);
	} // preLoad
	
  @Override
  public void doLoad() {		
		BuildMateriales buildMaterial= null;
		List<MaterialVale>partidas   = null;
		List<MaterialVale>conceptos  = null;		
		List<TreeNode>removes        = null;		
		TreeNode partida             = null;
		TreeNode concepto            = null;
		int total                    = 0;
    try {    			
			buildMaterial= new BuildMateriales(this.toClaveEstacion(), Long.valueOf(this.attrs.get("idDepartamento").toString()));
			partidas= buildMaterial.toPartidas();			
			for(MaterialVale recordPartida: partidas){
				partida= new CheckboxTreeNode("partida", recordPartida, this.treeConceptos);
				conceptos= buildMaterial.toConceptos(recordPartida.getClave());
				for(MaterialVale recordConcepto: conceptos){
					concepto= new CheckboxTreeNode("concepto", recordConcepto, partida);
					concepto.setExpanded(false);
					concepto.setSelectable(!recordConcepto.isRegistrado());										
				} // for				
			} // for		
			total= this.treeConceptos.getChildCount();
			removes= new ArrayList<>();
			for(int count=0; count<total; count++){
				if(this.treeConceptos.getChildren().get(count).getChildCount() == 0)
					removes.add(this.treeConceptos.getChildren().get(count));					
			} // for
			for(TreeNode node: removes)
				this.treeConceptos.getChildren().remove(node);
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch    
  } // doLoad	  
	
	public void doLoadExistente(){
		BuildMateriales buildMaterial         = null;		
		List<MaterialVale>conceptosRegistrados= null;				
		int count=0;		
		try {
			doLoad();
			buildMaterial= new BuildMateriales(this.toClaveEstacion(), Long.valueOf(this.attrs.get("idDepartamento").toString()));
			conceptosRegistrados= buildMaterial.toConceptosRegistrados(Long.valueOf(this.attrs.get("idVale").toString()));
			for(MaterialVale concepto: conceptosRegistrados){
				for(TreeNode nodePartida: this.treeConceptos.getChildren()){
					count=0;
					for(TreeNode nodeConcepto: nodePartida.getChildren()){
						if(((MaterialVale)nodeConcepto.getData()).getIdMaterial().equals(concepto.getIdMaterial())){							
							nodePartida.setExpanded(true);
							nodePartida.setPartialSelected(true);
							((CheckboxTreeNode)nodeConcepto).setSelectable(true);
							((CheckboxTreeNode)nodeConcepto).setSelected(true);
							initNodeSelect(nodeConcepto);
							count++;
						} // if
					} // for
					if(count== nodePartida.getChildren().size()){
						nodePartida.setPartialSelected(false);
						nodePartida.setSelected(true);
					} // if
				} // for
			} // for
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
	} // doLoadExistente
	
	private void initNodeSelect(TreeNode seleccion) {		
		BuildMateriales buildMaterial= null;
		List<MaterialVale> articulos = null;
		MaterialVale pivote          = null;
		try {
			buildMaterial= new BuildMateriales("");			
			loadNodeParents(seleccion);
			articulos= buildMaterial.toMateriales(((MaterialVale)seleccion.getData()).getClave(), ((MaterialVale)seleccion.getData()).getNivel(), Long.valueOf(this.attrs.get("idVale").toString()));			
			for(MaterialVale material: articulos){
				if(this.materiales.isEmpty())
					this.materiales.add(material);				
				else{					
					if(this.materiales.contains(material)){
						pivote= this.materiales.get(this.materiales.indexOf(material));
						this.materiales.get(this.materiales.indexOf(material)).setCantidad(pivote.getCantidad() + material.getCantidad());
						this.materiales.get(this.materiales.indexOf(material)).setCosto(pivote.getCosto() + material.getCosto());
					} // if
					else
						this.materiales.add(material);									
				} // else			
				this.detalle.add(new DetalleVale(material));
			} // for						
			this.attrs.put("totalMateriales", this.materiales.size());
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
	} // onNodeSelect
	
	public void onNodeSelect(NodeSelectEvent event) {
		TreeNode seleccion           = null;
		BuildMateriales buildMaterial= null;
		List<MaterialVale> articulos = null;
		MaterialVale pivote          = null;
		try {
			buildMaterial= new BuildMateriales("");
			seleccion= event.getTreeNode();		
			loadNodeParents(seleccion);
			if(this.attrs.get("idVale")!= null)				
				articulos= buildMaterial.toMateriales(((MaterialVale)seleccion.getData()).getClave(), ((MaterialVale)seleccion.getData()).getNivel(), Long.valueOf(this.attrs.get("idVale").toString()));			
			else
				articulos= buildMaterial.toMateriales(((MaterialVale)seleccion.getData()).getClave(), ((MaterialVale)seleccion.getData()).getNivel());			
			for(MaterialVale material: articulos){
				if(this.materiales.isEmpty())
					this.materiales.add(material);				
				else{					
					if(this.materiales.contains(material)){
						pivote= this.materiales.get(this.materiales.indexOf(material));
						this.materiales.get(this.materiales.indexOf(material)).setCantidad(pivote.getCantidad() + material.getCantidad());
						this.materiales.get(this.materiales.indexOf(material)).setCosto(pivote.getCosto() + material.getCosto());
					} // if
					else
						this.materiales.add(material);									
				} // else			
				this.detalle.add(new DetalleVale(material));
			} // for						
			this.attrs.put("totalMateriales", this.materiales.size());
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
	} // onNodeSelect

	private void loadNodeParents(TreeNode selection){
		MaterialVale material= null;
		try {
			material= (MaterialVale) selection.getData();
			if(material.getNivel().equals(NIVEL_CONCEPTO))
				this.padres.add(new DetalleVale(material));
			else{
				for(TreeNode child: selection.getChildren()){
					if(!((MaterialVale) child.getData()).isRegistrado())
						this.padres.add(new DetalleVale((MaterialVale) child.getData()));
				} // for
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // loadNodeParents
	
	public void onNodeUnselect(NodeUnselectEvent event) {
		TreeNode seleccion                 = null;
		BuildMateriales buildMaterial      = null;
		List<MaterialVale> articulos       = null;
		List<MaterialVale> materialesPivote= null;
		MaterialVale pivote                = null;
		DetalleVale detalleVale            = null;
		try {
			buildMaterial= new BuildMateriales("");
			seleccion= event.getTreeNode();
			removeNodeParents(seleccion);
			if(this.attrs.get("idVale")!= null)				
				articulos= buildMaterial.toMateriales(((MaterialVale)seleccion.getData()).getClave(), ((MaterialVale)seleccion.getData()).getNivel(), Long.valueOf(this.attrs.get("idVale").toString()));			
			else
				articulos= buildMaterial.toMateriales(((MaterialVale)seleccion.getData()).getClave(), ((MaterialVale)seleccion.getData()).getNivel());			
			for(MaterialVale material: articulos){
				if(!this.materiales.isEmpty()){					
					if(this.materiales.contains(material)){
						pivote= this.materiales.get(this.materiales.indexOf(material));
						this.materiales.get(this.materiales.indexOf(material)).setCantidad(pivote.getCantidad() - material.getCantidad());
						this.materiales.get(this.materiales.indexOf(material)).setCosto(pivote.getCosto() - material.getCosto());
					} // if					
				} // if
				detalleVale= new DetalleVale(material);
				if(this.detalle.contains(detalleVale))
					this.detalle.remove(detalleVale);
			} // for	
			materialesPivote= new ArrayList<>();
			for(MaterialVale material: this.materiales){
				if(!material.getCantidad().equals(0D))
					materialesPivote.add(material);
			} // for
			this.materiales= materialesPivote;
			this.attrs.put("totalMateriales", this.materiales.size());			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
		} // catch		
	}	// onNodeUnselect
	
	private void removeNodeParents(TreeNode selection){
		MaterialVale material       = null;
		DetalleVale detalleSeleccion= null;
		try {
			material= (MaterialVale) selection.getData();
			detalleSeleccion= new DetalleVale(material);
			if(material.getNivel().equals(NIVEL_CONCEPTO)){
				if(this.padres.contains(detalleSeleccion))
					this.padres.remove(detalleSeleccion);
			} //if
			else{
				for(TreeNode child: selection.getChildren()){
					detalleSeleccion= new DetalleVale((MaterialVale) child.getData());
					if(this.padres.contains(detalleSeleccion))
						this.padres.remove(detalleSeleccion);
				} // for
			} // for
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
	} // loadNodeParents
	
	private String toClaveEstacion() {
		StringBuilder regresar= null;
		try {			
			regresar= new StringBuilder();
			regresar.append(Cadena.rellenar(this.attrs.get("idEmpresa").toString(), 3, '0', true));
      if(this.attrs.get("seleccionadoPivote")!= null && !((Entity)this.attrs.get("seleccionadoPivote")).isEmpty()) {
			  regresar.append(((Entity)this.attrs.get("seleccionadoPivote")).toString("ejercicio"));
			  regresar.append(Cadena.rellenar(((Entity)this.attrs.get("seleccionadoPivote")).toString("ordenContrato"), 3, '0', true));
			  regresar.append(Cadena.rellenar(((Entity)this.attrs.get("seleccionadoPivote")).toString("orden"), 3, '0', true));
      } // if  
      else {
			  regresar.append(Fecha.getAnioActual());
			  regresar.append(Cadena.rellenar("9", 6, '9', true));
      } // else 
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar.toString();
	} // toClaveEstacion				
	
	public String doAceptar() {
    String regresar        = null;    		
		Transaccion transaccion= null;		
		EAccion accion         = null;
    try {								
			if(this.materiales.size() > 0){
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
			} // if
			else
				JsfBase.addMessage("Captura de materiales", "No se ha seleccionado ningun concepto.", ETipoMensaje.ERROR);			
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
			regresar.setIdTipoVale(ETiposVales.NORMAL.getKey());
			regresar.setMateriales(this.materiales);
			regresar.setDetalle(this.detalle);
			regresar.setTipoFigura(figura.toLong("tipo"));
			regresar.setNombreFigura(figura.toString("nombreCompleto"));
			regresar.setPadres(this.padres);
			regresar.setIdDesarrollo((Long)this.attrs.get("idDesarrollo"));
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
}