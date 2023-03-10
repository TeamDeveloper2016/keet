package mx.org.kaana.keet.estaciones.backing;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.libs.pagina.IBaseAttribute;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@Named(value = "keetEstacionesEstructura")
@ViewScoped
public class Estructura extends IBaseAttribute implements Serializable {

  private static final long serialVersionUID = 4534803108384959391L;
	
	protected TcKeetEstacionesDto current;
	private TreeNode root;

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}

  @PostConstruct
  @Override
  protected void init() {
		Estaciones estaciones=null;
    try {
			estaciones=new Estaciones();
			estaciones.cleanLevels();
			if(JsfBase.getFlashAttribute("estacionProcess")!= null){
				this.current= (TcKeetEstacionesDto)JsfBase.getFlashAttribute("estacionProcess");
				this.attrs.put("estacionPadre", this.current);
				this.attrs.put("retorno", JsfBase.getFlashAttribute("retorno"));
				root = new DefaultTreeNode(this.current, null);
				cargarHijos(root);
			} // if
			else
				UIBackingUtilities.execute("janal.isPostBack('cancelar')");
    } // try
    catch (Exception e) {
      Error.mensaje(e);
      JsfBase.addMessageError(e);
    } // catch		
  } // init

  
	protected List<TcKeetEstacionesDto> actualizarChildren(int nivel) throws Exception {
		return actualizarChildren(nivel, 0);
	}
	
	protected List<TcKeetEstacionesDto> actualizarChildren(int nivel, int aumentarNivel) throws Exception {
		Estaciones estaciones=null;
		List<TcKeetEstacionesDto> regresar= null;
		try {
			this.current=((TcKeetEstacionesDto)this.attrs.get("seleccionado"))==null ? this.current : ((TcKeetEstacionesDto)this.attrs.get("seleccionado"));
			estaciones=new Estaciones();
			regresar=estaciones.toChildren(aumentarNivel, this.current.getClave(), this.current.getNivel().intValue()+nivel, 0);
		} // try
		catch (Exception e) {
			throw e;
		} // catch
		return regresar;
	} // actualizarChildren

	private void cargarHijos(TreeNode nodoPadre) throws Exception{
		Estaciones estaciones= null;
		TreeNode nodoHijo    = null;
		List<TcKeetEstacionesDto> hijos= null;
		try {
			hijos= actualizarChildren(1);
			for(TcKeetEstacionesDto item: hijos){
				nodoHijo= new DefaultTreeNode(item);
				nodoPadre.getChildren().add(nodoHijo);
				this.current= item;
				cargarHijos(nodoHijo);
			} // for
		} // try
		catch (Exception e) {
			throw e;
		} // catch
	} // cargarHijos
	
	public String doCancelar() {   
		JsfBase.setFlashAttribute("estacionProcess", ((TcKeetEstacionesDto)this.attrs.get("estacionPadre")));
    return (String) this.attrs.get("retorno");
  } // doCancelar	

	
	
}