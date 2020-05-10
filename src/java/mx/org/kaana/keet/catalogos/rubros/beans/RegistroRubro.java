package mx.org.kaana.keet.catalogos.rubros.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.catalogos.rubros.reglas.MotorBusqueda;
import mx.org.kaana.libs.pagina.JsfBase;

public class RegistroRubro implements Serializable {

	private static final long serialVersionUID = 5873766099969069853L;
	private List<RubroGrupo> rubrosGrupos;
	private Rubro rubro;

	public RegistroRubro() {
		this(-1L);
	}
	
  public RegistroRubro(Long idRubro) {
		init(idRubro);
	}

	public List<RubroGrupo> getRubrosGrupos() {
		return rubrosGrupos;
	}

	public void setRubrosGrupos(List<RubroGrupo> rubrosGrupos) {
		this.rubrosGrupos = rubrosGrupos;
	}

	public Rubro getRubro() {
		return rubro;
	}

	public void setRubro(Rubro rubro) {
		this.rubro = rubro;
	}

	private void init(Long idRubro) {
		MotorBusqueda motor= null;
		try {
			if(idRubro> 0L) {
				motor= new MotorBusqueda(idRubro);
				this.rubro= motor.toRubro();
				this.rubrosGrupos= motor.toRubrosGrupos();
			} // if
			else{				
				this.rubro= new Rubro();
				this.rubrosGrupos= new ArrayList<>();
			} // else
		} // try
		catch (Exception e) {			
			mx.org.kaana.libs.formato.Error.mensaje(e);				
		} // catch		
	} // init

	
	public void doAgregarRubroGrupo(){
		RubroGrupo rubroGrupo= null;
		try {					
			rubroGrupo= new RubroGrupo(ESql.INSERT, this.rubrosGrupos.size()*-1L);							
			this.rubrosGrupos.add(rubroGrupo);			
		} // try
		catch (Exception e) {
			mx.org.kaana.libs.formato.Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch		
	} // doAgregarRubroGrupo
	
	public void doEliminarRubroGrupo(RubroGrupo selecion){
		try {			
			if(selecion.getKey()>0L)
				this.rubrosGrupos.get(this.rubrosGrupos.indexOf(selecion)).setAccion(ESql.DELETE);
			else
				this.rubrosGrupos.remove(selecion);
		} // try
		catch (Exception e) {
			mx.org.kaana.libs.formato.Error.mensaje(e);
			JsfBase.addMessageError(e);			
		} // catch			
	} // doEliminarRubroGrupo
	

	

}