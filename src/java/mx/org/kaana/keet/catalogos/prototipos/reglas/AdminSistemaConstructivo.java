package mx.org.kaana.keet.catalogos.prototipos.reglas;

import java.util.ArrayList;
import java.util.List;
import mx.org.kaana.kajool.db.comun.dto.IBaseDto;
import mx.org.kaana.kajool.db.comun.operation.Delete;
import mx.org.kaana.kajool.db.comun.operation.Insert;
import mx.org.kaana.kajool.db.comun.operation.Update;
import mx.org.kaana.keet.catalogos.prototipos.beans.SistemaConstructivo;

public class AdminSistemaConstructivo {
	
	private List<SistemaConstructivo> registros;

	public AdminSistemaConstructivo() {
		this(new ArrayList<IBaseDto>());
	}
	
	public AdminSistemaConstructivo(List<IBaseDto> registros) {
		this.registros= new ArrayList<>();
		for(IBaseDto item: registros)
			this.registros.add(new SistemaConstructivo(new Update(item)));
			
	}

	public List<SistemaConstructivo> getRegistros() {
		return registros;
	}

	public void setRegistros(List<SistemaConstructivo> registros) {
		this.registros = registros;
	}
	
	public boolean addSistemaConstructivo(IBaseDto iBaseDto) throws Exception{
		boolean regresar= false;
		SistemaConstructivo sistemaConstructivo= null;
		try {
			sistemaConstructivo= new SistemaConstructivo(new Update(iBaseDto));
		  if (this.registros.contains(sistemaConstructivo) && (this.registros.get(this.registros.indexOf(sistemaConstructivo)).getSistema() instanceof Delete)){
				this.registros.add(this.registros.indexOf(sistemaConstructivo), new SistemaConstructivo(new Update(iBaseDto)));
				regresar= true;
			} // if
			else {
				this.registros.add(this.registros.indexOf(sistemaConstructivo), new SistemaConstructivo(new Insert(iBaseDto)));
				regresar= true;
			} // else
		} // try
		catch(Exception e){
			throw e;
		} // catch
		return regresar;
	} // addSistemaConstructivo
	
	public boolean removeSistemaConstructivo(IBaseDto iBaseDto) throws Exception{
		boolean regresar= false;
		SistemaConstructivo sistemaConstructivo= null;
		try {
			sistemaConstructivo= new SistemaConstructivo(new Update(iBaseDto));
		  if (this.registros.contains(sistemaConstructivo)){
				if( (this.registros.get(this.registros.indexOf(sistemaConstructivo)).getSistema() instanceof Update))
				  this.registros.add(this.registros.indexOf(sistemaConstructivo), new SistemaConstructivo(new Delete(iBaseDto)));
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
