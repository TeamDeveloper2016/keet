package mx.org.kaana.keet.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import mx.org.kaana.kajool.db.comun.hibernate.DaoFactory;
import mx.org.kaana.kajool.db.comun.sql.Entity;
import mx.org.kaana.kajool.enums.EAccion;
import mx.org.kaana.kajool.reglas.IBaseTnx;
import mx.org.kaana.keet.db.dto.TcKeetContratosLotesDto;
import mx.org.kaana.keet.db.dto.TcKeetEstacionesDto;
import mx.org.kaana.keet.enums.EEstacionesEstatus;
import mx.org.kaana.keet.estaciones.reglas.Estaciones;
import mx.org.kaana.libs.formato.Cadena;
import mx.org.kaana.libs.formato.Variables;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;

/**
 * @company KAANA
 * @project KAJOOL (Control system polls)
 * @date 12/04/2021
 * @time 03:04:50 PM
 * @author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public class Proceso extends IBaseTnx {

  private static final Log LOG = LogFactory.getLog(Proceso.class);
  private Long idContrato;
  
  public Proceso(Long idContrato) {
    this.idContrato= idContrato;
  }

  /**
   * @param args the command line arguments
   */
  public static void main(String[] args) throws Exception {
    Proceso proceso= new Proceso(28L);
    proceso.ejecutar(EAccion.ACTIVAR);
  }

  @Override
  protected boolean ejecutar(Session sesion, EAccion accion) throws Exception {
		TcKeetEstacionesDto padre= null;
    Estaciones estaciones     = null;
		try {			
      List<TcKeetContratosLotesDto> lotes= (List<TcKeetContratosLotesDto>)DaoFactory.getInstance().toEntitySet(sesion, TcKeetContratosLotesDto.class, "TcKeetContratosLotesDto", "byContrato", Variables.toMap("idContrato~"+ this.idContrato));
      if(lotes!= null && !lotes.isEmpty()) {
        estaciones= new Estaciones(sesion);
        for (TcKeetContratosLotesDto lote: lotes) {
          String clave= estaciones.toCode("001"+ "2021"+ Cadena.rellenar(this.idContrato.toString(), 3, '0', true)+ Cadena.rellenar(lote.getOrden().toString(), 3, '0', true));
          padre= (TcKeetEstacionesDto)DaoFactory.getInstance().toEntity(sesion, TcKeetEstacionesDto.class, "TcKeetEstacionesDto", "identically", Variables.toMap("clave~"+ clave));
          if(padre!= null) {
            LOG.error("["+ padre.getNivel()+ "] "+ padre.getCodigo()+ " "+ padre.getNombre()+ " "+ padre.getClave());
            this.partidas(sesion, estaciones, padre, lote.getIdContratoLote());
          } // if
        } // for  
      } // if
		} // try
		catch (Exception e) {			
			throw e; 
		} // catch		
    return true;
  }

  private void partidas(Session sesion, Estaciones estaciones, TcKeetEstacionesDto padre, Long idContratoLote) throws Exception  {
		List<TcKeetEstacionesDto> hijos= null;
		int terminados, iniciar        = 0;
    Long idEstacionEstatus         = 1L;
		try {
      hijos= estaciones.toChildren(padre.getClave(), padre.getNivel().intValue(), 1);
      if(hijos!= null && !hijos.isEmpty()) {
        terminados= 0;
        iniciar   = 0;
        for(TcKeetEstacionesDto item: hijos) {
          this.conceptos(sesion, estaciones, item);
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
        DaoFactory.getInstance().update(sesion, padre);
        TcKeetContratosLotesDto contratoLote= (TcKeetContratosLotesDto) DaoFactory.getInstance().findById(sesion, TcKeetContratosLotesDto.class, idContratoLote);
        contratoLote.setIdContratoLoteEstatus(Objects.equals(EEstacionesEstatus.TERMINADO.getKey(), idEstacionEstatus)? idEstacionEstatus+ 1L: idEstacionEstatus);			
        DaoFactory.getInstance().update(sesion, contratoLote);
      } // if  
		} // try
		catch (Exception e) {			
			throw e; 
		} // catch		
  }
  
  private void conceptos(Session sesion, Estaciones estaciones, TcKeetEstacionesDto padre) throws Exception  {
    Long idEstacionEstatus   = 1L;
		Map<String, Object>params= null;
		try {
      String clave= estaciones.toOnlyKey(padre.getClave(), padre.getNivel().intValue()+ 1);
			params= new HashMap<>();
			params.put("clave", clave);
			params.put("longitud", clave.length());
			params.put("nivel", padre.getNivel()+ 1L);
      Entity row= (Entity)DaoFactory.getInstance().toEntity(sesion, "TcKeetEstacionesDto", "totales", params);
      if(row!= null && !row.isEmpty()) {
        idEstacionEstatus= Objects.equals(row.toLong("terminados"), row.toLong("total"))? EEstacionesEstatus.TERMINADO.getKey(): Objects.equals(row.toLong("iniciados"), row.toLong("total"))? EEstacionesEstatus.INICIAR.getKey(): EEstacionesEstatus.EN_PROCESO.getKey();
        padre.setIdEstacionEstatus(idEstacionEstatus);
        DaoFactory.getInstance().update(sesion, padre);
      } // if  
		} // try
		catch (Exception e) {			
			throw e; 
		} // catch		
  }
  
}
