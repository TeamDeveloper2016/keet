package mx.org.kaana.keet.catalogos.desarrollos.reglas;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.keet.catalogos.desarrollos.beans.Desarrollo;
import mx.org.kaana.keet.catalogos.desarrollos.beans.Domicilio;
import mx.org.kaana.libs.Constantes;
import mx.org.kaana.libs.pagina.UISelectEntity;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticDomiciliosDto;

public class MotorBusqueda implements Serializable{
	
	private static final long serialVersionUID = -2951697223110542896L;
	private Long idDesarrollo;

	public MotorBusqueda(Long idDesarrollo) {
		this.idDesarrollo = idDesarrollo;
	}
	
	public Desarrollo toDesarrollo() throws Exception{
		Desarrollo regresar       = null;
		Map<String, Object>params= null;
		try {
		  params= new HashMap<>();
			params.put("idDesarrollo", this.idDesarrollo);
			regresar= (Desarrollo) DaoFactory.getInstance().toEntity(Desarrollo.class, "TcKeetDesarrollosDto", "byId", params);
			if(regresar!= null && regresar.isValid())
				regresar.setIkCliente(new UISelectEntity(regresar.getIdCliente()));
		} // try
		catch (Exception e) {			
			throw e;
		} // catch
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toDesarrollo
	
	public TcManticDomiciliosDto toDomicilio(Long idDomicilio) throws Exception {
		TcManticDomiciliosDto regresar= null;
		try {
			regresar= (TcManticDomiciliosDto) DaoFactory.getInstance().findById(TcManticDomiciliosDto.class, idDomicilio);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		return regresar;
	} // toDomicilio
	
	public Domicilio toDomicilio(Desarrollo desarrollo) throws Exception {
		Domicilio regresar= null;
		Map<String, Object>params     = null;
		try {
			params= new HashMap<>();
			params.put(Constantes.SQL_CONDICION, "id_domicilio=".concat(desarrollo.getIdDomicilio().toString()));
			regresar= (Domicilio)DaoFactory.getInstance().toEntity(Domicilio.class,"TcManticDomiciliosDto", "row", params);
		} // try
		catch (Exception e) {			
			throw e;
		} // catch		
		finally {
			Methods.clean(params);
		} // finally
		return regresar;
	} // toDomicilio
	

}
