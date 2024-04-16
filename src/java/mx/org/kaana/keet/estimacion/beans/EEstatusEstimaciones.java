package mx.org.kaana.keet.estimacion.beans;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EEstatusEstimaciones {

  REGISTRADA(1L),
  ACEPTADA  (2L),
  TERMINADA (3L),
  CANCELADA (4L),
  SALDADA   (5L);

  private final Long idEstatusEstimacion;
	private static final Map<Long, EEstatusEstimaciones> lookup= new HashMap<>();

  static {
    for (EEstatusEstimaciones item: EnumSet.allOf(EEstatusEstimaciones.class)) 
      lookup.put(item.getIdEstatusFicticia(), item);    
  }

  private EEstatusEstimaciones(Long idEstatusEstimacion) {
    this.idEstatusEstimacion = idEstatusEstimacion;
  }

  public Long getIdEstatusFicticia() {
    return idEstatusEstimacion;
  }
	
	public static EEstatusEstimaciones fromIdEstimacion(Long idEstatusEstimacion) {
    return lookup.get(idEstatusEstimacion);
  } 

}