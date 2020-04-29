package mx.org.kaana.keet.nomina.enums;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 29/04/2020
 *@time 08:58:47 AM 
 *author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public enum EGrupoConceptos {
	
  APORTACIONES("C1"), DEDUCCIONES("D1"), PERCEPCIONES("E1"), NETO("F1");
	
	private String celda;

	private EGrupoConceptos(String celda) {
		this.celda=celda;
	}
	
	public String celda() {
		return this.celda;
	}
	
}
