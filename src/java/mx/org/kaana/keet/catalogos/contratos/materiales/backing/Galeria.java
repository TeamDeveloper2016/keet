package mx.org.kaana.keet.catalogos.contratos.materiales.backing;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.db.comun.sql.Value;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.kajool.reglas.comun.Columna;
import mx.org.kaana.keet.catalogos.desarrollos.beans.RegistroDesarrollo;
import mx.org.kaana.keet.enums.EOpcionesResidente;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIEntity;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.recurso.Configuracion;
import mx.org.kaana.libs.reflection.Methods;

@Named(value = "keetCatalogosContratosMaterialesGaleria")
@ViewScoped
public class Galeria extends IBaseFilter implements Serializable {

	private static final long serialVersionUID = 154600879172477099L;	
	private RegistroDesarrollo registroDesarrollo;				
	
	public RegistroDesarrollo getRegistroDesarrollo() {
		return registroDesarrollo;
	}

	public void setRegistroDesarrollo(RegistroDesarrollo registroDesarrollo) {
		this.registroDesarrollo = registroDesarrollo;
	}			

  @PostConstruct
  @Override
  protected void init() {		
    EOpcionesResidente opcion= null;
		Long idDesarrollo        = null;
    try {
			this.attrs.put("idEmpresa", JsfBase.getAutentifica().getEmpresa().getIdEmpresa());				
			this.attrs.put("opcionAdicional", JsfBase.getFlashAttribute("opcionAdicional"));
			opcion= (EOpcionesResidente) JsfBase.getFlashAttribute("opcionResidente");
			idDesarrollo= (Long) JsfBase.getFlashAttribute("idDesarrollo");			
			this.attrs.put("opcionResidente", opcion);
			this.attrs.put("idDesarrollo", idDesarrollo);
			this.attrs.put("figura", (UISelectEntity) JsfBase.getFlashAttribute("figura"));
			this.attrs.put("seleccionadoPivote", (Entity) JsfBase.getFlashAttribute("seleccionado"));
			this.attrs.put("seleccionado", (Entity) JsfBase.getFlashAttribute("seleccionado"));			
			this.attrs.put("pathPivote", File.separator.concat((Configuracion.getInstance().getEtapaServidor().name().toLowerCase())).concat("/").concat("vales").concat("/"));						
			loadCatalogos();
			doLoad();
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init	
	
	private void loadCatalogos() throws Exception {		
		try {			
			this.registroDesarrollo= new RegistroDesarrollo((Long)this.attrs.get("idDesarrollo"));      
			this.attrs.put("domicilio", toDomicilio());									
		} // try    
		catch(Exception e){
			throw(e);
		} // catch
	} // loadCatalogos	
	
	private String toDomicilio(){
		StringBuilder regresar= null;
		try {
			regresar= new StringBuilder();
			regresar.append(this.registroDesarrollo.getDomicilio().getCalle()).append(" , ");
			if(!Cadena.isVacio(this.registroDesarrollo.getDomicilio().getNumeroExterior()))
				regresar.append(this.registroDesarrollo.getDomicilio().getNumeroExterior()).append(" , ");
			if(!Cadena.isVacio(this.registroDesarrollo.getDomicilio().getNumeroInterior()))
				regresar.append(this.registroDesarrollo.getDomicilio().getNumeroInterior()).append(" , ");
			regresar.append(this.registroDesarrollo.getDomicilio().getAsentamiento()).append(" , C.P. ");
			regresar.append(this.registroDesarrollo.getDomicilio().getCodigoPostal());
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar.toString();
	} // toDomicilio
	
  @Override
  public void doLoad() {
		List<Columna> columns= null;		
		try {
			columns= new ArrayList<>();      
      columns.add(new Columna("nombreUsuario", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("observaciones", EFormatoDinamicos.MAYUSCULAS));
      columns.add(new Columna("registro", EFormatoDinamicos.FECHA_HORA_CORTA));	
			this.attrs.put("idVale", ((Entity)this.attrs.get("seleccionado")).getKey());
		  this.attrs.put("importados", UIEntity.build("VistaValesArchivosDto", "importados", this.attrs, columns));
			doLoadFiles();
		} // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
    finally {
      Methods.clean(columns);
    }// finally		
  } // doLoad		
	
	private void doLoadFiles(){
		List<Entity>importados= null;		
		String dns            = null;
		String url            = null;
		try {
			dns= Configuracion.getInstance().getPropiedad("sistema.dns.".concat(Configuracion.getInstance().getEtapaServidor().name().toLowerCase()));			
			importados= (List<Entity>) this.attrs.get("importados");
			for(Entity importado: importados){
				url= dns.substring(0, dns.indexOf(JsfBase.getContext())).concat("/").concat((String)this.attrs.get("pathPivote")).concat(importado.toString("ruta")).concat(importado.toString("archivo"));
				importado.put("url", new Value("url", url));
			} // for
			this.attrs.put("importados", importados);
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);
			throw e;
		} // catch		
	} // doLoadFiles		
	
	public String doCancelar() {
    String regresar= null;    
    try {						
			JsfBase.setFlashAttribute("opcionResidente", (EOpcionesResidente)this.attrs.get("opcionResidente"));												
			JsfBase.setFlashAttribute("opcionAdicional", this.attrs.get("opcionAdicional"));												
			JsfBase.setFlashAttribute("idDesarrollo", (Long)this.attrs.get("idDesarrollo"));			
			JsfBase.setFlashAttribute("idDesarrolloProcess", (Long)this.attrs.get("idDesarrollo"));			
			JsfBase.setFlashAttribute("figura", (UISelectEntity)this.attrs.get("figura"));			
			regresar= "filtro".concat(Constantes.REDIRECIONAR);			
		} // try
		catch (Exception e) {
			JsfBase.addMessageError(e);
			Error.mensaje(e);			
		} // catch		
    return regresar;
  } // doCancelar			
}