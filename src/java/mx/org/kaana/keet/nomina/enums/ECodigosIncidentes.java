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
  
	FALTA(1L, "AE1|AF1|AG1|AH1|AI1", Boolean.FALSE, 3), 
  DIAFESTIVO(15L, "R1|S1|T1", Boolean.FALSE, 3), 
  EXEDENTE(16L, "V1|W1|X1|Z1|Y1", Boolean.TRUE, 5), 
  TRIPLE(17L, "U1", Boolean.FALSE, 3), 
  PRESTAMO(18L, "AA1|AB1", Boolean.TRUE, 6), 
  ABONO(19L, "AJ1|AK1|AL1|AM1|AN1|AO1", Boolean.TRUE, 6),
  AGREMIADOS(-1L, "J1", Boolean.TRUE, -1),
  SALARIOS(-1L, "K1", Boolean.TRUE, -1),
  DESTAJO(-1L, "L1", Boolean.TRUE, -1),
  COMPLEMENTO(-1L, "N1", Boolean.TRUE, -1),
  PERIODO(-1L, "G1", Boolean.TRUE, -1);
	
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
