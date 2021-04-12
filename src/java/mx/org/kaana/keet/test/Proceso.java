package mx.org.kaana.keet.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesDto;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 12/04/2021
 * @time 03:04:50 PM
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public class Proceso {

  private static final Log LOG = LogFactory.getLog(Proceso.class);
  
  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws Exception {
		List<TcKeetEstacionesDto> padres= null;
    TcKeetEstacionesDto padre       = null;
		List<TcKeetEstacionesDto> hijos = null;
    Estaciones estaciones           = null;
		int terminados, iniciar         = 0;
    Long idEstacionEstatus          = 1L;
		try {			
      TcKeetEstacionesDto hijo= (TcKeetEstacionesDto)DaoFactory.getInstance().findById(TcKeetEstacionesDto.class, 53580L);
			estaciones= new Estaciones();
			padres    = estaciones.toFather(hijo.getClave());			
      if(padres!= null && !padres.isEmpty()) {
        padres.remove(padres.size()- 1);
        int index= padres.size()- 1;
        while(index>= 0) {
          padre     = padres.get(index);
          terminados= 0;
          iniciar   = 0;
          hijos     = estaciones.toChildren(padre.getClave(), padre.getNivel().intValue(), 1);
          if(hijos!= null && !hijos.isEmpty()) {
            for(TcKeetEstacionesDto item: hijos) {
              switch(item.getIdEstacionEstatus().intValue()) {
                case 3: // EEstacionesEstatus.TERMINADO
                  terminados++;
                  break;
                case 1: // EEstacionesEstatus.EN_PROCESO
                  iniciar++;
                  break;
              } // switch
            } // for
            idEstacionEstatus= terminados== hijos.size()? EEstacionesEstatus.TERMINADO.getKey(): iniciar== hijos.size()? EEstacionesEstatus.INICIAR.getKey(): EEstacionesEstatus.EN_PROCESO.getKey();
            padre.setIdEstacionEstatus(idEstacionEstatus);
           // DaoFactory.getInstance().update(sesion, TcKeetEstacionesDto.class, padre);
           // ACTUALIZAR EL ESTATUS DEL LOTE DEL CONTRATO CON EL AVANCE
           if(Objects.equals(padre.getNivel(), 4L)) {
//			      TcKeetContratosLotesDto contratoLote= (TcKeetContratosLotesDto) DaoFactory.getInstance().findById(sesion, TcKeetContratosLotesDto.class, this.revision.getIdContratoLote());
//			      contratoLote.setIdContratoLoteEstatus(Objects.equals(EEstacionesEstatus.TERMINADO.getKey(), idEstacionEstatus)? idEstacionEstatus+ 1L: idEstacionEstatus);			
//			      DaoFactory.getInstance().update(sesion, contratoLote);
           } // if
          } // if  
          index--;
        } // for
      } // if
		} // try
		catch (Exception e) {			
			throw e; 
		} // catch		
  }

}
