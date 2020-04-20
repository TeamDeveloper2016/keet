package mx.org.kaana.keet.prestamos.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum EEstatusPrestamos {
  
  INICIALIZADA(1L),
  PARCIALIZADA(2L),  
  LIQUIDADA   (3L),
	SALDADA     (4L),
	CANCELADA   (5L);

  private Long idEstatusPrestamo;
	private static final Map<Long, EEstatusPrestamos> lookup= new HashMap<>();

  static {
    for (EEstatusPrestamos item: EnumSet.allOf(EEstatusPrestamos.class)) 
      lookup.put(item.getIdEstatusPrestamo(), item);    
  }

  private EEstatusPrestamos(Long idEstatusPrestamo) {
    this.idEstatusPrestamo = idEstatusPrestamo;
  }

  public Long getIdEstatusPrestamo() {
    return idEstatusPrestamo;
  }
	
	public static EEstatusPrestamos fromIdEstatusPrestamo(Long idEstatusPrestamo) {
    return lookup.get(idEstatusPrestamo);
  } 
}