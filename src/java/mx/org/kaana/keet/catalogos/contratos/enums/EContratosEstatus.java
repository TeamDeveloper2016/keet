package mx.org.kaana.keet.catalogos.contratos.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 4/01/2022
 *@time 09:47:39 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */

public enum EContratosEstatus {

  EN_FIRMA,
  FIRMADO,
  RECIBIDO,
  ESCANEADO,
  ACTIVO,
  ENCUENTAS,
  FINIQUITO,
  COBRADO,
  LIQUIDADO,
  TERMINADO;
  
  private static final Map<Long, EContratosEstatus> lookup= new HashMap<>();
	
	static {
    for (EContratosEstatus item: EnumSet.allOf(EContratosEstatus.class)) 
      lookup.put(item.getKey(), item);    
  } // static
	
	public Long getKey(){
		return this.ordinal() + 1L;
	} // getKey
	
	public static EContratosEstatus fromIdContratoEstatus(Long idContratoEstatus) {
    return lookup.get(idContratoEstatus);
  } // fromIdContratoEstatus

}
