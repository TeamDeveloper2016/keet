package mx.org.kaana.keet.nomina.reglas;

import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.procesos.acceso.beans.Autentifica;
import mx.org.kaana.keet.db.dto.TcKeetNominasDto;
import mx.org.kaana.keet.db.dto.TcKeetNominasPeriodosDto;
import org.hibernate.Session;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 10/01/2025
 *@time 04:33:32 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Puente extends Transaccion {

	public Puente(TcKeetNominasDto nomina, Nomina calculos, Long idNomina, Long idEmpresaPersona, Autentifica autentifica) {
		super(idNomina, idEmpresaPersona, autentifica);
    this.nomina  = nomina;
    this.calculos= calculos;
	}
	
	public Puente(TcKeetNominasDto nomina, Factura factura, Long idNomina, Long idProveedor, Autentifica autentifica) {
		super(idNomina, autentifica, idProveedor);
    this.nomina = nomina;
    this.factura= factura;
	}
  
	@Override
	protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
		boolean regresar= Boolean.TRUE;
    try {
			switch(accion) {
				case EMPLEADO:
          this.calculos.setSesion(sesion);
					this.persona(sesion);
					break;
				case PROVEEDOR:
          this.factura.setSesion(sesion);
					this.proveedor(sesion);
					break;
      } // switch
		} // try
		catch (Exception e) {			
      if(e!= null)
        if(e.getCause()!= null)
          this.messageError= this.messageError.concat("<br/>").concat(e.getCause().toString());
        else
          this.messageError= this.messageError.concat("<br/>").concat(e.getMessage());
			throw new Exception(this.messageError);
		} // catch		
		return regresar;	
  }
  
}
