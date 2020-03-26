package mx.org.kaana.keet.catalogos.contratos.beans;

import java.util.Collections;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Value;

public class ContadoresListas {

	private static final Long INCREMENTO= 10000L;
	private Long totalContratoPersona;	

	public ContadoresListas() {
		init();
	} // ContadoresListas
	
	public ContadoresListas(Long totalContratoPersona) {
		this.totalContratoPersona= totalContratoPersona;		
	} // ContadoresListas

	public Long getTotalContratoPersona() {
		return totalContratoPersona;
	}

	public void setTotalContratoPersona(Long totalContratoPersona) {
		this.totalContratoPersona = totalContratoPersona;
	}		
	
	private void init(){
		try {
			this.totalContratoPersona= toMaxContratoPersona();			
		} // try
		catch (Exception e) {
			Error.mensaje(e);						
		} // catch		
	} // init
	
	private Long toMaxContratoPersona() throws Exception{
		Long regresar= 0L;
		Value maximo = null;
		try {
			maximo= DaoFactory.getInstance().toField("TcKeetContratosPersonalDto", "maximo", Collections.EMPTY_MAP, "maximo");
			if(maximo.getData()!= null)
				regresar= Long.valueOf(maximo.toString()) + INCREMENTO;
		} // try
		catch (Exception e) {			
			throw e;
		} // catch				
		return regresar;
	} // toMaxConstructivos
}
