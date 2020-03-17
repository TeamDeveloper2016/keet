package mx.org.kaana.keet.catalogos.prototipos.beans;

import java.util.Collections;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;

public class ContadoresListas {

	private static final Long INCREMENTO= 10000L;
	private Long totalConstructivos;	

	public ContadoresListas() {
		init();
	} // ContadoresListas
	
	public ContadoresListas(Long totalConstructivos) {
		this.totalConstructivos= totalConstructivos;		
	} // ContadoresListas

	public Long getTotalConstructivos() {
		return totalConstructivos;
	}

	public void setTotalConstructivos(Long totalConstructivos) {
		this.totalConstructivos = totalConstructivos;
	}	
	
	private void init(){
		try {
			this.totalConstructivos= toMaxConstructivos();			
		} // try
		catch (Exception e) {
			Error.mensaje(e);						
		} // catch		
	} // init
	
	private Long toMaxConstructivos() throws Exception{
		Long regresar= 0L;
		Value maximo = null;
		try {
			maximo= DaoFactory.getInstance().toField("TcKeetPrototiposConstructivosDto", "maximo", Collections.EMPTY_MAP, "maximo");
			if(maximo.getData()!= null)
				regresar= Long.valueOf(maximo.toString()) + INCREMENTO;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
		return regresar;
	} // toMaxConstructivos
}
