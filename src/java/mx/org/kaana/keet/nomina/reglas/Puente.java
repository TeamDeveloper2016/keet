package mx.org.kaana.keet.nomina.reglas;

import mx.org.kaana.kajool.catalogos.backing.Monitoreo;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.procesos.acceso.beans.Autentifica;
import mx.org.kaana.keet.db.dto.TcKeetNominasDto;
import org.hibernate.Session;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 10/01/2025
 *@time 04:33:32 PM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public class Puente extends Transaccion {

	public Puente(TcKeetNominasDto nomina, Nomina calculos, Long idEmpresaPersona, Autentifica autentifica, String[] idNotificar, String realPath, Boolean automatico) {
		super(nomina, autentifica, idNotificar, realPath, idEmpresaPersona);
    this.calculos= calculos;
	}
	
	public Puente(TcKeetNominasDto nomina, Factura factura, Long idProveedor, Autentifica autentifica, String[] idNotificar, String realPath, Boolean automatico) {
    super(nomina, autentifica, idNotificar, idProveedor, realPath);
    this.factura= factura;
	}
  
	public Puente(TcKeetNominasDto nomina, Autentifica autentifica, String[] idNotificar, String realPath, Boolean automatico) {
    super(nomina, autentifica, idNotificar, realPath);
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
				case NOTIFICAR:
          this.notificarResumenDestajos(sesion);
          this.notificarControl(sesion);
          this.toAddNewNomina(sesion);
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
