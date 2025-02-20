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
  
  // PRESTAMO(18L, "AK1|AL1", Boolean.TRUE, 6), 
  // ABONO(19L, "BD1|BE1|BF1|BG1|BH1|BI1", Boolean.TRUE, 6),
  
	FALTA(1L, "BW1|BX1|BY1|BZ1|CA1", Boolean.FALSE, 3), 
  DIAFESTIVO(15L, "AB1|AC1|AD1", Boolean.FALSE, 3), 
  EXCEDENTE(16L, "AF1|AG1|AH1|AI1|AJ1", Boolean.TRUE, 5), 
  TRIPLE(17L, "AE1", Boolean.FALSE, 1), 
  PRESTAMO(18L, "AK1", Boolean.TRUE, 1), 
  ABONO(19L, "BD1", Boolean.TRUE, 1),
  AGREMIADOS(-1L, "J1", Boolean.TRUE, -1),
  SALARIOS(-1L, "K1", Boolean.TRUE, -1),
  DESTAJO(-1L, "L1", Boolean.TRUE, -1),
  COMPLEMENTO(-1L, "N1", Boolean.TRUE, -1),
  PERIODO(-1L, "G1", Boolean.TRUE, -1),
  APERTURACH(20L, "AN1", Boolean.TRUE, 1),
  SALDOCH(21L, "AO1", Boolean.TRUE, 1),
  SOBRESUELDO(-1L, "AP1", Boolean.TRUE, -1),
  HORAS(22L, "AQ1|AR1|AS1|AT1|AU1|AV1", Boolean.TRUE, 6),
  MEDIODIA(23L, "BJ1|BK1|BL1|BM1|BN1|BO1", Boolean.TRUE, 6),
  DOBLE(24L, "AW1|AX1", Boolean.FALSE, 2),
	PENSION(-1L, "BP1|BQ1|BR1|BS1|BT1", Boolean.TRUE, 5),
	INFONAVIT(-1L, "P1", Boolean.TRUE, 1),
	PUNTUALIDAD(-1L, "S1", Boolean.TRUE, 1),
	ASISTENCIA(-1L, "T1", Boolean.TRUE, 1);
	
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
