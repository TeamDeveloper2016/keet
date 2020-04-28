package mx.org.kaana.keet.nomina.enums;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 28/04/2020
 *@time 02:41:10 PM 
 *author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public enum ENominaEstatus {
  
	INICIADA(1L), ENPROCESO(2L), CALCULADA(3L), TERMINADA(4L);
	
	private Long idKey;

	private ENominaEstatus(Long idKey) {
		this.idKey=idKey;
	}
	
	public Long getIdKey() {
    return this.idKey;
  } 

}
