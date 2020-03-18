package mx.org.kaana.keet.catalogos.proyectos.beans;

import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.db.dto.TcKeetProyectosDto;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class Proyecto extends TcKeetProyectosDto{

	private static final long serialVersionUID = -2978341734389180932L;	
	private UISelectEntity ikCliente;
	private UISelectEntity ikDesarrollo;
	private UISelectEntity ikTipoObra;
   private List<Lote> lotes;
	 
	public Proyecto() {
		this(new UISelectEntity(-1L), new UISelectEntity(-1L), new UISelectEntity(-1L));
	} 

	public Proyecto(UISelectEntity ikCliente, UISelectEntity ikDesarrollo, UISelectEntity ikTipoObra) {
		super();
		this.lotes= new ArrayList<>();
		this.ikCliente   = ikCliente;
		this.ikDesarrollo= ikDesarrollo;
		this.ikTipoObra  = ikTipoObra;
	}

	public UISelectEntity getIkCliente() {
    return ikCliente;
  } 

  public void setIkCliente(UISelectEntity ikCliente) {
    this.ikCliente = ikCliente;
		if(this.ikCliente!= null)
			this.setIdCliente(this.ikCliente.getKey());
  } 

	public UISelectEntity getIkDesarrollo() {
		return ikDesarrollo;
	} 

	public void setIkDesarrollo(UISelectEntity ikDesarrollo) {
		this.ikDesarrollo = ikDesarrollo;
		if(this.ikDesarrollo!= null)
			this.setIdDesarrollo(this.ikDesarrollo.getKey());
	} 

	public UISelectEntity getIkTipoObra() {
		return ikTipoObra;
	} 

	public void setIkTipoObra(UISelectEntity ikTipoObra) {
		this.ikTipoObra = ikTipoObra;
		if(this.ikTipoObra!= null)
			this.setIdTiposObras(this.ikTipoObra.getKey());
	}

	public List<Lote> getLotes() {
		return lotes;
	}

	public void setLotes(List<Lote> lotes) {
		this.lotes = lotes;
	}
	
  public boolean addLote(Lote lote) throws Exception{
		boolean regresar= false;
		try {
			lote.setAccion(ESql.INSERT);
			this.lotes.add(lote);
			regresar= true;
		} // try
		catch(Exception e){
			throw e;
		} // catch
		return regresar;
	} // addLote
	
	public boolean removeLote(Lote lote) throws Exception{
		boolean regresar= false;
		try {
		  if (this.lotes.contains(lote)){
				if( (this.lotes.get(this.lotes.indexOf(lote)).getAccion().equals(ESql.UPDATE)))
				  this.lotes.get(this.lotes.indexOf(lote)).setAccion(ESql.DELETE);
				else
					this.lotes.remove(this.lotes.indexOf(lote));
				regresar= true;
			} // if
		} // try
		catch(Exception e){
			throw e;
		} // catch
		return regresar;
	} // removeLote	
	
  @Override
  public Class toHbmClass() {
    return TcKeetProyectosDto.class;
  }
	
}	
