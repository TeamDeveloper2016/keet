package mx.org.kaana.keet.catalogos.proyectos.beans;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.catalogos.prototipos.beans.DiaHabil;
import mx.org.kaana.keet.db.dto.TcKeetPrototiposDto;
import mx.org.kaana.keet.db.dto.TcKeetProyectosDto;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;

public class Proyecto extends TcKeetProyectosDto {

	private static final long serialVersionUID = -2978341734389180932L;	
	private UISelectEntity ikCliente;
	private UISelectEntity ikDesarrollo;
	private UISelectEntity ikTipoObra;
  private List<Lote> lotes;
	private Lote loteSeleccion;
	 
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
			this.setIdTipoObra(this.ikTipoObra.getKey());
	}

	public List<Lote> getLotes() {
		return lotes;
	}

	public void setLotes(List<Lote> lotes) {
		this.lotes = lotes;
	}

	public Lote getLoteSeleccion() {
		return loteSeleccion;
	}

	public void setLoteSeleccion(Lote loteSeleccion) {
		this.loteSeleccion = loteSeleccion;
	}
	
  public boolean addLote(Lote lote) throws Exception{
		boolean regresar= false;
		try {
			this.lotes.add(lote);
			regresar= true;
		} // try
		catch(Exception e){
			throw e;
		} // catch
		return regresar;
	} // addLote
	
	public boolean doRemoveLote() throws Exception{
		boolean regresar= false;
		try {
		  if (this.lotes.contains(loteSeleccion)){
				if( (this.lotes.get(this.lotes.indexOf(loteSeleccion)).getAccion().equals(ESql.UPDATE)))
				  this.lotes.get(this.lotes.indexOf(loteSeleccion)).setAccion(ESql.DELETE);
				else
					this.lotes.remove(this.lotes.indexOf(loteSeleccion));
				regresar= true;
			} // if
		} // try
		catch(Exception e){
			throw e;
		} // catch
		return regresar;
	} // removeLote	
	
	public void doAddLote() throws Exception{
		this.addLote(new Lote(ESql.INSERT, (this.lotes.size()+1)*(-1L)));
	}
	
	public boolean validaPrototipos(List<UISelectEntity> uISelectEntitys) throws Exception{
		boolean regresar= true;
		try {
		  for(Lote item: this.lotes){
				if(!uISelectEntitys.contains(new UISelectEntity(new Entity(item.getIdPrototipo())))){
					this.loteSeleccion= item;
					doRemoveLote();
					regresar= false;
				} // if
			} // for
		} // try
		catch(Exception e){
			throw e;
		} // catch
		return regresar;
	} // removeLote	
	
	public void doCalculateFecha(Lote lote){
		TcKeetPrototiposDto tcKeetPrototiposDto= null;
		List<DiaHabil> diasHabiles             = null;
		Map<String, Object>params= null;
	  try {
			params= new HashMap<>();
			params.put("idPrototipo",  lote.getIdPrototipo());
			if(lote.getIkPrototipo()!= null && lote.getIdPrototipo()>0L){
			  tcKeetPrototiposDto= (TcKeetPrototiposDto)DaoFactory.getInstance().findById(TcKeetPrototiposDto.class, lote.getIdPrototipo());
				lote.setDiasConstruccion(tcKeetPrototiposDto.getDiasConstruccion());
				if(tcKeetPrototiposDto.getIdTipoDia().equals(1L)) // dias naturales
				  lote.setFechaTermino(lote.getFechaInicio().plusDays(tcKeetPrototiposDto.getDiasConstruccion()));
				else{
					diasHabiles= (List<DiaHabil>)DaoFactory.getInstance().toEntitySet(DiaHabil.class, "VistaPrototiposDto", "getDias", params);
					lote.setFechaTermino(addWorkingDays(lote.getFechaInicio(), lote.getDiasConstruccion().intValue(), diasHabiles));
				} // else
				//lote.getFechaInicio().
			} // if
    } // try
    catch (Exception e) {
      JsfBase.addMessageError(e);
    } // catch
    finally {
			Methods.clean(params);
			Methods.clean(diasHabiles);
		} // finally		
	} // doCalculateFecha
	
  @Override
  public Class toHbmClass() {
    return TcKeetProyectosDto.class;
  }
		
	private LocalDate addWorkingDays(LocalDate date, int cuantos, List<DiaHabil> diasHabiles) throws Exception{
		LocalDate regresar= null;
		String dia        = null;
		for(int i=0; cuantos>0; i++){
			regresar= date.plusDays(Long.valueOf(i));
			dia=Fecha.getNombreDia(regresar.getDayOfWeek().getValue()==7? 1:regresar.getDayOfWeek().getValue()+1).toUpperCase();
			if(diasHabiles.contains(new DiaHabil(dia)))
				cuantos--;
		} // for
		return regresar;
	}
	
}	
