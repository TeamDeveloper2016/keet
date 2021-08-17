package mx.org.kaana.kajool.procesos.acceso.backing;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.enums.EFormatoDinamicos;
import mx.org.kaana.libs.formato.Global;
import mx.org.kaana.libs.formato.Error;
import mx.org.kaana.libs.pagina.IBaseFilter;
import mx.org.kaana.libs.pagina.UIBackingUtilities;
import mx.org.kaana.libs.reflection.Methods;
import mx.org.kaana.mantic.db.dto.TcManticControlRespaldosDto;
import mx.org.kaana.mantic.db.dto.TcManticRespaldosDto;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 17/08/2021
 *@time 11:40:17 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public abstract class Respaldos extends IBaseFilter implements Serializable {

  private static final long serialVersionUID = 5455484470100202377L;
  
  protected void checkDownloadBackup() {
	  Map<String, Object> params= null;
		try {
		  params=new HashMap<>();
			TcManticRespaldosDto respaldo= (TcManticRespaldosDto)DaoFactory.getInstance().toEntity(TcManticRespaldosDto.class, "TcManticRespaldosDto", "ultimo", Collections.EMPTY_MAP);
			if(respaldo!= null) {
			  params.put("idRespaldo", respaldo.getIdRespaldo());
				TcManticControlRespaldosDto control= (TcManticControlRespaldosDto)DaoFactory.getInstance().toEntity(TcManticControlRespaldosDto.class, "TcManticControlRespaldosDto", "ultimo", params);
			  if(control!= null) {
					if(!control.getIdRespaldo().equals(respaldo.getIdRespaldo())) {
					  this.attrs.put("messageBackup", "NO se ha DESCARGADO el respaldo de la 'Base de Datos', desde "+ Global.format(EFormatoDinamicos.DIA_FECHA_HORA, control.getRegistro()));
  				  UIBackingUtilities.execute("PF('downloadBackup').show()");
	  			} // if
				} // if	
				else {
					this.attrs.put("messageBackup", "NUNCA se ha DESCARGADO el respaldo de la 'Base de Datos', por favor realice una descarga a su equipo de trabajo !");
				  UIBackingUtilities.execute("PF('downloadBackup').show()");
				} // else	
			} // if
		} // try
		catch (Exception e) {
			Error.mensaje(e);
		} // catch
		finally {
			Methods.clean(params);
		} // finally	
	} // checkDownloadBackup

}
