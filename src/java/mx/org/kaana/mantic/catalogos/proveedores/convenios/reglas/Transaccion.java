package mx.org.kaana.mantic.catalogos.proveedores.convenios.reglas;

import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TrKeetArticuloProveedorClienteDto;
import mx.org.kaana.libs.pagina.JsfBase;
import org.hibernate.Session;

public class Transaccion extends IBaseTnx {

	private TrKeetArticuloProveedorClienteDto precio;	
	private String messageError;

	public Transaccion(TrKeetArticuloProveedorClienteDto precio) {
		this.precio= precio;
	}

	public String getMessageError() {
		return messageError;
	}

	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {		
		boolean regresar          = false;
		Map<String, Object> params= new HashMap<>();
		try {
			params.put("sucursales", JsfBase.getAutentifica().getEmpresa().getSucursales());
			this.messageError= "Ocurrio un error al ".concat(accion.name().toLowerCase()).concat(" el registrar el precio !");
			switch(accion){
				case AGREGAR:
					regresar= DaoFactory.getInstance().insert(sesion, this.precio)>= 1L;
					break;
				case MODIFICAR:
					regresar= DaoFactory.getInstance().update(sesion, this.precio)>= 1L;
					break;				
				case ELIMINAR:
					regresar= DaoFactory.getInstance().delete(sesion, this.precio)>= 1L;
					break;
			} // switch
			if(!regresar)
        throw new Exception("");
		} // try
		catch (Exception e) {			
			throw new Exception(this.messageError.concat("<br/>")+ (e!= null? e.getCause().toString(): ""));
		} // catch		
		return regresar;
	}	// ejecutar
	
}