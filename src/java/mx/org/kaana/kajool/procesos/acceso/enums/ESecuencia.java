package mx.org.kaana.kajool.procesos.acceso.enums;

/**
 *@company KAANA
 *@project KAJOOL (Control system polls)
 *@date 5/09/2019
 *@time 09:47:13 AM 
 *@author Team Developer 2016 <team.developer@kaana.org.mx>
 */
public enum ESecuencia {

	IGUAL("<=", "desc"),
	MENOR("<", "desc"),
	MAYOR(">", "");
	
	private String operador;
	private String orden;

	private ESecuencia(String operador, String orden) {
		this.operador= operador;
		this.orden   = orden;
	}

	public String getOperador() {
		return this.operador;
	}

  public String getOrden() {
    return orden;
  }

}
