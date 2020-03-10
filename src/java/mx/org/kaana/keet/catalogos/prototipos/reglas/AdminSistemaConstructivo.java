package mx.org.kaana.keet.catalogos.prototipos.reglas;

import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.kajool.enums.ESql;
import mx.org.kaana.keet.catalogos.prototipos.beans.SistemaConstructivo;
import mx.org.kaana.libs.pagina.UISelectEntity;

public class AdminSistemaConstructivo {
	
	private List<SistemaConstructivo> registros;

	public AdminSistemaConstructivo() {
		this(new ArrayList<SistemaConstructivo>());
	}
	
	public AdminSistemaConstructivo(List<SistemaConstructivo> registros) {
		this.registros= registros;
	}

	public List<SistemaConstructivo> getRegistros() {
		return registros;
	}

	public void setRegistros(List<SistemaConstructivo> registros) {
		this.registros = registros;
	}
	
	public boolean addSistemaConstructivo(UISelectEntity uISelectEntity) throws Exception{
		boolean regresar= false;
		SistemaConstructivo sistemaConstructivo= null;
		try {
			sistemaConstructivo= new SistemaConstructivo(uISelectEntity);
		  if (this.registros.contains(sistemaConstructivo) && (this.registros.get(this.registros.indexOf(sistemaConstructivo)).getAccion().equals(ESql.DELETE))){
				this.registros.add(this.registros.indexOf(sistemaConstructivo), new SistemaConstructivo(ESql.UPDATE, uISelectEntity));
				regresar= true;
			} // if
			else {
				this.registros.add(new SistemaConstructivo(ESql.INSERT, uISelectEntity));
				regresar= true;
			} // else
		} // try
		catch(Exception e){
			throw e;
		} // catch
		return regresar;
	} // addSistemaConstructivo
	
	public boolean removeSistemaConstructivo(UISelectEntity uISelectEntity) throws Exception{
		boolean regresar= false;
		SistemaConstructivo sistemaConstructivo= null;
		try {
			sistemaConstructivo= new SistemaConstructivo(uISelectEntity);
		  if (this.registros.contains(sistemaConstructivo)){
				if( (this.registros.get(this.registros.indexOf(sistemaConstructivo)).getAccion().equals(ESql.UPDATE)))
				  this.registros.add(this.registros.indexOf(sistemaConstructivo), new SistemaConstructivo(ESql.DELETE, uISelectEntity));
				else
					this.registros.remove(this.registros.indexOf(sistemaConstructivo));
				regresar= true;
			} // if
		} // try
		catch(Exception e){
			throw e;
		} // catch
		return regresar;
	} // removeSistemaConstructivo	
	
}
