package mx.org.kaana.keet.nomina.enums;

import mx.org.kaana.libs.Constantes;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 29/04/2020
 *@time 08:58:47 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public enum ECodigosIncidentes {
  
	FALTA(1L, "AY1|AZ1|BA1|BB1|BC1", Boolean.FALSE, 3), 
  DIAFESTIVO(15L, "AB1|AC1|AD1", Boolean.FALSE, 3), 
  EXEDENTE(16L, "AF1|AG1|AH1|AI1|AJ1", Boolean.TRUE, 5), 
  TRIPLE(17L, "AE1", Boolean.FALSE, 3), 
  PRESTAMO(18L, "AK1|AL1", Boolean.TRUE, 6), 
  ABONO(19L, "BD1|BE1|BF1|BG1|BH1|BI1", Boolean.TRUE, 6),
  AGREMIADOS(-1L, "J1", Boolean.TRUE, -1),
  SALARIOS(-1L, "K1", Boolean.TRUE, -1),
  DESTAJO(-1L, "L1", Boolean.TRUE, -1),
  COMPLEMENTO(-1L, "N1", Boolean.TRUE, -1),
  PERIODO(-1L, "G1", Boolean.TRUE, -1),
  APERTURACH(20L, "AN1", Boolean.TRUE, 3),
  SALDOCH(21L, "AO1", Boolean.TRUE, 3);
	
	private Long idTipoIncidente;
	private String codigos;
	private String[] celdas;
	private Integer size;
	private Integer max;
	private Boolean recuperar;
	private String siglas;

	private ECodigosIncidentes(Long idTipoIncidente, String celdas, Boolean recuperar, Integer max) {
		this.idTipoIncidente=idTipoIncidente;
		this.codigos=celdas;
		this.celdas=this.codigos.split(Constantes.SEPARADOR_SPLIT);
		this.size= this.celdas.length;
		this.recuperar= recuperar;
		this.max= max;
	}

	public Long idTipoIncidente() {
		return this.idTipoIncidente;
	}

	public String codigos() {
		return this.codigos;
	}
	
	public String[] celdas() {
		return this.celdas;
	}

	public Integer size() {
		return size;
	}

	public Boolean recuperar() {
		return recuperar;
	}

	public String siglas() {
		return siglas;
	}

	public Integer max() {
		return max;
	}
	
}
