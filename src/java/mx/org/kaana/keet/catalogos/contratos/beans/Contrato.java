package mx.org.kaana.keet.catalogos.contratos.beans;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.catalogos.prototipos.beans.DiaHabil;
import mx.org.kaana.keet.db.dto.TcKeetPrototiposDto;
import mx.org.kaana.keet.db.dto.TcKeetContratosDto;
import mx.org.kaana.libs.formato.Fecha;
import mx.org.kaana.libs.pagina.JsfBase;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.pagina.UISelectItem;
import mx.org.kaana.libs.reflection.Methods;

public class Contrato extends TcKeetContratosDto {

	private static final long serialVersionUID= -2816173235044810661L;
	private UISelectEntity ikProyecto;
  private List<Lote> lotes;
	private Lote loteSeleccion;
	private String claveDesarrollo;
	private String desarrollo;		
	private ESql sqlAccion;
	private Boolean nuevo;		

	public Contrato() {
		this(-1L);
	}

	public Contrato(Long key) {
		this(key, ESql.UPDATE);
	}
	
	public Contrato(Long key, ESql sqlAccion) {
		this(key, sqlAccion, false);
	}
	
	public Contrato(Long key, ESql sqlAccion, Boolean nuevo) {		
		this(key, "", "", new ArrayList<>(), new Lote(), new UISelectEntity(-1L), sqlAccion, nuevo);		
	}
	
	public Contrato(Long key, String claveDesarrollo, String desarrollo, List<Lote> lotes, Lote loteSeleccion, UISelectEntity ikProyecto, ESql sqlAccion, Boolean nuevo) {
		super(key);
		this.claveDesarrollo= claveDesarrollo;
		this.desarrollo     = desarrollo;				
		this.sqlAccion      = sqlAccion;
		this.nuevo          = nuevo;
		this.lotes          = lotes;
		this.loteSeleccion  = loteSeleccion;
		this.ikProyecto     = ikProyecto;
	}
	
	public UISelectEntity getIkProyecto() {
		return ikProyecto;
	} 

	public void setIkProyecto(UISelectEntity ikProyecto) {
		this.ikProyecto = ikProyecto;
		if(this.ikProyecto!= null)
			this.setIdProyecto(this.ikProyecto.getKey());
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

	public String getClaveDesarrollo() {
		return claveDesarrollo;
	}

	public void setClaveDesarrollo(String claveDesarrollo) {
		this.claveDesarrollo = claveDesarrollo;
	}

	public String getDesarrollo() {
		return desarrollo;
	}

	public void setDesarrollo(String desarrollo) {
		this.desarrollo = desarrollo;
	}

	public ESql getSqlAccion() {
		return sqlAccion;
	}

	public void setSqlAccion(ESql sqlAccion) {
		this.sqlAccion = sqlAccion;
	}

	public Boolean getNuevo() {
		return nuevo;
	}

	public void setNuevo(Boolean nuevo) {
		this.nuevo = nuevo;
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
	} // doAddLote
	
	public boolean validaPrototipos(List<UISelectItem> lista) throws Exception{
		boolean regresar= true;
		try {
		  for(Lote item: this.lotes){
				if(!lista.contains(new UISelectItem(item.getIdPrototipo()))){
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
		Map<String, Object>params              = null;
	  try {
			params= new HashMap<>();
			params.put("idPrototipo",  lote.getIdPrototipo());
			if(lote.getIkPrototipo()!= null && lote.getIdPrototipo()>0L){
			  tcKeetPrototiposDto= (TcKeetPrototiposDto)DaoFactory.getInstance().findById(TcKeetPrototiposDto.class, lote.getIdPrototipo());
				lote.setDiasConstruccion(tcKeetPrototiposDto.getDiasConstruccion());
				if(tcKeetPrototiposDto.getIdTipoDia().equals(1L)) // dias naturales
				  lote.setTermino(lote.getInicio().plusDays(tcKeetPrototiposDto.getDiasConstruccion()));
				else{
					diasHabiles= (List<DiaHabil>)DaoFactory.getInstance().toEntitySet(DiaHabil.class, "VistaPrototiposDto", "getDias", params);
					lote.setTermino(addWorkingDays(lote.getInicio(), lote.getDiasConstruccion().intValue(), diasHabiles));
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
	}	// addWorkingDays

	public Boolean getPaginator() {
		return this.lotes.size() > 10;
	}	
}	